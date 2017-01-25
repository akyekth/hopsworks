'use strict';

angular.module('hopsWorksApp')
    .factory('PublicSearchService', ['$http', function ($http) {
            var service = {
              getMoreInfo: function (url, id) {
                return $http.get(url + '/api/publicSearch/getMoreInfo/' + id);
              },
              getReadme: function (url, path) {
                return $http.get(url + '/api/publicSearch/readme/' + path);
              }
            };
        return service;
    }]);



