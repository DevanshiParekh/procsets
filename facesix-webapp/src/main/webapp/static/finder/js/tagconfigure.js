		var tabcnt 			= 0;
		var radio2g 		= 0;
		var interfaces2g 	= 0;
		var interface_mode	= "";
		var txPwrError 		= 0;
		
		function makempty() {
			radio2g = 0; interfaces2g =0;
	   	  	$('#div2gr,#div2gi,#div5gr,#div5gi').html('');
		}
		function prefilldata(srvrdta){
			makempty();
			var jsondata = JSON.parse(srvrdta.replace(/&quot;/g,'"'));
			
			var inpid = "";
			var ctryval = "";
			$.each(jsondata, function (key, data) {
				if(key=="attributes") 			inpid = "2gr_";
               // console.log("prefill data"+JSON.stringify(data));

			    $.each(data, function (i, data) {
			    	if(key=="attributes") 			addtab('#hid2gr','#div2gr',0);
			    	
			    	$.each(data, function (index, data) {

			    			//console.log("Index " + index + "Data " + data + "Val " +inpid + "i " + i);
			    			if (index == "interval" && data == 0) {
			    				$('#'+inpid+index+"_"+i).val(100);
			    			} else if (index == "txpower" && data == 0){
			    				$('#'+inpid+index+"_"+i).val(4);
			    			} else {
			    				$('#'+inpid+index+"_"+i).val(data);
			    			}
			    		
				    })
			    })
			}) 
		}
		$(function() {
			
			$.get('/facesix/template/finder/finder-tag-configure').then(function(responseData) {
				$('#onloadata').html(responseData);
				if(srvrdta!=""){
						prefilldata(srvrdta);
				} else {
					addtab('#hid2gr','#div2gr',1);
					addtab('#hid2gi','#div2gi',1);				
				}
			});
			
			
			
			$(document).on('click', '.tabtn', function(){ 
				var attr =$(this ).attr('id');
				sowide(attr);
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
					
					//console.log ("Key Value" + $(this).val());
					
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
				
				if(t1=="" && t2==""){
					$('#h32gr').css("color", "red");
					//console.log(222);
					ok = 3
				} 
				
				if (txPwrError == 1) {
					ok = 7
				}
				
				if($('#flag').length > 0){
					//console.log("--Venue/Floor----");
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
				
				//console.log('-------'+ok)
				if(ok == 0){
					$("#deconfig").val(mytxt);
					$(".loader_box").show();
					$('#configform').submit();
					
				}
				return false;
				
			});
			
			
		});
		function makeJstr(mydiv,mynam,ite) {
			var mytxt = "";
			var mode = "";
			var avl;
			var lastindex = -1;
			
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
				
				if(lastindex < ar[0]){
					mytxt = mytxt.replace(/,\s*$/, "");
					mytxt += '},{';
					lastindex = ar[0];
				}
				
				if(ar[1]=="mode"){
					mode=field.value.trim();
				}
				if(ar[1]=="acl" && (mode=="mesh" || mode=="sta")) {
					return true;
				}
					mytxt += '"'+ar[1]+'":"'+field.value.trim()+'",';
				
			});
			mytxt = mytxt.replace(/,\s*$/, "");
			if(avl>0) mytxt += '}]';
			//console.log('final'+mytxt);
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

			tabcnt++;
			var mydata = $(frmd).html().replace(/zzz/g,tabcnt); 
			
			if(tod == "#div2gr") 		mydata = mydata.replace(/yyy/g,radio2g); 
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
			
			//console.log ("cntry" + cid + "tx" + v + "CC" + myar[cid]);
			
			if (myar[cid] > v) {
				txPwrError = 0;
				$(txid).css("border", "1px black");
				$(txid).val(v)
			} else {
				//console.log ("ErrorK");
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
			//console.log('val '+val+'y '+y+'x '+x);
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
		 function Validate(event) {
		        var regex = new RegExp("^[0-9-!@#$%*?.]");
		        var key = String.fromCharCode(event.charCode ? event.which : event.charCode);
		        if (!regex.test(key)) {
		            event.preventDefault();
		            return false;
		        }
		    }    