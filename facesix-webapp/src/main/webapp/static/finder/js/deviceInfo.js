(function () {
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	var timer = 10000;
	var count = 1;
    VenueDashboard = {
            timeoutCount: 10000,
            beacontables: {
                url: {
                	beaconClientsTable: '/facesix/rest/beacon/trilaterationReports/deviceInfo?cid='+urlObj.cid
                },
                setTable: {
                	beaconClientsTable: function (reload) {
                		var dataurl = VenueDashboard.beacontables.url.beaconClientsTable;
                		if(reload == 'reload')
                		{
                			 dataurl= '/facesix/rest/beacon/trilaterationReports/deviceInfo?cid='+urlObj.cid;
                		}
                        $.ajax({
                            url: dataurl,
                            method: "get",
                            success: function (result) {
                                 
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
                                $(".loader_box").hide();
                                $('.showCol').show();
                            },
                            error: function (data) {
                            	$(".loader_box").hide();
                            },
                            dataType: "json"

                        });
                    }
                }
            },  
            init: function () {
            	$(".loader_box").show();
            	var that = this;  
                var tableList   = ['beaconClientsTable']
                $.each(tableList, function (key, val) {
                    that.beacontables.setTable[val]();
                });
                

            }

        }
})();

$(document).ready(function(){
	VenueDashboard.init();
$('.showCol').hide();
$('.showTab').hide();
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


})


function crashDump(fileName,filestatus){
 	console.log( "fileName " + fileName + " filestatus " +filestatus);
 	if(fileName != "NA" && filestatus == "0"){
 		reportlink = "/facesix/rest/beacon/ble/networkdevice/CrashDumpFileDownload?fileName="+fileName;	
     	window.open(reportlink);
 	}
 }
 function crashHistory(){
 	 
 	search = window.location.search.substr(1)
 	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
 	var time=$("#time").val();
 	var url ="/facesix/rest/beacon/ble/networkdevice/finder_Device_crash_info?cid="+urlObj.cid+"&time="+time
 	$.ajax({
          url: url,
          method: "get",
          success: function (result) {
        	  var crash = result;
        	  if(crash == "" || crash == "undefined"){
        		  $('.crash').hide();
        		  $('#binary-table').hide();        		  
        	  }
        	  console.log("resr" + JSON.stringify(result))
          		if (result && result.length) {
                                var show_previous_button = false;
                                var show_next_button = false;
                                _.each(result, function (i, key) {
                                    i.index = key + 1;
                                })
                                devInfoData = result;
                                if (result.length > 10) {
                                    var filteredDataOne = result.slice(0, 10);
                                    show_next_button = true;
                                } else {
                                    var filteredDataOne = result;
                                }

                                var source = $("#chartbox-acl-template").html();
                                var template = Handlebars.compile(source);
                                var rendered = template({
                                    "data": filteredDataOne,
                                    "current_page": 1,
                                    "show_previous_button": show_previous_button,
                                    "show_next_button": show_next_button,
                                    "startIndex": 1
                                });
                                $('.acl-table-chart-box').html(rendered);
                                
                            }
              $(".loader_box").hide();
              
          },
          error: function (data) {
          	$(".loader_box").hide();
          },
          dataType: "json"

      });
 	
 }	


 var row_limit = 50;
 
 $('body').on('change', ".tablelengthone", function (e) { 
     	
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
         if (devInfoData.length > current_page * row_limit) {
             show_next_button = true;
         }
         var filteredDataOne = devInfoData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
         var source = $("#chartbox-acl-template").html();
         var template = Handlebars.compile(source);
         var rendered = template({
             "data": filteredDataOne,
             "current_page": previous_page,
             "show_previous_button": show_previous_button,
             "show_next_button": show_next_button,
             "startIndex": (previous_page * row_limit) - row_limit
         });
         $('.acl-table-chart-box').html(rendered); 
         $('#tablelengthone').val(row_limit);
         
     }); 
     
 $('body').on('click', ".tablePreviousPage", function (e) {
	  //console.log("previous")
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
     if (devInfoData.length > previous_page * row_limit) {
         show_next_button = true;
     }
     
     var filteredDataOne = devInfoData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
     var source = $("#chartbox-acl-template").html();
     var template = Handlebars.compile(source);
     var rendered = template({
         "data": filteredDataOne,
         "current_page": previous_page,
         "show_previous_button": show_previous_button,
         "show_next_button": show_next_button,
         "startIndex": (previous_page * row_limit) - row_limit
     });
     $('.acl-table-chart-box').html(rendered); 
     $('#tablelengthone').val(row_limit);
 });

 $('body').on('click', ".tableNextPage", function (e) {

     var show_previous_button = true;
     var show_next_button = false;

     var tableName = $(this).closest('span').attr("data-table-name");
     var $tableBlock = $('#' + tableName);
     var current_page = $tableBlock.attr('data-current-page');
     current_page = parseInt(current_page);
     next_page = current_page + 1 
      
     if (devInfoData.length > next_page * row_limit) {
         show_next_button = true;
     }

     var filteredDataOne = devInfoData.slice(row_limit * current_page, row_limit * next_page);
     var source = $("#chartbox-acl-template").html();
     var template = Handlebars.compile(source);
     var rendered = template({
         "data": filteredDataOne,
         "current_page": next_page,
         "show_previous_button": show_previous_button,
         "show_next_button": show_next_button,
         "startIndex": row_limit * current_page
     });
     $('.acl-table-chart-box').html(rendered); 
     $('#tablelengthone').val(row_limit);
 });



 $(document).on("change",".changeTimeInfo",function(evt){
 	crashHistory();
 })
 $(document).on("click",".acl-refreshTable",function(evt){
 	crashHistory();
 })

  $('#toggle').on('click', function(e){
	  		$(".coll").toggleClass('collapsed');
		    $(".showTab").toggle();
		    $(this).toggleClass('class1');
		    
		});

