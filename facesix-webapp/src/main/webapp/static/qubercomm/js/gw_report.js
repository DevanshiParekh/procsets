(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
 
 $(document).ready(function(){


	 $('#fileformat').change(function(){
		 var val = $(this).val();
		 $('#time').html('');
		 if(val == 'html'){
			 var options = '<option value="4h" selected>4 Hours</option><option value="6h">6 Hours</option><option value="8h">8 Hours</option><option value="12h">12 Hours</option>'; 
		 }else{
			 var options = '<option selected="" value="12h">12 Hours</option><option value="24h">24 Hours</option><option value="7d">7 Days</option><option value="15d">15 Days</option><option value="30d">1 Month</option>';
		 }
	 	$('#time').append(options);
	 });
	 
	var custid = location.search.split("&")[0].replace("?","").split("=")[1];
	console.log(custid);
	$.ajax({
		type : "GET",
		url : "/facesix/rest/beacon/trilaterationReports/venuelist?cid="+custid,
		dataType : "json",
		success : function(data) {
			//console.log(JSON.stringify(data));
			if(data.site.length > 0){
				$.each(data.site, function(i, obj) {
					//alert(obj.id + ":" + obj.name);
					var div_data = "<option value=" + obj.id + ">"+ obj.name + "</option>";
					$(div_data).appendTo('#venuename');
				});
			} else {
				var div_data = "<option value='' disabled selected>No venue available</option>";
				$(div_data).appendTo('#venuename');
				document.getElementById("but").disabled = true;
			}
			
			
			
		}
	});
});
 
$('#venuename').on('change',function(){
	 $("#smallLoadingFloor").addClass("smallLoader");
 $('#floorname').empty();
 $('#location').empty();
 
  var de = "<option value = 'all'>ALL</option>";
  $(de).appendTo('#floorname');
  $(de).appendTo('#location');
  var cid = location.search.split("&")[0].replace("?","").split("=")[1];
  var sid =  $('#venuename').val();
  var spid = $('#floorname').val();
  console.log("sid  is "+sid);
  if(sid != 'all') {
 
 	 $.ajax({
		type : "GET",
		url : "/facesix/rest/beacon/trilaterationReports/floorlist?cid="+cid+"&sid="+sid,
		dataType : "json",
		success : function(data) {
			 $("#smallLoadingFloor").removeClass("smallLoader");
			console.log(JSON.stringify(data));
			$.each(data.portion, function(i, obj) {
				//console.log(obj.id + ":" + obj.name);
				var div_data = "<option value=" + obj.id + ">"+ obj.name + "</option>";
				console.log("div_data is "+div_data)
				$(div_data).appendTo('#floorname');
			});
		}
	});
 }
  
});
 $('#floorname').on('change',function(){
 
 $("#smallLoading").addClass("smallLoader");
 $('#location').empty();
  var de = "<option value = 'all'>ALL</option>";
  $(de).appendTo('#location');
  var cid = location.search.split("&")[0].replace("?","").split("=")[1];
  var sid =  $('#venuename').val();
  var spid = $('#floorname').val();
  if(spid != 'all') {
 	
 	$.ajax({
		type : "GET",
		url : "/facesix/rest/gatewayreport/locationlist?cid="+cid+"&sid="+sid+"&spid="+spid,
		dataType : "json",
		success : function(data) {
			$("#smallLoading").removeClass("smallLoader");
			console.log("locationlist"+JSON.stringify(data));
			$.each(data.location, function(i, obj) {
				console.log(obj.name);
				var div_data = "<option value="+obj.id+">"+ obj.name + "</option>";
				console.log("div_data is "+div_data)
				$(div_data).appendTo('#location');
			});
		}
	});
	} else {
		$("#smallLoading").removeClass("smallLoader");
	}
 }); 
 

 
 $('#filtertype').on('change',function(){
	
	 var sid =  $('#venuename').val(); 
	$.ajax({
		type : "GET",
		url : "/facesix/rest/beacon/trilaterationReports/floorlist?cid="+cid+"&sid="+sid,
		dataType : "json",
		success : function(data) {
			console.log(JSON.stringify(data));
			$.each(data.portion, function(i, obj) {
				//console.log(obj.id + ":" + obj.name);
				var div_data = "<option value=" + obj.id + ">"+ obj.name + "</option>";
				console.log("div_data is "+div_data)
				$(div_data).appendTo('#floorname');
			});
		}
	});
	var ft = $('#filtertype').val();
	console.log("hey"+ft)
	$('#floorname').empty();
	$('#location').empty();
	var de = "<option value = 'all'>ALL</option>";
  	$(de).appendTo('#floorname');
  	$(de).appendTo('#location');
  	
  	if($('#fileformat').val() == 'html'){
  	  document.getElementById("time").value = "4h";
 	}else {
  	  document.getElementById("time").value = "12h";
 	}
	document.getElementById("floorname").disabled 	= true;	
	document.getElementById("location").disabled 	= true;
	document.getElementById("venuename").disabled 	= false;
	document.getElementById("time").disabled 		= false; 
	
	document.getElementById("floorname").value 		= "all";
	document.getElementById("venuename").value 		= $('#venuename option:nth-child(1)').val();
	document.getElementById("location").value 		= ""; 
				
	if (ft == "floor") {
			document.getElementById("floorname").disabled = false;
	} else if (ft == "venuename") {
			document.getElementById("floorname").disabled 	= false;
			document.getElementById("location").disabled  	= false;
			
			//document.getElementById("location").value 		= "all";
			document.getElementById("venuename").value = $('#venuename option:nth-child(1)').val();
	} else if (ft == "default") {
			document.getElementById("venuename").disabled 	= true;
			document.getElementById("floorname").disabled 	= true;
			//document.getElementById("location").value 		= "all";
			document.getElementById("default").value 		= "all";
	
	} else if (ft == "location") {
		document.getElementById("floorname").disabled 	= false;
		document.getElementById("location").disabled  	= false;
		
		document.getElementById("location").value 		= "all";
	} else if (ft == "devStatus") {
		document.getElementById("devStatus").disabled 	= false;
		document.getElementById("venuename").disabled 	= true;
		document.getElementById("time").disabled 		= true;
		
		document.getElementById("floorname").value = "";
		document.getElementById("devStatus").value = "all";
		document.getElementById("venuename").value = '';
	}else if(ft =="deviceInfo"){
		document.getElementById("floorname").disabled = false;
		document.getElementById("time").disabled 		= true;
	}
	});
 })();





function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

$(document).ready(function(){
	search = window.location.search.substr(1)
	var custid = location.search.split("&")[0].replace("?","").split("=")[1]; 
	 
	var selectedSPID = getParameterByName('sid');  
	console.log(selectedSPID);
	var sidActive = '';
	$.ajax({
		type : "GET",
		url : "/facesix/rest/beacon/trilaterationReports/venuelist?cid="+custid,
		dataType : "json",
		success : function(data) { 
			$.each(data.site, function(i, obj) {
				//alert(obj.id + ":" + obj.name);
				var selected = '';
				if(selectedSPID != '' && selectedSPID != undefined && selectedSPID == obj.id){
					selected = 'selected';
				}
				var div_data = "<option value=" + obj.id + " "+selected+">"+ obj.name + "</option>";
				$(div_data).appendTo('#venuenames');
				
			}); 
			VenueDashboard.init();
		}
	});  
	
	$('#venuenames').on('change',function(){
		 
		 var cid = getParameterByName('cid');
		 var sid =  $('#venuenames').val();
		 var spid = $('#floornames').val();
		 if(sid != 'all') {
			 var url = "/facesix/gwreports?cid="+cid+"&sid="+sid;
			 window.location = url;
		 }
		  
	});
	$('#floornames').on('change',function(){
		 $('#locations').empty();
		  var de = "<option value = 'all'>ALL</option>";
		  $(de).appendTo('#locations');
		  var cid = location.search.split("&")[0].replace("?","").split("=")[1];
		  var sid =  $('#venuenames').val();
		  var spid = $('#floornames').val();
		  if(spid != 'all') {
		 	
		 	$.ajax({
				type : "GET",
				url : "/facesix/rest/gatewayreport/locationlist?cid="+cid+"&sid="+sid+"&spid="+spid,
				dataType : "json",
				success : function(data) { 
					$.each(data.location, function(i, obj) { 
						var div_data = "<option value="+obj.id+">"+ obj.name + "</option>"; 
						$(div_data).appendTo('#locations');
					});
					
				}
			});
			}
	 });

	$('#reloadReport').on('click',function(){
		 
		 var cid = location.search.split("&")[0].replace("?","").split("=")[1];
		 var sid =  $('#venuenames').val();
		 var spid = $('#floornames').val();
		 if(sid != 'all') {
			 var url = "/facesix/gwreports?cid="+cid+"&sid="+sid+"&spid="+spid;
			 window.location = url;
		 }  
		 
	});
	
});
 
(function () {
	   search = window.location.search.substr(1)
	   urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	    var peerStats;  
	   var counterIncrement  = 0;
		var counterIncrement1 = 0;
		var counterIncrement2 = 0;
		var counterIncrement3 = 0;
		var timeChartDataLoaded = false;
		var timeChartDatachart;
		var timeChartData =  { 
		    "data1": [],
		    "data2": []
		};
	    VenueDashboard = {
	        timeoutCount: 10000,
	        beacontables: {
	            url: {
	            	//beaconClientsTable: '/facesix/rest/gatewayreport/deviceInfo?sid='+urlObj.sid
	            },
	            setTable: {
	            	beaconClientsTable: function (reload) { 
	            		var dataurl = VenueDashboard.beacontables.url.beaconClientsTable;
	            		if(reload == 'reload')
	            		{
	            			// dataurl= '/facesix/rest/gatewayreport/deviceInfo?sid='+urlObj.sid;
	            		}
		            	console.log(dataurl);
	                    $.ajax({
	                        url: dataurl,
	                        method: "get",
	                        success: function (result) {
	                           // result=result.cust_dev_list 
	                            console.log("result" + JSON.stringify(result))
	                            if (result && result.length) {
	                                var show_previous_button = false;
	                                var show_next_button = false;
	                                $.each(result, function (i, key) { 
	                                	key.index = i + 1;
	                                })
	                              //   console.log(result);
	                                VenueDashboard.activeoneClientsData = result;
	                                if (result.length > 50) {
	                                    var filteredData = result.slice(0, 50);
	                                    show_next_button = true;
	                                } else {
	                                    var filteredData = result;
	                                }  
	                                var source = $("#chartbox-beacon-template").html();
	                                var template = Handlebars.compile(source);
	                                var rendered = template({
	                                    "data": filteredData,
	                                    "current_page": 1,
	                                    "show_previous_button": show_previous_button,
	                                    "show_next_button": show_next_button,
	                                    "startIndex": 1
	                                });
	                                $('.table-chart-box').html(rendered);
	                                //$('table .aclPopup ').on("tap",aclMenu);                                
	                                
	                            }
	                            $(".loader_boxtwo").hide();
	                            
	                        },
	                        error: function (data) {
	                        	$(".loader_boxtwo").hide();
	                        },
	                        dataType: "json"

	                    });
	                }
	            }
	        },  
	        charts: {
	            urls: {
	                txRx: '/facesix/rest/site/portion/networkdevice/avg_tx_rx?sid='+urlObj.sid+"&cid="+urlObj.cid, //iii
	                netFlow: '/facesix/rest/site/portion/networkdevice/getpeers?sid='+urlObj.sid, //Todo Url has to be changed here
	                typeOfDevices: '/facesix/rest/site/portion/networkdevice/heatMapDeviceList?sid='+urlObj.sid,
	                typeOfTags: '/facesix/rest/site/portion/networkdevice/flraggr?sid='+urlObj.sid+"&cid="+urlObj.cid, // All Tags
	                FloorTraffic: '/facesix/rest/site/portion/networkdevice/venueagg?sid='+urlObj.sid, // Floor vs traffic 
	                devicesConnected: '/facesix/rest/site/portion/networkdevice/getpeers?sid='+urlObj.sid, //Todo Url has to be changed here
	            },
	              
	            setChart: {
	                txRx: function (initialData) {
	                	console.log(VenueDashboard.charts.urls.txRx);
	                	var link 	 = VenueDashboard.charts.urls.txRx;
	                	var filter = $('#filtertype').val();
	                	if(filter == "venue"){
									link: '/facesix/rest/site/portion/networkdevice/avg_tx_rx?sid='+urlObj.sid+"&cid="+urlObj.cid;
	                    	} else if (filter == "floor"){
								    link: '/facesix/rest/site/portion/networkdevice/avg_tx_rx?spid='+urlObj.spid+"&cid="+urlObj.cid;
	                    	} else {
	                    			link: '/facesix/rest/site/portion/networkdevice/avg_tx_rx?uid='+urlObj.uid+"&cid="+urlObj.cid;
	                    	}
	                    $.ajax({
	                    	     url : link,
	   	                        success: function (result) {
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
	            						/*txArr[i] = txArr[i]/100;
	            						rxArr[i] = rxArr[i]/100;*/

	            						var formatedTime = result[i].time;
	            						//var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
	            						//c_formatedTime = new Date (c_formatedTime);
	                                    timings.push(formatedTime);
	                                }
	                                VenueDashboard.charts.chartConfig.txRx.data.columns = [txArr, rxArr];
	                                VenueDashboard.charts.chartConfig.txRx.axis.x.categories = timings;
	                            }
	                                VenueDashboard.charts.getChart.txRx = c3.generate(VenueDashboard.charts.chartConfig.txRx);
	                           	 
	                        },
	                        error: function (data) { 
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
	                        },
	                        error: function (data) { 
	                        },
	                        dataType: "json"
	                    });
	                },
	                netFlow: function (initialData) {
	                    $.ajax({
	                        url: VenueDashboard.charts.urls.netFlow,
	                        success: function (result) { 
	                        	c = result.devicesConnected;
	        	                VenueDashboard.charts.chartConfig.netFlow.data.columns = [result.devicesConnected[1], 
									   result.devicesConnected[2], 
									   result.devicesConnected[3], 
									   result.devicesConnected[4],
									   result.devicesConnected[5],
									   result.devicesConnected[6]
									   ]
	        	                VenueDashboard.charts.getChart.netFlow = c3.generate(VenueDashboard.charts.chartConfig.netFlow);
	         	                        	
	                        },
	                        error: function (data) { 
	                        },
	                        dataType: "json"
	                    });	                	
	                		                	
		            },
                	
	                FloorTraffic: function (initialData) { 
	                	$.ajax({
	                        url: VenueDashboard.charts.urls.FloorTraffic,
	                        success: function (result) {
								var timings = [];
	                            var floor 	= ["floor"];
	                            var ulink 	= ["Downlink"];
	                            var dlink 	= ["Uplink"];
	                            //console.log(result);
	                            for (var i = 0; i < result.length; i++){    
	        						
									floor.push(result[i].Floor);
									
	    							ulink.push(result[i].Rx);
	    							dlink.push(result[i].Tx);        						           						
	                            }                        
	                        
	                            if (result) {
	                            	console.log(result);
	                                VenueDashboard.charts.chartConfig.FloorTraffic.data.columns = [floor, ulink, dlink];
	                                //VenueDashboard.charts.chartConfig.netFlow.data.x= timings;
	                                VenueDashboard.charts.getChart.FloorTraffic = c3.generate(VenueDashboard.charts.chartConfig.FloorTraffic);
	                            } 
	                        },
	                        error: function (data) { 
	                        },
	                        dataType: "json"
	                    });
	                	
	                },
	                typeOfDevices: function (initialData) {
	                	$.ajax({
	                        url: VenueDashboard.charts.urls.typeOfDevices,
	                        success: function (result) { 
	                                    
	                                  var deviceType = result.devType; 
	                                  var c = deviceType;
	                                   		
	                                  VenueDashboard.charts.chartConfig.typeOfDevices.data.columns = [c[0],c[1],c[2],c[3],c[4],c[5]]
	                                  VenueDashboard.charts.getChart.typeOfDevices = c3.generate(VenueDashboard.charts.chartConfig.typeOfDevices); 
	                                
	                                  timeChartData.data1.push(new Date(result.chartDetails[2]));
	                                   	timeChartData.data2.push(result.chartDetails[1]);
	                                   	  
	                                   	
	                                   	if(timeChartDataLoaded == false){
	                                   		timeChartDatachart = c3.generate({
	                                       		bindto: '#dd-chart10', 
	                                       	    data: {
	                                       	        x: 'x',
//	                                       	        xFormat: '%Y%m%d', // 'xFormat' can be used as custom format of 'x'
	                                       	     columns: [
	                                       	            ['x'].concat(timeChartData.data1),
	                                       	            ['Clients'].concat(timeChartData.data2), 
	                                       	        ]
	                                       	    },
	    	                                   	 padding: {
	    	                                         top: 30,
	    	                                         right: 30,
	    	                                         bottom: 30,
	    	                                         left: 60,
	    	                                     },
	                                       	    axis: {
	                                       	        x: {
	                                       	            type: 'timeseries',
	                                       	            tick: {
	                                       	            	fit: true,
	                                       	                format: '%m-%d-%Y \n %H:%M:%S'
	                                       	            }
	                                       	        },
	    	                                     	y: {
	    	                                     		min: 0,
	    	                                     		tick: {
	    	                                     			format: d3.format('d')
	    	                                     		}
	    	                                     	}	                                       	        
	                                       	    }
	                                       	});
	                                   		
	                                   		timeChartDataLoaded = true;
	                                   	}else{
	                                   		timeChartDatachart.load({
	                                   	        columns: [
	                                   	        	['x'].concat(timeChartData.data1),
	                                   	            ['Clients'].concat(timeChartData.data2), 
	                                   	        ]
	                                   	    });
	                                   	} 
	                        },
	                        error: function (data) {
	                            
	                        },
	                        dataType: "json"
	                    });
	                },
	                
	                typeOfTags: function (initialData,params) {
	                	var duration = params;
	                	var len		 = duration?duration.length:0;
	                	var link 	 = VenueDashboard.charts.urls.typeOfTags;
	                	
	                	if (len != 0 && duration.localeCompare("time=5") != 0) {
							link = "/facesix/rest/site/portion/networkdevice/flraggr?sid="+urlObj.sid+"&"+params;
						} else {
							link = "/facesix/rest/site/portion/networkdevice/flraggr?sid="+urlObj.sid+"&"+"time=120";
						}
	                	$.ajax({
	                        url:link,
	                        success: function (result) {
	                        	//console.log(result);
								var timings = [];
	                            var radio 	= ["radio"];
	                            var ulink 	= ["Downlink"];
	                            var dlink 	= ["Uplink"];						
	                            for (var i = 0; i < result.length; i++){   
	                                if (result[i].max_vap_rx_bytes < 0) {
	        							//console.log("Negative RX");
	        							//result[i].max_vap_rx_bytes = 0;
	        							result[i].max_vap_rx_bytes = Math.abs(result[i].max_vap_rx_bytes);
	        						}
	        						
	        						if (result[i].max_vap_tx_bytes < 0) {
	        							//console.log("Negative TX");
	        							//result[i].max_vap_tx_bytes = 0; 
	        							result[i].max_vap_tx_bytes = Math.abs(result[i].max_vap_tx_bytes);
	        						} 
	        						  
	                            	radio.push(result[i].Radio);
	    							ulink.push(Math.round(result[i].max_vap_rx_bytes/100));
	    							dlink.push(Math.round(result[i].max_vap_tx_bytes/100));   						           						
	                            }                         
	                        
	                            if (result) {

	                            	VenueDashboard.charts.chartConfig.typeOfTags.data.columns = [radio, ulink, dlink];
	                                //FloorDashboard.charts.chartConfig.netFlow.axis.x.categories = timings;
	                            	VenueDashboard.charts.getChart.typeOfTags = c3.generate(VenueDashboard.charts.chartConfig.typeOfTags);
	                            }
	                             
	                        },
	                        error: function (data) { 
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
	                            VenueDashboard.charts.chartConfig.devicesConnected.data.columns = [result.devicesConnected[1], 
	                            																   result.devicesConnected[2], 
	                            																   result.devicesConnected[3], 
	                            																   result.devicesConnected[4],
	                            																   result.devicesConnected[5],
	                            																   result.devicesConnected[6],
	                            																   result.devicesConnected[7]]
	                        	
	                        	//console.log (VenueDashboard.charts.chartConfig.devicesConnected.data.columns);
	                            VenueDashboard.charts.getChart.devicesConnected = c3.generate(VenueDashboard.charts.chartConfig.devicesConnected);
	                            VenueDashboard.charts.setChart.typeOfDevices(true); 
	                        },
	                        error: function (data) { 
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
	                        },
	                        error: function (data) { 
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
	                        },
	                        error: function (data) { 
	                        },
	                        dataType: "json"
	                    });
	                }
	            },
	            getChart: {},
	            chartConfig: {
	                txRx: {
	                    size: {
	                        height: 300,
	                    },
	                    bindto: '#fd_chart2',

	                    padding: {
	                        top: 15,
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
	                            Tx: 'spline',
	                            Rx: 'spline',


	                        },
	                        colors: {
	                            Tx: '#5cd293',
	                            Rx: '#1a78dd',

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
	                netFlow: {
	                    size: {
	                        height: 300,
	                    },
	                    bindto: '#vdChart1',
	                    padding: {
	                        top: 15,
	                        right: 25,
	                        bottom: 0,
	                        left: 50,
	                    },

	                    data: {
	                        columns: [
	                        ],
	                        colors: {
	                        },
	                        type: 'pie'
	                    },
	                    data: {
	                        columns: [
	                            ['IOS', 7],
	                            ['Mac', 12],
	                            ['Win', 83],
	                            ['Android', 83],
	                            ['Others', 83],
	                        ],
	                        colors: {
	                            IOS: '#f14e5a',
	                            Mac: '#f1f494',
	                            Win: '#79d58a',
	                            Android: '#85d1fb',
	                            Others: '#c278ed',
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
	                FloorTraffic: {
	                	size: {
	                		height: 300,
	                	},
	                	bindto: '#FloorTraffic',
	                	padding: {
	                		top: 15,
	                		right: 25,
	                		bottom: 0,
	                		left: 100,
	                	},

	                	data: {
	                    	x: 'floor',
	                        columns: [
				                ['floor'],
				                ['ulink'],
				                ['dlink']                        
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
	                devicesConnected: {
	                    size: {
	                        height: 300,
	                    },
	                    bindto: '#fd_chart4',
	                    padding: {
	                        top: 15,
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
	                        height: 300,
	                    },
	                    bindto: '#dd-chart5',
	                    padding: {
	                      	top: 15,
	                        right: 15,
	                        bottom: 0,
	                        left: 15,
	                    },
	                    data: { 
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
	                            	if (value == 1) { return value + ' Client';} else
	                                	return value + ' Clients';
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
	                
	                typeOfTags: {
	                    size: {
	                        height: 300,
	                    },
	                    bindto: '#tagTypes',
	                    padding: {
	                      	top: 15,
	                        right: 15,
	                        bottom: 0,
	                        left: 50,
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

	                }
	            }
	        },
	        init: function (params) {
	            var c3ChartList = [ 'typeOfDevices', 'devicesConnected', 'txRx', 'netFlow',  'typeOfTags', 'FloorTraffic'];
	            var that = this;
	            $.each(c3ChartList, function (key, val) {
	                that.charts.setChart[val](true,params?params:"");
	            });
	            
	            var tableList   = ['beaconClientsTable']
	            $.each(tableList, function (key, val) {
	                that.beacontables.setTable[val]();
	            });
	            

	        }

	    }
	})();

	window.currentDashboard=VenueDashboard;

	var row_limit = 50;
	$('body').on('click', '.chk-refreshTable', function () {
		VenueDashboard.beacontables.setTable.beaconClientsTable('reload');
	});

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
	    if (VenueDashboard.activeoneClientsData.length > current_page * row_limit) {
	        show_next_button = true;
	    }
	    var filteredData = VenueDashboard.activeoneClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
	    
	    var source = $("#chartbox-beacon-template").html();
	    var template = Handlebars.compile(source);
	    var rendered = template({
	        "data": filteredData,
	        "current_page": previous_page,
	        "show_previous_button": show_previous_button,
	        "show_next_button": show_next_button,
	        "startIndex": (previous_page * row_limit) - row_limit
	    });
	    
	    $('.table-chart-box').html(rendered);
	     
	    $('#tablelength').val(row_limit);
	    
	}); 

	$('body').on('click', ".acl-tablePreviousPage", function (e) {
	 
	    var show_previous_button = true;
	    var show_next_button = false;

	    var tableName = $(this).closest('span').attr("data-table-name"); 
	    var $tableBlock = $('#' + tableName);
	    var current_page = $tableBlock.attr('data-current-page');
	    current_page = parseInt(current_page);
	    previous_page = current_page - 1;
	    if (previous_page == 1) {
	        show_previous_button = false;
	    }
	    if (VenueDashboard.activeoneClientsData.length > previous_page * row_limit) {
	        show_next_button = true;
	    }
	    var filteredData = VenueDashboard.activeoneClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
	    var source = $("#chartbox-beacon-template").html();
	    var template = Handlebars.compile(source);
	    var rendered = template({
	        "data": filteredData,
	        "current_page": previous_page,
	        "show_previous_button": show_previous_button,
	        "show_next_button": show_next_button,
	        "startIndex": (previous_page * row_limit) - row_limit
	    });
	    
	    $('.table-chart-box').html(rendered);
	     
	    $('#tablelength').val(row_limit); 

	});
	var checkStatus = false; 
	$('body').on('click', ".acl-tableNextPage", function (e) {
	  
	    var show_previous_button = true;
	    var show_next_button = false;

	    var tableName = $(this).closest('span').attr("data-table-name");
	    var $tableBlock = $('#' + tableName);
	    var current_page = $tableBlock.attr('data-current-page');
	    current_page = parseInt(current_page);
	    next_page = current_page + 1
	      

	    if (VenueDashboard.activeoneClientsData.length > next_page * row_limit) {
	        show_next_button = true;
	    }

	    var filteredData = VenueDashboard.activeoneClientsData.slice(row_limit * current_page, row_limit * next_page);
	    var source = $("#chartbox-beacon-template").html();
	    var template = Handlebars.compile(source);
	    var rendered = template({
	        "data": filteredData,
	        "current_page": next_page,
	        "show_previous_button": show_previous_button,
	        "show_next_button": show_next_button,
	        "startIndex": row_limit * current_page
	    });
	    $('.table-chart-box').html(rendered); 
	    $('#tablelength').val(row_limit); 
	});  

  
 

/* Customized Report */
var defaultChartEnabled = true;
function timeOut(){ 
	if(defaultChartEnabled == true){ 
		VenueDashboard.charts.setChart.typeOfDevices();
		VenueDashboard.charts.setChart.devicesConnected(); 
		VenueDashboard.charts.setChart.txRx();
		VenueDashboard.charts.setChart.netFlow();
		VenueDashboard.charts.setChart.typeOfTags();
		VenueDashboard.charts.setChart.FloorTraffic();
		$('.loaderbox_camera').hide();
	} 
}

var timer = setInterval(function () {
	timeOut();
}, VenueDashboard.timeoutCount);

var ExportModule = {
	pxTomm: function(px) {
		return Math.floor(px / $('#my_mm').height());
	}
}

/* Filter Graph Function */
$('document').ready(function(){
	
	/* Getting Url Parameters */
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	
	$('#but').click(function(e){ 
		if($('#fileformat').val() == 'html'){
			$('.ui_charts').html('');
			clearInterval(timer);
			$('body').addClass('customizedReportEnabled');
			var time = $('#time').val(); 
			var filterType = $('#filtertype').val();
			var venuename = $('#venuename').val();
			var floorname = $('#floorname').val();
			var location = $('#location').val();  
			var cid = urlObj.cid;
			var options = {
				time: time, 
				filterType: filterType,
				venuename: venuename,
				floorname: floorname,
				location: location, 
				cid: cid
			};
			generateCP(options);
			e.preventDefault();
		}
		else{
			$('body').removeClass('customizedReportEnabled');
			$('#defaultGraph').show();
			defaultChartEnabled = true;
			$('.graphFilterType').removeClass('active');
			var timer = setInterval(function () {
			   	timeOut();
		   	}, VenueDashboard.timeoutCount);
		}
	});
});

(function($) {
    $.extend($.fn, {
        makeCssInline: function() {
            this.each(function(idx, el) {
                var style = el.style;
                var properties = [];
                for(var property in style) { 
                    if($(this).css(property)) {
                        properties.push(property + ':' + $(this).css(property));
                    }
                }
                this.style.cssText = properties.join(';');
                $(this).children().makeCssInline();
            });
        }
    }); 
    
}(jQuery)); 

/* Generate Screenshot */
function generate(){ 
	$('.loaderbox_camera').show();
	clearInterval(timer);  
	//$('svg').makeCssInline();
	$('svg').makeCssInline(); 
	var div = $("#printableArea")[0];
	var rect = div.getBoundingClientRect();
	
	var canvas = document.createElement("canvas");
	canvas.width = rect.width;
	canvas.height = rect.height;
	
	var ctx = canvas.getContext("2d");
	ctx.translate(-rect.left,-rect.top);
	
	html2canvas(div, {
	    canvas:canvas,
	    height:rect.height,
	    width:rect.width,
	    onrendered: function(canvas) {
	        var image = canvas.toDataURL("image/png");
	        if(defaultChartEnabled == true){
	        	timeOut();
		    	var timer = setInterval(function () {
		    		timeOut();
		    	}, VenueDashboard.timeoutCount);
	        } else {
	        	var time = $('#time').val(); 
				var filterType = $('#filtertype').val();
				var venuename = $('#venuename').val();
				var floorname = $('#floorname').val();
				var location = $('#location').val();  
				var cid = urlObj.cid;
				var options = {
					time: time, 
					filterType: filterType,
					venuename: venuename,
					floorname: floorname,
					location: location, 
					cid: cid
				};
				generateCP(options);
	        }
	    	var currentTime = new Date(); 
	    	var a = document.createElement('a'); 
	        a.href = image.replace("image/png", "image/octet-stream");
	        a.download = 'report_'+currentTime+'.png';
	        a.click();    
	    }
	}); 
  
}   



/* Customized Report */

function generateCP(options){  
	function pad_with_zeroes(number, length) {

	    var my_string = '' + number;
	    while (my_string.length < length) {
	        my_string = '0' + my_string;
	    }

	    return my_string;

	}
	
	$('.loaderbox_camera').show();
	clearInterval(timer);  		
	defaultChartEnabled = false;
	$('.graphFilterType').removeClass('active');
	$('#defaultGraph').hide();
	
	switch (options.filterType){
		case 'location':  
			
			if(options.location == 'all'){
				$('#location_cpu_graph_option, #location_memory_graph_option').hide();
			}else{
				$('#location_cpu_graph_option, #location_memory_graph_option').show();
			}
			$('#graph_location').addClass('active');
			
			
			var ajaxURL = '/facesix/rest/gatewayreport/gw_htmlCharts?time='+options.time+'&venuename='+options.venuename+'&floorname='+options.floorname+'&location='+options.location+'&cid='+options.cid+'&filtertype=location&fileformat=html';
			
			// var result = {"rxtx":[{"Tx":1234,"Rx":1234,"time":"2018-01-24 10:23:20.182"},{"Tx":1234,"Rx":1234,"time":"2018-01-24 10:23:20.182"}],"twoGfiveG":[{"fiveG":2,"twoG":6,"time":"2018-01-24 10:13:20.182"},{"fiveG":6,"twoG":2,"time":"2018-01-24 10:23:20.182"}],"activeClients":[{"time":"2018-01-24 10:11:20.182","count":123},{"time":"2018-01-24 10:23:20.186","count":123}],"cpu":[{"uid":"12:34:56:78:10","cpu":1234,"time":"2018-01-24 10:11:20.182"},{"uid":"12:34:56:78:11","cpu":1234,"time":"2018-01-24 10:11:26.182"}],"mem":[{"uid":"12:34:56:78:10","mem":1234,"time":"2018-01-24 10:11:20.182"},{"uid":"12:34:56:78:11","mem":1234,"time":"2018-01-24 10:11:27.182"}]};
			
			$.ajax({
				url: ajaxURL,
				method: "get",
				success: function (result) {  
					
					if(options.location != 'all'){
						/* Memory */
						var mem = result.mem; 
						if(mem.length > 0){
							var date = [];
							var data = [];
							var uid = '';
							mem.sort().reverse().forEach(function(e) {
								uid = e.uid;
								data.push(e.mem);
								date.push(e.time);
							});
							
							/* Memory Utilization */
							var chart = c3.generate({
								bindto: "#location_memory_utilazation",
								size: {
									height: 300,
								},
								padding: {
									top: 15,
									right: 30,
									bottom: 15,
									left: 60,
								},
								data: {
									x: 'x',
									xFormat: '%Y-%m-%d %H:%M:%S.%L', 
									columns: [
										['x'].concat(date), 
										[uid].concat(data)
									]
								}, 
								color: {
									pattern: ['#60b044', '#F97600', '#F6C600', '#60B044'],  
								}, tooltip: {
									contents: function (data, defaultTitleFormat, defaultValueFormat, color) { 
										var $$ = this, config = $$.config,
										titleFormat = config.tooltip_format_title || defaultTitleFormat,
										nameFormat = config.tooltip_format_name || function (name) { return name; },
										valueFormat = config.tooltip_format_value || defaultValueFormat,
										text, i, title, value;
										text = '<table class="c3-tooltip"><tbody>';
										for (i = 0; i < data.length; i++) {
											if (! (data[i] && (data[i].value || data[i].value === 0))) { continue; }  
											var tagids = data[i].id; 
											var jk = (data[i].index);
											var date = new Date(Date.parse(cpu[jk]["time"])).toUTCString();
											text += '<tr><th colspan="2">'+date+'</th></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>UID</td><td class="value">'+mem[jk]["uid"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Memory</td><td class="value">'+mem[jk]["mem"]+'</td></tr>';
											
										}
										text += '</tbody></table>';
										return text;
									},
								}, 
								zoom: {
									enabled: true
								}, 
								axis: {
									x: {
										type: 'timeseries', 
										tick: {
											 count: 20,
											 format: '%Y-%m-%d \n %H:%M:%S'
										}
									}
								}
							});
						}
						
						/* CPU */

						var cpu = result.cpu;
						if(cpu.length > 0){
							var date = [];
							var data = [];
							var uid = '';
							cpu.sort().reverse().forEach(function(e) {
								uid = e.uid;
								data.push(e.cpu);
								date.push(e.time);
							});
							
							/* Memory Utilization */
							var chart = c3.generate({
								bindto: "#location_cpu_utilazation",
								size: {
									height: 300,
								},
								padding: {
									top: 15,
									right: 30,
									bottom: 15,
									left: 60,
								},
								data: {
									x: 'x',
									xFormat: '%Y-%m-%d %H:%M:%S.%L', 
									columns: [
										['x'].concat(date), 
										[uid].concat(data)
									]
								}, 
								color: {
									pattern: ['#F97600', '#F6C600', '#60B044'],  
								}, tooltip: {
									contents: function (data, defaultTitleFormat, defaultValueFormat, color) { 
										var $$ = this, config = $$.config,
										titleFormat = config.tooltip_format_title || defaultTitleFormat,
										nameFormat = config.tooltip_format_name || function (name) { return name; },
										valueFormat = config.tooltip_format_value || defaultValueFormat,
										text, i, title, value;
										text = '<table class="c3-tooltip"><tbody>';
										for (i = 0; i < data.length; i++) {
											if (! (data[i] && (data[i].value || data[i].value === 0))) { continue; }  
											var tagids = data[i].id; 
											var jk = (data[i].index);
											var date = new Date(Date.parse(cpu[jk]["time"])).toUTCString();
											text += '<tr><th colspan="2">'+date+'</th></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>UID</td><td class="value">'+cpu[jk]["uid"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>CPU</td><td class="value">'+cpu[jk]["cpu"]+'</td></tr>';
											
										}
										text += '</tbody></table>';
										return text;
									},
								}, 
								zoom: {
									enabled: true
								}, 
								axis: {
									x: {
										type: 'timeseries', 
										tick: {
											 count: 20,
											 format: '%Y-%m-%d \n %H:%M:%S'
										}
									}
								}
							});
						}
					}
					

					 

					/* Tx Rx */
					var rxtx = result.rxtx; 
					if(rxtx.length > 0){
						var date = [];
						var tx = [];
						var rx = [];
						rxtx.forEach(function(e) {
							date.push(e.time); 
							tx.push(e.Tx);
							rx.push(e.Rx);
						}); 
						
						var charts = c3.generate({
							bindto: '#location_txrx', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 30,
								bottom: 15,
								left: 60,
							},
							data: {
								x: 'x', 
								columns: [
									['x'].concat(date), 
									['Tx'].concat(tx), 
									['Rx'].concat(rx), 
								],
								types: {
									Tx: 'spline',
									Rx: 'spline'
									// 'line', 'spline', 'step', 'area', 'area-step' are also available to stack
								},
								colors: {
									Tx: '#5cd293',
									Rx: '#1a78dd',

								},
								groups: [['Tx', 'Rx']]
							},
							legend:{
								item:{
									"onclick":function(id){
										charts.focus(id)
									}
								}
							 }, 
							point: {
								show: true
							},
							zoom: {
								enabled: true
							},
							tooltip: {
								show: true
							},
							axis: {
								x: {
									type: 'category',
									/*tick: {
										count: 10,
									},*/
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
						});
						$('.location_uplink_downlink').change(function(){
							var val = $(this).val();
							charts.focus(val)
						});
					}


					/* 2G vs 5G Active Clients */
					var clientDetails = result.clientDetails;
					if(clientDetails.length > 0){
						var chart;
						var chart1;
						var chart2;
						var k = 1;
						clientDetails.forEach(function(e) { 
							var data = [];  
							var data1 = [];  
							var data2 = [];  
							var date = [];
							var index = e.uid;
							var details = e.details;  
							details.forEach(function(e) { 
								if(e.twoG != null && e.twoG!= undefined){
									data.push(e.twoG);
								}
								if(e.fiveG != null && e.fiveG != undefined){
									data1.push(e.fiveG);
								}
								data2.push(e.count);
								date.push(e.time); 
							}); 
							
							if(k == 1){
								chart = c3.generate({
									bindto: "#location_2g",
									size: {
										height: 300,
									},
									padding: {
										top: 15,
										right: 30,
										bottom: 15,
										left: 60,
									},
									data: {
										x: 'x',
										xFormat: '%Y-%m-%d %H:%M:%S.%L', 
										columns: [
											['x'].concat(date), 
											[index].concat(data),   
										]
									}, 
									color: {
										// pattern: ['#f44336', '#e91e63', '#9c27b0'],  
									},
									tooltip: {
										format: {
											title: function (d) { return  new Date(Date.parse(d)).toUTCString(); },  
										}
									},
									zoom: {
										enabled: true
									},
									axis: {
										x: {
											type: 'timeseries',
											tick: {
												format: '%Y-%m-%d \n %H:%M:%S'
											}
										},
										y: {
				                     		min: 0,
				                     		tick: {
				                     			format: d3.format('d')
				                     		}
				                     	}  
									}
								});
								
								chart1 = c3.generate({
									bindto: "#location_5g",
									size: {
										height: 300,
									},
									padding: {
										top: 15,
										right: 30,
										bottom: 15,
										left: 60,
									},
									data: {
										x: 'x',
										xFormat: '%Y-%m-%d %H:%M:%S.%L', 
										columns: [
											['x'].concat(date), 
											[index].concat(data1),   
										]
									}, 
									color: {
										// pattern: ['#3f51b5', '#2196f3', '#03a9f4'],  
									},
									tooltip: {
										format: {
											title: function (d) { return  new Date(Date.parse(d)).toUTCString(); },  
										}
									},
									zoom: {
										enabled: true
									},
									axis: {
										x: {
											type: 'timeseries',
											tick: {
												format: '%Y-%m-%d \n %H:%M:%S'
											}
										},
										y: {
				                     		min: 0,
				                     		tick: {
				                     			format: d3.format('d')
				                     		}
				                     	}  
									}
								});
								
								/*chart2 = c3.generate({
									bindto: "#location_active_clients",
									size: {
										height: 300,
									},
									padding: {
										top: 15,
										right: 30,
										bottom: 15,
										left: 60,
									},
									data: {
										x: 'x',
										xFormat: '%Y-%m-%d %H:%M:%S.%L', 
										columns: [
											['x'].concat(date), 
											[index].concat(data2),   
										]
									}, 
									color: {
										// pattern: ['#009688', '#8bc34a', '#ffeb3b'],  
									},
									tooltip: {
										format: {
											title: function (d) { return  new Date(Date.parse(d)).toUTCString(); },  
										}
									},
									zoom: {
										enabled: true
									},
									axis: {
										x: {
											type: 'timeseries',
											tick: {
												format: '%Y-%m-%d \n %H:%M:%S'
											}
										}
									}
								});*/
								
								
							} else {
								chart.load({
									columns: [
										['x'].concat(date), 
										[index].concat(data1),   
									]
							    });
								chart1.load({
									columns: [
										['x'].concat(date), 
										[index].concat(data1),   
									]
							    });
								/*chart2.load({
									columns: [
										['x'].concat(date), 
										[index].concat(data2),   
									]
							    });*/
								
							}
							k = k + 1;
							  
						}); 
						
					} 



 
					$('.loaderbox_camera').hide();  
				},
				error: function (data) {
					$('.loaderbox_camera').hide();  
				},
				dataType: "json"

			}); 
			 
		break;
		
		case 'floor':  
			$('#graph_floor').addClass('active');
			
			var ajaxURL = '/facesix/rest/gatewayreport/gw_htmlCharts?time='+options.time+'&venuename='+options.venuename+'&floorname='+options.floorname+'&cid='+options.cid+'&filtertype=venue&fileformat=html';
			
			// var result = {"clientDetails":[{"uid":"40:a5:ef:8a:2e:a2","details":[{"count":20634,"time":"2018-01-29 06:18:28.129","fiveG":9134,"twoG":11500},{"count":20634,"time":"2018-01-29 06:20:28.129","fiveG":9134,"twoG":11500},{"count":20634,"time":"2018-01-29 06:22:28.129","fiveG":9134,"twoG":11500}]},{"uid":"40:a5:ef:8a:2a:a5","details":[{"count":20634,"time":"2018-01-29 07:18:28.129","fiveG":100,"twoG":200},{"count":20634,"time":"2018-01-29 07:20:28.129","fiveG":300,"twoG":400},{"count":20634,"time":"2018-01-29 07:22:28.129","fiveG":500,"twoG":600}]}],"rxtx":[{"Tx":2069430168,"Rx":228524879,"time":"2018-01-29 06:18:53.117"},{"Tx":2068990737,"Rx":228400659,"time":"2018-01-29 06:18:48.305"},{"Tx":2068983101,"Rx":228398874,"time":"2018-01-29 06:18:48.059"},{"Tx":2068091955,"Rx":228275735,"time":"2018-01-29 06:18:43.260"},{"Tx":2068090910,"Rx":228275305,"time":"2018-01-29 06:18:43.014"},{"Tx":694809272,"Rx":176095357,"time":"2018-01-29 06:18:53.364"},{"Tx":694394893,"Rx":176056576,"time":"2018-01-29 06:18:53.361"},{"Tx":690561723,"Rx":175903288,"time":"2018-01-29 06:18:48.306"},{"Tx":690478972,"Rx":175898530,"time":"2018-01-29 06:18:48.303"},{"Tx":687202808,"Rx":175751408,"time":"2018-01-29 06:18:43.261"}]};
		 
			$.ajax({
				url: ajaxURL,
				method: "get",
				success: function (result) {    
					/* Tx Rx */
					var rxtx = result.rxtx;
					console.log(rxtx);
					if(rxtx.length > 0){
						var date = [];
						var tx = [];
						var rx = [];
						rxtx.forEach(function(e) {
							date.push(e.time); 
							tx.push(e.Tx);
							rx.push(e.Rx);
						}); 
						
						var charts = c3.generate({
							bindto: '#floor_txrx', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 30,
								bottom: 15,
								left: 60,
							},
							data: {
								x: 'x', 
								columns: [
									['x'].concat(date), 
									['Tx'].concat(tx), 
									['Rx'].concat(rx), 
								],
								types: {
									Tx: 'spline',
									Rx: 'spline'
									// 'line', 'spline', 'step', 'area', 'area-step' are also available to stack
								},
								colors: {
									Tx: '#5cd293',
									Rx: '#1a78dd',

								},
								groups: [['Tx', 'Rx']]
							},
							legend:{
								item:{
									"onclick":function(id){
										charts.focus(id)
									}
								}
							 }, 
							point: {
								show: true
							},
							zoom: {
								enabled: true
							},
							tooltip: {
								show: true
							},
							axis: {
								x: {
									type: 'category',
									/*tick: {
										count: 10,
									},*/
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
						});
						$('.floor_uplink_downlink').change(function(){
							var val = $(this).val();
							charts.focus(val)
						});
					}


					/* 2G vs 5G Active Clients */
					var clientDetails = result.clientDetails;
					if(clientDetails.length > 0){
						var chart;
						var chart1;
						var chart2;
						var k = 1;
						clientDetails.forEach(function(e) { 
							var data = [];  
							var data1 = [];  
							var data2 = [];  
							var date = [];
							var index = e.uid;
							var details = e.details;  
							details.forEach(function(e) { 
								if(e.twoG != null && e.twoG!= undefined){
									data.push(e.twoG);
								}
								if(e.fiveG != null && e.fiveG != undefined){
									data1.push(e.fiveG);
								}
								data2.push(e.count);
								date.push(e.time); 
							}); 
							
							if(k == 1){
								chart = c3.generate({
									bindto: "#floor_2g",
									size: {
										height: 300,
									},
									padding: {
										top: 15,
										right: 30,
										bottom: 15,
										left: 60,
									},
									data: {
										x: 'x',
										xFormat: '%Y-%m-%d %H:%M:%S.%L', 
										columns: [
											['x'].concat(date), 
											[index].concat(data),   
										]
									}, 
									color: {
										// pattern: ['#f44336', '#e91e63', '#9c27b0'],  
									},
									tooltip: {
										format: {
											title: function (d) { return  new Date(Date.parse(d)).toUTCString(); },  
										}
									},
									zoom: {
										enabled: true
									},
									axis: {
										x: {
											type: 'timeseries',
											tick: {
												format: '%Y-%m-%d \n %H:%M:%S'
											}
										},
										y: {
				                     		min: 0,
				                     		tick: {
				                     			format: d3.format('d')
				                     		}
				                     	}  
									}
								});
								
								chart1 = c3.generate({
									bindto: "#floor_5g",
									size: {
										height: 300,
									},
									padding: {
										top: 15,
										right: 30,
										bottom: 15,
										left: 60,
									},
									data: {
										x: 'x',
										xFormat: '%Y-%m-%d %H:%M:%S.%L', 
										columns: [
											['x'].concat(date), 
											[index].concat(data1),   
										]
									}, 
									color: {
										// pattern: ['#3f51b5', '#2196f3', '#03a9f4'],  
									},
									tooltip: {
										format: {
											title: function (d) { return  new Date(Date.parse(d)).toUTCString(); },  
										}
									},
									zoom: {
										enabled: true
									},
									axis: {
										x: {
											type: 'timeseries',
											tick: {
												format: '%Y-%m-%d \n %H:%M:%S'
											}
										},
										y: {
				                     		min: 0,
				                     		tick: {
				                     			format: d3.format('d')
				                     		}
				                     	}  
									}
								});
								
								/*chart2 = c3.generate({
									bindto: "#floor_active_clients",
									size: {
										height: 300,
									},
									padding: {
										top: 15,
										right: 30,
										bottom: 15,
										left: 60,
									},
									data: {
										x: 'x',
										xFormat: '%Y-%m-%d %H:%M:%S.%L', 
										columns: [
											['x'].concat(date), 
											[index].concat(data2),   
										]
									}, 
									color: {
										// pattern: ['#009688', '#8bc34a', '#ffeb3b'],  
									},
									tooltip: {
										format: {
											title: function (d) { return  new Date(Date.parse(d)).toUTCString(); },  
										}
									},
									zoom: {
										enabled: true
									},
									axis: {
										x: {
											type: 'timeseries',
											tick: {
												format: '%Y-%m-%d \n %H:%M:%S'
											}
										}
									}
								});*/
								
								
							} else {
								chart.load({
									columns: [
										['x'].concat(date), 
										[index].concat(data1),   
									]
							    });
								chart1.load({
									columns: [
										['x'].concat(date), 
										[index].concat(data1),   
									]
							    });
								/*chart2.load({
									columns: [
										['x'].concat(date), 
										[index].concat(data2),   
									]
							    });*/
								
							}
							k = k + 1;
							  
						}); 
						
					}
					 
					  
					$('.loaderbox_camera').hide(); 
				 },
				error: function (data) {
					 // console.log(data); .
					$('.loaderbox_camera').hide();
				},
				dataType: "json"

			}); 
			 
			
		break;
		
		case 'venue':  
			$('#graph_venue').addClass('active');
			
			var ajaxURL = '/facesix/rest/gatewayreport/gw_htmlCharts?time='+options.time+'&venuename='+options.venuename+'&cid='+options.cid+'&filtertype=venue&fileformat=html';
			
			// var result = {"clientDetails":[{"uid":"40:a5:ef:8a:2e:a2","details":[{"count":50,"time":"2018-01-25 11:51:42.685","fiveG":24,"twoG":26},{"count":120,"time":"2018-01-25 12:51:42.685","fiveG":60,"twoG":80}]},{"uid":"40:a5:ef:8a:2e:a6","details":[{"count":500,"time":"2018-01-26 11:51:42.685","fiveG":600,"twoG":400},{"count":800,"time":"2018-01-27 11:51:42.685","fiveG":600,"twoG":800}]}],"rxtx":[{"Tx":-992771814,"Rx":-1966860747,"time":"2018-01-25 11:51:47.732"},{"Tx":-992879408,"Rx":-1966862287,"time":"2018-01-25 11:51:47.510"},{"Tx":-993506644,"Rx":-1966935132,"time":"2018-01-25 11:51:42.685"},{"Tx":-993506738,"Rx":-1966935198,"time":"2018-01-25 11:51:42.464"},{"Tx":-993732391,"Rx":-1967008430,"time":"2018-01-25 11:51:37.629"},{"Tx":2083763222,"Rx":2110388829,"time":"2018-01-25 11:51:47.752"},{"Tx":2083743822,"Rx":2110386490,"time":"2018-01-25 11:51:47.729"},{"Tx":2082363977,"Rx":2110140082,"time":"2018-01-25 11:51:42.702"},{"Tx":2082284652,"Rx":2110134187,"time":"2018-01-25 11:51:42.682"},{"Tx":2079965673,"Rx":2109943248,"time":"2018-01-25 11:51:37.656"}]};
			 
			$.ajax({
				url: ajaxURL,
				method: "get",
				success: function (result) {  
					/* Tx Rx */
					var rxtx = result.rxtx; 
					if(rxtx.length > 0){
						var date = [];
						var tx = [];
						var rx = [];
						rxtx.forEach(function(e) {
							date.push(e.time); 
							tx.push(e.Tx);
							rx.push(e.Rx);
						}); 
						
						var charts = c3.generate({
							bindto: '#venue_txrx', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 30,
								bottom: 15,
								left: 60,
							},
							data: {
								x: 'x', 
								columns: [
									['x'].concat(date), 
									['Tx'].concat(tx), 
									['Rx'].concat(rx), 
								],
								types: {
									Tx: 'spline',
									Rx: 'spline'
									// 'line', 'spline', 'step', 'area', 'area-step' are also available to stack
								},
								colors: {
									Tx: '#5cd293',
									Rx: '#1a78dd',

								},
								groups: [['Tx', 'Rx']]
							},
							legend:{
								item:{
									"onclick":function(id){
										charts.focus(id)
									}
								}
							 }, 
							point: {
								show: true
							},
							zoom: {
								enabled: true
							},
							tooltip: {
								show: true
							},
							axis: {
								x: {
									type: 'category',
									tick: {
										count: 10,
									},
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
						});
						$('.venue_uplink_downlink').change(function(){
							var val = $(this).val();
							charts.focus(val)
						});
					}


					/* 2G vs 5G Active Clients */
					var clientDetails = result.clientDetails;
					if(clientDetails.length > 0){
						var chart;
						var chart1;
						var chart2;
						var k = 1;
						clientDetails.forEach(function(e) { 
							var data = [];  
							var data1 = [];  
							var data2 = [];  
							var date = [];
							var index = e.uid;
							var details = e.details;  
							details.forEach(function(e) { 
								if(e.twoG != null && e.twoG!= undefined){
									data.push(e.twoG);
								}
								if(e.fiveG != null && e.fiveG != undefined){
									data1.push(e.fiveG);
								}
								data2.push(e.count);
								date.push(e.time); 
							}); 
							
							if(k == 1){
								chart = c3.generate({
									bindto: "#venue_2g",
									size: {
										height: 300,
									},
									padding: {
										top: 15,
										right: 30,
										bottom: 15,
										left: 60,
									},
									data: {
										x: 'x',
										xFormat: '%Y-%m-%d %H:%M:%S.%L', 
										columns: [
											['x'].concat(date), 
											[index].concat(data),   
										]
									}, 
									color: {
										// pattern: ['#f44336', '#e91e63', '#9c27b0'],  
									},
									tooltip: {
										format: {
											title: function (d) { return  new Date(Date.parse(d)).toUTCString(); },  
										}
									},
									zoom: {
										enabled: true
									},
									axis: {
										x: {
											type: 'timeseries',
											tick: {
												format: '%Y-%m-%d \n %H:%M:%S'
											}
										},
										y: {
				                     		min: 0,
				                     		tick: {
				                     			format: d3.format('d')
				                     		}
				                     	}  
									}
								});
								
								chart1 = c3.generate({
									bindto: "#venue_5g",
									size: {
										height: 300,
									},
									padding: {
										top: 15,
										right: 30,
										bottom: 15,
										left: 60,
									},
									data: {
										x: 'x',
										xFormat: '%Y-%m-%d %H:%M:%S.%L', 
										columns: [
											['x'].concat(date), 
											[index].concat(data1),   
										]
									}, 
									color: {
										// pattern: ['#3f51b5', '#2196f3', '#03a9f4'],  
									},
									tooltip: {
										format: {
											title: function (d) { return  new Date(Date.parse(d)).toUTCString(); },  
										}
									},
									zoom: {
										enabled: true
									},
									axis: {
										x: {
											type: 'timeseries',
											tick: {
												format: '%Y-%m-%d \n %H:%M:%S'
											}
										},
										y: {
				                     		min: 0,
				                     		tick: {
				                     			format: d3.format('d')
				                     		}
				                     	}  
									}
								});
								
								/*chart2 = c3.generate({
									bindto: "#venue_active_clients",
									size: {
										height: 300,
									},
									padding: {
										top: 15,
										right: 30,
										bottom: 15,
										left: 60,
									},
									data: {
										x: 'x',
										xFormat: '%Y-%m-%d %H:%M:%S.%L', 
										columns: [
											['x'].concat(date), 
											[index].concat(data2),   
										]
									}, 
									color: {
										// pattern: ['#009688', '#8bc34a', '#ffeb3b'],  
									},
									tooltip: {
										format: {
											title: function (d) { return  new Date(Date.parse(d)).toUTCString(); },  
										}
									},
									zoom: {
										enabled: true
									},
									axis: {
										x: {
											type: 'timeseries',
											tick: {
												format: '%Y-%m-%d \n %H:%M:%S'
											}
										}
									}
								});
								*/
								
							} else {
								chart.load({
									columns: [
										['x'].concat(date), 
										[index].concat(data1),   
									]
							    });
								chart1.load({
									columns: [
										['x'].concat(date), 
										[index].concat(data1),   
									]
							    });
								/*chart2.load({
									columns: [
										['x'].concat(date), 
										[index].concat(data2),   
									]
							    });*/
								
							}
							k = k + 1;
							  
						}); 
						
					}



					 
					
					$('.loaderbox_camera').hide(); 
				},
				error: function (data) {
					 // console.log(data); .
					$('.loaderbox_camera').hide();
				},
				dataType: "json"

			}); 
			 
			
		break;
		 
		
		default:
			
		break;
	}
}