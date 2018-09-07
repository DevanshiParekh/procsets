		var tabcnt 			= 0;
		var radio2g 		= 0;
		var interfaces2g 	= 0;
		var radio5g 		= 0;
		var interfaces5g 	= 0;
		var interface_mode	= "";
		var txPwrError 		= 0;
		
		var modes = new Array(6);
			modes["11b"]	= new Array(5);	  modes["11b"] 	= [1, 2,   5.5, 11];
			modes["11bg"]	= new Array(12);  modes["11bg"] = [1, 2,   5.5, 6,	9,	 11,	12,	18,	24,	36,	48,	54];
			modes["11ng"]	= new Array(40);  modes["11ng"] = [1, 2,   5.5, 6,	6.5, 7.2, 9, 11, 12, 13, 14.4, 18, 19.5, 21.7, 24,	26, 28.9, 36, 39, 43.3, 48, 52, 57.8, 58.5, 65, 72.2, 78, 86.7, 104, 115.6, 117, 130.3, 144.4, 156, 173.3, 175.5, 195, 216.7];
			modes["11a"]	= new Array(8);   modes["11a"]	= [6, 9,   12,  18, 24,  36, 48, 54];
			modes["11na"]	= new Array(58);  modes["11na"]	= [6, 6.5, 7.2, 9,  12,  13, 13.5, 14.4, 15, 18, 19.5, 21.7, 24, 26, 27, 28.9, 30, 36, 39, 40.5, 43.3, 45, 48, 52, 54, 57.8, 58.5, 60, 65, 72.2, 78, 81, 86.7, 90, 104, 108, 115.6, 117, 120, 121.5, 130, 130.3, 135, 144.4, 150, 156, 162, 173.3, 175.5, 180, 195, 216, 216.7, 240, 243, 270, 300];
			modes["11ac"]	= new Array(100); modes["11ac"]	= [6, 6.5, 7.2, 9,  12,  13, 13.5, 14.4, 15, 18, 19.5, 21.7, 24, 26, 27, 28.9, 29.3, 30, 32.5, 36, 39, 40.5, 43.3, 45, 48, 52, 54, 57.8, 58.5, 60, 65, 72.2, 78, 81, 86.7, 87.8, 90, 97.5, 104, 108, 115.6, 117, 120, 121.5, 130, 130.3, 135, 144.4, 150, 156, 162, 173.3, 175.5, 180, 195, 200, 216, 216.7, 234, 240, 243, 260, 263.3,  270, 292.5, 300, 324, 325, 351, 360,  390, 433.3,  468, 520, 526.5, 540, 585, 600, 650, 702, 780, 866.7, 936, 1040, 1053, 1170, 1300, 1404, 1560, 1579.5, 1733.3, 1755, 1950, 2106, 2340];
		
		var req2g = new Array(50);
					
			req2g["AT"]=new Array(11);	 req2g["AT"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["AU"]=new Array(11);	 req2g["AU"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["BE"]=new Array(13);	 req2g["BE"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["BR"]=new Array(11);	 req2g["BR"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["CA"]=new Array(11);	 req2g["CA"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["CH"]=new Array(11);	 req2g["CH"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["CN"]=new Array(13);	 req2g["CN"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["CY"]=new Array(11);	 req2g["CY"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["CZ"]=new Array(11);	 req2g["CZ"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["DE"]=new Array(11);	 req2g["DE"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["DK"]=new Array(11);	 req2g["DK"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["EE"]=new Array(11);	 req2g["EE"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["ES"]=new Array(11);	 req2g["ES"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["FI"]=new Array(11);	 req2g["FI"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["FR"]=new Array(11);	 req2g["FR"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["GB"]=new Array(11);	 req2g["GB"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["GR"]=new Array(11);	 req2g["GR"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["HK"]=new Array(11);	 req2g["HK"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["HU"]=new Array(11);	 req2g["HU"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["ID"]=new Array(13);	 req2g["ID"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["IE"]=new Array(11);	 req2g["IE"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["IL"]=new Array(11);	 req2g["IL"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["ILO"]=new Array(9);	 req2g["ILO"] = [5,6,7,8,9,10,11,12,13];
			req2g["IN"]=new Array(11);	 req2g["IN"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["IS"]=new Array(11);	 req2g["IS"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["IT"]=new Array(11);	 req2g["IT"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["J1"]=new Array(14);	 req2g["J1"] = [1,2,3,4,5,6,7,8,9,10,11,12,13,14];
			req2g["JP"]=new Array(14);	 req2g["JP"] = [1,2,3,4,5,6,7,8,9,10,11,12,13,14];
			req2g["KE"]=new Array(13);	 req2g["KE"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["KR"]=new Array(13);	 req2g["KR"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["LT"]=new Array(11);	 req2g["LT"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["LU"]=new Array(11);	 req2g["LU"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["LV"]=new Array(11);	 req2g["LV"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["MY"]=new Array(13);	 req2g["MY"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["NL"]=new Array(11);	 req2g["NL"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["NO"]=new Array(11);	 req2g["NO"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["NZ"]=new Array(11);	 req2g["NZ"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["PH"]=new Array(11);	 req2g["PH"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["PL"]=new Array(11);	 req2g["PL"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["PT"]=new Array(11);	 req2g["PT"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["SE"]=new Array(11);	 req2g["SE"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["SG"]=new Array(13);	 req2g["SG"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["SI"]=new Array(11);	 req2g["SI"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["SK"]=new Array(11);	 req2g["SK"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["TH"]=new Array(13);	 req2g["TH"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["TW"]=new Array(13);	 req2g["TW"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			req2g["US"]=new Array(11);	 req2g["US"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["USE"]=new Array(11);	 req2g["USE"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["USL"]=new Array(11);	 req2g["USL"] = [1,2,3,4,5,6,7,8,9,10,11];
			req2g["ZA"]=new Array(13);	 req2g["ZA"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
				
			
		var req5g = new Array(44);
			req5g["AT"]=new Array(4);	 req5g["AT"] = [36,40,44,48];
			req5g["AU"]=new Array(12);	 req5g["AU"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["BE"]=new Array(8);	 req5g["BE"] = [36, 40, 44, 48, 52, 56, 60, 64];
			req5g["BR"]=new Array(12);	 req5g["BR"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["CA"]=new Array(12);	 req5g["CA"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["CH"]=new Array(8);	 req5g["CH"] = [36, 40, 44, 48, 52, 56, 60, 64];
			req5g["CN"]=new Array(4);	 req5g["CN"] = [149, 153, 157, 161];
			req5g["CY"]=new Array(12);	 req5g["CY"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["CZ"]=new Array(12);	 req5g["CZ"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["DE"]=new Array(17);	 req5g["DE"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["DK"]=new Array(17);	 req5g["DK"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["EE"]=new Array(12);	 req5g["EE"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["ES"]=new Array(17);	 req5g["ES"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["FI"]=new Array(17);	 req5g["FI"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["FR"]=new Array(8);	 req5g["FR"] = [36, 40, 44, 48, 52, 56, 60, 64];
			req5g["GB"]=new Array(17);	 req5g["GB"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			//req5g["GR"]=new Array(11);	 req5g["GR"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			
			
			req5g["HK"]=new Array(12);	 req5g["HK"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["HU"]=new Array(8);	 req5g["HU"] = [36, 40, 44, 48, 52, 56, 60, 64];
			//req5g["ID"]=new Array(13);	 req5g["ID"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			
			req5g["IE"]=new Array(8);	 req5g["IE"] = [36, 40, 44, 48, 52, 56, 60, 64];
			req5g["IL"]=new Array(8);	 req5g["IL"] = [36, 40, 44, 48, 52, 56, 60, 64];
			req5g["ILO"]=new Array(8);	 req5g["ILO"] = [36, 40, 44, 48, 52, 56, 60, 64];
			//req5g["IN"]=new Array(11);	 req5g["IN"] = [1,2,3,4,5,6,7,8,9,10,11];
			
			req5g["IS"]=new Array(17);	 req5g["IS"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["IT"]=new Array(17);	 req5g["IT"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["J1"]=new Array(8);	 req5g["J1"] = [36, 40, 44, 48, 52, 56, 60, 64];
			req5g["JP"]=new Array(4);	 req5g["JP"] = [34, 38, 42, 46];
			req5g["KE"]=new Array(18);	 req5g["KE"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 149, 153, 157, 161];
			req5g["KR"]=new Array(4);	 req5g["KR"] = [149, 153, 157, 161];
			req5g["LT"]=new Array(12);	 req5g["LT"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["LU"]=new Array(17);	 req5g["LU"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["LV"]=new Array(12);	 req5g["LV"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			//req2g["MY"]=new Array(13);	 req5g["MY"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			
			req5g["NL"]=new Array(17);	 req5g["NL"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["NO"]=new Array(17);	 req5g["NO"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["NZ"]=new Array(12);	 req5g["NZ"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["PH"]=new Array(12);	 req5g["PH"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["PL"]=new Array(12);	 req5g["PL"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["PT"]=new Array(17);	 req5g["PT"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["SE"]=new Array(17);	 req5g["SE"] = [36, 40, 44, 48, 52, 56, 60, 64,	104, 108, 112, 116, 120, 124, 128, 132, 140];
			req5g["SG"]=new Array(12);	 req5g["SG"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["SI"]=new Array(12);	 req5g["SI"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["SK"]=new Array(12);	 req5g["SK"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			//req5g["TH"]=new Array(13);	 req5g["TH"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			
			req5g["TW"]=new Array(17);	 req5g["TW"] = [56, 60, 64,	100, 104, 108, 112, 116, 120, 124, 128, 132, 140, 149, 153, 157, 161];
			req5g["US"]=new Array(12);	 req5g["US"] = [36, 40, 44, 48, 52, 56, 60, 64,	149, 153, 157, 161];
			req5g["USE"]=new Array(8);	 req5g["USE"] = [36, 40, 44, 48, 52, 56, 60, 64];
			req5g["USL"]=new Array(8);	 req5g["USL"] = [36, 40, 44, 48, 52, 56, 60, 64];
			//req5g["ZA"]=new Array(13);	 req5g["ZA"] = [1,2,3,4,5,6,7,8,9,10,11,12,13];
			
			var myar = Array();
			
			myar["AT"]=20;
			myar["AU"]=23;
			myar["BE"]=20;
			myar["BR"]=30;
			myar["CA"]=36;
			myar["CH"]=20;
			myar["CN"]=27;
			myar["CY"]=30;
			myar["CZ"]=23;
			myar["DE"]=20;
			myar["DK"]=20;
			myar["EE"]=30;
			myar["ES"]=20;
			myar["FI"]=20;
			myar["FR"]=20;
			myar["GB"]=20;
			myar["GR"]=20;
			myar["HK"]=20;
			myar["HU"]=30;
			myar["ID"]=20;
			myar["IE"]=20;
			myar["IL"]=20;
			myar["ILO"]=20;
			myar["IN"]=36;
			myar["IS"]=20;
			myar["IT"]=20;
			myar["J1"]=23;
			myar["JP"]=23;
			myar["KE"]=27;
			myar["KR"]=27;
			myar["LT"]=30;
			myar["LU"]=20;
			myar["LV"]=20;
			myar["MY"]=20;
			myar["NL"]=20;
			myar["NO"]=20;
			myar["NZ"]=30;
			myar["PH"]=30;
			myar["PL"]=20;
			myar["PT"]=20;
			myar["SE"]=20;
			myar["SG"]=23;
			myar["SI"]=30;
			myar["SK"]=30;
			myar["TH"]=20;
			myar["TW"]=30;
			myar["US"]=36;	
			myar["USE"]=30;
			myar["USL"]=30;
			myar["ZA"]=30;
			
			// 5G
			var myar5 = Array();
			myar5["AT"]=20;
			myar5["AU"]=23;
			myar5["BE"]=20;
			myar5["BR"]=30;
			myar5["CA"]=36;
			myar5["CH"]=20;
			myar5["CN"]=27;
			myar5["CY"]=30;
			myar5["CZ"]=23;
			myar5["DE"]=20;
			myar5["DK"]=20;
			myar5["EE"]=30;
			myar5["ES"]=20;
			myar5["FI"]=20;
			myar5["FR"]=20;
			myar5["GB"]=20;
			myar5["GR"]=20;
			myar5["HK"]=20;
			myar5["HU"]=30;
			myar5["ID"]=20;
			myar5["IE"]=20;
			myar5["IL"]=20;
			myar5["ILO"]=20;
			myar5["IN"]=36;
			myar5["IS"]=20;
			myar5["IT"]=20;
			myar5["J1"]=23;
			myar5["JP"]=23;
			myar5["KE"]=27;
			myar5["KR"]=27;
			myar5["LT"]=30;
			myar5["LU"]=20;
			myar5["LV"]=20;
			myar5["MY"]=20;
			myar5["NL"]=20;
			myar5["NO"]=20;
			myar5["NZ"]=30;
			myar5["PH"]=30;
			myar5["PL"]=20;
			myar5["PT"]=20;
			myar5["SE"]=20;
			myar5["SG"]=23;
			myar5["SI"]=30;
			myar5["SK"]=30;
			myar5["TH"]=20;
			myar5["TW"]=30;
			myar5["US"]=36;	
			myar5["USE"]=30;
			myar5["USL"]=30;
			myar5["ZA"]=30;
		
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
			var txpwr = "";
			$('.aclGrp').hide();
			

			
			$.each(jsondata, function (key, data) {
				if(key=="radio2g") 				inpid = "2gr_";
				else if(key=="interfaces2g")	inpid = "2gi_";
				else if(key=="radio5g")			inpid = "5gr_";
				else if(key=="interfaces5g")	inpid = "5gi_";

			    $.each(data, function (i, data) {
			    	if(key=="radio2g") 				addtab('#hid2gr','#div2gr',0);
					else if(key=="interfaces2g")	addtab('#hid2gi','#div2gi',0);
					else if(key=="radio5g")			addtab('#hid5gr','#div5gr',0);
					else if(key=="interfaces5g")	addtab('#hid5gi','#div5gi',0);
			    	
			    	$.each(data, function (index, data) {
			    		/*if(data == 1) {
			    			$('#cbx_'+inpid+index+"_"+i).attr('checked','checked');
			    		}*/
			    		
			    		data = data.replace(/&amp;/g, '&');
						 if(index=="channel"){
			    			channel = data; 
			    		} else if(index=="fixedrate"){
			    			fixedrate = data; 
			    		} else if(index=="mcast"){
			    			mcast = data; 
			    		} else if (index=="acl"){
			    			$('#'+inpid+index+"_"+i).val(data);
			    			$('.aclGrp').show();
				    		
			    		} else if(index=="dcs"){
							$('#'+inpid+index+"_"+i).val(data);
							if(data == 'false'){
								$('#'+inpid+index+"_"+i).prop('readonly',true);
								$('#'+inpid+index+"_"+i).addClass('avoid-clicks');
							} else {
								$('#'+inpid+index+"_"+i).prop('readonly',false);
								$('#'+inpid+index+"_"+i).removeClass('avoid-clicks');
							}
			    		}else {
			    			$('#'+inpid+index+"_"+i).val(data);
			    		}
			    		if(index == "reg"){
			    			ctryval = data;
			    		}
			    		if (index == "txpwr") {
			    			txpwr = data;
			    		}

				    })
				      if(key=="radio2g") {
				    	reg2g(ctryval,txpwr,i);
		    			$('#'+inpid+"channel_"+i).val(channel);
		    			$.each(data, function (index, data) {
		    				if(index == 'dcs') {
		    					if(data == 'false' && channel == 'auto'){
		    						$("#2gr_channel_0").val($("#2gr_channel_0 option:eq(1)").val());
		    					}
		    				}
		    				
		    			});
		    			
					} else if(key=="interfaces2g") {
						getModes($('#2gr_hwmode_0').val(),2,i);
						$('#'+inpid+"fixedrate_"+i).val(fixedrate);
		    			$('#'+inpid+"mcast_"+i).val(mcast);
		    			
		    			var encryption = $('#2gi_encryption_0').val();
		    			if(encryption == "open"){
		    				$( "#2gi_key_0" ).prop( "disabled", true );
		    			}
		    			
					} else if(key=="radio5g") {
						reg5g(ctryval,txpwr,i);
		    			$('#'+inpid+"channel_"+i).val(channel);
		    			$.each(data, function (index, data) {
		    				if(index == 'dcs') {
		    					if(data == 'false' && channel == 'auto'){
		    						$("#5gr_channel_0").val($("#5gr_channel_0 option:eq(1)").val());
		    					}
		    				}
		    				
		    			});
						
		    		} 
					else if(key=="interfaces5g") {
		    			getModes($('#5gr_hwmode_0').val(),5,i);
		    			$('#'+inpid+"fixedrate_"+i).val(fixedrate);
		    			$('#'+inpid+"mcast_"+i).val(mcast);
		    			
		    			var encryption = $('#5gi_encryption_0').val();
		    			if(encryption == "open"){
		    				$( "#5gi_key_0" ).prop( "disabled", true );
		    			}
		    			
					}
			    })
			}) 
		

		}
		$(function() {
			
			$.get('/facesix/template/qubercomm/config-source').then(function(responseData) {
				$('#onloadata').html(responseData);
				if(srvrdta!=""){
						prefilldata(srvrdta);
				} else {
					addtab('#hid2gr','#div2gr',1);
					addtab('#hid2gi','#div2gi',1);
					addtab('#hid5gr','#div5gr',1);
					addtab('#hid5gi','#div5gi',1);
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
			
			$('#steering').click(function() {
				var flag = $('#steering').prop('checked');
			    if(flag==true){
			    	$('#steering').val(true);
			    }else{
			    	$('#steering').val(false);
			    }
			});
			
			$('#loadBalance').click(function() {
				var flag = $('#loadBalance').prop('checked');
			    if(flag==true){
			    	$('#loadBalance').val(true);
			    }else{
			    	$('#loadBalance').val(false);
			    }
			});
            
            
			
			$('#valsubmit').click( function() {
				
				$('#duplicate').hide();
				$("#deconfig").val('');
				
				var mytxt = "{";
				var myit = 0;
				
				var t1,t2,t3,t4 = "";
				
				t1 = makeJstr('#div2gr *','radio2g',myit);
				mytxt += t1;
				if(mytxt!="{") myit = 1;
				
				t2 = makeJstr('#div2gi *','interfaces2g',myit);
				mytxt += t2;
				if(mytxt!="{") myit = 1;
				
				t3 = makeJstr('#div5gr *','radio5g',myit);
				mytxt += t3;
				if(mytxt!="{") myit = 1;
				
				t4 = makeJstr('#div5gi *','interfaces5g',myit);
				mytxt += t4;
				
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
				if(t1=="" && t3==""){
					console.log(1)
					$('#h32gr,#h35gr').css("color", "red");
					ok = 2
				} 
				if(t1!="" && t2==""){
					$('#h32gi').css("color", "red");
					console.log(222);
					ok = 3
				} 
				if(t3!="" && t4==""){
					$('#h35gi').css("color", "red");
					
					//console.log(333)
					ok = 4
				} 
				
				var rts2g,fmt2g,rts5g,fmt5g;
				rts2g = $('#2gr_rtsth_0').val();
				fmt2g = $('#2gr_fgmth_0').val();
				rts5g = $('#5gr_rtsth_0').val();
				fmt5g = $('#5gr_fgmth_0').val();
				
				if (rts2g=="" || fmt2g==""){
					$( "#tabtn-1-2" ).click();
					$('#tabtn-1-2').css("color", "red");
					$("html, body").animate({ scrollTop: 0 }, 100);
				} else {
					$('#tabtn-1-2').css("color", "black");
				}
				
				if (rts5g=="" || fmt5g==""){	
					$( "#tabtn-3-2" ).click();
					$('#tabtn-3-2').css("color", "red");
				} else {
					$('#tabtn-3-2').css("color", "black");
				}
				
				
				if (txPwrError == 1) {
					ok = 7
				}
				
				if($('#flag').length > 0){
					console.log("--Venue/Floor----");
				} else {
					if($('#alias').val()==""){
						$('#alias').css("border", "1px solid red");
						$('#alias').focus();
						ok = 5
					} 
					
					if($('#uuid').val()=="" || $('#uuid').val().length<17){
						$('#uuid').css("border", "1px solid red");
						$('#uuid').focus();
						ok = 6
					} if($('#statusInterval').val()==""){
						$('#statusInterval').css("border", "1px solid red");
						$('#statusInterval').focus();
						ok =7
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
			
			if(makmnu == 1) {
				if(tod == "#div2gr") {
					ctryval = $('#2gr_reg_'+radio2g).val();
					reg2g(ctryval,0,radio2g);
				} else if(tod == "#div2gi") {
					getModes($('#2gr_hwmode_'+(radio2g-1)).val(),2,interfaces2g);
					
				} else if(tod == "#div5gr") {
					ctryval = $('#5gr_reg_'+radio5g).val();
					reg5g(ctryval,0,radio5g);
				} else if(tod == "#div5gi") {
					getModes($('#5gr_hwmode_'+(radio5g-1)).val(),5,interfaces5g);
					
				}
			} 
			
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
		
		function getHotspot(v,g,i){			
			var bridgeVal = "";
			
			if(g==2) {
				bridgeVal = "#2gi_bridge_"+i;
			} else {
				bridgeVal = "#5gi_bridge_"+i;
			}
			if (v == "on") {
				$(bridgeVal).css('pointer-events','none');
				$(bridgeVal).css('background-color','lightgray');
				$(bridgeVal).val("lan");
			} else {
				$(bridgeVal).val("wan");
				$(bridgeVal).css('pointer-events','auto');
				$(bridgeVal).css('background-color','');
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
			
			if (myar[cid] >= v) {
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
			if($('#steering').prop('checked') == true){
				var yes = true;
				//console.log("====="+tod);
				$('.saveareathree').show();
				$(tod+' .tabgrp:last').fadeOut().remove();
				if(tod == "#div2gr") radio2g--;
				
			} else {
				var yes = false;
				$(tod+' .tabgrp:last').fadeOut().remove();
				if(tod == "#div2gr") radio2g--;
			}
			
		}
		//console.log("++++" + tod)
		function reg2g(vl,txpwr,y) {

			if (txpwr != 0) {
				$('#2gr_txpwr_'+y).val(txpwr);
			} else {
				$('#2gr_txpwr_'+y).val(myar[vl]);
			}	
			
			makedropdownArray('#2gr_channel_'+y,req2g[vl]);
			makecbx2g(y,req2g[vl]);
		}
		function reg5g(vl,txpwr,y) {

			if (txpwr != 0) {
				$('#5gr_txpwr_'+y).val(txpwr);
			} else {
				$('#5gr_txpwr_'+y).val(myar5[vl]);
			}
			makedropdownArray('#5gr_channel_'+y,req5g[vl]);
			makecbx5g(y,req5g[vl]);
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
						$('#2gi_hotspot_'+y).parent().parent().show();
						//console.log( "2gshow");
					}	else {
						$('#5gi_acl_'+y).parent().parent().show();
						$('#5gi_hotspot_'+y).parent().parent().show();
		
						//console.log("5g show");
					}
				} else {
					//console.log("Device is in mesh or station mode");
					if (x == 0) {
						$('#2gi_acl_'+y).parent().parent().hide();
						$('#2gi_hotspot_'+y).parent().parent().hide();
					} else { 
						$('#5gi_acl_'+y).parent().parent().hide();
						$('#5gi_hotspot_'+y).parent().parent().hide();
					}
				}
				
				var cur_val = $('#2gi_mode_1').val();
				//console.log(cur_val);
				if(cur_val == "mesh"){
					$('#root').val('yes');
					//$('#loadBalance').prop('disabled',false);
				} else {
					$('#root').val('no');
					$('#loadBalance').prop('checked',false);
					//$('#loadBalance').prop('disabled',true);
					$('.loadSteer ').hide();
				}
		
		}

		function set_gVal (val, y, x){
				cur_val_2g = $('#div2gi div').size();
					
					if(cur_val_2g == '104' && val != "ap"){
						$('#2gr_dcs_0').attr('readonly',true);
						$("#2gr_dcs_0").val('false');
						$("#2gr_dcs_0").addClass('avoid-clicks');
					} else {
						$('#2gr_dcs_0').attr('readonly',false);
						$("#2gr_dcs_0").removeClass('avoid-clicks');
						//$('#2gr_dcs_0').val('true');
					}
		}


		function set_fivegVal (val, y, x){
				cur_val_5g = $('#div5gi div').size();
					
					if(cur_val_5g == '104' && val != "ap"){
						$('#5gr_dcs_0').attr('readonly',true);
						$("#5gr_dcs_0").val('false');
						$("#5gr_dcs_0").addClass('avoid-clicks');
					} else {
						$('#5gr_dcs_0').attr('readonly',false);
						$("#5gr_dcs_0").removeClass('avoid-clicks');
						//$('#2gr_dcs_0').val('true');
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
		
		$('#steering').on('change',function(){
				
			var radioTwo = $('#div2gr > div').length;
			var radioFive = $('#div5gr > div').length;
			
			//console.log(radioTwo + ">>>>" + radioFive);
		
			if(radioFive != "0" && radioTwo != "0"){
				var sVal = $('#steering').val();
				console.log("one"+sVal)
				if(sVal == "true"){
					$('.bandSteer').show();
					$('#bssStationRejectThresh').val('2');
					$('#bssChanPbusyPercent').val('75');
					$('#bssRejectTimeout').val('900');
					$('#band2GStaRatio').val('30');
					$('#band5GStaRatio').val('70');
					$('#bandRcpiDiff').val('10');
				} else {
					console.log("three"+sVal)
					$('.bandSteer').hide();
					$('#bssStationRejectThresh').val('2');
					$('#bssChanPbusyPercent').val('75');
					$('#bssRejectTimeout').val('900');
					$('#band2GStaRatio').val('30');
					$('#band5GStaRatio').val('70');
					$('#bandRcpiDiff').val('10');
				}
			} else {
				$(".saveareatwo").show();
			}
			
			var cur_steer = $('#steering').val();
			var cur_bal = $('#loadBalance').val();
			if(cur_steer == "false" && cur_bal == "true"){
				$('.loadSteer').css('margin-left','0px');
			} else {
				$('.loadSteer').css('margin-left','20px');
			}
			
		});
		
		
		$('#loadBalance').on('change',function(){
			
			var cur_val = $('#2gi_mode_1').val();
			var root    = $('#root').val();
						
			//console.log(cur_val);
			if(cur_val == undefined || cur_val== ''){
				 cur_val = $('#5gi_mode_1').val();
			}	
			
			if(cur_val != undefined){
				if(cur_val != "mesh" || (cur_val == "mesh" && root == "no")){
					
					$('#minStaCount').val('');
					$('#rssiThreshold').val('');
					$('#rcpiRange').val('');
					$('#avgStaCntLb').val('');
					
					
				} else {
					var loadVal = $('#loadBalance').val();
					if(loadVal == "true" && root == "yes"){
						$('.loadSteer').show();
						$('#minStaCount').val('10');
						$('#rssiThreshold').val('-60');
						$('#rcpiRange').val('8');
						$('#avgStaCntLb').val('0');
					} else {
						$('.loadSteer').hide();
						$('#minStaCount').val('10');
						$('#rssiThreshold').val('-60');
						$('#rcpiRange').val('8');
						$('#avgStaCntLb').val('0');
						
					}
				}
			} else {
				$('.saveareaseven').show();
			}
			
			
			var cur_steer = $('#steering').val();
			var cur_bal = $('#loadBalance').val();
			if(cur_steer == "false" || cur_steer == "" && cur_bal == "true"){
				$('.loadSteer').css('margin-left','0px');
			}
		});
		
		
		$("body").on("click",'#cancelD',function(evt){
			if($('#steering').prop('checked') == true){
				$('#steering').prop('checked',false)
			}else{
				$('#steering').prop('checked',true)
			}
	  		$(".saveareatwo").hide();
	  })

	  $("body").on("click",'#cancelseven',function(evt){
			if($('#loadBalance').prop('checked') == true){
				$('#loadBalance').prop('checked',false)
			}else{
				$('#loadBalance').prop('checked',true)
			}
	  		$(".saveareaseven").hide();
	  		//$("#root").val('yes');
	  })

	  
	  $("body").on("click",'#cancelthree',function(evt){
			
	  		$(".saveareathree").hide();
	  	  var twoG = $('#2gr_hwmode_0').val();
	  	  var fiveG = $('#5gr_hwmode_0').val();
			
			if(twoG == undefined){
				addtab('#hid2gr','#div2gr',1);
			}
			
			if(fiveG == undefined){
				addtab('#hid5gr','#div5gr',1);
			}
	  })
	  
	   $("body").on("click",'#cancelfour',function(evt){
			
	  		$(".saveareafour").hide();
	  		$(".savearea").hide();
	  		$(".saveareaone").hide();
	  })
	  
	   $("body").on("click",'#cancelfive',function(evt){
			
	  		$(".saveareafive").hide();
	  		$('#root').val('yes');
	  })
	  
	   $("body").on("click",'#cancelsix',function(evt){
			
	  		$(".saveareasix").hide();
	  		$(".savearea").hide();
	  		$(".saveareaone").hide();
	  })
	  
	  $("body").on("click",'#showthree',function(evt){
		  if($('#steering').prop('checked') == true){
				$('#steering').prop('checked',false)
				$('.bandSteer').hide();
				$('#bssStationRejectThresh').val('2');
				$('#bssChanPbusyPercent').val('75');
				$('#bssRejectTimeout').val('900');
				$('#band2GStaRatio').val('30');
				$('#band5GStaRatio').val('70');
				$('#bandRcpiDiff').val('10');
			}
		  
	  		$(".saveareathree").hide();
	  })
	  
	  
	    $("body").on("click",'#showfour',function(evt){
		  if($('#steering').prop('checked') == true){
				$('#steering').prop('checked',false)
				$('.bandSteer').hide();
				$('#bssStationRejectThresh').val('2');
				$('#bssChanPbusyPercent').val('75');
				$('#bssRejectTimeout').val('900');
				$('#band2GStaRatio').val('30');
				$('#band5GStaRatio').val('70');
				$('#bandRcpiDiff').val('10');
			}
		  document.getElementById('steering').value = false;
		  	$('#custlegacy').click();
		  	//$('#custmeshconfig').click();
	  		$(".saveareafour").hide();
	  		$(".savearea").hide();
	  		$(".saveareaone").hide();
	  })
	  
	  $("body").on("click",'#showsix',function(evt){
		  if($('#steering').prop('checked') == true){
				$('#steering').prop('checked',false)
				$('.bandSteer').hide();
				$('#bssStationRejectThresh').val('2');
				$('#bssChanPbusyPercent').val('75');
				$('#bssRejectTimeout').val('900');
				$('#band2GStaRatio').val('30');
				$('#band5GStaRatio').val('70');
				$('#bandRcpiDiff').val('10');
			}
		  document.getElementById('steering').value = false;
		  	
		  	$('#custmeshconfig').click();
	  		$(".saveareasix").hide();
	  		$(".savearea").hide();
	  		$(".saveareaone").hide();
	  })
	  
	   $("body").on("click",'#showfive',function(evt){
		  if($('#loadBalance').prop('checked') == true){
				$('#loadBalance').prop('checked',false)
				$('.loadSteer').hide();
				//$('#loadBalance').prop('disabled',true)
			}
		  
	  		$(".saveareafive").hide();
	  })
	  
	  
	  
	  