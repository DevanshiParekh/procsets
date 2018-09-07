app.controller('navigation', navigation);
app.controller('header', header);
function navigation($location, $scope, $templateCache, $rootScope,$http, $timeout, data) { 
	$scope.getClass = function (path) {
		var cur_path = $location.path().substr(0, path.length);
		if (cur_path == path) {
			if($location.path().substr(0).length > 1 && path.length == 1 )
				return "";
			else
				return "active";
		} else {
			return "";
		}
	} 
	
	
}
function header($location, $scope, $templateCache, $rootScope,$http, $timeout, data) { 
	$scope.pageTitle = 'Qubercomm | Compute Connect Cloud';
	$scope.favicon = '';
	data.myProfile().then(function successCallback(response) {
		var customerData = response.data;
		$scope.namedood = response.data.fname + " " + response.data.lname ;
		$http({
			method: 'GET',
			url: '/facesix/rest/customer/paramValue?cid='+customerData.customerId 
		}).then(function(response) {  
			 if(response.data.pref_url){
				pref_url = response.data.customerName;
				$scope.pageTitle = response.data.customerName; 
				$scope.favicon = response.data.logofile;
			 } 
		});
		
	}, function errorCallback(response) {

	});
	
}
app.controller('ClientCtrl', ['$http', '$scope', 'data', function ($http, $scope, data) {

	 
    $scope.init = function () {
        $scope.newCus = {};
        $scope.editCus = {};
        $scope.udata = {};
        $scope.accounts = [];
        $scope.CreateOpen = false;
        $scope.customerstep1 = true;
        $scope.stepCus1 = true;
        $scope.getZone ={};
        

        data.getClients().then(function successCallback(response) {
            $scope.accounts = response.data.customer;
        }, function errorCallback(response) {

        });
        
        $scope.timezones = data.timezone();
        data.timezone().then(function successCallback(response) {
            $scope.getZone = response.data.timezone;
           	$scope.newCus.timezone  = "Asia/Calcutta"
        	
           // console.log("Response" + $scope.newCus.timezone);
        }, function errorCallback(response) {

        });
    } 
    
    $scope.init();

    $scope.$watch('newCus.serviceDurationinMonths', function (newDate) {
        var d   = new Date($scope.newCus.serviceStartDate);
        var dur = parseInt($scope.newCus.serviceDurationinMonths);
        d.setMonth(d.getMonth() + dur);
        d.setDate(d.getDate() - 1);
        $scope.newCus.serviceExpiryDate = d;
    });
    
    $scope.$watch('newCus.serviceStartDate', function (newDate) {
        var d = new Date($scope.newCus.serviceStartDate);
        var dur = parseInt($scope.newCus.serviceDurationinMonths);
        d.setMonth(d.getMonth() + dur);
        d.setDate(d.getDate() - 1);
        $scope.newCus.serviceExpiryDate = d;
    });
    
    $scope.emptyToken = function(oauth){ 
    	if(oauth == 'false'){
    		$scope.newCus.restToken = ''; 
    		$scope.newCus.jwtrestToken = ''; 
    		$scope.newCus.mqttToken = ''; 
    		$scope.newCus.jwtmqttToken = ''; 
    	}
    	
    } 
    $scope.$watch('editCus.serviceDurationinMonths', function (newDate) {
        var d = new Date($scope.editCus.serviceStartDate);
        var dur = parseInt($scope.editCus.serviceDurationinMonths);
        d.setMonth(d.getMonth() + dur);
        d.setDate(d.getDate() - 1);
        $scope.editCus.serviceExpiryDate = d;
    });

    $scope.UpdateCustomer = function (editCus) {
        //$scope.CusStep5();
        var file = $scope.myFile
        var logo = editCus.get_logo
        var background = editCus.background_image
        console.log(background);
       // console.log('file is ' + JSON.stringify(file));
        //console.log('logo is ' + JSON.stringify(logo));
        // console.log('-------------background is -------------' + JSON.stringify(background));
       // console.dir(file);
        //var formData=new FormData();
        //formData.append("file", file);
        //editCus.keyFile = formData;
       var cert =$scope.myCert;
       var cid 	=editCus.id;
     console.log("---------cust edit------------"+JSON.stringify(editCus));
      // console.log("cust cid>"+JSON.stringify(cid));
        var datas = editCus;
        console.log(editCus);
        $scope.preloadTrue = true;
        console.log(datas);
        data.updateClient(datas).then(function successCallback(response) {
        	console.log(" before posting  cid "+cid);
        	$scope.newCustomerOpen();
        	cid = datas.id;
        	console.log(" after posting datas "+cid);
        	var res = data.UploadCust(file,cert,logo,background,cid);
        	console.log(res);
        	
            $scope.messageShow(response);
            $scope.preloadTrue = false;
           // $scope.getUsers();
            console.log("success");
            
           
        	data.getClients().then(function successCallback(response) {
                $scope.accounts = response.data.customer;
                var myEl = angular.element( document.getElementsByClassName( 'card' ) );
            	myEl.removeClass("editmode");
            }, function errorCallback(response) {

            });
        	
            
        }, function errorCallback(response) {

        })
        console.log(editCus);
    }
    /*
    $scope.checkUserDuplicate = function(value){ 
		$http({
	   		method: 'GET',
	   		url: '/facesix/rest/customer/duplicateCustomerName?customerName='+value 
	   	}).then(function(response) {
	   		console.log(response.data.body)
	            if(response.data.body === 'new'){
	            	 
	            }else{ 
	            	$scope.newCus.customerName.errors == true; 
	            	$scope.newCus.customerName ="User Name already exists";
	           }
	   	}); 
	} 
     */
    
    $scope.GenerateToken = function(newCus){ 
    	console.log(newCus);
    	$scope.newToken = false;
    	// $scope.newCus.restToken = 'lSIr<1Mytc+S)zrj$T"dHG6-ys}(=cguG)wE+T:Tlw{D45xE:t)]fvjNg$%L6n~';
    	
    	var postData = {name: newCus.customerName, address: newCus.address};
     
    	
    	$http({
    		method: 'POST',
    		url: '/facesix/rest/token/restToken', 
    		headers: {'Content-Type': undefined}, 
    		data: postData
    	}).then(function(response) {
    		
            if(response.status == 200){
            	$scope.newCus.restToken = response.data.restToken;
            	$scope.newCus.jwtrestToken = response.data.jwtrestToken;
            }
            else{
            	$scope.newCus.restToken = '';
            	$scope.newCus.jwtrestToken ='';
            }
       });
    	
    	
    }
    
    $scope.AskGenerateNewToken = function(){
    	$scope.newToken = true;
    }
    
    $scope.GenerateNewTokenClose = function(){
    	$scope.newToken = false;
    }
    
    $scope.GenerateMqtt_token = function(newCus){ 
    	$scope.newMqtt_token = false;
    	// $scope.newCus.mqttToken = 'lSIr<1Mytc+S)zrj$T"dHG6-ys}(=cguG)wE+T:Tlw{D45xE:t)]fvjNg$%L6n~';
    	var postData = {name: newCus.customerName, postalCode: newCus.postalCode};
    	
    	$http({
    		method: 'POST',
    		url: '/facesix/rest/token/mqttToken', 
    		headers: {'Content-Type': undefined}, 
    		data: postData 
    	}).then(function(response) {
    		console.log(response.status);
            if(response.status == 200){
            	$scope.newCus.mqttToken = response.data.mqttToken;
            	$scope.newCus.jwtmqttToken = response.data.jwtmqttToken;
            }
            else{
            	$scope.newCus.mqttToken = '';
            	$scope.newCus.jwtmqttToken = '';
            }
       });
    	
    	
    }
    
    $scope.AskGenerateNewMqtt_token = function(){
    	$scope.newMqtt_token = true;
    }
    
    $scope.GenerateNewMqtt_tokenClose = function(){
    	$scope.newMqtt_token = false;
    }
    
 
    $scope.newClient = function (newCus) {
        
        var datas = newCus;
        console.log(datas);
       // $scope.CusStep7();
        $scope.preloadTrue = true; 
        
    
        var file = $scope.myFile
        console.log('file is ' + JSON.stringify(file));
        console.dir(file);
        
        
        var cert =$scope.myCert;
        var logo = newCus.get_logo;
        var background = newCus.background_image;
        console.log(background); 
        console.log(logo);  
        data.newClientData(datas).then(function successCallback(response) {
        	
            $scope.messageShow(response);
            $scope.preloadTrue = false;
        	if(response.data.success == true){
        		var cid =response.data;
            	var custid = cid.id;
            	console.log(" cid  " + JSON.stringify(custid));
            	console.log(" -------logo ---------> " + logo + "-------- background------- "+background);
            	var res = data.UploadCust(file,cert,logo,background,custid);
            	console.log(res);
            	$scope.newCustomerOpen();
        		$scope.init();
        		//$scope.CusStep7();
        		$scope.getClientvalue = response.data.body;
        		$scope.newCus = {};
        	}
        	
        }, function errorCallback(response) {

        })
        console.log(newCus);  
    }
    
    $scope.clearType = function() {
    	
    	if($scope.newCus.venueType == 'Patient-Tracker' || $scope.newCus.venueType == 'Locatum'){
    		$scope.newCus.noOfGateway     = null;
    		$scope.newCus.threshold 	  = "50";
            $scope.newCus.tagcount  	  = "500";
            $scope.newCus.tagInact  	  = "60";
            $scope.newCus.preferedUrlName = ""; 
            $scope.newCus.solution        = "GeoFinder";
    	}
    	if($scope.newCus.venueType == 'Gateway'){
    		$scope.newCus.tagcount 		  = null;
    		$scope.newCus.threshold 	  = null;
    		$scope.newCus.noOfGateway 	  = "1";
    		$scope.newCus.tagInact  	  = null;
    		$scope.newCus.solution        = "Gateway";
    	}
    	 $scope.errorMsgLable = false;
    	 
    }

   $scope.solutionType = function(){
	   if($scope.newCus.solution == 'GatewayFinder' && $scope.newCus.venueType == 'Gateway'){
   		   $scope.newCus.noOfGateway  		= null;
   		   $scope.newCus.threshold 	  		= "50";
           $scope.newCus.tagcount  	  		= "500";
           $scope.newCus.tagInact  	  		= "60";
           $scope.newCus.preferedUrlName 	= ""; 
           $scope.newCus.noOfGateway 	  	= "1";
   	}
   }
    
    $scope.singleAccount = function (value, e) {
    	
    	console.log(e);
    	var myEl = angular.element( document.getElementById( e ) );
    	myEl.addClass("editmode"); 
    	$scope.newToken = false; 
    	$scope.newMqtt_token = false; 
    	
        $scope.newCustomerOpen();
        $scope.EditOpen = true;
        $scope.newCus 	= value;
        $scope.ActivecustomerName = value.customerName;
        $scope.ActivecustomerName = value.customerName;
        $scope.ActivecustomerEmail = value.email;
        $scope.customerStep1.$valid = true;
        $scope.customerStep1.customerName.$setValidity('urlAvailable', true);  

         
    }
    $scope.updatelog = function (customer) {
    	
        var datas 	= {};
    	//console.log ("Update log " + customer)
    	$scope.newCus = customer;
    	//console.log ("Update log " + $scope.newCus.logs);
    	
        datas.id 	= customer.id;
        datas.logState	= $scope.newCus.logs;
        
    	//data.cloudlog("<<<<" + $scope.newCus.logs);
    	
        data.cloudlog(datas).then(function successCallback(response) {
            //console.log(response.data); 
        }, function errorCallback(response) {
        	//console.log(response.data);
        });
    }    


	$scope.updateVpn = function (customer) {
    		
        var datas 	= {};
    	$scope.newCus = customer;
    	//console.log ("Update vp " + $scope.newCus.vpn);
    	
        datas.id 	= customer.id;
        datas.vpnState	= $scope.newCus.vpn;
        
        data.openVpn(datas).then(function successCallback(response) {
           // console.log("response >>>" + JSON.stringify(response.data)); 
        }, function errorCallback(response) {
        	//console.log(response.data);
        });
    }    
    
    
    $scope.AllstepFalse = function(){
        $scope.stepCus1 = false;
        $scope.stepCus2 = false;
        $scope.stepCus3 = false;
        $scope.stepCus4 = false;
        $scope.stepCus5 = false;
        $scope.stepCus6 = false;
        $scope.stepCus7 = false;
        $scope.stepCus8 = false;
    }
    
    $scope.newCustomerOpen = function(objectClose){
    	 
    	$scope.newCus.restToken 	= '';
    	$scope.newCus.jwtrestToken 	= '';
    	$scope.newCus.mqttToken 	= '';
    	$scope.newCus.jwtmqttToken 	= '';
    	
    	$scope.newToken = false;
    	$scope.newMqtt_token = false;
        $scope.AllstepFalse();
        $scope.newCus = {};
        $scope.EditOpen = false;
        $scope.EditOpen = false;
        
        // var myEl = angular.element( document.getElementById( e ) );
    	// myEl.addClass("editmode");
        
        if(objectClose == 'close'){
        	console.log(objectClose);
        	var myEl = angular.element( document.getElementsByClassName( 'card' ) );
        	myEl.removeClass("editmode");
        }
    	
        $scope.CreateOpen = !$scope.CreateOpen;
        $scope.stepCus1 = true;
        $scope.customerstep1 = true;
        $scope.customerstep2 = false;
        $scope.customerstep3 = false;
        $scope.customerstep4 = false;
        $scope.customerstep5 = false;
        $scope.customerstep6 = false;
        $scope.customerstep7 = false;
        $scope.newCus.qubercommAssist = "false";
        $scope.newCus.threshold="20";
        $scope.newCus.tagcount="500";
        $scope.errorMsgLable = false;
        $scope.newCus.oauth = "false"; 
        
        console.log($scope.EditOpen)
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
    	$scope.newCus.alexa = "false";
    	
    	 
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
    	$scope.customerstep5 = false;
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
	    if ($scope.customerStep4.$valid) {
	    	$scope.errorMsgLable = false;
	        $scope.AllstepFalse();
	        $scope.stepCus5 = true;
	        $scope.customerstep5 = true; 	        
	    }
	    else{
	        $scope.errorMsgLable = true;
	    }
    }
    $scope.CusStep6 = function(){
	    if ($scope.customerStep5.$valid) {
	        $scope.AllstepFalse();
	        $scope.stepCus6 = true;  
		    if ($scope.newCus.get_logo != undefined) {
				$scope.newCus.logofile = $scope.newCus.get_logo.name;
			}
			if ($scope.newCus.background_image != undefined) {
				$scope.newCus.background = $scope.newCus.background_image.name;
			}
		}
	    else{
	        $scope.errorMsgLable = true;
	    }
    }
    
  /*  $scope.CusStep6 = function(){
        $scope.AllstepFalse();
        $scope.stepCus6 = true;
    } */
    
    $scope.CusStep7 = function(){
        $scope.AllstepFalse();
        $scope.stepCus7 = true;
    }
    
    $scope.CusStep8 = function(){
        $scope.AllstepFalse();
        $scope.stepCus8 = true;
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
        $scope.stepCus8 = true;
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
    
    
    
    
    $scope.defvalSolution = [{"name":"Select", value:""}, {"name":"Gateway", value:"Gateway"}]
    $scope.defgeoSolution = [{"name":"GeoLocation", value:"GeoLocation"}]
    $scope.defretailSolution = [{"name":"Retail/Carrier", value:"Retail"}]
    $scope.deffinderSolution = [{"name":"GeoFinder", value:"GeoFinder"}]
    $scope.deffindergateSolution = [{"name":"Gateway + Finder", value:"GatewayFinder"}]
    $scope.defheatmapSolution = [{"name":"Heatmap", value:"Heatmap"}]
    
    $scope.defvalType = [{"name":"Select", value:""}, {"name":"Patient-Tracker", value:"Patient-Tracker"},{"name":"Locatum", value:"Locatum"},{"name":"Gateway",value:"Gateway"}]
    $scope.defvalPackage = [{"name":"Select", value:""}, {"name":"Public Cloud", value:"Public Cloud"},{"name":"Private Cloud", value:"Private Cloud"}]

    $scope.askDeleteCustomer=function(){
        $scope.deleteTrue = !$scope.deleteTrue;
    }
    $scope.confirmDeleteCustomer = function(newCus){
    	var datas = {};
        datas.customerId=newCus.id
        $scope.preloadTrue = true;
        data.deleteClientData(datas).then(function successCallback(response) {
             $scope.messageShow(response);
             $scope.preloadTrue = false;
            $scope.askDeleteCustomer();
            $scope.init();
            //$scope.newCustomerOpen();
        }, function errorCallback(response) {

        })
    }
    $scope.messageShow = function(response){
    	console.log(response);
        if(response.data.success == true){
            $scope.successMsg = response.data.body;
        } else{
            $scope.errorMsg = response.data.body;
        }
        //setTimeout(function(){$scope.successMsg = ""; $scope.errorMsg="";}, 3000);
    }

    $scope.uploadFile = function() {
      var file = $scope.myFile;

      console.log('file is ');
      console.dir(file);

      var uploadUrl = "/fileUpload/save";
      //fileUpload.uploadFileToUrl(file, uploadUrl);
    }

    $scope.checkEmailDuplicate = function(value){
      	console.log("hey")
      	$scope.ActivecustomerEmail
	  	var enteredval = value;
  		var dbval = $scope.ActivecustomerEmail;
  		if(enteredval == dbval){
	    	
    	} else {
      	data.checkEmailDuplicate(value).then(function successCallback(response) {
      		var result = response.data.data;
      		console.log(result);
      		if (result == "new"){
      			$scope.mymail = {
          		        "border-color" : "lightgray",        		        
          		    }   
      			$scope.myVar= false;
      		} else {
      			$scope.mymail = {
      					 "border-color" : "red",
          		    }  
      			
      				$scope.newCus.email ="Email ID already exists";
          			$scope.myVar= true;
      		}
      		
         }, function errorCallback(response) {
      	   console.log("error");
         });
    	}
      } 

}]);

app.controller('AccountCtrl', ['$scope', 'data', '$http', '$timeout' , function ($scope, data, $http, $timeout) {
    
	
  /* $http({
          method: 'POST',
          url: 'CreateProducts',
          headers: {
              "Content-Type": undefined
          },
          data: fd

      }) 
      .success(function (response) {
          $scope.names = response;
      })

      .error(function (response) {
          console.log(response);
      });
   */
	$scope.subMenuActive = false;
	$scope.subMenuChange = function() {
	    $scope.subMenuActive = !$scope.subMenuActive;
    }
	$scope.deleteImageTrue = false;
	$scope.askImageDelete = function(){
		$scope.deleteImageTrue = !$scope.deleteImageTrue;
	} 
	  
    $scope.setFile = function (element) {
    	
    	 
        
        var reader = new FileReader(); 
        reader.onload = function (event) {
                $scope.image_source = event.target.result
                $scope.$apply()

        }
            // when the file is read it triggers the onload event above.
        reader.readAsDataURL(element.files[0]);
        
         var formData=new FormData();
         formData.append("profilepic", element.files[0]); //key
         formData.append("userid", $scope.profileData.id);  
        $http({
    		method: 'POST',
    		url: '/facesix/rest/user/profileupload', // The URL to Post.
    		headers: {'Content-Type': undefined}, // Set the Content-Type to undefined always.
    		data: formData,
    		transformRequest: function(data, headersGetterFunction) {
    			console.log(data); 
    			return data;
    		} 
    	}).then(function(response) {
             if(response.data.code == 200){
            	 $scope.currentFile = element.files[0];
            	 $scope.NewProfileImg = true;
            	 var myEl = angular.element( document.querySelector( '.ui-success' ) );
            	 myEl.addClass('active');
            	 $timeout( function(){
            		 myEl.removeClass('active');
                 }, 1000 );
            	 
             }else{
            	var myEl = angular.element( document.querySelector( '.ui-error' ) );
            	myEl.addClass('active');
            	$timeout( function(){
           		 	myEl.removeClass('active');
                }, 1000 );
            }
        });        
        /*$http({
            method: 'POST',
            url: '/rest/user/profileupload',
            headers: {
                "Content-Type": undefined
            },
            data: { userid: $scope.profileData.id, profilepic: element.files[0] }

        }).then(function(response) {
             console.log(response);
        });;
        */
        /*$http.post('/user/', { username: '', file: element.files[0] })
		.success(function (response) {  
			 console.log(response);
		});
        */
 
    }
    $scope.files = [];
    $scope.$on("fileSelected", function (event, args) {
        $scope.$apply(function () {            
            //add the file object to the scope's files collection
            $scope.files.push(args.file);
        });
    });
    $scope.cancelNewProfileImg = function(){
    	$scope.deleteImageTrue = false;
    	console.log($scope.subMenuActive);
        var formData=new FormData();
        formData.append("profilepic", ''); //key
        formData.append("userid", $scope.profileData.id); 
        $http({
    		method: 'POST',
    		url: '/facesix/rest/user/profileupload', // The URL to Post.
    		headers: {'Content-Type': undefined}, // Set the Content-Type to undefined always.
    		data: formData,
    		transformRequest: function(data, headersGetterFunction) {
    			console.log(data);
        		return data;
    		}
    	}).then(function(response) {
    		
            if(response.data.code == 200){
            	$scope.NewProfileImg = false;
            	$scope.image_source = ""; 
            	var myEl = angular.element( document.querySelector( '.ui-success' ) );
            	myEl.addClass('active');
            	$timeout( function(){
           		 	myEl.removeClass('active');
                }, 1000 );
            }
            else{
            	var myEl = angular.element( document.querySelector( '.ui-error' ) );
            	myEl.addClass('active');
            	$timeout( function(){
           		 	myEl.removeClass('active');
                }, 1000 );
            }
       });
    }

    $scope.getProfileData = function () {
        data.myProfile().then(function successCallback(response) {
            $scope.profileData = response.data; 
            $scope.profileDbData = response.data.email;
           console.log($scope.profileData.imgpath);
            if($scope.profileData.imgpath != ''){
            	$scope.NewProfileImg = true;
            	$scope.image_source = '/facesix/web/account/profilepic?userid='+$scope.profileData.id;
            }else{
            	$scope.NewProfileImg = false;
            	$scope.image_source = '';
            }
            
        }, function errorCallback(response) {

        });
       
    }
 
    $scope.showresetPassword = function () {
    	$scope.mpdata = {};
        $scope.myprofile = true;
        $scope.resetOpen = !$scope.resetOpen;
        $scope.errorMsgLable = false;
        $scope.passwordValidate = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    }
    
    $scope.remadmin = function (){
    	$scope.mpdata = {};
        $scope.myprofile = true;
        $scope.resetOpen = !$scope.resetOpen;
        $scope.errorMsgLable = false;
        $scope.passwordValidate = false;
    }
    
    $scope.showabout = function () {
    	$scope.mpdata = {};
        $scope.myprofile = true;
        $scope.aboutOpen = !$scope.aboutOpen;
        $scope.errorMsgLable = false;
    }
    
    $scope.init = function () {
        $scope.getProfileData();
        $scope.myprofile = true;
        $scope.profileData ={};
    }
    $scope.init();
    $scope.messageShow = function(response){
    	//console.log(response);
        if(response.data.success == true){
            $scope.successMsg = response.data.body;
        } else{
            $scope.errorMsg = response.data.body;
        }
        setTimeout(function(){$scope.successMsg = ""; $scope.errorMsg="";}, 3000);
    }

  $scope.UpdateProfile = function(value) {
	 
    if ($scope.formprofile.$valid) {
    	$scope.preloadTrue = true;
      var datas = value;
      data.updateMyProfile(datas).then(function successCallback(response) {
           $scope.messageShow(response);
    	  $scope.profileUpdateTrue = true;
          $scope.preloadTrue = false;
      //  console.log(response);
      }, function errorCallback(response) {
       // console.log(response);
          $scope.preloadTrue = false;
      });
    } else {
      $scope.errorMsgLable = true;
    }

  }
  $scope.resetPassword = function(mpdata){
      if($scope.resetPasswordForm.$valid){
          $scope.preloadTrue = true;
    	  var datas = {};
    	  datas.p=mpdata.password;
    	  datas.cp=mpdata.confirmPassword;
          data.changePassword(datas).then(function successCallback(response) {
              $scope.preloadTrue = false;
               $scope.messageShow(response);
        	  if(response.data.success == true){
        		  //$scope.resetOpen = false;
        		  $scope.showresetPassword();
     			 $scope.successMsg = response.data.body;
     		 } else{
     			$scope.errorMsg = response.data.body;
     		 }
          }, function errorCallback(response) {
          //  console.log(response);
              $scope.preloadTrue = false;
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

$scope.checkEmailDuplicate = function(value){
	  
	  $scope.profileDbData;
	  	var enteredval = value;
  		var dbval =  $scope.profileDbData;
  		//console.log("enterred " + enteredval)
  		//console.log("from db " + dbval)
  		if(enteredval == dbval){
	    	
    	} else {
  	data.checkEmailDuplicate(value).then(function successCallback(response) {
  		var result = response.data.data;
  		console.log(result);
  		if (result == "new"){
  			$scope.mymail = {
      		        "border-color" : "lightgray",        		        
      		    }   
  			$scope.myVar= false;
  		} else {
  			$scope.mymail = {
  					 "border-color" : "red",
      		    }  
  			
  				$scope.profileData.email ="Email ID already exists";
      			$scope.myVar= true;
  		}
  		
     }, function errorCallback(response) {
  	   console.log("error");
     });
    }
}

}]);
app.controller('UsermanagementCtrl', ['$http','$scope', 'data','$filter', function($http,$scope, data,$filter){
     
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
        $scope.getCustiactiveData();
        $scope.getSupportMail();
    }
    
     $scope.userResetPassword = function(udata){
    	 
    	 $scope.UserOpen = false;
    	 $scope.resetOpen = true;
    	 $scope.resetdata = udata;
    	 udata.password = '';  
	     $scope.resetdata.confirmPassword = ''; 
    	 $scope.passwordValidate = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
     }
     $scope.closeresetPassword = function(){
    	 
    	 $scope.resetOpen = false;
    	 $scope.UserOpen = true;
    	 $scope.resetdata = {};
    	 $scope.passwordValidate = false;
    	 $scope.errorMsgLable = false;
     }
     
     $scope.messageShow = function(response){
    	// console.log(response);
        if(response.data.success == true){
            $scope.successMsg = response.data.body;
        } else{
            $scope.errorMsg = response.data.body;
        }
        setTimeout(function(){$scope.successMsg = ""; $scope.errorMsg="";}, 3000);
    }
     $scope.getCheckedFalse = function(){
    	    return false;
    	};

    	$scope.getCheckedTrue = function(){
    	    return true;
    	};
     $scope.resetuserPassword = function(value){
    	 if($scope.resetPasswordForm2.$valid){
             $scope.preloadTrue = true;
    	 var datas = {};
    	 datas.id=value.id;
    	 datas.p = value.password;
    	 datas.cp = value.confirmPassword;
    	 data.pwdUser(datas).then(function successCallback(response) {
              $scope.messageShow(response);
             $scope.preloadTrue = false;
    		 if(response.data.success == true){
    			 $scope.closeresetPassword();
    			 $scope.success = response.data.body;
    			 $scope.resetdata = {};
    		 } else{
    			 $scope.errorMsg = response.data.body;
    			 $scope.resetdata = {};
    		 }
         }, function errorCallback(response) {

         });}
    	 else{
    		 $scope.resetdata = {};
    		 $scope.errorMsgLable = true;
    	 }
     }
     
     
    $scope.getGuestPass = function () {
        data.guestPass().then(function successCallback(response) {
            $scope.guestPassData = response.data;
        }, function errorCallback(response) {

        });
  }

  $scope.sendByprint = function(value) {
    var gpPrintData = {};
    $scope.gpPrintData = value;
    var guestpassexpireson = $filter('date')($scope.gpPrintData.guestpassexpireson, 'dd-MM-yyyy');

    var myTable= "<table style='border-collapse: collapse;width:100%' border='2px'><tr><td> Name </td>";
    		    myTable+= "<td style=' border: 1px solid black;'> Mobile No</td>";
    		    myTable+="<td> Allowed Network</td>";
    		    myTable+="<td> No of Device</td>";
    		    myTable+="<td> Expired Date</td>";
    		    myTable+="<td> Status</td></tr>";
    		    myTable+="<tr><td>";
    		  for (var i=0; i <1; i++) {
    		    myTable+= $scope.gpPrintData.passName + "</td>"
    		    +"<td>" + $scope.gpPrintData.mobileNumber + "</td>"
    		    +"<td>" + $scope.gpPrintData.allowedNetwork + "</td>"
    		    +"<td >" + $scope.gpPrintData.noOfdevices + "</td>"
    		    +"<td>" + guestpassexpireson + "</td>"
    		    +"<td>" + $scope.gpPrintData.passStatus + "</td></tr>";
    		  }
			    myTable += "</table>";
			    newWin = window.open("");
			    newWin.document.write(myTable);
			    newWin.print();
			    newWin.close();
  }



  $scope.getCustiactiveData = function () {
        data.getcustinactiveData().then(function successCallback(response) {
            $scope.custinactiveData = response.data.inactivecustomer;
            console.log($scope.custinactiveData);
        }, function errorCallback(response) {

        });
    }
    
    $scope.updateCustData = function (value) {
        var datas = {};
        datas.customerId = value.id;
        data.updateCustData(datas).then(function successCallback(response) {
             $scope.messageShow(response);
            $scope.getCustiactiveData();
            $scope.getLicence();
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
        $scope.updateGpopen = false;
        $scope.gpdeleteTrue = false;
        $scope.deleteTrue = false;
        $scope.EditOpenGP = false;
        $scope.getGuestPass();
        $scope.gpdata = {};
        $scope.mobileValidate = /^[0-9]{10}$/;
        $scope.emailValidate = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
    }
    
    $scope.closeGP = function () {
        $scope.GPOpen = !$scope.GPOpen;
        $scope.updateGpopen = false;
        $scope.gpdeleteTrue = false;
        $scope.deleteTrue = false;
        $scope.EditOpenGP = false;
        $scope.getGuestPass();
        $scope.gpdata = {};
        $scope.errorMsgLable = false;
        $scope.mobileValidate = false;
        $scope.emailValidate = false;
    }

    $scope.editOpen = function (value) {
    	//console.log("edit " +JSON.stringify(value));
    	$scope.errorMsgLable = false;
        $scope.gpdata = value;
        $scope.GPOpen = true;
        $scope.EditOpenGP = true;
        $scope.updateGpopen = true;
    }

    $scope.updateGp = function (value) {
    	 if($scope.guestPassForm.$valid){
        $scope.preloadTrue = true;
         var datas = value;
    data.updateguestPass(datas).then(function successCallback(response) {
         $scope.messageShow(response);
        $scope.preloadTrue = false;
        $scope.getGuestPass();
        $scope.GPOpen = false;
        $scope.EditOpenGP = false;       
        $scope.errorMsgLable = false;
        $scope.gpdata = {};
    }, function errorCallback(response) {

    });
    }else{
		  $scope.preloadTrue = false;
        $scope.errorMsgLable = true;
	 }
   
}
    $scope.askDeleteGP = function(){
        $scope.gpdeleteTrue = !$scope.gpdeleteTrue;
    }
    $scope.confirmDeleteGP = function(value){
        var datas = value;
        data.deleteguestPass(datas).then(function successCallback(response) {
             $scope.messageShow(response);
            $scope.getGuestPass();
            $scope.gpdata = {};
            $scope.openGP();
        }, function errorCallback(response) {

        });
    }
        
    $scope.newGp = function (value) {
    	 if($scope.guestPassForm.$valid){
 	   $scope.preloadTrue = true;
     var datas = value;
     data.newguestPass(datas).then(function successCallback(response) {
     	  $scope.messageShow(response);
           $scope.preloadTrue = false;
         $scope.getGuestPass();
         $scope.GPOpen = false;
        $scope.gpdata = {};
     }, function errorCallback(response) {

     });
    	 }else{
   		  $scope.preloadTrue = false;
          $scope.errorMsgLable = true;
   	 }
 }

    $scope.updateGuestStatus = function (value) {
        var datas = {};
        datas.sid = value.id;
        datas.flag = value.supportFlag;
        data.updateGuestStatus(datas).then(function successCallback(response) {
             $scope.messageShow(response);
            $scope.getGuestPass();
        }, function errorCallback(response) {

        });
    }

    $scope.getSupport = function () {
        data.getSupportData().then(function successCallback(response) {
             $scope.messageShow(response);
            $scope.supportData = response.data.support;
        }, function errorCallback(response) {

        });
    }
    $scope.updateSupport = function (value) {
        var datas = {};
        datas.id = value.id;
        datas.flag = value.qubercommAssist;
        data.updateSupportData(datas).then(function successCallback(response) {
             $scope.messageShow(response);
            $scope.getSupport();
        }, function errorCallback(response) {

        });
    }
    
    $scope.custSupportEmailEnable = function (value) {
           	  
    		  var datas = {};
    	        
    	      datas.cid  = value.cid;
    	      datas.flag = value.support;
    	       
    	       $http({
    	    		method: 'POST',
    	    		url: '/facesix/rest/customer/emailsupport', 
    	    		headers: {'Content-Type': 'application/json'},
    	    		data: JSON.stringify(datas)
    	    	}).then(function(response) {
    	    		//console.log("response" + JSON.stringify(response))
    	    	
    	       });
    }
    
    $scope.supportEmailEdit = function (value) {
    	if($('#support').is(':checked') == true){
    		$('#edit').show();
    		$('#editV').text("EDIT");
    	} else {
    		$('#edit').hide();
    		$('#editV').text("");
    	}
    }
    
    $scope.updateEditMail = function (value) {
    	if($('#edit').is(':checked') == true){
    		$('#host').prop('disabled',false);
        	$('#port').prop('disabled',false);
        	$('#username').prop('disabled',false);
        	$('#password').prop('disabled',false);
    	} else {
    		$('#host').prop('disabled',true);
        	$('#port').prop('disabled',true);
        	$('#username').prop('disabled',true);
        	$('#password').prop('disabled',true);
    	}
        
    }
    
    $scope.getLicence = function () {
        data.getLicenceData().then(function successCallback(response) {
            $scope.licenceData = response.data.licence;
            console.log(response.data); 
//            $scope.progressdata.used = $scope.licenceData.usageNo;
//            $scope.progressdata.total = $scope.licenceData.totalNo;
        }, function errorCallback(response) {

        });
    }
  
    
    $scope.getSupportMail = function () {
        data.getSupportMailData().then(function successCallback(response) {
            $scope.supportMailData = response.data;
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
            //console.log(response);
        }, function errorCallback(response) {

        });
    }

    $scope.newRoles = function (value) {
        if($scope.userRoleForm.$valid){
            $scope.preloadTrue = true;
        var datas = value;
        data.createNewRoles(datas).then(function successCallback(response) {
             $scope.messageShow(response);
            $scope.getRoles();
            $scope.preloadTrue = false;
            $scope.rolesNewData = {};
        }, function errorCallback(response) {

        });}
        else{
           $scope.errorMsgLable = true; 
            $scope.preloadTrue = false;
        }
    }
    $scope.editRoles = function (value) {
        $scope.rolesNewData = value;
        $scope.editRoleBtn = true;
    }
    $scope.updateRoles = function (value) {
        if($scope.userRoleForm.$valid){
            $scope.preloadTrue = true;
        var datas = value;
        data.updateNewRoles(datas).then(function successCallback(response) {
             $scope.messageShow(response);
            $scope.getRoles();
            $scope.preloadTrue = false;
            $scope.rolesNewData = {};
            $scope.editRoleBtn = false;
        }, function errorCallback(response) {});
        }
        else{
            $scope.errorMsgLable = true; 
            $scope.preloadTrue = false;
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
            $scope.preloadTrue = true;
            var datas = value;
        data.updateUsersData(datas).then(function successCallback(response) {
        	$scope.EditOpen = false;
            $scope.updateUopen = false;
            $scope.UserOpen = !$scope.UserOpen;
             $scope.messageShow(response);
             $scope.getUsers();
            
            //$scope.newUser();
            //$scope.udata = {};
            $scope.preloadTrue = false;
        }, function errorCallback(response) {

        });
        }
        else{
            $scope.preloadTrue = false;
            $scope.errorMsgLable = true;
        }
        
    }
    
    $scope.createSupportmail = function (value) {
    	$('#nameDisp').text("");
    	
    	if($scope.userAdd.$valid){
            $scope.preloadTrue = true;
            var datas = value;     
            $scope.errorMsgLable = false;
        } else{
        	$scope.errorMsgLable = true;
        }      
    	 $scope.errorMsgLable = false;
		
		if($('#support').is(':checked') == true){
			var support = true;
		} else {
			var support = false;
		}
		
		var host = $('#host').val();
		var port = $('#port').val();
		var user = $('#username').val();
		var pass = $('#password').val();
		
		if(host == ""){
			$('#host').css('border-color','red');
		} else {
			$('#host').css('border-color','#ccc');
		}	
		if(port == ""){
			$('#port').css('border-color','red');
		} else {
			$('#port').css('border-color','#ccc');
		}	
		if(user == ""){
			$('#username').css('border-color','red');
		} else {
			$('#username').css('border-color','#ccc');
		}	
		if(pass == ""){
			$('#password').css('border-color','red');
		} else {
			$('#password').css('border-color','#ccc');
		}
		if(host != "" &&port != "" &&user != "" &&pass != ""){
			
			$('#mailData').prop("disabled",true)
		    $("#mailData").append($("<i class='fa fa-spinner fa-spin' style='color: #0083a3;font-size: 28px;margin-left: 30px;margin-top:5px;'></i>"));
			
			data.myProfile().then(function successCallback(response) {
				
	            $scope.preloadTrue = true;
	    		$scope.finalOutput = { 
	    				 cid: response.data.customerId, 
	    	             host     : $('#host').val(),
	    	             port     : $('#port').val(),
	    	             username : $('#username').val(),
	    	             password : $('#password').val(),
	    	             support  : support,
	    		};

	        	$http({
	        		method: 'POST',
	        		url: '/facesix/rest/customer/updateSupportDetails', 
	        		headers: {'Content-Type': 'application/json'},
	        		data: JSON.stringify($scope.finalOutput)
	        	}).then(function(response) {
	        		 if(response.data.code == 200){
	        			 $scope.errorMsgLable = false;
	        			 $('#nameDisp').text("Update Success !").css("color","green");
	        			 $('#mailData').prop("disabled",false);
	        		} else {	        			
	        			$('#nameDisp').text("Authentication Failure !").css("color","red");
	        			$('#mailData').prop("disabled",false);
	        		}
	           });
	     
	    	});
		}
		} 
    
    
    $scope.newUser = function () {
    	$scope.EditOpen = false;
        $scope.udata = {};
        $scope.updateUopen = false;
        $scope.UserOpen = !$scope.UserOpen;
        $scope.errorMsgLable = false;
        $scope.passwordValidate = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
        $scope.mobileValidate = /^[0-9]{10}$/;
        $scope.emailValidate =/^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
        $scope.zipCode = /^(\d{5}-\d{4}|\d{5})$/;
        $scope.onlyText = /^[a-zA-Z ]+$/;
    }
    
    $scope.rem = function (){
  	  	$scope.EditOpen = false;
        $scope.udata = {};
        $scope.updateUopen = false;
        $scope.UserOpen = !$scope.UserOpen;
        $scope.errorMsgLable = false;
  	  	$scope.passwordValidate = false;
  	  	$scope.mobileValidate = false;
  	  	$scope.emailValidate = false;
  	  	$scope.zipCode =false;
  	  	$scope.onlyText = false;
  	  	$scope.mymail = {
		        "border-color" : "lightgray",        		        
		}   
  	    $scope.udata.email = {};
    }
  
    $scope.reload = function()
    {
       location.reload(); 
    }
    $scope.askDeleteUser = function(){
        $scope.deleteTrue = !$scope.deleteTrue;
    }
    $scope.confirmDeleteUser = function(udata){
        var datas = udata;
        data.deleteUserData(datas).then(function successCallback(response) {
             $scope.messageShow(response);
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
        $scope.curEval = value.email;
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
        $scope.showallIA = false;
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
        $scope.getSupportMail();
     
    	data.myProfile().then(function successCallback(response) {
    		var cid = response.data.customerId;
    		if(cid != undefined){
    			$http({
    	 	 		method: 'GET',
    	 	 		url: '/facesix/rest/customer/supportDetails?cid='+cid,
    	 	 		headers: {'Content-Type': 'application/json'},
    	 	 	}).then(function(response) { 
    	 	 		 if(response.status == 200){
    	 	 			var resData = response.data;
    	 	 			var ss = resData[0].support;
    	 	 			
    	 	 			if(ss == "true"){
    	 	 				$( "#support" ).prop( "checked", true );
    	 	 				
    	 	 			} else {
    	 	 				$( "#support" ).prop( "checked", false );
    	 	 			}
    	 	 				var pass = resData[0].password;
        	 	 			$('#host').val(resData[0].host);
        	        		$('#port').val(resData[0].port);
        	        		$('#username').val(resData[0].username);
        	        		$('#password').val(pass);  
    	 	 			
    	 	 			  	        		
    	        		$('#host').prop('disabled',true);
    	            	$('#port').prop('disabled',true);
    	            	$('#username').prop('disabled',true);
    	            	$('#password').prop('disabled',true);
    	            	
    	            	if($('#support').is(':checked') == true){
    	            		$('#edit').show();
    	            		$('#editV').text("EDIT");
    	            	} else {
    	            		$('#edit').hide();
    	            		$('#editV').text("");
    	            		$( "#support" ).prop( "checked", false );
    	            	}
    	            	
    	 	 		 }
    	 	 		 
    	 	 	});
    		} 
    		
    	});
        
    }
    $scope.showlicence = function () {
        $scope.ProfileAllFalse();
        $scope.showalllicense = true;
    }
    $scope.showgp = function () {
        $scope.ProfileAllFalse();
        $scope.showallgp = true;
    }
    $scope.showIA = function () {
        $scope.ProfileAllFalse();
        $scope.showallIA = true;
    }
    $scope.showAllProfileMenu = function () {
        $scope.showProfileMenuAll = !$scope.showProfileMenuAll;
       
    }
    $scope.myprofiles = function () {
        $scope.ProfileAllFalse();
        $scope.showProfileMenu = false;
        $scope.showProfileMenuAll = false;

    }
    $scope.deactivateLicense = function (value) {
        var datas = {};
        //console.log("value "+value.cid);
        datas.id = value.cid;
        data.deactivateLicense(datas).then(function successCallback(response) {
             $scope.messageShow(response);
             $scope.getLicence();
             $scope.getCustiactiveData();
        }, function errorCallback(response) {

        });
    }
    
    $scope.checkEmailDuplicate = function(value){
    	console.log("hey++++")
    	$scope.curEval;
		var enteredval = value;
    	var dbval = $scope.curEval;
    	console.log("enterred " + enteredval)
    	console.log("from db " + dbval)
    	
    	if(enteredval == dbval){
    		    	
    	} else {
    	data.checkEmailDuplicate(value).then(function successCallback(response) {
    		var result = response.data.data;
    		console.log(result);
    		if (result == "new"){
    			$scope.myVar= false;
    		} else {
    				$scope.udata.email ="Email ID already exists";
        			$scope.myVar= true;
        			$scope.mymail = {
       					 "border-color" : "red",
           		    }  
    		}
    		
       }, function errorCallback(response) {
    	   console.log("error");
       });
    	}
    }
    
  
    
    $scope.init();
      
}]);

app.directive('usernameAvailable', function($timeout, $q, $http) {
 return {
   restrict: 'AE',
   require: 'ngModel',
   link: function(scope, elm, attr, model) { 
   	
   	elm.bind('blur', function () {
   	
   	var defer = $q.defer(); 
           if(scope.EditOpen == true){ 
            model.$setValidity('username', true);
            console.log(scope.ActivecustomerName);
           	var data = {customerName: elm[0].value};
           	if(elm[0].value != scope.ActivecustomerName && scope.ActivecustomerName != 'undefined' && scope.ActivecustomerName != ''){
           	$http({
           	  method: 'GET',
           	  url: '/facesix/rest/customer/duplicateCustomerName?customerName='+elm[0].value
           	  }).then(function(response) {
           	  console.log(response.data.body)
           	           if(response.data.body === 'new'){
           	            model.$setValidity('username', true);

           	           }else{
           	            model.$setValidity('username', false);  
           	          }
           	  }); 
           	} 
           	
           } else{
             
           	var data = {customerName: elm[0].value};
               $http({
       	  method: 'GET',
       	  url: '/facesix/rest/customer/duplicateCustomerName?customerName='+elm[0].value
       	  }).then(function(response) {
       	  console.log(response.data.body)
       	           if(response.data.body === 'new'){
       	            model.$setValidity('username', true);

       	           }else{
       	            model.$setValidity('username', false); 
       	           	elm[0].value ="Customer Name already exists";
       	          }
       	  });     
           }  
           return defer.promise;
       });
   	
 
   	
   }
 } 
});

/*app.directive('usernameAvailable', function($timeout, $q, $http) {
  return {
    restrict: 'AE',
    require: 'ngModel',
    link: function(scope, elm, attr, model) { 
    	
    	elm.bind('blur', function () {
    		var defer = $q.defer(); 
            if(scope.EditOpen == true){ 
            	model.$setValidity('username', true);
            } else{
              
            	var data = {customerName: elm[0].value};
                $http({
        	   		method: 'GET',
        	   		url: '/facesix/rest/customer/duplicateCustomerName?customerName='+elm[0].value 
        	   	}).then(function(response) {
        	   		console.log(response.data.body)
        	            if(response.data.body === 'new'){
        	            	model.$setValidity('username', true);

        	            }else{
        	            	model.$setValidity('username', false); 
        	            	elm[0].value ="Customer Name already exists";
        	           }
        	   	});     
            }  
            return defer.promise;
        });
    	 
  
    	
    }
  } 
});*/


app.directive('urlAvailable', function($timeout, $q, $http) {
	  return {
	    restrict: 'AE',
	    require: 'ngModel',
	    link: function(scope, elm, attr, model) { 
	    	elm.bind('blur', function () { 
		        var defer = $q.defer();
		          
		        if(scope.EditOpen == true){ 
		        	model.$setValidity('url', true);
		        } else{  
		        	var data = {customerName: elm[0].value};
		            $http({
		    	   		method: 'GET',
		    	   		url: '/facesix/rest/customer/duplicateUrlName?preferredUrl='+elm[0].value 
		    	   	}).then(function(response) {
		    	   		console.log(response.data.body)
		    	   		if(response.data.body === 'new'){
		    	   			model.$setValidity('url', true);
	    	            }else{
	    	            	model.$setValidity('url', false); 
	    	            	elm[0].value ="Preferred URL already exists";
	    	           }
		    	   	});   
		           
		        } 
		         
		        return defer.promise;
	    	});
	    }
	  } 
});

 