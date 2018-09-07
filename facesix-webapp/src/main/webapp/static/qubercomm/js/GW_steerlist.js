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
	var timeSeries = "";

	 steerBoard = {
        timeoutCount: 10000,
        tables: {
            url: {
                activeClientsTable: '/facesix/rest/site/portion/networkdevice/getpeers?sid='+urlObj.sid
            },
            setTable: {
                activeClientsTable: function () {
                    $.ajax({
                        url: steerBoard.tables.url.activeClientsTable,
                        method: "get",
                        success: function (result) {
                        	
                        	peerStats = result.devicesConnected;
                            var result=result.devicesConnected[0]
                            
                            //console.log (">>>>>>>>>>>>>>>>>>>table"+ JSON.stringify(result))
                            
							if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                steerBoard.activeClientsData = result;
                                if (result.length > 10) {
                                    var filteredData = result.slice(0, 10);
                                    show_next_button = true;
                                } else {
                                    var filteredData = result;
                                }

                                var source = $("#chartbox-acl-template").html();
                                var template = Handlebars.compile(source);
                                var rendered = template({
                                    "data": filteredData,
                                    "current_page": 1,
                                    "show_previous_button": show_previous_button,
                                    "show_next_button": show_next_button,
                                    "startIndex": 1
                                });
                                $('.acl-table-chart-box').html(rendered);
                                $('table .showPopup ').on("tap",rightMenu);                                
                                
                            }
                            
                            setTimeout(function () {
                              steerBoard.tables.setTable.activeClientsTable();
                           }, 10000);                            

                        },
                        error: function (data) {
                            setTimeout(function () {
                              steerBoard.tables.setTable.activeClientsTable();
                           }, 10000);                            
                        },
                        dataType: "json"

                    });
                }
            }
        }, 
        charts: {
            urls: {
                activeConnections:'/facesix/rest/site/portion/networkdevice/steerStatsDashboard?sid='+urlObj.sid+"&cid="+urlObj.cid, 
            },
            setChart: {
                activeConnections: function (initialData,params) {
                    $.ajax({
                        url:steerBoard.charts.urls.activeConnections,
                        success: function (result) {
                        	//console.log(" activeConnections  " + JSON.stringify(result));
                            steerBoard.charts.chartConfig.activeConnections.targetPos = targetPos = result;
                            steerBoard.charts.chartConfig.activeConnections.innerHTML ='<i class="fa fa-tags"></i></br>0';
                            	 
                                counter=0;
                                
                                    var deployedAp = result.deployedAp;
                                    $('#deployedAp').html(deployedAp);
                                    if(counterIncrement1 == 0){
                                     	 $('#deployedAp').each(function () {
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
                                    
                                    var peerCount = result.peerCount;
                                    $('#peerCount').html(peerCount);
                                    if(counterIncrement1 == 0){
                                     	 $('#peerCount').each(function () {
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
                                    
                                    var twoG = result.twoG;
                                    $('#twoG').html(twoG);
                                    if(counterIncrement1 == 0){
                                     	 $('#twoG').each(function () {
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
                                    var fiveG = result.fiveG;
                                    $('#fiveG').html(fiveG);
                                    if(counterIncrement1 == 0){
                                     	 $('#fiveG').each(function () {
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
                                    
                                 
                                    var result=result;
                                    //console.log(">>"+JSON.stringify(result));
                                    var LBF = result.totLBFailurePercent;
                                    var LBS = result.totLBSuccessPercent;
                                    var BBF = result.totBBFailurePercent;
                                    var BBS = result.totBBSuccessPercent;
                                    var LBC = result.LBClients;
                                    var BBC = result.BBClients;
                                    
                                   
                                    var finalBBS = BBS/100*BBC;
                                    var finalBBF = BBF/100*BBC;
                                    var finalLBS = LBS/100*LBC;
                                    var finalLBF = LBF/100*LBC;
                                    
                                   var columns;
                                    /**/                  	
                                    var chart = c3.generate({
                                    size: {
                                        height: 270,
                                    },
                                    bindto: '#twogvsfiveg',
                                    padding: {
                                    	top: 10,
                                        right: 15,
                                        bottom: 0,
                                        left: 40,
                                    },
                                    transition: {
                                	  duration: 500
                                	},
                                    data: {
                                   	 columns : [
                                            ['Success', finalBBS, finalLBS],
                                            ['Failure', finalBBF, finalLBF],
                                            ['Total', BBC, LBC],
                                          ],
                                         type: 'bar',
                                         types: {
                                             Total: 'u',
                                         },
                                         groups: [
                                             ['Success','Failure']
                                         ],
                                         axes: {
                                             Total: 'y'
                                         }
                                        
                                    },
                                		axis: {
                							x: {
                								show: true,
                								tick: {
                									format:  function (d) {
                										if(d == 0){
                											return  ['BAND BALANCE']
                										} else {
                											return  ['LOAD BALANCE']
                										}
                										
                									},  
                								}
                							},
                							y: {
                                         		min: 1,
                                         		tick: {
                                         			format: d3.format('d')
                                         		}
                                         	}
                						},
                                    tooltip: {
                                        format: {
                                        	title: function (v) {
                                        		if(v == "0"){
                                        			return "Band Balance";
                                        		} else {
                                        			return "Load Balance";
                                        		}
                                        		value (v);
                                    		},
                                    		value: function (i, j, k,v) {
                                    			//console.log("v = " + v)
                                    			if(k == "Total"){
                                    				if(v == 0){
                                    					i = BBC; 
                                    				} else {
                                    					i = LBC;
                                    				}
                                    				return i + ' Clients ';
                                    			} else if (k == "Success"){
                                    				if(v == 0){
                                    					i = BBS;
                                    				} else {
                                    					i = LBS;
                                    				}
                                    				return i + '%';
                                    			} else if (k == "Failure"){
                                    				if(v == 0){
                                    					i = BBF;
                                    				} else {
                                    					i = LBF;
                                    				}
                                    				return i + '%';
                                    			}
                                               	
                                            }
                                        }
                                    }, 
                                    grid: {
                						y: {
                							lines: [{value:0}]
                						}
                					},
                                    legend: {
                                        show: true
                                    }
                                    
                                    });                                  
                                    
                                    var resultDev=result;
                                    
                                    var badRssi = resultDev.badRSSI;
                                    var txFailure = resultDev.txFailure;
                                    var peerRetry = resultDev.peerRetry;
                                    var totBBFailurePercent = resultDev.totBBFailurePercent;
                                    var totLBFailurePercent = resultDev.totLBFailurePercent;
                                    
                                    var columns;
                                    
                                    var chart = c3.generate({

                                        size: {
                                            height: 270,
                                        },
                                        bindto: '#gb-chart',
                                        padding: {
                                        	top: 10,
                                            right: 15,
                                            bottom: 0,
                                            left: 40,
                                        },
                                        transition: {
                                    	  duration: 500
                                    	},
                                        data: {
                                       	 columns: [
                                       		 ['RSSI',badRssi],
                                             ['TX',txFailure],
                                             ['Peer Retry',peerRetry],
                                             ['Band Balance',totBBFailurePercent],
                                             ['Load Balance',totLBFailurePercent],
                                             ],
                                             type: 'bar',
                                        },
                                        bar: {
                                            width: {
                                                ratio: 0.5 
                                            }
                                        },
                                    	axis: {
                                    		x: {
               								show: true,
               								tick: {
               									format:  function (d) {
               										if(d == 0){
               											return  ['']
               										}
               									},  
               								}
               							},
                    							y: {
                    								show: true,
                    								min: 1,
                                            		tick: {
                                            			format: d3.format('d')
                                            		}
                                             	}    
                    						},
                    						
                                        tooltip: {
                                            format: {
                                           	 title: function (v) {
                                       			return "Failure Reasons";
                                           	 },
                                        		value: function (i, j, k) {
                                        			//console.log(k)
                                        			if(k == "Total"){
                                        				return i + ' Clients ';
                                        			} else {
                                        				return i + '%';
                                        			}
                                                   	
                                                }
                                            }
                                        }, 
                                        legend: {
                                            show: true
                                        }                              	
                               
                                    });
                                    
                                    
                           setTimeout(function () {
                             steerBoard.charts.setChart.activeConnections();
                           }, steerBoard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                           setTimeout(function () {
                             steerBoard.charts.setChart.activeConnections();
                           }, steerBoard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
             
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
            }
        },
        init: function (params) {
            var c3ChartList = ['activeConnections'];
            var that = this;
            
            var tableList   = ['activeClientsTable']
           
            $.each(tableList, function (key, val) {
                that.tables.setTable[val]();
            });   
            
            $.each(c3ChartList, function (key, val) {
                that.charts.setChart[val](true,params?params:"");
            });
          //  this.systemAlerts();
        },
    }
})();
currentDashboard=steerBoard;


var GatewayFinder   = false;
var Gateway 		= false;

function solutionInfo(s1,s2,s3,s4,s5){
	Gateway 		= s1;
	GatewayFinder   = s4;
	finder 			= s5;
}

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

var loading = 0 ;

var zoomLoaded = 0;

$(document).ready(function(){
  
	var row_limit = 10;
	   
	$('body').on('change', ".tablelength", function (e) { 
	    	
	    	row_limit = $(this).val();
	    	var target = $(this).attr('data-target');
	    	$(target).attr('data-row-limit', row_limit);
	    	$(target).attr('data-current-page', '1');
	    	 
	        var show_previous_button = true;
	        var show_next_button = false;

	        var tableName = $(this).attr("data-target"); 
	        var $tableBlock = $(tableName); 
	        current_page = 1;
	        previous_page = 1
	        next_page = current_page + 1  
	        
	        if (previous_page == 1) {
	            show_previous_button = false;
	        }
	        if (steerBoard.activeClientsData.length > current_page * row_limit) {
	            show_next_button = true;
	        }
	        var filteredData = steerBoard.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
	        var source = $("#chartbox-acl-template").html();
	        var template = Handlebars.compile(source);
	        var rendered = template({
	            "data": filteredData,
	            "current_page": previous_page,
	            "show_previous_button": show_previous_button,
	            "show_next_button": show_next_button,
	            "startIndex": (previous_page * row_limit) - row_limit
	        });
	        $('.acl-table-chart-box').html(rendered); 
	        $('#tablelength').val(row_limit);
	        
	    }); 
	    
	$('body').on('click', ".acl-tablePreviousPage", function (e) {

	    var show_previous_button = true;
	    var show_next_button = true;

	    var tableName = $(this).closest('span').attr("data-table-name");
	    var $tableBlock = $('#' + tableName);
	    var current_page = $tableBlock.attr('data-current-page');
	    current_page = parseInt(current_page);
	    previous_page = current_page - 1 
	    
	    if (previous_page == 1) {
	        show_previous_button = false;
	    }
	    var filteredData = steerBoard.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
	    var source = $("#chartbox-acl-template").html();
	    var template = Handlebars.compile(source);
	    var rendered = template({
	        "data": filteredData,
	        "current_page": previous_page,
	        "show_previous_button": show_previous_button,
	        "show_next_button": show_next_button,
	        "startIndex": (previous_page * row_limit) - row_limit
	    });
	    $('.acl-table-chart-box').html(rendered); 
	    $('#tablelength').val(row_limit);
	});

	$('body').on('click', ".acl-tableNextPage", function (e) {

	    var show_previous_button = true;
	    var show_next_button = false;

	    var tableName = $(this).closest('span').attr("data-table-name");
	    var $tableBlock = $('#' + tableName);
	    var current_page = $tableBlock.attr('data-current-page');
	    current_page = parseInt(current_page);
	    next_page = current_page + 1 
	     
	    if (steerBoard.activeClientsData.length > next_page * row_limit) {
	        show_next_button = true;
	    }

	    var filteredData = steerBoard.activeClientsData.slice(row_limit * current_page, row_limit * next_page);
	    var source = $("#chartbox-acl-template").html();
	    var template = Handlebars.compile(source);
	    var rendered = template({
	        "data": filteredData,
	        "current_page": next_page,
	        "show_previous_button": show_previous_button,
	        "show_next_button": show_next_button,
	        "startIndex": row_limit * current_page
	    });
	    $('.acl-table-chart-box').html(rendered); 
	    $('#tablelength').val(row_limit);
	});

	$('body').on('click', '.acl-refreshTable', function () {
	    steerBoard.tables.setTable.activeClientsTable();
	});
	
	loadBand();
	
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

$('#circle').on('change',function(){
	 circleval = document.getElementById('circle').value;
	 //console.log("circle" + circleval);
});

$('#inactive').on('change',function(){
	 inactval = document.getElementById('inactive').value;
	//console.log("inactive" + inactval);
});

function updateTagType(tag_type) {}

$('.full-screen').click(function(){
	$('.fullActive').toggleClass('fullScreenfit'); 
	var $container = $('#sbtChart'),
	width = $container.width(),
	height = $container.height();
	radius = Math.min(width, height);
	var svg = d3.select("#sbtChart").selectAll("svg") 
		.attr("width", Math.min(width,height)+'px')
		.attr("height", Math.min(width,height) +'px');
  // $('body').toggleClass('overflow-hidden');   
   
});

function loadBand(){
	
	var url  = '/facesix/rest/site/portion/networkdevice/steerStatsDashboard?sid='+urlObj.sid+"&cid="+urlObj.cid;
	$.ajax({
		  	url:url,
		  	method:'GET',
		  	data:{},
		  	headers:{
		  		'content-type':'application/json'
		  	},
		  	success:function(response){  
		  		//var data;
		  	//console.log("response geData " + JSON.stringify(response));
		  		console.log("response getData ");
		  		data = response.stationDetails;
		  	
		  	plotFunc(data);
		  	setTimeout(function () {
		  		loadBand();
		      }, 10000);
		  	},
		  	error:function(error){
		  		console.log(error);
		  	}
	});
	
}


function plotFunc(val){
	
	  if(val != undefined){
		  $('#tooltip').remove();
		  $('#sbtChart').empty();
	  }
	 
	  var $container = $('#sbtChart'),
	  w = $container.width(),
	  h = $container.height(),
	  radius = Math.min(w, h) / 2;
    
    
    var formatNumber = d3.format(",d");

var percentBase = 100;

var x = d3.scale.linear()
  .range([0, 2 * Math.PI]);

var y = d3.scale.linear()
  .range([0, radius]);

var partition = d3.layout.partition()
  .value(function(d) {
    return d.size;
  });

    var hue = d3.scale.category10();
	
	var luminance = d3.scale.sqrt()
	    .domain([0, 1e6])
	    .clamp(true)
	    .range([50, 90]);
    
// responsive svg
var svg = d3.select("#sbtChart").append("svg")
  .attr("width", w)
  .attr("height", h)
  .append("g")
  .attr("transform", "translate(" + w / 2 + "," + h / 2 + ")");

var arc = d3.svg.arc()
  .startAngle(function(d) {
    return Math.max(0, Math.min(2 * Math.PI, x(d.x)));
  })
  .endAngle(function(d) {
    return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx)));
  })
  .innerRadius(function(d) {
    return Math.max(0, y(d.y));
  })
  .outerRadius(function(d) {
    return Math.max(0, y(d.y + d.dy));
  });


   var root = data;
 /*d3.json("flare-labeled.json", function(error, root) {
  if (error) return console.warn(error);*/
  // Compute the initial layout on the entire tree to sum sizes.
  // Also compute the full name and fill color for each node,
  // and stash the children so they can be restored as we descend.
  //http://bl.ocks.org/kaz-a/5c26993b5ee7096c8613e0a77bdd972b
	
  partition
      .value(function(d) { return d.size; })
      .nodes(root)
      .forEach(function(d) {
        d._children = d.children;
        d.sum = d.value;
        d.key = key(d);
        d.fill = fill(d);
      });

  // Now redefine the value function to use the previously-computed sum.
  partition
      .children(function(d, depth) { return depth < 2 ? d._children : null; })
      .value(function(d) { return d.sum; });

  
 /* center.append("title")
      .text("zoom out");*/
      
  var partitioned_data=partition.nodes(root).slice(1)
  
    
  var path = svg.selectAll("path")
      .data(partitioned_data)
      .enter().append("path")
      .attr("d", arc)
      .style("fill", function(d) {
    	  	 var loadS = d.load_sucess;
			 var loadF = d.load_failure;
	    	 var bandS = d.band_sucess;
	    	 var bandF = d.band_failure;
    	  
    	  if(d.name == "Load Balance"){
    		  //myPattern(d.fill,d.size/2,d.uid);
    		  if(loadS == "0" && loadF == "0"){
    			  d.fill = "#d3d3d3";
    		  } else {
    			  d.fill = "#8fbc8f";
    		  }
    		  
    		  //d.fill="/f13d"
    	  } else if(d.name == "Band Balance"){
    		  if(bandS == "0" && bandF == "0"){
    			  d.fill = "#d3d3d3";
    		  } else {
    			  d.fill = "#5f9ea0";
    		  }
    		  
    		  var a = d.fill;
    	  } else if(d.name !="Load Balance" && d.name !="Band Balance"){
    		  d.fill = "#E8EDD0";
    	  }
    	  return d.fill; }
      
      )
      .each(function(d) { this._current = updateArc(d)
      });
      /*.on("click", zoomIn)
		.on("mouseover", mouseOverArc)
        .on("mousemove", mouseMoveArc)
        .on("mouseout", mouseOutArc);*/
  
  var total = 0;
        
  var texts = svg.selectAll("text")
      	.data(partitioned_data)
      	.enter().append("text")
		//.filter(filter_min_arc_size_text) 
    	.attr("transform", function(d) {
      return "translate(" + arc.centroid(d) + ")rotate(" + computeTextRotation(d) + ")";
    })
    .attr("text-anchor", "middle")
    .attr("dx", "0") // margin
    .attr("dy", ".35em") // vertical-align
	.style("font-size", 11 +"px")
    	.style("cursor","pointer")	
		.text(function(d,i) {
			var name = d.name;
			 var loadS = d.load_sucess;
			 var loadF = d.load_failure;
	    	 var bandS = d.band_sucess;
	    	 var bandF = d.band_failure;
			if(name !="Load Balance" && name !="Band Balance"){
				if (d.alias.length > 14) {
					name = d.alias.substr(0, 14)
					name+="..";
				}
				
	    	  } else if(name =="Load Balance"){
	    		  if(loadS == "0" && loadF == "0"){
	    			  name = "0"+"%";
	    		  } else 
	    			  {
	    			  name = d.load_sucess +"%";
	    			  }
	    		  
	    	  } else if(name =="Band Balance"){
	    		  if(bandS == "0" && bandF == "0"){
	    			  name = "0"+"%";
	    		  } else {
	    			  name = d.band_sucess +"%";
	    		  }
	    		  
	    	  }
			 
			return name})
		//.on("click", zoomIn)
		.on("mouseover", mouseOverArc)
		.on("mousemove", mouseMoveArc)
		.on("mouseout",  mouseOutArc);

 var ttt = texts.append("tspan")
	//.filter(filter_min_arc_size_text) 
	.attr("transform", function(d) { return "rotate(" + computeTextRotation(d) + ")"; })    	
	//.style("fill","white")
	.style("fill", function(d) {
	  
		  d.fill = "black";
	 
	  return d.fill; })
	.style("font-size", 12 +"px")
	.style("cursor","pointer")
	.style("font-family","cursive")
	.attr("x", function(d) { return radius / 3 * d.depth; })	
	.attr("dx", "-125") // margin
	.attr("dy", "2.5em") // vertical-align	
	.attr("s","10px")
	.text(function(d,i) {
		 var name = d.name;
		 var loadS = d.load_sucess;
		 var loadF = d.load_failure;
   	     var bandS = d.band_sucess;
   	     var bandF = d.band_failure;
		if(name !="Load Balance" && name !="Band Balance"){
			 total+= d.stationCount;
			name = "Total Clients - "+ d.stationCount;
			if (name.length > 18) {
				name = name.substr(0, 18)
				name+="..";
			}
  	  } else if(name =="Load Balance"){
		  name = "";
		  
	  } else if(name =="Band Balance"){
		  name= "";
		  
	  }
		
		return name})
	//.on("click", zoomIn)
	.on("mouseover", mouseOverArc)
	.on("mousemove", mouseMoveArc)
	.on("mouseout",  mouseOutArc);
  
  
  
  var center = svg.append("text")
  .data(partitioned_data)
  .attr("dx", "-50")
  .style("font-size", 12 +"px")
  .style("cursor","pointer")
  .attr("font-family","FontAwesome")
  .text(function(d){
     var totalClients ="Total Clients - " + total;
      return totalClients;
	  
  });

d3.select(self.frameElement).style("height", h + "px");

// Interpolate the scales!
function arcTween(d) {
  var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
    yd = d3.interpolate(y.domain(), [d.y, 1]),
    yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
  return function(d, i) {
    return i ? function(t) {
      return arc(d);
    } : function(t) {
      x.domain(xd(t));
      y.domain(yd(t)).range(yr(t));
      return arc(d);
    };
  };
}

function computeTextRotation(d) {
  var ang = (x(d.x + d.dx / 2) - Math.PI / 2) / Math.PI * 180;
  return (ang > 90) ? 180 + ang : ang;
}
    
    
  function mouseOverArc(d) {
		console.log("mouse over")
		d3.select(this).attr("fill","black")
		tooltip.html(format_description(d));
	    return tooltip.transition()
	    	.duration(50)
	        .style("opacity", 0.9)
	        .style("top", (d3.event.pageY-10)+"px")
		    .style("left", (d3.event.pageX+10)+"px")
		    .style("background-color", "#1e1f1f")
		    .style("border-radius", 5 +"px")
		    .style("padding", 10 +"px")
		    .style("color", "cornsilk");
	    }
	
	function mouseOutArc(){
		d3.select(this).attr("stroke","")
		$('#tooltip').hide();
		return tooltip.style("opacity", 0);
	}
	
	function mouseMoveArc (d) {
		$('#tooltip').show();
		return tooltip
	    .style("top", (d3.event.pageY-10)+"px")
	    .style("left", (d3.event.pageX+10)+"px")
	    .style("background-color", "#1e1f1f")
	    .style("border-radius", 5 +"px")
	    .style("padding", 10 +"px")
	    .style("color", "cornsilk");
	}  
    
    
   //Tooltip description
	var tooltip = d3.select("body")
	    .append("div")
	    .attr("id", "tooltip")
	    .style("position", "absolute")
	    .style("z-index", "9999")
	    .style("opacity", 0);
	
	/*function format_number(x) {
	  return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	}*/
	
	function format_description(d) {
	  var description = d.description;
	  if(d.name == "Load Balance"){
		  return  '<b>UID - ' + d.uid + '</b></br><b> Alias - ' + d.alias + '</b></br><b> Load Balance Details</b><br> Success - &nbsp;'+ d.load_sucess +'%<br>';
	  } else if(d.name == "Band Balance"){
		  return  '<b>UID - ' + d.uid + '</b></br><b> Alias - ' + d.alias + '</b></br><b> Band Balance Details</b></br> Success - &nbsp;'+ d.band_sucess +'%<br>';
	  } else if(d.name !="Load Balance" && d.name !="Band Balance") {
		  return  '<b>UID - ' + d.uid + '</b></br><b> Alias - ' + d.alias + '</b></br><b> Load Balance Details</b><br> Success - &nbsp;'+ d.load_sucess +'%<br><b> Band Balance Details</b></br> Success - &nbsp;'+ d.band_sucess +'%<br>';
	  } 
	      
	}
	 

	function key(d) {
	  var k = [], p = d;
	  while (p.depth) k.push(p.name,p.uid,p.size), p = p.parent;
	  return k.reverse().join(".");
	}
	
	function fill(d) {
	  var p = d;
	  while (p.depth > 1) p = p.parent;
	  var c = d3.lab(hue(p.name,p.uid,p.size));
	  c.l = luminance(d.sum);
	  return c;
	}
	
	function arcTween(b) {
	  var i = d3.interpolate(this._current, b);
	  this._current = i(0);
	  return function(t) {
	    return arc(i(t));
	  };
	}
	
	function updateArc(d) {
	  return {depth: d.depth, x: d.x, dx: d.dx};
	}
	
	d3.select(self.frameElement).style("height", Math.min(w,h)+'px')
	   

}