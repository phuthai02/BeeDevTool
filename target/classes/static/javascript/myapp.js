const app = angular.module('DevApp', ['ngRoute']);

app.config(function ($routeProvider, $locationProvider) {
    $locationProvider.hashPrefix('');
    $routeProvider
        .when("/home", {
            templateUrl: '/beedev/home',
            controller: 'HomeController'
        })
        .when("/timekeeping", {
            templateUrl: '/beedev/timekeeping',
            controller: 'TimeKeepingController'
        })
        .otherwise({redirectTo: '/home'})
});

app.controller("HomeController", HomeController);
app.controller("TimeKeepingController", TimeKeepingController);


