 var receiver_row_limit = 5;

(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
    //var timer = 500000;
	//var count = 1;
	var peerStats;
	    TagDashboard = {
	        timeoutCount: 0,
	        acltables: {

	            setTable: {
	            	
	                aclClientsTable: function () {
	                	var scantimer=$("#time").val();
	                	
	                	link = '/facesix/rest/beacon/ble/networkdevice/tagactivity?macaddr='+urlObj.macaddr+"&time="+scantimer
	                	//console.log(link);
	                    $.ajax({
	                    	 url: link,
	                         method: "get",
	                         success: function (result) {
	                         	//console.log (link);	
	                             var result=result.bottleneck
	                             if (result && result.length) {
	                                 var show_previous_button = false;
	                                 var show_next_button = false;
	                                 _.each(result, function (i, key) {
	                                     i.index = key + 1;
	                                 })
	                                TagDashboard.activeClientsData = result;
	                              
	                             /*if (result.length > 5) {
                                    var filteredData = result.slice(0, 5);
                                    show_next_button = true;
                                     } else {
                                    var filteredData = result;
                                 }*/

                                   var size = $('#tablelength').val();
                                   if(size == undefined){
                                   	var filteredData =result.slice(0,size); 
                                   } else {
                                   	var filteredData = TagDashboard.activeClientsData;
                                   }
                                  
	                                var source = $("#chartbox-template").html();
	                                var template = Handlebars.compile(source);
	                                var rendered = template({
	                                    "data": filteredData,
	                                    "current_page": 1,
	                                    "show_previous_button": show_previous_button,
	                                    "show_next_button": show_next_button,
	                                    "startIndex": 1
	                                });
	                                $('.table-chart-box').html(rendered);
	                                if(size == '5'){
	                                 $('html, tbody').css('height', '205px'); 
	                                } else if (size == '10'){
	                                	$('html, tbody').css('height', '405px');
	                                } else if (size == '25'){
	                                	$('html, tbody').css('height', '1042px');
	                                } else if (size == '50'){
	                                	$('html, tbody').css('height', '2088px');
	                                } else if (size == '100'){
	                                	$('html, tbody').css('height', '4193px');
	                                }


	                                if(size == undefined){
                                        size = 5;
                                         $('html, tbody').css('height', '205px'); 
	                                } 
	                                $('#tablelength').val(size);

	                                if(result.length == 0 || result.length == undefined){
	                                	$('.tableHide').hide();
	                                } else {
	                                	$('.tableHide').show();
	                                }
	                              setTimeout(function () {
	                              TagDashboard.acltables.setTable.aclClientsTable();
	                            }, 10000);
	                            }
	                        },
	                        error: function (data) {
	                            setTimeout(function () {
	                              TagDashboard.acltables.setTable.aclClientsTable();
	                            }, 10000);                            
	                        },
	                        dataType: "json"

	                    });
	                }
	            }
	        },   
        init: function (params) {
            var aclList     = ['aclClientsTable']
            var that        = this;
            
            $.each(aclList, function (key, val) {
                that.acltables.setTable[val]();
            });  
           
           
        },

    }
})();
currentDashboard=TagDashboard;

$(document).ready(function(){
	$('.tableHide').hide();
    
   
   
$('body').on('change', ".tablelength", function (e) { 
 
    $('html, tbody').css('height', '');
   
	receiver_row_limit = $(this).val();
	var target = $(this).attr('data-target');
	
	$(target).attr('data-row-limit', receiver_row_limit);
	$(target).attr('data-current-page', '1');
	
    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).attr("data-target"); 
    var $tableBlock = $(tableName); 
    current_page = 1;
    previous_page = 1
    next_page = current_page + 1   

    var filteredData = TagDashboard.activeClientsData;
    var source = $("#chartbox-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData
    });
    $('.table-chart-box').html(rendered);
     
    $('#tablelength').val(receiver_row_limit); 
   
   if(receiver_row_limit == "5" && filteredData.length >=5){
      $('html, tbody').css('height', '205px'); 
   } else if(receiver_row_limit == "10" && filteredData.length >=10) {
         $('html, tbody').css('height', '405px');
   } else if(receiver_row_limit == "25" && filteredData.length >=25){
         $('html, tbody').css('height', '1042px');
   } else if(receiver_row_limit == "50" && filteredData.length >=50) {
        $('html, tbody').css('height', '2088px');
   } else if(receiver_row_limit == "100" && filteredData.length >=100) {
         $('html, tbody').css('height', '4193px');
   } else {
       $('html, tbody').css('height', 'auto');
   }
          
}); 

$('body').on('click', ".tabPreviousPage", function (e) {

    var show_previous_button = true;
    var show_next_button = true;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    previous_page = current_page - 1
    //console.log("previous fisrt>>>>>>>>.." +  previous_page);

    var row_limit = $tableBlock.attr('data-row-limit');
    row_limit = parseInt(row_limit);

    if (previous_page == 1) {
        show_previous_button = false;
    }
    var filteredData = TagDashboard.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
    var source = $("#chartbox-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * row_limit) - row_limit
    });
    $('.table-chart-box').html(rendered);

});

$('body').on('click', ".tabNextPage", function (e) {
    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    next_page = current_page + 1

    var row_limit = $tableBlock.attr('data-row-limit');
    row_limit = parseInt(row_limit);
    
    if (TagDashboard.activeClientsData.length > next_page * row_limit) {
        show_next_button = true;
    }

    var filteredData = TagDashboard.activeClientsData.slice(row_limit * current_page, row_limit * next_page)
    var source = $("#chartbox-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": next_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": row_limit * current_page
    });
    $('.table-chart-box').html(rendered);

});


$('body').on('click', '.refreshTable', function () {
    TagDashboard.acltables.setTable.aclClientsTable();
});
})
//TagDashboard.init();
//fetchSummaryTemplateData();
//window.fetchSummaryInterval = setInterval(function (){fetchSummaryTemplateData();}, TagDashboard.timeoutCount);
// fetchSummaryTemplateData();
$(document).on("change",".changeTime",function(evt){
	TagDashboard.acltables.setTable.aclClientsTable();
})

$(document).on("change",".chTime",function(evt){
	TagDashboard.receivertables.setTable.receiverClientsTable();
})
//Network config Replica
var imageW		= 40;
var imageH		= 40;
var changetag 	= 1;
var bReady		= true;
var tagsCounter = 0;

var tagSPID;
function showTag(v){
	 $('.person').show();
}
var floornetworkConfig={
    'plantDevices':function(image,type,x,y,status,uid,cnt,tag,param1,param2,param3,bleType,alias){
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
        } 
        
        var DeviceId = uid;
        DeviceId = DeviceId.replace(/:/g , "-");
        var tagsFound = document.getElementById(DeviceId);
        if(tagsFound == null){
        	var anchor=this.svg.append("a")
	        .attr({
	            'xlink:href':url,
	            'id':DeviceId 
	        });
    
		    var newImage=anchor.append("image")
		    .attr({
		        'x':x,
		        'y':y,
		        'xlink:href':image,
		        'status':status,
		        'height':imageH,
		        'width':imageW,
		        'data-uid':uid,
		        'class': 'animatedImage',
		        'type':type
		    });

		    $(anchor[0]).appendTo('.uiFloorGroupOuter')
            .attr('title','Device Type:' +  bleType  + ' UID:'+ uid   + ' Location:'+ alias); 
		     $(anchor).tooltip({container:'body'});
        } 
        
        
        var counter  = "0";
        var tagtype  = "\uf007";
        var color    = "#4337AE"//"#90EE90"
        var tagcolor = "#FFA500";
        var fontsize = "50px";
         
        
        //console.log("param1" +param1 +"param2" +param2 +"param3 " +param3);
        
        if (param1 != "true") {
        	
        	if (typeof tag != "undefined" && tag !="") {
    			obj = tag;
    		}
        } else {
        	
        	if (typeof tag != "undefined" && tag !="") {
    			obj = JSON.parse(tag);
    		}
        }
 
        if (type == "sensor") { 
        	if (typeof obj != "undefined" && obj !="") {
	        	for(var i=0;i<cnt;i++) {
	        		
	        		if (obj != undefined) {
	        			counter = obj[i];
	        		}
	       			if (param1 != "true") {
	        			
	        			
		        		if (obj != undefined) {
		        			counter = obj[i];
		        		}
		        		
		        		myx = counter.x;
		        		myy = counter.y;
		        		maa = myx*1+20;
		        		mbb = myy*1-15;	        		
		        		console.log("x value >" + myx + "y value" + myy)
	        		} else {
	        			
	        			myx = Math.floor(Math.random() * 51) - 20
		        		myy = Math.floor(Math.random() * 51) - 20
		        		myx = x*1+5+myx;
		        		myy = y*1+13+myy;
		        		
		        		if (obj != undefined) {
		        			counter = obj.tag[i];
		        		}
		        		
	        		}
	       			
	       			var macaddr = counter.macaddr;
	       			
	       			if (macaddr=="3F:23:AC:22:FF:F3" || macaddr=="3F:23:AC:22:FF:F4") { // test
		        		myx = x*1+5+(Math.floor(Math.random() * 350));
		        		myy = y*1+13+(Math.floor(Math.random() * 200));
		        		if (myx >= 1376) {
        	        		myx = 500;
        	        	}
        	        	if (myy >= 768) {
        	        		myy = 250;
        	        	}
		        		
		        		maa = myx*1+20;
		        		mbb = myy*1-15;
		        		 
	       			}
	       			 
	       			if (counter.tagtype != undefined && counter.tagtype != "") {
	        			tagtype = counter.tagtype
	        		} 

	        		
	        		tags = obj[i];
	    			var mcId = 'tags-'+tags.macaddr;
					mcId = mcId.replace(/:/g , "-");
	    			var tagsFound = document.getElementById(mcId);
	    			
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
					
					var date = tags.lastReportingTime;
              		if(date == null || date == undefined){
              			date = "Not Seen";
              		}

	    			if(tagsFound == null){
	    				
	    				
						var mainGroup = this.svg.append('g')
		    				.style("cursor","pointer")
		    				.attr('id', mcId)
		    				.attr("fill", bgColor) 
		    				.attr("class","person animateZoom "+state)
		    				.attr("info","Name:"+tags.assignedto + " <br/> Last Seen:" + date + "<br/> Location :" + tags.location)
		    				.attr('transform', "translate("+myx+","+myy+")") 
		    				.attr('data-html', 'true')
		    				.attr('title',"Name:"+tags.assignedto + " <br/> Last Seen:" + date + "<br/> Location :" + tags.location); 
		    				$(mainGroup).tooltip({container:'body'});
		    			
		    			var subGroup = mainGroup.append('g')
		    				.attr('id', mcId+'-sub') 
		    				.attr('transform','translate(0,0)') 
		    				.attr("class","onlyscale"); 
		   	    			
		    			var circle = subGroup.append("circle")
		  					.attr("r", "30")
		  					.attr("y", "0").
		  					attr("class", "animateZoomCircle");
			        	var txt = subGroup.append("text") 
			        	  	.attr("alignment-baseline",'middle')  
			        	  	.attr("font-family","FontAwesome")
			        	  	.style("fill",'#fff')
			        	  	.style("cursor","pointer")  
			        	  	.attr("text-anchor", "middle") 
			        	  	.attr("y", "9") 
			        	  	.attr('font-size', function(d) { return '30px';} )
			        	  	.text(function(d) { return tagtype; });
			        	  
			        	tagsCounter = tagsCounter + 1;
		        	
	    			} else {
	    				this.svg.selectAll('#'+mcId).transition()
   	    			    .duration(1000)
   	    			    .attr("fill", bgColor)
   	    			    .attr('transform', "translate("+myx+","+myy+")")
                        .attr('data-original-title',"Name:"+tags.assignedto + " <br/> Last Seen:" + date + "<br/> Location :" + tags.location); 
	    			}
	        	}
        	}
        	         	
        }
		if ($('.selectag').val() == "1")
			$('.person').show();
		else
			$('.person').hide();
    	
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
    getDevices:function(spid,param1,param2,param3){
        var that=this;
        var urlObj=this.fetchurlParams(window.location.search.substr(1));
       
        var taginfo;
     	var person  = 0;
     	var bChangeFloor = false;
     	
     	if (bReady == true) {
     		tagSPID = urlObj.spid;
     		bReady  = false;
     	} else {
     		tagSPID = spid;
     	}
     	
     	var urlLink ='/facesix/rest/site/portion/networkdevice/list?spid='+tagSPID;  	
     
     	//console.log(" param1 " +param1 +" param2 "  +param2 +" param3 " +param3);
     	
        if (param1 != "true") {
         	urlLink = '/facesix/rest/site/portion/networkdevice/finder/list?spid='+tagSPID+"&macaddr="+urlObj.macaddr+"&param="+1;
        }
        
       
         $.ajax({
             url:urlLink,
             method:'get',
             success:function(response){
             	 bChangeFloor = false;
                 var devices=response;
                 
                // console.log("--devices --" +JSON.stringify(devices));
                 
                 if (param1 != "true") {
                	 
                	 //console.log("locatum solution"); 
                	 
                	 taginfo = response.taglist;
                     person = response.tagcount;
                     
                    // console.log("--taginfo --" +JSON.stringify(taginfo));
                     //console.log("--tagcount --" +JSON.stringify(person));
                     
                   //  d3.selectAll("svg > *").remove();
                     devices = devices.list;
                     if (typeof taginfo != "undefined" && taginfo !="") {
                    	 obj = taginfo;
	                     counter = obj[0];
	                     //console.log ("Counter  "  + counter)
	     	        	 //console.log ("New SPID::tagSPID  " + counter.spid + tagSPID)
	     	        	 
	     	        	 if (tagSPID != counter.spid) {
	     	        	 		        		 
		     	        	 /* var url = "url('/facesix/web/site/portion/planfile?spid=" + counter.spid
		     	        	 url 	= url +   "') no-repeat"
		    	    		 
		     	        	 d3.selectAll("svg > *").remove();    	    		
		     	        	 $('#tagplan').html("");
		     	        	 $('#tagplan').style = url;
		     	        	 $('#tagplan').css ('background', url);
		     	    		 $('#tagplan').css ('width', 	counter.width);
		     	    		 $('#tagplan').css ('height',   counter.height);
		     	    		 floornetworkConfig.svg = d3.select("svg.floorsvg").selectAll('.uiFloorGroupOuter');
		     	    		 
		     	    		 */
	     	        	 
	     	        		$('#tagplan').html('');
		     	       		var url = "url('/facesix/web/site/portion/planfile?spid=" + counter.spid
		     	       		url 	= url +   "') no-repeat"
		     	       		
		     	       		 
		     	       		// $('#tagplan').style = url;
		     	       		// $('#tagplan').css ('background', url);
		     	       		//console.log(counter.width);
		     	       		$('#tagplan').css ('width', counter.width);
		     	       		$('#tagplan').css ('height', counter.height);
		     	       		
		     	       		 
		     	       		var imagePath = "/facesix/web/site/portion/planfile?spid="+counter.spid;
		     	       		
			     	       	var svgHolder = d3.select('svg.floorsvg');
			     	       	var svgNew = svgHolder.append('g')
			     	   		.attr('class','uiFloorGroupOuter');
			     	   	 
		     	       		
		     	       		var svgMap = d3.select("svg.floorsvg").selectAll('.uiFloorGroupOuter');
		     	       		var imageFound = document.getElementById('bgImage');
		     	       		if(imageFound == null){
		     	       			var newImage=svgNew.insert("image")
		     	       		    .attr({
		     	       		        'x':0,
		     	       		        'y':0,
		     	       		        'xlink:href':imagePath, 
		     	       		        'height': counter.height,
		     	       		        'width': counter.width,
		     	       		        'id': 'bgimage'
		     	       		    }); 
		     	       		    
		     	       		}
		     	       		
		     	       		var width = counter.width;
		     	       		var uiFloorWrpWidth = $('.canvas-container').innerWidth();
		     	       	    var twidth = (uiFloorWrpWidth - width)/2;
		     	       	    if(twidth > 0){
		     	       	    	svgNew.attr('transform', 'translate(' +twidth+ ',10)'); 
		     	       	    } 
		     	       	    $('.enlarge').click(function(){
		     	       	    	
		     	       	    	var svgNew = d3.select('svg.floorsvg').selectAll('.uiFloorGroupOuter');
		     	       	        var uiFloorWrpWidth = $('.canvas-container').innerWidth();
		     	       	        var twidth = (uiFloorWrpWidth - width)/2;
		     	       	        if(twidth > 0){
		     	       	        	svgNew.attr('transform', 'translate(' +twidth+ ',10)'); 
		     	       	        }
		     	       	    });
		     	       	    $('#bgimage').load(function() {
		     	       			//console.log('Image Loading');
		     	       	    }); 
		     	       	    var beforePan
		     	       	    beforePan = function(oldPan, newPan){ 
		     	       	    	var	zoom = this.getSizes().realZoom;
		     	       	    	//console.log(zoom);
		     	       	    	zoom = zoom /5;
		     	       	    	gutterWidth = 100 * zoom;
		     	       	      var stopHorizontal = false
		     	       	       , sizes = this.getSizes()
		     	       	        , stopVertical = false 
		     	       	        , gutterHeight = 100
		     	       	          // Computed variables
		     	       	       
		     	       	        , leftLimit = -((sizes.viewBox.x + sizes.viewBox.width) * sizes.realZoom) + gutterWidth  
		     	       	        , rightLimit = sizes.width - gutterWidth - (sizes.viewBox.x * sizes.realZoom) 
		     	       	        , topLimit = -((sizes.viewBox.y + sizes.viewBox.height) * sizes.realZoom) + gutterHeight
		     	       	        , bottomLimit = sizes.height - gutterHeight - (sizes.viewBox.y * sizes.realZoom)
		     	       	      customPan = {}
		     	       	      customPan.x = Math.max(leftLimit, Math.min(rightLimit, newPan.x))
		     	       	      customPan.y = Math.max(topLimit, Math.min(bottomLimit, newPan.y))
		     	       	      return customPan
		     	       	    }
		     	       	    var panZoom;
		     	       	    window.initialize = function () {
		     	       	    	 panZoom = svgPanZoom('#tagplan', {
		     	       				zoomEnabled: true,
		     	       				zoomScaleSensitivity: .2,
		     	       				minZoom: 1,
		     	       				maxZoom: 100,
		     	       				panEnabled: true,
		     	       				contain: false,
		     	       				controlIconsEnabled: false,
		     	       			    fit: false,
		     	       			    center: false,  
		     	       			    beforePan: beforePan,
		     	       				onZoom: function(e){ 
		     	       					floorChange(e);
		     	       				}
		     	       			});
		     	       	    }
		     	       	    initialize();
		     	       		document.getElementById('zoom-in').addEventListener('click', function(ev){
		     	       		    ev.preventDefault()
		     	       		    panZoom.zoomIn()
		     	       		});
		     	       		document.getElementById('zoom-out').addEventListener('click', function(ev){
		     	       		    ev.preventDefault()
		     	       		    panZoom.zoomOut()
		     	       		   
		     	       		});
		     	       		document.getElementById('reset').addEventListener('click', function(ev){
		     	       		    ev.preventDefault()  
		     	       		    panZoom.destroy();
		     	       		    initialize();
		     	       		});
		     	       		
			     	       	panZoom.destroy();
	     	       		    initialize();
		     	       		
		     	       		function floorChange(e){
		     	       			var circleRadius = 10;
		     	       			var fSize = 10;
		     	       			fnsize = (10/e)+3; 
		     	       			circleRadius = (10/e)+3; 
		     	       			var newSize = e/3;
		     	       			//console.log(newSize);
		     	       			var cyposition = 9;
		     	       			var image = d3.select("svg.floorsvg").selectAll(".animatedImage");
		     	       			if(newSize >= 20){
		     	       				image.attr('width', '3px')
		     	       				 .attr('height', '3px'); 
		     	       			}  
		     	       			else if(newSize >= 15){
		     	       				image.attr('width', '6px')
		     	       				 .attr('height', '6px'); 
		     	       			}
		     	       			else if(newSize >= 10){
		     	       				image.attr('width', '8px')
		     	       				 .attr('height', '8px'); 
		     	       			}
		     	       			else if(newSize >= 5){
		     	       				image.attr('width', '10px')
		     	       				 .attr('height', '10px'); 
		     	       			}
		     	       			else if(newSize >= 3){
		     	       				image.attr('width', '15px')
		     	       				 .attr('height', '15px'); 
		     	       			}
		     	       			else if(newSize >= 1){
		     	       				image.attr('width', '20px')
		     	       				 .attr('height', '20px'); 
		     	       			}
		     	       			else if(newSize >= .5){
		     	       				image.attr('width', '20px')
		     	       				 .attr('height', '20px'); 
		     	       			}
		     	       			else if(newSize >= .3){
		     	       				image.attr('width', '40px')
		     	       				 .attr('height', '40px'); 
		     	       			}
		     	       			else{
		     	       				image.attr('width', '40px')
		     	       				 .attr('height', '40px'); 
		     	       			}	
		     	       			 
		     	       			if(newSize >= 20){
		     	       				fnsize=1;
		     	       				cyposition = .3;
		     	       			} 
		     	       			else if(newSize >= 10){
		     	       				fnsize=2;
		     	       				cyposition = .8;
		     	       			}
		     	       			else if(newSize >= 5){
		     	       				fnsize=3;
		     	       				cyposition = 1;
		     	       			}
		     	       			else if(newSize >= 2){
		     	       				fnsize=6;
		     	       				cyposition = 2;
		     	       			}
		     	       			else if(newSize >= 1){
		     	       				fnsize=8;
		     	       				cyposition = 3;
		     	       			}
		     	       			else if(newSize >= .5){
		     	       				fnsize = 15;
		     	       				cyposition = 5;
		     	       			} 
		     	       			else{
		     	       				fnsize = 30;
		     	       				cyposition = 9;
		     	       			} 
		     	       			
		     	       			var circle = d3.select("svg.floorsvg").selectAll("circle");
		     	       			circle.attr('r', fnsize);
		     	       			var txt = d3.select("svg.floorsvg").selectAll("text");
		     	       			txt.attr('font-size', fnsize+'px');
		     	       			txt.attr('y', cyposition);
		     	       		}
		     	       		
		     	       	    floornetworkConfig.svg = d3.select("svg.floorsvg").selectAll('.uiFloorGroupOuter');
		     	    		 
		     	    		 
		     	    		 tagSPID = counter.spid;
		     	    		 bChangeFloor = true;	 
		     	    		 
	     	        	 }

                     }
                                         
                 } else {
                	console.log("entry-exit solution"); 
                 }
                 
                 if (bChangeFloor == false) {
	                 var ii;
	                 for(ii=0;ii<devices.length;ii++)
	                 {
	                	 if(devices[ii].parent=="ble")
	                 		var type="sensor";
	                	 else
	                 		var type=devices[ii].typefs;
	                 		
	                     var status  = devices[ii].status;
	                     var image	 = "/facesix/static/qubercomm/images/networkicons/"+type+"_"+status+".png";
	                     var uid	 = devices[ii].uid;
                         var bleType = devices[ii].bleType;
                         var alias = devices[ii].alias;
	                     
	                     if (param1 == "true") {
	                         taginfo = devices[ii].tagstring;
	                         person = devices[ii].activetag;
	                     }
	     	        	 that.plantDevices (image,type,devices[ii].xposition,devices[ii].yposition,status,uid,person, taginfo,param1,param2,param3,bleType,alias)
	                 }
	                 
					 setTimeout(function() {
                     	floornetworkConfig.getDevices(tagSPID,param1,param2,param3);
                 	 }, changetag*1000);
                 } else {                
					 setTimeout(function() {
                     	floornetworkConfig.getDevices(tagSPID,param1,param2,param3);
                 	 }, changetag*1000);
                 }
             },
             error:function(err){
					 setTimeout(function() {
                     	floornetworkConfig.getDevices(tagSPID,param1,param2,param3);
                 	 }, changetag*1000);  
             }
         });
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

$('#tagrefresh').on('change',function(){
	changetag=document.getElementById('tagrefresh').value;
	//console.log("changetag" + changetag);
});

$('#tagreport').click(function(e) {
	
		time=document.getElementById('time').value;
		//console.log("time value is "+time +"cid is "+urlObj.cid +" macaddr is "+urlObj.macaddr);
	    reportlink = '/facesix/rest/site/portion/networkdevice/report?filtertype=tagBased&reporttype=tagBased&macaddr='+urlObj.macaddr+'&cid='+urlObj.cid+'&time='+time;
	    window.open(reportlink);
});