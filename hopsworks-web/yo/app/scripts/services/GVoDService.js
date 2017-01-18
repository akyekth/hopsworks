'use strict';
/*
 * Service allowing fetching topic history objects by type.
 */
angular.module('hopsWorksApp')

        .factory('GVoDService', ['$http', function ($http) {
                var service = {
                    
                    downloadRequest: function (json) {
                        return $http({
                            method: 'PUT',
                            url: '/api/gvod/startdownload',
                            data: json
                        });
                    },
                    
                    getContents: function (json){
                      
                      return $http({
                            method: 'PUT',
                            url: '/api/gvod/contents',
                            data: json
                        });
                        
                        
                    },
                    
                    getExtendedDetails: function (json){
                      
                      return $http({
                            method: 'PUT',
                            url: '/api/gvod/details',
                            data: json
                        });
                        
                        
                    },
                    
                    remove: function (json){
                      
                      return $http({
                            method: 'PUT',
                            url: '/api/gvod/remove',
                            data: json
                        });
                        
                        
                    },
                    
                    downloadHdfs: function (json) {
                        return $http({
                            method: 'PUT',
                            url: '/api/gvod/downloadhdfs',
                            data: json
                        });
                    },
                    downloadKafka: function (json) {
                        return $http({
                            method: 'PUT',
                            url: '/api/gvod/downloadkafka',
                            data: json
                        });
                    }
                };
                return service;
            }]);
