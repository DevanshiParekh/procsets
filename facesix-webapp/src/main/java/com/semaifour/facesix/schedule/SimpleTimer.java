package com.semaifour.facesix.schedule;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleTimer {
	Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@PostConstruct
	public void init() {
		LOG.info("initialized...");
	}
	
}
