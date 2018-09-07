/* ========================================================================
 * Qubercomm Scripts v0.1
 * ========================================================================
 * Copyright 2016 Qubercomm.
 * ======================================================================== */  

//devconfig
$(document).ready(function(){
	var url  = "/facesix/rest/user/profile";	
	$.ajax({
 	  	url:url,
 	  	method:'GET',
 	  	data:{},
 	  	headers:{
 	  		'content-type':'application/json'
 	  	},
 	  	success:function(response){
 	  	var ok = response.fname;
 	  	var tk = response.lname;
 	  	var fullname = ok + " " + tk
 	  	console.log(fullname)
 	  	var div_data = "<p>"+ response.fname + " " +response.lname+ "</p>";
		$(div_data).appendTo('#dispName');
 	  	},
 	  	error:function(error){
 	  		console.log(error);
 	  	}
	});
	
	search = window.location.search.substr(1)
	urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
	
	var url  = '/facesix/rest/beacon/ble/networkdevice/inactivityNotify?cid='+urlObj.cid	
	$.ajax({
 	  	url:url,
 	  	method:'GET',
 	  	data:{},
 	  	headers:{
 	  		'content-type':'application/json'
 	  	},
 	  	success:function(response){
 	  	  	
 	  	//console.log(JSON.stringify(response))
 	  	if(response > 0){
	 	  	var div_data = '<i class="att">'+"&nbsp;" + response + "&nbsp;" +'</i>';
			$(div_data).appendTo('.altNot');
 	  	}
 	  	},
 	  	error:function(error){
 	  		console.log(error);
 	  	}
	});
	
})

$('#slide1').click(function(e) {
    e.preventDefault();
   // Toggle all 4 classes off or on
   $('#wrapper').toggleClass('wrapperone');
   $('#sidebar-wrapper').toggleClass('sidebar-wrapperone');
   $('.material-button').toggleClass('open');
        $('.option').toggleClass('scale-on');
		  $('.fa-bars').toggleClass('fabars');
		
});

// rough List search option



//click search icon function
$("#searchClick").click(function() {
	var uid = $('#uid').val();
	if(uid != ""){
		$( "#target" ).submit();
	}
});

$('#target').submit(function() {
    if ($.trim($("#uid").val()) == "") {
        console.log('you did not fill out one of the fields');
        return false;
    } 
});



$('#legclick').on('click',function(){
     $("#flag1").text($('#flag').val());
   });

$('#meshclick').on('click',function(){
    $("#flag2").text($('#flag').val());
  });

$('#mesh').on('click',function(){
	
	if ($("#meshband2g").is(':checked') == true) {
		  band2g="2G";
	     } else {
	    	 band2g="0";
	     } 
	     
	     if ($("#meshband5g").is(':checked') == true) {
	    	 band5g="5G";
	     } else {
	    	 band5g="0";
	     } 
	
	     var url  = "/facesix/rest/device/meshdefaultconfig?&band2g="+band2g+"&band5g="+band5g;
			
			$.ajax({
		  	   	  	url:url,
		  	   	  	method:'POST',
		  	   	  	data:{},
		  	   	  	headers:{
		  	   	  		'content-type':'application/json'
		  	   	  	},
		  	   	  	success:function(response,error){
		  	   	  		prefilldata(JSON.stringify(response));
		  	   	  	},
		  	   	  	error:function(error){
		  	   	  		 console.log(error);
		  	   	  	}
		  	   	  });
});

$('#legacy').on('click',function(){
	
	 if($("#cband").is(':checked') == true) {
		  band2g="2G";
	     } else {
	    	 band2g="0";
	     } 
	     
	   if($("#pband").is(':checked') == true) {
	    	 band5g="5G";
	     } else {
	    	 band5g="0";
	     } 

	     var url  = "/facesix/rest/device/legacydefaultconfig?band2g="+band2g+"&band5g="+band5g;
		  	
			$.ajax({
	  	   	  	url:url,
	  	   	  	method:'POST',
	  	   	  	data:{},
	  	   	  	headers:{
	  	   	  		'content-type':'application/json'
	  	   	  	},
	  	   	  	success:function(response,error){
	  	   	  		prefilldata(JSON.stringify(response));
	  	   	  	},
	  	   	  	error:function(error){
	  	   	  		 console.log(error);
	  	   	  	}
	  });

});


$('#me').on('click',function(){

	if ($("#dual2g").is(':checked') == true) {
		  band2g="2G";
	     } else {
	    	 band2g="0";
	     } 
	     
	     if ($("#dual5g").is(':checked') == true) {
	    	 band5g="5G";
	     } else {
	    	 band5g="0";
	     } 
	     
	var url  = "/facesix/rest/device/meshdefaultconfig?&band2g="+band2g+"&band5g="+band5g;
	
	$.ajax({
  	   	  	url:url,
  	   	  	method:'POST',
  	   	  	data:{},
  	   	  	headers:{
  	   	  		'content-type':'application/json'
  	   	  	},
  	   	  	success:function(response,error){
  	   	  		prefilldata(JSON.stringify(response));
  	   	  	},
  	   	  	error:function(error){
  	   	  		 console.log(error);
  	   	  	}
  	   	  });
	});

$('#lg').on('click',function(){

	if ($("#dualband2g").is(':checked') == true) {
		  band2g="2G";
	     } else {
	    	 band2g="0";
	     } 
	     
	     if ($("#dualband5g").is(':checked') == true) {
	    	 band5g="5G";
	     } else {
	    	 band5g="0";
	     } 
	     
	     var url  = "/facesix/rest/device/legacydefaultconfig?band2g="+band2g+"&band5g="+band5g;
  	
		$.ajax({
	   	  	url:url,
	   	  	method:'POST',
	   	  	data:{},
	   	  	headers:{
	   	  		'content-type':'application/json'
	   	  	},
	   	  	success:function(response,error){
	   	  		prefilldata(JSON.stringify(response));
	   	  	},
	   	  	error:function(error){
	   	  		 console.log(error);
	   	  	}
	});

});

var timerId = 0;
var percent = 0;
var status="";


$('body').on('click', ".tablePreviousPage", function (e) {
	var Binary = $('#result').val();
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
    var filteredData = bisect.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
    var source = $("#chartbox-binary-template").html();
    var template = Handlebars.compile(source);
    var rendered = template({
        "data": filteredData,
        "current_page": previous_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": (previous_page * row_limit) - row_limit
    });
    $('.binary-table-chart-box').html(rendered);

});

$('body').on('click', ".tableNextPage", function (e) {
	var Binary = $('#result').val();
	//console.log("Binary" + JSON.stringify(Binary));
    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    //console.log("current_page"+current_page);
    current_page = parseInt(current_page);
    next_page = current_page + 1
    var row_limit = $tableBlock.attr('data-row-limit');
    row_limit = parseInt(row_limit);

    if (bisect.length > next_page * row_limit) {
        show_next_button = true;
    }
    
    console.log("row_limit"+row_limit+"current_page"+current_page);
    var filteredData = bisect.slice(row_limit * current_page, row_limit * next_page);
    var source = $("#chartbox-binary-template").html();
    var template = Handlebars.compile(source);
   // console.log("filter" + JSON.stringify(filteredDatanew));
    var rendered = template({
        "data": filteredData,
        "current_page": next_page,
        "show_previous_button": show_previous_button,
        "show_next_button": show_next_button,
        "startIndex": row_limit * current_page
    });
    $('.binary-table-chart-box').html(rendered);

}); 


var row_limit = 50;

$('body').on('change', ".tablelengthtwo", function (e) { 
    	
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
        if (dumpValData.length > current_page * row_limit) {
            show_next_button = true;
        }
        var filteredData = dumpValData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
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
        $('#tablelengthtwo').val(row_limit);
        
    }); 
    
$('body').on('click', ".bi-tablePreviousPage", function (e) {

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
    var filteredData = dumpValData.slice((previous_page * row_limit) - row_limit, previous_page * row_limit);
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
    $('#tablelengthtwo').val(row_limit);
});

$('body').on('click', ".bi-tableNextPage", function (e) {

    var show_previous_button = true;
    var show_next_button = false;

    var tableName = $(this).closest('span').attr("data-table-name");
    var $tableBlock = $('#' + tableName);
    var current_page = $tableBlock.attr('data-current-page');
    current_page = parseInt(current_page);
    next_page = current_page + 1 
     
    if (dumpValData.length > next_page * row_limit) {
        show_next_button = true;
    }

    var filteredData = dumpValData.slice(row_limit * current_page, row_limit * next_page);
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
    $('#tablelengthtwo').val(row_limit);
});


$(document).on("click",".bi-refreshTable",function(evt){
	binaryHistory();
})

$(document).on("change",".changeTime",function(evt){
	binaryHistory();
})
/*

	$('#binary-upgrade').on('click',function(){
	
	
	 var pf=document.getElementById('planFilepath').value;
	 var ver=document.getElementById('version').value;
	 var flag = true;
	  
	 if(pf == "" || pf==undefined){
	       document.getElementById('planFilepath').style.borderColor = "red";
	       flag =false;
	       return false;
	   }else if(ver == "" || ver==undefined){
	       document.getElementById('version').style.borderColor = "red";
	       flag =false;
	       return false;
	   }
	   
	   if( flag =true){
		   pay(800);
	   }
	  		
});


function pay(timeintervel){
	

	var path = $('#planFilepath').val();
	var flag =$('#binaryType').val();
	var version	=$('#version').val();
	var planFilepath  =$('#planFilepath').val();
	var sid  	= location.search.split("&")[0].replace("?","").split("=")[1];
	var spid = location.search.split("&")[1].replace("?","").split("=")[1];
	var uid  	= location.search.split("&")[2].replace("?","").split("=")[1];
	var cid  	= location.search.split("&")[3].replace("?","").split("=")[1];
 
	var url  = "/facesix/rest/binary/upgrade?sid="+sid+"&spid="+spid+"&uid="+uid+
	"&cid="+cid+"&flag="+flag+"&version="+version+"&planFilepath="+planFilepath;
	
	
	  // reset progress bar

	  $('#pay').attr('disabled', true);
	  $('#loadone').css('width', '0px');
	  $('#loadone').addClass('progress-bar-striped active');
	
 timerId = setInterval(function() {
	 	
	var result= $.ajax({
		 url:url,
		 method:'POST', 
		 async:false,
		 data:{},
		  success:function(response,error){
			  console.log("success :  " + response);
			  if(response!="onprogress"){
				  percent = 100;
				  timeintervel =0;
				  console.log("complete :  " + response);
				  status = response;
				  // complete
					  clearInterval(timerId);
					  $('#loadone').css('width', percent + '%');
						$('#loadone').html(percent + '%');
						
					  $('#pay').attr('disabled', false);
					  $('#loadone').removeClass('progress-bar-striped active');
					  $('#loadone').html(status);
					  percent= 0;
			  }else{
			   // increment progress bar
				  	percent += 1;		
					$('#loadone').css('width', percent + '%');
					$('#loadone').html(percent + '%');
			  }
		 },
		 error:function(error){
		  console.log("error " +error);
		 }
		 }); 
	  },timeintervel);
 
	}
*/


//formvalidation

function FormValidation(){

   var pf=document.getElementById('planFilepath').value;
   if(pf == ""){
       document.getElementById('planFilepath').style.borderColor = "red";
       return false;
   }else{
       document.getElementById('planFilepath').style.borderColor = "skyblue";
   }
   
   var ver=document.getElementById('version').value;
   if(ver == ""){
       document.getElementById('version').style.borderColor = "red";
       return false;
   }else{
       document.getElementById('version').style.borderColor = "skyblue";
   }
}   
//password

$("#pwd").bind('input propertychange', function(){
	
	var newPassword = document.getElementById('pwd').value;
	console.log(">>>>>"+newPassword);
    var minNumberofChars = 8;
    var regularExpression = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    console.log("...length...="+newPassword.length);
    console.log("....regularExpression.test(newPassword)....="+regularExpression.test(newPassword));
    if(newPassword.length <minNumberofChars || !regularExpression.test(newPassword)){
        $("#pwd").css("border", "1px  solid red");
    }else{
    	$("#pwd").css("border", "1px  solid blue");
    }
    
    }); 

$( ".res" ).focus(function() {
	   var ok = $('#pwd').val();
	   var tk = $('#c_pwd').val();
 if(ok == tx){
     $("#c_pwd").css("border", "1px  solid blue");
 }else{
 	$("#c_pwd").css("border", "1px  solid red");
 }
	});
//macid 

function doInsert(ctl)
{
   vInit = ctl.value;
   ctl.value = ctl.value.replace(/[^a-f0-9:]/ig, "");
   // ctl.value = ctl.value.replace(/:\s*$/, "");
   vCurrent = ctl.value;
   if(vInit != vCurrent)
       return false;   

   var v = ctl.value;
   var l = v.length;
   var lMax = 17;

   if(l >= lMax)
   {
       return false;
   }

   if(l >= 2 && l < lMax)
   {
       var v1 = v;     
       /* Removing all ':' to calculate get actaul text */
       while(!(v1.indexOf(":") < 0)) { // Better use RegEx
           v1 = v1.replace(":", "");          
       }

       /* Insert ':' after ever 2 chars */     
       var arrv1 = v1.match(/.{1,2}/g); // ["ab", "dc","a"]
       ctl.value = arrv1.join(":");
   }
}


$('#custlegacy').on('click',function(){

	var steer = $('#steering').val();
	
	if ($("#custband2g").is(':checked') == true) {
		  band2g="2G";
	     } else {
	    	 band2g="0";
	     }
	     
	     if ($("#custband5g").is(':checked') == true) {
	    	 band5g="5G";
	     } else {
	    	 band5g="0";
	     } 
	     
	     if (band2g == 0 && band5g == 0) {
	     	$("#custband2g").css("outline", "solid 1px  red");
	     	$("#custband5g").css("outline", "solid 1px  red");
	     	return;
	     }
	     	 
	     
	     if ($("#custband2g").is(':checked') == true && $("#custband5g").is(':checked') == true) {
				
				$(".savearea").hide();
				var url  = "/facesix/rest/device/legacydefaultconfig?band2g="+band2g+"&band5g="+band5g;
				$.ajax({
		  	   	  	url:url,
		  	   	  	method:'POST',
		  	   	  	data:{},
		  	   	  	headers:{
		  	   	  		'content-type':'application/json'
		  	   	  	},
		  	   	  	success:function(response,error){
		  	   	  		//console.log(JSON.stringify(response));
		  	   	  		prefilldata(JSON.stringify(response));
		  	   	  	},
		  	   	  	error:function(error){
		  	   	  		 console.log(error);
		  	   	  	}
		  	   	  });
				$(".savearea").hide();
			
			} else if($("#custband2g").is(':checked') == true || $("#custband5g").is(':checked') == true){
				
				if (steer == "true"){
					$(".saveareafour").show();
				} else {
					var url  = "/facesix/rest/device/legacydefaultconfig?band2g="+band2g+"&band5g="+band5g;
					$.ajax({
			  	   	  	url:url,
			  	   	  	method:'POST',
			  	   	  	data:{},
			  	   	  	headers:{
			  	   	  		'content-type':'application/json'
			  	   	  	},
			  	   	  	success:function(response,error){
			  	   	  		//console.log(JSON.stringify(response));
			  	   	  		prefilldata(JSON.stringify(response));
			  	   	  	},
			  	   	  	error:function(error){
			  	   	  		 console.log(error);
			  	   	  	}
			  	   	  });
					$(".savearea").hide();
				}
					
			}
	     var root = $('#root').val();
	     if(root == "yes"){
    	   	root = $('#root').val('no');
    	   	 $('.loadSteer').hide();
    	     $('#loadBalance').prop('checked',false)
	   }
});



$('#custmeshconfig').on('click',function(){

	var steer = $('#steering').val();
	
	if ($("#custdualband2g").is(':checked') == true) {
		  band2g="2G";
	     } else {
	    	 band2g="0";
	     } 
	     
	     if ($("#custdualband5g").is(':checked') == true) {
	    	 band5g="5G";
	     } else {
	    	 band5g="0";
	     }
	     
	     if (band2g == 0 && band5g == 0) {
	     	$("#custdualband2g").css("outline", "solid 1px  red");
	     	$("#custdualband5g").css("outline", "solid 1px  red");
	     	return;
	     }
	     
	     
	     if ($("#custdualband2g").is(':checked') == true && $("#custdualband5g").is(':checked') == true) {
				
				$(".savearea").hide();
				var url  = "/facesix/rest/device/meshdefaultconfig?&band2g="+band2g+"&band5g="+band5g;
				
				$.ajax({
			  	   	  	url:url,
			  	   	  	method:'POST',
			  	   	  	data:{},
			  	   	  	headers:{
			  	   	  		'content-type':'application/json'
			  	   	  	},
			  	   	  	success:function(response,error){
			  	   	  		//console.log(JSON.stringify(response));
			  	   	  		prefilldata(JSON.stringify(response));
			  	   	  	},
			  	   	  	error:function(error){
			  	   	  		 console.log(error);
			  	   	  	}
			  	   	  });
				
				$(".saveareaone").hide();
			
			} else if($("#custdualband2g").is(':checked') == true || $("#custdualband5g").is(':checked') == true){
				
				if (steer == "true"){
					$(".saveareasix").show();
				} else {	
					
				var url  = "/facesix/rest/device/meshdefaultconfig?&band2g="+band2g+"&band5g="+band5g;
				
				$.ajax({
			  	   	  	url:url,
			  	   	  	method:'POST',
			  	   	  	data:{},
			  	   	  	headers:{
			  	   	  		'content-type':'application/json'
			  	   	  	},
			  	   	  	success:function(response,error){
			  	   	  		//console.log(JSON.stringify(response));
			  	   	  		prefilldata(JSON.stringify(response));
			  	   	  	},
			  	   	  	error:function(error){
			  	   	  		 console.log(error);
			  	   	  	}
			  	   	  });
				
				$(".saveareaone").hide();
				
				}
				}
	
	     var root = $('#root').val();
	     if(root == "yes"){
	    	 $('#loadBalance').prop('disabled',false)
	     }
});


$('#exportJSON').on('click',function(){
	var uid=$('#uuid').val();
	var conf=$('#deconfig').val();
	var url  = "/facesix/rest/site/portion/networkdevice/exportJSONConfig?uid="+uid+"&conf="+conf;
	window.open(url, "_blank");
});

$(document).on("click",".csvOption",function(evt){
	window.open("/facesix/rest/site/portion/networkdevice/csv", "_blank");  
});

//plot
$('#plot').on('click',function(){

	var sid  = location.search.split("&")[0].replace("?","").split("=")[1];
	var url  = "/facesix/web/site/portion/plot?sid="+sid;
	window.location.href=url;
	
});
// common variable
var $wrapper =  $("#wrapper"),
$sidebar =  $("#sidebar-wrapper"),
windowWidth =  $(window).width();

var touch = 'ontouchstart' in document.documentElement?true:false

$(window).resize(function() {
	if(!touch){
		$(".sb-name").removeClass("newSide")
		$(".sidebarHeading").removeClass("hide")
	}
	
	$("#wrapper").addClass("toggled-2");
	if(!touch && $(window).width() < 768){
		$("#sidebar-wrapper").removeClass("open");
	}
});


// sidebar collapse
$("#menu-toggle").on("click",function(e){
	var touch = 'ontouchstart' in document.documentElement?true:false
	$(".sidebarHeading").addClass("hide");
	if($(window).width() < 768){

		$(".navbar-collapse").removeClass("in");

		if($(window).width() < 768){
			$("#wrapper").removeClass("toggled-2");
			if($('#sidebar-wrapper').hasClass("open")){
				$('.common-slide').removeClass("postion-f OverscrollFixed");
			}
			else {
				$('.common-slide').addClass("postion-f OverscrollFixed");
			}
		} 


		if(!$("#sidebar-wrapper").hasClass("open")){
			$(".sb-name").addClass("newSide")
		}else{
			$(".sb-name").removeClass("newSide")
		}

		if(!touch && $(window).width() < 768){
			if(!$("#sidebar-wrapper").hasClass("open")){
				$(".sidebarHeading").addClass("hide");
			}else{
				$(".sidebarHeading").removeClass("hide");
			}
		}
		if(touch && $(window).width() < 768){
			$(".sidebarHeading").addClass("hide");
		} 

		$sidebar.toggleClass('open');
		var s = $('#rightPanel').width();
		if($('#rightPanel').hasClass("active")){
			$('#rightPanel').removeClass('active');
			$('#rightPanel').addClass('inactive');
			$('#rightPanel').css('right', (s-(s*2)));
			$('.right_panel_icon').addClass('active');
		}

	}

	windowWidth =  $(window).width();
	if($wrapper.hasClass("toggled-2")){
		$wrapper.removeClass("toggled-2");
		if (windowWidth > 767 ){
			$(this).addClass('active');
		}
	}else{
		$("#page-content-wrapper").removeClass("new")
		if (windowWidth > 767 ){
			$(".sidebarHeading").removeClass("hide")
			$(this).removeClass('active');
		}
		$wrapper.addClass("toggled-2");
	}

	

	// floorlist page fixed tab animate on sidebar hide and show
	if (windowWidth >= 768) {
		if($wrapper.hasClass("toggled-2")){
			$('.FloorListTab').addClass('FloorListTab-active');
			$('.fl-toast').addClass('fl-toast-active');
			$('.FloorListTab').removeClass('FloorListTab-inactive');
			$('.fl-toast').removeClass('fl-toast-inactive');
		}else{
			$('.FloorListTab').addClass('FloorListTab-inactive');
			$('.fl-toast').addClass('fl-toast-inactive');
			$('.FloorListTab').removeClass('FloorListTab-active');
			$('.fl-toast').removeClass('fl-toast-active');
		}

	};

	// networkconfig page animate on sidebar hide and show
	if (windowWidth >= 768) {
		if($wrapper.hasClass("toggled-2")){
			$('.networkconfig-sec .main-section-activity').addClass('main-section-active');
			$('.networkconfig-sec .main-section-activity').removeClass('main-section-inactive');
		}else{
			$('.networkconfig-sec .main-section-activity').removeClass('main-section-inactive');
			$('.networkconfig-sec .main-section-activity').removeClass('main-section-active');
		}
	};
});
// select box color value change
$('select').click(function(){
	$(this).removeClass('active');
})


// topbar sidebar close when sidebar open 
$(".toggle-align").on("click",function(){
	setTimeout(function() {
		$sidebar.removeClass("open");
	}, 100);
})
//plot
$(".plot").on("click",function(){
	
	url="facesix/web/site/portion/list?sid=58b6f1a4e77b2a53e552b7ab&cid=58b3e6d2e77b2a290d5da0a0";
});
// floorlist search
$(".FloorListTab .search-align,.FloorListTab .search-box .icon").hover(function(){
	$(".FloorListTab .search-opt .search-box .search-align").addClass('active-search');
	$('.FloorListTab .search-opt .search-box .search-align').attr("placeholder", "Search Floor...");
},function(){
	$(".FloorListTab .search-opt .search-box .search-align").removeClass('active-search');
	$('.FloorListTab .search-opt .search-box .search-align').attr("placeholder", "");
});

$(".log-sec .lth-right .search-align, .log-sec .lth-right .search-box .icon").hover(function(){
	$(".log-sec .lth-right .search-opt .search-box .search-align").addClass('active-search');
	$('.log-sec .lth-right .search-opt .search-box .search-align').attr("placeholder", "Search Logs...");
},function(){
	$(".log-sec .lth-right .search-opt .search-box .search-align").removeClass('active-search');
	$('.log-sec .lth-right .search-opt .search-box .search-align').attr("placeholder", "");
});

$(".lth-right.searchVenues .search-box .icon,.lth-right.searchVenues .search-box .search-align").hover(function(){
	$(".lth-right.searchVenues .search-box .search-align ").addClass('active-search');
	$('.lth-right.searchVenues .search-box .search-align').attr("placeholder", "Search Venues...");
},function(){
	$(".lth-right.searchVenues .search-box .search-align").removeClass('active-search');
	$('.lth-right.searchVenues .search-box .search-align').attr("placeholder", "");
});

$(".lth-right.searchUID .search-box .icon,.lth-right.searchUID .search-box .search-align").hover(function(){
	$(".lth-right.searchUID .search-box .search-align ").addClass('active-search');
	$('.lth-right.searchUID .search-box .search-align').attr("placeholder", "Search UID/Name...");
},function(){
	$(".lth-right.searchUID .search-box .search-align").removeClass('active-search');
	$('.lth-right.searchUID .search-box .search-align').attr("placeholder", "");
});

// logs page script
if (windowWidth <= 1030) {
	$('.logsSection.main-section-activity').addClass("col-sm-12")
	$('.logsSection.main-section-activity').removeClass("col-sm-9 col-md-9")
}

// homepage banner and conntact page banner
if (windowWidth >= 768) {
	function setHeight() {
		var windowHeight = $(window).height();
		$('#banner, #contact_banner').css('height', windowHeight);
	};
	setHeight();
}
$(window).resize(function() {
	setHeight();
});

// sidebar overscroll avoid
function setHeight() {
	var windowHeight = $(window).height();
	$("#sidebar-wrapper .LogsSection").css('height', windowHeight);
};
setHeight();

$(window).resize(function() {
	setHeight();
});

// addvenue & create venue page column change
function venueRefresh() {
	if($(window).width() < 1100)
	{   
		$(".change-col").removeClass("col-md-6 col-md-12 col-md-12").addClass("col-md-12").css("margin-bottom","15px");
	}
}
venueRefresh();

$(window).resize(function() {
	venueRefresh();
});

function venueNoRefresh() {
	if($(window).width() > 1100)
	{   
	/*	$(".change-col").removeClass("col-md-12");
		$(".venuepeerChart").addClass("col-md-6");
		$(".venueCarousel").addClass("col-md-12");
		$(".venueAlertAct").addClass("col-md-12");
	*/
	}
}
venueNoRefresh()

$(window).resize(function() {
	venueNoRefresh();
});

// addvenue collapse
$(".network-heading.venueCollapse").click(function(){
	if($(this).parent().siblings().hasClass("hide")){
		$(this).parent().siblings().slideToggle();
	}else{
		$(this).parent().siblings().slideToggle();
	}
})

// homepage search
$(".search-mob-hide").click(function(){
	$(this).addClass("hide");
	$(".network-heading").addClass("hide");
	$(".search-opt").removeClass("hide");
});


$("body").on('mouseover','.floor-content',function() {
	$(this).parent('div').find('.floor-dot').addClass('hide');
	$(this).parent('div').find('.floor-options').addClass('animated fadeInDown');
	$(this).parent('div').find('.floor-options').show();
	$(this).find(".listViewLink").removeClass("hide")
})
$("body").on("mouseleave",".floor-content",function() {
	$('.floor-dot').removeClass('hide');
	$('.floor-options').hide();
	$(".listViewLink").addClass("hide");
});

// PDF Gif animation
$(function(){
	$('.pdf-Option img').hover(function(){
     // on mouse enter
     var customdata = $(this).parent().attr('href');
     $(this).attr('src',customdata); 
 }, function(){
      // on mouse leave
      $(this).attr('src',$(this).attr('data-orig'));
  });
});
// PDF Gif animation
$(function(){
	$('.submit1 img').hover(function(){
     // on mouse enter
     var customdata = "/facesix/static/qubercomm/images/floorlist/delete-hover.png";
     $(this).attr('src',customdata); 
 }, function(){
      // on mouse leave
      $(this).attr('src',$(this).attr('data-orig'));
  });
  
});

var acl_val = "Whitelist";

 $("body").on('click','.submitDelete',function(evt){
 

 		
     	$("#custband2g").css("outline", "solid 1px  black");
     	$("#custband5g").css("outline", "solid 1px  black");

  		evt.preventDefault();
  		
  	 	$("#custband2g").prop('checked', true);
 		$("#custband5g").prop('checked', true);	
  		
  		$(".savearea").show();
  		$("#deleteItem").attr("data-target",$(this).attr("href"));
  })
  
  $("body").on('click','.submitMesh',function(evt){
     	

     	
     	$("#custdualband2g").css("outline", "solid 1px  black");
     	$("#custdualband5g").css("outline", "solid 1px  black");
     	
  		evt.preventDefault();
  		
  	    $("#custdualband2g").prop('checked', true);
 		$("#custdualband5g").prop('checked', true);
 		
  		$(".saveareaone").show(); 		
  })
  
  $("body").on('click','#deleteItem',function(evt){
	 
	   var devtype = $(this).attr("devtype");
  	   var href=$(this).attr("data-target");
  	   var action=$(this).attr("action");
  	   var ap = $(this).attr("ap");
  	   var uid = $(this).attr("uid");
  	 
  	   console.log("ap " + ap +" "+ action); 
  	   if (ap == "Sensor" && action == "reset" ) {
  		 findrpc(uid,"?","?", "RESET");
  		 return;
  	   } else if (ap == "Sensor" && action == "reboot") {
  		 findrpc(uid,"?","?", "RESTART");
  		 return;
  	   }
  	   
  	   if(action=="delete"){
  	   	  var url=$(this).attr("action-url");
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
  	   	  })
  	   }
  	   else
  	     {
         	var mac=$(this).attr("mac-id");

      		if (ap === acl_val) {
      			console.log ("acl is same");
      		} else {
      			ap = acl_val;
      			console.log ("acl is NA");
      		}
         	
         	if(action=="reset")
         		rpc(uid,"?","?", "RESET")
         	else if(action=="reboot")
         		rpc(uid,"?","?", "RESTART")
         	else if(action=="block")
         		blk(uid,mac);
         	else if(action=="unblock")
         		rpcACL(uid,$(this).attr("ap"),mac,"ACL")
         	else if(action=="acl"){
         		mac=uid;
         		rpcACL(uid,$(this).attr("ap"),mac,"ACL")
         	}
         	else if(action=="remove")
         		rpc(uid,ap,mac,"DELSTA")
         	else if(action=="report")
         		rpc(uid,"?",mac,"REPORT")
         	else if(action=="qcast")
         		rpcQcast(uid,"?",mac,"QCAST")
         	else if(action=="kill")
         		rpcQcast(uid,"?",mac,"KILL")
         	else if(action=="refresh")
         		rpcQcast(uid,"?",mac,"REFRESH")
         	else if(action=="qclose")
         		rpcQcast(uid,"?",mac,"QCLOS")
			else if(action=="qcastReset")
         		rpcQcast(uid,"?",mac,"QCASTRESET") 
         	else if(action=="qacl")
         		rpcACL(uid,ap,mac,"QACL")
           	else if(action=="racl")
         		rpcACL(uid,ap,mac,"RACL")	
         	else{
         		window.location.href=href;
         	}
  	     }
  })
  $("body").on("click",'#cancelDelete',function(evt){
  		$(".rebootPopup").hide();
  		$(".savearea").hide();
  })	
  
  $("body").on('change','#aclval',function(evt){
    acl_val = this.value;
	console.log ("Changed");
	console.log (acl_val);
  })
  
//Restart and Power off function


function rpcACL(uid, ap, macid,cmd) {
 
	 	var sid  	= location.search.split("&")[0].replace("?","").split("=")[1];
		var spid = location.search.split("&")[1].replace("?","").split("=")[1];
		var cid  	= location.search.split("&")[2].replace("?","").split("=")[1];
		
   	var url="/facesix/rest/device/rpcACL?args=none&uid=" + uid + "&ap="+ap + "&mac="+macid + "&cmd="+cmd+"&sid="+sid+"&spid="+spid+"&cid="+cid;
   	console.log(url);
   	
   	$.ajax({
   	  	url:url,
   	  	method:'POST',
   	  	success:function(response,error){
           location.reload();
   	  	},
   	  	error:function(error){
   	  		//console.log(error);	
   	  	}
   	 });
   	 
   	 $(".rebootPopup").hide();
   	 
   	
	return false;
}

function rpcQcast(uid, ap, macid, cmd) {
 
   	var url="/facesix/rest/device/rpcQcast?args=none&uid=" + uid + "&ap="+ap + "&mac="+macid + "&cmd="+cmd;
   	$.ajax({
   	  	url:url,
   	  	method:'POST',
   	  	success:function(response,error){
           //Called on successful response
   	  	},
   	  	error:function(error){
   	  		//console.log(error);	
   	  	}
   	 })	
   	 
   	 $(".rebootPopup").hide();
   	 
   	
	return false;
}

function findrpc(uid, ap, macid, cmd) {
	if(uid=="?")
		return false;
  	var url="/facesix/rest/beacon/device/rpc?args=none&uid=" + uid + "&ap="+ap + "&mac="+macid + "&cmd="+cmd;
  	console.log("find rpc" + url);
   	$.ajax({
   	  	url:url,
   	  	method:'POST',
   	  	success:function(response,error){
           //Called on successful response
   	  	},
   	  	error:function(error){
   	  		//console.log(error);	
   	  	}
   	 })	
   	 
   	 $(".rebootPopup").hide();
   	 
   	 if (cmd == "BLOCK" || cmd == "UNBLOCK") {
		location.reload();
   	 }
	
	return false;
}

function rpc(uid, ap, macid, cmd) {
	if(uid=="?")
		return false;
	
  	var url="/facesix/rest/device/rpc?args=none&uid=" + uid + "&ap="+ap + "&mac="+macid + "&cmd="+cmd;
   	$.ajax({
   	  	url:url,
   	  	method:'POST',
   	  	success:function(response,error){
           //Called on successful response
   	  	},
   	  	error:function(error){
   	  		//console.log(error);	
   	  	}
   	 })	
   	 
   	 $(".rebootPopup").hide();
   	 
   	 if (cmd == "BLOCK" || cmd == "UNBLOCK") {
		location.reload();
   	 }
	
	return false;
}

// Blocked MACID function
function blk(uid, macid) {

	//console.log ("Table Selected ROW Start==>");
	
	var client_t  = block_row[0].cells[1].innerHTML;
	var devname_t = block_row[0].cells[2].innerHTML;
	var macid_t   = block_row[0].cells[3].innerHTML;
	var radio_t   = block_row[0].cells[4].innerHTML;
	var ap_t 	  = block_row[0].cells[5].innerHTML;
	var ssid 	  = block_row[0].cells[6].innerHTML;
	var tx 	 	  = block_row[0].cells[7].innerHTML;
	var rx 	  	  = block_row[0].cells[8].innerHTML;
	var rssi_t 	  = block_row[0].cells[9].innerHTML;
	
	//console.log ("Table Selected ROW End==>");

 	var networkconfig={
   		client:client_t,
   		devname:devname_t,
   		uid:macid_t,
   		radio:radio_t,
   		ap:ap_t,
   		ssid:ssid,
   		tx:tx,
   		rx:rx,
   		rssi:rssi_t,
   		pid:uid,
 	};
     	
  	var url="/facesix/rest/site/portion/clientdevice/save";
   	$.ajax({
   	  	url:url,
   	  	method:'POST',
        data:JSON.stringify(networkconfig),
 		headers: {
             'content-type': 'application/json'
        },  	   	  	
   	  	success:function(response,error){
   	  		location.reload();
   	  	},
   	  	error:function(error){
   	  		//console.log(error);	
   	  	}
   	 })
   	 
   	block_row = null;
   	$(".rebootPopup").hide();
   
	return true;
}

$("body").on('click','.floor-action',function() {
	$(this).addClass('hide');
	$(this).parent('.floor-head').find('.fh-options').removeClass('hide fadeOutUp').addClass('fadeInDown');
});
/*$("body").on('mouseleave',".fh-options",function(){
	$('.floor-action').removeClass('hide');
	$(this).addClass('fadeOutUp');
});*/

// floorlist icon on click function
$('.listView').click(function(){
	$(this).addClass('hide');
	$('#grid_view').addClass('hide');
	$('#list_view').removeClass('hide');
	$('.gridView').removeClass('hide').css("display","inline-block");
})

// gridlist icon on click function
$('.gridView').click(function(){
	$(this).addClass('hide');
	$('#grid_view').removeClass('hide')
	$('#list_view').addClass('hide');
	$('.listView').removeClass('hide').css("display","inline-block");
})

var block_row;

// Right click option on devicedashboard table
//function setContextMenu(){
function rightMenu(event) {
	event.preventDefault();
	$(".custom-menu").finish().toggle(100).
	css({
		top: event.pageY?event.pageY:event.originalEvent.pageY+ "px",
		left: event.pageX?event.pageX:event.originalEvent.pageX + "px"
	});
	block_row = $(this).parent().closest('tr').clone();
	
	$(".custom-menu .block").attr({"action-url":"/facesix/static/qubercomm/dashboard.json","macId":$(this).parent().attr("mac-id"),"action":"block","uid":$(this).parent().attr("uid"),"ap":$(this).parent().attr("ap"),"ssid":$(this).parent().attr("ssid")})
	$(".custom-menu .delete").attr({"action-url":"/facesix/static/qubercomm/dashboard.json","macId":$(this).parent().attr("mac-id"),"action":"remove","uid":$(this).parent().attr("uid"),"ap":$(this).parent().attr("ap"),"ssid":$(this).parent().attr("ssid")})
}	
$(document).on("contextmenu",'table .showPopup ',rightMenu);
$(document).on("tap",'table .showPopup ',rightMenu);

function blockMenu(event) {
	event.preventDefault();
	$(".custom-menu-block").finish().toggle(100).
	css({
		top: event.pageY?event.pageY:event.originalEvent.pageY+ "px",
		left: event.pageX?event.pageX:event.originalEvent.pageX + "px"
	});
	
	$(".custom-menu-block .unblock").attr({"action-url":"/facesix/static/qubercomm/dashboard.json","macId":$(this).parent().attr("mac-id"),"action":"unblock","uid":$(this).parent().attr("uid"),"ap":$(this).parent().attr("ap"),"ssid":$(this).parent().attr("ssid")})
}

$(document).on("contextmenu",'table .blockPopup ',blockMenu);
$(document).on("tap",'table .blockPopup ',blockMenu);

$(document).on("click",".handle",function(evt){
	evt.preventDefault();
	var actionurl=$(this).attr("action-url");
	var action=$(this).attr("action");
	var macId=$(this).attr("macId");
	var uid=$(this).attr("uid");
	var ap=$(this).attr("ap");
	var ssid=$(this).attr("ssid");
	$("#deleteItem").attr({action:action,"action-url":actionurl,"mac-id":macId,"uid":uid,"ap":ap,"ssid":ssid});
	$(".left-section label").text("Are you sure you want to "+action+" the station?");
	$(".savearea").show();
	$(".custom-menu").toggle(100);
	$(".custom-menu-block").toggle(100);
})
//}
//setContextMenu();
$(document).bind("mousedown", function (e) {
	if (!$(e.target).parents(".custom-menu").length > 0) {
		$(".custom-menu").hide(100);
	}
	
	if (!$(e.target).parents(".custom-menu-block").length > 0) {
		$(".custom-menu-block").hide(100);
	}	
});

// Side bar sibling onclick show
$(".sidebar-nav li>ul").hide();
$(".sidebar-nav li").on("click",function(){
	$(".sidebar-nav li").siblings().removeClass("active");
	$(this).siblings().find('ul').hide();
	if($(this).has("ul").length){
		$(this).find('ul').slideToggle();
		$(this).addClass("active");
	}
});

if(windowWidth <=1050){
	$('.LogsSection').addClass('logs-Network');
}


//networkconfig page canvas height & width
if($(window).width() < 1200){
	var setheight = $(window).height()-200
	$('.maping-canvas').css("height", setheight);
}

var setheight = $(window).height()-150
$('.maping-canvas').css("height", setheight);
var canvasHeight=$(".maping-canvas").height();
$("#mapSVG").attr("height",450);  

$(window).resize(function() {
	var setheight = $(window).height()-150
	$('.maping-canvas').css("height", setheight); 
	var canvasHeight=$(".maping-canvas").height();
	$("#mapSVG").attr("height",450);   
});
$(document).on("keyup",".searchLogs",function(evt){
		evt.preventDefault();
		var searchText=$(this).val();
		if(searchText && searchText.length){
		    $(".log-table tbody tr").each(function(){
		    	var tdText=$(this).children().first().text();
		    	tdText=tdText.replace("[","").split(" ");
		    	if(tdText[0].toLowerCase().indexOf(searchText.toLowerCase())!=-1 || tdText[1].indexOf(searchText)!=-1 || tdText.join(" ").indexOf(searchText)!=-1)
		    		$(this).show();
		    	else
		    		$(this).hide();
		    })
		}else{
			$(".log-table tr").show();
		}
})
$(document).on("click",".pdfOption",function(evt){
	window.open("/facesix/rest/site/portion/networkdevice/pdf", "_blank");  
})

$(document).on("click",".export",function(evt){
	window.open("/facesix/rest/site/portion/networkdevice/export", "_blank");
})



// floorlist hide below 1024
function floorListResize(){
	if($(window).width() <= 1024)
	{
		$("#list_view").addClass("hide")
		$("#grid_view").removeClass("hide")
		$(".gridView").addClass("hide")
	}else if ($(window).width() > 1024) {
		if($('.floor-list').hasClass("hide")){
			$('.gridView').addClass("hide")
			$('.listView').removeClass("hide")
		}else if($('.gridlist').hasClass("hide")){
			$('.gridView').removeClass("hide")
			$('.listView').addClass("hide")
		}
		
	};
}
floorListResize();

// right sidebar desktop and mobile 
$(".right_panel_icon").click(function(e){
    e.preventDefault();
    $(".sb-name").removeClass("newSide")
    if($(window).width() < 768){
        $("#sidebar-wrapper").removeClass("open");
        if (!$("#sidebar-wrapper").hasClass("open")) {
            $(".sidebarHeading").removeClass("hide")
        }
    }
    var s = $('#rightPanel').width();
    if($('#rightPanel').hasClass("active")){
        if($(window).width() <= 768){
            $('.common-slide').removeClass("postion-f OverscrollFixed");
        }
        $('#rightPanel').removeClass('active');
        $('#rightPanel').addClass('inactive');
        $('#rightPanel').css('right', (s-(s*2)));
        $('.right_panel_icon').addClass('active');
    }else if($('#rightPanel').hasClass("inactive")){
        if($(window).width() <= 768){
            $('.common-slide').addClass("postion-f OverscrollFixed");
        }
        $('#rightPanel').addClass('active');
        $('#rightPanel').removeClass('inactive');
        $('#rightPanel').css('right', 0);
        $('.right_panel_icon').removeClass('active');
    }
});

$(window).resize(function() {
	floorListResize();
	
	var touch = 'ontouchstart' in document.documentElement?true:false
	if(!touch){
		if($('#rightPanel').hasClass("inactive")){
			$('.common-slide').removeClass("postion-f OverscrollFixed");
		}
	}
});


// floordashboard page below 1100px column change
function floorResize(){
	if($(window).width() <= 1100)
	{
		$(".floordash-col").removeClass("col-md-8 col-md-4 col-md-3").addClass("col-md-12 margin-bottom-20");
	}
}
floorResize();

function floorNoResize(){
	if($(window).width() > 1100)
	{
		$(".floordash-col").removeClass("col-md-12");
		$(".floorChartLine").addClass("col-md-3");
		$(".floorCanvas").addClass("col-md-8");
		$(".flooraddCommon").addClass("col-md-4");
	}
}

$(window).resize(function() {
	floorNoResize();
	floorResize();
	vdResize();
	vdNoResize();
	deviceResize();
	deviceNoResize();
});

// 	venuedashboard page below 1100px column change
function vdResize(){
	if($(window).width() <= 1100)
	{
		$(".venue-col").removeClass("col-md-8 col-md-4").addClass("col-md-12 margin-bottom-20");
	}
}
vdResize();

function vdNoResize(){
	if($(window).width() > 1100)
	{
		$(".venue-col").removeClass("col-md-12");
		$(".vdcol-8").addClass("col-md-8");
		$(".vdcol-4").addClass("col-md-4");
	}
}
vdNoResize();

// 	devicedashboard page below 1100px column change
function deviceResize(){
	if($(window).width() <= 1100)
	{
		$(".device-col").removeClass("col-md-6 col-md-3 col-md-12").addClass("col-md-12 margin-bottom-20");
	}
}
deviceResize();

function deviceNoResize(){
	if($(window).width() > 1100)
	{
		$(".device-col").removeClass("col-md-12");
		$(".devicecol-3").addClass("col-md-3");
		$(".devicecol-6").addClass("col-md-6");
	}
}
deviceNoResize();


$('.pdf-Option a').click(function(e){
	e.preventDefault();
})

if (windowWidth < 768) {
	function setHeight() {
		var windowHeight = $(window).height();
		$('.common-slide').css('height', windowHeight);
	};
	setHeight();
}

$(window).resize(function() {
	setHeight();
});

var activeFloors = [];
var currentView = 'gridview';
// FloorList
$('body').on('click', '.select-floor-grid-view', function () {
	$(this).toggleClass('active');
	$(this).closest('li').find('.floor-content').toggleClass('active');
	$(this).closest('li').find('.floor-tools').toggleClass('none');
	var $activeList = $('.select-floor-grid-view.active');
	var $selectFloor = $('.floor-count .select-floor');
	var $flToast = $('.fl-toast');
	var sid = $(this).attr("data-sid");
	if ($('.select-floor').hasClass('active')) {
		$flToast.css('bottom', 0);
	} else {
		$flToast.css('bottom', -100);
	}
	if ($activeList.length == $('.select-floor-grid-view').length) {
		$selectFloor.addClass('active');
	} else {
		$selectFloor.removeClass('active');
	}
	if ($(this).hasClass('active')) {
		activeFloors.push(sid);
		activeFloors = _.uniq(activeFloors)
	} else {
		activeFloors = _.without(activeFloors, sid);
	}
	$('.count-number').text($activeList.length);
});
$('body').on('click', '.select-floor-list-view', function () {
	$(this).toggleClass('active');
	$(this).closest('li').find('.floor-content').toggleClass('active');
	$(this).closest('li').find('.floor-tools').toggleClass('none');
	var $activeList = $('.select-floor-list-view.active');
	var $flToast = $('.fl-toast');
	var $selectFloor = $('.floor-count .select-floor');
	var sid = $(this).attr("data-sid");
	if ($('.select-floor').hasClass('active')) {
		$flToast.css('bottom', 0);
	} else {
		$flToast.css('bottom', -100);
	}
	if ($activeList.length == $('.select-floor-list-view').length) {
		$selectFloor.addClass('active');
		activeFloors.push(sid);
	} else {
		$selectFloor.removeClass('active');
		activeFloors = _.without(activeFloors, sid);
	}
	if ($(this).hasClass('active')) {
		activeFloors.push(sid);
		activeFloors = _.uniq(activeFloors)
	} else {
		// activeFloors.push(sid);
		_.without(activeFloors, "2");
	}
	$('.count-number').text($activeList.length);
});

// floorlist icon on click function
$('body').on('click', '.listView', function () {
	var $selectFloor = $('.floor-count .select-floor');
	var $selectFloorListView = $('.select-floor-list-view');
	$(this).addClass('hide');
	$('#grid_view').addClass('hide');
	$('#list_view').removeClass('hide');
	$('.gridView').removeClass('hide').css("display", "inline-block");
	$selectFloor.removeClass('floorGridView');
	$selectFloor.addClass('floorListView');
	$('.count-number').text(activeFloors.length);
	_.each($selectFloorListView, function (item) {
		if (_.indexOf(activeFloors, $(item).attr("data-sid")) > -1) {
			$(item).addClass('active');
			$(item).closest('li').find('.floor-content').addClass('active');
            $(item).closest('li').find('.floor-tools').addClass('none');
		} else {
			$(item).removeClass('active');
			$(item).closest('li').find('.floor-content').removeClass('active');
            $(item).closest('li').find('.floor-tools').removeClass('none');
		}
	});
	currentView = 'listview';
});

// gridlist icon on click function
$('body').on('click', '.gridView', function () {
	var $selectFloor = $('.floor-count .select-floor');
	var $selectFloorGridView = $('.select-floor-grid-view');
	$(this).addClass('hide');
	$('#grid_view').removeClass('hide')
	$('#list_view').addClass('hide');
	$('.listView').removeClass('hide').css("display", "inline-block");
	$selectFloor.removeClass('floorListView');
	$selectFloor.addClass('floorGridView');
	$('.count-number').text(activeFloors.length);
	_.each($selectFloorGridView, function (item) {
		if (_.indexOf(activeFloors, $(item).attr("data-sid")) > -1) {
			$(item).addClass('active');
		} else {
			$(item).removeClass('active');
		}
	});
	currentView = 'gridview';
});
$('body').on('click', '.copyIcon', function (e) {
	if (currentView == "gridview") {
		var sid = $(this).closest('.floor-head').find('.select-floor').attr('data-sid');
	} else {
		var sid = $(this).closest('.list_item').find('.select-floor-list-view').attr('data-sid');
	}
	if (sid) {
		sid = [].concat(sid);
	} else {
		sid = activeFloors;
	}
	if (sid.length) {
		var data = JSON.stringify({ ids: sid, count: sid.length });
		$.ajax({
			url: 'copytest',
			data: data,
			success: function (result) {

			},
			error: function (data) {
				//console.log(data);

			},
			type: "POST",
			contentType: "application/json"
		});
	}

});

search = window.location.search.substr(1)
urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')

$('body').on('click', '.deleteIcon', function (e) {
	if (currentView == "gridview") {
		var spid = $(this).closest('.floor-head').find('.select-floor').attr('data-sid');
	} else {
		var spid = $(this).closest('.list_item').find('.select-floor-list-view').attr('data-sid');
	}
	if (spid) {
		spid = [].concat(spid);
	} else {
		spid = activeFloors;
	}
	$.each( spid, function( k, v ){
	  //console.log( "Key: " + k + ", Value: " + v );
		$.ajax({
			url: '/facesix/web/site/portion/delete?sid='+urlObj.sid+'&spid='+v,
			success: function (result) {				
				location.reload();
			},
			error: function (data) {
				//console.log(data);
				location.reload();
			},
			type: "GET",
		});	  
	});	
});

var sortedFloorList = [];

function sortFloorList(url) {
	if (sortedFloorList && sortedFloorList.length) {
		if (window.isReverseList) {
			sortedFloorList = sortedFloorList.reverse();
		}
		var source = $("#floorlist-template").html();
		var template = Handlebars.compile(source);
		if (currentView == "gridview") {
			var showGridView = '';
			var showListView = 'hide';
		} else {
			var showGridView = 'hide';
			var showListView = '';
		}
		var rendered = template({
			"floorList": sortedFloorList,
			"show_grid_view": showGridView,
			"show_list_view": showListView,
		});
		$('#floorList').html(rendered);
		window.isReverseList = window.isReverseList ? false : true;
	} else {
		$.ajax({
			url: url,
			success: function (result) {
				if (result.sortedData && result.sortedData.length) {
					sortedFloorList = _.sortBy(result.sortedData, 'floor_name')
					if (window.isReverseList) {
						sortedFloorList = sortedFloorList.reverse();
					}
					var source = $("#floorlist-template").html();
					var template = Handlebars.compile(source);
					if (currentView == "gridview") {
						var showGridView = '';
						var showListView = 'hide';
					} else {
						var showGridView = 'hide';
						var showListView = '';
					}
					var rendered = template({
						"floorList": sortedFloorList,
						"show_grid_view": showGridView,
						"show_list_view": showListView,
					});
					$('#floorList').html(rendered);
					window.isReverseList = window.isReverseList ? false : true;
				}
			},
			error: function (result) {
				//console.log(result);
			},
			dataType: "json"
		});
	}
}
$(document).on("click","")
$("body").on('click', '.sortView', function () {
	//sortFloorList('/facesix/static/qubercomm/dashboard.json', window.isReverseList);
});

$("body").on("click",".captureIcon",function(evt){
	window.open("/facesix/rest/site/portion/networkdevice/imgcapture", "_blank");  	
})

$("body").on('click', '.floor-count .select-floor', function () {
	var $selectFloorListView = $('.select-floor-list-view');
	if ($(this).hasClass('active')) {
		if ($(this).hasClass('floorGridView')) {
			$('.select-floor-grid-view').removeClass('active');
			$('.count-number').text($('.select-floor-grid-view.active').length);
			activeFloors = [];
		} else {
			$('.select-floor-list-view').removeClass('active');
			$('.count-number').text($('.select-floor-list-view.active').length);
			activeFloors = [];
		}
		$(".floorListItem .floor-content").removeClass("active");
		$(".floorListItem .floor-tools").removeClass("none");
		$(".fl-toast").css('bottom','-100px')
		$(this).removeClass('active');

	} else {
		if ($(this).hasClass('floorGridView')) {
			$('.select-floor-grid-view').addClass('active');
			$('.count-number').text($('.select-floor-grid-view.active').length);
			activeFloors = [];
			$.each($('.select-floor-list-view'), function (key, item) {
				var sid = $(item).attr('data-sid');
				activeFloors.push(sid);
			});
		} else {
			$('.select-floor-list-view').addClass('active');
			$('.count-number').text($('.select-floor-list-view.active').length);
			activeFloors = [];
			$.each($('.select-floor-grid-view'), function (key, item) {
				var sid = $(item).attr('data-sid');
				activeFloors.push(sid);
			});
			$(".floorListItem .floor-content").addClass("active");
			$(".floorListItem .floor-tools").addClass("none")	

		}
		$(this).addClass('active');
	}


});

$('.floor-list-search').on('keyup', function (e) {
	var that = this;
	var $floorListArr = $('.floorListItem');
	var searchText = $(that).val();
	if (searchText.length && searchText.length > 0) {
		for (var i = 0; i < $floorListArr.length; i++) {
			var floorName = $($floorListArr[i]).attr('data-floor-name');
			if (floorName.toLowerCase().indexOf(searchText.toLowerCase()) != 0) {
				$($floorListArr[i]).hide();
			}
		}
	} else {
		$('.floorListItem').show();
	}

});

$(function(){
    $('.sidebaricon').hover(function(){
    // on mouse enter
    var customdata = "/facesix/static/qubercomm/images/sidebaricon.png"
    $(this).children().attr('src',customdata); 
}, function(){
     // on mouse leave
     $(this).children().attr('src',$(this).children().attr('data-orig'));
 });
});

var sideNetworkTree={
	search:function(uid){
	   $(".device-name").removeClass("current");	
       $("div[data-uid='"+uid+"']").addClass("current")
	},
	highlightDevice:function(search){
		$(".device-name").each(function(){
			var uid=$(this).attr("data-uid").replace(/:\s*/g, "");
			var toMatch=search.replace(/:\s*/g, "");
			// var length=toMatch.length;
			// var match="";
			// for(var i=0;i<length-1;i++)
			// 	match+=uid[i];
			// match=match.substr(0,length);
			if(uid.toLowerCase().indexOf(toMatch)!=-1)
				$(this).addClass("current");
		})	
	},
	filter:function(device){
       $(".device-name").removeClass("current");
       $("div[data-type='"+device+"']").addClass("current")
	},
	topologySearch:function(uid){
       var networkTree=factoryObj.network.networkTree;
       for(var key in networkTree)
           this.getDeviceTree(networkTree[key].device,uid);  
	},
	getDeviceTree:function(device,uid){
		var current=device;
		var self=this;
		var findDeviceByUid=function(currentDevice,uid){
			if(currentDevice.uid==uid){
				self.reRenderMap(currentDevice)
				return
			}
			var children=currentDevice.children?currentDevice.children:currentDevice._children
			if(!children || children==null)
				return
			for(var i=0;i<children.length;i++)
				findDeviceByUid(children[i],uid);
		}	
		findDeviceByUid(current,uid)
	},
	reRenderMap:function(tree){
		//remove SVG First
        $("svg").remove();
        var devices = new networkDevice(tree.type, tree.uid, tree.status)
        var addChildRef = function(current, pid) {
            var childDevice = networkDevice(current.type, current.uid, current.status, true, pid);
            devices.addImmediateChild(childDevice);
            if (!current.children)
                current.children = current._children;
            current._children = null;
            for (var i = 0; i < current.children.length; i++)
                addChildRef(current.children[i],current.uid);
        }
        if (!tree.children)
            tree.children = tree._children;
        tree._children = null;
        for (var i = 0; i < tree.children.length; i++)
            addChildRef(tree.children[i], tree.uid);
        createMap(devices.device);
        svgEvents();
	},
	onUidChange:function(evt){
		evt.preventDefault();
		var value=$(evt.currentTarget).val();
		if(value.length>17){
	    	$(evt.currentTarget).val(value.substr(0,value.length-1))
	    	return false;
	    }
	    if(value[value.length-1]==":")
	    	return;
	    
		$(".device-name").removeClass("current"); 
		if(value.length>3)
		  sideNetworkTree.highlightDevice(value)
	},
	formSubmit:function(evt){
		evt.preventDefault();
		var uid=$(".search-align").val();
		sideNetworkTree.search(uid);
		$(".search-align").val("");
	},
	filterSelect:function(evt){
		evt.preventDefault();
		var type=$(this).find("a").text();
		sideNetworkTree.filter(type);
		$(".filter-dropdown").removeClass("open");
	},
	resetTree:function(evt){
		evt.preventDefault();
		$("svg").remove();
		createMap(shallowTree);
		$(".search-topo").val("");
		svgEvents();
	},
	handleMapSearch:function(evt){
		evt.preventDefault();
		var value=$(".search-topo").val();
		sideNetworkTree.topologySearch(value.toLowerCase());
		$(".closeicon").show();	
	},
	addEvents:function(){
		$("#uidForm").on("submit",this.formSubmit);
		//$(".search-align").attr("onInput","javascript:triggerChange(event)");
		$("#mapForm").on("submit",this.handleMapSearch);
		$(".closeicon").on("click",this.resetTree);
		$(".filter-style li").on("click",this.filterSelect);
	}
}
	sideNetworkTree.addEvents();
	function triggerChange(evt){
		if($(evt.target).hasClass("searchLogs"))
			return;
		sideNetworkTree.onUidChange(evt);
	}

//Tx Rx Select Option

$(".txrxswap").on("change",function(evt){
    evt.preventDefault();
    var id=$(this).attr("data-id");
    var txRx={
    	'Tx':'Rx',
    	'Rx':'Tx'
    };
    var value=$(this).val();
    if(value!="Tx" && value!="Rx")
    	value=["Tx","Rx"];
    if(currentDashboard.chartList && id)
    	currentDashboard.chartList[id].focus(value)
    else
    	currentDashboard.charts.getChart.txRx.focus(value);
})
$(document).on("change",".selectTime",function(evt){
		evt.preventDefault();
		var value=$(this).val();
		var actionUrl=$(this).attr("data-action");
		var params="time="+value;
		currentDashboard.init(params);
})	
$("body").on("click",".powerBtn",function(evt){
		evt.preventDefault();
		evt.stopPropagation();
		var type=$(this).attr("btn-type");
		var devtype = $(this).attr("devtype");
		var macId=$(this).attr("macId");
		var ap=$(this).attr("ap");
		if(devtype == "sensor" || devtype == "Sensor") {
			$("#deleteItem").attr({"data-target":$(this).attr("data-action"),"action":type,"uid":$(this).attr("uid"), "ap":devtype})
		} else {
			$("#deleteItem").attr({"data-target":$(this).attr("data-action"),"action":type,"uid":$(this).attr("uid"), "ap":ap})
		}
		
	
		console.log("click function");
	 	if(type=="kill"){
			$(".rebootPopup").show();
			$(".rebootPopup .left-section label").show().text("Are you sure you want to Kill the device?");
		}  if(type=="refresh"){
			$(".rebootPopup").show();
			$(".rebootPopup .left-section label").show().text("Are you sure you want to Reset the device?");
		}
				
		if (devtype=="AP" || devtype=="Ap" || devtype=="ap" || devtype=="sensor" || devtype=="Sensor")
		{
			$(".rebootPopup").show();
			if(type=="reset"){
				$(".rebootPopup .left-section label").show().text("Are you sure you want to reset the device?");
			} else if(type=="report"){
				$(".rebootPopup .left-section label").show().text("Are you sure you need a report from the device?");
			}else if(type=="qcast"){
				$(".rebootPopup .left-section label").show().text("Are you sure you want to qcasting the device?");
			}else if(type=="qclose"){
				$(".rebootPopup .left-section label").show().text("Are you sure you want to close the device?");
			}else if(type=="qcastReset"){
				$(".rebootPopup .left-section label").show().text("Are you sure you want to reset the device?");
			}else if(type=="qacl"){
				$(".rebootPopup .left-section label").show().text("Are you sure you want to apply ACL to the device?");
			}else if(type=="racl"){
				$(".rebootPopup .left-section label").show().text("Are you sure you want to remove ACL lists from all access points?");
			}else if(type=="acl"){
				$(".rebootPopup .left-section label").show().text("Are you sure you want to commit ACL changes?");
			}else{			
				$(".rebootPopup .left-section label").show().text("Are you sure you want to power-off the device?");
			}
		}
});


if($(window).width()<755){
        $(".disablemenu").addClass("hide")
    }

   /* $(window).on("orientationchange",function(){
        if(window.orientation == 0)
        { 
          $(".disablemenu").addClass("hide")
        }
        else
        {
          $(".disablemenu").addClass("hide")
        }
});*/
if ($(window).width() > 1024) {
	if($(".scrollbar-inner").length){
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
}

if(!touch){
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
}

function sel_tbl1(col){
	var $currentTable = $('.sel_tbl1').closest('table');
	    $currentTable.find('td').removeClass('selected');
	  $currentTable.find('th').removeClass('selected');
	    $currentTable.find('tr').each(function() {
	        $(this).find('td').eq(col).addClass('selected');
	        $(this).find('th').eq(col).addClass('selected');
	    });
}
function sel_tbl2(col){
	var $currentTable = $('.sel_tbl2').closest('table');
	    $currentTable.find('td').removeClass('selected');
	  $currentTable.find('th').removeClass('selected');
	    $currentTable.find('tr').each(function() {
	        $(this).find('td').eq(col).addClass('selected');
	        $(this).find('th').eq(col).addClass('selected');
	    });
}
function sel_tbl3(col){
	var $currentTable = $('.sel_tbl3').closest('table');
	    $currentTable.find('td').removeClass('selected');
	  $currentTable.find('th').removeClass('selected');
	    $currentTable.find('tr').each(function() {
	        $(this).find('td').eq(col).addClass('selected');
	        $(this).find('th').eq(col).addClass('selected');
	    });
}
function sel_tbl4(col){
	/*$( ".cls").unbind( "click" );*/
	var $currentTable = $('.sel_tbl4').closest('table');
	    $currentTable.find('td').removeClass('selected');
	  $currentTable.find('th').removeClass('selected');
	    $currentTable.find('tr').each(function() {
	        $(this).find('td').eq(col).addClass('selected');
	        $(this).find('th').eq(col).addClass('selected');
	    });
}

var asc1 = 1, asc2 = 1, asc3 = 1;
function sort_table(tbody, col, asc) {
	p = document.getElementById(tbody);
	console.log("tty" + " " +  p);
    var rows = p.rows,
        rlen = rows.length,
        arr = new Array(),
        i, j, cells, clen;
    // fill the array with values from the table
    for (i = 0; i < rlen; i++) {
        cells = rows[i].cells;
        clen = cells.length;
        arr[i] = new Array();
        for (j = 0; j < clen; j++) {
            arr[i][j] = cells[j].innerHTML;
        }
    }

    /*
    // sort the array by the specified column number (col) and order (asc)
    arr.sort(function (a, b) {
        return (a[col] == b[col]) ? 0 : ((a[col] > b[col]) ? asc : -1 * asc);
    });
    // replace existing rows with new rows created from the sorted array
    for (i = 0; i < rlen; i++) {
        rows[i].innerHTML = "<td>" + arr[i].join("</td><td>") + "</td>";
    }
    */
    
    // sort the array by the specified column number (col) and order (asc)
    arr.sort(function(a, b)
    {
        var retval=0;
        var fA=parseFloat(a[col]);
        var fB=parseFloat(b[col]);
        if(a[col] != b[col])
        {
            if((fA==a[col]) && (fB==b[col]) ){ retval=( fA > fB ) ? asc : -1*asc; } //numerical
            else { retval=(a[col] > b[col]) ? asc : -1*asc;}
        }
        return retval;
    });
    
    for(var rowidx=0;rowidx<rlen;rowidx++)
    {
        for(var colidx=0;colidx<arr[rowidx].length;colidx++){ p.rows[rowidx].cells[colidx].innerHTML=arr[rowidx][colidx]; }
    }
}


var width = $(window).width();
if(width <= 991){
	$('#slide1').addClass('open');
	$('#slide1').find('span').removeClass('fabars');
	$('#wrapper').addClass('wrapperone');
	$('#sidebar-wrapper').addClass('sidebar-wrapperone');
}

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
	var location = window.location.href; 
	console.log(location);
	$('.navbar-nav a, .sidebar-nav a').each(function(){
		var thishref = $(this).attr('href');
		if(location.indexOf(thishref) != -1){
			$(this).parent('li').addClass('menuactive');
		}
		 
	}); 
	$('.menuactive > a').click(function(e){
		e.preventDefault();
	});
	
	var title = "Qubercomm | Compute Connect Cloud";
	var prefix = '';
	var c_cid = getParameterByName('cid'); 
	var url = '/facesix/rest/customer/paramValue?cid='+c_cid;
	$.ajax({
   	  	url:url,
   	  	method:'GET',
   	  	success:function(response,error){
   	  		prefix = response.pref_url;  
   	  		title = response.customerName; 
   	  		var favicon = response.logofile;
   	  		if(favicon != ''){
   	  		$('link[rel="shortcut icon"]').attr('href', '/facesix/static/qubercomm/images/favicon_blank.png?v=2');
   	  		}
   	  		$('title').html(title); 
   	  		if(prefix != ''){
   	  			$('#NavLogout > a').attr('href', '/facesix/goodbye/'+prefix);
   	  		}else{
   	  			$('#NavLogout > a').attr('href', '/facesix/goodbye');
   	  		}
   	  	},
   	  	error:function(error){
   	  		 
   	  	}
   	 });
	 

});

$('#header .navbar-right li').click(function(){ 
	if($(this).hasClass('menuactive') == false){ 
		$('#header .navbar-right li').removeClass('menuactive');
		$(this).addClass('menuactive');
	} else{
		$(this).removeClass('menuactive');
	} 
});