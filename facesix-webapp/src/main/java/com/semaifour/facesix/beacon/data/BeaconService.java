package com.semaifour.facesix.beacon.data;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.data.elasticsearch.ElasticService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import com.semaifour.facesix.mqtt.Payload;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.CustomerUtils;
import net.sf.json.JSONArray;

/**
 * 
 * Service to manage beacons
 * 
 * @author mjs
 *
 */

@Service
public class BeaconService {

	private static String classname 	= BeaconService.class.getName();

	Logger LOG = LoggerFactory.getLogger(classname);

	private static Map<String, Map<String, Beacon>> scannedBeacons = new HashMap<String, Map<String, Beacon>>();

	@Autowired(required = false)
	private BeaconRepository repository;

	@Autowired
	private DeviceEventPublisher deviceEventPublisher;

	@Autowired
	private ElasticService elasticService;
	
	@Autowired
	CCC _CCC;

	@Autowired
	private BeaconDeviceService beaconDeviceService;
	
	@Autowired
	private CustomerUtils customerUtils;
	
	@Autowired
	private PortionService portionService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private NetworkDeviceService networkDeviceService;

	String beaconEventTable = "facesix-int-beacon-event";
	
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	DateFormat parse  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public BeaconService() {
		LOG.info("service created");
	}

	@PostConstruct
	public void init() {
		beaconEventTable = _CCC.properties.getProperty("facesix.data.beacon.trilateration.table", beaconEventTable);
		LOG.info("service started...");
	}

	
	String mqttMsgTemplate = " \"opcode\":\"{0}\", \"type\":\"{1}\",\"uid\":\"{2}\", \"by\":\"{3}\", \"newversion\":\"{4}\", \"conf\":{5}";
	
	@Autowired
	private DeviceEventPublisher mqttPublisher;
	
	
	/**
	 * Returns beacons in scanned list currently
	 * 
	 * @return
	 */
	public Collection<Beacon> getScannedBeacons() {
		List<Beacon> list = new ArrayList<Beacon>();
		for(Map<String, Beacon> m : scannedBeacons.values()) {
			list.addAll(m.values());
		}
		return list;
	}

	/**
	 * Returns beacons in scanned list currently
	 * 
	 * @return
	 */
	public Collection<Beacon> getScannedBeacons(String scope) {
		Map<String, Beacon> m  = getScannedBeaconMap(scope);
		return m == null ? null : m.values();
	}

	/**
	 * Adds a beacon to scanned list for the given scope
	 * 
	 * @param beacon
	 * @param sope
	 */
	public void addScannedBeacon(Beacon beacon, String scope) {
		if (scope == null) scope = "global";
		Map<String, Beacon> m = scannedBeacons.get(scope);
		if (m == null) {
			m = new HashMap<String, Beacon>();
			scannedBeacons.put(scope, m);
		}
		
		m.put(beacon.getMacaddr(), beacon);

	}

	/**
	 * Remove a beacon from scanned list
	 * 
	 * @param beacon
	 * @param scope
	 */
	public Beacon removeScannedBeacon(String macaddr, String scope) {
		Map<String, Beacon> m =  getScannedBeaconMap(scope);
		return m == null ? null : m.remove(macaddr);
	}

	/**
	 * Clears all scanned beacons
	 * 
	 */
	public void clearScannedBeacons(String scope) {
		Map<String, Beacon> m =  getScannedBeaconMap(scope);
		if (m != null) m.clear();
	}

	/**
	 * Saves beacon
	 * 
	 * @param beacon
	 * @return
	 */
	public Beacon save(Beacon beacon, boolean notify) {
		beacon = repository.save(beacon);
		//LOG.info("Beacon saved successfully " +beacon.getMacaddr());
		if (beacon.getPkid() == null) {
			beacon.setPkid(beacon.getId());
			beacon = repository.save(beacon);
		}
		
		if(notify) {
			notify(beacon,"tag-update");
		}
		
		return beacon;
	}

	private boolean notify(Beacon beacon, String message) {
		try {
			Payload payload = new Payload("beacon-" + beacon.getStatus(), 
					beacon.getModifiedBy(),
					beacon.getScannerUid(),
					message);
					payload.put("beacon", beacon);
					deviceEventPublisher.publish(payload.json(), payload.target());
		
		    return true;
		
		} catch (Exception e){
			LOG.info("Beacon notify error ,"+e);
		}
		return false;
	}

	public Beacon getScannedBeacon(String macaddr, String scope) {
		Map<String, Beacon> m =  getScannedBeaconMap(scope);
		return m == null ? null : m.get(macaddr);
	}
	
	/**
	 * Returns a resolved beacon map for the give scope.
	 * If not found for the scope, returns map for global scope.
	 * 
	 * @param scope
	 * @return
	 */
	public Map<String, Beacon> getScannedBeaconMap(String scope) {
		Map<String, Beacon> m =  scannedBeacons.get(scope);
		return m != null ? m : scannedBeacons.get("global");
	}
	
	public List<Beacon> getSavedBeaconsByStatus(String status) {
		return repository.findByStatus(status);
	}

	public List<Beacon> getSavedBeaconsByScanner(String scannerUid) {
		return repository.findByStatus(scannerUid);
	}

	public List<Beacon> getSavedBeaconsByAssignedTo(String assignedTo) {
		return repository.findByStatus(assignedTo);
	}
	
	public List<Beacon> getSavedBeaconBySidAndAssignedTo(String sid, String name){
		return repository.getSavedBeaconBySidAndAssignedTo(sid, name);
	}
	
	public Beacon getSavedBeacon(String id) {
		return repository.findOne(id);
	}

	public List<Beacon> getSavedBeaconsByUid(String uid) {
		return repository.findByUid(uid);
	}

	public List<Beacon> getSavedBeaconByMacaddr(String macaddr) {
		return repository.findByMacaddr(macaddr);
	}
	
	public List<Beacon> getSavedBeaconByCid(String cid){
		return repository.findByCid(cid);
	}
	
	public List<Beacon> getSavedBeaconBySid(String sid){
		return repository.findBySid(sid);
	}
	
	public List<Beacon> getSavedBeaconBySpid(String spid){
		return repository.findBySpid(spid);
	}
	
	public Beacon getSavedBeaconByUuid(String uuid){
		return repository.findByUuid(uuid);
	}
	
	public List<Beacon> getSavedBeaconByServerid(String serverid){
		return repository.findByServerid(serverid);
	}
	public List<Beacon> findByReciverinfo(String reciverUid){
		return repository.findByReciverinfo(reciverUid);
	}
	
	public List<Beacon> getSavedBeaconByTagType(String type){
		return repository.findByTagType(type);
	}
	
	public List<Beacon> findAll(){
		return repository.findAll();
	}
	
	public Beacon getSavedBeaconByUuidAndMajorAndMinor(String uuid, int major, int minor){
		return repository.findByUuidAndMajorAndMinor(uuid, major, minor);
	}

	public boolean sendBeaconCommand(Payload payload) {
		return deviceEventPublisher.publish(payload.json(), payload.target());
	}

	public void postEvent(Map<String, Object> map) {
		elasticService.post(beaconEventTable,"visit", map);
	}

	public void postReportEvent(Map<String, Object> map) {
		elasticService.post(beaconEventTable,"trilateration", map);
	}

	public Beacon findOneByMacaddr(String macId) {
		List<Beacon> list = getSavedBeaconByMacaddr(macId);
		if (list != null & list.size() > 0 ) {
			Beacon bdev = list.get(0);
			if (macId.equalsIgnoreCase(bdev.getMacaddr())){
				return bdev;
			}			
		}
		return null;
	}
	
	public List<Beacon> findByReciverId(String uid){
		return repository.findByReciverId(uid);
	}
	
	public void delete(Beacon beacon) {
		repository.delete(beacon);
	}
	
	public void deleteAll() {
		repository.deleteAll();
	}

	public Beacon checkout(String macaddr, String assto, String tag_type, 
						   String cid, String name, String bi, String txpwr, String tagmod, String reftx,
						   String jsonScannerUid,
						   String whoami, 
						   HttpServletRequest request) {
		
		Beacon beacon = null;
		beacon = findOneByMacaddr(macaddr);
		
		String scannerUid 		= "0";
		int minor 				=  0;
		int major 				=  0;
		int batteryLevel 		=  0;
		long batteryTimestamp   =  0;
		String 	uid 			= "0";
		
		LOG.info("cid " +cid);
		
		if (beacon == null) {
			beacon = new Beacon();
			beacon.setMacaddr(macaddr);
			beacon.setCreatedBy(whoami);
			beacon.setCreatedOn(new Date());
			
			//LOG.info("=========New Tags Entry in MongoDB ====> mac  " +macaddr);
		}
	
		if (beacon != null) {
			if (beacon.getStatus() != null && beacon.getStatus().equalsIgnoreCase("checkedout")) {
				//LOG.info(" duplicate  ====> mac  " +beacon.getMacaddr());
				//LOG.info(" minor ======> " + beacon.getMinor() +"major " +beacon.getMajor());
				//LOG.info("cid  ========> " +beacon.getCid());
				//LOG.info(" scannerUid=========== > " +beacon.getScannerUid());				
				return null;
			} 
		}
		
		Beacon Newbeacon = getScannedBeacon(macaddr, request.getSession().getId());	
		if (Newbeacon != null) {			
			scannerUid  	 = Newbeacon.getScannerUid();
			minor 			 = Newbeacon.getMinor();
			major 			 = Newbeacon.getMajor();
			batteryLevel     = Newbeacon.getBattery_level();
			batteryTimestamp = Newbeacon.getBattery_timestamp();
			uid              = Newbeacon.getUid();
			
			//LOG.info("beacon major=========== > " +major);
			//LOG.info("beacon minor=========== > " +minor);
			//LOG.info("beacon scannerUid=========== > " +scannerUid);
			//LOG.info("New Tags====> mac  " +macaddr);
			//LOG.info("beacon majore=========== > " +major);
			//LOG.info("beacon minor=========== > " +minor);
			//LOG.info("beacon batteryLevel=========== > " +batteryLevel);
			//LOG.info("beacon uid=========== > " +uid);
			
		} else {
			if (name.equals("qubertag")) {
				scannerUid  	 = jsonScannerUid;
				minor 			 =  1;
				major 			 =  1000;
				batteryLevel     =  0;
				uid              = "00BBCCEE-6666-4FCE-11AC-00BBCCEE66AA";
				LOG.info("beacon scannerUid=========== > " +scannerUid);
			} else {
				LOG.info("=============Invalid TAGS===================");
				return null;
			}
		}
		
		//LOG.info("=== scannerUid" + scannerUid);
		//LOG.info("===== minor" + minor + " major " + major);
		//LOG.info("===== checkout beacons	" + beacon);
		//LOG.info("==== assto	" + assto + "	macaddr	" + macaddr + "tagname" + name);
		//LOG.info("=======bi	" + bi + "	txpwr	" + txpwr);
		//LOG.info("batteryLevel" + batteryLevel + "	batteryTimestamp	" + batteryTimestamp);
		
		int tpwr = Integer.parseInt(txpwr);
		int b 	 = Integer.parseInt(bi);
		//LOG.info("BECON	" + b + "	TXPWR	" + tpwr);
		
		if (macaddr != null && assto != null) {
			beacon.setScannerUid(scannerUid);
			beacon.setMinor(minor);
			beacon.setMajor(major);
			beacon.setName(name);
			beacon.setAssignedTo(assto);
			beacon.setUid(uid);
			
			if (tpwr != 55) {
				beacon.setTxPower(tpwr);
			}
			
			if (b != 55) {
				beacon.setInterval(b);
			}
			
			beacon.setState("inactive");
			beacon.setMailsent("false");
			beacon.setLastactive(0);
			beacon.setTagType(tag_type);
			beacon.setStatus("checkedout");
			beacon.setUpdatedstatus("checkedout");
			beacon.setModifiedOn(new Date());
			beacon.setModifiedBy(whoami);
			beacon.setCid(cid);
			beacon.setBattery_level(100);
			beacon.setBattery_timestamp(batteryTimestamp);
			beacon.setSid(null);
			beacon.setSpid(null);
			beacon.setLocation(null);
			beacon.setLastSeen(0);
			beacon.setReciveralias(null);
			beacon.setReciverinfo(null);
			beacon.setServerid(null);
			beacon.setEntryFloor(null);
			beacon.setEntry_loc(null);
			beacon.setDebug("disable");
			beacon.setExitTime(null);
			
			beacon.setTagModel(tagmod);
			beacon.setRefTxPwr(reftx);
			
			beacon = save(beacon, true);
			removeScannedBeacon(beacon.getMacaddr(), request.getSession().getId());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("opcode", 	   "checkedout");
			map.put("visitId", 		beacon.getId());
			map.put("timestamp", 	new Date());
			map.put("name", 		assto);
			map.put("tag_type", 	tag_type);
			map.put("tag_model", 	tagmod);
			map.put("ref_txpwr", 	reftx);
			postEvent(map);
		}
		
		return beacon;
	}
	
	/**
	 * Check-in a used beacon
	 * 
	 * @param id
	 * @return
	 */
	public @ResponseBody Beacon checkin(String id, String whoami) {
		
		Beacon beacon = getSavedBeacon(id);
		//LOG.info("BeaconSaved===>" +beacon + " Status" + beacon.getStatus());
		if (beacon != null && "checkedout".equals(beacon.getStatus())) {
			
			/*
			 *  Mark Exit For Beacon
			 * 
			 */
			
			markExitForBeacon(beacon);
			
			beacon.setStatus("checkedin");
			beacon.setUpdatedstatus("checkedin");
			beacon.setModifiedOn(new Date());
			beacon.setModifiedBy(whoami);
			beacon.setTemplate(null);
			beacon = save(beacon, true);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("opcode", "checkedin");
			map.put("visitId", beacon.getId());
			map.put("timestamp", new Date());
			postEvent(map);
		
		}
		return beacon;
	}

	
	/*
	 *  Mark Exit For Beacon
	 * 
	 */
	
	public void markExitForBeacon(Beacon beacon) {
		
		if(beacon == null){
			return;
		}
		
		Map<String,Object> postMap = new HashMap<String,Object>();
		
		String tag_uid = beacon.getMacaddr();
		String tagtype = beacon.getTagType();
		String assignedto = beacon.getAssignedTo();
		
		String cid = beacon.getCid();
		String sid = beacon.getSid();
		String spid = beacon.getSpid();
		String location = beacon.getReciverinfo();
		
		String entry_loc = beacon.getEntry_loc();
		String entry_floor = beacon.getEntry_floor();
		String exitTime = null;
		
		long elapsed_loc = 0;
		long elapsed_floor= 0;
		long exit = 0;
		
		try{
			
			Customer cust = customerService.findById(cid);
			
			if (cust == null || (entry_loc == null || entry_loc.isEmpty())
							 || (entry_floor == null || entry_floor.isEmpty())) {
				return;
			}
			
			TimeZone timezone = customerUtils.FetchTimeZone(cust.getTimezone());
			format.setTimeZone(timezone);
			
			exitTime = format.format(new Date());
			exit = format.parse(exitTime).getTime();
			
			elapsed_loc = exit - format.parse(entry_loc).getTime();
			elapsed_floor = exit - format.parse(entry_floor).getTime();
			
			postMap.put("opcode", 	 "reports");
			postMap.put("tagid",      tag_uid);
			postMap.put("tagtype",	  tagtype);
			postMap.put("assingedto", assignedto);
			
			postMap.put("cid", 		  cid);
			postMap.put("sid", 		  sid);
			postMap.put("spid", 	  spid);
			postMap.put("location",   location);
			
			postMap.put("entry_floor",entry_floor);
			postMap.put("entry_loc",  entry_loc);
			
			postMap.put("exit_floor", exitTime);
			postMap.put("exit_loc",   exitTime);
			
			postMap.put("elapsed_floor", elapsed_floor);
			postMap.put("elapsed_loc",   elapsed_loc);
			
			postReportEvent(postMap);
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public Beacon saveBeaconTags(Beacon beacon, String universalId, String universlaName, boolean notify) {
		beacon = repository.save(beacon);
		if (notify) {
			notifyMQTT(beacon, "tag-update",universalId,universlaName);
		}
		return beacon;
	}
	
	public boolean notifyMQTT(Beacon device, String opcode,String universalId, String universlaName) {
		try {
			
			String conf =  device.getTemplate();
			net.sf.json.JSONObject template	= net.sf.json.JSONObject.fromObject(conf);
		
			String message = MessageFormat.format(mqttMsgTemplate,new Object[]{opcode, 
																				"tag",
																	   device.getMacaddr().toUpperCase(),
																	   device.getModifiedBy(),
																	   "1",
																	   template.toString()});
			
			mqttPublisher.publish("{" + message + "}", universalId.toUpperCase());
			
			//LOG.info("BEACON TAG MQTT MESSAGE " + message);
			
			return true;
		} catch (Exception e) {
			LOG.warn("Failed to notify update", e);
			return false;
		}
	}

	public List<Beacon> save(List<Beacon> jBeaconlist) {
		jBeaconlist=repository.save(jBeaconlist);
		return jBeaconlist;
	}

	public List<Beacon> getSavedBeaconBySidAndTagType(String sid, String type) {
		return repository.getSavedBeaconBySidAndTagType(sid,type);
	}
	
	public String batteryStatus(int intBattery) {
		
		String color = "black";
		String fafa = "fa fa-battery-empty fa-2x";
		
		if (intBattery >= 75) {
			color = "green";
			fafa  = "fa fa-battery-full fa-2x";
		} else if (intBattery >= 50 && intBattery <= 75) {
			color = "green";
			fafa  = "fa fa-battery-three-quarters fa-2x";
		} else if (intBattery >= 25 && intBattery <= 50) {
			color = "orange";
			fafa  = "fa fa-battery-half fa-2x";
		} else if (intBattery >= 15 && intBattery <= 25) {
			color = "red";
			fafa  = "fa fa-battery-quarter fa-2x";
		} else if (intBattery <=15) {
			color = "red";
			fafa  = "fa fa-battery-empty fa-2x";
		} 
		return fafa+"&"+color;
	}

	public void delete(List<Beacon> beacon) {
		repository.delete(beacon);
	}
	
	public List<Beacon> getSavedBeaconByCidAndTagType(String cid, String type) {
		return repository.getSavedBeaconByCidAndTagType(cid,type);
	}

	public List<Beacon> getSavedBeaconByCidAndAssignedTo(String cid, String name) {
		return repository.getSavedBeaconByCidAndAssignedTo(cid, name);
	}
	
	public List<Beacon> getSavedBeaconByMacaddrAndStatus(String macaddr, String status) {
		return repository.getSavedBeaconByMacaddrAndStatus(macaddr,status);
	}

	public List<Beacon> getSavedBeaconBySpidAndStatus(String spid, String status) {
		return repository.getSavedBeaconBySpidAndStatus(spid,status);
	}

	public Collection<Beacon> getSavedBeaconByCidAndStatus(String cid,String status) {
		return  repository.getSavedBeaconByCidAndStatus(cid,status);
	}

	public List<Beacon> getSavedBeaconByCidAndMacAddr(String cid, String macaddr) {
		return repository.getSavedBeaconByCidAndMacAddr(cid,macaddr);
	}
	
	public List<Beacon> getSavedBeaconBySidAndStatus(String sid,String status) {
		return  repository.getSavedBeaconBySidAndStatus(sid,status);
	}

	public List<Beacon> findByCidStatusMailSentLastSeenBefore(String cid,String status,String mailSent,long lastseen) {
		return repository.findByCidStatusMailSentLastSeenBefore(cid,status,mailSent,lastseen);
	}

	public List<Beacon> getSavedBeaconBySpidStateAndStatus(String spid, String state, String status) {
		return repository.getSavedBeaconBySpidStateAndStatus(spid,state,status);
	}

	public List<Beacon> getSavedBeaconByCidSidStateAndStatus(String cid,String sid, String state, String status) {
		return repository.getSavedBeaconByCidSidStateAndStatus(cid,sid,state,status);
	}

	public List<Beacon> getSavedBeaconByCidSpidStateAndStatus(String cid, String spid, String state, String status) {
		return repository.getSavedBeaconByCidSpidStateAndStatus(cid,spid,state,status);
	}

	public Collection<Beacon> getSavedBeaconByCidStateAndStatus(String cid, String state, String status) {
		return repository.getSavedBeaconByCidSpidStateAndStatus(cid,state,status);
	}

	public List<Beacon> getSavedBeaconByCidAndSid(String cid, String sid) {
		return repository.getSavedBeaconByCidAndSid(cid,sid);
	}

	public Collection<Beacon> getSavedBeaconByCidSidAndStatus(String cid, String sid, String status) {
		return repository.getSavedBeaconByCidSidAndStatus(cid,sid,status);
	}

	public List<Beacon> findByCidTagTypeStatusMailSentLastSeenBefore(String cid, String tagType, String status,
			String sentMail, long time) {
		return repository.findByCidTagTypeStatusMailSentLastSeenBefore(cid,tagType,status,sentMail,time);
	}
	
	public List<Beacon> getSavedBeaconByReciverinfoAndStatus(String receiverInfo, String status) {
		return repository.getSavedBeaconByReciverinfoAndStatus(receiverInfo,status);
	}

	public List<Beacon> findByCidStatusAndBatteryLevel(String cid, String status, int battery) {
		return repository.findByCidStatusAndBatteryLevel(cid,status,battery);
	}

	public Collection<Beacon> getSavedBeaconByReciverinfoStateAndStatus(String receiverinfo, String state,
			String status) {
		return repository.getSavedBeaconByReciverinfoStateAndStatus(receiverinfo,state,status);
	}

	public List<Beacon> findBySpidStateStatusAndTagType(String spid, String state, String status, String tagtype) {
		return repository.findBySpidStateStatusAndTagType(spid,state,status,tagtype);
	}

	public List<Beacon> getSavedBeaconByCidTagTypeAndStatus(String cid, String tagtype, String status) {
		return repository.getSavedBeaconByCidTagTypeAndStatus(cid,tagtype,status);
	}

	public List<Beacon> findByCidTagTypeStatusLastSeenBefore(String cid, String tagtype, String status, long inactivityTime) {
		return repository.findByCidTagTypeStatusLastSeenBefore( cid,  tagtype,  status, inactivityTime);
	}

	public List<Beacon> findByCidTagTypeStatusAndTagIds(String cid, String tagtype, String status,
			net.sf.json.JSONArray tagids) {
		
		List<String> macaddrs = convertJSONArrayToList(tagids);
		return repository.findByCidTagTypeStatusAndMacaddr( cid,  tagtype,  status, macaddrs);
	}

	public List<Beacon> findByCidTagTypeStatusAndNotSids(String cid, String tagtype, String status,
			net.sf.json.JSONArray placeIds) {
		List<String> sid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusAndNotSid(cid,tagtype,status,sid);
	}

	public List<Beacon> findByCidTagTypeStatusAndNotSpids(String cid, String tagtype, String status,
			net.sf.json.JSONArray placeIds) {
		List<String> spid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusAndNotSpid(cid,tagtype,status,spid);
	}

	public List<Beacon> findByCidTagTypeStatusAndNotReceiverInfos(String cid, String tagtype, String status,
			net.sf.json.JSONArray placeIds) {
		List<String> receiverinfo = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusAndNotReceiverInfo(cid,tagtype,status,receiverinfo);
	}
	
	public List<Beacon> findByCidTagTypeStatusTagIdsAndNotSids(String cid, String tagtype, String status,
			net.sf.json.JSONArray tagids, net.sf.json.JSONArray placeIds) {
		List<String> macaddr = convertJSONArrayToList(tagids);
		List<String> sid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusTagIdsAndNotSid(cid,tagtype,status,macaddr,sid);
	}

	public List<Beacon> findByCidTagTypeStatusTagIdsAndNotSpids(String cid, String tagtype, String status,
			net.sf.json.JSONArray tagids, net.sf.json.JSONArray placeIds) {
		List<String> macaddr = convertJSONArrayToList(tagids);
		List<String> spid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusTagIdsAndNotSpid(cid,tagtype,status,macaddr,spid);
	}

	public List<Beacon> findByCidTagTypeStatusTagIdsAndNotReceiverInfos(String cid, String tagtype, String status,
			net.sf.json.JSONArray tagids, net.sf.json.JSONArray placeIds) {
		List<String> macaddr = convertJSONArrayToList(tagids);
		List<String> receiverinfo = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusTagIdsAndNotReceiverInfo(cid,tagtype,status,macaddr,receiverinfo);
	}
	
	/*public List<Beacon> findByCidTagTypeStatusAndAssignedTo(String cid, String tagtype, String status,
			net.sf.json.JSONArray tagids) {
		
		List<String> assignedto = convertJSONArrayToList(tagids);
		return repository.findByCidTagTypeStatusAndAssignedTo( cid,  tagtype,  status, assignedto);
	}*/
	
	public List<Beacon> findByCidStatusAndMacaddrs(String cid, String status, JSONArray tagnames) {
		List<String> list = convertJSONArrayToList(tagnames);
		return repository.findByCidStatusAndMacaddrs(cid,status,list);
	}
	
	public List<Beacon> findByCidTagTypeStatusSidsAndLastSeenBefore(String cid, String tagtype, String status,
			net.sf.json.JSONArray placeIds, long inactivityTime) {
		List<String> sid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusSidsAndLastSeenBefore(cid,tagtype,status,sid,inactivityTime);
	}
	
	public List<Beacon> findByCidTagTypeStatusSpidsAndLastSeenBefore(String cid, String tagtype, String status,
			net.sf.json.JSONArray placeIds, long inactivityTime) {
		List<String> spid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusSpidsAndLastSeenBefore(cid,tagtype,status,spid,inactivityTime);
	}
	
	public List<Beacon> findByCidTagTypeStatusReceiverInfosAndLastSeenBefore(String cid, String tagtype, String status,
			net.sf.json.JSONArray placeIds, long inactivityTime) {
		List<String> receiverinfo = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusReceiverInfosAndLastSeenBefore(cid,tagtype,status,receiverinfo,inactivityTime);
	}
	
	public List<Beacon> findByCidTagTypeStatusTagIdsSidsAndLastSeenBefore(String cid, String tagtype, String status,
			net.sf.json.JSONArray tagids, net.sf.json.JSONArray placeIds, long inactivityTime) {
		List<String> macaddr = convertJSONArrayToList(tagids);
		List<String> sid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusTagIdsSidsAndLastSeenBefore(cid,tagtype,status,macaddr,sid,inactivityTime);
	}
	
	public List<Beacon> findByCidTagTypeStatusTagIdsSpidsAndLastSeenBefore(String cid, String tagtype, String status,
			net.sf.json.JSONArray tagids, net.sf.json.JSONArray placeIds, long inactivityTime) {
		List<String> macaddr = convertJSONArrayToList(tagids);
		List<String> spid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusTagIdsSpidsAndLastSeenBefore(cid,tagtype,status,macaddr,spid,inactivityTime);
	}
	
	public List<Beacon> findByCidTagTypeStatusTagIdsReceiverInfosAndLastSeenBefore(String cid, String tagtype, String status,
			net.sf.json.JSONArray tagids, net.sf.json.JSONArray placeIds, long inactivityTime) {
		List<String> macaddr = convertJSONArrayToList(tagids);
		List<String> receiverinfo = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusTagIdsReceiverInfosAndLastSeenBefore(cid,tagtype,status,macaddr,receiverinfo,inactivityTime);
	}
	
	public List<Beacon> findByCidTagTypeStatusSidsAndLastSeenAfterMailSent(String cid, String tagtype, String status,
			net.sf.json.JSONArray placeIds, long inactivityTime, String activeMailSent) {
		List<String> sid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusSidsAndLastSeenAfterMailSent(cid,tagtype,status,sid,inactivityTime,activeMailSent);
	}

	public List<Beacon> findByCidTagTypeStatusSpidsAndLastSeenAfterMailSent(String cid, String tagtype, String status,
			net.sf.json.JSONArray placeIds, long inactivityTime, String activeMailSent) {
		List<String> spid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusSpidsAndLastSeenAfterMailSent(cid,tagtype,status,spid,inactivityTime,activeMailSent);
	}

	public List<Beacon> findByCidTagTypeStatusReceiverInfosAndLastSeenAfterMailSent(String cid, String tagtype,
			String status, net.sf.json.JSONArray placeIds, long inactivityTime, String activeMailSent) {
		List<String> receiverinfo = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusReceiverInfosAndLastSeenAfterMailSent(cid,tagtype,status,receiverinfo,inactivityTime,activeMailSent);
	}

	public List<Beacon> findByCidTagTypeStatusTagIdsSidsAndLastSeenAfterMailSent(String cid, String tagtype,
			String status, net.sf.json.JSONArray tagids, net.sf.json.JSONArray placeIds, long inactivityTime,
			String activeMailSent) {
		List<String> macaddr = convertJSONArrayToList(tagids);
		List<String> sid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusTagIdsSidsAndLastSeenAfterMailSent(cid,tagtype,status,macaddr,sid,inactivityTime,activeMailSent);
	}

	public List<Beacon> findByCidTagTypeStatusTagIdsSpidsAndLastSeenAfterMailSent(String cid, String tagtype,
			String status, net.sf.json.JSONArray tagids, net.sf.json.JSONArray placeIds, long inactivityTime,
			String activeMailSent) {
		List<String> macaddr = convertJSONArrayToList(tagids);
		List<String> spid = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusTagIdsSpidsAndLastSeenAfterMailSent(cid,tagtype,status,macaddr,spid,inactivityTime,activeMailSent);
	}

	public List<Beacon> findByCidTagTypeStatusTagIdsReceiverInfosAndLastSeenAfterMailSent(String cid, String tagtype,
			String status, net.sf.json.JSONArray tagids, net.sf.json.JSONArray placeIds, long inactivityTime,
			String activeMailSent) {
		List<String> macaddr = convertJSONArrayToList(tagids);
		List<String> receiverinfo = convertJSONArrayToList(placeIds);
		return repository.findByCidTagTypeStatusTagIdsReceiverInfosAndLastSeenAfterMailSent(cid,tagtype,status,macaddr,receiverinfo,inactivityTime,activeMailSent);
	}

	public List<Beacon> findByreciverinfoAndStatus(String receiverinfo, String status) {
		return repository.findByreciverinfoAndStatus(receiverinfo,status);
	}

	public List<Beacon> getSavedBeaconByRecieverInfoTagTypeAndStatus(String receiverinfo, String type, String status) {
		return repository.getSavedBeaconByRecieverInfoTagTypeAndStatus(receiverinfo,type,status);
	}

	public List<Beacon> getSavedBeaconBySpidTagTypeAndStatus(String spid, String type, String status) {
		return repository.getSavedBeaconBySpidTagTypeAndStatus(spid,type,status);
	}

	public List<Beacon> getSavedBeaconBySpidNotInTagTypeAndStatus(String spid, List<String> tagtypeList,
			String status) {
		return repository.getSavedBeaconBySpidNotInTagTypeAndStatus(spid,tagtypeList,status);
	}
	
	public List<Beacon> getSavedBeaconBySpidInTagTypeAndStatus(String spid, List<String> tagtypeList,
			String status) {
		return repository.getSavedBeaconBySpidInTagTypeAndStatus(spid,tagtypeList,status);
	}
	
	public List<Beacon> getSavedBeaconByRecieverInfoNotInTagTypeAndStatus(String spid, List<String> tagtypeList,
			String status) {
		return repository.getSavedBeaconByRecieverInfoNotInTagTypeAndStatus(spid,tagtypeList,status);
	}
	
	public List<Beacon> getSavedBeaconByRecieverInfoInTagTypeAndStatus(String spid, List<String> tagtypeList,
			String status) {
		return repository.getSavedBeaconByRecieverInfoInTagTypeAndStatus(spid,tagtypeList,status);
	}

	public List<Beacon> getSavedBeaconBySpidStatusAndAssignedto(String spid, String status, String assingedto) {
		return repository.getSavedBeaconBySpidStatusAndAssignedto(spid,status,assingedto);
	}

	public List<Beacon> findByCidAssingnedtoStatusAndReceiverInfo(String cid, String tagname, String status,
			String receiverInfo) {
		return repository.findByCidAssingnedtoStatusAndReciverinfo(cid,tagname,status,receiverInfo);
	}
	
	public List<Beacon> findByMacaddrs(List<String> macaddr){
		return repository.findByMacaddrs(macaddr);
	}
	
	public List<Beacon> findByMacaddrsMailSentAndLastSeenBefore(List<String> macaddr,String mailsent,long lastseen){
		return repository.findByMacaddrsMailSentAndLastSeenBefore(macaddr,mailsent,lastseen);
	}
	
	private List<String> convertJSONArrayToList(JSONArray array) {
		List<String> list = new ArrayList<String>();
		Iterator<String> iter = array.iterator();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}

	public void entryExit(String op, Map<String, Object> map) {

		try {
			
			String tag_uid 	 = (String)map.get("tag_uid");
			String curRecId  = (String)map.get("uid");
			String timestamp = (String)map.get("timestamp");
			
			Beacon beacon 			  	   = findOneByMacaddr(tag_uid);
			BeaconDevice curReceiver 	   = beaconDeviceService.findOneByUid(curRecId);
			NetworkDevice curNetworkDevice = networkDeviceService.findOneByUuid(curRecId.replaceAll("[^a-zA-Z0-9]", ""));
			
			if (beacon == null || curReceiver == null || curNetworkDevice == null) {
				LOG.info("**** Beacon  ****" + beacon);
				LOG.info("**** beaconDevice  ****" + curReceiver);
				LOG.info("**** NetworkDevice  ****" + curNetworkDevice);
				return;
			}
			
			String status = beacon.getStatus();
			
			if ("checkedin".equalsIgnoreCase(status)) {
				LOG.info("**** CHECKED IN TAG ****" + tag_uid);
			}
			
			boolean postEvent = true;
			boolean enablelog = false;
			
			String cid 		  = beacon.getCid();
			Customer cust 	  = customerService.findById(cid);
			
			if (cust == null) {
				LOG.info("**** Customer null ****" + cid);
				return;
			}
			
			boolean entryexit = cust.getVenueType().equals("Patient-Tracker");
			
			if (!entryexit) {
				LOG.info("**** its not Entry Exit Solution ****" + entryexit);
				return;
			}
			
			if(cust.getLogs() != null && cust.getLogs().equals("true")) {
				enablelog = true;
			}
			
			TimeZone timezone = customerUtils.FetchTimeZone(cust.getTimezone());
			format.setTimeZone(timezone);
			
			Date reportingTime 		 = parse.parse(timestamp);
			String lastReportingTime = format.format(reportingTime);
			long lastseen 		     = reportingTime.getTime();
			
			String curSid 		= curReceiver.getSid();
			String curSpid 		= curReceiver.getSpid();
			String curLocName 	= curReceiver.getAlias() == null ? curRecId : curReceiver.getAlias().toUpperCase();
			String curFloorName = "NA";
			
			String curXPos = curNetworkDevice.getXposition();
			String curYPos = curNetworkDevice.getYposition();
			
			if (curSpid != null && !curSpid.isEmpty()) {
				Portion p = portionService.findById(curSpid);
				if (p != null) {
					curFloorName = p.getUid();
				}
			} else {
				LOG.info("**** Portion null ****" + curSpid);
				return;
			}
			
			String prevSid 		= beacon.getSid();
			String prevSpid 	= beacon.getSpid();
			String prevRecId 	= beacon.getReciverinfo();
			String state 		= beacon.getState();
			String entry_floor 	= beacon.getEntry_floor();
			String entry_loc 	= beacon.getEntry_loc();
			
			String tagtype 		= beacon.getTagType();
			String assignedto 	= beacon.getAssignedTo();
			
			String exitTime 	= beacon.getExitTime();
			
			if (op.equals("entry")) {
				
				String dist =  "0";
				
				if(map.containsKey("distance")){
					dist = map.get("distance").toString();
				}
				
				double distance = Double.valueOf(dist);
				
				/*
				 * int battery_level = (int)map.get("battery_level");
				 * beacon.setBattery_level(battery_level);
				 * 
				 */
				
				beacon.setDistance(distance);

				beacon.setSid(curSid);
				beacon.setSpid(curSpid);
				beacon.setReciverinfo(curRecId);
				
				beacon.setLocation(curFloorName);
				beacon.setReciveralias(curLocName);
				
				beacon.setLastReportingTime(lastReportingTime);
				beacon.setState("active");
				beacon.setMailsent("false");
				
				beacon.setX(curXPos);
				beacon.setY(curYPos);
				
				if (exitTime == null && prevSpid != null && !prevSpid.isEmpty()) {
					customerUtils.logs(enablelog,classname,"Exit data missed, exit from previous location will now be " + lastReportingTime);
				}
				
				beacon.setExitTime(null);
				
				if (!curSid.equals(prevSid) || !curSpid.equals(prevSpid) || state.equals("inactive")) {
					beacon.setEntry_floor(lastReportingTime);
				}
				
				if (!curRecId.equals(prevRecId) || state.equals("inactive")) {
					beacon.setEntry_loc(lastReportingTime);
				} else {
					customerUtils.logs(enablelog,classname,"ENTRY for same receiver id received without exit , Recv Id == " + curRecId);
					return;
				}
				
				if(prevSid == null || prevSpid == null || exitTime != null){
					postEvent = false;
				}
				beacon.setLastSeen(lastseen);
				
			} else if (op.equals("exit")) {
				
				if (curRecId.equals(prevRecId) && !state.equals("inactive")) {
					
					beacon.setState("inactive");
					beacon.setLastReportingTime(lastReportingTime);
					beacon.setLastSeen(lastseen);
					beacon.setExitTime(lastReportingTime);
					
				} else {
					customerUtils.logs(enablelog,classname,"EXIT IS NOT FROM THE CURRENT PLACE");
					customerUtils.logs(enablelog,classname,"Given exit location = "+curRecId+" tag is in location "+prevRecId);
					return;
				}
			}
		
			beacon = save(beacon, false);
			
			if(postEvent){
				
				long locationElapsedTime = 0;
				
				Map<String, Object> postMap = new HashMap<String, Object>();
				
				if (!prevSid.equals(curSid) || !prevSpid.equals(curSpid)) {

					Date entryFloor = format.parse(entry_floor);

					if (exitTime != null) {
						lastReportingTime = exitTime;
						lastseen 		  = format.parse(lastReportingTime).getTime();
					}
					
					long floorElapsedTime = lastseen - entryFloor.getTime();
					floorElapsedTime 	  = TimeUnit.MILLISECONDS.toSeconds(floorElapsedTime);
					
					postMap.put("exit_floor", lastReportingTime);
					postMap.put("elapsed_floor", floorElapsedTime);

				}
				
				Date entryloc 		= format.parse(entry_loc);
				
				if(map.containsKey("timeElapsed")){
					locationElapsedTime = Long.valueOf(map.get("timeElapsed").toString());
				} else {
					locationElapsedTime = lastseen - entryloc.getTime();
					locationElapsedTime = TimeUnit.MILLISECONDS.toSeconds(locationElapsedTime);
				}

				postMap.put("opcode", 	  	 "reports");
				postMap.put("tagid",      	 tag_uid);
				postMap.put("tagtype",	   	 tagtype);
				postMap.put("assingedto", 	 assignedto);
				postMap.put("cid", 		  	 cid);
				postMap.put("sid", 		  	 prevSid);
				postMap.put("spid", 	  	 prevSpid);
				postMap.put("location",   	 prevRecId);
				postMap.put("entry_loc",  	 entry_loc);
				postMap.put("entry_floor",	 entry_floor);
				postMap.put("exit_loc",   	 lastReportingTime);
				postMap.put("elapsed_loc",   locationElapsedTime);
				
				
				postReportEvent(postMap);
				customerUtils.logs(enablelog,classname,"postmap "+postMap);
			}
		} catch (Exception e) {
			LOG.info("Error While Updadating Entry Exit Tag  information, Opcode :  " + op);
			e.printStackTrace();
		}
	}
	public double getDiff(Date recordSeen, Date recordSent) {
		long diff = recordSeen.getTime() - recordSent.getTime();
		//return TimeUnit.MILLISECONDS.toSeconds(diff);
		return Double.valueOf(diff);
	}
}
