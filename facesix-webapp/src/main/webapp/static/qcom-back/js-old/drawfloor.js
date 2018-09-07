app.directive('drawingfloor', ['$http', function($http){
    function link (scope, element, attrs){
        //console.log(scope.scale);
        /*Base Canvas for Grid*/
        function Grid(){
            var base = element[0].children[0];
            var baseCtx = base.getContext('2d');
            var step = 20;
            var baseW = base.width;
            var baseH = base.height;
            baseCtx.fillStyle = "#ffffff";
            baseCtx.fillRect(0, 0, base.width, base.height);
            baseCtx.beginPath();

            for (var x = 0; x <= baseW; x += step) {
                //console.log(x);
                baseCtx.moveTo(x, 0);
                baseCtx.lineTo(x, baseH);
            }
            baseCtx.strokeStyle = 'lightgrey';
            baseCtx.lineWidth = 1;
            baseCtx.stroke();
            baseCtx.beginPath();
            for (var y = 0; y <= baseH; y += step) {
                baseCtx.moveTo(0, y);
                baseCtx.lineTo(baseW, y);
            }
            //baseCtx.strokeStyle = 'lightgrey';
            baseCtx.lineWidth = 1;
            baseCtx.stroke();

            baseCtx.fillStyle = "grey";
            baseCtx.fillRect(0, 0, 75, baseH);

            baseCtx.textAlign = 'center';
            baseCtx.font = "14px Arial";
            baseCtx.fillStyle = "#ffffff";
            baseCtx.fillText("Tools", 37.5, 20);

            baseCtx.beginPath();
            baseCtx.moveTo(0, 30);
            baseCtx.lineTo(75, 30);
            baseCtx.strokeStyle = "#ffffff";
            baseCtx.stroke();
        }  
        Grid();
        
        function Tools(tooldata){
            this.x = tooldata.x || 0;
            this.y = tooldata.y || 0;
            this.w = tooldata.w || 1;
            this.h = tooldata.h || 1;
            this.type = tooldata.type;
            this.child = tooldata.child;
            this.toolId = tooldata.id;  
        }
        
        Tools.prototype.draw = function (ctx) {
            ctx.strokeStyle = "rgba(128,128,128, 0.5)";
            if (this.type == "fillRect") {
                ctx.fillStyle = "#ffffff";
                ctx.fillRect(this.x, this.y, this.w, this.h);
                
                var imgtool = new Image();
                    imgtool.src = this.child.img;
                    ctx.drawImage(imgtool, this.child.x, this.child.y, 18, 18); 
            }
        }       
        Tools.prototype.contains = function (mx, my) {
            return (this.x <= mx) && (this.x + this.w >= mx) &&
                (this.y <= my) && (this.y + this.h >= my);
        }  
        function Shape(data){ 
            //console.log(data);
            this.startX = data.startX;
            this.startY = data.startY;
            this.endX = data.endX;
            this.endY = data.endY;
            this.type = data.type;
            this.clear = data.clear;
            this.img = data.img;
            this.preset = data.preset;
        }
        
        Shape.prototype.draw = function(ctx){
            var canvas = ctx.canvas; 
            ctx.lineWidth = "2"
            ctx.strokeStyle = "rgba(128,128,128, 0.8)";
            ctx.font = "14px Comic Sans MS";
            ctx.fillStyle = "red";
            var constructW = scope.scale.length;
            var constructH = scope.scale.breadth;
            var unit = scope.scale.unit;
            
            var constructSh = ((canvas.height - 40) / constructH);
            var constructSw = ((canvas.width - 140) / constructW);
            if(constructSw < constructSh){
                var constructS = constructSw;
            }
            else{
                var constructS = constructSh;
            }
            
            if(this.preset == "draw"){
                
                if(this.type == "line"){
                    var xs = 0;
                    var ys = 0;
                    xs = this.endX - this.startX;
                    xs = xs * xs;
                    ys = this.endY - this.startY;
                    ys = ys * ys;
                    var lengthPixel = Math.sqrt( xs + ys );
                    var lengthfeet = lengthPixel / constructS;
                    lengthfeet = +lengthfeet.toFixed(2)
                    ctx.fillText(lengthfeet + " "+ unit, this.startX + 2+((this.endX - this.startX)/2), this.startY - 5 +((this.endY - this.startY) / 2));
                    ctx.beginPath();
                    ctx.moveTo(this.startX, this.startY);
                    ctx.lineTo(this.endX, this.endY);         
                    ctx.stroke();   
                }
                if(this.type == "rect"){
                    var w = this.endX - this.startX;
                    var h = this.endY - this.startY;
                    var lengthPixel = w;
                    var heightPixel = h;
                    var lengthfeet = lengthPixel / constructS;
                    var heightfeet = heightPixel / constructS;
                    lengthfeet = +lengthfeet.toFixed(2);
                    heightfeet = +heightfeet.toFixed(2);
                    ctx.fillText(lengthfeet + " "+ unit, this.startX -25 +((this.endX - this.startX)/2), this.startY - 5);
                    ctx.fillText(heightfeet + " "+ unit, this.endX + 5, this.startY -5 +((this.endY - this.startY)/2));
                    ctx.beginPath();
                    ctx.rect(this.startX, this.startY, w, h);
                    ctx.stroke();
                }
                if(this.type == "circle"){
                    var radius = Math.sqrt((this.startX - this.endX)* (this.startX - this.endX)+ (this.startY - this.endY)*(this.startY - this.endY) );
                    var w = radius * 2;
                    var lengthPixel = w;
                    var heightPixel = h;
                    var lengthfeet = lengthPixel / constructS;
                    lengthfeet = +lengthfeet.toFixed(2);
                    ctx.fillText(lengthfeet + " "+ unit, this.startX - 20, this.startY - 5);
                    ctx.beginPath();
                    ctx.arc(this.startX, this.startY, radius, 0, 2*Math.PI);
                    ctx.stroke();
                }   
                if(this.type == "lline"){
                    ctx.beginPath();
                    ctx.moveTo(this.startX, this.startY);
                    var w = this.endX - this.startX;
                    var h = this.endY - this.startY;
                    var lengthPixel = Math.abs(w);
                    var heightPixel = Math.abs(h);
                    var lengthfeet = lengthPixel / constructS;
                    var heightfeet = heightPixel / constructS;
                    lengthfeet = +lengthfeet.toFixed(2);
                    heightfeet = +heightfeet.toFixed(2);
                    ctx.fillText(lengthfeet + " "+ unit, this.startX -15 +((this.endX - this.startX)/2), this.endY - 5);
                    ctx.fillText(heightfeet + " "+ unit, this.startX + 5, this.startY +((this.endY - this.startY)/2));
                    ctx.lineTo(this.startX, this.endY);
                    ctx.lineTo(this.endX, this.endY);
                    ctx.stroke();
                }    
            }
            if(this.preset == "image"){
                var imgtool = new Image();
                imgtool.src = this.img;
                ctx.drawImage(imgtool, this.endX, this.endY, imgtool.width/1.25, imgtool.height/1.25);
            }
            
        }
        
        Shape.prototype.contains = function (mx, my) {
            return (this.x <= mx) && (this.x + this.w >= mx) &&
                (this.y <= my) && (this.y + this.h >= my);
        }
        
        function CanvasState(canvas){
            this.ctx = canvas.getContext('2d');
            this.canvas = canvas;
            //console.log(canvas.offsetWidth);
            this.width = canvas.offsetWidth;
            this.height = canvas.height;
            var stylePaddingLeft, stylePaddingTop, styleBorderLeft, styleBorderTop;
            
            if (document.defaultView && document.defaultView.getComputedStyle) {
                this.stylePaddingLeft = parseInt(document.defaultView.getComputedStyle(canvas, null)['paddingLeft'], 15) || 0;
                this.stylePaddingTop = parseInt(document.defaultView.getComputedStyle(canvas, null)['paddingTop'], 100) || 0;
                this.styleBorderLeft = parseInt(document.defaultView.getComputedStyle(canvas, null)['borderLeftWidth'], 0) || 0;
                this.styleBorderTop = parseInt(document.defaultView.getComputedStyle(canvas, null)['borderTopWidth'], 0) || 0;
            }
            
            var html = document.body.parentNode;
            
            this.htmlTop = html.offsetTop;
            
            this.htmlLeft = html.offsetLeft;
            
            //this.stylePaddingLeft = 15;
            this.valid = false;
            
            this.tools = [];
            this.shapes = [];
            this.redo =[];
            this.Finishedshapes =[];
            this.toolSelection = null;
            this.currentDrawing = null;
            this.drawing = false;
            
            var myState = this;
            
            canvas.addEventListener('mousedown', function(e){
                e.preventDefault();
                /*Right Click Event*/
                if(e.which == 3){
                    if(myState.drawing){
                        var ctx = myState.ctx;
                        var canvas = ctx.canvas;
                        myState.drawing = false;
                        myState.firstClick=false;
                        myState.shapes =[];
                        ctx.clearRect(0,0,myState.width, myState.height); 
                    }
                }
                
                /*LeftClick Event*/
                if(e.which == 1){
                var mouse = myState.getMouse(e);
                var mx = mouse.x;
                var my = mouse.y;
                
                /*Check Tool Selected*/
                var tools = myState.tools;
                var toolsLength = tools.length;
                for (var i = toolsLength - 1; i >= 0; i--) {
                        if (tools[i].contains(mx, my)) {
                            var myToolSel = tools[i];
                            //console.log(myToolSel);
                            myState.toolSelection = myToolSel;
                            
                            if(myState.toolSelection.child.preset == "image"){
                                myState.firstClick = true;
                                myState.drawing = true;
                                myState.toolSelected = true;
                                var shape = {
                                    type: myState.toolSelection.toolId,
                                    preset: myState.toolSelection.child.preset,
                                    img:myState.toolSelection.child.img,
                                    startX:mx,
                                    startY:my,
                                    endX:mx,
                                    endY:my,
                                    clear:true
                                }
                                var shapes =[];
                                myState.addShape(new Shape(shape));
                            }
                            else{myState.toolSelected = true;}
                            
                            if(myState.toolSelection.child.type == 'undo' || myState.toolSelection.child.type == "redo"){
                                doundoredo(myState.toolSelection);  
                            }
                            else{
                                myState.toolSelected = true;
                            }
                            
                            return;
                        }
                }
                    
                    var finishedShape = myState.Finishedshapes;
                    var finishedShapeLength = finishedShape.length;
                    for (var i = finishedShapeLength - 1; i >= 0; i--) {
                        if (finishedShape[i].contains(mx, my)) {
                            var myToolSel = tools[i];
                            //console.log(myToolSel);
                            myState.ShapeSelection = myShapeSel;
                            myState.shapeSelected = true;
                            //console.log(myState.shapeSelected);
                            return;
                        }
                }
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                function doundoredo(undoredo){
                        if(undoredo.child.type == "undo"){
                            var undo = myState.Finishedshapes;
                            var fl =    undo.length;
                             myState.Finishedshapes = [];
                             //console.log(myState.Finishedshapes);  
                              //console.log(fl);
                                if(fl != '0')
                                  { 
                                      var lenfl = fl-1;
                                      for (var i = 0; i < fl; i++) {
                                          if(lenfl == i)
                                              {
                                                 myState.redo.push(undo[i]);
                                              }
                                          else
                                              {  
                                                    myState.Finishedshapes.push(undo[i]); 
                                              } 
                                        } 
                                   }  
                        }
                        if(undoredo.child.type == "redo"){
                            
                            var redo = myState.redo;
                            var fl =    redo.length;  
                             myState.redo = [];
                             //console.log(myState.redo);  
                              //console.log(fl);
                                if(fl != '0')
                                  { 
                                      var lenfl = fl-1;
                                      for (var i = 0; i < fl; i++) {
                                          if(lenfl == i)
                                              { 
                                                  myState.Finishedshapes.push(redo[i]); 
                                              }
                                          else
                                              {  
                                                  myState.redo.push(redo[i]);
                                              } 
                                        } 
                                   } 
                            
                            
                        }
                       
                    }
                
                if(myState.toolSelected){
                    if(myState.firstClick){
                        myState.firstClick = false;
                        myState.drawing = false;
                        myState.shapes[0].endX = mx;
                        myState.shapes[0].endY = my;
                        myState.shapes[0].clear = false;
                        myState.addFinishedShape(new Shape(myState.shapes[0]));
                        //console.log(myState.FinishedShape);
                        myState.shapes =[];
                    }
                    else{
                        myState.firstClick = true;
                        myState.drawing = true;
                        var shape = {
                            type: myState.toolSelection.toolId,
                            preset: myState.toolSelection.child.preset,
                            img:myState.toolSelection.child.img,
                            startX:mx,
                            startY:my,
                            endX:mx,
                            endY:my,
                            clear:false
                        }
                        //console.log(shape);
                        var shapes =[];
                        myState.addShape(new Shape(shape));
                        this.valid = false;
                    }
                }
                
                }
            }, true);
            
            canvas.addEventListener('mousemove', function(e){
                var mouse = myState.getMouse(e);
                var mx = mouse.x;
                var my = mouse.y;
                //console.log(myState.drawing);
                if(myState.drawing){
                    
                    myState.shapes[0].endX = mx;
                    myState.shapes[0].endY = my;
                }
                    //console.log(myState.shapes[0]);
            }, true);
            
            canvas.addEventListener('mouseup', function(e){
               var mouse = myState.getMouse(e);
           }, true);
            
            canvas.addEventListener('contextmenu', function(e){
                e.preventDefault(); 
            }, true);
            
            this.interval = 30;
            
            setInterval(function () {
                 myState.draw();
            }, myState.interval);
            
        }
        
        CanvasState.prototype.addShape = function(shape){
            this.shapes.push(shape);
            //console.log(this.shapes);
            this.valid = false;
        }
        
        CanvasState.prototype.addTool = function(tool){
            this.tools.push(tool);
        }
        
        CanvasState.prototype.addFinishedShape = function(shape){
            this.Finishedshapes.push(shape);
            //console.log(this.Finishedshapes);
            this.valid = false;
        }
        
        CanvasState.prototype.clear = function(){
            this.ctx.clearRect(0, 0, this.width, this.height);
        }
        
        CanvasState.prototype.draw = function(){
            if(!this.valid){ 
                var ctx = this.ctx; 
                this.clear();
                var tools = this.tools;
                var toolslength = tools.length;
                var finishedShapes = this.Finishedshapes;
                var lenFS = finishedShapes.length;
                
                if(this.drawing){
                    this.shapes[0].draw(ctx);
                }
                
                
                
                for (var i = 0; i < toolslength; i++) {
                    var tool = tools[i];
//                    //console.log(tool);
                    if (tool.x > this.width || tool.y > this.height ||
                        tool.x + tool.w < 0 || tool.y + tool.h < 0) continue;
                    tools[i].draw(ctx); 
                }
                for (var i = 0; i < lenFS; i++) {
                   // //console.log(ctx.strokeStyle);
                    var finishedShape = finishedShapes[i];
                    finishedShapes[i].draw(ctx);
                }
                
                
            }
        }
        
        CanvasState.prototype.getMouse = function(e){
            //console.log(e);
             var element = this.canvas, offsetX = 0, offsetY = 0, mx, my;
            //console.log(this.canvas);
            //console.log(element);
            // Compute the total offset
            if (element.offsetParent !== undefined) {
                do {
                    offsetX += element.offsetLeft;
                    offsetY += element.offsetTop;
                } while ((element = element.offsetParent));
            }

            // Add padding and border style widths to offset
            // Also add the <html> offsets in case there's a position:fixed bar
//            offsetX += this.stylePaddingLeft + this.styleBorderLeft + this.htmlLeft;
//            offsetY += this.stylePaddingTop + this.styleBorderTop + this.htmlTop;
            
            offsetX += this.stylePaddingLeft + this.styleBorderLeft + this.htmlLeft;
            offsetY += this.stylePaddingTop + this.styleBorderTop + this.htmlTop;
            
            
            
            mx = e.pageX - offsetX;
            my = e.pageY - offsetY;
            //console.log(e.pageX, e.pageY, e.offsetX, e.offsetY, mx, my);
            // We return a simple javascript object (a hash) with x and y defined
            return {
                x: mx,
                y: my
            };
        }
        
        function init(){
            var s = new CanvasState(element[0].children[1]);
            var tool = scope.tools.toolsItem.items;
            var toolLength = tool.length;
            for (var i = 0; i < toolLength; i++) {
                 s.addTool(new Tools(tool[i]));
            }
            
            
             
            
        }
        init();   
    }
    return {
        restrict: 'E',
        replace: true,
        scope: {
            data: '=',
            tools: '=',
            scale: '='
        },
        link: link,
        template: '<div class="drawer"><canvas class="canvaselm" width="1319" height="550"></canvas><canvas class="canvasbase" width="1319" height="550"></canvas><ul id="ctm" class="canvas-context"><li><i class="fa fa-trash"></i>Delete</li></ul></div>'
    };
}]);
