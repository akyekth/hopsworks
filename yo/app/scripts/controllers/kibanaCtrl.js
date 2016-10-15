'use strict';

angular.module('hopsWorksApp')
        .controller('KibanaCtrl', ['$scope', 'KibanaService', 'ModalService', '$routeParams', 'growl',
          function ($scope, KibanaService, ModalService, $routeParams, growl) {
            var self = this;
            self.ui = "";
            var getKibanaUI = function () {

              KibanaService.getKibanaUI().then(
                      function (success) {
                        self.ui = success.data;
                      }, function (error) {
                growl.error(error.data.errorMsg, {title: 'Error fetching ui.', ttl: 15000});
              });

            }
            getKibanaUI();
          }]);


