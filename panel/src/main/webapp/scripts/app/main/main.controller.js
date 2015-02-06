'use strict';

angular.module('zedpanelApp')
    .controller('MainController', function ($scope, $http, Principal) {
        Principal.identity().then(function (account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;

            $http.get('http://localhost:8080/jolokia/exec/zed:name=zedShell/invokeCommand/deploy_list').
                success(function (data, status, headers, config) {
                    var deployListHeaderLinesCount = 2;
                    $scope.deployablesCount = data.value.length - deployListHeaderLinesCount;
                    $scope.flash = null
                }).
                error(function (data, status, headers, config) {
                    $scope.flash = 'Can\'t connect to the ThingsCloud shell.'
                });

            var staticAvailableServices = {};
            staticAvailableServices["mongodb"] = "MongoDB";
            staticAvailableServices["attachmentService"] = "Binary Attachments Service";
            $scope.availableServices = staticAvailableServices
        });
    });
