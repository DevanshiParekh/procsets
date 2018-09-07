
(function () {
   search = window.location.search.substr(1)
   urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
   var peerStats;
    VenueDashboard = {
        timeoutCount: 10000,
         tables: {
            url: {
                activeClientsTable: '/facesix/rest/site/portion/networkdevice/getpeers?sid='+urlObj.sid
            },
            setTable: {
                activeClientsTable: function () {
                    $.ajax({
                        url: VenueDashboard.tables.url.activeClientsTable,
                        method: "get",
                        success: function (result) {
                            
                            peerStats = result.devicesConnected;
                            var result=result.devicesConnected[0]
                            
                           // console.log (">>>>>>>>>>>>>>>>>>>table"+ JSON.stringify(result))                           
                                     if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                VenueDashboard.activeClientsData = result;
                                if (result.length > 5) {
                                    var filteredData = result.slice(0, 5);
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
                              VenueDashboard.tables.setTable.activeClientsTable();
                           }, 10000);                            

                        },
                        error: function (data) {
                            setTimeout(function () {
                              VenueDashboard.tables.setTable.activeClientsTable();
                           }, 10000);                            
                        },
                        dataType: "json"

                    });
                }
            }
        }, 
        charts: {
            urls: {
               // txRx: '/facesix/rest/site/portion/networkdevice/rxtx?sid='+urlObj.sid,
                //netFlow: '/facesix/rest/site/portion/networkdevice/venueagg?sid='+urlObj.sid,
                //typeOfDevices: '/facesix/rest/site/portion/networkdevice/climap?sid='+urlObj.sid, //Todo Url has to be changed here
                devicesConnected: '/facesix/rest/site/portion/networkdevice/getpeers?sid='+urlObj.sid, //Todo Url has to be changed here
                
            },
            setChart: {
                txRx: function (initialData) {
                    $.ajax({
                        url: VenueDashboard.charts.urls.txRx,
                        success: function (result) {
                            if (result && result.length) {                         
                                var timings = [];
                                var txArr = ["Tx"];
                                var rxArr = ["Rx"];
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
            						var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
            						c_formatedTime = new Date (c_formatedTime);
                                    timings.push(c_formatedTime.getHours() + ":" + c_formatedTime.getMinutes());
                                }
                                VenueDashboard.charts.chartConfig.txRx.data.columns = [txArr, rxArr];
                                VenueDashboard.charts.chartConfig.txRx.axis.x.categories = timings;
                            }
                                VenueDashboard.charts.getChart.txRx = c3.generate(VenueDashboard.charts.chartConfig.txRx);
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
                            var floor 	= ["floor"];
                            var ulink 	= ["Downlink"];
                            var dlink 	= ["Uplink"];
                            //console.log(result);
                            for (var i = 0; i < result.length; i++){    
        						if (result[i].max_vap_rx_bytes < 0) {
        							//console.log("Negative RX");
        							result[i].max_vap_rx_bytes = Math.abs(result[i].max_vap_rx_bytes);
        						}
        						
        						if (result[i].max_vap_tx_bytes < 0) {
        							//console.log("Negative TX");
        							result[i].max_vap_tx_bytes = Math.abs(result[i].max_vap_tx_bytes); 
        						}        						
        						
								floor.push(result[i].Floor);
								
    							ulink.push(Math.round(result[i].max_vap_rx_bytes/100));
    							dlink.push(Math.round(result[i].max_vap_tx_bytes/100));        						           						
                            }                        
                        
                            if (result) {

                                VenueDashboard.charts.chartConfig.netFlow.data.columns = [floor, ulink, dlink];
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
                	var c = peerStats;
	                VenueDashboard.charts.chartConfig.typeOfDevices.data.columns = [c[8], c[9]]
	                VenueDashboard.charts.getChart.typeOfDevices = c3.generate(VenueDashboard.charts.chartConfig.typeOfDevices);
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
                            																   result.devicesConnected[6]
                            																   ]
                        	
                        	//console.log (VenueDashboard.charts.chartConfig.devicesConnected.data.columns);
                            VenueDashboard.charts.getChart.devicesConnected = c3.generate(VenueDashboard.charts.chartConfig.devicesConnected);
                            VenueDashboard.charts.setChart.typeOfDevices(true);
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
                        //url: VenueDashboard.charts.urls.avgUplinkSpeed,
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
                        //url: VenueDashboard.charts.urls.avgDownlinkSpeed,
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
                            Tx: 'area-spline',
                            Rx: 'area-spline',


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
                        left: 100,
                    },

                    data: {
                    	x: 'floor',
                        columns: [
			                ['floor', 'Category1', 'Category2'],
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
                devicesConnected: {
                    size: {
                        height: 340,
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
                        width: 50
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
                        height: 340,
                    },
                    bindto: '#fd_chart3',
                    padding: {
                        top: 5,
                        right: 15,
                        bottom: 0,
                        left: 15,
                    },
                    data: {
                        columns: [
                        ],
                        colors: {
                        },
                        type: 'pie'
                    },
                    pie: {
                        label: {
                          format: function(value, ratio, id) {
                            return;
                          }
                        }
                    },

                    tooltip: {
                        format: {
                            value: function (value, ratio, id) {
                                return value + " Clients";
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
            var c3ChartList = ['txRx', 'netFlow', 'devicesConnected'];
            var that = this;

             var tableList   = ['activeClientsTable']
           
            $.each(tableList, function (key, val) {
                that.tables.setTable[val]();
            });   

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
       // url: '/facesix/rest/site/portion/networkdevice/alerts?sid='+urlObj.sid+"&cid="+urlObj.cid, //Todo-Url has to be changed here
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


$(document).ready(function(){
  
    var row_limit = 5;
       
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
            if (VenueDashboard.activeClientsData.length > current_page * row_limit) {
                show_next_button = true;
            }
            var filteredData = VenueDashboard.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
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
        var filteredData = VenueDashboard.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
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
         
        if (VenueDashboard.activeClientsData.length > next_page * row_limit) {
            show_next_button = true;
        }

        var filteredData = VenueDashboard.activeClientsData.slice(row_limit * current_page, row_limit * next_page);
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
        VenueDashboard.tables.setTable.activeClientsTable();
    });
    
    
})

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
