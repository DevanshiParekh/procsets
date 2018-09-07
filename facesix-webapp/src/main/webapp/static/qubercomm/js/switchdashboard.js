(function () {
   	search = window.location.search.substr(1)
 	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
 
    SwitchDashboard = {
        timeoutCount: 10000,
        tables: {
            url: {
                activeClientsTable: '/facesix/rest/site/portion/networkdevice/getpeers?swid='+urlObj.uid
            },
            setTable: {
                activeClientsTable: function () {
                    $.ajax({
                        url: SwitchDashboard.tables.url.activeClientsTable,
                        method: "get",
                        success: function (result) {
                            var result = result.devicesConnected[0]
                            if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                SwitchDashboard.activeClientsData = result;
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
                                $(document).off("contextmenu","table tbody tr")
                                //setContextMenu();
                            }

                        },
                        error: function (data) {
                            //console.log(data);
                        },
                        dataType: "json"
                    });
                }
            }
        },
        charts: {
       
            urls: {
                txRx: '/facesix/rest/site/portion/networkdevice/rxtx?swid='+urlObj.uid,
                activeConnections: '/facesix/rest/site/portion/networkdevice/peercount?swid='+urlObj.uid,
            },
            setChart: {
                txRx: function (initialData,params) {
                	var duration = params;
                	var len		 = duration?duration.length:0;
                	var link 	 = SwitchDashboard.charts.urls.txRx;
                	if (len != 0 && duration.localeCompare("time=5") != 0) {
						link = "/facesix/rest/site/portion/networkdevice/rxtxagg?swid="+urlObj.uid+"&"+params;
                	} else {
                		link = SwitchDashboard.charts.urls.txRx;
                		len = 0;
                	}
                	
                	//console.log (link);             
                    $.ajax({
                        url: link,
                        success: function (result) {
                            if (result && result.length) {                                
                                var timings = [];
                                var txArr = ["Tx"];
                                var rxArr = ["Rx"];
                                for (var i = 1; i < result.length; i++){                                  
                                                                  
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
	            						txArr[i] = txArr[i]/100;
	            						rxArr[i] = rxArr[i]/100;
            						} else {
            							txArr[i] = result[i].max_vap_tx_bytes/100;
            							rxArr[i] = result[i].max_vap_rx_bytes/100;           						
            						}
            						
            						var formatedTime = result[i].time;
            						var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
            						c_formatedTime = new Date (c_formatedTime);
                                    timings.push(c_formatedTime.getHours() + ":" + c_formatedTime.getMinutes());
                                }
                                SwitchDashboard.charts.chartConfig.txRx.data.columns = [txArr, rxArr];
                                SwitchDashboard.charts.chartConfig.txRx.axis.x.categories = timings;
                                SwitchDashboard.charts.getChart.txRx = c3.generate(SwitchDashboard.charts.chartConfig.txRx);
                            }
                            setTimeout(function () {
                            	SwitchDashboard.charts.setChart.txRx();
                            }, SwitchDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                            setTimeout(function () {
                             SwitchDashboard.charts.setChart.txRx();
                           }, SwitchDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                },
                activeConnections: function (initialData,params) {
                    $.ajax({
                        url: SwitchDashboard.charts.urls.activeConnections,
                        success: function (result) {
                            SwitchDashboard.charts.chartConfig.activeConnections.targetPos = targetPos = (result /100)*100;
                            SwitchDashboard.charts.chartConfig.activeConnections.innerHTML = '<i class="fa fa-wifi" style="color:green;"></i></br>0';
                            SwitchDashboard.charts.chartConfig.activeConnections.rotateBy = 360/targetPos;
                            if (initialData) {
                                $('#demo-pie-1').circles(SwitchDashboard.charts.chartConfig.activeConnections);
                                counter=0;
                                var timer=setInterval(function(){
                                    var pieChart=$("#demo-pie-1").data("circles");
                                    if(counter>=targetPos){
                                    	clearInterval(timer);
                                    }
                                    else{
                                        counter+=1;
                                        	var str = '<i class="fa fa-wifi" style="color:green;"></i></br>'
                                            var str = str + counter.toString();
                                            pieChart.innerhtml.html(str);
                                        }
                                },100)
                            } else {
                                var pieChart = $('#demo-pie-1').data('circles');
                                pieChart.moveProgress(SwitchDashboard.charts.chartConfig.activeConnections.targetPos);
                            }
                            setTimeout(function () {
                              SwitchDashboard.charts.setChart.activeConnections();
                            }, SwitchDashboard.timeoutCount);
                        },
                        error: function (data) {
                            //console.log(data);
                           setTimeout(function () {
                             SwitchDashboard.charts.setChart.activeConnections();
                           }, SwitchDashboard.timeoutCount);
                        },
                        dataType: "json"
                    });
                }
            },
            getChart: {},
            chartConfig: {
                txRx: {
                    size: {
                        height: 320,
                    },
                    bindto: '#chart_div1',

                    padding: {
                        top: 10,
                        right: 15,
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
                               SwitchDashboard.charts.getChart.txRx.focus(id);  
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
                activeConnections: {
                    innerHTML: '',
                    showProgress: 1,
                    initialPos: 0,
                    targetPos: 3,
                    scale: 100,
                    rotateBy: 360 / 6,
                    speed: 250,
                    delayAnimation:0,
                    onFinishMoving: function (pos) {
                        //console.log('done ', pos);
                    }
                }
            }
        },
        init: function (params) {
            var c3ChartList = ['txRx', 'activeConnections'];
            var tableList = ['activeClientsTable']
            var that = this;
            $.each(c3ChartList, function (key, val) {
                that.charts.setChart[val](true,params?params:"");
            });
            $.each(tableList, function (key, val) {
                that.tables.setTable[val]();
            });

        }
    }
    SwitchDashboard.init();
})();
currentDashboard=SwitchDashboard;
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
    var filteredData = SwitchDashboard.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
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

    if (SwitchDashboard.activeClientsData.length > next_page * row_limit) {
        show_next_button = true;
    }

    var filteredData = SwitchDashboard.activeClientsData.slice(row_limit * current_page, row_limit * next_page);
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
    SwitchDashboard.tables.setTable.activeClientsTable();
});


///Network config tree
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
var heatmap 		= false;
var GatewayFinder 	= false;

function solutionInfo(param1, param2,param3,param4) {
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
                deviceData['total']=tree.length;
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
            '"><a class="dashbrdLink"><div data-status="'+status+'" data-type="Server" data-uid="'+uid+'" data-href="/facesix/web/site/portion/dashboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=server&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-cref="#" data-bref="#" data-sref="#" class="device-name"><label>' +
            '<i class="fa fa-2x fa-angle-down" aria-hidden="true"></i>' +
            '<img src="/facesix/static/qubercomm/images/networkconfig/icon/server_inactive.png" alt=""></label>' +
            '<span>SVR-' + uid + '</span><label class="connected device-status pull-right">' +
            '<span>' + status + '</span></label></div></a>' +
            '<ul class="child list-unstyled" parent-id="'+uid+'" id="server' + id + '-tree"></ul></li>';            	 
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

        network['sensor'] = '<li  class="deviceInfo" id="sensor-id-' + id + '"><a class="dashbrdLink"><div data-status="'+status+'" data-type="Sensor" data-uid="'+uid+'" data-href="/facesix/web/finder/device/devboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=device&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-cref="/facesix/web/finder/device/configure?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-bref="/facesix/web/finder/device/binary?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-sref="/facesix/web/beacon/list?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" class="device-name"><label><img src="/facesix/static/qubercomm/images/networkconfig/icon/sensor_inactive.png" alt="">' +
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
        var type=$(this).attr("data-type");
        var cref=$(this).attr("data-cref");
        $(".powerBtn").attr("devtype",type)
        var href=$(this).attr("data-href");
        var bref=$(this).attr("data-bref");
        var cref=$(this).attr("data-cref");
        var sref=$(this).attr("data-sref");
        
        $(".dshbrdLink").attr("href",href);
        $(".binaryLink").attr("href",bref);
        $(".devcfgLink").attr("href",cref);
        $(".scanLink").attr("href",sref);
}