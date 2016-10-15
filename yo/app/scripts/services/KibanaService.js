'use strict';
/*
 * 
 */
angular.module('hopsWorksApp')

        .factory('KibanaService', ['$http', function ($http) {
            var service = {
              /**
               * Get Kibana main page.
               * @returns {unresolved} Kibana content.
               */
              getAllJobsInProject: function () {
                return $http.get('/hopsworks/kibana');
              }
            };
            return service;
          }]);
