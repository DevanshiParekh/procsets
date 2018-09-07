(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	var timer = 10000;
	var count = 1; 
	var receiver_row_limit = 10;
    DeviceACL = {
        timeoutCount: 0,
        acltables: {
            url: {
                aclClientsTable: '/facesix/rest/beacon/device/scanner?cid='+urlObj.cid
            },
            setTable: {
                aclClientsTable: function () {
                    $.ajax({
                        url: DeviceACL.acltables.url.aclClientsTable,
                        method: "get",
                        success: function (result) {
                            var checkedValuesScaner = [];
                            var result=result.blescanner;
                            //console.log("blescanner"+JSON.stringify(result));
                            if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                DeviceACL.activeClientsData = result;
                                $.each(DeviceACL.activeClientsData, function(index, optionValue) {   
                            		 if(optionValue.debugflag === 'checked'){
                            			 checkedValuesScaner.push(optionValue.mac_address);
                            		 }
                            	});
                                
                                if (result.length > 10) {
                                    var filteredData = result.slice(0, 10);
                                    show_next_button = true;
                                } else {
                                    var filteredData = result;
                                }

                                var source = $("#chartbox-scanner-template").html();
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
                                if(checkedValuesScaner.length === DeviceACL.activeClientsData.length){
                                	$('#CheckAllScaner').prop('checked', true);
                                }
                            }
                            
                            setTimeout(function () {
                                DeviceACL.acltables.setTable.aclClientsTable();
                             }, 60000);  
                            //setTimeout(function () {
                            //  DeviceACL.tables.setTable.aclClientsTable();
                            //}, 10000);                            

                        },
                        error: function (data) {
                            //setTimeout(function () {
                            //  DeviceACL.tables.setTable.aclClientsTable();
                            //}, 10000);                            
                        },
                        dataType: "json"

                    });
                }
            }
        },         

        receivertables: {
            url: {
            	receiverClientsTable: '/facesix/rest/beacon/device/receiver?cid='+urlObj.cid+"&uid="+urlObj.uid
            },
            setTable: {
            	receiverClientsTable: function (reload) {
            		var dataurl = DeviceACL.receivertables.url.receiverClientsTable;
            		if(reload == 'reload')
            		{
            			 dataurl= '/facesix/rest/beacon/device/receiver?cid='+urlObj.cid;
            		}
                    $.ajax({
                        url: dataurl,
                        method: "get",
                        success: function (result) {
                        	 var checkedValues = [];
                            var result=result.blereceiver;
                            if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                DeviceACL.receiverClientsTable = result;
                                if (result.length >receiver_row_limit) {
                                    var filteredData = result.slice(0,receiver_row_limit);
                                    show_next_button = true;
                                } else {
                                    var filteredData = result;
                                }
                                
                                DeviceACL.ReceiverClientsData = result;
                               // console.log(DeviceACL.ReceiverClientsData);
                                $.each(DeviceACL.ReceiverClientsData, function(index, optionValue) {  
                            		 if(optionValue.debugflag === 'checked'){
                            			 checkedValues.push(optionValue.mac_address);
                            		 }
                            	}); 
                                var source = $("#chartbox-receiver-template").html();
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
                                if(checkedValues.length == DeviceACL.ReceiverClientsData.length){
                                	$('#CheckAll').prop('checked', true);
                                }
                                
                            }
                            $(".loader_boxtwo").hide(); 
                            $(".col-md-15").show(); 
                            setTimeout(function () {
                                DeviceACL.receivertables.setTable.receiverClientsTable();
                             }, 60000); 

                        },
                        error: function (data) {
                          $(".loader_boxtwo").hide();                           
                        },
                        dataType: "json"

                    });
                }
            }
        },   
        
        
        
        servertables: {
            url: {
            	serverClientsTable: '/facesix/rest/beacon/device/server?cid='+urlObj.cid
            },
            setTable: {
            	serverClientsTable: function () {
                    $.ajax({
                        url: DeviceACL.servertables.url.serverClientsTable,
                        method: "get",
                        success: function (result) {
                            var checkedValuesServers = [];
                            var result=result.bleserver
                            console.log("bleserver"+JSON.stringify(result));
                            if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                DeviceACL.serverClientsData = result;
                                $.each(DeviceACL.serverClientsData, function(index, optionValue) {   
                            		 if(optionValue.debugflag === 'checked'){
                            			 checkedValuesServers.push(optionValue.mac_address);
                            		 }
                            	});
                                
                                if (result.length > 10) {
                                    var filteredData = result.slice(0, 10);
                                    show_next_button = true;
                                } else {
                                    var filteredData = result;
                                }

                                var b_source = $("#chartbox-server-template").html();
                                var b_template = Handlebars.compile(b_source);
                                var b_rendered = b_template({
                                    "data": filteredData,
                                    "current_page": 1,
                                    "show_previous_button": show_previous_button,
                                    "show_next_button": show_next_button,
                                    "startIndex": 1
                                });
                                $('.bl-table-chart-box').html(b_rendered);
                                //$('table .aclPopup ').on("tap",aclMenu);                                
                                if(checkedValuesServers.length === DeviceACL.serverClientsData.length){
                                	$('#CheckAllServers').prop('checked', true);
                                }
                            }
                            
                          //  $(".loader_boxtwo").hide();
                            setTimeout(function () {
                                DeviceACL.servertables.setTable.serverClientsTable();
                             }, 60000);  
                        },
                        error: function (data) {
                        	//$(".loader_boxtwo").hide();
                        },
                        dataType: "json"

                    });
                }
            }
        }, 
        
        init: function (params) {
            var aclList    	  = ['aclClientsTable']
            var tableList     = ['receiverClientsTable']
            var serverList    = ['serverClientsTable']
            
            var that = this;
            
            $(".loader_boxtwo").show();
            $.each(aclList, function (key, val) {
                that.acltables.setTable[val]();
            });

            $.each(tableList, function (key, val) {
                that.receivertables.setTable[val]();
            }); 
            
            $.each(serverList, function (key, val) {
                that.servertables.setTable[val]();
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


$(document).ready(function(){
    DeviceACL.init();
    $('body').on('click', "#tableSortChanged th:not(.tableFilter)", function (e) {
    	checkCheckboxstatus(); updateCheckboxstatus();
    }); 
 //scanner    
$('body').on('click', ".acl-tablePreviousPage", function (e) {

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
    var source = $("#chartbox-scanner-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * row_limit) - row_limit
    });
    $('.acl-table-chart-box').html(rendered);

});

$('body').on('click', ".acl-tableNextPage", function (e) {

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
    var source = $("#chartbox-scanner-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": next_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": row_limit * current_page
    });
    $('.acl-table-chart-box').html(rendered);

});

var receiver_row_limit = 10;
$('body').on('change', ".tablelength", function (e) { 
 
	receiver_row_limit = $(this).val();
	var target = $(this).attr('data-target');
	
	$(target).attr('data-row-limit', receiver_row_limit);
	$(target).attr('data-current-page', '1');
	
	checkCheckboxstatus();
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
    if (DeviceACL.receiverClientsTable.length > current_page * receiver_row_limit) {
        show_next_button = true;
    } 
    var filteredData = DeviceACL.receiverClientsTable.slice((previous_page * receiver_row_limit) - receiver_row_limit, previous_page * receiver_row_limit);
    var source = $("#chartbox-receiver-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * receiver_row_limit) - receiver_row_limit
    });
    $('.table-chart-box').html(rendered);
    updateCheckboxstatus(); 
    $('#tablelength').val(receiver_row_limit);  
        
}); 

//receiver
$('body').on('click', ".rec-tablePreviousPage", function (e) {

	
	checkCheckboxstatus();
    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    previous_page = current_page - 1
    next_page = current_page + 1  

    if (previous_page == 1) {
        show_previous_button = false;
    }  
    if (DeviceACL.receiverClientsTable.length > previous_page * receiver_row_limit) {
        show_next_button = true;
    }
    
    var filteredData = DeviceACL.receiverClientsTable.slice((previous_page * receiver_row_limit) - receiver_row_limit, previous_page * receiver_row_limit);
    var source = $("#chartbox-receiver-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * receiver_row_limit) - receiver_row_limit
    });
    $('.table-chart-box').html(rendered);
    updateCheckboxstatus(); 
    $('#tablelength').val(receiver_row_limit); 
});


$('body').on('click', ".rec-tableNextPage", function (e) {

	checkCheckboxstatus();
    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    next_page = current_page + 1  
    
    if (DeviceACL.receiverClientsTable.length > next_page * receiver_row_limit) {
        show_next_button = true;
    }

    var filteredData = DeviceACL.receiverClientsTable.slice(receiver_row_limit * current_page, receiver_row_limit * next_page);
    var source = $("#chartbox-receiver-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": next_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": receiver_row_limit * current_page
    });
    $('.table-chart-box').html(rendered);
    updateCheckboxstatus(); 
    $('#tablelength').val(receiver_row_limit); 
});

function checkCheckboxstatus(){ 
	
	if($('#CheckAll').prop('checked') === true)
	{
		checkStatus = true;
	}
	else 
	{
		checkStatus = false;
	} 
} 
function updateCheckboxstatus(){
	
	var target = $('#CheckAll').attr('data-children');
	var name;
	$('.'+target).each(function(){
		name = $(this).attr('name'); 
		if(checkedValues.indexOf(name) > -1){
			
		}
		else{
			if(checkStatus === true){
				$(this).prop('checked', true);
			}
			else{
				$(this).prop('checked', false);
			}
		}
	});
    if(checkStatus === true){ 
    	$('#CheckAll').prop('checked', true);
    }else{ 
    	$('#CheckAll').prop('checked', false);
    }
    
	$.each(checkedValues, function(index, optionValue) {   
		$('.'+target+'[name="'+optionValue+'"]').prop('checked', true);
	}); 
}


//server
$('body').on('click', ".ser-tablePreviousPage", function (e) {

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
    var source = $("#chartbox-server-template").html();
    var template = Handlebars.compile(source);
    var b_rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * row_limit) - row_limit
    });
    $('.bl-table-chart-box').html(b_rendered);

});

$('body').on('click', ".ser-tableNextPage", function (e) {

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
    var source = $("#chartbox-server-template").html();
    var template = Handlebars.compile(source);
    var b_rendered = template({
        "data": filteredData,
        "current_page": next_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": row_limit * current_page
    });
    $('.bl-table-chart-box').html(b_rendered);

});


$('body').on('click', '.acl-refreshTable', function () {
    DeviceACL.acltables.setTable.aclClientsTable();
});
$('body').on('click', '.refreshTable', function () {
	DeviceACL.receivertables.setTable.receiverClientsTable('reload');
	
	
});
$('body').on('click', '.bl-refreshTable', function () {
	DeviceACL.servertables.setTable.serverClientsTable();
});

})

 
