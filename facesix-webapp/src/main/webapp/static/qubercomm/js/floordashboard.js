(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
    //var timer = 500000;
	//var count = 1;
	var peerStats; 
	var counterIncrement = 0;
	var counterIncrement1 = 0;
	var counterIncrement2 = 0;
	var counterIncrement3 = 0;
    FloorDashboard = {
        timeoutCount: 10000,
        charts: {
            urls: {
                txRx: '/facesix/rest/site/portion/networkdevice/avg_tx_rx?spid='+urlObj.spid+"&cid="+urlObj.cid,
                activeConnections:'/facesix/rest/site/portion/networkdevice/deviceCounts?spid='+urlObj.spid,//Todo Url has to be changed here 
                netFlow: "",//Todo Url has to be changed here
                typeOfDevices: "",//Todo Url has to be changed here               
                activeClients: "",//Todo Url has to be changed here               
                devicesConnected: '/facesix/rest/site/portion/networkdevice/getpeers?spid='+urlObj.spid,//Todo Url has to be changed here                
                avgUplinkSpeed: 'avguplinkspeed',// URl is not required.Utilizing Tx/Rx Data
                avgDownlinkSpeed: 'avgdownlinkspeed' // URl is not required.Utilizing Tx/Rx Data
            },
            setChart: {
                txRx: function (initialData,params) {
                    
                	var duration = params;
                	var len		 = duration?duration.length:0;
                	var link 	 = FloorDashboard.charts.urls.txRx;
                	/*if (len != 0 && duration.localeCompare("time=5") != 0) {
						link = "/facesix/rest/site/portion/networkdevice/floor_avg_tx_rx?spid="+urlObj.spid+"&"+params;
                	} else {
                		link = FloorDashboard.charts.urls.txRx;
                		len = 0;
                	}  */              	
                	
                    $.ajax({
                        url: link,
                        success: function (result) {
                        	
                        	console.log("rxtx " +JSON.stringify(result));
                        	
                        	
                            if (result && result.length) {                                
                                var timings = [];
                                var txArr = ["Tx"];
                                var rxArr = ["Rx"];
                                for (var i = 0; i < result.length; i++){   
	  						        	
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
	            						txArr[i] = txArr[i];///100;
	            						rxArr[i] = rxArr[i];///100;
            						
            						var formatedTime = result[i].time;
            						timings.push(formatedTime);
            						//var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
            						//c_formatedTime = new Date (c_formatedTime);
                                   // timings.push(c_formatedTime.getHours() + ":" + c_formatedTime.getMinutes());
                                }
                                FloorDashboard.charts.chartConfig.txRx.data.columns = [txArr, rxArr];
                                FloorDashboard.charts.chartConfig.txRx.axis.x.categories = timings;
                            }
                            if (initialData) {
                                FloorDashboard.charts.getChart.txRx = c3.generate(FloorDashboard.charts.chartConfig.txRx);
                                //FloorDashboard.charts.setChart.avgDownlinkSpeed(true);
                               // FloorDashboard.charts.setChart.avgUplinkSpeed(true);
                            } else {
                               // FloorDashboard.charts.setChart.avgDownlinkSpeed();
                               // FloorDashboard.charts.setChart.avgUplinkSpeed();
                                FloorDashboard.charts.getChart.txRx.load({ "columns": FloorDashboard.charts.chartConfig.txRx.data.columns, "categories":  FloorDashboard.charts.chartConfig.txRx.axis.x.categories});
                            }
                            FloorDashboard.charts.getChart.txRx = c3.generate(FloorDashboard.charts.chartConfig.txRx);
                            setTimeout(function () {
                             FloorDashboard.charts.setChart.txRx();
                            }, FloorDashboard.timeoutCount);
                        },
                        error: function (data) { 
                            setTimeout(function () {
                              FloorDashboard.charts.setChart.txRx();
                            }, FloorDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                activeConnections: function (initialData,params) {
                    $.ajax({
                        url:FloorDashboard.charts.urls.activeConnections,
                        success: function (result) { 
                        	
                        	var data = result.devicesCounts;
                        	var result = data[3];
                        	
                            FloorDashboard.charts.chartConfig.activeConnections.targetPos = targetPos = result[1];
                            FloorDashboard.charts.chartConfig.activeConnections.innerHTML = '<i class="fa fa-wifi" style="color:green;"></i></br>0';
                           // if (initialData) {
                             //   $('#demo-pie-2').circles(FloorDashboard.charts.chartConfig.activeConnections);
                                counter=0;
                               // var timer=setInterval(function(){
                                 //   var pieChart=$("#demo-pie-2").data("circles");
                               
                                $('#demo-pie-2').html(result[1]);
                                if(counterIncrement == 0){
                                	 $('#demo-pie-2').each(function () {
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
                           setTimeout(function () {
                             FloorDashboard.charts.setChart.activeConnections();
                           }, FloorDashboard.timeoutCount);
                        },
                        error: function (data) { 
                           setTimeout(function () {
                             FloorDashboard.charts.setChart.activeConnections();
                           }, FloorDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                netFlow: function (initialData,params) {/*
                	var duration = params;
                	var len		 = duration?duration.length:0;
                	var link 	 = FloorDashboard.charts.urls.netFlow;
                	
                	if (len != 0 && duration.localeCompare("time=5") != 0) {
						link = "/facesix/rest/site/portion/networkdevice/flraggr?spid="+urlObj.spid+"&"+params;
					} else {
						link = "/facesix/rest/site/portion/networkdevice/flraggr?spid="+urlObj.spid+"&"+"time=120";
					}
                	
                    $.ajax({
                        url:link,
                        success: function (result) { 
							var timings = [];
                            var radio 	= ["radio"];
                            var ulink 	= ["Downlink"];
                            var dlink 	= ["Uplink"];
                            
                            
                            for (var i = 0; i < result.length; i++){
                            	
                                if (result[i].max_vap_rx_bytes < 0) { 
        							result[i].max_vap_rx_bytes = Math.abs(result[i].max_vap_rx_bytes);
        						}
        						
        						if (result[i].max_vap_tx_bytes < 0) { 
        							result[i].max_vap_tx_bytes = Math.abs(result[i].max_vap_tx_bytes);
        						} 
        						  
                            	radio.push(result[i].Radio);
    							ulink.push(Math.round(result[i].max_vap_rx_bytes/100));
    							dlink.push(Math.round(result[i].max_vap_tx_bytes/100));   						           						
                            }                         
                        
                            if (result) {

                                FloorDashboard.charts.chartConfig.netFlow.data.columns = [radio, ulink, dlink];
                                //FloorDashboard.charts.chartConfig.netFlow.axis.x.categories = timings;
                                FloorDashboard.charts.getChart.netFlow = c3.generate(FloorDashboard.charts.chartConfig.netFlow);
                            }
                            setTimeout(function () {
                              FloorDashboard.charts.setChart.netFlow();
                            }, FloorDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                            setTimeout(function () {
                              FloorDashboard.charts.setChart.netFlow();
                           }, FloorDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                */},
                typeOfDevices: function (initialData) {
                	
                    $.ajax({
                        url:FloorDashboard.charts.urls.devicesConnected,
                        success: function (result) { 
                        	 var str = 0;
                        	 if(result.devicesConnected[8][1] !== 'undefined'){
                        		 str = result.devicesConnected[8][1];
                        	 }
                                $('#piechart1').html(str);
                                if(counterIncrement1 == 0){
                                	 $('#piechart1').each(function () {
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
                           setTimeout(function () {
                             FloorDashboard.charts.setChart.typeOfDevices();
                           }, FloorDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                           setTimeout(function () {
                             FloorDashboard.charts.setChart.typeOfDevices();
                           }, FloorDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                activeClients: function (initialData) {
                	
                    $.ajax({
                        url:FloorDashboard.charts.urls.devicesConnected,
                        success: function (result) {
                        	
                        	 var str = 0;
                        	 if(result.devicesConnected[9][1] !== 'undefined'){
                        		 str = result.devicesConnected[9][1];
                        	 } 
                                // var c = peerStats; 
                                $('#piechart3').html(str); 
                                if(counterIncrement2 == 0){
                                	 $('#piechart3').each(function () {
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
                           setTimeout(function () {
                             FloorDashboard.charts.setChart.activeClients();
                           }, FloorDashboard.timeoutCount);
                           
                        },
                        error: function (data) {
                            //console.log(data);
                           setTimeout(function () {
                             FloorDashboard.charts.setChart.activeClients();
                           }, FloorDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                devicesConnected: function (initialData,params) {
                    $.ajax({
                        url:FloorDashboard.charts.urls.devicesConnected,
                        success: function (result) {
                        	peerStats = result.devicesConnected;
                        	$.each(result.devicesConnected, function (key, val) {
                        			
                        		result.devicesConnected[key][1] = result.devicesConnected[key][1]+.2;
 
                        	}); 
                        	
                            FloorDashboard.charts.chartConfig.devicesConnected.data.columns = [result.devicesConnected[1], 
																							   result.devicesConnected[2], 
																							   result.devicesConnected[3], 
																							   result.devicesConnected[4],
																							   result.devicesConnected[5],
																							   result.devicesConnected[6],
																							   result.devicesConnected[7]]
                            FloorDashboard.charts.getChart.devicesConnected = c3.generate(FloorDashboard.charts.chartConfig.devicesConnected);
                            FloorDashboard.charts.setChart.typeOfDevices(true); 
                            setTimeout(function () {
                              FloorDashboard.charts.setChart.devicesConnected();
                            }, FloorDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                            setTimeout(function () {
                            	FloorDashboard.charts.setChart.devicesConnected();
                            }, FloorDashboard.timeoutCount);                           
                        },
                        dataType: "json"
                    });
                },
                avgUplinkSpeed: function (initialData) {
                   /* $.ajax({
                        url: FloorDashboard.charts.urls.avgUplinkSpeed,
                        success: function (result) {
                            FloorDashboard.charts.chartConfig.avgUplinkSpeed.data.columns = result;
                            if (initialData) {
                                FloorDashboard.charts.getChart.avgUplinkSpeed = c3.generate(FloorDashboard.charts.chartConfig.avgUplinkSpeed);
                            } else {
                                FloorDashboard.charts.getChart.avgUplinkSpeed.load({ "columns": FloorDashboard.charts.chartConfig.avgUplinkSpeed.data.columns });
                            }
                            setTimeout(function () {
                              FloorDashboard.charts.setChart.avgUplinkSpeed();
                            }, FloorDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                            setTimeout(function () {
                              FloorDashboard.charts.setChart.avgUplinkSpeed();
                            }, FloorDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });*/

                    FloorDashboard.charts.chartConfig.avgUplinkSpeed.data.columns = [FloorDashboard.charts.chartConfig.txRx.data.columns[1]];
                            if (initialData) {
                                FloorDashboard.charts.getChart.avgUplinkSpeed = c3.generate(FloorDashboard.charts.chartConfig.avgUplinkSpeed);
                            } else {
                                FloorDashboard.charts.getChart.avgUplinkSpeed.load({ "columns": FloorDashboard.charts.chartConfig.avgUplinkSpeed.data.columns });
                            }
                },
                avgDownlinkSpeed: function (initialData) {
                    /*$.ajax({
                        url: FloorDashboard.charts.urls.avgDownlinkSpeed,
                        success: function (result) {
                            FloorDashboard.charts.chartConfig.avgDownlinkSpeed.data.columns = result;
                            if (initialData) {
                                FloorDashboard.charts.getChart.avgDownlinkSpeed = c3.generate(FloorDashboard.charts.chartConfig.avgDownlinkSpeed);
                            } else {
                                FloorDashboard.charts.getChart.avgDownlinkSpeed.load({ "columns": FloorDashboard.charts.chartConfig.avgDownlinkSpeed.data.columns });
                            }
                           setTimeout(function () {
                             FloorDashboard.charts.setChart.avgDownlinkSpeed();
                           }, FloorDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                           	setTimeout(function () {
                             FloorDashboard.charts.setChart.avgDownlinkSpeed();
                            }, FloorDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });*/

                     FloorDashboard.charts.chartConfig.avgDownlinkSpeed.data.columns = [FloorDashboard.charts.chartConfig.txRx.data.columns[0]];
                            if (initialData) {
                                FloorDashboard.charts.getChart.avgDownlinkSpeed = c3.generate(FloorDashboard.charts.chartConfig.avgDownlinkSpeed);
                            } else {
                                FloorDashboard.charts.getChart.avgDownlinkSpeed.load({ "columns": FloorDashboard.charts.chartConfig.avgDownlinkSpeed.data.columns });
                            }
                }
            },
            getChart: {},
            chartConfig: {
                txRx: {
                    size: {
                        height: 320,
                    },
                    bindto: '#fd_chart2',

                    padding: {
                        top: 10,
                        right: 15,
                        bottom: 0,
                        left: 40,
                    },
                    data: {
                        columns: [],
                        types: {
                            Rx: 'spline',
                            Tx: 'spline'
                        },
                        colors: {
                            Tx: '#5cd293',
                            Rx: '#1a78dd'
                        },
                        color: {
                            pattern: ['#2F9E63', '#1a78dd']
                        },
                        point: {
                            show: true
                        }
                    },
                     legend:{
                        item:{

                            "onclick":function(id){
                               FloorDashboard.charts.getChart.txRx.focus(id);  
                            }
                        }
                     },
                    tooltip: {
                        show: true
                    },
                    point: {
                        show: true
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
                activeConnections: {
                    innerHTML: '',
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 3,
                    scale: 100,
                    rotateBy: 360 / 6,
                    speed: 900,
                    delayAnimation:false,
                    onFinishMoving: function (pos) {
                        //console.log('done ', pos);
                    }
                },
                netFlow: {
                    size: {
                        height: 320,
                    },
                    bindto: '#vdChart1',
                    padding: {
                        top: 10,
                        right: 15,
                        bottom: 0,
                        left: 55,
                    },
                    onresized: function () {
                        FloorDashboard.charts.getChart.netFlow.resize();
                    },
                    data: {

                    	x: 'radio',
                        columns: [
			                ['radio', 'Category1', 'Category2'],
			                ['ulink', 300, 400],
			                ['dlink', 300, 400]                   
                        ],

                        type:'bar',
                       
                        colors: {
                            ulink: '#f36e65',
                            dlink: '#1a78d0',
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
                                format: d3.format("s")
                            }
                        },                        
                    }

                },
                typeOfDevices: {
                    innerHTML: '',
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 3,
                    scale: 100,
                    rotateBy: 360 / 6,
                    speed: 900,
                    delayAnimation:false,
                    onFinishMoving: function (pos) {
                        //console.log('done ', pos);
                    }
                },
                activeClients: {
                    innerHTML: '',
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 3,
                    scale: 100,
                    rotateBy: 360 / 6,
                    speed: 900,
                    delayAnimation:false,
                    onFinishMoving: function (pos) {
                    }
                },
                devicesConnected: {
                    size: {
                        height: 90,
                    },
                    bindto: '#fd_chart_client',
                    padding: {
                        top:15,
                        right: 2,
                        bottom: 0,
                        left: 0,
                    },
                    axis: {
                	    x: {
                	      show: false
                	    },
                	    y: {
                  	      show: false
                  	    }
                	},
                    data: {
                        columns: [],
                        colors: {
                            "2G": '#85d1fb',
                            "5G": '#79d58a',
                        },
                        type: 'bar'
                    },
                    donut: {
                        title: "Clients Connected",
                        label: {
                            threshold: 0.03,
                            format: function (value, ratio, id) {
                                'Clients Connected';
                            }
                        },
                        width: 40
                    },
                    tooltip: {
                        format: {
                        	title: function (v) {
                        		return "Clients Connected";
                    		},
                            value: function (value, ratio, id) {
								if (value == 1) { return (value-.2) + ' Client';} else
                                	return (value-.2) + ' Clients';
                            }
                        }
                    },
                    axis: {
                        x: {
                            show: false
                        }
                    },
                    legend: {
                        show: false
                    }

                },
                avgUplinkSpeed: {
                    bindto: '#downchart',
                    padding: {
                        top: 0,
                        right: 20,
                        bottom: 0,
                        left: 20,
                    },
                    data: {
                        colors: {
                            "Rx": '#59a88d',
                        },
                        columns: [],
                        type: 'bar'
                    },
                    bar: {
                        width: {
                            ratio: 0.8
                        }
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
                        show: true,
                        grouped:false
                    },
                    size: {
                        height: 75,
                    },
                    legend: {
                        show: false
                    }

                },
                avgDownlinkSpeed: {

                    bindto: '#upchart',
                    padding: {
                        top: 0,
                        right: 20,
                        bottom: 0,
                        left: 20,
                    },
                    data: {
                        colors: {
                            "Tx": '#59a88d',
                        },
                        columns: [],
                        type: 'bar'
                    },
                    bar: {
                        width: {
                            ratio: 0.8
                        }
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
                        show: true,
                        grouped:false
                    },
                    size: {
                        height: 75,
                    },
                    legend: {
                        show: false
                    }

                }
            }
        },
        init: function (params) {
            var c3ChartList = ['txRx', 'activeConnections', 'netFlow', 'devicesConnected','typeOfDevices','activeClients'];
            var that = this;
            $.each(c3ChartList, function (key, val) {
                that.charts.setChart[val](true,params?params:"");
            });
            this.systemAlerts();
        },
        systemAlerts:function(){
            $.ajax({
                url:'/facesix/rest/site/portion/networkdevice/alerts?spid='+urlObj.spid+"&cid="+urlObj.cid,
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
        }

    }
})();
currentDashboard=FloorDashboard;
var renderSummaryTemplate = function (data) {
    if (data && data.summary) {
        var source = $("#summary-template").html();
        var template = Handlebars.compile(source);
        var rendered = template(data);
        $('.summaryTable').html(rendered);
    }

}

var fetchSummaryTemplateData = function (){
    $.ajax({
        url: '',
        //url:/facesix/rest/site/portion/networkdevice/loginfo?spid='+urlObj.spid
        success: function (result) {
        
        	//console.log (result);
            var templateObj={
                        data:[]
                     }

                     for(var i=0;i<result.length;i++){
                        var obj={};
                        obj.count=result[i].time;
                        obj.description=result[i].snapshot;
                        if(obj.description!=null)
                         templateObj.data.push(obj);
                     }
            renderSummaryTemplate({summary:templateObj.data});
        },
        error: function (data) {
            //console.log(data);
        },
        dataType: "json"
    })
}

var renderAlertsTemplate = function (data) {
    if (data && data.alerts) {
        var source = $("#alerts-template").html();
        var template = Handlebars.compile(source);
        var rendered = template(data);
        $('.summaryTable').html(rendered);
    }

}

var fetchAlertsTemplateData = function (){
    $.ajax({
        url: '/facesix/rest/site/portion/networkdevice/alerts?spid='+urlObj.spid+"&cid="+urlObj.cid,
        success: function (result) {

            var templateObj={
                data:[]
			}

            for(var i=0;i<result.length;i++){
            	var obj={};	
            	obj.description=result[i];
            	templateObj.data.push(obj);
            }
            		
            renderAlertsTemplate({alerts:templateObj.data});

        },
        error: function (data) {
            //console.log(data);
        },
        dataType: "json"
    })
}

$('body').on('click', '.viewAlertsTable', function (evt) {
    evt.preventDefault();
    window.clearInterval(fetchSummaryInterval);
    fetchAlertsTemplateData();
    window.fetchAlertsInterval = setInterval(function (){fetchAlertsTemplateData();}, FloorDashboard.timeoutCount);
});
$('body').on('click', '.viewSummaryTable', function () {
    window.clearInterval(fetchAlertsInterval);
    fetchSummaryTemplateData();
    window.fetchSummaryInterval = setInterval(function (){fetchSummaryTemplateData();}, FloorDashboard.timeoutCount);
});

//FloorDashboard.init();
//fetchSummaryTemplateData();
fetchAlertsTemplateData();
window.fetchSummaryInterval = setInterval(function (){fetchSummaryTemplateData();}, FloorDashboard.timeoutCount);
// fetchSummaryTemplateData();

//Network config Replica
var imageW=40;
var imageH=40;
var fzie = 30;
var txty = 9;
var twoGOn = 1;
var clientsOn = 1;
var fiveGOn = 1;
var category = [];
var list;
var tagsCounter;

$('#clientsONOFF').change(function(){ 
	if($(this).prop('checked') == true){
		clientsOn = 1;
		d3.selectAll('.person').classed('tagdisable', false); 
		$('.tagsfilters').removeClass('active');
		if($('#twoGONOFF').prop('checked') == true){ 
			d3.selectAll('.twoGdevice').classed('tagdisable', false); 
		}else{
			d3.selectAll('.twoGdevice').classed('tagdisable', true); 
		}
		if($('#fiveGONOFF').prop('checked') == true){ 
			d3.selectAll('.fiveGdevice').classed('tagdisable', false); 
		}else{
			d3.selectAll('.fiveGdevice').classed('tagdisable', true); 
		}
	}
	else{
		clientsOn = 0;
		d3.selectAll('.person').classed('tagdisable', true); 
		$('.tagsfilters').addClass('active');
	}  
}); 
$('#twoGONOFF').change(function(){ 
	if($(this).prop('checked') == true){
		twoGOn = 1;
		d3.selectAll('.twoGdevice').classed('tagdisable', false); 
	}
	else{
		twoGOn = 0;
		d3.selectAll('.twoGdevice').classed('tagdisable', true); 
	}  
});
$('#fiveGONOFF').change(function(){ 
	if($(this).prop('checked') == true){
		fiveGOn = 1;
		d3.selectAll('.fiveGdevice').classed('tagdisable', false); 
	}
	else{
		fiveGOn = 0;
		d3.selectAll('.fiveGdevice').classed('tagdisable', true); 
	}  
});

var GatewayFinder = false;

function solutionInfo(s1,s2,s3,s4) {
	GatewayFinder = s4;
	console.log("GatewayFinder " +GatewayFinder)
}

var floornetworkConfig={
    
     'plantDevicesTags' :function(spid,p1, p2, p3){
	   if (p1 == "true") {
		   	$('.person').remove();
            $('.tooltip').remove();
			// $('.qrnd').remove();
	      
	    	$.ajax({
	         	url:'/facesix/rest/site/portion/clientdevice/clientsDetails?spid='+spid,
	             method:'get',
                 async: false, 
	             success:function(response){
	            	 list = response.clientsDetails;
                     return list;
	             },  error:function(err){
	             }
	         });
           
           function getRandomFloat(min, max) {
	    		return Math.random() * (max - min) + min;
	    	}
           
	    	if (typeof list != "undefined" && list !="") {
   	    		for (var j = 0; j < list.length; j++) {   
   	    			tags = list[j];
   	    			
   	    			category.push(tags.client_type); 
   	    			
   	    			var mcId = 'tags-'+tags.macaddr;
	    				mcId = mcId.replace(/:/g , "-");
   	    			var tagsFound = document.getElementById(mcId);
   	    			
                        
                        if(list[j].client_type == 'mac'){
        				client_type  = "\uf179"
        			}else if(list[j].client_type == 'android'){
        				client_type  = "\uf17b"
        			}else if(list[j].client_type == 'windows'){
        				client_type  = "\uf17a"
        			}else if(list[j].client_type == 'laptop'){
        				client_type  = "\uf109"
        			}else if(list[j].client_type == 'speaker'){
        				client_type  = "\uf026"
        			}else if(list[j].client_type == 'printer'){
        				client_type  = "\uf02f"
        			}
        			
        			myx = Math.floor(Math.random() * 51) - 30
        			myy = Math.floor(Math.random() * 51) - 30
        			myx = tags.x*1+5+myx;
        			myy = tags.y*1+13+myy;
        			maa = myx*1+8;
        			mbb = myy*1-5;
                    
                    
        			if(list[j].radio == '2.4Ghz'){
        				tagColor = 'rgba(150, 200, 100, 0.5)';
        				newClass = 'twoGdevice';
        			}else if(list[j].radio == '5Ghz'){
        				newClass = 'fiveGdevice';
        				tagColor = 'rgba(64, 224, 208, 0.5)';
        			}
        			if(clientsOn == 0){
        				disabledClass = 'tagdisable';
        			} 
        			if(list[j].radio == '2.4Ghz' && twoGOn == 0){
        				disabledClass = 'tagdisable';
        			}else if(list[j].radio == '5Ghz' && fiveGOn == 0){
        				disabledClass = 'tagdisable';
        			}  
        			
        				var mainGroup = this.svg.append('g')
        				.attr('id', mcId)
        				.attr("class","person animateZoom "+newClass+' '+mcId+' '+mcId) 
        				.attr('data-x',maa) 
        				.attr('data-y',mbb)  
        				.attr('transform', "translate("+maa+","+mbb+")") 
        				.attr('data-html', 'true')
        				.attr('title',"Client Mac : "+list[j].mac_address + "<br /> SSID : "+list[j].ssid);
        				$(mainGroup).tooltip({container:'body'});
        			
        				var subGroup = mainGroup.append('g')
   	    				.attr('id', mcId+'-sub') 
   	    				.attr('transform','translate(0,0)') 
    					.attr("class","onlyscale"); 
   	    			
	   	    			var circle = subGroup.append("circle")
	   	    				.attr("r", fzie)
	   	    				.attr("fill", tagColor) 
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
	   	    				.text(function(d) { return client_type; });
                            tagsCounter = tagsCounter + 1;
                        
                    
        			                    
  	   		}
  		
            }	
	             }
         
         
         if($("#clientsONOFF").prop('checked') == true){
            if(twoGOn == 1){
            d3.selectAll('.twoGdevice').classed('tagdisable', false); 
            } else {
            d3.selectAll('.twoGdevice').classed('tagdisable', true); 
            }
         
         
         if(fiveGOn == 1){
           d3.selectAll('.fiveGdevice').classed('tagdisable', false); 
            } else {
            d3.selectAll('.fiveGdevice').classed('tagdisable', true); 
            }
         } else {
              d3.selectAll('.twoGdevice').classed('tagdisable', true);
              d3.selectAll('.fiveGdevice').classed('tagdisable', true); 
         }
            
         
  		 setTimeout(function() {
	     	floornetworkConfig.plantDevicesTags(spid,p1, p2, p3);  
        }, 10000);
  
     },
    'plantDevices':function(image,type,x,y,status,uid,list){
        var urlMap={
            "server":'flrdash',
            'switch':'swiboard',
            'ap':'devboard',
            'sensor':'devboard'
        }
        if (type == "server") {
            var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&cid="+this.urlObj.cid+"&type=server" 
        } else if (type == "sensor") {
        	if (GatewayFinder=='true') {
                var url="/facesix/web/finder/device/"+urlMap[type]+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&uid="+uid+"&cid="+this.urlObj.cid+"&param=2"
        	} else {
                var url="/facesix/web/finder/device/"+urlMap[type]+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&uid="+uid+"&cid="+this.urlObj.cid 
        	}
       } else {
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
      
      //  $(anchor[0]).appendTo("svg.floorsvg");
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
        var that=this;
        var urlObj=this.fetchurlParams(window.location.search.substr(1));
        $.ajax({
            url:'/facesix/rest/site/portion/networkdevice/list?spid='+urlObj.spid,
            method:'get',
            success:function(response){
                var devices=response; 
                for(var i=0;i<devices.length;i++)
                { 
                    //var type=devices[i].typefs;
                	
                	if(devices[i].parent=="ble")
                		var type="sensor";
                	else
                		var type=devices[i].typefs;
                	
                    var status=devices[i].status;
                    var image="/facesix/static/qubercomm/images/networkicons/"+type+"_"+status+".png";
                    var uid=devices[i].uid;
                    var list = devices[i].list;
                    if(list != undefined && list != ''){
                    	list = JSON.parse(devices[i].list);
                    }
                    
                    that.plantDevices(image,type,devices[i].xposition,devices[i].yposition,status,uid,list);
                   
                }
               
            },
            error:function(err){
                //console.log(err);    
            }
        })
         if (p1 == "true") {
             var spid = urlObj.spid;
         	that.plantDevicesTags(spid,p1, p2,p3);
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
        $('.floorCanvas').toggleClass('deviceexpand');
        $('.floorCanvas').toggleClass('pad0');
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


$('select#floorName').on('change', function() {
	
	var spid    =$('#floorName').val();
	var sid  	= location.search.split("&")[0].replace("?","").split("=")[1];
	var cid 	= location.search.split("&")[2].replace("?","").split("=")[1];
    
	var param = GatewayFinder;
	//console.log(param +"hello")
	var url  = "/facesix/web/site/portion/dashboard?sid="+sid+"&spid="+spid+"&cid="+cid;
	if(param = true){
		url  = "/facesix/web/site/portion/dashboard?sid="+sid+"&spid="+spid+"&cid="+cid+"&param=1";
	}
	 
	
	$.ajax({
  	   	  	url:url,
  	   	  	method:'GET',
  	   	  	data:{},
  	   	  	success:function(response,error){
  	   	  		location.replace(url);
  	   	  	},
  	   	  	error:function(error){ 
  	   	  	}
  	   	  });
	
});