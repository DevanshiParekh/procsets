app.controller('VenueCtrl', ['$scope', '$timeout', '$compile', '$http', '$routeParams', function($scope, $timeout, $compile, $http, $routeParams) {



  $scope.openSunburst = function() {
    $scope.sunburstfullview = !$scope.sunburstfullview;
  }

  $scope.frameVaps = function() {
    $scope.Vaps = {
      "ActiveVaps": [
        ["2GVAP", 7],
        ["5GVAP", 7]
      ]
    }
    $scope.dataVaps = [];
    $scope.dataVaps.name = [];
    $scope.dataVaps.value = [];
    var value = $scope.Vaps.ActiveVaps;
    var length = value.length;
    for (i = 0; i < length; i++) {
      $scope.dataVaps.name.push(value[i][0]);
      $scope.dataVaps.value.push(value[i][1]);
    }
    ;
  //console.log($scope.dataVaps);
  }
  $scope.frameVaps();
  $scope.GetVaps = function() {
    $http({
      method: 'Get',
      url: '/facesix/rest/site/portion/networkdevice/getvaps?sid=' + $routeParams.sid,
      headers: {
        'content-type': 'application/json'
      }
    }).then(function successCallback(response) {
      //console.log(response);
    }, function errorCallback(response) {
      //console.log(response);
    });
  }
  //$scope.GetVaps();


  $scope.GetVenueMap = function() {
    //    	$http({
    //              method:'Get',
    //             url:'/facesix/rest/site/portion/networkdevice/venuemap?sid='+$routeParams.sid,
    //              headers: {'content-type': 'application/json'}
    //          }).then(function successCallback(response){
    //              //console.log(response);
    $scope.VenueMapGotData = {
      "typeOfDevices": [["Floor", 4], ["Server", 4], ["Switch", 5], ["AP", 9], ["SNR", 0]]
    }
    $scope.dataVM = [];
    $scope.dataVM.name = [];
    $scope.dataVM.value = [];
    var pvalue = $scope.VenueMapGotData.typeOfDevices;
    var plength = pvalue.length;
    for (i = 0; i < plength; i++) {
      $scope.dataVM.name.push(pvalue[i][0]);
      $scope.dataVM.value.push(pvalue[i][1]);
    }
    ;
    //console.log($scope.dataVM);

  //          }, function errorCallback(response){
  //              //console.log(response);
  //          });
  }
  $scope.GetVenueMap();



  $scope.GetTxRX = function() {
    //   	$http({
    //              method:'Get',
    //             url:'/facesix/rest/site/portion/networkdevice/rxtx?spid='+$routeParams.spid,
    //              headers: {'content-type': 'application/json'}
    //          }).then(function successCallback(response){
    //              //console.log(response);
    $scope.sampleRxTx = [{
      "Tx": "-1559183487 bytes",
      "Rx": "611200272 bytes",
      "time": "2016-11-25 14:11:57.198"
    }, {
      "Tx": "-1559277580 bytes",
      "Rx": "611188768 bytes",
      "time": "2016-11-25 14:11:51.856"
    }, {
      "Tx": "-1559344484 bytes",
      "Rx": "611176765 bytes",
      "time": "2016-11-25 14:11:46.512"
    }, {
      "Tx": "-1559452031 bytes",
      "Rx": "611165368 bytes",
      "time": "2016-11-25 14:11:41.170"
    }, {
      "Tx": "-1559516429 bytes",
      "Rx": "611153501 bytes",
      "time": "2016-11-25 14:11:35.826"
    }, {
      "Tx": "-1559183487 bytes",
      "Rx": "611200272 bytes",
      "time": "2016-11-25 14:11:57.198"
    }, {
      "Tx": "-1559277580 bytes",
      "Rx": "611188768 bytes",
      "time": "2016-11-25 14:11:51.856"
    }, {
      "Tx": "-1559344484 bytes",
      "Rx": "611176765 bytes",
      "time": "2016-11-25 14:11:46.512"
    }, {
      "Tx": "-1559452031 bytes",
      "Rx": "611165368 bytes",
      "time": "2016-11-25 14:11:41.170"
    }, {
      "Tx": "-1559516429 bytes",
      "Rx": "611153501 bytes",
      "time": "2016-11-25 14:11:35.826"
    }];

    //console.log($scope.sampleRxTx);
    $scope.dataRxTx = [];
    $scope.dataRxTx.name = [];
    $scope.dataRxTx.value = [];
    $scope.dataRxTx.valueTx = [];
    $scope.dataRxTx.valueBarTx = [];
    $scope.dataRxTx.valueRx = [];
    $scope.dataRxTx.valueBarRx = [];
    $scope.dataRxTx.series = ['Tx', 'Rx'];
    $scope.dataRxTx.barTxSeries = ['Tx'];
    $scope.dataRxTx.barRxSeries = ['Rx'];
    var rxtxvalue = $scope.sampleRxTx;
    var rxtxlength = rxtxvalue.length;
    for (i = 0; i < rxtxlength; i++) {
      //console.log(rxtxvalue[i].time);
      var Tx = Math.round(parseInt(rxtxvalue[i].Tx.replace(" bytes", "")) / 1000000);
      var Rx = Math.round(parseInt(rxtxvalue[i].Rx.replace(" bytes", "")) / 1000000);

      var formatedTime = rxtxvalue[i].time;
      var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
      c_formatedTime = new Date(c_formatedTime);
      $scope.dataRxTx.name.push(c_formatedTime.getHours() + ":" + c_formatedTime.getMinutes());
      //$scope.dataRxTx.name.push(rxtxvalue[i].time);
      $scope.dataRxTx.valueTx.push(Tx);

      $scope.dataRxTx.valueRx.push(Rx);
    }
    ;
    $scope.dataRxTx.valueBarTx.push($scope.dataRxTx.valueTx);
    $scope.dataRxTx.valueBarRx.push($scope.dataRxTx.valueRx);
    $scope.dataRxTx.value.push($scope.dataRxTx.valueTx);
    $scope.dataRxTx.value.push($scope.dataRxTx.valueRx);
    //console.log($scope.dataRxTx);


    //			}, function errorCallback(response){
    //              //console.log(response);
    //          });

  }

  $scope.GetTxRX();




  $scope.GetvenueAgg = function() {
    //   		$http({
    //              method:'Get',
    //             url:'/facesix/rest/site/portion/networkdevice/rxtx?spid='+$routeParams.spid,
    //              headers: {'content-type': 'application/json'}
    //          }).then(function successCallback(response){
    //              console.log(response);

    $scope.floorT = [{
      "Floor": "floor1",
      "min_vap_tx_bytes": 657200591,
      "avg_vap_tx_bytes": 939551676,
      "max_vap_rx_bytes": 64328863,
      "max_vap_tx_bytes": 1223446116,
      "avg_vap_rx_bytes": 48803490.75,
      "time": "2016-11-26T06:40:00.000Z",
      "min_vap_rx_bytes": 33311847
    }, {
      "Floor": "floor 2",
      "min_vap_tx_bytes": 102651046,
      "avg_vap_tx_bytes": 708052837.4523809,
      "max_vap_rx_bytes": 2034246568,
      "max_vap_tx_bytes": 1343997724,
      "avg_vap_rx_bytes": 987497391.8809524,
      "time": "2016-11-26T06:40:00.000Z",
      "min_vap_rx_bytes": -60568850
    }, {
      "Floor": "floor 3",
      "min_vap_tx_bytes": 24127384,
      "avg_vap_tx_bytes": 139831397.25,
      "max_vap_rx_bytes": 33237137,
      "max_vap_tx_bytes": 255584567,
      "avg_vap_rx_bytes": 20505851.25,
      "time": "2016-11-26T06:40:00.000Z",
      "min_vap_rx_bytes": 7783779
    }, {
      "Floor": "floor 4",
      "min_vap_tx_bytes": -1129499972,
      "avg_vap_tx_bytes": -1063057875.9791666,
      "max_vap_rx_bytes": 307525467,
      "max_vap_tx_bytes": -994454688,
      "avg_vap_rx_bytes": -881803921.25,
      "time": "2016-11-26T06:40:00.000Z",
      "min_vap_rx_bytes": -2071474005
    }];

    $scope.dataVenueAgg = [];
    $scope.dataVenueAgg.name = [];
    $scope.dataVenueAgg.value = [];
    $scope.dataVenueAgg.dlink = [];
    $scope.dataVenueAgg.ulink = [];
    for (i = 0; i < $scope.floorT.length; i++) {
      var infor = $scope.floorT[i];
      $scope.dataVenueAgg.name.push(infor.Floor);
      $scope.dataVenueAgg.ulink.push(Math.max(0, Math.round(infor.max_vap_tx_bytes / 100000)));
      $scope.dataVenueAgg.dlink.push(Math.max(0, Math.round(infor.max_vap_rx_bytes / 100000)));
    }
    $scope.dataVenueAgg.value.push($scope.dataVenueAgg.dlink);
    $scope.dataVenueAgg.value.push($scope.dataVenueAgg.ulink);
    //console.log($scope.dataVenueAgg);

  //  }, function errorCallback(response){
  //              console.log(response);
  //          });
  }
  $scope.GetvenueAgg();

  $scope.GetActivity = function() {}
  $scope.GetActivity();
}]);

app.controller('VenueActionCtrl', ['$scope', '$rootScope', '$window', '$timeout', function($scope, $rootScope, $window, $timeout) {
  var currentMapCenter = null;
  var mapOptions = {
    center: new google.maps.LatLng(0, 0),
    zoom: 2,
    minZoom: 1
  };
  var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
  //var marker = new google.maps.Marker({map: map});
  var marker = new google.maps.Marker({
    map: map,
    draggable: true,
    animation: google.maps.Animation.DROP
  });
  $scope.venuestep1 = true;
  $scope.searchtrue = true;
  $scope.venue = {};
  $scope.venuenext = function() {
    if ($scope.venuestep2 == true) {
      $scope.venuestep1 = false;
      $scope.venuestep2 = false;
      $scope.venuestep3 = true;
      $scope.nexttrue = false;
      $scope.savetrue = true;
    }
    if ($scope.venuestep1 == true) {

      $scope.venuestep1 = false;
      $scope.venuestep2 = true;
      $scope.venuestep3 = false;
      $scope.nexttrue = false;
      $scope.prevtrue = true;
      $scope.savetrue = true;
      map.setCenter(center);
      $scope.searchtrue = false;
    }
  }
  $scope.venueprev = function() {
    if ($scope.venuestep3 == true) {
      $scope.venuestep1 = false;
      $scope.venuestep2 = true;
      $scope.venuestep3 = false;
      $scope.prevtrue = false;
      $scope.savetrue = false;
      $scope.nexttrue = true;
    }
    if ($scope.venuestep2 == true) {
      $scope.venuestep1 = true;
      $scope.venuestep2 = false;
      $scope.venuestep3 = false;
      $scope.prevtrue = false;
      $scope.savetrue = false;
      $scope.nexttrue = true;
      $scope.searchtrue = true;
    }
  }
  $scope.user = {
    'from': '',
    'fromLat': '',
    'fromLng': ''
  };
  var inputFrom = document.getElementById('from');
  var autocompleteFrom = new google.maps.places.Autocomplete(inputFrom);
  google.maps.event.addListener(autocompleteFrom, 'place_changed', function() {
    var place = autocompleteFrom.getPlace();
    if (!place.geometry) {
      // Inform the user that the place was not found and return.
      input.className = 'notfound';
      return;
    }
    if (place.geometry.viewport) {
      map.fitBounds(place.geometry.viewport);
      $scope.nexttrue = true;
    } else {
      map.setCenter(place.geometry.location);
      map.setZoom(17); // Why 17? Because it looks good.
      $scope.nexttrue = true;
    }

    marker.setIcon( /** @type {google.maps.Icon} */ ({
      url: place.icon,
      size: new google.maps.Size(71, 71),
      origin: new google.maps.Point(0, 0),
      anchor: new google.maps.Point(17, 34),
      scaledSize: new google.maps.Size(35, 35)
    }));
    marker.setPosition(place.geometry.location);
    marker.setVisible(true);

    //console.log(place);
    $scope.place = place;

    $scope.venue.name = place.name;
    $scope.venue.address = place.formatted_address;
    $scope.venue.latitude = place.geometry.location.lat();
    $scope.venue.longitude = place.geometry.location.lng();
    //console.log($scope.venue);
    //        $scope.user.fromLat = place.geometry.location.lat();
    //        $scope.user.fromLng = place.geometry.location.lng();
    //$scope.user.from = place.formatted_address;
    $scope.$apply();
  });
  google.maps.event.addListener(map, 'click', function(event) {
    marker.setPosition(event.latLng);
    var latitude = event.latLng.lat();
    var longitude = event.latLng.lng();

    $scope.venue.latitude = latitude;
    $scope.venue.longitude = longitude;

  });
  google.maps.event.addListener(marker, 'dragend', function(event) {

    var latitude = event.latLng.lat();
    var longitude = event.latLng.lng();
    $scope.venue.latitude = latitude;
    $scope.venue.longitude = longitude;

  });
  var center;
  function calculateCenter() {
    center = map.getCenter();
  //console.log(center);
  }
  google.maps.event.addDomListener(map, 'idle', function() {
    calculateCenter();
  });
  google.maps.event.addDomListener(window, 'resize', function() {
    map.setCenter(center);
  });
}]);

app.controller('FloorCtrl', ['$scope', '$rootScope', '$http', '$routeParams', '$window', function($scope, $rootScope, $http, $routeParams, $window) {
  $scope.floorMultiSelect = [];
  $scope.openConfig = function() {
    $scope.fullConfigView = !$scope.fullConfigView;
  }
  $scope.openConfirmBox = function() {
    $scope.confirmBox = !$scope.confirmBox;
  }

  $scope.deleteFloor = function(value) {
    console.log(value);
    $http.get('/facesix/web/site/portion/deleteall?&spid=' + value).then(function successCallback(response) {
      $window.location.reload();
    }, function errorCallback(response) {
      $window.location.reload();
    })
  }

  $scope.deleteAllFloor = function(arry) {
    for (i = 0; i < arry.length; i++) {
      $http.get('/facesix/web/site/portion/deleteall?&spid=' + arry[i]).then(function successCallback(response) {}, function errorCallback(response) {});
    }
    $window.location.reload();
  }
  $scope.deleteMultiFloor = function() {
    $scope.deleteAllFloor($scope.floorMultiSelect)
  }
  function checkArray(array, value) {
    for (i = 0; i < array.length; i++) {
      if (array[i] == value) {
        return i;
      }
    }
    return null;
  }

  $scope.floorMultiSAdd = function(value) {

    var checkContain = checkArray($scope.floorMultiSelect, value);
    console.log($scope.floorMultiSelect.length);

    if (checkContain == null) {
      $scope.floorMultiSelect.push(value);
    } else {
      console.log(checkContain);
      $scope.floorMultiSelect.splice(checkContain, 1);
    }
    if ($scope.floorMultiSelect.length > 1) {
      $scope.showMultiSelect = true;
    }
    console.log($scope.floorMultiSelect);
  }

  $scope.openPopupDel = function() {
    $scope.popdelopen = !$scope.popdelopen;
  }

  //console.log($scope.Vaps.ActiveVaps);

  $scope.frameVaps = function() {
    $scope.Vaps = {
      "ActiveVaps": [
        ["2GVAP", 7],
        ["5GVAP", 7]
      ]
    }
    $scope.dataVaps = [];
    $scope.dataVaps.name = [];
    $scope.dataVaps.value = [];
    var value = $scope.Vaps.ActiveVaps;
    var length = value.length;
    for (i = 0; i < length; i++) {
      $scope.dataVaps.name.push(value[i][0]);
      $scope.dataVaps.value.push(value[i][1]);
    }
    ;
  //console.log($scope.dataVaps);
  }
  $scope.frameVaps();
  $scope.GetVaps = function() {
    $http({
      method: 'Get',
      url: '/facesix/rest/site/portion/networkdevice/getvaps?spid=' + $routeParams.spid,
      headers: {
        'content-type': 'application/json'
      }
    }).then(function successCallback(response) {
      console.log(response);
    }, function errorCallback(response) {
      console.log(response);
    });
  }
  //$scope.GetVaps();


  $scope.GetPeers = function() {
    //    	$http({
    //              method:'Get',
    //             url:'/facesix/rest/site/portion/networkdevice/getpeers?spid='+$routeParams.spid,
    //              headers: {'content-type': 'application/json'}
    //          }).then(function successCallback(response){
    //              console.log(response);
    $scope.PeerGotData = {
      "devicesConnected": [
        ["Mac", 5],
        ["Android", 0],
        ["Win", 0],
        ["Others", 10], {}
      ]
    }
    $scope.dataPeers = [];
    $scope.dataPeers.name = [];
    $scope.dataPeers.value = [];
    var pvalue = $scope.PeerGotData.devicesConnected;
    var plength = 4;
    for (i = 0; i < plength; i++) {
      $scope.dataPeers.name.push(pvalue[i][0]);
      $scope.dataPeers.value.push(pvalue[i][1]);
    }
    ;
    //console.log($scope.dataPeers);

  //          }, function errorCallback(response){
  //              console.log(response);
  //          });
  }
  $scope.GetPeers();


  $scope.linedata = {
    label: ["10:54", "10:56", "10:58", "11:00", "11:02", "11:04", "11:06", "11:08", "11:10", "11:12"],
    series: ['Tx', 'Rx'],
    data: [
      [65, 59, 80, 81, 56, 55, 40, 45, 50, 60],
      [28, 48, 40, 58, 86, 47, 69, 40, 55, 65]
    ],
    colours: ["#03A9F4", "#2196F3"]
  };
  //console.log($scope.linedata.data);


  $scope.GetTxRX = function() {
    //   	$http({
    //              method:'Get',
    //             url:'/facesix/rest/site/portion/networkdevice/rxtx?spid='+$routeParams.spid,
    //              headers: {'content-type': 'application/json'}
    //          }).then(function successCallback(response){
    //              console.log(response);
    $scope.sampleRxTx = [{
      "Tx": "-1559183487 bytes",
      "Rx": "611200272 bytes",
      "time": "2016-11-25 14:11:57.198"
    }, {
      "Tx": "-1559277580 bytes",
      "Rx": "611188768 bytes",
      "time": "2016-11-25 14:11:51.856"
    }, {
      "Tx": "-1559344484 bytes",
      "Rx": "611176765 bytes",
      "time": "2016-11-25 14:11:46.512"
    }, {
      "Tx": "-1559452031 bytes",
      "Rx": "611165368 bytes",
      "time": "2016-11-25 14:11:41.170"
    }, {
      "Tx": "-1559516429 bytes",
      "Rx": "611153501 bytes",
      "time": "2016-11-25 14:11:35.826"
    }, {
      "Tx": "-1559183487 bytes",
      "Rx": "611200272 bytes",
      "time": "2016-11-25 14:11:57.198"
    }, {
      "Tx": "-1559277580 bytes",
      "Rx": "611188768 bytes",
      "time": "2016-11-25 14:11:51.856"
    }, {
      "Tx": "-1559344484 bytes",
      "Rx": "611176765 bytes",
      "time": "2016-11-25 14:11:46.512"
    }, {
      "Tx": "-1559452031 bytes",
      "Rx": "611165368 bytes",
      "time": "2016-11-25 14:11:41.170"
    }, {
      "Tx": "-1559516429 bytes",
      "Rx": "611153501 bytes",
      "time": "2016-11-25 14:11:35.826"
    }];

    //console.log($scope.sampleRxTx);
    $scope.dataRxTx = [];
    $scope.dataRxTx.name = [];
    $scope.dataRxTx.value = [];
    $scope.dataRxTx.valueTx = [];
    $scope.dataRxTx.valueBarTx = [];
    $scope.dataRxTx.valueRx = [];
    $scope.dataRxTx.valueBarRx = [];
    $scope.dataRxTx.series = ['Tx', 'Rx'];
    $scope.dataRxTx.barTxSeries = ['Tx'];
    $scope.dataRxTx.barRxSeries = ['Rx'];
    var rxtxvalue = $scope.sampleRxTx;
    var rxtxlength = rxtxvalue.length;
    for (i = 0; i < rxtxlength; i++) {
      //console.log(rxtxvalue[i].time);
      var Tx = Math.round(parseInt(rxtxvalue[i].Tx.replace(" bytes", "")) / 1000000);
      var Rx = Math.round(parseInt(rxtxvalue[i].Rx.replace(" bytes", "")) / 1000000);

      var formatedTime = rxtxvalue[i].time;
      var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
      c_formatedTime = new Date(c_formatedTime);
      $scope.dataRxTx.name.push(c_formatedTime.getHours() + ":" + c_formatedTime.getMinutes());
      //$scope.dataRxTx.name.push(rxtxvalue[i].time);
      $scope.dataRxTx.valueTx.push(Tx);

      $scope.dataRxTx.valueRx.push(Rx);
    }
    ;
    $scope.dataRxTx.valueBarTx.push($scope.dataRxTx.valueTx);
    $scope.dataRxTx.valueBarRx.push($scope.dataRxTx.valueRx);
    $scope.dataRxTx.value.push($scope.dataRxTx.valueTx);
    $scope.dataRxTx.value.push($scope.dataRxTx.valueRx);
    //console.log($scope.dataRxTx);


    //			}, function errorCallback(response){
    //              console.log(response);
    //          });

  }

  $scope.GetTxRX();

  $scope.GetRadioTraffic = function() {
    //   		$http({
    //              method:'Get',
    //             url:'/facesix/rest/site/portion/networkdevice/rxtx?spid='+$routeParams.spid,
    //              headers: {'content-type': 'application/json'}
    //          }).then(function successCallback(response){
    //              console.log(response);

    $scope.radioT = [{
      "min_vap_tx_bytes": 1291937982,
      "avg_vap_tx_bytes": 1292662047.875,
      "max_vap_rx_bytes": 88769254,
      "max_vap_tx_bytes": 1293405457,
      "avg_vap_rx_bytes": 88670948,
      "Radio": "2G",
      "time": "2016-11-26T08:00:00.000Z",
      "min_vap_rx_bytes": 88571575
    }, {
      "min_vap_tx_bytes": 760532455,
      "avg_vap_tx_bytes": 760785934.125,
      "max_vap_rx_bytes": 40086813,
      "max_vap_tx_bytes": 761033616,
      "avg_vap_rx_bytes": 40071784.25,
      "Radio": "5G",
      "time": "2016-11-26T08:00:00.000Z",
      "min_vap_rx_bytes": 40055912
    }];
    $scope.RadioTraffic = [];
    $scope.RadioTraffic.name = [];
    $scope.RadioTraffic.value = [];
    $scope.RadioTraffic.dlink = [];
    $scope.RadioTraffic.ulink = [];
    for (i = 0; i < $scope.radioT.length; i++) {
      var infor = $scope.radioT[i];
      $scope.RadioTraffic.name.push(infor.Radio);
      $scope.RadioTraffic.ulink.push(Math.max(0, Math.round(infor.max_vap_tx_bytes / 100000)));
      $scope.RadioTraffic.dlink.push(Math.max(0, Math.round(infor.max_vap_rx_bytes / 100000)));
    }
    $scope.RadioTraffic.value.push($scope.RadioTraffic.dlink);
    $scope.RadioTraffic.value.push($scope.RadioTraffic.ulink);
    console.log($scope.RadioTraffic);

  //  }, function errorCallback(response){
  //              console.log(response);
  //          });
  }
  $scope.GetRadioTraffic();





  $scope.setFile = function(element) {
    $scope.currentFile = element.files[0];

    var reader = new FileReader();

    reader.onload = function(event) {

      $scope.image_source = event.target.result;

      var x = document.createElement("IMG");
      x.setAttribute("src", $scope.image_source);
      document.getElementById("imgHolder").appendChild(x);

      // var myEl = angular.element( document.querySelector( '#imgHolder' ) );
      //var myEl = document.getElementById ("imgHolder");
      //console.log(myEl);
      // var img = '<img src="'+$scope.image_source+'"';
      //myEl.appendChild(img); 
      //console.log(myEl);
      //console.log($scope.image_source);
      $scope.$apply()

    }
    // when the file is read it triggers the onload event above.
    reader.readAsDataURL(element.files[0]);

    $scope.hideIcons = true;

  }
  $scope.clearImg = function() {

    var imageInput = document.getElementById("floorimg")
    imageInput.type = "text";
    imageInput.value = "";
    imageInput.type = "file";

    var list = document.getElementById("imgHolder")
    list.removeChild(list.childNodes[0]);
    $scope.hideIcons = false;
    $scope.image_source = "";

  }
  $scope.floorlist = true;
  $scope.floordashboard = false;

  $scope.showFloorList = function() {
    $scope.floorlist = true;
    $scope.floordashboard = false;
  }
  $scope.showFloorDashboard = function() {
    $scope.floorlist = false;
    $scope.floordashboard = true;
  }

  $scope.barlabels = ['2006', '2007', '2008', '2009', '2010', '2011', '2012', '2013', '2014', '2015', '2016', '2017', '2018'];
  $scope.barseries = ['Series A'];

  $scope.bardata = [
    [65, 59, 80, 81, 56, 55, 40, 30, 35, 60, 70, 20, 30]
  ];


  //$scope.floordata = {};
  $scope.value = {};
  $scope.value.data = "20";
  $scope.value.total = "50";
  $scope.value.label = "System Active";
  $scope.value.color = "#03A9F4";



  $scope.options = {
    scaleShowGridLines: "rgba(0,0,0,0)",
  };

  $scope.baroptions = {
    scaleShowLabels: false,
    scaleShowHorizontalLines: false,
    showScale: false
  }

  $scope.health = {};
  $scope.health.color = "#66BB6A";
  $scope.health.label = "Connected";
  $scope.health.total = "20";

  $scope.labels = ["IOS", "Mac", "Win", "Android", "Others"];
  $scope.data = [7, 12, 83, 83, 83];
  //$scope.dougcolours = ["#27ae60", "#2980b9", "#f39c12"]
  $scope.dougcolours = ["#2196F3", "#4CAF50", "#FF5722", "#FFC107", "#e57373", "#FF5722", "#FFB300", "#F4511E", "#546E7A", "#3F51B5", "#9C27B0", "#e57373"];
  $scope.barcolours = ["#4CAF50"]
  $scope.dougdate = {
    "typeOfDevices": [
      ["IOS", 7],
      ["Mac", 12],
      ["Win", 83],
      ["Android", 83],
      ["Others", 83]
    ]
  }


  $scope.floordata = {
    img: "http://localhost/project-quber/project-06092016/asset/img/ground.png",
    device: [
      {
        dev_type: "server",
        x: 200,
        y: 300
      },
      {
        dev_type: "server",
        x: 250,
        y: 350
      },
      {
        dev_type: "ap",
        x: 270,
        y: 380
      }
    ]
  };
  $scope.tools = {
    "toolsItem": {
      "items": [
        {
          "type": "fillRect",
          "x": 1,
          "y": 35,
          "w": 35,
          "h": 35,
          "id": "undo",
          "child": {
            "preset": "draw",
            "type": "undo",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/undo.png",
            "x": 10,
            "y": 43,
            "sx": 10,
            "sy": 60
          }
        },
        {
          "type": "fillRect",
          "x": 39,
          "y": 35,
          "w": 35,
          "h": 35,
          "id": "redo",
          "child": {
            "preset": "draw",
            "type": "redo",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/redo.png",
            "x": 46.5,
            "y": 42.5,
            "w": 24,
            "h": 24
          }
        },
        {
          "type": "fillRect",
          "x": 1,
          "y": 72,
          "w": 35,
          "h": 35,
          "id": "line",
          "child": {
            "preset": "draw",
            "type": "line",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/line.png",
            "x": 10.5,
            "y": 80.5,
            "w": 20,
            "h": 20
          }
        },
        {
          "type": "fillRect",
          "x": 39,
          "y": 72,
          "w": 35,
          "h": 35,
          "id": "rect",
          "child": {
            "preset": "draw",
            "type": "rect",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/rectangle.png",
            "x": 45.5,
            "y": 80.5,
            "w": 20,
            "h": 20
          }
        },
        {
          "type": "fillRect",
          "x": 1,
          "y": 109,
          "w": 35,
          "h": 35,
          "id": "circle",
          "child": {
            "preset": "draw",
            "type": "circle",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/circle.png",
            "x": 10.5,
            "y": 120.5,
            "w": 24,
            "h": 24
          }
        },
        {
          "type": "fillRect",
          "x": 39,
          "y": 109,
          "w": 35,
          "h": 35,
          "id": "lline",
          "child": {
            "preset": "draw",
            "type": "lline",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/lline.png",
            "x": 46.5,
            "y": 120.5,
            "w": 20,
            "h": 20
          }
        },
        {
          "type": "fillRect",
          "x": 1,
          "y": 146,
          "w": 35,
          "h": 35,
          "id": "doubledoor",
          "child": {
            "preset": "image",
            "type": "doubledoor",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/doubledoor.png",
            "x": 10.5,
            "y": 155.5,
            "w": 20,
            "h": 20
          }
        },
        {
          "type": "fillRect",
          "x": 39,
          "y": 146,
          "w": 35,
          "h": 35,
          "id": "singledoor",
          "child": {
            "preset": "image",
            "type": "singledoor",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/singledoor.png",
            "x": 46.5,
            "y": 155.5,
            "w": 20,
            "h": 20
          }
        },
        {
          "type": "fillRect",
          "x": 1,
          "y": 183,
          "w": 35,
          "h": 35,
          "id": "ofcdesk",
          "child": {
            "preset": "image",
            "type": "ofcdesk",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/ofcdesk.png",
            "x": 10.5,
            "y": 190.5,
            "w": 20,
            "h": 20
          }
        },
        {
          "type": "fillRect",
          "x": 39,
          "y": 183,
          "w": 35,
          "h": 35,
          "id": "table",
          "child": {
            "preset": "image",
            "type": "table",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/table.png",
            "x": 46.5,
            "y": 190.5,
            "w": 20,
            "h": 20
          }
        },
        {
          "type": "fillRect",
          "x": 1,
          "y": 223,
          "w": 35,
          "h": 35,
          "id": "threesetshoba",
          "child": {
            "preset": "image",
            "type": "threesetshoba",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/threesetshoba.png",
            "x": 10.5,
            "y": 230.5,
            "w": 20,
            "h": 20
          }
        },
        {
          "type": "fillRect",
          "x": 39,
          "y": 223,
          "w": 35,
          "h": 35,
          "id": "roundtable",
          "child": {
            "preset": "image",
            "type": "roundtable",
            "img": "http://localhost/project-quber/project-03102016/asset/img/tools/roundtable.png",
            "x": 46.5,
            "y": 230.5,
            "w": 20,
            "h": 20
          }
        }
      ]
    }
  };
  $scope.scale = {
    "length": 200,
    "breadth": 40,
    "unit": "m"
  };
  //$scope.$apply();

}]);

app.controller('topCtrl', ['$scope', function($scope, $rootScope) {}]);

app.controller('ClientCtrl', ['$scope', '$rootScope', '$http', '$filter', function($scope, $rootScope, $http, $filter) {
  $scope.newCus = {};
  $scope.$watch('newCus.serviceDuration', function(newDate) {

    var d = new Date($scope.newCus.serviceStartdate);
    d.setMonth(d.getMonth() + $scope.newCus.serviceDuration);
    d.setDate(d.getDate() - 1);
    $scope.newCus.serviceExpirydate = d;
    console.log($scope.newCus.serviceExpirydate);
  });



  $scope.CreateOpen = false;
  $scope.accounts = [];

  // /facesix/rest/customer/getCustomerInfo

  $http.get('/facesix/rest/customer/list').then(function successCallback(response) {
    $scope.accounts = response;
    console.log($scope.accounts);
   // alert($scope.accounts)
  }, function errorCallback(response) {});



  $scope.singleAccount = function(x) {
    console.log(x);
  }

  $scope.newCustomerOpen = function() {
    $scope.CreateOpen = !$scope.CreateOpen;
    $scope.CreateVenue = false;
    $scope.EditOpen = false;
    $scope.CusStep5();
    $scope.CusStep4();
    $scope.CusStep3();
    $scope.CusStep2();
    $scope.CusStep1();
    $scope.stepCus5 = false;
    $scope.stepCus6 = false;
    $scope.stepCus7 = false;
  }
  //    $scope.OpenVenue = function(){
  //        $scope.CreateOpen = false;
  //        window.location.href('addvenue');
  //    }

  $scope.EditCus = function() {
    $scope.CreateOpen = false;
    $scope.CusStep5();
    $scope.CusStep4();
    $scope.CusStep3();
    $scope.CusStep2();
    $scope.CusStep1();
    $scope.stepCus5 = false;
    $scope.stepCus6 = false;
    $scope.EditOpen = !$scope.EditOpen;
  }

  $scope.customerstep1 = true;
  $scope.stepCus1 = true;
  $scope.CusStep1 = function() {
    $scope.customerstep1 = true;
    $scope.stepCus1 = true;
    $scope.stepCus2 = false;
    $scope.customerstep2 = false;

  }
  $scope.CusStep2 = function() {
    $scope.customerstep1 = true;
    $scope.stepCus1 = false;
    $scope.stepCus2 = true;
    $scope.stepCus3 = false;
    $scope.customerstep2 = true;
    $scope.customerstep3 = false;

  }
  $scope.CusStep3 = function() {
    $scope.customerstep1 = true;
    $scope.stepCus2 = false;
    $scope.stepCus3 = true;
    $scope.stepCus4 = false;
    $scope.customerstep2 = true;
    $scope.customerstep3 = true;
    $scope.customerstep4 = false;

  }
  $scope.CusStep4 = function() {
    $scope.customerstep1 = true;
    $scope.stepCus2 = false;
    $scope.stepCus3 = false;
    $scope.stepCus4 = true;
    $scope.customerstep2 = true;
    $scope.customerstep3 = true;
    $scope.customerstep4 = true;

  }


  $scope.saveCustomerData = function(newCus) {
    $scope.list = [];
    if (newCus.role != null) {
      var response = $http.post('/facesix/rest/customer/save', $scope.newCus);
      response.success(function(data, status, headers, config) {
        $scope.list.push(data);
      });
      response.error(function(data, status, headers, config) {
        //  alert("Exception details: " + JSON.stringify({ data: data}));
      });
    }

  }

  $scope.CusStep5 = function() {
    $scope.customerstep1 = true;
    $scope.stepCus2 = false;
    $scope.stepCus3 = false;
    $scope.stepCus4 = false;
    $scope.stepCus5 = true;
    $scope.customerstep2 = true;
    $scope.customerstep3 = true;
    $scope.customerstep4 = true;



  }

  $scope.generateemail = function() {
    $scope.customerstep1 = true;
    $scope.stepCus2 = false;
    $scope.stepCus3 = false;
    $scope.stepCus4 = false;
    $scope.stepCus5 = false;
    $scope.stepCus6 = true;
    $scope.customerstep2 = true;
    $scope.customerstep3 = true;
    $scope.customerstep4 = true;
  }


  $scope.sendMail = function() {
    $scope.customerstep1 = true;
    $scope.stepCus2 = false;
    $scope.stepCus3 = false;
    $scope.stepCus4 = false;
    $scope.stepCus5 = false;
    $scope.stepCus6 = false;
    $scope.stepCus7 = true;
    $scope.customerstep2 = true;
    $scope.customerstep3 = true;
    $scope.customerstep4 = true;
  }


}]);

app.controller('AccountCtrl', ['$scope', '$http', function($scope, $http) {
  console.log(screen.width);
  $scope.mobileValidate = /^[0-9]{10}$/;
  $scope.emailValidate = /^[a-z]+[a-z0-9._]+@[a-z]+\.[a-z.]{2,5}$/;

  $scope.UpdateProfile = function(profileData) { //updating useraccount
    console.log(profileData);
    $http.post('/facesix/rest/user/save', profileData).then(function successCallback(response) {
      $scope.showalluser();
    }, function errorCallback(response) {});
  }



  $scope.newUser = function() {
    $scope.profileData = {};
    $scope.newUserBtn = true;
    $scope.showmyprofile();
  }
  $scope.addNewUser = function(profile) {}



  $scope.profileData = [];

  $scope.GetRoles = function() {
    $http.get('/facesix/rest/user/role').then(function successCallback(response) {
      $scope.role.push(response.data.profileInfo);
     console.log(response)
    }, function errorCallback(response) {
      console.log(response);
    });
  }

  $scope.GetRoles();
  $scope.GetProfileData = function() {
    $http.get('/facesix/rest/user/get').then(function successCallback(response) {
      $scope.profileData = response.data.profileInfo;
      console.log(response);
    //alert(JSON.stringify($scope.profileData));
    }, function errorCallback(response) {
      console.log(response);
    });
  }

  $scope.GetProfileData();
  if (screen.width > 1023) {
    $scope.myprofile = true;
  }
  $scope.openGP = function() {
    $scope.GPOpen = !$scope.GPOpen;
  }
  $scope.ProfileAllFalse = function() {
    $scope.myprofile = false;
    $scope.alluser = false;
    $scope.allroles = false;
    $scope.showallsupport = false;
    $scope.showalllicense = false;
    $scope.showallnotification = false;
    $scope.showProfileMenu = true;
    $scope.showProfileMenuAll = false;
    $scope.showallgp = false;
  }
  $scope.showmyprofile = function() {
    $scope.ProfileAllFalse();
    $scope.myprofile = true;
  }
  $scope.showalluser = function() {
    $scope.ProfileAllFalse();
    $scope.alluser = true;
  }
  $scope.showallrole = function() {
    $scope.ProfileAllFalse();
    $scope.allroles = true;
  }
  $scope.shownotification = function() {
    $scope.ProfileAllFalse();
    $scope.showallnotification = true;
  }
  $scope.showsupport = function() {
    $scope.ProfileAllFalse();
    $scope.showallsupport = true;
  }
  $scope.showlicence = function() {
    $scope.ProfileAllFalse();
    $scope.showalllicense = true;
  }
  $scope.showgp = function() {
    $scope.ProfileAllFalse();
    $scope.showallgp = true;
  }
  $scope.showAllProfileMenu = function() {
    $scope.showProfileMenuAll = !$scope.showProfileMenuAll;
  }
  $scope.myprofiles = function() {
    $scope.ProfileAllFalse();
    $scope.showProfileMenu = false;
    $scope.showProfileMenuAll = false;

  }

  $scope.progressdata = {
    total: 20,
    used: 5
  }
}]);