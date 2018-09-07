package com.semaifour.facesix.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class SimpleUtil {
	static Logger LOG = LoggerFactory.getLogger(SimpleUtil.class.getName());

	/*
	 * key=value,key=value will become a map
	 * 
	 */
	public static Map<String, Object> tomap(String addattrs) {
		Map<String, Object> params  = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(addattrs)) {
			for(String kv : addattrs.split("&")) {
				try {
					String[] v = kv.split(":");
					params.put(v[0], v[1]);
				} catch (Exception e) {
					LOG.warn("addattrs parse failed :" + kv);
				}
			}
		}
		return params;
	}

}
