(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	var timer = 10000;
	var count = 1;
    DeviceACL = {
        timeoutCount: 0,
        acltables: {
            url: {
                aclClientsTable: '/facesix/rest/device/cust/dev/list?cid='+urlObj.cid+"&uid="+urlObj.uid
            },
            setTable: {
                aclClientsTable: function (reload) {
                	var dataurl = DeviceACL.acltables.url.aclClientsTable;
            		if(reload == 'reload')
            		{
            			 dataurl= '/facesix/rest/device/cust/dev/list?cid='+urlObj.cid;
            		}
                	
                    $.ajax({
                        url: dataurl,
                        method: "get",
                        success: function (result) {
                            var result=result.cust_dev_list
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
                            
                            //setTimeout(function () {
                            //  DeviceACL.tables.setTable.aclClientsTable();
                            //}, 10000);                            
                            $(".loader_boxtwo").hide(); 
                            $(".conf-hide").show();
                            setTimeout(function () {
                                DeviceACL.acltables.setTable.aclClientsTable();
                             }, 60000);
                        },
                        error: function (data) {
                        	$(".loader_boxtwo").hide(); 
                            //setTimeout(function () {
                            //  DeviceACL.tables.setTable.aclClientsTable();
                            //}, 10000);                            
                        },
                        dataType: "json"

                    });
                }
            }
        },         

        init: function (params) {
            var aclList = ['aclClientsTable']
            var that = this;      
            $(".loader_boxtwo").show();
            $.each(aclList, function (key, val) {
                that.acltables.setTable[val]();
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
	$(".conf-hide").hide();
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
    DeviceACL.acltables.setTable.aclClientsTable('reload');
});
})


