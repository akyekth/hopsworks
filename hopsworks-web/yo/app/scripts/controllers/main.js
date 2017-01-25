/*jshint undef: false, unused: false, indent: 2*/
/*global angular: false */

'use strict';

angular.module('hopsWorksApp')
        .controller('MainCtrl', ['$interval', '$cookies', '$location', '$scope', 
                                 'AuthService', 'UtilsService', 'ElasticService', 
                                 'md5', 'ModalService', 'ProjectService', 'growl',
                                 'MessageService', '$routeParams', '$window', 'PublicSearchService',
          function ($interval, $cookies, $location, $scope, AuthService, UtilsService, 
                     ElasticService, md5, ModalService, ProjectService, growl, 
                     MessageService, $routeParams, $window, PublicSearchService) {

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
              $window.location.href = '/hopsworks-admin/security/protected/admin/adminIndex.xhtml';
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

            var getUnreadCount = function () {
              MessageService.getUnreadCount().then(
                      function (success) {
                        self.unreadMessages = success.data.data.value;
                      }, function (error) {
              });
            };
            
            var getMessages = function () {
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
                        growl.success(success.data.successMessage,
                                {title: 'Success', ttl: 1000});
                      }, function (error) { });
            };

            self.searchTerm = "";
            self.searching = false;
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
            self.pageSize = 16;
            self.hitEnter = function (evt) {
              if (angular.equals(evt.keyCode, 13)) {
                self.search();
              }
            };

            self.keyTyped = function (evt) {
              if (self.searchTerm.length > 3 
                  || (self.searchResult.length > 0 && self.searchTerm.length > 0)) {
                self.searchReturned = "Searching for <b>" + self.searchTerm + "<b> ...";
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
              self.pageSize = 16;
              self.searchResult = [];
              self.searchReturned = "";
              self.searchResultPublicSearch = [];
              self.searchReturnedPublicSearch = "";

              if (self.searchTerm === undefined || self.searchTerm === "" || self.searchTerm === null) {
                return;
              }
              self.searching = true;
              if (self.searchType === "global") {
                var local_data;
                var global_data;
                //triggering a global search
                elasticService.globalSearch(self.searchTerm)
                        .then(function (response) {
                          var searchHits = response.data;
                          //console.log("RECEIVED RESPONSE ", response);
                          if (searchHits.length > 0) {
                            if (self.globalClusterBoundary) {
                              self.searchReturned = "Result for <b>" + self.searchTerm + "</b>";
                            } else {
                              self.searchReturned = "Result for <b>" + self.searchTerm + "</b>";
                            }
                            self.searchResult = searchHits;
                          } else {
                            self.searchResult = [];
                            if (self.globalClusterBoundary) {
                              self.searchReturned = "No result found for <b>" + self.searchTerm + "</b>";
                            } else {
                              self.searchReturned = "No result found for <b>" + self.searchTerm + "</b>";
                            }
                          }
                          self.resultPages = Math.ceil(self.searchResult.length / self.pageSize);
                          self.resultItems = self.searchResult.length;
                          elasticService.publicSearch(self.searchTerm).then(function (response2) {
                            global_data = response2.data;
                            if (global_data.length > 0) {
                              self.searchReturnedPublicSearch = "Public search results for <b>" + self.searchTerm + "</b>";
                              self.searchResultPublicSearch = global_data;
                              self.searchResult.push(global_data);
                              self.searching = false;
                            } else {
                              self.searching = false;
                              self.searchResultPublicSearch = [];
                              self.searchReturnedPublicSearch = "No public search results found for <b>" + self.searchTerm + "</b>";
                            }
                            self.resultPagesPublicSearch = Math.ceil(Math.max(self.searchResultPublicSearch.length, self.searchResult.length) / self.pageSize);
                            self.resultItemsPublicSearch = self.searchResultPublicSearch.length;
                            self.resultPages = Math.ceil(self.searchResult.length / self.pageSize);
                            self.resultItems = self.searchResult.length;
                          });
                        }, function (error) {
                          self.searching = false;
                          growl.error(error.data.errorMsg, {title: 'Error', ttl: 5000});
                        });
              } else if (self.searchType === "projectCentric") {
                elasticService.projectSearch(UtilsService.getProjectName(), self.searchTerm)
                        .then(function (response) {
                          self.searching = false;
                          var searchHits = response.data;
                          //console.log("RECEIVED RESPONSE ", response);
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
                          self.searching = false;
                          growl.error(error.data.errorMsg, {title: 'Error', ttl: 5000});
                        });
              } else if (self.searchType === "datasetCentric") {
                elasticService.datasetSearch($routeParams.projectID, UtilsService.getDatasetName(), self.searchTerm)
                        .then(function (response) {
                          self.searching = false;
                          var searchHits = response.data;
                          //console.log("RECEIVED RESPONSE ", response);
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
                          self.searching = false;
                          growl.error(error.data.errorMsg, {title: 'Error', ttl: 5000});
                        });
              }
              datePicker();// this will load the function so that the date picker can call it.
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
            
            self.viewType = function (listView) {
              if (listView) {
                self.pageSize = 4;
              } else {
                self.pageSize = 6;
              }
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
            
            self.viewType = function (listView) {
              if (listView) {
                self.pageSize = 4;
              } else {
                self.pageSize = 16;
              }
            };

            self.incrementPage = function () {
              self.pageSize = self.pageSize + 1;
            };

            self.decrementPage = function () {
              if (self.pageSize < 2) {
                return;
              }
              self.pageSize = self.pageSize - 1;
            };
            
            self.viewDelail = function(result) {
              if (result.localDataset) {
                if (result.type === 'proj') {
                  ProjectService.getProjectInfo({projectName: result.name}).$promise.then(
                          function (response) {
                            ModalService.viewSearchResult('lg', response, result);
                          }, function (error) {
                    growl.error(error.data.errorMsg, {title: 'Error', ttl: 5000});
                  });
                } else if (result.type === 'ds') {
                  ProjectService.getDatasetInfo({inodeId: result.id}).$promise.then(
                          function (response) {
                            var projects;
                            console.log(response);
                            ProjectService.query().$promise.then(
                                    function (success) {
                                      projects = success;
                                      ModalService.viewSearchResult('lg', response, result, projects);
                                    }, function (error) {
                              growl.error(error.data.errorMsg, {title: 'Error', ttl: 5000});
                            });
                          });
                }
              } else {
                if (content.searchEndpoint !== undefined) {                  
                  PublicSearchService.getMoreInfo(content.searchEndpoint, content.id)
                          .then(function (success) {
                          
                  }, function (error) {
                    console.log("Error getting more info ", error);
                  });
                }
              }             
            };            
}]);
