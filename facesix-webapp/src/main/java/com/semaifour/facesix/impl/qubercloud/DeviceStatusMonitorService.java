package com.semaifour.facesix.impl.qubercloud;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.spring.CCC;

@Service
public class DeviceStatusMonitorService {
	//private static Logger LOG = LoggerFactory.getLogger(DeviceStatusMonitorService.class.getName());

	@Autowired
	CCC _CCC;
	
	@PostConstruct
	public void init() {
		if (_CCC.properties.getBoolean("qubercloud.devicekeepalive.enabled")) {
			//TimerTask timerTask = new DeviceKeepAlive();
			//Timer timer 		= new Timer(true);
			//timer.scheduleAtFixedRate(timerTask, 0, 1800*1000);
		}		
	}
}
