 
 var NMeshDataService = function() {
    //this.baseurl = 'https://locatum.qubercomm.com/facesix/rest/nmesh/';
    this.baseurl = '/facesix/rest/nmesh/';
    
  }
  
  NMeshDataService.prototype.getClientSummary = function(cid) {
    var path='wireless_client_details?cid='+cid;
    return $.ajax({
        url: this.baseurl + path,
        method: "get",
        dataType:'json'
    });
  }
  
  NMeshDataService.prototype.getTopoData = function(cid){
      var path='mesh_link?cid='+cid;
      return $.ajax({
        url: this.baseurl + path,
        method: "get",
        dataType:'json'
     }); 
  }

  NMeshDataService.prototype.getDevicesStatiscs = function(cid){
    var path = 'list?cid='+cid;
    return $.ajax({
        url: this.baseurl + path,
        method: "get",
        dataType:'json'
    });
  }

  NMeshDataService.prototype.getCurrDeviceStatiscs = function(uid){
      var path = 'device_metrics_histogram?uid='+uid;
      return $.ajax({
        url: this.baseurl + path,
        method: "get",
        dataType:'json'
      }); 
  }


  
  
  