package com.semaifour.facesix.followon.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.datasource.Datasource;
import com.semaifour.facesix.data.elasticsearch.datasource.DatasourceService;
import com.semaifour.facesix.data.jdbc.ConsumableQueueRowMapper;
import com.semaifour.facesix.data.jdbc.GelfNetcatRowProcessor;
import com.semaifour.facesix.data.jdbc.NetcatRowProcessor;
import com.semaifour.facesix.data.jdbc.Reader;
import com.semaifour.facesix.data.jdbc.RowConsumer;
import com.semaifour.facesix.data.jdbc.RowProcessor;
import com.semaifour.facesix.domain.JSONMap;
import com.semaifour.facesix.task.FSTimerTask;
import com.semaifour.facesix.util.SimpleUtil;

public class DatasourceExecutorTask extends FSTimerTask {
	
	static Logger LOG = LoggerFactory.getLogger(DatasourceExecutorTask.class.getName());
	
	private Datasource datasource;
	private String query = null;

	@Override
	public void run() {
		try {
			
			String wty = datasource.getSettings().getProperty("writeType");
			String writeTo = datasource.getSettings().getProperty("writeTo");
			String addattrs = datasource.getSettings().getProperty("addattrs");
			
			//execute query
			LOG.info("Executing Datasource :" + datasource.getUid());
			LOG.info("Datasource Query :" + query);
			try {
				boolean isAggregation = false;
				RowProcessor processor = null;
				if (writeTo.indexOf("gelf:") >= 0) {
					processor = new GelfNetcatRowProcessor(writeTo.split(":")[1], Integer.parseInt(writeTo.split(":")[2]));
					isAggregation = true;
				} else if (writeTo.indexOf("tcp:")  >= 0) {
					processor = new NetcatRowProcessor(writeTo.split(":")[1], Integer.parseInt(writeTo.split(":")[2]));
					isAggregation = true;
				} else {
					processor = new RowProcessor();
					isAggregation = true;
				}
						
				RowConsumer consumer = new RowConsumer(processor);
				Reader reader = new Reader(datasource);
				List<JSONMap> list = reader.read(query, new ConsumableQueueRowMapper(consumer, SimpleUtil.tomap(addattrs), isAggregation));
				
				LOG.info("Datasource execution done with count:" + list.size());
			} catch (Exception e) {
				LOG.info("Datasource execution failed", e);
			}
			LOG.info("Done Executing Datasource :" + datasource.getUid());
		} catch(Exception e){
			LOG.info("Failed to run :", e);
		}
	}

	@Override
	public void setParameters(JSONMap configMap) {
		DatasourceService service = Application.context.getBean(DatasourceService.class);
		datasource = service.findOneByUid(configMap.getString("datasource"));
		query = configMap.getString("query");
		if (query == null) {
			query = datasource.getSettings().getProperty(configMap.getString("queryname"));
		}
		if (query == null) {
			query = datasource.getSettings().getProperty("query");
		}
	}


}
