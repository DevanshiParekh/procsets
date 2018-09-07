/*
 *Util functions
 *
 */
var NMeshUtil = function(){
}
    NMeshUtil.prototype.getBytesString = function(tx) {
        if (tx > 1000000000)
          return parseInt(tx / 1000000000) + 'GB'
        if (tx > 1000000)
          return parseInt(tx / 1000000) + 'MB'
        else if (tx > 1000)
          return parseInt(tx / 1000) + 'KB'
        else
          return parseInt(tx);
      }
    
      NMeshUtil.prototype.getSignalStrengthText = function(d) {
        var ssa = (d.ssst + d.ssts) / 2.0;
        if (ssa >= 80)
          return 'Excellent'
        else if (ssa >= 60)
          return 'Good'
        else if (ssa >= 40)
          return 'Fair';
        else
          return 'Poor';
      }
      
      NMeshUtil.prototype.getSignalStrengthColor = function(d) {
        var ssa = (d.ssst + d.ssts) / 2.0;
        if (ssa >= 75)
          return 'green'
        else if (ssa >= 25)
          return 'green'
        else if (ssa >= 15)
          return 'red';
        else
          return 'grey'
      }

      NMeshUtil.prototype.getImageForAP = function(noofstream) {
        switch (noofstream) {
          case 1:
            return "/facesix/static/qubercomm/images/nmesh/ap11.png";
          case 2:
            return "/facesix/static/qubercomm/images/nmesh/ap22.png"
          case 3:
            return "/facesix/static/qubercomm/images/nmesh/ap33.png"
          case 4:
            return "/facesix/static/qubercomm/images/nmesh/ap44.png"
          default:
            return "/facesix/static/qubercomm/images/nmesh/ap11.png"
        }
      }
    
      NMeshUtil.prototype.getImageForClientOS = function(type) {
        switch (type) {
          case 'android':
            return "/facesix/static/qubercomm/images/nmesh/android.png";
          case 'laptop':
            return "/facesix/static/qubercomm/images/nmesh/laptop.png"
          default:
            return "/facesix/static/qubercomm/images/nmesh/windows.png"
        }
      }

      NMeshUtil.prototype.getUrlParameter = function (sParam) {
        var sPageURL = decodeURIComponent(window.location.search.substring(1)),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;
    
        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');
    
            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : sParameterName[1];
            }
        }
    };

var util = new NMeshUtil();
