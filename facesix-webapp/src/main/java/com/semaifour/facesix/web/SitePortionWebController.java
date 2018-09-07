package com.semaifour.facesix.web;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.beacon.finder.geo.GeoFinderLayoutData;
import com.semaifour.facesix.beacon.finder.geo.GeoFinderLayoutDataService;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.ClientDevice;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.imageconverter.ImageConverter;
import com.semaifour.facesix.imageconverter.JpegToTiffConverter;
import com.semaifour.facesix.rest.ClientConfRestController;
import com.semaifour.facesix.rest.NetworkConfRestController;
import com.semaifour.facesix.rest.NetworkDeviceRestController;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;

/**
 * 
 * Site Portion Controller for the webapp - responsible for managing portions of iot site locations
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/web/site/portion")
public class SitePortionWebController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(SitePortionWebController.class.getName());
			
	@Autowired
	SiteService siteService;
	
	@Autowired
	PortionService portionService;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	HttpServletResponse response;	
	
	@Autowired
	NetworkDeviceService networkDeviceService;	
	
	@Autowired
	NetworkDeviceRestController networkDeviceRestController;
	
	@Autowired
	NetworkConfRestController networkcntrl;	
	
	
	@Autowired
	DeviceService _deviceService;	
	
	@Autowired
	ClientDeviceService  _clientDeviceService;
	
	@Autowired
	ClientConfRestController  clientConfRestController;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CustomerUtils CustomerUtils;
	
	private ImageConverter converter;
	
	@Autowired
	BeaconService 	beaconService;
	
	@Autowired
	BeaconDeviceService beaconDeviceService;
	
	@Autowired
	GeoFinderLayoutDataService geoFinderLayoutDataService;

	@Autowired
	BeaconDeviceService _beacondeviceService;

	public String resolveSite(String sid, HttpServletRequest request, HttpServletResponse response) {
		if (!sessionCache.equals(request.getSession(), "sid", sid)) {
			Site site = siteService.findById(sid);
			if (site != null) {
				sessionCache.setAttribute(request.getSession(), "sid", sid);
				sessionCache.setAttribute(request.getSession(), "suid", site.getUid());
				sessionCache.setAttribute(request.getSession(), "cid", site.getCustomerId());
				SessionUtil.setCurrentSite(request.getSession(), sid);
				SessionUtil.setCurrentCustomer(request.getSession(), site.getCustomerId());
				SessionUtil.setCurrentSiteCustomerId(request.getSession(), site.getCustomerId());
			} else {
				return null;
			}
		}
		return sid;
	}
	
		
	public String resolveSitePortion(String spid, HttpServletRequest request, HttpServletResponse response) {
		if (!sessionCache.equals(request.getSession(), "spid", spid)) {
			Portion site = portionService.findById(spid);
			if (site != null) {
				sessionCache.setAttribute(request.getSession(), "spid", spid);
				sessionCache.setAttribute(request.getSession(), "spuid", site.getUid());
				sessionCache.setAttribute(request.getSession(), "cid", site.getCid());
				SessionUtil.setCurrentCustomer(request.getSession(), site.getCid());
				SessionUtil.setCurrentPortionCustomerId(request.getSession(), site.getCid());
				
			} else {
				return null;
			}
		}
		return spid;
	}
	
	/**
	 * 
	 * Lists all Sites
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/list")
	public String list(Map<String, Object> model, @RequestParam(value = "sid", required = false) String sid,
												  @RequestParam(value = "cid", required = false) String cid,
												  HttpServletRequest request, HttpServletResponse response) {
		
		if (SessionUtil.isAuthorized(request.getSession())) {

			List<Portion> fsobjects = null;
			
			if (sid != null) {
				
				sid=resolveSite(sid, request, response);
				Site site = siteService.findById(sid);
				fsobjects = portionService.findBySiteId(sid);
				fsobjects = checkPortionStatus(fsobjects);
				model.put("fsobjects", fsobjects);
				
				if (cid == null) {
					if (site.getCustomerId() != null) {
						cid = site.getCustomerId();
					} else {
						cid = SessionUtil.getCurrentCustomer(request.getSession());
					}					
				}
				
				model.put("cid", site.getCustomerId());
				model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
				model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
				model.put("Gateway", 		CustomerUtils.Gateway(cid));
				model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
				model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
				
				model.put("sid", sid);
				model.put("sitename", site.getUid());
			}
			prepare(model, request, response);
		}
		

		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");
	}

	@RequestMapping("/sort")
	public String sort(Map<String, Object> model, @RequestParam(value = "sid", required=false) String sid,
												  @RequestParam(value = "cid", required = false) String cid,
												  HttpServletRequest request, HttpServletResponse response) {
		if (sid != null) {
			sid = resolveSite(sid, request, response);
			List<Portion> plist  = portionService.findBySiteId(sid);
			plist = checkPortionStatus(plist);
			List<Portion> mlist  = Collections.unmodifiableList(plist);
			List<Portion> list   = new ArrayList<Portion>(mlist);
			
			Comparator<Portion> cmp = Collections.reverseOrder(); 
			Collections.sort(list, cmp); 			
			model.put("fsobjects", list);
			model.put("sid", sid);
			model.put("cid", cid);
		}
		
		prepare(model, request, response);		
		
		return this.list(model, sid, cid, request, response);
	}	
	
	/**
	 * Opens a blank form
	 * 
	 * @param model
	 * @param sid
	 * @return
	 */
	@RequestMapping("/new")
	public String addnew(Map<String, Object> model, @RequestParam(value = "sid", required = true) String sid,
													@RequestParam(value = "cid", required = false) String cid,
													HttpServletRequest request, HttpServletResponse response) {
		sid=resolveSite(sid, request, response);
		Site site = siteService.findById(sid);
		model.put("message", Message.newInfo("Please enter new portion details correctly"));
		
		sessionCache.setAttribute(request.getSession(), "cid", site.getCustomerId());
		

		if (cid == null) {
			if (site.getCustomerId() != null) {
				cid = site.getCustomerId();
			} else {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}			
		}
		model.put("cid", cid);
		prepare(model, request, response);
		
		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		model.put("Heatmap", 	CustomerUtils.Heatmap(cid));
		
		return _CCC.pages.getPage("facesix.iot.site.portion.edit", "site-portion-edit");
	}
	
	
	/**
	 * 
	 * Copies given Portion to another
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/copy")
	public String copy(Map<String, Object> model, @RequestParam(value = "spid") String spid, HttpServletRequest request, HttpServletResponse response) {
		Portion portion = null;
		if (spid != null) {
			portion = portionService.findById(spid);
			if (portion == null) {
				model.put("message", Message.newError("Portion not found for copy, please enter new Portion details"));
			} else {
				//Portion = new Portion();
				portion.setId(null);
				portion.setUid("Copy of " + portion.getUid());
				portion.setName("Copy of " + portion.getName());
				model.put("message", Message.newInfo("Please correct copied details and save to persist."));
			}
		} else {
			model.put("message", Message.newError("No site to copy, please enter new site details"));
		}
		
		model.put("fsobject", portion);
		
		prepare(model, request, response);

		return _CCC.pages.getPage("facesix.iot.site.portion.edit", "site-portion-edit");
	}
	
	
	
	/**
	 * 
	 * Open a given Site
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/open")
	public String open(Map<String, Object> model, 
						@RequestParam(value = "sid", required = true) String sid,
						@RequestParam(value = "cid", required = false) String cid,
						@RequestParam(value = "spid", required = true) String spid, 
						HttpServletRequest request, HttpServletResponse response) {
		
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			Portion portion = null;
			portion = portionService.findById(spid);
			model.put("message", Message.newInfo("Please update portion details correctly"));
			model.put("fsobject", 	portion);
			model.put("width",  	portion.getWidth()+"(width)");
			model.put("height", 	portion.getHeight()+"(height)");
			Site site = siteService.findById(portion.getSiteId());
			
			prepare(model, request, response);
			
			if  (cid == null) {
				cid = site.getCustomerId();
			}
			
			model.put("cid", cid);
			model.put("sid", sid);
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 	CustomerUtils.Heatmap(cid));
			
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
		
		return _CCC.pages.getPage("facesix.iot.site.portion.edit", "site-portion-edit");
	}
	
	
	/**
	 * Deletes the Id
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	public String delete(Map<String, Object> model,
						@RequestParam(value = "sid",  required = false) String sid, 
						@RequestParam(value = "spid", required = true) String spid, 
						@RequestParam(value = "cid", required = false) String cid,
						HttpServletRequest request, HttpServletResponse response) {
		
		List<ClientDevice> clientDeviceList =null;
		List<Device> deviceList=null;
		
		//LOG.info("Delete SPID " + spid);
		//LOG.info("Delete SID " + sid);
		
		Portion portion = portionService.findById(spid);
		
		List<Beacon> beacon = null;
		List<BeaconDevice> beaconDevice=null;
		GeoFinderLayoutData geofinder = null;
		
		try {

			if (portion != null) {

					//LOG.info("Finder delete option ");

					beacon = beaconService.getSavedBeaconBySpid(spid);
					if (beacon != null) {
						for (Beacon b : beacon) {
							beaconService.delete(b);
						}
					}

					beaconDevice = beaconDeviceService.findBySpid(spid);
					if (beaconDevice != null) {
						for (BeaconDevice beaconDv : beaconDevice) {
							beaconDeviceService.delete(beaconDv);
						}
					}

				
			clientDeviceList = getClientDeviceService().findBySpid(portion.getId());
				if (clientDeviceList != null) {
					for (ClientDevice clientDevice : clientDeviceList) {
						getClientDeviceService().delete(clientDevice);
					}

				}
				
			 deviceList = getDeviceService().findBySpid(portion.getId());
				if (deviceList != null) {
					for (Device device : deviceList) {
						getDeviceService().delete(device); // DEVICES 
					}
				}

				networkcntrl.deletespid(portion.getId()); // NetworkDevices
				portionService.delete(portion.getId()); // Portion

				geofinder = geoFinderLayoutDataService.getSavedGeoLayoutDataBySpid(spid); //JNI GEOPOINTS DELETE OPTIONS
				if (geofinder != null) {
					geoFinderLayoutDataService.delete(geofinder);
				}
				
				model.put("fsobject", portion);
				model.put("message", Message.newError("Deleted successfully :" + portion.getUid()));
			}
			if (sid != null) {
				model.put("fsobjects", portionService.findBySiteId(sid));
			}

		} catch (Exception e) {
			LOG.error("Error delete portion", e);
		}

		model.put("cid", cid);
		prepare(model, request, response);
		try {
			String str = "/facesix/web/site/portion/list?sid="+sid+"&cid="+cid;
			response.sendRedirect(str);  
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");

	}
	
		
	/**
	 * Dashboard Floor
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/dashboard")
	public String dashboard(Map<String, Object> model, @RequestParam(value = "spid", required 	= false) String spid, 
													   @RequestParam(value = "sid", required 	= false)  String sid,
													   @RequestParam(value = "cid", required 	= false) String cid,
													   @RequestParam(value = "param", required 	= false) String param,
													   HttpServletRequest request, HttpServletResponse response) {
		spid = resolveSitePortion(spid, request, response);
		Portion portion = null;
		
		if (spid != null && !spid.isEmpty()) {
			portion = portionService.findById(spid);
		}
		
		model.put("fsobject", portion);
		prepare(model, request, response);
		if (portion != null) {
			model.put("sid", sid);
			model.put("spid", spid);
		    model.put("cid", cid);
		    model.put("param",param);
		    model.put("portionname",portion.getUid());
		    model.put("id", portion.getId());
			model.put("height", 	portion.getHeight());
			model.put("width",  	portion.getWidth());		    
		    sid = portion.getSiteId();
		    cid = portion.getCid();
		   
		}
		
		if (sid != null) {
			Site site = siteService.findById(sid);
			model.put("sitename", site.getUid());
			
			List<Portion> portionlist = portionService.findBySiteId(sid);
			model.put("portion", portionlist);
		}
		
		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		
		model.put("entryexit", 		CustomerUtils.entryexit(cid));
		model.put("locatum", 		CustomerUtils.Locatum(cid));
		model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
		
		String retPage = "";
		if (CustomerUtils.Gateway(cid)) {
			retPage = _CCC.pages.getPage("facesix.iot.site.portion.dashboard", "site-portion-dashboard");
		} else if (CustomerUtils.GeoFinder(cid)){
			retPage = _CCC.pages.getPage("facesix.web.finder.floor.dashview", "site-portion-dashboard");
		} else if (CustomerUtils.GatewayFinder(cid)) {
			if (param.equals("1")) {
				retPage = _CCC.pages.getPage("facesix.iot.site.portion.dashboard", "site-portion-dashboard");
			} else if (param.equals("2")) {
				retPage = _CCC.pages.getPage("facesix.web.finder.floor.dashview", "site-portion-dashboard");
			}
			else if (param.equals("0")) {
			retPage = _CCC.pages.getPage("facesix.iot.site.portion.dashboard", "site-portion-dashboard");		
			}
		}
		return retPage;
	}

	@RequestMapping("/flrdash")
	public String flrdash(Map<String, Object> model, @RequestParam(value = "uid", required = false) String uid, 
													 @RequestParam(value = "spid", required = true) String spid,
													 @RequestParam(value = "cid", required = false) String cid,
													 HttpServletRequest request, HttpServletResponse response) {
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			String sid="";
			
			spid = resolveSitePortion(spid, request, response);
			
			Portion portion = portionService.findById(spid);
			
			if (portion != null) {

				cid = portion.getCid();
				sid = portion.getSiteId();
				
				model.put("fsobject", 	 portion);
				model.put("portionname", portion.getUid());
				model.put("id", 		 portion.getId());
				model.put("height", 	 portion.getHeight());
				model.put("width",  	 portion.getWidth());
			}

			model.put("uid", 		uid);
			model.put("spid", 		spid);
			model.put("cid",   		cid);
			
			if (sid != null) {
				
				List<Portion> portionlist = portionService.findBySiteId(sid);
				if (portionlist !=null) {
					model.put("portion", portionlist);
					model.put("sid", sid);
				}
				
				Site site = siteService.findById(sid);
				if (site != null) {
					model.put("sitename", site.getUid());
				}
			}

			prepare(model, request, response);
			
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
			
			page = _CCC.pages.getPage("facesix.iot.site.portion.flrdash", "site-portion-flrdash");
		}
		
		return page;
	}	
		
	@RequestMapping("/nwcfg")
	public String nwcfg(Map<String, Object> model, @RequestParam(value = "sid",  required = true)  String sid, 
												   @RequestParam(value = "spid", required = true)  String spid ,
												   @RequestParam(value = "cid",  required = true)  String cid ,
												   @RequestParam(value = "uid",  required = false) String uid, HttpServletRequest request, HttpServletResponse response) {
		
		prepare(model, request, response);
		model.put("message", Message.newInfo("Please enter site details correctly"));
		spid = resolveSitePortion(spid, request, response);
		Portion portion = portionService.findById(spid);
		
		if (portion != null) {
			
			if (cid != null) {
				cid = portion.getCid();	
			}
			
			model.put("fsobject", 	portion);
			model.put("sid",  		sid);
			model.put("spid", 		spid);
			model.put("cid", 		cid);
			model.put("uid",  		uid);
			model.put("tree", 		true);
			model.put("height", 	portion.getHeight());
			model.put("width",  	portion.getWidth());
			
			if (sid != null) {
				Site site = siteService.findById(sid);
				if (site != null) {
					model.put("sitename", site.getUid());
				}
			}
			
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));	
			model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
		}
				
		return _CCC.pages.getPage("facesix.iot.site.portion.nwcfg", "site-portion-nwcfg");
	}	
	
	@RequestMapping("/binary")
	public String binary(Map<String, Object> model, @RequestParam(value = "sid",  required = true)  String sid, 
												   @RequestParam(value = "spid", required = true)  String spid ,
												   @RequestParam(value = "uid",  required = false)  String uid,
												   @RequestParam(value = "cid",  required = true)  String cid, HttpServletRequest request, HttpServletResponse response) {
		
		model.put("sid",  sid);
		model.put("spid", spid);
		model.put("uid",  uid);
		model.put("cid",  cid);
		prepare(model, request, response);
		return _CCC.pages.getPage("facesix.iot.site.portion.binary", "site-portion-binary");
	}
	
	
	
	@RequestMapping("/find")
	public String find(Map<String, Object> model, 
			@RequestParam(value = "sid",  required  = false)  String sid, 
			@RequestParam(value = "cid",  required  = true) String cid,
			@RequestParam(value = "spid",  required = true)  String spid,HttpServletRequest request, HttpServletResponse response  ) {
		
		model.put("message", Message.newInfo("Please enter site details correctly"));
		prepare(model, request, response);
		
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
		
		
		model.put("sid",  sid);
		// Add spid
		model.put("spid", spid);
		model.put("cid", cid);
	    model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.iot.site.portion.find", "site-portion-find");
	}
	
	@RequestMapping("/draw")
	public String draw(Map<String, Object> model, @RequestParam(value = "sid",  required = true)  String sid, 
			@RequestParam(value = "spid",  required = true)  String spid, HttpServletRequest request, HttpServletResponse response  ) {
		
		model.put("message", Message.newInfo("Please enter site details correctly"));
		model.put("sid",  sid);
		prepare(model, request, response);
		// Add spid
		model.put("spid", spid);
		model.put("GeoDraw", true);
		return _CCC.pages.getPage("facesix.iot.site.portion.draw", "site-portion-draw");
	}
	
	@RequestMapping("/plot")
	public String plot(Map<String, Object> model, @RequestParam(value = "sid",  required = true)  String sid , 
			@RequestParam(value = "spid",  required = true)  String spid,HttpServletRequest request, HttpServletResponse response ) {
		
		model.put("message", Message.newInfo("Please enter site details correctly"));
		model.put("sid",  sid);		
		prepare(model, request, response);
		model.put("spid", spid);
		return _CCC.pages.getPage("facesix.iot.site.portion.plot", "site-portion-plot");
	}	
	
	@RequestMapping("/campaign")
	public String campaign(Map<String, Object> model, @RequestParam(value = "sid",  required = true)  String sid  , HttpServletRequest request, HttpServletResponse response) {
		
		model.put("message", Message.newInfo("Please enter site details correctly"));
		model.put("sid",  sid);
		prepare(model, request, response);
		return _CCC.pages.getPage("facesix.iot.site.portion.campaign", "site-portion-campaign");
	}	
	
	@RequestMapping("/dashview")
	public String dashview(Map<String, Object> model, @RequestParam(value = "sid", required = true) String sid,
													  @RequestParam(value = "spid", required = false) String spid,
													  @RequestParam(value = "cid", required = false) String cid,
													  @RequestParam(value = "param", required = false) String param,
													  @RequestParam(value = "dashview", required = false) String dashview,
													  HttpServletRequest request, HttpServletResponse response) {
		sid = resolveSite(sid, request, response);
		List<Portion>  portionList=null;
		String retPage = "";
		
		if (sid != null) {
			portionList = portionService.findBySiteId(sid);
			portionList = checkPortionStatus(portionList); //checking portion status
			model.put("fsobjects",portionList);

			Site site = siteService.findById(sid);
			prepare(model, request, response);
			
			model.put("sid", sid);
			model.put("spid", spid);
		    model.put("cid", cid);
		    model.put("sitename",site.getUid());
		    model.put("dashview", dashview);

		    model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));

			//retPage = _CCC.pages.getPage("facesix.iot.site.portion.dashview", "site-portion-dashview");

			if (CustomerUtils.Gateway(cid)) {
				retPage = _CCC.pages.getPage("facesix.iot.site.portion.dashview", "site-portion-dashview");
			} else if(CustomerUtils.GeoFinder(cid)){
				retPage = _CCC.pages.getPage("facesix.web.finder.device.dashview", "site-portion-dashview");
			}else if (CustomerUtils.GatewayFinder(cid)) {
				if (dashview.equals("1")) {
					retPage = _CCC.pages.getPage("facesix.iot.site.portion.dashview", "site-portion-dashview");
				} else if (dashview.equals("2")) {
					retPage = _CCC.pages.getPage("facesix.web.finder.device.dashview", "site-portion-dashview");
				}
				else if (dashview.equals("0")) {
					retPage = _CCC.pages.getPage("facesix.iot.site.portion.dashview", "site-portion-dashview");		
			}
			}			
		}
		
		return retPage;
	}	
	
	
	@RequestMapping("/map")
	public String map(	Map<String, Object> model, @RequestParam(value = "spid", required = true) String spid,
						@RequestParam(value = "sid", required = false) String sid,
						@RequestParam(value = "cid", required = false) String cid,
						HttpServletRequest request, HttpServletResponse response) {

		spid = resolveSitePortion(spid, request, response);
		Portion portion = portionService.findById(spid);
		if (portion.getStatus().equals(CustomerUtils.ACTIVE())) {
			model.put("fsobject", portion);
		}
		prepare(model, request, response);
		model.put("message", Message.newInfo("Please enter site details correctly"));
		model.put("spid", spid);
		model.put("sid", sid);
		model.put("cid", cid);
		if (sid != null) {
			Site site = siteService.findById(sid);
			if (site != null) {
				model.put("sitename", site.getUid());
			}
		}
		
		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		model.put("Heatmap", 		CustomerUtils.Heatmap(cid));
		
		return _CCC.pages.getPage("facesix.iot.site.portion.map", "site-portion-map");
	}
	
	@RequestMapping("/logview")
	public String logview(Map<String, Object> model, @RequestParam(value = "sid",  required = false) String sid, 
													 @RequestParam(value = "spid", required = false) String spid, 
													 @RequestParam(value = "uid",  required = false) String uid,
													 @RequestParam(value = "cid", required = false) String cid,
													 @RequestParam(value = "logview", required = false) String logview,
													 HttpServletRequest request, HttpServletResponse response) {
		String retPage = "";
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		if (logview == null || logview.isEmpty()) {
			logview = "0";
		}
		
		prepare(model, request, response);
		model.put("sid",  sid);
		model.put("spid", spid);
		model.put("uid",  uid);
		model.put("cid",  cid);
		model.put("logview",  logview);


		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
		 
		//return _CCC.pages.getPage("facesix.iot.site.portion.logview", "site-portion-logview");
		if (CustomerUtils.Gateway(cid) || CustomerUtils.Heatmap(cid)) {
			retPage = _CCC.pages.getPage("facesix.iot.site.portion.logview", "site-portion-logview");
		} else if(CustomerUtils.GeoFinder(cid)){
			retPage = _CCC.pages.getPage("facesix.beacon.finder.logs", "finder-logs");
		}else if (CustomerUtils.GatewayFinder(cid)) {
			if (logview.equals("1")) {
				retPage = _CCC.pages.getPage("facesix.iot.site.portion.logview", "site-portion-logview");
			} else if (logview.equals("2")) {
				retPage = _CCC.pages.getPage("facesix.beacon.finder.logs", "finder-logs");
			}
			else if (logview.equals("0")) {
				retPage = _CCC.pages.getPage("facesix.iot.site.portion.logview", "site-portion-logview");		
			}
		}				
	
		return retPage;
	}		
	
	
	@RequestMapping("/devboard")
	public String devboard(Map<String, Object> model,  @RequestParam(value = "sid", required = false) String sid,
													   @RequestParam(value = "spid",required = false) String spid,
													   @RequestParam(value = "uid", required = false) String uid,													   
													   @RequestParam(value = "cid", required = false) String cid, 													   
													   @RequestParam(value = "mid",  required = false) String mid,
													   @RequestParam(value = "place",  required = false) String place,
													   HttpServletRequest request, HttpServletResponse response) {
	
		int    mac 	= 0;
		Device dv 	= null;
		String str 	= null;
		String name	= uid;
		int size = 0;
		String alias = null;
		
		prepare(model, request, response);
		
		//LOG.info("Welcome to devboard>>>" + " " + mac + " " + uid + " " + sid + " " + spid + " " + cid);
		
		if(cid == null){
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		if (uid != null) {
			uid = uid.toLowerCase().trim();
			//LOG.info("UID Lower case" + uid);
		}
		
		if (uid != null && cid != null) {
			dv = getDeviceService().findByUidAndCid(uid, cid);
			if(dv == null){
				if (name != null) {
					name = name.trim();
					List<Device> devices= getDeviceService().findByCidAndAlias(cid,name);
					if (devices != null && devices.size() > 0) {
						size = devices.size();
						if(size == 1){
							dv = devices.get(0);
						}
					}
				}
			}
		}
		
		if (mid != null) {
			if (dv != null) {
				spid = dv.spid;
				sid  = dv.sid;
				cid  = dv.getCid();
				alias = dv.getAlias() == null? uid: dv.getAlias().toUpperCase();
				model.put("uid", uid);
				model.put("alias", alias);
				//LOG.info("!!!!!!");
			}
			
		} else if (uid != null && sid != null && spid == null) {
			if (dv != null) {
				spid = dv.spid;
				sid = dv.sid;
				cid = dv.getCid();
				uid = dv.getUid().toLowerCase();
				alias = dv.getAlias() == null? uid: dv.getAlias().toUpperCase();
				model.put("uid", uid);
				model.put("alias", alias);
				mac = 1;
				//LOG.info("Devboard<<<<<>>>>>!!!!" + " " + mac +" "+ uid + " " + sid + " " + spid);
			}
		} else {
			//LOG.info("Welcome!!!!@@###");
			if (spid != null) {
				spid = resolveSitePortion (spid, request, response);	
			}
			alias = (dv != null && dv.getAlias() == null)? uid: dv.getAlias().toUpperCase();
			model.put("uid", uid);
			model.put("alias", alias);
		}
				
		if (sid != null) {
			//LOG.info("Welcome1111111111");
			model.put("sid", sid);
			Site site = siteService.findById(sid);
			if (site != null) {
				model.put("sitename", site.getUid());
			}
		}

		if (spid != null) {
			//LOG.info("Welcome22222");

			model.put("spid", spid);
			Portion portion = portionService.findById(spid);
			if (portion != null) {
				model.put("portionname", portion.getUid());
			}
		}
		
		if (cid != null) {
			model.put("cid", cid);
			//LOG.info("Welcome23333");
			
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 		CustomerUtils.Heatmap(cid));
			
			model.put("entryexit", 		CustomerUtils.entryexit(cid));
			model.put("locatum", 		CustomerUtils.Locatum(cid));
		}
				
		if (spid != null) {
			//LOG.info("Welcome4444");

			Portion portion = portionService.findById(spid);
			
			if (portion.getStatus().equals(CustomerUtils.ACTIVE())) {
				model.put("fsobject", portion);
			}
			model.put("tree", true);
		}
		
		if (dv == null) {
			//LOG.info("Welcome555");

			try {
					if(uid != null && size != 0){
						str = "/facesix/web/finder/device/list?cid=" + cid+"&uid="+uid;
					} else {
						place = place == null ? "none" : place;
						if (place.equals("gatewayfloor")) {
							str = "/facesix/web/site/portion/dashboard?sid=" + sid + "&spid=" + spid + "&cid=" + cid;
						} else if (place.equals("gatewayconfig")) {
							str = "/facesix/spots?cid=" +cid;
						} else if (place.equals("gatewayvenue")){
							str = "/facesix/web/site/portion/dashview?sid=" + sid + "&cid=" + cid;
						} else if (place.equals("gatewayfindervenue") || place.equals("steervenueGatewayFinder")){
							str = "/facesix/web/finder/device/devboard?sid="+ sid + "&cid="+ cid + "&uid="+uid + "&place="+place;
						} else if (place.equals("steervenue")) {
							str = "/facesix/web/site/portion/GW_SteerList?sid="+sid+"&cid="+cid;
						}
					}
					response.sendRedirect(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return _CCC.pages.getPage("facesix.spots", "facesix-spots");
			
		}
		if (place != null && place.equals("gatewayfloor")){
			//LOG.info("Welcome666");

			if (dv != null && dv.spid==null) {
				try {
					if (sid != null) {
						str = "/facesix/spots?cid="+cid+"&sid="+sid+"&uid="+uid;
					} else {
						str = "/facesix/spots?cid="+cid+"&uid="+uid;
					}
					str = "/facesix/spots?cid="+cid+"&uid="+uid;
					response.sendRedirect(str);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			
			///LOG.info("Welcome77");

			if (mid != null) {
				//LOG.info("Welcome88");

				if (cid != null && (spid == null || sid == null)) {
					str = "/facesix/spots?cid=" + cid;
					response.sendRedirect(str);
					return _CCC.pages.getPage("facesix.spots", "facesix-spots");
				}

				if (cid != null && sid != null && spid != null) {
					//LOG.info("Welcome11199");

					str = "/facesix/web/site/portion/devboard?sid=" + sid + "&spid=" + spid + "&uid=" + uid + "&cid="+ cid + "&type=ap";
				}

				response.sendRedirect(str);
				return _CCC.pages.getPage("facesix.iot.site.portion.dev.dashboard", "site-portion-dev-dashboard");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if (mac == 1) {
			try {
				//LOG.info("Welcome222299");

				if (cid != null &&(sid == null||spid == null)) {
					str = "/facesix/spots?cid=" + cid+"&uid="+uid;
				} else if ((place != null && place.equals("gatewayconfig"))|| (sid == null && spid == null)) {
					str = "/facesix/spots?cid=" + cid +"&uid="+uid;
				} else if (sid != null && spid != null) {
					str = "/facesix/web/site/portion/devboard?sid=" + sid + "&spid=" + spid + "&uid=" + uid + "&cid="+ cid + "&type=ap";
				} else if (place !=null && place.equals("gatewayvenue") && (spid==null || sid == null)) {
					str = "/facesix/spots?cid=" + cid +"&uid="+uid;
				}
				response.sendRedirect(str);
			} catch (IOException e) {
				LOG.info("While devboard  Page Redirection Error ", e);
			}
		}
		//LOG.info("Welcome1000000");
		
		return _CCC.pages.getPage("facesix.iot.site.portion.dev.dashboard", "site-portion-dev-dashboard");
	}	
	
	@RequestMapping("/swiboard")
	public String swiboard(Map<String, Object> model, @RequestParam(value = "sid", required = false) String sid,
													  @RequestParam(value = "spid", required = false) String spid,
													  @RequestParam(value = "uid", required = false) String uid, 
													  @RequestParam(value = "cid", required = false) String cid, 
													  HttpServletRequest request, HttpServletResponse response) {
		spid = resolveSitePortion(spid, request, response);
		
		Portion portion = portionService.findById(spid);
		if (portion != null) {
			model.put("fsobject", portion);
			sid = portion.getSiteId();
			cid = portion.getCid();
		}
		
		prepare(model, request, response);
		
		Site site = siteService.findById(sid);
		if (site !=null) {
			model.put("sitename", site.getUid());
		}
		
		model.put("sid", sid);
		model.put("spid",spid);
		model.put("uid", uid);
		model.put("cid", cid);
		model.put("tree", true);
	
		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.iot.site.portion.swi.dashboard", "site-portion-swi-dashboard");
	}	
	
	
	
	@RequestMapping("/heatmap")
	public String heatmap(Map<String, Object> model, 
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "uid", required = false) String uid,
			@RequestParam(value = "mid",  required = false) String mid, 
			HttpServletRequest request, HttpServletResponse response) {

		int mac 	= 0;
		Device dv 	= null;
		String str 	= null;

		prepare(model, request, response);

		if (uid != null) {
			uid = uid.toLowerCase();
			dv = getDeviceService().findOneByUid(uid);
		}

		if (mid != null) {
			if (dv != null) {
				spid = dv.spid;
				sid = dv.sid;
				cid = dv.getCid();
				model.put("uid", uid);
			}

		} else if (uid != null && sid != null && spid == null) {
			if (dv != null) {
				spid = dv.spid;
				sid = dv.sid;
				cid = dv.getCid();
				uid = dv.getUid().toLowerCase();
				model.put("uid", uid);
				mac = 1;
			}
		} else {
			if (spid != null) {
				spid = resolveSitePortion(spid, request, response);
			}

			model.put("uid", uid);
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
			model.put("height", portion.getHeight());
			model.put("width", portion.getWidth());
			if (portion.getStatus().equals(CustomerUtils.ACTIVE())) {
				model.put("fsobject", portion);
			}
		}

		if (cid != null) {
			model.put("cid", cid);

			model.put("GatewayFinder", CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", CustomerUtils.GeoFinder(cid));
			model.put("Gateway", CustomerUtils.Gateway(cid));
			model.put("GeoLocation", CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 	CustomerUtils.Heatmap(cid));
		}

		if (dv == null) { // invalid UID search Redirection
			try {
				str = "/facesix/web/site/portion/heatmap?sid=" + sid + "&cid=" + cid;
				response.sendRedirect(str);
			} catch (IOException e) {
				LOG.info("While Page Redirection Error ", e);
			}
		}

		if (mid != null) {
			if (spid == null && cid == null) {
				try {
					str = "/facesix/spots?cid=" + cid;
					response.sendRedirect(str);
				} catch (IOException e) {
					LOG.info("While spots  Page Redirection Error ", e);
				}
				return _CCC.pages.getPage("facesix.spots", "facesix-spots");
			}

		try {
				if (spid == null && sid == null) {
					str = "/facesix/web/site/portion/heatmap?uid="+uid+"&cid="+cid;
				} else {
					str = "/facesix/web/site/portion/heatmap?sid="+sid+"&spid="+spid+"&uid="+uid+"&cid="+cid;
				}

				response.sendRedirect(str); // duplicate save avoid using page
											// redirection
			} catch (IOException e) {
				LOG.info("While devboard  Page Redirection Error ", e);
			}
			return _CCC.pages.getPage("facesix.iot.site.portion.heatmap", "site-portion-heatmap");
		}

		if (mac == 1) {
			if (spid == null) {
				return _CCC.pages.getPage("facesix.iot.site.portion.heatmap", "site-portion-heatmap");
			}

			try {
				str = "/facesix/web/site/portion/heatmap?sid=" + sid + "&spid=" + spid + "&uid=" + uid + "&cid=" + cid;
				response.sendRedirect(str); // duplicate save avoid using page
											// redirection
			} catch (IOException e) {
				LOG.info("While devboard  Page Redirection Error ", e);
			}
		}
		
		return _CCC.pages.getPage("facesix.iot.site.portion.heatmap", "site-portion-heatmap");
	}
	
	@RequestMapping("/fullheatmap")
	public String fullheatmap(Map<String, Object> model, @RequestParam(value = "sid",  required = false)  String sid, 
												   @RequestParam(value = "spid", required = false)  String spid ,
												   @RequestParam(value = "uid",  required = false)  String uid,
												   @RequestParam(value = "cid",  required = false)  String cid, HttpServletRequest request, HttpServletResponse response) {
		
		prepare(model, request, response);
		
		if (sid != null) {
			model.put("sid", sid);
			Site site = siteService.findById(sid);
			if (site != null) {
				model.put("sitename", site.getUid());
				model.put("site",  site);
			}
			
		}
	
		if (spid != null) {
			spid = resolveSitePortion(spid, request, response);
		}
		if (spid != null) {
			model.put("spid", spid);
			Portion portion = portionService.findById(spid);
			if (portion != null) {
				model.put("portionname", portion.getUid());
				model.put("portion",  portion);
			}
			model.put("height", portion.getHeight());
			model.put("width", portion.getWidth());
			if (portion.getStatus().equals(CustomerUtils.ACTIVE())) {
				model.put("fsobject", portion);
			}
		}
		
		model.put("sid",  sid);
		model.put("spid", spid);
		model.put("uid",  uid);
		model.put("cid",  cid);
		
		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
		return _CCC.pages.getPage("facesix.iot.site.portion.fullheatmap", "site-portion-fullheatmap");
	}
	
	/**
	 * Saves Sites
	 * 
	 * @param model
	 * @param newfso
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Map<String, Object> model, @ModelAttribute Portion newfso, @RequestParam( value="file", required=false) MultipartFile planFile, HttpServletRequest request, HttpServletResponse response) throws Exception {
		resolveSite(newfso.getSiteId(), request, response);
		Site site=siteService.findById(newfso.getSiteId());
		String cid = site.getCustomerId();
		//LOG.info("site cid " +site.getCustomerId());
		boolean shouldSave = true;
		if (newfso.getId() == null) {
			newfso.setCreatedOn(new Date());
			newfso.setModifiedOn(new Date());
			newfso.setCreatedBy(SessionUtil.currentUser(request.getSession()));
			newfso.setModifiedBy(newfso.getCreatedBy());
			newfso.setStatus(CustomerUtils.ACTIVE());
			newfso.setCid(cid);
		} else {
			resolveSitePortion(newfso.getId(), request, response);
			//it's existing
			Portion oldfso = portionService.findById(newfso.getId());
			if (oldfso == null) {
				model.put("message", Message.newFailure("Site not found with ID :" + newfso.getId()));
				shouldSave = false;
			} else {
				//check the mac/device id not overwritten
				oldfso.setUid(newfso.getUid());
				oldfso.setDescription(newfso.getDescription());
				oldfso.setModifiedOn(new Date());
				newfso.setModifiedBy(SessionUtil.currentUser(request.getSession()));
				newfso = oldfso;
			}
		}
		
		if (shouldSave ) {
			newfso = portionService.save(newfso);
			model.put("disabled", "disabled");
			model.put("message", Message.newSuccess("Site saved successfully."));
		}
		
		LOG.info("planFile " +planFile);
		//LOG.info("site portion Cid " + newfso.getCid() + "UID " + newfso.getUid());
		if(planFile !=null && !planFile.isEmpty() && planFile.getSize() > 1) {
			try {
				Path path = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"), (newfso.getId() + "_" + planFile.getOriginalFilename()));
				Files.createDirectories(path.getParent());
				
				LOG.info( "FileName " + planFile.getOriginalFilename());
				LOG.info( "GetName " + planFile.getName());
				LOG.info( "PlanFile " + planFile.toString());
				LOG.info( "Dest " + path.toString());
				LOG.info( "PathFile " + path.getFileName());
				
				
				//String localPath = "/home/qubercomm/Desktop/geofloorplan.tif";
				String name = planFile.getOriginalFilename();
				String fileName = name.split("\\.")[0];
				
				Path jnipath = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"), ("geo"+newfso.getId() + "_" + fileName + ".tif"));
				
				converter = new JpegToTiffConverter(); 
				converter.convert(planFile.getInputStream(), jnipath.toString());
				
				int width  = 0;
				int height = 0;
				
				BufferedImage bimg = ImageIO.read(planFile.getInputStream());
				width      = bimg.getWidth();
				height     = bimg.getHeight();
				
				LOG.info(" geo path  " + jnipath.toString());
				LOG.info(" jni file name  " + fileName + " height " +height + " width " +width);
				
				Files.copy(planFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				newfso.setPlanFilepath(path.toString());
				newfso.setJNIFilepath(jnipath.toString());
				newfso.setHeight(height);
				newfso.setWidth(width);
				newfso = portionService.save(newfso);
			}  catch (IOException e) {
				LOG.warn("Failed save floor plan file", e);
			}
		}
		
		model.put("fsobjects", portionService.findBySiteId(newfso.getSiteId()));
		
		prepare(model, request, response);
		
		try {
			String str = "/facesix/web/site/portion/list?sid=" + newfso.getSiteId()+"&cid="+cid;
			response.sendRedirect(str); //	duplicate save avoid using page redirection
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");
	}
	
	
	/**
	 * Returns floor plan file content
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/planfile", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPlanFile(@RequestParam(value = "spid", required = true) String spid) {

		try {
			Portion oldfso = portionService.findById(spid);
			if (oldfso != null && oldfso.getPlanFilepath() != null) {
				
				return ResponseEntity.ok(resourceLoader.getResource("file:" + oldfso.getPlanFilepath()));
				
			}
			//LOG.info("FILE PATH " + oldfso.getPlanFilepath());
		} catch (Exception e) {
			LOG.warn("Failed to load floor plan for portion :" + spid, e);
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.notFound().build();
	}

	@RequestMapping("/GW_SteerList")
	public String GW_SteerList(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = true) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			List<Portion> portionList = null;
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			if (sid != null) {
				model.put("sid", sid);
				portionList = portionService.findBySiteId(sid);
				model.put("portion", portionList);
				
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
					model.put("height", 	 portion.getHeight());
					model.put("width",  	 portion.getWidth());	
				}
			}
					
			if (portionList != null && portionList.size() > 0) {
				Portion object = portionList.get(0);
				model.put("portionname", object.getUid());
				model.put("spid", 		 object.getId());
				//LOG.info("Width" + object.getWidth());
				//LOG.info("Height" + object.getHeight());
			}
			
			
			model.put("cid", cid);
			model.put("sid", sid);
			model.put("spid",spid);
			
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			
			page = _CCC.pages.getPage("facesix.iot.site.portion.GW_SteerList", "site-portion-GW_SteerList");
		}
		return page;
		
		
	}
	
	public List<Portion> checkPortionStatus(List<Portion> portion) {
		ArrayList<Portion> portionList = new ArrayList<Portion>();
		if (portion != null) {
			for (Portion portin : portion) {
				if (portin.getStatus() != null) {
					if (portin.getStatus().equals(CustomerUtils.ACTIVE())) {
						portionList.add(portin);
						//LOG.info(" PortionStatus : " + portin.getStatus());
					}
				}

			}
		}

		return portionList;
	}
	
	private DeviceService getDeviceService() {
		if (_deviceService == null) {
			_deviceService = Application.context.getBean(DeviceService.class);
		}
		return _deviceService;
	}	
	
	private BeaconDeviceService getBeaconDeviceService() {
		if (_beacondeviceService == null) {
			_beacondeviceService = Application.context.getBean(BeaconDeviceService.class);
		}
		return _beacondeviceService;
	}
	
	private ClientDeviceService getClientDeviceService() {
		if (_clientDeviceService == null) {
			_clientDeviceService = Application.context.getBean(ClientDeviceService.class);
		}
		return _clientDeviceService;
	}	
	
	private boolean geoUsers(String customerId) {
		Customer customer = customerService.findById(customerId);
		if (customer.getSolution().equals("GeoLocation")) {
			return true;
		}
		return false;
	}
}