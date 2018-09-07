var NMeshDeviceSummary = function (cid, uid, config) {
  this.cid = cid;
  //Contains div id for all chart
  //Chart x width for flow charts
  this.config = config
}

//Used when switch device
NMeshDeviceSummary.prototype.clearDynamicCharts = function () {
  this.bandWidthChart = null;
  this.cpuUsageChart = null;
  this.memUsageChart = null;
}

NMeshDeviceSummary.prototype.getDefaultDevice = function () {
  var index = 0;
  var max = 0;
  this.staticchartsdata.summarychart.forEach((a, i) => {
    if (max < a[1]) {
      max = a[1];
      index = i;
    }
  })
  return { uid: this.deviceIds[index][0], name: this.deviceIds[index][1] };
}

NMeshDeviceSummary.prototype.setCurrDevice = function (uid) {
  this.uid = uid;
  this.clearDynamicCharts();
}

NMeshDeviceSummary.prototype.getDeviceName = function(uid){
  var dev = this.deviceIds.filter(d => d[0] == uid)[0];
  if(dev)
    return dev[1];
  else
    return null;
}

NMeshDeviceSummary.prototype.parseSummaryJson = function () {

  var newsummarychartdata = [];
  var newbandratiochartsdata = [];
  var newhealthchartsdata = [];
  this.deviceIds = [];
  this.summaryjsondata.device_details.forEach((d, i) => {
    newsummarychartdata.push([d.location, d.metrics._2g_metrics._2g_station_count + d.metrics._5g_metrics._5g_station_count]);
    this.deviceIds.push([d.uid, d.location, i]);
    var bandratio = [];
    bandratio.push(['2GHz', d.metrics._2g_metrics._2g_station_count])
    bandratio.push(['5GHz', d.metrics._5g_metrics._5g_station_count])
    newbandratiochartsdata.push(bandratio);
    var health = [];
    health.push(['Poor', (d.metrics._2g_metrics._2g_poor_clients + d.metrics._5g_metrics._5g_poor_clients) / 2])
    health.push(['Fair', (d.metrics._2g_metrics._2g_fair_clients + d.metrics._5g_metrics._5g_fair_clients) / 2])
    health.push(['Good', (d.metrics._2g_metrics._2g_good_clients + d.metrics._5g_metrics._5g_good_clients) / 2])
    newhealthchartsdata.push(health);
  })
  this.staticchartsdata = {
    summarychart: newsummarychartdata,
    //Device related chart for all devices
    bandratiochart: newbandratiochartsdata,
    healthchart: newhealthchartsdata,
  }
  this.converttopercentage();
}

//Convert the number into percentage
NMeshDeviceSummary.prototype.converttopercentage = function () {
  var total = 0;
  this.staticchartsdata.summarychart.forEach(a => {
    total += a[1];
  })
  if (total > 0) {
    this.staticchartsdata.summarychart.forEach(a => {
      a[1] = (a[1] / total) * 100;
    })
  }

   total = 0;
  this.staticchartsdata.healthchart.forEach(a => {
    total += a[1];
  })
  if (total > 0) {
    this.staticchartsdata.healthchart.forEach(a => {
      a[1] = (a[1] / total) * 100;
    })
  }
  

  this.staticchartsdata.bandratiochart.forEach(d => {
    total = 0;
    d.forEach(a => {
      total += a[1];
    })
    if (total > 0) {
      d.forEach(a => {
        a[1] = (a[1] / total) * 100;
      })
    }
  })
}

NMeshDeviceSummary.prototype.onDeviceChange = function(d, name, uid){
  var _this = this;
  _this.clearDynamicCharts();
  var event = jQuery.Event("nmeshcurrdevchanged");
  if(d){
    event.name = _this.deviceIds[d.index][1];
    event.uid = _this.deviceIds[d.index][0];
  }
  else{
    event.name = name;
    event.uid = uid;
  }
  $(_this.config.summarychartdivid).trigger(event);
}
NMeshDeviceSummary.prototype.drawSummaryChart = function (jsondata) {
  var _this=this;
  if (jsondata) {
    this.summaryjsondata = jsondata;
    this.parseSummaryJson();
  }
  if (!this.summaryjsondata)
    return;
  var _this = this;
  if(!this.summarychart)
  {
    this.summarychart = c3.generate({
      bindto: this.config.summarychartdivid,
      data: {
        columns: this.staticchartsdata.summarychart,
        type: 'pie',
        onclick: function (d, i) {
          _this.onDeviceChange(d)
        },
      },
      pie: {
        label: {
          format: function (value, ratio, id) {
            return d3.format(".1f")(value) + '%';
          }
        }
      }
    })
  }
  else{
    //Unload data from the chart
    //If the device is not in the JSON
    var curKeys = Object.keys(this.summarychart.xs())
    var unload = false;
    var deletedKeys = [];
    var newKeys = [];
    this.staticchartsdata.summarychart.forEach(n => {
      newKeys.push(n[0]);
    })
    if(curKeys.length == newKeys.length)
    {
      curKeys.forEach(k => {
        if(newKeys.indexOf(k) < 0)
          deletedKeys.push(k);
      })
      if(deletedKeys.length)
        unload = true;
    }
    else
      unload=true;
    if(unload)
    {
      this.summarychart = null;
      this.drawSummaryChart(jsondata);
    }
    else
      this.summarychart.load({columns:this.staticchartsdata.summarychart})
  }

  return this.summarychart;
}

NMeshDeviceSummary.prototype.getCurrIndex = function () {
  var index = null;
  var d = this.deviceIds.filter(d =>  d[0] == this.uid)
  return d[0][2];
}

NMeshDeviceSummary.prototype.drawBandRatioChart = function () {
  var _this=this;
  var columns = this.staticchartsdata.bandratiochart[_this.getCurrIndex()]
  if(!this.bandchart)
    this.bandchart = c3.generate({
      bindto: this.config.bandchartdivid,
      data: {
        columns: columns,
        type: 'donut',
        onclick: function (d, i) {
          console.log("onclick", d, i);

        },
      },
      pie: {
        label: {
          format: function (value, ratio, id) {
            return d3.format(".1f")(value) + '%';
          }
        }
      }
    })
  else
    this.bandchart.load({columns:columns})

  return this.bandchart;
}


NMeshDeviceSummary.prototype.drawBandWidthChart = function (divid) {
  if (!this.bandWidthChart) {
    this.bandWidthChart = c3.generate({
      bindto: this.config.bandwidthchartdivid,
      data: {
        x: 'x',
        columns: this.dynamicchartsdata.networkusage
      },
      axis: {
        x: {
          type: 'timeseries',
          tick: {
            format: '%H:%M:%S'
          }
        },
        y: {
          tick: {
            format: function (y) {
              return util.getBytesString(y);
            }
          }
        }
      }
    });
  }
  else {
    var count = this.bandWidthChart.xs()['uplink'].length;
    var f = new Date(this.bandWidthChart.xs()['uplink'][0]);
    var l = new Date(this.bandWidthChart.xs()['uplink'][count - 1]);
    if (l.getTime() - f.getTime() > this.config.bandwidthcharttimewidth)
      to = new Date(l.getTime() - this.config.bandwidthcharttimewidth);
    else
      to = f;

    this.bandWidthChart.flow({
      columns: this.dynamicchartsdata.networkusage,
      to: to
    });
  }
}
NMeshDeviceSummary.prototype.drawCPUUsageChart = function () {
  if (!this.cpuUsageChart) {
    this.cpuUsageChart = c3.generate({
      bindto: this.config.cpuusagechartdivid,
      data: {
        x: 'x',
        columns: this.dynamicchartsdata.cpuusage
      },
      tooltip: {
        show:true,
        format: {
          title: function (x) { return 'Data ' + x; }
        }
      },
      axis: {
        y: {
          min: 0,
          padding:{bottom: 0},
          tick: {
            format: function (y) {
              return d3.format(".1%")(y/100)
            }
          }
        },
        x: {
          type: 'timeseries',
          tick: {
            format: '%H:%M:%S'
          }
        }
      }
    });
  }
  else {
    var to = 0;
    var count = this.cpuUsageChart.xs()['CPU Usage'].length;
    var f = new Date(this.cpuUsageChart.xs()['CPU Usage'][0]);
    var l = new Date(this.cpuUsageChart.xs()['CPU Usage'][count - 1]);
    if (l.getTime() - f.getTime() > this.config.cpucharttimewidth)
      to = new Date(l.getTime() - this.config.cpucharttimewidth);
    else
      to = f;


    this.cpuUsageChart.flow({
      columns: this.dynamicchartsdata.cpuusage,
      to: to
    });
  }
}
NMeshDeviceSummary.prototype.drawMemUsageChart = function () {
  if (!this.memUsageChart) {
    this.memUsageChart = c3.generate({
      bindto: this.config.memusagechartdivid,
      data: {
        x: 'x',
        columns: this.dynamicchartsdata.memusage
      },
      tooltip: {
        show:true
      },
      axis: {
        y: {
          padding:{bottom: 0},
          min: 0,
          tick: {
            format: function (value, ratio, id) {
              return d3.format(".0%")(value/100)
            }
          }
        },
        x: {
          type: 'timeseries',
          tick: {
            format: '%H:%M:%S'
          }
        }
      }
    });
  }
  else {
    var to = 0;
    var count = this.memUsageChart.xs()['Mem Usage'].length;
    var f = new Date(this.memUsageChart.xs()['Mem Usage'][0]);
    var l = new Date(this.memUsageChart.xs()['Mem Usage'][count - 1]);
    if (l.getTime() - f.getTime() > this.config.memcharttimewidth)
      to = new Date(l.getTime() - this.config.memcharttimewidth);
    else
      to = f;


    this.memUsageChart.flow({
      columns: this.dynamicchartsdata.memusage,
      to: to
    });
  }
}

NMeshDeviceSummary.prototype.drawClientHealthChart = function () {
  if(!this.healthchart){
  this.healthchart = c3.generate({
    bindto: this.config.healthchartdivid,
    data: {
      columns: this.staticchartsdata.healthchart[this.getCurrIndex()],
      type: 'pie'
    },
    pie: {
      // label: {
      //   format: function (value, ratio, id) {
      //     return d3.format(".1f")(value) + '%';
      //   }
      // }
    }
  })

  
}
else
  this.healthchart.load({columns:this.staticchartsdata.healthchart[this.getCurrIndex()]})
}


NMeshDeviceSummary.prototype.isValidDeviseJson = function (jsondata) {
  var curdata = jsondata["device_metrics_histogram "];
  if(!curdata || curdata.length == 0)
    return false;
  else
    return true;
}

NMeshDeviceSummary.prototype.isValidSummaryJson = function (jsondata) {
  if(jsondata.device_details && jsondata.device_details.length > 0)
    return true;
  else
    return false;
}

//
NMeshDeviceSummary.prototype.parseDeviceJson = function (jsondata) {
  //Make sure the summary data is available
  var curdata = jsondata["device_metrics_histogram "];

  var  devcpuusage = []
  var devnetworkusage = []
  var  devmemusage = []


  //Add cpu usage data
  if (devcpuusage.length == 0) {
    devcpuusage.push(['x']);
    devcpuusage.push(['CPU Usage']);
  }

  if (devmemusage.length == 0) {
    devmemusage.push(['x']);
    devmemusage.push(['Mem Usage']);
  }

  if (devnetworkusage.length == 0) {
    devnetworkusage.push(['x']);
    devnetworkusage.push(['uplink']);
    devnetworkusage.push(['downlink']);
  }
  curdata.forEach((cur, i) => {
    var time = new Date(curdata[i].cpu.time);
    devcpuusage[0].push(time);
    devcpuusage[1].push(curdata[i].cpu.cpu_percentage);

    time = new Date(curdata[i].memory.time);
    devmemusage[0].push(time);
    devmemusage[1].push(curdata[i].memory.mem_percentage);


    curdata[i].tx_rx_histogram.forEach(txrxdata => {
      time = new Date(txrxdata.time)
      devnetworkusage[0].push(time);
      devnetworkusage[1].push(txrxdata.tx_bytes);
      devnetworkusage[2].push(txrxdata.rx_bytes);
    })

  })
  this.dynamicchartsdata={};

  this.dynamicchartsdata.networkusage=devnetworkusage;
  this.dynamicchartsdata.memusage = devmemusage;
  this.dynamicchartsdata.cpuusage = devcpuusage;
}

NMeshDeviceSummary.prototype.drawDeviceCharts = function (json, uid) {

  this.uid = uid;

  this.parseDeviceJson(json);
  this.drawBandRatioChart();
  this.drawCPUUsageChart();
  this.drawMemUsageChart();
  this.drawClientHealthChart();
  this.drawBandWidthChart();

}

NMeshDeviceSummary.prototype.drawArrowLegend = function (chart, data) {
  d3.selectAll(".c3-chart-arc text").each(function (v) {
    var label = d3.select(this);
    var pos = label.attr("transform").match(/-?\d+(\.\d+)?/g);

    // pos[0] is x, pos[1] is y. Do some position changes and update value
    label.attr("transform", "translate(" + pos[0] + "," + pos[1] + ")");
  });
}
