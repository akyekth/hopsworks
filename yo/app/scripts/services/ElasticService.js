/*jshint undef: false, unused: false, indent: 2*/
/*global angular: false */

'use strict';

angular.module('hopsWorksApp')
        .factory('ElasticService', ['$http', function ($http) {
            return function () {
              var services = {
                /**
                 * Do a global search hitting two indices: 'project' and 'datasets'
                 * 
                 * @param {type} searchTerm
                 * @returns {unresolved}
                 */
                globalSearch: function (searchTerm) {
                  return $http.get('/api/elastic/globalsearch/' + searchTerm);
                },
                /**
                 * Do a cluster wide search for datasets in all hops-site registered clusters
                 * 
                 * @param {type} searchTerm
                 * @returns {unresolved}
                 */
                clusterWideSearch: function (searchTerm) {
                  return $http.get('/api/elastic/clusterwidesearch/' + searchTerm);
                },
                /**
                 * Search under a project hitting hitting 'project' index
                 * 
                 * @param {type} projectName
                 * @param {type} searchTerm
                 * @returns {unresolved}
                 */
                projectSearch: function (projectName, searchTerm) {
                  return $http.get('/api/elastic/projectsearch/' + projectName + '/' + searchTerm);
                },
                /**
                 * Search under a dataset hitting hitting 'dataset' index
                 * 
                 * @param {type} datasetName
                 * @param {type} searchTerm
                 * @returns {unresolved}
                 */
                datasetSearch: function (datasetName, searchTerm) {
                  return $http.get('/api/elastic/datasetsearch/' + datasetName + '/' + searchTerm);
                }
              };
              return services;
            };
          }]);

