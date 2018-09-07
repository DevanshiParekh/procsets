app.run(['$rootScope' , '$timeout', 'data', function($rootScope, $timeout, data){
    
    $rootScope.SuccessMsg = "";
    
    $rootScope.ErrorMsg = "";
    
    $rootScope.ErrorMessage = function(response){
        $rootScope.ErrorMsg = response.data.Message;
        $rootScope.SuccessMsg = "";
        $rootScope.timeout();
    }
    
    $rootScope.SuccessMessage = function(response){
        $rootScope.ErrorMsg = "";
        $rootScope.SuccessMsg = response.data.Message;
        $rootScope.timeout();
    }
    
    $rootScope.timeout = function(){
        $timeout(function() {
            $rootScope.ErrorMsg = "";
            $rootScope.SuccessMsg = "";
        }, 10000);
    }
    
    $rootScope.errorMethod = function (response) {
        console.log(response);
        if(response.status == 401){
            if(response.data.ShowLogin == true){
                $window.sessionStorage.clear();
                $window.location.href = "index.html";
            } else{ $rootScope.ErrorMessage(response);
            }
        } else if(response.status == 500){$rootScope.ErrorMsg = "Server Error. Please try again later";
            console.log($rootScope.ErrorMsg);
            $rootScope.timeout();
        }
        else if(response.status == 400)
        {$rootScope.ErrorMsg = "Requested resources not found";
            $rootScope.timeout();
        }
        else {$rootScope.ErrorMsg = "Unable to Connect"; $rootScope.timeout();}
        $rootScope.SuccessMsg = "";
    }
    
    $rootScope.successMethod = function(response){
        
    }
    

}]);


app.controller('MyCtrl', ['$scope', '$rootScope', 'data', function($scope, $rootScope, data){
    
    $scope.users = [
        {"name":"James Alexander", "designation":"CEO", "role":"SuperAdmin", "img":"/facesix/static/qcom/img/profile-img/1.jpg", "status":"Active", "alert":"email"},
        {"name":"Nathan Jacobson", "designation":"Network Engineer", "role":"Admin", "img":"/facesix/static/qcom/img/profile-img/2.jpg", "status":"Inactive", "alert":"email"},
        {"name":"Margo Baker", "designation":"Enginner", "role":"Team - A", "img":"/facesix/static/qcom/img/profile-img/3.jpg", "status":"Active", "alert":"mobile"},
        {"name":"Barbara Walden", "designation":"General Manager", "role":"Admin", "img":"/facesix/static/qcom/img/profile-img/3.jpg", "status":"Active", "alert":"email"},
        {"name":"Hanna Dorman", "designation":"Team Leader", "role":"Team - B", "img":"/facesix/static/qcom/img/profile-img/4.jpg", "status":"Active", "alert":"email"},
        {"name":"Benjamin Loretti", "designation":"Network engineer", "role":"Team - A", "img":"/facesix/static/qcom/img/profile-img/5.jpg", "status":"Inactive", "alert":"email"},
        {"name":"Vanessa Aurelius", "designation":"Network engineer", "role":"Team - B", "img":"/facesix/static/qcom/img/profile-img/6.jpg", "status":"Inactive", "alert":"email"},
        {"name":"William Brenson", "designation":"Chief officer", "role":"SuperAdmin", "img":"/facesix/static/qcom/img/profile-img/9.jpg", "status":"Active", "alert":"mobile"}
    ];
    
    $scope.roles = [
        {"name":"Super Admin"},
        {"name":"Admin"},
        {"name":"Team - A"},
        {"name":"Team - B"},
        {"name":"Executive"},
        {"name":"Team Leader"},
        {"name":"Assistant"},
        {"name":"Maintanace"}
    ];
    $scope.myprofile = true;
    $scope.showmyprofile = function(){
        $scope.myprofile = true;
        $scope.alluser = false;
         $scope.allroles = false;
    }
    $scope.showalluser = function(){
         $scope.myprofile = false;
        $scope.alluser = true;
         $scope.allroles = false;
    }
    $scope.showallrole = function(){
         $scope.myprofile = false;
        $scope.alluser = false;
        $scope.allroles = true;
    } 
}]);

app.controller('MyFloor', function($scope, $rootScope){
});

app.controller('VenueCtrl', ['$scope', '$rootScope', 'data', '$window', '$timeout', function($scope, $rootScope, data, $window, $timeout){
      var currentMapCenter = null;  
    var mapOptions = {
	   center: new google.maps.LatLng(0, 0),
	   zoom: 2,
	   minZoom: 1
    };
    var map = new google.maps.Map(document.getElementById('map-canvas'),mapOptions ); 
    //var marker = new google.maps.Marker({map: map});
    var marker = new google.maps.Marker({
            map: map,
            draggable:true,
            animation: google.maps.Animation.DROP
        });
    $scope.venuestep1 = true;
    $scope.searchtrue = true;
    $scope.venue = {};
    $scope.venuenext = function(){
        if($scope.venuestep2 == true){
            $scope.venuestep1 = false;
            $scope.venuestep2 = false;
            $scope.venuestep3 = true;
            $scope.nexttrue = false;
            $scope.savetrue = true;
        }
        if($scope.venuestep1 == true){
            
            $scope.venuestep1 = false;
            $scope.venuestep2 = true;
            $scope.venuestep3 = false;
            $scope.nexttrue = false;
            $scope.prevtrue = true;
            $scope.savetrue = true;
             map.setCenter(center);
            $scope.searchtrue = false; 
        }
    }
    $scope.venueprev = function(){
        if($scope.venuestep3 == true){
            $scope.venuestep1 = false;
            $scope.venuestep2 = true;
            $scope.venuestep3 = false;
            $scope.prevtrue = false;
            $scope.savetrue = false;
            $scope.nexttrue = true;
        }
        if($scope.venuestep2 == true){
            $scope.venuestep1 = true;
            $scope.venuestep2 = false;
            $scope.venuestep3 = false;
            $scope.prevtrue = false;
            $scope.savetrue = false;
            $scope.nexttrue = true;
            $scope.searchtrue = true; 
        }
    }
    $scope.user = {'from': '', 'fromLat': '', 'fromLng' : ''};  
    var inputFrom = document.getElementById('from');
    var autocompleteFrom = new google.maps.places.Autocomplete(inputFrom);
    google.maps.event.addListener(autocompleteFrom, 'place_changed', function() {
        var place = autocompleteFrom.getPlace();
        if (!place.geometry) {
            // Inform the user that the place was not found and return.
            input.className = 'notfound';
            return;
        }
        if (place.geometry.viewport) {
            map.fitBounds(place.geometry.viewport);
            $scope.nexttrue = true;
        } else {
            map.setCenter(place.geometry.location);
            map.setZoom(17); // Why 17? Because it looks good.
            $scope.nexttrue = true;
        }
        
        marker.setIcon( /** @type {google.maps.Icon} */ ({
            url: place.icon,
            size: new google.maps.Size(71, 71),
            origin: new google.maps.Point(0, 0),
            anchor: new google.maps.Point(17, 34),
            scaledSize: new google.maps.Size(35, 35)
        }));
        marker.setPosition(place.geometry.location);
        marker.setVisible(true);
    
        console.log(place);
        $scope.place = place;
        
        $scope.venue.name = place.name;
        $scope.venue.address = place.formatted_address;
        $scope.venue.latitude = place.geometry.location.lat();
        $scope.venue.longitude = place.geometry.location.lng();
        console.log($scope.venue);
//        $scope.user.fromLat = place.geometry.location.lat();
//        $scope.user.fromLng = place.geometry.location.lng();
        //$scope.user.from = place.formatted_address;
        $scope.$apply();
    });
    google.maps.event.addListener(map, 'click', function(event){
        marker.setPosition(event.latLng);
        var latitude = event.latLng.lat();
        var longitude = event.latLng.lng();
        
        $scope.venue.latitude = latitude;
        $scope.venue.longitude = longitude;
        
    });
    google.maps.event.addListener(marker, 'dragend', function(event){
        
        var latitude = event.latLng.lat();
        var longitude = event.latLng.lng();
        $scope.venue.latitude = latitude;
        $scope.venue.longitude = longitude;
        
    });
    var center;
    function calculateCenter() {
      center = map.getCenter();
        console.log(center);
    }
    google.maps.event.addDomListener(map, 'idle', function() {
      calculateCenter();
    });
    google.maps.event.addDomListener(window, 'resize', function() {
      map.setCenter(center);
    });
    $scope.createvenue = function(venue){
        console.log(venue);
        var datas = venue;
        data.CreateVenue(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.getallvenue = function(){
        data.GetAllVenue.then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.getvenue = function(venuelist){
        var datas = venuelist;
        data.GetVenue(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.deletevenue = function(venuelist){
        var datas = venuelist;
        data.DeleteVenue(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
                $scope.getallvenue();
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    
}]);

app.controller('LoginCtrl', ['$scope', '$rootScope', 'data', function($scope, $rootScope, data){
    $scope.loginAuth = function(login){
        var datas = login;
        data.Login(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.loginAuth(login);
}]);

app.controller('FloorCtrl', ['$scope', '$rootScope', 'data', function($scope, $rootScope, data){
    
    $scope.createfloor = function(floor){
        console.log(floor);
        var datas = floor;
        data.CreateFloor(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.getallfloor = function(){
        data.GetAllFloor.then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.getfloor = function(floorlist){
        var datas = floorlist;
        data.GetFloor(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.deletefloor = function(floorlist){
        var datas = venuelist;
        data.DeleteVenue(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
                $scope.getallvenue();
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    
    
    
}]);

app.controller('DashboardCtrl', ['$scope', '$rootScope', 'data', function($scope, $rootScope, data){
    
    $scope.getssdsdashboard = function(data){
        
        var datas = data;
        data.GetSSDSDashboard(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.getfloordashboard = function(data){
        
        var datas = data;
        data.GetFloorDashboard(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.getvenuedashboard = function(data){
        
        var datas = data;
        data.GetVenueDashboard(datas).then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.getlogdashboard = function(){
        
        data.GetLogDashboard().then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    
    $scope.getactiveclient = function(){
        
        data.GetActiveClient().then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    
    $scope.gettypeofdevices = function(){
        
        data.GetTypeOfDevices().then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    $scope.getactiveinterfaces = function(){
        
        data.GetActiveInterfaces().then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    
    $scope.getnetflow = function(){
        
        data.GetNetflow().then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    
    $scope.getconnecteddevices = function(){
        
        data.GetConnectedDevices().then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    
    $scope.getactiveconnections = function(){
        
        data.GetActiveConnections().then(function successCallback(response){
            if(response.data.Status == true){
                $rootScope.SuccessMessage(response);
            }
            else{
                $rootScope.ErrorMessage(response);
            }
        }, function errorCallback(response){
            $rootScope.errorMethod(response);
        });
    }
    
    
}]);