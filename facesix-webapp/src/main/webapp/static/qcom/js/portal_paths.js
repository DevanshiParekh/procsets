var app = angular.module('myApp', ['ngRoute', 'mgcrea.ngStrap']);
app.config(function ($interpolateProvider) {
    $interpolateProvider.startSymbol('{[{');
    $interpolateProvider.endSymbol('}]}');
});
 
    
app.config(function ($routeProvider, $locationProvider) { 
	$locationProvider.html5Mode({
		enabled: true, 
		// ,requireBase: false
	}); 
    $routeProvider

    //Client View
	 
	.when('/', {
	    templateUrl: '/facesix/template/qcom/captive_portal/view_template',
	    controller: 'CPortantRegisterCtrl'
	})
    .otherwise({
        redirectTo: '/'
    });
     
});
app.run(run);
function run($rootScope, $http, $location, $timeout)
{
	
}
app.controller('CPortantRegisterCtrl', CPortantRegisterCtrl);

function CPortantRegisterCtrl($location,$window, $scope, $templateCache, $rootScope,$http, $timeout, $routeParams) { 
	var param_id = locationPath;   
	
	$scope.loaderEnabled = 'active';
	$scope.otpSend = false;
	var vm = this;   
	$scope.userForm = {};
	$scope.errorSave = false; 
	$scope.portalType = 'login';
	$scope.portalName = 'Login Form';
	$scope.portalTheme = 'registration_form'; 
    
    $scope.backgroundImg = '';
    $scope.portalType = 'login';
    
	
	$scope.backgroundImg = '';
    $scope.logoImg = '';
	 
    $http({
		method: 'GET',
		url: '/facesix/rest/captive/portal/prefferdUrl/?url='+param_id
	}).then(function(response) { 
		
		var data = response.data; 
		if(response.data != ''){
			$scope.portalName = data.portalName; 
		   // $scope.template = "/facesix/template/qcom/captive_portal/templates/action_"+$scope.portalTheme;
		    $scope.backgroundImg = data.backgroundImg; 
		    $scope.portalId = data.id; 
		    $scope.logoImg = data.logoImg;
		    $scope.bgScreenShot = data.bgScreenShot;
		    $scope.portalType = data.portalType;
		    $scope.cid = data.cid;
		    $scope.customerName = data.customerName;
		    $scope.formValues = JSON.parse(data.portalComponents); 
		    var date = new Date(); 
		    for(var i=0; i < $scope.formValues.length; i++ ){ 
		    	if($scope.formValues[i].type == "offer" || $scope.formValues[i].type == "voucher" || $scope.formValues[i].type == "add"){ 
		    		var validFrom = new Date($scope.formValues[i].validFrom);
		    		var validTo = new Date($scope.formValues[i].validTo);
		    		
		    		if(validFrom <= date && validTo >= date){
		    			$scope.formValues[i].viewStatus = true;
		    		}else{
		    			$scope.formValues[i].viewStatus = false;
		    		} 
		    	}
		    	//if($scope.formValues[i] == '')
		    	//console.log($scope.formValues[i]);
		    }
		} 
		$scope.loaderEnabled = '';
	});
    
    $scope.register = {};
	$scope.previewMode = 'active';
	$scope.otpEmpty = false;
     
	/* Submit Actions */
	$scope.erroMessage = false; 
	$scope.formSuccess = false;
	$scope.errorOtp = false;
	$scope.otpSendtomail = false;
	$otpInvalid = false;
	$scope.registerID = '';
	$scope.closeSuccess = function(){
		$scope.otpSendtomail1 = false;
	}
	$scope.closeError = function(){
		$scope.otpInvalid1 = false;
	}
	
	$scope.verifyOtp = function(){
		$scope.otpSendtomail = true;
		$scope.otpEmpty = false;
		console.log($scope.register.otp);
		if($scope.register.otp == undefined || $scope.register.otp == ''){
			$scope.otpEmpty = true;
			return false;
		}
		else{
			$scope.loaderEnabled = 'active';
			console.log('wewe'); 
			$scope.validation  = {
				id: $scope.registerID,
				otp: $scope.register.otp,
			};  
			console.log($scope.validation);
			$http({
				method: 'POST',
				url: '/facesix/rest/portal/users/validateOTP',  
				headers: {'Content-Type': 'application/json'},   
				data: JSON.stringify($scope.validation)
			}).then(function(response) { 
				console.log(response);
				if (response.status == 200) {
					if(response.data.body != 'invalidOTP'){ 
						$scope.otpInvalid = false;
						$scope.otpInvalid1 = false;
						location = 'https://www.google.com'; 
		    			
					}else{
						$scope.formSuccess = false;
						$scope.otpInvalid = true;
						$scope.otpInvalid1 = true;
					}
					
				} else {
					$scope.errorOtp = true;
					$scope.otpInvalid = true;
				}
				$scope.loaderEnabled = '';
			});
		}
		
		
	} 
	$scope.submitForm = function(){  
		$scope.valnow = location.search.split("&")[0].replace("?","").split("=")[1];
		$scope.curMacVal = $scope.valnow.replace("/","");		
		$scope.otpEmpty = false;
		$scope.otpInvalid = false; 
		$scope.register.cid = $scope.cid;
		$scope.register.portalId = $scope.portalId;
		$scope.register.customerName = $scope.customerName;
		$scope.register.uid = $scope.curMacVal;
		console.log(JSON.stringify($scope.register)); 
		$scope.loaderEnabled = 'active';
		
		$http({
    		method: 'POST',
    		url: '/facesix/rest/portal/users/save',  
    		headers: {'Content-Type': 'application/json'},   
    		data: JSON.stringify($scope.register)
    	}).then(function(response) { 
    		console.log(response);
    		if (response.status == 200) {
    			$scope.registerID = response.data.id;
    			if($scope.registerID != ''){
    				$scope.otpSendtomail = true;
    				$scope.otpSendtomail1 = true;
    				$scope.otpSend = true; 
    			} else{
    				$scope.erroMessage = true;
    			}
			} else {
				 
			}
    		$scope.loaderEnabled = '';
        });  
		
	}
	 
}
/* Set url is trusted */
app.filter('trustUrl', ['$sce', function ($sce) {
  return function(url) {
    return $sce.trustAsResourceUrl(url);
  };
}]);

/* Apply content filter for dymanic html */
app.filter('trustContent', ['$sce', function ($sce) {
	  return function(content) {
	    return $sce.trustAsHtml(content);
	  };
	  
}]);
