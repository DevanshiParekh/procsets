package com.semaifour.facesix.task;

import java.util.Timer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.semaifour.facesix.domain.JSONMap;
import com.semaifour.facesix.spring.CCC;

/**
 * 
 * 
 * Example config:
 * 
 * facesix.timertask.list=task1,task2
 * facesix.timertask.task1.configJson={"classname" : "my.class.name", "delay" : "300000", "period" : "3600000", "attr1" : "value1", "attrx: "valuex", "attry" : ["a","b","c"] }
 * 
 * 
 * @author mjs
 *
 */
public class FSTimerTaskService {
	
	static Logger LOG = LoggerFactory.getLogger(FSTimerTaskService.class.getName());

	@Autowired
	CCC _CCC;
	
	public FSTimerTaskService() {
	}
	
	@PostConstruct
	public void init() {
		String tasks = _CCC.properties.getProperty("facesix.timertask.list");
		if (tasks != null) {
			String[] ts = tasks.split(",");
			for(String t : ts) {
				try {
					String configJson = _CCC.properties.getProperty("facesix.timertask." + t + ".configJson");
					JSONMap configMap = JSONMap.toJSONMap(configJson);
					
					String classname = configMap.getString("classname");
					long delay = configMap.getLong("delay", 60*1000);
					long period = configMap.getLong("period", -1);

					Class<?> clazz = Class.forName(classname);
					Object task = clazz.newInstance();
					if (task instanceof FSTimerTask  && period > 1000) {
						((FSTimerTask)task).setParameters(configMap);
						Timer timer = new Timer(t, true);
						timer.schedule((FSTimerTask)task, delay, period);
					} else {
						LOG.warn("Can't initialize class %s | period: %l. It must be a FSTimerTask and schedule.period > 1000", classname, period);
					}
				} catch(Exception e) {
					LOG.warn("Failed to scheduled job :%s", t, e);
				}
			}
		}
	}

}
