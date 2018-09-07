
var TopoMaker = function(topoconfig, jsondata) {
  this.topoconfig = {};
  this.apimageinfo = {
    width: 120,
    height: 120,
    yoffset: -20
  }
  this.apnametxtsize = 20;
  this.interneturl = "./img/cloud1.png";
  this.internetoffset = 75;

  if (!topoconfig) {
    this.topoconfig = {
      width: 500,
      height: 670,
    }
  }
  else
    this.topoconfig = topoconfig;
  this.jsondata = jsondata;



  //Assume the layout as 10 x 10 square layout
  this.layouts = [
    [[-2, 0, 3], [2, 0, 3]],
    [[0, -2, 1], [-2, 1, 4], [2, 1, 2]],
    [[-1.5, -1, 1], [1.5, -1, 1], [-1.5, 1, 3], [1.5, 1, 3]],
    [[-2, -1, 4], [2, -1, 2], [0, 0, 3], [-2, 1, 3], [2, 1, 3]],
  ]

  this.parseJSON(jsondata);
}

TopoMaker.prototype.line_maker = function(line, data, mul ){
  var x1=parseInt(d3.select(line).attr("x1"));
  var y1=parseInt(d3.select(line).attr("y1"));
  var x2=parseInt(d3.select(line).attr("x2"));
  var y2=parseInt(d3.select(line).attr("y2"));

  const obj = d3.interpolateObject({x:x1, y:y1}, {x: x2, y: y2});
  const width = 20;
  const height = 10;
  const theta = Math.atan((y2-y1)/(x2-x1));
  const xdelta = Math.sin(theta)*height;
  const ydelta = Math.cos(theta)*height;
  var m = Math.abs(Math.floor(Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1,2))/10));
  //var m = 31;
  var lines = [];
  for (i in d3.range(m))
  {
      var k = obj(i/(m-1));
      var n = Object.assign({},k);
      lines.push(n);
  }

  var svgline = d3.line()
  .x(function(d,i)
  {

      if(i%2==0)
          return d.x;
      if((i+1)%4 == 0)
          sign = 1;
      else
          sign = -1;
      var res = d.x + mul*sign*xdelta; 
      return res;
  })
  .y(function(d,i)
  {
      if(i%2==0)
          return d.y;
      var sign
      if((i+1)%4 == 0)
        sign = -1;
      else
        sign = 1;
      var y = d.y + mul*sign*ydelta;
      return y;
  })
  .curve(d3.curveBasis)
  return svgline(lines);
}

TopoMaker.prototype.drawwaveline = function(parent, classname, colorfn )
{
  var _this = this;
  var line1 = parent
      .append("svg:path")
        .attr("class", classname)
        
        .attr("stroke-width", "2")
        .attr("stroke", colorfn)
        .attr("fill", "none")
      .merge(parent)
        .attr("d", function(d,i) {
          return _this.line_maker(d3.selectAll('line')._groups[0][i],d, 1 ) 
        }
    )
  var line2 = parent
      .append("svg:path")
        .attr("class", classname)
        
        .attr("stroke-width", "4")
        .attr("stroke", colorfn)
        .attr("fill", "none")
        .merge(parent).attr("d", function(d,i) {
          return _this.line_maker(d3.selectAll('line')._groups[0][i],d, -1) 
        }
    )

 return [line1, line2];        
}

TopoMaker.prototype.getxcoord = function (x) {
    var xfac = this.topoconfig.width / 6
    var yfac = this.topoconfig.height / 5
    return (x * xfac) + this.topoconfig.width / 2
  }
  TopoMaker.prototype.getycoord = function (y) {
    var xfac = this.topoconfig.width / 6
    var yfac = this.topoconfig.height / 5
    return (y * yfac) + this.topoconfig.height / 2
  }

  TopoMaker.prototype.addLinesToLinks = function(links, strokewidth, stroke, color = null) {
    var _this = this;
    links
      .enter()
      .append("g")
      .attr("class", "link")
      .append("line")
        .attr("class", "link")
        .attr("stroke", (d) => color ? color : util.getSignalStrengthColor(d))
        .attr("stroke-width", strokewidth)
        .style("stroke-dasharray", stroke ? stroke : "")
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
        .attr("x1", function (l,) {
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

  //Set background rectangle for node text
  TopoMaker.prototype.initNodeNames = function(links) {
    links.selectAll("text").each(function (d, i) {
      d.bb = this.getBBox(); // get bounding box of text field and store it in texts array
      d.transform = d3.select(this).attr("transform");
    });

    var paddingLeftRight = 18; // adjust the padding values depending on font and font size
    var paddingTopBottom = 5;

    links.selectAll("rect").each(function (d, i) {
      d3.select(this).attr("x", function (d) { return d.bb.x; })
      d3.select(this).attr("y", function (d) { return d.bb.y; })
      d3.select(this).attr("width", function (d) { return d.bb.width + paddingLeftRight; })
      d3.select(this).attr("height", function (d) { return d.bb.height + paddingTopBottom; });
      d3.select(this).attr("fill", "white");
      d3.select(this).attr("transform", d.transform)
    })
  }


  TopoMaker.prototype.connectInternetHaul = function(svg, svgborder) {
    var _this = this;
    var offsets = {
      r2: 80,
      u1: 70,
    }
    svg.selectAll("image").each(function (d, i) {
      
      if (d.connectedtointernet == 1) {
        var bb = this.getBBox();
        var concolor = d.wanstatus == 'connected' ? 'green' : 'black';
        svgborder.select('path').each(function (l, i) {
          var cur = d3.select(this).attr("d");
          var y1 = _this.getycoord(d.y) + _this.internetoffset;
          var x1 = _this.getxcoord(d.x);
          var x2 = x1;
          var y2 = y1 - offsets.u1;
          var x5 = parseInt(cur.split(' ')[0].substr(1))
          var y5 = parseInt(cur.split(' ')[1])
          var x3 = x5 - offsets.r2;
          var y3 = y2;
          var x4 = x3;
          var y4 = y5;
          d3.select(this).attr("d", `M${x1} ${y1}, L${x2} ${y2 + 10}, Q${x2} ${y2}, ${x2 + Math.sign(x3-x2)*10} ${y2},  L${x3 +Math.sign(x2-x3)*10} ${y3}, Q${x3} ${y3}, ${x3} ${y3 - 10}, L${x4} ${y4 + 10}, Q${x4} ${y4}, ${x4 +Math.sign(x5-x4)*10} ${y4}, L${x5} ${y5}`);
          d3.select(this).attr("stroke", concolor)
        })
      }
    })
  }

  TopoMaker.prototype.parseJSON = function()
  {
    this.topodata = {
      nodes: [],
      links: []
    }

    var devices = this.jsondata.topology;
    var layout = this.layouts[devices.length - 2];
    devices.forEach((ap, i) => {
      var nodedata = {
        id: ap.uid, name: ap.location, x: layout[i][0], y: layout[i][1], textpos: layout[i][2],
        connectedtointernet: ap.wan_backhaul ? true : false, txrx: 0, noofstream: ap.no_of_streams ? ap.no_of_streams : 2
      }
      this.topodata.nodes.push(nodedata)
      ap.mesh_links.forEach((l, i) => {
        var exist = false;
        nodedata.txrx += (l._mesh_tx_bytes + l._mesh_rx_bytes)
        //Check the links already exists
        this.topodata.links.forEach(link => {
          if ((link.source == ap.uid && link.target == l.uid) || (link.target == ap.uid && link.source == l.uid)) {
            exist = true;
            if (link.source == ap.uid && link.target == l.uid)
              link.ssst = l.signal_strength_percent
            else
              link.ssts = l.signal_strength_percent
          }
        })

        if (!exist)
          this.topodata.links.push({ source: ap.uid, target: l.uid, ssst: l.signal_strength_percent })
      })
    });
  }

  

  TopoMaker.prototype.draw = function(divid, redraw){
    var _this = this;
       
    _this.divid = divid;
    var chartDiv = document.getElementById(_this.divid.split('#')[1]);
    var width = chartDiv.clientWidth;
    var height = 0.45*width;

    _this.topoconfig = {
      width,
      height,
    }

    if(!redraw)
    {
      _this.div = d3.select("body").append("div")
        .attr("class", "tooltip")
        .style("opacity", 0);
      svg = d3.select(divid)
        .append("svg");
      _this.svg = svg;
    }
      

    _this.svg.attr("width", _this.topoconfig.width)
             .attr("height", _this.topoconfig.height);
    
    
    if(!redraw)
    {
      _this.svg.append("svg:path")
        .attr("class", "path")
        .attr("d", `M${_this.topoconfig.width / 2} ${_this.topoconfig.height / 6.5}`)
        .attr("stroke-width", "4")
        .attr("stroke", 'blue')
        .attr("fill", "none")

      _this.internet = _this.svg.append("image")
        .attr("xlink:href", _this.interneturl)
        .attr("class", "internetimage")
        .attr("width", 100)
        .attr("height", 100);

      _this.svgborder = _this.svg;
      _this.svg = _this.svg.append("g")
      .attr("transform", `translate(0,${_this.internetoffset})`);
    }

    _this.internet.attr("x", (_this.topoconfig.width / 2) - 50)
                  .attr("y", 10)
     

    
    var links = _this.svg.selectAll("link")
      .data(_this.topodata.links)

    
     
    _this.addLinesToLinks(links, "10", null, "black");
    //_this.drawwaveline(links, 'wave', (d) => { return util.getSignalStrengthColor(d) });

    /*
    var nodes = _this.svg.selectAll("node")
      .data(_this.topodata.nodes)
      .enter()
      .append("g")
      .attr("class", "node");
    if(!redraw)
      nodes.append("rect");

    nodes.append("text")
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
      .merge(nodes).attr("x", n => _this.getxcoord(n.x))
      .attr("y", n => _this.getycoord(n.y))
      .attr("transform", function (d) {
        switch (d.textpos) {
          case 1:
            return `translate(0,-${(_this.apimageinfo.height / 2) + 30})`;
          case 2:
            return `translate(${_this.apimageinfo.width},0)`;
          case 3:
            return `translate(0,${_this.apimageinfo.height / 2})`;
          case 4:
            return `translate(-${_this.apimageinfo.width / 2},0)`;
        }
      })
      

    nodes.append("image")
      .attr("xlink:href", function (d) { return util.getImageForAP(d.noofstream) })
      .attr("class", "apimage")
      .attr("width", _this.apimageinfo.width)
      .attr("height", _this.apimageinfo.height)
    .merge(nodes)
      .attr("x", n => _this.getxcoord(n.x) - _this.apimageinfo.width / 2)
      .attr("y", n => _this.getycoord(n.y) - (_this.apimageinfo.width / 2) + _this.apimageinfo.yoffset)
    
     
    _this.connectInternetHaul(_this.svg, _this.svgborder);
    _this.initNodeNames(nodes);
    */
  }

