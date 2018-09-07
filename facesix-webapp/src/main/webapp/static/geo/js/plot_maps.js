var labels = '0';
var iconBase = '/facesix/static/geo/images/location_icon.png';
var iconBlue = '/facesix/static/geo/images/grn-blank.png';
var markers = [];
var latlongs = [];
var map;

function initialize() {
	var qubercomm = { lat: 12.958028, lng: 80.247511 };
    map = new google.maps.Map(document.getElementById('map'), {
      	center: qubercomm,
      	zoom: 16,      	
      	mapTypeControlOptions: {
		    position: google.maps.ControlPosition.TOP_RIGHT,
		}
    });

    // Create the search box and link it to the UI element.

    var input = document.getElementById('pac-input');
    var searchBox = new google.maps.places.SearchBox(input);
    map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

    // Bias the SearchBox results towards current map's viewport.
    map.addListener('bounds_changed', function() {

      searchBox.setBounds(map.getBounds());
    });
	$('span.n_search').click(function(){ 				
	 });  
    google.maps.event.addListener(map, 'click', function(event) {
      	addMarker(event.latLng, map);					          	
    });
    
    // Listen for the event fired when the user selects a prediction and retrieve
    // more details for that place.
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

        // For each place, get the icon, name and location.
        var bounds = new google.maps.LatLngBounds();
        places.forEach(function(place) {
            if (!place.geometry) {
              	console.log("Returned place contains no geometry");
              	return;
            }
            var icon = {
              	url: place.icon,
              	size: new google.maps.Size(71, 71),
              	origin: new google.maps.Point(0, 0),
              	anchor: new google.maps.Point(17, 34),
              	scaledSize: new google.maps.Size(25, 25)
            };

            // Create a marker for each place.
            markers.push(new google.maps.Marker({
              	map: map,
              	icon: icon,
              	title: place.name,
              	position: place.geometry.location
            }));

            if (place.geometry.viewport) {
              	// Only geocodes have viewport.
              	bounds.union(place.geometry.viewport);
            } else {
              	bounds.extend(place.geometry.location);
            }
        });

        map.fitBounds(bounds);
    });
    
    reloadMap();
    

}

// Adds a marker to the map.
function addMarker(location, map) {
	labels++;
	latlongs.push({"latitude": location.lat(), "longitude": location.lng()});
	var marker = new google.maps.Marker({
	  	position: location,
	  	draggable:true,
	  	clickable:true,
	  	title:"Drag me!",
	  	labelClass: "n_labels",
	  	//label: {text:labels+"",color:"white"},
	  	map: map,
	  	'anchorPoint': new google.maps.Point(location.lat(), location.lng()),
	  	icon:iconBase,
	});

    showInfoWindow(location,marker);
    removeStyleInfoWindow();

   	google.maps.event.addListener(marker, 'dragend', function(ev){
	    console.log("after drag : "+marker.getPosition().lat()+" , "+marker.getPosition().lat());
	    showInfoWindow(marker.getPosition(),marker);
	    removeStyleInfoWindow();
	});

	google.maps.event.addListener(marker, 'click', function(ev){
	    // showInfoWindow(marker.getPosition(),marker);
	    // console.log(marker.icon);
	    if(marker.icon == iconBase){
	    	marker.setIcon(iconBlue);
	    }else{
	    	marker.setIcon(iconBase);
	    }
	    removeStyleInfoWindow();
	});


	markers.push(marker);
}

function showInfoWindow(location,marker) {
	infowindow = new google.maps.InfoWindow();
	infowindow.setContent('<div class="mrker_info">'+location.lat().toFixed(6)+' , '+location.lng().toFixed(6)+'</div>');
	infowindow.open(map, marker);

	google.maps.event.addListener(infowindow,'closeclick',function(){
		marker.setMap(null);
	});					        
}

function removeStyleInfoWindow() { 
	var gm=$('.gm-style-iw');
		gm.prev().remove();
		// gm.next().remove();

	setTimeout(function(){
		var gm=$('.gm-style-iw');
		gm.prev().remove();
		//gm.next().remove();
	}, 100);				      		
}


$(document).on("mouseleave", ".gmnoprint", function() {
	$(this).parent().next().find('img').css('opacity','0');
});

$(document).on('click','.gmnoprint img',function(){
	$('.gmnoprint').click(false);
})

$(document).on('mouseenter','.gmnoprint',function(){
	var xx = $(this).index();
	$(this).parent().next().find('.gm-style-iw:eq('+xx+')+div').addClass('pk');
})
$(document).on('mouseleave','.gmnoprint',function(){
	$(this).parent().next().find('.gm-style-iw+div').removeClass('pk');
})

$(document).on("mouseenter", ".gmnoprint", function() {
	$(this).parent().next().find('img').css('opacity','0');
	
	$(this).parent().next().find('img').css('opacity','0');
	$(this).parent().removeClass('mymarkhover');
});
$(document).on("mouseenter", ".gm-style-iw+div", function() {

$(this).addClass('pk');
});
$(document).on("mouseleave", ".gm-style-iw+div", function() {

$(this).removeClass('pk');
});

$(function(){
	$('span.n_close').click(function(){
		$('input#pac-input').val('');
	});

$('span.n_search').click(function(){
	var event=new Event('place_changed');
	 $('#pac-input').trigger('place_changed');    
});
});

function resetMap(){
    // Clear out the old markers.
    markers.forEach(function(marker) {
    	marker.setMap(null);
    });

    markers = [];
    latlongs = [];
    labels  = '0';
}

function exportMapData(){
	return latlongs;
}

function reloadMap(){
	var canvas_url = '/facesix/api/geo/plot/'+spid;
	$.ajax({
	    url:canvas_url+'/map',
	    type:'GET',                                          
	    success: function(data){
	    	console.log('Received canvas data');
	    	console.log(data);	    		    	
	    	if(data!=''){
	    		var s = JSON.parse(JSON.stringify(data));	
	    		reloadMarkers(s);
	    	}
	    }
	 });
}

function reloadMarkers(map_data) {
	console.log('Reloading markers');
	var myLatLng;
	latlongs = map_data;	
	labels = '0';
	 for (var i = 0; i < latlongs.length; i++) {
	 	labels++;
	 	myLatLng = new google.maps.LatLng(latlongs[i].latitude, latlongs[i].longitude);		
		var marker = new google.maps.Marker({
		  	position: myLatLng,
		  	draggable:true,
		  	clickable:true,
		  	title:"Drag me!",
		  	labelClass: "n_labels",
		  	//label: {text:labels+"",color:"white"},
		  	map: map,
		  	'anchorPoint': new google.maps.Point(myLatLng.lat(), myLatLng.lng()),
		  	icon:iconBase,
		});

	    showInfoWindow(myLatLng,marker);
	    removeStyleInfoWindow();

	   	google.maps.event.addListener(marker, 'dragend', function(ev){
		    console.log("after drag : "+marker.getPosition().lat()+" , "+marker.getPosition().lat());
		    showInfoWindow(marker.getPosition(),marker);
		    removeStyleInfoWindow();
		});

		google.maps.event.addListener(marker, 'click', function(ev){
		    // showInfoWindow(marker.getPosition(),marker);
		    // console.log(marker.icon);
		    if(marker.icon == iconBase){
		    	marker.setIcon(iconBlue);
		    }else{
		    	marker.setIcon(iconBase);
		    }
		    removeStyleInfoWindow();
		});
		markers.push(marker);
	}
	console.log('re-center map nearer to the markers!');
	map.setCenter(myLatLng);
}