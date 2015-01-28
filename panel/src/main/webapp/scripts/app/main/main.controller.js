'use strict';

angular.module('zedpanelApp')
    .controller('MainController', function ($scope, $http, Principal) {
        Principal.identity().then(function (account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;

            $scope.details = function (collection) {
                $http.get('http://localhost:15001/api/document/count/' + collection).
                    success(function (data, status, headers, config) {
                        $scope.collectionCount = data;
                        $scope.flash = null
                    }).
                    error(function (data, status, headers, config) {
                        $scope.flash = 'Can\'t connect to the Document Service.'
                    });
            };

            var collections = ['foo', 'bar'];
            $scope.collections = collections;
        });
    });
