package com.semaifour.facesix.data.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.domain.JSONMap;

public class ConsumableQueueRowMapper extends GenericRowMapper {
	
	static Logger LOG = LoggerFactory.getLogger(ConsumableQueueRowMapper.class.getName());

	private LinkedBlockingQueue<JSONMap> queue = new LinkedBlockingQueue<JSONMap>(1000);
	private RowConsumer consumer;
	private Map<String, Object> addattrs;
	private int count = 0;
	private boolean isAggregator;
	private JSONMap summary;
	
	public ConsumableQueueRowMapper(RowConsumer consumer, boolean isAggregator) {
		super();
		this.consumer = consumer;
		this.isAggregator = isAggregator;
		if (isAggregator) {
			summary = new JSONMap();
			summary.put("startTime", System.currentTimeMillis());
		}
		consumer.start(queue);
		LOG.info("started");
	}
	
	public ConsumableQueueRowMapper(RowConsumer consumer, Map<String, Object> addattrs, boolean isAggregator) {
		this(consumer, isAggregator);
		this.addattrs = addattrs;
		consumer.start(queue);
		LOG.info("started");
	}
	
	@Override
	public void begin() {
		count = 0;
		LOG.info("begin with count :" + count);
	}
	
	@Override
	public JSONMap mapRow(ResultSet rs, int rowNum) throws SQLException {
		JSONMap row = super.mapRow(rs, rowNum);
		count++;
		if (addattrs != null) {
			row.putAll(addattrs);
		}
		try {
			queue.offer(row, 10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error("failed to insert to queue :", e);
		}
		if (count % 1000 == 0) {
			LOG.info("mapped row count :" + count);
		}
		
		if (isAggregator) {
			summary.put("count", count);
			return summary;
		} else {
			return row;
		}
	}

	@Override
	public void end() {
		consumer.finish();
		summary.put("endTime", System.currentTimeMillis());
		LOG.info("end with count :" + count);
	}
	
	
}
