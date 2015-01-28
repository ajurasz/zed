'use strict';

angular.module('zedpanelApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {});
    });


