

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

                self.topicsRemainingForCreation = 0;
                self.topicsCreated = false;
                self.topicValues = [];
                self.topicDone = [];
                
                self.topicsMap = {};

                self.validTopicName = function (topicName) {
                    var topics = [];

                    KafkaService.getTopics(self.projectId).then(function (success) {
                        topics = success.data;
                    },
                    function(error){
                        growl.error(error.data.errorMsg, {title: 'Failed to get Topics', ttl: 5000});
                    });
                  
                    
                    for (var i = 0; i < topics.length; i++) {
                        if (topicName === topics[i].name) {
                            return false;
                        }
                    }
                    return true;
                };

                self.createTopic = function (topicName, schema, index) {

                    var schemaDetail = {};
                    schemaDetail.name = topicName;
                    schemaDetail.contents = schema;
                    schemaDetail.version = 1;

                    KafkaService.createSchema(self.projectId, schemaDetail).then(
                            function (success) {

                            }, function (error) {
                        growl.error(error.data.errorMsg, {title: 'Failed to create schema', ttl: 5000});
                    });

                    var topicDetails = {};
                    topicDetails.name = topicName;
                    topicDetails.numOfPartitions = 2;
                    topicDetails.numOfReplicas = 1;
                    topicDetails.schemaName = topicName;
                    topicDetails.schemaVersion = 1;

                    KafkaService.createTopic(self.projectId, topicDetails).then(
                            function (success) {
                                self.topicsRemainingForCreation--;
                                self.topicsMap.fileName = topicName;
                                self.topicDone[index] = false;
                                if(self.topicsRemainingForCreation === 0){
                                    self.topicsCreated = true;
                                }
                            }, function (error) {
                        growl.error(error.data.errorMsg, {title: 'Failed to create topic', ttl: 5000});
                        
                    });

                };

                self.setTopicsRemainingForCreation = function (files) {
                    for (var i = 0; i < files.length; i++) {
                        if (files[i].schema !== '') {
                            self.topicsRemainingForCreation++;
                            self.topicDone[i] = false;
                            self.topicValues[i] = self.projectId + '_' + self.datasetName + '_' + files[i].fileName;
                        }
                    }
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
                    json.datasetName = self.datasetName;
                    json.datasetId = self.datasetId;
                    json.projectId = self.projectId;
                    GVoDService.downloadRequest(json).then(function (success) {
                        self.manifest = success.data;
                        self.manifestAvailable = true;
                        if (self.manifest.supportKafka) {
                            self.setTopicsRemainingForCreation(self.manifest.fileInfos);
                        }
                    },
                            function (error) {
                                
                                growl.error(error.data.errorMsg, {title: 'Failed to get manifest', ttl: 5000});

                            });



                };

                self.downloadTypeHdfs = function () {
                    self.DownloadTypeKafka = false;
                };

                self.downloadTypeKafkaHdfs = function () {
                    self.DownloadTypeKafka = true;
                };

                self.download = function () {

                    if (!self.DownloadTypeKafka) {
                        
                        var json = {};
                        json.datasetName = self.manifest.datasetName;
                        json.projectId = self.projectId;
                        json.datasetId = self.datasetId;
                        json.partners = self.partners;
                        
                        GVoDService.downloadHdfs(json).then(function(success){
                           
                            $modalInstance.close(success);
                            
                        },function(error){
                             growl.error(error.data.errorMsg, {title: 'Failed to start download', ttl: 5000});
                        });

                    } else {
                        
                        var json = {};
                        json.datasetName = self.manifest.datasetName;
                        json.projectId = self.projectId;
                        json.datasetId = self.datasetId;
                        json.partners = self.partners;
                        json.topics = self.topicsMap;
                        
                        GVoDService.downloadKafka(json).then(function(success){
                           
                            $modalInstance.close(success);
                            
                        },function(error){
                             growl.error(error.data.errorMsg, {title: 'Failed to start download', ttl: 5000});
                        });
                    }

                };

                self.showSchema = function (schema) {

                    ModalService.json('md', 'Schema', schema).then(function (success) {

                    });

                };

            }]);