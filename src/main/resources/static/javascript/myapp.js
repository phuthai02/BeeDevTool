const app = angular.module('DevApp', ['ngRoute']);

app.config(function ($routeProvider, $locationProvider) {
    $locationProvider.hashPrefix('');
    $routeProvider
        .when("/home", {
            templateUrl: '/beedev/home',
            controller: 'HomeController'
        })
        .when("/workday-explain", {
            templateUrl: '/beedev/workday-explain',
            controller: 'WorkdayExplainController'
        })
        .otherwise({redirectTo: '/home'})
});

app.controller("HomeController", HomeController);
app.controller("WorkdayExplainController", WorkdayExplainController);


