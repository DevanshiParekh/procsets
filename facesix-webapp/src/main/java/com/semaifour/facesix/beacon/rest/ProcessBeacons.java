package com.semaifour.facesix.beacon.rest;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.ElasticService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.jni.bean.Coordinate;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.CustomerUtils;

import scala.concurrent.forkjoin.RecursiveTask;

@Service
public class ProcessBeacons extends RecursiveTask<Integer> {

	private static final long serialVersionUID = 1L;
	
	private static String classname = ProcessBeacons.class.getName();
	static Logger LOG 	            = LoggerFactory.getLogger(classname);

	@Autowired
	BeaconService beaconservice;

	@Autowired
	PortionService portionservice;

	@Autowired
	CustomerService customerservice;

	@Autowired
	GeoFinderRestController geoFinderRestController;

	@Autowired
	private ElasticService elasticService;
	
	@Autowired
	BeaconDeviceService beaconDeviceService;
	
	@Autowired
	CCC _CCC;
	
	@Autowired
	CustomerUtils customerUtils;
	
	private String indexname 	= "clu-report-event";
	
	@PostConstruct
	public void init() {
		indexname = _CCC.properties.getProperty("clu.report.data.table", indexname);
	}

	String cid = "";
	String sid = "";
	String spid = "";
	String date = "";
	String serverId = "";
	String floorName = "";
	
	List<Map<String,Object>>  tagDetail;
	boolean debugLog = false;
	int height = 0;
	int width = 0;
	int reportFlag = 0;
	int tagCount = 0;
	String tagId = "";
	HashMap<String, String> reportMap;
	int 	recordcount = 0;

	private String recordSent;
	private String recordSeen;
	private TimeZone timeZone;
	
	public void setRecordSentDate(String recordDate) {
		this.recordSent = recordDate;
	}
	
	public void setRecordSeenDate(String receivedTime) {
		this.recordSeen = receivedTime;
	}
	
	public void setTagDetail(List<Map<String, Object>> tagDetail) {
		this.tagDetail = tagDetail;
	}

	public void setCustomerDetails(String cid, String serverid, boolean debugLog, String date) {
		this.cid = cid;
		this.serverId = serverid;
		this.debugLog = debugLog;
		this.date = date;
	}

	public void setPortionDetails(String sid, String spid, String floorname, int height, int width) {
		
		this.sid = sid;
		this.spid = spid;
		this.floorName = floorname;
		this.height = height;
		this.width = width;
	}
	
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	
	@Override
	protected Integer compute() {
		DateFormat format 			= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		long computeStart = System.currentTimeMillis();
		int tag_count 						= 0;
		
		try {

			List<ProcessBeacons> rpttasks = new ArrayList<ProcessBeacons>();

			Map<String, Beacon> beaconMap   = null;
			List<Coordinate> coordinates 	= null;
			List<Beacon> beaconlist 		= null;

			String spid = this.spid;
			String cid = this.cid;
			String serverId = this.serverId;
			String date = this.date;
			String sid = this.sid;
			String floorName = this.floorName;
			int height = this.height;
			int width = this.width;
			boolean debugLog = this.debugLog;
			format.setTimeZone(this.timeZone);

			beaconMap = new HashMap<String, Beacon>();

			coordinates = new ArrayList<Coordinate>();
			beaconlist = new ArrayList<Beacon>();

			if (this.tagDetail == null) {
				return 0;
			}

			getCustomerUtils().logs(debugLog, classname, "----- New Thread ----  " + Thread.currentThread().getName()
					+ " Processing Data Sent at " + this.recordSent + " Recevied at " + this.recordSeen);

			List<Map<String, Object>> tagDetail = this.tagDetail;
			Iterator<Map<String, Object>> taglistiter = tagDetail.iterator();

			HashMap<String, HashMap<String, String>> recInfo = new HashMap<String, HashMap<String, String>>();
			Map<String, Map<String, Object>> deviceMap = new HashMap<String, Map<String, Object>>();
			HashMap<String, String> rcList = null;

			while (taglistiter.hasNext()) {

				Map<String, Object> tagiter = taglistiter.next();

				String tagid = (String) tagiter.get("uid");

				if (tagid == null) {
					continue;
				}

				Beacon beacon = getBeaconService().findOneByMacaddr(tagid);
				if (beacon == null) {
					continue;
				}

				tag_count++;

				Map<String, Object> coordinate = (Map<String, Object>) tagiter.get("coordinate");

				double accuracy = Double.valueOf(tagiter.get("accuracy").toString());
				double range = Double.valueOf(tagiter.get("range").toString());

				Double latitude = Double.valueOf(coordinate.get("latitude").toString());
				Double longitude = Double.valueOf(coordinate.get("longitude").toString());

				coordinates.add(new Coordinate(latitude, longitude, tagid));

				String reciverUid = "NA";
				String alias = "NA";
				double distance = 0;

				if (tagiter.containsKey("receiver_list")) {

					List<Map<String, Object>> reciverArray = (List<Map<String, Object>>) tagiter.get("receiver_list");

					if (reciverArray != null && reciverArray.size() > 0) {

						Map<String, Object> ob = reciverArray.get(0);
						reciverUid = (String) ob.get("uid");
						distance = Double.valueOf(ob.get("distance").toString());

						if (reciverUid != null) {
							Map<String, Object> dvMap = new HashMap<String, Object>();
							if (deviceMap.containsKey(reciverUid)) {
								dvMap = deviceMap.get(reciverUid);
								alias = (String) dvMap.get("alias");
							} else {
								BeaconDevice device = getBeaconDeviceService().findOneByUid(reciverUid);
								if (device != null) {
									alias = device.getAlias().toUpperCase();
								}
								dvMap.put("alias", alias);
								deviceMap.put(reciverUid, dvMap);
							}
						}
					}

				}

				rcList = new HashMap<String, String>();
				rcList.put("reciverUid", reciverUid);
				rcList.put("alias", alias);
				recInfo.put(tagid, rcList);

				long lastSeen = System.currentTimeMillis();

				String state = beacon.getState();
				long lastactive = beacon.getLastactive();
				String mailSent = beacon.getMailsent();

				if (lastactive < (lastSeen - 3600000) && lastactive != 0) { // 1hr
					beacon.setState("idle");
				} else if (state == null || !state.equals("active")) {
					beacon.setState("active");
					if (state != null && !state.equals("idle")) {
						beacon.setEntryFloor(date);
						beacon.setEntry_loc(date);
					}
				}

				if (mailSent == null || mailSent.equals("true")) {
					beacon.setMailsent("false");
				}

				beacon.setLastReportingTime(date);
				beacon.setLastSeen(lastSeen);
				beacon.setServerid(serverId);

				beacon.setAccuracy(accuracy);
				beacon.setRange(range);
				beacon.setLat(latitude);
				beacon.setLon(longitude);
				beacon.setDistance(distance);

				beacon.setHeight(height);
				beacon.setWidth(width);
				beaconMap.put(tagid, beacon);
			}

			long jni_response_start = System.currentTimeMillis();
			String jniResponse = getGeoFinderRestController().Coordinate2Pixel(spid, coordinates);
			long jni_response_elapsed = System.currentTimeMillis() - jni_response_start;

			getCustomerUtils().logs(debugLog, classname, " Time Taken for JNI Response " + jni_response_elapsed);

			if (jniResponse != null) {

				net.sf.json.JSONObject locationinfo = net.sf.json.JSONObject.fromObject(jniResponse);

				if (locationinfo.containsKey("result")) {

					net.sf.json.JSONArray tagcoordinates = (net.sf.json.JSONArray) locationinfo.get("result");

					Iterator<net.sf.json.JSONObject> list = tagcoordinates.iterator();

					while (list.hasNext()) {

						net.sf.json.JSONObject object = list.next();
						String x = object.getString("x");
						String y = object.getString("y");
						String object_macaddr = object.getString("macaddr");

						if (!beaconMap.containsKey(object_macaddr)) {
							continue;
						}
						Beacon beacon = beaconMap.get(object_macaddr);

						String prev_x = beacon.getX();
						String prev_y = beacon.getY();
						String tagid = beacon.getMacaddr();
						String tagCid = beacon.getCid();

						if (!x.isEmpty() && x.length() > 0) {
							beacon.setX(x);
						}
						if (!y.isEmpty() && y.length() > 0) {
							beacon.setY(y);
						}

						if (prev_x != null && prev_y != null && !x.isEmpty() && !y.isEmpty()
								&& (!prev_x.equals(x) || !prev_y.equals(y)) || beacon.getLastactive() == 0) {

							beacon.setLastactive(System.currentTimeMillis());
						}

						if (!recInfo.containsKey(tagid)) {
							continue;
						}
						rcList = recInfo.get(tagid);

						if (!tagCid.equals(cid)) {
							LOG.info("+++++++ TAG ID DOES NOT BELONG TO THIS CUSTOMER +++++++");
							LOG.info(" TAG ID " + tagid + " SPID " + spid);
							continue;
						}

						String prev_reciverUid = beacon.getReciverinfo();
						String prev_spid = beacon.getSpid();
						String reciverUid = rcList.get("reciverUid");
						String alias = rcList.get("alias");
						String entry_loc = beacon.getEntry_loc();
						String entry_floor = beacon.getEntryFloor();

						if (!spid.equalsIgnoreCase(prev_spid) || !reciverUid.equalsIgnoreCase(prev_reciverUid)) {

							if (entry_loc == null || entry_loc.isEmpty()) {
								beacon.setEntry_loc(date);
							}
							if (entry_floor == null || entry_floor.isEmpty()) {
								beacon.setEntryFloor(date);
							}

							if (prev_spid == null || !prev_spid.equals(spid)) {
								beacon.setSid(sid);
								beacon.setSpid(spid);
								beacon.setEntryFloor(date);

								getCustomerUtils().logs(debugLog, classname, " +++ FLOOR  CHANGED SPID ID +++ "
										+ " TAG ID " + tagid + " Prev Spid " + prev_spid + " Cur SPID "+spid);
			
							}
							if (prev_reciverUid == null || !prev_reciverUid.equals(reciverUid)) {
								beacon.setEntry_loc(date); // location
								beacon.setReciverinfo(reciverUid);

								getCustomerUtils().logs(debugLog, classname, " +++ LOCATION CHANGE RECIVER ID +++ "
										+ " TAG ID " + tagid + " PREVIOUS LOCATION ID " + prev_reciverUid + " CUR LOCATION ID "+reciverUid);
							}
						}
						String cur_time = format.format(new Date());
						getCustomerUtils().logs(debugLog, classname, "TAGID UPDATED TIME" + cur_time);
						Date recordSeen = format.parse(this.recordSeen);
						Date recordSent = format.parse(this.recordSent);
						Date recordUpdate = format.parse(cur_time);

						double avgUpdateTime = beacon.getAvgUpdateTime();
						double avgProcessTime = beacon.getAvgProcessTime();
						double avgReceiveTime = beacon.getAvgReceiveTime();

						avgUpdateTime = (avgUpdateTime + getBeaconService().getDiff(recordUpdate, recordSent)) / 2;
						avgProcessTime = (avgProcessTime + getBeaconService().getDiff(recordUpdate, recordSeen)) / 2;
						avgReceiveTime = (avgReceiveTime + getBeaconService().getDiff(recordSeen, recordSent)) / 2;

						beacon.setLocation(floorName);
						beacon.setReciveralias(alias);

						beacon.setRecordSeen(format.parse(this.recordSeen));
						beacon.setRecordSent(format.parse(this.recordSent));
						beacon.setRecordUpdate(format.parse(cur_time));
						beacon.setAvgUpdateTime(avgUpdateTime);
						beacon.setAvgProcessTime(avgProcessTime);
						beacon.setAvgReceiveTime(avgReceiveTime);

						beaconlist.add(beacon);
					}
					getBeaconService().save(beaconlist);
					getCustomerUtils().logs(debugLog, classname, "beacon list saved data received date = "+this.recordSent);

				}
			}

			for (ProcessBeacons r : rpttasks) {
				recordcount = recordcount + r.join();
			}

			long elcompute = System.currentTimeMillis() - computeStart;
			getCustomerUtils().logs(debugLog, classname, " Compute over " + elcompute + "date = " + this.recordSent);

		} catch (Exception e) {
			getCustomerUtils().logs(debugLog,classname," FORK Join Tag Processing error " +e);
		}
		return tag_count;
	}
	

	public BeaconService getBeaconService() {
		if(beaconservice == null) {
			beaconservice = Application.context.getBean(BeaconService.class);
		}
		return beaconservice;
	}
	
	public PortionService getPortionService() {
		if(portionservice == null) {
			portionservice = Application.context.getBean(PortionService.class);
		}
		return portionservice;
	}
	
	public CustomerService getCustomerService() {
		if(customerservice == null) {
			customerservice = Application.context.getBean(CustomerService.class);
		}
		return customerservice;
	}
	
	public GeoFinderRestController getGeoFinderRestController() {
		if(geoFinderRestController == null) {
			geoFinderRestController = Application.context.getBean(GeoFinderRestController.class);
		}
		return geoFinderRestController;
	}
	
	public ElasticService getElasticService() {
		if(elasticService == null) {
			elasticService = Application.context.getBean(ElasticService.class);
		}
		return elasticService;
	}
	
	public BeaconDeviceService getBeaconDeviceService() {
		if(beaconDeviceService == null) {
			beaconDeviceService = Application.context.getBean(BeaconDeviceService.class);
		}
		return beaconDeviceService;
	}
	
	public CCC getCCC() {
		if(_CCC == null) {
			_CCC = Application.context.getBean(CCC.class);
		}
		return _CCC;
	}
	
	public CustomerUtils getCustomerUtils() {
		if(customerUtils == null) {
			customerUtils = Application.context.getBean(CustomerUtils.class);
		}
		return customerUtils;
	}
}
