var interval_id;

// store state of the canvas and google map points	
function initiatePlotProcess(){
	// show loading animation
	var fg_json = JSON.stringify(exportFgCanvasAsJson());
	var gmap_markers = JSON.stringify(exportMapData());
	var geo_points = generateGeoPoints();	
	var pixels = JSON.stringify(getCoordinateArray());
	if(geo_points  === '') return;
	$('.loader_box').show();	
	var plot_data = new Object();
	plot_data.type = "plot";
	plot_data.fg_json = fg_json;
	plot_data.gmap_markers = gmap_markers;
	plot_data.geo_points = geo_points;
	plot_data.pixels = pixels;
	 
	var pdata = JSON.stringify(plot_data);
	console.log(pdata);

	// send data to server
	$.ajax({
        url:"/facesix/api/geo/plot/"+spid,
        type:"POST",
        data: pdata, 
        contentType:"application/json; charset=utf-8",
        dataType:"json",
        statusCode: {
            200: function() {
              console.log('successfully processed');
              interval_id = setInterval(poll, 4000);
            }
        }
	});
};

function generateGeoPoints(){
    console.log("merge arrays");
    var geopoints = [];
    var coordinates = getCoordinateArray();
    var latlongs    = exportMapData();
    var output = '';
    
    if(latlongs.length == coordinates.length){    
	    for(var i=0; i<coordinates.length; i++){
	        geopoints.push({"latitude": latlongs[i].latitude, "longitude": latlongs[i].longitude,
	                "x": coordinates[i].x, "y":coordinates[i].y});
	    }
	    console.log(JSON.stringify(geopoints));
	    output =JSON.stringify(geopoints); 
    } else {
    	var error_message = '';
    	if(latlongs.length < coordinates.length){
    		// missing marker on google maps
    		bootbox.alert('No. of map markers aren\'t matching with co-ordinates!');   		
    	} else if(coordinates.length < latlongs.length){
    		// missing co-ordinates 
    		bootbox.alert('No. of co-ordinates aren\'t matching with markers!');
    	}
    }
    
    return output;
}

//'/facesix/web/site/portion/planfile?spid={{spid}}'
function reset(){
	console.log("Reset map and canvas");
	resetCanvas();
	resetMap();
}

function reload(){
	console.log('reload');
	reloadMap();
	reloadCanvas();
}

function plotcorners(){    
    console.log("Trigger plor corners process...");
    var points = mergeArrays();
                                     
}

function poll(){
   console.log('polling for status update...');
   $.ajax({
      url:"/facesix/api/geo/plot/status/"+spid,
      type:"GET",                                          
      success: function(data){
      	  if(data == 'in-progess'){
      		  console.log('operation in progress...');
          } else if(data == 'success'){
          		console.log('successfully processed!');
              	// hide animation
              	$('.loader_box').hide();
              	$('.completed_box').fadeIn(300, function(){$('.completed_box').show();
              		setTimeout(function(){
              			$('.completed_box').hide();
              		},1000)
              	});
              	// cancel polling on successful response
              	clearInterval(interval_id);               	  
          } else if(data == 'failure'){
          		console.log('failed');
        		$('.loader_box').hide();
        		$('.failure_box').fadeIn(300, function(){$('.failure_box').show();
        			setTimeout(function(){
        				$('.failure_box').hide();
        			},1000)
        		});
        		clearInterval(interval_id);
          }
      }
   });
} 

// poi 

//store state of the canvas and google map points	
function initiatePoiProcess(){
	// show loading animation
	var fg_json = JSON.stringify(exportFgCanvasAsJson());
	//$('.loader_box').show();	
	var poi_data = new Object();
	poi_data.type = "poi";
	poi_data.fg_json = fg_json;
	 
	var pdata = JSON.stringify(poi_data);
	console.log('poi data : '+pdata);

	// send data to server
	$.ajax({
        url:"/facesix/api/geo/poi/"+spid,
        type:"POST",
        data: pdata, 
        contentType:"application/json; charset=utf-8",
        dataType:"json",
        statusCode: {
            200: function() {
              console.log('successfully processed');
              //interval_id = setInterval(poll, 4000);
//              $('.loader_box').hide();
//            	$('.completed_box').fadeIn(300, function(){$('.completed_box').show();
//            		setTimeout(function(){
//            			$('.completed_box').hide();
//            		},1000)
//            	});
            }
        }
	});
};



$("#myModal").on("show", function() {    // wire up the OK button to dismiss the modal when shown
    $("#myModal a.btn").on("click", function(e) {
        console.log("button pressed");   // just as an example...
        $("#myModal").modal('hide');     // dismiss the dialog
    });
});
$("#myModal").on("hide", function() {    // remove the event listeners when the dialog is dismissed
    $("#myModal a.btn").off("click");
});

$("#myModal").on("hidden", function() {  // remove the actual elements from the DOM when fully hidden
    $("#myModal").remove();
});

$("#myModal").modal({                    // wire up the actual modal functionality and show the dialog
  "backdrop"  : "static",
  "keyboard"  : true,
  "show"      : true                     // ensure the modal is shown immediately
});