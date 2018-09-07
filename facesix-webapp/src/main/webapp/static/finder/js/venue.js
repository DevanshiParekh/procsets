
(function () {
   search = window.location.search.substr(1)
   urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
   var peerStats;
    VenueDashboard = {
        timeoutCount: 10000,
        charts: {
            urls: {
                txRx: '/facesix/rest/beacon/ble/networkdevice/rxtx?sid='+urlObj.sid,
                netFlow: '/facesix/rest/beacon/ble/networkdevice/venueagg?sid='+urlObj.sid+"&cid="+urlObj.cid,
                typeOfDevices: '/facesix/rest/beacon/ble/networkdevice/venue/connectedTagType?sid='+urlObj.sid+"&cid="+urlObj.cid,
                devicesConnected: '/facesix/rest/beacon/ble/networkdevice/gettags?sid='+urlObj.sid+"&cid="+urlObj.cid, //Todo Url has to be changed here
                
            },
            setChart: {
                txRx: function (initialData) {
                    $.ajax({
                        url: VenueDashboard.charts.urls.txRx,
                        success: function (result) {
                            if (result && result.length) {                         
                                var timings = [];
                                var txArr = ["Uplink"];
                                var rxArr = ["Downlink"];
                                for (var i = 1; i < result.length; i++){                                    						        	                                   
                                    
						        	if (result[i].Tx == undefined) {
						        		continue;
						        	}
						        	if (result[i].Rx == undefined) {
						        		continue;
						        	}
						        	if (result[i].time == undefined) {
						        		continue;
						        	}                                  
            						txArr.push(result[i].Tx);
            						rxArr.push(result[i].Rx);
            						txArr[i] = txArr[i]/100;
            						rxArr[i] = rxArr[i]/100;
            						var formatedTime = result[i].time;
                                    var date = new Date(formatedTime + 'UTC');

            						//var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
            						//c_formatedTime = new Date ();
                                    timings.push(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
                                }
                                var txrxVal = $( ".txrxswap option:selected" ).val();
                                if(txrxVal == "Venue Downlink/Uplink Speed"){
                                	VenueDashboard.charts.chartConfig.txRx.data.columns = [txArr, rxArr];
                                    VenueDashboard.charts.chartConfig.txRx.axis.x.categories = timings;
                                }
                                
                            }
                            if(txrxVal == "Venue Downlink/Uplink Speed"){
                                VenueDashboard.charts.getChart.txRx = c3.generate(VenueDashboard.charts.chartConfig.txRx);
                            }
                                setTimeout(function () {
                             		VenueDashboard.charts.setChart.txRx();
                            	}, VenueDashboard.timeoutCount);
                        },
                        error: function (data) {
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.txRx();
                            }, VenueDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                activeConnections: function (initialData) {
                    $.ajax({
                        url: VenueDashboard.charts.urls.activeConnections,
                        success: function (result) {
                            VenueDashboard.charts.chartConfig.activeConnections.targetPos = (result.activeDevice / result.totalDevice) * 100;
                            VenueDashboard.charts.chartConfig.activeConnections.innerHTML = ((result.activeDevice / result.totalDevice) * 100).toString();
                            if (initialData) {
                                $('#demo-pie-2').circles(VenueDashboard.charts.chartConfig.activeConnections);
                            } else {
                                var pieChart = $('#demo-pie-2').data('circles');
                                pieChart.moveProgress(VenueDashboard.charts.chartConfig.activeConnections.targetPos);
                            }
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.activeConnections();
                            }, VenueDashboard.timeoutCount);
                        },
                        error: function (data) {
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.activeConnections();
                            }, VenueDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                netFlow: function (initialData) {
                    $.ajax({
                        url: VenueDashboard.charts.urls.netFlow,
                        success: function (result) {
							var timings = [];
                            var floor 	= ["Status"];
                            var ulink 	= ["Tags"];
                            
                            console.log ("Netflow" +  result.length)

                            console.log("Netflow"+JSON.stringify(result));
                            
                            for (var i = 0; i < result.length; i++){  
                            	 console.log (result[i])
								floor.push(result[i].Status);
								
    							ulink.push(result[i].Tags);
      						           						
                            }                        
                        
                            if (result) {

                                VenueDashboard.charts.chartConfig.netFlow.data.columns = [floor, ulink ];
                                //VenueDashboard.charts.chartConfig.netFlow.data.x= timings;
                                VenueDashboard.charts.getChart.netFlow = c3.generate(VenueDashboard.charts.chartConfig.netFlow);
                            }
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.netFlow();
                            }, VenueDashboard.timeoutCount);
                        },
                        error: function (data) {
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.netFlow();
                            }, VenueDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                
                typeOfDevices: function (initialData) {
                	 $.ajax({
                         url: VenueDashboard.charts.urls.typeOfDevices,
                         success: function (result) {
                             var columns=[];
                             var names={};
                             var colors={},colorMap={
                                 'enabled':"#6baa01",
                                 'disabled':'#cccccc'
                             }
                             
                             for(var i = 0; i< result.length; i++){
                                    columns.push([result[i].tagType,result[i].tagCount]);
                                    names["data"+(i+1)]=result[i].tagType;
                             }                            	

                             VenueDashboard.charts.chartConfig.typeOfDevices.data.columns = columns;
                             VenueDashboard.charts.chartConfig.typeOfDevices.data.names   = names;
                             VenueDashboard.charts.chartConfig.typeOfDevices.data.colors  = colors;
                         	 VenueDashboard.charts.getChart.typeOfDevices = c3.generate(VenueDashboard.charts.chartConfig.typeOfDevices);
                             if (initialData) {
                             	VenueDashboard.charts.getChart.typeOfDevices = c3.generate(VenueDashboard.charts.chartConfig.typeOfDevices);
                             } else {
                             	VenueDashboard.charts.getChart.typeOfDevices.load({ "columns": VenueDashboard.charts.chartConfig.typeOfDevices.data.columns,'colors': VenueDashboard.charts.chartConfig.typeOfDevices.data.colors});
                             }                        	
                         	

                             setTimeout(function () {
                             	VenueDashboard.charts.setChart.typeOfDevices();
                             }, VenueDashboard.timeoutCount);
                             
                         },
                         error: function (data) {
                            //console.log(data);
                            setTimeout(function () {
                         	   VenueDashboard.charts.setChart.typeOfDevices();
                            }, VenueDashboard.timeoutCount);
                            
                         },
                         dataType: "json"
                     });
                },
                
                devicesConnected: function (initialData) {
                    $.ajax({
                        url: VenueDashboard.charts.urls.devicesConnected,
                        success: function (result) {
                        	
                        	//console.log ("Device Conected")
                        	
                        	peerStats = result.devicesConnected;
                        	if (peerStats.length == 4) {
                        		VenueDashboard.charts.chartConfig.devicesConnected.data.columns = [result.devicesConnected[1],
																                        		   result.devicesConnected[2],
																                        		   result.devicesConnected[3]];
                        	} else {
                        		VenueDashboard.charts.chartConfig.devicesConnected.data.columns = [result.devicesConnected[1], 
									   result.devicesConnected[2], 
									   result.devicesConnected[3], 
									   result.devicesConnected[4],
									   result.devicesConnected[5]]                        		
                        	}
                        	
                        	
                            //VenueDashboard.charts.chartConfig.devicesConnected.data.columns = [result.devicesConnected[1], 
                            //																   result.devicesConnected[2], 
                            //																   result.devicesConnected[3], 
                            //																   result.devicesConnected[4],
                            //																   result.devicesConnected[5]]
                        	
                        	//console.log (VenueDashboard.charts.chartConfig.devicesConnected.data.columns);
                            VenueDashboard.charts.getChart.devicesConnected = c3.generate(VenueDashboard.charts.chartConfig.devicesConnected);
                            //VenueDashboard.charts.setChart.typeOfDevices(true);
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.devicesConnected();
                            }, VenueDashboard.timeoutCount);
                        },
                        error: function (data) {
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.devicesConnected();
                            }, VenueDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                avgUplinkSpeed: function (initialData) {
                    $.ajax({
                        url: VenueDashboard.charts.urls.avgUplinkSpeed,
                        success: function (result) {
                            VenueDashboard.charts.chartConfig.avgUplinkSpeed.data.columns = result;
                            if (initialData) {
                                VenueDashboard.charts.getChart.avgUplinkSpeed = c3.generate(VenueDashboard.charts.chartConfig.avgUplinkSpeed);
                            } else {
                                VenueDashboard.charts.getChart.avgUplinkSpeed.load({ "columns": VenueDashboard.charts.chartConfig.avgUplinkSpeed.data.columns });
                            }
                            setTimeout(function () {
                             VenueDashboard.charts.setChart.avgUplinkSpeed();
                            }, VenueDashboard.timeoutCount);
                        },
                        error: function (data) {
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.avgUplinkSpeed();
                            }, VenueDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                avgDownlinkSpeed: function (initialData) {
                    $.ajax({
                        url: VenueDashboard.charts.urls.avgDownlinkSpeed,
                        success: function (result) {
                            VenueDashboard.charts.chartConfig.avgDownlinkSpeed.data.columns = result;
                            if (initialData) {
                                VenueDashboard.charts.getChart.avgDownlinkSpeed = c3.generate(VenueDashboard.charts.chartConfig.avgDownlinkSpeed);
                            } else {
                                VenueDashboard.charts.getChart.avgDownlinkSpeed.load({ "columns": VenueDashboard.charts.chartConfig.avgDownlinkSpeed.data.columns });
                            }
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.avgDownlinkSpeed();
                            }, VenueDashboard.timeoutCount);
                        },
                        error: function (data) {
                            setTimeout(function () {
                              VenueDashboard.charts.setChart.avgDownlinkSpeed();
                            }, VenueDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                }
            },
            getChart: {},
            chartConfig: {
                txRx: {
                    size: {
                        height: 220,
                    },
                    bindto: '#fd_chart2',

                    padding: {
                        top: 10,
                        right: 25,
                        bottom: 0,
                        left: 40,
                    },
                    data: {
                        transition: {
                            duration: 20000
                        },
                        columns: [],
                        types: {
                            Uplink: 'area-spline',
                            Downlink: 'area-spline',


                        },
                        colors: {
                            Uplink: '#5cd293',
                            Downlink: '#1a78dd',

                        },

                    },
                     legend:{
                        item:{
                            "onclick":function(id){
                               VenueDashboard.charts.getChart.txRx.focus(id);  
                            }
                        }
                     },
                    tooltip: {
                        show: false
                    },
                    point: {
                        show: false
                    },
                    axis: {
                        x: {
                            type: 'category',
                            padding: {
                                left: -0.5,
                                right: -0.5,
                            },
                        },
                        y: {
                            padding: { bottom: 0 },
                            min: 0,
                            tick: {
                                format: d3.format("s")
                            }
                        },
                    }
                    
                                       
                },
                netFlow: {
                    size: {
                        height: 220,
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
			                ['Status'],
			                ['Tag', 300],                      
                        ],

                        type:'bar',
                       
                        colors: {
                            Tag: '#1a78d0',
                        },
                    },
                    tooltip: {
                        show: true
                    },
                    point: {
                        show: false
                    },
                    axis: {
                    	rotated: true,
			            x: {
			                type: 'category'
			            },
                        y: {
                            padding: { bottom: 0 },
                            min: 0,
                            tick: {
                                format: d3.format("d")
                            }
                        }, 			                              	
                    }
                },
                devicesConnected: {
                    size: {
                        height: 270,
                    },
                    bindto: '#fd_chart4',
                    padding: {
                        top: 5,
                        right: 15,
                        bottom: 0,
                        left: 15,
                    },
                    data: {
                        columns: [
                            ['IOS', 7],
                            ['Mac', 12],
                            ['Win', 83],
                            ['Android', 83],
                            ['Others', 83],
                            ['BLE', 12],
                        ],
                        colors: {
                            IOS: '#f14e5a',
                            Mac: '#f1f494',
                            Win: '#79d58a',
                            Android: '#85d1fb',
                            Others: '#c278ed',
                            BLE:'#79d58a',	
                        },
                        type: 'donut'
                    },
                    donut: {
                        title: "",
                        label: {
                            threshold: 0.03,
                            format: function (value, ratio, id) {
                                0;
                            }
                        },
                        width: 40
                    },
                    tooltip: {
                        format: {
                            value: function (value, ratio, id) {
                                return value + ' (' + d3.format('%')(ratio) + ')';
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
                
                typeOfDevices: {
                    size: {
                        height: 270,
                    },
                    bindto: '#dd-chart5',
                    padding: {
                      	top: 5,
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
                        width: 40,
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
                               	return 'Tags='+i;
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
                }
            }
        },
        init: function () {
            var c3ChartList = ['txRx', 'typeOfDevices', 'netFlow', 'devicesConnected'];
            var that = this;
            $.each(c3ChartList, function (key, val) {
                that.charts.setChart[val](true);
            });

        }

    }
})();
VenueDashboard.init();

$(window).resize(function() {
	$("#sbtChart svg text").css("font-size","12px")
});

if ($(window).width() > 1024) {
	$(".scrollbar-inner").niceScroll({
		cursorcolor:"#2496d8",
		cursoropacitymin: 0,
		cursoropacitymax: 1,
		cursorwidth: "4px",
		touchbehavior: true,
		cursorborder: "1px solid #2496d8",
		cursorborderradius: "0px",
		smoothscroll: true,
		preventmultitouchscrolling:false,
	});
}

$("#menu-toggle").on("click",function(){
	$(".scrollbar-inner").getNiceScroll().remove();
	setTimeout( function(){ 
		$(".scrollbar-inner").niceScroll({
			cursorcolor:"#2496d8",
			cursoropacitymin: 0,
			cursoropacitymax: 1,
			cursorwidth: "4px",
			cursorborder: "1px solid #2496d8",
			cursorborderradius: "0px",
		});
	}, 1000 );
});

var renderRecentActivitiesTemplate = function (data) {
    if (data && data.recent_activities) {
        var source = $("#activities-template").html();
        var template = Handlebars.compile(source);
        var rendered = template(data);
        $('.activities-section').html(rendered);
    }

}
var fetchRecentActivitiesTemplateData = function (){
    $.ajax({
        url: '/facesix/rest/site/portion/networkdevice/alerts?cid='+urlObj.cid+'&sid='+urlObj.sid, //Todo-Url has to be changed here
        success: function (result) {
             var templateObj={
                        data:[]
                     }

                     for(var i=0;i<result.length;i++){
                        var obj={};
			            //var formatedTime = result[i].time;
			            //var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
			            //c_formatedTime = new Date (c_formatedTime);
			            //obj.count = c_formatedTime.getHours() + ":" + c_formatedTime.getMinutes()+ ":" + c_formatedTime.getSeconds();
                        obj.description=result[i];
                        if(obj.description!=null)
                         templateObj.data.push(obj);
                     }
                     var tagurl ='/facesix/rest/beacon/ble/networkdevice/beacon/alerts?sid='+urlObj.sid+"&cid="+urlObj.cid
                     fetchTagData(templateObj,tagurl)
           // renderRecentActivitiesTemplate({recent_activities:templateObj.data});
            setTimeout(function(){fetchRecentActivitiesTemplateData();}, VenueDashboard.timeoutCount);
        },
        error: function (data) {
            setTimeout(function(){fetchRecentActivitiesTemplateData();}, VenueDashboard.timeoutCount);
        },
        dataType: "json"
    })
}

var fetchTagData = function (templateObj,tagurl){
	console.log("tagurl is "+tagurl)
    $.ajax({
        url: tagurl, //Todo-Url has to be changed here
        success: function (result) {

                     for(var i=0;i<result.length;i++){
                        var obj={};
			            //var formatedTime = result[i].time;
			            //var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
			            //c_formatedTime = new Date (c_formatedTime);
			            //obj.count = c_formatedTime.getHours() + ":" + c_formatedTime.getMinutes()+ ":" + c_formatedTime.getSeconds();
                        obj.description=result[i];
                        if(obj.description!=null)
                         templateObj.data.push(obj);
                     }
            renderRecentActivitiesTemplate({recent_activities:templateObj.data});
            setTimeout(function(){fetchRecentActivitiesTemplateData();}, VenueDashboard.timeoutCount);
        },
        error: function (data) {
            setTimeout(function(){fetchRecentActivitiesTemplateData();}, VenueDashboard.timeoutCount);
        },
        dataType: "json"
    })
}

var renderSummaryListTemplate = function (data) {
    if (data && data.summary) {
        var source = $("#summary-template").html();
        var template = Handlebars.compile(source);
        var rendered = template(data);
        $('.summary-section').html(rendered);
    }

}
window.currentDashboard=VenueDashboard;
fetchRecentActivitiesTemplateData();
$('.full-screen').click(function(){
	$('.fullActive').toggleClass('fullScreenfit'); 
	var $container = $('#sbtChart'),
	width = $container.width(),
	height = $container.height(),
	radius = Math.min(width, height) / 2;
	var svg = d3.select("#sbtChart").selectAll("svg") 
		.attr("width", Math.min(width,height)+'px')
		.attr("height", Math.min(width,height)+'px');
   $('body').toggleClass('overflow-hidden');
   /*if($('.fullActive').hasClass('fullScreenfit')){
	   var height = $("#sbtChart").height();
	   $('#sbtChart').css('width', height);
   }else{
	   $('#sbtChart svg').css('width', height);
	   $('#sbtChart').css('height', height);
   } */
   /* $("#sbtChart").toggleClass('heightAuto');
   $(".sunburstBig").toggleClass("overflowAuto")
   $(".sunburstChartSection").toggleClass('noborder'); */
   
});

function myFunction(theForm) {
var searchText=$('#finduid')
//console.log ("searchText" + searchText.val());
}

$(".search-align").on("myFunction",function(evt){
		evt.preventDefault();
		//console.log ("searchText >>");
		
		var searchText=$(this).val();
		if(searchText && searchText.length){
			//console.log ("searchText" + searchText);
       }
});
