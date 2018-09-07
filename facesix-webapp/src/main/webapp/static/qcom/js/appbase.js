var app = angular.module('myApp', ['ngRoute', 'chart.js', 'mgcrea.ngStrap', 'as.sortable','angularjs-datetime-picker']);
app.config(function ($interpolateProvider) {
    $interpolateProvider.startSymbol('{[{');
    $interpolateProvider.endSymbol('}]}');
});

app.directive('fileModel', ['$parse',
      function($parse) {
        return {
          restrict: 'A',
          link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function() {
              scope.$apply(function() {
                modelSetter(scope, element[0].files[0]);
              });
            });
          }
		};
	}
]);
    
app.config(function ($routeProvider, $locationProvider) {
    $routeProvider

    //Client View
	    .when('/client', {
	    templateUrl: '/facesix/template/qcom/views/client',
	    controller: 'ClientCtrl'
	})

	.when('/myaccount', {
	    templateUrl: '/facesix/template/qcom/views/account',
	    controller: 'AccountCtrl'
	})
	.when('/usermanagement', {
	    templateUrl: '/facesix/template/qcom/views/usermanagement',
	    controller: 'UsermanagementCtrl'
	})
	.when('/captiveportal', {
	    templateUrl: '/facesix/template/qcom/captive_portal/index',
	    controller: 'CPortantCtrl'
	})
	.when('/captiveportal/edit/:path', {
	    templateUrl: '/facesix/template/qcom/captive_portal/edit_template',
	    controller: 'CPortantEditCtrl'
	})
	.when('/captiveportal/add/:path', {
	    templateUrl: '/facesix/template/qcom/captive_portal/add_template',
	    controller: 'CPortantAddCtrl'
	})
	.when('/captiveportal/customizelogin/:path', {
	    templateUrl: '/facesix/template/qcom/captive_portal/add_template',
	    controller: 'CPortantAddCtrl'
	})
	.when('/captiveportal/add/login/:path', {
	    templateUrl: '/facesix/template/qcom/captive_portal/add_login_template',
	    controller: 'CPortantLoginAddCtrl'
	})
	.when('/captiveportal/edit/login/:path', {
	    templateUrl: '/facesix/template/qcom/captive_portal/edit_login_template',
	    controller: 'CPortantLoginEditCtrl'
	})
	.when('/captiveportal/view/:path', {
	    templateUrl: '/facesix/template/qcom/captive_portal/view_template',
	    controller: 'CPortantViewCtrl'
	})
	.when('/captiveportal/view/login/:path', {
	    templateUrl: '/facesix/template/qcom/captive_portal/view_template',
	    controller: 'CPortantLoginViewCtrl'
	}) 
    .otherwise({
        redirectTo: '/client'
    });
});

app.run(['$rootScope', '$window', '$route', '$timeout','data', '$http', function ($rootScope, $window, $route, $timeout,data,$http) {

	var pref_url = '';
	data.myProfile().then(function successCallback(response) {
		var customerData = response.data;
		$http({
			method: 'GET',
			url: '/facesix/rest/customer/paramValue?cid='+customerData.customerId 
		}).then(function(response) { 
			 if(response.data.pref_url){
				pref_url = response.data.pref_url;
				 console.log(pref_url);
			 } 
		});
		
	}, function errorCallback(response) {

	});
	
	
    // Logout Function
    $rootScope.Logout = function () {
            //sessionStorage.removeItem(k);
            $window.sessionStorage.clear(); 
            var link = "/facesix/goodbye";
            if(pref_url != ''){
            	link = "/facesix/goodbye/"+pref_url;
            }
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
    
    
    $rootScope.getRoles = function(){
    	data.getRolesData().then(function successCallback(response){
    		console.log(response);
    		$rootScope.roles = response.data;
    		console.log($rootScope.roles);
    	}, function errorCallback(response){
    		
    	});
    }
    $rootScope.getRoles();
    
 /*  $rootScope.getRoles =function(){
		    var val=$rootScope.roles.ACC_READ;
			$("#accprofile :input").attr("disabled",val);
			//$("#accprofile").find("input,button,textarea,select").attr("disabled",  $rootScope.roles.ACC_READ);
   }*/
   
    $rootScope.passwordValidate = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    $rootScope.mobileValidate = /^[0-9]{10}$/;
    $rootScope.emailValidate = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
    $rootScope.zipCode = /^(\d{5}-\d{4}|\d{5})$/;
    $rootScope.onlyText = /^[a-zA-Z ]+$/;
      
}]);