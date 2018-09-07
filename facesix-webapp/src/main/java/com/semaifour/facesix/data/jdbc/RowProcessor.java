package com.semaifour.facesix.data.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.domain.JSONMap;

public class RowProcessor {
	static Logger LOG = LoggerFactory.getLogger(RowProcessor.class.getName());

	public void begin() {
		LOG.info("begin");
	}

	public void end() {
		LOG.info("end");
	}

	public void offer(JSONMap map) {	
		LOG.debug("offered :" + map.toJSONString());
	}

}
