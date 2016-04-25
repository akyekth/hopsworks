/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module('partialsApplication',[]);

angular.module('partialsApplication').controller('Downloader', ['BackendService',function(BackendService){
        
        var self = this;
        self.filename;
        self.identifier;
        self.result;

        
        
        self.download = function(){
            var JSONObj = { "name ":self.filename, "identifier":self.identifier };
            BackendService.download(JSONObj).then(function(result){
                self.result = result;
            });
            
        };
        
}]);
angular.module('partialsApplication').controller('Uploader', ['BackendService',function(BackendService){
        
        var self = this;
        self.filename;
        self.identifier;
        self.result;

        
        
        self.upload = function(){
            var JSONObj = { "name ":self.filename, "identifier":self.identifier };
            BackendService.upload(JSONObj).then(function(result){
                self.result = result;
            });
            
        };
        
}]);
angular.module('partialsApplication').controller('Stoper', ['BackendService',function(BackendService){
        
        var self = this;
        self.filename;
        self.identifier;
        self.result;

        
        
        self.stop = function(){
            var JSONObj = { "name ":self.filename, "identifier":self.identifier };
            BackendService.stop(JSONObj).then(function(result){
                self.result = result;
            });
            
        };
        
}]);
angular.module('partialsApplication').factory('BackendService',['$http',function($http){
        var service = {
            
            download: function (json) {
                  return $http('http://localhost:18180/torrent/download',json);
                },
            upload: function (json) {
                  return $http.put('http://localhost:18180/torrent/upload',json);
                },
            stop: function (json) {
                  return $http.put('http://localhost:18180/torrent/stop',json);
                }    
            
        };
        
        return service;
}]);

