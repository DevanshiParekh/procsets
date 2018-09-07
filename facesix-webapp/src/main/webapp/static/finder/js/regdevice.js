(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	var timer = 10000;
	var count = 1;
    DeviceACL = {
        timeoutCount: 0,
        registertables: {
            url: {
            	registerClientsTable: '/facesix/rest/beacon/device/registered?cid='+urlObj.cid
            },
            setTable: {
            	registerClientsTable: function () {
                    $.ajax({
                        url: DeviceACL.registertables.url.registerClientsTable,
                        method: "get",
                        success: function (result) {
                        	$(".loader_boxone").hide(); 
                            var result=result.registered
                            console.log("registered"+JSON.stringify(result));
                            if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                DeviceACL.registerClientsTable = result;
                                if (result.length > 10) {
                                    var filteredData = result.slice(0, 10);
                                    show_next_button = true;
                                } else {
                                    var filteredData = result;
                                }

                                var source = $("#chartbox-register-template").html();
                                var template = Handlebars.compile(source);
                                var rendered = template({
                                    "data": filteredData,
                                    "current_page": 1,
                                    "show_previous_button": show_previous_button,
                                    "show_next_button": show_next_button,
                                    "startIndex": 1
                                });
                                $('.reg-table-chart-box').html(rendered);
                                $('.reg-hide').show();
                            }
                        },
                        error: function (data) {
                        	$(".loader_boxone").hide();          
                        },
                        dataType: "json"

                    });
                }
            }
        }, 
        
        init: function (params) {
            var registerList  = ['registerClientsTable']
            $(".loader_boxone").show();
            var that = this;            
            
            $.each(registerList, function (key, val) {
                that.registertables.setTable[val]();
            }); 
            
        },
    }
})();

$("body").on('click','.submitDelete',function(evt){
		evt.preventDefault();
		$(".savearea").show();
		$("#deleteItem").attr("data-target",$(this).attr("href"));
})
$("body").on('click','.deleteAll',function(evt){
		evt.preventDefault();
		$(".saveareaone").show();
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

   $("body").on("click",'#cancelAll',function(evt){
  		$(".rebootPopup").hide();
  		$(".saveareaone").hide();
  })

$(document).ready(function(){
	
	$('.reg-hide').hide();
    DeviceACL.init();

//register 
$('body').on('click', ".reg-tablePreviousPage", function (e) {

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
    var filteredData = DeviceACL.registerClientsTable.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
    var source = $("#chartbox-register-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * row_limit) - row_limit
    });
    $('.reg-table-chart-box').html(rendered);

});

$('body').on('click', ".reg-tableNextPage", function (e) {

    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    next_page = current_page + 1
    var row_limit = $tableBlock.attr('data-row-limit');
    row_limit = parseInt(row_limit);
    
    if (DeviceACL.registerClientsTable.length > next_page * row_limit) {
        show_next_button = true;
    }

    var filteredData = DeviceACL.registerClientsTable.slice(row_limit * current_page, row_limit * next_page);
    var source = $("#chartbox-register-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": next_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": row_limit * current_page
    });
    $('.reg-table-chart-box').html(rendered);

});


$('body').on('click', '.reg-refreshTable', function () {
	DeviceACL.registertables.setTable.registerClientsTable();
});


$('#deleteAllUid').on('click',function(){
	
	var cid  	= location.search.split("&")[0].replace("?","").split("=")[1];
    var url  = "/facesix/rest/beacon/device/deleteall?cid="+cid;	
	$(".loader_box").show();

		$.ajax({
 	   	  	url:url,
 	   	  	method:'GET',
 	   	  	data:{},
 	   	  	success:function(response,error){
 	   		$(".loader_box").hide();
 	   		
 	   	  	},
 	   	  	error:function(error){
 	   	  		 console.log(error);
 	   	  	}
 });
 $(".saveareaone").hide();
});

})


