var treeJson = {
    "name":"Floor",
    "spid":"",
    "type":"floor",
    "status":"online",
    "children":[]
};
nodeData = {
    'server': 0,
    'switch': 0,
    'ap': 0,
    'sensor': 0
}

function networkDevice(type, uid, status, child,pid) {
	
    var shortHand={
        "server":"SVR",
        "switch":"SW",
        "ap":"AP",
        "sensor":"BLE"
    }
    if (child) {
        return {
                name: shortHand[type] ,
                uid: uid,
                parent:pid,
                type:type,
                status: status=="Added"?"offline":status,
                children: []
        }
    }
    if(pid == "ble") 	type = "sensor";
    this.device = {
        name: shortHand[type],
        uid: uid,
        type:type,
        status: status=="Added"?"offline":status,
        children: []
    }
    if(pid == "ap") 	type = "ap";
    this.device = {
        name: shortHand[type],
        uid: uid,
        type:type,
        status: status=="Added"?"offline":status,
        children: []
    }
}
networkDevice.prototype.addChildren = function() {
    var current = this.device;
    var recursiveDepthAdd=function(current){
       for(var i=0;i<networkMap.childDevices.length;i++)
       {
        if(current.uid==networkMap.childDevices[i].parent)
          current.children.push(networkMap.childDevices[i])
       }
       for(var i=0;i<current.children.length;i++)
        recursiveDepthAdd(current.children[i])
    }
    recursiveDepthAdd(current)
}
networkDevice.prototype.addImmediateChild=function(device){
    var current=this.device;
    var addChild=function(currentDevice,device){
        if(currentDevice.uid==device.parent){
            currentDevice.children.push(device)
            return;
        }
        for(var i=0;i<currentDevice.children.length;i++)
            addChild(currentDevice.children[i],device);
    }
    addChild(current,device)
}
networkDevice.prototype.recursiveAdd=function(current,child){
   if(current.uid==child.parent){
     current.children.push(child)
     return
   }
   for(var i=0;i<current.children.length;i++)
     this.recursiveAdd(current.children[i],child)
}
// tree script
function createMap(treejson) {
    var margin = {
            top: 100,
            right: 20,
            bottom: 20,
            left: 120
        },
        width = 950;
    height = 850;


    var i = 0,
        duration = 750,
        root;

    var tree = d3.layout.tree()
        .size([height, width]);

    var diagonal = d3.svg.diagonal()
        .projection(function(d) {
            return [d.x, d.y];
        });

    var svg = d3.select(".TreeMap").append("svg")
        .attr("width", width + margin.right + margin.left)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


    root = treejson;

    root.x0 = height / 2;
    root.y0 = 0;

    function collapse(d) {
        if (d.children) {
            d._children = d.children;
            d._children.forEach(collapse);
            d.children = null;
        }
    }

    root.children.forEach(collapse);
    update(root);

    d3.select(self.frameElement).style("height", "100px");

    function update(source) {
        var imageurl = "/facesix/static/qubercomm/images/networkmap/";

        // Compute the new tree layout.
        var nodes = tree.nodes(root).reverse(),
            links = tree.links(nodes);
        // Normalize for fixed-depth.
        nodes.forEach(function(d) {
            d.y = d.depth * 180;
        });

        // Update the nodes…

        var node = svg.selectAll("g.node")
            .data(nodes, function(d) {
                return d.id || (d.id = ++i);
            });

        // Enter any new nodes at the parent's previous position.
        var nodeEnter = node.enter().append("g")
            .attr("class", "node")
            .attr("transform", function(d) {
                return "translate(" + d.x + "," + d.y + ")";
            });
        var ua = window.navigator.userAgent;
        var msie = ua.indexOf("MSIE ");
        var x1 = y1 = x2 = y2 = 0;

        if (document.documentMode || /Edge/.test(navigator.userAgent)) // If Internet Explorer, return version number
        {
            x1 = 35;
            x2 = 15;
            y1 = 5;
            y2 = -10;
        } else // If another browser, return 0
        {
            x1 = 25;
            x2 = 15;
            y1 = y2 = 0;
        }
        var textImageGroup = nodeEnter.append("g").attr("transform",function(d){
           if(d.name=="Floor")
             return "translate(-55 -15) rotate(90)"
           else
             return "translate(-37 23) rotate(90)"   			
        }).attr("width", "auto").attr("height", "auto").attr("class", "textImageGroup").attr("data-uid",function(d){return d.uid}).attr("data-type",function(d){return d.type}).attr("data-status",function(d){return d.status})

        nodeEnter.append("circle")
            .attr("class", function(d) {
                return d.status
            })
            .attr("r", 1e-6).attr("transform", "translate(0 0)")
            .attr("width", "60").on("click", click);

        textImageGroup.append("text")
            .attr("x", x1).attr("y", y1)
            .attr("type", function(d) {
                return d.type
            })
            .text(function(d) {
                return d.name;
            })

        textImageGroup.append("svg:image").attr("x", x2).attr("y", y2).attr('width', "1.32em").attr('height', "1.32em").attr('xlink:href', function(d) {
            if(d.name!="Floor")
               return imageurl + d.type + "-" + d.status + ".png"
        }).attr('class', function(d) {
            return d.status + d.type
        })

        // Transition nodes to their new position.
        var nodeUpdate = node.transition()
            .attr("transform", function(d) {
                return "translate(" + d.x + "," + d.y + ")";
            });


        nodeUpdate.select("circle")
            .attr("r", 9)
            .style("fill", function(d) {
                return d._children ? "lightsteelblue" : "#fff";
            });

        nodeUpdate.select("text")
            .style("fill-opacity", 1).style("transform", "translateX(-20px)");

        // Transition exiting nodes to the parent's new position.

        var nodeExit = node.exit().transition()
            .remove();

        nodeExit.select("circle")
            .attr("r", 1e-6);

        nodeExit.select("text")
            .style("fill-opacity", 1e-6);

        // Update the links…
        var link = svg.selectAll("path.link")
            .data(links, function(d) {
                return d.target.id;
            });

        // Enter any new links at the parent's previous position.
        link.enter().insert("path", "g")
            .attr("class", "link")
            .attr("d", function(d) {
                var o = {
                    x: source.x0,
                    y: source.y0
                };
                return diagonal({
                    source: o,
                    target: o
                });


            });

        // Transition links to their new position.
        link.transition()
            .attr("d", diagonal);

        // Transition exiting nodes to the parent's new position.
        link.exit().transition()
            .attr("d", function(d) {
                var o = {
                    x: source.x,
                    y: source.y
                };
                return diagonal({
                    source: o,
                    target: o
                });
            })
            .remove();

        // Stash the old positions for transition.
        nodes.forEach(function(d) {
            d.x0 = d.x;
            d.y0 = d.y;
        });
        // $('svg text').css("transform", "rotate(-90deg)");
        //$('.textImageGroup').css("transform", "rotate(90deg)");
    }

    // Toggle children on click.
    function click(d) {
        if (d.children) {
            d._children = d.children;
            d.children = null;
        } else {
            d.children = d._children;
            d._children = null;
        }
        update(d);


    }
}
// $('svg').css("transform", "rotate(90deg)");
var networkMap = {
    fetchUrlParams:function(search){
		var urlObj={}
		if(search)
		  urlObj=JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
		this.urlObj=urlObj;
		return urlObj; 
	},
    networkTree: {},
    childDevices:[],
    getJson: function() {
        var that = this;
        this.fetchUrlParams(window.location.search.substr(1));
        $.ajax({
            url: '/facesix/rest/site/portion/networkdevice/list?spid='+this.urlObj.spid,
            method: 'get',
            success: function(response) {
                var tree = response;
                for (var i = 0; i < tree.length; i++) {
                    var parent = tree[i].parent;               	 
                   
                    if(parent != undefined && parent == 'ap'){
                    	tree[i].typefs = 'server';
                    }
                	if (tree[i].typefs.indexOf("server") != -1) {
                        nodeData[tree[i].typefs] += 1;
                        var device = new networkDevice(tree[i].typefs, tree[i].uid, tree[i].status,false,tree[i].parent)
                        that.networkTree[tree[i].typefs + "" + nodeData[tree[i].typefs]] = device;
                    } else {
                        nodeData[tree[i].typefs] += 1;
                        var device = networkDevice(tree[i].typefs, tree[i].uid, tree[i].status, true,tree[i].parent)
                        that.childDevices.push(device);
                    }
                }
                console.log(that.networkTree)
                for(var key in that.networkTree){
                  that.networkTree[key].addChildren()
                  treeJson.children.push(that.networkTree[key].device);
                }
                shallowTree=$.extend(true,{},treeJson);
                that.buildTree();

            }
        })
    },
    buildTree:function(){
      createMap(treeJson);
      $(".onlinefloor").hide();
      svgEvents();
    }
}

networkMap.getJson();

//$('.textImageGroup').css("transform", "rotate(90deg)");
function svgEvents(){
	$("svg").on("click tap", '.node g', function(e){
		
    	var xAxis = $(this).offset().left;
   	 	var yAxis = $(this).offset().top+30;
    	$("#uid").html($(this).attr("data-uid"));
    	$("#status").html($(this).attr("data-status"));
    	$(".powerBtn").attr("uid",$(this).attr("data-uid"))
    	$(".powerBtn").attr("devtype",$(this).attr("data-type"))
    	
    	if ($(this).attr("data-type") == "ap" || $(this).attr("data-type")=="sensor")
    		$('.network-popup').show().css({'left':xAxis,'top':yAxis})
	});
	$("svg").on("click tap", '.node circle', function(e){
    	$('.network-popup').hide()
	});
}
$(document).click(function (e)
{
    var container = $(".node g");
    if (!container.is(e.target) 
        && container.has(e.target).length === 0) 
    {
        $('.network-popup').hide()
    }
});
window.factoryObj={
    network:networkMap
}

