
(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	var timer = 10000;
	var count = 1;
	var peerStats;
    DeviceDashboard = {
        timeoutCount: 10000,
        tables: {
            url: {
                activeClientsTable: '/facesix/rest/beacon/ble/networkdevice/getpeers?uid='+urlObj.uid+"&spid="+urlObj.spid+"&cid="+urlObj.cid
            },
            setTable: {
                activeClientsTable: function () {
                    $.ajax({
                        url: DeviceDashboard.tables.url.activeClientsTable,
                        method: "get",
                        success: function (result) {
                        	console.log (""+JSON.stringify(result.devicesConnected));
                        	peerStats = result.devicesConnected;
                            var result=result.devicesConnected[0];
                            console.log("active Tags " +result)
							if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                DeviceDashboard.activeClientsData = result;
                                if (result.length > 5) {
                                    var filteredData = result.slice(0, 5);
                                    show_next_button = true;
                                } else {
                                    var filteredData = result;
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
                                $('table .showPopup ').on("tap",rightMenu);                                
                                
                            }
                            if(result.length == 0 || result.length == undefined){
                            	$('.tableHide').hide();
                            } else {
                            	$('.tableHide').show();
                            }
                            DeviceDashboard.charts.setChart.devicesConnected(true);
                            //DeviceDashboard.charts.setChart.typeOfDevices(true);
                            DeviceDashboard.charts.setChart.batteryinfo(true);

                            
                            setTimeout(function () {
                              DeviceDashboard.tables.setTable.activeClientsTable();
                           }, 10000);                            

                        },
                        error: function (data) {
                            setTimeout(function () {
                              DeviceDashboard.tables.setTable.activeClientsTable();
                           }, 10000);                            
                        },
                        dataType: "json"

                    });
                }
            }
        },
        
        charts: {
            urls: {
                txRx: '/facesix/rest/beacon/ble/networkdevice/rxtx?uid='+urlObj.uid,
                connectedInterfaces: '/facesix/rest/beacon/ble/networkdevice/getintf?uid='+urlObj.uid,
                batteryinfo: '/facesix/rest/beacon/ble/networkdevice/battery?uid='+urlObj.uid,
                tcpudpconnections: '/facesix/rest/beacon/ble/networkdevice/getstacount?uid='+urlObj.uid,
               // netFlow: '/facesix/rest/beacon/ble/networkdevice/battery?uid='+urlObj.uid,
                typeOfDevices: '/facesix/rest/beacon/ble/networkdevice/venue/connectedTagType?uid='+urlObj.uid+"&cid="+urlObj.cid,
                devicesConnected: '/facesix/rest/beacon/ble/networkdevice/getdevcon?uid='+urlObj.uid,
                avgUplinkSpeed: '/facesix/rest/beacon/ble/networkdevice/getcpu?uid='+urlObj.uid,
                avgDownlinkSpeed: '/facesix/rest/beacon/ble/networkdevice/getmem?uid='+urlObj.uid
            },
            setChart: {
                txRx: function (initialData,params) {
                	var duration = params;
                	var len		 = 0;
                	if (duration != undefined) {
                		len		 = duration.length;
                	}
                	
                	var link 	 = DeviceDashboard.charts.urls.txRx;
                	if (len != 0 && duration.length != 0) {
                		//console.log("rxtx111" + duration)
						link = "/facesix/rest/beacon/ble/networkdevice/rxtxagg?uid="+urlObj.uid+"&"+params;
                	} else {
                		link = DeviceDashboard.charts.urls.txRx;
                		//console.log("rxtx222")
                		len = 0;
                	}                
                
                    $.ajax({
                        url: link,
                        success: function (result) {
                        	
                        	console.log("rxtx " +JSON.stringify(result))
                        	console.log("length=== " +result.length + "len" + len);
                            if (result && result.length) {                           
                                var timings = [];
                                var txArr = ["Uplink"];
                                var rxArr = ["Downlink"];
                                for (var i = 0; i < result.length; i++) {
                                	
                                	if (len == 0) {
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
	            						//txArr[i] = txArr[i]/100;
	            						//rxArr[i] = rxArr[i]/100;
            						} else {
            							txArr[i] = result[i].avg_ble_tx_bytes;
            							rxArr[i] = result[i].avg_ble_rx_bytes;           						
            						}
                   					var formatedTime = result[i].time;
                                    var date = new Date(formatedTime + 'UTC');
                                    timings.push(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
                                }
                                
                                var txrxVal = $( ".txrxswap option:selected" ).val();
                                if(txrxVal == "Uplink/Downlink Avg. Bytes"){
                                	DeviceDashboard.charts.chartConfig.txRx.data.columns = [txArr, rxArr];
                                    DeviceDashboard.charts.chartConfig.txRx.axis.x.categories = timings;
                                    DeviceDashboard.charts.getChart.txRx = c3.generate(DeviceDashboard.charts.chartConfig.txRx);
                                }
                                //DeviceDashboard.charts.setChart.avgDownlinkSpeed(true)
                                //DeviceDashboard.charts.setChart.avgUplinkSpeed(true)
                            }
                            
                            setTimeout(function () {
                              DeviceDashboard.charts.setChart.txRx();
                           }, DeviceDashboard.timeoutCount);
                        },
                        error: function (data) {
                           console.log(data);
                           setTimeout(function () {
                             DeviceDashboard.charts.setChart.txRx();
                           }, DeviceDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                
                connectedInterfaces: function (initialData,params) {
                	
                    $.ajax({
                        url: DeviceDashboard.charts.urls.connectedInterfaces,
                        success: function (result) {
                            var result=result.connectedInterfaces;
                            var columns=[];
                            var names={};
                            var colors={},colorMap={
                                'enabled':"#6baa01",
                                'disabled':'#cccccc'
                            }
                            for(var i=0;i<result.length;i++){
                                   columns.push([result[i].device,result[i].vapcount]);
                                   names["data"+(i+1)]=result[i].device;
                                   colors[result[i].device]=colorMap[result[i].status];
                            }
                            DeviceDashboard.charts.chartConfig.connectedInterfaces.data.columns = columns;
                            DeviceDashboard.charts.chartConfig.connectedInterfaces.data.names = names;
                            DeviceDashboard.charts.chartConfig.connectedInterfaces.data.colors = colors;
                            if (initialData) {
                                DeviceDashboard.charts.getChart.connectedInterfaces = c3.generate(DeviceDashboard.charts.chartConfig.connectedInterfaces);
                            } else {
                                DeviceDashboard.charts.getChart.connectedInterfaces.load({ "columns": DeviceDashboard.charts.chartConfig.connectedInterfaces.data.columns,'colors': DeviceDashboard.charts.chartConfig.connectedInterfaces.data.colors});
                            }
                            setTimeout(function () {
                              DeviceDashboard.charts.setChart.connectedInterfaces();
                            }, DeviceDashboard.timeoutCount);
                            
                        },
                        error: function (data) {
                           //console.log(data);
                           setTimeout(function () {
                             DeviceDashboard.charts.setChart.connectedInterfaces();
                           }, DeviceDashboard.timeoutCount);
                           
                        },
                        dataType: "json"
                    });
                },
                
                
                batteryinfo: function (initialData) {

                	var c = peerStats;
                	DeviceDashboard.charts.chartConfig.batteryinfo.targetPos = targetPos = c[1][1];
                	DeviceDashboard.charts.chartConfig.batteryinfo.innerHTML = '<i class="fa fa-tags" style="color:green;"></i></br>0';
                	
                	console.log ("Tags==>" + c[1][1])
                	var result = c[1][1];
                	
                    //if (initialData) {
                    $('#dd-chart2').circles(DeviceDashboard.charts.chartConfig.batteryinfo);
                    counter=0;
                    //var timer=setInterval(function(){
                        var pieChart=$("#dd-chart2").data("circles");
                        var str = '<i class="fa fa-tags" aria-hidden="true" style="color:green"></i></br>'
                        var str = str + result;
                        pieChart.innerhtml.html(str);

                        /*    if(counter>=targetPos) {
                        	counter = 0;
                            clearInterval(timer);
                        }
                        else{
                        		counter+=1;
                        		//pieChart.innerhtml.text(counter.toString())   
                        		 var str = '<i class="fa fa-tags" aria-hidden="true" style="color:green"></i></br>'
                                 var str = str + counter.toString();
                                 pieChart.innerhtml.html(str);
                            }*/
                    //},100)
                    //}
                },
                
                tcpudpconnections: function (initialData,params) {
                	
                    $.ajax({
                        url: DeviceDashboard.charts.urls.tcpudpconnections,
                        success: function (result) {
                            var result=result.typeOfDevices;
                            
                            DeviceDashboard.charts.chartConfig.tcpudpconnections.data.columns = result;
                            if (initialData) {
                                DeviceDashboard.charts.getChart.tcpudpconnections = c3.generate(DeviceDashboard.charts.chartConfig.tcpudpconnections);
                            } else {
                                DeviceDashboard.charts.getChart.tcpudpconnections.load({ "columns": DeviceDashboard.charts.chartConfig.tcpudpconnections.data.columns});
                            }
                            setTimeout(function () {
                             DeviceDashboard.charts.setChart.tcpudpconnections();
                            }, 12000);
                            
                        },
                        error: function (data) {
                           //console.log(data);
                           setTimeout(function () {
                           	DeviceDashboard.charts.setChart.tcpudpconnections();
                           }, 12000);
                           
                        },
                        dataType: "json"
                    });
                },                
                          
               /* netFlow: function (initialData,params) {
                    $.ajax({
                        url: DeviceDashboard.charts.urls.netFlow,
                        success: function (result) {
                            var result=result.batteryinfo;
                            var columns=[];
                            var names={};
                            var colors={},colorMap={
                                'enabled':"#6baa01",
                                'disabled':'#cccccc'
                            }
                            for(var i=0;i<result.length;i++){
                                   columns.push([result[i].device,result[i].batterylevel]);
                                   names["data"+(i+1)]=result[i].device;
                                   colors[result[i].device]=colorMap[result[i].status];
                            }
                            DeviceDashboard.charts.chartConfig.netFlow.data.columns = columns;
                            DeviceDashboard.charts.chartConfig.netFlow.data.names = names;
                            DeviceDashboard.charts.chartConfig.netFlow.data.colors = colors;
                            if (initialData) {
                                DeviceDashboard.charts.getChart.netFlow = c3.generate(DeviceDashboard.charts.chartConfig.netFlow);
                            } else {
                                DeviceDashboard.charts.getChart.netFlow.load({ "columns": DeviceDashboard.charts.chartConfig.netFlow.data.columns,'colors': DeviceDashboard.charts.chartConfig.netFlow.data.colors});
                            }
                            
                            setTimeout(function () {
                              DeviceDashboard.charts.setChart.netflow();
                            }, DeviceDashboard.timeoutCount);
                            
                        },
                        
                        error: function (data) {
                           setTimeout(function () {
                             DeviceDashboard.charts.setChart.netflow();
                           }, DeviceDashboard.timeoutCount);
                           
                        },
                        dataType: "json"
                    });
                },*/
                
                typeOfDevices: function (initialData) {
               	 $.ajax({
                        url: DeviceDashboard.charts.urls.typeOfDevices,
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

                            DeviceDashboard.charts.chartConfig.typeOfDevices.data.columns = columns;
                            DeviceDashboard.charts.chartConfig.typeOfDevices.data.names   = names;
                            DeviceDashboard.charts.chartConfig.typeOfDevices.data.colors  = colors;
                        	DeviceDashboard.charts.getChart.typeOfDevices = c3.generate(DeviceDashboard.charts.chartConfig.typeOfDevices);
                            if (initialData) {
                            	DeviceDashboard.charts.getChart.typeOfDevices = c3.generate(DeviceDashboard.charts.chartConfig.typeOfDevices);
                            } else {
                            	DeviceDashboard.charts.getChart.typeOfDevices.load({ "columns": DeviceDashboard.charts.chartConfig.typeOfDevices.data.columns,'colors': DeviceDashboard.charts.chartConfig.typeOfDevices.data.colors});
                            }                        	
                        	

                            setTimeout(function () {
                            	DeviceDashboard.charts.setChart.typeOfDevices();
                            }, DeviceDashboard.timeoutCount);
                            
                        },
                        error: function (data) {
                           //console.log(data);
                           setTimeout(function () {
                        	   DeviceDashboard.charts.setChart.typeOfDevices();
                           }, DeviceDashboard.timeoutCount);
                           
                        },
                        dataType: "json"
                    });

               },
               	devicesConnected: function (initialData,params) {
                	var c = peerStats;
                	DeviceDashboard.charts.chartConfig.devicesConnected.data.columns = [c[1], c[2]]
                	DeviceDashboard.charts.getChart.devicesConnected = c3.generate(DeviceDashboard.charts.chartConfig.devicesConnected);
                }, 
                tcpudpconnections: function (initialData,params) {
                	var c = peerStats;
                	if(c!=undefined){
                		DeviceDashboard.charts.chartConfig.tcpudpconnections.data.columns = [c[6], c[7]]
                    	DeviceDashboard.charts.getChart.tcpudpconnections = c3.generate(DeviceDashboard.charts.chartConfig.tcpudpconnections);
                	}
                },
                avgUplinkSpeed: function (initialData) {
                    $.ajax({
                        url: DeviceDashboard.charts.urls.avgUplinkSpeed,
                        success: function (result) {
                        	console.log(result[0].cpu);
                        	//console.log(result[0].cpu.split(' ', 1)[0]);
                        	
                        	var cpu_per = result[0].cpu; //by test
                        	//var cpu_per = "5";
                        	//console.log (cpu_per);
                        	if (cpu_per <= 0) {
                        		cpu_per = 2;
                        	}
                        	
                        	if (cpu_per >= 80) {
                        		cpu_per -= 25;
                        	}                        	
                        	
                          	DeviceDashboard.charts.chartConfig.avgUplinkSpeed.data.columns = [["Cpu", cpu_per]];
                          	DeviceDashboard.charts.getChart.avgUplinkSpeed = c3.generate(DeviceDashboard.charts.chartConfig.avgUplinkSpeed);                        
                            setTimeout(function () {
                              DeviceDashboard.charts.setChart.avgUplinkSpeed();
                            }, DeviceDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                          	DeviceDashboard.charts.chartConfig.avgUplinkSpeed.data.columns = [["Cpu", 0]];
                          	DeviceDashboard.charts.getChart.avgUplinkSpeed = c3.generate(DeviceDashboard.charts.chartConfig.avgUplinkSpeed);                            
                            setTimeout(function () {
                              DeviceDashboard.charts.setChart.avgUplinkSpeed();
                           }, DeviceDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                    //DeviceDashboard.charts.chartConfig.avgUplinkSpeed.data.columns = [["data", 1]];
                    //DeviceDashboard.charts.getChart.avgUplinkSpeed = c3.generate(DeviceDashboard.charts.chartConfig.avgUplinkSpeed);
                },
                avgDownlinkSpeed: function (initialData) {
                    $.ajax({
                        url: DeviceDashboard.charts.urls.avgDownlinkSpeed,
                        success: function (result) {
                        //	console.log(JSON.stringify(result))
                        	var cpu_mem = 0; //by test
                        	if (result[0].mem != undefined)
                        		cpu_mem = result[0].mem;
                        	//cpu_mem = "4";
                            DeviceDashboard.charts.chartConfig.avgDownlinkSpeed.data.columns = [["Mem", cpu_mem]];
                            DeviceDashboard.charts.getChart.avgDownlinkSpeed = c3.generate(DeviceDashboard.charts.chartConfig.avgDownlinkSpeed);
                            setTimeout(function () {
                             	DeviceDashboard.charts.setChart.avgDownlinkSpeed();
                           	}, DeviceDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                            DeviceDashboard.charts.chartConfig.avgDownlinkSpeed.data.columns = [["Mem", 0]];
                            DeviceDashboard.charts.getChart.avgDownlinkSpeed = c3.generate(DeviceDashboard.charts.chartConfig.avgDownlinkSpeed);
                            
                            setTimeout(function () {
                              DeviceDashboard.charts.setChart.avgDownlinkSpeed();
                           	}, DeviceDashboard.timeoutCount);
                        },
                        dataType: "json"                   	
                    });
					//DeviceDashboard.charts.chartConfig.avgDownlinkSpeed.data.columns = [["data", 1]];
                    //DeviceDashboard.charts.getChart.avgDownlinkSpeed = c3.generate(DeviceDashboard.charts.chartConfig.avgDownlinkSpeed);
                }

            },
            getChart: {},
            chartConfig: {
                txRx: {
                    size: {
                        height: 270,
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
                               DeviceDashboard.charts.getChart.txRx.focus(id);  
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
                            }
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
                connectedInterfaces: {
                    size: {
                        height: 220,
                    },
                    bindto: '#dd-chart1',
                    padding: {
                        top: 0,
                        right: 0,
                        bottom: 30,
                        left: 0,
                    },
                    data: {
                        columns: [],
                        names: {},
                        colors: {},
                        type: 'donut',
                    },
                    donut: {
                        width: 42,
                        label: {
                            format: function (i, j, k) {
                                return k;
                            }
                        },

                    },
                    tooltip: {
                        format: {
                            value: function (i, j, k) {
                            	if (k=='ble')
                                	return 'ble='+i;
                                else if (k=='xbee')	
                                	return 'xbee='+i;
                                else
                                	return 'vap='+i;
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
                
                batteryinfo: {
                    innerHTML: '',	
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 0,
                    scale: 500,
                    rotateBy: 360 / 6,
                    speed: 900,
                    delayAnimation:false,
                    onFinishMoving: function (pos) {
                    }
                },
                
                tcpudpconnections: {
                    size: {
                        height: 270,
                    },
                    bindto: '#fd_chart5',
                    padding: {
                        top: 0,
                        right: 15,
                        bottom: 0,
                        left: 15,
                    },
                    data: {
                        columns: [
                            ['Active', 7],
                            ['Block', 12],
                        ],
                        colors: {
                            "Active": '#85d1fb',
                            "Block": '#79d58a',
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
                                return value + 'Tags';
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
                activeConnections: {
                    innerHTML: '',
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 3,
                    scale: 500,
                    rotateBy: 360 / 6,
                    speed: 700,
                    delayAnimation: 1000,
                    onFinishMoving: function (pos) {
                        //console.log('done ', pos);
                    }
                },
                /*netFlow: {
                    size: {
                        height: 300,
                    },
                    bindto: '#vdChart1',
                    padding: {
                        top: 0,
                        right: 0,
                        bottom: 30,
                        left: 0,
                    },
                    data: {
                        columns: [],
                        names: {},
                        colors: {},
                        type: 'donut',
                    },
                    donut: {
                        width: 55,
                        label: {
                            format: function (i, j, k) {
                                return i+'%';
                            }
                        },

                    },
                    tooltip: {
                        format: {
                            value: function (i, j, k) {
                              	return "";
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

                },*/
                typeOfDevices: {
                    size: {
                        height: 300,
                    },
                    bindto: '#dd-chart5',
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
                },
                devicesConnected: {
                    size: {
                        height: 300,
                    },
                    bindto: '#fd_chart4',
                    padding: {
                        top: 0,
                        right: 15,
                        bottom: 0,
                        left: 15,
                    },
                    data: {
                        columns: [
                            ['Tags', 7],
                            ['Others', 12],
                            ['Total', 12],
                        ],
                        colors: {
                            "Tags": '#f14e5a',
                            "Others": '#79d58a',
                            "Total": '#c278ed',
                        },
                        type: 'donut'
                    },
                    donut: {
                        title: "",
                        label: {
                            threshold: 0.03,
                            format: function (value, ratio, id) {
                                return value;
                            }
                        },
                        width: 55
                    },
                    tooltip: {
                        format: {
                            value: function (value, ratio, id) {
                                return value;
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
                avgUplinkSpeed: {
                    bindto: '#downchart',

                    data: {
                        columns: [
                            ['Cpu', 91.4]
                        ],
                        type: 'gauge'
                    },
                    gauge: {
                    },
                    color: {
                    	pattern: ['#60B044', '#F6C600', '#F97600', '#FF0000'],
                    	threshold: {
            				values: [30, 60, 90, 100]
        				}
                    },
                    tooltip: {
                        show: true
                    },
                    size: {
                        height: 160,
                    }

                },
                avgDownlinkSpeed: {
                    bindto: '#upchart',

                    data: {
                        columns: [
                            ['Mem', 91.4]
                        ],
                        type: 'gauge'
                    },
                    gauge: {
                    },
                    color: {
                    	pattern: ['#60B044', '#F6C600', '#F97600', '#FF0000'],
                    	threshold: {
            				values: [30, 60, 90, 100]
        				}
                    },
                    tooltip: {
                        show: true
                    },
                    size: {
                        height: 160,
                    }

                }
            }
        },
        init: function (params) {
            var c3ChartList = ['txRx', 'typeOfDevices', 'connectedInterfaces', 'tcpudpconnections', 'avgDownlinkSpeed', 'avgUplinkSpeed'];
            var tableList   = ['activeClientsTable']
            var that        = this;
            
            $.each(tableList, function (key, val) {
                that.tables.setTable[val]();
            });      
            
            this.systemAlerts();
            
            $.each(c3ChartList, function (key, val) {
                that.charts.setChart[val](true,params?params:"");
            });
            
        },
        systemAlerts:function(){
            $.ajax({
                url:'/facesix/static/qubercomm/dashboard.json',
                method:'GET',
                success:function(result){
                     var result=result.floorAlerts;
                     if(result==0){
                        $(".alerts-gif").removeClass("hide").attr('src','/facesix/static/qubercomm/images/venue/correct.gif');
                        $(".alertText").text("All Systems Healthy");
                     }
                     else{
                        $(".alerts-gif").removeClass("hide").attr('src','/facesix/static/qubercomm/images/venue/inactive.gif');
                        $(".alertText").text("Systems at Risk");
                     }       
                },
                error:function(){

                },
                dataType:'json'
            })
        }

    }
})();
currentDashboard=DeviceDashboard;
//Network config Replica
var imageW=30;
var imageH=30;
var floornetworkConfig={
    'plantDevices':function(image,type,x,y,status,uid){
        var anchor=this.svg.append("a")
        .attr("xlink:href","#")
        .attr("target","_blank")
        var newImage=anchor
        .append("image")
        .attr({
            'x':x,
            'y':y,
            'xlink:href':image,
            'status':status,
            'height':imageH,
            'width':imageW,
            'data-uid':uid,
            'type':type
        });
        $(anchor[0]).appendTo("svg.floorsvg");
     },
     getDevices:function(spid){
        var that=this;
        $.ajax({
            url:'/gettree',
            method:'get',
            success:function(response){
                var devices=response.content;
                for(var i=0;i<devices.length;i++)
                {
                    var type=devices[i].typefs;
                    var image="images/networkicons/"+type+"_active.png";
                    var uid=devices[i].uid;
                    var status=devices[i].status;
                    that.plantDevices(image,type,devices[i].xposition,devices[i].yposition,status,uid)
                }
            },
            error:function(err){
                //console.log(err);    
            }
        })
     }
}
$(document).ready(function(){
	$('.tableHide').hide();
    floornetworkConfig.svg=d3.select("svg.floorsvg");
    //floornetworkConfig.getDevices();
    DeviceDashboard.init();



$('body').on('click', ".tablePreviousPage", function (e) {

    var show_previous_button = true;
    var show_next_button = true;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    previous_page = current_page - 1
    var row_limit = $tableBlock.attr('data-row-limit');
    row_limit = parseInt(row_limit);

    if (previous_page == 1) {
        show_previous_button = false;
    }
    var filteredData = DeviceDashboard.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
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

$('body').on('click', ".tableNextPage", function (e) {

    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    next_page = current_page + 1
    var row_limit = $tableBlock.attr('data-row-limit');
    row_limit = parseInt(row_limit);

    if (DeviceDashboard.activeClientsData.length > next_page * row_limit) {
        show_next_button = true;
    }

    var filteredData = DeviceDashboard.activeClientsData.slice(row_limit * current_page, row_limit * next_page);
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
    DeviceDashboard.tables.setTable.activeClientsTable();
});
})


//Network config tree
//Device Count Map
deviceData = {
    'server': 0,
    'switch': 0,
    'ap': 0,
    'sensor': 0,
    'total':0
}
//Device Prototype
function Device(type, status, uid, pid, child) {
    if (child) {
        var device = {
            type: type,
            status: status,
            uid: uid,
            parent: pid,
            child: []
        }
        device[type + "_id"] = deviceData[type];
        return device
    }
    this.devices = {
        type: type,
        status: status,
        uid: uid,
        parent: pid,
        child: []
    }
    this.devices[type + "_id"] = deviceData[type];
}
Device.prototype.buildTree = function(current, parent) {
    var type = current.type;
    var uid = current.uid;
    var status = current.status=="Added"?"Offline":current.status;
    var id=current[type+"_id"];
    if (current.parent=="ble")
        networkTree.addNode(type, uid, status,'ble',id)
        else if (!parent)
        networkTree.addNode(type, uid, status,'network',id)
    else
        networkTree.addNode(type, uid, status, parent,id);
}
Device.prototype.addChildren = function() {
    var current = this.devices;
    this.buildTree(current);
    var self = this;
    var recursiveDepthAdd = function(current) {
        var children = networkTree.childDevices;
        for (var i = 0; i < children.length; i++) {
            if (current.uid == children[i].parent)
                current.child.push(children[i]);
        }
        for (var j = 0; j < current.child.length; j++) {
            self.buildTree(current.child[j], current.type + "" + current[current.type + "_id"])
            recursiveDepthAdd(current.child[j])
        }
    }
    recursiveDepthAdd(current)

}

var gateway 		= false;
var finder  		= false;
var heatmap  		= false;
var GatewayFinder   = false;

function getDevices(param1, param2,param3,param4) {
	gateway 		= param1;
	finder  		= param2;
	heatmap 		= param3;
	GatewayFinder 	= param4;
	//console.log ("Param1 " + gateway + "Param2 " + finder + "param3" + heatmap  + "param4" + GatewayFinder)
}

var networkTree = {
    'fetchurlParams':function(search){
        var urlObj={}
        if(search)
          urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
        this.urlObj=urlObj;
        return urlObj; 
    },
    getJSON: function() {
        var self = this;
        var urlObj=this.fetchurlParams(window.location.search.substr(1));
        $.ajax({
            url: '/facesix/rest/site/portion/networkdevice/list?spid='+urlObj.spid,
            method: "get",
            success: function(repsonse) {
                var tree = repsonse;
                for (var i = 0; i < tree.length; i++) {
                    var type = tree[i].typefs;
                    var uid = tree[i].uid;
                    var status = tree[i].status;
                    var parent = tree[i].parent;
                    
                    if ((type.indexOf("server") != -1) || (parent.indexOf("ap") != -1)) {
                        deviceData[type] += 1;
                        var device = new Device(type, status, uid, tree[i].parent);
                        self.deviceTree[type + "" + deviceData[type]] = device;
                    } else {
                        deviceData[type] += 1;
                        var device = Device(type, status, uid, tree[i].parent, true)
                        self.childDevices.push(device);
                    }
                }
                deviceData['total'] = tree.length; 
                for(var key in self.deviceTree)
                    self.deviceTree[key].addChildren();
                var devices=deviceData['total']==1?"1 Device":deviceData['total']+" Devices";
                $(".device-section span").text(devices)
                addEvents();
                $("div[data-uid='"+urlObj.uid+"']").addClass("current");
            },
            error: function(error) {
                //console.log(error)
            }
        })
    },
    deviceTree: {},
    childDevices: [],
    addNode: function(type,uid,status,parent,id) {
        var network = {}
        if (finder == "true") {
          network['server'] = '<li class="deviceInfo" id="server-id-' + id +
        '"><a class="dashbrdLink" href="#"><div  data-type="Server" data-uid="'+uid+'" data-href="/facesix/web/site/portion/dashboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=server&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-cref="#" data-bref="#" data-sref="#" class="device-name"><label>' +
        '<i class="fa fa-2x fa-angle-down" aria-hidden="true"></i>' +
        '<img src="/facesix/static/qubercomm/images/networkconfig/icon/server_inactive.png" alt=""></label>' +
        '<span>SVR-' + uid + '</span><label class="connected device-status pull-right">' +
        '<span>' + status + '</span></label></div></a>' +
        '<ul class="child list-unstyled" id="server' + id + '-tree"></ul></li>';
        
        } else if(GatewayFinder == "true"){
       	 	network['server'] = '<li class="deviceInfo" id="server-id-' + id +
            '"><a class="dashbrdLink"><div data-status="'+status+'" data-type="Server" data-uid="'+uid+'" data-href="/facesix/web/site/portion/dashboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=server&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'&param=1" data-cref="#" data-bref="#" data-sref="#" class="device-name"><label>' +
            '<i class="fa fa-2x fa-angle-down" aria-hidden="true"></i>' +
            '<img src="/facesix/static/qubercomm/images/networkconfig/icon/server_inactive.png" alt=""></label>' +
            '<span>SVR-' + uid + '</span><label class="connected device-status pull-right">' +
            '<span>' + status + '</span></label></div></a>' +
            '<ul class="child list-unstyled" parent-id="'+uid+'" id="server' + id + '-tree"></ul></li>';
        } else {
            network['server'] = '<li class="deviceInfo" id="server-id-' + id +
            '"><a class="dashbrdLink"><div data-status="'+status+'" data-type="Server" data-uid="'+uid+'" data-href="/facesix/web/site/portion/dashboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=server&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-cref="#" data-bref="#" data-sref="#" class="device-name"><label>' +
            '<i class="fa fa-2x fa-angle-down" aria-hidden="true"></i>' +
            '<img src="/facesix/static/qubercomm/images/networkconfig/icon/server_inactive.png" alt=""></label>' +
            '<span>SVR-' + uid + '</span><label class="connected device-status pull-right">' +
            '<span>' + status + '</span></label></div></a>' +
            '<ul class="child list-unstyled" parent-id="'+uid+'" id="server' + id + '-tree"></ul></li>';            	 
        }

        if (finder == "true") {
            network['switch'] = '<li  class="deviceInfo" id="switch-id-' + id +
            '"><a class="dashbrdLink"><div data-status="'+status+'" data-type="Switch" data-uid="'+uid+'" data-href="#" data-cref="#" data-bref="#" data-sref="#" class="device-name"><label>' +
            '<img src="/facesix/static/qubercomm/images/networkconfig/icon/switch_inactive.png" alt=""></label>' +
            '<span>SW-' + uid + '</span><label class="connected device-status pull-right">' +
            '<span>' + status + '</span></label></div></a>' +
            '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="switch' + id + '-tree"></ul></li>';            	 
        } else {
            network['switch'] = '<li  class="deviceInfo" id="switch-id-' + id +
            '"><a class="dashbrdLink"><div data-status="'+status+'" data-type="Switch" data-uid="'+uid+'" data-href="/facesix/web/site/portion/swiboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=switch&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-cref="#" data-bref="#" data-sref="#" class="device-name"><label>' +
            '<img src="/facesix/static/qubercomm/images/networkconfig/icon/switch_inactive.png" alt=""></label>' +
            '<span>SW-' + uid + '</span><label class="connected device-status pull-right">' +
            '<span>' + status + '</span></label></div></a>' +
            '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="switch' + id + '-tree"></ul></li>';            	 
        }

        if (finder == "true") {
            network['ap'] = '<li  class="deviceInfo" id="ap-id-' + id + '"><a class="dashbrdLink" ><div data-status="'+status+'" data-type="Ap" data-uid="'+uid+'" class="device-name"><label><img src="/facesix/static/qubercomm/images/networkconfig/icon/ap_inactive.png" alt="">' +
            '</label><span>AP-' +uid + '</span><label class="connected device-status pull-right"><span>' + status + '</span>' +
            '</label></div></a>' + '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="ap' + id + '-tree"></ul></li>';            	 
        } else {
            network['ap'] = '<li  class="deviceInfo" id="ap-id-' + id + '"><a class="dashbrdLink"><div data-status="'+status+'" data-type="Ap" data-uid="'+uid+'" data-href="/facesix/web/site/portion/devboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=device&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-cref="/facesix/web/device/custconfig?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-bref="/facesix/web/finder/device/binary?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-sref="/facesix/scan?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" class="device-name"><label><img src="/facesix/static/qubercomm/images/networkconfig/icon/ap_inactive.png" alt="">' +
            '</label><span>AP-' +uid + '</span><label class="connected device-status pull-right"><span>' + status + '</span>' +
            '</label></div></a>' + '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="ap' + id + '-tree"></ul></li>';            	 
        }

    network['sensor'] = '<li  class="deviceInfo" id="sensor-id-' + id + '"><a class="dashbrdLink" href="#"><div data-type="Sensor" data-uid="'+uid+'" data-href="/facesix/web/finder/device/devboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=device&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-cref="/facesix/web/finder/device/configure?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-sref="/facesix/web/beacon/list?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-bref="/facesix/web/finder/device/binary?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'"  class="device-name"><label><img src="/facesix/static/qubercomm/images/networkconfig/icon/sensor_inactive.png" alt="">' +
        '</label><span>BLE-' + uid + '</span><label class="connected device-status pull-right ">' +
        '<span>' + status + '</span></label></div></a></li>';
             if(parent == "ble") 
                	$('#network-tree').append(network["sensor"]);
                 else
             $('#' + parent + '-tree').append(network[type]);
    }
}
networkTree.getJSON();
function addEvents(){
   $(".device-name").on("click",highlight)
}
function highlight(evt){
        evt.preventDefault();
        $(".device-name").removeClass("current")
        $(".deviceInfo a").attr("href","#");
        $(this).addClass("current")
        $(".powerBtn").attr("uid",$(this).attr("data-uid"))
        $(".powerBtn").attr("devtype",$(this).attr("data-type"))
        var href=$(this).attr("data-href");
        var bref=$(this).attr("data-bref");
        var cref=$(this).attr("data-cref");
        var sref=$(this).attr("data-sref");
        
        $(".dshbrdLink").attr("href",href);
        $(".binaryLink").attr("href",bref);
        $(".devcfgLink").attr("href",cref);
        $(".scanLink").attr("href",sref);
    }
        