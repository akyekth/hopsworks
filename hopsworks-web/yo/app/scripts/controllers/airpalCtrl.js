'use strict';

angular.module('hopsWorksApp')
        .controller('AirpalCtrl', ['$scope', '$routeParams','$route',
          function ($scope, $routeParams, $route) {

            var self = this;
            self.projectId=$routeParams.projectID;

            self.getAirpalURL = function () {
              return getLocationBase() + "/airpal?projectID="+ self.projectId;
            };
          }]);


