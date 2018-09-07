package com.semaifour.facesix.data.captive.portal;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import net.sf.json.JSONArray;

@Service
public class PortalUsersService {

	Logger LOG = LoggerFactory.getLogger(PortalUsersService.class.getName());

	@Autowired(required = false)
	private PortalUsersRepository repository;

	@Autowired
	DeviceEventPublisher deviceEventMqttPub;
	
	@Autowired
	CaptivePortalService captivePortalService;
	
	String mqttMsgTemplate = "\"opcode\":\"{0}\",\"peer_mac\":\"{1}\"";

	
	public PortalUsersService() {
	}

	public List<PortalUsers> findByUid(String uid) {
		return repository.findByUid(uid);
	}

	public PortalUsers findById(String id) {
		return repository.findOne(QueryParser.escape(id));
	}

	public boolean exists(String id) {
		return repository.exists(QueryParser.escape(id));
	}

	public void deleteAll() {
		repository.deleteAll();
	}

	public void delete(String id) {
		repository.delete(id);
	}

	public void delete(PortalUsers captivePortal) {
		repository.delete(captivePortal);
	}

	public long count() {
		return repository.count();
	}

	
	public PortalUsers save(PortalUsers captivePortal) {
		return save(captivePortal, true);
	}

	public PortalUsers save(PortalUsers portalusers, boolean notify) {
		portalusers = repository.save(portalusers);
		if (portalusers.getPkid()== null) {
			portalusers.setPkid(portalusers.getId());
			portalusers = repository.save(portalusers);
		}
		
		if (notify) {
			notify(portalusers);
		}
		
		return portalusers;
	}

	public boolean notify(PortalUsers portalusers) {
		
		String peer_mac = portalusers.getUid();
		
		if (peer_mac !=null) {
			
			String portalId      = portalusers.getPortalId();
			final String opcode  = "HS_ALLOW_STA";
			
			String message 		 = MessageFormat.format(mqttMsgTemplate, new Object[] { opcode, peer_mac });
			CaptivePortal portal = captivePortalService.findById(portalId);
			
			if (portal != null) {
				JSONArray assocId = portal.getAssociationIds();
				if (assocId != null) {
					Iterator<String> it = assocId.iterator();
					while (it.hasNext()) {
						String id = it.next();
						deviceEventMqttPub.publish("{" + message + "}", id);
					}
					return true;
				}
			}		
		}
	
		return false;
	}

	public Iterable<PortalUsers> findAll() {
		return repository.findAll();
	}

	public Iterable<PortalUsers> findOneById(String id) {
		return repository.findOneById(id);
	}

	public List<PortalUsers> findByCid(String id) {
		return repository.findByCid(id);
	}

	public PortalUsers findByPhone(String phone) {
		return repository.findByPhone(phone);
	}

	public PortalUsers findOneByUid(String mac) {
		List<PortalUsers> portal = findByUid(mac);
		if (portal != null && portal.size() > 0) {
			return portal.get(0);
		}
		return null;
	}

}
