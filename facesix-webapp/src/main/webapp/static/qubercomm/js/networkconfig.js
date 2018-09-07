 // // Qubercomm NetworkConfig Namespace Variables
 // // ============================================================

 // // initial position for new object in canvas
 var svg = d3.select("svg")
 var svgWidth = $('#mapSVG').width();
 var svgOffset = $('#mapSVG').offset()
 var svgHeight = $('#mapSVG').height();
 var reposition = true;
 var objectW = 40;
 var objectH = 40;

 
 var isTouchDevice = 'ontouchstart' in document.documentElement ? true : false;
 var isnewobject = false;
 startX = 100;
 startY = 100;
 
 var gateway 			= false;
 var finder  			= false;
 var heatmap  			= false;
 var GatewayFinder 		= false;
 
 function getDevices(param1, param2,param3,param4) {
 	gateway 		= param1;
 	finder  		= param2;
 	heatmap 		= param3;
 	GatewayFinder 	= param4;
 	//console.log ("Param1 " + gateway + "Param2 " + finder + "param3" + heatmap + "param4" + GatewayFinder)
 }
 
 var nodeData = {
     'server': 0,
     'switch': 0,
     'ap': 0,
     'sensor': 0,
     'total': 0
 }
 var shortName={
    "SVR":"server",
    "SW":"switch",
    "AP":'ap',
    "SNR":'sensor'
 }
 var template = _.template($("#selectDevices").html())

 if (!isTouchDevice) {
     $(".reposition-modal label").text("To reposition the device, Drag to a correct location on the floorplan.")
 } else {
     $(".reposition-modal label").text("To reposition the device, click anywhere on the floorplan.")

 }


 /* ========================================================================
  * Qubercomm NetworkConfig Events
  * ======================================================================== */
 $('#switchAdd').on('click', function(event) {
	 $elem.panzoom("disable");
     if ($(".draggable[ismovable='true']").length || $("#reposition-modal").css("display")!="none")
         return
     
     var image = '/facesix/static/qubercomm/images/networkicons/switch_inactive.png';
     var popupOffset = $(this).offset();
     var serverCount = [];
     for (var key in networkVariable.networkTree){
    	 if((networkVariable.networkTree[key].devices.parent!="ble") && (networkVariable.networkTree[key].devices.parent!="ap")) {
    		 var string="SVR-"+networkVariable.networkTree[key].devices.uid
    		 serverCount.push(string)
    	 }
     }
     console.log(serverCount)
     delete networkVariable.initialParent;
     if (!serverCount.length)
         networkVariable.toggleDevicePopup(popupOffset.left - ($(".popup-switch").width() / 2) + ($(this).width() / 2), popupOffset.top + 60, 'server', 'addSwitch', serverCount);
     else if (serverCount.length > 0)
         networkVariable.toggleDevicePopup(popupOffset.left - ($(".popup-switch").width()) + ($(this).width() / 2), popupOffset.top + 60, 'server', 'addSwitch', serverCount);
     else {
         networkVariable.showNewObjectImage(event, 'switch', "ap");
         var parentId=networkVariable.initialParent;
         if(!parentId){
            for(var key in networkVariable.networkTree)
              parentId="SVR-"+networkVariable.networkTree[key].devices.uid;
         }
         $('#main-div').data('parentValue', parentId);
     }
     $("#addSwitch").on('click', function(event) {
         event.stopPropagation();
         event.preventDefault();
         var parentId = $('#deviceSelect').val();
         if (parentId == "Select") {
             $("#deviceSelect").addClass("border-error")
             return;
         }
         $('#main-div').data('parentValue', parentId);
         networkVariable.showNewObjectImage(event, 'switch', "ap");
     })
 })

 // add new AP to the canvas
 // ============================================================

 $('#apAdd').on('click', function(event) {
 
	 $elem.panzoom("disable");
     if ($(".draggable[ismovable='true']").length || $("#reposition-modal").css("display")!="none")
         return
     
     var image = '/facesix/static/qubercomm/images/networkicons/ap_inactive.png';
     var popupOffset = $(this).offset();
     var switchCount = [];
     delete networkVariable.initialParent;
     for (var key in networkVariable.networkTree)
         networkVariable.networkTree[key].getChild("switch", switchCount);
     if (switchCount.length > 1)
         networkVariable.toggleDevicePopup(popupOffset.left - ($(".popup-switch").width()) + ($(this).width() / 2), popupOffset.top + 60, 'switch', 'addAp', switchCount);
     else {
         networkVariable.showNewObjectImage(event, 'ap', "sensor");
         var parentId=networkVariable.initialParent;
         if(!parentId){                
         		for(var key in networkVariable.networkTree)
         			networkVariable.networkTree[key].findDevice("switch")
         		
         	    parentId=networkVariable.initialParent;
         }
         if (parentId == undefined) {
        	 parentId = "AP-Master"
         }
         $('#main-div').data('parentValue', parentId);
     }
     $("#addAp").on('click', function(event) {

         event.stopPropagation();
         event.preventDefault();
         var parentId = $('#deviceSelect').val();
         if (parentId == "Select") {
             $("#deviceSelect").addClass("border-error")
             return;
         }
         $('#main-div').data('parentValue', parentId);
         networkVariable.showNewObjectImage(event, 'ap', "sensor");
     })

 })

 // add new sensor to the canvas
 // ============================================================

 $('#sensorAdd').on('click', function(event) {
	 $elem.panzoom("disable");
     if ($(".draggable[ismovable='true']").length || $("#reposition-modal").css("display")!="none")
         return
     var image = '/facesix/static/qubercomm/images/networkicons/sensor_inactive.png';
     var popupOffset = $(this).offset();
     var apCount = [];
     delete networkVariable.initialParent;
     
     for (var key in networkVariable.networkTree)
         networkVariable.networkTree[key].getChild("ap", apCount);
    // console.log(apCount)     
     if (apCount.length > 1) {
         networkVariable.toggleDevicePopup(popupOffset.left - ($(".popup-switch").width()) + ($(this).width() / 2), popupOffset.top + 60, 'ap', 'addSensor', apCount);
         //console.log ("Sensor Addition1111 ")
     }
     else {
    	 //console.log ("Sensor Additio333 ")
         networkVariable.showNewObjectImage(event, 'sensor');
         var parentId=networkVariable.initialParent;
         if(!parentId){               
         		for(var key in networkVariable.networkTree){
         		     networkVariable.networkTree[key].findDevice("ap")         		    
         		}         		
         	    parentId=networkVariable.initialParent;
         }         
         //console.log ("parentId" + parentId);
         
         if (parentId == undefined) {
        	 parentId = "BLE-Master"
         }        
         $('#main-div').data('parentValue', parentId);
     }
     $("#addSensor").on('click', function(event) {
         event.stopPropagation();
         event.preventDefault();
         var parentId = $('#deviceSelect').val();
         if (parentId == "Select") {
             $("#deviceSelect").addClass("border-error")
             return;
         }
         $('#main-div').data('parentValue', parentId);
         networkVariable.showNewObjectImage(event, 'sensor');
     })

 })

 $('#addMasterSwitch').on("click", function(evt) {
	 $elem.panzoom("disable");
     if ($(".draggable[ismovable='true']").length || $("#reposition-modal").css("display")!="none")
         return
     var image = '/facesix/static/qubercomm/images/networkicons/server_inactive.png';
     $('#main-div').data('parentValue', 'network');
     // networkVariable.newObject(image,'server',evt);
     networkVariable.showNewObjectImage(evt, 'server', "switch");
 });


 function svgImageEvent(e) {
     e.preventDefault();
     if (isTouchDevice && networkVariable.isNewDevice) {
         var left = parseInt($(this).attr("x"), 10)
         var top = parseInt($(this).attr("y"), 10)
         networkVariable.newObject(networkVariable.newImage, networkVariable.newDevice, left + 10, top + 10);
         networkVariable.isNewDevice = false
         return;
     }
     
    // $('.panzoom').panzoom('disable');
     disableZoom: true;
     $('.popup-switch').addClass('hide');
     var modelType = $(this).attr("class") == "draggable" ? $('.reposition-modal') : $('.networkconfig');
     if ($('image[isnewobject="true"]').length) {
         $(".networkconfig").hide();
         var offset = $('image[isnewobject="true"]').offset()
         networkVariable.setModalPosition($(".reposition-modal"), offset.left, offset.top);
         return
     } else if ((!$('image[isnewobject="true"]').length) && $(".draggable").length) {
         var offset = $(".draggable").offset()
         $(".networkconfig").hide();
         networkVariable.currentPosition = $(".draggable").offset()
         networkVariable.currentElement = $(".draggable")
         networkVariable.setModalPosition($(".reposition-modal"), offset.left, offset.top);
         return
     }
     var xAxis = $(this).offset().left;
     var yAxis = $(this).offset().top;
     networkVariable.currentPosition = $(this).offset();
     networkVariable.currentElement = $(this)[0]
     $('.reposition-modal').hide();
     $('body').removeClass('draggingEnabled');
     $(".newobject-modal").hide()

     //$("#deviceHeading").text($(this).attr("name"));
     setTimeout(function() {
         $(this).appendTo("svg");
         var uid=$(this).attr("dev-uid");
     var status=$(this).attr("dev-status")
     $("#deviceHeading").text(uid);
     $("#status").text(status);
         networkVariable.setModalPosition(modelType, xAxis, yAxis);
         // clearTimeout(timer)
     }.bind(this), 200)
     // return true;
 }
 /* ==================================================
Reposition Modal Functions
==================================================*/
 $('#reposition-menu').on('click', function(e) {
     e.stopPropagation();
     $("#duplicate").hide();
    // $(".panzoom").panzoom("disable");
     
     $elem.panzoom("disable");
     

     
     $(networkVariable.currentElement).attr('class', "draggable") 
     if (!isTouchDevice) {
         networkVariable.init()
     }
     
     //console.log ("Reposition")
     reposition = true;
     $(".left-section").children().show();
     $(".macAddress").hide();
     networkVariable.originalPositon = {
         "x": $(networkVariable.currentElement).attr("x"),
         "y": $(networkVariable.currentElement).attr("y")
     }
     $(networkVariable.currentElement).attr('ismovable', true)
     $(".networkconfig").hide()
     // networkVariable.setModalPosition($('#reposition-modal'), $(".draggable").offset().left, $(".draggable").offset().top);
     $("#reposition-modal").show();
     $('body').addClass('draggingEnabled');
     $(".left-section p").text("To reposition the device, Drag to a correct location on the floorplan.")
     $("#delete-device").addClass("hide");
     $("#save-reposition").show();
     $("#cancel-reposition span").text("Undo")
     $("#cancel-reposition label img").hide().last().show();
 });

 $("#cancel-reposition").on('click', function(e) {
     e.stopPropagation(); 
     $elem.panzoom("enable");
     var attr=$(this).attr("isDelete");
     if(attr=="true")
     {
        $("#reposition-modal").hide();
        $('body').removeClass('draggingEnabled');
        $(this).attr("isDelete",false);
        return;
     }
     //console.log(networkVariable.currentElement)
     var is_newobject = $(networkVariable.currentElement[0]).attr("isnewobject");
     if (is_newobject == "true") {
         $(networkVariable.currentElement).remove()
     } else {
         $(networkVariable.currentElement).attr({
             "x": networkVariable.originalPositon.x,
             "y": networkVariable.originalPositon.y,
             "ismovable": "false"
         });
     }
     $(".draggable").attr({
         "class": "image",
         'ismovable': false
     });
     $("#reposition-modal").hide();
     $('body').removeClass('draggingEnabled');
     reposition = false;
     //$(".panzoom").panzoom("enable")
     disableZoom: false;
     $('.plus.zoom-out').trigger('click');

 });
 $("#deleteDevice").on("click", function() {
     var noServerFound = '<li id="noServerFound">No Servers found!</li>';
     $("#reposition-modal .left-section p").text("Are you sure you want to delete the device?");
     $("#save-reposition").hide();
     $("#delete-device").removeClass("hide");
     $("#reposition-modal .macAddress").hide();
     $("#cancel-reposition").attr("isDelete",true);
     $("#duplicate").hide();
     $("#reposition-modal .left-section label").show();
     $("#reposition-modal .left-section p").show();
     $(".macId").val("")
     $("#cancel-reposition span").text("Cancel")
     $("#reposition-modal").show();
     $('body').addClass('draggingEnabled');

 })
 $("#delete-device").on("click",function(){
    	 var elementType = $(networkVariable.currentElement).attr("type");
         var deviceName  = $(networkVariable.currentElement).attr("name");
         var pid  = $(networkVariable.currentElement).attr("pid");
         var parent  = $(networkVariable.currentElement).attr("parent");
         var str = "";
         
         console.log ("pid " + pid + "parent " + parent);
         
         console.log ("e " + elementType + " d " + deviceName);
         
		 $.each(networkVariable.currentElement.attributes, function() {
		    // this.attributes is not a plain object, but an array
		    // of attribute nodes, which contain both the name and value
		    //if(this.specified) {
		      console.log(this.name, this.value);
		    //}
		 });
		  
		 for (var key in networkVariable.networkTree) {
         	 console.log ("Key==> " + key)
         }
         
         var i = 0;
     	 if (parent == "ble") {
     		var num = deviceName.match(/\d+/g);
     		console.log ("Number " + num + "Int " + num[0]);
     		str = "server" + num;
     		console.log ("str==> " + str)
     		networkVariable.networkTree[str].deleteDevices(str, 0)
     		location.reload();
     	 }else if (parent == "ap") {
     		var num = deviceName.match(/\d+/g);
     		//console.log ("Number " + num + "Int " + num[0]);
     		str = "server" + num;
     		//console.log ("str==> " + str)
     		networkVariable.networkTree[str].deleteDevices(str, 0)
     		location.reload();
     	 }else if (elementType == "server") {

             networkVariable.networkTree[deviceName].deleteDevices(deviceName, 0)
         
         } else {
             for (var key in networkVariable.networkTree) {
                 networkVariable.networkTree[key].deleteDevices(deviceName, i)
                 i++;
             }
         }
         if (nodeData["total"] == 0) {
             $("#network-tree").html(noServerFound)
         }
         var deviceText = nodeData["total"] == 1 ? " Device" : " Devices";
         $(".device-section span").text(nodeData["total"] + deviceText);
         $("#" + $(networkVariable.currentElement).attr("deviceId")).remove();
         $(".networkconfig").hide();
         
 })
 $("#save-reposition").on('click', function(e) {
     e.stopPropagation();
     networkVariable.currentUid = ""
     var is_newobject = $(networkVariable.currentElement).attr("isnewobject");
     var elementOffset = $(".draggable").offset()
     
     //console.log ("Save Reposition" + is_newobject)
     if (!networkVariable.validateMacid()) {
         return
     }
     
    
     if (is_newobject == "true") {
         var nodeType = $('#main-div').data('type');
         var parentValue = $('#main-div').data('parentValue');
         //console.log ("Parent Value " + parentValue)
         parentValue=parentValue.split("-");
         $("#noServerFound").addClass("hide");
         nodeData[nodeType] += 1;
         nodeData["total"] += 1;
         var deviceText = nodeData["total"] == 1 ? " Device" : " Devices";
         $(".device-section span").text(nodeData["total"] + deviceText);
         $(networkVariable.currentElement[0]).attr({
             "ismovable": "false",
             "isnewobject": "false",
             'dev-uid':networkVariable.currentUid,
             "dev-status":"Offline",
             "class": "draggable",
             "name": nodeType + "" + nodeData[nodeType],
             "deviceId": nodeType + "-id-" + nodeData[nodeType]
         })
         var parentNode=parentValue[1];
        
         networkVariable.addNode(nodeType, parentNode, 'Offline',networkVariable.currentUid);
         var x = $(networkVariable.currentElement[0]).attr("x");
         var y = $(networkVariable.currentElement[0]).attr("y");
         var deviceId = nodeType + "" + nodeData[nodeType]
         
         if (nodeType == "server") {
             var NetworkTree = new networkTree();
             var newObject = NetworkTree.createParent(nodeType, nodeData[nodeType], x, y, networkVariable.currentUid)
             networkVariable.networkTree[deviceId] = newObject;
            
         }  else if(parentNode=="Master"){
        	 nodeType = $('#main-div').data('type');
        	 var NetworkTree = new networkTree();
        	 console.log("Master ");
             var newObject = NetworkTree.createParent(nodeType, nodeData[nodeType], x, y, networkVariable.currentUid)
             networkVariable.networkTree[deviceId] = newObject;
        	 
     	 } else {
        	
             networkVariable.updateTree(nodeType, x, y, "addChild", parentNode, networkVariable.currentUid)
              
         }
         
         //console.log ($("#cband").is(':checked'));

     }
     $("#deviceHeading").text($(networkVariable.currentElement).attr("name"));
     $(networkVariable.currentElement).attr({
         "ismovable": "false"
     })
     $(".draggable").attr({
         "class": "image",
         'ismovable': false
     })
     reposition = false;
     if (is_newobject == "false") {
    	 
         var nodeName = $(networkVariable.currentElement).attr("name");
         var offsetX  = $(networkVariable.currentElement).attr("x")
         var offsetY  = $(networkVariable.currentElement).attr("y")
         var parent   = $(networkVariable.currentElement).attr("parent");
         var type 	  = $(networkVariable.currentElement).attr("type");

         console.log ("parentNode" + parent + "Type " + type)

         
    	 //console.log ("Save Reposition Node " + nodeName)
    	 //var parentValue = $('#main-div').data('parentValue');
    	 //console.log ("Save Reposition parent " + parentValue)
         
         networkVariable.updateTree(nodeName, offsetX, offsetY,  'updateDevices', parent)
     }
     // networkVariable.setModalPosition($('.networkconfig'), elementOffset.left, elementOffset.top);
     //$(".panzoom").panzoom("enable")
     disableZoom: false;
 })

  
 
 function svgEvent(evt) {
     // $(".panzoom").panzoom("disable");
	 disableZoom: true;
     if (isnewobject || networkVariable.isNewDevice) {
    	 
    	 svgWidth 	= $('#mapSVG').width();
    	 svgOffset 	= $('#mapSVG').offset()
    	 svgHeight 	= $('#mapSVG').height();
    	 
         $(document).unbind("mousemove");
         var image = $("#" + networkVariable.newDevice + "Offline").attr("src");
         var type = $("#" + networkVariable.newDevice + "Offline").attr("type")
         var x = evt.offsetX ? (evt.offsetX + objectW / 2 > svgWidth ? evt.offsetX - objectW / 2 : evt.offsetX) : evt.originalEvent.layerX;
         var y = evt.offsetY ? (evt.offsetY + objectH / 2 > svgHeight ? evt.offsetY - objectH / 2 : evt.offsetY) : evt.originalEvent.layerY;
         
         console.log ("Width"  + svgWidth);
         console.log ("Height" + svgHeight);
        
         if(networkVariable.isNewDevice){
            networkVariable.isNewDevice=false;
            type=networkVariable.newDeviceType;
         }
         networkVariable.newObject(image, type, x, y)
         isnewobject = false;
         $("#" + networkVariable.newDevice + "Offline").hide();
         //$(".panzoom").panzoom('disable')
         disableZoom: true;
         //Show the mac id text box
         $(".left-section").children().hide();
         $(".macAddress").show();
         $("#reposition-modal").show();
         $('body').addClass('draggingEnabled');
         $("#reposition-modal .left-section p").text("To reposition the device, Drag to a correct location on the floorplan.")
         $("#reposition-modal .rightsection #save-reposition").show();
         $("#reposition-modal .rightsection #delete-device").addClass("hide");
         $("#reposition-modal .rightsection").show();
         //console.log (type);
        
         return;
     }
     if (reposition && isTouchDevice) {

         var left = evt.offsetX ? evt.offsetX : evt.layerX;
         var top = evt.offsetY ? evt.offsetY : evt.layerY;
         if (networkVariable.isNewDevice)
             networkVariable.newObject(networkVariable.newImage, networkVariable.newDevice, left, top)
         else
             $(networkVariable.currentElement).attr({
                 'x': left,
                 'y': top
             })
             // $(networkVariable.currentElement).trigger("click")
         $("#reposition-modal").show();
         $('body').addClass('draggingEnabled');
         networkVariable.isNewDevice = false
         //$(".panzoom").panzoom("disable");
         disableZoom: true;
         $("#reposition-modal").show();
         $('body').addClass('draggingEnabled');
         $("#reposition-modal .left-section p").text("To reposition the device, click on the floorplan.")
         $("#reposition-modal .rightsection").show();
         evt.stopPropagation();
         return
     }
     // $(".panzoom").panzoom("dis");
     disableZoom: true;
     $(".networkconfig").hide();
     if (!reposition)
       // $(".panzoom").panzoom("enable");
     disableZoom: false;

 }
 window.hexaError = false;

 function autohexaFill(evt) {
     if (hexaError)
         return;
     if (evt.target.value.length == 1) {
         var value = $(evt.target).val();
         value = "0" + value;
         $(evt.target).val(value)
     }
 }

 function checkHexaValues(evt) {
     var regx = new RegExp(/^[a-f0-9]+$/i)
     if (regx.test(evt.target.value)) {
         hexaError = false
         return true;
     } else {
         var value = $(evt.target).val();
         var value = value.substr(0, value.length - 1);
         $(evt.target).val(value)
         hexaError = true;
         return false
     }
 }

 function macIdValidation(evt) {
     checkHexaValues(evt)
     if (evt.target.value.length == 2)
         $(evt.target).parent().next().next().find("input").focus();
     if (evt.target.value.length > 2) {
         var value = $(evt.target).val();
         var value = value.substr(0, value.length - 1);
         $(evt.target).val(value)
         return false;
     }
 }

      	 

 $(".macId").on('blur', autohexaFill);
 $(".macId").attr('onInput', "macIdValidation(event)")
 // $(".macId").attr('onKeydown',"checkTab(event)")
 $(".zoomControl").on('click', function() {
     if ($(".draggable[ismovable='true']").length)
         return
     //$(".panzoom").panzoom("enable");
         disableZoom: false;
     $(".networkconfig").hide();
     $(".reposition-modal").hide();
     $('body').removeClass('draggingEnabled');
 })


 $(document).click(function(e) {
     if ($(e.target).parent().hasClass("addobject") || $(".addobject").is(e.target) || $("image").is(e.target) || $(e.target).closest(".deviceForm").length) {
         return
     }

     if (!$(e.target).is($("#mapSVG")) && isnewobject) {
         $("#reposition-modal").hide();
         $('body').removeClass('draggingEnabled');
         isnewobject = false;
        

         $(document).unbind("mousemove")
     }
     if ((!$(e.target).is($("#mapSVG")) || !isnewobject) && !networkVariable.isNewDevice) {
         $("#" + networkVariable.newDevice + "Offline").hide();
         isnewobject = false;
         
         $(document).unbind("mousemove")
     }
     $(".popup-switch").addClass("hide");
     $(".networkconfig").hide();
 });
  
 $("svg").on("click tap","image",function(evt){
    if(reposition)
        return
        
    if(heatmap != "true"){
        window.location.href=evt.currentTarget.getAttribute("hyperLink")
    }
 })
  


 function moveDevice(evt) {
     if ($(evt.target).closest("#mapSVG").length)
         $("#" + networkVariable.newDevice + "Offline").show().css({
             position: 'absolute',
             left: evt.pageX + 2,
             top: evt.pageY + 2
         })
     else
         $("#" + networkVariable.newDevice + "Offline").hide();
 }

 /* =============================
Network Functions 
=============================*/
 var networkVariable = {
         'deviceOptions': {
             'server': [],
             'switch': [],
             'sensor': [],
             'ap': []
         },
         networkTree: {},
         childDevices: [],
         init: function() {
             Snap("#mapSVG .draggable").drag(networkVariable.dragMove, networkVariable.dragStart, networkVariable.dragEnd);

             // $(".draggable").draggable();
         },
         dragStart: function(x, y, evt)

         {
        	 $('.draggable').closest('a').click(function(e){
        			e.preventDefault();
        		});
             try {
                 startX = parseInt(Snap(".draggable").attr("x"), 10);
                 startY = parseInt(Snap(".draggable").attr("y"), 10);
                 $("reposition-modal").toggle()
                 $('body').toggleClass('draggingEnabled');
             } catch (err) {
            	 
             }
         },
         dragMove: function(dx, dy, x, y, evt) {
             var ismovable = $(".draggable").attr("ismovable")
             if (ismovable == "true") {
                 $(".reposition-modal").hide();
                 $('body').addClass('draggingEnabled');
                 $(".networkconfig").hide();
                 if ($(evt.target).is($("#mapSVG")) || $(evt.target).is($("image"))) {
                     Snap(".draggable").attr({
                         "x": typeof InstallTrigger == "undefined" ? evt.offsetX - 10 : evt.layerX - 10,
                         "y": typeof InstallTrigger == "undefined" ? evt.offsetY - 5 : evt.layerY - 5
                     });
                 } else {
                     return;
                 }
             }
             $('.draggable').closest('a').click(function(e){
         		e.preventDefault();
         	});

         },
         dragEnd: function() {
             $('image[ismovable="true"]').trigger("click");
             var imageOffset = $(".draggable").offset();
             $(".networkconfig").hide();
             // $(".reposition-modal label").text("If you are sure ,save this Location");
             $(".reposition-modal #save-reposition").show();
             //$(".panzoom").panzoom('disable')
             disableZoom: true;

             
         },
         'moveElementCSS': function(elem, top, left) {
             $(elem).css({
                 position: 'absolute',
                 top: top,
                 left: left
             });
         },
         'newObject': function(image, type, x, y,uid,initial, id,status,parent) {
        	 
             var x = x ? x : "";
             var y = y ? y : "";
             $('#main-div').data('newElement', 1);
             $('#main-div').data('type', type);
             $(".popup-switch").addClass("hide");
             var isNewObject = $(".draggable").attr("isnewobject");
             var urlMap={
            	"server":'flrdash',
            	'switch':'swiboard',
            	'ap':'devboard',
            	'sensor':'devboard'
        		}
             
        	 this.fetchurlParams(window.location.search.substr(1));
              
             
			 if (type == "server") {
				 
				console.log ("Finder " + finder);
				
				if (finder == "true") {
					console.log ("Finder TRUE Type Server")
				 	var url="/facesix/web/site/portion/dashboard"+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&cid="+this.urlObj.cid+"&type="+"server" 
				} else {
					console.log ("Finder FALSE Type Server")
				 	var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&cid="+this.urlObj.cid+"&type="+"server" 					
				}
				
			 }
			 else if (type == "sensor") {
				 if (gateway != "true") {
				 var url="/facesix/web/finder/device/"+urlMap[type]+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&uid="+uid+"&cid="+this.urlObj.cid+"&type="+"sensor" 
				 } else {
						var url = "javascript:void(0)"
					}
			 } else if (type == "switch") {
				 		if(finder != "true"){
				 			var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&uid="+uid+"&cid="+this.urlObj.cid+"&type="+"sensor" 
		       } else {
					var url = "javascript:void(0)"
				}
		     }
			 else if (type == "ap") {
						if(finder != "true"){
							var url="/facesix/web/site/portion/"+urlMap[type]+"?sid="+this.urlObj.sid+"&spid="+this.urlObj.spid+"&uid="+uid+"&cid="+this.urlObj.cid+"&type="+"device" 			
				} else {
					var url = "javascript:void(0)"
				}
				}
			
        	 //var url="/facesix/web/site/portion/"+urlMap[type]+"?uid="+uid+"&type="+(type=="switch" || type=="server"?type:"device")+"&spid="+this.urlObj.spid        
             if (isNewObject == undefined || isNewObject == "false" || initial) {
                 $("#cancel-reposition span").text("Cancel")
                 $("#cancel-reposition label img").hide().first().show();
                 //$(".panzoom").panzoom('disable');
                 disableZoom: true;
                 
                 if (!$("image[type=" + type + "]").length && !initial)
                     nodeData[type] = 0;
                 if(!initial){
                 var anchor=svg.append("a")
        		            .attr("xlink:href",url)    
                 var newObject =anchor.append("image").attr({
                     "class": "draggable",
                     "xlink:href": image,
                     "x": x,
                     "y": y,
                     'hyperLink':url,
                     "width": objectW,
                     "height": objectH,
                     "isnewobject": initial ? false : true,
                     "type": type,
                     "ismovable": initial ? false : true,
                     "parent":parent
                 });
                 $(anchor[0]).appendTo('svg'); 
                 }
                 //$(".panzoom").panzoom("disable");
                 disableZoom: true;
                 if (!initial) {
                     $(".reposition-modal").show();
                     $('body').addClass('draggingEnabled');
                     networkVariable.currentElement = newObject[0];
                     if (!isTouchDevice)
                         networkVariable.init();

                 } else {
                	 
                     var anchor=svg.append("a").attr("xlink:href",url)
        		  	 var newObject=anchor.append("image").attr({
                     			"xlink:href": image,
                     			"x": x,
                     			"y": y,
                     			"width": objectW,
                    			"height": objectH,
                     			"isnewobject": initial ? false : true,
                     			"type": type,
                                "hyperLink":url,
                     			"dev-status":status,
                     			"dev-uid":uid,
                     			"ismovable": initial ? false : true,
                         		"class": "image",
                         		"name": type + "" + id,
                         		"deviceId": type + "-id-" + id,
                         		"parent":parent
                     		  })
                     $(anchor[0]).appendTo("svg")
                 }
                 $("image").bind('contextmenu', svgImageEvent);
                 // $(".reposition-modal label").text("Pin the element to it's position");
                 // networkVariable.setModalPosition($(".networkconfig"), $(newObject[0]).offset().left, $(newObject[0]).offset().top);
             }
             if(heatmap == "true"){
            	 $('#mapSVG a').attr('href', 'javascript:void(0)');
            	 $('#mapSVG image').removeAttr('hyperLink');
             } 
         },
         //Update the network tree list
         "updateTree": function(nodeType, x, y, exec, parentValue, uid) {
        	 
             for (var key in this.networkTree) {
            	 console.log ("Key" + key + "Parent" + parentValue + "nodeType" + nodeType);
            	 if (parentValue ==  "ble" && (nodeType.indexOf("sensor")) > -1) {
            		 //eval("this.networkTree[key]." + exec + "(nodeType, x, y,parentValue,uid)");
            		 //break;
            	 }
            	 if (parentValue ==  "ap" && (nodeType.indexOf("ap")) > -1) {
            		 //eval("this.networkTree[key]." + exec + "(nodeType, x, y,parentValue,uid)");
            		 //break;
            	 }
            	 //console.log ("UID" + uid);
                 eval("this.networkTree[key]." + exec + "(nodeType, x, y,parentValue,uid)");
             }
         },

         'addNode': function(type, parent, status,uid,id) {
        	//console.log(parent);
             var id = id ? id : nodeData[type]
             var network = {}
             if (finder == "true") {
                 network['server'] = '<li class="deviceInfo" id="server-id-' + id +
                 '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Server" data-uid="'+uid+'" data-href="/facesix/web/site/portion/dashboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=server&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-bref="#" data-cref="#" data-sref="#" class="device-name"><label>' +
                 '<i class="fa fa-2x fa-angle-down" aria-hidden="true"></i>' +
                 '<img src="/facesix/static/qubercomm/images/networkconfig/icon/server_inactive.png" alt=""></label>' +
                 '<span>SVR-' + uid + '</span><label class="connected device-status pull-right">' +
                 '<span>' + status + '</span></label></div></a>' +
                 '<ul class="child list-unstyled" parent-id="'+uid+'" id="server' + id + '-tree"></ul></li>';            	 
             } else if(GatewayFinder == "true"){
            	 network['server'] = '<li class="deviceInfo" id="server-id-' + id +
                 '"><a class="dashbrdLink"><div data-status="'+status+'" data-type="Server" data-uid="'+uid+'" data-href="/facesix/web/site/portion/dashboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=server&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'&param=1" data-bref="#" data-cref="#" data-sref="#" class="device-name"><label>' +
                 '<i class="fa fa-2x fa-angle-down" aria-hidden="true"></i>' +
                 '<img src="/facesix/static/qubercomm/images/networkconfig/icon/server_inactive.png" alt=""></label>' +
                 '<span>SVR-' + uid + '</span><label class="connected device-status pull-right">' +
                 '<span>' + status + '</span></label></div></a>' +
                 '<ul class="child list-unstyled" parent-id="'+uid+'" id="server' + id + '-tree"></ul></li>';
            	 
             } else if (heatmap == "true"){
            	 
            	 network['server'] = '<li class="deviceInfo" id="server-id-' + id +
                 '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Server" data-uid="'+uid+'" data-href="#" data-bref="#" data-cref="#" data-sref="#"  class="device-name"><label>' +
                 '<i class="fa fa-2x fa-angle-down" aria-hidden="true"></i>' +
                 '<img src="/facesix/static/qubercomm/images/networkconfig/icon/server_inactive.png" alt=""></label>' +
                 '<span>SVR-' + uid + '</span><label class="connected device-status pull-right">' +
                 '<span>' + status + '</span></label></div></a>' +
                 '<ul class="child list-unstyled" parent-id="'+uid+'" id="server' + id + '-tree"></ul></li>';
             }
             
             else {
                 network['server'] = '<li class="deviceInfo" id="server-id-' + id +
                 '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Server" data-uid="'+uid+'" data-href="/facesix/web/site/portion/dashboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=server&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-bref="#" data-cref="#" data-sref="#" class="device-name"><label>' +
                 '<i class="fa fa-2x fa-angle-down" aria-hidden="true"></i>' +
                 '<img src="/facesix/static/qubercomm/images/networkconfig/icon/server_inactive.png" alt=""></label>' +
                 '<span>SVR-' + uid + '</span><label class="connected device-status pull-right">' +
                 '<span>' + status + '</span></label></div></a>' +
                 '<ul class="child list-unstyled" parent-id="'+uid+'" id="server' + id + '-tree"></ul></li>';            	 
             }

             if (finder == "true") {
                 network['switch'] = '<li  class="deviceInfo" id="switch-id-' + id +
                 '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Switch" data-uid="'+uid+'" data-href="#" data-bref="#" data-cref="#" data-sref="#" class="device-name"><label>' +
                 '<img src="/facesix/static/qubercomm/images/networkconfig/icon/switch_inactive.png" alt=""></label>' +
                 '<span>SW-' + uid + '</span><label class="connected device-status pull-right">' +
                 '<span>' + status + '</span></label></div></a>' +
                 '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="switch' + id + '-tree"></ul></li>';            	 
             } else if(heatmap == "true"){
            	 network['switch'] = '<li  class="deviceInfo" id="switch-id-' + id +
                 '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Switch" data-uid="'+uid+'" data-href="#" data-bref="#" data-cref="#" data-sref="#" class="device-name"><label>' +
                 '<img src="/facesix/static/qubercomm/images/networkconfig/icon/switch_inactive.png" alt=""></label>' +
                 '<span>SW-' + uid + '</span><label class="connected device-status pull-right">' +
                 '<span>' + status + '</span></label></div></a>' +
                 '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="switch' + id + '-tree"></ul></li>';
           	 
            	 
             } else {
                 network['switch'] = '<li  class="deviceInfo" id="switch-id-' + id +
                 '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Switch" data-uid="'+uid+'" data-href="/facesix/web/site/portion/swiboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=switch&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-bref="#" data-cref="#" data-sref="#" class="device-name"><label>' +
                 '<img src="/facesix/static/qubercomm/images/networkconfig/icon/switch_inactive.png" alt=""></label>' +
                 '<span>SW-' + uid + '</span><label class="connected device-status pull-right">' +
                 '<span>' + status + '</span></label></div></a>' +
                 '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="switch' + id + '-tree"></ul></li>';            	 
             }


             if (finder == "true") {
                 network['ap'] = '<li  class="deviceInfo" id="ap-id-' + id + '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Ap" data-uid="'+uid+'" data-href="#" data-cref="#" data-bref="#" data-sref="#" class="device-name"><label><img src="/facesix/static/qubercomm/images/networkconfig/icon/ap_inactive.png" alt="">' +
                 '</label><span>AP-' +uid + '</span><label class="connected device-status pull-right"><span>' + status + '</span>' +
                 '</label></div></a>' + '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="ap' + id + '-tree"></ul></li>';            	 
             } else {
                 network['ap'] = '<li  class="deviceInfo" id="ap-id-' + id + '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Ap" data-uid="'+uid+'" data-href="/facesix/web/site/portion/devboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=device&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-cref="/facesix/web/device/custconfig?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-bref="/facesix/web/finder/device/binary?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-sref="/facesix/scan?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" class="device-name"><label><img src="/facesix/static/qubercomm/images/networkconfig/icon/ap_inactive.png" alt="">' +
                 '</label><span>AP-' +uid + '</span><label class="connected device-status pull-right"><span>' + status + '</span>' +
                 '</label></div></a>' + '<ul class="list-unstyled childOfChild" parent-id="'+uid+'" id="ap' + id + '-tree"></ul></li>';            	 
             }

             network['sensor'] = '<li  class="deviceInfo" id="sensor-id-' + id + '"><a class="dashbrdLink" href="#"><div data-status="'+status+'" data-type="Sensor" data-uid="'+uid+'" data-href="/facesix/web/finder/device/devboard?sid='+this.urlObj.sid+'&uid='+uid+'&type=device&spid='+this.urlObj.spid+'&cid='+this.urlObj.cid+'" data-cref="/facesix/web/finder/device/configure?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-bref="/facesix/web/finder/device/binary?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" data-sref="/facesix/web/beacon/list?sid='+this.urlObj.sid+'&spid='+this.urlObj.spid+'&uid='+uid+'&cid='+this.urlObj.cid+'" class="device-name"><label><img src="/facesix/static/qubercomm/images/networkconfig/icon/sensor_inactive.png" alt="">' +
                 '</label><span>BLE-' + uid + '</span><label class="connected device-status pull-right ">' +
                 '<span>123' + status + '</span></label></div></a></li>';
            //console.log(parent);
            if(parent == "ble")
            	$('#network-tree').append(network["sensor"]);
            else if(parent == "ap")
            	$('#network-tree').append(network["ap"]);
            else if(type=="server")
                $('#network-tree').append(network[type]);
            else
                $("ul[parent-id='"+parent+"']").append(network[type])
            
             var parentSelect = type + id;
             networkVariable.deviceOptions[type].push(parentSelect);
         },
         'toggleDevicePopup': function(left, top, type, addId, count) {
        	 if (!count.length) {
                 $(".errorModal").show();
                 $(".errorModal span").html("Please select a " + type.toUpperCase() + "")
                 return;
             }
             $(".popup-switch").addClass("hide")
             $(".errorModal").hide();
             $(".master-form").html(template({
                 options: count,
                 id: addId
             }));
            
             $(".popup-switch").removeClass("hide");
             $(".networkconfig").hide();
             

             networkVariable.moveElementCSS($(".popup-switch"), top, left)
         },
         'validateMacid': function() {
             if ($(".macAddress").css("display") != 'none') {
                 $(".macId").each(function(index, node) {
                     if ($(this).val() == "") {
                         $(this).addClass("addborder")
                         return false;
                     } else
                         networkVariable.currentUid += $(this).val() + (index < 5 ? ":" : "");
                 })
                 if (networkVariable.currentUid.replace(/:/g, "").length < 12)
                     return false;
             }
             return true;
         },
         'deleteImages': function(deviceTree) {
             var deviceId = deviceTree.type + "" + deviceTree[deviceTree.type + "_id"]
             var nodeId= deviceTree.type+"-id-"+ deviceTree[deviceTree.type + "_id"]
             $("image[name=" + deviceId + "]").remove();
             nodeData["total"] -= 1;
             for (var i = 0; i < deviceTree.child.length; i++)
                 this.deleteImages(deviceTree.child[i])
             var deviceCount=nodeData['total']==1?"1 Device":nodeData['total']+" Devices"
             $(".device-section span").text(deviceCount)
             $("#"+nodeId).remove();
             //$(".panzoom").panzoom("enable")
             disableZoom: false;
         },
         'buildInitialNetwork': function() {
             for (var key in this.networkTree)
                 this.networkTree[key].initialSetup();
             $("#noServerFound").addClass("hide");
             var deviceText = nodeData["total"] == 1 ? " Device" : " Devices";
             $(".device-section span").text(nodeData["total"] + deviceText);
             reposition = false;
         },
         setModalPosition: function(modeltype, xAxis, yAxis) {
             var xValue, yValue;
             var modelWidth = parseInt(modeltype.width());
             var modelHeight = parseInt(modeltype.height());
             var sidebarWidth = $('#sidebar-wrapper').hasClass("hide") ? 0 : $('#sidebar-wrapper').width() - 20;
             var mapWidth = $(".main-section-activity").width() + sidebarWidth;
             var mapHeight = $(".main-section-activity").height();
             var totalWidth = modelWidth + xAxis + (objectW / 2);
             var totalHeight = modelHeight + yAxis;
             var widthCheck = totalWidth > mapWidth ? "widthPlus" : "widthMinus";
             var heightCheck = totalHeight > mapHeight ? "heightPlus" : "heightMinus";

             if (widthCheck == "widthPlus" && heightCheck == "heightPlus") {
                 xValue = xAxis - modelWidth;
                 yValue = yAxis - modelHeight;
                 networkVariable.showModal(modeltype, xValue, yValue);

             } else if (widthCheck == "widthMinus" && heightCheck == "heightMinus") {
                 xValue = xAxis + (objectW / 2);
                 yValue = yAxis + (objectW / 2);
                 networkVariable.showModal(modeltype, xValue, yValue);


             } else if (widthCheck == "widthPlus" && heightCheck == "heightMinus") {
                 xValue = xAxis - modelWidth + (objectW / 2);
                 yValue = yAxis + (objectW / 2);
                 networkVariable.showModal(modeltype, xValue, yValue);


             } else if (widthCheck == "widthMinus" && heightCheck == "heightPlus") {

                 xValue = xAxis + (objectW / 2);
                 yValue = yAxis - modelHeight + (objectW / 2);
                 networkVariable.showModal(modeltype, xValue, yValue);
             }

         },
         cancelReposition: function() {
             $(".draggable").attr({
                 "x": networkVariable.originalPositon.x,
                 "y": networkVariable.originalPositon.y,
                 "ismovable": "false"
             })
             

             $('#reposition-modal').hide();
             $('body').removeClass('draggingEnabled');

         },
         showModal: function(modeltype, xValue, yValue) {
             modeltype.show().css({
                 "left": xValue,
                 "top": yValue,
                 "z-index": 99,
                 "position": "absolute"
             });

         },
         toJSON: function() {
             var json = {};

             // $("#mapSVG").attr()

         },
         'showNewObjectImage': function(evt, type, child) {
             if (isTouchDevice) {
                 networkVariable.newImage = '/facesix/static/qubercomm/images/networkicons/' + type + '_inactive.png';
                 networkVariable.isNewDevice = true;
                 networkVariable.newDeviceType=type;
             } else {
                 isnewobject = true;
                 // $("#movedevice").remove();
                 // var image = 'images/networkicons/' + type + '_inactive.png';
                 // var img = document.createElement("img");
                 // img.src = image;
                 $("#" + type + "Offline").show().css({
                     'position': 'absolute',
                     left: evt.pageX + 5,
                     top: evt.pageY + 5,
                     'z-index': 9999
                 }).attr({
                     type: type
                 })
                 evt.stopPropagation();
                 $(document).bind("mousemove", moveDevice)
             }
             reposition = true;
             networkVariable.newDevice = type;
             networkVariable.childType = child;
             //Show the left section
             $(".left-section").children().show();
             $(".macAddress").hide();
             $(".macId").val("")
			 		 
			         
             $("#deviceSelect").removeClass("border-error")
             $(".popup-switch").addClass("hide");
            // $(".panzoom").panzoom("disable");
             disableZoom: true;
             $(".errorModal").hide();
             $("#reposition-modal").show();
             $('body').addClass('draggingEnabled');
             $(".rebootPopup").hide();
             $("#reposition-modal .left-section p").text("Click on the floorplan to place the device");
             //$("#reposition-modal .left-section label").text("QuberCloud");
             $("#reposition-modal .rightsection").hide();
             $("#duplicate").hide();
             
             $('.draggable').closest('a').click(function(e){
         		e.preventDefault();
         	});

         },
         'saveNetworkDevice': function(uid, devices, parent, deviceType, pid,currentObj,ble, ap) {
	     devices['parent']=pid?pid:'';
	   
	     var myble = devices.parent;
	     var devtyp = devices.type;
	     if(ble=="ble") {
	    	 myble = "ble";
	    	 devtyp = "bleserver"
	     } else if(ble=="ap"){
	    	 myble  = "ap";
	    	 devtyp = "apserver"
	     }
	     
	     var networkconfig={
	       uid:devices.uid,
	       xposition:devices.xposition,
	       yposition:devices.yposition,
	       status:devices.status,
	       type:devtyp,
	       sid:devices.sid,
	       spid:devices.spid,
	       parent:myble,
	       gparent:currentObj.parent,	       
	     };
	     
	      console.log (networkconfig);
	      	    
             $.ajax({
                 method: 'post',
                 url: '/facesix/rest/site/portion/networkdevice/save',
                 data:JSON.stringify(networkconfig),
		 		 headers: {
                     'content-type': 'application/json'
                 },
                 success: function(response) {
                    console.log(response)
                     if(!response){
                        $("#duplicate").show();
                        var nodeId= devices.type+"-id-"+ devices[devices.type + "_id"]
                        nodeData[devices.type] -= 1;
                        nodeData["total"] -= 1;
                        if(!parent)
                          delete networkVariable.networkTree[devices.type+""+devices[devices.type+"_id"]]
                        $(networkVariable.currentElement).attr({
                            "isnewobject":true,
                            "class":"draggable"
                        })
                        $("#"+nodeId).remove();
                      return;
                     }
                     $(".macId").each(function() {
                         $(this).val("")
                         $(this).removeClass("addborder");
                     });
                     if (!parent)
                         currentObj.devices = devices
                     else
                         currentObj.child.push(devices);
                      var deviceMap={
                            "server":"SVR",
     						"switch":"SW",
     						"ap":"AP",
     						"sensor":"SNR"
    			     }    
                     if(nodeData[devices.type]==1){
                			var parentNode=deviceMap[devices.type]+"-"+devices.uid;
                			if(!networkVariable.initialParent)
                			networkVariable.initialParent=parentNode;
                     }    
                     $('#reposition-modal').hide();
                     $('body').removeClass('draggingEnabled');
                     if(devices.type == "ap"){
                    	 search = window.location.search.substr(1)
                    	 url	   = JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
                    	 var path = "/facesix/web/device/custconfig?sid="+url.sid+"&spid="+url.spid+"&uid="+networkVariable.currentUid+"&cid="+url.cid
                    	 window.location.replace(path);
                     }
                     if(devices.type == "sensor"){
                    	 search = window.location.search.substr(1)
                    	 url	   = JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
                    	 var path = "/facesix/web/finder/device/configure?sid="+url.sid+"&spid="+url.spid+"&uid="+networkVariable.currentUid+"&cid="+url.cid
                    	 window.location.replace(path);
                     }
                 },
                 error: function(err) {
                     //console.log(err);
                     $(".macId").each(function() {
                         $(this).val("")
                         $(this).removeClass("addborder");
                     });
                     $(networkVariable.currentElement).remove();
                     nodeData[devices.type] -= 1;
                     if (nodeData["total"] == 0) {
                         $("#network-tree").html(noServerFound)
                     }
                     var noServerFound = '<li id="noServerFound">No Servers found!</li>';
                     var deviceText = nodeData["total"] == 1 ? " Device" : " Devices";
                     $(".device-section span").text(nodeData["total"] + deviceText);
                     $("#" + $(networkVariable.currentElement).attr("deviceId")).remove();
                     $("div[data-uid='"+deviceTree.uid+"']").closest("li").remove();
                     $(".networkconfig").hide();
                 }
             })
         },
         'updateCoords':function(device,x,y){
             var self=this;
               var networkconfig={
           uid:device.uid,
           xposition:x,
           yposition:y,
           status:device.status,
           type:device.type,
           sid:device.sid,
           spid:device.spid,
           parent:device.parent
           
         }
             $.ajax({
                url:'/facesix/rest/site/portion/networkdevice/update',
                method:'post',
                data:JSON.stringify(networkconfig),
                headers:{
                    'content-type':'application/json'
                },
                success:function(response){
                    device.xposition=x,
                    device.yposition=y
                    $("#reposition-modal").hide();
                    $('body').removeClass('draggingEnabled');
                },
                error:function(error){
                   //console.log(error);
                   $(self.currentElement).attr({
                    'x':self.originalPositon.x,
                    'y':self.originalPositon.y
                   });
                }
             })
         },
         'deleteNetworkDevice': function(device, prevArray,index) {
        	 var uid = device.uid;
         	 var spid = device.spid;
         	 var type = device.type;
         	 		console.log("uid " +uid + " spid " +spid + " type " +type);         	        
             $.ajax({
                 method: 'post',
                 url: '/facesix/rest/site/portion/networkdevice/delete?spid='+spid+'&uid='+uid+'&type='+type,

                 success: function(response) {
                    console.log("Deleted successfully");
                    console.log(" respose " +JSON.stringify(response));
                     if (!prevArray) {
                         networkVariable.deleteImages(networkVariable.networkTree[device.type+device[device.type+"_id"]].devices)
                         
                         delete networkVariable.networkTree[device.type+""+device[device.type+"_id"]];
                     } else {
                         networkVariable.deleteImages(device);
                         prevArray.splice(index, 1);
                     }
                     delete networkVariable.initialParent;
                     $("#cancel-reposition").attr("isDelete",false);
                     $("#reposition-modal").hide();
                     $('body').removeClass('draggingEnabled');
                 },
                 error: function(err) {
                    console.log(err)
                    console.log("Cannot delete device")
                 }
             })
         },
	  'fetchurlParams':function(search){
		var urlObj={}
		if(search)
		  urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
		networkVariable.urlObj=urlObj; 
	},
     }
     networkVariable.fetchurlParams(window.location.search.substr(1)) 
     //Network-tree prototyping starts here
 var Device = function(device, id, x, y, uid, childType, parent) {
     this.node = {
         type: device,
         xposition: x,
         yposition: y,
         uid: uid,
         createdBy: 'Qubercomm',
         modifiedBy: 'Qubercomm',
         createdOn: Date.now(),
         modifiedOn: "",
         score: 0,
         ver: "1",
         status: "Added",
         template: null,
         tags: null,
         conf: "",
         childType: childType,
         name: device,
         version: "1",
         child: []
     }
     this.node['sid']=networkVariable.urlObj.sid;
     this.node['spid']=networkVariable.urlObj.spid;
     this.node[device + "_id"] = id;
     parent ? this.node['parent'] = parent : '';
     return this.node
 }
 var networkTree = function(device) {
     if (device) {
         this.devices = device
         return this
     }
     this.devices = {}
 }
 networkTree.prototype.createParent = function(device, id, x, y, uid) {
     var newDevice = Device(device, id, x, y, uid, networkVariable.childType);
     
     var noparent = "";
     if(device=="sensor"){
    	 noparent = "ble";
     } else if(device == "ap"){
    	 noparent = "ap";
     }
     
     
     networkVariable.saveNetworkDevice(uid, newDevice, false, "server", false, this, noparent);
     return this;
 }
 

 networkTree.prototype.addChild = function(nodeType, x, y, parentValue, uid) {
	 var current = this.devices;	
     var i = 0;
     var server = this.devices;
     var deviceMap={
     	"switch":"SW",
     	"ap":"AP",
     	"sensor":"SNR"
     }
     var newDevice = Device(nodeType, nodeData[nodeType], x, y, uid, networkVariable.childType)
     var addSubChild = function(currentDevice, newDevice, parentValue) {
         var id = currentDevice[currentDevice.type + "_id"]
         if (currentDevice.uid== parentValue) {
             networkVariable.saveNetworkDevice(uid, newDevice, currentDevice.type, newDevice.type, currentDevice.uid, currentDevice)
             return
         }
         if (currentDevice.child.length) {
             for (var i = 0; i < currentDevice.child.length; i++)
                 addSubChild(currentDevice.child[i], newDevice, parentValue);
         }
     }
     addSubChild(current, newDevice, parentValue)
 }
 networkTree.prototype.getChild = function(device, deviceCount) {
     var current = this.devices;
     var devicemap={
        "switch":'SW',
        "ap":"AP",
        "sensor":"SNR"
     }
     
     var getSubChild = function(currentDevice, device) {
         var id = currentDevice[currentDevice.type + "_id"];
         
         if (currentDevice.type == device) {
        	 deviceCount.push( devicemap[currentDevice.type]+ "-" +currentDevice.uid)
             return
         }
         
         for (var i = 0; i < currentDevice.child.length; i++)
             getSubChild(currentDevice.child[i], device)
     }
     if(device == "ap"){
	    if(device == "ap" && deviceCount.length==0) deviceCount.push( "BLE-Master");
	     getSubChild(current, device)
     }
     if(device == "switch"){
    	    if(device == "switch" && deviceCount.length==0) deviceCount.push( "AP-Master");
    	     getSubChild(current, device)
   }
 }
 
 networkTree.prototype.deleteDevices = function(device, index) {
     var current = this.devices;
     if (device.indexOf("server") != -1) {
         networkVariable.deleteNetworkDevice(current)
         return
     }
     var deleteDevice = function(currentDevice, device, index, prevArray) {
         var id = currentDevice[currentDevice.type + "_id"]
         if (currentDevice.type + "" + id == device) {
             networkVariable.deleteNetworkDevice(currentDevice, prevArray,index)
             return
         }
         for (var i = 0; i < currentDevice.child.length; i++)
             deleteDevice(currentDevice.child[i], device, i, currentDevice.child)
     }

     deleteDevice(current, device, 0)
 }
 networkTree.prototype.updateDevices = function(nodeType, x, y, parentValue) {
     var current = this.devices;
     var updateChildDevices = function(nodeType, currentDevice, x, y) {
         var id = currentDevice[currentDevice.type + "_id"]
         
         console.log ("Update Device" + nodeType + "ID " + id + "Type " +  currentDevice.type)
         console.log ("Parent Value " + parentValue + " " + currentDevice.type + "" + id)
         
         var nodeArray = nodeType.replace(/[^0-9]+/ig,"");
         console.log ("Node Array" + nodeArray)
         if (parentValue == "ble" && (nodeType.indexOf("sensor") > -1)) {
        	 if (id == nodeArray) {
        		 networkVariable.updateCoords(currentDevice, x, y) 
        	 }
             
             return
         }
         if (parentValue == "ap" && (nodeType.indexOf("ap") > -1)) {
        	 if (id == nodeArray) {
        		 networkVariable.updateCoords(currentDevice, x, y) 
        	 }
             return
         }
                  
         if (currentDevice.type + "" + id == nodeType) {
             networkVariable.updateCoords(currentDevice, x, y)
             return
         }
         
         for (var i = 0; i < currentDevice.child.length; i++)
             updateChildDevices(nodeType, currentDevice.child[i], x, y)
     }
     updateChildDevices(nodeType, current, x, y)
 }
 networkTree.prototype.initialSetup = function() {
	 
     var current = this.devices;
     var buildInitalSetup = function(currentDevice, parent) {
         var image = "/facesix/static/qubercomm/images/networkicons/" + currentDevice.type + "_active.png";
         var type = currentDevice.type
         var id = currentDevice[currentDevice.type + "_id"]
         networkVariable.newObject(image, type, currentDevice.x, currentDevice.y, true /*initial flag*/ , id)
         if (type.indexOf("server") != -1)
             networkVariable.addNode(type, "network", 'Online', id)
         else 
             networkVariable.addNode(type, parent, 'Online', id)
         for (var i = 0; i < currentDevice.child.length; i++)
             buildInitalSetup(currentDevice.child[i], currentDevice.type + "" + id)
     }
     buildInitalSetup(current, 'network');
 }
 networkTree.prototype.plantDevices = function(currentDevice, parent) {
	 
     var image = "/facesix/static/qubercomm/images/networkicons/" + currentDevice.type + "_inactive.png";
     var type = currentDevice.type
     var id = currentDevice[currentDevice.type + "_id"]
     var status=currentDevice.status;
     
     if(currentDevice.parent=="ble"){
    	 var newimg = "/facesix/static/qubercomm/images/networkicons/sensor_inactive.png";
    	 networkVariable.newObject(newimg, "sensor", currentDevice.xposition, currentDevice.yposition,currentDevice.uid,true , id,status, currentDevice.parent)
     } else if(currentDevice.parent=="ap"){
    	 var newimg = "/facesix/static/qubercomm/images/networkicons/ap_inactive.png";
    	 networkVariable.newObject(newimg, "ap", currentDevice.xposition, currentDevice.yposition,currentDevice.uid,true , id,status, currentDevice.parent)
     }else{
    	 networkVariable.newObject(image, type, currentDevice.xposition, currentDevice.yposition,currentDevice.uid,true , id,status, currentDevice.parent) 
     }
     
     
     if (type.indexOf("server") != -1)
         networkVariable.addNode(type, currentDevice.parent,(status=="Added"?"Offline":status),currentDevice.uid, id)
     else
         networkVariable.addNode(type, parent, (status=="Added"?"Offline":status),currentDevice.uid,id)
 }
 networkTree.prototype.findDevice=function(type){
    var current=this.devices;
    var deviceMap={
     	"switch":"SW",
     	"ap":"AP",
     	"sensor":"SNR"
     }
    var findDeviceRecursively=function(currentDevice,type){
    		if(currentDevice.type==type){
    		  var parent=deviceMap[currentDevice.type]+"-"+currentDevice.uid
    		  networkVariable.initialParent=parent;
    		}
    		for(var i=0;i<currentDevice.child.length;i++)
    		     findDeviceRecursively(currentDevice.child[i],type)
    }
    findDeviceRecursively(current,type);
    
 }
/* networkTree.prototype.buildInitialTree = function() {
     var current = this.devices
     var children = networkVariable.childDevices
     this.plantDevices(current)
     for (var i = 0; i < children.length; i++)
         this.buildChildDevices(current, children[i])
 }
 networkTree.prototype.buildChildDevices = function(current, child) {
     if (current.uid == child.parent) {
         child[child.type+"_id"]=current[current.type+"_id"];
         current.child.push(child)
         this.plantDevices(child, current.type)
         return
     }
     for (var i = 0; i < current.child.length; i++)
         this.buildChildDevices(current.child[i], child)
 }*/
 networkTree.prototype.addChildren=function(){
    var current=this.devices;
    this.plantDevices(current);
    var that=this;
    var recursiveDepthAdd=function(current){
        var children = networkVariable.childDevices
        for(var i=0;i<children.length;i++){
            if(current.uid==children[i].parent)
                current.child.push(children[i]);
        }
        for(var i=0;i<current.child.length;i++){
            that.plantDevices(current.child[i],current.uid)
            recursiveDepthAdd(current.child[i])
        }
    }
    recursiveDepthAdd(current)
 }
 //Ends here
 
 

 function getTree() {
     $.ajax({
         method: 'get',
         url: '/facesix/rest/site/portion/networkdevice/list?spid='+networkVariable.urlObj.spid,
         headers: {
             'content-type': 'application/json'
         },
         success: function(response) {
             var tree = response;
             
             for (var i = 0; i < tree.length; i++) {
                 var type = tree[i].typefs;
                 var parent = tree[i].parent;
            	 
                 if(parent != undefined && parent == 'ap'){
                	 type = 'server';
                 }                	 
                 nodeData[type.toLowerCase()] += 1;
                 nodeData['total'] += 1;
                 var id = nodeData[type.toLowerCase()];
                 if (type.toLowerCase().indexOf("server") != -1) {
                	 var device = Device("server", id, tree[i].xposition, tree[i].yposition, tree[i].uid,'',tree[i].parent);
                	 networkVariable.networkTree[type.toLowerCase() + "" + id] = new networkTree(device);
                	
                 } else {
                     var device = Device(	type.toLowerCase(), id, tree[i].xposition, tree[i].yposition, tree[i].uid, '', tree[i].parent);
                     networkVariable.childDevices.push(device);
                 }
             }
             for (var key in networkVariable.networkTree) {
                 networkVariable.networkTree[key].addChildren();
             }
             //if(Object.keys(networkVariable.networkTree).length)
             $("#noServerFound").addClass("hide");
             var deviceText = nodeData["total"] == 1 ? " Device" : " Devices";
             $(".device-section span").text(nodeData["total"] + deviceText);
             reposition = false;
            // $(".panzoom").panzoom("enable");
             disableZoom: false;
         },
         error: function(err) {
             //console.log(err);
         }
     })
 }
 getTree();
   $(document).on("click",".device-name",highlight)
   $(".location").on("click",function(evt){
        evt.preventDefault();
        var uid=$(this).attr("data-uid");
        var type=$(this).attr("data-type");
        var status=$(this).attr("data-status");

        $("image").each(function(){
            var datauri=$(this).attr("data-orig");
            if(datauri)
                $(this).attr("href",datauri);
        })
        if(uid && type && status){
            var url=$("image[dev-uid='"+uid+"']").attr("href");
            $("image[dev-uid='"+uid+"']").attr("data-orig",url);
            $("image[dev-uid='"+uid+"']").attr("href","/facesix/static/qubercomm/images/networkicons/"+type.toLowerCase()+"_"+status.toLowerCase()+"_locate.gif");
        }
   })
function highlight(evt){
        evt.preventDefault();
        var statusMap={
            'offline':'inactive',
            'online':'active',
            'idle':'idle',
        }
        $(".device-name").removeClass("current")
        $(".deviceInfo a").attr("href","#");
        $(this).addClass("current")
        $(".powerBtn").attr("uid",$(this).attr("data-uid"))
        
        var uid=$(this).attr("data-uid");
        var href=$(this).attr("data-href");
        var cref=$(this).attr("data-cref");
        var bref=$(this).attr("data-bref");
        var sref=$(this).attr("data-sref");
        var type=$(this).attr("data-type");
        var status=$(this).attr("data-status")
        $(".dshbrdLink").attr("href",href);
        $(".devcfgLink").attr("href",cref);
        $(".binaryLink").attr("href",bref);
        $(".scanLink").attr("href",sref);
        $("svg a").each(function(index,item){
            /*var datauri=$(this).attr("data-orig");
            var devuid=$(this).attr("dev-uid");
            if(datauri){
                $(this).attr("href",datauri);
            }*/
            $(item).find(".clone").hide();
            $($(item).children()[0]).show();
        })
        if(uid && type && status){
            var $elem=$("image[dev-uid='"+uid+"']")
            var $parent=$elem.parent();
            $parent.find(".clone").remove();
            var $clone=$elem.clone(true);
            $clone.attr("class","clone");
            $parent.append($clone)
            $elem.hide();
            $clone.show();
            $clone.attr("href","/facesix/static/qubercomm/images/networkicons/"+type.toLowerCase()+"_"+statusMap[status.toLowerCase()].toLowerCase()+"_locate.gif")
        }
        $(".powerBtn").attr("devtype",type)
}
   