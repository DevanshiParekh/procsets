var imageW	  = 40;
var imageH	  = 40;
var fzie = 30;
var txty = 9;
(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')

	var peerStats;
	var counterIncrement = 0;
	var counterIncrement1 = 0;
	var counterIncrement2 = 0;
	var counterIncrement3 = 0;
    VenueDashboard = {
        timeoutCount: 10000,
        charts: {
            urls: {
                activeConnections:'/facesix/rest/beacon/ble/networkdevice/venue/taginfo?sid='+urlObj.sid+"&cid="+urlObj.cid,
            },
            setChart: {
                activeConnections: function (initialData,params) {
                    $.ajax({
                        url:VenueDashboard.charts.urls.activeConnections,
                        success: function (result) {
                        	
                              counter=0;
                                console.log("counter >>>" + JSON.stringify(result));
                                
                                 var tagsTotal = result.totalCheckedoutTags;
                                 $('#tagsTotal').html(tagsTotal);
                                    if(counterIncrement == 0){
                                     $('#tagsTotal').each(function () {
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

                                  var tagsActive = result.activeTags;
                                    $('#tagsActive').html(tagsActive);
                                    if(counterIncrement1 == 0){
                                     	 $('#tagsActive').each(function () {
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
                                   
                                     var tagsIdle = result.idleTags;
                                    $('#tagsIdle').html(tagsIdle);
                                    if(counterIncrement2 == 0){
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
                                  counterIncrement2 = 1;
                                }

                                var tagsInactive = result.inactiveTags;
                                  $('#tagsInactive').html(tagsInactive);
                                  if(counterIncrement3 == 0){
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
                                  counterIncrement3 = 1;
                                 }

                            var resultFT=result.floorVsTraffic;
                            var timings = [];
                            var floor   = ["Status"];
                            var actag   = ["activeTags"];
                            var idtag   = ["idleTags"];
                            var intag   = ["inactTags"];
                            
                     
                            for (var i = 0; i < resultFT.length; i++){  
                              floor.push(resultFT[i].Status);
                              actag.push(resultFT[i].activeTags);
                              idtag.push(resultFT[i].idleTags);
                              intag.push(resultFT[i].inactTags);
                                        }
                                    
                            if (resultFT) {
                              var idleVal = resultFT[0].idleTags;
                              if(idleVal == undefined){
                                VenueDashboard.charts.chartConfig.netFlow.data.columns = [floor, actag,  intag];
                              } else {
                                VenueDashboard.charts.chartConfig.netFlow.data.columns = [floor, actag, idtag,  intag];
                              }
                                 VenueDashboard.charts.getChart.netFlow = c3.generate(VenueDashboard.charts.chartConfig.netFlow);
                               }


                            var result=result.connectedTagType;
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

                            VenueDashboard.charts.chartConfig.TagTypes.data.columns = columns;
                            VenueDashboard.charts.chartConfig.TagTypes.data.names   = names;
                            VenueDashboard.charts.chartConfig.TagTypes.data.colors  = colors;
                            VenueDashboard.charts.getChart.TagTypes = c3.generate(VenueDashboard.charts.chartConfig.TagTypes);

                           setTimeout(function () {
                             VenueDashboard.charts.setChart.activeConnections();
                           }, VenueDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                           setTimeout(function () {
                             VenueDashboard.charts.setChart.activeConnections();
                           }, VenueDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                }

            },

            getChart: {},
            chartConfig: {
                activeConnections: {
                    innerHTML: '',
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 3,
                    scale: 500,
                    rotateBy: 360 / 6,
                    speed: 900,
                    delayAnimation:false,
                    onFinishMoving: function (pos) {
                        //console.log('done ', pos);
                    }
                },
                idle: {
                    innerHTML: '',
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 3,
                    scale: 500,
                    rotateBy: 360 / 6,
                    speed: 900,
                    delayAnimation:false,
                    onFinishMoving: function (pos) {
                        //console.log('done ', pos);
                    }
                },
                netFlow: {
                    size: {
                        height: 300,
                    },
                    bindto: '#vdChart1',
                    padding: {
                        top: 10,
                        right: 25,
                        bottom: 0,
                        left: 50,
                    },

                    data: {
                    	x: 'Status',
                        columns: [
			                ['Status', 		400],
			                ['activeTags', 	300],
			                ['idleTags', 	200],
			                ['inactTags', 	100],                      
                        ],

                        type:'bar',
				        groups: [
				            ['activeTags', 'idleTags', 'inactTags']
				        ],
				  		selection: {
                    		enabled: true
                		},
                    },
                    tooltip: {
                        show: true
                    },
                    color: {
                		pattern: ['#6da900', '#f2700d', '#a90000']
            		},
                    axis: {
                    	//rotated: true,
			            x: {
			                type: 'category'
			            },			                              	
                    },
		            grid: {
		                y: {
		                  lines: [{value:0}]
		                }
		            }                    
                },
                
                TagTypes: {
                    size: {
                        height: 320,
                    },
                    bindto: '#dd-chart3',
                    padding: {
                    	 top: 0,
                         right: 15,
                         bottom: 0,
                         left: 15,
                    },
                    data: {
                        columns: [],
                        names: {},
                        colors: {},
                        type: 'donut',
                    },
                    donut: {
                    	 width:45,
                        label: {
                            threshold: 0.03,
                            format: function (value, ratio, id) {
                                0;
                            }                        	
                        	
                        },
                    },
                    tooltip: {
                        format: {
                            value: function (i, j, k) {
                               	return 'Tag='+i;
                            }
                        }
                    },
                    axis: {
                        x: {
                            show: false
                        }
                    },
                    legend: {
                        show: true
                    }
                },                
                
                 devicesConnected: {
                    innerHTML: '',
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 3,
                    scale: 500,
                    rotateBy: 360 / 6,
                    speed: 900,
                    delayAnimation:false,
                    onFinishMoving: function (pos) {
                        //console.log('done ', pos);
                    }
                },
                avgUplinkSpeed: {
                    innerHTML: '',
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 3,
                    scale: 500,
                    rotateBy: 360 / 6,
                    speed: 900,
                    delayAnimation:false,
                    onFinishMoving: function (pos) {
                       //console.log('done ', pos);
                    }

                },

            }
        },
        init: function (params) {
            var c3ChartList = ['activeConnections'];
            var that = this;
            $.each(c3ChartList, function (key, val) {
                that.charts.setChart[val](true,params?params:"");
            });
           // this.systemAlerts();
        },
        
     /*   systemAlerts:function(){
            $.ajax({
                url:'/facesix/rest/beacon/ble/networkdevice/venue/alerts?sid='+urlObj.sid+"&cid="+urlObj.cid,
                method:'GET',
                success:function(result){
                     var result=result.length;
                     if(result==0){
                        $(".alert-gif").removeClass("hide").attr('src','/facesix/static/qubercomm/images/venue/correct.gif');
                        $(".alertText").text("All Systems Healthy");
                     }
                     else{
                        $(".alert-gif").removeClass("hide").attr('src','/facesix/static/qubercomm/images/venue/alert.gif');
                        $(".alertText").text("Alerts");
                     }       
                },
                error:function(){

                },
                dataType:'json'
            })
        }*/

    }
})();
currentDashboard=VenueDashboard;


var GatewayFinder   = false;
var Gateway 		= false;

function solutionInfo(s1,s2,s3,s4,s5){
	Gateway 		= s1;
	GatewayFinder   = s4;
	finder 			= s5;
	//console.log("Gateway" +s1, "s2" +s2,"s3" +s3, " GatewayFinder " +s4);
}

var renderAlertsTemplate = function (data) {
    if (data && data.alerts) {
        var source = $("#alerts-template").html();
        var template = Handlebars.compile(source);
        var rendered = template(data);
        $('.summaryTable').html(rendered);
    }

}
var fetchTagStatus = function (templateObj,id,cid){
    	var tagStatusUrl ='/facesix/rest/beacon/ble/networkdevice/beacon/alerts?sid='+id+"&cid="+cid
    	$.ajax({
            	url: tagStatusUrl,
            	success: function(result) {
                    
                    for (var i = 0; i < result.length; i++){
			            	var obj = {};	
			            	obj.description = result[i];
			            	templateObj.data.push(obj);
			        } 
                	//console.log("tag templateObj.data " + templateObj.data);		
                	renderAlertsTemplate({alerts:templateObj.data});
            	},
            	error: function(data) {
                	//console.log(data);
            	},
            	dataType: "json"
        	})
 }
var fetchAlertsTemplateData = function (){
    $.ajax({
        url: '/facesix/rest/site/portion/networkdevice/alerts?sid='+urlObj.sid+"&cid="+urlObj.cid,
        success: function (result) {

            var templateObj={
                data:[]
			}

            for(var i=0;i<result.length;i++){
            	var obj={};	
            	obj.description=result[i];
            	templateObj.data.push(obj);
            }
            fetchTagStatus(templateObj,urlObj.sid,urlObj.cid)
            //console.log("device templateObj.data " + templateObj.data);
          //  renderAlertsTemplate({alerts:templateObj.data});

        },
        error: function (data) {
            //console.log(data);
        },
        dataType: "json"
    })
}

$('body').on('click', '.viewAlertsTable', function (evt) {
    evt.preventDefault();
    fetchAlertsTemplateData();
    window.fetchAlertsInterval = setInterval(function (){fetchAlertsTemplateData();}, VenueDashboard.timeoutCount);
});


fetchAlertsTemplateData();
window.fetchAlertsInterval  = setInterval(function (){fetchAlertsTemplateData();}, VenueDashboard.timeoutCount);


//Network config Replica

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

var isReady 	=  false;
var gway 		= "false";
var extryexit 	= "false";
var locatum 	= "true"; 
var getTimer;
var spid;

var list;
var tagtype 	= "\uf007";
var color 		= "#4337AE"//"#90EE90"
var counter 	= "0";
var tagcolor 	= "#FFA500";
var bDemofound  = false;
var tagsCounter = 0;
var tagStatus 	= 0;

var filterTagactive = 1;
var filterCategories = [];
var zoomEnabled = 0;
var tagsONOFF = 1;
var inactiveONOFF = 1;
var switchONOFF = 1;
var category = [];
var toggleDevice;
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
	    toggleDevice = switchONOFF;
		d3.selectAll('.animatedImage').classed('tagdisable', false);
		floornetworkConfig.getDevices(toggleDevice)
	}
	else{
		switchONOFF = 0;	
		toggleDevice = switchONOFF;
		d3.selectAll('.animatedImage').classed('tagdisable', true);
		floornetworkConfig.getDevices(toggleDevice)
		
	} 	
});

$('.catFilter .multiselect-ui').change(function(){
	filterCategories = []; 
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
});
var loading = 0 ;
var floornetworkConfig={
		    	
		'plantDevicesTags' :function(p1, p2, p3){
			
			if (p3 == "true") {
			   	//$('.person').remove();
				//$('.qrnd').remove(); 
				 var callTime = new Date();
				  console.log("plantDevice called at "+callTime.getHours()+":"+callTime.getMinutes()+":"+callTime.getSeconds()+"."+callTime.getMilliseconds());
			      var milli = callTime.getTime();
			    	
		    	$.ajax({
		         	url:'/facesix/rest/site/portion/networkdevice/personinfo?spid='+spid+"&time="+milli,
		             method:'get',
		             success:function(response){
		            	 var curTime = (new Date()).getTime();
		            	 var responseTime = curTime - milli;
		            	 console.log("response time in venuedashboard= "+responseTime);
		            	 list = response;
		            	 
		            	 getTimer = setTimeout(function() {
		             		floornetworkConfig.plantDevicesTags(p1, p2,p3); 
		         		}, 1000);
		             },
		             error:function(err){
		                 console.log(err);  
		             }
		        }); 
		    	$('.person').each(function(){
    				var dataid = $(this).attr('data-id'); 
    				if(dataid != spid){
    					$(this).remove();
    				}
    			});	 
		    	/*var finalObj = [];
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
	   	    	if (typeof list != "undefined" && list != "") {
	   	    			
	   	    		for (var i = 0; i < list.length; i++) {
	   	    			 
	   	    			tags = list[i]; 
	   	    			if(tags.spid == spid){
		   	    			category.push(tags.tagType); 
		   	    			var mcId = 'tags-'+tags.macaddr;
	   	    				mcId = mcId.replace(/:/g , "-");
		   	    			var tagsFound = document.getElementById(mcId); 
		   	    			 
		   	    			if($( "g[data-x='"+tags.x+"'][data-y='"+tags.y+"']" ).length != 0 ){  
		   	    				var newElement = $( "g[data-x='"+tags.x+"'][data-y='"+tags.y+"']" ).attr('id');
		   	    				if(newElement != mcId){
		   	    					tags.x = parseInt(tags.x) + getRandomFloat(-1,1);
			   	    				tags.y = parseInt(tags.y) + getRandomFloat(-1,1);
		   	    				} 
		   	    			}
		   	    			
		   	    				if (tags.state != undefined && tags.state != "") {        			
			              			state = tags.state       			
			              		}
		   	    				// console.log(tags);
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
		        					bgColor = "rgba(240, 114, 0, 0.5)";
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
			   	    				.attr('id', mcId)
			   	    				.attr("fill", bgColor) 
			   	    				.attr("class","person animateZoom "+state+" "+tags.tag_type)
			   	    				.attr('data-id',spid) 
			   	    				.attr('data-x',tags.x) 
			   	    				.attr('data-y',tags.y) 
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
		   	    			
		   	    				//console.log("tags else x" + tags.x+ "tags y esle" + tags.y);
		   	    				this.svg.selectAll('#'+mcId).transition()
		   	    			    .duration(1000)
		   	    				.attr("fill", bgColor) 
		   	    				.attr('transform', "translate("+tags.x+","+tags.y+")")
                                .attr('data-original-title',"Name:"+tags.assignedTo + " <br/> Last Seen:" + date + "<br/> Location :" + tags.reciveralias);
		   	    				this.svg.selectAll('#'+mcId).attr('data-x',tags.x);
		   	    				this.svg.selectAll('#'+mcId).attr('data-y',tags.y);
		   	    				// console.log(tags.x);
		   	    			}
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
   	    	   	    	
  	    	 
   	    	d3.selectAll('.animatedImage').classed('tagdisable', true);  	    	
   	    	
   	    	 
   	    		if(switchONOFF == 1){   	    			 		
   	    			d3.selectAll('.animatedImage').classed('tagdisable', false);
   	    		}
   	    		else{   	    					
   	    			d3.selectAll('.animatedImage').classed('tagdisable', true);
   	    		} 	
   	    	
   	    	
			if (bDemofound) {
				bDemofound = false;
				
        			
			} else {
							
			}
			             	
		},
			 
	   'plantDevices':function(image,type,x,y,status,uid,cnt,tag,p1,p2,p3,toggleDevice,bleType,alias){
    	var obj;
    	var state = "active";
        var urlMap={
            "server":'dashboard',
            'switch':'swiboard',
            'ap':'devboard',
            'sensor':'devboard'
        }
        var urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
        if (type == "server") {
            var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+urlObj.sid+"&spid="+spid+"&cid="+urlObj.cid+"&uid="+uid+"&param=1" 
        } else if (type == "sensor") {
        	if (GatewayFinder=='true') {
                var url="/facesix/web/finder/device/"+urlMap[type]+"?sid="+urlObj.sid+"&spid="+spid+"&uid="+uid+"&cid="+urlObj.cid+"&dashview=2"
        	} else {
                var url="/facesix/web/finder/device/"+urlMap[type]+"?sid="+urlObj.sid+"&spid="+spid+"&uid="+uid+"&cid="+urlObj.cid
        	}
        } else if (type == "switch"){
        	if(GatewayFinder == "true"){
        		var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+urlObj.sid+"&spid="+spid+"&uid="+uid+"&cid="+urlObj.cid
        		
        	} else {
        		var url ="javascript:void(0)";
        	}
        	      	
        } else if (type == "ap"){
        	if(GatewayFinder == "true"){
        		var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+urlObj.sid+"&spid="+spid+"&uid="+uid+"&cid="+urlObj.cid
        		
        	} else {
        		var url ="javascript:void(0)";
        	}
        	
        } else {
            var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+urlObj.sid+"&spid="+spid+"&uid="+uid+"&cid="+urlObj.cid+"&type="+(type=="switch" || type=="ap"?type:"device") 
       }

        
        var mcId = 'devices-'+uid;
		mcId = mcId.replace(/:/g , "-");
		var deviceFound = document.getElementById(mcId);
		if(deviceFound == null){
			 var anchor=this.svg.append("a").attr("xlink:href",url);
			 if(switchONOFF == 1 || toggleDevice == 1){
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
			            'id': mcId,
			            'type':type
			        });

		    var ble = bleType.toUpperCase();
		    $(anchor[0]).appendTo('.uiFloorGroupOuter')
            .attr('title','Device Type:' +  ble  + ' UID:'+ uid  + ' Location:'+ alias); 
		     $(anchor).tooltip({container:'body'}); 
            }
			
		}
       
        
        //$(anchor[0]).bind('contextmenu',this.showPopupMenu)
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
    getDevices:function(p1,p2,p3){
        
    	var that	= this;
    	var urlLink ='/facesix/rest/site/portion/networkdevice/list?spid='+spid;
    	var person	= 0;
     	var taginfo;
     	
     	if (isReady  = false) {
     		isReady 	= true;
         	gway 		= p1;
         	extryexit 	= p2;
         	locatum 	= p3;
     	}
    	 
        //console.log("urlLink " +urlLink);
         
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

                        if (p3 == "false") {
                            taginfo 	= devices[ii].tagstring;
                            person 		= devices[ii].activetag;
                        }
                        
                        that.plantDevices(image,type,devices[ii].xposition,devices[ii].yposition,status,uid,person, taginfo,p1,p2,p3,toggleDevice,bleType,alias)
                   }
             },
             error:function(err){
                 console.log(err);    
             }
         });
         
         
     	 if (p3 == "true") {
         	that.plantDevicesTags(p1, p2, p3);
         }
     }
}
var zoomLoaded = 0;
$('#floorType').on('change', function() {  
	category = [];
	$('.catFilter .multiselect-ui').find('option').remove();
	$('.catFilter .multiselect-ui').multiselect({
		 includeSelectAllOption: true
	});
	$('.catFilter .multiselect-ui').multiselect('rebuild');
	$('.catFilter .multiselect-ui').multiselect('selectAll', true);
	
	list = ''; 
	clearTimeout(getTimer);
	$('svg.floorsvg').html('');
	var id =$('#floorType').val();
	var width=$(this).find(':selected').data('width');
	var height=$(this).find(':selected').data('height');	    
	
	var param1;
	var param2;
	spid = id;
	
	 
	var svgHolder = d3.select('svg.floorsvg');
	var svgNew = svgHolder.append('g')
		.attr('class','uiFloorGroupOuter');
	
	floornetworkConfig.svg=d3.select("svg.floorsvg").selectAll('.uiFloorGroupOuter');
	floornetworkConfig.getDevices(gway,extryexit,locatum);
	if (isReady == true) {
		clearTimeout(getTimer);
	} 
	
	
	var url = "url('/facesix/web/site/portion/planfile?spid=" + spid
	url 	 = url +   "') no-repeat"
	 
    // $('#flrmap').style = url;
    // $('#flrmap').css ('background', url); 
	
	 
 	var imagepath = '/facesix/web/site/portion/planfile?spid='+ spid; 
 	
    
    var newImage=svgNew.append("image")
    .attr({
        'x':0,
        'y':0,
        'xlink:href':imagepath, 
        'src':imagepath, 
        'height': height,
        'width':width,
        'id': 'bgimage',
        'alt': 'Venue Dashboard'
    }); 
    
    
    
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
    
    
    if (width != 0) {
    	$('#flrmap').css ('width', 	 width);
    }
	
	if (height != 0) {
		$('#flrmap').css ('height',  height);
	}
	
    $('#bgimage').load(function() {
		//console.log("Image Loading");
		 
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
    	 panZoom = svgPanZoom('#flrmap', {
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
    panZoom.destroy();
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
	
	function floorChange(e){
		var circleRadius = 10;
		var fSize = 10;
		fnsize = (10/e)+3; 
		circleRadius = (10/e)+3; 
		var newSize = e/3; 
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
		 
		fzie = fnsize;
		txty = cyposition;
		var circle = d3.select("svg.floorsvg").selectAll("circle");
		circle.attr('r', fnsize);
		var txt = d3.select("svg.floorsvg").selectAll("text");
		txt.attr('font-size', fnsize+'px');
		txt.attr('y', cyposition);
	}
	
		    
    //console.log ("URL" + url)
    	
})

$('#floorTypeZoom').on('change', function() {  
	category = [];
	$('.catFilter .multiselect-ui').find('option').remove();
	$('.catFilter .multiselect-ui').multiselect({
		 includeSelectAllOption: true
	});
	$('.catFilter .multiselect-ui').multiselect('rebuild');
	$('.catFilter .multiselect-ui').multiselect('selectAll', true);
	
	list = ''; 
	clearTimeout(getTimer);
	$('svg.floorsvg').html('');
	var id =$('#floorTypeZoom').val();
	var width=$(this).find(':selected').data('width');
	var height=$(this).find(':selected').data('height');	    
	
	var param1;
	var param2;
	spid = id;
	
	 
	var svgHolder = d3.select('svg.floorsvg');
	var svgNew = svgHolder.append('g')
		.attr('class','uiFloorGroupOuter');
	
	floornetworkConfig.svg=d3.select("svg.floorsvg").selectAll('.uiFloorGroupOuter');
	floornetworkConfig.getDevices(gway,extryexit,locatum);
	if (isReady == true) {
		clearTimeout(getTimer);
	} 
	
	
	var url = "url('/facesix/web/site/portion/planfile?spid=" + spid
	url 	 = url +   "') no-repeat"
	 
    // $('#flrmap').style = url;
    // $('#flrmap').css ('background', url); 
	
	 
 	var imagepath = '/facesix/web/site/portion/planfile?spid='+ spid; 
 	
    
    var newImage=svgNew.append("image")
    .attr({
        'x':0,
        'y':0,
        'xlink:href':imagepath, 
        'src':imagepath, 
        'height': height,
        'width':width,
        'id': 'bgimage',
        'alt': 'Venue Dashboard'
    }); 
    
    
    
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
    
    
    if (width != 0) {
    	$('#flrmap').css ('width', 	 width);
    }
	
	if (height != 0) {
		$('#flrmap').css ('height',  height);
	}
	
    $('#bgimage').load(function() {
		//console.log("Image Loading");
		 
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
    	 panZoom = svgPanZoom('#flrmap', {
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
    panZoom.destroy();
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
	
	function floorChange(e){
		var circleRadius = 10;
		var fSize = 10;
		fnsize = (10/e)+3; 
		circleRadius = (10/e)+3; 
		var newSize = e/3; 
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
		 
		fzie = fnsize;
		txty = cyposition;
		var circle = d3.select("svg.floorsvg").selectAll("circle");
		circle.attr('r', fnsize);
		var txt = d3.select("svg.floorsvg").selectAll("text");
		txt.attr('font-size', fnsize+'px');
		txt.attr('y', cyposition);
	}
	
		    
    //console.log ("URL" + url)
    	
})

$(document).ready(function(){
    // floornetworkConfig.svg=d3.select("svg.floorsvg").selectAll('.uiFloorGroupOuter');
    //floornetworkConfig.path = floornetworkConfig.svg.selectAll("#style")
    // VenueDashboard.init();
    // $("select#floorType").change();
})
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
        $('.plan').toggleClass('dropdisable');
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
/* 
section = $('.panzoom'),
panzoom = section.panzoom({
    $zoomIn: $("#zoomin-ctl"),
    $zoomOut: $("#zoomout-ctl"),
    $reset: $("#reset"),
    contain: "invert", //"invert",
    minScale: 1,
    maxScale: 2
});
section.on('mousewheel.focal', function (e) {
    e.preventDefault();
    panzoom.panzoom('zoom', e.originalEvent.wheelDelta < 0, {
        increment: 0.1,
        focal: e
    });
});
*/


/*
var $panzoom =$('.panzoom').panzoom({});
$panzoom.parent().on('mousewheel.focal', function( e ) {
  e.preventDefault();
  var delta = e.delta || e.originalEvent.wheelDelta;
  var zoomOut = delta ? delta < 0 : e.originalEvent.deltaY > 0;
  $panzoom.panzoom('zoom', zoomOut, {
    increment: 1,
    animate: false,
    focal: e,  
    contain: "invert",
	minScale: 1,
	maxScale: 10,
	duration: 500,
  });
}); */


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