app.controller('CPortantCtrl', CPortantCtrl);
app.controller('CPortantEditCtrl', CPortantEditCtrl);
app.controller('CPortantAddCtrl', CPortantAddCtrl);
app.controller('CPortantViewCtrl', CPortantViewCtrl);
app.controller('CPortantLoginAddCtrl', CPortantLoginAddCtrl);
app.controller('CPortantLoginEditCtrl', CPortantLoginEditCtrl);
app.controller('CPortantLoginViewCtrl', CPortantLoginViewCtrl); 

var portVal ='';

function CPortantCtrl($location, $scope, $templateCache, $rootScope,$http, $timeout, data, $compile) { 
	
	
	
	$scope.loaderEnabled = 'active';
	/* Load Created Portals */
	$scope.active_portals = [];
	
	/* Load Default templates */
	$scope.casting = [];
	/*$scope.casting = [
		{id: 0, name: 'Screenshot', screenshot: 'http://localhost:8175/facesix/static/qcom/img/QLoginPage4.jpg', url: 'http://localhost:8175/facesix/static/qcom/img/QLoginPage4.jpg', type: 'http://localhost:8175/facesix/static/qcom/img/QLoginPage4.jpg' },
		{id: 0, name: 'including multi-line ellipsis', screenshot: 'http://localhost:8175/facesix/static/qcom/img/QLoginPage4.jpg', url: 'http://localhost:8175/facesix/static/qcom/img/QLoginPage3.jpg', type: 'http://localhost:8175/facesix/static/qcom/img/QLoginPage3.jpg' }
	]; */
	$scope.cids = '';
	data.myProfile().then(function successCallback(response) {
		var customerData = response.data;
		$scope.cid = customerData.customerId;    
		$scope.cids = customerData.customerId;
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) { 
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 			console.log($scope.casting);
 	 		 }
 	 	});
		
		if($scope.cid == '' || $scope.cid == undefined){  	
			$scope.templates = [
				{id: "0", name: "Captive Portal", url: '', editText: 'Create New Portal', isActive: 1, bgScreenShot: "/facesix/static/qcom/img/captive_portals/regform.jpg"},
				{id: "1", name: "Login Form ", url: 'login', editText: 'Create New Login', isActive: 1, bgScreenShot: "/facesix/static/qcom/img/captive_portals/loginform.jpg"} 
			];
			$scope.cusList = '';
			data.clientList().then(function successCallback(response) {
				 if(response.data.customer != undefined && response.data.customer.length > 0){
					 $scope.cusList = response.data.customer; 
				 }
				
			}, function errorCallback(response) {

			});
			
		}else{
			$http({
	 	 		method: 'GET',
	 	 		url: '/facesix/rest/captive/portal/existingPortalType/?cid='+$scope.cid,  
	 	 		headers: {'Content-Type': 'application/json'} 
	 	 	}).then(function(response) {
	 	 		 if(response.status == 200){
	 	 			$scope.templates = response.data;
	 	 		 }
	 	 	});  
		}
		
	}, function errorCallback(response) {

	});
	
	
 	
	
 	$scope.loadPortals = function(){
 		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/portal/list/',  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) {
 	 		 if(response.status == 200){ 
 	 			$scope.active_portals = response.data;
 	 			$scope.loaderEnabled = '';
 	 		 }
 	 	});
 	};
 	
 	$scope.selectTab = function(val){
 		$scope.finalVal = val;
 		portVal = $scope.finalVal;
 		console.log(" selected Tab val " +portVal)
 		return portVal;
 	};

 	$scope.loadPortals();
 	 
 	/* Quick Tabs Start */

 	if(portVal == 'a'){
 		$scope.tab = 1;
 	} else {
 		$scope.tab = 3;
 	}

    $scope.setTab = function(newTab){
      $scope.tab = newTab;
    };

    $scope.isSet = function(tabNum){
      return $scope.tab === tabNum;
    };
    /* Quick Tabs End */
    
    /* Remove Action start */
    $scope.remove = 100000;
    $scope.setRemove = function(newTab){
	    $scope.remove = newTab;
	};
	
    $scope.isRemove = function(tabNum){
	    return $scope.remove === tabNum;
    };
	  
    
    /* Remove Action end */
    
    /* Remove Portals */
    $scope.SuccessDetete = false;
    $scope.errorDeteted = false;
    $scope.CastingSuccessDetete = false;
    $scope.CastingerrorDeteted = false;
    $scope.CastingSuccessUpload = false;
    $scope.CastingerrorUpload = false;
    
    
    $scope.removePortals = function(id){
    	var data = {
    		id: id
    	};
    	$http({
    		method: 'POST',
    		url: '/facesix/rest/captive/portal/delete/',  
    		headers: {'Content-Type': 'application/json'}, 
    		data: JSON.stringify(data)
    	}).then(function(response) {
    		if(response.status == 200){
    			$scope.loadPortals();
    			$scope.SuccessDetete = true;
    			
    		}else{
    			$scope.errorDeteted = true;
    		}
    	});   
    	$scope.remove = 100000;
    }
    
    $scope.loadCastingList = function(){
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) {
 	 		console.log(response);
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 		 }
 	 	});
	}
    
    $scope.removeCasting = function(id){
    	var data = {
			path: id 
    	};
    	$http({
    		method: 'POST',
    		url: '/facesix/rest/captive/casting/castingDelete',  
    		headers: {'Content-Type': 'application/json'}, 
    		data: JSON.stringify(data)
    	}).then(function(response) {
    		console.log(response);
    		if(response.status == 200){
    			$scope.loadCastingList();
    			$scope.CastingSuccessDetete = true;
    			
    		}else{
    			$scope.CastingerrorDeteted = true;
    		}
    	});   
    	$scope.remove = 100000;
    }
    $scope.filePopUp = false;
    $scope.previewPopUp = false;
    $scope.previewType = '';
    $scope.previewPath = '';
    $scope.showPreview = function(path, type){
    	$scope.previewPath = path;
    	$scope.previewPopUp = true;
    	$scope.previewType = type;
    }
    $scope.closepreviewPopUp = function(){
    	$scope.previewPopUp = false; 
    } 
    $scope.filename = '';
    $scope.addNewFilePopUp = function(){
    	$scope.filePopUp = true; 
    	$scope.filename = '';
    	$scope.jsonFile = '';
    }
    $scope.closeNewFilePopUp = function(){
    	$scope.filePopUp = false; 
    }  
    
	$scope.changeCid = function(cid){
		$scope.cid = cid; 
	}
	$scope.fileChoose = '';
	$scope.jsonFile = {};
	$scope.setFileTitle = function(element){ 
		
		var reader = new FileReader();  
    	$scope.formData = new FormData();
    	$scope.filename = element.files[0].name
    	document.getElementById("filename").value = element.files[0].name; 
    	$scope.$apply();
    	  
    	
        reader.onload = function (event) { 
        	$scope.jsonFile = {
        		file: event.target.result,
        		name: $scope.filename,
        		lastModified: element.files[0].lastModified,
        		size: element.files[0].size,
        		type: element.files[0].type,
        		cid: $scope.cid
        	}; 
        	$scope.dataVideo = event.target.result;
        	var myEl = angular.element( document.querySelector( '#videoIS' ) ); 
    		myEl.remove();
		    var image = new Image(); 
		    
		    image.src = event.target.result;
		    var canvas = document.getElementById('thecanvas'); 
		    var ctx = canvas.getContext('2d');  
		    ctx.clearRect(0, 0, canvas.width, canvas.height);

        	var fitImageOn = function(canvas, imageObj) {
        		var imageAspectRatio = imageObj.width / imageObj.height;
        		var canvasAspectRatio = canvas.width / canvas.height;
        		var renderableHeight, renderableWidth, xStart, yStart;

        		// If image's aspect ratio is less than canvas's we fit on height
        		// and place the image centrally along width
        		if(imageAspectRatio < canvasAspectRatio) {
        			renderableHeight = canvas.height;
        			renderableWidth = imageObj.width * (renderableHeight / imageObj.height);
        			xStart = (canvas.width - renderableWidth) / 2;
        			yStart = 0;
        		}

        		// If image's aspect ratio is greater than canvas's we fit on width
        		// and place the image centrally along height
        		else if(imageAspectRatio > canvasAspectRatio) {
        			renderableWidth = canvas.width
        			renderableHeight = imageObj.height * (renderableWidth / imageObj.width);
        			xStart = 0;
        			yStart = (canvas.height - renderableHeight) / 2;
        		}

        		// Happy path - keep aspect ratio
        		else {
        			renderableHeight = canvas.height;
        			renderableWidth = canvas.width;
        			xStart = 0;
        			yStart = 0;
        		}
        		ctx.drawImage(imageObj, xStart, yStart, renderableWidth, renderableHeight);
        	};
        	
            // get the canvas context for drawing
		   
		    
		    if (canvas.getContext) {
		    	if(element.files[0].type.indexOf('image') !== -1){
		    		image.onload = function() {  
			    		fitImageOn(canvas, image);
			    		var dataURL = thecanvas.toDataURL();
			    		$scope.jsonFile.screenshot = dataURL; 
				    	// $scope.formData.append("screenshot", dataURL); 
			    	}  
		    		
		    	}else if(element.files[0].type.indexOf('video') !== -1){
		    		
		    		$scope.temps = '<video muted autoplay id="videoIS" style="width: 282px; height: 190px;" height="190"><source src="'+$scope.dataVideo+'" type="video/mp4">Video not supported</video>'
		    		$scope.temp1 = $compile($scope.temps)($scope);
		    		var myEl = angular.element( document.querySelector( '.preview-section' ) );  
		    		myEl.append($scope.temp1); 
		    		var v = document.getElementById('videoIS');
		    		
		    		v.addEventListener('play', function(){
		    			$timeout(function(){
		    				ctx.drawImage(v,0,0,canvas.width,canvas.height);
		    				var dataURL = thecanvas.toDataURL();
		    				$scope.jsonFile.screenshot = dataURL; 
		    		    	// $scope.formData.append("screenshot", dataURL);
		    		    	// console.log("Screensho: - " + dataURL);
		    			});
		    	    },false);
		    		
		    	}
		    	
		    	
		    }  
		    $scope.$apply();
		    console.log($scope.jsonFile);
		    
        } 
        reader.readAsDataURL(element.files[0]); 
         
       
        
	}
	$scope.uploadLoader = false;
	
	$scope.submitFile = function(){ 
		$scope.jsonFile.name = document.getElementById("filename").value;
		// console.log($scope.jsonFile);
		$scope.uploadLoader = true;
		 $http({
    		method: 'POST',
    		url: '/facesix/rest/captive/casting/castingUpload',  
    		headers: {'Content-Type': 'application/json'},  
    		data: JSON.stringify($scope.jsonFile) 
    	}).then(function(response) {
    		 console.log(response);
             if(response.data.code == 200){
            	 $scope.jsonFile = '';
            	 $scope.CastingSuccessUpload = true;
            	 $scope.loadCastingList();
             }else{
            	 $scope.CastingerrorUpload = true;
            }
            $scope.filePopUp = false; 
            $scope.uploadLoader = false;
        });    
	}
}

function CPortantEditCtrl($location,$window, $scope, $templateCache, $rootScope,$http, $timeout, data, $routeParams) {
	 
	 
	$scope.filesError = true;
	$scope.loaderEnabled = '';
	
	$scope.EditModeId = '';
	$scope.cid = '';
	 
	
	$scope.errorSave = false;
	var i;
    $scope.itemsList = {
        components: [],
        advancedcomponents: [] 
    };
    
	/* Advanced components */
    $scope.itemsList.advancedcomponents=[{id:100,type:"offer",field_icon:"tags",name:"test",value:"",isDefault:0,placeholder:"Input Field",content:"Offer Information",align:"left",font_size:14,label:"Offer Code",inputType:"text",validFrom:"",validTo:"",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:101,type:"add",field_icon:"tags",name:"test",value:"",isDefault:0,placeholder:"Input Field",content:"Advertisement details here...",align:"left",addVertisementLink:"#",font_size:14,label:"Advertisement",validFrom:"",validTo:"",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:80,w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"}];
    
    /* Normal components */
    $scope.itemsList.components=[{id:0,type:"input",field_icon:"input",name:"test",value:"",isDefault:0,placeholder:"Input Field",content:"Submit Text",align:"left",font_size:14,label:"Input Field",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:3,type:"text",field_icon:"text",name:"test",value:"",isDefault:"",placeholder:"Text Field",content:"Your content here...",align:"center",font_size:14,label:"Text Field",inputType:"p",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#000000",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:1,type:"textarea",field_icon:"textarea",name:"test",value:"",isDefault:"",placeholder:"Textarea",content:"",align:"left",font_size:14,label:"Textarea",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:2,type:"image",field_icon:"file-image-o",name:"test",value:"",isDefault:"",placeholder:"Image Field",content:"Submit",align:"center",font_size:14,label:"Image Field",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#ffffff",borderStyle:"solid",bgColor:"transparent",textColor:"#ffffff",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:4,type:"video",field_icon:"file-video-o",name:"video",isDefault:"",value:"",placeholder:"video",videoPath:"",align:"center",font_size:14,label:"video",inputType:"p",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#000000",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:300,w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:5,type:"voucher",field_icon:"tags",name:"voucher",value:"",isDefault:"",placeholder:"Voucher",content:"",align:"center",font_size:14,label:"Voucher",inputType:"",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:0,paddingBottom:10,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#000000",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:7,type:"terms",field_icon:"file-image-o",name:"test",value:"",isDefault:"",linkColor:"#000000",placeholder:"Image Field",content:'Please review and accept our <a href="javascript:void(0);" target="_blank">Terms &amp; Conditions</a> for access to Wi-Fi. ',align:"center",font_size:14,label:"Terms",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#ffffff",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:8,type: "social",field_icon: "share-alt",name: "test",value: "",isDefault: "",linkColor: "#000000",placeholder: "Image Field",content: "",align: "center",font_size: 14,label: "Social",inputType: "text",wrapperClass: "",imagePath: "",paddingTop: 0,paddingRight: 0,paddingBottom: 0,paddingLeft: 0,marginTop: 0,marginRight: 0,marginBottom: 0,marginLeft: 0,borderWidth: 0,borderRadius: 0,mediaStyle: "round",showText: 0,socialMedias: [{name: "facebook",url: "https://www.facebook.com/",status: true},{name: "twitter",url: "https://twitter.com/",status: true},{name: "googleplus",url: "https://plus.google.com/",status: true},{name: "linkedin",url: "https://www.linkedin.com/",status: true},],borderColor: "#fff",borderStyle: "solid",bgColor: "transparent",textColor: "#000",field_width: "",field_height: "",w_paddingTop: 10,w_paddingRight: 0,w_paddingBottom: 10,w_paddingLeft: 0,w_marginTop: 0,w_marginRight: 0,w_marginBottom: 0,w_marginLeft: 0,w_borderWidth: 0,w_borderRadius: 0,w_borderColor: "#f0f0f0",w_borderStyle: "solid",w_bgColor: "#ffffff"}];
    
    
	/* Quick Tabs Start */
	$scope.tab = 1;

    $scope.setTab = function(newTab){
      $scope.tab = newTab;
    };

    $scope.isSet = function(tabNum){
      return $scope.tab === tabNum;
    };
    /* Quick Tabs End */
    
    /* Accordian */
    $scope.acc = 1;
    $scope.setAcc = function(newTab){
    	if($scope.acc == newTab){
    		$scope.acc = 100000;
    	}
    	else{
    		$scope.acc = newTab;
    	}
	    
	};
	
    $scope.isSets = function(tabNum){
	    return $scope.acc === tabNum;
    };
	  
    
    /* Accordian */
	 
    /* Remove Action start */
    $scope.remove = 100000;
    $scope.setRemove = function(newTab){
	    $scope.remove = newTab;
	};
	
    $scope.isRemove = function(tabNum){
	    return $scope.remove === tabNum;
    };
	  
    
    /* Remove Action end */
     
	 
	/* Color Themes Start */ 
	$scope.activeSkin = 0;
	$scope.activeSkinColor = {
		color1: '#5f9018',
		color2: '#89b923'
	};
	
	
	$scope.changeTemp = function(cid){
		
		//console.log("change temp edit")
		var cid = $scope.cid;
			
			var associatedVal = $scope.associationTemp;
			
			console.log("associatedVal" + associatedVal)
			console.log("cid" + cid)
			
			if(associatedVal == "sid"){
				var associatedwith = "sid";	
				$scope.dButton     = false;
			} else if(associatedVal == "spid"){
				var associatedwith = "spid";
				$scope.dButton = false;
			} else {
				var associatedwith = cid;
				$scope.dButton = true;
			}
					
		 var url= "/facesix/rest/captive/portal/associationList?cid="+cid+"&associatedwith="+associatedwith
		// console.log(url);
	    	$http({
	    		method: 'GET',
	    		url: url, 
	    		headers: {'Content-Type': 'application/json'}
	    		
	    	}).then(function(response) {
	    		console.log(JSON.stringify(response.data));
	    			/*$scope.choose = (response.data);
	    			console.log($scope.choose)*/
	    			 var toAppend = '';
	    			 $('#sessions').empty();
		             $.each(response.data,function(i,o){
		             toAppend += '<option value=' + o.id + ' >'+ o.uid + '</option>';
		            });
		           $('#sessions').append(toAppend);
	           
	       });
	    	
	
	}
	
	/* Preview Class */ 
	
    $scope.setEditOptions = function(tab, item){ 
    	 
    	$scope.tab = tab;
    	$scope.singleField = item;
    	
    }
	var param_id = $routeParams.path;
	
	
	$scope.activate= function(index){
		console.log(index);
		$scope.index=index;
	};
	$scope.removeField = function(item){
		var index =$scope.formValues.indexOf(item);
		$scope.formValues.splice(index, 1);   
		$scope.tab = 1; 
		$scope.remove = 100000;
		$scope.checkRegistration();
	} 
     
    $scope.backgroundImg = '';
    $scope.portalType = 'registration';
    

	/* Change Skin color */
    $scope.colorPalate = function(tabNum, color1, color2){ 
    	$scope.activeSkinColor.color1 = color1;
    	$scope.activeSkinColor.color2 = color2;
    	$scope.activeSkin = tabNum;
    	
    	for (var i = 0; i < $scope.formValues.length; i++)
        { 
    		if($scope.formValues[i].type == 'registration'){
    			$scope.formValues[i].w_bgColor = color1; 
    		}
    		if($scope.formValues[i].type == 'login'){
    			$scope.formValues[i].w_bgColor = color1; 
    		}
    		if($scope.formValues[i].type == 'text'){
    			$scope.formValues[i].w_bgColor = color2; 
    		}
    		if($scope.formValues[i].inputType == 'submit'){
    			$scope.formValues[i].w_bgColor = color2;
    		}
    		if($scope.formValues[i].type == 'voucher'){
    			$scope.formValues[i].w_bgColor = color2;
    		}
    		
        } 
    };
    $scope.portalTheme = 'registration_form';
	var param_id = $routeParams.path;
	$scope.notfound = true;
	$scope.customerName = '';
	$http({
		method: 'GET',
		url: '/facesix/rest/captive/portal/get/?id='+param_id 
	}).then(function(response) {
		//console.log(response);
		var data = response.data[0];
		//console.log(data);
		if(response.data != ''){
			//console.log(">>"+JSON.stringify(response.data[0].associationIds));
			$scope.activeSkin = JSON.parse(data.activeSkin);
			$scope.activeSkin = $scope.activeSkin.skinId; 
			$scope.portalName = data.portalName;
			$scope.preferedUrl = data.preferedUrl;
			$scope.associationTemp = response.data[0].associationWith;
			$scope.selected = response.data[0].associationIds;
			$scope.EditModeId = data.id; 
			$scope.previewModeClass = 'desktop';
			$scope.cid = data.cid;
			$scope.customerName = data.customerName;
			
		    $scope.setPreviewClass = function(previewClass){
		    	$scope.previewModeClass = previewClass;
		    }
		    
		    $scope.portalTheme = data.portalTheme;
		    $scope.template = "/facesix/template/qcom/captive_portal/templates/"+$scope.portalTheme;
		    $scope.portalType = data.portalType;
		    $scope.formValues = JSON.parse(data.portalComponents);
		    $scope.loadEditingOptions(); 
		    $scope.notfound = false;
		    $scope.backgroundImg = data.backgroundImg;
		    $scope.bgScreenShot = data.bgScreenShot;
		    if(data.portalType != 'registration'){
		    	$location.path('/captiveportal');
    			$location.replace();
		    }
		    
		    var cid = $scope.cid;
			
			var associatedVal = $scope.associationTemp ;
			
			if(associatedVal == "sid"){
				var associatedwith = "sid";
				
				$scope.dButton = false;
			} else if(associatedVal == "spid"){
				var associatedwith = "spid";
				$scope.dButton = false;
			} else {
				var associatedwith = cid;
				$scope.dButton = true;
			}
					
		 var url= "/facesix/rest/captive/portal/associationList?cid="+cid+"&associatedwith="+associatedwith
		 //console.log(url);
	    	$http({
	    		method: 'GET',
	    		url: url, 
	    		headers: {'Content-Type': 'application/json'}
	    		
	    	}).then(function(response) {
	    		console.log(JSON.stringify(response.data));
	    		 var toAppend = '';
	    		 $('#sessions').empty();
	             $.each(response.data,function(i,o){
	            	 if($scope.selected == response.data[i].id){
	            		 toAppend += '<option selected="selected">'+response.data[i].uid+'</option>';
	            	 } else {
	            		 toAppend += '<option value=' + o.id + ' >'+ o.uid + '</option>';
	            	 }
	            	 	 
	            });

	           $('#sessions').append(toAppend);
	           
	       });
	    	
		}else{
			$location.path('/captiveportal');
			$location.replace();
			$scope.notfound = true;
		}
		 
	});
	 
     
    
    
	/* File Upload Actions */
    $scope.setSingleVideoFile = function (element) {
    	
    	var reader = new FileReader();  
        
        reader.readAsDataURL(element.files[0]);  
        reader.onload = function (event) {
        	console.log(event);
            $scope.singleField.videoPath = event.target.result;
            console.log($scope.singleField.videoPath);
            $scope.$apply() 
        } 
        
        console.log(element.files[0]);
        console.log($scope.singleField.videoPath);
        
    }
    
	$scope.loadEditingOptions = function(){
		$scope.isEditingMode = true;
	     
	    
	    $scope.sortableOptions = {
	        containment: '#sortable-container',
	        allowDuplicates: true ,
	        itemMoved: function (event) {$scope.changeOrder();},
	        orderChanged: function(event) {$scope.changeOrder();}
	    };
	    
	    
	    $scope.sortableCloneOptions = {
	        containment: '#sortable-container',
	        clone: true,
	        itemMoved: function (event) {$scope.changeOrder();},
	        orderChanged: function(event) {$scope.changeOrder();}
	    };
	    
	    /* Reorder components */
	    $scope.changeOrder = function(){ 
	    	$scope.remove = 100000;
	    	for (var i = 0; i < $scope.formValues.length; i++)
	        {
	    		$scope.formValues[i].id = i;
	        }
	    	$scope.checkRegistration();
	    }
	    
	    /* File Upload Actions */
	    $scope.setSingleImageFile = function (element) {
	    	
	    	var reader = new FileReader();  
	        reader.onload = function (event) {
	             $scope.singleField.imagePath = event.target.result;
	             $scope.$apply()
	              
	        } 
	        reader.readAsDataURL(element.files[0]); 
	        console.log(element.files[0]);
	        
	        var formData=new FormData();
	        formData.append("portalImage", element.files[0]); 
	        formData.append("cid", $scope.cid); 
	       
	        /* 
	        $http({
	    		method: 'POST',
	    		url: '/facesix/rest/captive/portal/portalBgUpload',  
	    		headers: {'Content-Type': undefined}, 
	    		data: formData,
	    		transformRequest: function(data, headersGetterFunction) {
	    			console.log(data); 
	    			return data;
	    		} 
	    	}).then(function(response) {
	    		 console.log(response);
	             if(response.data.code == 200){ 
	            	 if(response.data.id !=''){
	            		 $scope.singleField.imagePath = response.data.id; 
	            	 }
	             }else{
	            	 $scope.singleField.imagePath = '';
	             }
	        });   */
	       
	        
	    }
	    
	    /* Change / Set theme background */
	    $scope.setThemeBg = function (element) {
	    	
	    	var reader = new FileReader(); 
	    	console.log(element.files[0]);
	        reader.onload = function (event) {
	                $scope.backgroundImg = event.target.result;
	                $scope.$apply()
	        } 
	        reader.readAsDataURL(element.files[0]);  
	        
	    }
	    
	    /* Save template */
	    $scope.errorMessage = false; 
	    $scope.errorplaceholder = ''; 
	    $scope.errorMessage1 = false;
	    
	    $scope.savePortals = function(){
	    	$scope.errorCount = [];
	    	$scope.loaderEnabled = 'active';
	    	if($scope.portalType == 'registration'){
	    	    for (var i = 0; i < $scope.formValues.length; i++)
	            { 
	    	    	
	    	    	if($scope.formValues[i].name == 'registration'){ 
	    	    		 
	    	    		 var count = 0;
	    	    		 var fields = $scope.formValues[i].registration_fields;
	    	    		 for (var j = 0; j < fields.length; j++)
	    	             { 
	    	    			 if(fields[j].status == true){
	    	    				 count = count + 1;
	    	    			 }
	    	             }
	    	    		 if(count < 2){
	    	    			 $scope.errorMessage = true;
	    	    			 $scope.loaderEnabled = '';
	    	    			 return false;
	    	    		 }else{
	    	    			 $scope.errorMessage = false;
	    	    		 }
	    	    		 
	    	    	}
	    	    	else if($scope.formValues[i].type == 'input' || $scope.formValues[i].type == 'textarea') {
	    	    		
	    	    		var placeholder = $scope.formValues[i].placeholder;
	    	    		$scope.errorCount.push(placeholder);
	    	    		
	    	    	}
	    	 	    
	            } 
	        } 
	    	
	    	
	    	console.log($scope.errorCount);
	    	function find_duplicate_in_array(arra1) {
	    		  var i,
	    		  len=arra1.length,
	    		  result = [],
	    		  obj = {}; 
	    		  for (i=0; i<len; i++)
	    		  {
	    			  obj[arra1[i]]=0;
	    		  }
	    		  for (i in obj) {
	    			  result.push(i);
	    		  }
	    		  return result;
		   }
	      
	    	if($scope.errorCount.length != find_duplicate_in_array($scope.errorCount).length){ 
	    		$scope.errorMessage1 = true;
	    		$scope.loaderEnabled = '';
	    		return false;
	    	} else{
	    		 $scope.errorMessage1 = false;
	    	}
	    	$scope.urlFound == true;
	    	$http({
	    	   		method: 'GET',
	    	   		url: '/facesix/rest/captive/portal/duplicatePreferedUrl?preferedUrl='+$scope.preferedUrl+'&id='+$scope.EditModeId+'&portalType'+$scope.portalType
    	   	}).then(function(response) { 
	    	   		if(response.data.body === 'new'){
	    	   			$scope.urlFound = true; 
	    	   		}else{
	    	   			$scope.urlFound = false;
	    	   		}
	    	   		$scope.update();
    	   	});	
	    	$scope.update = function(){
	    		if($scope.errorMessage == false && $scope.errorMessage1 == false && $scope.urlFound == true){
		    		 
		    		$scope.activeSkin =  {
	    				colorSet: JSON.stringify($scope.activeSkinColor),
	    				skinId: $scope.activeSkin 
	    			};
		    		$scope.bgScreenShot = '';
		    		// console.log($scope.cid);
		    		 html2canvas($("#login-form-center")[0]).then(function (canvas) {
		    			$scope.errorSave = true;
		    			var base64 = canvas.toDataURL();
		    			// console.log(base64); 
		    			$scope.bgScreenShot = base64;
		    			$scope.associationIdsArray=[];
		    			
		    			var cid = $scope.cid
		    			if($scope.associationTemp != "cid"){
		    				var sessionVal = $('#sessions').val();
		    				//console.log("value" + yes)
			        		$scope.associationIdsArray.push(sessionVal);
			        
			        		var arrayAssociated = $scope.associationIdsArray;
		        		} else if($scope.associationTemp == "cid"){
		        			var cur_val = cid;
			        		$scope.associationIdsArray.push(cur_val);
			        		var arrayAssociated = $scope.associationIdsArray;
		        		}
	  		
	      		$scope.finalOutput = { 
	        				id: param_id,
	        				portalName: $scope.portalName,
	        				preferedUrl: $scope.preferedUrl,
	        				associationWith : $scope.associationTemp,
	        				associationIds : arrayAssociated,
	        				cid: $scope.cid,
	        				backgroundImg: $scope.backgroundImg,
	        				portalComponents: JSON.stringify($scope.formValues),
	        				activeSkin: JSON.stringify($scope.activeSkin),
	        				portalType: $scope.portalType, 
	        				portalTheme: $scope.portalTheme,
	        				bgScreenShot: $scope.bgScreenShot,
	        				customerName: $scope.customerName
            	    }; 
	        	    	// alert("thats"+ JSON.stringify($scope.finalOutput))
	        	    	$http({
	        	    		method: 'POST',
	        	    		url: '/facesix/rest/captive/portal/save',  
	        	    		headers: {'Content-Type': 'application/json'},   
	        	    		data: JSON.stringify($scope.finalOutput)
	        	    	}).then(function(response) { 
	        	    		if (response.status == 200) {
	        	    			$location.path('/captiveportal1');
	        	    			$location.replace();
	        				} else {
	        					$scope.errorSave = false;
	        					$scope.loaderEnabled = '';
	        				}
	        	        }); 
	        	    	
		    		}); 
	    	    	  
		    	}
		    	else{
		    		$scope.loaderEnabled = '';
		    		return false;
		    	}
	    	}
	    	
	    	
	    }
	  
	    
	    /* Preview Portals */
	    $scope.previewPortals = function(){
	    	
	    	$scope.previewMode = 'active';
	    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
	    	myEl.addClass("bodyhidden");
	    	$scope.isEditingMode = false;
	    	
	    }
	    
	    /* Preview Close Option */
	    $scope.previewClose = function(){
	    	$scope.previewMode = '';
	    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
	    	myEl.removeClass("bodyhidden");
	    	$scope.isEditingMode = true;
	    }
	   
	    $scope.disabledStatus = false;
	    /* Disable registration component if exist */
	    $scope.checkRegistration = function(){
	    	for (var j = 0; j < $scope.itemsList.components.length; j++)
	        {
	    		$scope.itemsList.components[j].disabled = false;
	        }
	    	for (var i = 0; i < $scope.formValues.length; i++)
	        {
	    		for (var j = 0; j < $scope.itemsList.components.length; j++)
	            {
	    			 if($scope.formValues[i].type == "registration" && $scope.itemsList.components[j].type == "registration"){
	    				 $scope.itemsList.components[j].disabled = true;
	    			 }
	    			 else if($scope.formValues[i].type == "terms" && $scope.itemsList.components[j].type == "terms"){
	    				 $scope.itemsList.components[j].disabled = true;
	    			 } 
	            }
	        }
	    }
	    $scope.checkRegistration();
	    
	    /* Change font size when tag changed */
	    $scope.txtTypeChange = function(value){
		 
			if(value == 'h1')
				$scope.singleField.font_size = 36;
			else if(value == 'h2')
				$scope.singleField.font_size = 30;
			else if(value == 'h3')
				$scope.singleField.font_size = 24;
			else if(value == 'h4')
				$scope.singleField.font_size = 18;
			else if(value == 'h5')
				$scope.singleField.font_size = 16;
			else if(value == 'h6')
				$scope.singleField.font_size = 12;
			else
				$scope.singleField.font_size = 14; 
	    	
	    }
	}
    
    
    
	/* File Upload */
	$scope.color = '';
	$scope.fileSelected = '';
	$scope.casting = [];
	$scope.filePopUp = false;
	$scope.format = '';
	$scope.itemList = '';
	$scope.fileFM = '';
	$scope.chooseFile = function(file, format = null, item){
		$scope.fileFM = file; 
		$scope.format = format;
		$scope.itemList = item;
		console.log($scope.itemList);
		$scope.filePopUp = true;
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid+'&fileType='+$scope.fileFM,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) { 
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 			console.log($scope.casting);
 	 		 }
 	 	});
		
	} 
	$scope.closeNewFilePopUp = function(){
    	$scope.filePopUp = false; 
    }
	$scope.appyFileFile = function(fileSelected){
		console.log(fileSelected);
		console.log($scope.itemList);
		if($scope.format == 'video'){
			$scope.itemList.videoPath = fileSelected;
		}else if($scope.format == 'image'){
			$scope.itemList.imagePath = fileSelected;
		}else if($scope.format == 'background'){
			$scope.backgroundImg = fileSelected;
		}else if($scope.format == 'logo'){
			$scope.logoImg = fileSelected;
		}
		
		$scope.filePopUp = false; 
	}
	$scope.uploadLoader = false;
	$scope.reloadFiles = function(){
		$scope.uploadLoader = true;
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid+'&fileType='+$scope.fileFM,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) { 
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 			console.log($scope.casting);
 	 			$scope.uploadLoader = false;
 	 		 }
 	 	});
		
	}
	
	
}

function CPortantAddCtrl($location,$window, $scope, $templateCache, $rootScope,$http, $timeout, data, $routeParams) { 
	
	
	$scope.filesError = true;
	$scope.loaderEnabled = '';
	
	$scope.EditModeId = '';
	$scope.errorSave = false;
	$scope.cid = '';
	
	$scope.cids = ' '; 
	data.myProfile().then(function successCallback(response) {
		var customerData = response.data;
		$scope.cid = customerData.customerId; 
		$scope.cids = customerData.customerId;  
		
		if($scope.cids == '' || $scope.cids == undefined){  	
			$scope.cusList = '';
			data.clientList().then(function successCallback(response) {
				 if(response.data.customer != undefined && response.data.customer.length > 0){
					 $scope.cusList = response.data.customer; 
				 }
				
			}, function errorCallback(response) {

			});
		} 
		
	}, function errorCallback(response) {

	});
	  
	data.myProfile().then(function successCallback(response) {
		//console.log(JSON.stringify(response))
		$scope.associationTemp = "cid";
		$scope.dButton = true;
	});
	
	
	$scope.changeTemp = function(cid){
		console.log("change temp")
		data.myProfile().then(function successCallback(response) {
			
			var cid = $scope.cid;
			if(cid == undefined || cid == ""){
				var cid = response.data.customerId;	
			}
			var associatedVal = $scope.associationTemp ;
			if(associatedVal == "sid"){
				var associatedwith = "sid";
				$scope.dButton = false;
			} else if(associatedVal == "spid"){
				var associatedwith = "spid";
				$scope.dButton = false;
			} else {
				var associatedwith = cid;
				$scope.dButton = true;
			}
					
		 var url= "/facesix/rest/captive/portal/associationList?cid="+cid+"&associatedwith="+associatedwith
		 console.log(url);
	    	$http({
	    		method: 'GET',
	    		url: url, 
	    		headers: {'Content-Type': 'application/json'}
	    		
	    	}).then(function(response) {
	    		console.log(JSON.stringify(response.data[0].uid));
	    			$scope.choose = (response.data);
	    			console.log($scope.choose)
	       });
	    	
		});
	}
	
	

	
	$scope.changeCid = function(cid){
		$scope.cid = cid; 
	}
	
	/* Quick Tabs Start */
	$scope.tab = 1;

    $scope.setTab = function(newTab){
      $scope.tab = newTab;
    };

    $scope.isSet = function(tabNum){
      return $scope.tab === tabNum;
    };
    /* Quick Tabs End */
    
    /* Accordian */
    $scope.acc = 1;
    $scope.setAcc = function(newTab){
    	if($scope.acc == newTab){
    		$scope.acc = 100000;
    	}
    	else{
    		$scope.acc = newTab;
    	}
	    
	};
	
    $scope.isSets = function(tabNum){
	    return $scope.acc === tabNum;
    };
	  
    
    /* Accordian */
	 
    /* Remove Action start */
    $scope.remove = 100000;
    $scope.setRemove = function(newTab){
	    $scope.remove = newTab;
	};
	
    $scope.isRemove = function(tabNum){
	    return $scope.remove === tabNum;
    };
	  
    
    /* Remove Action end */
     
	 
	/* Color Themes Start */ 
	$scope.activeSkin = 0;
	$scope.activeSkinColor = {
		color1: '#5f9018',
		color2: '#89b923'
	};

	/* Change Skin color */
    $scope.colorPalate = function(tabNum, color1, color2){ 
    	$scope.activeSkinColor.color1 = color1;
    	$scope.activeSkinColor.color2 = color2;
    	$scope.activeSkin = tabNum;
    	
    	for (var i = 0; i < $scope.formValues.length; i++)
        { 
    		if($scope.formValues[i].type == 'registration'){
    			$scope.formValues[i].w_bgColor = color1; 
    		}
    		if($scope.formValues[i].type == 'login'){
    			$scope.formValues[i].w_bgColor = color1; 
    		}
    		if($scope.formValues[i].type == 'text'){
    			$scope.formValues[i].w_bgColor = color2; 
    		}
    		if($scope.formValues[i].inputType == 'submit'){
    			$scope.formValues[i].w_bgColor = color2;
    		}
    		if($scope.formValues[i].type == 'voucher'){
    			$scope.formValues[i].w_bgColor = color2;
    		} 
        } 
    };
    
    $scope.activeSkin = 5;
    /* Color Themes End */ 
    
    /* Preview Class */
    $scope.previewModeClass = 'desktop';
    $scope.setPreviewClass = function(previewClass){
    	$scope.previewModeClass = previewClass;
    }
    
    $scope.portalTheme = 'registration_form';  
    
    var param_id = $routeParams.path;
    
    $scope.backgroundImg = '';
    $scope.portalName = 'Captive Portal Registration Form';
    $scope.portalType = 'registration';
    switch (param_id){
    	case '0':
    		$scope.portalType = 'registration';
    		$scope.portalName = 'Captive Portal Registration Form';
    		$scope.portalTheme = 'registration_form';  
    		break;
    	default:
    		$location.path('/captiveportal');
    		$location.replace();	
		break;
    }
    
	console.log($scope.portalTheme);
	$scope.template = "/facesix/template/qcom/captive_portal/templates/"+$scope.portalTheme;
	
    $scope.setEditOptions = function(tab, item){ 
    	 
    	$scope.tab = tab;
    	$scope.singleField = item;
    	
    }
	
	
	
	$scope.activate= function(index){
		console.log(index);
		$scope.index=index;
	};
	$scope.removeField = function(item){
		var index =$scope.formValues.indexOf(item);
		$scope.formValues.splice(index, 1);   
		$scope.tab = 1; 
		$scope.remove = 100000;
		$scope.checkRegistration(); 
	}
	
	var i;
    $scope.itemsList = {
        components: [],
        advancedcomponents: [] 
    };
    
     
    
    
   
    /* Advanced components */
    $scope.itemsList.advancedcomponents=[{id:100,type:"offer",field_icon:"tags",name:"test",value:"",isDefault:0,placeholder:"Input Field",content:"Offer Information",align:"left",font_size:14,label:"Offer Code",inputType:"text",validFrom:"",validTo:"",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:101,type:"add",field_icon:"tags",name:"test",value:"",isDefault:0,placeholder:"Input Field",content:"Advertisement details here...",align:"left",addVertisementLink:"#",font_size:14,label:"Advertisement",inputType:"text",validFrom:"",validTo:"",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:80,w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"}];
    $scope.isEditingMode = true;
    
    /* Normal components */
    $scope.itemsList.components=[{id:0,type:"input",field_icon:"input",name:"test",value:"",isDefault:0,placeholder:"Input Field",content:"Submit Text",align:"left",font_size:14,label:"Input Field",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:3,type:"text",field_icon:"text",name:"test",value:"",isDefault:"",placeholder:"Text Field",content:"Your content here...",align:"center",font_size:14,label:"Text Field",inputType:"p",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#000000",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:1,type:"textarea",field_icon:"textarea",name:"test",value:"",isDefault:"",placeholder:"Textarea",content:"",align:"left",font_size:14,label:"Textarea",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:2,type:"image",field_icon:"file-image-o",name:"test",value:"",isDefault:"",placeholder:"Image Field",content:"Submit",align:"center",font_size:14,label:"Image Field",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#ffffff",borderStyle:"solid",bgColor:"transparent",textColor:"#ffffff",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:4,type:"video",field_icon:"file-video-o",name:"video",isDefault:"",value:"",placeholder:"video",videoPath:"",align:"center",font_size:14,label:"video",inputType:"p",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#000000",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:300,w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:5,type:"voucher",field_icon:"tags",name:"voucher",value:"",isDefault:"",placeholder:"Voucher",content:"",align:"center",font_size:14,label:"Voucher",inputType:"",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:0,paddingBottom:10,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#000000",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:0,w_paddingRight:0,w_paddingBottom:0,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:7,type:"terms",field_icon:"file-image-o",name:"test",value:"",isDefault:"",linkColor:"#000000",placeholder:"Image Field",content:'Please review and accept our <a href="javascript:void(0);" target="_blank">Terms &amp; Conditions</a> for access to Wi-Fi. ',align:"center",font_size:14,label:"Terms",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#ffffff",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:8,type: "social",field_icon: "share-alt",name: "test",value: "",isDefault: "",linkColor: "#000000",placeholder: "Image Field",content: "",align: "center",font_size: 14,label: "Social",inputType: "text",wrapperClass: "",imagePath: "",paddingTop: 0,paddingRight: 0,paddingBottom: 0,paddingLeft: 0,marginTop: 0,marginRight: 0,marginBottom: 0,marginLeft: 0,borderWidth: 0,borderRadius: 0,mediaStyle: "round",showText: 0,socialMedias: [{name: "facebook",url: "https://www.facebook.com/",status: true},{name: "twitter",url: "https://twitter.com/",status: true},{name: "googleplus",url: "https://plus.google.com/",status: true},{name: "linkedin",url: "https://www.linkedin.com/",status: true},],borderColor: "#fff",borderStyle: "solid",bgColor: "transparent",textColor: "#000",field_width: "",field_height: "",w_paddingTop: 10,w_paddingRight: 0,w_paddingBottom: 10,w_paddingLeft: 0,w_marginTop: 0,w_marginRight: 0,w_marginBottom: 0,w_marginLeft: 0,w_borderWidth: 0,w_borderRadius: 0,w_borderColor: "#f0f0f0",w_borderStyle: "solid",w_bgColor: "#ffffff"}];
    
    
    if($scope.portalType == 'registration'){
    	$scope.formValues=[{id:0,type:"image",field_icon:"file-image-o",name:"test",value:"",isDefault:"",placeholder:"Image Field",content:"Submit",align:"center",font_size:14,label:"Image Field",inputType:"text",wrapperClass:"",imagePath:"/facesix/static/qubercomm/images/header/logo.png",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#ffffff",borderStyle:"solid",bgColor:"transparent",textColor:"#ffffff",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:1,type:"text",field_icon:"text",name:"test",value:"",isDefault:"",placeholder:"Text Field",content:"Register Here.",align:"center",font_size:14,label:"Text Field",inputType:"p",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#000000",borderStyle:"solid",bgColor:"transparent",textColor:"#ffffff",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#89b923"},{id:2,type:"registration",field_icon:"registration",name:"registration",value:"",isDefault:1,registration_fields:[{name:"username",status:true,placeholder:"User Name"},{name:"email",status:true,placeholder:"Email"},{name:"phone",status:true,placeholder:"Phone"}],placeholder:"Registration Form",content:"",align:"left",font_size:14,label:"Registration",inputType:"",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:10,marginTop:0,marginRight:0,marginBottom:10,marginLeft:0,borderWidth:0,borderRadius:5,borderColor:"#000000",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:45,w_paddingTop:30,w_paddingRight:30,w_paddingBottom:15,w_paddingLeft:30,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#5f9018"},{id:3,type:"terms",field_icon:"file-image-o",name:"test",linkColor:"#000000",value:"",isDefault:"",placeholder:"Image Field",content:'Please review and accept our <a href="javascript:void(0);" target="_blank">Terms &amp; Conditions</a> for access to Wi-Fi. ',align:"left",font_size:16,label:"Terms",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#ffffff",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:10,w_paddingBottom:10,w_paddingLeft:10,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:4,type:"submit",field_icon:"input",name:"login",value:"",isDefault:1,placeholder:"Input Field",content:"Register",align:"center",font_size:14,label:"Input Field",inputType:"submit",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:10,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:15,w_paddingRight:0,w_paddingBottom:15,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#89b923"}];	
    }else if($scope.portalType == 'login'){
    	$scope.backgroundImg = '/facesix/static/qcom/img/QLoginPage4.jpg';
    	$scope.formValues=[{id:0,type:"image",field_icon:"file-image-o",name:"test",imagePath:"/facesix/static/qcom/img/logo-home.png"}];
    }
    
    
    $scope.sortableOptions = {
        containment: '#sortable-container',
        allowDuplicates: true ,
        itemMoved: function (event) {$scope.changeOrder();},
        orderChanged: function(event) {$scope.changeOrder();}
    };
    
    
    $scope.sortableCloneOptions = {
        containment: '#sortable-container',
        clone: true,
        itemMoved: function (event) {$scope.changeOrder();},
        orderChanged: function(event) {$scope.changeOrder();}
    };
    
    /* Reorder components */
    $scope.changeOrder = function(){ 
    	$scope.remove = 100000;
    	for (var i = 0; i < $scope.formValues.length; i++)
        {
    		$scope.formValues[i].id = i;
        }
    	$scope.checkRegistration();
    }
    
    /* File Upload Actions */
    $scope.setSingleImageFile = function (element) {
    	
    	var reader = new FileReader();  
        reader.onload = function (event) {
             $scope.singleField.imagePath = event.target.result;
             $scope.$apply()
              
        } 
        reader.readAsDataURL(element.files[0]);  
        
    }
    
    /* File Upload Actions */
    $scope.setSingleVideoFile = function (element) {
    	
    	var reader = new FileReader();  
        reader.onload = function (event) {
             $scope.singleField.videoPath = event.target.result;
             $scope.$apply()
              
        } 
        reader.readAsDataURL(element.files[0]);  
        console.log($scope.singleField.videoPath);
        
    }
    
    
    /* Change / Set theme background */
    $scope.setThemeBg = function (element) {
    	
    	var reader = new FileReader(); 
    	console.log(element.files[0]);
        reader.onload = function (event) {
                $scope.backgroundImg = event.target.result;
                $scope.$apply()
        } 
        reader.readAsDataURL(element.files[0]);  
        
    }
    $scope.errorMessage = false;
    /* Save template */
    $scope.errorMessage1 = false;
    $scope.errorplaceholder = '';
    
    $scope.savePortals = function(){
    	$scope.errorCount = [];
    	$scope.loaderEnabled = 'active'; 
        if($scope.portalType == 'registration'){
    	    for (var i = 0; i < $scope.formValues.length; i++)
            { 
    	    	if($scope.formValues[i].name == 'registration'){  
    	    		 var count = 0;
    	    		 var fields = $scope.formValues[i].registration_fields;
    	    		 for (var j = 0; j < fields.length; j++)
    	             { 
    	    			 if(fields[j].status == true){
    	    				 count = count + 1;
    	    			 }
    	             }
    	    		 if(count < 2){
    	    			 $scope.errorMessage = true;
    	    			 $scope.loaderEnabled = '';
    	    			 return false;
    	    		 }else{
    	    			 $scope.errorMessage = false;
    	    		 } 
    	    	}
    	    	else if($scope.formValues[i].type == 'input' || $scope.formValues[i].type == 'textarea') {
    	    		
    	    		var placeholder = $scope.formValues[i].placeholder;
    	    		$scope.errorCount.push(placeholder);
    	    		
    	    	}
            }
        } 
        function find_duplicate_in_array(arra1) {
	  		  var i,
	  		  len=arra1.length,
	  		  result = [],
	  		  obj = {}; 
	  		  for (i=0; i<len; i++)
	  		  {
	  			  obj[arra1[i]]=0;
	  		  }
	  		  for (i in obj) {
	  			  result.push(i);
	  		  }
	  		  return result;
		   }
	    
	  	if($scope.errorCount.length != find_duplicate_in_array($scope.errorCount).length){ 
	  		$scope.errorMessage1 = true;
	  		$scope.loaderEnabled = '';
	  		return false;
	  	} else{
	  		 $scope.errorMessage1 = false;
	  	}
	  	$http({
		   		method: 'GET',
		   		url: '/facesix/rest/captive/portal/duplicatePreferedUrl?preferedUrl='+$scope.preferedUrl+'&id='+$scope.EditModeId+'&portalType'+$scope.portalType
		   	}).then(function(response) { 
		   		if(response.data.body === 'new'){
		   			$scope.urlFound = true; 
		   		}else{
		   			$scope.urlFound = false;
		   		}
		   		$scope.update();
	   	});	 
	  	$scope.update = function(cid){
	  		//console.log(">> cid" + cid)
	  		if($scope.errorMessage == false && $scope.errorMessage1 == false && $scope.urlFound == true){
        		$scope.activeSkin =  {
        			colorSet: $scope.activeSkinColor,
        			skinId: $scope.activeSkin 
        		};
        		$scope.bgScreenShot = '';
	        	html2canvas($("#login-form-center")[0]).then(function (canvas) {
		        	$scope.errorSave = true;	
	        		var base64 = canvas.toDataURL();
	        		$scope.bgScreenShot = base64;
	        		$scope.associationIdsArray=[];
	        		
	        		
	        		data.myProfile().then(function successCallback(response) {
	        			
	        			var cid = response.data.customerId;	
	        			if($scope.associationTemp != "cid"){
		        			$scope.assId = $scope.selected.id;
			        		$scope.associationIdsArray.push($scope.assId);
			        
			        		var arrayAssociated = $scope.associationIdsArray;
		        		} else {
		        			
		        			if($scope.cid != null || $scope.cid != ""){
		        				$scope.associationIdsArray.push($scope.cid);
			        			var arrayAssociated = $scope.associationIdsArray;
		        			} else {
		        			    var cur_cid = cid;
		        				$scope.associationIdsArray.push(cur_cid);
			        			var arrayAssociated = $scope.associationIdsArray;
		        			}
		        			
		        		}
	        			
	        		
	        		$scope.finalOutput = { 
            			portalName: $scope.portalName,
            			preferedUrl: $scope.preferedUrl,
            			associationWith : $scope.associationTemp,
        				associationIds : arrayAssociated,
            			cid: $scope.cid,
            			backgroundImg: $scope.backgroundImg,
            			portalComponents: JSON.stringify($scope.formValues),
            			activeSkin: JSON.stringify($scope.activeSkin),
            			portalType: $scope.portalType,
            			portalTheme: $scope.portalTheme,
            			bgScreenShot: $scope.bgScreenShot
            	    };  
                	
                	// console.log(JSON.stringify($scope.finalOutput));
                    $http({
                		method: 'POST',
                		url: '/facesix/rest/captive/portal/save',  
                		headers: {'Content-Type': 'application/json'},   
                		data: JSON.stringify($scope.finalOutput)
                	}).then(function(response) { 
                		 console.log(response);
                          if(response.status == 200){
                        	  
                        	  $location.path('/captiveportal');
          	    			  $location.replace();
                          }else{
                        	  $scope.errorSave = true;
                        	  $scope.loaderEnabled = '';
                          }
                    }); 
	        	});
	        	
	        	});	
	        }else{
	        	$scope.loaderEnabled = '';
	        	return false;
	        }
	  	}
        
    }
    
    /* Preview Portals */
    $scope.previewPortals = function(){
    	$scope.previewMode = 'active';
    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
    	myEl.addClass("bodyhidden");
    	$scope.isEditingMode = false;
    }
    
    /* Preview Close Option */
    $scope.previewClose = function(){
    	$scope.previewMode = '';
    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
    	myEl.removeClass("bodyhidden");
    	$scope.isEditingMode = true;
    }
   
    /* Disable registration component if exist */
    $scope.checkRegistration = function(){
    	for (var j = 0; j < $scope.itemsList.components.length; j++)
        {
    		$scope.itemsList.components[j].disabled = false;
        }
    	for (var i = 0; i < $scope.formValues.length; i++)
        {
    		for (var j = 0; j < $scope.itemsList.components.length; j++)
            {
    			 if($scope.formValues[i].type == "registration" && $scope.itemsList.components[j].type == "registration"){
    				 $scope.itemsList.components[j].disabled = true;
    			 } 
    			 else if($scope.formValues[i].type == "terms" && $scope.itemsList.components[j].type == "terms"){
    				 $scope.itemsList.components[j].disabled = true;
    			 } 
    			 else if($scope.itemsList.components[j].type == "registration" && $scope.portalType == 'login'){
    				 $scope.itemsList.components[j].disabled = true;
    			 }    
            }
        }
    }
    $scope.checkRegistration();
    
    /* Change font size when tag changed */
    $scope.txtTypeChange = function(value){
	 
		if(value == 'h1')
			$scope.singleField.font_size = 36;
		else if(value == 'h2')
			$scope.singleField.font_size = 30;
		else if(value == 'h3')
			$scope.singleField.font_size = 24;
		else if(value == 'h4')
			$scope.singleField.font_size = 18;
		else if(value == 'h5')
			$scope.singleField.font_size = 16;
		else if(value == 'h6')
			$scope.singleField.font_size = 12;
		else
			$scope.singleField.font_size = 14; 
    	
    }
    
    
    /* File Upload */
	$scope.color = '';
	$scope.fileSelected = '';
	$scope.casting = [];
	$scope.filePopUp = false;
	$scope.format = '';
	$scope.itemList = '';
	$scope.fileFM = '';
	$scope.chooseFile = function(file, format = null, item){
		$scope.fileFM = file; 
		$scope.format = format;
		$scope.itemList = item;
		console.log($scope.itemList);
		$scope.filePopUp = true;
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid+'&fileType='+$scope.fileFM,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) { 
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 			console.log($scope.casting);
 	 		 }
 	 	});
		
	} 
	$scope.closeNewFilePopUp = function(){
    	$scope.filePopUp = false; 
    }
	$scope.appyFileFile = function(fileSelected){
		console.log(fileSelected);
		console.log($scope.itemList);
		if($scope.format == 'video'){
			$scope.itemList.videoPath = fileSelected;
		}else if($scope.format == 'image'){
			$scope.itemList.imagePath = fileSelected;
		}else if($scope.format == 'background'){
			$scope.backgroundImg = fileSelected;
		}else if($scope.format == 'logo'){
			$scope.logoImg = fileSelected;
		}
		
		$scope.filePopUp = false; 
	}
	$scope.uploadLoader = false;
	$scope.reloadFiles = function(){
		$scope.uploadLoader = true;
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid+'&fileType='+$scope.fileFM,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) { 
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 			console.log($scope.casting);
 	 			$scope.uploadLoader = false;
 	 		 }
 	 	});
		
	}
	
	
}

function CPortantLoginAddCtrl($location,$window, $scope, $templateCache, $rootScope,$http, $timeout, data, $routeParams){
	
	$scope.clientId = '';
	$scope.filesError = true;
	$scope.loaderEnabled = '';
	$scope.cid = '';
	
	$scope.cids = ' '; 
	data.myProfile().then(function successCallback(response) {
		var customerData = response.data;
		$scope.cid = customerData.customerId; 
		$scope.cids = customerData.customerId; 
		if($scope.cids == '' || $scope.cids == undefined){  	
			$scope.cusList = '';
			data.clientList().then(function successCallback(response) {
				 if(response.data.customer != undefined && response.data.customer.length > 0){
					 $scope.cusList = response.data.customer; 
				 }
				
			}, function errorCallback(response) {

			});
		} 
		
	}, function errorCallback(response) {

	});
	$scope.changeCid = function(cid){
		$scope.cid = cid; 
	}
 
	
	$scope.portalType = 'login';
	$scope.portalName = 'Login Form';
	$scope.portalTheme = 'login_form';
	var param_id = $routeParams.path; 
    $scope.backgroundImg = '/facesix/static/qcom/img/QLoginPage1.jpg';
    $scope.logoImg = '/facesix/static/qcom/img/logo-home.png';
    if(param_id != 1){
    	$location.path('/captiveportal');
		$location.replace();
    }
    
	$scope.template = "/facesix/template/qcom/captive_portal/templates/"+$scope.portalTheme;
	$scope.bgChanged = false;
	$scope.logoChanged = false;
	 /* File Upload Actions */
    $scope.setSingleImageFile = function (element) {
    	$scope.logoChanged = true;
    	var reader = new FileReader();  
        reader.onload = function (event) {
             $scope.logoImg = event.target.result;
             $scope.$apply()   
        } 
        reader.readAsDataURL(element.files[0]);
        if($scope.logoChanged == true || $scope.bgChanged == true){
        	$scope.filesError = false;
        } 
    }
    
    /* Change / Set theme background */
    $scope.setThemeBg = function (element) {
    	$scope.bgChanged = true;
    	var reader = new FileReader(); 
        reader.onload = function (event) {
                $scope.backgroundImg = event.target.result;
                $scope.$apply()
                
        } 
        reader.readAsDataURL(element.files[0]); 
        if($scope.logoChanged == true || $scope.bgChanged == true){
        	$scope.filesError = false;
        }
        
    }
    
    /* Preview Portals */
    $scope.previewPortals = function(){
    	
    	$scope.previewMode = 'active';
    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
    	myEl.addClass("bodyhidden");
    	$scope.isEditingMode = false;
    	
    }
    
    /* Preview Close Option */
    $scope.previewClose = function(){
    	$scope.previewMode = '';
    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
    	myEl.removeClass("bodyhidden");
    	$scope.isEditingMode = true;
    }
    /* Preview Class */
    $scope.previewModeClass = 'desktop';
    $scope.setPreviewClass = function(previewClass){
    	$scope.previewModeClass = previewClass;
    }
    
    $scope.savePortals = function(){
    	$scope.loaderEnabled = 'active';
    	    
    		$scope.bgScreenShot = '';
    		// console.log($scope.cid);
    		 html2canvas($("#login-form-center")[0]).then(function (canvas) {
    			$scope.errorSave = true;
    			var base64 = canvas.toDataURL();
    			// console.log(base64); 
    			$scope.bgScreenShot = base64;
    			$scope.finalOutput = {   
    				portalName: $scope.portalName, 
    				cid: $scope.cid,
    				backgroundImg: $scope.backgroundImg,  
    				logoImg: $scope.logoImg,  
    				portalType: $scope.portalType, 
    				portalTheme: $scope.portalTheme,
    				bgScreenShot: $scope.bgScreenShot,
    				supportComponents: JSON.stringify($scope.login),
    				associationWith : $scope.associationTemp,
    				associationIds : $scope.selected
    		    };   
    	    	console.log($scope.finalOutput);
    	    	 
    	    	$http({
    	    		method: 'POST',
    	    		url: '/facesix/rest/captive/portal/save',  
    	    		headers: {'Content-Type': 'application/json'},   
    	    		data: JSON.stringify($scope.finalOutput)
    	    	}).then(function(response) { 
    	    		if (response.status == 200) {
    	    			$location.path('/captiveportal');
    	    			$location.replace();
    				} else {
    					$scope.errorSave = false;
    					$scope.loaderEnabled = '';
    				}
    	        });
    	    	 
    		}); 
	     
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
                    $scope.login.state = x[i].long_name;
                } else if(y[j] == "administrative_area_level_2"){
                    $scope.login.city = x[i].long_name;
                } else if(y[j] == "country"){
                    $scope.login.country = x[i].long_name;
                }
            }
            
        } 
        $scope.$apply();
    });
    
    /* File Upload */
	$scope.color = '';
	$scope.fileSelected = '';
	$scope.casting = [];
	$scope.filePopUp = false;
	$scope.format = '';
	$scope.itemList = '';
	$scope.fileFM = '';
	$scope.chooseFile = function(file, format = null, item){
		$scope.fileFM = file; 
		$scope.format = format;
		$scope.itemList = item;
		console.log($scope.itemList);
		$scope.filePopUp = true;
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid+'&fileType='+$scope.fileFM,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) { 
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 			console.log($scope.casting);
 	 		 }
 	 	});
		
	} 
	$scope.closeNewFilePopUp = function(){
    	$scope.filePopUp = false; 
    }
	$scope.appyFileFile = function(fileSelected){
		console.log(fileSelected);
		console.log($scope.itemList);
		if($scope.format == 'video'){
			$scope.itemList.videoPath = fileSelected;
		}else if($scope.format == 'image'){
			$scope.itemList.imagePath = fileSelected;
		}else if($scope.format == 'background'){
			$scope.backgroundImg = fileSelected;
		}else if($scope.format == 'logo'){
			$scope.logoImg = fileSelected;
		}
		
		$scope.filePopUp = false; 
	}
	$scope.uploadLoader = false;
	$scope.reloadFiles = function(){
		$scope.uploadLoader = true;
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid+'&fileType='+$scope.fileFM,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) { 
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 			console.log($scope.casting);
 	 			$scope.uploadLoader = false;
 	 		 }
 	 	});
		
	}
	
	
    	
}

function CPortantLoginEditCtrl($location,$window, $scope, $templateCache, $rootScope,$http, $timeout, data, $routeParams){
	
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
                    $scope.login.state = x[i].long_name;
                } else if(y[j] == "administrative_area_level_2"){
                    $scope.login.city = x[i].long_name;
                } else if(y[j] == "country"){
                    $scope.login.country = x[i].long_name;
                }
            }
            
        } 
        $scope.$apply();
    });
    
	$scope.filesError = true;
	$scope.loaderEnabled = '';
	$scope.fieldError = '';
	$scope.cid = ''; 
	
	var param_id = $routeParams.path; 
	$scope.portalType = 'login';
	$scope.portalName = 'Login Form';
	$scope.portalTheme = 'login_form';
	
    $scope.backgroundImg = '/facesix/static/qcom/img/QLoginPage1.jpg';
    $scope.logoImg = '/facesix/static/qcom/img/logo-home.png';
    
	$http({
		method: 'GET',
		url: '/facesix/rest/captive/portal/get/?id='+param_id 
	}).then(function(response) {
		console.log(response);
		var data = response.data[0];
		console.log(data);
		if(response.data != ''){  
			$scope.portalName = data.portalName; 
		    $scope.template = "/facesix/template/qcom/captive_portal/templates/"+$scope.portalTheme;
		    $scope.backgroundImg = data.backgroundImg; 
		    $scope.logoImg = data.logoImg;
		    $scope.bgScreenShot = data.bgScreenShot;
		    $scope.portalType = data.portalType;
		    $scope.cid = data.cid;
		    console.log(data.supportComponents);
		    if(data.supportComponents != null && data.supportComponents != ''){
		    	$scope.login = JSON.parse(data.supportComponents);
		    }
		    if($scope.portalType != 'login'){
		    	$location.path('/captiveportal');
    			$location.replace();
		    }
		    
		}else{
			$location.path('/captiveportal');
			$location.replace();
			$scope.notfound = true;
		}
		 
	});
	
	  
	$scope.template = "/facesix/template/qcom/captive_portal/templates/"+$scope.portalTheme;
	$scope.bgChanged = false;
	$scope.logoChanged = false;
	 /* File Upload Actions */
    $scope.setSingleImageFile = function (element) {
    	$scope.logoChanged = true;
    	var reader = new FileReader();  
        reader.onload = function (event) {
             $scope.logoImg = event.target.result;
             $scope.$apply()   
        } 
        reader.readAsDataURL(element.files[0]);
        if($scope.logoChanged == true || $scope.bgChanged == true){
        	$scope.filesError = false;
        } 
    }
    
    /* Change / Set theme background */
    $scope.setThemeBg = function (element) {
    	$scope.bgChanged = true;
    	var reader = new FileReader(); 
        reader.onload = function (event) {
                $scope.backgroundImg = event.target.result;
                $scope.$apply()
                
        } 
        reader.readAsDataURL(element.files[0]); 
        if($scope.logoChanged == true || $scope.bgChanged == true){
        	$scope.filesError = false;
        } 
        
    }
    /* Preview Portals */
    $scope.previewPortals = function(){
    	
    	$scope.previewMode = 'active';
    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
    	myEl.addClass("bodyhidden");
    	$scope.isEditingMode = false;
    	
    }
    
    /* Preview Close Option */
    $scope.previewClose = function(){
    	$scope.previewMode = '';
    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
    	myEl.removeClass("bodyhidden");
    	$scope.isEditingMode = true;
    }
    /* Preview Class */
    $scope.previewModeClass = 'desktop';
    $scope.setPreviewClass = function(previewClass){
    	$scope.previewModeClass = previewClass;
    }
    
    $scope.savePortals = function(){
    	$scope.loaderEnabled = 'active';
    	    console.log(JSON.stringify($scope.login));
    		$scope.bgScreenShot = '';
    		// console.log($scope.cid);
    		 html2canvas($("#login-form-center")[0]).then(function (canvas) {
    			$scope.errorSave = true;
    			var base64 = canvas.toDataURL();
    			// console.log(base64); 
    			$scope.bgScreenShot = base64;
    			$scope.finalOutput = {   
    				portalName: $scope.portalName,
    				id: param_id,
    				cid: $scope.cid,
    				backgroundImg: $scope.backgroundImg,  
    				logoImg: $scope.logoImg,  
    				portalType: $scope.portalType, 
    				portalTheme: $scope.portalTheme,
    				bgScreenShot: $scope.bgScreenShot,
    				supportComponents: JSON.stringify($scope.login),
    				associationWith : $scope.associationTemp,
    				associationIds : $scope.selected
    		    };  
    	    	console.log($scope.finalOutput);
    	    	 
    	    	$http({
    	    		method: 'POST',
    	    		url: '/facesix/rest/captive/portal/save',  
    	    		headers: {'Content-Type': 'application/json'},   
    	    		data: JSON.stringify($scope.finalOutput)
    	    	}).then(function(response) { 
    	    		if (response.status == 200) {
    	    			$location.path('/captiveportal');
    	    			$location.replace(); 
    				} else {
    					$scope.errorSave = false;
    					$scope.loaderEnabled = '';
    				}
    	        });  
    	    	 
    		}); 
	     
    }
	
    
    /* File Upload */
	$scope.color = '';
	$scope.fileSelected = '';
	$scope.casting = [];
	$scope.filePopUp = false;
	$scope.format = '';
	$scope.itemList = '';
	$scope.fileFM = '';
	$scope.chooseFile = function(file, format = null, item){
		$scope.fileFM = file; 
		$scope.format = format;
		$scope.itemList = item;
		console.log($scope.itemList);
		$scope.filePopUp = true;
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid+'&fileType='+$scope.fileFM,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) { 
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 			console.log($scope.casting);
 	 		 }
 	 	});
		
	} 
	$scope.closeNewFilePopUp = function(){
    	$scope.filePopUp = false; 
    }
	$scope.appyFileFile = function(fileSelected){
		console.log(fileSelected);
		console.log($scope.itemList);
		if($scope.format == 'video'){
			$scope.itemList.videoPath = fileSelected;
		}else if($scope.format == 'image'){
			$scope.itemList.imagePath = fileSelected;
		}else if($scope.format == 'background'){
			$scope.backgroundImg = fileSelected;
		}else if($scope.format == 'logo'){
			$scope.logoImg = fileSelected;
		}
		
		$scope.filePopUp = false; 
	}
	$scope.uploadLoader = false;
	$scope.reloadFiles = function(){
		$scope.uploadLoader = true;
		$http({
 	 		method: 'GET',
 	 		url: '/facesix/rest/captive/casting/castingListAll/?cid='+$scope.cid+'&fileType='+$scope.fileFM,  
 	 		headers: {'Content-Type': 'application/json'} 
 	 	}).then(function(response) { 
 	 		 if(response.status == 200){
 	 			$scope.casting = response.data;
 	 			console.log($scope.casting);
 	 			$scope.uploadLoader = false;
 	 		 }
 	 	});
		
	}
	
	
}




function CPortantViewCtrl($location,$window, $scope, $templateCache, $rootScope,$http, $timeout, data, $routeParams) {
	$scope.previewModeClass = 'desktop';

	$scope.setPreviewClass = function(previewClass){
	    $scope.previewModeClass = previewClass;
	}
	/* Preview Portals */
	    $scope.previewPortals = function(){
	    	$scope.previewMode = 'active';
	    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
	    	myEl.addClass("bodyhidden");
	    	$scope.isEditingMode = false;
	    }
	    
	    /* Preview Close Option */
	    $scope.previewClose = function(){
	    	$scope.previewMode = '';
	    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
	    	myEl.removeClass("bodyhidden");
	    	$scope.isEditingMode = true;
	    }
	$scope.errorSave = false;
	var i;
	var param_id = $routeParams.path; 
    
    $scope.backgroundImg = '';
    $scope.portalType = 'registration';
    
	var param_id = $routeParams.path; 
	$scope.notfound = true;
	$scope.portalTheme = 'registration_form';  
	if(param_id == 0){
		$scope.notfound = false;
		$scope.backgroundImg = '';
	    $scope.portalName = 'Captive Portal Registration Form';
	    $scope.portalType = 'registration';
	    $scope.portalTheme = 'registration_form';
	    switch (param_id){
	    	case '0':
	    		$scope.portalType = 'registration';
	    		$scope.portalName = 'Captive Portal Registration Form';
	    		$scope.portalTheme = 'registration_form';  
	    		$scope.formValues=[{id:0,type:"image",field_icon:"file-image-o",name:"test",value:"",isDefault:"",placeholder:"Image Field",content:"Submit",align:"center",font_size:14,label:"Image Field",inputType:"text",wrapperClass:"",imagePath:"/facesix/static/qubercomm/images/header/logo.png",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#ffffff",borderStyle:"solid",bgColor:"transparent",textColor:"#ffffff",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:1,type:"text",field_icon:"text",name:"test",value:"",isDefault:"",placeholder:"Text Field",content:"Register Here.",align:"center",font_size:14,label:"Text Field",inputType:"p",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#000000",borderStyle:"solid",bgColor:"transparent",textColor:"#ffffff",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:0,w_paddingBottom:10,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#89b923"},{id:2,type:"registration",field_icon:"registration",name:"registration",value:"",isDefault:1,registration_fields:[{name:"username",status:true,placeholder:"User Name"},{name:"email",status:true,placeholder:"Email"},{name:"phone",status:true,placeholder:"Phone"}],placeholder:"Registration Form",content:"",align:"left",font_size:14,label:"Registration",inputType:"",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:10,marginTop:0,marginRight:0,marginBottom:10,marginLeft:0,borderWidth:0,borderRadius:5,borderColor:"#000000",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:45,w_paddingTop:30,w_paddingRight:30,w_paddingBottom:15,w_paddingLeft:30,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#000000",w_borderStyle:"solid",w_bgColor:"#5f9018"},{id:3,type:"terms",field_icon:"file-image-o",name:"test",linkColor:"#000000",value:"",isDefault:"",placeholder:"Image Field",content:'Please review and accept our <a href="javascript:void(0);" target="_blank">Terms &amp; Conditions</a> for access to Wi-Fi. ',align:"left",font_size:16,label:"Terms",inputType:"text",wrapperClass:"",imagePath:"",paddingTop:0,paddingRight:0,paddingBottom:0,paddingLeft:0,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:0,borderColor:"#ffffff",borderStyle:"solid",bgColor:"transparent",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:10,w_paddingRight:10,w_paddingBottom:10,w_paddingLeft:10,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#ffffff"},{id:4,type:"submit",field_icon:"input",name:"login",value:"",isDefault:0,placeholder:"Input Field",content:"Register",align:"center",font_size:14,label:"Input Field",inputType:"submit",wrapperClass:"",imagePath:"",paddingTop:10,paddingRight:15,paddingBottom:10,paddingLeft:15,marginTop:0,marginRight:0,marginBottom:0,marginLeft:0,borderWidth:0,borderRadius:10,borderColor:"#cccccc",borderStyle:"solid",bgColor:"#ffffff",textColor:"#000000",field_width:"",field_height:"",w_paddingTop:15,w_paddingRight:0,w_paddingBottom:15,w_paddingLeft:0,w_marginTop:0,w_marginRight:0,w_marginBottom:0,w_marginLeft:0,w_borderWidth:0,w_borderRadius:0,w_borderColor:"#f0f0f0",w_borderStyle:"solid",w_bgColor:"#89b923"}];
	    		break; 
	    	default:
	    		$location.path('/captiveportal');
				$location.replace();
			break;
	    } 
		$scope.template = "/facesix/template/qcom/captive_portal/templates/view_"+$scope.portalTheme;
		
	}else{
		$http({
			method: 'GET',
			url: '/facesix/rest/captive/portal/get/?id='+param_id 
		}).then(function(response) { 
			var data = response.data[0];
			if(response.data != ''){
				$scope.activeSkin = JSON.parse(data.activeSkin);
				$scope.activeSkin = $scope.activeSkin.skinId; 
				$scope.portalName = data.portalName;
				$scope.preferedUrl = data.preferedUrl; 
				$scope.associationTemp   = data.associationWith;
  				$scope.selected   = data.associationIds;
				$scope.previewModeClass = 'desktop';
			    $scope.setPreviewClass = function(previewClass){
			    	$scope.previewModeClass = previewClass;
			    }
			    
			    $scope.portalTheme = data.portalTheme;
			    $scope.template = "/facesix/template/qcom/captive_portal/templates/view_"+$scope.portalTheme;
			    console.log($scope.template);
			    $scope.portalType = data.portalType;
			    $scope.formValues = JSON.parse(data.portalComponents);
			    $scope.loadEditingOptions(); 
			    $scope.notfound = false;
			    $scope.backgroundImg = data.backgroundImg;
			    if(data.portalType != 'registration'){
			    	$location.path('/captiveportal');
	    			$location.replace();
			    }
			}else{
				$location.path('/captiveportal');
    			$location.replace();
				$scope.notfound = true;
			}
			 
		});
	}
	
	$scope.previewMode = 'active';
     
    
	$scope.loadEditingOptions = function(){
		$scope.isEditingMode = false;  
	    /* Reorder components */
	    $scope.changeOrder = function(){  
	    	for (var i = 0; i < $scope.formValues.length; i++)
	        {
	    		$scope.formValues[i].id = i;
	        }
	    }
	  
	} 
}


function CPortantLoginViewCtrl($location,$window, $scope, $templateCache, $rootScope,$http, $timeout, data, $routeParams) {
	$scope.previewModeClass = 'desktop';

	$scope.setPreviewClass = function(previewClass){
	    $scope.previewModeClass = previewClass;
	}
	/* Preview Portals */
	    $scope.previewPortals = function(){
	    	$scope.previewMode = 'active';
	    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
	    	myEl.addClass("bodyhidden");
	    	$scope.isEditingMode = false;
	    }
	    
	    /* Preview Close Option */
	    $scope.previewClose = function(){
	    	$scope.previewMode = '';
	    	var myEl = angular.element( document.getElementsByTagName( 'body' ) );
	    	myEl.removeClass("bodyhidden");
	    	$scope.isEditingMode = true;
	    }
	$scope.errorSave = false; 
	$scope.portalType = 'login';
	$scope.portalName = 'Login Form';
	$scope.portalTheme = 'login_form';
	var param_id = $routeParams.path; 
    
    $scope.backgroundImg = '';
    $scope.portalType = 'login';
    
	var param_id = $routeParams.path;   
	$scope.backgroundImg = '/facesix/static/qcom/img/QLoginPage1.jpg';
    $scope.logoImg = '/facesix/static/qcom/img/logo-home.png';
	if(param_id == 1){ 
	    if(param_id != 1){
	    	$location.path('/captiveportal');
			$location.replace();
	    }
	    
		$scope.template = "/facesix/template/qcom/captive_portal/templates/"+$scope.portalTheme;
		
	}else{ 
		$http({
			method: 'GET',
			url: '/facesix/rest/captive/portal/get/?id='+param_id
		}).then(function(response) { 
			var data = response.data[0];
			if(response.data != ''){
				$scope.portalName = data.portalName; 
			    $scope.template = "/facesix/template/qcom/captive_portal/templates/"+$scope.portalTheme;
			    $scope.backgroundImg = data.backgroundImg; 
			    $scope.logoImg = data.logoImg;
			    $scope.bgScreenShot = data.bgScreenShot;
			    $scope.portalType = data.portalType;
			    if($scope.portalType != 'login'){
			    	$location.path('/captiveportal');
	    			$location.replace();
			    } 
			}else{
				$location.path('/captiveportal');
    			$location.replace();
				$scope.notfound = true;
			} 
		});
	}
	
	$scope.previewMode = 'active';
      
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


app.directive('portalAvailable', function($timeout, $q, $http) {
	  return {
	    restrict: 'AE',
	    require: 'ngModel',
	    link: function(scope, elm, attr, model) {
	    	  elm.bind("keydown keypress", function (event) {
	    		model.$setValidity('preurl', true);	   
	    		scope.errorSave = false;
	        });  
	    	elm.bind('blur', function () {
	    		var defer = $q.defer(); 
	    		if(elm[0].value == "Preferred URL already exists"){
	    			scope.errorSave = true;
	    			model.$setValidity('preurl', false); 
	    		}else{
	    			 
	 		        $http({
	 	    	   		method: 'GET',
	 	    	   		url: '/facesix/rest/captive/portal/duplicatePreferedUrl?preferedUrl='+elm[0].value+'&id='+scope.EditModeId+'&portalType'+scope.portalType
	 	    	   	}).then(function(response) {
	 	    	   		console.log(response.data.body)
	 	    	   		if(response.data.body === 'new'){
	 	    	   			model.$setValidity('preurl', true);
	 	    	   			scope.errorSave = false;
	     	            }else{
	     	            	model.$setValidity('preurl', false); 
	     	            	elm[0].value ="Preferred URL already exists";
	     	            	scope.errorSave = true;
	     	           }
	 	    	   	});	
	    		}
		       
		         
		        return defer.promise;
	    	});
	    }
	  } 
}); 


function popup_yes(){	
	$('.closed').removeClass("show");
	window.open('#!/captiveportal', '_blank');
}
 
function popup_no(){	 
	 $('.closed').removeClass("show");
}

function myFunction() {
	var popup = document.getElementById("newfilePopup");
    popup.classList.toggle("show");     
}