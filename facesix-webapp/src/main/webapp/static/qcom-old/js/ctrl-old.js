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
            "child":[]
        },
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
            "child":[]
        }
    ];
    $scope.check = function(input){
        console.log(input.name);
    }
}]);

app.directive('topology', function(){
    
    function link(scope, element, attrs){
        var projectWidth = element[0].clientWidth;
        var projectheight = window.innerHeight - 150;
        var canvas = element[0].children[1];
        canvas.setAttribute("width", projectWidth);
        canvas.setAttribute("height", projectheight);
        
        /*Scope Click Function*/
        scope.serveropen = function(){
            scope.serveropened = !scope.serveropened;
        }
        
        
        function Shape(data){
            //console.log(data);
            this.devType = data.devtype;
            this.xposition = data.xposition;
            this.yposition = data.yposition;
            if(!(data.xCalc == null || data.xCalc == undefined)){
                this.xCalc = data.xCalc;
            }
            if(!(data.yCalc == null || data.yCalc == undefined)){
                this.yCalc = data.yCalc;
            }
            if(this.devType == "server"){
                this.imgSrc = "/facesix/static/qcom/img/icons/server-grey.png"
            }
            else if(this.devType == "iot"){
                this.imgSrc = "http://localhost/project-quber/project-03102016/asset/img/icons/server-grey.png"
            }
        }
        Shape.prototype.draw = function(ctx){
            var img = new Image();
            //console.log(this.xCalc);
            img.src = this.imgSrc;
            if(this.devType == "server"){
            ctx.drawImage(img, this.xCalc, this.yCalc, 24, 24);}
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
            scope.newServer = function(server){
                if(!(server.name == null || server.name == undefined) && !(server.id == null || server.id == undefined)){
                    scope.serveropened = false;
                    var shape = {
                        devtype : server.devType,
                        xposition:server.xposition,
                        yposition:server.xposition,
                        name:server.name,
                        id:server.id
                    }; 
                    myState.addShape(new Shape(shape));
                    scope.server.name = "";
                    scope.server.id = "";
                }
                else if(server.name == null || server.name == undefined){
                    scope.errorName = true;
                }
                else if(server.id == null || server.name == id){
                    scope.errorid = true;
                }
            }
            
//            var items = element[0].children[0].children[0].children;
//            for (var i = 0; i < items.length; i++) {
//                var children = items[i].addEventListener('click', clickHandler);
//            }
//            
//            function clickHandler(e) {
//               var item = e.target.attributes;
//                var child = e.target.children[0];
//                //console.log(child);
//                   for (var i = 0; i < item.length; i++) {
//                    if(item[i].name == "click"){
//                        var icon = item[i].value;
//                    }  
//                }
//               selectedicon(e, icon);
//            }
//            
//            function selectedicon(e, icon){
//                console.log(e);
////                var shape = {
////                    devtype : icon,
////                    xposition:250,
////                    yposition:250
////                };
////                myState.addShape(new Shape(shape));
//            }
            
            
            
            
            
            
            
            
            
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
                
                var xCalc = this.width / len;
                var yCalc = 20;
                //console.log(xCalc);
                var intialX = 0;
                for(var i = 0; i < len; i++){
                    
                    shapes[i].xCalc = intialX + (xCalc / 2) - 12; 
                    shapes[i].yCalc = yCalc; 
                    //console.log(shapes[i].xCalc); 
                    var shape = shapes[i];
                    shapes[i].draw(ctx);
                    intialX = intialX + xCalc;
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