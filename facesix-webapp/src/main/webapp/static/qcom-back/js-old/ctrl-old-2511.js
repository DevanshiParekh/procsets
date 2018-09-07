app.controller('topCtrl',['$scope', function($scope, $rootScope){

}]);

app.directive('topology', function(){
    function link(scope, element, attrs){
        scope.head ={}
        scope.head.title = "Network Config - Topology"
        var projectWidth = element[0].clientWidth;
        var projectheight = window.innerHeight - 150;
        var canvas = element[0].children[1];
        var contexthtml = element[0].children[2];
        canvas.setAttribute("width", projectWidth);
        canvas.setAttribute("height", projectheight);
        var hostUrl = "http://localhost/project-quber/project-03102016%20-%20New/asset/img/quber-icons/"
        
        scope.allFalse = function(){
            scope.serveropened = false;
            scope.switchopened = false;
            scope.apopened = false;
            scope.deviceopened = false;
            scope.checkServer();
        }
        scope.serveropen = function(){
            if(scope.serveropened){
                scope.serveropened = !scope.serveropened;
            }
            else{
                scope.allFalse();
                scope.serveropened = !scope.serveropened;
            }
        }
        scope.switchopen = function(){
            
            if(scope.switchopened){scope.switchopened = !scope.switchopened;}
            else{scope.allFalse(); scope.switchopened = !scope.switchopened;}
        }
        scope.apopen = function(){
            if(scope.apopened){scope.apopened = !scope.apopened;}
            else{scope.allFalse(); scope.apopened = !scope.apopened;}
        }
        scope.deviceopen = function(){
            if(scope.deviceopened){scope.deviceopened = !scope.deviceopened;}
            else{scope.allFalse(); scope.deviceopened = !scope.deviceopened;}
        }
        
        function Shape(data){
            //console.log(data);
            this.data = data;
        }
        Shape.prototype.draw = function(ctx){
            //console.log(this);
            var shapeData = this.data;
            //console.log(shapeData.devtype);
                var img = new Image();
                img.src = hostUrl + shapeData.imgUrl;
                ctx.drawImage(img, shapeData.xCalc, shapeData.yCalc, 24, 24);
            //console.log(shapeData.bezier);
            if(shapeData.bezier.parentX != null || shapeData.bezier.parentX != undefined){
                //console.log(shapeData.bezier.parentX);
                ctx.strokeStyle = "rgba(0,155,194,0.4)";
                ctx.lineWidth = 2;
                ctx.beginPath();
                ctx.moveTo(shapeData.bezier.parentX + 12, shapeData.bezier.parentY + 30);
                ctx.bezierCurveTo(shapeData.bezier.parentX + 12, shapeData.bezier.parentY + 70, shapeData.xCalc + 12, shapeData.bezier.parentY + 40, shapeData.xCalc + 12, shapeData.yCalc - 5);
                ctx.stroke();
            }
        }
        Shape.prototype.contains = function (mx, my) {
            //console.log(this.data);
            return (this.data.xCalc <= mx) && (this.data.xCalc + 24 >= mx) &&
                (this.data.yCalc <= my) && (this.data.yCalc + 24 >= my);
        }
        function CanvasState(canvas){
            this.offSetX = 15;
            this.offSetY = 122;
            this.canvas = canvas;
            this.ctx = canvas.getContext('2d');
            this.width = canvas.width;
            this.height = canvas.height;
            this.valid = false;
            this.shapes = [];
            var myState = this;
            this.valid = true;
            this.itemContext = false;
            this.vServer = [];
            this.vSwitch = [];
            this.vAp = [];
            this.vDevice = [];
            scope.checkServer = function()
            {
                myState.vServer = [];
                myState.vSwitch = [];
                myState.vAp = [];
                myState.vDevice = [];
                var xServer = 0;
                var xSwitch = 0;
                var xAp = 0;
                var xDevice = 0;
                if(myState.shapes.length == 0){
                    scope.showSwitch = false;
                }
                else {
                    for(var i = 0; i < myState.shapes.length; i++){
                        var cShape = myState.shapes[i];
                        if(cShape.data.devtype == "server"){
                            xServer = xServer + 1;
                            myState.vServer.push(myState.shapes[i].data);
                        } else if(cShape.data.devtype == "switch"){
                            xSwitch = xSwitch + 1;
                            myState.vSwitch.push(myState.shapes[i].data);
                        } else if(cShape.data.devtype == "ap"){
                            xAp = xAp + 1;
                            myState.vAp.push(myState.shapes[i].data);
                        } else if(cShape.data.devtype == "device"){
                            xDevice = xDevice + 1;
                            myState.vDevice.push(myState.shapes[i].data);
                        } 
                    };
                    if(xServer == 1){
                        scope.showSwitch = true;
                        scope.needServer = false;
                    }
                    else{
                        scope.showSwitch = true; 
                        scope.needServer = true;
                        scope.servers = myState.vServer;
                    }
                    if(xSwitch == 1){
                        scope.showAp = true;
                        scope.needSwitch = false;
                    }
                    else{
                        scope.showAp = true; 
                        scope.needSwitch = true;
                        scope.switchs = myState.vSwitch;
                    }
                    if(xAp == 1){
                        scope.showDevice = true;
                        scope.needAp = false;
                    }
                    else{
                        scope.showDevice = true;
                        scope.needAp = true;
                        scope.aps = myState.vAp;
                        console.log(scope.aps);
                    }
                    if(xDevice == 1){}
                    else{}
                }
                
            }
            scope.newServer = function(dataserver){  
                if(dataserver.devname == null || dataserver.devname == undefined || dataserver.devname == ""){
                    scope.errorName = true;
                }
                else if(dataserver.devuid == null || dataserver.devuid == undefined || dataserver.devuid == ""){
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.serveropened = false;
                    console.log(dataserver.devname);
                    //Send Data to Backend Server
                    var shape = {
                        devtype : "server",
                        xposition:"",
                        yposition:"",
                        devname:dataserver.devname,
                        devuid:dataserver.devuid,
                        parent:""
                    }; 
                    myState.addShape(new Shape(shape));
                    //console.log(myState.shapes);
                    scope.dataserver = {};
                    scope.serveropen();
                }
            }
            scope.newSwitch = function(dataswitch){
                console.log(dataswitch);
                if(myState.vServer.length == 1){
                    dataswitch.parent = myState.vServer[0].devuid;
                }
                
                if(dataswitch.parent == null || dataswitch.parent == undefined || dataswitch.parent == ""){
                    scope.errorServer = true;
                }
                else if(dataswitch.devname == null || dataswitch.devname == undefined || dataswitch.devname == ""){
                    scope.errorServer = false;
                    scope.errorName = true;
                }
                else if(dataswitch.devuid == null || dataswitch.devuid == undefined || dataswitch.devuid == ""){
                    scope.errorServer = false;
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                    console.log(dataswitch);
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.switchopened = false;
                    var shape = {
                        devtype : "switch",
                        xposition:"",
                        yposition:"",
                        devname:dataswitch.devname,
                        devuid:dataswitch.devuid,
                        parent:dataswitch.parent
                    }; 
                    myState.addShape(new Shape(shape));
                    scope.dataswitch = {};
                    scope.switchopened = true;
                    scope.switchopen();
                }
            }
            scope.newAp = function(dataap){
                
                if(myState.vSwitch.length == 1){
                    dataap.parent = myState.vSwitch[0].devuid;
                }
                
                if(dataap.parent == null || dataap.parent == undefined || dataap.parent == ""){
                    scope.errorServer = true;
                }
                else if(dataap.devname == null || dataap.devname == undefined || dataap.devname == ""){
                    scope.errorServer = false;
                    scope.errorName = true;
                }
                else if(dataap.devuid == null || dataap.devuid == undefined || dataap.devuid == ""){
                    scope.errorServer = false;
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.switchopened = false;
                    var shape = {
                        devtype : "ap",
                        xposition:"",
                        yposition:"",
                        devname:dataap.devname,
                        devuid:dataap.devuid,
                        parent:dataap.parent
                    }; 
                    myState.addShape(new Shape(shape));
                    scope.dataap = {};
                    scope.apopen();
                }
            }
            scope.newDevice = function(datadevice){
                
                if(myState.vAp.length == 1){
                    datadevice.parent = myState.vAp[0].devuid;
                }
                
                if(datadevice.parent == null || datadevice.parent == undefined || datadevice.parent == ""){
                    scope.errorServer = true;
                }
                else if(datadevice.devname == null || datadevice.devname == undefined || datadevice.devname == ""){
                    scope.errorServer = false;
                    scope.errorName = true;
                }
                else if(datadevice.devuid == null || datadevice.devuid == undefined || datadevice.devuid == ""){
                    scope.errorServer = false;
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.switchopened = false;
                    var shape = {
                        devtype : "device",
                        xposition:"",
                        yposition:"",
                        devname:datadevice.devname,
                        devuid:datadevice.devuid,
                        parent:datadevice.parent
                    };
                    myState.addShape(new Shape(shape));
                    scope.datadevice = {};
                    scope.deviceopen();
                }
            }
            scope.macDevice = function(datadevice){
                scope.datadevice = datadevice;
                var v = scope.datadevice.devuid;
				var l = v.length;
				var maxLen = 17 // Length of mac string including ':'
				if(l >= 2 && l < maxLen) { 
					var v1;
					v1 = v;					
					/* Removing all ':' to calculate get actaul text */
					while(!(v1.indexOf(":") < 0)) { // Better use RegEx
						v1 = v1.replace(":", "")
					}					
					/* Insert ':' after ever 2 chars */
					if(v1.length%2 == 0) {
						scope.datadevice.devuid = v + ":";
					}
				}
            }
            scope.macAp = function(dataap){
                scope.dataap = dataap;
                var v = scope.dataap.devuid;
				var l = v.length;
				var maxLen = 17 // Length of mac string including ':'
				if(l >= 2 && l < maxLen) { 
					var v1;
					v1 = v;					
					/* Removing all ':' to calculate get actaul text */
					while(!(v1.indexOf(":") < 0)) { // Better use RegEx
						v1 = v1.replace(":", "")
					}					
					/* Insert ':' after ever 2 chars */
					if(v1.length%2 == 0) {
						scope.dataap.devuid = v + ":";
					}
				}
            }
            scope.macSwitch = function(dataswitch){
                scope.dataswitch = dataswitch;
                var v = scope.dataswitch.devuid;
				var l = v.length;
				var maxLen = 17 // Length of mac string including ':'
				if(l >= 2 && l < maxLen) { 
					var v1;
					v1 = v;					
					/* Removing all ':' to calculate get actaul text */
					while(!(v1.indexOf(":") < 0)) { // Better use RegEx
						v1 = v1.replace(":", "")
					}					
					/* Insert ':' after ever 2 chars */
					if(v1.length%2 == 0) {
						scope.dataswitch.devuid = v + ":";
					}
				}
            }
            scope.macServer = function(dataserver){
                scope.dataserver = dataserver;
                var v = scope.dataserver.devuid;
				var l = v.length;
				var maxLen = 17 // Length of mac string including ':'
				if(l >= 2 && l < maxLen) { 
					var v1;
					v1 = v;					
					/* Removing all ':' to calculate get actaul text */
					while(!(v1.indexOf(":") < 0)) { // Better use RegEx
						v1 = v1.replace(":", "")
					}					
					/* Insert ':' after ever 2 chars */
					if(v1.length%2 == 0) {
						scope.dataserver.devuid = v + ":";
					}
				}
            }
            
            canvas.addEventListener('mousedown', function(e){
                e.preventDefault();
                contexthtml.style.display="none";
                contexthtml.style.top = "0";
                contexthtml.style.left ="0";
                contexthtml.innerHTML ="";
                
            });
            
            canvas.addEventListener('mousemove', function(e){
                
            });
            
            
            canvas.addEventListener('contextmenu', function(e){
                e.preventDefault();
                var mouse = myState.getMouse(e);
                var mx = mouse.x;
                var my = mouse.y;
                var shapes = myState.shapes;
                var lenShapes = shapes.length;
                for (var i = lenShapes-1; i >= 0; i--) {
                  if (shapes[i].contains(mx, my)) {
                        var mySel = shapes[i];
                      scope.mySel = shapes[i];
                      //console.log(scope.mySel);
                    var link = "http://google.com";
//                    contexthtml.innerHTML = "<li><a href='"+ link +"'><i class='fa fa-trash'></i>Trash</a></li>"
                    contexthtml.innerHTML = "<li><a href='"+ scope.mySel.data.sid +"'><i class='fa fa-eye'></i>View</a></li>" + 
                            "<li><a href='"+ scope.mySel.data.sid +"'><i class='fa fa-edit'></i>Edit</a></li>"+
                            "<li><a href='"+ scope.mySel.data.sid +"'><i class='fa fa-undo'></i>Restart</a></li>"+
                            "<li><a href='"+ scope.mySel.data.sid +"'><i class='fa fa-power-off'></i>Power-Off</a></li>"+
                            "<li><a href='"+ scope.mySel.data.sid +"'><i class='fa fa-trash'></i>Delete</a></li>"
                    //contexthtml.style.top = mx "px";
                      contexthtml.style.display="block";
                      contexthtml.style.top = my + "px";
                      contexthtml.style.left =mx +30+ "px";
                      this.itemContext = true;
                    return;
                  }
                }
                
                
            });
            
            
            this.interval = 30;
            setInterval(function () {
                 myState.draw();
            }, myState.interval);
        }
        CanvasState.prototype.addShape = function(shape){
            this.shapes.push(shape);
            this.valid = false;
        }
        CanvasState.prototype.clear = function(){
            this.ctx.clearRect(0, 0, this.width, this.height);
        }
        
        CanvasState.prototype.draw = function(){
            if(!this.valid){
                var ctx = this.ctx; 
                this.clear();
                var shapes = this.shapes;
                var len = shapes.length;
                var serverArray = [];
                var switchArray = [];
                var apArray = [];
                var devArray = [];
                for(var i = 0; i < len; i++){
                    if(shapes[i].data.devtype == "server"){
                        serverArray.push(i);
                    }
                    if(shapes[i].data.devtype == "switch"){
                        
                    }
                }
                var intialX = 0;
                for(var i = 0; i < len; i++){
                    if(shapes[i].data.devtype == "server"){
                        var spiltWidth = this.width / serverArray.length;
                        //console.log(spiltWidth);
                        var xCalc = (intialX + (spiltWidth / 2)) - 12;
                        var yCalc = 20;
                        shapes[i].data.imgUrl = "server_inactive.png";
                        shapes[i].data.xCalc = xCalc;
                        shapes[i].data.yCalc = yCalc;
                        shapes[i].data.bezier = {};
                        
                        shapes[i].draw(ctx);
                        
                        var parentID = shapes[i].data.devuid;
                        for(var j = 0; j < len; j++){
                            if(shapes[j].data.parent == parentID){
                                //console.log(shapes[j].data.parent);
                                switchArray.push(shapes[j]);
                            }
                        }
                        var switchIntialX = intialX;
                        for(var k = 0; k < switchArray.length; k++){
                            var switchSplitWidth = spiltWidth / switchArray.length;
                            var xCalc = (switchIntialX + (switchSplitWidth / 2)) - 12;
                            var yCalc = 150;
                            switchArray[k].data.imgUrl = "switch_inactive.png";
                            switchArray[k].data.xCalc = xCalc;
                            switchArray[k].data.yCalc = yCalc;
                            //console.log(shapes[i].data.xCalc);
                            switchArray[k].data.bezier = {"parentX": shapes[i].data.xCalc ,  "parentY" : shapes[i].data.yCalc};
                            //console.log(switchArray[k].data.bezier);
                            switchArray[k].draw(ctx);
                            
                            
                            var parentID1 = switchArray[k].data.devuid;
                            //console.log(parentID1);
                            for(var ap = 0; ap < len; ap++){
                                if(shapes[ap].data.parent == parentID1){
                                    apArray.push(shapes[ap]);
                                }
                            }
                            var apIntialX = switchIntialX;
                            //console.log(apArray.length);
                            for(var ap1 = 0; ap1 < apArray.length; ap1++){
                                var apSplitWidth = switchSplitWidth / apArray.length;
                                var xCalc = (apIntialX + (apSplitWidth / 2)) - 12;
                                var yCalc = 270;
                                apArray[ap1].data.imgUrl = "ap_inactive.png";
                                apArray[ap1].data.xCalc = xCalc;
                                apArray[ap1].data.yCalc = yCalc;
                                apArray[ap1].data.bezier = {"parentX": switchArray[k].data.xCalc ,  "parentY" : switchArray[k].data.yCalc};
                                apArray[ap1].draw(ctx);
                                
                                var parentID2 = apArray[ap1].data.devuid;
                                
                                for(var dev = 0; dev < len; dev++){
                                    if(shapes[dev].data.parent == parentID2){
                                        devArray.push(shapes[dev]);
                                    }
                                }
                                var devIntialX = apIntialX;
                                //console.log(devIntialX);
                                for(var dev1 = 0; dev1 < devArray.length; dev1++){
                                    var devSplitWidth = apSplitWidth / devArray.length;
                                    var xCalc = (devIntialX + (devSplitWidth / 2)) - 12;
                                    var yCalc = 390;
                                    devArray[dev1].data.imgUrl = "sensor_inactive.png";
                                    devArray[dev1].data.xCalc = xCalc;
                                    devArray[dev1].data.yCalc = yCalc;
                                    devArray[dev1].data.bezier = {"parentX": apArray[ap1].data.xCalc ,  "parentY" : apArray[ap1].data.yCalc};
                                    
                                    devArray[dev1].draw(ctx);
                                    devIntialX = devIntialX + devSplitWidth;
                                }
                                devArray = [];
                                apIntialX = apIntialX + apSplitWidth;
                            }
                            apArray = [];
                            switchIntialX = switchIntialX + switchSplitWidth;
                            //switchArray[k].data.bezier = [];
                        }
                        switchArray = [];
                        intialX = intialX + spiltWidth;
                    } 
                }
                
                
                
                
                
                //this.valid = true;
            }
        }
        CanvasState.prototype.getMouse = function(e){
            mx = e.pageX - this.offSetX;
            my = e.pageY - this.offSetY;
            return{
                x: mx,
                y: my
            };
        }
        
        function init(){
            var s = new CanvasState(canvas);
            var shape = scope.data;
            var shapeLength = shape.length;
            for (var i = 0; i < shapeLength; i++){
                s.addShape(new Shape(shape[i]));
            }
        }
        init();
    }
    return{
        restrict: 'E',
        replace: true,
        scope: {
            data: '=',
            items: '=',
            click:'@'
        },
        link: link,
        templateUrl: 'Views/top-view.html'
    };
    
});

app.directive('configfloor', function($http, $routeParams, $rootScope){
    function link(scope, element, attrs){
        scope.head ={}
        $rootScope.Networkdatas = [];
        scope.head.title = "Network Config - Floor Plan"
        //console.log(attrs);
        var projectWidth = element[0].clientWidth;
        var projectheight = window.innerHeight - 150;
        var canvas = element[0].children[1];
        var canvas2 = element[0].children[2];
        var contexthtml = element[0].children[3];
        //console.log(contexthtml);
        canvas.setAttribute("width", projectWidth);
        canvas.setAttribute("height", projectheight);
        canvas2.setAttribute("width", projectWidth);
        canvas2.setAttribute("height", projectheight);
        var hostUrl = "/facesix/static/qcom/img/quber-icons/"
        
       
        
        scope.allFalse = function(){
            scope.serveropened = false;
            scope.switchopened = false;
            scope.apopened = false;
            scope.deviceopened = false;
            scope.checkServer();
        }
        scope.serveropen = function(){
            if(scope.serveropened){
                scope.serveropened = !scope.serveropened;
            }
            else{
                scope.allFalse();
                scope.serveropened = !scope.serveropened;
            }
        }
        scope.switchopen = function(){
            
            if(scope.switchopened){scope.switchopened = !scope.switchopened;}
            else{scope.allFalse(); scope.switchopened = !scope.switchopened;}
        }
        scope.apopen = function(){
            if(scope.apopened){scope.apopened = !scope.apopened;}
            else{scope.allFalse(); scope.apopened = !scope.apopened;}
        }
        scope.deviceopen = function(){
            if(scope.deviceopened){scope.deviceopened = !scope.deviceopened;}
            else{scope.allFalse(); scope.deviceopened = !scope.deviceopened;}
        }
        
        function Shape(data){
            //console.log(data);
            this.data = data;
        }
        Shape.prototype.draw = function(ctx){
                var shapeData = this.data;
                shapeData.xposition = parseInt(shapeData.xposition);
                shapeData.yposition = parseInt(shapeData.yposition);
                var img = new Image();
                img.src = hostUrl + shapeData.imgUrl;
                ctx.drawImage(img, shapeData.xposition, shapeData.yposition, 36, 36);
        }
        Shape.prototype.contains = function (mx, my) {
            //console.log(this);
            return (parseInt(this.data.xposition) <= mx) && (parseInt(this.data.xposition) + 36 >= mx) &&
                (parseInt(this.data.yposition) <= my) && (parseInt(this.data.yposition) + 36 >= my);
        }
        function CanvasState(canvas){
            this.offSetX = 15;
            this.offSetY = 122;
            this.canvas = canvas;
            this.ctx = canvas.getContext('2d');
            this.width = canvas.width;
            this.height = canvas.height;
            this.valid = false;
            this.shapes = [];
            var myState = this;
            this.valid = true;
            this.itemContext = false;
            this.vServer = [];
            this.vSwitch = [];
            this.vAp = [];
            this.vDevice = [];
            this.NetworkData = [];
            this.mySel = {};
            this.mySelct = {};
            this.shapeSelected = false;
            scope.checkServer = function()
            {
                myState.vServer = [];
                myState.vSwitch = [];
                myState.vAp = [];
                myState.vDevice = [];
                var xServer = 0;
                var xSwitch = 0;
                var xAp = 0;
                var xDevice = 0;
                if(myState.shapes.length == 0){
                    scope.showSwitch = false;
                }
                else {
                    for(var i = 0; i < myState.shapes.length; i++){
                        var cShape = myState.shapes[i];
                        if(cShape.data.typefs == "server"){
                            xServer = xServer + 1;
                            myState.vServer.push(myState.shapes[i].data);
                        } else if(cShape.data.typefs == "switch"){
                            xSwitch = xSwitch + 1;
                            myState.vSwitch.push(myState.shapes[i].data);
                        } else if(cShape.data.typefs == "ap"){
                            xAp = xAp + 1;
                            myState.vAp.push(myState.shapes[i].data);
                        } else if(cShape.data.typefs == "device"){
                            xDevice = xDevice + 1;
                            myState.vDevice.push(myState.shapes[i].data);
                        } 
                    };
                    if(xServer == 1){
                        scope.showSwitch = true;
                        scope.needServer = false;
                    }
                    else{
                        scope.showSwitch = true; 
                        scope.needServer = true;
                        scope.servers = myState.vServer;
                        //console.log(scope.servers);
                    }
                    if(xSwitch == 1){
                        scope.showAp = true;
                        scope.needSwitch = false;
                    }
                    else{
                        scope.showAp = true; 
                        scope.needSwitch = true;
                        scope.switchs = myState.vSwitch;
                    }
                    if(xAp == 1){
                        scope.showDevice = true;
                        scope.needAp = false;
                    }
                    else{
                        scope.showDevice = true;
                        scope.needAp = true;
                        scope.aps = myState.vAp;
                        //console.log(scope.aps);
                    }
                    if(xDevice == 1){}
                    else{}
                }
                
            }
            scope.newServer = function(dataserver){  
                if(dataserver.devname == null || dataserver.devname == undefined || dataserver.devname == ""){
                    scope.errorName = true;
                }
                else if(dataserver.devuid == null || dataserver.devuid == undefined || dataserver.devuid == ""){
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.serveropened = false;
                    
                    //Send Data to Backend Server
                    var shape = {
                        typefs : "server",
                        type : "server",
                        xposition:"",
                        yposition:"",
                        status:"inactive",
                        sid:$routeParams.sid,
                        spid:$routeParams.spid,
                        devname:dataserver.devname,
                        uid:dataserver.devuid,
                        parent:"",
                        new:"true",
                        band:"",
                        gparent:""
                        
                        
                    }; 
                    scope.dataserver = {};
                    scope.serveropened = true;
                    scope.serveropen();
                    myState.addShape(new Shape(shape));
                    var lasti = myState.shapes.length - 1;
                    myState.mySel = myState.shapes[lasti];
                    myState.shapeSelected = true;
                    //console.log(myState.shapes);
                    
                }
            }
            scope.newSwitch = function(dataswitch){
                console.log(dataswitch);
                if(myState.vServer.length == 1){
                    dataswitch.parent = myState.vServer[0].uid;
                }
                
                if(dataswitch.parent == null || dataswitch.parent == undefined || dataswitch.parent == ""){
                    scope.errorServer = true;
                }
                else if(dataswitch.devname == null || dataswitch.devname == undefined || dataswitch.devname == ""){
                    scope.errorServer = false;
                    scope.errorName = true;
                }
                else if(dataswitch.devuid == null || dataswitch.devuid == undefined || dataswitch.devuid == ""){
                    scope.errorServer = false;
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                    console.log(dataswitch);
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.switchopened = false;
                    var shape = {
                        typefs : "switch",
                        type : "switch",
                        xposition:"",
                        yposition:"",
                        status:"inactive",
                        sid:$routeParams.sid,
                        spid:$routeParams.spid,
                        devname:dataswitch.devname,
                        uid:dataswitch.devuid,
                        parent:dataswitch.parent,
                        new:"true",
                        band:"",
                        gparent:""
                    }; 
                    scope.dataswitch = {};
                    scope.switchopened = true;
                    scope.switchopen();
                    myState.addShape(new Shape(shape));
                    var lasti = myState.shapes.length - 1;
                    myState.mySel = myState.shapes[lasti];
                    myState.shapeSelected = true;
                }
            }
            scope.newAp = function(dataap){
                console.log(myState.vSwitch);
                if(myState.vSwitch.length == 1){
                    dataap.parent = myState.vSwitch[0].uid;
                }
                //console.log(dataap);
                if(dataap.parent == null || dataap.parent == undefined || dataap.parent == ""){
                    scope.errorServer = true;
                }
                else if(dataap.devname == null || dataap.devname == undefined || dataap.devname == ""){
                    scope.errorServer = false;
                    scope.errorName = true;
                }
                else if(dataap.devuid == null || dataap.devuid == undefined || dataap.devuid == ""){
                    scope.errorServer = false;
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                console.log(dataap);
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.switchopened = false;
                    var shape = {
                        typefs : "ap",
                        type : "ap",
                        xposition:"",
                        yposition:"",
                        status:"inactive",
                        sid:$routeParams.sid,
                        spid:$routeParams.spid,
                        devname:dataap.devname,
                        uid:dataap.devuid,
                        parent:dataap.parent,
                        new:"true",
                        band:"",
                        gparent:""
                    }; 
                    scope.dataap = {};
                    scope.apopened = true;
                    scope.apopen();
                    myState.addShape(new Shape(shape));
                    var lasti = myState.shapes.length - 1;
                    myState.mySel = myState.shapes[lasti];
                    myState.shapeSelected = true;
                }
            }
            scope.newDevice = function(datadevice){
                if(myState.vAp.length == 1){
                    datadevice.parent = myState.vAp[0].uid;
                }
                
                if(datadevice.parent == null || datadevice.parent == undefined || datadevice.parent == ""){
                    scope.errorServer = true;
                }
                else if(datadevice.devname == null || datadevice.devname == undefined || datadevice.devname == ""){
                    scope.errorServer = false;
                    scope.errorName = true;
                }
                else if(datadevice.devuid == null || datadevice.devuid == undefined || datadevice.devuid == ""){
                    scope.errorServer = false;
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.switchopened = false;
                    var shape = {
                        typefs : "sensor",
                        type : "sensor",
                        xposition:"",
                        yposition:"",
                        status:"inactive",
                        sid:$routeParams.sid,
                        spid:$routeParams.spid,
                        devname:datadevice.devname,
                        uid:datadevice.devuid,
                        parent:datadevice.parent,
                        new:"true",
                        band:"",
                        gparent:""
                    };
                    scope.datadevice = {};
                    scope.deviceopened = true;
                    scope.deviceopen();
                    myState.addShape(new Shape(shape));
                    var lasti = myState.shapes.length - 1;
                    myState.mySel = myState.shapes[lasti];
                    myState.shapeSelected = true;
                }
            }
            scope.macDevice = function(datadevice){
                scope.datadevice = datadevice;
                var v = scope.datadevice.devuid;
				var l = v.length;
				var maxLen = 17 // Length of mac string including ':'
				if(l >= 2 && l < maxLen) { 
					var v1;
					v1 = v;					
					/* Removing all ':' to calculate get actaul text */
					while(!(v1.indexOf(":") < 0)) { // Better use RegEx
						v1 = v1.replace(":", "")
					}					
					/* Insert ':' after ever 2 chars */
					if(v1.length%2 == 0) {
						scope.datadevice.devuid = v + ":";
					}
				}
            }
            scope.macAp = function(dataap){
                scope.dataap = dataap;
                var v = scope.dataap.devuid;
				var l = v.length;
				var maxLen = 17 // Length of mac string including ':'
				if(l >= 2 && l < maxLen) { 
					var v1;
					v1 = v;					
					/* Removing all ':' to calculate get actaul text */
					while(!(v1.indexOf(":") < 0)) { // Better use RegEx
						v1 = v1.replace(":", "")
					}					
					/* Insert ':' after ever 2 chars */
					if(v1.length%2 == 0) {
						scope.dataap.devuid = v + ":";
					}
				}
            }
            scope.macSwitch = function(dataswitch){
                scope.dataswitch = dataswitch;
                var v = scope.dataswitch.devuid;
				var l = v.length;
				var maxLen = 17 // Length of mac string including ':'
				if(l >= 2 && l < maxLen) { 
					var v1;
					v1 = v;					
					/* Removing all ':' to calculate get actaul text */
					while(!(v1.indexOf(":") < 0)) { // Better use RegEx
						v1 = v1.replace(":", "")
					}					
					/* Insert ':' after ever 2 chars */
					if(v1.length%2 == 0) {
						scope.dataswitch.devuid = v + ":";
					}
				}
            }
            scope.macServer = function(dataserver){
                scope.dataserver = dataserver;
                var v = scope.dataserver.devuid;
				var l = v.length;
				var maxLen = 17 // Length of mac string including ':'
				if(l >= 2 && l < maxLen) { 
					var v1;
					v1 = v;					
					/* Removing all ':' to calculate get actaul text */
					while(!(v1.indexOf(":") < 0)) { // Better use RegEx
						v1 = v1.replace(":", "")
					}					
					/* Insert ':' after ever 2 chars */
					if(v1.length%2 == 0) {
						scope.dataserver.devuid = v + ":";
					}
				}
            }
            
            canvas.addEventListener('mousedown', function(e){
                e.preventDefault();
                if(myState.shapeSelected){
                    myState.shapeSelected = false;
                    console.log(myState.mySel);
                    var data = myState.mySel.data;
                    data.xposition = String(data.xposition);
                    data.yposition = String(data.yposition);
                    if(data.new == "true"){
                            $http({
                                method:'post',
                                url:'/facesix/rest/site/portion/networkdevice/save',
                                data:JSON.stringify(data),
                            }).then(function successCallback(response){
                                console.log(response);
                                myState.mySel.data.new = false;
                            }, function errorCallback(response){
                                console.log(response);
                            });
                        
                        console.log(myState.shapes);
                    }
                    //console.log(JSON.stringify(data));
                    else{
                        $http({
                            method:'post',
                            url:'/facesix/rest/site/portion/networkdevice/update',
                            data:JSON.stringify(data),
                        }).then(function successCallback(response){
                            console.log(response);
                        }, function errorCallback(response){
                            console.log(response);
                        });
                    }
                }
                else{
                contexthtml.style.display="none";
                contexthtml.style.top = "0";
                contexthtml.style.left ="0";
                contexthtml.innerHTML = "";
                var mouse = myState.getMouse(e);
                var mx = mouse.x;
                var my = mouse.y;
                    //console.log(mx, my);
                var shapes = myState.shapes;
                var lenShapes = shapes.length;
                for (var i = lenShapes-1; i >= 0; i--) {
                    //console.log(shapes[i].contains(mx, my));
                  if (shapes[i].contains(mx, my)) {
                          myState.mySel = shapes[i];
                            myState.shapeSelected = true;
                  }
                };
                }
            });
            
            canvas.addEventListener('mousemove', function(e){
                if(myState.shapeSelected){
                    var mouse = myState.getMouse(e);
                    var mx = mouse.x;
                    var my = mouse.y;
                    myState.mySel.data.xposition = mx;
                    myState.mySel.data.yposition = my;
                }
            });
            
            
            canvas.addEventListener('contextmenu', function(e){
                e.preventDefault();
                myState.shapeSelected = false;
                var mouse = myState.getMouse(e);
                var mx = mouse.x;
                var my = mouse.y;
                var shapes = myState.shapes;
                var lenShapes = shapes.length;
                for (var i = lenShapes-1; i >= 0; i--) {
                  if (shapes[i].contains(mx, my)) {
                        myState.mySel = shapes[i];
                      scope.mySelcontext = shapes[i];
                      //angular.element(temp.childNodes[11]).html($compile('<button ng-click="save($event)">Save</button>')($scope));
                    contexthtml.innerHTML = "<li><a href='"+ scope.mySelcontext.data.sid +"'><i class='fa fa-eye'></i>View</a></li>" +
                            "<li><a href='"+ scope.mySelcontext.data.sid +"'><i class='fa fa-edit'></i>Edit</a></li>"+
                            "<li><a href='"+ scope.mySelcontext.data.sid +"'><i class='fa fa-undo'></i>Restart</a></li>"+
                            "<li><a href='"+ scope.mySelcontext.data.sid +"'><i class='fa fa-power-off'></i>Power-Off</a></li>"+
                            "<li><a href='"+ scope.mySelcontext.data.sid +"'><i class='fa fa-trash'></i>Delete</a></li>";
                      
                      
                      
//                    contexthtml.style.top = mx "px";
                      
                      contexthtml.style.display="block";
                      contexthtml.style.top = my + "px";
                      contexthtml.style.left =mx +30+ "px";
                      this.itemContext = true;
                    return;
                  }
                }
                
                
            });
            scope.shapeMove = function(){
                console.log('ok');
                alert('ok');
                //myState.shapeSelected = true;
            }
            
            this.interval = 30;
            setInterval(function () {
                 myState.draw();
                //myState.valid = true;
            }, myState.interval);
        }
        CanvasState.prototype.addShape = function(shape){
            this.shapes.push(shape);
            this.valid = false;
        }
        CanvasState.prototype.clear = function(){
            this.ctx.clearRect(0, 0, this.width, this.height);
        }
        
        CanvasState.prototype.draw = function(){
            if(!this.valid){
                var ctx = this.ctx; 
                this.clear();
//                
                var shapes = this.shapes;
                var len = shapes.length;
                var serverArray = [];
                var switchArray = [];
                var apArray = [];
                var devArray = [];
                this.NetworkData = [];
                for(var i = 0; i < len; i++){
                    if(shapes[i].data.typefs == "server"){
                        serverArray.push(i);
                    }
                    if(shapes[i].data.typefs == "switch"){
                        
                    }
                }
                var intialX = 0;
                for(var i = 0; i < len; i++){
                    this.NetworkData.push(shapes[i].data);
                    
                    if(shapes[i].data.typefs == "server"){
                        var spiltWidth = this.width / serverArray.length;
                        //console.log(spiltWidth);
                        var xCalc = (intialX + (spiltWidth / 2)) - 12;
                        var yCalc = 20;
                        shapes[i].data.parent = "";
                        shapes[i].data.imgUrl = "server_inactive.png";
                       // shapes[i].data.xCalc = xCalc;
                       // shapes[i].data.yCalc = yCalc;
                        //shapes[i].data.bezier = {};
                        
                        shapes[i].draw(ctx);
                        //$rootScope.Shapedata;
                        var parentID = shapes[i].data.uid;
                        for(var j = 0; j < len; j++){
                            if(shapes[j].data.parent == parentID){
                                //console.log(shapes[j].data.parent);
                                
                                switchArray.push(shapes[j]);
                                //$rootScope.Shapedata.shapes[i].push(shapes[j]);
                                
                            }
                        }
                        
                        var switchIntialX = intialX;
                        for(var k = 0; k < switchArray.length; k++){
                            var switchSplitWidth = spiltWidth / switchArray.length;
                            var xCalc = (switchIntialX + (switchSplitWidth / 2)) - 12;
                            var yCalc = 150;
                            switchArray[k].data.imgUrl = "switch_inactive.png";
                           // switchArray[k].data.xCalc = xCalc;
                           // switchArray[k].data.yCalc = yCalc;
                            //console.log(shapes[i].data.xCalc);
                           // switchArray[k].data.bezier = {"parentX": shapes[i].data.xCalc ,  "parentY" : shapes[i].data.yCalc};
                            //console.log(switchArray[k].data.bezier);
                            switchArray[k].draw(ctx);
                            
                            
                            var parentID1 = switchArray[k].data.uid;
                            //console.log(parentID1);
                            for(var ap = 0; ap < len; ap++){
                                if(shapes[ap].data.parent == parentID1){
                                    apArray.push(shapes[ap]);
                                }
                            }
                            var apIntialX = switchIntialX;
                            //console.log(apArray.length);
                            for(var ap1 = 0; ap1 < apArray.length; ap1++){
                                var apSplitWidth = switchSplitWidth / apArray.length;
                                var xCalc = (apIntialX + (apSplitWidth / 2)) - 12;
                                var yCalc = 270;
                                apArray[ap1].data.imgUrl = "ap_inactive.png";
                                //apArray[ap1].data.xCalc = xCalc;
                                //apArray[ap1].data.yCalc = yCalc;
                                //apArray[ap1].data.bezier = {"parentX": switchArray[k].data.xCalc ,  "parentY" : switchArray[k].data.yCalc};
                                apArray[ap1].draw(ctx);
                                
                                var parentID2 = apArray[ap1].data.uid;
                                
                                for(var dev = 0; dev < len; dev++){
                                    if(shapes[dev].data.parent == parentID2){
                                        devArray.push(shapes[dev]);
                                    }
                                }
                                var devIntialX = apIntialX;
                                //console.log(devIntialX);
                                for(var dev1 = 0; dev1 < devArray.length; dev1++){
                                    var devSplitWidth = apSplitWidth / devArray.length;
                                    var xCalc = (devIntialX + (devSplitWidth / 2)) - 12;
                                    var yCalc = 390;
                                    devArray[dev1].data.imgUrl = "sensor_inactive.png";
                                    //devArray[dev1].data.xCalc = xCalc;
                                    //devArray[dev1].data.yCalc = yCalc;
                                    //devArray[dev1].data.bezier = {"parentX": apArray[ap1].data.xCalc ,  "parentY" : apArray[ap1].data.yCalc};
                                    
                                    devArray[dev1].draw(ctx);
                                    devIntialX = devIntialX + devSplitWidth;
                                }
                                
                                devArray = [];
                                apIntialX = apIntialX + apSplitWidth;
                            }
                            apArray = [];
                            switchIntialX = switchIntialX + switchSplitWidth;
                            //switchArray[k].data.bezier = [];
                        }
                        switchArray = [];
                        intialX = intialX + spiltWidth;
                    }
                    
                    
                }
                //$rootScope.Shapedata = shapes;
                //console.log(this.NetworkData);
                
                function buildHierarchy(arry) {
                    var roots = [], children = {};
                    // find the top level nodes and hash the children based on parent
                    for (var i = 0, len = arry.length; i < len; ++i) {
                        var item = arry[i];
                        
                            var p = item.parent,
                            
                            target = !p ? roots : (children[p] || (children[p] = []));
							
                        target.push({ value: item });
                        //console.log(item);
                        //console.log(target);
                    }

                    // function to recursively build the tree
                    var findChildren = function(parent) {
                        //console.log(children[parent.value.uid]);
                        if (children[parent.value.uid]) {
                            parent.children = children[parent.value.uid];
                            //console.log(parent);
                            for (var i = 0, len = parent.children.length; i < len; ++i) {
                                findChildren(parent.children[i]);
                            }
                        }
                    };
						
                    // enumerate through to handle the case where there are multiple roots
                    for (var i = 0, len = roots.length; i < len; ++i) {
                        findChildren(roots[i]);
                    }
					//console.log(roots);
                    return roots;
                }
                
                var items = this.NetworkData;
                //console.log(this.NetworkData);
               // $rootScope.Networkdatas = [];
                $rootScope.Networkdatas = buildHierarchy(items);
                
                 
                
            }
            //this.valid = true;
        }
        CanvasState.prototype.getMouse = function(e){
            mx = e.pageX - this.offSetX;
            my = e.pageY - this.offSetY;
            return{
                x: mx,
                y: my
            };
        }
        
        function init(){
            var drawBaseTrue = true;
            function drawBase(){
                var ctx2 = canvas2.getContext('2d');
                var baseImg = new Image();
                baseImg.src = "/facesix/web/site/portion/planfile?spid="+$routeParams.spid;
                ctx2.fillStyle = "#ffffff";
                ctx2.fillRect(0,0,canvas2.width,canvas2.height);
                    //console.log(img);
                var wrh = baseImg.width / baseImg.height;
                var hrw = baseImg.height/ baseImg.width;
                var newWidth = canvas2.height / hrw;
                var wcenter = (canvas2.width / 2) - (newWidth /2);
                ctx2.drawImage(baseImg,wcenter,0, newWidth , canvas2.height);
                //console.log(ctx2);
            }
            drawBase();
            
            setInterval(function () {
               if(drawBaseTrue){
                    drawBase();
                }
                //drawBaseTrue = false;
            }, 30);
            
            
             
            
            var s = new CanvasState(canvas);
            $http({
                method:'Get',
                url:'/facesix/rest/site/portion/networkdevice/list?spid='+$routeParams.spid,
                headers: {'content-type': 'application/json'}
            }).then(function successCallback(response){
            //console.log(response.data);
                var shape = response.data;
                var shapeLength = shape.length;
                if(shapeLength == 0){}else{
                for (var i = 0; i < shapeLength; i++){
                    s.addShape(new Shape(shape[i]));
                }}
            }, function errorCallback(response){
                
            });
        }
        init();
        //$rootScope.$apply();
    }
    return{
        restrict: 'E',
        replace: true,
        scope: {
            data: '=',
            items: '=',
            click:'@',
            Networkdatas:'='
        },
        link: link,
        templateUrl: '/facesix/web/site/portion/topview'
    };
    
});

