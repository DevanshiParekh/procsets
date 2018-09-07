(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	var timer = 10000;
	var count = 1;
    DeviceACL = {
        timeoutCount: 0,
        battables: {
            url: {
                batClientsTable: '/facesix/rest/beacon/ble/networkdevice/beaconbattery?cid='+urlObj.cid+"&level=40"
            },
            setTable: {
                batClientsTable: function () {
                    $.ajax({
                        url: DeviceACL.battables.url.batClientsTable,
                        method: "get",
                        success: function (result) {
                            var result=result.beaconbattery
                            console.log("beaconbattery"+JSON.stringify(result));
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

                                var source = $("#chartbox-bat-template").html();
                                var template = Handlebars.compile(source);
                                var rendered = template({
                                    "data": filteredData,
                                    "current_page": 1,
                                    "show_previous_button": show_previous_button,
                                    "show_next_button": show_next_button,
                                    "startIndex": 1
                                });
                                $('.bat-table-chart-box').html(rendered);
                                //$('table .aclPopup ').on("tap",aclMenu);                                
                                
                            }
                            
                            //setTimeout(function () {
                            //  DeviceACL.tables.setTable.batClientsTable();
                            //}, 10000);                            

                        },
                        error: function (data) {
                            //setTimeout(function () {
                            //  DeviceACL.tables.setTable.batClientsTable();
                            //}, 10000);                            
                        },
                        dataType: "json"

                    });
                }
            }
        },         
        devicetables: {
            url: {
            	deviceClientsTable: '/facesix/rest/site/portion/networkdevice/gw_alert?cid='+urlObj.cid
            },
            setTable: {
            	deviceClientsTable: function () {
                    $.ajax({
                        url: DeviceACL.devicetables.url.deviceClientsTable,
                        method: "get",
                        success: function (result) {
                         var result=result.inactive_list;
                            // console.log("devicealert"+JSON.stringify(result));
                            if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                DeviceACL.deviceClientsData = result;
                                if (result.length > 10) {
                                    var filteredData = result.slice(0, 10);
                                    show_next_button = true;
                                } else {
                                    var filteredData = result;
                                }

                                var b_source = $("#chartbox-device-template").html();
                                var b_template = Handlebars.compile(b_source);
                                var b_rendered = b_template({
                                    "data": filteredData,
                                    "current_page": 1,
                                    "show_previous_button": show_previous_button,
                                    "show_next_button": show_next_button,
                                    "startIndex": 1
                                });
                                $('.device-table-chart-box').html(b_rendered);
                                //$('table .aclPopup ').on("tap",aclMenu);                                
                                
                            }
                            
                            var hidecol = result[0].portionname;
                            if(hidecol == "NA"){
                            	$('.dumphey').remove();
                            }
                            $(".loader_boxtwo").hide();
                            $(".col-md-15").show();
                        },
                        error: function (data) {
                        	$(".loader_boxtwo").hide();                      
                        },
                        dataType: "json"

                    });
                }
            }
        }, 
        tagtables: {
            url: {
            	tagClientsTable: '/facesix/rest/beacon/ble/networkdevice/inactivetags?cid='+urlObj.cid
            },
            setTable: {
            	tagClientsTable: function () {
                    $.ajax({
                        url: DeviceACL.tagtables.url.tagClientsTable,
                        method: "get",
                        success: function (result) {
                            var result=result.inactivetags
                           // console.log("inactivetags"+JSON.stringify(result));
                            if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                DeviceACL.tagClientsTable = result;
                                if (result.length >10) {
                                    var filteredData = result.slice(0,10);
                                    show_next_button = true;
                                } else {
                                    var filteredData = result;
                                }

                                var source = $("#chartbox-tag-template").html();
                                var template = Handlebars.compile(source);
                                var rendered = template({
                                    "data": filteredData,
                                    "current_page": 1,
                                    "show_previous_button": show_previous_button,
                                    "show_next_button": show_next_button,
                                    "startIndex": 1
                                });
                                $('.tag-table-chart-box').html(rendered);
                                //$('table .aclPopup ').on("tap",aclMenu);                                
                                
                            }
                            

                        },
                        error: function (data) {
                                                    
                        },
                        dataType: "json"

                    });
                }
            }
        },   
        
        devtables: {
            url: {
            	devClientsTable: '/facesix/rest/beacon/ble/networkdevice/beacondevicealert?cid='+urlObj.cid
            },
            setTable: {
            	devClientsTable: function () {
                    $.ajax({
                        url: DeviceACL.devtables.url.devClientsTable,
                        method: "get",
                        success: function (result) {
                            var result=result.beacondevicealert
                             console.log("beacondevicealert"+JSON.stringify(result[0].uid));
                          
                            if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                DeviceACL.serverClientsData = result;
                                if (result.length > 10) {
                                    var filteredData = result.slice(0, 10);
                                    show_next_button = true;
                                } else {
                                    var filteredData = result;
                                }

                                var b_source = $("#chartbox-dev-template").html();
                                var b_template = Handlebars.compile(b_source);
                                var b_rendered = b_template({
                                    "data": filteredData,
                                    "current_page": 1,
                                    "show_previous_button": show_previous_button,
                                    "show_next_button": show_next_button,
                                    "startIndex": 1
                                });
                                $('.dev-table-chart-box').html(b_rendered);
                                //$('table .aclPopup ').on("tap",aclMenu);                                
                                
                            }
                            
                            var hidecol = result[0].uid;
                            if(hidecol == "-"){
                            	$('.dumphey').remove();
                            }
                            
                            $(".loader_boxtwo").hide();
                            $(".col-md-15").show();
                        },
                        error: function (data) {
                        	$(".loader_boxtwo").hide();                      
                        },
                        dataType: "json"

                    });
                }
            }
        }, 
        
        init: function (params) {
            var aclList    	  = ['batClientsTable']
            var tableList     = ['tagClientsTable']
            var serverList    = ['devClientsTable']
            var devicetables  = ['deviceClientsTable']
            
            var that = this; 
            $(".loader_boxtwo").show();
            $.each(aclList, function (key, val) {
                that.battables.setTable[val]();
            });

            $.each(tableList, function (key, val) {
                that.tagtables.setTable[val]();
            }); 
            
            $.each(serverList, function (key, val) {
                that.devtables.setTable[val]();
            });
            
            $.each(devicetables, function (key, val) {
                that.devicetables.setTable[val]();
            });
            
                        
        },
    }
})();

$("body").on('click','.submitDelete',function(evt){
		evt.preventDefault();
		$(".savearea").show();
		$("#deleteItem").attr("data-target",$(this).attr("href"));
})

  $("body").on('click','#deleteItem',function(evt){
  	   var href=$(this).attr("data-target");
  	   var action=$(this).attr("action");
  	   var url=$(this).attr("action-url");
  	   
  	 console.log(action);
  	 
  	 console.log(url);
  	 
  	 
  	   	  $.ajax({
  	   	  	url:url,
  	   	  	method:'GET',
  	   	  	data:{},
  	   	  	headers:{
  	   	  		'content-type':'application/json'
  	   	  	},
  	   	  	success:function(response,error){
               //Called on successful response
  	   	  	},
  	   	  	error:function(error){
  	   	  		//console.log(error);	
  	   	  	}
  	   	  });
  	   
  })
  $("body").on("click",'#cancelDelete',function(evt){
  		$(".rebootPopup").hide();
  		$(".savearea").hide();
  })

function crashDump(fileName,filestatus){
 	console.log("fileName "+fileName + " filestatus " +filestatus);
 	if(fileName != "NA" && filestatus == "0"){
 		reportlink = "/facesix/rest/beacon/ble/networkdevice/CrashDumpFileDownload?fileName="+fileName;	
     	window.open(reportlink);
 	}
 }
 
$(document).ready(function(){
	
	$(".col-md-15").hide();
    DeviceACL.init();
   

 //scanner    
$('body').on('click', ".bat-tablePreviousPage", function (e) {

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
    var filteredData = DeviceACL.activeClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
    var source = $("#chartbox-bat-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * row_limit) - row_limit
    });
    $('.bat-table-chart-box').html(rendered);

});

$('body').on('click', ".bat-tableNextPage", function (e) {

    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    next_page = current_page + 1
    var row_limit = $tableBlock.attr('data-row-limit');
    row_limit = parseInt(row_limit);
    
    if (DeviceACL.activeClientsData.length > next_page * row_limit) {
        show_next_button = true;
    }

    var filteredData = DeviceACL.activeClientsData.slice(row_limit * current_page, row_limit * next_page);
    var source = $("#chartbox-bat-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": next_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": row_limit * current_page
    });
    $('.bat-table-chart-box').html(rendered);

});

//receiver
$('body').on('click', ".tag-tablePreviousPage", function (e) {

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
    var filteredData = DeviceACL.tagClientsTable.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
    var source = $("#chartbox-tag-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * row_limit) - row_limit
    });
    $('.tag-table-chart-box').html(rendered);

});

$('body').on('click', ".tag-tableNextPage", function (e) {

    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    next_page = current_page + 1
    var row_limit = $tableBlock.attr('data-row-limit');
    row_limit = parseInt(row_limit);
    
    if (DeviceACL.tagClientsTable.length > next_page * row_limit) {
        show_next_button = true;
    }

    var filteredData = DeviceACL.tagClientsTable.slice(row_limit * current_page, row_limit * next_page);
    var source = $("#chartbox-tag-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": next_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": row_limit * current_page
    });
    $('.tag-table-chart-box').html(rendered);

});

//server
$('body').on('click', ".dev-tablePreviousPage", function (e) {

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
    var filteredData = DeviceACL.serverClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
    var source = $("#chartbox-dev-template").html();
    var template = Handlebars.compile(source);
    var b_rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * row_limit) - row_limit
    });
    $('.dev-table-chart-box').html(b_rendered);

});

	$('body').on('click', ".dev-tableNextPage", function (e) {
	
	    var show_previous_button = true;
	    var show_next_button = false;
	
	    var tableName = $(this).closest('span').attr("data-table-name");
	    var $tableBlock = $('#' + tableName);
	    var current_page = $tableBlock.attr('data-current-page');
	    current_page = parseInt(current_page);
	    next_page = current_page + 1
	    var row_limit = $tableBlock.attr('data-row-limit');
	    row_limit = parseInt(row_limit);
	    
	    if (DeviceACL.serverClientsData.length > next_page * row_limit) {
	        show_next_button = true;
	    }
	
	    var filteredData = DeviceACL.serverClientsData.slice(row_limit * current_page, row_limit * next_page);
	    var source = $("#chartbox-dev-template").html();
	    var template = Handlebars.compile(source);
	    var b_rendered = template({
	        "data": filteredData,
	        "current_page": next_page,
	        "show_previous_button": show_previous_button,
	        "show_next_button": show_next_button,
	        "startIndex": row_limit * current_page
	    });
	    $('.dev-table-chart-box').html(b_rendered);
	
	});

	
	$('body').on('click', '.bat-refreshTable', function () {
	    DeviceACL.battables.setTable.batClientsTable();
	});
	$('body').on('click', '.tag-refreshTable', function () {
		DeviceACL.tagtables.setTable.tagClientsTable();
	});
	$('body').on('click', '.dev-refreshTable', function () {
		DeviceACL.devtables.setTable.devClientsTable();
	});
	$('body').on('click', '.device-refreshTable', function () {
		DeviceACL.devicetables.setTable.deviceClientsTable();
	});

	//Device
	$('body').on('click', ".device-tablePreviousPage", function (e) {

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
	    var filteredData = DeviceACL.deviceClientsData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
	    var source = $("#chartbox-device-template").html();
	    var template = Handlebars.compile(source);
	    var b_rendered = template({
	        "data": filteredData,
	        "current_page": previous_page,
	        "show_previous_button": show_previous_button,
	        "show_next_button": show_next_button,
	        "startIndex": (previous_page * row_limit) - row_limit
	    });
	    $('.device-table-chart-box').html(b_rendered);

	});

	$('body').on('click', ".device-tableNextPage", function (e) {

	    var show_previous_button = true;
	    var show_next_button = false;

	    var tableName = $(this).closest('span').attr("data-table-name");
	    var $tableBlock = $('#' + tableName);
	    var current_page = $tableBlock.attr('data-current-page');
	    current_page = parseInt(current_page);
	    next_page = current_page + 1
	    var row_limit = $tableBlock.attr('data-row-limit');
	    row_limit = parseInt(row_limit);
	    
	    if (DeviceACL.deviceClientsData.length > next_page * row_limit) {
	        show_next_button = true;
	    }

	    var filteredData = DeviceACL.deviceClientsData.slice(row_limit * current_page, row_limit * next_page);
	    var source = $("#chartbox-device-template").html();
	    var template = Handlebars.compile(source);
	    var b_rendered = template({
	        "data": filteredData,
	        "current_page": next_page,
	        "show_previous_button": show_previous_button,
	        "show_next_button": show_next_button,
	        "startIndex": row_limit * current_page
	    });
	    $('.device-table-chart-box').html(b_rendered);

	});

	
})


