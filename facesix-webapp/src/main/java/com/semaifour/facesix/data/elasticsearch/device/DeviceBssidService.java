package com.semaifour.facesix.data.elasticsearch.device;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DeviceBssidService {

	private static String classname = DeviceBssidService.class.getName();

	Logger LOG = LoggerFactory.getLogger(classname);

	@Autowired(required = false)
	private DeviceBssidRepository repository;

	public DeviceBssidService() {
		LOG.info("service created");
	}

	
	public DeviceBssid findByBssid(String bssid) {
		return repository.findByBssid(bssid);
	}

	public List<DeviceBssid> findByUid(String uid) {
		return repository.findByUid(uid);
	}

	public DeviceBssid findOneByUid(String uid) {
		
		List<DeviceBssid> devicebssid = findByUid(uid);
		
		if (devicebssid != null && devicebssid.size() > 0) {
			DeviceBssid devBssid = devicebssid.get(0);
			String bss_uid       = devBssid.getUid();
			if (bss_uid.equalsIgnoreCase(uid))
				return devBssid;
			else
				return null;
		}
		return null;
	}

	public List<DeviceBssid> findByCid(String cid) {
		return repository.findByCid(cid);
	}
	
	
	public void delete(DeviceBssid beacon) {
		repository.delete(beacon);
	}
	
	 public void deleteAll() {
		repository.deleteAll();
	}

	public void delete(List<DeviceBssid> beacon) {
		repository.delete(beacon);
	}

	public DeviceBssid save(DeviceBssid bssidmapping) {
		return repository.save(bssidmapping);

	}

}
