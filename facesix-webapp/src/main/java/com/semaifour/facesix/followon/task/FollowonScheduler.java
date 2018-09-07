package com.semaifour.facesix.followon.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FollowonScheduler {

	static Logger LOG = LoggerFactory.getLogger(FollowonScheduler.class.getName());

	//@Scheduled(initialDelay=60000, fixedRate=180000)
	public void loadLog() {
		LOG.info("loading log");
	}
}
