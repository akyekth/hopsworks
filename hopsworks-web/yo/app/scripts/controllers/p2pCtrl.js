angular.module('hopsWorksApp')
       .controller('P2PCtrl', ['GVoDService', '$routeParams', '$scope', '$interval', 
         function (GVoDService, $routeParams, $scope, $interval) {
        var self = this;
        self.projectId = $routeParams.projectID;

        self.preview = {};
        self.contents = [];


        self.remove = function (torrentId) {
            var json = {};
            json.torrentId = torrentId;
            GVoDService.remove(json).then(function (success) {
                getContents();
            });
        };

        var getContents = function () {

            var json = {};
            json.projectId = self.projectId;

            GVoDService.getContents(json).then(function (success) {
                self.contents = success.data.contents;
                var length = self.contents.length;
                for (var j = 0; j < length; j++) {
                    if (self.contents[j].torrentStatus === "DOWNLOADING") {
                        var prevObj = self.preview[self.contents[j].torrentId.val];
                        if (prevObj === null) {
                            prevObj = {
                                fileName: self.contents[j].fileName,
                                torrentId: self.contents[j].torrentId.val,
                                torrentStatus: self.contents[j].torrentStatus,
                                speed: 0,
                                dynamic: 0
                            };
                            self.preview[self.contents[j].torrentId.val] = prevObj;
                        }
                        var ejson = {};
                        ejson.torrentId = self.contents[j].torrentId.val;
                        GVoDService.getExtendedDetails(ejson).then(function (success) {
                            self.preview[success.data.torrentId.val].dynamic = Math.round(success.data.percentageCompleted);
                            self.preview[success.data.torrentId.val].speed = Math.round(success.data.downloadSpeed / 1024);
                        });
                    } else {
                        prevObj = {
                            fileName: self.contents[j].fileName,
                            torrentId: self.contents[j].torrentId.val,
                            torrentStatus: self.contents[j].torrentStatus
                        };
                        self.preview[self.contents[j].torrentId.val] = prevObj;
                    }
                }
            });

        };

        getContents();


        var contentsInterval = $interval(function () {
            getContents();
        }, 1000);

        $scope.$on("$destroy", function () {
            $interval.cancel(contentsInterval);
        });
    }]);