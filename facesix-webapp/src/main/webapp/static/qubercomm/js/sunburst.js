function sunburstF(chart) {
  var $container = $('#sbtChart'),
  width = $container.width(),
  height = $container.height(),
  radius = Math.min(width, height) / 2;
  
  
   var totalSize = 0; 
   // Breadcrumb dimensions: width, height, spacing, width of tip/tail.
   var b = {
  		w: 75, h: 50, s: 3, t: 10
   };
   
	// Mapping of step names to colors.
	var colors = {
	  "server": "#5687d1",
	  "switch": "#7b615c",
	  "ap": "#de783b",
	  "sensor": "#6ab975",
	  "end": "#bbbbbb"
	};   
 
  var x = d3.scale.linear()
  .range([0, 2 * Math.PI]);

  var y = d3.scale.linear()
  .range([0, radius]);

  var color = d3.scale.category20c();
  
  d3.select("svg").remove();
  //d3.select("#sbtChart	").select("svg").remove();
  //$("#sbtChart").html("");
  

  var svg = d3.select("#sbtChart").append("svg")
  .classed("svg-container", true) //container class to make it responsive
  .attr("width", Math.min(width,height)+'px')
  .attr("height", Math.min(width,height)+'px')
  .attr('viewBox','0 0 '+Math.min(width,height) +' '+Math.min(width,height) )
  .attr('preserveAspectRatio','xMinYMin meet')
  .append("g")
  
  .attr("transform", "translate(" + Math.min(width,height) / 2 + "," + Math.min(width,height) / 2 + ")")
  .classed("svg-content-responsive", true); 
  var partition = d3.layout.partition()
  .value(function(d) { return d.size; });

  var arc = d3.svg.arc()
  .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
  .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
  .innerRadius(function(d) { return Math.max(0, y(d.y)); })
  .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });

  // Basic setup of page elements.
  initializeBreadcrumbTrail();
  drawLegend();
  d3.select("#togglelegend").on("click", toggleLegend);


// d3.json("js/flare.json", function(error, root) {
  var root=chart;
  
  //console.log (root);
  var g = svg.selectAll("g")
  .data(partition.nodes(root))
  .enter().append("g");
  
  

  var path = g.append("path")
  .attr("d", arc)
  .attr("fill-rule", "evenodd")
  .style("fill", function(d) { return d.bgColor ? d.bgColor : color((d.children ? d : d.parent).name); })
  .style("opacity", 1)
  .style("stroke", '#fff')
  .on("mouseover", mouseover);
  //.on("click", click);
  
   d3.select("#sbtChart").on("mouseleave", mouseleave);
     // Get total size of the tree = value of root node from partition.
  totalSize = path.node().__data__.value;

  var link = g.append("a").attr("xlink:href",function(d){ 
	  
  	   var spid = d.spid;
       var urlMap={
        'server':'flrdash',
        'switch':'swiboard',
        "ap":'devboard',
        'sensor':'devboard'
       }
       if(d.type=="venue")
        var url="/facesix/web/site/portion/list?sid="+urlObj.sid+"&cid="+urlObj.cid;
       else if(d.type=="venue")
       	var url="/facesix/web/site/portion/list?cid="+urlObj.cid;
       else if(d.type=="floor")
        var url="/facesix/web/site/portion/dashboard?sid="+urlObj.sid+"&spid="+spid+"&uid="+d.uid+"&cid="+urlObj.cid+"&param="+urlObj.dashview;
       else if(d.type=="server")
        var url="/facesix/web/site/portion/dashboard?sid="+urlObj.sid+"&spid="+d.fid+"&uid="+d.uid+"&cid="+urlObj.cid+"&param="+urlObj.dashview;
       else if(d.type=="sensor")
    	   var url="/facesix/web/finder/device/"+urlMap[d.type]+"?sid="+urlObj.sid+"&spid="+d.fid+"&uid="+d.uid+"&cid="+urlObj.cid+"&type="+d.type;
       else
    	   var url="/facesix/web/site/portion/"+urlMap[d.type]+"?sid="+urlObj.sid+"&spid="+d.fid+"&uid="+d.uid.toLowerCase()+"&cid="+urlObj.cid+"&type="+d.type;
       return url
     })
     
  link.append("title").text(function(d){
        if(d.type=="venue" || d.type=="floor")
        	return d.name
        return d.title
  })
  var text = link.append("text")
  .attr("transform", function(d) { return "rotate(" + computeTextRotation(d) + ")"; })
  .attr("x", function(d) { 
    if(d.type=="venue")
      return y(d.y)
    return y(d.y); 
    })
    .attr("dx",function(d){
      if($(window).width() < 1100){
         if(d.type=="venue")
          return -20
         return 2;
      }
       if(d.type=="venue")
          return -50
       return 6; 
    })// margin
    .attr("dy", ".35em") // vertical-align
    .text(function(d) { 

      var str=d.name;
      if($(window).width() < 1100){
        if(str.length>10){
          str=str.substr(0,8)
          str+="...";
          return str;
        }
      }else if($(window).width() > 1100){
         if(str.length>12){
          str=str.substr(0,10)
          str+="...";
          return str;
        }
      }
      return d.name; 
    });

    function click(d) {
    // fade out all text elements
    text.transition().attr("opacity", 0);

    path.transition()
    .duration(750)
    .attrTween("d", arcTween(d))
    .each("end", function(e, i) {
          // check if the animated element's data e lies within the visible angle span given in d
          if (e.x >= d.x && e.x < (d.x + d.dx)) {
            // get a selection of the associated text element
            var arcText = d3.select(this.parentNode).select("text");
            // fade in the text element and recalculate positions
            arcText.transition().duration(750)
            .attr("opacity", 1)
            .attr("transform", function() { return "rotate(" + computeTextRotation(e) + ")" })
            .attr("x", function(d) { 
              if(d.type=="venue")
                return y(d.y)
              return y(d.y); 
            });
          }
        });
  	}
  	
  
  function initializeBreadcrumbTrail() {
  	// Add the svg area.
  	var trail = d3.select("#sequence").append("svg:svg")
      .attr("width", width)
      .attr("height", 50)
      .attr("id", "trail");
  	// Add the label at the end, for the percentage.
  	trail.append("svg:text")
    .attr("id", "endlabel")
    .style("fill", "#000");
	}
  
	  function getAncestors(node) {
	  var path = [];
	  var current = node;
	  while (current.parent) {
	    path.unshift(current);
	    current = current.parent;
	  }
	  return path;
	}
	
	
	// Generate a string that describes the points of a breadcrumb polygon.
	function breadcrumbPoints(d, i) {
	  var points = [];
	  points.push("0,0");
	  points.push(b.w + ",0");
	  points.push(b.w + b.t + "," + (b.h / 2));
	  points.push(b.w + "," + b.h);
	  points.push("0," + b.h);
	  if (i > 0) { // Leftmost breadcrumb; don't include 6th vertex.
	    points.push(b.t + "," + (b.h / 2));
	  }
	  return points.join(" ");
	}
	
	// Update the breadcrumb trail to show the current sequence and percentage.
	function updateBreadcrumbs(nodeArray, percentageString) {
	
	  // Data join; key function combines name and depth (= position in sequence).
	  var g = d3.select("#trail")
	      .selectAll("g")
	      .data(nodeArray, function(d) { return d.name + d.depth; });
	
	  // Add breadcrumb and label for entering nodes.
	  var entering = g.enter().append("svg:g");
	
	  entering.append("svg:polygon")
	      .attr("points", breadcrumbPoints)
	      .style("fill", function(d) { return colors[d.name]; });
	
	  entering.append("svg:text")
	      .attr("x", (b.w + b.t) / 2)
	      .attr("y", b.h / 2)
	      .attr("dy", "0.35em")
	      .attr("text-anchor", "middle")
	      .text(function(d) { return d.name; });
	
	  // Set position for entering and updating nodes.
	  g.attr("transform", function(d, i) {
	    return "translate(" + i * (b.w + b.s) + ", 0)";
	  });
	
	  // Remove exiting nodes.
	  g.exit().remove();
	
	  // Now move and update the percentage at the end.
	  d3.select("#trail").select("#endlabel")
	      .attr("x", (nodeArray.length + 0.5) * (b.w + b.s))
	      .attr("y", b.h / 2)
	      .attr("dy", "0.35em")
	      .attr("text-anchor", "middle")
	      .text(percentageString);
	
	  // Make the breadcrumb trail visible, if it's hidden.
	  d3.select("#trail")
	      .style("visibility", "");
	
	}
	
	// Fade all but the current sequence, and show it in the breadcrumb trail.
	function mouseover(d) {
	
	  var percentage = (100 * d.value / totalSize).toPrecision(3);
	  var percentageString = percentage + "%";
	  if (percentage < 0.1) {
	    percentageString = "< 0.1%";
	  }
	
	  d3.select("#percentage")
	      .text(percentageString);
	
	  d3.select("#explanation")
	      .style("visibility", "");
	
	  var sequenceArray = getAncestors(d);
	  updateBreadcrumbs(sequenceArray, percentageString);
	
	  // Fade all the segments.
	  d3.selectAll("path")
	      .style("opacity", 0.3);
	
	  // Then highlight only those that are an ancestor of the current segment.
	  svg.selectAll("path")
	      .filter(function(node) {
	                return (sequenceArray.indexOf(node) >= 0);
	              })
	      .style("opacity", 1);
	}
	
	// Restore everything to full opacity when moving off the visualization.
	function mouseleave(d) {
	
	  // Hide the breadcrumb trail
	  d3.select("#trail")
	      .style("visibility", "hidden");
	
	  // Deactivate all segments during transition.
	  d3.selectAll("path").on("mouseover", null);
	
	  // Transition each segment to full opacity and then reactivate it.
	  d3.selectAll("path")
	      .transition()
	      .duration(1000)
	      .style("opacity", 1)
	      .each("end", function() {
	              d3.select(this).on("mouseover", mouseover);
	            });
	
	  d3.select("#explanation")
	      .style("visibility", "hidden");
	}
	
	function drawLegend() {
	
	  // Dimensions of legend item: width, height, spacing, radius of rounded rect.
	  var li = {
	    w: 75, h: 30, s: 3, r: 3
	  };
	
	  var legend = d3.select("#legend").append("svg:svg")
	      .attr("width", li.w)
	      .attr("height", d3.keys(colors).length * (li.h + li.s));
	
	  var g = legend.selectAll("g")
	      .data(d3.entries(colors))
	      .enter().append("svg:g")
	      .attr("transform", function(d, i) {
	              return "translate(0," + i * (li.h + li.s) + ")";
	           });
	
	  g.append("svg:rect")
	      .attr("rx", li.r)
	      .attr("ry", li.r)
	      .attr("width", li.w)
	      .attr("height", li.h)
	      .style("fill", function(d) { return d.value; });
	
	  g.append("svg:text")
	      .attr("x", li.w / 2)
	      .attr("y", li.h / 2)
	      .attr("dy", "0.35em")
	      .attr("text-anchor", "middle")
	      .text(function(d) { return d.key; });
	}
	
	function toggleLegend() {
	  var legend = d3.select("#legend");
	  if (legend.style("visibility") == "hidden") {
	    legend.style("visibility", "");
	  } else {
	    legend.style("visibility", "hidden");
	  }
	}	
  
  
  
  
  
  
// });

d3.select(self.frameElement).style("height", height + "px");

// Interpolate the scales!
function arcTween(d) {
  var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
  yd = d3.interpolate(y.domain(), [d.y, 1]),
  yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
  return function(d, i) {
    return i
    ? function(t) { return arc(d); }
    : function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
  };
}

function computeTextRotation(d) {
  return (x(d.x + d.dx / 4) - Math.PI / 2) / Math.PI * 180;
}
}

// sunburstF();

$(window).resize(function() {
  //sunburstF();
});
var sunburst1 = {
    "name": "venue",
    "bgColor": "#ff8672",
    "children": []
};
var bgColors={
  "active":"#6baa01",
  'idle':'#f8bd19',
  "inactive":"#cccccc",
  "floor":"#33bdda"
}
function Floor(floor) {
    this.Floor = {
        'name': floor.name,
        'bgColor':bgColors['floor'],
        'children': [],
        'spid':floor.spid,
        'type':'floor',
        'size': 1
    }
    return this;
}
Floor.prototype.addDevices = function(devices) {
    var currentFloor = this.Floor
    currentFloor.children.push(devices)
}


function networkDevices(device, id,child) {

	var deviceName=device.typefs.split("");
	var twochar = deviceName[0].concat(deviceName[1]);
	if(twochar!=null) twochar = twochar.toUpperCase();
	
	if (twochar == "AP")  {
		if (device.alias == undefined)
			deviceName = device.typefs.split("");
		else
			deviceName = device.alias.split("");
	}
	//console.log(twochar + '-' + deviceName)
	if(deviceName.length)
		deviceName[0]=deviceName[0].toUpperCase();
	
	deviceName=deviceName.join("")
	if(deviceName == "Sensor") {
		//deviceName ="BLE";
		deviceName = device.alias;
	}
    if (child) {
        return {
            'name': deviceName+" ",
            'bgColor': bgColors[device.status],
            'size': 1,
            'type':device.typefs,
            'children': [],
            'parent': device.parent,
            'fid':device.spid,	
            'title':deviceName+"-"+device.uid,
            'uid': device.uid,
            'childCount':0,
        }
    }
	if(device.parent=="ble") {
		var devAlias = device.alias;
		this.device = {
		        'name': devAlias,
		        'bgColor': bgColors[device.status],
		        'size': 1,
		        'type':"sensor",
		        'newtyp': device.parent,
		        'uid': device.uid,
		        'title':devAlias+"-"+device.uid,
		        'fid':device.spid,
		        'children': [],
		        'childCount':0,
		    }
	}else{
	    this.device = {
	        'name': deviceName+" "+id,
	        'bgColor': bgColors[device.status],
	        'size': 1,
	        'type':device.typefs,
	        'newtyp': device.parent,
	        'uid': device.uid,
	        'title':deviceName+"-"+device.uid,
	        'fid':device.spid,
	        'children': [],
	        'childCount':0,
	    }
	}
}
networkDevices.prototype.addChildren = function() {
    var current = this.device;
    var that = this;
    var recursiveDepthAdd = function(current) {
        var children = sunBurstChart.childDevices
        for (var i = 0; i < children.length; i++) {
            if (current.uid == children[i].parent){
            	if (children[i].name == "BLE ") {
            		//children[i].name;
            	} else {
            		children[i].name+=++current.childCount;
            	}
                
                current.children.push(children[i]);
              }
        }
        for (var i = 0; i < current.children.length; i++) {
            recursiveDepthAdd(current.children[i])
        }
    }
    recursiveDepthAdd(current)
}
var sunBurstChart = {
    'fetchurlParams':function(search){
		var urlObj={}
		if(search)
		  urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
		return urlObj; 
	},
    getJSON: function() {
        var self = this;
        var urlObj=this.fetchurlParams(window.location.search.substr(1));
        $.ajax({
            url: '/facesix/rest/site/portion/networkdevice/view?sid='+urlObj.sid,
            method: 'get',
            success: function(response,error){
                var json = response.floors;
                
                sunburst1.name=response.name;
                sunburst1.sid=response.sid;  
                sunburst1.type="venue";             
                for (var i = 0; i < json.length; i++) {
                    var floor = new Floor(json[i].floor)
                    var nodeData = {
                        'server': 0,
                        'switch': 0,
                        'ap': 0,
                        'sensor': 0
                    }
                    var devices = json[i].floor.devices;
                    self.childDevices=[];
                    self.deviceTree={};
                    
                    for (var j = 0; j < devices.length; j++) {
                    	
                        if (devices[j].typefs.indexOf("server") != -1 || devices[j].parent.indexOf("ap") != -1) {
                            nodeData[devices[j].typefs] += 1;
                            var server = new networkDevices(devices[j], nodeData[devices[j].typefs])
                            self.deviceTree[devices[j].typefs + "" + nodeData[devices[j].typefs]] = server;
                        } else {
                            nodeData[devices[j].typefs] += 1;
                            var chilDevice = networkDevices(devices[j], nodeData[devices[j].typefs], true)
                            self.childDevices.push(chilDevice);
                        }
                    }
                    for (var key in self.deviceTree) {
                        self.deviceTree[key].addChildren();
                        floor.addDevices(self.deviceTree[key].device)
                    }
                    sunburst1.children.push(floor.Floor);
                }
                sunburstF(sunburst1);
                //$("#sbtChart").css({height:"auto"})
                $(".sunburstBig").css({height:$(window).height()})
                
	            setTimeout(function () {
	            	//sunburst1 = {};
	            	sunburst1.children=[];
	            	sunBurstChart.getJSON();

	            }, 10000);                
            },
            error: function(error) {
				//console.log(error)
            }
        })
    },
}
sunBurstChart.getJSON();