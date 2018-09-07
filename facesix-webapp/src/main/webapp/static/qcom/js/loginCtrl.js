 var app = angular.module('myApp', []);
 app.config(function ($interpolateProvider) {
	    $interpolateProvider.startSymbol('{[{');
	    $interpolateProvider.endSymbol('}]}');
	});
        app.controller('loginCtrl', ["$scope", function($scope){
            $scope.urlPath = "/facesix/static/qcom/";
            $scope.resetShow = function(){
                $scope.showReset = true;
            }
            $scope.resetHide = function(){
                $scope.showReset = false;
                $scope.resetTrue = false;
            }
            $scope.resetPassword = function(){
                $scope.resetTrue = true;
            }
        }]);