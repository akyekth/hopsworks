'use strict';

angular.module('hopsWorksApp')
        .factory('RequestInterceptorService', ['$location', '$q', 
            function ($location, $q) {
            return {
              request: function (config) {

                var RESOURCE_SERVER = getApiLocationBase(); 
                var RESOURCE_NAME = 'api';

                var isApi = config.url.indexOf(RESOURCE_NAME);
                var isFullPath = config.url.indexOf('http://');
                if (isApi !== -1 && isFullPath === -1) {
                  config.url = RESOURCE_SERVER + config.url;
                  return config || $q.when(config);
                } else {
                  return config;
                }

              }
            };
          }]);
