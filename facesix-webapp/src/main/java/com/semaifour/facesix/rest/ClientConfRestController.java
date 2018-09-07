package com.semaifour.facesix.rest;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.device.ClientDevice;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

/**
 * 
 * Rest Device Controller handles all rest calls for network configuration
 * 
 * @author mjs
 *
 */
@RestController
@RequestMapping("/rest/site/portion/clientdevice")
public class ClientConfRestController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(ClientConfRestController.class.getName());

	ClientDeviceService _clientDeviceService;
	
	DeviceService _deviceService;

	@Autowired
	NetworkDeviceService 	networkDeviceService;
	
	@Autowired
	private DeviceEventPublisher mqttPublisher;
	
	@RequestMapping(value = "/byuid", method = RequestMethod.GET)
	public List<ClientDevice> getuid(@RequestParam("uid") String uid) {
		String uid_s = uid.replaceAll("[^a-zA-Z0-9]", "");
		List<ClientDevice> list = getClientDeviceService().findByUid(uid_s);
		return list;
	}
	
	
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public Iterable<ClientDevice> query(@RequestParam("uid") String uid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		String uid_s = uid.replaceAll("[^a-zA-Z0-9]", "");
		Iterable<ClientDevice> list = getClientDeviceService().findByQuery(uid_s, type, status, sort, page, size);
		return list;
	}


	@RequestMapping(value = "/getid", method = RequestMethod.GET)
	public ClientDevice getid(@RequestParam("id") String id) {
		ClientDevice nd = getClientDeviceService().findById(id);
		return nd;
	}
	
	/**
	 * Delete ClientDevice by its 'id'.
	 * 
	 * @param id
	 * @return
	 */
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(@RequestParam("uid") String uid) throws Exception {	

		String uid_s = uid.replaceAll("[^a-zA-Z0-9]", "");
		ClientDevice device = getClientDeviceService().findOneByUid(uid_s);
		if (device != null) {
			getClientDeviceService().delete (device);
		}
		
	}

	@RequestMapping(value = "/delall", method = RequestMethod.GET)
	public void delall() {
		getClientDeviceService().deleteAll();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/clientsDetails", method = RequestMethod.GET)
    public JSONObject getpeers(@RequestParam(value="sid", 	 	required=false) String sid, 
    						   @RequestParam(value="spid", 	 	required=false) String spid,
    						   @RequestParam(value="cid", 	 	required=false) String cid,
    						   @RequestParam(value="uid", 	 	required=false) String uid
    						   ) throws IOException {

    	
    	JSONArray client 		= new JSONArray();
    	JSONObject devlist 	 	=null;
    	JSONObject details =  new JSONObject();

		List<ClientDevice> clientDevices = null;

		HashMap<String, Integer> dupsmap = new HashMap<String, Integer>();
		
    	try {
    		
 	
    		final String status = "active";
    		
			if (uid != null) {
				String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
				clientDevices = getClientDeviceService().findByUuidAndStatus(uuid, status);
			} else if (spid != null) {
				clientDevices = getClientDeviceService().findBySpidAndStatus(spid, status);
			} else if (sid != null) {
				clientDevices = getClientDeviceService().findBySidAndStatus(sid, status);
			} else {
				clientDevices = getClientDeviceService().findByCidAndStatus(cid, status);
			}

			HashMap<String, String> map = new HashMap<String, String> ();
			
    		if (clientDevices !=null) {
    			for (ClientDevice clientDev : clientDevices) {

    				devlist =  new JSONObject();
    				
    				boolean _11r = clientDev.is_11r();
    				boolean _11k = clientDev.is_11k();
    				boolean _11v = clientDev.is_11v();
    				
    				String client_mac   = clientDev.getMac();
    				String ap 		    = clientDev.getUid();
    				String devtype   	= clientDev.getTypefs() == null ? "Others" : clientDev.getTypefs();
    				String radioType	= clientDev.getRadio_type()== null ? "2.4Ghz" : clientDev.getRadio_type();
    				String ssid      	= clientDev.getSsid() == null ? "UNKOWN" : clientDev.getSsid() ;
					String rssi         = clientDev.getPeer_rssi() == null ? "0"  : clientDev.getPeer_rssi();
   
					if (dupsmap.containsKey(client_mac)) {
						dupsmap.put(client_mac, dupsmap.get(client_mac) + 1);
						continue;
					} else {
		
        				devlist.put("mac_address",  client_mac);
        				devlist.put("rssi", 		rssi);
        				devlist.put("tx", 			clientDev.getCur_peer_tx_bytes());
        				devlist.put("rx", 			clientDev.getCur_peer_rx_bytes());
        				devlist.put("k11", 			_11k);
        				devlist.put("r11", 			_11r);
        				devlist.put("v11", 			_11v);
        				devlist.put("client_type",  devtype);
        				devlist.put("devtype",      clientDev.getDevname());
        				devlist.put("ssid", 		ssid);
        				devlist.put("radio", 		radioType);
        				devlist.put("uid", 			ap);
        				devlist.put("conn_time_sec",     clientDev.getPeer_conn_time());
        				
    					String uuid = ap.replaceAll("[^a-zA-Z0-9]", "");
    					
    					String x = "350";
    					String y = "430";
    					
    					if (!map.containsValue(uuid)) {
    						NetworkDevice nd = networkDeviceService.findOneByUuid(uuid);
    						if (nd != null) {
    							x = nd.getXposition();
    							y = nd.getYposition();
    						}
    						map.put("uid", uuid);
    						map.put("x", x);
    						map.put("y", y);

    					} else {
    						x = map.get("x");
    						y = map.get("y");
    					}

    					devlist.put("x", x);
    					devlist.put("y", y);
        				
						client.add(devlist);

						dupsmap.put(client_mac, 0);
					}

				}
			}

    	} catch (Exception e) {
    		LOG.error("while clientDetail processing error - > "+e);
    		e.printStackTrace();
    	}
		
    	//LOG.info("clientsDetails" +client);
    	
    	details.put("clientsDetails", client);		
    	
		return details;		
    }
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ClientDevice save(@RequestBody String newfso, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				JSONObject json 	= JSONObject.fromObject(newfso);
				
				String mac 		= (String) json.get("uid");
				String status 	= "blocked";
				String pid		= (String) json.get("pid");

				String ACLMQTTMsgTemplate 	= " \"opcode\":\"{0}\", \"uid\":\"{1}\", \"ssid\":\"{2}\", \"peer_mac\":\"{3}\" ";
				String	peer_mac 			= mac.replaceAll("[^a-zA-Z0-9]", "");
				
				ClientDevice qubeClient = getClientDeviceService().findOneByPeermac(peer_mac);

				if (qubeClient != null) {

					qubeClient.setPid("uid"); // policy
					qubeClient.setStatus(status);
					qubeClient.setModifiedOn(new Date());
					qubeClient.setCreatedOn(new Date());
					qubeClient.setCreatedBy(SessionUtil.currentUser(request.getSession()));
					qubeClient.setModifiedOn(new Date());
					qubeClient.setModifiedBy(qubeClient.getCreatedBy());
					qubeClient = getClientDeviceService().save(qubeClient);

					String ssid = qubeClient.getSsid();
				
				if (qubeClient != null) {
					String message = MessageFormat.format(ACLMQTTMsgTemplate, new Object[] { "BLOCK",pid.toLowerCase(), ssid,qubeClient.getMac()});
					mqttPublisher.publish("{" + message + "}", pid.toLowerCase());
					LOG.info("ACL MQTT MESSAGE" +message);
					}
				}
			}
		} catch (Exception e) {
			LOG.info("While BLK Client Save Error ," + e);
		}

		return null;
	}

	private ClientDeviceService getClientDeviceService() {
		if (_clientDeviceService == null) {
			_clientDeviceService = Application.context.getBean(ClientDeviceService.class);
		}
		return _clientDeviceService;
	}	
	
	private DeviceService getDeviceService() {
		if (_deviceService == null) {
			_deviceService = Application.context.getBean(DeviceService.class);
		}
		return _deviceService;
	}	
		
}

