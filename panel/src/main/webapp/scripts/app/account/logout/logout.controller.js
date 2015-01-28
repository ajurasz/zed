'use strict';

angular.module('zedpanelApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
