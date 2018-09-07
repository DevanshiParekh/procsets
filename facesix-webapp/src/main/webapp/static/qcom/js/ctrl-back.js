app.controller('ClientCtrl', ['$scope', 'data', function ($scope, data) {

    $scope.init = function () {
        $scope.newCus = {};
        $scope.editCus = {};
        $scope.udata = {};
        $scope.accounts = [];
        $scope.CreateOpen = false;
        $scope.customerstep1 = true;
        $scope.stepCus1 = true;

        data.getClients().then(function successCallback(response) {
            $scope.accounts = response.data;
        }, function errorCallback(response) {

        });
    }
    $scope.init();

    $scope.$watch('newCus.serviceDurationinMonths', function (newDate) {
        var d = new Date($scope.newCus.serviceStartDate);
        d.setMonth(d.getMonth() + $scope.newCus.serviceDurationinMonths);
        d.setDate(d.getDate() - 1);
        $scope.newCus.serviceExpirydate = d;
    });
    $scope.$watch('newCus.serviceStartDate', function (newDate) {
        var d = new Date($scope.newCus.serviceStartDate);
        d.setMonth(d.getMonth() + $scope.newCus.serviceDurationinMonths);
        d.setDate(d.getDate() - 1);
        $scope.newCus.serviceExpirydate = d;
    });

    $scope.$watch('editCus.serviceDurationinMonths', function (newDate) {
        var d = new Date($scope.editCus.serviceStartDate);
        d.setMonth(d.getMonth() + $scope.editCus.serviceDurationinMonths);
        d.setDate(d.getDate() - 1);
        $scope.editCus.serviceExpirydate = d;
    });

    $scope.UpdateCustomer = function (editCus) {
        //$scope.CusStep5();
        var datas = editCus;
        data.updateClient(datas).then(function successCallback(response) {
            $scope.newCustomerOpen();
        }, function errorCallback(response) {

        })
        console.log(editCus);
    }
    $scope.newClient = function (newCus) {
        
        var datas = newCus;
        data.newClientData(datas).then(function successCallback(response) {
        	if(response.data.success == true){
        		$scope.init();
        		$scope.CusStep5();
        		$scope.getClientvalue = response.data.body;
        	}
        	
        }, function errorCallback(response) {

        })
        console.log(newCus);
    }

    $scope.singleAccount = function (value) {
        $scope.newCustomerOpen();
        $scope.EditOpen = true;
        $scope.newCus = value;
    }
    $scope.AllstepFalse = function(){
        $scope.stepCus1 = false;
        $scope.stepCus2 = false;
        $scope.stepCus3 = false;
        $scope.stepCus4 = false;
        $scope.stepCus5 = false;
        $scope.stepCus6 = false;
        $scope.stepCus7 = false;
    }
    
    $scope.newCustomerOpen = function(){
        $scope.AllstepFalse();
        $scope.newCus = {};
        $scope.EditOpen = false;
        $scope.CreateOpen = !$scope.CreateOpen;
        $scope.stepCus1 = true;
        $scope.newCus.qubercommAssist = "false";
    }
    $scope.customerstepAllFalse = function(){
        $scope.customerstep1 = false;
        $scope.customerstep2 = false;
        $scope.customerstep3 = false;
        $scope.customerstep4 = false;
    }
    $scope.CusStep1 = function(){
        $scope.AllstepFalse();
        $scope.stepCus1 = true;
        $scope.customerstepAllFalse();
        $scope.customerstep1 = true;
    }
    
    $scope.CusStep2 = function(){
        if ($scope.customerStep1.$valid) {
            $scope.errorMsgLable = false;
            $scope.AllstepFalse();
            $scope.stepCus2 = true;
            $scope.customerstep2 = true;
            $scope.customerstep3 = false;
        }
        else{
            $scope.errorMsgLable = true;
        }
    }
    
    $scope.CusStep3 = function(){
        if ($scope.customerStep2.$valid) {
            $scope.errorMsgLable = false;
            $scope.AllstepFalse();
            $scope.stepCus3 = true;
            $scope.customerstep3 = true;
            $scope.customerstep4 = false;
        }
        else{
            $scope.errorMsgLable = true;
        }
    }
    $scope.CusStep4 = function(){
        if ($scope.customerStep3.$valid) {
            $scope.errorMsgLable = false;
            $scope.AllstepFalse();
            $scope.stepCus4 = true;
            $scope.customerstep4 = true;
        }
        else{
            $scope.errorMsgLable = true;
        }
    }
    
    $scope.CusStep5 = function(){
        $scope.AllstepFalse();
        $scope.stepCus5 = true;
    }
    $scope.CusStep6 = function(){
        $scope.AllstepFalse();
        $scope.stepCus6 = true;
    }
    $scope.CusStep7 = function(){
        $scope.AllstepFalse();
        $scope.stepCus7 = true;
    }
    
    $scope.generateemail = function () {
        $scope.customerstep1 = true;
        $scope.AllstepFalse();
        $scope.stepCus6 = true;
        $scope.customerstep2 = true;
        $scope.customerstep3 = true;
        $scope.customerstep4 = true;
    }

    $scope.sendMail = function () {
        $scope.customerstep1 = true;
        $scope.AllstepFalse();
        $scope.stepCus7 = true;
        $scope.customerstep2 = true;
        $scope.customerstep3 = true;
        $scope.customerstep4 = true;
    }

    var inputFrom = document.getElementById('getcity');
    
    var autocompleteFrom = new google.maps.places.Autocomplete(inputFrom);
    console.log(autocompleteFrom);
    google.maps.event.addListener(autocompleteFrom, 'place_changed', function() {
        var place = autocompleteFrom.getPlace();
        console.log(place);
        var x = place.address_components;
        for(var i=0; i<x.length; i++){
            var y = x[i].types;
            for(var j=0; j<y.length; j++){
                if(y[j] == "administrative_area_level_1"){
                    $scope.newCus.state = x[i].long_name;
                } else if(y[j] == "administrative_area_level_2"){
                    $scope.newCus.city = x[i].long_name;
                } else if(y[j] == "country"){
                    $scope.newCus.country = x[i].long_name;
                }
            }
            
        }
        
//        $scope.user.fromLat = place.geometry.location.lat();
//        $scope.user.fromLng = place.geometry.location.lng();
//        $scope.user.from = place.formatted_address;
        $scope.$apply();
    });
    
    
    
    
    $scope.defvalSolution = [{"name":"Select", value:""}, {"name":"Gateway + Location", value:"Gateway + Location"}]
    $scope.defvalType = [{"name":"Select", value:""}, {"name":"EnterPrises", value:"Enterprises"},{"name":"Sport Arena", value:"Sport Arena"}]
    $scope.defvalPackage = [{"name":"Select", value:""}, {"name":"Gateway + Public Cloud", value:"Gateway + Public Cloud"},{"name":"Gateway + Private Cloud", value:"Gateway + Private Cloud"}]

    $scope.askDeleteCustomer=function(){
        $scope.deleteTrue = !$scope.deleteTrue;
    }
    $scope.confirmDeleteCustomer = function(newCus){
        var datas = newCus;
        data.deleteClientData(datas).then(function successCallback(response) {
            $scope.askDeleteCustomer();
            $scope.init();
            //$scope.newCustomerOpen();
        }, function errorCallback(response) {

        })
    }

}]);

app.controller('AccountCtrl', ['$scope', 'data', '$http', function ($scope, data, $http) {
    
    $scope.setFile = function (element) {
        $scope.currentFile = element.files[0];
        var reader = new FileReader();

        reader.onload = function (event) {
                $scope.image_source = event.target.result
                $scope.$apply()

            }
            // when the file is read it triggers the onload event above.
        reader.readAsDataURL(element.files[0]);
        $scope.NewProfileImg = true;
    }
    $scope.files = [];
    $scope.$on("fileSelected", function (event, args) {
        $scope.$apply(function () {            
            //add the file object to the scope's files collection
            $scope.files.push(args.file);
        });
    });
    $scope.cancelNewProfileImg = function(){
        $scope.NewProfileImg = false;
        $scope.image_source = "";
    }

    $scope.getProfileData = function () {
        data.myProfile().then(function successCallback(response) {
            $scope.profileData = response.data;
        }, function errorCallback(response) {

        });
    }

    
    
    $scope.showresetPassword = function () {
    	$scope.mpdata = {};
        $scope.myprofile = true;
        $scope.resetOpen = !$scope.resetOpen;
        $scope.errorMsgLable = false;
    }

    $scope.init = function () {
        $scope.getProfileData();
        $scope.myprofile = true;
        $scope.profileData ={};
    }
    $scope.init();

  $scope.UpdateProfile = function(value) {
	 
    if ($scope.formprofile.$valid) {
    	console.log('ok');
      var datas = value;
      data.updateMyProfile(datas).then(function successCallback(response) {
    	  $scope.profileUpdateTrue = true;
        console.log(response);
      }, function errorCallback(response) {
        console.log(response);
      });
    } else {
      $scope.errorMsgLable = true;
    }

  }
  $scope.resetPassword = function(mpdata){
      if($scope.resetPasswordForm.$valid){
    	  var datas = {};
    	  datas.p=mpdata.password;
    	  datas.cp=mpdata.confirmPassword;
          data.changePassword(datas).then(function successCallback(response) {
        	  if(response.data.success == true){
        		  //$scope.resetOpen = false;
        		  $scope.showresetPassword();
     			 $scope.successMsg = response.data.body;
     		 } else{
     			$scope.errorMsg = response.data.body;
     		 }
          }, function errorCallback(response) {
            console.log(response);
          });
    	  
          $scope.profileData.password = mpdata.password;
          $scope.profileData.confirmPassword = mpdata.confirmPassword;
         // $scope.profileData.resetPasswordTrue = true;
          var profileData = $scope.profileData;
          $scope.UpdateProfile(profileData);
      }
      else{
          $scope.errorMsgLable = true;
      }
  }



}]);
app.controller('UsermanagementCtrl', ['$scope', 'data', function($scope, data){
    
    $scope.init = function(){
        if (screen.width > 1023) {
            $scope.myprofile = true;
        }
        $scope.alluser = true;
        $scope.getGuestPass();
        $scope.getLicence();
        $scope.getRoles();
        $scope.getUsers();
        $scope.getSupport();
        $scope.getNetwork();
    }
    
     $scope.userResetPassword = function(udata){
    	 $scope.errorMsgLable = false;
    	 $scope.UserOpen = false;
    	 $scope.resetOpen = true;
    	 $scope.resetdata = udata;
     }
     $scope.closeresetPassword = function(){
    	 
    	 $scope.resetOpen = false;
    	 $scope.UserOpen = true;
    	 $scope.resetdata = {};
     }
     
     $scope.resetuserPassword = function(value){
    	 if($scope.resetPasswordForm2.$valid){
    	 var datas = {};
    	 datas.id=value.id;
    	 datas.p = value.password;
    	 datas.cp = value.confirmPassword;
    	 data.pwdUser(datas).then(function successCallback(response) {
    		 if(response.data.success == true){
    			 $scope.closeresetPassword();
    			 $scope.successMsg = response.data.body;
    		 } else{
    			 $scope.errorMsg = response.data.body;
    		 }
         }, function errorCallback(response) {

         });}
    	 else{
    		 $scope.resetdata = {};
    	 }
     }
     
     
    $scope.getGuestPass = function () {
        data.guestPass().then(function successCallback(response) {
            $scope.guestPassData = response.data;
        }, function errorCallback(response) {

        });
    }
    $scope.getNetwork = function () {
        data.network().then(function successCallback(response) {
            $scope.allowedNetData = response.data.body;
        }, function errorCallback(response) {

        });
    }

    $scope.openGP = function () {
        $scope.GPOpen = !$scope.GPOpen;
        $scope.gpdata = {};
    }

    $scope.editOpen = function (value) {
    	$scope.errorMsgLable = false;
        $scope.gpdata = value;
        $scope.GPOpen = true;
        $scope.EditOpenGP = true;
        $scope.updateGpopen = true;
    }

    $scope.updateGp = function (value) {
        if($scope.guestPassForm.$valid){
             var datas = value;
        data.updateguestPass(datas).then(function successCallback(response) {
            $scope.getGuestPass();
            $scope.GPOpen = false;
            $scope.errorMsgLable = false;
            $scope.gpdata = {};
            $scope.openGP();
        }, function errorCallback(response) {

        });
        } else{
            $scope.errorMsgLable = true; 
        } 
    }
    $scope.askDeleteGP = function(){
        $scope.gpdeleteTrue = !$scope.gpdeleteTrue;
    }
    $scope.confirmDeleteGP = function(value){
        var datas = value;
        data.deleteguestPass(datas).then(function successCallback(response) {
            $scope.getGuestPass();
            $scope.gpdata = {};
            $scope.openGP();
        }, function errorCallback(response) {

        });
    }

    $scope.newGp = function (value) {
        var datas = value;
        data.newguestPass(datas).then(function successCallback(response) {
            $scope.getGuestPass();
            $scope.GPOpen = false;
            $scope.gpdata = {};
        }, function errorCallback(response) {

        });
    }
    $scope.getSupport = function () {
        data.getSupportData().then(function successCallback(response) {
            $scope.supportData = response.data;
            console.log($scope.supportData);
        }, function errorCallback(response) {

        });
    }
    $scope.updateSupport = function (value) {
        var datas = {};
        datas.sid = value.id;
        datas.flag = value.supportFlag;
        console.log(value);
        console.log(datas);
        data.updateSupportData(datas).then(function successCallback(response) {
            $scope.getSupport();
        }, function errorCallback(response) {

        });
    }
    
    $scope.getLicence = function () {
        data.getLicenceData().then(function successCallback(response) {
            $scope.licenceData = response.data.licence;
//            $scope.progressdata.used = $scope.licenceData.usageNo;
//            $scope.progressdata.total = $scope.licenceData.totalNo;
        }, function errorCallback(response) {

        });
    }
    $scope.getClientList = function () {
        data.clientList().then(function successCallback(response) {
            $scope.clientListData = response.data.customer;
        }, function errorCallback(response) {

        });
    }
    $scope.getClientList();
    
    $scope.getRoles = function () {
        data.getRoleData().then(function successCallback(response) {
            $scope.rolesData = response.data;
        }, function errorCallback(response) {

        });
    }

    $scope.newRoles = function (value) {
        if($scope.userRoleForm.$valid){
        var datas = value;
        data.createNewRoles(datas).then(function successCallback(response) {
            $scope.getRoles();
            $scope.rolesNewData = {};
        }, function errorCallback(response) {

        });}
        else{
           $scope.errorMsgLable = true; 
        }
    }
    $scope.editRoles = function (value) {
        $scope.rolesNewData = value;
        $scope.editRoleBtn = true;
    }
    $scope.updateRoles = function (value) {
        if($scope.userRoleForm.$valid){
        var datas = value;
        data.updateNewRoles(datas).then(function successCallback(response) {
            $scope.getRoles();
            
            $scope.rolesNewData = {};
            $scope.editRoleBtn = false;
        }, function errorCallback(response) {});
        }
        else{
            $scope.errorMsgLable = true; 
        }
    }
    
    $scope.getUsers = function () {
        data.getUsersData().then(function successCallback(response) {
            $scope.usersData = response.data.users;
        }, function errorCallback(response) {

        });
    }

    $scope.updateUsers = function (value) {
        if($scope.userAdd.$valid){
        	console.log(value);
            var datas = value;
        data.updateUsersData(datas).then(function successCallback(response) {
            $scope.getUsers();
            $scope.newUser();
            $scope.udata = {};
        }, function errorCallback(response) {

        });
        }
        else{
            $scope.errorMsgLable = true;
        }
        
    }
    $scope.newUser = function () {
    	$scope.EditOpen = false;
        $scope.udata = {};
        $scope.updateUopen = false;
        $scope.UserOpen = !$scope.UserOpen;

    }
    $scope.askDeleteUser = function(){
        $scope.deleteTrue = !$scope.deleteTrue;
    }
    $scope.confirmDeleteUser = function(udata){
        var datas = udata;
        data.deleteUserData(datas).then(function successCallback(response) {
            $scope.askDeleteUser();
            $scope.getUsers();
            $scope.newUser();
        }, function errorCallback(response) {

        })
    }
    $scope.editOpenUd = function (value) {
    	$scope.EditOpen = true;
        $scope.UserOpen = true;
        $scope.udata = value;
        $scope.updateUopen = true;
    }
    
    $scope.ProfileAllFalse = function () {
        $scope.myprofile = false;
        $scope.alluser = false;
        $scope.allroles = false;
        $scope.showallsupport = false;
        $scope.showalllicense = false;
        $scope.showallnotification = false;
        $scope.showProfileMenu = true;
        $scope.showProfileMenuAll = false;
        $scope.showallgp = false;
    }

    $scope.showmyprofile = function () {
        $scope.ProfileAllFalse();
        $scope.myprofile = true;
    }
    $scope.showalluser = function () {
        $scope.ProfileAllFalse();
        $scope.alluser = true;
    }
    $scope.showallrole = function () {
        $scope.ProfileAllFalse();
        $scope.allroles = true;
    }
    $scope.shownotification = function () {
        $scope.showmyprofile();
        $scope.resetOpen = !$scope.resetOpen;
        $scope.errorMsgLable = false;
    }
    $scope.showsupport = function () {
        $scope.ProfileAllFalse();
        $scope.showallsupport = true;
    }
    $scope.showlicence = function () {
        $scope.ProfileAllFalse();
        $scope.showalllicense = true;
    }
    $scope.showgp = function () {
        $scope.ProfileAllFalse();
        $scope.showallgp = true;
    }
    $scope.showAllProfileMenu = function () {
        $scope.showProfileMenuAll = !$scope.showProfileMenuAll;
    }
    $scope.myprofiles = function () {
        $scope.ProfileAllFalse();
        $scope.showProfileMenu = false;
        $scope.showProfileMenuAll = false;

    }
    $scope.init();
    
}]);