package com.semaifour.facesix.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconAlertData;
import com.semaifour.facesix.beacon.data.BeaconAlertDataService;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.beacon.data.ReportBeacon;
import com.semaifour.facesix.beacon.data.ReportBeaconService;
import com.semaifour.facesix.beacon.rest.GeoFinderRestController;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.elasticsearch.ElasticService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.jni.bean.Coordinate;
import com.semaifour.facesix.rest.FSqlRestController;
import com.semaifour.facesix.rest.NetworkConfRestController;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.CustomerUtils;
import scala.concurrent.forkjoin.ForkJoinPool;
import scala.concurrent.forkjoin.RecursiveTask;

@Controller
public class TrilaterationScheduledTask extends RecursiveTask<Integer> {
	/**
	 *
	 */
	private static final long 	serialVersionUID 	= -8742123571623116452L;
	private static final String SCHEDULER_TIME 		= "11";
	private boolean flag 							= false;
	private static String classname					= TrilaterationScheduledTask.class.getName();
	static Logger LOG 								= LoggerFactory.getLogger(classname);
	static final int THRESHOLD 	= 1;
	private String indexname 	= "clu-report-event";
	DateFormat format 			= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	DateFormat parse 			= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	@Autowired
	BeaconService beaconservice;

	@Autowired
	FSqlRestController fsqlRestController;

	@Autowired
	PortionService portionservice;

	@Autowired
	CustomerService customerservice;

	GeoFinderRestController geoFinderRestController;

	@Autowired
	NetworkDeviceService networkDeviceService;

	@Autowired
	private ElasticService elasticService;
	
	@Autowired
	BeaconDeviceService beaconDeviceService;
	
	@Autowired
	CCC _CCC;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	BeaconAlertDataService beaconAlertDataService; 

	@Autowired
	NetworkConfRestController networkConfRestController;

	@Autowired
	ReportBeaconService reportBeaconservice;

	@Value("${facesix.trilaterationscheduledtask.enable}")
	private boolean tritask_enable;

	String cur_solution  			  = "GeoFinder";

	List<Portion> portionlist 		= null;
	ArrayList<String> list			= new ArrayList<String>();
	String 	trilaterationEventTable = "facesix-int-beacon-event";
	String 	spid 		= null;
	int 	record 		= 0;
	int 	recordcount = 0;
	TimeZone timezone   = null;
	boolean logenabled  = false;
	int 	reportFlag;
	int 	tlu			= 0;
	HashMap<String,String> rpMap;
	
	ForkJoinPool forkJoinPool   = null;
	int parallel 			    =  0;
	SimpleDateFormat dateFormat = new SimpleDateFormat("hh");
	List<String> solution 		= Arrays.asList("GatewayFinder","GeoFinder");

	@PostConstruct
	public void init() {
		trilaterationEventTable = _CCC.properties.getProperty("facesix.data.beacon.trilateration.table",
				trilaterationEventTable);

		indexname = _CCC.properties.getProperty("clu.report.data.table", "clu-report-event");
	}
	
	private void setTimeZone (TimeZone timeZone) {
		this.timezone = timeZone;
		//getCustomerUtils().logs(this.logenabled, classname, "timezone > " + timeZone.getDisplayName());
	}
	
	private void setlogenabled (boolean logenabled) {
		this.logenabled = logenabled;
	}
	
	private void setSpid (String spid) {
		this.spid = spid;
		//getCustomerUtils().logs(this.logenabled, classname, " Floor ID> " + spid);
	}
	
	private void setRecordNumber (int record) {
		this.record = record;
		//getCustomerUtils().logs(this.logenabled, classname, " Record Number> " + record);
	}
	
	private void setIndexName(String indexname) {
		this.indexname = indexname;
	}

	private void setReportFlag(int enable ) {
		this.reportFlag = enable;
	}
	private void setRpData(HashMap<String, String> rpMap) {
		this.rpMap = rpMap;
	}
	private void setTlu(int tlu){
		this.tlu = tlu;
	}
	private void setSolution(String sol){
		this.cur_solution = sol;
	}
	
	@Override
	protected Integer compute() {
		
		//getCustomerUtils().logs(this.logenabled, classname, " FN_InsideCompute>Floor ID> " + this.spid + " Recored Number> " + this.record);
		int record_num 	= 1;
		
		if (this.record != 0 ) {
			record_num 	= this.record;	
		}
		
		int max_record 						= 0;
		int tag_count 						= 0;

		List<Map<String, Object>> logs  = null;
		List<Coordinate> coordinates 	= null;
		List<ReportBeacon> reportBeaconList = null;
		Map<String, ReportBeacon> reportBeaconMap = null;

		String spid  			= this.spid;
		String dbIndexname 		= this.indexname;
		String tagid 			= null;
		String x 				= "";
		String y 				= "";
		String jniResponse 		= null;		
		String date 			= null;
		String json_spid 		= "";
		String serverid  		= "";
		String fsql  			= null;
		TimeZone timeZone       = this.timezone;
		int    timeInterval     = this.tlu;
		boolean debugLog		= this.logenabled;
		String sol				= this.cur_solution;
		
		List<TrilaterationScheduledTask> subtasks = null;
		List<TrilaterationScheduledTask> rpttasks = null;
		
		try {
			
			if (this.reportFlag == 1) {
				getCustomerUtils().logs(debugLog, classname,"report map before processing "+rpMap);
				reportProcess(this.rpMap);
			} 
			
			if (this.reportFlag == 0) {
				
				Iterator<Map<String, Object>> iterator = null;

				fsql = "index=" + dbIndexname + ",sort=timestamp desc,size=1,type=cluReport,query=timestamp:>now-3s"
						+ " AND record_num:" + record_num + " AND opcode:\"current-location-update\" AND spid:" + spid
						+ "|value(uid,uid,NA);value(spid,spid,NA);value(max_record,max_record,NA);value(tag_list,tag_list, NA);"
						+ "value(dateTime,dateTime, NA);value(server_send_ts,server_send_ts,NA)|table ;";

				//getCustomerUtils().logs(debugLog, classname, " fsql> " + fsql);

				long fsql_fetch_start = System.currentTimeMillis();
				logs = getFSqlRestController().query(fsql);
				long fsql_fetch_elapsed = System.currentTimeMillis()-fsql_fetch_start;
				//getCustomerUtils().logs(debugLog, classname," fsql fetch elapsed = "+fsql_fetch_elapsed);
				
				getCustomerUtils().logs(debugLog, classname, " logs> " + logs);
				
				iterator = logs.iterator();
				
				long fsql_iteration_start = System.currentTimeMillis();
				while (iterator.hasNext()) {

					TreeMap<String, Object> log = new TreeMap<String, Object>(iterator.next());
					coordinates 				= new ArrayList<Coordinate>();
					reportBeaconList 			= new ArrayList<ReportBeacon>();
					reportBeaconMap 			= new HashMap<String, ReportBeacon>();

					max_record = Integer.parseInt(log.get("max_record").toString());

					if (max_record > 1 && this.record == 0) {
						//getCustomerUtils().logs(debugLog, classname, " Max Record> " + max_record);
		              for (int i = 2;  i <= max_record;  i++) {
		            		subtasks = new ArrayList<TrilaterationScheduledTask>();
							TrilaterationScheduledTask task = new TrilaterationScheduledTask ();
							task.setlogenabled(debugLog);
							task.setTimeZone(timeZone);
							task.setSpid(spid);
							task.setRecordNumber(i);
							task.setIndexName(dbIndexname);
							task.setReportFlag (0);
							task.setTlu(timeInterval);
							task.setSolution(sol);
							task.fork();
							subtasks.add(task);
				        }
					}

					serverid = (String) log.get("uid");

					List<Map<String, Object>> tagDetail 	= (List<Map<String, Object>>) log.get("tag_list");
					Iterator<?> taglistiter					= tagDetail.iterator();
					HashMap<String,List<String>> recInfo 	= new HashMap<String,List<String>>();
					Map<String,Map<String,Object>> deviceMap= new HashMap<String,Map<String,Object>>();
					List<String> rcList 					= null;
					
					long tag_list_iteration_start = System.currentTimeMillis();
					while (taglistiter.hasNext()) {
						Map<String, Object> tagiter = (Map<String, Object>) taglistiter.next();
						tagid = (String) tagiter.get("uid");
						tag_count++;

						if (tagid == null) {
							continue;
						}

						Beacon beacon = getBeaconService().findOneByMacaddr(tagid);
						if (beacon == null) {
							// getCustomerUtils().logs(debugLog, classname, "Intruder found");
							continue;
						}
						
						ReportBeacon reportBeacon = getReportBeaconService().findOneByMacaddr(tagid);
						if (reportBeacon == null) {
							reportBeacon = setNewReportBeacon(beacon);
							reportBeaconMap.put(tagid, reportBeacon);
							continue;
						}

						Map<String, Double> coordinate = (Map<String, Double>) tagiter.get("coordinate");

						double accuracy = Double.valueOf(tagiter.get("accuracy").toString());
						double range = Double.valueOf(tagiter.get("range").toString());

						Double latitude = coordinate.get("latitude");
						Double longitude = coordinate.get("longitude");

						coordinates.add(new Coordinate(latitude, longitude, tagid));

						String   reciverUid = "NA";
						String   alias 		= "NA";
						double distance 	=  0;
						List<Map<String, Object>> reciverArray = null;

						long fetch_receiver_start = System.currentTimeMillis();
						if (tagiter.containsKey("receiver_list")) {
							reciverArray 		 = (List<Map<String, Object>>)tagiter.get("receiver_list");
						}
						
						if (reciverArray != null && reciverArray.size() > 0) {
							Map<String, Object> ob = reciverArray.get(0);
							reciverUid 		       = (String) ob.get("uid");
							distance 			   = (Double) ob.get("distance");

							if (reciverUid != null) {
								Map<String,Object> dvMap = new HashMap<String,Object>();
								if(deviceMap.containsKey(reciverUid)) {
									dvMap = deviceMap.get(reciverUid);
									alias = (String)dvMap.get("alias");
								} else {
									BeaconDevice device = getBeaconDeviceService().findOneByUid(reciverUid);
									if (device != null) {
										alias = device.getAlias().toUpperCase();
									}
									dvMap.put("alias", alias);
									deviceMap.put(reciverUid,dvMap);
								}
							}
						}

						rcList = new ArrayList<String>();
						rcList.add(reciverUid);
						rcList.add(alias);
						recInfo.put(tagid, rcList);

						long fetch_receiver_elapsed = System.currentTimeMillis() - fetch_receiver_start;

						// getCustomerUtils().logs(debugLog, classname,"fetch receiver =
						// "+fetch_receiver_elapsed);

						reportBeacon.setAccuracy(accuracy);
						reportBeacon.setRange(range);
						reportBeacon.setLat(latitude);
						reportBeacon.setLon(longitude);
						reportBeacon.setServerid(serverid);
						reportBeacon.setDistance(distance);
						reportBeacon.setAssignedTo(beacon.getAssignedTo());
						reportBeaconMap.put(tagid, reportBeacon);

					}

					long tag_list_iteration_elapsed = System.currentTimeMillis() - tag_list_iteration_start;

					long jni_response_start = System.currentTimeMillis();
					jniResponse = getGeoFinderRestController().Coordinate2Pixel(spid, coordinates);
					long jni_response_elapsed = System.currentTimeMillis() - jni_response_start;


					if (jniResponse == null) {
						getCustomerUtils().logs(debugLog, classname, "jni null");
						getReportBeaconService().saveList(reportBeaconList);
					}

					long jni_parse_start = System.currentTimeMillis();
					if (jniResponse != null) {
						net.sf.json.JSONObject locationinfo = net.sf.json.JSONObject.fromObject(jniResponse);
						if (locationinfo.containsKey("result")) {
							getCustomerUtils().logs(debugLog, classname, "locationinfo = " + locationinfo);
							// List<Beacon> jBeaconlist = new ArrayList<Beacon>(beaconlist.size());
							net.sf.json.JSONArray tagcoordinates = (net.sf.json.JSONArray) locationinfo
									.get("result");
							Iterator<net.sf.json.JSONObject> list = tagcoordinates.iterator();
							Map<String, HashMap<Object, Object>> portionMap = new HashMap<String, HashMap<Object, Object>>();

							long iter_beaconlist_start = System.currentTimeMillis();
							reportBeaconList = new ArrayList<ReportBeacon>();
							while (list.hasNext()) {
								net.sf.json.JSONObject object = list.next();
								String object_macaddr = object.getString("macaddr");
								ReportBeacon reportBeacon = reportBeaconMap.get(object_macaddr);
								String prev_x = reportBeacon.getX();
								String prev_y = reportBeacon.getY();
								tagid = reportBeacon.getMacaddr();
								String tagCid = reportBeacon.getCid();
								x = object.getString("x");
								y = object.getString("y");

								if (!x.isEmpty() && x.length() > 0) {
									reportBeacon.setX(x);
								}
								if (!y.isEmpty() && y.length() > 0) {
									reportBeacon.setY(y);
								}

								if (prev_x != null && prev_y != null && !x.isEmpty() && !y.isEmpty()
										&& (!prev_x.equals(x) || !prev_y.equals(y))
										|| reportBeacon.getLastactive() == 0) {

									reportBeacon.setLastactive(System.currentTimeMillis());
								}

								date = log.get("dateTime").toString();
								rcList = recInfo.get(tagid);

								String sid = null;
								String floorname = "NA";
								int height = 0;
								int width = 0;
								String portionCid = "";

								if (portionMap.containsKey(spid)) {

									HashMap<Object, Object> details = (HashMap<Object, Object>) portionMap
											.get(spid);
									portionCid = (String) details.get("cid");
									sid = (String) details.get("sid");
									floorname = (String) details.get("floorname");
									height = (int) details.get("height");
									width = (int) details.get("width");

								} else {

									Portion portion = getPortionService().findById(spid);

									if (portion != null) {

										sid = portion.getSiteId();
										floorname = portion.getUid().toUpperCase();
										height = portion.getHeight();
										width = portion.getWidth();
										portionCid = portion.getCid();

										HashMap<Object, Object> m = new HashMap<Object, Object>();
										m.put("cid", portionCid);
										m.put("sid", sid);
										m.put("floorname", floorname);
										m.put("height", height);
										m.put("width", width);

										portionMap.put(spid, m);
									}
								}

								if (!tagCid.equals(portionCid)) {
									LOG.info( "+++++++++++++ TAG ID DOES NOT BELONG TO THIS CUSTOMER +++++++++++++++++++");
									LOG.info(" TAG ID " + tagid + " SPID " + spid);
									continue;
								}

								String prev_reciverUid = reportBeacon.getReciverinfo();
								String prev_spid = reportBeacon.getSpid();
								String reciverUid = rcList.get(0);
								String alias = rcList.get(1);

								getCustomerUtils().logs(debugLog, classname,
										"tagid " + tagid + "+++++++++++ reciverUid = " + reciverUid
												+ " prev_reciverUid = " + prev_reciverUid + " +++++++++");

								Beacon beacon = getBeaconService().findOneByMacaddr(tagid);
								
								if (!spid.equalsIgnoreCase(prev_spid) || !reciverUid.equalsIgnoreCase(prev_reciverUid)) {
									date = beacon.getEntry_loc();
									String entry_loc = reportBeacon.getEntry_loc();
									String entry_floor = reportBeacon.getEntryFloor();
									
									if (entry_loc == null || entry_loc.isEmpty()) {
										reportBeacon.setEntry_loc(date);
									}
									if (entry_floor == null || entry_floor.isEmpty()) {
										reportBeacon.setEntryFloor(date);
									}

									if (prev_spid == null || !prev_spid.equals(spid)) {
										reportBeacon.setSid(sid);
										reportBeacon.setSpid(spid);
										reportBeacon.setLocation(floorname);
										reportBeacon.setEntryFloor(date);
									}
									if (prev_reciverUid == null || !prev_reciverUid.equals(reciverUid)) {
										reportBeacon.setEntry_loc(date); // location
										reportBeacon.setReciveralias(alias);
										reportBeacon.setReciverinfo(reciverUid);
									}

									rpttasks = new ArrayList<TrilaterationScheduledTask>();

									HashMap<String, String> rpMap = new HashMap<String, String>();
									rpMap.put("macaddr", tagid);
									rpMap.put("cid", reportBeacon.getCid());
									rpMap.put("tagtype", reportBeacon.getTag_type());
									rpMap.put("prev_sid", reportBeacon.getSid());
									rpMap.put("cur_sid", sid);
									rpMap.put("prev_spid", prev_spid);
									rpMap.put("cur_spid", spid);
									rpMap.put("prev_reuid", prev_reciverUid);
									rpMap.put("cur_reuid", reciverUid);
									rpMap.put("en_location", entry_loc);
									rpMap.put("en_floor", entry_floor);
									rpMap.put("date", date);
									rpMap.put("assto", reportBeacon.getAssignedTo());

									TrilaterationScheduledTask rtask = new TrilaterationScheduledTask();
									rtask.setReportFlag(1);
									rtask.setlogenabled(debugLog);
									rtask.setTimeZone(timeZone);
									rtask.setSpid(spid);
									rtask.setRpData(rpMap);
									rtask.fork();
									rpttasks.add(rtask);

								} 
								if(beacon.getSpid().equals(reportBeacon.getSpid())){
									reportBeacon.setEntryFloor(beacon.getEntry_floor());
								}
								
								if(reportBeacon.getReciverinfo().equals(beacon.getReciverinfo())) {
									reportBeacon.setEntry_loc(beacon.getEntry_loc());
								}
								
								long lastseen = System.currentTimeMillis();
								reportBeacon.setHeight(height);
								reportBeacon.setWidth(width);
								reportBeacon.setLastSeen(lastseen);
								reportBeacon.setLastReportingTime(date);
								reportBeacon.setModifiedBy("cloud");
								reportBeacon.setModifiedOn(new Date(lastseen));

								reportBeaconList.add(reportBeacon);
							}
							reportBeaconList = getReportBeaconService().saveList(reportBeaconList);
							getCustomerUtils().logs(debugLog, classname, " saved reportBeaconList " + reportBeaconList);
						}
					} else {
						getCustomerUtils().logs(debugLog, classname, " jni response null spid " + spid);
					}
				}
			}
			
			} catch (Exception e) {
				getCustomerUtils().logs(debugLog,classname," FORK Join Tag Processing error " +e);
			}
			
			if (subtasks != null) {
				for (TrilaterationScheduledTask r : subtasks) {
					recordcount  = recordcount + r.join();
				}			
			}
			
			if (rpttasks != null) {
				for (TrilaterationScheduledTask r : rpttasks) {
					recordcount  = recordcount + r.join();
				}			
			}
			
			return tag_count;
		}

	private ReportBeacon setNewReportBeacon(Beacon beacon) {
		ReportBeacon reportBeacon = new ReportBeacon();
		reportBeacon.setCid(beacon.getCid());
		reportBeacon.setSid(beacon.getSid());
		reportBeacon.setSpid(beacon.getSpid());
		reportBeacon.setReciverinfo(beacon.getReciverinfo());
		reportBeacon.setEntry_floor(beacon.getEntry_floor());
		reportBeacon.setEntry_loc(beacon.getEntry_loc());
		reportBeacon.setTagType(beacon.getTagType());
		reportBeacon.setMacaddr(beacon.getMacaddr());
		reportBeacon.setAssignedTo(beacon.getAssignedTo());
		return getReportBeaconService().save(reportBeacon);
	}

	@Scheduled (fixedDelay=500)
	public void trilaterationTask() throws InterruptedException {

		if (!tritask_enable) {
			return;
		}

		boolean enablelog =false;
		try {

			String  cid 		= "";
			Integer floor_size 	= 0;
			Integer tagExecutor = 0;

			List<Portion> portionlist 		= null;
			List<NetworkDevice> device 		= null;
			List<BeaconDevice> beacondevice = null;
			Iterable<Customer> customerlist = getCustomerService().findBySolutionAndStatus(solution,"ACTIVE");

			String currentTime 	= dateFormat.format(new Date()).toString();
			String scheduleTime = SCHEDULER_TIME;
			if (currentTime.equals(scheduleTime) && flag == false) {
			//	LOG.info("time is  equal and flag is 0");
				processingBattery();
				flag = true;
				Thread.sleep(60000);
				return;
			}
			
			if(!currentTime.equals(scheduleTime) && flag == true){
				//LOG.info("time is not equal and flag is 1");
				flag = false;
			} 
			
			for (Customer cx : customerlist) {
				
				cid = cx.getId();

				if(cx.getLogs() != null && cx.getLogs().equals("true")) {
					enablelog = true;
				}else{
					enablelog = false;
				}
				String customerName = cx.getCustomerName();
				String sol 			= cx.getVenueType();
				
				boolean entryExit = sol.equals("Patient-Tracker") ? true : false;

				getCustomerUtils().logs(enablelog,classname,"Customer Name "+customerName + "solution type "+sol);

				/*
				 * follow trilateration path only for locatum solution
				 */
				if (!entryExit) {

					forkJoinPool 	= new ForkJoinPool();
					tagExecutor     = 0;
					portionlist 	= getPortionService().findByCid(cid);
					device 			= getNetworkDeviceService().findByCid(cid);
					beacondevice 	= getBeaconDeviceService().findByCid(cid);

					if (portionlist == null || device == null || beacondevice == null) {
						continue;
					}

					floor_size = portionlist.size();

					if (floor_size <= 0) {
						continue;
					}

					String zone = cx.getTimezone();
					List<TrilaterationScheduledTask> myRecursiveTask = new ArrayList<TrilaterationScheduledTask>();

					int i = 0;
					for (Portion portion : portionlist) {
						
						beacondevice = getBeaconDeviceService().findBySpid(portion.getId());
						device 		 = getNetworkDeviceService().findBySpid(portion.getId());
						if (beacondevice == null || device == null) {
							continue;
						}

						TrilaterationScheduledTask task = new TrilaterationScheduledTask ();
						task.setlogenabled(enablelog);
						task.setTimeZone(getCustomerUtils().FetchTimeZone(zone));
						task.setSpid(portion.getId());
						task.setRecordNumber(0);
						task.setIndexName(indexname);
						task.setReportFlag (0);
						task.setSolution(sol);
						myRecursiveTask.add(task);
						forkJoinPool.execute(task);
						//getCustomerUtils().logs(enablelog,classname,"For Executor Index> " + i);
						i++;
					}
					tagExecutor = i;

					i = 0;
					//getCustomerUtils().logs(enablelog,classname,"TagExecutor> " + tagExecutor);
					do
					{

						try
						{
						   TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e)
						{
						   e.printStackTrace();
						}
						
						if (myRecursiveTask.get(i).isDone() 				|| 
							myRecursiveTask.get(i).isCancelled()  			||
							myRecursiveTask.get(i).isCompletedAbnormally() 	|| 
							myRecursiveTask.get(i).isCompletedNormally()) {
							//getCustomerUtils().logs(enablelog,classname,"Index Range> " + i);
							i++;
						}
						
						if (i == tagExecutor) {
							//getCustomerUtils().logs(enablelog,classname,"Index reaches Floor Count, Safe Exit" + i);
							break;
						}
						
					} while (tagExecutor >= i);
					
					forkJoinPool.shutdownNow();
					for (i = 0; i < tagExecutor; i++) {
						myRecursiveTask.get(i).join();
					}
					
					forkJoinPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
					getCustomerUtils().logs(enablelog,classname,"*** Shutdown NOW ***");
				}

				tagInactivityMail(cx);
				
				if (customerName.equalsIgnoreCase("DEMO")) {

					long time = System.currentTimeMillis();
					long diff = (Long.parseLong(cx.getTagInact()) + 1) * 60 * 1000; // inactivity time + 3 minutes it will get inactive
					List<String> macaddrs = getDemoInactiveTagMac();
					List<Beacon> beacons = getBeaconService().findByMacaddrs(macaddrs);
					for (Beacon b : beacons) {
						long activetime = b.getLastSeen();
						if (time - activetime >= diff) {
							b.setState("active");
							b.setLastactive(time);
							b.setLastSeen(time);
							b.setLastReportingTime(format.format(new Date()));
							b.setMailsent("false");
							b.setLocalInactivityMailSent("false");
							getBeaconService().save(b, false);
						}
					}
				}
			}
			
		} catch (Exception e) {
			getCustomerUtils().logs(enablelog,classname,"Tag process failed " + e);
			e.printStackTrace();
		} finally {
			getCustomerUtils().logs(enablelog,classname,"*** Done ***");
		}
	}
	private void reportProcess (HashMap<String,String> rpMap){

		try {
			
			long rpStart = System.currentTimeMillis();
			Map<String, Object> map = new HashMap<String, Object>();

			String date 		    = rpMap.get("date");
			
			String cid				= rpMap.get("cid");
			String tagid       		= rpMap.get("macaddr");
			String assignedto       = rpMap.get("assto");
			String tagtype 			= rpMap.get("tagtype");
			
			String prev_sid         = rpMap.get("prev_sid");
			String prev_spid 		= rpMap.get("prev_spid");
			String prev_rec 		= rpMap.get("prev_reuid");
			
			String cur_sid 			= rpMap.get("cur_sid");
			String cur_spid 		= rpMap.get("cur_spid");
			String cur_rec 			= rpMap.get("cur_reuid");
			
			
			String entry_floor 		= rpMap.get("en_floor");
			String entry_loc   		= rpMap.get("en_location");
			
			String exit_floor  		= date;
			String exit_loc		    = date;
			
			Date entry_flr 		= entry_floor == null? format.parse(date):format.parse(entry_floor);
			Date exit_flr	    = format.parse(exit_floor);
			
			Date entry_location = entry_loc == null?format.parse(date):format.parse(entry_loc);
			Date exit_location  = format.parse(exit_loc);
			
			long elapsed_loc	= customerUtils.calculateElapsedTime(entry_location, exit_location);
			
			getCustomerUtils().logs(this.logenabled, classname,"+++++++++++++++++ report for the tag "+tagid+" ++++++++++++++++++");
			
			if (prev_spid == null || !prev_spid.equalsIgnoreCase(cur_spid)
					|| prev_rec.equalsIgnoreCase(cur_rec)) {
				
				if(prev_spid != null){
					long elapsed_flr = customerUtils.calculateElapsedTime(entry_flr, exit_flr);
					map.put("opcode",		"reports");
					map.put("tagid", 		tagid);
					map.put("tagtype", 		tagtype);
					map.put("assingedto",   assignedto);
					map.put("cid", 			cid);
					map.put("sid", 			prev_sid);
					map.put("spid", 		prev_spid);
					map.put("location", 	prev_rec);
					map.put("entry_loc", 	entry_loc);
					map.put("entry_floor",  entry_floor);
					map.put("exit_loc", 	exit_loc);
					map.put("exit_floor", 	exit_floor);
					map.put("elapsed_loc", 	elapsed_loc);
					map.put("elapsed_floor",elapsed_flr);
					getElasticService().post(trilaterationEventTable, "trilateration", map);
					map.clear();
				}
				
				/*map.put("opcode",		"reports");
				map.put("tagid", 		tagid);
				map.put("assingedto",   assignedto);
				map.put("cid", 			cid);
				map.put("sid", 			cur_sid);
				map.put("spid", 		cur_spid);
				map.put("location", 	cur_rec);
				map.put("entry_loc", 	exit_loc);
				map.put("entry_floor",  exit_floor);
				
				getElasticService().post(trilaterationEventTable, "trilateration", map);*/

			} else if(prev_rec == null || !prev_rec.equalsIgnoreCase(cur_rec)){

				if(prev_rec != null)
				{
					map.put("opcode",		"reports");
					map.put("tagid", 		tagid);
					map.put("assingedto",   assignedto);
					map.put("tagtype", 		tagtype);
					map.put("cid", 			cid);
					map.put("sid", 			prev_sid);
					map.put("spid", 		prev_spid);
					map.put("location", 	prev_rec);
					map.put("entry_loc", 	entry_loc);
					map.put("entry_floor",  entry_floor);
					map.put("exit_loc", 	exit_loc);
					map.put("elapsed_loc",  elapsed_loc);

					getElasticService().post(trilaterationEventTable, "trilateration", map);
					map.clear();
				}

				/*map.put("opcode",		"reports");
				map.put("tagid", 		tagid);
				map.put("assingedto",   assignedto);
				map.put("cid", 			cid);
				map.put("sid", 			cur_sid);
				map.put("spid", 		cur_spid);
				map.put("location", 	cur_rec);
				map.put("entry_loc", 	exit_loc);
				map.put("entry_floor", 	entry_floor);
				
				long elastic_post_start = System.currentTimeMillis();
				getElasticService().post(trilaterationEventTable, "trilateration", map);
				long elastic_post_elapsed = System.currentTimeMillis() - elastic_post_start;
				
				getCustomerUtils().logs(this.logenabled, classname," elastic_post_elapsed "+elastic_post_elapsed); */
			}
		long rpElapsed = System.currentTimeMillis() - rpStart;
			//getCustomerUtils().logs(this.logenabled, classname,"Report Elapsed time  " + rpElapsed);
		}catch (Exception e) {
			getCustomerUtils().logs(this.logenabled,classname,"Report processing error " +e);
			e.printStackTrace();
		}
	}
	private boolean processingBattery() {
		boolean bRet 			= false;
		int    time     		= 0;
		int    batt_level       = 0;

		try {

			ArrayList<Beacon> taglist 		= new ArrayList<Beacon>();
			List<Map<String, Object>> logs  = null;
			Iterable<Customer> customer 	= getCustomerService().findAll();
			
			for (Customer cust : customer) {
				String cid 		= cust.getId();
				
				if (customerUtils.trilateration(cid) || customerUtils.entryexit(cid)) {
				
					String status 	= "checkedout";
					
					Collection<Beacon> beacon = getBeaconService().getSavedBeaconByCidAndStatus(cid, status);
					
					for (Beacon b : beacon) {
						
						String tagid = b.getMacaddr();
						
						String fsql = "index="+indexname+",size=1,query=timestamp:>now-12h"
								+ " AND opcode:\"batteryReport\" AND uid:\""+tagid+"\" | value(uid,taguid,NA);"
								+ " value(batt_level,batteryLevel,NA);value(batt_timestamp,time,NA) | table";

						logs = getFSqlRestController().query(fsql);

						if (logs == null || logs.isEmpty()) {
							continue;
						}
						Map<String, Object> map = logs.get(0);
						
						if (map.get("batteryLevel") != null ) {
							if (map.get("time") == null) {
								time = 0;
							} else {
								time = Integer.parseInt(map.get("time").toString());
							}
							
							batt_level      = Integer.parseInt(map.get("batteryLevel").toString());
							
							b.setBattery_timestamp(time);
							b.setBattery_level(batt_level);
							b.setModifiedBy("cloud");
							b.setModifiedOn(new Date());
							taglist.add(b);
							bRet = true;
						}
		
					}
					getBeaconService().save(taglist);
					
					String battery 			= cust.getBattery_threshold();
					int batteryThreshold 	= 40;
					int lowBatteryCount 	= 0;
					
					if (battery != null && !battery.isEmpty()) {
						batteryThreshold = Integer.parseInt(battery);
					}
					
					List<Beacon> lowBatteryBeacons = beaconservice.findByCidStatusAndBatteryLevel(cid, "checkedout", batteryThreshold);
					
					if (lowBatteryBeacons != null && lowBatteryBeacons.size() > 0) {
						lowBatteryCount = lowBatteryBeacons.size();
					}
					
					cust.setBatteryAlertCount(lowBatteryCount);
					cust = customerservice.save(cust);
				}
			}

		} catch (Exception e) {
			getCustomerUtils().logs(this.logenabled,classname,"Battery Processing error " +e);
			e.printStackTrace();
		}

		return bRet;
	}

	private final void tagInactivityMail(Customer cx) {
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					long sTimer 		  	  = System.currentTimeMillis();
					long defaultInactiveTime  = 60;
					long time 				  = 0;
					
					String cid 			  = cx.getId();
					String sentMail 	  = "false";
					String status 		  = "checkedout";
					String ismailalert 	  = "true";
					String inactivityMail = cx.getInactivityMail();
					String date 		  = null;
					
					boolean logs 		  = true;
					boolean hasDataToSend = false;
					boolean needsMail 	  = false;
					boolean entryexit 	  = cx.getVenueType().equals("Patient-Tracker");
					
					List<Beacon> inactiveBeacons 		  = new ArrayList<Beacon>();
					List<Beacon> beaconlist 	 		  = null;
					List<UserAccount> useracclist 		  = getUserAccountService().findByCustomerIdAndIsMailAlert(cid,ismailalert);
					List<BeaconAlertData> beaconAlertData = getBeaconAlertDataService().findByCid(cid);
					
					int useracc_size 		= useracclist != null ? useracclist.size() : 0;
					String newline 			= System.getProperty("line.separator");
					StringBuilder mailBody 	= null;
					
					TimeZone zone 			= getCustomerUtils().FetchTimeZone(cx.getTimezone());
					
					format.setTimeZone(zone);
					
					date = format.format(new Date());

					if (useracc_size > 0 && inactivityMail != null && inactivityMail.equals("true")) {
						needsMail = true;
					}
					
					if (cx.getLogs() == null || cx.getLogs().equals("false")) {
						logs = false;
					}
					
					if (cx.getTagInact() != null) {
						defaultInactiveTime = Long.parseLong(cx.getTagInact()) *60000;
					}
					
					time = System.currentTimeMillis() - defaultInactiveTime;
					
					if (cx.getCustomerName().equalsIgnoreCase("demo")) {
						List<String> macaddrs = getDemoInactiveTagMac();
						inactiveBeacons = getBeaconService().findByMacaddrsMailSentAndLastSeenBefore(macaddrs,sentMail,time);

					} else {
						inactiveBeacons = getBeaconService().findByCidStatusMailSentLastSeenBefore(cid, status, sentMail, time);
					}
					
					if (inactiveBeacons != null && inactiveBeacons.size() > 0 ) {
						
						mailBody 	= buildInactivityTable();
						//getCustomerUtils().logs(logs, classname,"==========INACTIVE BEACON COUNT============ "+inactiveBeacons.size());
						int i =1;
						beaconlist = new ArrayList<Beacon>();
						for (Beacon beacon : inactiveBeacons) {
							
							String tagstate 	= beacon.getState();
							
							if(entryexit && !tagstate.equals("inactive")){
								continue;
							}
							
							if(!hasDataToSend){
								hasDataToSend = true;
							}
							
							String tagId 		= beacon.getMacaddr();
							String tagType 		= beacon.getTag_type();
							String assignedTo 	= beacon.getAssignedTo();
							String floorName 	= "NA";
							String location 	= "NA";
							String lastseenDate = "-";
							String sid 		    = beacon.getSid();
							String spid 		= beacon.getSpid();
							String ruid 		= beacon.getReciverinfo();
							String entry_loc 	= beacon.getEntry_loc();
							String entry_floor  = beacon.getEntry_floor();
							

							if (needsMail) {
								
								long lastSeen 		= beacon.getLastSeen();
								
								if(lastSeen != 0) {
									lastseenDate = date;
								}
								
								if(beacon.getLocation() != null){
									floorName 	= beacon.getLocation().toUpperCase();
								}
								
								if(beacon.getReciveralias() != null){
									location 	= beacon.getReciveralias().toUpperCase();
								}
								
								mailBody.append("<tr>")
										.append("<td>" + i + "</td>")
										.append("<td  style=\"color:blue\">" + tagId + newline + "</td>")
										.append("<td  style=\"color:blue\">" + tagType + newline + "</td>")
										.append("<td  style=\"color:blue\">" + assignedTo + newline + "</td>")
										.append("<td  style=\"color:blue\">" + floorName + newline + "</td>")
										.append("<td  style=\"color:blue\">" + location + newline + "</td>")
										.append("<td  style=\"color:blue\">" + lastseenDate + newline + "</td>").append("</tr>");			
							
								beacon.setMailsent("true");
								i++;
							}

							if (!beacon.getState().equals("inactive")) {
								
								ReportBeacon reportBeacon = getReportBeaconService().findOneByMacaddr(tagId);
								if(reportBeacon != null) {
									reportBeacon.setState("inactive");
									reportBeacon.setEntry_floor(null);
									reportBeacon.setEntry_loc(null);
									getReportBeaconService().save(reportBeacon);
								}
								
								date = beacon.getLastReportingTime();
								beacon.setState("inactive");
								
								HashMap<String, String> rpMap = new HashMap<String, String>();
								rpMap.put("macaddr",   tagId);
								rpMap.put("cid", 	   cid);
								rpMap.put("tagtype",   beacon.getTag_type());
								rpMap.put("prev_sid",  sid);
								rpMap.put("cur_sid",   sid);
								rpMap.put("prev_spid", spid);
								rpMap.put("cur_spid",  spid);
								rpMap.put("prev_reuid",ruid);
								rpMap.put("cur_reuid", ruid);
								rpMap.put("en_location", entry_loc);
								rpMap.put("en_floor",  entry_floor);
								rpMap.put("date",      date);
								rpMap.put("assto",     beacon.getAssignedTo());
								
								getCustomerUtils().logs(logs, classname,"report map before processing "+rpMap);
								reportProcess(rpMap);
								beacon.setExitTime(date);

							}
							beaconlist.add(beacon);	
						}
						
						//getCustomerUtils().logs(logs, classname, "useracclist " + useracclist);
						
						if (beaconlist !=null && beaconlist.size() >0) {
							mailBody.append("</table><br/>");
							getBeaconService().save(beaconlist);	
						}
						
						if (needsMail && hasDataToSend) {
							sendMailToUsers(mailBody, useracclist);
						} else {
							//getCustomerUtils().logs(logs, classname, "INACTIVE TAGS needsMail == "+needsMail+" has data to send "+hasDataToSend);
						}
					}else{
						//getCustomerUtils().logs(logs, classname, "INACTIVE TAGS needsMail == "+needsMail+" has data to send "+hasDataToSend);
					}
					
					if (beaconAlertData != null && beaconAlertData.size() > 0 && needsMail) {
						
						boolean allTagidsenabled   = false;
						boolean allplaceIdsenabled = false;
						hasDataToSend = false;
						String tagtype;
						String place;
						String fieldName = "";
						String activeMailSent = "true";
						
						List<Beacon> activeBeacons = null;
						
						net.sf.json.JSONArray tagids;
						net.sf.json.JSONArray placeIds;
						
						long duration;
						status = "checkedout";
						
						
						long inactivityTime = 0;
						int i 				= 1;
						
						List<Map<String, Object>> result = null;
						Set<String> tags 				 = new HashSet<String>();
						
						for (BeaconAlertData b : beaconAlertData) {
							
							tagtype  = b.getTagtype();
							tagids   = b.getTagids();
							place    = b.getPlace();
							placeIds = b.getPlaceIds();
							duration = b.getDuration() * 60000;

							inactiveBeacons = new ArrayList<Beacon>();
							
							if(place.equals("venue")){
								fieldName = "sid";
							}else if(place.equals("floor")){
								fieldName = "spid";
							}else if(place.equals("location")){
								fieldName = "location";
							}
							
							if (placeIds.size() == 1 && placeIds.getString(0).equals("all")){
								allplaceIdsenabled = true;
							}
							
							if (tagids.size() == 1 && tagids.getString(0).equals("all")){
								allTagidsenabled = true;
							}
							
							inactivityTime = System.currentTimeMillis() - duration;
							
							if (allTagidsenabled && allplaceIdsenabled) {
								
								beaconlist = getBeaconService().getSavedBeaconByCidTagTypeAndStatus(cid, tagtype, status);
							
							} else if (allplaceIdsenabled && !allTagidsenabled) {
								
								beaconlist = getBeaconService().findByCidTagTypeStatusAndTagIds(cid,tagtype,status,tagids);
								
							} else if (!allplaceIdsenabled && allTagidsenabled) {
								
								if (place.equals("venue")) {
									beaconlist = getBeaconService().findByCidTagTypeStatusAndNotSids(cid,tagtype,status,placeIds);
									activeBeacons = getBeaconService().findByCidTagTypeStatusSidsAndLastSeenAfterMailSent(cid,tagtype,status,placeIds,inactivityTime,activeMailSent);
								} else if (place.equals("floor")) {
									beaconlist = getBeaconService().findByCidTagTypeStatusAndNotSpids(cid, tagtype, status, placeIds);
									activeBeacons = getBeaconService().findByCidTagTypeStatusSpidsAndLastSeenAfterMailSent(cid,tagtype,status,placeIds,inactivityTime,activeMailSent);
								} else if (place.equals("location")) {
									beaconlist = getBeaconService().findByCidTagTypeStatusAndNotReceiverInfos(cid,tagtype,status,placeIds);
									activeBeacons = getBeaconService().findByCidTagTypeStatusReceiverInfosAndLastSeenAfterMailSent(cid,tagtype,status,placeIds,inactivityTime,activeMailSent);
								}
								
							} else if (!allplaceIdsenabled && !allTagidsenabled) {
								
								if (place.equals("venue")) {
									beaconlist = getBeaconService().findByCidTagTypeStatusTagIdsAndNotSids(cid,tagtype,status,tagids,placeIds);
									activeBeacons = getBeaconService().findByCidTagTypeStatusTagIdsSidsAndLastSeenAfterMailSent(cid,tagtype,status,tagids,placeIds,inactivityTime,activeMailSent);
								} else if (place.equals("floor")) {
									beaconlist = getBeaconService().findByCidTagTypeStatusTagIdsAndNotSpids(cid,tagtype,status,tagids,placeIds);
									activeBeacons = getBeaconService().findByCidTagTypeStatusTagIdsSpidsAndLastSeenAfterMailSent(cid,tagtype,status,tagids,placeIds,inactivityTime,activeMailSent);
								} else if (place.equals("location")) {
									beaconlist = getBeaconService().findByCidTagTypeStatusTagIdsAndNotReceiverInfos(cid,tagtype,status,tagids,placeIds);
									activeBeacons = getBeaconService().findByCidTagTypeStatusTagIdsReceiverInfosAndLastSeenAfterMailSent(cid,tagtype,status,tagids,placeIds,inactivityTime,activeMailSent);
								}
							}
							
							inactiveBeacons = new ArrayList<Beacon>();
							
							if (beaconlist != null && beaconlist.size() > 0) {
								
								inactivityTime  = format.parse(date).getTime() - duration;

								Map<String,String> portionMap  = new HashMap<String,String>();
								Map<String,String> receiverMap = new HashMap<String,String>();
								Map<String, Object> log 	   = null;
								
								Portion p 		= null;
								BeaconDevice bd = null;
								
								for(Beacon bcn : beaconlist){
									
									String tagid 	  	= bcn.getMacaddr();
									String tagType	 	= bcn.getTag_type();
									String assignedto 	= bcn.getAssignedTo();
									String floorname  	= bcn.getLocation()				   == null ? "NA" 	: bcn.getLocation();
									String location     = bcn.getReciveralias() 		   == null ? "NA" 	: bcn.getReciveralias();
									String lastSeenDate = bcn.getLastReportingTime() 	   == null ? "NA" 	: bcn.getLastReportingTime();
									String mail 		= bcn.getLocalInactivityMailSent() == null ? "false": bcn.getLocalInactivityMailSent();
									String sid 			= bcn.getSid()					   == null ? "NA" 	: bcn.getSid();
									String spid 		= bcn.getSpid()					   == null ? "NA" 	: bcn.getSpid();
									String uid			= bcn.getReciverinfo()			   == null ? "NA" 	: bcn.getReciverinfo();
									String timespent 	= "00:00:00";
									String cur_location = location;
									boolean addtag = true;
									
									if((place.equals("venue") && !sid.equals("NA") && placeIds.contains(sid))||
									   (place.equals("floor") && !spid.equals("NA") && placeIds.contains(spid))||
									   (place.equals("location") && !uid.equals("NA") && placeIds.contains(placeIds))){
										long elps = customerUtils.calculateElapsedTime(format.parse(bcn.getEntry_loc()),format.parse(bcn.getLastReportingTime()));
										timespent = getTimeSpent(String.valueOf(elps));
									}else{

										String fsql = "index="+trilaterationEventTable + ",size=1,type=trilateration,query="+
													  "opcode:\"reports\" AND cid:" + cid +" AND tagid:\""+tagid+"\" ";
										
										if(!allplaceIdsenabled){
											fsql += "AND " + BuildFsqlOrCondition(placeIds, fieldName);
										}
										
										if(!place.equals("location")){
											fsql += " AND exit_floor:*" ;
										}
										
										fsql += " AND exit_loc:*,sort=timestamp DESC|value(timestamp,Date,typecast=date);" + " value(tagid,tagid,null);"
												+ " value(assingedto,assingedto,null);value(spid,spid,null);value(location,location,null);"
												+ "value(entry_floor ,entry_floor,null);value(exit_floor,exit_floor,null);value(elapsed_floor,elapsed_floor,null);"
												+ "value(entry_loc ,entry_loc,null);value(exit_loc,exit_loc,null);value(elapsed_loc,elapsed_loc,null);"
												+ "|table,sort=Date:desc;";
										
										result = getFSqlRestController().query(fsql);
										
										if(result != null && !result.isEmpty()){
											
											log = result.get(0);
											
											lastSeenDate  = log.get("exit_loc").toString();
											
											long exittime = format.parse(lastSeenDate).getTime();
											
											if ((exittime < inactivityTime) && (mail== null || mail.equals("false"))) {
												
												spid = log.get("spid").toString();
												uid = log.get("location").toString();

												if(portionMap.containsKey(spid)){
													floorname = portionMap.get(spid);
												}else{
													 p = getPortionService().findById(spid);
													if (p != null) {
														floorname = p.getUid();
													}
													 portionMap.put(spid, floorname);
												}
												
												if (receiverMap.containsKey(uid)) {
													location = receiverMap.get(uid);
												} else {
													bd = getBeaconDeviceService().findByUidAndCid(uid, cid);
													if (bd != null) {
														location = bd.getAlias();
													}
													receiverMap.put(uid, location);
												}
												
												timespent = getTimeSpent(log.get("elapsed_loc").toString());
												
											} else {
												
												addtag = false;
												
												if ((exittime > inactivityTime) && mail.equals("true")) {
													bcn.setLocalInactivityMailSent("false");
													bcn.setAssginedLocationLastSeen(lastSeenDate);
													inactiveBeacons.add(bcn);
												}
											}
										}
									}
									
									if ((addtag && tags.contains(tagid)) || (addtag && mail.equals("true"))) {
										addtag = false;
									} else if (addtag) {
										tags.add(tagid);
									}
									
									if (addtag) {
										
										if (!hasDataToSend) {
											mailBody 	= buildBeaconAlertDataTable();
											hasDataToSend = true;
											
										}
										
										bcn.setLocalInactivityMailSent("true");
										bcn.setAssginedLocationLastSeen(lastSeenDate);
										inactiveBeacons.add(bcn);
										
										if (cur_location.equals(location)) {
											cur_location = "-";
										}
										
										mailBody.append("<tr>")
												.append("<td>" + i + "</td>")
												.append("<td  style=\"color:blue\">" + tagid + newline + "</td>")
												.append("<td  style=\"color:blue\">" + tagType + newline + "</td>")
												.append("<td  style=\"color:blue\">" + assignedto + newline + "</td>")
												.append("<td  style=\"color:blue\">" + floorname + newline + "</td>")
												.append("<td  style=\"color:blue\">" + location + newline + "</td>")
												.append("<td  style=\"color:blue\">" + lastSeenDate + newline + "</td>")
												.append("<td  style=\"color:blue\">" + timespent + newline + "</td>")
												.append("<td  style=\"color:blue\">" + cur_location + newline + "</td>")
												.append("</tr>");
									}
								}
								
								if (inactiveBeacons.size() > 0) {
									getBeaconService().save(inactiveBeacons);
								}
							}
							
							beaconlist = new ArrayList<Beacon>();
							if(activeBeacons != null && activeBeacons.size()>0){
								for(Beacon bcn: activeBeacons){
									bcn.setLocalInactivityMailSent("false");
									beaconlist.add(bcn);
								}
								getBeaconService().save(beaconlist);
							}
						}

						if (needsMail && hasDataToSend) {
							mailBody.append("</table><br/></div><br/>");
							sendMailToUsers(mailBody, useracclist);
							//getCustomerUtils().logs(logs, classname, "NOT IN ASSIGNED LOCATION TAGS needsMail == "+needsMail+" has data to send "+hasDataToSend);
						} else {
							//getCustomerUtils().logs(logs, classname, "NOT IN ASSIGNED LOCATION TAGS needsMail == "+needsMail+" has data to send "+hasDataToSend);
						}
					}
				
					long elapsedTime = System.currentTimeMillis() - sTimer;
					//getCustomerUtils().logs(logs, classname, " elapsed time SendMail function " + elapsedTime);
					
					
					Collection<Beacon> inactiveBeacon = beaconservice.getSavedBeaconByCidStateAndStatus(cid, "inactive", "checkedout");
					
					int curtInacTagsCount = 0;
					int prevInacTagCount  = cx.getTagAlertCount();
					
					if(inactiveBeacon != null && inactiveBeacon.size()>0){
						curtInacTagsCount = inactiveBeacon.size();
					}
					if (prevInacTagCount != curtInacTagsCount) {
						cx.setTagAlertCount(curtInacTagsCount);
						customerservice.save(cx);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private String BuildFsqlOrCondition(net.sf.json.JSONArray placeIds, String fieldName) {
				if (placeIds.size() > 0) {
		    		StringBuilder sb = new StringBuilder(fieldName).append(":(");
		    		boolean isFirst = true;
		    		Iterator<String> iter = placeIds.iterator();
		    		String placeid;
					while (iter.hasNext()) {
						placeid = iter.next();
						if (isFirst) {
							isFirst = false;
						} else {
							sb.append(" OR ");
						}
						sb.append("\"" + placeid + "\"");
					}
					sb.append(")");

					return sb.toString();
				} else {
					return "";
				}
			}
			
			private String getTimeSpent(String elapsed) {
				String timespent = "-";
				int elps = Integer.parseInt(elapsed);
				if (elps != 0) {
					int hours = elps / 3600;
					int minutes = (elps % 3600) / 60;
					int seconds = (elps % 3600) % 60;
					timespent = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				}
				return timespent;
			}

		});
		t.start();
	}
	
	
	
	private StringBuilder buildInactivityTable () {
		StringBuilder mailBody = new StringBuilder();
		mailBody.append("<div style=\"padding:0px\">").append("Dear Customer,").append("<br/>").append("<br/>")
				.append("&nbsp;&nbsp;You have a new Alert Message!!!<br/>")
				.append("&nbsp;&nbsp;Below is the detailed list of inactive tags with its last seen location. ")
				.append("Kindly look into this as a high priority.<br/>")
				.append("<br/>")
				.append("&nbsp;&nbsp;TAGS NOT SEEN IN ANY VENUE ARE LISTED BELOW <br/> <br/>")
				.append("<table border=\"1\" style=\"border-collapse:collapse;text-align:center;width: 100%;\">")
				.append("<tr>")
				.append(" <th style=\"padding:10px\">S.No</th>")
				.append(" <th style=\"padding:10px\">Id</th>")
				.append(" <th style=\"padding:10px\">Tag Type</th>")
				.append(" <th style=\"padding:10px\">Assigned To</th>")
				.append(" <th style=\"padding:10px\">Floor Name</th>")
				.append(" <th style=\"padding:10px\">Location</th>")
				.append(" <th style=\"padding:10px\">Last Seen</th>")
				.append("</tr>");
			
		return mailBody;
		
	}
	
	private StringBuilder buildBeaconAlertDataTable () {
		StringBuilder mailBody = new StringBuilder();
		mailBody.append("<div style=\"padding:0px\">").append("Dear Customer,").append("<br/>").append("<br/>")
				.append("&nbsp;&nbsp;You have a new Alert Message!!!<br/>")
				.append("&nbsp;&nbsp;Below is the detailed list of Tags who are not in their assigned location. ")
				.append("Kindly look into this as a high priority.<br/>")
				.append("<br/>")
				.append("&nbsp;&nbsp;<br/> TAGS INACTIVE IN ASSIGNED REGION <br/> <br/>")
				.append("<table border=\"1\" style=\"border-collapse:collapse;text-align:center;width: 100%;\">")
				.append("<tr>")
				.append(" <th style=\"padding:10px\">S.No</th>")
				.append(" <th style=\"padding:10px\">Id</th>")
				.append(" <th style=\"padding:10px\">Tag Type</th>")
				.append(" <th style=\"padding:10px\">Assigned To</th>")
				.append(" <th style=\"padding:10px\">Floor Name</th>")
				.append(" <th style=\"padding:10px\">Location Name</th>")
				.append(" <th style=\"padding:10px\">Time</th>")
				.append(" <th style=\"padding:10px\">Timespent</th>")
				.append(" <th style=\"padding:10px\">Current Location</th>")
				.append("</tr>");
		
		return mailBody;
		
	}
	private void sendMailToUsers(StringBuilder mailBody, List<UserAccount> useraccountlist) {
		try {

			String cid  = useraccountlist.get(0).getCustomerId();
			Customer cx = getCustomerService().findById(cid);
			String emailId  = null;
			Boolean enablelog = true;
			
			if (cx.getLogs() == null || cx.getLogs().equals("false")) {
				enablelog = false;
			}

			String subject = "Qubercomm Notification For Tags Not in Assigned Location";

			if (mailBody.toString().contains("detailed list of inactive tags")) {
				subject = "Qubercomm Notification For Inactive Tags";
			}

			for (UserAccount user : useraccountlist) {
				emailId = user.getEmail();
				getCustomerUtils().logs(enablelog, classname, " mail sent to email id " + emailId);
				getCustomerUtils().customizeSupportEmail(cid, emailId, subject, mailBody.toString(), null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*private void constructEmailAlert(final String emailId, final StringBuilder body,boolean enablelog) {
		getJavaMailSender().send(new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws MessagingException {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				message.setTo(emailId);
				
				if(body.toString().contains("detailed list of inactive tags")){
					message.setSubject("Qubercomm Notification For Inactive Tags");
				}else{
					message.setSubject("Qubercomm Notification For Tags Not in Assigned Location");
				}
				
				message.setText(body.toString(), true);
			}
		});
		getCustomerUtils().logs(enablelog, classname, " mail sent to email id " +emailId) ;
	}*/
	
	public List<String> getDemoInactiveTagMac() {
		return Arrays.asList("3F:23:AC:22:FF:F6", "3F:23:AC:22:F8:F8");
	}
	
	public CustomerService getCustomerService() {
		if (customerservice == null) {
			customerservice = Application.context.getBean(CustomerService.class);
		}
		return customerservice;
	}

	public PortionService getPortionService() {
		if (portionservice == null) {
			portionservice = Application.context.getBean(PortionService.class);
		}
		return portionservice;
	}

	public BeaconService getBeaconService() {
		if (beaconservice == null) {
			beaconservice = Application.context.getBean(BeaconService.class);
		}
		return beaconservice;
	}

	private NetworkDeviceService getNetworkDeviceService() {

		try {
			if (networkDeviceService == null) {
				networkDeviceService = Application.context.getBean(NetworkDeviceService.class);
			}
		} catch (Exception e) {

		}
		return networkDeviceService;
	}

	private FSqlRestController getFSqlRestController() {
		if (fsqlRestController == null && Application.context != null) {
			fsqlRestController = Application.context.getBean(FSqlRestController.class);
		}
		return fsqlRestController;
	}

	private GeoFinderRestController getGeoFinderRestController() {
		if (geoFinderRestController == null && Application.context != null) {
			geoFinderRestController = Application.context.getBean(GeoFinderRestController.class);
		}
		return geoFinderRestController;
	}

	public ElasticService getElasticService() {
		if (elasticService == null) {
			elasticService = Application.context.getBean(ElasticService.class);
		}
		return elasticService;
	}

	public CCC getCCC() {
		if (_CCC == null) {
			_CCC = Application.context.getBean(CCC.class);
		}
		return _CCC;
	}
	
	public CustomerUtils getCustomerUtils() {
		if (customerUtils == null) {
			customerUtils = Application.context.getBean(CustomerUtils.class);
		}
		return customerUtils;
	}

	public BeaconDeviceService getBeaconDeviceService() {
		if (beaconDeviceService == null) {
			beaconDeviceService = Application.context.getBean(BeaconDeviceService.class);
		}
		return beaconDeviceService;
	}
	
	public UserAccountService getUserAccountService() {
		if (userAccountService == null) {
			userAccountService = Application.context.getBean(UserAccountService.class);
		}
		return userAccountService;
	}
	
	public JavaMailSender getJavaMailSender() {
		if (javaMailSender == null) {
			javaMailSender = Application.context.getBean(JavaMailSender.class);
		}
		return javaMailSender;
	}
	
	public BeaconAlertDataService getBeaconAlertDataService() {
		if (beaconAlertDataService == null) {
			beaconAlertDataService = Application.context.getBean(BeaconAlertDataService.class);
		}
		return beaconAlertDataService;
	}
	
	public NetworkConfRestController getNetworkConfRestController() {
		if (networkConfRestController == null) {
			networkConfRestController = Application.context.getBean(NetworkConfRestController.class);
		}
		return networkConfRestController;
	}
	
	private ReportBeaconService getReportBeaconService() {
		if (reportBeaconservice == null) {
			reportBeaconservice = Application.context.getBean(ReportBeaconService.class);
		}
		return reportBeaconservice;
	}
}