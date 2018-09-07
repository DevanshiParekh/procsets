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
	$('#fileformat').change(function(){
		var val = $(this).val();
		$('#time').html('');
		if(val == 'html'){
			var options = '<option value="4h" selected>4 Hours</option><option value="6h">6 Hours</option><option value="8h">8 Hours</option><option value="12h">12 Hours</option>'; 
		}else{
			var options = '<option  selected="" value="12h">12 Hours</option><option value="24h">24 Hours</option><option value="7d">7 Days</option><option value="15d">15 Days</option><option value="30d">1 Month</option>';
		}
		$('#time').append(options);
	});
	search = window.location.search.substr(1)
	var custid = location.search.split("&")[0].replace("?","").split("=")[1]; 
	 
	var selectedSPID = getParameterByName('sid');   
	var sidActive = '';
	$.ajax({
		type : "GET",
		url : "/facesix/rest/beacon/trilaterationReports/venuelist?cid="+custid,
		dataType : "json",
		success : function(data) { 
			if(data.site.length > 0){
				$.each(data.site, function(i, obj) {
					//alert(obj.id + ":" + obj.name);
					var selected = '';
					if(selectedSPID != '' && selectedSPID != undefined && selectedSPID == obj.id){
						selected = 'selected';
					}
					var div_data = "<option value=" + obj.id + " "+selected+"  >"+ obj.name + "</option>";
					$(div_data).appendTo('#venuenames');
					
				}); 
			}else{
				var div_data = "<option value disabled selected>No venue available</option>";
				$(div_data).appendTo('#venuenames');
				document.getElementById("filtertype").disabled 	= true;
				document.getElementById("but").disabled 	= true;
			}
			
			VenueDashboard.init();
		}
	});  
	
	$('#venuenames').on('change',function(){
		 
		 var cid = getParameterByName('cid');
		 var sid =  $('#venuenames').val();
		 var spid = $('#floornames').val();
		 if(sid != 'all') {
			 var url = "/facesix/web/beacon/reports?cid="+cid+"&sid="+sid;
			 window.location = url;
		 }  
		 
		/*
		 $('#floornames').empty();
		 $('#locations').empty();
		 
		  var de = "<option value = 'all'>ALL</option>";
		  $(de).appendTo('#floornames');
		  $(de).appendTo('#locations');
		  var cid = location.search.split("&")[0].replace("?","").split("=")[1];
		  var sid =  $('#venuenames').val();
		  var spid = $('#floornames').val(); 
		  if(sid != 'all') {
		 
		 	 $.ajax({
				type : "GET",
				url : "/facesix/rest/beacon/trilaterationReports/floorlist?cid="+cid+"&sid="+sid,
				dataType : "json",
				success : function(data) { 
					$.each(data.portion, function(i, obj) { 
						var div_data = "<option value=" + obj.id + ">"+ obj.name + "</option>"; 
						$(div_data).appendTo('#floornames');
					});
				}
			});
		 } */
		  
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
				url : "/facesix/rest/beacon/trilaterationReports/locationlist?cid="+cid+"&sid="+sid+"&spid="+spid,
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
			 var url = "/facesix/web/beacon/reports?cid="+cid+"&sid="+sid+"&spid="+spid;
			 window.location = url;
		 }  
		 
	});
	
});
 
 


(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
 
 $(document).ready(function(){

	var custid = location.search.split("&")[0].replace("?","").split("=")[1]; 
	$.ajax({
		type : "GET",
		url : "/facesix/rest/beacon/trilaterationReports/venuelist?cid="+custid,
		dataType : "json",
		success : function(data) { 
			if(data.site.length > 0){
				$.each(data.site, function(i, obj) {
					//alert(obj.id + ":" + obj.name);
					var div_data = "<option value=" + obj.id + " >"+ obj.name + "</option>";
					$(div_data).appendTo('#venuename'); 
				});
			}else{
				var div_data = "<option value disabled selected>No venue available</option>";
				$(div_data).appendTo('#venuename');
				document.getElementById("filtertype").disabled 	= true;
				document.getElementById("but").disabled 	= true;
			}
			
		}
	});
});
 
 $('#venuename').on('change',function(){
 
 $("#smallLoadingFloor").addClass("smallLoader");
 $(".venueOk").css({"margin-top":"47px"});

 $('#floorname').empty();
 $('#location').empty();
 
  var de = "<option value = 'all'>ALL</option>";
  $(de).appendTo('#floorname');
  $(de).appendTo('#location');
  var cid = location.search.split("&")[0].replace("?","").split("=")[1];
  var sid =  $('#venuename').val();
  var spid = $('#floorname').val(); 
  if(sid != 'all') {
 
 	 $.ajax({
		type : "GET",
		url : "/facesix/rest/beacon/trilaterationReports/floorlist?cid="+cid+"&sid="+sid,
		dataType : "json",
		success : function(data) { 
			$("#smallLoadingFloor").removeClass("smallLoader");
			 $(".venueOk").css({"margin-top":"67px"});
			$.each(data.portion, function(i, obj) { 
				var div_data = "<option value=" + obj.id + ">"+ obj.name + "</option>"; 
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
		url : "/facesix/rest/beacon/trilaterationReports/locationlist?cid="+cid+"&sid="+sid+"&spid="+spid,
		dataType : "json",
		success : function(data) { 
			$("#smallLoading").removeClass("smallLoader");
			$.each(data.location, function(i, obj) { 
				var div_data = "<option value="+obj.id+">"+ obj.name + "</option>"; 
				$(div_data).appendTo('#location');
			});
		}
	});
	} else {
		 $("#smallLoading").removeClass("smallLoader");
	 }
 }); 
 
 $('#generateReport').on('click',function(){
 
	 var cid = location.search.split("&")[0].replace("?","").split("=")[1];
 	 var sid =  $('#venuename').val();
 	 var spid = $('#floorname').val();
 	 var reporttype =  $('#reporttype').val();
 	 var time =  $('#time').val();
 	 var tagType =  $('#tagType').val();
 	 var tagname =  $('#tagname').val();
 
 	
 	 $.ajax({
		type : "GET",
		url : "/facesix/rest/beacon/trilaterationReports/pdf?cid="+cid+"&sid="+sid+"&spid="+spid+"&reporttype="+reporttype+"&time="+time+"&tagtype="+tagType+"&tagname="+tagname,
		data: inputxml,
        contentType: "application/xml; charset=utf-8",
        success: function(data)
        {
        	var blob =  new Blob([data], { type: response.headers['content-type'] } );
            window.URL.createObjectURL(blob);
        }
	});
 	 
 });
 
 $('#filtertype').on('change',function(){
	
	var ft = $('#filtertype').val();
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
	document.getElementById("tagType").disabled 	= true;
	document.getElementById("tagname").disabled 	= true; 
	document.getElementById("venuename").disabled 	= false;
	document.getElementById("time").disabled 		= false;
	document.getElementById("reporttype").disabled 	= false;
	
	document.getElementById("floorname").value 		= "all";
	document.getElementById("location").value 		= "";
	document.getElementById("tagType").value 		= "";
	document.getElementById("tagname").value 		= ""; 
	document.getElementById("venuename").value 		= $('#venuename option:nth-child(1)').val();
	document.getElementById("reporttype").value 	= "all";
	
	$('#tagname').trigger("chosen:updated");
	$('#tagType').trigger("chosen:updated");
	console.log("ft" + ft)
	if (ft == "floor") {
			document.getElementById("floorname").disabled = false;
			$('#venuename').trigger('change');
			
	} else if (ft == "location") {
			document.getElementById("floorname").disabled 	= false;
			document.getElementById("location").disabled  	= false;
			
			document.getElementById("location").value 		= "all";
			$('#venuename').trigger('change');
	} else if (ft == "default") {
			document.getElementById("venuename").disabled 	= true;
			
			document.getElementById("location").value 		= "all";
			document.getElementById("tagType").value 		= "";
			document.getElementById("tagname").value 		= "";
			document.getElementById("tagstatus").value 		= "all";
	} else if (ft == "tagType") {
			document.getElementById("tagType").disabled 	= false;
			document.getElementById("venuename").disabled 	= true;
			document.getElementById("time").disabled 		= false;
			document.getElementById("reporttype").disabled 	= true;

			
			document.getElementById("floorname").value 		= "";
			document.getElementById("venuename").value 		= "";			
	} else if (ft == "tagname") {
			document.getElementById("tagname").disabled 	= false;
			document.getElementById("time").disabled 		= false;
			document.getElementById("reporttype").disabled 	= true;
			document.getElementById("floorname").disabled 	= false;
			document.getElementById("location").disabled  	= false;
			
			document.getElementById("location").value 		= "all";	
			$('#venuename').trigger('change');
			/*document.getElementById("floorname").value 		= "";
			document.getElementById("venuename").value 		= "";*/
	} else if (ft == "tagstatus") {
			document.getElementById("tagstatus").disabled 	= false;
			document.getElementById("venuename").disabled 	= true;
			document.getElementById("time").disabled 		= true;
			
			document.getElementById("floorname").value = "";
			document.getElementById("tagstatus").value = "all";
			document.getElementById("venuename").value = "";
	} else if(ft =="deviceInfo"){
		document.getElementById("floorname").disabled = false;
		document.getElementById("time").disabled 		= true;
		document.getElementById("reporttype").disabled 	= true;
		$('#venuename').trigger('change');
	}
	
});
	
 })();

var checkedValues = [];
function selectChild(e){
	 
	var target = $(e).attr('data-children'); 
	var debugflag = 'false';
	if($(e).prop('checked') === true)
	{ 
		$.each(DeviceACL.activeoneClientsData, function(index, optionValue) {   
			checkedValues.push(optionValue.macaddr); 
		});  
		debugflag = 'true';
		
	}else{
		checkedValues = [];
		$('.'+target).prop('checked', false);
	}
	
	$.each(checkedValues, function(index, optionValue) {   
		$('.'+target+'[name="'+optionValue+'"]').prop('checked', true);
	});  
	
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
	uniqueArray(checkedValues); 
		
	if(DeviceACL.activeoneClientsData.length <= checkedValues.length){
		debug_flag = 'true'; 
	}
	if((DeviceACL.activeoneClientsData.length <= checkedValues.length) || (checkedValues.length == 0)){ 
		var result = $.ajax({
			url: '/facesix/rest/beacon/debugByTag?cid='+client_id+'&debugflag='+debugflag,
			type : "POST",
			enctype : 'multipart/form-data',
			processData : false,
			contentType : false,
			success : function(result) { 
			},
			error : function(result) { 
			}
		});
	}
	
} 


(function () {
   search = window.location.search.substr(1)
   urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
    var peerStats;  
    VenueDashboard = {
        timeoutCount: 10000,
        beacontables: {
            url: {
            	//beaconClientsTable: '/facesix/rest/beacon/trilaterationReports/deviceInfo?sid='+urlObj.sid
            },
            setTable: {
            	beaconClientsTable: function (reload) {
            		var dataurl = VenueDashboard.beacontables.url.beaconClientsTable;
            		if(reload == 'reload')
            		{
            			// dataurl= '/facesix/rest/beacon/trilaterationReports/deviceInfo?sid='+urlObj.sid;
            		}
                    $.ajax({
                        url: dataurl,
                        method: "get",
                        success: function (result) {
                            //result=result.checkedout 
                            console.log("data" + JSON.stringify(result))
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
                txRx: '/facesix/rest/beacon/ble/networkdevice/rxtx?sid='+urlObj.sid,
                netFlow: '/facesix/rest/beacon/ble/networkdevice/venueagg?sid='+urlObj.sid+"&cid="+urlObj.cid,
                typeOfDevices: '/facesix/rest/beacon/ble/networkdevice/venue/connectedTagType?sid='+urlObj.sid+"&cid="+urlObj.cid,
                typeOfTags: '/facesix/rest/beacon/ble/networkdevice/alltagstatus?sid='+urlObj.sid+"&cid="+urlObj.cid, // All Tags
                FloorTraffic: '/facesix/rest/beacon/ble/networkdevice/venue/agg?sid='+urlObj.sid+"&cid="+urlObj.cid, // Floor vs traffic 
               // devicesConnected: '/facesix/rest/beacon/ble/networkdevice/gettags?sid='+urlObj.sid+"&cid="+urlObj.cid, //Todo Url has to be changed here
                devicesConnected: '/facesix/rest/beacon/ble/networkdevice/finderScatterChart?sid='+urlObj.sid+"&cid="+urlObj.cid,
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
                                    timings.push(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
                                }
                                var txrxVal = $( ".txrxswap option:selected" ).val();
                                if(txrxVal == "Receiver Venue Downlink/Uplink Speed"){
                                	VenueDashboard.charts.chartConfig.txRx.data.columns = [txArr, rxArr];
                                    VenueDashboard.charts.chartConfig.txRx.axis.x.categories = timings;
                                }
                                
                            }
                            if(txrxVal == "Receiver Venue Downlink/Uplink Speed"){
                                VenueDashboard.charts.getChart.txRx = c3.generate(VenueDashboard.charts.chartConfig.txRx);
                            }
                                /*setTimeout(function () {
                             		VenueDashboard.charts.setChart.txRx();
                            	}, VenueDashboard.timeoutCount);*/
                        },
                        error: function (data) {
                            /*setTimeout(function () {
                              VenueDashboard.charts.setChart.txRx();
                            }, VenueDashboard.timeoutCount);*/
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
                            /*setTimeout(function () {
                              VenueDashboard.charts.setChart.activeConnections();
                            }, VenueDashboard.timeoutCount);*/
                        },
                        error: function (data) {
                            /*setTimeout(function () {
                              VenueDashboard.charts.setChart.activeConnections();
                            }, VenueDashboard.timeoutCount);*/
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
                              
                            
                            for (var i = 0; i < result.length; i++){   
								floor.push(result[i].Status);
								
    							ulink.push(result[i].Tags);
      						           						
                            }                        
                        
                            if (result) {

                                VenueDashboard.charts.chartConfig.netFlow.data.columns = [floor, ulink ];
                                //VenueDashboard.charts.chartConfig.netFlow.data.x= timings;
                                VenueDashboard.charts.getChart.netFlow = c3.generate(VenueDashboard.charts.chartConfig.netFlow);
                            }
                            /*setTimeout(function () {
                              VenueDashboard.charts.setChart.netFlow();
                            }, VenueDashboard.timeoutCount);*/
                        },
                        error: function (data) {
                            /*setTimeout(function () {
                              VenueDashboard.charts.setChart.netFlow();
                            }, VenueDashboard.timeoutCount);*/
                        },
                        dataType: "json"
                    });
                },

                FloorTraffic: function (initialData) {
                	$.ajax({
                		url: VenueDashboard.charts.urls.FloorTraffic,
                		success: function (result) {
                			 
                			
                			var timings = [];
                			var floor 	= ["Status"];
                			var actag 	= ["activeTags"];
                			var idtag 	= ["idleTags"];
                			var intag 	= ["inactTags"];  
                			for (var i = 0; i < result.length; i++){   
                				floor.push(result[i].Status);
                				actag.push(result[i].activeTags);
                				idtag.push(result[i].idleTags);
                				intag.push(result[i].inactTags);
                			}
                		
                			if (result) {

                				VenueDashboard.charts.chartConfig.FloorTraffic.data.columns = [floor, actag, idtag,  intag];
                				VenueDashboard.charts.getChart.FloorTraffic = c3.generate(VenueDashboard.charts.chartConfig.FloorTraffic);
                			}
                			/*setTimeout(function () {
                			  VenueDashboard.charts.setChart.FloorTraffic();
                			}, VenueDashboard.timeoutCount);*/
                		},
                		error: function (data) {
                			/*setTimeout(function () {
                			  VenueDashboard.charts.setChart.FloorTraffic();
                			}, VenueDashboard.timeoutCount);*/
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
                         	

                             /*setTimeout(function () {
                             	VenueDashboard.charts.setChart.typeOfDevices();
                             }, VenueDashboard.timeoutCount);*/
                             
                         },
                         error: function (data) { 
                            /*setTimeout(function () {
                         	   VenueDashboard.charts.setChart.typeOfDevices();
                            }, VenueDashboard.timeoutCount);*/
                            
                         },
                         dataType: "json"
                     });
                },
                
                typeOfTags: function (initialData) {
               	 $.ajax({
                        url: VenueDashboard.charts.urls.typeOfTags,
                        success: function (result) {  
                            var result=result.tagstatus; 
                            var columns=[];
                            var names={};
                            var colors={},colorMap={
                                'enabled':"#6baa01",
                                'disabled':'#cccccc'
                            }
                            
                            for(var i = 0; i< result.length; i++){
                                
                            }   
                            

                            var chart = c3.generate({
                                data: {
                                    columns: [
                                        ['Active', result[0][1]],
                                        ['Idle', result[1][1]],
                                        ['Inactive', result[2][1]],
                                        ['Total', result[3][1]],
                                    ],
                                    type : 'donut', 
                                },
                                donut: {
                                    title: "Iris Petal Width"
                                },
                                size: {
                                    height: 300,
                                },
                                bindto: '#tagTypes',
                                padding: {
                                  	top: 15,
                                    right: 15,
                                    bottom: 0,
                                    left: 15,
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
                            });
                             
                           /* VenueDashboard.charts.chartConfig.typeOfTags.data.columns = columns;
                            VenueDashboard.charts.chartConfig.typeOfTags.data.names   = names;
                             VenueDashboard.charts.chartConfig.typeOfTags.data.colors  = colors;
                        	 VenueDashboard.charts.getChart.typeOfTags = c3.generate(VenueDashboard.charts.chartConfig.typeOfTags);
                            if (initialData) {
                            	VenueDashboard.charts.getChart.typeOfTags = c3.generate(VenueDashboard.charts.chartConfig.typeOfTags);
                            } else {
                            	VenueDashboard.charts.getChart.typeOfTags.load({ "columns": VenueDashboard.charts.chartConfig.typeOfTags.data.columns,'colors': VenueDashboard.charts.chartConfig.typeOfTags.data.colors});
                            }                        	
                        	*/

                            /*setTimeout(function () {
                            	VenueDashboard.charts.setChart.typeOfTags();
                            }, VenueDashboard.timeoutCount);*/
                            
                        },
                        error: function (data) { 
                           /*setTimeout(function () {
                        	   VenueDashboard.charts.setChart.typeOfTags();
                           }, VenueDashboard.timeoutCount);*/
                           
                        },
                        dataType: "json"
                    });
               },
               
                devicesConnected: function (initialData) {
                    $.ajax({
                        url: VenueDashboard.charts.urls.devicesConnected,
                        success: function (result) { 
                        	console.log(VenueDashboard.charts.urls.devicesConnected);
                        	var cids = getParameterByName('cid');   
                        	 stateCount = result;
                        	 var floornames = stateCount.floornames; 
                        	 var lengthofY = floornames.length;
                        	 var activeDataX = [];
                        	 var activeDataY = [];

                        	  
                        	 var activeClients = stateCount['active']['floors'];
                        	 if(activeClients != undefined){
                        	 	if(activeClients.length > 0){
                        	 		activeClients.forEach(function(e) {
                        	 			activeDataX.push(50);
                        	 			var locationname = e.floorname;
                        	 			floornames.forEach(function(e, index) { 
                        	 				if(e.toLowerCase() == locationname.toLowerCase()){
                        	 					activeDataY.push(index+1); 
                        	 				}
                        	 			});
                        	 			
                        	 		});
                        	 	}
                        	 } 
                        	  
                        	 var chart = c3.generate({
                        	 	bindto: '#fd_chart4',
                        	 	size: {
                        	 		height: 300,
                        	 	},
                        	 	padding: {
                        	 		top: 15,
                        	 		right: 15,
                        	 		bottom: 0,
                        	 		left: 60,
                        	 	},
                        	 	data: {
                        	 		
                        	 		x: 'TagStatus',  
                        	 		columns: [ 
                        	 			['TagStatus'].concat(activeDataX),
                        	 			['Active'].concat(activeDataY),
                        	 		], 
                        	 		colors: {
                        	 			'Active': function(d) { return d.value < 0 ? '#eef2f6' : '#6da900'; },
                        	 			'Inactive': function(d) { return d.value < 0 ? '#eef2f6' : '#a90000'; },
                        	 			'Idle': function(d) { return d.value < 0 ? '#eef2f6' : '#f2700d'; }
                        	 		},
                        	 		onclick: function () { 
                        	 			console.log(arguments); 
                        	 		},
                        	 		type: 'scatter',
                        	 		labels: true,
                        	 	},  
                        	 	zoom: {
                        	 		enabled: true
                        	 	},

                        	 	point: {
                        	 	  r: 6,
                        	 	  focus: {
                        	 		expand: {
                        	 		  r: 15
                        	 		}
                        	 	  }
                        	 	},
                        	 	tooltip: {
                        	 		position: function () {
                        	 			var position = c3.chart.internal.fn.tooltipPosition.apply(this, arguments);
                        	 			position.top = 0;
                        	 			return position;
                        	 		},
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
                        	 				var jk = data[i].index; 
                        	 				var content = '';
                        	 				var location = '';
                        	 				if(tagids == 'Active'){
                        	 					content = stateCount['active']['floors'];
                        	 				} else if(tagids == 'Inactive'){
                        	 					content = stateCount['inactive']['floors'];
                        	 				} else if(tagids == 'Idle'){
                        	 					content = stateCount['idle']['floors'];
                        	 				}   
                        	 				text += '<tr><th colspan="2">Status: '+tagids+'</th></tr>'; 
                        	 				text += '<tr><th colspan="2">Location: '+floornames[data[i].value-1]+'</th></tr>';
                        	 				var tagList = content[jk]['taglist'];
                        	 				tagList.forEach(function(e, index) {  
                        	 					if(e.spid != undefined && e.spid != '' && e.spid != null){
                        	 						text += '<tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Tag</td><td class="value"><a href="/facesix/web/beacon/tagDashview?macaddr='+e.tagid+'&sid='+e.sid+'&spid='+e.spid+'&cid='+cids+'">'+e.tagid+'</a></td></tr>';
                        	 					}else{
                        	 						text += '<tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Tag</td><td class="value"><a href="/facesix/web/beacon/list?cid='+cids+'&name='+e.tagid+'&sid='+e.sid+'">'+e.tagid+'</a></td></tr>';
                        	 					}
                        	 					
                        	 				});
                        	 				
                        	 				
                        	 			}  
                        	 			text += '</tbody></table>';
                        	 			return text;
                        	 		},
                        	 	}, 
                        	 	
                        	 	axis: {
                        	 		rotated: true, 
                        	 		y: {
                        	 			label: 'Floors', 
                        	 			min: 0, 
                        	 			max: floornames.length + 2, 
                        	 			tick: { 
                        	 				rotate: 20, 
                        	 				count: 0,
                        	 				multiline: true,
                        	 				format : function(y) {   
 
                        	 					if(y > 0 && y % 1 === 0){ 
                        	 						if(floornames[y-1] != undefined && floornames[y-1] != null && floornames[y-1] != ''){
                        	 							return floornames[y-1];
                        	 							console.log(floornames[y-1]);
                        	 						} 
                        	 					} 
                        	 				}, 
                        	 			},
                        	 			height: 60
                        	 		},
                        	 		x: { 
                        	 			label: 'Status', 
                        	 			max: 200,
                        	 			min: 0,
                        	 			tick: {
                        	 				format : function(x) {
                        	 					if(x == 50){
                        	 						return "Active";
                        	 					}else if(x == 100){
                        	 						return "Inactive";
                        	 					}else if(x == 150){
                        	 						return "Idle";
                        	 					}else {
                        	 						return '';
                        	 					}
                        	 				}
                        	 			} 
                        	 		},
                        	 	},
                        	 });
                        	   

                        	 var inactiveDataX = [];
                        	 var inactiveDataY = [];

                        	 var inactiveClients = stateCount['inactive']['floors'];
                        	 if(inactiveClients != undefined){
                        	 	if(inactiveClients.length > 0){
                        	 		inactiveClients.forEach(function(e) {
                        	 			inactiveDataX.push(100);
                        	 			var locationname = e.floorname;
                        	 			floornames.forEach(function(e, index) { 
                        	 				if(e.toLowerCase() == locationname.toLowerCase()){
                        	 					inactiveDataY.push(index+1);
                        	 				}
                        	 			});
                        	 			
                        	 		});
                        	 	}
                        	 }


                        	 chart.load({
                        	 	columns: [
                        	 		['TagStatus'].concat(inactiveDataX),
                        	 		['Inactive'].concat(inactiveDataY),
                        	 	]
                        	 });

                        	 var idleDataX = [];
                        	 var idleDataY = [];

                        	 var idleClients = stateCount['idle']['floors']; 
                        	 if(idleClients != undefined){
                        	 	if(idleClients.length > 0){
                        	 		idleClients.forEach(function(e) {
                        	 			idleDataX.push(150);
                        	 			var locationname = e.floorname;
                        	 			floornames.forEach(function(e, index) { 
                        	 				if(e.toLowerCase() == locationname.toLowerCase()){
                        	 					idleDataY.push(index+1);
                        	 				}
                        	 			});
                        	 			
                        	 		});
                        	 	}
                        	 }


                        	 chart.load({
                        	 	columns: [
                        	 		['TagStatus'].concat(idleDataX),
                        	 		['Idle'].concat(idleDataY),
                        	 	]
                        	 });


                        	 var originalHideTooltip = chart.internal.hideTooltip
                        	 chart.internal.hideTooltip = function () {
                        	 	setTimeout(originalHideTooltip, 100)
                        	 }; 

                        	 $('#fd_chart4').hover(function(){
                        	 	
                        	 }, function(){
                        	 	$(this).find('.c3-tooltip-container').html('');
                        	 });

                        	 
                        	/*
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
                        	 
                            VenueDashboard.charts.getChart.devicesConnected = c3.generate(VenueDashboard.charts.chartConfig.devicesConnected);
                           */
                        },
                        error: function (data) {
                            /*setTimeout(function () {
                              VenueDashboard.charts.setChart.devicesConnected();
                            }, VenueDashboard.timeoutCount);*/
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
                            /*setTimeout(function () {
                             VenueDashboard.charts.setChart.avgUplinkSpeed();
                            }, VenueDashboard.timeoutCount);*/
                        },
                        error: function (data) {
                            /*setTimeout(function () {
                              VenueDashboard.charts.setChart.avgUplinkSpeed();
                            }, VenueDashboard.timeoutCount);*/
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
                            /*setTimeout(function () {
                              VenueDashboard.charts.setChart.avgDownlinkSpeed();
                            }, VenueDashboard.timeoutCount);*/
                        },
                        error: function (data) {
                            /*setTimeout(function () {
                              VenueDashboard.charts.setChart.avgDownlinkSpeed();
                            }, VenueDashboard.timeoutCount);*/
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
                FloorTraffic: {
                	size: {
                		height: 300,
                	},
                	bindto: '#FloorTraffic',
                	padding: {
                		top: 15,
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
                		y: {
                     		min: 0,
                     		tick: {
                     			format: d3.format('d')
                     		}
                     	}    
                	},
                	grid: {
                		y: {
                		  lines: [{value:0}]
                		}
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
                
                typeOfTags: {
                    size: {
                        height: 300,
                    },
                    bindto: '#tagTypes',
                    padding: {
                      	top: 15,
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
            var c3ChartList = ['txRx', 'typeOfDevices', 'netFlow', 'devicesConnected', 'typeOfTags', 'FloorTraffic'];
            var that = this;
            $.each(c3ChartList, function (key, val) {
                that.charts.setChart[val](true);
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
	console.log ("Row Limit " + row_limit);
	
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



var defaultChartEnabled = true;
function timeOut(){ 
	if(defaultChartEnabled == true){ 
		VenueDashboard.charts.setChart.txRx();
		VenueDashboard.charts.setChart.typeOfDevices(); 
		VenueDashboard.charts.setChart.netFlow();
		VenueDashboard.charts.setChart.devicesConnected();
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

	
	/* Start Getting tag types */
	$.ajax({
		url: '/facesix/rest/customer/getTagTypes?cid='+urlObj.cid,
		method: "get",
		success: function (result) { 
			var selected = '';
			for(var i = 0; i <= result.length - 1; i++){   
				if(i == 0){
					selected = 'selected';
				}else{
					selected = '';
				}
				 var slectOption = "<option value='"+result[i]+"' "+selected+">"+result[i]+"</option>";
				 $("#tagType").append(slectOption);
			}  
			$("#tagType").chosen({no_results_text: "Oops, nothing found!"}); 
		},
		error: function (data) {
			$('.loaderbox_camera').hide();
		},
		dataType: "json"

	}); 
	/* End Getting tag types */
	
	/* Start Getting tag Names */
	$.ajax({
		url: '/facesix/rest/customer/getTagNames?cid='+urlObj.cid,
		method: "get",
		success: function (result) { 
			var selected = '';
			for(var i = 0; i <= result.length - 1; i++){   
				if(i == 0){
					selected = 'selected';
				}else{
					selected = '';
				}
				 var slectOption = "<option value='"+result[i]+"' "+selected+">"+result[i]+"</option>";
				 $("#tagname").append(slectOption);
			}  
			$("#tagname").chosen({no_results_text: "Oops, nothing found!"}); 
		},
		error: function (data) {
			$('.loaderbox_camera').hide();  
		},
		dataType: "json"

	}); 
	/* End Getting tag Names */
	
	
	$('#but').click(function(e){ 
		
		var floorVal = $('#floorname').val();
		var locaVal = $('#location').val();
		
		if (floorVal != "all" && locaVal == "all") {
			$('.freqFloor').hide();
			$('.freqLocation').show();
			$('.freqLocation').removeClass('col-md-6 col-sm-6');
			$('.freqLocation').addClass('col-md-12 col-sm-6');
		} else if (floorVal != "all" && locaVal != "all") {
			$('.freqFloor').hide();
			$('.freqLocation').hide();
		} else {
			$('.freqFloor').show();
			$('.freqLocation').show();
			$('.freqLocation').addClass('col-md-6 col-sm-6');
		}
		
		
		
		if($('#fileformat').val() == 'html'){
			$('.ui_charts').html('');
			clearInterval(timer);
			$('body').addClass('customizedReportEnabled');
			var time = $('#time').val();
			var reporttype = $('#reporttype').val();
			var filterType = $('#filtertype').val();
			var venuename = $('#venuename').val();
			var floorname = $('#floorname').val();
			var location = $('#location').val();
			var tagType = $('#tagType').val();
			var tagname = $('#tagname').val();
			var cid = urlObj.cid;
			var options = {
				time: time,
				reporttype: reporttype,
				filterType: filterType,
				venuename: venuename,
				floorname: floorname,
				location: location,
				tagType: tagType,
				tagname: tagname,
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
				var reporttype = $('#reporttype').val();
				var filterType = $('#filtertype').val();
				var venuename = $('#venuename').val();
				var floorname = $('#floorname').val();
				var location = $('#location').val();
				var tagType = $('#tagType').val();
				var tagname = $('#tagname').val();
				var cid = urlObj.cid;
				var options = {
					time: time,
					reporttype: reporttype,
					filterType: filterType,
					venuename: venuename,
					floorname: floorname,
					location: location,
					tagType: tagType,
					tagname: tagname,
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
		case 'tagType': 
			
			$('#graph_tagType').addClass('active');
			var ajaxURL = '/facesix/rest/beacon/trilaterationReports/htmlCharts?time='+options.time+'&tagType='+options.tagType+'&cid='+options.cid+'&filtertype=tagType&fileformat=html';
			console.log(ajaxURL);
			$.ajax({
				url: ajaxURL,
				method: "get",
				success: function (result) {  
					
					var tagTypeCount = result.tagTypeCount;
					var stateCount = result.stateCount;
					var locationFreqCount = result.locationFreqCount;
					var floorFreqCount = result.floorFreqCount;
					
					/* Tag Type */
					
					
					 
				 
					/* Tag Status */
					 /*
					var chart = c3.generate({
						bindto: '#tags_stateCount', 
						size: {
	                        height: 300,
	                    },
	                    padding: {
	                      	top: 15,
	                        right: 15,
	                        bottom: 0,
	                        left: 30,
	                    },
					    data: {
					        // iris data from R
					        columns: [
					        	['Status'],
				                ['Active', stateCount['active']['count']],
				                ['Inactive', stateCount['inactive']['count']],
				                ['Idle', stateCount['idle']['count']],
					        ],
					        type : 'bar' 
					    },
					    color: {
	                		pattern: ['#6da900', '#a90000', '#f2700d']
	            		},
	                    tooltip: {
	                        format: {
	                            title: function (d) { return 'Tag Status'  },  
	                        }
	                    },
	                    axis: {
	                        x: {
	                            show: false
	                        }
	                    },
					});
					*/
					
					/* Frequently Used Locations */
					if(locationFreqCount.length > 0){
						var data = {};  
						var tagNames = []; 
						locationFreqCount.forEach(function(e) {
							tagNames.push(e.locationname);
						    data[e.locationname] = e.frequency;
						});
						var chart = c3.generate({
							bindto: '#tags_locationFreqCount', 
							size: {
		                        height: 300,
		                    },
		                    padding: {
		                      	top: 15,
		                        right: 15,
		                        bottom: 0,
		                        left: 30,
		                    },
						    data: {
						        // iris data from R
						    	json: [ data ],
						        keys: {
						            value: tagNames,
						        },
						        type:'bar'
						    },
						    zoom: {
						        enabled: true
						    },
						    legend: {
						    	show: true
						    },
						    axis: {
						    	x: {
						    		show: true,
						    		tick: {
						    			format:  function (d) { 
						    				return  '' 
						    			},  
						    		}
						    	},
						    	y: {
                             		min: 0,
                             		tick: {
                             			format: d3.format('d')
                             		}
                             	}    
						    }, 
		                    tooltip: {
		                        format: {
		                            title: function (d) { return 'Frequently Used Locations'  }, 

		                        }
		                    }
						});
						
					}
					
					
					/* Frequently Used Floors */
					if(floorFreqCount.length > 0){
						var data = {};  
						var tagNames = []; 
						floorFreqCount.forEach(function(e) {
							tagNames.push(e.floorname);
						    data[e.floorname] = e.frequency;
						});
						var chart = c3.generate({
							bindto: '#tags_floorFreqCount', 
							size: {
		                        height: 300,
		                    },
		                    padding: {
		                      	top: 15,
		                        right: 15,
		                        bottom: 0,
		                        left: 30,
		                    },
						    data: {
						        // iris data from R
						    	json: [ data ],
						        keys: {
						            value: tagNames,
						        },
						        type:'bar'
						    },
						    zoom: {
						        enabled: true
						    },
						    legend: {
						    	show: true
						    },
						    axis: {
						    	x: {
						    		show: true,
						    		tick: {
						    			format:  function (d) { 
						    				return  '' 
						    			},  
						    		}
						    	},
						    	y: {
                             		min: 0,
                             		tick: {
                             			format: d3.format('d')
                             		}
                             	}    
						    }, 
		                    tooltip: {
		                        format: {
		                            title: function (d) { return 'Frequently Used Floors'  }, 

		                        }
		                    }
						}); 
						
					}
					
					
					
					
					$('.loaderbox_camera').hide();
					
				},
				error: function (data) {
					 // console.log(data); 
					$('.loaderbox_camera').hide();
				},
				dataType: "json"

			}); 
			
				
			
		break;
		
		case 'tagname':
			$('#graph_tagname').addClass('active');
			var ajaxURL = '/facesix/rest/beacon/trilaterationReports/htmlCharts?time='+options.time+'&tagname='+encodeURIComponent(options.tagname)+'&venuename='+options.venuename+'&floorname='+options.floorname+'&location='+options.location+'&cid='+options.cid+'&filtertype=tagname&fileformat=html';
			console.log(ajaxURL);
			$.ajax({
				url: ajaxURL,
				method: "get",
				success: function (result) {  
					
					var activity = result.activityArray;
					var locationFreqCount = result.locationFreqCount;
					var floorFreqCount = result.floorFreqCount; 
					var newChart = [];
					/* Activity */  
					
					
					if(activity.length > 0){ 
						
						 
						var chart;
						var chart_1;
						var k = 1;
						var tagNames = []; 
						var activityData = [];
						var activityDatas = [];
						
						activity.forEach(function(e) {  
							var xData = [];
							var ClientsData = [];
							var tagid = 'Activity';
							var exit_loc = [];
							var activityValues = e.activity;
							tagid = e.tagid;  
							activityData[tagid] = [];
							
							
							var newActivity = [];
							var ClientsDatas = [];
							activityDatas[tagid] = []
							
							activityValues.forEach(function(e) {  
								activityData[tagid].push(e);
								
								xData.push(e.entry_loc); 
								
								newActivity.push(e.entry_loc);
								newActivity.push(e.exit_loc);
								
								var timevalue = e.timespent;
								timevalue = timevalue.replace(/:/g , ""); 
								ClientsData.push(timevalue); 
								exit_loc.push(exit_loc); 
								 
								ClientsDatas.push(0);
								ClientsDatas.push(100); 
								activityDatas[tagid].push(e);
								activityDatas[tagid].push(e);
								  
								if(k == 1){
									chart = c3.generate({
										bindto: '#tagname_stateCount', 
										data: {
											x: 'x',
											xFormat: '%Y-%m-%d %H:%M:%S.%L', // 'xFormat' can be used as custom format of 'x'
											columns: [
												['x'].concat(xData),
												[tagid].concat(ClientsData),
											],
											type: 'step',
										},
										padding: {
											 top: 30,
											 right: 30,
											 bottom: 30,
											 left: 60,
										},
										zoom: {
											enabled: true
										},
										tooltip: {
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
													var jk = data[i].index; 
													 
													text += '<tr><th colspan="2">'+tagids+'</th></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Location Name</td><td class="value">'+activityData[tagids][jk]["locationname"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Entry Time</td><td class="value">'+activityData[tagids][jk]["entry_loc"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Exit Time</td><td class="value">'+activityData[tagids][jk]["exit_loc"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Time Spent</td><td class="value">'+activityData[tagids][jk]["timespent"]+'</td></tr>';
												}
												text += '</tbody></table>';
												return text;
											},
										}, 
										axis: {
											x: {
												type: 'timeseries',
												tick: {
													format: '%Y-%m-%d \n %H:%M:%S'
												},
												
											},
											y: { 
												tick: {
													format: function (d) { var newNum = pad_with_zeroes(d, 10); return newNum.toString().substr(1, 2) + ':' + newNum.toString().substr(2,2)+ ':' + newNum.toString().substr(4,2); }
												} 
											}
										}
									});
									
									 
									chart_1 = c3.generate({
										bindto: '#tagname_stateCount_Unique', 
										data: {
											x: 'x',
											xFormat: '%Y-%m-%d %H:%M:%S.%L', // 'xFormat' can be used as custom format of 'x'
											columns: [
												['x'].concat(newActivity),
												[tagid].concat(ClientsDatas),
											]
										},
										padding: {
											 top: 30,
											 right: 30,
											 bottom: 30,
											 left: 60,
										},
										zoom: {
											enabled: true
										},
										tooltip: {
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
													if(jk % 2 === 0){
														text += '<tr><th colspan="2">'+tagids+'</th></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Location Name</td><td class="value">'+activityDatas[tagids][jk]["locationname"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Entry Time</td><td class="value">'+activityDatas[tagids][jk]["entry_loc"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Exit Time</td><td class="value">'+activityDatas[tagids][jk]["exit_loc"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Time Spent</td><td class="value">'+activityDatas[tagids][jk]["timespent"]+'</td></tr>';
													} else {
														text += '<tr><th colspan="2">'+tagids+'</th></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Location Name</td><td class="value">'+activityDatas[tagids][jk]["locationname"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Exit Time</td><td class="value">'+activityDatas[tagids][jk]["exit_loc"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Entry Time</td><td class="value">'+activityDatas[tagids][jk]["entry_loc"]+'</td></tr><tr class="c3-tooltip-name--r2"><td class="name text-left"><span style="background-color:#8c564b"></span>Time Spent</td><td class="value">'+activityDatas[tagids][jk]["timespent"]+'</td></tr>';
													}
													
												}
												text += '</tbody></table>';
												return text;
											},
										}, 
										axis: {
											x: {
												type: 'timeseries',
												tick: {
													format: '%Y-%m-%d \n %H:%M:%S'
												}
											},
											y: { 
												tick: {
													format: function (d) { var newNum = pad_with_zeroes(d, 10); return newNum.toString().substr(1, 2) + ':' + newNum.toString().substr(2,2)+ ':' + newNum.toString().substr(4,2); }
												} 
											}
										}
									});
									
									
								} else {
									chart.load({
										columns: [
											['x'].concat(xData),
											[tagid].concat(ClientsData),
										]
						            });
									
									chart_1.load({
										columns: [
											['x'].concat(newActivity),
											[tagid].concat(ClientsDatas),
										]
						            });
									
								}
								
								
								
							}); 
							k = k + 1;
						}); 
						 
						
						 
					}
					
					/* Frequently Used Locations */
					if(locationFreqCount.length > 0){
						var data = {};  
						var tagNames = []; 
						locationFreqCount.forEach(function(e) {
							tagNames.push(e.locationname);
						    data[e.locationname] = e.frequency;
						});
						var chart = c3.generate({
							bindto: '#tagname_locationFreqCount', 
							size: {
		                        height: 300,
		                    },
		                    padding: {
		                      	top: 15,
		                        right: 15,
		                        bottom: 0,
		                        left: 30,
		                    },
						    data: {
						        // iris data from R
						    	json: [ data ],
						        keys: {
						            value: tagNames,
						        },
						        type:'bar'
						    },
						    zoom: {
						        enabled: true
						    },
						    legend: {
						    	show: true
						    },
						    axis: {
						    	x: {
						    		show: true,
						    		tick: {
						    			format:  function (d) { 
						    				return  '' 
						    			},  
						    		}
						    	},
						    	y: {
                             		min: 0,
                             		tick: {
                             			format: d3.format('d')
                             		}
                             	}    
						    }, 
		                    tooltip: {
		                        format: {
		                            title: function (d) { return 'Frequently Used Locations'  },  
		                        }
		                    }
						});
						
					} 
					
					
					/* Frequently Used Floors */
					if(floorFreqCount.length > 0){
						var data = {};  
						var tagNames = []; 
						floorFreqCount.forEach(function(e) {
							tagNames.push(e.floorname);
						    data[e.floorname] = e.frequency;
						});
						var chart = c3.generate({
							bindto: '#tagname_floorFreqCount', 
							size: {
		                        height: 300,
		                    },
		                    padding: {
		                      	top: 15,
		                        right: 15,
		                        bottom: 0,
		                        left: 30,
		                    },
						    data: {
						        // iris data from R
						    	json: [ data ],
						        keys: {
						            value: tagNames,
						        },
						        type:'bar'
						    },
						    zoom: {
						        enabled: true
						    },
						    legend: {
						    	show: true
						    },
						    axis: {
						    	x: {
						    		show: true,
						    		tick: {
						    			format:  function (d) { 
						    				return  '' 
						    			},  
						    		}
						    	},
						    	y: {
                             		min: 0,
                             		tick: {
                             			format: d3.format('d')
                             		}
                             	}    
						    },
		                    tooltip: {
		                        format: {
		                            title: function (d) { return 'Frequently Used Floors'  }, 

		                        }
		                    }
						}); 
						
					}
					
					
					
					
					$('.loaderbox_camera').hide();
					
					
				},
				error: function (data) {
					 // console.log(data); 
					$('.loaderbox_camera').hide();
				},
				dataType: "json"

			}); 
			
		break; 
		
		case 'default': 
			$('#graph_default').addClass('active');
			var ajaxURL = '/facesix/rest/beacon/trilaterationReports/htmlCharts?time='+options.time+'&reporttype='+options.reporttype+'&cid='+options.cid+'&filtertype=default&fileformat=html';
			console.log(ajaxURL);
			// var result = {"tagstatus":{"checkedin":11,"checkedout":1,"active":0,"inactive":1,"idle":0},"floorFreqCount":[{"floorname":"techfront warehouse","frequency":12}],"locationFreqCount":[{"locationname":"r11","frequency":6},{"locationname":"r9","frequency":0},{"locationname":"r8","frequency":0},{"locationname":"r10","frequency":2},{"locationname":"r4","frequency":2},{"locationname":"r2","frequency":0},{"locationname":"r3","frequency":1},{"locationname":"r7","frequency":0},{"locationname":"r6","frequency":0},{"locationname":"r1","frequency":0},{"locationname":"r5","frequency":1}],"tagTypes":[{"tagtype":"Phone","details":{"active":1,"inactive":0,"idle":0,"typeCount":1}},{"tagtype":"Gift","details":{"active":0,"inactive":1,"idle":0,"typeCount":1}},{"tagtype":"Male","details":{"active":1,"inactive":1,"idle":0,"typeCount":2}},{"tagtype":"WheelChair","details":{"active":1,"inactive":0,"idle":0,"typeCount":1}},{"tagtype":"Doctor","details":{"active":3,"inactive":0,"idle":0,"typeCount":3}},{"tagtype":"Film","details":{"active":0,"inactive":1,"idle":0,"typeCount":1}},{"tagtype":"Child","details":{"active":1,"inactive":0,"idle":0,"typeCount":1}},{"tagtype":"Car","details":{"active":0,"inactive":1,"idle":0,"typeCount":1}},{"tagtype":"Library","details":{"active":1,"inactive":0,"idle":0,"typeCount":1}}]};
			
			console.log(ajaxURL);
			$.ajax({
				url: ajaxURL,
				method: "get",
				success: function (result) {  
					
					var floorBasedTagType = result.floorBasedTagType;
					if(floorBasedTagType.length > 0){
							
						var data =  [];  
						var tagNames = []; 
						 
						floorBasedTagType.forEach(function(e, index) {
							
							var array = {}; 
							array["floorname"] = index;  
							var tagTypes = e.tagTypes;  
							tagTypes.forEach(function(e) { 
								array[e.tagtype] = e.count; 
								if(tagNames.indexOf(e.tagtype) > -1)
								{ 
								} else { 
									tagNames.push(e.tagtype);
								}
									
							}); 
							data.push(array);	
						});   
						
						var chart = c3.generate({
							bindto: '#default_BasedTagType', 
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
								x: "floorname",
								json: data,
								keys: {
									x: "floorname",
									value: tagNames
								},
								/*columns: [
									['data1', -30, 200, 200, 400, -150, 250],
									['data2', 130, 100, -100, 200, -150, 50],
									['data3', -230, 200, 200, -300, 250, 250]
								], */
								type: 'bar',
								groups: [ tagNames ], 
								
							},
							axis: {
								x: {
									show: true,
									tick: {
										rotate: 15,
										format:  function (d) { 
											return  floorBasedTagType[d]['floorname'] 
										},  
									}
								}
							},
							tooltip: {
								format: {
									title: function (d) { return  floorBasedTagType[d]['floorname'] },  
								}
							},
							zoom: {
								enabled: true
							},
							grid: {
								y: {
									lines: [{value:0}]
								}
							}
						});
						   
					}
					
					
					var tagstatus = result.tagstatus; 
					if(tagstatus != undefined){
						
						/* Tag Status */
						 
						var chart = c3.generate({
							bindto: '#default_tag_status', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 15,
								bottom: 0,
								left: 100,
							},
							data: {
								// iris data from R
								x: 'Status',
								columns: [
									['Status', 'Checkedin', 'Checkedout', 'Active', 'Inactive', 'Idle'],
									['Tags', tagstatus['checkedin'], tagstatus['checkedout'], tagstatus['active'], tagstatus['inactive'], tagstatus['idle']],
									
								],
								type : 'bar' 
							},
							color: {
								pattern: ['#6da900', '#a90000', '#f2700d']
							},
							tooltip: {
								format: {
									title: function (d) { return 'Tag Status'  },  
								}
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
									type: 'category',
									
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
					}


					/* Frequently  used floors */ 
					var floorFreqCount = result.floorFreqCount;
					if(floorFreqCount.length > 0){
						var data = {};  
						var tagNames = []; 
						floorFreqCount.forEach(function(e) {
							tagNames.push(e.floorname);
							data[e.floorname] = e.frequency;
						});
						
						var chart = c3.generate({
							bindto: '#default_fuf', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 15,
								bottom: 0,
								left: 30,
							},
							data: {
								// iris data from R
								json: [ data ],
								keys: {
									value: tagNames,
								},
								type:'bar'
							},
							zoom: {
								enabled: true
							},
							legend: {
								show: true
							},
							axis: {
								x: {
									show: false
								}
							}, 
							tooltip: {
								format: {
									title: function (d) { return 'Frequently Used Floors'  }, 

								}
							}
						}); 
					}

					/* Frequently Used Locations */ 
					var locationFreqCount = result.locationFreqCount;
					if(locationFreqCount.length > 0){
						var data = {};  
						var tagNames = []; 
						locationFreqCount.forEach(function(e) {
							tagNames.push(e.locationname);
							data[e.locationname] = e.frequency;
						});
						 
						var chart = c3.generate({
							bindto: '#default_ful', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 15,
								bottom: 0,
								left: 30,
							},
							data: {
								// iris data from R
								json: [ data ],
								keys: {
									value: tagNames,
								},
								type:'bar'
							},
							zoom: {
								enabled: true
							},
							legend: {
								show: true
							},
							axis: {
								x: {
									show: false
								}
							}, 
							tooltip: {
								format: {
									title: function (d) { return 'Frequently Used Locations'  },  
								}
							}
						});
					}
 
					$('.loaderbox_camera').hide();
					
				},
				error: function (data) {
					 // console.log(data); 
					$('.loaderbox_camera').hide();
				},
				dataType: "json"

			}); 
			
			
		break;
		
		case 'venue': 
			$('#graph_venue').addClass('active'); 
			var ajaxURL = '/facesix/rest/beacon/trilaterationReports/htmlCharts?time='+options.time+'&reporttype='+options.reporttype+'&venuename='+options.venuename+'&cid='+options.cid+'&filtertype=venue&fileformat=html';
			console.log(ajaxURL);
			// var result = {"tagstatus":{"checkedin":11,"checkedout":1,"active":0,"inactive":1,"idle":0},"floorFreqCount":[{"floorname":"techfront warehouse","frequency":12}],"locationFreqCount":[{"locationname":"r11","frequency":6},{"locationname":"r9","frequency":0},{"locationname":"r8","frequency":0},{"locationname":"r10","frequency":2},{"locationname":"r4","frequency":2},{"locationname":"r2","frequency":0},{"locationname":"r3","frequency":1},{"locationname":"r7","frequency":0},{"locationname":"r6","frequency":0},{"locationname":"r1","frequency":0},{"locationname":"r5","frequency":1}],"tagTypes":[{"tagtype":"Phone","details":{"active":1,"inactive":0,"idle":0,"typeCount":1}},{"tagtype":"Gift","details":{"active":0,"inactive":1,"idle":0,"typeCount":1}},{"tagtype":"Male","details":{"active":1,"inactive":1,"idle":0,"typeCount":2}},{"tagtype":"WheelChair","details":{"active":1,"inactive":0,"idle":0,"typeCount":1}},{"tagtype":"Doctor","details":{"active":3,"inactive":0,"idle":0,"typeCount":3}},{"tagtype":"Film","details":{"active":0,"inactive":1,"idle":0,"typeCount":1}},{"tagtype":"Child","details":{"active":1,"inactive":0,"idle":0,"typeCount":1}},{"tagtype":"Car","details":{"active":0,"inactive":1,"idle":0,"typeCount":1}},{"tagtype":"Library","details":{"active":1,"inactive":0,"idle":0,"typeCount":1}}]};
			if(options.venuename == 'all'){
				$('#venue_du_com').hide(); 
			}else{
				$('#venue_du_com').show(); 
			}
			console.log(ajaxURL);
			$.ajax({
				url: ajaxURL,
				method: "get",
				success: function (result) {  
					 
					if(options.venuename != 'all'){
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
										['Uplink'].concat(tx), 
										['Downlink'].concat(rx), 
									],
									types: {
										Uplink: 'area-spline',
										Downlink: 'area-spline'
										// 'line', 'spline', 'step', 'area', 'area-step' are also available to stack
									},
									colors: {
										Uplink: '#5cd293',
										Downlink: '#1a78dd',
	
									},
									groups: [['Uplink', 'Downlink']]
								},
								legend:{
									item:{
										"onclick":function(id){
											charts.focus(id)
										}
									}
								 }, 
								point: {
									show: false
								},
								zoom: {
									enabled: true
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
					}
					
					var floorBasedTagType = result.floorBasedTagType;
					if(floorBasedTagType.length > 0){
							
						var data =  [];  
						var tagNames = []; 
						 
						floorBasedTagType.forEach(function(e, index) {
							
							var array = {}; 
							array["floorname"] = index;  
							var tagTypes = e.tagTypes;  
							tagTypes.forEach(function(e) { 
								array[e.tagtype] = e.count; 
								if(tagNames.indexOf(e.tagtype) > -1)
								{ 
								} else { 
									tagNames.push(e.tagtype);
								}
									
							}); 
							data.push(array);	
						});   
						
						var chart = c3.generate({
							bindto: '#venue_BasedTagType', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 30,
								bottom: 15,
								left: 30,
							},
							data: {
								x: "floorname",
								json: data,
								keys: {
									x: "floorname",
									value: tagNames
								},
								/*columns: [
									['data1', -30, 200, 200, 400, -150, 250],
									['data2', 130, 100, -100, 200, -150, 50],
									['data3', -230, 200, 200, -300, 250, 250]
								], */
								type: 'bar',
								groups: [ tagNames ], 
								
							},
							axis: {
								x: {
									show: true,
									tick: {
										// rotate: 15,
										format:  function (d) { 
											return  floorBasedTagType[d]['floorname'] 
										},  
									}
								},
								y: {
                             		min: 0,
                             		tick: {
                             			format: d3.format('d')
                             		}
                             	}    
							},
							tooltip: {
								format: {
									title: function (d) { return  floorBasedTagType[d]['floorname'] },  
								}
							},
							zoom: {
								enabled: true
							},
							grid: {
								y: {
									lines: [{value:0}]
								}
							}
						});
						   
					}

					/* Frequently  used floors */ 
					var floorFreqCount = result.floorFreqCount;
					if(floorFreqCount.length > 0){
						var data = {};  
						var tagNames = []; 
						floorFreqCount.forEach(function(e) {
							tagNames.push(e.floorname);
							data[e.floorname] = e.frequency;
						});
						
						var chart = c3.generate({
							bindto: '#venue_fuf', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 15,
								bottom: 15,
								left: 30,
							},
							data: {
								// iris data from R
								json: [ data ],
								keys: {
									value: tagNames,
								},
								type:'bar'
							},
							zoom: {
								enabled: true
							},
							legend: {
								show: true
							},
							axis: {
								x: {
									show: true,
									tick: {
										format:  function (d) { 
											return  '' 
										},  
									}
								},
								y: {
                             		min: 0,
                             		tick: {
                             			format: d3.format('d')
                             		}
                             	}    
							}, 
							tooltip: {
								format: {
									title: function (d) { return 'Frequently Used Floors'  }, 

								}
							}
						}); 
					}

					/* Frequently Used Locations */ 
					var locationFreqCount = result.locationFreqCount;
					if(locationFreqCount.length > 0){
						var data = {};  
						var tagNames = []; 
						locationFreqCount.forEach(function(e) {
							tagNames.push(e.locationname);
							data[e.locationname] = e.frequency;
						});
						 
						var chart = c3.generate({
							bindto: '#venue_ful', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 15,
								bottom: 15,
								left: 30,
							},
							data: {
								// iris data from R
								json: [ data ],
								keys: {
									value: tagNames,
								},
								type:'bar'
							},
							zoom: {
								enabled: true
							},
							legend: {
								show: true
							},
							axis: {
								x: {
									show: true,
									tick: {
										format:  function (d) { 
											return  '' 
										},  
									}
								},
								y: {
                             		min: 0,
                             		tick: {
                             			format: d3.format('d')
                             		}
                             	}    
							}, 
							tooltip: {
								format: {
									title: function (d) { return 'Frequently Used Locations'  },  
								}
							}
						});
					}

					  
					$('.loaderbox_camera').hide();
					
				},
				error: function (data) {
					 // console.log(data); 
					$('.loaderbox_camera').hide();
				},
				dataType: "json"

			}); 
			
			
		break;
		
		case 'floor': 
			$('#graph_floor').addClass('active'); 
			if(options.venuename == 'all'){
				$('#floor_uplinkDlink').hide();  
			}else{
				$('#floor_uplinkDlink').show();  
			}
			if(options.floorname != 'all'){
				$('#floor_fuf_graph_option').hide(); 
			}else {
				$('#floor_fuf_graph_option').show(); 
			}
			if(options.venuename == 'all' && options.floorname == 'all'){
				$('.min_width1').addClass('col-md-4').removeClass('col-md-6');
			} else if(options.venuename != 'all' && options.floorname == 'all'){
				$('.min_width1').addClass('col-md-4').removeClass('col-md-6');
			} else {
				$('.min_width1').removeClass('col-md-4').addClass('col-md-6');
			} 
			var ajaxURL = '/facesix/rest/beacon/trilaterationReports/htmlCharts?time='+options.time+'&reporttype='+options.reporttype+'&venuename='+options.venuename+'&floorname='+options.floorname+'&cid='+options.cid+'&filtertype=floor&fileformat=html';
			console.log(ajaxURL);
		 
			// floor_BasedTagType
			
			console.log(ajaxURL);
			$.ajax({
				url: ajaxURL,
				method: "get",
				success: function (result) {  
					var floorBasedTagType = result.floorBasedTagType;
					if(floorBasedTagType.length > 0){
							
						var data =  [];  
						var tagNames = []; 
						 
						floorBasedTagType.forEach(function(e, index) {
							
							var array = {}; 
							array["floorname"] = index;  
							var tagTypes = e.tagTypes;  
							tagTypes.forEach(function(e) { 
								array[e.tagtype] = e.count; 
								if(tagNames.indexOf(e.tagtype) > -1)
								{
									console.log('1');
								} else {
									console.log('asas');
									tagNames.push(e.tagtype);
								}
									
							}); 
							data.push(array);	
						});   
						
						var chart = c3.generate({
							bindto: '#floor_BasedTagType', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 30,
								bottom: 15,
								left: 30,
							},
							data: {
								x: "floorname",
								json: data,
								keys: {
									x: "floorname",
									value: tagNames
								},
								/*columns: [
									['data1', -30, 200, 200, 400, -150, 250],
									['data2', 130, 100, -100, 200, -150, 50],
									['data3', -230, 200, 200, -300, 250, 250]
								], */
								type: 'bar',
								groups: [ tagNames ], 
								
							},
							axis: {
								x: {
									show: true,
									tick: {
										// rotate: 15,
										format:  function (d) { 
											return  floorBasedTagType[d]['floorname'] 
										},  
									}
								},
								y: {
                             		min: 0,
                             		tick: {
                             			format: d3.format('d')
                             		}
                             	}    
							},
							tooltip: {
								format: {
									title: function (d) { return  floorBasedTagType[d]['floorname'] },  
								}
							},
							zoom: {
								enabled: true
							},
							grid: {
								y: {
									lines: [{value:0}]
								}
							}
						});
						   
					}
					if(options.venuename != 'all'){ 
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
										['Uplink'].concat(tx), 
										['Downlink'].concat(rx), 
									],
									types: {
										Uplink: 'area-spline',
										Downlink: 'area-spline'
										// 'line', 'spline', 'step', 'area', 'area-step' are also available to stack
									},
									colors: {
										Uplink: '#5cd293',
										Downlink: '#1a78dd',

									},
									groups: [['Uplink', 'Downlink']]
								},
								axis: {
									x: {
										show: true,
										tick: {
											rotate: 15,
											format:  function (d) { 
												return  floorBasedTagType[d]['floorname'] 
											},  
										}
									}
								},
								legend:{
									item:{
										"onclick":function(id){
											charts.focus(id)
										}
									}
								 }, 
								point: {
									show: false
								},
								zoom: {
									enabled: true
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
							$('.uplink_downlink').change(function(){
								var val = $(this).val();
								charts.focus(val)
							});
							
						}
					}
					
					if(options.floorname == 'all'){
						/* Frequently  used floors */ 
						var floorFreqCount = result.floorFreqCount;
						if(floorFreqCount.length > 0){
							var data = {};  
							var tagNames = []; 
							floorFreqCount.forEach(function(e) {
								tagNames.push(e.floorname);
								data[e.floorname] = e.frequency;
							});
							
							var chart = c3.generate({
								bindto: '#floor_fuf', 
								size: {
									height: 300,
								},
								padding: {
									top: 15,
									right: 15,
									bottom: 15,
									left: 30,
								},
								data: {
									// iris data from R
									json: [ data ],
									keys: {
										value: tagNames,
									},
									type:'bar'
								},
								zoom: {
									enabled: true
								},
								legend: {
									show: true
								},
								axis: {
									x: {
										show: true,
										tick: {
											format:  function (d) { 
												return  '' 
											},  
										}
									}
								}, 
								tooltip: {
									format: {
										title: function (d) { return 'Frequently Used Floors'  }, 
	
									}
								}
							}); 
						}  

					}
					/* Frequently Used Locations */ 
					var locationFreqCount = result.locationFreqCount;
					if(locationFreqCount.length > 0){
						var data = {};  
						var tagNames = []; 
						locationFreqCount.forEach(function(e) {
							tagNames.push(e.locationname);
							data[e.locationname] = e.frequency;
						});
						 
						var chart = c3.generate({
							bindto: '#floor_ful', 
							size: {
								height: 300,
							},
							padding: {
								top: 15,
								right: 15,
								bottom: 15,
								left: 30,
							},
							data: {
								// iris data from R
								json: [ data ],
								keys: {
									value: tagNames,
								},
								type:'bar'
							},
							zoom: {
								enabled: true
							},
							legend: {
								show: true
							},
							axis: {
								x: {
									show: true,
									tick: {
										format:  function (d) { 
											return  '' 
										},  
									}
								},
								y: {
                             		min: 0,
                             		tick: {
                             			format: d3.format('d')
                             		}
                             	}    
							}, 
							tooltip: {
								format: {
									title: function (d) { return 'Frequently Used Locations'  },  
								}
							}
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
		
		
		case 'location': 
			$('#graph_location').addClass('active'); 
			if(options.venuename != 'all'){
				$('#location_uplinkDlink').show();
				
			} else {
				$('#location_uplinkDlink').hide(); 
			}
			if(options.location == 'all'){
				$('#location_ful_graph_option').show();
				$('#location_lbtt_graph_option').show();
				$('#location_cpu_graph_option, #location_memory_graph_option').hide();
				$('#location_uplinkDlink').removeClass('col-md-6').addClass('col-md-12');
				$('#location_basedTagTypes').hide();
			}else {
				$('#location_ful_graph_option').hide();
				$('#location_lbtt_graph_option').hide();
				$('#location_cpu_graph_option, #location_memory_graph_option').show();
				$('#location_uplinkDlink').removeClass('col-md-12').addClass('col-md-6');
				$('#location_basedTagTypes').show();
			}
			if(options.floorname != 'all'){
				$('#location_fuf_graph_option').hide(); 
			} else {
				$('#location_fuf_graph_option').show();  
			}
			if(options.venuename != 'all' && options.floorname == 'all'){
				$('.min_width2').removeClass('col-md-6').addClass('col-md-4');
			} else if(options.floorname != 'all'){
				$('.min_width2').removeClass('col-md-4').addClass('col-md-6');
			}
			/*if(options.location == 'all'){
				$('#location_ful_graph_option').show();
				$('#location_fuf_graph_option').show(); 
				$('#location_scatter_graph_option').hide();
				
			}else {
				$('#location_ful_graph_option').hide(); 
				$('#location_fuf_graph_option').hide();
				$('#location_scatter_graph_option').show();
				
			} */
			var ajaxURL = '/facesix/rest/beacon/trilaterationReports/htmlCharts?time='+options.time+'&reporttype='+options.reporttype+'&venuename='+options.venuename+'&floorname='+options.floorname+'&location='+options.location+'&cid='+options.cid+'&filtertype=location&fileformat=html';
		  
			console.log(ajaxURL);
			 $.ajax({
				url: ajaxURL,
				method: "get",
				success: function (result) {    
					if(options.location != 'all'){
						
						var locationBasedTagType = result.locationBasedTagType;
						if(locationBasedTagType.length > 0){
								
							var data =  [];  
							var tagNames = []; 
							 
							locationBasedTagType.forEach(function(e, index) {
								
								var array = {}; 
								array["floorname"] = index;  
								var tagTypes = e.tagTypes;  
								tagTypes.forEach(function(e) { 
									array[e.tagtype] = e.count; 
									if(tagNames.indexOf(e.tagtype) > -1)
									{ 
									} else { 
										tagNames.push(e.tagtype);
									}
										
								}); 
								data.push(array);	
							});   
							
							var chart = c3.generate({
								bindto: '#location_btt', 
								size: {
									height: 300,
								},
								padding: {
									top: 15,
									right: 30,
									bottom: 15,
									left: 30,
								},
								data: {
									x: "floorname",
									json: data,
									keys: {
										x: "floorname",
										value: tagNames
									},
									/*columns: [
										['data1', -30, 200, 200, 400, -150, 250],
										['data2', 130, 100, -100, 200, -150, 50],
										['data3', -230, 200, 200, -300, 250, 250]
									], */
									type: 'bar',
									groups: [ tagNames ], 
									
								},
								axis: {
									x: {
										show: true,
										tick: {
											rotate: 15,
											format:  function (d) { 
												return  locationBasedTagType[d]['locationname'] 
											},  
										}
									},
									y: {
	                             		min: 0,
	                             		tick: {
	                             			format: d3.format('d')
	                             		}
	                             	}
								},
								tooltip: {
									format: {
										title: function (d) { return  locationBasedTagType[d]['locationname'] },  
									}
								},
								zoom: {
									enabled: true
								},
								grid: {
									y: {
										lines: [{value:0}]
									}
								}
							});
							   
						}
						
						
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
							        },
							        y: {
	                             		min: 0,
	                             		tick: {
	                             			format: d3.format('d')
	                             		}
	                             	}    
							    }
							});
					  
						}
						
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
							        },
							        y: {
	                             		min: 0,
	                             		tick: {
	                             			format: d3.format('d')
	                             		}
	                             	}
							    }
							});
						}
					}
					if(options.venuename != 'all'){
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
										['Uplink'].concat(tx), 
										['Downlink'].concat(rx), 
									],
									types: {
										Uplink: 'area-spline',
										Downlink: 'area-spline'
										// 'line', 'spline', 'step', 'area', 'area-step' are also available to stack
									},
									colors: {
										Uplink: '#5cd293',
										Downlink: '#1a78dd',
	
									},
									groups: [['Uplink', 'Downlink']]
								},
								legend:{
									item:{
										"onclick":function(id){
											charts.focus(id)
										}
									}
								 }, 
								point: {
									show: false
								},
								zoom: {
									enabled: true
								},
								axis: {
									x: {
										type: 'category',
										tick: {
											count: 10,
											format: function(e){ return rxtx[e]['time'];}
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
							$('.location_uplink_downlink').change(function(){
								var val = $(this).val();
								charts.focus(val)
							});
							
						}
					}	
					if(options.location == 'all'){
						var floorBasedTagType = result.locationBasedTagType;
						if(floorBasedTagType.length > 0){
								
							var data =  [];  
							var tagNames = []; 
							 
							floorBasedTagType.forEach(function(e, index) {
								
								var array = {}; 
								array["floorname"] = index;  
								var tagTypes = e.tagTypes;  
								tagTypes.forEach(function(e) { 
									array[e.tagtype] = e.count; 
									if(tagNames.indexOf(e.tagtype) > -1)
									{ 
									} else { 
										tagNames.push(e.tagtype);
									}
										
								}); 
								data.push(array);	
							});   
							
							var chart = c3.generate({
								bindto: '#location_BasedTagType', 
								size: {
									height: 300,
								},
								padding: {
									top: 15,
									right: 30,
									bottom: 15,
									left: 30,
								},
								data: {
									x: "floorname",
									json: data,
									keys: {
										x: "floorname",
										value: tagNames
									},
									/*columns: [
										['data1', -30, 200, 200, 400, -150, 250],
										['data2', 130, 100, -100, 200, -150, 50],
										['data3', -230, 200, 200, -300, 250, 250]
									], */
									type: 'bar',
									groups: [ tagNames ], 
									
								},
								axis: {
									x: {
										show: true,
										tick: {
											rotate: 15,
											format:  function (d) { 
												return  floorBasedTagType[d]['locationname'] 
											},  
										}
									},
									y: {
	                             		min: 0,
	                             		tick: {
	                             			format: d3.format('d')
	                             		}
	                             	}    
								},
								tooltip: {
									format: {
										title: function (d) { return  floorBasedTagType[d]['locationname'] },  
									}
								},
								zoom: {
									enabled: true
								},
								grid: {
									y: {
										lines: [{value:0}]
									}
								}
							});
							   
						}
						
						/* Frequently Used Locations */ 
						var locationFreqCount = result.locationFreqCount;
						if(locationFreqCount.length > 0){
							var data = {};  
							var tagNames = []; 
							locationFreqCount.forEach(function(e) {
								tagNames.push(e.locationname);
								data[e.locationname] = e.frequency;
							});
							 
							var chart = c3.generate({
								bindto: '#location_ful', 
								size: {
									height: 300,
								},
								padding: {
									top: 15,
									right: 15,
									bottom: 15,
									left: 30,
								},
								data: {
									// iris data from R
									json: [ data ],
									keys: {
										value: tagNames,
									},
									type:'bar'
								},
								zoom: {
									enabled: true
								},
								legend: {
									show: true
								},
								axis: {
									x: {
										show: true,
										tick: {
											format:  function (d) { 
												return  '' 
											},  
										}
									},
									y: {
	                             		min: 0,
	                             		tick: {
	                             			format: d3.format('d')
	                             		}
	                             	}    
								}, 
								tooltip: {
									format: {
										title: function (d) { return 'Frequently Used Locations'  },  
									}
								}
							});
						}
						
					}
					 
					if(options.floorname == 'all'){
						/* Frequently  used floors */ 
						var floorFreqCount = result.floorFreqCount;
						if(floorFreqCount.length > 0){
							var data = {};  
							var tagNames = []; 
							floorFreqCount.forEach(function(e) {
								tagNames.push(e.floorname);
								data[e.floorname] = e.frequency;
							});
							
							var chart = c3.generate({
								bindto: '#location_fuf', 
								size: {
									height: 300,
								},
								padding: {
									top: 15,
									right: 15,
									bottom: 15,
									left: 30,
								},
								data: {
									// iris data from R
									json: [ data ],
									keys: {
										value: tagNames,
									},
									type:'bar'
								},
								zoom: {
									enabled: true
								},
								legend: {
									show: true
								},
								axis: {
									x: {
										show: true,
										tick: {
											format:  function (d) { 
												return  '' 
											},  
										}
									}
								}, 
								tooltip: {
									format: {
										title: function (d) { return 'Frequently Used Floors'  }, 

									}
								}
							}); 
						} 
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