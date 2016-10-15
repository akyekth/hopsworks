'use strict';
/*
 * 
 */
angular.module('hopsWorksApp')

        .factory('KibanaService', ['$http', function ($http) {
            var service = {
              /**
               * Get Kibana main page.
               * @returns Kibana content.
               */
              getKibanaUI: function () {
                return $http.get('/hopsworks/kibana');
              }
            };
            return service;
          }]);
