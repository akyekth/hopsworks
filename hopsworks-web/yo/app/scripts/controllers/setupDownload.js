

angular.module('hopsWorksApp')
        .controller('SetupDownloadCtrl', ['$modalInstance', 'DataSetService', 'KafkaService', 'GVoDService', 'growl', 'defaultDatasetName', 'projectId', 'datasetId', 'partners', 'ModalService',
            function ($modalInstance, DataSetService, KafkaService, GVoDService, growl, defaultDatasetName, projectId, datasetId, partners, ModalService) {

                var self = this;
                self.projectId = projectId;
                self.datasetId = datasetId;
                self.partners = partners;

                self.datasetName = defaultDatasetName;

                var dataSetService = DataSetService(self.projectId);


                self.manifestAvailable = false;
                self.manifest;

                self.DownloadTypeKafka = false;
                self.typeChosen = false;

                self.topicValues = [];
                self.topicDone = [];
                self.topicsCreated = false;
                self.topicsRemainingForCreation = 0;
                self.limit = 5;

                self.topicsMap = {};


                self.initTopic = function (index, fileName) {
                    self.topicValues[index] = self.projectId + '_' + self.datasetName + '_' + fileName;
                    self.topicDone[index] = false;
                    self.topicsRemainingForCreation++;
                };

                self.validTopicName = function (topicName) {
                    var topics = [];

                    KafkaService.getTopics(self.projectId).then(function (success) {
                        topics = success.data;
                    },
                            function (error) {
                                growl.error(error.data.errorMsg, {title: 'Failed to get Topics', ttl: 5000});
                            });


                    for (var i = 0; i < topics.length; i++) {
                        if (topicName === topics[i].name) {
                            return false;
                        }
                    }
                    return true;
                };

                self.createTopic = function (topicName, schema, fileName, index) {

                    var schemaDetail = {};
                    schemaDetail.name = topicName;
                    schemaDetail.contents = schema;
                    schemaDetail.version = 1;
                    schemaDetail.versions = [];

                    KafkaService.createSchema(self.projectId, schemaDetail).then(
                            function (success) {

                                var topicDetails = {};
                                topicDetails.name = topicName;
                                topicDetails.numOfPartitions = 2;
                                topicDetails.numOfReplicas = 1;
                                topicDetails.schemaName = topicName;
                                topicDetails.schemaVersion = 1;

                                KafkaService.createTopic(self.projectId, topicDetails).then(
                                        function (success) {
                                            self.topicsRemainingForCreation--;
                                            self.topicDone[index] = true;
                                            self.topicsMap[fileName] = topicName;
                                            if (self.topicsRemainingForCreation === 0) {
                                                self.topicsCreated = true;
                                            }
                                        }, function (error) {
                                    growl.error(error.data.errorMsg, {title: 'Failed to create topic', ttl: 5000});

                                });


                            }, function (error) {
                        growl.error(error.data.errorMsg, {title: 'Failed to create schema', ttl: 5000});
                    });



                };

                self.myFilter = function (item) {

                    return item.schema !== '';

                };

                self.isNameOk = function () {

                    dataSetService.getAllDatasets().then(function (success) {

                        var data = success.data;
                        for (var i = 0; i < data.length; i++) {
                            if (data[i].name === self.datasetName) {
                                self.datasetNameOk = false;
                                return;
                            }
                        }

                        self.datasetNameOk = true;

                    }, function (error) {

                    });

                };

                self.datasetNameOk = self.isNameOk();


                self.DownloadRequest = function () {

                    var json = {};
                    json.destinationDatasetName = self.datasetName;
                    json.publicDatasetId = self.datasetId;
                    json.projectId = self.projectId;
                    json.partners = self.partners;
                    GVoDService.downloadRequest(json).then(function (success) {
                        self.manifest = success.data;
                        self.manifestAvailable = true;
                    },
                            function (error) {

                                growl.error(error.data.details, {title: 'Failed to start initiate Download', ttl: 5000});

                            });



                };

                self.downloadTypeHdfs = function () {
                    self.DownloadTypeKafka = false;
                    self.typeChosen = true;
                };

                self.downloadTypeKafkaHdfs = function () {
                    self.DownloadTypeKafka = true;
                    self.typeChosen = true;
                };

                self.download = function () {

                    if (!self.DownloadTypeKafka) {

                        var json = {};
                        json.destinationDatasetName = self.manifest.datasetName;
                        json.projectId = self.projectId;
                        json.publicDatasetId = self.datasetId;
                        json.partners = self.partners;
                        
                        for(var i = 0;i<self.manifest.fileInfos.length;i++){
                            var keyName = self.manifest.fileInfos[i].fileName;
                            self.topicsMap[keyName] = "";
                        }
                        
                        json.topics = JSON.stringify(self.topicsMap);
                        
                        GVoDService.downloadHdfs(json).then(function (success) {
                            
                            growl.success(success.data.details, {title: 'Success', ttl: 1000});
                            $modalInstance.close(success);

                        }, function (error) {
                            growl.error(error.data.details, {title: 'Failed to start download', ttl: 5000});
                        });

                    } else {

                        var json = {};
                        json.destinationDatasetName = self.manifest.datasetName;
                        json.projectId = self.projectId;
                        json.publicDatasetId = self.datasetId;
                        json.partners = self.partners;
                        
                        for(var i = 0; i<self.manifest.fileInfos.length;i++){
                            var keyName = self.manifest.fileInfos[i].fileName;
                            if(!(keyName in self.topicsMap)){
                                self.topicsMap[keyName] = "";
                            }
                        }
                        
                        json.topics = JSON.stringify(self.topicsMap);

                        GVoDService.downloadKafka(json).then(function (success) {
                            
                            growl.success(success.data.details, {title: 'Success', ttl: 1000});
                            $modalInstance.close(success);

                        }, function (error) {
                            growl.error(error.data.details, {title: 'Failed to start download', ttl: 5000});
                        });
                    }

                };

                self.showSchema = function (schema) {

                    ModalService.json('md', 'Schema', schema).then(function (success) {

                    });

                };
                
                self.showMore = function(){
                    
                    self.limit = self.limit + 5;
                };

            }]);