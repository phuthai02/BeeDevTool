const app = angular.module('DevApp', ['ngRoute']);

app.config(function ($routeProvider, $locationProvider) {
    $locationProvider.hashPrefix('');
    $routeProvider
        .when("/home", {
            templateUrl: '/beedev/home',
            controller: 'HomeController'
        })
        .when("/workday-standardize", {
            templateUrl: '/beedev/workday-standardize',
            controller: 'WorkdayStandardizeController'
        })
        .otherwise({redirectTo: '/home'})
});

app.controller("HomeController", HomeController);
app.controller("WorkdayStandardizeController", WorkdayStandardizeController);
