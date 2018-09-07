		var tabcnt 			= 0;
		var diagcnt 		= 0;
		var radio2g 		= 0;
		var interfaces2g 	= 0;
		var radio5g 		= 0;
		var interfaces5g 	= 0;
		var interface_mode	= "";
		var txPwrError 		= 0;
		
		function makempty() {
			radio2g = 0; interfaces2g =0;
	   	  	radio5g = 0; interfaces5g =0;
	   	  	$('#div2gr,#div2gi,#div5gr,#div5gi').html('');
		}
		function prefilldata(srvrdta){
			makempty();
			var jsondata = JSON.parse(srvrdta.replace(/&quot;/g,'"'));
			var inpid = "";
			var ctryval = "";
			$.each(jsondata, function (key, data) {
				if(key=="attributes") 			inpid = "2gr_";

			    $.each(data, function (i, data) {
			    	if(key=="attributes") {
			    		console.log ("TestKey==> " + key);
			    		console.log ("DataKey==> " + data.diag_key +" "+ data.diag_value);
			    		if (data.diag_key == undefined || data.diag_value == undefined) {
			    			addtab('#hid2gr','#div2gr',0);
			    			
			    		} else if (data.diag_key != undefined && data.diag_value != undefined ){
			    			add_diagtab('#addbox','#boxhead',0);
			    			$('#boxhead'+' .tabbox:last').fadeOut().remove();
			    			$('#2gr_diag_key_dbg',1).val(data.diag_key);
			    			$('#2gr_diag_value_dbg',1).val(data.diag_value);
			    			
			    		}
			    	}
			    	
			    	$.each(data, function (index, data) {
			    		/*if(data == 1) {
			    			$('#cbx_'+inpid+index+"_"+i).attr('checked','checked');
			    		}*/
			    		data = data.replace(/&amp;/g, '&');
			    		
			    		if(index=="acs"){
			    			acs = data.split(" ");
			    			hidval = data;
			    		} else if(index=="channel"){
			    			channel = data; 
			    		} else if(index=="fixedrate"){
			    			fixedrate = data; 
			    		} else if(index=="mcast"){
			    			mcast = data; 
			    		} else if (index=="acl"){
				    		interface_mode = $('#'+inpid+"mode"+"_"+i).val();
				    		if(interface_mode != "ap") 
				    			$('#'+inpid+index+"_"+i).parent().parent().hide();
			    		} else {
			    			$('#'+inpid+index+"_"+i).val(data);
			    		}
			    		if(index == "reg"){
			    			ctryval = data;
			    		}
			    		
				    })
				    if(key=="attributes") 
				    	if(key=="radio5g") {
						reg5g(ctryval,i);
				    	for(j=0;j<acs.length;j++){
		    				$('#'+inpid+"acs_"+i+'_'+acs[j]).prop("checked", true);
		    			}
		    			$('#'+inpid+"hid_"+i).val(hidval);
		    			$('#'+inpid+"channel_"+i).val(channel);
						
		    		} else if(key=="interfaces5g") {
		    			getModes($('#5gr_hwmode_0').val(),5,i);
		    			$('#'+inpid+"fixedrate_"+i).val(fixedrate);
		    			$('#'+inpid+"mcast_"+i).val(mcast);
					}
			    })
			}) 
		}
		function configFunc(vpn) {
			
			$.get('/facesix/template/finder/finder-config-source').then(function(responseData) {
			
			//	console.log ("Data ====>" + responseData + svrdata);
				$('#onloadata').html(responseData);
				if(srvrdta!=""){
						//console.log ("srvrdta ====>" + srvrdta.replace(/&quot;/g,'"') + ">>>>"+ srvrdta);
						prefilldata(srvrdta);
				} else {
					$('.tunnelHide').hide();
					console.log ("NOSVR ====>" );	
					addtab('#hid2gr','#div2gr',1);
					addtab('#hid2gi','#div2gi',1);
					addtab('#hid5gr','#div5gr',1);
					addtab('#hid5gi','#div5gi',1);
				}

                 var v = vpn;
                 //console.log("vpn value" + v)
                if(v == "true"){
                    v = "enable";
                } else {
                    v = "disable";
                }
                 fillconfig(v);
                 $('.tunnelHide').hide();
			});
			
			search = window.location.search.substr(1)
	        urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
            
			$(document).on('click', '.tabtn', function(){ 
				var attr =$(this ).attr('id');
				sowide(attr);
               var keyVal = location.search.split("&")[0].replace("?","").split("=")[0];
                console.log(">>>>>>>>>>>>>>" + keyVal);
                if(keyVal == "cid"){
                    $('#headRemove').remove();
                    add_diagtab('#addbox','#boxhead',0);
                }
                
			});
			$(document).on('change', '.livcbx', function(){
				var tmpid = '#'+ $(this).attr('id').replace("cbx_","");
				if(this.checked) {
					$(tmpid).val(1);
					
				} else {
					$(tmpid).val(0);
				}
					
			});
		
			
			$('#valsubmit').click( function() {
				

				$('#duplicate').hide();
				$("#deconfig").val('');
				
				var mytxt = "{";
				var myit = 0;
				
				var t1,t2 = "";
				
				t1 = makeJstr('#div2gr *','attributes',myit);
				mytxt += t1;
				if(mytxt!="{") myit = 1;
				
				t2 = makeJstr('#div2gi *','beacontags',myit);
				mytxt += t2;
				if(mytxt!="{") myit = 1;
				
				mytxt += '}';
				
				var requir = 0;
				$('#allfromdata .requir').each(function () {
					if($(this).val()==""){
						requir=1;
						$(this).css("border", "1px solid red");
						$(this).focus();
					}
				});
				
				var key_req = 0;
				$('#allfromdata .key_req').each(function () {
					
					console.log ("Key Value" + $(this).val());
					
					var isDisabled = $(this).prop('disabled');
					
					if($(this).val()=="" && isDisabled == false){
						key_req=1;
						$(this).css("border", "1px solid red");
						$(this).focus();
					}
				});
				
				var ok = 0;
								
				$('#h32gr,#h35gr,#h32gi,#h35gi').css("color", "#333");
				if(requir==1 || key_req==1) {
					ok = 1
				} 
				
				/*if(t1!="" && t2==""){
					$('#h32gi').css("color", "red");
					console.log(222);
					ok = 3
				} */
				var ssi,kk,bb,ss,tt;
			    ssi = $('#2gr_ssid_0').val();
			    kk  = $('#2gr_key_0').val();				
				bb  = $('#2gr_batteryinterval_0').val();				
				ss  = $('#2gr_statusinterval_0').val();
				tt  = $('#2gr_tluinterval_0').val();
				kl  = $('#2gr_keepaliveinterval_0').val();
				
				if (ssi=="" || tt=="" || bb=="" || kk=="" || ss=="" || kl==""){	
					$( "#tabtn-1-2" ).click();
					$('#tabtn-1-2').css("color", "red");
				} else{
					$('#tabtn-1-2').css("color", "black");
				}
				
				if(t1=="" && t2==""){
					$('#h32gr').css("color", "red");
					ok = 3
				} 
				
				if (txPwrError == 1) {
					ok = 7
				}
				
				if($('#flag').length > 0){
					console.log("--Venue/Floor----");
				} else {
					if($('#name').val()==""){
						$('#name').css("border", "1px solid red");
						$('#name').focus();
						ok = 5
					} 
					
					if($('#uuid').val()=="" || $('#uuid').val().length<17){
						$('#uuid').css("border", "1px solid red");
						$('#uuid').focus();
						ok = 6
					} 
				}
				
				console.log('-------'+ok)
				if(ok == 0){
					$("#deconfig").val(mytxt);
					$(".loader_box").show();
					$('#configform').submit();					
				}
				return false;
				
			});
			
			
		}
		function makeJstr(mydiv,mynam,ite) {
			var mytxt = "";
			var mode = "";
			var avl;
			var lastindex = -1;
			var bNan = false;
			
			avl = 0;
			$.each($(mydiv).serializeArray(), function(i, field) {
				avl++;
				if(avl==1) {
					if(ite==1) mytxt += ',';
					mytxt += '"'+mynam+'":[';
					mytxt += '{';
					lastindex = 0;
				}
				var ar = field.name.split('__');
				ar[0] = ar[0] * 1;
				
				console.log ("AR0 " + ar[0] + "AR1 " + ar[1])
				//if(lastindex < ar[0] && ar[1] !="diag_key" && ar[1] !="diag_value"){
				
				if (isNaN (ar[0]) && bNan == false) {
					ar[0] = lastindex + 1;
					bNan = true;
				}
				if (bNan == true && ar[0] == lastindex) {
					ar[0] = lastindex + 1;
				}
				
				if(lastindex < ar[0]){
					mytxt = mytxt.replace(/,\s*$/, "");
					mytxt += '},{';
					lastindex = ar[0];
				}
				
				mytxt += '"'+ar[1]+'":"'+field.value.trim()+'",';
				console.log ("mtxt " + mytxt)
				console.log ("Key " + ar[1])
				console.log ("Val " + field.value.trim())
				
			});
			mytxt = mytxt.replace(/,\s*$/, "");
			if(avl>0) mytxt += '}]';
			console.log('final'+mytxt);
			return mytxt;
		}
		function sowide(attr) {	
			//console.log(attr);
			var tid = attr.split("-");
			$(".tabtn"+tid[1]).removeClass('active');
			$("#tabtn-"+tid[1]+"-"+tid[2]).addClass('active');
			  
			$(".tabc"+tid[1]).hide();
			$("#tabc-"+tid[1]+"-"+tid[2]).show();
		}
		function makedropdownArray(ddid,ary) {
			var ddopt = "<option value='auto'>auto</option>";
			for(i=0;i<ary.length;i++){
				ddopt += "<option value='"+ary[i]+"'>"+ary[i]+"</option>";
			}
			$(ddid).html(ddopt);
		}
		function makedropdown(ddid,cnt) {
			var ddopt = "<option value='auto'>auto</option>";
			
			for(i=1;i<=cnt;i++){
				ddopt += "<option value='"+i+"'>"+i+"</option>";
			}
			$(ddid).html(ddopt);
		}
		
		function makecbx5g(x,ary) {
			var ddopt = "";
			var cbxid = "#cbx_grp_"+x;
			for(i=0;i<ary.length;i++){
				ddopt += '<span><input type="checkbox" id="5gr_acs_'+x+'_'+ary[i]+'" class="5gr_acs_'+x+'" value="'+ary[i]+'" onClick="getACS(\'.5gr_acs_'+x+'\',\'#5gr_hid_'+x+'\')">'+ary[i]+'</span>';
			}
			$(cbxid).html(ddopt);
		}
		function makecbx2g(x,ary) {
			var ddopt = "";
			var cbxid = "#cbx_grp2g_"+x;
			for(i=0;i<ary.length;i++){
				ddopt += '<span><input type="checkbox" id="2gr_acs_'+x+'_'+ary[i]+'" class="2gr_acs_'+x+'" value="'+ary[i]+'" onClick="getACS(\'.2gr_acs_'+x+'\',\'#2gr_hid_'+x+'\')">'+ary[i]+'</span>';
			}
			//console.log(ddopt);
			$(cbxid).html(ddopt);
		}
		function addtab(frmd,tod,makmnu) {

			console.log ("TodDDD" + tod);
			tabcnt++;
			var mydata = $(frmd).html().replace(/zzz/g,tabcnt); 
			
			if (tod == "#div2gr") 		mydata = mydata.replace(/yyy/g,radio2g); 
			else if(tod == "#div2gi") 	mydata = mydata.replace(/yyy/g,interfaces2g); 
			else if(tod == "#div5gr") 	mydata = mydata.replace(/yyy/g,radio5g); 
			else if(tod == "#div5gi") 	mydata = mydata.replace(/yyy/g,interfaces5g); 
			
			
			$(tod).append(mydata);
			sowide('tabtn-'+tabcnt+'-1')
			
			
			
			if(tod == "#div2gr") 	radio2g++;
			else if(tod == "#div2gi"){ makedropdown('#2gi_multicat_snoop_'+interfaces2g,64); interfaces2g++;}
			else if(tod == "#div5gr") radio5g++;
			else if(tod == "#div5gi"){ makedropdown('#5gi_multicat_snoop_'+interfaces5g,64); interfaces5g++; }
		}
		
		function add_diagtab(frmd,tod,makmnu,cur_val) {
          
            var diagVal = $(cur_val).parent().find('input').val();
            var keyVal  = $(cur_val).parent().parent().parent().parent().find('input').val();
             if(diagVal != "" && keyVal != ""){
                 diagcnt++;
                    var mydata = $(frmd).html();
                    if(tod == "#boxhead") mydata = mydata.replace(/dbg/g, diagcnt); 
                    $(tod).append(mydata);
                    $(cur_val).parent().find('input').css("border-color", "lightgray");
                    $(cur_val).parent().parent().parent().parent().find('input').css("border-color", "lightgray");
             } else {
                    $(cur_val).parent().find('input').css("border-color", "red");
                    $(cur_val).parent().parent().parent().parent().find('input').css("border-color", "red");
             }
			
		}		
		
            function del_diagtab(rmval,boxhead) {
            var removediag = $(rmval).parent().parent().parent();
            //var headVal = $(removediag).parent().parent().parent().attr("id");
            var boxVal = $('#boxhead').children().size();   
                
                //console.log("hae" + headVal + "box " +  boxVal)
                if(boxVal > 1){
                $(removediag).parent().remove();
            } 
		}


		function getModes(v,g,i){
			
			var ddopt = "";
			var ddid = "";
			
			if(g==2) {
				ddid = "#2gi_fixedrate_"+i;
				mcid = "#2gi_mcast_"+i;
			} else {
				ddid = "#5gi_fixedrate_"+i;
				mcid = "#5gi_mcast_"+i;
			}
			//console.log(v+'---'+g+'---'+i);
			ddopt = "";
			for(j=0;j<modes[v].length;j++){
				ddopt += "<option value='"+modes[v][j]+"'>"+modes[v][j]+"</option>";
			}
			$(ddid).html(ddopt);
			$(mcid).html(ddopt);
			
		}
		
		function getEncr(v,g,i){			
			var ddid = "";
			
			if(g==2) {
				ddid = "#2gi_key_"+i;
			} else {
				ddid = "#5gi_key_"+i;
			}
			if (v == "open") {
				$(ddid).val("");
				$(ddid).prop("disabled", true); 
			} else {
				$(ddid).prop("disabled", false); 
			}
			
		}
		
		function getTxpwr(v,g,i){			
			var ddid = "";
			txPwrError = 0;
			if(g == 2) {
				ddid = "#2gr_reg_"+i;
				txid = "#2gr_txpwr_"+i;
			} else {
				ddid = "#5gr_reg_"+i;
				txid = "#5gr_txpwr_"+i;
			}
			
			var cid = $(ddid).val();
			
			console.log ("cntry" + cid + "tx" + v + "CC" + myar[cid]);
			
			if (myar[cid] > v) {
				txPwrError = 0;
				$(txid).css("border", "1px black");
				$(txid).val(v)
			} else {
				console.log ("ErrorK");
				txPwrError = 1;
				$(txid).css("border", "1px solid red");
				$(txid).focus();
			}
					
		}
				
		function deltab(tod) {
			$(tod+' .tabgrp:last').fadeOut().remove();
			if(tod == "#div2gr") radio2g--;
			
		}
		
		
		
		function getACS(frm,toc) {
			var sThisVal = "";
			var cnd = 0;
			$('input:checkbox'+frm).each(function () {
				if(this.checked){
					sThisVal +=  $(this).val() + " ";
					cnd++;
				}
			});
			var acslen = toc.replace("hid","acs_len")
			$(toc).val(sThisVal);
			$(acslen).val(cnd)
			//console.log(acslen+'---'+cnd)

		}
		function sethw(x){
			if(x=="AU" || x=="CZ" || x=="J1" || x=="JP"){
				$('#2gr_hwmode_0').val('11b');
				getModes('11b',2,0);
			}
		}
		function setacs(x){
			y = $('#2gr_reg_0').val();
			if(x!="11b"){
				if(y=="J1" || y=="JP") {
					$('#2gr_acs_0_14').parent().hide();
				} 
			} else {
				$('#2gr_acs_0_14').parent().show();
			}
		}
		function set_acl (val, y, x)
		{
			console.log('val '+val+'y '+y+'x '+x);
				if (val == "ap") {
					//console.log("Device is in AP mode");
					if ( x== 0) {
						$('#2gi_acl_'+y).parent().parent().show();
						//console.log( "2gshow");
					}	else {
						$('#5gi_acl_'+y).parent().parent().show();
		
						//console.log("5g show");
					}
				} else {
					//console.log("Device is in mesh or station mode");
					if (x == 0) {
						$('#2gi_acl_'+y).parent().parent().hide();
					} else { 
						$('#5gi_acl_'+y).parent().parent().hide();
					}
				}
		}

		function getEncr(v,g,i){			
			var ddid = "";
			
			if(g==2) {
				ddid = "#2gr_key_"+i;
			}
			if (v == "open") {
				$(ddid).val("");
				$(ddid).prop("disabled", true); 
			} else {
				$(ddid).prop("disabled", false); 
			}
			
		}
		
		function configChange(mode,radioType,rowCount){
			
			if(mode == "unilateration"){
				$('.proxVal').val('6.0');
			} else {
				$('.proxVal').val('100.0');
			}
		}
		
		 $("#upload-file-selector").on('change', prepareLoad);
		var files;
		function prepareLoad(event) {
			files = event.target.files;
			var oMyForm = new FormData();
			oMyForm.append("file", files[0]);
			var url = "/facesix/rest/device/uploadconfig";
			var result= $.ajax({
				dataType : 'json',
				url : url,
				data : oMyForm,
				type : "POST",
				enctype : 'multipart/form-data',
				processData : false,
				contentType : false,
				success : function(result) {
					prefilldata(JSON.stringify(result));
				},
				error : function(result) {
				}
			});
		}