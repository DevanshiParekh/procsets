package com.semaifour.facesix.data.jdbc;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.domain.JSONMap;

public class RowConsumer implements Runnable {
	
	static Logger LOG = LoggerFactory.getLogger(RowConsumer.class.getName());

	LinkedBlockingQueue<JSONMap> queue = null;
	private RowProcessor processor = null;
	private boolean finished = false;
	private boolean started = false;
	private int     retry = 0;
	private int     rowcount = 0;
	
	public RowConsumer(RowProcessor processor) {
		this.processor = processor;
		this.processor.begin();
	}
	
	public void start(LinkedBlockingQueue<JSONMap> queue) {
		this.queue = queue;
		Thread t = new Thread(this);
		t.start();
		LOG.info("start");
	}
	
	public void finish() {
		LOG.info("finish");
		finished = true;
	}

	@Override
	public void run() {
		LOG.info("consumer running");
		try {
			JSONMap map = null;
			do {
				map = queue.poll(10, TimeUnit.SECONDS);
				if (map != null) {
					rowcount++;
					retry = 0;
					started = true;
					processor.offer(map);
				} else {
					retry++;
				}
			} while (!finished && retry <= 3);
			LOG.info("existing with status :" + (finished ? "FORCED" : "NORMAL") + ", retry :" + retry + ", rowcount :" + rowcount);
		} catch (Exception e) {
			LOG.info("Error occurred reading from queue. TERMINATED", e);
		}
		processor.end();
		LOG.info("consumer done");
	}
}
