app.controller('deviceCtrl', ['$scope','$http', '$routeParams', function($scope, $http, $routeParams){
    
    $scope.GetDeviceData = function(){
//        $http({
//              method:'Get',
//             url:'/facesix/rest/site/portion/networkdevice/getpeers?uid='+$routeParams.uid,
//              headers: {'content-type': 'application/json'}
//          }).then(function successCallback(response){
            
            $scope.activeClients = [{"devtype":"Intel Corporate","rssi":"46 ","mac_address":"34:e6:ad:6d:d4:a9","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"Hon Hai Precision Ind. Co.,Ltd.","rssi":"56 ","mac_address":"68:14:01:28:72:d5","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"Raspberry Pi Foundation","rssi":"33 ","mac_address":"b8:27:eb:f5:73:72","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"D-Link International","rssi":"41 ","mac_address":"54:b8:0a:5e:ef:95","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"Intel Corporate","rssi":"51 ","mac_address":"2c:6e:85:cb:5c:05","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"Apple, Inc.","rssi":"25 ","mac_address":"5c:ad:cf:11:0c:a7","ip_address":"NULL","time":"0 ","client_type":"mac","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"Cisco-Linksys, LLC","rssi":"40 ","mac_address":"00:25:9c:b2:6a:ee","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"Samsung Electronics Co.,Ltd","rssi":"31 ","mac_address":"f0:5b:7b:72:b4:a4","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"Letv Mobile and Intelligent Information Technology (Beijing) Cor...","rssi":"36 ","mac_address":"84:73:03:c5:e1:bb","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"Apple, Inc.","rssi":"48 ","mac_address":"34:36:3b:83:88:18","ip_address":"NULL","time":"0 ","client_type":"mac","ap":"54:3d:37:1e:aa:68","radio":"2.4Ghz"},{"devtype":"Liteon Technology Corporation","rssi":"20 ","mac_address":"30:52:cb:31:2c:9d","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:9e:aa:6e","radio":"5Ghz"},{"devtype":"Intel Corporate","rssi":"28 ","mac_address":"2c:6e:85:68:39:09","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:9e:aa:6e","radio":"5Ghz"},{"devtype":"Apple, Inc.","rssi":"21 ","mac_address":"34:36:3b:87:85:30","ip_address":"NULL","time":"0 ","client_type":"mac","ap":"54:3d:37:9e:aa:6e","radio":"5Ghz"},{"devtype":"Motorola (Wuhan) Mobility Technologies Communication Co., Ltd.","rssi":"11 ","mac_address":"98:0c:a5:18:c8:cc","ip_address":"NULL","time":"0 ","client_type":"laptop","ap":"54:3d:37:9e:aa:6e","radio":"5Ghz"}];
            
        
//        }, function errorCallback(response){
//              //console.log(response);
//          });
        
        
        
        
        
        
        
        
        
    }
    $scope.GetConnectedInterfaces = function(){
//        $http({
//              method:'Get',
//             url:'/facesix/rest/site/portion/networkdevice/intf?uid='+$routeParams.uid,
//              headers: {'content-type': 'application/json'}
//          }).then(function successCallback(response){
            
            var data = [{"vapcount":"1","device":"wlan2g","status":"enabled"},{"vapcount":"1","device":"wlan5g","status":"enabled"},{"vapcount":"1","device":"ble","status":"disabled"}];
            data.name = [];
            data.value = [];
            for(i=0; i<data.length; i++){
                data.name.push(data[i].device);
                data.value.push(data[i].vapcount);
            }
            $scope.ConnectedInterfaces = data;
         //console.log($scope.ConnectedInterfaces);
        
//        }, function errorCallback(response){
//              //console.log(response);
//          });
        
    }
    $scope.ActiveBlock = function(){
//        $http({
//              method:'Get',
//             url:'/facesix/rest/site/portion/networkdevice/getstacount?uid='+$routeParams.uid,
//              headers: {'content-type': 'application/json'}
//          }).then(function successCallback(response){
            
            var data = [["Active","0"],["Blocked",null]]
            data.name = [];
            data.value = [];
            for(i=0; i<data.length; i++){
                data.name.push(data[i][0]);
                data.value.push(data[i][1]);
            }
            $scope.typeOfDevices = data;
         console.log($scope.typeOfDevices);
        
//        }, function errorCallback(response){
//              //console.log(response);
//          });
        
    }
    
    
    $scope.GetTxRX = function(){
 //   	$http({
//              method:'Get',
//             url:'/facesix/rest/site/portion/networkdevice/rxtx?uid='+$routeParams.uid,
//              headers: {'content-type': 'application/json'}
//          }).then(function successCallback(response){
//              //console.log(response);
			$scope.sampleRxTx = [{"Tx":"-1559183487 bytes","Rx":"611200272 bytes","time":"2016-11-25 14:11:57.198"},{"Tx":"-1559277580 bytes","Rx":"611188768 bytes","time":"2016-11-25 14:11:51.856"},{"Tx":"-1559344484 bytes","Rx":"611176765 bytes","time":"2016-11-25 14:11:46.512"},{"Tx":"-1559452031 bytes","Rx":"611165368 bytes","time":"2016-11-25 14:11:41.170"},{"Tx":"-1559516429 bytes","Rx":"611153501 bytes","time":"2016-11-25 14:11:35.826"},{"Tx":"-1559183487 bytes","Rx":"611200272 bytes","time":"2016-11-25 14:11:57.198"},{"Tx":"-1559277580 bytes","Rx":"611188768 bytes","time":"2016-11-25 14:11:51.856"},{"Tx":"-1559344484 bytes","Rx":"611176765 bytes","time":"2016-11-25 14:11:46.512"},{"Tx":"-1559452031 bytes","Rx":"611165368 bytes","time":"2016-11-25 14:11:41.170"},{"Tx":"-1559516429 bytes","Rx":"611153501 bytes","time":"2016-11-25 14:11:35.826"}];

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
					for(i=0; i < rxtxlength; i++){
					//console.log(rxtxvalue[i].time);
						var Tx = Math.round(parseInt(rxtxvalue[i].Tx.replace(" bytes", ""))/1000000);
						var Rx = Math.round(parseInt(rxtxvalue[i].Rx.replace(" bytes", ""))/1000000);
						
						var formatedTime = rxtxvalue[i].time;
            			var c_formatedTime = formatedTime.substr(0, 10) + "T" + formatedTime.substr(11, 8);
            			c_formatedTime = new Date (c_formatedTime);
                        $scope.dataRxTx.name.push(c_formatedTime.getHours() + ":" + c_formatedTime.getMinutes());
						//$scope.dataRxTx.name.push(rxtxvalue[i].time);
						$scope.dataRxTx.valueTx.push(Tx);
						
						$scope.dataRxTx.valueRx.push(Rx);
					};
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
    
    
    $scope.GetPeers = function(){
//    	$http({
//              method:'Get',
//             url:'/facesix/rest/site/portion/networkdevice/getdevcon?uid='+$routeParams.uid,
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
					for(i=0; i < plength; i++){
						$scope.dataPeers.name.push(pvalue[i][0]);
						$scope.dataPeers.value.push(pvalue[i][1]);
					};
					//console.log($scope.dataPeers);
					
//          }, function errorCallback(response){
//              console.log(response);
//          });
    }
    
    
    
    $scope.frameVaps = function(){
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
		for(i=0; i < length; i++){
			$scope.dataVaps.name.push(value[i][0]);
			$scope.dataVaps.value.push(value[i][1]);
		};
		//console.log($scope.dataVaps);
	}
	$scope.frameVaps();
    $scope.GetVaps = function(){
    	$http({
                method:'Get',
                url:'/facesix/rest/site/portion/networkdevice/getdevtype?uid='+$routeParams.uid,
                headers: {'content-type': 'application/json'}
            }).then(function successCallback(response){
                console.log(response);
            }, function errorCallback(response){
                console.log(response);
            });
    }
    
    
    
    
    
    
    $scope.linedata = {
        series: ['Tx', 'Rx'],
        colours: ["#03A9F4", "#2196F3"]
    };
    $scope.init = function(){
        $scope.GetDeviceData();
        $scope.currentPage = 0;
        $scope.pageSize = 5;
        $scope.GetConnectedInterfaces();
        $scope.ActiveBlock();
        $scope.GetPeers();
        //$scope.GetVaps();
    }
    $scope.init();
    
}]);
app.filter('startFrom', function() {
    return function(input, start) {
        start = +start; //parse to int
        return input.slice(start);
    }
});