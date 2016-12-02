'use strict';

angular.module('hopsWorksApp')
        .controller('CardCtrl', ['$scope', 'ProjectService', 'GVoDService',
          function ($scope, ProjectService, GVoDService) {
            var self = this;
            self.detail = [];
            var init = function (content) {
              if (content.details !== undefined) {
                console.log("No need to get detail: ", content);
                return;
              }
              content.map.entry.forEach(function (element) {
                self.detail[element.key] = element.value;
              });

              ProjectService.getMoreInfo({type: content.type, inodeId: content.id})
                .$promise.then( function (success) {
                  //console.log("More info ", success);
                  self.detail["createDate"] = success.createDate;
                  self.detail["downloads"] = success.downloads;
                  self.detail["size"] = success.size;
                  self.detail["user"] = success.user;
                  self.detail["votes"] = success.votes;
                }, function (error) {
                  console.log("More info error ", error);
              });

              content.details = self.detail;
              console.log("Controller init: ", content);
            };

            $scope.$watch("content", function (newValue, oldValue) {
              init(newValue);
            });
            
            self.sizeOnDisk = function (fileSizeInBytes) {
              return convertSize(fileSizeInBytes);
            };
          }]);


