var app = angular.module('myApp', ['ngRoute', 'chart.js']);
app.config(function ($interpolateProvider) {
    $interpolateProvider.startSymbol('{[{');
    $interpolateProvider.endSymbol('}]}');
});
app.config(function ($routeProvider, $locationProvider) {
    $routeProvider

    //Client View
        .when('/client', {
        templateUrl: '/facesix/template/qcom/views/client',
        controller: 'ClientCtrl'
    })

    //Venue
    .when('/venue', {
            templateUrl: '/facesix/template/qcom/views/venue',
            controller: 'VenueCtrl'
        })
        .when('/venuedashboard/:sid', {
            templateUrl: function (params) {
                return '/facesix/web/site/portion/dashview?sid=' + params.sid
            },
            controller: 'VenueCtrl'
        })
        .when('/addvenue', {
            templateUrl: '/facesix/web/site/open?sid=',
            controller: 'VenueActionCtrl'
        })
        .when('/editvenue', {
            templateUrl: 'Views/editvenue.html',
            controller: 'VenueActionCtrl'
        })

    //Floor

    .when('/floor/:sid', {
            templateUrl: function (params) {
                return '/facesix/web/site/portion/list?sid=' + params.sid
            },
            controller: 'FloorCtrl'
        })
        .when('/floordashboard/:spid', {
            templateUrl: function (params) {
                return '/facesix/web/site/portion/dashboard?spid=' + params.spid
            },
            controller: 'FloorCtrl'
        })
        .when('/addfloor/:sid', {
            templateUrl: function (params) {
                return '/facesix/web/site/portion/new?sid=' + params.sid
            },
            controller: 'FloorCtrl'
        })
        .when('/drawfloor', {
            templateUrl: 'Views/drawfloor.html',
            controller: 'FloorCtrl'
        })
        .when('/configfloor/:sid/:spid/:uid', {
            templateUrl: function (params) {
                return '/facesix/web/site/portion/nwcfg?sid=' + params.sid + '&spid=' + params.spid + '&uid=' + params.uid
            },
            controller: 'topCtrl'
        })
        .when('/topology/:sid/:spid', {
            templateUrl: function (params) {
                return '/facesix/web/site/portion/map?sid=' + params.sid + '&spid=' + params.spid;
            },
            controller: 'topCtrl'
        })

    .when('/deviceconfig', {
        templateUrl: 'Views/deviceconfig.html',
        controller: 'FloorCtrl'
    })

    //Device
    .when('/devboard/:uid/:type/:spid', {
            templateUrl: function (params) {
                return '/facesix/web/site/portion/devboard?uid=' + params.uid + '&type=' + params.type + '&spid=' + params.spid
            },
            controller: 'deviceCtrl'
        })
        //Account
         .when('/account', {
                templateUrl : '/facesix/template/qcom/views/account',
                controller  : 'AccountCtrl'
            }) 
        .otherwise({
            redirectTo: '/client'
        });
});

app.run(['$rootScope', '$window', '$route', '$timeout', function ($rootScope, $window, $route, $timeout) {

    // Logout Function
    $rootScope.Logout = function () {
            //sessionStorage.removeItem(k);
            $window.sessionStorage.clear();
            var link = "/facesix/goodbye";
            $window.location.href = link;
        }
        //Network Tree Open
    $rootScope.OpenNetworkFull = function () {
        $rootScope.NetworkFull = !$rootScope.NetworkFull;
    }

    //Back function
    $rootScope.GoBack = function () {
        $window.history.back();
    }


}]);