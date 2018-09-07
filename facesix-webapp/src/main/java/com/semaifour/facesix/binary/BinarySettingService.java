package com.semaifour.facesix.binary;

import java.text.MessageFormat;
import java.util.List;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import com.semaifour.facesix.spring.CCC;

@Service
public class BinarySettingService {
	static Logger LOG = LoggerFactory.getLogger(BinarySetting.class.getName());

	@Autowired
	private BinarySettingRepository repository;

	@Autowired
	private DeviceEventPublisher mqttPublisher;
	
	@Autowired
	CCC _CCC;
	
	public BinarySettingService() {
	}

	public List<BinarySetting> findOneById(String id) {
		return repository.findOneById(QueryParser.escape(id));// null
	}

	public BinarySetting findByCid(String cid) {
		return repository.findByCid(cid);
	}

	public List<BinarySetting> findBySid(String sid) {
		return repository.findBySid(sid);
	}

	public List<BinarySetting> findBySpid(String spid) {
		return repository.findBySpid(spid);
	}

	public List<BinarySetting> findByUid(String uid) {
		return repository.findByUid(QueryParser.escape(uid));
	}
	

	public BinarySetting findOneByUid(String uid) {
		List<BinarySetting> list = findByUid(uid);
		
		if (list != null & list.size() > 0 ) {
			BinarySetting bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		
		return null;
	}
	
	public BinarySetting findById(String id) {
		return repository.findById(id);
	}

	public Iterable<BinarySetting> findAll() {
		return repository.findAll();
	}

	public BinarySetting save(BinarySetting newfso, boolean notify) {
		newfso = repository.save(newfso);
		if (newfso.getPkid()== null) {
			newfso.setPkid(newfso.getId());
			newfso = repository.save(newfso);
		}
		LOG.info("Binary Settings Saved successfully :" + newfso.getId());
		return newfso;
	}
	
	@SuppressWarnings("unchecked")
	public BinarySetting BINARY_BOOT(BinarySetting newfso, boolean notify, String id) {
		newfso = repository.save(newfso);
		
		JSONObject json = new JSONObject();
		String cloudName= _CCC.properties.getProperty("facesix.cloud.name", "cloud.qubercomm.com");
		
		json.put("filename", 	newfso.getMqttfilePath());
		json.put("filepath", 	cloudName);
		json.put("md5sum", 		newfso.getMd5Checksum());
		if (notify) {
			notify(newfso.getUpgradeType(),json.toString(), id);
		}
		
		return newfso;
	}

	private boolean notify(String opcode, String json, String id) {
		try {
			String mqttMsgTemplate = " \"opcode\":\"{0}\",\"by\":\"{1}\", \"type\":\"{2}\", \"value\":{3} ";
			String message = MessageFormat.format(mqttMsgTemplate,new Object[] { opcode, "qubercloud", "DFU", json });
			//mqttPublisher.publish("{" + message + "}", "40:a5:ef:85:4d:a8");
			mqttPublisher.publish("{" + message + "}", id);
			LOG.info("BINARY BOOT MQTT MESSAGE " + message +" " + id);
			return true;
		} catch (Exception e) {
			LOG.warn("Failed to notify update", e);
			return false;
		}
	}

	public void delete(String id) {
		repository.delete(id);
	}

	public void delete() {
		repository.deleteAll();
	}

	
}
