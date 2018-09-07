package com.semaifour.facesix.web;


import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.account.Privilege;
import com.semaifour.facesix.account.PrivilegeService;
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
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.rest.NetworkConfRestController;
import com.semaifour.facesix.rest.NetworkDeviceRestController;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;


/**
 * 
 * Site Controller for the webapp - responsible for managing sites locations
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/web/site")
public class SiteWebController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(SiteWebController.class.getName());
			
	@Autowired
	SiteService service;
	
	@Autowired
	HttpServletResponse response;
	
	@Autowired
	NetworkConfRestController networkcntrl;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	PrivilegeService  privilegeService;
	
	@Autowired
	DeviceService  	_deviceService;
	
	
	@Autowired
	SitePortionWebController  sitePortionWebController;
	
	@Autowired
	PortionService  portionService;
	
	@Autowired
	ClientDeviceService  _clientDeviceService;
		
	@Autowired
	NetworkDeviceRestController  networkDeviceRestController;
	
	@Autowired
	CustomerUtils CustomerUtils;
	
	@Autowired
	BeaconService 	beaconService;
	
	@Autowired
	BeaconDeviceService beaconDeviceService;
	
	@Autowired
	GeoFinderLayoutDataService geoFinderLayoutDataService;
	
	/**
	 * 
	 * Lists all Sites
	 * 
	 * @param model
	 * @return
	 */
	
	public String resolveSiteCustomer(String cid, HttpServletRequest request, HttpServletResponse response) {
		if (!sessionCache.equals(request.getSession(), "cid", cid)) {
			Customer customer = customerService.findById(cid);
			if (customer != null) {
				sessionCache.setAttribute(request.getSession(), "cid", customer.getId());
				SessionUtil.setCurrentCustomer(request.getSession(), customer.getId());
				SessionUtil.setCurrentSiteCustomerId(request.getSession(), cid);
				
			} else {
				return null;
			}
		}
		return cid;
	}
	
	@RequestMapping("/listAll")
	public String listAll(Map<String, Object> model) {
		Iterable<Site> fsobjects = service.findAll();
		model.put("fsobjects", fsobjects);
		return _CCC.pages.getPage("facesix.iot.site.list", "site-list");
	}
	
	@RequestMapping("/list")
	public String list( Map<String, Object> model,
						@RequestParam(value = "cid", required = false) String  cid,
						@RequestParam(value = "sid", required = false) String  sid,
						@RequestParam(value = "spid", required = false) String spid,
						HttpServletRequest request, 
						HttpServletResponse response) throws IOException {
		
		sessionCache.clearAttribute(request.getSession(), "sid", "suid", "spid", "spuid", "cid");
		
		resolveSiteCustomer(cid, request, response);
		List<Site> fsobjects = null;
		fsobjects = service.findByCustomerId(cid);
		
		if (fsobjects != null && fsobjects.size() > 0) {
			Site site    = fsobjects.get(0);
			sid 		 = site.getId();
		}
		
		super.prepare(model, request, response);
		model.put("fsobjects", fsobjects);
		
		if (cid.isEmpty() || cid.equals("")) {
			cid  = SessionUtil.getCurrentCustomer (request.getSession());
			String str = "/facesix/web/site/list?cid=" +cid+"&sid="+sid+"&spid="+spid;
			response.sendRedirect(str);
		} else {
			SessionUtil.setCurrentSiteCustomerId (request.getSession(), cid);
		}
		
		if (cid != null && !cid.contains("null")) {
			model.put("cid", cid);
		}
		
		model.put("sid", sid);
		model.put("spid", spid);
		
		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
		model.put("Retail", 	    CustomerUtils.isRetail(cid));
		
		return _CCC.pages.getPage("facesix.iot.site.list", "site-list");
	}
	
	@RequestMapping("/sitelist")
	public String sitelist(Map<String, Object> model, @RequestParam("sid") String sid, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		if (sid != null) {
			sitePortionWebController.resolveSite(sid, request, response);
		}
		
		Site site = null;
		site=service.findById(sid);
		String cid = "";
		if (site != null) {
			cid = site.getCustomerId();
			model.put("fsobjects", site);
			
			if (cid.isEmpty() || cid.equals("")) {
				cid  = SessionUtil.getCurrentCustomer(request.getSession());
			} else {
				SessionUtil.setCurrentSiteCustomerId(request.getSession(), cid);	
			}
					
			model.put("cid", cid);
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
			model.put("Retail", 	    CustomerUtils.isRetail(cid));
			
		}
		return this.list(model, cid, sid, null, request, response);
	}

	
	/**
	 * 
	 * Copies given Site to another
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/copy")
	public String copy(Map<String, Object> model, @RequestParam(value = "sid") String sid) {
		Site site = null;
		if (sid != null) {
			site = service.findById(sid);
			if (site == null) {
				model.put("message", Message.newError("Site not found for copy, please enter new site details"));
			} else {
				//Site = new Site();
				site.setId(null);
				site.setUid("Copy of " + site.getUid());
				site.setName("Copy of " + site.getName());
				model.put("message", Message.newInfo("Please correct copied details and save to persist."));
			}
		} else {
			model.put("message", Message.newError("No site to copy, please enter new site details"));
		}
		
		model.put("fsobject", site);

		return _CCC.pages.getPage("facesix.iot.site.edit", "site-edit");
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
			 		  @RequestParam(value = "sid", required = false) String sid,
					  @RequestParam(value = "cid", required = false) String cid,
					  HttpServletRequest request, HttpServletResponse response) {
		
		Site site = null;
		if (sid != null) {
			site = service.findById(sid);
		} else {
			model.put("message", Message.newInfo("Please enter new site details correctly"));
		}
		
		super.prepare(model, request, response);
		
		model.put("fsobject", site);
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}

		Customer customer = customerService.findById(cid);
		model.put("customerId", customer.getId());
		model.put("customerName", customer.getCustomerName());
		model.put("cid", customer.getId());
		
		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
		
		model.put("Site", TAB_HIGHLIGHTER);

		boolean flag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);

		ArrayList<Customer> List = new ArrayList<Customer>();
		if (flag) {
			Iterable<Customer> custList = customerService.findAll();
			for (Customer cust : custList) {
				if (cust.getStatus().equals(CustomerUtils.ACTIVE())) {
					List.add(cust);

				}
			}
			model.put("custList", List);
			model.put("list", true);
		} else {
			Iterable<Customer> custList = customerService.findOneById(cid);
			for (Customer cust : custList) {
				if (cust.getStatus().equals(CustomerUtils.ACTIVE())) {
					List.add(cust);

				}
			}
			model.put("custList", List);
			model.put("find", true);
		}

		if (!cid.isEmpty()) {
			SessionUtil.setCurrentSiteCustomerId(request.getSession(), cid);
		}
		
		

		return _CCC.pages.getPage("facesix.iot.site.edit", "site-edit");
	}
	
	/**
	 * Deletes the Id
	 * 
	 * @param model
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/delete")
	public String delete(Map<String, Object> model, @RequestParam(value = "sid", required = true) String sid, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {

			Site Site = service.findById(sid);
			List<ClientDevice> clientDeviceList= null;
			List<Device> deviceList = null;
			List<Portion> portionList = null;
			List<Beacon> beacon = null;
			List<BeaconDevice> beaconDevice=null;
			List<GeoFinderLayoutData> geofinder = null;
			
			if (Site != null) {
					
				beacon = beaconService.getSavedBeaconBySid(sid);
				if (beacon != null) {
					for (Beacon b : beacon) {
						beaconService.delete(b);
					}
				}

				beaconDevice = beaconDeviceService.findBySid(sid);
				if (beaconDevice != null) {
					for (BeaconDevice beaconDv : beaconDevice) {
						LOG.info("BeaconDevice UId " +beaconDv.getUid());
						beaconDeviceService.delete(beaconDv);
					}
				}

				clientDeviceList =getClientDeviceService().findBySid(Site.getId());
				if (clientDeviceList != null) {
					for (ClientDevice clientDevice : clientDeviceList) {
						getClientDeviceService().delete(clientDevice);
					}
					
				}
				
				deviceList = getDeviceService().findBySid(Site.getId());
				if (deviceList != null) {
					for (Device device : deviceList) {
						LOG.info(" Device UId " +device.getUid());
							getDeviceService().delete(device); // DEVICES
						}
					}

				networkcntrl.deletesid(Site.getId()); //NETWORKDEVICES
				
				portionList = portionService.findBySiteId(Site.getId()); // PORTION
				if (portionList != null) {
					for (Portion portion : portionList) {
						LOG.info("pid " + portion.getUid());
						portionService.delete(portion);
					}
				}

				LOG.info("sid " +Site.getId());
				service.delete(Site.getId());// SITE	
				
				geofinder = geoFinderLayoutDataService.findBySid(sid); //JNI GEOPOINTS DELETE OPTIONS
				if (geofinder != null) {
					for (GeoFinderLayoutData data : geofinder) {
						geoFinderLayoutDataService.delete(data);
					}
				}

				model.put("message", Message.newError("Site deleted successfully"));
			
			
				model.put("Site", TAB_HIGHLIGHTER);
				String cid=SessionUtil.getCurrentSiteCustomerId(request.getSession());
				String str = "/facesix/web/site/list?cid=" + cid;
				response.sendRedirect(str);
			}
		} catch (Exception e) {
			LOG.error("Error delete site", e);
		}
		return this.list(model, SessionUtil.getCurrentSiteCustomerId(request.getSession()), sid, null, request, response);
	}

	/**
	 * Saves Sites
	 * 
	 * @param model
	 * @param newfso
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Map<String, Object> model, @ModelAttribute Site newfso, 
					   @RequestParam(value = "sid", required = false) String sid,
					   HttpServletRequest request) throws IOException {
		boolean shouldSave = true;
		model.put("time", new Date());
		
		String cid=SessionUtil.getCurrentSiteCustomerId(request.getSession());
		try {
			Site site = service.findById(sid);
			//response.sendRedirect("/facesix/qubercloud/welcome");
			if (site == null) {
				newfso.setCreatedOn(new Date());
				newfso.setModifiedOn(new Date());
				newfso.setCreatedBy(SessionUtil.currentUser(request.getSession()));
				newfso.setModifiedBy(newfso.getCreatedBy());
				newfso.setCustomerId(SessionUtil.getCurrentSiteCustomerId(request.getSession()));
				newfso.setStatus(CustomerUtils.ACTIVE());
				newfso.setLatitude(newfso.getLatitude());
				newfso.setLongitude(newfso.getLongitude());
				if (StringUtils.isEmpty(newfso.getUid()) || StringUtils.isEmpty(newfso.getName())) {
					model.put("message", Message.newError("Title or Address can not be blank."));
					shouldSave = false;
				}
			} else {
				//it's existing
				site.setCustomerId(SessionUtil.getCurrentSiteCustomerId(request.getSession()));
				site.setLatitude(newfso.getLatitude());
				site.setLongitude(newfso.getLongitude());
				site.setName(newfso.getName());
				site.update(newfso);
				site.setModifiedOn(new Date());
				site.setModifiedBy(SessionUtil.currentUser(request.getSession()));
				site.setStatus(CustomerUtils.ACTIVE());
				newfso = site;
			}
			
			if (shouldSave) {
				newfso = service.save(newfso);
				model.put("disabled", "disabled");
				model.put("message", Message.newSuccess("Site saved successfully"));
			}
			String str = "/facesix/web/site/list?cid=" + cid;
			response.sendRedirect(str); //	duplicate save avoid using page redirection
			
		} catch (Exception e) {
			LOG.error("Error saving site", e);
		}
		return this.list(model, cid, sid, null, request, response);
		
	}
	
	
	
	
	@RequestMapping("/dashboard")
	public String dashboard(Map<String, Object> model, @RequestParam("sid")String sid, HttpServletRequest request, HttpServletResponse response) {
		SessionUtil.setCurrentSite(request.getSession(), sid);
		super.prepare(model, request, response);
		return _CCC.pages.getPage("facesix.iot.site.dashboard", "site-dashboard");
	}
	
	@RequestMapping("/venue")
	public String venue(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		List<Site> fsobjects= service.findByCustomerId(SessionUtil.getCurrentSiteCustomerId(request.getSession()));
		fsobjects=checkSiteStatus(fsobjects);
		model.put("fsobjects", fsobjects);
		sessionCache.clearAttribute(request.getSession(), "sid", "suid", "spid", "spuid");
		return _CCC.pages.getPage("facesix.iot.site.venue", "site-venue");
	}	
	
	public List<Site> checkSiteStatus(List<Site> fsobjects) {
		ArrayList<Site> siteList = new ArrayList<Site>();
		if (fsobjects != null) {
			for (Site site : fsobjects) {
				if (site.getStatus() != null) {
					if (site.getStatus().equals(CustomerUtils.ACTIVE())) {
						siteList.add(site);
					}
				}

			}
		}

		return siteList;
	}

	private DeviceService getDeviceService() {
		if (_deviceService == null) {
			_deviceService = Application.context.getBean(DeviceService.class);
		}
		return _deviceService;
	}	
	
	private ClientDeviceService getClientDeviceService() {
		if (_clientDeviceService == null) {
			_clientDeviceService = Application.context.getBean(ClientDeviceService.class);
		}
		return _clientDeviceService;
	}	

	
}
