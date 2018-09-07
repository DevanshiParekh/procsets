package com.semaifour.facesix.impl.qubercloud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.HeartBeat;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.rest.FSqlRestController;
import com.semaifour.facesix.rest.NetworkDeviceRestController;
import com.semaifour.facesix.util.CustomerUtils;

public class DeviceKeepAlive extends TimerTask {

	@Autowired
	NetworkDeviceService 	_networkDeviceService;
	
	@Autowired
	ClientDeviceService 	clientDeviceService;
	   
	@Autowired
	FSqlRestController 		_fsqlRestController;	
	
	@Autowired
	DeviceService 			_deviceService;
	
	@Autowired
	CustomerUtils 			_customerUtils;
	
	@Autowired
	private BeaconDeviceService _beaconDeviceService;
	
	static Logger LOG = LoggerFactory.getLogger(NetworkDeviceRestController.class.getName());
	public static final List<Map<String, Object>> EMPTY_LIST_TIM = new ArrayList<Map<String, Object>>();
	
	public void run() {
	  if (getNetworkDeviceService() == null || getDeviceService() == null || getFSqlRestController() == null) {
		  LOG.info("looks like it is not ready yet...will try again later");
		  return;
	  }
	  completeTask();
    }

    private void completeTask() {
    	
    	List<Map<String, Object>>  logs = EMPTY_LIST_TIM;
    	
    	Iterable<Device> devices = getDeviceService().findAll();
    	String fsql  = "index=qubercomm_*,sort=timestamp desc,size=1,query=keep_alive:\"Qubercloud Manager\" AND timestamp:>now-" + "30m" + " AND ";
    	
    	if (devices != null) {

    		for (Device nd : devices) {

		    	fsql  = "index=qubercomm_*,sort=timestamp desc,size=1,query=keep_alive:\"Qubercloud Manager\" AND timestamp:>now-" + "30m" + " AND ";
				fsql  = fsql + "uid:\"" + nd.getUid() + "\"";
	        	fsql  = fsql + "|value(timestamp,time,NA)|table";
				logs  = getFSqlRestController().query(fsql); 								
				
				if (logs.size() == 0 && !nd.getStatus().equals("inactive")) {
					
			    	LOG.info("Device State changed to Inactive" + nd.getUid());

					// Node is in-active
					nd.setState("inactive");
					nd.setModifiedBy("cloud");
					nd.setModifiedOn(new Date(System.currentTimeMillis()));   
					getDeviceService().save(nd, false);
				} 
				
    		}
    		
    	}   
    	
    }
    
	private NetworkDeviceService getNetworkDeviceService() {
		if (_networkDeviceService == null && Application.context != null) {
			_networkDeviceService = Application.context.getBean(NetworkDeviceService.class);
		}
		return _networkDeviceService;
	}  
	
	private DeviceService getDeviceService() {
		if (_deviceService == null && Application.context != null) {
			_deviceService = Application.context.getBean(DeviceService.class);
		}
		return _deviceService;
	}	
	
	private FSqlRestController getFSqlRestController() {
		if (_fsqlRestController == null && Application.context != null) {
			_fsqlRestController = Application.context.getBean(FSqlRestController.class);
		}
		return _fsqlRestController;
	} 	
	
	private BeaconDeviceService getBeaconDeviceService() {
		
		try {
			if (_beaconDeviceService == null) {
				_beaconDeviceService = Application.context.getBean(BeaconDeviceService.class);
			}
		} catch (Exception e) {
			
		}
		
		try {
			if (_beaconDeviceService == null) {
				LOG.info("Unable to load BeaconDeviceService, please check");
			}
		} catch (Exception e) {
			
		}
		return _beaconDeviceService;
	}

}
