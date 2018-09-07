
package com.semaifour.facesix.device.data;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.mqtt.DeviceEventPublisher;

@Service
public class DeviceItemService {
	
	Logger LOG = LoggerFactory.getLogger(DeviceItemService.class.getName());

	public static String mqttMsgTemplate = " \"opcode\":\"{0}\", \"uid\":\"{1}\", \"by\":\"{2}\", \"newversion\":\"{3}\", \"mac\":{4} ";

	@Autowired
	private DeviceItemRepository repository;
	
	@Autowired
	private DeviceEventPublisher mqttPublisher;
	
	
	public DeviceItemService() {
	}
	
	public Page<DeviceItem> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<DeviceItem> findByMac(String mac) {
		return repository.findByName(mac);
	}
	
	public List<DeviceItem> findByUid(String uid) {
		return repository.findByUid(uid);
	}

	public List<DeviceItem> findByType(String type) {
		return repository.findByTypefs(type);
	}

	public DeviceItem findOneByMac(String mac) {
		List<DeviceItem> list = findByMac(mac);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public DeviceItem findOneByUid(String uid) {
		List<DeviceItem> list = findByUid(uid);
		if (list != null & list.size() > 0 ) {
			DeviceItem bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public DeviceItem findById(String id) {
		return repository.findOne(id);
	}
	
	public boolean exists(String id) {
		return repository.exists(id);
	}
	
	public boolean exists(String uid, String mac) {
		if (findOneByUid(uid) != null) return true;
		if (findOneByMac(mac) != null) return true;
		return false;
	}
	
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void delete(String id) {
		repository.delete(id);
	}

	public void deleteByMac(String mac) {
		DeviceItem item = this.findOneByMac(mac);
		repository.delete(item);
	}

	public void delete(DeviceItem deviceItem) {
		repository.delete(deviceItem);
	}
	
	public long count() {
		return repository.count();
	}
	
	/**
	 * Save device and notify 
	 * 
	 * @param device
	 * @return
	 */
	public DeviceItem save(DeviceItem deviceItem) {
		return save(deviceItem, true);
	}
	
	/**
	 * 
	 * Save device and notify=true or false
	 * 
	 * @param deviceItem
	 * @param notify
	 * @return
	 */
	public DeviceItem save(DeviceItem deviceItem, boolean notify) {
		deviceItem = repository.save(deviceItem);
		if (deviceItem.getPkid()== null) {
			deviceItem.setPkid(deviceItem.getId());
			deviceItem = repository.save(deviceItem);
		}
		LOG.info("Device saved successfully :" + deviceItem.getId());
		if (notify) {
			try {
				String message = MessageFormat.format(mqttMsgTemplate,new Object[]{"UPDATE", 
																		   deviceItem.getUid(),
																		   deviceItem.getModifiedBy(),
																		   "1",
																		   deviceItem.getName()});
				mqttPublisher.publish("{" + message + "}", deviceItem.getUid());
			} catch (Exception e) {
				LOG.warn("Failed to notify update", e);
	
			}
		}
		return deviceItem;
	}
	
	public Iterable<DeviceItem> findAll() {
		return repository.findAll();
	}

}
