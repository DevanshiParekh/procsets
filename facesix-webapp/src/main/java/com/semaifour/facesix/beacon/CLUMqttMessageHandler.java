package com.semaifour.facesix.beacon;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.rest.ProcessBeacons;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.ElasticService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.mqtt.DefaultMqttMessageReceiver;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.CustomerUtils;

import scala.concurrent.forkjoin.ForkJoinPool;

public class CLUMqttMessageHandler extends DefaultMqttMessageReceiver {
	
	private static String classname = CLUMqttMessageHandler.class.getName();
	Logger LOG = LoggerFactory.getLogger(classname);
	

	ForkJoinPool forkJoinPool   = new ForkJoinPool();
	
	Map<Integer,ProcessBeacons> myRecursiveTask = new HashMap<Integer,ProcessBeacons>();
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	PortionService portionService;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	CCC _CCC;
	
	@Autowired
	private ElasticService elasticService;
	
	private String indexname = "clu-report-event";

	private String cluReportOpcode = "current-location-update";
	
	@PostConstruct
	public void init() {
		indexname = getCCC().properties.getProperty("clu.report.data.table", "clu-report-event");
	}
	
	@Override
	public String getName() {
		return "CLUMqttMessageHandler";
	}
	
	@Override
	public boolean messageArrived(String topic, MqttMessage message) {
		return messageArrived(topic, message.toString());
	}
	
	@Override
	public boolean messageArrived(String topic, String message) {
		
		try {
			long stimer = System.currentTimeMillis();
			ObjectMapper mapper     = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(message, new TypeReference<HashMap<String, Object>>(){});
			
			String op = String.valueOf(map.get("opcode"));
			
			/*
			 * date = timestamp when the data was sent
			 */
			String sentTime = map.get("server_send_ts").toString();
			
			switch (op) {
			
			case "current-location-update":
				
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				DateFormat parse  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				
				if (!map.containsKey("tag_list")) {
					return false;
				}
				
				String spid 		= (String) map.get("spid");
				String serverid		= (String) map.get("uid");
				int tagCount 		= (int)map.get("tag_count");
				int max_record		= (int)map.get("max_record");
				int record_num		= (int)map.get("record_num");

				List<Map<String, Object>> tag_list = (List<Map<String, Object>>) map.get("tag_list");

				Portion p = getPortionService().findById(spid);
				String cid = p.getCid();
				String sid = p.getSiteId();
				String floorname = p.getUid();
				int height = p.getHeight();
				int width = p.getWidth();

				Customer cx = getCustomerService().findById(cid);
				boolean debugLog = cx.getLogs() != null && cx.getLogs().equals("true") ? true : false;
				
				getCustomerUtils().logs(debugLog, classname,"MESSAGE Tag Count ===> " + map.get("tag_count")+ "date = " +sentTime);
				
				TimeZone timeZone = getCustomerUtils().FetchTimeZone(cx.getTimezone());
				format.setTimeZone(timeZone);
				String date = format.format(new Date());
				Date parsedSentTime = parse.parse(sentTime);
				String recSentTime = format.format(parsedSentTime);
				
				ProcessBeacons processBeacons = new ProcessBeacons();
				
				processBeacons.setTagDetail(tag_list);
				processBeacons.setCustomerDetails(cid,serverid,debugLog,recSentTime);
				processBeacons.setPortionDetails(sid,spid,floorname,height,width);
				processBeacons.setTimeZone(timeZone);
				/*
				 * record date is the field included for testing purpose to find out when was the data
				 * actually sent and when it is getting processed.
				 */
				processBeacons.setRecordSentDate(recSentTime);
				processBeacons.setRecordSeenDate(date);
				forkJoinPool.execute(processBeacons);
				
				HashMap<String, Object> cluPost = new HashMap<String, Object>();

				parse.setTimeZone(TimeZone.getTimeZone("UTC"));
				String formatedSentTime = parse.format(parsedSentTime);
				cluPost.put("opcode", cluReportOpcode);
				cluPost.put("uid", serverid);
				cluPost.put("spid", spid);
				cluPost.put("tag_count", tagCount);
				cluPost.put("record_num", record_num);
				cluPost.put("max_record", max_record);
				cluPost.put("tag_list", tag_list);
				cluPost.put("server_send_ts", parse.parse(formatedSentTime));
				cluPost.put("dateTime", recSentTime);
				
				if(getElasticService() != null) {
					//getCustomerUtils().logs(debugLog, classname,"CLU POST = "+cluPost);
					getElasticService().post(indexname, "cluReport", cluPost);
				}
				
				long eTimer = System.currentTimeMillis() - stimer;
				
				getCustomerUtils().logs(debugLog, classname, " Time taken to complete the record " + " = " + eTimer+" date = "+sentTime);

				break;
			default:
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public CustomerService getCustomerService() {

		if (customerService == null) {
			customerService = Application.context.getBean(CustomerService.class);
		}

		return customerService;

	}
	
	public CustomerUtils getCustomerUtils() {

		if (customerUtils == null) {
			customerUtils = Application.context.getBean(CustomerUtils.class);
		}

		return customerUtils;
	}
	
	private PortionService getPortionService() {
		if (portionService == null) {
			portionService = Application.context.getBean(PortionService.class);
		}

		return portionService;
	}

	public CCC getCCC() {
		if (_CCC == null) {
			_CCC = Application.context.getBean(CCC.class);
		}
		return _CCC;
	}
	
	public ElasticService getElasticService() {
		if (elasticService == null) {
			elasticService = Application.context.getBean(ElasticService.class);
		}
		return elasticService;
	}
}
