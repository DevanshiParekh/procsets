var TopoMaker = function (cid, divid) {
  this.divid = divid;
  this.apimageinfo = {
    width: 120,
    height: 120,
    yoffset: -20
  }
  this.apnametxtsize = 20;
  this.interneturl = "/facesix/static/qubercomm/images/nmesh/cloud1.png";
  this.internetoffset = -4;
  this.cid = cid;

  //Assume the layout as 10 x 10 square layout
  this.layouts = [
    [[0, 0, 3]],
    [[-2, 0, 3], [2, 0, 3]],
    [[0, -0.5, 2], [-2, 1.2, 3], [2, 1.2, 3]],
    [[-1.5, -1, 1], [1.5, -1, 1], [-1.5, 1, 3], [1.5, 1, 3]],
    [[-2, -1, 4], [2, -1, 2], [0, 0, 3], [-2, 1, 3], [2, 1, 3]],
  ]

  this.clearPopup();
}

//Create line for wave from the start point and end point of singel link
//Mul used to shift the phase by 180 degree. 
//Create zig zag line /\/\. Then smooth using curve function.
//Return the points for the path.
TopoMaker.prototype.line_maker = function (data, mul) {
  var _this = this;
  var sourceNode = _this.topodata.nodes.filter(function (d, i) {
    return d.id == data.source
  })[0];
  var y1 = _this.getycoord(sourceNode.y)
  var x1 = _this.getxcoord(sourceNode.x)

  var targetNode = _this.topodata.nodes.filter(function (d, i) {
    return d.id == data.target
  })[0];
  var y2 = _this.getycoord(targetNode.y)
  var x2 = _this.getxcoord(targetNode.x)

  const obj = d3.interpolateObject({ x: x1, y: y1 }, { x: x2, y: y2 });
  const width = 20;
  const height = 10;
  const theta = Math.atan((y2 - y1) / (x2 - x1));
  const xdelta = Math.sin(theta) * height;
  const ydelta = Math.cos(theta) * height;
  var m = Math.abs(Math.floor(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) / 10));
  //var m = 31;
  var lines = [];
  for (i in d3.range(m)) {
    var k = obj(i / (m - 1));
    var n = Object.assign({}, k);
    lines.push(n);
  }

  var svgline = d3.line()
    .x(function (d, i) {

      if (i % 2 == 0)
        return d.x;
      if ((i + 1) % 4 == 0)
        sign = 1;
      else
        sign = -1;
      var res = d.x + mul * sign * xdelta;
      return res;
    })
    .y(function (d, i) {
      if (i % 2 == 0)
        return d.y;
      var sign
      if ((i + 1) % 4 == 0)
        sign = -1;
      else
        sign = 1;
      var y = d.y + mul * sign * ydelta;
      return y;
    })
    .curve(d3.curveBasis)
  return svgline(lines);
}

//Draw single wave line for a link
//Class name used select the wave line
//Each link has two wave line shifted by 180 deg
//This function create one singel wave line based on sign
TopoMaker.prototype.drawwaveline = function (links, classname, colorfn, sign) {
  var _this = this;
  links
    .enter()
    .append("svg:path")
    .attr("class", classname)
    .attr("stroke-width", () => { return sign == -1 ? "2" : "4" })
    .attr("fill", "none")
    .merge(links)
    .attr("d", function (d, i) {
      return _this.line_maker(d, sign)
    })
    .attr("stroke", colorfn)
}

TopoMaker.prototype.getxcoord = function (x) {
  var xfac = this.topoconfig.width / 6
  return (x * xfac) + this.topoconfig.width / 2
}
TopoMaker.prototype.getycoord = function (y) {
  var yfac = this.topoconfig.height / 6
  return (y * yfac) + this.topoconfig.height / 2
}

TopoMaker.prototype.clear = function(){
    $('div#topology')[0].innerHTML = "";
}
//Draw thick line as background for the wave line
//This is required to have mouseover popup
TopoMaker.prototype.addLinesToLinks = function (links, strokewidth, stroke, color = null) {
  var _this = this;
  links
    .enter()
    .append("line")
    .attr("class", "link")
    .attr("stroke", (d) => color ? color : util.getSignalStrengthColor(d))
    .attr("stroke-width", strokewidth)
    .style("stroke-dasharray", stroke ? stroke : "")
    .style("opacity", 0)
    .on("mousemove", function(d) {
      _this.lastmove=new Date().getTime();
    })
    .on("mouseover", function (d) {
      _this.div.transition()
        .duration(200)
        .style("opacity", .9);
      _this.div.html('Singnal Strength:' + (d.ssst + d.ssts) / 2.0 + '%' + "<br/>Band: 2.4Ghz")
        .style("left", (d3.event.pageX) + "px")
        .style("top", (d3.event.pageY - 28) + "px");
    })
    .on("mouseout", function (d) {
      _this.div.style("opacity", 0);
    })
    .merge(links)
    .attr("x1", function (l) {
      var sourceNode = _this.topodata.nodes.filter(function (d, i) {
        return d.id == l.source
      })[0];
      d3.select(this).attr("y1", _this.getycoord(sourceNode.y));
      return _this.getxcoord(sourceNode.x)
    })
    .attr("x2", function (l) {
      var targetNode = _this.topodata.nodes.filter(function (d, i) {
        return d.id == l.target
      })[0];
      d3.select(this).attr("y2", _this.getycoord(targetNode.y));
      return _this.getxcoord(targetNode.x)
    })

}

//Set background rectangle for AP text
TopoMaker.prototype.initNodeNames = function () {
  var _this = this;
  _this.svg.selectAll(".aptext").each(function (d, i) {
    d.bb = this.getBBox(); // get bounding box of text field and store it in texts array
    d.transform = d3.select(this).attr("transform");
  });

  var paddingLeftRight = 18; // adjust the padding values depending on font and font size
  var paddingTopBottom = 5;

  _this.svg.selectAll(".aptextrect").each(function (d, i) {
    d3.select(this).attr("x", function (d) { return d.bb.x; })
    d3.select(this).attr("y", function (d) { return d.bb.y; })
    d3.select(this).attr("width", function (d) { return d.bb.width + paddingLeftRight; })
    d3.select(this).attr("height", function (d) { return d.bb.height + paddingTopBottom; });
    d3.select(this).attr("transform", d.transform)
  })
}


TopoMaker.prototype.connectInternetHaul = function (svg, svgborder) {
  var _this = this;
  var offsets = {
    r2: 80,
    u1: 70,
  }
  svg.selectAll(".apimage").each(function (d, i) {

    if (d.connectedtointernet == 1) {
      var bb = this.getBBox();
      var concolor = d.wanstatus == 'connected' ? 'green' : 'black';
      svgborder.select('.internetconn').each(function (l, i) {
        var cur = d3.select(this).attr("d");
        var y1 = _this.getycoord(d.y);
        var x1 = _this.getxcoord(d.x);
        var x2 = x1;
        var y2 = y1 - offsets.u1;
        var x5 = parseInt(cur.split(' ')[0].substr(1))
        var y5 = parseInt(cur.split(' ')[1])
        var x3 = x5 - offsets.r2;
        var y3 = y2;
        var x4 = x3;
        var y4 = y5;
        d3.select(this).attr("d", `M${x1} ${y1}, L${x2} ${y2 + 10}, Q${x2} ${y2}, ${x2 + Math.sign(x3 - x2) * 10} ${y2},  L${x3 + Math.sign(x2 - x3) * 10} ${y3}, Q${x3} ${y3}, ${x3} ${y3 - 10}, L${x4} ${y4 + 10}, Q${x4} ${y4}, ${x4 + Math.sign(x5 - x4) * 10} ${y4}, L${x5} ${y5}`);
        d3.select(this).attr("stroke", concolor)
      })
    }
  })
}

TopoMaker.prototype.clearPopup = function() {
  var _this = this;
  if(!_this.inverval){
    _this.interval = setInterval(()=>{
      var curTime = new Date().getTime();
      if(curTime - _this.lastmove > 3000)
      {
        _this.div.style("opacity", 0);
      }
    }, 3000)
  }
}
TopoMaker.prototype.parseJSON = function () {
  this.topodata = {
    nodes: [],
    links: []
  }

  var devices = this.jsondata.topology;
  var layout = this.layouts[devices.length - 1];

  var arrangeddevices = [];
  devices.forEach((ap, i) => {
    if(ap.wan_backhaul){
      arrangeddevices.push(ap);
    }
  })
  devices.forEach((ap, i) => {
    if(!ap.wan_backhaul){
      arrangeddevices.push(ap);
    }
  })

  arrangeddevices.forEach((ap, i) => {
    var nodedata = {
      id: ap.uid, state:ap.state, name: ap.location, x: layout[i][0], y: layout[i][1], textpos: layout[i][2],
      connectedtointernet: ap.wan_backhaul ? true : false, txrx: 0, noofstream: ap.no_of_streams ? ap.no_of_streams : 2
    }
    this.topodata.nodes.push(nodedata)
    ap.mesh_links.forEach((l, i) => {
      var exist = false;
      nodedata.txrx += (l._mesh_tx_bytes + l._mesh_rx_bytes)
      //Check the links already exists
      this.topodata.links.forEach(link => {
        if ((link.source == ap.uid && link.target == l.mesh_mac) || (link.target == ap.uid && link.source == l.mesh_mac)) {
          exist = true;
          if (link.source == ap.uid && link.target == l.mesh_mac)
            link.ssst = l.signal_strength_percent
          else
            link.ssts = l.signal_strength_percent
        }
      })

      if (!exist)
        this.topodata.links.push({ source: ap.uid, target: l.mesh_mac, ssst: l.signal_strength_percent })
    })
  });
}



TopoMaker.prototype.draw = function (redraw, jsondata) {
  var _this = this;
  if(!jsondata)
    return;
  _this.jsondata = jsondata;

  _this.parseJSON();

  var chartDiv = document.getElementById(_this.divid.split('#')[1]);
  var width = chartDiv.clientWidth;
  var height = 0.5 * width;

  _this.topoconfig = {
    width,
    height,
  }

  if (!redraw) {
    _this.div = d3.select("body").append("div")
      .attr("class", "tooltip")
      .style("opacity", 0);
    svg = d3.select(_this.divid)
      .append("svg");
    _this.svg = svg;
  }


  _this.svg.attr("width", _this.topoconfig.width)
    .attr("height", _this.topoconfig.height);


  if (!redraw) {
    _this.internetconn = _this.svg.append("svg:path")
      .attr("class", "internetconn")
      .attr("stroke-width", "4")
      .attr("stroke", 'blue')
      .attr("fill", "none")

    _this.internet = _this.svg.append("image")
      .attr("xlink:href", _this.interneturl)
      .attr("class", "internetimage")
      .attr("width", 75)
      .attr("height", 75);

    _this.svgborder = _this.svg;
    _this.svg = _this.svg.append("g")
      .attr("transform", "translate(0,0)");
  }
  _this.internetconn.attr("d", `M${_this.topoconfig.width / 2} ${75/2}`)
  _this.internet.attr("x", (_this.topoconfig.width / 2) - 50)
    .attr("y", -5)



  
  var linkwaves1 = _this.svg.selectAll(".wavelink1")
    .data(_this.topodata.links)

  var links = _this.svg.selectAll(".link")
    .data(_this.topodata.links)

  _this.drawwaveline(linkwaves1, 'wavelink1', (d) => { return util.getSignalStrengthColor(d) }, 1);

  var linkwaves2 = _this.svg.selectAll(".wavelink2")
    .data(_this.topodata.links)
  _this.drawwaveline(linkwaves2, 'wavelink2', (d) => { return util.getSignalStrengthColor(d) }, -1);

  _this.addLinesToLinks(links, "10", null, "navajowhite");


  var apimage = _this.svg.selectAll(".apimage")
    .data(_this.topodata.nodes)
  
  var apstate = _this.svg.selectAll(".apstate")
    .data(_this.topodata.nodes)


  var aptext = _this.svg.selectAll(".aptext")
    .data(_this.topodata.nodes)


  var aptextrect = _this.svg.selectAll(".aptextrect")
    .data(_this.topodata.nodes)


  aptextrect
    .enter().append("rect")
    .attr('class', 'aptextrect')
    .attr("fill", "white")


  aptext
    .enter().append("text")
    .attr('class', 'aptext')
    .text(function (d) { return d.name })
    .attr("text-anchor", function (d) {
      switch (d.textpos) {
        case 1:
          return "middle";
        case 2:
          return "start";
        case 3:
          return "middle";
        case 4:
          return "end"
      }
    })
    .attr("font-weight", 700)
    .attr("font-size", 20)
    .merge(aptext)
    .attr("x", n => _this.getxcoord(n.x))
    .attr("y", n => _this.getycoord(n.y))
    .attr("transform", function (d) {
      switch (d.textpos) {
        case 1:
          return `translate(0,-${(_this.apimageinfo.height / 2) + 30})`;
        case 2:
          return `translate(${_this.apimageinfo.width / 2 + 10},0)`;
        case 3:
          return `translate(0,${_this.apimageinfo.height / 2})`;
        case 4:
          return `translate(-${_this.apimageinfo.width / 2},0)`;
      }
    })


  apimage
    .enter().append("image")
    .attr("xlink:href", function (d) { return util.getImageForAP(d.noofstream) })
    .attr("class", "apimage")
    .attr("width", _this.apimageinfo.width)
    .attr("height", _this.apimageinfo.height)
    .attr("cursor", "pointer")
    .on('click', function (d) {
      window.location = '/facesix/device_details?cid='+_this.cid+'&uid='+d.id;;
    })
  .merge(apimage)
    .attr("x", n => _this.getxcoord(n.x) - _this.apimageinfo.width / 2)
    .attr("y", n => _this.getycoord(n.y) - (_this.apimageinfo.width / 2) + _this.apimageinfo.yoffset)
  .append("svg:title")
    .text(function (d, i) { return "Click to view device statistics"; })

  apstate
    .enter().append("image")
    .attr("xlink:href", "/facesix/static/qubercomm/images/nmesh/inactive_symbol.png")
    .attr("width", 30)
    .attr("height", 30)
    .attr('class', 'apstate')
    .style("opacity", (d)=>d.state == 'inactive'?1:0)
  .merge(apstate)
    .attr("x", n => _this.getxcoord(n.x)+(_this.apimageinfo.width / 2))
    .attr("y", n => _this.getycoord(n.y)+((_this.apimageinfo.height-110) / 2))
  .append("svg:title")
    .text((d)=>d.state == 'inactive'?'Inactive':'');
 
  _this.connectInternetHaul(_this.svg, _this.svgborder);
  _this.initNodeNames();

}

