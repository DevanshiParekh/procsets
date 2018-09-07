package com.semaifour.facesix.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.HeartBeat;
import com.semaifour.facesix.web.WebController;

@RestController
@RequestMapping("/rest/cache")
public class CacheController  extends WebController {
	static Logger LOG = LoggerFactory.getLogger(CacheController.class.getName());

	@Autowired
	DeviceService deviceService;
	
	 @RequestMapping("/device/health/list")
	    public @ResponseBody Collection<HeartBeat> listdevicehealthcache(@RequestParam(value="uid", required=false) String uid) {
	    	if (uid == null) {
	    		HeartBeat b = deviceService.getDeviceHealth(uid);
	    		List<HeartBeat> list = new ArrayList<HeartBeat>();
	    		if (b != null) {
	    			list.add(b);
	    		}
	    		return list;
	    	} else {
	    		return deviceService.getAllDeviceHealth();
	    	}
	    }
	   
	 
    @RequestMapping("/device/health/clear")
    public @ResponseBody boolean cleardevicehealthcache(@RequestParam(value="uid", required=false) String uid) {
    	if (uid == null) {
    		deviceService.clearDeviceHealthCache();
    		return true;
    	} else {
    		return deviceService.clearDeviceHealthCache(uid) == null ? true : false;
    	}
    }
    
}