
 var NMeshClientSummary = function() {
   
  //For reference
  this.columns = [
    "",
    "Name",
    "IP Address",
    "MAC Address / OS Type",
    "Capability",
    "Connected Band",
    "Connected Time",
    "Steering Capability",
    "Traffic",
    "Connected AP"
  ]

  //Fields in the
  this.fields = [
    "slno",
    "name",
    "ip",
    "mac",
    "capability",
    "band",
    "contime",
    "mbo",
    "txrx",
    "device"
  ]

}

NMeshClientSummary.prototype.parseJSON = function() {
  this.clients = this.jsondata.wireless_client_details;
}

NMeshClientSummary.prototype.getTableData = function(jsondata){
  if(jsondata){
    this.jsondata = jsondata;
    this.parseJSON();
  }
  if(!this.jsondata)
    return [];
  var tableData = [];
  this.clients.forEach((c, i) => {
    var row = [];
    this.fields.forEach(c1 => {
      var result = 'N/A';
      switch (c1) {
        case "name":
          result = (c.host_name && c.host_name.length > 0)?c.host_name:"Name"+i;
          break;
        case "slno":
          result = "";
          break;
        case "device":
          result = `<b>${c.location}</b>`
          break;
        case "mac":
          var img = util.getImageForClientOS(c['os']);
          result = `<div class="cellmac"><span>${c['mac_address']}</span><img title="${c['os']}" src="${img}"></img></div>`;
          break;
        case "contime":
          var contime = c['conn_time_sec'];
          if (contime)
          {
            result = parseInt(contime / (60 * 60 * 24)) + ':' + new Date(contime * 1000).toISOString().substr(11, 8);
            var parts = result.split(':');
            result = "";
            if(parts[0]>0)
              result += (parseInt(parts[0]).toString() + (parseInt(parts[0])>1?'days':'day'));
            if(parts[1]>0)
            {
              if(result.length)
                result += ', '
              result += (parseInt(parts[1]).toString() + (parseInt(parts[1])>1?'hrs':'hr'));
            }
            if(parts[2]>0)
            {
              if(result.length)
                result += ', '
              result += (parseInt(parts[2]).toString() + (parseInt(parts[2])>1?'mins':'min'));
            }
          }
          break;
        case "ip":
          result = c['ip'];
          break;
        case "capability":
          result = c['client_type']+(c['no_of_streams']?' ('+c['no_of_streams']+'x'+c['no_of_streams']+')':'');
          break;
        case "band":
          result = c['radio'];
          break;
        case "mbo":
          result = c['_11v']?'Yes':'No';
          break;
        case "txrx":
          var tx = c['_peer_tx_bytes'];
          var rx = c['_peer_rx_bytes'];
          var strtx = util.getBytesString(tx);
          var strrx = util.getBytesString(rx)
          result =  `<div class="cellmac"><span>${strtx}</span><i class="fa fa-long-arrow-up" aria-hidden="true"></i><br><span>${strrx}</span><i class="fa fa-long-arrow-down" aria-hidden="true"></i></div>`
          break;
      }
      row.push(result);
    })
    tableData.push(row);
  })
  return tableData;
}


