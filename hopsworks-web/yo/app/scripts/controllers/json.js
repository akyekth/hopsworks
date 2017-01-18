'use strict';

angular.module('hopsWorksApp')
        .controller('JSONCtrl', ['$modalInstance', 'title', 'json',
          function ($modalInstance, title, json) {

            var self = this;
            self.title = title;
            self.json = json;

            self.ok = function () {
              $modalInstance.close();
            };
          }]);

