app.factory('data', ['$http', function($http) {
    
    var urlBase = '/facesix';
    var data = {};
    
    /*Login*/
    data.Login = function(datas){return $http.post(urlBase + '/qubercloud/welcome', datas);};
    
    /*Contact Us*/
    data.ContactUs = function(datas){return $http.post(urlBase + 'qubercloud/sendmail', datas);};
    
    /*Account Authentication*/
    data.Account = function(datas){return $http.post(urlBase + 'web/site/portion/save', datas);};
    
    /*Venue*/
    data.GetAllVenue = function(){return $http.get(urlBase + 'rest/site/portion/networkdevice/view?sid=');};
    data.GetVenue = function(datas){return $http.get(urlBase + 'rest/site/portion/networkdevice/view?sid=' + datas.VenueId);};
    data.CreateVenue = function(datas){return $http.post(urlBase + 'web/site/save', datas);};
    data.DeleteVenue = function(datas){return $http.post(urlBase + 'web/site/delete', datas);};
    
    /*Floor*/
    data.GetAllFloor = function(){return $http.get(urlBase + 'rest/site/portion/networkdevice/list?spid=');};
    data.GetFloor = function(datas){return $http.get(urlBase + 'rest/site/portion/networkdevice/list?spid=' + datas.FloorId);};
    data.CreateFloor = function(datas){return $http.post(urlBase + 'web/site/portion/save', datas);};
    data.DeleteFloor = function(datas){return $http.post(urlBase + 'web/site/portion/delete', datas);};
    
    /*Dashboard*/
    /* Switch/Server/Device/Sensor Dashboard */
    
    data.GetSSDSDashboard = function(datas){return $http.get(urlBase + 'rest/site/portion/networkdevice/list?uid=' + datas.DeviceId);};
    
    /*Floor Dashboard*/
    data.GetFloorDashboard = function(datas){return $http.get(urlBase + 'rest/site/portion/networkdevice/rxtx?spid=' + datas.FloorId);};
    
    /*Venue Dashboard*/
    data.GetVenueDashboard = function(datas){return $http.get(urlBase + 'rest/site/portion/networkdevice/rxtx?sid=' + datas.VenueId);};
    
    /*Log Dashboard*/
    data.GetLogDashboard = function(){return $http.get(urlBase + 'rest/entity/query/gw_logs');};
    
    /*Active Client*/
    data.GetActiveClient = function(){return $http.get(urlBase + 'rest/entity/query/gw_cli');};
    
    /*Type Of Devices*/
    data.GetTypeOfDevices = function(datas){return $http.get(urlBase + 'rest/entity/query/typeof');};
    
    /*Active Interfaces*/
    data.GetActiveInterfaces = function(){return $http.get(urlBase + 'rest/entity/query/activeintf');};
    
    /*Netflow*/
    data.GetNetflow = function(){return $http.get(urlBase + 'rest/entity/query/netflow');};
    
    /*Connected Devices*/
    data.GetConnectedDevices = function(datas){return $http.get(urlBase + 'rest/entity/query/devconn');};
    
    /*Active Connections*/
    data.GetActiveConnections = function(datas){return $http.get(urlBase + 'rest/entity/query/activeconn');};
        
    return data;
    
}]);