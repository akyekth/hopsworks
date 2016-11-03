/*jshint undef: false, unused: false, indent: 2*/
/*global angular: false */
'use strict';

angular.module('hopsWorksApp')
        .controller('MainCtrl', ['$interval', '$cookies', '$location', '$scope', 'AuthService', 'UtilsService', 'ElasticService', 'md5', 'ModalService', 'ProjectService', 'growl', 'MessageService', '$routeParams', '$window',
          function ($interval, $cookies, $location, $scope, AuthService, UtilsService, ElasticService, md5, ModalService, ProjectService, growl, MessageService, $routeParams, $window) {

            var self = this;
            self.email = $cookies.get('email');
            self.emailHash = md5.createHash(self.email || '');
            var elasticService = ElasticService();

            if (!angular.isUndefined($routeParams.datasetName)) {
              self.searchType = "datasetCentric";
            } else if (!angular.isUndefined($routeParams.projectID)) {
              self.searchType = "projectCentric";
            } else {
              self.searchType = "global";
            }

            self.isAdmin = function () {
              return $cookies.get('isAdmin');
            };
                              
            self.goToAdminPage = function () {
              $window.location.href = '/hopsworks/security/protected/admin/adminIndex.xhtml';
            };

            self.getEmailHash = function(email) {
              return md5.createHash(email || '');
            };

            self.logout = function () {
              AuthService.logout(self.user).then(
                function (success) {
                  $location.url('/login');
                  $cookies.remove("email");
                  $cookies.remove("isAdmin");
                  localStorage.removeItem("SESSIONID");
                  sessionStorage.removeItem("SESSIONID");
                }, function (error) {
                  self.errorMessage = error.data.msg;
                });
            };

            self.profileModal = function () {
              ModalService.profile('md');
            };

            self.sshKeysModal = function () {
              ModalService.sshKeys('lg');
            };

            self.getHostname = function () {
              return $location.host();
            };

            self.getUser = function () {
              return self.email.substring(0, self.email.indexOf("@"));
            };

            self.view = function (name, id, dataType) {

              if (dataType === 'project') {
                ProjectService.getProjectInfo({projectName: name}).$promise.then(
                        function (success) {

                          ModalService.viewSearchResult('md', success, dataType)
                                  .then(function (success) {
                                    growl.success(success.data.successMessage, {title: 'Success', ttl: 1000});
                                  }, function (error) {

                                  });
                        }, function (error) {
                  growl.error(error.data.errorMsg, {title: 'Error', ttl: 10000});
                });
              } else if (dataType === 'dataset') {
                //fetch the dataset
                ProjectService.getDatasetInfo({inodeId: id}).$promise.then(
                        function (response) {
                          var projects;

                          //fetch the projects to pass them in the modal. Fixes empty projects array on ui-select initialization
                          ProjectService.query().$promise.then(
                                  function (success) {
                                    projects = success;

                                    //show dataset
                                    ModalService.viewSearchResult('md', response, dataType, projects)
                                            .then(function (success) {
                                                growl.success(success.data.successMessage, {title: 'Success', ttl: 1000});
                                            }, function (error) {

                                            });
                                }, function (error) {
                            growl.error(error.data.errorMsg, {title: 'Error', ttl: 10000});
                                    });
                          });
              } else if (dataType === 'ds') {
                //fetch the dataset
                        ProjectService.getDatasetInfo({inodeId: id}).$promise.then(
                        function (response) {
                          var projects;
                          //fetch the projects to pass them in the modal. Fixes empty projects array on ui-select initialization
                          ProjectService.query().$promise.then(
                                  function (success) {
                                    projects = success;

                                    //show dataset
                                    ModalService.viewSearchResult('md', response, dataType, projects)
                                            .then(function (success) {
                                                            growl.success(success.data.successMessage, {title: 'Success', ttl: 1000});
                                            }, function (error) {

                                            });
                                            }, function (error) {
                                    console.log('Error: ' + error);
                                  });

                                }, function (error) {
                          growl.error(error.data.errorMsg, {title: 'Error', ttl: 10000});
                        });
              }
            };

            var getUnreadCount = function () {
              MessageService.getUnreadCount().then(
                      function (success) {
                        self.unreadMessages = success.data.data.value;
                            }, function (error) {
              });
            };
            var getMessages = function () {//
              MessageService.getMessages().then(
                      function (success) {
                        self.messages = success.data;
                      }, function (error) {
              });
            };

            var getPopularPublicDatasets = function () {
              ProjectService.getPopularPublicDatasets().$promise.then(function (result) {
                self.popularDatasets = result;
              });
            };

            getPopularPublicDatasets();
            getUnreadCount();
            getMessages();
            //this might be a bit to frequent for refresh rate 
            var getUnreadCountInterval = $interval(function () {
              getUnreadCount();
            }, 10000);

//            var getPopularPublicDatasetsInterval = $interval(function () {
//              getPopularPublicDatasets();
//            }, 6000);


            self.getMessages = function () {
              getMessages();
            };
            self.openMessageModal = function (selected) {
              if (selected !== undefined) {
                MessageService.markAsRead(selected.id);
              };
              ModalService.messages('lg', selected)
                      .then(function (success) {
                                growl.success(success.data.successMessage, {title: 'Success', ttl: 1000})
                            }, function (error) { });
            };

            self.searchTerm = "";
            self.globalClusterBoundary = false;
            self.searchReturned = "";
            self.searchReturnedPublicSearch = "";
            self.searchResult = [];
            self.searchResultPublicSearch = [];
            self.resultPages = 0;
            self.resultPagesPublicSearch = 0;
            self.resultItems = 0;
            self.resultItemsPublicSearch = 0;
            self.currentPage = 1;
            self.pageSize = 5;
            self.hitEnter = function (evt) {
              if (angular.equals(evt.keyCode, 13)) {
                self.search();
              }
            };

            self.keyTyped = function (evt) {

              if (self.searchTerm.length > 3 || (self.searchResult.length > 0 && self.searchTerm.length > 0)) {
                self.search();
              } else {
                self.searchResult = [];
                self.searchReturned = "";
                self.searchResultPublicSearch = [];
                self.searchReturnedPublicSearch = "";

              }
            };

            self.search = function () {
              //ask for the project name when it is time to search
              self.projectName = UtilsService.getProjectName();
              self.currentPage = 1;
              self.pageSize = 5;
              self.searchResult = [];
              self.searchReturned = "";
              self.searchResultPublicSearch = [];
              self.searchReturnedPublicSearch = "";

              if (self.searchTerm === undefined || self.searchTerm === "" || self.searchTerm === null) {
                return;
              }
              
              if (self.searchType === "global") {
                //triggering a global search

                var local_data;
                var global_data;
                //triggering a global search
                elasticService.globalSearch(self.searchTerm).then(function (response) {
                  local_data = response.data;
                  if (local_data.length > 0) {
                    self.searchReturned = "Result for <b>" + self.searchTerm + "</b>";
                    self.searchResult = local_data;
                  } else {
                    self.searchResult = [];
                    self.searchReturned = "No result found for <b>" + self.searchTerm + "</b>";
                  }
                  self.resultPages = Math.ceil(self.searchResult.length / self.pageSize);
                  self.resultItems = self.searchResult.length;
                  elasticService.publicSearch(self.searchTerm).then(function (response2) {
                    global_data = response2.data;
                    if (global_data.length > 0) {
                      self.searchReturnedPublicSearch = "Public search results for <b>" + self.searchTerm + "</b>";
                      self.searchResultPublicSearch = global_data;
                    } else {
                      self.searchResultPublicSearch = [];
                      self.searchReturnedPublicSearch = "No public search results found for <b>" + self.searchTerm + "</b>";
                    }
                    self.resultPagesPublicSearch = Math.ceil(Math.max(self.searchResultPublicSearch.length, self.searchResult.length) / self.pageSize);
                    self.resultItemsPublicSearch = self.searchResultPublicSearch.length;
                  });
                });
              } else if (self.searchType === "projectCentric") {
                elasticService.projectSearch(UtilsService.getProjectName(), self.searchTerm)
                        .then(function (response) {

                          var searchHits = response.data;
                          //console.log("RECEIVED RESPONSE " + JSON.stringify(response));
                          if (searchHits.length > 0) {
                            self.searchReturned = "Result for <b>" + self.searchTerm + "</b>";
                            self.searchResult = searchHits;
                          } else {
                            self.searchResult = [];
                            self.searchReturned = "No result found for <b>" + self.searchTerm + "</b>";
                          }
                          self.resultPages = Math.ceil(self.searchResult.length / self.pageSize);
                          self.resultItems = self.searchResult.length;

                        }, function (error) {
                                    growl.error(error.data.errorMsg, {title: 'Error', ttl: 10000});
                  });
              } else if (self.searchType === "datasetCentric") {
                elasticService.datasetSearch($routeParams.projectID, UtilsService.getDatasetName(), self.searchTerm)
                        .then(function (response) {

                          var searchHits = response.data;
                          //console.log("RECEIVED RESPONSE " + JSON.stringify(response));
                          if (searchHits.length > 0) {
                            self.searchReturned = "Result for <b>" + self.searchTerm + "</b>";
                            self.searchResult = searchHits;
                          } else {
                            self.searchResult = [];
                            self.searchReturned = "No result found for <b>" + self.searchTerm + "</b>";
                          }
                          self.resultPages = Math.ceil(self.searchResult.length / self.pageSize);
                          self.resultItems = self.searchResult.length;

                        }, function (error) {
                            growl.error(error.data.errorMsg, {title: 'Error', ttl: 10000});
                });
              }
              datePicker(); // this will load the function so that the date picker can call it.
            };

            var datePicker = function () {
              $(function () {
                $('[type="datepicker"]').datetimepicker({format: 'DD/MM/YYYY'});
                $("#datepicker1").on("dp.change", function (e) {
                  $('#datepicker2').data("DateTimePicker").minDate(e.date);
                });
                $("#datepicker2").on("dp.change", function (e) {
                  $('#datepicker1').data("DateTimePicker").maxDate(e.date);
                });
                $("#datepicker3").on("dp.change", function (e) {
                  $('#datepicker4').data("DateTimePicker").minDate(e.date);
                });
                $("#datepicker4").on("dp.change", function (e) {
                  $('#datepicker3').data("DateTimePicker").maxDate(e.date);
                });
              });
            };
            
            self.row = function (index) {
              var width = angular.element('#cards')[0].offsetWidth;
              if (width === 375 && index%4 === 0 ) {
                return true;
              } else if (width === 500 && index%3 === 0 ) {
                return true;
              } else if (width === 1500) {
                return true;
              }
              return false;
            };

            $scope.$on("$destroy", function () {
              $interval.cancel(getUnreadCountInterval);
              //$interval.cancel(getPopularPublicDatasetsInterval);
            });


            self.downloadPublicDataset = function (datasetId, defaultDatasetName, partners) {

              ModalService.selectProject('md', true, "/[^]*/",
                      "Select a Project as download destination.").then(
                      function (success) {
                        var destProj = success.projectId;
                        ModalService.setupDownload('md', destProj, datasetId, defaultDatasetName, partners)
                                .then(function (success) {

                                }, function (error) {
                                  growl.error(error, {title: 'Error', ttl: 1000});
                                });
                      }, function (error) {
                growl.error(error, {title: 'Error', ttl: 1000});
              });
            };
            
            self.incrementPage = function () {
              self.pageSize = self.pageSize+1;
            };

            self.decrementPage = function () {
              if (self.pageSize < 2) {
                return;
              }
              self.pageSize=self.pageSize-1;
            };
            
            self.searchResults = [
              {color: "green", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "blue", label: "project", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "red", label: "dataset", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "yellow", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "gold", label: "project", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "cyan", label: "dataset", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "hotPink", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "green", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "green", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "blue", label: "project", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "red", label: "dataset", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "yellow", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "gold", label: "project", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "cyan", label: "dataset", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "hotPink", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "green", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "green", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "blue", label: "project", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "red", label: "dataset", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "yellow", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "gold", label: "project", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "cyan", label: "dataset", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "hotPink", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"},
              {color: "green", label: "public", hader: "A glimpse inside the mind of a data scientist",
                details: "<div><label>source</label><div class='value'>IBM</div></div><div><label>Date</label><div class='value'>Aug 16, 2016</div></div>"}
            ];
          }]);
