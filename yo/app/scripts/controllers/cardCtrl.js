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

              ProjectService.getMoreInfo({type: content.type, inodeId: content.id}).$promise.then(
                function (response) {

                }, function (error) {
              });

              content.details = self.detail;
              console.log("Controller init: ", content);
            };

            $scope.$watch("content", function (newValue, oldValue) {
              init(newValue);
            });
          }]);


