app.controller('topCtrl',['$scope', function($scope){
    $scope.devices = [
        {
            "createdBy": "qubercomm",
            "modifiedBy": "qubercomm",
            "createdOn": 1472125459551,
            "modifiedOn": 1472125459551,
            "id": "AVbBikDkn32QqPNAhbrk",
            "parent": "",
            "sid": "579597914b28bdc67fefbb64",
            "spid": "57bea096c1b08db18f2517e3",
            "xposition": "283",
            "yposition": "205",
            "dev_uid": "13:3a:22:ea:1a:1f",
            "dev_status": "Added",
            "dev_type": "ap",
            "devuid": "13:3a:22:ea:1a:1f",
            "devstatus": "active",
            "devtype": "server",
            "settingsAsString": "",
            "child":[],
            "devname":"server1"
        },{
            "createdBy": "qubercomm",
            "modifiedBy": "qubercomm",
            "createdOn": 1472125459551,
            "modifiedOn": 1472125459551,
            "id": "AVbBikDkn32QqPNAhbrk",
            "parent": "13:3a:22:ea:1a:1f",
            "sid": "579597914b28bdc67fefbb64",
            "spid": "57bea096c1b08db18f2517e3",
            "xposition": "283",
            "yposition": "205",
            "dev_uid": "13:3a:22:ea:1a:1f",
            "dev_status": "Added",
            "dev_type": "ap",
            "devuid": "13:3a:22:ea:1a:1f",
            "devstatus": "active",
            "devtype": "switch",
            "settingsAsString": "",
            "child":[],
            "devname":"switch1"
        },{
            "createdBy": "qubercomm",
            "modifiedBy": "qubercomm",
            "createdOn": 1472125459551,
            "modifiedOn": 1472125459551,
            "id": "AVbBikDkn32QqPNAhbrk",
            "parent": "",
            "sid": "579597914b28bdc67fefbb64",
            "spid": "57bea096c1b08db18f2517e3",
            "xposition": "283",
            "yposition": "205",
            "dev_uid": "13:3a:22:ea:1a:1f",
            "dev_status": "Added",
            "dev_type": "ap",
            "devuid": "13:3a:22:ea:1a:1f",
            "devstatus": "active",
            "devtype": "server",
            "settingsAsString": "",
            "child":[],
            "devname":"server2"
        }
    ];
    
}]);

app.directive('topology', function(){
    
    function link(scope, element, attrs){
        var projectWidth = element[0].clientWidth;
        var projectheight = window.innerHeight - 150;
        var canvas = element[0].children[1];
        canvas.setAttribute("width", projectWidth);
        canvas.setAttribute("height", projectheight);
        var hostUrl = "http://localhost/project-quber/project-03102016/asset/img/icons/"
//        var hostUrl = "http://localhost/project-quber/project-03102016/asset/img/icons/"
        /*Scope Click Function*/
        scope.serveropen = function(){
            scope.serveropened = !scope.serveropened;
            scope.switchopened = false;
        }
        scope.switchopen = function(){
            scope.switchopened = !scope.switchopened;
            scope.serveropened = false;
            scope.checkServer();
        }
        
        function Shape(data){
            //console.log(data);
            this.devType = data.devtype;
            this.xposition = data.xposition;
            this.yposition = data.yposition;
            this.devuid = data.devuid;
            this.devname = data.devname;
            if(data.parent == ""){
                this.parentif = false;
                this.parent = "";
            }else{this.parentif = true; this.parent = data.parent;}
            if(!(data.xCalc == null || data.xCalc == undefined)){
                this.xCalc = data.xCalc;
            }
            if(!(data.yCalc == null || data.yCalc == undefined)){
                this.yCalc = data.yCalc;
            }
            if(this.devType == "server"){
                this.imgSrc = hostUrl + "server-grey.png"
            }
            else if(this.devType == "switch"){
                this.imgSrc = hostUrl + "switch-grey.png"
            }
        }
        Shape.prototype.draw = function(ctx){
            var img = new Image();
            //console.log(this.xCalc);
            img.src = this.imgSrc;
//            if(this.devType == "server"){
            ctx.drawImage(img, this.xCalc, this.yCalc, 24, 24);
//        }
            
        }
        Shape.prototype.contains = function (mx, my) {
            return (this.x <= mx) && (this.x + this.w >= mx) &&
                (this.y <= my) && (this.y + this.h >= my);
        }
                
        function CanvasState(canvas){
            this.offSetX = 15;
            this.offSetY = 122;
            this.ctx = canvas.getContext('2d');
            this.width = canvas.width;
            this.height = canvas.height;
            this.valid = false;
            this.shapes = [];
            var myState = this;
            scope.server = {
                devType:"server",
                xposition:"",
                yposition:""
            };
            //scope.servers ={};
            scope.switch ={
                devType:"switch",
                xposition:"",
                yposition:""
            };
            scope.newServer = function(server){  
                if(server.devname == null || server.devname == undefined || server.devname == ""){
                    scope.errorName = true;
                }
                else if(server.devuid == null || server.devuid == undefined || server.devuid == ""){
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.serveropened = false;
                    //Send Data to Backend Server
                    var shape = {
                        devtype : server.devType,
                        xposition:server.xposition,
                        yposition:server.xposition,
                        devname:server.devname,
                        devuid:server.devuid
                    }; 
                    myState.addShape(new Shape(shape));
                    scope.server.devname = "";
                    scope.server.devuid = "";
                }
            }
            scope.checkServer = function(){
                
                if(myState.shapes.length == 0){
                    scope.showSwitch = false;
                }
                else if(myState.shapes.length == 1){
                    scope.showSwitch = true;
                    scope.needServer = false;
                    //console.log('false');
                    scope.switch.parent = myState.shapes[0].devuid;
                }
                else{
                    scope.showSwitch = true; 
                    scope.needServer = true;
                    //console.log('false');
                    scope.servers = myState.shapes;
                    scope.switch.parent = scope.servers[0].value;
                }
            }
            //console.log(scope.switch);
            scope.newSwitch = function(data){
                if(data.parent == null || data.parent == undefined || data.parent == ""){
                    scope.errorServer = true;
                }
                else if(data.devname == null || data.devname == undefined || data.devname == ""){
                    scope.errorServer = false;
                    scope.errorName = true;
                }
                else if(data.devuid == null || data.devuid == undefined || data.devuid == ""){
                    scope.errorServer = false;
                    scope.errorName = false;
                    scope.errorid = true;
                }
                else{
                    scope.errorName = false;
                    scope.errorid = false;
                    scope.switchopened = false;
                    //Send Data to Backend Server
                    var shape = {
                        devtype : data.devType,
                        xposition:data.xposition,
                        yposition:data.xposition,
                        devname:data.devname,
                        devuid:data.devuid
                    }; 
                    myState.addShape(new Shape(shape));
                    scope.switch.parent = scope.servers[0].value;
                    scope.switch.devname = "";
                    scope.switch.devuid = "";
                }
                
            }
            
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
                var sx = 0;
                var swx = 0;
                for(var i = 0; i < len; i++){
                    if(shapes[i].devType == "server"){
                        if(shapes[i].parent == ""){
                            sx = sx + 1;
                        } 
                    }
                    if(shapes[i].devType == "switch"){
                            swx = swx + 1;
                    }
                     
                }
                //var xCalc = this.width / len;
                
                //console.log(xCalc);
                var intialsX = 0;
                var intialswX = 0;
                
                for(var i = 0; i < len; i++){
                    //console.log(shapes[i].devType);
                    if(shapes[i].devType == "server"){
                        
                        var xServer = sx;
                        var xCalc = this.width / sx;
                        var yCalc = 20;
                        shapes[i].xCalc = intialsX + (xCalc / 2) - 12; 
                        shapes[i].yCalc = yCalc; 
                    }
                    if(shapes[i].devType == "switch"){
                        var xCalc = xServer / swx;
                        var yCalc = 100;
                        shapes[i].xCalc = intialswX + (xCalc / 2) - 12; 
                        shapes[i].yCalc = yCalc; 
                    }
                    
                    //console.log(shapes[i].xCalc); 
                    var shape = shapes[i];
                    shapes[i].draw(ctx);
                    intialsX = intialsX + xCalc;
                }
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
    return {
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