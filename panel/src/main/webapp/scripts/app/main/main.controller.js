'use strict';

angular.module('zedpanelApp')
    .controller('MainController', function ($scope, $http, Principal) {
        Principal.identity().then(function (account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;

            $scope.selection = [];
            $scope.availableServices = [
                ["mongodb", "MongoDB"],
                ["attachmentService", "Binary Attachments Service"]
            ];

            shellCommand($http, 'deploy_list', function (data) {
                var deployListHeaderLinesCount = 2;
                $scope.deployablesCount = data.value.length - deployListHeaderLinesCount;
                $scope.flash = null
            }, function () {
                $scope.flash = 'Can\'t connect to the ThingsCloud shell.'
            });

            $scope.selectService = function toggleSelection(selectedValue) {
                var idx = $scope.selection.indexOf(selectedValue);
                if (idx > -1) {
                    $scope.selection.splice(idx, 1);
                } else {
                    $scope.selection.push(selectedValue);
                }
            };

            $scope.initialize = function () {
                $scope.flash = 'Initializing ThingsCloud. Please wait...';
                if ($scope.selection.indexOf("mongodb") > -1) {
                    shellCommand($http, 'deploy mongodb:docker', function (data) {
                        shellCommand($http, 'deploy_start_all', function (data) {
                            $scope.deployablesCount = 1;
                            $scope.flash = null;
                        }, function () {
                            $scope.flash = 'Can\'t connect to the ThingsCloud shell.'
                        });
                    }, function () {
                        $scope.flash = 'Can\'t connect to the ThingsCloud shell.'
                    });
                }
            }
        });
    });
