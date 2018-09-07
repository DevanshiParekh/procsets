var imageW	  = 40;
var imageH	  = 40;
var fzie = 30;
var txty = 9;
(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
    //var timer = 500000;
	//var count = 1;
	var peerStats;
	var counterIncrement = 0;
	var counterIncrement1 = 0;
	var counterIncrement2 = 0; 
    FloorDashboard = {
        timeoutCount: 10000,
        charts: {
            urls: {
              activeConnections: '/facesix/rest/beacon/ble/networkdevice/floor/taginfo?spid='+urlObj.spid+"&cid="+urlObj.cid+"&sid="+urlObj.sid,
            },
            setChart: {
               
                activeConnections: function (initialData,params) {
                    $.ajax({
                        url:FloorDashboard.charts.urls.activeConnections,
                        success: function (result) {

                          console.log("active >>" + JSON.stringify(result));
                        	   counter=0; 
                           var tagsIn = result.activeTags;
                            $('#tagsIn').html(tagsIn);
                            if(counterIncrement == 0){
                               $('#tagsIn').each(function () {
                                     $(this).prop('Counter',0).animate({
                                         Counter: $(this).text()
                                     }, {
                                         duration: 2000,
                                         easing: 'swing',
                                         step: function (now) {
                                             $(this).text(Math.ceil(now));
                                         }
                                     });
                                 });
                              counterIncrement = 1;
                            }
                                                   

                          var tagsIdle = result.idleTags;
                            $('#tagsIdle').html(tagsIdle);
                            if(counterIncrement1 == 0){
                               $('#tagsIdle').each(function () {
                                     $(this).prop('Counter',0).animate({
                                         Counter: $(this).text()
                                     }, {
                                         duration: 2000,
                                         easing: 'swing',
                                         step: function (now) {
                                             $(this).text(Math.ceil(now));
                                         }
                                     });
                                 });
                              counterIncrement1 = 1;
                            }

                          var tagsInactive = result.inactiveTags;
                            $('#tagsInactive').html(tagsInactive);
                            if(counterIncrement2 == 0){
                               $('#tagsInactive').each(function () {
                                     $(this).prop('Counter',0).animate({
                                         Counter: $(this).text()
                                     }, {
                                         duration: 2000,
                                         easing: 'swing',
                                         step: function (now) {
                                             $(this).text(Math.ceil(now));
                                         }
                                     });
                                 });
                              counterIncrement2 = 1;
                            }
                           
                            
                             var result  = result.connectedTagType;
                             var columns=[];
                             var names={};
                             var colors={},colorMap={
                                 'enabled':"#6baa01",
                                 'disabled':'#cccccc'
                             }
                             
                             for(var i = 0; i< result.length; i++){
                                    columns.push([result[i].tagType,result[i].tagCount]);
                                    names["data"+(i+1)]=result[i].tagType;
                                    colors[result[i].tagType]=colorMap[result[i].status];
                             }                              

                             FloorDashboard.charts.chartConfig.typeOfDevices.data.columns = columns;
                             FloorDashboard.charts.chartConfig.typeOfDevices.data.names   = names;
                             FloorDashboard.charts.chartConfig.typeOfDevices.data.colors  = colors;
                             FloorDashboard.charts.getChart.typeOfDevices = c3.generate(FloorDashboard.charts.chartConfig.typeOfDevices);
                             if (initialData) {
                              FloorDashboard.charts.getChart.typeOfDevices = c3.generate(FloorDashboard.charts.chartConfig.typeOfDevices);
                             } else {
                              FloorDashboard.charts.getChart.typeOfDevices.load({ "columns": FloorDashboard.charts.chartConfig.typeOfDevices.data.columns,'colors': FloorDashboard.charts.chartConfig.typeOfDevices.data.colors});
                             }  
                            
                          
                            setTimeout(function () {
                             FloorDashboard.charts.setChart.activeConnections();
                            }, FloorDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                           setTimeout(function () {
                             FloorDashboard.charts.setChart.activeConnections();
                           }, FloorDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                }
            },
            getChart: {},
            chartConfig: {
    
                typeOfDevices: {
                    size: {
                        height: 90,
                    },
                    bindto: '#dd-chart',
                    padding: {
                    	top: 15,
                        right: 2,
                        bottom:0,
                        left: 0,
                    },
                    transition: {
                	  duration: 500
                	},
                    data: {
                        columns: [],
                        names: {},
                        colors: {},
                        type: 'bar',
                    },
 
                	axis: {
                	    x: {
                	      show: false
                	    },
                	    y: {
                  	      show: false
                  	    }
                	},
                    tooltip: {
                        format: {
                        	title: function (v) {
                        		return "Active Tag Types";
                    		},
                    		value: function (i, j, k) {
                               	return 'Tags='+i;
                            }
                        }
                    }, 
                    legend: {
                        show: false
                    }
                }
            }
        },
        init: function (params) {
            var c3ChartList = ['activeConnections'];
            var that = this;
            $.each(c3ChartList, function (key, val) {
                that.charts.setChart[val]();
            });
        }
        
    }
})();
currentDashboard=FloorDashboard;


//Network config Replica
var changetag = 1;
var circleval = 0;
var inactval  = 0;	

function showTag(v) {
	if (v == "1") {
		$('.person').show();
		$('.qrnd').show();
	} else {
		$('.person').hide();
		$('.qrnd').hide();
	}

}

function zoomicon(value) {
	if (value == "1") {
		$('.slider-section').show();
	} else {
		$('.slider-section').hide();
	}

}
var urlLink;
var tagtype  = "\uf007";
var color 	 = "#4337AE"//"#90EE90"
var tagcolor = "#FFA500";
var list;
var bDemofound = false;
var tagsCounter = 0;

var tagStatus 	= 0;

var filterTagactive = 1;
var filterCategories = [];
var zoomEnabled = 0;
var tagsONOFF = 1;
var inactiveONOFF = 1;
var switchONOFF = 0;
var category = [];
$('#tagsONOFF').change(function(){ 
	if($(this).prop('checked') == true){
		tagsONOFF = 1;
		d3.selectAll('.person').classed('tagdisable', false);
		$('.filterUI').addClass('active');
	}
	else{
		tagsONOFF = 0;
		d3.selectAll('.person').classed('tagdisable', true);
		$('.filterUI').removeClass('active');
	}  
});
$('#inactiveONOFF').change(function(){ 
	if($(this).prop('checked') == true){
		inactiveONOFF = 1; 
	}
	else{
		inactiveONOFF = 0; 
	} 
	if(tagsONOFF == 1){ 
		$.each(filterCategories, function(index, val) { 
			if(inactiveONOFF == 1){
				d3.selectAll('.person.'+val).classed('tagdisable', false);
			}
			else{
				d3.selectAll('.person.inactive').classed('tagdisable', true);
				d3.selectAll('.person.active.'+val).classed('tagdisable', false);
				d3.selectAll('.person.idle.'+val).classed('tagdisable', false);
			}
		});
	}
	else{
		d3.selectAll('.person').classed('tagdisable', true);
	}
});
$('#switchONOFF').change(function(){ 
	if($(this).prop('checked') == true){
		switchONOFF = 1; 
		d3.selectAll('.animatedImage').classed('tagdisable', false);	
	}
	else{
		switchONOFF = 0; 
		d3.selectAll('.animatedImage').classed('tagdisable', true);
	} 
});
$('.catFilter .multiselect-ui').change(function(){
	filterCategories = []; 
	$.each($(".catFilter .multiselect-ui option:selected"), function(){            
    	filterCategories.push($(this).val());  
    });  
	if(tagsONOFF == 1){ 
		$.each(filterCategories, function(index, val) { 
			if(inactiveONOFF == 1){
				d3.selectAll('.person.'+val).classed('tagdisable', false);
			}
			else{
				d3.selectAll('.person.inactive').classed('tagdisable', true);
				d3.selectAll('.person.active.'+val).classed('tagdisable', false);
				d3.selectAll('.person.idle.'+val).classed('tagdisable', false);
			}
		});
	}
	else{
		d3.selectAll('.person').classed('tagdisable', true);
	}
});


var floornetworkConfig={
		
   'plantDevicesTags' :function(urlSpid,p1, p2, p3){
	   
	   if (p1 != "true") {
		   //	$('.person').remove();
			// $('.qrnd').remove();
		  var callTime = new Date();
		  console.log("plantDevice called at "+callTime.getHours()+":"+callTime.getMinutes()+":"+callTime.getSeconds()+"."+callTime.getMilliseconds());
	      var milli = callTime.getTime();
	    	$.ajax({
	         	url:'/facesix/rest/site/portion/networkdevice/personinfo?spid='+urlSpid+"&time="+milli,
	             method:'get',
	             success:function(response){
	            	 var curTime = (new Date()).getTime();
	            	 var responseTime = curTime - milli;
	            	 console.log("response time in floordashboard= "+responseTime);
	            	 list = response;
	            	 
	            	 setTimeout(function() {
                  var cur_time = new Date();
                  console.log("timer call  "+cur_time.getHours()+":"+cur_time.getMinutes()+":"+cur_time.getSeconds()+"."+cur_time.getMilliseconds());
	         	     	floornetworkConfig.plantDevicesTags(urlSpid,p1, p2, p3);  
	                 }, changetag*1000);
	            	 
	             },  error:function(err){
	             }
	         });
        	
	    	
	    	/* var finalObj = [];
	    	function getRandomFloat(min, max) {
	    	  return Math.random() * (max - min) + min;
	    	}
	    	if (typeof list != "undefined" && list !="") {
		    	for (var i = 0; i < list.length; i++) {
		    		var count = checkxy(parseInt(list[i].x),parseInt(list[i].y),finalObj); 
		    		if(count > 0 && list[i].state == 'active'){ 
		    			list[i].x = parseInt(list[i].x) + getRandomFloat(-1,1);
		    			list[i].y = parseInt(list[i].x) + getRandomFloat(-1,1);  
		    			finalObj.push(list[i]);
		    		}else{
		    			finalObj.push(list[i]);
		    		}
		    	}
	    	}
	    	function checkxy(x,y, finalObj){ 
	    		var count = 0; 
	    		$.each(finalObj, function (key, val) {
	    			if((parseInt(val.x) == x) && parseInt(val.y) == y){
	    				count = count + 1;
	    			}
	    		});
	    		return count;
	    	}  
	    	*/
	    	function getRandomFloat(min, max) {
	    		return Math.random() * (max - min) + min;
	    	}
	    	if (typeof list != "undefined" && list !="") {
	    			
   	    		for (var i = 0; i < list.length; i++) {   
   	    			tags = list[i];
   	    			
   	    			category.push(tags.tagType); 
   	    			
   	    			var mcId = 'tags-'+tags.macaddr;
	    				mcId = mcId.replace(/:/g , "-");
   	    			var tagsFound = document.getElementById(mcId);
   	    			
   	    			if($( "g[data-x='"+tags.x+"'][data-y='"+tags.y+"']" ).length != 0){  
   	    				var newElement = $( "g[data-x='"+tags.x+"'][data-y='"+tags.y+"']" ).attr('id');
   	    				if(newElement != mcId){
   	    					tags.x = parseInt(tags.x) + getRandomFloat(-1,1);
	   	    				tags.y = parseInt(tags.y) + getRandomFloat(-1,1);
   	    				} 
   	    			}
   	    			
   	    				if (tags.state != undefined && tags.state != "") {        			
	              			state = tags.state       			
	              		}
	              		
        				if (state == "active"){        			
        					tagcolor  = "yellow";
        					strkcolor = "green"  
        					color 	  = "#3d3ef7" 
        					bgColor = "rgba(70, 191, 189, 0.5)";
        				} else if (state == "inactive"){        			
        					tagcolor  = "gray";  
        					strkcolor = "red";
        					color 	  = "#051a08" 
        					bgColor = "rgba(246, 70, 75, 0.5)";
        				} else if (state == "idle"){        			
  			        		tagcolor 	= "yellow";//"#FFA500";
        					strkcolor 	= "orange"; 
        					color 		= "#381a08"   
        					bgColor = "rgba(240, 114, 0, .5)";
        				}
	              	
	              		if (tags.tag_type != undefined && tags.tag_type != "") {
	              			tagtype = updateTagType(tags.tag_type);
	              		}
	              		
	              		var date = tags.lastReportingTime;;
	              		if(date == null || date == undefined){
	              			date = "Not Seen";
	              		}
	              		
	              if(tagsFound == null) {
	              			
	              		var mainGroup = this.svg.append('g')
	              			.style("cursor","pointer")
	              			.attr('id', mcId)
	              			.attr("fill", bgColor)
	              			.attr('data-x',tags.x)
	   	    				.attr('data-y',tags.y)
	              			.attr("class","person animateZoom "+state+" "+tags.tag_type)
	              			.attr("info","Name:"+tags.assignedTo + " <br/> Last Seen:" + date + "<br/> Location :" + tags.reciveralias)
	              			.attr('transform', "translate("+tags.x+","+tags.y+")") 
	              			.attr('data-html', 'true')
	              			.attr('title',"Name:"+tags.assignedTo + " <br/> Last Seen:" + date + "<br/> Location :" + tags.reciveralias);
	              			$(mainGroup).tooltip({container:'body'});
	
	              		var subGroup = mainGroup.append('g')
	              			.attr('id', mcId+'-sub') 
	              			.attr('transform','translate(0,0)') 
	              			.attr("class","onlyscale"); 
		
	              		var circle = subGroup.append("circle")
	              			.attr("r", fzie)
	              			.attr("y", "0").
	              			attr("class", "animateZoomCircle");
	              		var txt = subGroup.append("text") 
	              			.attr("alignment-baseline",'middle')  
	              			.attr("font-family","FontAwesome")
	              			.style("fill",'#fff')
	              			.style("cursor","pointer")  
	              			.attr("text-anchor", "middle") 
	              			.attr("y", txty) 
	              			.attr('font-size', function(d) { return fzie+'px';} )
	              			.text(function(d) { return tagtype; });
	  
      		        	tagsCounter = tagsCounter + 1;
      		        	
   	    			} else {
   	    				//console.log(state);
            			var macaddr = tags.macaddr;
               			
               			if (macaddr=="3F:23:AC:22:FF:F3" || macaddr=="3F:23:AC:22:FF:F4") { // test
               			
        	        		tags.x = tags.x*1+5+(Math.floor(Math.random() * 200));	
        	        		tags.y = tags.y*1+13+(Math.floor(Math.random() * 200));	
        	        		if (tags.x >= 1376) {
        	        			tags.x = 500;
        	        		}
        	        		if (tags.y >= 768) {
        	        			tags.y = 250;
        	        		}	
        	        		//console.log(" macaddr : " +macaddr);
        	        		bDemofound = true;
               			}
               			   			
               			this.svg.selectAll('#'+mcId).transition()
   	    			    .duration(1000)
   	    			    .attr("fill", bgColor)
   	    			    .attr('transform', "translate("+tags.x+","+tags.y+")")
                        .attr('data-original-title',"Name:"+tags.assignedTo + " <br/> Last Seen:" + date + "<br/> Location :" + tags.reciveralias);
               			this.svg.selectAll('#'+mcId).attr('data-x',tags.x);
   	    				this.svg.selectAll('#'+mcId).attr('data-y',tags.y);
   	    			}
   	    			 
  		        	
       	    	}
   	    		
  	   		}
  		
        }		
        

		var uniqueArray = function(arrArg) {
		  return arrArg.filter(function(elem, pos,arr) {
		    return arr.indexOf(elem) == pos;
		  });
		};

		var uniqEs6 = (arrArg) => {
		  return arrArg.filter((elem, pos, arr) => {
		    return arr.indexOf(elem) == pos;
		  });
		} 
	    	/* $.each(category, function(i, el){
	    	    if($.inArray(el, uniqueCat) === -1) uniqueCat.push(el);
	    	});  */
		var uniqueCat = [];
		function containsAny(source,target)
		{
		    var result = source.filter(function(item){ return target.indexOf(item) > -1});   
		    return (result.length > 0);  
		}  
		
		$('.catFilter .multiselect-ui option').each(function(){  
			uniqueCat.push(this.value)
		}); 
		//console.log();
	    	$.each(uniqueArray(category), function(index, optionValue) {  
	    		//$('.catFilter .multiselect-ui').append( $('<option selected></option>').val(optionValue).html(optionValue) );
	    		if(containsAny(category, uniqueCat) === false){
	    			$('.catFilter .multiselect-ui').append( $('<option selected></option>').val(optionValue).html(optionValue) );
	    		}
	    		// $('.catFilter .multiselect-ui').append( $('<option></option>').val(val).html(val) );

	    	});
	    	$('.catFilter .multiselect-ui').multiselect('rebuild');
	    	
	    	d3.selectAll('.person').classed('tagdisable', true);
	    	$.each($(".catFilter .multiselect-ui option:selected"), function(){            
	    	filterCategories.push($(this).val());  
	    	
	    });
	    	
	    	if(tagsONOFF == 1){ 
	    		$.each(filterCategories, function(index, val) { 
	    			if(inactiveONOFF == 1){
	    				d3.selectAll('.person.'+val).classed('tagdisable', false);
	    			}
	    			else{
	    				d3.selectAll('.person.inactive').classed('tagdisable', true);
	    				d3.selectAll('.person.active.'+val).classed('tagdisable', false);
	    				d3.selectAll('.person.idle.'+val).classed('tagdisable', false);
	    			}
	    	});
		}
		else{
			d3.selectAll('.person').classed('tagdisable', true);
		}
	    	
        if (bDemofound == true) {
        	bDemofound = false;
        	changetag = 10;
        }
        
  
	 },
	 
    'plantDevices':function(image,type,x,y,status,uid,cnt,tag, p1, p2, p3,bleType,alias){
    
    	var obj;
    	var state = "active";
        var urlMap={
            "server":'dashboard',
            'switch':'swiboard',
            'ap':'devboard',
            'sensor':'devboard'
        }
        if (type == "server") {
            var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&cid="+this.urlObj.cid+"&type="+"server" 
        } else if (type == "sensor") {
            var url="/facesix/web/finder/device/"+urlMap[type]+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&uid="+uid+"&cid="+this.urlObj.cid+"&type="+"sensor" 
        } else if (p1 == true){
            var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+this.urlObj.sid+"&uid="+uid+"&cid="+this.urlObj.cid+"&type="+(type=="switch" || type=="server"?type:"device")+"&spid="+this.urlObj.spid 
        }   
        
        var anchor=this.svg.append("a").attr("xlink:href",url);
        var newImage=anchor.append("image")
        .attr({
            'x':x,
            'y':y,
            'xlink:href':image,
            'status':status,
            'height':imageH,
            'width':imageW,
            'class':'animatedImage',
            'data-uid':uid,
            'type':type
        });

        var ble = bleType.toUpperCase();
         $(anchor[0]).appendTo('.uiFloorGroupOuter')
            .attr('title','Device Type:' +  ble  + ' UID:'+ uid   + ' Location:'+ alias); 
		     $(anchor).tooltip({container:'body'});
       
     },
     showPopupMenu:function(evt){
            evt.preventDefault();
            var offsets=$(this).offset();
            var status=$(evt.target).attr("status");
            var uid=$(evt.target).attr("data-uid");
            $(".viewActivity").attr("href",$(this).attr("href"))
            $(".powerBtn").attr("uid",uid);
            floornetworkConfig.moveElementandShow(offsets,uid,status)
     },
     moveElementandShow:function(offsets,uid,status){
            $("#deviceHeading").text(uid);
            $("#status").text(status);
            $(".networkconfig").show().css({
                'position':'absolute',
                'left':offsets.left+(imageW/2),
                'top':offsets.top+(imageH/2)
            });
     },
     'fetchurlParams':function(search){
        var urlObj={}
        if(search)
          urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
        this.urlObj=urlObj;
        return urlObj; 
    },
    getDevices:function(param1, param2,param3){
    	var that=this;
        var urlObj=this.fetchurlParams(window.location.search.substr(1));
        var urlObjSpid = urlObj.spid;
        var urlLink ='/facesix/rest/site/portion/networkdevice/list?spid='+urlObjSpid;
    	var taginfo;
     	var person=0;

     	//console.log (" param1  " + param1 +" param2 "+ param2 +" param3 " +param3)
            
        $.ajax({
            url:urlLink,
            method:'get',
            success:function(response){
            	 
            	var devices=response;
            	 
            	var ii=0;
                for(ii=0;ii<devices.length;ii++)
                {
                	if(devices[ii].parent=="ble")
                		var type="sensor";
                	else
                		var type=devices[ii].typefs;
                		
                    var status  = devices[ii].status;
                    var image	= "/facesix/static/qubercomm/images/networkicons/"+type+"_"+status+".png";
                    var uid		= devices[ii].uid;
                    var bleType = devices[ii].bleType;
                    var alias = devices[ii].alias;
                    
                    if (param1 == "true") {
                        taginfo 	= devices[ii].tagstring;
                        person 		= devices[ii].activetag;
                    }
                    
                    that.plantDevices(image,type,devices[ii].xposition,devices[ii].yposition,status,uid,person, taginfo, param1, param2,param3,bleType,alias)
                }	
            },
            error:function(err){
                console.log(	err);    
            }
        });
        
     	 if (param1 != "true") {
         	that.plantDevicesTags(urlObjSpid,param1, param2,param3);
         }
     	 
     }
}
$("#closebutton").on('click',function(evt){
    evt.preventDefault();
    $(".networkconfig").hide();
})
$(document).on('click',function(evt){
    $(".networkconfig").hide();
})  


//fullscreen network map
    $('.enlarge').click(function(e){
        $('.floorCan').toggleClass('deviceexpand');
        $('.floorCan').toggleClass('pad0');
        $('.na-panel').toggleClass('height100');
    });
/*
$(".panzoom").panzoom({
	$zoomIn: $(".zoom-in"),
	$zoomOut: $(".zoom-out"),
	$zoomRange:$(".zoom-range"),
	$reset: $(".reset"),
	contain:'automatic',
	increment:1,
	minScale:1,
	maxScale:5
});
*/
$('#floorName').on('change', function() {
	
	var spid    =$('#floorName').val();
	var sid  	= location.search.split("&")[0].replace("?","").split("=")[1];
	var cid 	= location.search.split("&")[2].replace("?","").split("=")[1];
    
	var url  = "/facesix/web/site/portion/dashboard?sid="+sid+"&spid="+spid+"&cid="+cid;
	
	//console.log("url" + url);
	
	$.ajax({
  	   	  	url:url,
  	   	  	method:'GET',
  	   	  	data:{},
  	   	  	success:function(response,error){
  	   	  	location.replace(url);
  	   	  	},
  	   	  	error:function(error){
  	   	  		 console.log(error);
  	   	  	}
  	   	  });
	
});


$('#floorfresh').on('change',function(){
	 changetag=document.getElementById('floorfresh').value;
	 //console.log("floorfreshtag" + changetag);
});

$('#circle').on('change',function(){
	 circleval = document.getElementById('circle').value;
	 //console.log("circle" + circleval);
});
$('#inactive').on('change',function(){
	 inactval = document.getElementById('inactive').value;
	 //console.log("inactive" + inactval);
});

function updateTagType(tag_type) {
	
	
	var  code = "\uf007"; //default tag Type

	if (tag_type != null) {

		if (tag_type=="Doctor") {
			code = "\uf0f0";
		} else if (tag_type=="WheelChair") {
			code = "\uf193";
		} else if (tag_type=="Asset") {
			code = "\uf217";
		} else if (tag_type=="Bed") {
			code = "\uf236";
		} else if (tag_type=="Ambulance") {
			code = "\uf0f9";
		} else if (tag_type=="MedicalKit") {
			code = "\uf0fa";
		} else if (tag_type=="Heartbeat") {
			code = "\uf21e";
		} else if (tag_type=="Cycle") {
			code = "\uf206";
		} else if (tag_type=="Truck") {
			code = "\uf0d1";
		} else if (tag_type=="Bus") {
			code = "\uf207";
		} else if (tag_type=="Car") {
			code = "\uf1b9";
		} else if (tag_type=="Child") {
			code = "\uf1ae";
		} else if (tag_type=="Female") {
			code = "\uf182";
		} else if (tag_type=="Male") {
			code = "\uf183";
		} else if (tag_type=="Fax") {
			code = "\uf1ac";
		} else if (tag_type=="User") {
			code = "\uf007";
		} else if (tag_type=="Library") {
			code = "\uf02d";
		} else if (tag_type=="Hotel") {
			code = "\uf0f5";
		} else if (tag_type=="Fireextinguisher") {
			code = "\uf134";
		} else if (tag_type=="Print") {
			code = "\uf02f";
		} else if (tag_type=="Clock") {
			code = "\uf017";
		} else if (tag_type=="Film") {
			code = "\uf008";
		} else if (tag_type=="Music") {
			code = "\uf001";
		} else if (tag_type=="Levelup") {
			code = "\uf148";
		} else if (tag_type=="Leveldown") {
			code = "\uf149";
		} else if (tag_type=="Trash") {
			code = "\uf014";
		} else if (tag_type=="Home") {
			code = "\uf015";
		} else if (tag_type=="Videocamera") {
			code = "\uf03d";
		} else if (tag_type=="Circle") {
			code = "\uf05a";
		} else if (tag_type=="Gift") {
			code = "\uf06b";
		} else if (tag_type=="Exit") {
			code = "\uf08b";
		} else if (tag_type=="Key") {
			code = "\uf084";
		} else if (tag_type=="Camera") {
			code = "\uf083";
		} else if (tag_type=="Phone") {
			code = "\uf083";
		} else if (tag_type=="Creditcard") {
			code = "\uf09d";
		} else if (tag_type=="Speaker") {
			code = "\uf0a1"; 
		} else if (tag_type=="Powerroom") {
			code = "\uf1e6";
		} else if (tag_type=="Toolset") {
			code = "\uf0ad";
		} else if (tag_type=="Batteryroom") {
			code = "\uf241";
		} else if (tag_type=="Computerroom") {
			code = "\uf241";
		} else if (tag_type=="Kidsroom") {
			code = "\uf113";
		} else if (tag_type=="TVroom") {
			code = "\uf26c";
		} else {
			code = "\uf007";
		}
	}

return code;
}

