var labels = '0';
var iconBase = '/facesix/static/geo/images/location_icon.png';
var iconBlue = '/facesix/static/geo/images/grn-blank.png';
var markers = [];
var latlongs = [];
var coordinates = [];
var map;
oWidth  = 0,
oHeight = 0;
var jnifileW = 1000;
var jnifileH = 600;

function savePoints(){
	var mcnt = 0;
	
	var mystr = '{"mapzoom":"'+map.getZoom()+'","opacity":"'+opa+'","zoom":"'+zom+'","rotation":"'+ang+'","latlng":[';
	markers.forEach(function(marker) {
		
		//console.log ("marker" + marker.coord[mcnt].x, marker.coord[mcnt].y)

		mystr = mystr + '{"latitude":'+marker.internalPosition.lat()+',"longitude":'+marker.internalPosition.lng()+',"x":'+marker.coord[mcnt].x+',"y":'+marker.coord[mcnt].y+'},';
		mcnt++;
	});
	mystr = mystr.slice(0, -1);
	mystr = mystr + ']}';
	

	//alert(mystr);
	//console.log(mystr);
	var uid= "";
	if(mcnt == 4) {
		$.ajax({
	        url:"/facesix/rest/beacon/geo/plot?spid="+spid+"&uid="+uid,
	        type:"POST",
	        data: mystr, 
	        contentType:"application/json; charset=utf-8",
	        dataType:"json",
	        statusCode: {
	            200: function(data) {
	              console.log('successfully processed');
	              self.location="/facesix/web/site/portion/list?sid="+sid;
	            }
	        }
		});
	} else {
		console.log("Mark four points")
	}
	$(".loader_box").show();
}
function initialize() {
	reloadMap();
}
$(document).ready(function(){
	  //console.log ("Ready" +"Test");
	  var txtbox = "";
	  $(document).on('focus', '#pac-input', function(){
	  txtbox = $(this).val();
		console.log(txtbox);
		$(this).val('');
	   });
	
	   $(document).on('blur', '#pac-input', function(){
		   $(this).val(txtbox);
	   });
	   
	   $(document).mousedown(function(event) {
           //console.log(event.clientX, event.clientY);
           iX = event.clientX;
           iY = event.clientY;
           //plot(options);
	   }); 
});



// Adds a marker to the map.
function addMarker(location, map) {
	labels=markers.length+1;
	if(labels>4) return;
	
	var oX = 0;
	var oY = 0;	
	if (labels == 1) {
		oX = 0;
		oY = 0;	
	}
	if (labels == 2) {
		oX = jnifileW-1;
		oY = 0;	
	}
	if (labels == 3) {
		oX = jnifileW-1;
		oY = jnifileH-1;	
	}
	if (labels == 4) {
		oX = 0;
		oY = jnifileH-1;	
	}
    console.log("New Cord" + oX, oY); 
	
	latlongs.push({"latitude": location.lat(), "longitude": location.lng()});
	coordinates.push({"x": oX , "y": oY});
	var marker = new google.maps.Marker({
	  	position: location,
	  	draggable:true,
	  	clickable:true,
	  	label:""+labels,
	  	map: map,
	  	'anchorPoint': new google.maps.Point(location.lat(), location.lng()),
	  	icon:iconBase,
	  	coord:coordinates,
	});
	google.maps.event.addListener(marker, 'dragstart', function() {
        disableMovement(true);
    });

    google.maps.event.addListener(marker, 'dragend', function() {
        disableMovement(false);
    });
	markers.push(marker);
}

function getCoordinateArray(){
	return coordinates;
}

function disableMovement(disable) {
    var mapOptions;
    if (disable) {
        mapOptions = {
            draggable: false,
            scrollwheel: false,
            disableDoubleClickZoom: true,
            zoomControl: false
        };
    } else {
        mapOptions = {
            draggable: true,
            scrollwheel: true,
            disableDoubleClickZoom: false,
            zoomControl: true
        };
    }
    map.setOptions(mapOptions);
}

function resetMap(){
    // Clear out the old markers.
    markers.forEach(function(marker) {
    	marker.setMap(null);
    });

    markers 	= [];
    latlongs 	= [];
    coordinates = [];
    labels  	= '0';
}

function exportMapData(){
	return latlongs;
}

function reloadMap(){
	var canvas_url = '/facesix/rest/beacon/geo/plot/'+spid+'/'+sid;
	$.ajax({
	    url:canvas_url+'/map',
	    type:'GET',                                          
	    success: function(data){
	    	if(data!=''){
	    		var s = JSON.parse(JSON.stringify(data));
		    	reloadMarkers(s.latlng,s.mapzoom*1);
		    	ang = s.rotation;
		    	zom = s.zoom;
		    	opa = s.opacity;
		    	jnifileW =s.width;
		    	jnifileH =s.height;
		    	adjOpa(0);
		    	adjZom(0);
		    	adjRot(0);
		    	//console.log("fgJson " +JSON.stringify(s));
		    	//console.log(" jnifileW " +jnifileW + " jnifileH " +jnifileH);
		    	
	    	} else {
	    		var testata = {"latlng":[{"latitude":12.958028,"longitude":80.247511}]}
	    		var s = JSON.parse(JSON.stringify(testata));
		    	reloadMarkers(s.latlng,10);
	    		console.log('empty resp');
	    		zom = 80;
	    		adjZom(0);
	    	}
	    }, error: function(data){
	    	var testata = {"latlng":[{"latitude":12.958028,"longitude":80.247511}]}
    		var s = JSON.parse(JSON.stringify(testata));
	    	reloadMarkers(s.latlng,10);
	    	console.log('error resp');
    		zom = 80;
    		adjZom(0);
	    }
	 });
}
var labels = 0;
function reloadMarkers(map_data,mapzom) {
	map = new google.maps.Map(document.getElementById('map'), {
      	mapTypeId: 'satellite',
      	disableDefaultUI: true,
      	mapTypeControl: true,
      	zoomControl:true,
      	mapTypeControlOptions: {
		    position: google.maps.ControlPosition.TOP_RIGHT,
		}
    });
	var input = document.getElementById('pac-input');
    var searchBox = new google.maps.places.SearchBox(input);
    map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

    // Bias the SearchBox results towards current map's viewport.
    map.addListener('bounds_changed', function() {

      searchBox.setBounds(map.getBounds());
    });
	 
    google.maps.event.addListener(map, 'click', function(event) {
      	addMarker(event.latLng, map);					          	
    });
    
    google.maps.event.addListener(map, 'dblclick', function(event) {
        return;
   });
    
   
    searchBox.addListener('places_changed', function() {
    	
    	var places = searchBox.getPlaces();

    	if (places.length == 0) {
        	return;
    	}

        // Clear out the old markers.
        markers.forEach(function(marker) {
        	marker.setMap(null);
        });
        markers = [];
        labels = 0;
        var bounds = new google.maps.LatLngBounds();
        places.forEach(function(place) {
            if (!place.geometry) {
              	//console.log("Returned place contains no geometry");
              	return;
            }
            var icon = {
              	url: place.icon,
              	size: new google.maps.Size(71, 71),
              	origin: new google.maps.Point(0, 0),
              	anchor: new google.maps.Point(17, 34),
              	scaledSize: new google.maps.Size(25, 25)
            };
         
            if (place.geometry.viewport) {
              	// Only geocodes have viewport.
              	bounds.union(place.geometry.viewport);
            } else {
              	bounds.extend(place.geometry.location);
            }
        });

        map.fitBounds(bounds);
    });
	var myLatLng;
	latlongs = map_data;	
	var cpt;
	var bounds = new google.maps.LatLngBounds();
	 for (var i = 0; i < latlongs.length; i++) {
	 	labels++;
	 	myLatLng = new google.maps.LatLng(latlongs[i].latitude, latlongs[i].longitude);
	 	if(i==1) cpt = myLatLng;
		var marker = new google.maps.Marker({
		  	position: myLatLng,
		  	draggable:true,
		  	clickable:true,
		  	labelClass:"",
		  	label:""+labels,
		  	map: map,
		  	'anchorPoint': new google.maps.Point(myLatLng.lat(), myLatLng.lng()),
		  	icon:iconBase,
		});
		bounds.extend(marker.getPosition());
	    
	   	google.maps.event.addListener(marker, 'dragstart', function() {
	        disableMovement(true);
	    });

	    google.maps.event.addListener(marker, 'dragend', function() {
	        disableMovement(false);
	    });
		
		markers.push(marker);
	}
	map.fitBounds(bounds);
	map.setZoom(mapzom);
	$('#planimg').delay(500).fadeIn(('slow'))
}
var ang = 0;
var zom = 100;
var opa = 0.8;


function adjOpa(v){
	opa = opa*1 + v*1;
	opa = opa.toFixed(1);
	if(opa>=1) {
		opa =1;
		$('#op').removeClass("clkyes");
		$('#op').addClass("clkno");
	} else {
		$('#op').removeClass("clkno");
		$('#op').addClass("clkyes");
	}
	if(opa<=0.1) {
		opa =0.1;
		$('#om').removeClass("clkyes");
		$('#om').addClass("clkno");
	} else {
		$('#om').removeClass("clkno");
		$('#om').addClass("clkyes");
	}

	$('#map').css("opacity", opa);
}
function adjZom(v) {
	
	console.log ("zoom value" + v)
	zom = zom*1 + v*1;
	console.log ("zoom Zom" + zom)
	if(zom >= 100) {
		zom = 100;
		$('#zp').removeClass("clkyes");
		$('#zp').addClass("clkno");
	} else {
		$('#zp').removeClass("clkno");
		$('#zp').addClass("clkyes");
	}
	if(zom <= 1) {
		zom = 1;
		$('#zm').removeClass("clkyes");
		$('#zm').addClass("clkno");
	} else {
		$('#zm').removeClass("clkno");
		$('#zm').addClass("clkyes");
	}
	console.log ("jnifileH" + jnifileW +" " + jnifileH)
	if (jnifileW == undefined) {
		jnifileW = 1000;
	}
	if (jnifileH == undefined) {
		jnifileH = 600;
	}
	
	var pW = jnifileW * zom / 100;
	var pH = jnifileH * zom / 100;
	
	console.log ("pW pH" + pW +" " + pH)
	$('#planimg').css("width",pW+"px");
	$('#planimg').css("height",pH+"px");	
	
    oWidth   = pW;
    oHeight  = pH;
}

function getScaledWidth(x){
    var x1 = 0, deltaWidth  = 0;
    var widthScaleUp = false;

    deltaWidth = oWidth;
     if(width > oWidth){
        widthScaleUp = true;
        deltaWidth = width / oWidth;
    } else if(width < oWidth){
        widthScaleUp = false;
        deltaWidth = oWidth / width;
    }

    if(widthScaleUp){
        console.log('width scaled up');
    } else {
        console.log('width scaled down');
    }

    if(widthScaleUp){
        x1 = Math.round(x / deltaWidth);
    } else {
        x1 = Math.round(x * deltaWidth);
    }
    console.log('x : '+x+' , x1 : '+x1);
    return x1;
}

// To get the exact 
function getScaledHeight(y){
    var y1 = 0, deltaHeight = 0;
    var heightScaleUp = false;    
    // calculate the scaling factor for height
    if(height > oHeight){
        heightScaleUp = true;
        deltaHeight = height / oHeight;
    } else if(height < oHeight){
        heightScaleUp = false;
        deltaHeight = oHeight / height;
    }
    if(heightScaleUp){
        y1 = Math.round(y / deltaHeight);
    } else {
        y1 = Math.round(y * deltaHeight);
    }
    console.log('y : '+y+' , y1 : '+y1);
    return y1;
}



function adjRot(v) {
	ang = ang*1 + v*1;
	if(ang>359) ang =0;
	if(ang<0) ang =359;
	$('#planimg').css("transform", "translate(-50%, -50%) rotate(" + ang + "deg)");
	$('#planimg').css("-webkit-transform", "translate(-50%, -50%) rotate(" + ang + "deg)");
}