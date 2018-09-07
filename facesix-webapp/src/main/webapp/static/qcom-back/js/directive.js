app.directive('stringToNumber', function() {
  return {
    require: 'ngModel',
    link: function(scope, element, attrs, ngModel) {
      ngModel.$parsers.push(function(value) {
        return '' + value;
      });
      ngModel.$formatters.push(function(value) {
        return parseFloat(value);
      });
    }
  };
});

app.directive('open', function(){
    function link(scope, element, attrs){
        var ele = element[0];
        var ele2 = document.getElementById(scope.open);
        
        ele.addEventListener('click', function(e){
            //console.log(ele.classList.contains("open"));
            if(ele.classList.contains("open")){
                ele.classList.remove("open");
                ele2.classList.remove("div-open");
            }
            else{
                ele.classList.add("open");
                ele2.classList.add("div-open");
            }
        });
        ele2.addEventListener('click', function(e){
            //console.log(ele.classList.contains("open"));
            if(ele.classList.contains("open")){
                ele.classList.remove("open");
                ele2.classList.remove("div-open");
            }
            else{
                ele.classList.add("open");
                ele2.classList.add("div-open");
            }
        });  
    }
     return {
        restrict: 'EA',
         scope:{
             open:"@"
         },
        link: link
    };
});

app.directive('barprogress', function(){
    function link(scope, element, attrs){
        scope.$watch('data', function(value){
            scope.percentage = value.used/value.total * 100;
        });
    }
    return{
        restrict: 'EA',
        replace: true,
        scope: {
            data:'='
                },
        link: link,
        template:'<div class="bar-progress"><div class="icon"><i class="fa fa-wifi"></i></div><div class="back"><div class="front" style="width:{{percentage}}%"></div></div><div class="value tc">{{data.used}}/{{data.total}}</div></div>'      
    };
});

app.directive('roundprogress', function($timeout){
    function link(scope, element, attrs){
        //alert('ok');
        var ctx = element[0].getContext('2d');
        ctx.clearRect(0,0,element[0].width,element[0].height);
        
        function hexToRgb (hex) {
            var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
            return result ? {
                r: parseInt(result[1], 16),
                g: parseInt(result[2], 16),
                b: parseInt(result[3], 16)
            } : null;
        }
        
        function rgba (color, alpha) {
            return 'rgba(' + color.r + ','+ color.g +','+color.b+','+ alpha +')';
        }
        
//        if(!(scope.percent == null || scope.percent == undefined)){
//                    var percent = scope.percent;
//                } else{var percent = 0;}
        if(!(scope.data == null || scope.data == undefined)){
                    var data = scope.data;
                } else{var data = 0;}
        
        if(!(scope.label == null || scope.label == undefined)){
                    var label = scope.label;
                } else{var label = "";}
                
        if(!(scope.size == null || scope.size == undefined)){
                    var size = scope.size;
                } else{var size = scope.$eval(attrs.width);}
        
        if(!(scope.color == null || scope.color == undefined)){
                    var color = hexToRgb(scope.color);
                } else{var color = "#4CAF50";}
                
        if(!(scope.linewidth == null || scope.linewidth == undefined)){
                    var lineWidth = scope.linewidth;
                } else{var lineWidth = 5;}
        
        if(!(scope.total == null || scope.total == undefined)){
                    var total = scope.total;
                } else{var total = 100;}
        
        var percent = data / total * 100;
        
        var radius = (size - lineWidth) / 2;
        var curPerc = 0;
        var full = 1;      
        var drawCircle = function(color, lineWidth, full) {
                ctx.clearRect(0,0,element[0].width,element[0].height);
                ctx.beginPath();
                ctx.arc(size/2, size/2, radius, -1.5, (Math.PI * 2 * full) - 1.5, false);
                ctx.strokeStyle = color;
                ctx.lineCap = 'round'; // butt, round or square
                ctx.lineWidth = lineWidth;
                ctx.stroke();
        };
        
        function getcolor(color){
                    return{
                        strokeColor: rgba(color, 0.7)
                    }
                }
            var getcolor = getcolor(color);
        
        var drawCircle1 = function(color, lineWidth, curPerc, percent1) {
                
                var endangle = percent1 / 100;
                //console.log(endangle);
                ctx.clearRect(0,0,element[0].width,element[0].height);
                ctx.imageSmoothingEnabled=true;
                drawCircle('#efefef', lineWidth, 100 / 100);  
                if(!(endangle == 0)){
                    ctx.beginPath();
                    ctx.arc(size/2, size/2, radius, -1.5, (Math.PI * 2 * curPerc) - 1.5, false);
                    ctx.strokeStyle = getcolor.strokeColor;
                    ctx.fillStyle = getcolor.strokeColor;
                    //console.log(ctx.strokeStyle)
                    ctx.lineCap = 'round'; // butt, round or square
                    ctx.lineWidth = lineWidth;
                    ctx.stroke(); 
                    if (curPerc < endangle) {
                        var curPerc = curPerc + 0.01;
                        $timeout(function(){drawCircle1(color, lineWidth, curPerc, percent1);}, 20);
                    }
                }
                ctx.textAlign ='center';
                ctx.font = "18px Arial";
                ctx.fillStyle = "grey";
                ctx.fillText(data, size/2 , size/2);
                ctx.font = "10px Arial";
                ctx.fillText(label, size/2, (size/2) +13);
                
        };
        scope.$watch('data', function(value){
            if(!(value == null || value == undefined || value =="")){
                if(value >=100){var percent1 = 100;}
                else{var percent1 = value / total *100;}   
            } else{var percent1 = 0;}
            drawCircle1(color, lineWidth, curPerc / 100, percent1);
        });
    }
    return{
    restrict: 'EA',
    replace: true,
    scope: {    
                data:'=',
                percent:'=',
                label:'=', 
                size:'=',
                linewidth :'=',
                color:'=',
                total:'='
              },
    link: link
        
    };
});

app.directive('timer', function($timeout){
    function link(scope, element, attrs){   
        function link(){
            //console.log(scope.timer);
           var elm = element[0].children;
            var length = elm.length;
            console.log(length);
            elm[0].classList.add('active');
            setInterval(function () {
                for(var i=0; i < length; i++){
                    //console.log(i);
                    var activeclass = angular.element(elm[i]).hasClass('active');
                    //console.log(activeclass);
                   if(activeclass){
                       if(length - 1 == i){
                           elm[0].classList.add('active');
                           
                       }
                       else{
                           elm[i + 1].classList.add('active');
                       }
                        
                       
                       
                        elm[i].classList.remove('active');
                       return false;
                   }
                    
                }
            }, 5000);
        }
        scope.$watch(scope.timer, function(newVal) {
            link();
        }, true);
    }
    return {
        restrict: 'EA',
        scope:{
            timer:"@"
        },
        link: link,
        controller:'VenueCtrl'
    };
});

app.directive('hchart', ['$http', function($http){
    function link(scope, element, attrs){
        //console.log(scope.labels);
        var canvas = element[0];
        var width = canvas.parentNode.clientWidth - 10;
        canvas.setAttribute("width", width);
        canvas.setAttribute("height",Math.round(width/2.04));
        var ctx = canvas.getContext("2d");
        
        if(scope.data[0] == null || scope.data[0] == undefined){
        	console.log('ok');
        }
        
	  	var barChartData = {
	  		labels : scope.labels,
	  		datasets : [
	  			{
	  				fillColor : "rgba(243,110,101,0.8)",
	  				strokeColor : "rgba(243,110,101,0.8)",
	  				highlightFill : "rgba(243,110,101,0.75)",
	  				highlightStroke : "rgba(243,110,101,1)",
	  				data : scope.data[0]
	  			},
	  			{
                    fillColor : "rgba(26,120,208,0.8)",
	  				strokeColor : "rgba(26,120,208,0.8)",
	  				highlightFill: "rgba(26,120,208,0.75)",
	  				highlightStroke: "rgba(26,120,208,1)",
	  				data : scope.data[1]
	  			}
	  		]

	  	};   
        function DrawChart(){
            new Chart(ctx).HorizontalBar(barChartData, {
	  			responsive: true,
	           barShowStroke: false
	  		});
        }
        DrawChart();
        scope.$watch('data', function(value){
        	DrawChart();
        });
        
    }
    return{
        restrict: 'E',
        replace: true,
        scope: {
            data: '=',
            labels:"="
        },
        link: link,
        template:"<canvas></canvas>"
    }
    
}]);

app.directive('gvenue', ['$http', '$compile', function($http,$compile){
    function link(scope, element, attrs){
        var geocoder;
        var map;
//        $http.get('sample.json').then(function successCallback(response){
//            
//        }, function errorCallback(response){
//            
//        });
        
//        google.maps.event.addDomListener(window, "load", initialize);
//        var locations = [
//          ['Bondi Beach', -33.890542, 151.274856,,, 'Bondi Beach', 4],
//          ['Coogee Beach', -33.923036, 151.259052,,,'Coogee Beach', 5],
//          ['Cronulla Beach', -34.028249, 151.157507,,,'Cronulla Beach', 3],
//          ['Manly Beach', -33.80010128657071, 151.28747820854187,,, 'Manly Beach', 2],
//          ['Maroubra Beach', -33.950198, 151.259302,,,'Maroubra Beach', 1]
//        ];
        var locations = [
            {Info:"M A Chidambram Stadium", deviceConnected:"2 Device", Lat: 13.070642, Lng:80.2038323,sid:"58063282e4b0591fbb5da5ad"},
            {Info:"M A Chidambram Stadium1", deviceConnected:"2 Venues", Lat: 41.926979, Lng:12.517385, sid:"58063282e4b0591fbb5da5ad"},
            {Info:"M A Chidambram Stadium2", deviceConnected:"2 Venues", Lat: 61.926979, Lng:12.517385, sid:"58063282e4b0591fbb5da5ad"}
        ]
        scope.item = {};
        function setMarkers(map, locations) {
            var bounds = new google.maps.LatLngBounds();
            for (var i = 0; i < locations.length; i++) {
                var item = locations[i];
                scope.item = locations[i];
                
                var myLatLng = new google.maps.LatLng(locations[i].Lat, locations[i].Lng);
                bounds.extend(myLatLng);
                
                var html = '<div class="content"><h4>'+locations[i].Info+'</h4>'+
                    '<p>'+scope.item.deviceConnected+'</p>'+
                '<span class="fl"><a href="#/venuedashboard/'+scope.item.sid+'"><i class="fa fa-line-chart"></i>Dashboard</a></span>'+
                    '<span class="fr"><a href="#/floor/'+scope.item.sid+'"><i class="fa fa-map-o"></i>Floors</span></div>';
                
                 var address = '<a href="#/venuedashboard/'+scope.item.sid+'"><div class="content">'+locations[i].Info+'</div></a>'
                var marker = new google.maps.Marker({
                    position: myLatLng,
                    map: map,
                });

                var content = html ;

                var infowindow = new google.maps.InfoWindow()
                
                google.maps.event.addListener(marker, 'click', (function (marker, content, infowindow) {
                    return function () {
//                        infowindow.close();
//                        infowindow.setContent(content);
//                        infowindow.open(map, marker);
                        window.location.href="#/venuedashboard/"+scope.item.sid;
                    };
                })(marker, content, infowindow));

                google.maps.event.addListener(marker, 'mouseover', (function (marker, content, infowindow) {
                    return function () {
                        infowindow.setContent(content);
                        infowindow.open(map, marker);
                    };
                })(marker, content, infowindow));
                google.maps.event.addListener(marker, 'mouseout', (function (marker, content, infowindow) {
                    return function () {
                        //infowindow.close();
                    };
                })(marker, content, infowindow));
                google.maps.event.addListener(map, 'click', (function (marker, content, infowindow) {
                    return function () {
                        infowindow.close();
                    };
                })(marker, content, infowindow));

            }
            
            map.fitBounds(bounds);
        }
        
        function initialize() {
            var map = new google.maps.Map(
            document.getElementById("venuelist"), {
                center: new google.maps.LatLng(37.4419, -122.1419),
                zoom: 13,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            });

        setMarkers(map,locations);
        }
        initialize();
    }
    
    return{
        restrict:'EA',
        scope:{},
        link:link
    };
    
}]);