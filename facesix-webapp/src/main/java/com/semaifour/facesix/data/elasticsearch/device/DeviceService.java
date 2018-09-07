
package com.semaifour.facesix.data.elasticsearch.device;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import com.semaifour.facesix.account.rest.CaptivePortalRestController;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import com.semaifour.facesix.util.SimpleCache;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class DeviceService {
	
	static Logger LOG = LoggerFactory.getLogger(DeviceService.class.getName());

	@Autowired
	private DeviceRepository repository;
	
	@Autowired
	private DeviceEventPublisher mqttPublisher;
	
	@Autowired
	ClientDeviceService  clientDeviceService;
	
	@Autowired
	CaptivePortalRestController captivePortalRestController;
	
	@Autowired
	DeviceBssidService deviceBssidService;
	
	@Autowired
	DeviceBssidRepository deviceBssidRepository;
	
	String mqttMsgTemplate = " \"opcode\":\"{0}\", \"uid\":\"{1}\", \"by\":\"{2}\", \"newversion\":\"{3}\", \"value\":{4}, \"name\":\"{5}\" ";

	@Autowired
	SimpleCache<HeartBeat> deviceHealthCache;
	
	public DeviceService() {
	}
	
	public Page<Device> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<Device> findByName(String name) {
		return repository.findByName(QueryParser.escape(name));
	}
	
	public List<Device> findByUid(String uid) {
		return repository.findByUid(QueryParser.escape(uid));
	}
	
	public Device findOneByName(String name) {
		List<Device> list = findByName(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public Device findOneByUid(String uid) {
		List<Device> list = findByUid(uid);
		if (list != null & list.size() > 0 ) {
			Device bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public Device findByUidAndCid(String uid,String cid) {
		List<Device> device  = repository.findByUidAndCid(QueryParser.escape(uid),cid);
		if (device != null && device.size() > 0) {
			Device dev = device.get(0);
			if (dev.getUid().equalsIgnoreCase(uid)) {
				return dev;
			}
		} 
		return null;
	}
	
	public Device findById(String id) {
		return repository.findOne(id);
	}
	
	public boolean exists(String id) {
		return repository.exists(id);
	}
	
	public boolean exists(String uid, String name) {
		if (findOneByUid(uid) != null) return true;
		if (findOneByName(name) != null) return true;
		return false;
	}
	
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void delete(String id) {
		Device device = repository.findOne(id);
		repository.delete(id);
		if (device != null) {
			notify(device, "DELETE");
		}
	}
	
	public void delete(Device device) {
		
		String uid = device.getUid();
		repository.delete(device);
		
		List<DeviceBssid> devicebssid = deviceBssidRepository.findByUid(uid);
		if (devicebssid != null) {
			devicebssid.forEach(dev -> {
				deviceBssidRepository.delete(dev.getId());
			});
		}
		notify(device, "DELETE");
	}
	
	public long count() {
		return repository.count();
	}
	
	
	public List<Device> findBySid(String sid){
		return repository.findBySid(sid);
	}
	
	public List<Device> findBySpid(String spid){
		return repository.findBySpid(spid);
	}
	
	public List<Device> findByCid(String cid){
		return repository.findByCid(cid);
	}
	
	/**
	 * Save device and notify 
	 * 
	 * @param device
	 * @return
	 */
	public Device save(Device device) {
		return save(device, true);
	}
	
	/**
	 * 
	 * Save device and notify=true or false
	 * 
	 * @param device
	 * @param notify
	 * @return
	 */
	public Device save(Device device, boolean notify) {
		device = repository.save(device);
		//LOG.info("Device saved successfully :" + device.getId());
		if (device.getPkid()== null) {
			device.setPkid(device.getId());
			device = repository.save(device);
		}
		if (notify) {
			notify(device, "UPDATE");
		}
		return device;
	}
	
	public Device reset(Device device, boolean notify) {
		if (notify) {
			notify(device, "RESET");		
		}
		LOG.info("Device reset successfully :" + device.getId());
		return device;
	}	

	public Iterable<Device> findAll() {
		return repository.findAll();
	}
	
	
	public Iterable<Device> findByQuery(String query, String sort, int page, int size) {
		QueryBuilder qb = QueryBuilders.queryStringQuery(query);
    	
		SearchQuery sq = new NativeSearchQueryBuilder()
	    .withQuery(qb)
	    .withPageable(new PageRequest(page,size))
	    .build();
		
		Iterable<Device> devices = repository.search(sq);
		
		for(Device dv : devices) {
			HeartBeat hb = getDeviceHealth(dv.getUid());
			if (hb != null) {
				if (hb.state().contains("Unknown")) {
					dv.setState ("inactive");
				} else {
					dv.setState ("idle");
				}

//				dv.setOthers(String.valueOf(new Date(hb.getTimestamp())));
			}
		}
		return devices;
	}

	public List<Device> findByStatus(String status) {
		return repository.findByStatus(status);
	}

	public void updateDeviceHealth(Map<String, Object> map) {
		HeartBeat beat = new HeartBeat(map);
		this.deviceHealthCache.putForGood(beat.getUid(), beat);
	}
	
	public Collection<HeartBeat> getAllDeviceHealth() {
		return this.deviceHealthCache.values();
	}
	
	public HeartBeat getDeviceHealth(String uid) {
		return this.deviceHealthCache.get(uid);
	}
	
	public void clearDeviceHealthCache() {
		this.deviceHealthCache.clear();
	}
		
	public HeartBeat clearDeviceHealthCache(String uid) {
		return this.deviceHealthCache.clear(uid);
	}
	
	/**
	 * 
	 * Sends a notification message to the device with given opcode
	 * 
	 * 	<pre>
	 *  { 
	 *    "opcode":"UPDATE|DELETE|RESET", "uid":"xxxxxx", "by":"modified_by", 
	 *    "newversion":"new version numer", "value":{..config..}, 
	 *    "name":"name of device"
	 *  }
     * </pre>
	 * @param device
	 * @param opcode
	 * @return
	 */
	public boolean notify(Device device, String opcode) {
		try {
			String message = MessageFormat.format(mqttMsgTemplate,new Object[]{opcode, 
																	   device.getUid().toLowerCase(),
																	   device.getModifiedBy(),
																	   "1",
																	   device.getConf(),
																	   device.getName()});
			mqttPublisher.publish("{" + message + "}", device.getUid().toLowerCase());
			LOG.debug("Device Config MQTT Data" + message);
			return true;
		} catch (Exception e) {
			LOG.warn("Failed to notify update", e);
			return false;
		}
	}
		
	public Device saveAndSendMqtt(Device device, boolean notify) {
		device = repository.save(device);
		//LOG.info(" Device Saved " + device.getId());
		
		if (device.getPkid()== null) {
			device.setPkid(device.getId());
			device = repository.save(device);
		}
		
		if (notify) {
			mqttNotify(device, "UPDATE");
		}
		return device;
	}

	
	private boolean mqttNotify(Device device, String opcode) {
		try {
			
			String conf =  device.getConf();
			JSONObject template	= JSONObject.fromObject(conf);
			
			String cid 			= device.getCid();
			String sid 			= device.getSid();
			String spid 		= device.getSpid();
			
			template.put("cid", cid);
			template.put("sid", sid);
			template.put("spid", spid);
			
			String steering = device.getSteering() == null ? "false" : device.getSteering();
			template.put("steering", steering);
			
			String loadbalance = device.getLoadBalance() == null ? "false" : device.getLoadBalance();
			template.put("loadbalance", loadbalance);

			String keepalive = device.getKeepAliveInterval() == null ? "30" : device.getKeepAliveInterval();
			template.put("keepalive", keepalive);
			
			String  root = device.getRoot() == null ? "no" : device.getRoot();
			template.put("root", root);

			String  workingMode = device.getWorkingMode() == null ? "normalmode" : device.getWorkingMode();
			template.put("workingmode", workingMode);
			
			/*
			 * Steer Band balancing
			 * 
			 */
			
			if (steering.equals("true")) {
				
				if (device.getBssStationRejectThresh() != null) {
					template.put("bss_station_reject_thresh", device.getBssStationRejectThresh());
				}if (device.getBssChanPbusyPercent() != null) {
					template.put("bss_chan_busy_percent", device.getBssChanPbusyPercent());
				}if (device.getBssRejectTimeout() != null) {
					template.put("bss_reject_timeout", device.getBssRejectTimeout());
				}if (device.getBand2GStaRatio() != null) {
					template.put("band_2g_sta_ratio", device.getBand2GStaRatio());
				}if (device.getBand5GStaRatio() != null) {
					template.put("band_5g_sta_ratio", device.getBand5GStaRatio());
				}if (device.getBandRcpiDiff() != null) {
					template.put("band_rcpi_diff", device.getBandRcpiDiff());
				}
				
			}
			
			
			/*
			 * Load balancing
			 * 
			 */
		
			if (loadbalance.equals("true")) {
				if (device.getMinStaCount() != null) {
					template.put("min_sta_count", device.getMinStaCount());
				}if (device.getRssiThreshold() != null) {
					template.put("rssi_threshold", device.getRssiThreshold());
				}if (device.getRcpiRange() != null) {
					template.put("rcpi_range", device.getRcpiRange());
				}if (device.getAvgStaCntLb() != null) {
					template.put("avg_sta_cnt_lb", device.getAvgStaCntLb());
				}
			}
			
			JSONArray hotspot = captivePortalRestController.getHotspotLink(cid, sid, spid);
			if (hotspot != null && hotspot.size() > 0) {
				template.put("hotspot", hotspot);
			}
			
			JSONObject ethernet_setting = new JSONObject();
			JSONObject lan_setting 		= new JSONObject();

			String lan_flag = device.getLan_Flag() == null ? "NA" : device.getLan_Flag();

			if (lan_flag.equals("lan_static")) {
				lan_setting.put("proto", "static");
				lan_setting.put("ipv4_addr", device.getLan_ipv4_Addr());
				lan_setting.put("ipv4_mask", device.getLan_ipv4_Mask());
			} else if (lan_flag.equals("lan_dhcp")) {
				lan_setting.put("proto", "dhcp");
			}

			JSONArray ethernet_setting_array = new JSONArray();
			ethernet_setting_array.add(lan_setting);
			
			ethernet_setting.put("lan", 	 ethernet_setting_array);
			
			template.put("network_setting", ethernet_setting);
			
			String mqttTemplate = " \"opcode\":\"{0}\", \"uid\":\"{1}\", \"by\":\"{2}\", \"newversion\":\"{3}\", \"value\":{4}";
			String message = MessageFormat.format(mqttTemplate,new Object[]{opcode,
																	    device.getUid().toLowerCase(),
																	    device.getModifiedBy(),
																	    "1",
																	    template.toString()});
			mqttPublisher.publish("{" + message + "}", device.getUid().toLowerCase());
			LOG.info("mqqt message " +message);
			LOG.debug("Device Config New MQTT Data" + message);
			return true;
		} catch (Exception e) {
			LOG.warn("Failed to notify update", e);
			return false;
		}
	}

	public void universalMQTT(String conf,String id,String nameFlag, boolean notify) {
		//LOG.info("universalMQTT>>>>>>>>> " +notify);
		if (notify) {
			universalMQTT(conf, id,nameFlag,"UPDATE");
		}
	}

	private boolean universalMQTT(String tempalte, String id,String nameFlag,String opcode) {
		try {
			
			JSONObject conf	= JSONObject.fromObject(tempalte);
			conf.put(nameFlag, id.toLowerCase());
			
			String mqttTemplate = " \"opcode\":\"{0}\", \"by\":\"{1}\", \"newversion\":\"{2}\", \"value\":{3}";
			String message = MessageFormat.format(mqttTemplate,new Object[]{opcode, "cloud","1", conf.toString()});
			mqttPublisher.publish("{" + message + "}", id.toLowerCase());
			
			LOG.info("UNIVERSAL CONFIG MQTT " +message);
			return true;
		} catch (Exception e) {
			LOG.warn("Universal MQTT Failed to notify ", e);
			return false;
		}
		
	}

	public Iterable<Device> findByCidAndState(String cid, String state) {
		return repository.findByCidAndState(cid,state);
	}
	public List<Device> findBySidAndState(String sid, String state) {
		return repository.findBySidAndState(sid,state);
	}
	public List<Device> findBySpidAndState(String spid, String state) {
		return repository.findBySpidAndState(spid,state);
	}
	
	public List<Device> findByCidAndAlias(String cid, String alias) {
		List<Device> deviceList = new ArrayList<Device>();
		List<Device> devices = repository.findByCidAndAlias(cid, QueryParser.escape(alias));
		if (devices != null && devices.size() > 0) {
			for (Device device : devices) {
				String devName = device.getAlias().trim();
				if (devName.equalsIgnoreCase(alias)) {
					deviceList.add(device);
				}
			}
			return deviceList;
		}
		return devices;
	}
	
	public void save(List<Device> device) {
		repository.save(device);
	}
	

}
