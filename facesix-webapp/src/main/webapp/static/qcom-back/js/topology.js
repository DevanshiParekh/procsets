app.directive('networktopology', ['$http', '$window', '$routeParams', '$templateRequest', '$compile', function ($http, $window, $routeParams, $templateRequest, $compile) {
    function link(scope, element, attrs) {
		$templateRequest("/facesix/web/site/portion/topcfg").then(function(html){
			
		      var template = angular.element(html);
		      element.append(template);
		      $compile(template)(scope);
		      
		      createview();
		   });
		function createview(){
        var treeJson = {
            "name": "Floor",
            "spid": "",
            "type": "floor",
            "status": "active",
            "children": [],
            "uid":""
        };
        
		var topView = document.getElementById("topconfig");
        function createMap(treejson) {
        

            var m = [20, 20, 20, 0],
                w = topView.clientWidth - 20,
                h = topView.clientWidth,
                i = 0,
                wx = (w / 5),
                root;
            var tree = d3.layout.tree()
                .size([h, w]);

            var diagonal = d3.svg.diagonal()
                .projection(function (d) {
                    return [d.x, d.y];
                });

            var vis = d3.select(topView).append("svg:svg")
                .attr("width", w)
                .attr("height", h)
                .attr("id","svg")
                .append("svg:g")
                .attr("transform", "translate(" + m[3] + "," + m[0] + ")");
            var setSVG = document.getElementById("svg");
            console.log(setSVG);
            setSVG.setAttribute("height", "1336");
            root = treejson;
            root.x0 = h / 2;
            root.y0 = 0;

            function toggleAll(d) {
                if (d.children) {
                    d._children = d.children;
                    d._children.forEach(toggleAll);
                    d.children = null;
                }
            }
            root.children.forEach(toggleAll);
            update(root);

            function getColor(status) {
                if (status == "active") {
                    return "#5cb85c";
                } else if (status == "idle") {
                    return "#f0ad4e";
                } else if (status == "inactive") {
                    return "#d9534f";
                }
            }
            function update(source) {
                var imageurl = "http://cloud.qubercomm.com/facesix/static/qubercomm/images/networkmap/";
                var duration = d3.event && d3.event.altKey ? 5000 : 500;

                // Compute the new tree layout.
                var nodes = tree.nodes(root).reverse(),
                    links = tree.links(nodes);

                // Normalize for fixed-depth.
                nodes.forEach(function (d) {
                    d.y = d.depth * 160;
                });

                // Update the nodes…
                var node = vis.selectAll("g.node")
                    .data(nodes, function (d) {
                        return d.id || (d.id = ++i);
                    });

                // Enter any new nodes at the parent's previous position.
                var nodeEnter = node.enter().append("svg:g")
                    .attr("class", "node")
                    .attr("transform", function (d) {
                        return "translate(" + source.x0 + "," + source.y0 + ")";
                    })
                    .attr("data-uid", function (d) {
                        return d.uid
                    })
                    .attr("data-status", function (d) {
                        return d.status
                    })
                    .on("click", function (d) {
                        toggle(d);
                        update(d);
                    });

                nodeEnter.append("svg:circle")
                    .attr("r", 1e-6)
                    .style("fill", function (d) {
                        return d._children ? getColor(d.status) : "#fff";
                    })
                    .style("stroke", function (d) {
                        return d._children ? getColor(d.status) : getColor(d.status);
                    });

                nodeEnter.append("svg:text")
                    .attr("x", function (d) {
                        if (d.name != "Floor") {
                            return d.children || d._children ? (-15 - 18) : (18 + 18);
                        } else {
                            return d.children || d._children ? -15 : 15;
                        }
                    })
                    .attr("dy", ".35em")
                    .attr("text-anchor", function (d) {
                        return d.children || d._children ? "end" : "start";
                    })
                    .text(function (d) {
                        return d.name;
                    })
                    .style('fill', function (d) {
                        return d.free ? 'black' : '#999';
                    })
                    .style("fill-opacity", 1e-6);

                nodeEnter.append("svg:image")
                    .attr("x", function (d) {
                        return d.children || d._children ? -30 : 13;
                    })
                    .attr("y", function (d) {
                        return d.children || d._children ? -9 : -9;
                    })
                    .attr('width', "18px")
                    .attr('height', "18px")
                    .attr('xlink:href', function (d) {
                        if (d.name != "Floor")
                            return imageurl + d.name + "-" + d.status + ".png"
                    })
                    .attr('class', function (d) {
                        return d.status + d.name
                    })


                nodeEnter.append("svg:title")
                    .text(function (d) {
                        return d.description;
                    });

                // Transition nodes to their new position.
                var nodeUpdate = node.transition()
                    //                    .duration(duration)
                    .attr("transform", function (d) {
                        return "translate(" + d.x + "," + d.y + ")";
                    });

                nodeUpdate.select("circle")
                    .attr("r", 8)
                    .style("fill", function (d) {
                        return d._children ? getColor(d.status) : "#fff";
                    })
                    .style("stroke", function (d) {
                        return d._children ? getColor(d.status) : getColor(d.status);
                    });

                nodeUpdate.select("text")
                    .style("fill-opacity", 1);

                // Transition exiting nodes to the parent's new position.
                var nodeExit = node.exit().transition()
                    //                    .duration(duration)
                    //                    .attr("transform", function (d) {
                    //                        return "translate(" + source.x + "," + source.y + ")";
                    //                    })
                    .remove();

                nodeExit.select("circle")
                    .attr("r", 1e-6);

                nodeExit.select("text")
                    .style("fill-opacity", 1e-6);

                // Update the links…
                var link = vis.selectAll("path.link")
                    .data(tree.links(nodes), function (d) {
                        return d.target.id;
                    });

                // Enter any new links at the parent's previous position.
                link.enter().insert("svg:path", "g")
                    .attr("class", "link green")
                    .attr("d", function (d) {
                        var o = {
                            x: source.x0,
                            y: source.y0
                        };
                        return diagonal({
                            source: o,
                            target: o
                        });
                    })
                    .transition()
                    .duration(duration)
                    .style("stroke", "green")
                    .attr("d", diagonal);

                // Transition links to their new position.
                link.transition()
                    .duration(duration)
                    .attr("d", diagonal)
                    .style("stroke", function (d) {
                        return d._children ? getColor(d.status) : getColor(d.status);
                    });

                // Transition exiting nodes to the parent's new position.
                link.exit().transition()
                    .duration(duration)
                    .attr("d", function (d) {
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
                nodes.forEach(function (d) {
                    d.x0 = d.x;
                    d.y0 = d.y;
                });
                var textId = vis.selectAll("text");
                //                function textData(){
                //                    
                //                }
                //                node.on("mousedown", function (d) {
                //                    var g = d3.select(this); // The node
                //                    console.log(g);
                //                });
                node.on("contextmenu", function (d) {
                    d3.event.preventDefault();
                    var g = d3.select(this); // The node
                    console.log(d);

                    if (d.status == "active") {
                        var className = "success",
                            varName = "Active";
                    } else if (d.status == "inactive") {
                        var className = "danger",
                            varName = "In Active";
                    } else if (d.status == "idle") {
                        var className = "warning",
                            varName = "Idle";
                    }
					if(d.name == "ap" || d.name == "sensor"){
	                    	console.log('ok');
	                    
                    
                    	var setCTM = document.getElementById("ctm");

	                    function ctm() {
	                        setCTM.innerHTML = "<div class='box'>" +
	                            "<li><a>" + d.name + "</a><span class='fr label label-" + className + "'>" + varName + "</span></li><li class='bb'><a href=''>" + g.attr("data-uid") + "</a></li>" +
	                            "</li><li><a href='' id='item'><i class='fa fa-undo'></i>  Reset</a></li></div>";

	                        setCTM.style.top = d.y + "px";
	                        setCTM.style.left = d.x + 20 + "px";
	                        setCTM.style.display = "block";

	                    }
	                    
	                    ctm();
                    
                    
                    
                    	var setCTM = document.getElementById("ctm");
                    	var item = document.getElementById("item");
                    var itemYes, itemNo;
                    console.log(item);

                    function confirmCTM() {
                        console.log(setCTM);
                        setCTM.innerHTML = "<div class='box' style='height:120px'>" +
                            "<li><a>" + d.name + "</a><span class='fr label label-" + className + "'>" + varName + "</span></li><li class='bb'><a href=''>" + g.attr("data-uid") + "</a></li>" +
                            "</li><li class='f12'>Are you Sure You Want to Reset</li><li class='tc'><a class='fl' id='itemYes'><i class='fa fa-check'></i> Yes</a><a id='itemNo' class='fr'><i class='fa fa-times'></i> No</a></li></div>";

                        confirmedCTM();
                    }
                    

                    
                    item.addEventListener("mousedown", function () {
                        confirmCTM();
                    });

                    function confirmedCTM() {
                    console.log('ok');
                        itemYes = document.getElementById("itemYes");
                        itemNo = document.getElementById("itemNo");
                        itemYes.addEventListener("mousedown", function () {
                        	$http.post('/facesix/rest/device/rpc?args=none&uid='+d.uid+'&ap=?&mac=?&cmd=RESET').then(function successCallback(response){console.log(response);}, function errorCallback(response){console.log(response);});
                        	 setCTM.style.display = "none";
                            setCTM.innerHTML = ""
                        });
                        itemNo.addEventListener("mousedown", function () {
                            setCTM.style.display = "none";
                            setCTM.innerHTML = ""
                        });
                    } }
                });
            }

            // Toggle children.
            function toggle(d) {
                if (d.children) {
                    d._children = d.children;
                    d.children = null;
                } else {
                    d.children = d._children;
                    d._children = null;
                }
            }

            setSVG.addEventListener("mousedown", function (e) {
                
                var setCTM = document.getElementById("ctm");
                setCTM.style.display = "none";
            });
        }

        function buildHierarchy(arr) {

            var tree = [],
                mappedArr = {},
                arrElem,
                mappedElem;

            // First map the nodes of the array to an object -> create a hash table.
            for (var i = 0, len = arr.length; i < len; i++) {
                arrElem = arr[i];
                mappedArr[arrElem.uid] = arrElem;
                mappedArr[arrElem.uid]['children'] = [];
            }


            for (var id in mappedArr) {
                if (mappedArr.hasOwnProperty(id)) {
                    mappedElem = mappedArr[id];
                    // If the element is not at the root level, add it to its parent array of children.
                    if (mappedElem.parentid) {
                        mappedArr[mappedElem['parentid']]['children'].push(mappedElem);
                    }
                    // If the element is at the root level, add it to first level elements array.
                    else {
                        tree.push(mappedElem);
                    }
                }
            }
            return tree;
        }

        function deleteEmpty(arry) {

            for (var i = 0; i < arry.length; i++) {
                if (arry[i].children.length > 0) {
                    deleteEmpty(arry[i].children);
                } else {
                    delete arry[i].children;
                }
            }

        }

        function init() {
            //            var topView = element[0];
         $http.get('/facesix/rest/site/portion/networkdevice/list?spid='+$routeParams.spid).then(function successCallback(response) {
//            $http.get('topology.json').then(function successCallback(response) {
                var data = response.data;
                var treeData = [];
                var netWorkTree = {
                    server: [],
                    switch: [],
                    ap: [],
                    sensor: []
                };
                for (var i = 0; i < data.length; i++) {
                    if (data[i].typefs == "server") {
                        netWorkTree.server.push(data[i]);
                    } else if (data[i].typefs == "switch") {
                        netWorkTree.switch.push(data[i]);
                    } else if (data[i].typefs == "ap") {
                        netWorkTree.ap.push(data[i]);
                    } else if (data[i].typefs == "sensor") {
                        netWorkTree.sensor.push(data[i]);
                    }
                    
                    var construct = {
                        name: data[i].typefs,
                        uid: data[i].uid,
                        status: data[i].status,
                        parentid: data[i].parent
                    }
                    treeData.push(construct);
                }
                netWorkTree.serverCount = netWorkTree.server.length;
                netWorkTree.switchCount = netWorkTree.switch.length;
                netWorkTree.apCount = netWorkTree.ap.length;
                netWorkTree.sensorCount = netWorkTree.sensor.length;
                $window.localStorage.setItem("netWorkTree", JSON.stringify(netWorkTree));
                console.log(netWorkTree);
                var tree = buildHierarchy(treeData);
                deleteEmpty(tree);
                treeJson = {
			            "name": "Floor",
			            "spid": "",
			            "type": "floor",
			            "status": "active",
			            "children": [],
			            "uid":""
			     };
                treeJson.children = tree;
                $window.localStorage.setItem("treeJson", JSON.stringify(treeJson));
                console.log(treeJson);
                createMap(treeJson);
                //createMap(response.data);
            }, function errorCallback(response) {console.log(response);});
        }

        init();

        angular.element($window).bind('resize', function () {
        var topView = document.getElementById("topconfig");
            topView.removeChild(topView.childNodes[1]);
            if ($window.localStorage.getItem("treeJson")) {
                var treeJson = JSON.parse($window.localStorage.getItem("treeJson"));
                createMap(treeJson);
            } else {
                $window.location.reload();
            }
            scope.$digest();
        });


        //Scope Functions
        scope.head = {};
        scope.head.title = "Network Config - Topology"
        scope.dataserver = {};
        scope.dataserver.type = "server";
        scope.dataswitch = {};
        scope.dataswitch.type = "switch";
        scope.dataap = {};
        scope.dataap.type = "ap";
        scope.datadevice = {};
        scope.datadevice.type = "sensor";
		scope.servers = [];
		scope.switchs = [];
		scope.aps = [];
		scope.sensors = [];


        
        scope.serveropen = function () {
            if (scope.serveropened) {
                scope.serveropened = !scope.serveropened;
            } else {
                scope.allFalse();
                scope.serveropened = !scope.serveropened;
            }
        }
        scope.switchopen = function () {

            if (scope.switchopened) {
                scope.switchopened = !scope.switchopened;
            } else {
                scope.allFalse();
                scope.switchopened = !scope.switchopened;
            }
        }
        scope.apopen = function () {
            if (scope.apopened) {
                scope.apopened = !scope.apopened;
            } else {
                scope.allFalse();
                scope.apopened = !scope.apopened;
            }
        }
        scope.deviceopen = function () {
            if (scope.deviceopened) {
                scope.deviceopened = !scope.deviceopened;
            } else {
                scope.allFalse();
                scope.deviceopened = !scope.deviceopened;
            }
        }

        scope.checkServer = function () {
            if ($window.localStorage.getItem("netWorkTree")) {
                var netWorkTree = JSON.parse($window.localStorage.getItem("netWorkTree"));
                scope.servers = netWorkTree.server;
                console.log(scope.servers[0].uid);
                scope.switchs = netWorkTree.switch;
                scope.aps = netWorkTree.ap;
                scope.sensors = netWorkTree.sensor;

                if (netWorkTree.serverCount == 1) {
                    scope.showSwitch = true;
                    scope.needServer = false;
                } else {
                    scope.showSwitch = true;
                    scope.needServer = true;
                }
                if (netWorkTree.switchCount == 1) {
                    scope.showAp = true;
                    scope.needSwitch = false;
                } else {
                    scope.showAp = true;
                    scope.needSwitch = true;
                }
                if (netWorkTree.apCount == 1) {
                    scope.showDevice = true;
                    scope.needAp = false;
                } else {
                    scope.showDevice = true;
                    scope.needAp = true;
                }
            }
        }
        scope.allFalse = function () {
            scope.serveropened = false;
            scope.switchopened = false;
            scope.apopened = false;
            scope.deviceopened = false;
            scope.checkServer();
        }
        scope.allFalse();
        scope.data = {};

        scope.newDevice = function (data) {
            if (data.type == "server") {
                data.parent = "";
            }
            if (data.idtypeval == "Y"){
            	var valUid = data.devuid;
            } else{
            	var valUid = data.devuids;
            	}
            console.log(data);
            var shape = {
            	type: data.type,
                typefs: data.type,
                xposition: "",
                yposition: "",
                status: "inactive",
                sid: $routeParams.sid,
                spid: $routeParams.spid,
                devname: data.devname,
                uid: valUid,
                parent: data.parent,
                band: "",
                gparent: ""
            };
            function checkUnd(val){
            	if(val == undefined){
            		var inputVal = "";
            		return inputVal;
            	} else {
            		return parseInt(val);
            	}
            	
            }
            if(shape.type == "ap"){
            	shape.band = checkUnd(data.band);
            	shape.alias = checkUnd(data.alias);
		   shape.ssid = checkUnd(data.ssid);
		   shape.vap = checkUnd(data.vap);
	       shape.band5 = checkUnd(data.band5);
	       shape.guest = checkUnd(data.guest);
	       shape.ch2g = checkUnd(data.ch2g);
	       shape.ch5g = checkUnd(data.ch5g);
            }
            console.log(shape);
            $http({
                method: 'post',
                url: '/facesix/rest/site/portion/networkdevice/save',
                data: JSON.stringify(shape),
            }).then(function successCallback(response) {
            $window.location.reload();
                //topView.removeChild(topView.childNodes[1]);
                //init();
                //console.log(response);
                scope.allFalse();
            }, function errorCallback(response) {
                console.log(response);
            });
        }
        
       
        }
        
    }
    return {
        restrict: 'E',
        replace: true,
        scope: {
            data: '='
        },
        link: link
        //        template: '<div class="networkTopology"><ul id="ctm" class="canvas-context spl"><div class="box"></div></ul></div>'
        //templateUrl: '/facesix/web/site/portion/topcfg'
    };
}]);

app.directive("macvalid", function(){
	function link(scope, element, attrs, ngModel){
			
			function formatMAC() {
				if(scope.ngModel != undefined){
				var input = scope.ngModel;
			    var r = /([a-f0-9]{2})([a-f0-9]{1})/i,
			        str = input.replace(/[^a-f0-9]/ig, "");
			    console.log(r.test(str));
			    while (r.test(str)) {
			        str = str.replace(r, '$1' + ':' + '$2');
			    }
				scope.ngModel = str.slice(0, 17);
			    //e.target.value = scope.ngModel;
			    ngModel.$render();
			    }
			};
			scope.$watch('ngModel', function(){
				formatMAC();
			});
	}

	return {
		restrict: 'EA',
		require:'ngModel',
		scope:{
		ngModel: '='
			},
        link: link
	}
});













