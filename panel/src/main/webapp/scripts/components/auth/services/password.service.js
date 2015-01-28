'use strict';

angular.module('zedpanelApp')
    .factory('Password', function ($resource) {
        return $resource('api/account/change_password', {}, {});
    });
