package com.semaifour.facesix.beacon.web;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.spring.SpringComponentUtils;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.DeviceHelper;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.SitePortionWebController;
import com.semaifour.facesix.web.WebController;


@Controller
@RequestMapping("/web/finder/device")
public class BeaconDeviceWebController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(BeaconDeviceWebController.class.getName());
	
	@Autowired
	private BeaconDeviceService beaconDeviceService;
	
	@Autowired
	private PortionService portionService;
	
	@Autowired
	private SitePortionWebController sitePortionWebController;
	
	@Autowired
	private SiteService siteService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private CustomerUtils customerUtils;
	
	@Autowired
	private CustomerService customerService;
	
	@RequestMapping("/list")
	public String list(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		List<Site> fsobjects = null;
		fsobjects = siteService.findByCustomerId(cid);
		
		if (fsobjects != null && fsobjects.size() > 0) {
			Site site    = fsobjects.get(0);
			sid 		 = site.getId();
		}
		
		model.put("cid", cid);
		model.put("sid", sid);
		model.put("spid",spid);
		
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		model.put("entryexit", 		customerUtils.entryexit(cid));
		model.put("locatum", 		customerUtils.Locatum(cid));
		model.put("vpn", 			customerUtils.Vpn(cid));
		
		SessionUtil.setCurrentCustomer(request.getSession(), cid);
		
		return _CCC.pages.getPage("facesix.beacon.device", "device-list");
	}
	
	@RequestMapping("/reglist")
	public String reglist(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		model.put("cid", cid);
		model.put("sid", sid);
		model.put("spid",spid);
		
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		SessionUtil.setCurrentCustomer(request.getSession(), cid);
		
		return _CCC.pages.getPage("facesix.beacon.regdevice", "regdevice");
	}
	
	@RequestMapping("/configure")
	public String configure(Map<String, Object> model,
							@RequestParam(value = "uid", required = false) String uid,
							@RequestParam(value = "sid", required = false) String sid,
							@RequestParam(value = "spid", required = false) String spid,
							@RequestParam(value = "cid", required = true) String cid,
							@RequestParam(value = "param", required = false) String param,
							HttpServletRequest request, HttpServletResponse response) {
		
		model.put("time", new Date());
		BeaconDevice device = null;
		
		//LOG.info( " device configure uid " +uid );
		
		if (uid != null) {
			//String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
			device = beaconDeviceService.findOneByUid(uid);
		}
		
		//LOG.info( " device device " +device );
		
		if (device != null) {
			model.put("message", Message.newInfo("Please update existing device config and press submit button to save"));
			model.put("hidden_value", false);
		}  else {
			device = new BeaconDevice();
			device.setUid(uid);
			device.setName(uid);
			device.setCid(cid);
			model.put("message", Message.newInfo("Please configure this new device and press submit button to save"));
			model.put("hidden_value", true);
		}
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		model.put("device", device);
		model.put("configuration", TAB_HIGHLIGHTER);
		model.put("sid", 	sid);
		model.put("spid",	spid);
		model.put("cid", 	cid);
		
		param = param == null ? "FloorConfig" : "DeviceConfig";
		model.put("param",  param);
		//LOG.info("param " +param );
		
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		model.put("vpn", 			customerUtils.Vpn(cid));
		
		return _CCC.pages.getPage("facesix.web.finder.device.config", "device-config");
	}
	
	
	@RequestMapping("/open")
	public String open(Map<String, Object> model,
					   @RequestParam(value = "id", required = false) String id,
					   @RequestParam(value = "uid", required = false) String uid,
					   @RequestParam(value = "fstype", required = false, defaultValue="default") String fstype,
					   @RequestParam(value = "name", required = false) String name,
					   @RequestParam(value = "cid", required = false) String cid,
					   HttpServletRequest request, HttpServletResponse response) {
		
		model.put("time", new Date());
		BeaconDevice device = null;
		if (id != null) {
			device = beaconDeviceService.findById(id);
		} else if (uid != null) {
			device = beaconDeviceService.findOneByUid(uid);
			//fetch config by id
		} else if (name != null) {
			//fetch config by namne
			device = beaconDeviceService.findOneByName(name);
		}
		
		if (device != null) {
			//model.put("disabled", "disabled");
			model.put("message", Message.newInfo("Please update existing device config correctly"));
		} else {
			device = new BeaconDevice(); //dummy
			device.setFstype(fstype);
			String template = SpringComponentUtils.getApplicationMessages().getMessage("facesix.beacon.device.template.default");
			if (template != null) 
			device.setConf(template);
			model.put("message", Message.newInfo("Please enter new device details correctly"));
		}
		
		model.put("device", device);
		model.put("configuration", TAB_HIGHLIGHTER);

		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.beacon.device.edit", "device-edit");
	}
	
	@RequestMapping("/topology")
	public String topology(Map<String, Object> model,@RequestParam(value = "id", required = true) String id,
						   @RequestParam(value = "cid", required = true) String cid,
						   HttpServletRequest request, HttpServletResponse response) {
		
		BeaconDevice device = beaconDeviceService.findById(id);
		try {
			
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			model.put("cid", cid);
			model.put("device", device);
			model.put("configuration", TAB_HIGHLIGHTER);
			model.put("tree", DeviceHelper.toJSON4D3BeacondeviceNetwork(device));

			model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
			model.put("Gateway", 		customerUtils.Gateway(cid));
			model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
			
		} catch (Exception e) {
			model.put("message", Message.newError("Failed to open device topology. Check internal error."));
			LOG.warn("Exception parsing device :" + id, e);
		}
	return _CCC.pages.getPage("facesix.beacon.device.topology", "device.topology");
	}
	
	@RequestMapping("/delete")
	public String delete(Map<String, Object> model,
					  @RequestParam(value = "id", required = true) String id, 
					  @RequestParam(value = "uid", required = false) String uid,
					  @RequestParam(value = "cid", required = false) String cid,
					  HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		BeaconDevice device = beaconDeviceService.findById(id);
		if (device == null && uid != null) {
			device = beaconDeviceService.findOneByUid(uid);
			device.setId(id);
			device = beaconDeviceService.save(device, false);
		}
		beaconDeviceService.delete(device);
		device.setId(null);
		model.put("device", device);
		model.put("message", Message.newInfo("Device deleted successfully :" + device.getUid()));
		model.put("configuration", TAB_HIGHLIGHTER);

		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		model.put("cid", cid);
		String url = "";

		try {
			url = "/facesix/web/finder/device/list?cid="+cid;
			response.sendRedirect(url);
		} catch (IOException e) {
			response.sendRedirect(url);
		}
		return _CCC.pages.getPage("facesix.beacon.device", "device-list");
	}
	
	@RequestMapping("/copy")
	public String open(Map<String, Object> model,@RequestParam(value = "id") String id) {
		
		model.put("time", new Date());
		BeaconDevice device = null;
		
		if (id != null) {
			device = beaconDeviceService.findById(id);
			if (device == null) {
				model.put("message", Message.newError("Device not found for copy, please enter new device details"));
			} else {
				BeaconDevice tmp = device;
				device = new BeaconDevice();
				device.setConf(tmp.getConf());
				device.setUid("Copy of " + tmp.getUid());
				device.setName("Copy of " + tmp.getName());
				model.put("message", Message.newInfo("Please correct copied details and save to persist."));
			}
		} else {
			model.put("message", Message.newError("No device to copy, please enter new device details"));
		}
		
		model.put("device", device);
		model.put("configuration", TAB_HIGHLIGHTER);
		model.put("finder",true);
		return _CCC.pages.getPage("facesix.beacon.device.edit", "device-edit");
	}
	
	@RequestMapping("/save")
	public String save(Map<String, Object> model,
					  @RequestParam(value = "id", required = false) String id,
					  @RequestParam(value = "uid", required = false) String uid,
					  @RequestParam(value = "name", required = false) String name,
					  @RequestParam(value = "fstype", required = false) String fstype,
					  @RequestParam(value = "conf", required = false) String conf,
					  @RequestParam(value = "cid", required = false) String cid,
					  HttpServletRequest request, HttpServletResponse response) {
		
		model.put("time", new Date());
		BeaconDevice device = null;
		Date dt = new Date();
		//LOG.info("conf " +conf);
		try {
			
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			boolean shouldSave = true;
			if (id == null) {
				uid = StringUtils.trimWhitespace(uid);
				name = StringUtils.trimWhitespace(name);
				conf = StringUtils.trimWhitespace(conf);
			
				device = new BeaconDevice();
				device.setFstype(fstype);
				device.setUid(uid);
				device.setName(name);
				device.setConf(conf);
				device.setCreatedOn(dt);
				device.setModifiedOn(dt);
				device.setCreatedBy(getCurrentUser(request, response));
				device.setModifiedBy(getCurrentUser(request, response));
				device.setCid(cid);
				if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(name)) {
					model.put("message", Message.newError("UID or Name can not be blank."));
					shouldSave = false;
				} else if (beaconDeviceService.exists(uid, name)) {
					model.put("message", Message.newError("Device with UID or Name already exists."));
					shouldSave = false;
				}
			} else {
				//it's existing
				device = beaconDeviceService.findById(id);
				if (device == null && uid != null) {
					device = beaconDeviceService.findOneByUid(uid);
					if (device != null) {
						device.setId(id);	
					}
				}
				
				if (device == null) {
					model.put("message", Message.newFailure("Device not found with ID :" + id));
					shouldSave = false;
				} else {
					//check the MAC/device id not overwritten
					
					conf = StringUtils.trimWhitespace(conf);
					device.setConf(conf);
					device.setModifiedBy(getCurrentUser(request, response));
					device.setModifiedOn(dt);
					if (!StringUtils.isEmpty(name)) device.setName(name);
				}
			}
			
			
			if (shouldSave) {
				device.setStatus(BeaconDevice.STATUS.CONFIGURED.name());
				device = beaconDeviceService.save(device);
				model.put("disabled", "disabled");
				model.put("message", Message.newSuccess("Device saved successfully."));
			}
			model.put("device", device);
			model.put("configuration", TAB_HIGHLIGHTER);
			model.put("cid", cid);

			model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
			model.put("Gateway", 		customerUtils.Gateway(cid));
			model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
			
			try {
				response.sendRedirect("/facesix/web/finder/device/list?cid="+cid);
			} catch (IOException e) {
				response.sendRedirect("/facesix/web/finder/device/list?cid="+cid);
			}
			
		} catch (Exception e) {
			LOG.info("while beacon device save error "+e);
		}
		
		return _CCC.pages.getPage("facesix.beacon.device", "device-list");
	}
	
	
	@RequestMapping("/devboard")
	public String devboard(Map<String, Object> model,  @RequestParam(value = "sid", required = false) String sid,
													   @RequestParam(value = "spid",required = false) String spid,
													   @RequestParam(value = "uid", required = false) String uid,													   
													   @RequestParam(value = "cid", required = false) String cid, 													   
													   @RequestParam(value = "mid",  required = false) String mid, 
													   @RequestParam(value = "place", required = false)String place,
													   HttpServletRequest request, HttpServletResponse response) {
	
	
		prepare(model, request, response);
		
		if(SessionUtil.isAuthorized(request.getSession())){
			
			int    mac 	= 0;
			BeaconDevice dv 	= null;
			String str 	= null;
			int size = 0;
			String alias = null;
			
			try {
		
				if (uid != null) {
					uid = uid.toLowerCase().trim();
					dv = beaconDeviceService.findByUidAndCid(uid, cid);
					if(dv == null){
						List<BeaconDevice> devices= beaconDeviceService.findByCidAndTypeAndAlias(cid,"receiver",uid);
						if (devices != null && devices.size() > 0) {
							size = devices.size();
							if(size == 1){
								dv = devices.get(0);
							}
						}
					}
				}
				
				if (mid != null) {
					if (dv != null) {
						spid = dv.spid;
						sid  = dv.sid;
						cid  = dv.getCid();
						alias = dv.getAlias() == null ? uid:dv.getAlias().toUpperCase();
						model.put("uid", uid);
						model.put("alias", alias);
					}
					
				} else if (uid != null && sid != null && spid == null) {
					if (dv != null) {
						spid = dv.spid;
						sid  = dv.sid;
						cid  = dv.getCid();
						uid = dv.getUid().toLowerCase();
						alias = dv.getAlias() == null ? uid:dv.getAlias().toUpperCase();
						model.put("uid", uid);
						model.put("alias", alias);
						mac = 1;
					}			
				} else {
					if (spid != null) {
						spid = sitePortionWebController.resolveSitePortion (spid, request, response);	
					}
					alias = (dv != null && dv.getAlias() == null)? uid: dv.getAlias().toUpperCase();
					model.put("uid", uid);
					model.put("alias", alias);
				}
						
				if (sid != null) {
					model.put("sid", sid);
					Site site = siteService.findById(sid);
					if (site != null) {
						model.put("sitename", site.getUid());
					}
				}

				if (spid != null) {
					model.put("spid", spid);
					Portion portion = portionService.findById(spid);
					if (portion != null) {
						model.put("portionname", portion.getUid());
					}
				}
				
				if (cid != null) {
					model.put("cid", cid);
					model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
					model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
					model.put("Gateway", 		customerUtils.Gateway(cid));
					model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
					
					model.put("entryexit", 		customerUtils.entryexit(cid));
					model.put("locatum", 		customerUtils.Locatum(cid));
				}
						
				if (spid != null) {
					Portion portion = portionService.findById(spid);
					model.put("fsobject", portion);
					model.put("tree", true);
				}
				
				if (dv == null) {
					try {
						if(uid != null && size != 0){
							str = "/facesix/web/finder/device/list?cid=" + cid+"&uid="+uid;
						} else {
							if (place != null) {
								if (place.equals("finderconfig")) {
									str = "/facesix/web/finder/device/list?cid=" + cid;
								} else if (place.equals("gatewayfindervenue")) {
									str = "/facesix/web/site/portion/dashview?sid=" + sid + "&cid="+cid+"&dashview=1";
								} else if (place.equals("steervenueGatewayFinder")) {
									str = "/facesix/web/site/portion/GW_SteerList?sid="+sid+"&cid="+cid;
								}
							} else {
								str = "/facesix/web/site/portion/dashview?sid=" + sid + "&cid=" + cid;
							}
						}
						response.sendRedirect(str);
					} catch (IOException e) {
						e.printStackTrace();
						LOG.info("While Page Redirection Error ", e);
					}
				}
				
				if (mid != null) {
					try {
						if (cid != null && (spid == null || sid == null)) {
							str = "/facesix/web/finder/device/list?cid=" + cid;
						} else if (cid != null && sid != null && spid != null) {
							str = "/facesix/web/finder/device/devboard?sid=" + sid + "&spid=" + spid + "&uid=" + uid+ "&cid=" + cid + "&type=sensor";
						}

						response.sendRedirect(str);
					} catch (IOException e) {
						e.printStackTrace();
						response.sendRedirect(str);
					}
				}
				
				if (mac == 1) {
					if (cid != null && (spid == null || sid == null)) {
						return _CCC.pages.getPage("facesix.beacon.device", "device-list");
					}
					
					try {
						if (place != null && place.equals("finderconfig")) {
							str = "/facesix/web/finder/device/list?cid=" + cid+"&uid="+uid;
						} else {
							str = "/facesix/web/finder/device/devboard?sid="+sid+"&spid="+spid+"&uid="+uid+"&cid="+cid+"&type=sensor";
						}
						
						response.sendRedirect(str);
					} catch (IOException e) {
						e.printStackTrace();
						response.sendRedirect(str);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return _CCC.pages.getPage("facesix.beacon.device", "device-list");
			}
		}else{
			return _CCC.pages.getPage("facesix.login", "login");
		}
		
		return _CCC.pages.getPage("facesix.web.finder.device.dashboard", "dev-dashboard");
	}	
	
	
	@RequestMapping("/logs")
	public String loginfo(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		model.put("cid", cid);
		model.put("sid", sid);
		model.put("spid",spid);
		
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.beacon.finder.logs", "finder-logs");
	}
	
	
	@RequestMapping(value = "/binary", method = RequestMethod.GET)
	public String list(Map<String, Object> model,
					   @RequestParam(value = "sid",  required = false)  String sid,
					   @RequestParam(value = "spid", required = false) String spid,
					   @RequestParam(value = "uid",  required = false) String uid,
					   @RequestParam(value = "cid",  required = false) String cid,
					   HttpServletRequest request) {

		String version   = "unknown";
		String buildTime = "unknown";
		
		String page =  _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			try {
				
				LOG.info("uid " + uid + "cid " + cid + " sid  " + sid + " spid " + spid);

				if (cid.isEmpty() || cid==null) {
					cid  = SessionUtil.getCurrentCustomer(request.getSession());
				}
				
				if (customerUtils.GeoFinder(cid)) {
					
					BeaconDevice beaconDevice = null;
					beaconDevice 			  = beaconDeviceService.findOneByUid(uid);
					
					model.put("fsobject", beaconDevice);
					
					if (beaconDevice != null) {
				        if(beaconDevice.getVersion() != null)
							version = beaconDevice.getVersion();
						if(beaconDevice.getBuild() !=null)
							buildTime = beaconDevice.getBuild();				
					}
					
				} else if (customerUtils.Gateway(cid) || customerUtils.Heatmap(cid)) {
					
					Device device = null;
					device 		  = deviceService.findOneByUid(uid);
					model.put("fsobject", device);
					
				} else if (customerUtils.GatewayFinder(cid)) {
					
					BeaconDevice beaconDevice = null;
					beaconDevice 			  = beaconDeviceService.findOneByUid(uid);
					
					if (beaconDevice != null) {
				        if(beaconDevice.getVersion() != null)
							version = beaconDevice.getVersion();
						if(beaconDevice.getBuild() !=null)
							buildTime = beaconDevice.getBuild();
						model.put("fsobject", beaconDevice);
					}
					
					Device device = null;
					device 		  = deviceService.findOneByUid(uid);
					if( device != null){
						model.put("fsobject", device);
					}
						
						
				}
				
				Customer customer = customerService.findById(cid);
				if (customer !=null) {
					model.put("custName", customer.getCustomerName());
				}
				
				version   = (version == null || version.isEmpty()) ? "UNKOWN" : version;
				buildTime = (buildTime == null || buildTime.isEmpty()) ? "UNKOWN" : buildTime;
				
				model.put("version"  , version);
				model.put("buildtime", buildTime);
				
				model.put("sid", sid);
				model.put("spid", spid);
				model.put("cid", cid);

				model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
				model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
				model.put("Gateway", 		customerUtils.Gateway(cid));
				model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
				model.put("Heatmap", 		customerUtils.Heatmap(cid));
				model.put("Retail", 	    customerUtils.isRetail(cid));
				
			} catch (Exception e) {
				LOG.info("While Binary Listing error ", e);
			}
			page = _CCC.pages.getPage("facesix.iot.site.binary", "site-binary");
			return page;
		}

		return page;
	}
	
}
