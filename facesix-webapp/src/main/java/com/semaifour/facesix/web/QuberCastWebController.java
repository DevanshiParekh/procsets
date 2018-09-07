package com.semaifour.facesix.web;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.itextpdf.text.Font;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.qubercast.QuberCast;
import com.semaifour.facesix.data.qubercast.QuberCastService;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import com.semaifour.facesix.rest.FSqlRestController;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.SessionUtil;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/web/qcast")
public class QuberCastWebController extends WebController {

	
	static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	static Logger LOG = LoggerFactory.getLogger(QuberCastWebController.class.getName());

	@Autowired
	CCC _CCCC;

	@Autowired
	QuberCastService qubercastService;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	NetworkDeviceService networkDeviceService;

	@Autowired
	SiteService siteService;

	@Autowired
	PortionService portionService;

	@Autowired
	FSqlRestController fsqlRestController;

	@Autowired
	ClientDeviceService clientDeviceService;

	@Autowired
	DeviceService devService;
	
	@Autowired
	private DeviceEventPublisher mqttPublisher;
	
	@Autowired
	DeviceService deviceManager;
	
	@Autowired
	DeviceEventPublisher deviceEventMqttPub;
	
	String mqttMsgTemplate = " \"opcode\":\"{0}\", \"uid\":\"{1}\", \"by\":\"{2}\", \"newversion\":\"{3}\", \"value\":{4} ";

	static private String reffId="a5a5";
	static private String spid_profile = "spid";
	
	@RequestMapping(value = "profile", method = RequestMethod.POST)
	public String profile(Map<String, Object> model, @ModelAttribute QuberCast quber, HttpServletRequest request, HttpServletResponse response)  {

		JSONObject jsonObject = new JSONObject();
		boolean bIsUpdate = true;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			QuberCast quberCast = qubercastService.findByReffId("a5a5");
			if (quberCast == null) {
				quberCast = new QuberCast();
				bIsUpdate = false;

			}
			
			quberCast.setReffId(reffId);
			quberCast.setMediaPath(quber.getMediaPath());
			quberCast.setMulticastPort(quber.getMulticastPort());
			quberCast.setMulicastAddress(quber.getMulicastAddress());
			quberCast.setLogFile(quber.getLogFile());  // totalFile
			quberCast.setLogLevel(quber.getLogLevel()); //payLoad

			if (bIsUpdate) {
				qubercastService.save(quberCast);
				LOG.info("if" +quberCast);
			} else {
				LOG.info("else" +quberCast);			
				quberCast.update (quberCast);
				quberCast.setModifiedOn(new Date(System.currentTimeMillis()));	
				quberCast.setModifiedBy("cloud");
				qubercastService.save(quberCast);
			}
			
			LOG.info("quberCast save method >> " +quberCast);
			
			model.put("quberCast", quberCast);
			model.put("qcastId", quberCast.getUid());
						
			jsonObject.put("mediaPath", 		quber.getMediaPath());
			jsonObject.put("multicastPort", 	quber.getMulticastPort());
			jsonObject.put("mulicastAddress", 	quber.getMulicastAddress());
			jsonObject.put("totalFiles", 		quber.getLogFile());
			jsonObject.put("payLoad", 			quber.getLogLevel());

			Iterable<Device> devices = getDeviceService().findAll();
			
			if (devices != null) {
				for (Device device : devices) {
					String message = MessageFormat.format(mqttMsgTemplate, new Object[] { "QCAST_START", device.getUid().toLowerCase(),
										"qubercloud", "0xFE", jsonObject.toString() });
					
					mqttPublisher.publish("{" + message + "}", device.getUid().toLowerCase());
				}

			}
			
			prepare(model, request, response);
			
			try {
				String str 	= "/facesix/web/qcast/profile?qcastId=a5a5&spid="+spid_profile;
				str 		= str + "&uid=";
				
				response.sendRedirect(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			return _CCC.pages.getPage("facesix.qcast", "qubercast");
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}
	
	@RequestMapping(value="profile", method = RequestMethod.GET)
	public String qubercast(Map<String, Object> model, @RequestParam( value="qcastId", required=false) String qcastId,
													   @RequestParam( value="spid", required=false) String spid, HttpServletRequest request, HttpServletResponse response) {	

		if (SessionUtil.isAuthorized(request.getSession())) {
			QuberCast qupercast = qubercastService.findByReffId("a5a5");
			if (qupercast == null) {
				qupercast = new QuberCast();
				qupercast.setUid(SessionUtil.currentUser(request.getSession()));
				
			}
			
			model.put("quberCast", 	qupercast);
			model.put("qcastId", 	qcastId);
			model.put("spid", 		spid);
			
			spid_profile = spid;
			
			prepare(model, request, response);
			
			
			
			LOG.info("getquberCast" + qupercast );	
			LOG.info("reffId " +reffId);
			
			return _CCC.pages.getPage("facesix.qcast", "qupercast");
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}

	private DeviceService getDeviceService() {
		if (devService == null) {
			devService = Application.context.getBean(DeviceService.class);
		}
		return devService;
	}
	
}