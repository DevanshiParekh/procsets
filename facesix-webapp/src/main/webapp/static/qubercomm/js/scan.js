var timer_event;
(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	var timer = 10000;
	var count = 1;
	
	var uid   = location.search.split("&")[2].replace("?","").split("=")[1];
    DeviceACL = {
        timeoutCount: 10000,
        
        acltables: {

            setTable: {
            
            	
                aclClientsTable: function (reload) {
                	var scantimer=$("#time").val();
                	
                	var acl_timeout = 1;
                	//console.log("timer_event= "+timer_event);
                	
                	if(scantimer != "auto" && timer_event != undefined){
                		acl_timeout = 0;
                		clearTimeout (timer_event);
                	}    
                	var cid = urlObj.cid;
                	
                	if (urlObj.clientmac != null) {
                //		console.log("clientmac is not null"+uid);
                		link = '/facesix/rest/qubercomm/scanner/probe_req_stats?uid='+uid+"&time="+"60m" +"&clientmac="+urlObj.clientmac+"&cid="+cid;
                		
                		acl_timeout =0;
                	} else {
                		link = '/facesix/rest/qubercomm/scanner/probe_req_stats?uid='+uid+"&time="+scantimer+"&cid="+cid
                	}
                	
                	if(urlObj.clientmac != null && reload == 'reload'){
                		link = '/facesix/rest/qubercomm/scanner/probe_req_stats?uid='+uid+"&time="+scantimer+"&cid="+cid
                	}
                	console.log("link"+link);
                    $.ajax({
                    	
                    	
                        url: link,
                        method: "get",
                        success: function (result) {
                        	//console.log (link);	
                            var result=result.probe_req_stats
                            if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                DeviceACL.activeClientsData = result;
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
                                //$('table .aclPopup ').on("tap",aclMenu);                                
                                
                            }
                            
                            if (acl_timeout == 1) {
                                timer_event=setTimeout(function () {
                                	DeviceACL.acltables.setTable.aclClientsTable();
                                }, 10000);                            	
                            }

                        },
                        error: function (data) {
                        	if (acl_timeout == 1) {
	                            setTimeout(function () {
	                            	DeviceACL.acltables.setTable.aclClientsTable();
	                            }, 10000);
                        	}
                        },
                        dataType: "json"
                        	
                    });
                }
            }
        },         

        init: function (params) {
            var aclList = ['aclClientsTable']
            var that = this;           
            $.each(aclList, function (key, val) {
                //console.log ("Init " + val + "key" + key + "p" + params)
                that.acltables.setTable[val]();
            });
        },
    }
})();

$(document).ready(function(){
    DeviceACL.init();
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
            if (DeviceACL.activeClientsData.length > current_page * row_limit) {
                show_next_button = true;
            }
            var filteredData = DeviceACL.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
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
    

devScan =DeviceACL;
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
    var filteredData = DeviceACL.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
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

    if (DeviceACL.activeClientsData.length > next_page * row_limit) {
        show_next_button = true;
    }

    var filteredData = DeviceACL.activeClientsData.slice(row_limit * current_page, row_limit * next_page);
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
    DeviceACL.acltables.setTable.aclClientsTable("reload");
});
})

/*///Network config tree
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
    if (!parent)
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
                    
                    if (type.indexOf("server") != -1) {
                        deviceData[type] += 1;
                        var device = new Device(type, status, uid);
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
        network['server'] = '<li class="deviceInfo" id="server-id-' + id +
        '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Server" data-uid="'+uid+'" data-href="/facesix/web/site/portion/flrdash?type=server&spid='+this.urlObj.spid+'"  class="device-name"><label>' +
        '<i class="fa fa-2x fa-angle-down" aria-hidden="true"></i>' +
        '<img src="/facesix/static/qubercomm/images/networkconfig/icon/server_inactive.png" alt=""></label>' +
        '<span>SVR-' + uid + '</span><label class="connected device-status pull-right">' +
        '<span>' + status + '</span></label></div></a>' +
        '<ul class="child list-unstyled" parent-id="'+uid+'" id="server' + id + '-tree"></ul></li>';

    network['switch'] = '<li  class="deviceInfo" id="switch-id-' + id +
        '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Switch" data-uid="'+uid+'" data-href="/facesix/web/site/portion/swiboard?uid='+uid+'&type=switch&spid='+this.urlObj.spid+'" class="device-name"><label>' +
        '<img src="/facesix/static/qubercomm/images/networkconfig/icon/switch_inactive.png" alt=""></label>' +
        '<span>SW-' + uid + '</span><label class="connected device-status pull-right">' +
        '<span>' + status + '</span></label></div></a>' +
        '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="switch' + id + '-tree"></ul></li>';

    network['ap'] = '<li  class="deviceInfo" id="ap-id-' + id + '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Ap" data-uid="'+uid+'" data-href="/facesix/web/site/portion/devboard?uid='+uid+'&type=device&spid='+this.urlObj.spid+'" data-cref="/facesix/web/device/custconfig?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-bref="/facesix/web/site/portion/binary?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-sref="/facesix/web/site/portion/scan?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" class="device-name"><label><img src="/facesix/static/qubercomm/images/networkconfig/icon/ap_inactive.png" alt="">' +
        '</label><span>AP-' +uid + '</span><label class="connected device-status pull-right"><span>' + status + '</span>' +
        '</label></div></a>' + '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="ap' + id + '-tree"></ul></li>';

    network['sensor'] = '<li  class="deviceInfo" id="sensor-id-' + id + '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Sensor" data-uid="'+uid+'" data-href="/facesix/web/site/portion/devboard?uid='+uid+'&type=device&spid='+this.urlObj.spid+'" class="device-name"><label><img src="/facesix/static/qubercomm/images/networkconfig/icon/sensor_inactive.png" alt="">' +
        '</label><span>SNR-' + uid + '</span><label class="connected device-status pull-right ">' +
        '<span>' + status + '</span></label></div></a></li>';

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
        $(".powerBtn").attr("devtype",type)
        var href=$(this).attr("data-href");
        var bref=$(this).attr("data-bref");
        var sref=$(this).attr("data-sref");
        $(".dshbrdLink").attr("href",href);
        $(".binaryLink").attr("href",bref);
        $(".scanLink").attr("href",sref);
}*/


$(document).on("change",".changeTime",function(evt){
	DeviceACL.acltables.setTable.aclClientsTable();
})	

search = window.location.search.substr(1)
urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
var uid   = location.search.split("&")[2].replace("?","").split("=")[1];

$('#pdfexport').on('click', function() {
	window.location.href = "/facesix/rest/qubercomm/scanner/export?uid="+uid+"&time="+$('#time').val();
});
