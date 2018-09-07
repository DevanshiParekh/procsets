package com.semaifour.facesix.data.jdbc;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.semaifour.facesix.domain.JSONMap;

/**
 * NetcatRowProcessor  is a row processor that forwards each row as a json string to a network host:port.
 * 
 * @author mjs
 *
 */
public class GelfNetcatRowProcessor extends NetcatRowProcessor {
	
	static Logger LOG = LoggerFactory.getLogger(GelfNetcatRowProcessor.class.getName());
	
	public GelfNetcatRowProcessor(String host, int port) {
		super(host, port);
	}

	@Override
	public void offer(JSONMap map) {
		super.offer(gelfit(map));
	}

	private JSONMap gelfit(JSONMap map) {
		JSONMap newmap = new JSONMap();
		newmap.put("version", "1.0");
		newmap.put("host", "facesix");
		newmap.put("short_message", "facesix");
		Set keys = map.keySet();
		for(Object key : keys) {
			if (!StringUtils.startsWithIgnoreCase(key.toString(), "_")) {
				newmap.put("_" + key, map.get(key));
			} else {
				newmap.put(key, map.get(key));
			}
		}
		return newmap;
	}

}
