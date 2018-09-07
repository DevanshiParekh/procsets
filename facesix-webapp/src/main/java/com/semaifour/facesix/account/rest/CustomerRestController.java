package com.semaifour.facesix.account.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.account.Privilege;
import com.semaifour.facesix.account.PrivilegeService;
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconAlertData;
import com.semaifour.facesix.beacon.data.BeaconAlertDataService;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.beacon.finder.geo.GeoFinderLayoutData;
import com.semaifour.facesix.beacon.finder.geo.GeoFinderLayoutDataService;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.captive.portal.CaptivePortal;
import com.semaifour.facesix.data.captive.portal.CaptivePortalService;
import com.semaifour.facesix.data.captive.portal.Casting;
import com.semaifour.facesix.data.captive.portal.CastingService;
import com.semaifour.facesix.data.captive.portal.PortalUsers;
import com.semaifour.facesix.data.captive.portal.PortalUsersService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.ClientDevice;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.gustpass.Gustpass;
import com.semaifour.facesix.gustpass.GustpassService;
import com.semaifour.facesix.rest.DeviceRestController;
import com.semaifour.facesix.rest.NetworkConfRestController;
import com.semaifour.facesix.rest.NetworkDeviceRestController;
import com.semaifour.facesix.schedule.CustomerScheduledTask;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.web.SitePortionWebController;
import com.semaifour.facesix.web.SiteWebController;
import com.semaifour.facesix.web.WebController;

import net.sf.json.JSONObject;

@RestController
@RequestMapping("/rest/customer")
public class CustomerRestController extends WebController {

	Logger LOG = LoggerFactory.getLogger(CustomerRestController.class.getName());

	@Autowired
	CustomerService customerService;

	@Autowired
	UserAccountService userAccountService;

	@Autowired
	SiteService siteService;

	@Autowired
	PortionService portionService;

	@Autowired
	PrivilegeService privilegeService;

	@Autowired
	NetworkConfRestController networkcntrl;

	@Autowired
	NetworkDeviceService networkDeviceService;

	@Autowired
	SiteWebController siteWebController;

	@Autowired
	SitePortionWebController sitePortionWebController;

	@Autowired
	NetworkDeviceRestController networkDeviceRestController;

	@Autowired
	private CustomerScheduledTask customerScheduledTask;
	
	@Autowired
	GustpassService gustpassService;
	
	@Autowired
	DeviceService devService;
	
	@Autowired
	DeviceRestController deviceRestController;
	
	@Autowired
	ClientDeviceService 	clientDeviceService;
	
	@Autowired
	BeaconService 	beaconService;
	
	@Autowired
	BeaconDeviceService beaconDeviceService;
	
	@Autowired
	GeoFinderLayoutDataService geoFinderLayoutDataService;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	CaptivePortalService captivePortalService;
	
	@Autowired
	CastingService castingService;
	
	@Autowired
	PortalUsersService portalUsersService;
	
	@Autowired
	BeaconAlertDataService beaconAlertDataService;
	
	@Value("${facesix.cloud.version}")
	private String cloud_version;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat advanceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody JSONObject listData(HttpServletRequest request, HttpServletResponse response) {

		JSONObject jsonList = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		if (SessionUtil.isAuthorized(request.getSession())) {
		
			try {

				JSONObject json 	  = null;
				String serviceStrDate = "";
				String serviceExpDate = "";
				String id 			  = "" ;
				String role 		  = "" ;
				
				Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			
				if (model != null && model.containsKey("id")) {
					id   = (String) model.get("id");
					role = (String) model.get("role");
				}
				
				boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);

				Iterable<Customer> customerList = null;
				List<Site> siteList 			= null;
				
				if (privFlag && role.equals("superadmin")) {
					customerList = customerService.findAll();
				} else {
					customerList = customerService.findOneById(id);
				}
				
				for (Customer customer : customerList) {
					json = new JSONObject();
							String cid = customer.getId();
							json.put("id", 						cid);
							json.put("customerName", 			customer.getCustomerName());
							json.put("country", 				customer.getCountry());
							json.put("venueType", 				customer.getVenueType());
							json.put("address", 				customer.getAddress());
							json.put("city", 					customer.getCity());
							json.put("state",					customer.getState());
							json.put("postalCode", 				customer.getPostalCode());
							json.put("offerPackage",			customer.getOfferPackage());
							json.put("noOfGateway", 			customer.getNoOfGateway());
							json.put("preferedUrlName", 		customer.getPreferedUrlName());
							if (customer.getServiceStartDate() != null)
								serviceStrDate = advanceDateFormat.format(customer.getServiceStartDate());
							json.put("serviceStartDate", 		serviceStrDate);
							if (customer.getServiceExpiryDate() != null)
								serviceExpDate = advanceDateFormat.format(customer.getServiceExpiryDate());
							json.put("serviceExpiryDate", 		serviceExpDate);
							json.put("serviceDurationinMonths", customer.getServiceDurationinMonths());
							json.put("contactPerson", 			customer.getContactPerson());
							json.put("contactPersonlname", 		customer.getContactPersonlname());
							json.put("designation", 			customer.getDesignation());
							json.put("contactNumber", 			customer.getContactNumber());
							json.put("mobileNumber", 			customer.getMobileNumber());
							json.put("qubercommAssist", 		customer.getQubercommAssist());
							json.put("email", 					customer.getEmail());
							json.put("status", 					customer.getStatus());
							json.put("alexacerfilepath", 		customer.getAlexacerfilepath());
							json.put("alexakeyfilepath", 		customer.getAlexakeyfilepath());
							json.put("alexaendpoint", 	 		customer.getAlexaendpoint());
							json.put("alexatopic", 		 		customer.getAlexatopic());
							json.put("tagcount", 			 	customer.getTagcount());
							json.put("threshold", 		 		customer.getThreshold());
							json.put("timezone", 		 		customer.getTimezone());
							json.put("bleserverip", 		 	customer.getBleserverip());
							json.put("logs", 		 	        customer.getLogs());
							json.put("tagInact", 		 		customer.getTagInact());
							json.put("logofile", 		 	    customer.getLogofile());
							json.put("background", 		 	    customer.getBackground());
							json.put("discover_text", 		 	customer.getDiscover_link());
							json.put("discover_link", 		 	customer.getDiscover_link());
							json.put("facebook", 		 	    customer.getFacebook());
							json.put("twitter", 		 	    customer.getTwitter());
							json.put("linkedin", 		 	    customer.getLinkedin());
							json.put("mqttToken", 		 	    customer.getMqttToken());
							json.put("restToken", 			    customer.getRestToken());
							json.put("jwtmqttToken", 		    customer.getJwtmqttToken());
							json.put("jwtrestToken", 			customer.getJwtrestToken());
							json.put("oauth", 					customer.getOauth());
							json.put("userAccId", 		 		customer.getUserAccId());
							
							String solution = customer.getSolution();
							
							json.put("solution",				solution);
							json.put("inactivityMail", 			customer.getInactivityMail());
							json.put("inactivitySMS", 			customer.getInactivitySMS());
							json.put("vpn", 		 	    	customer.getVpn());
							
							if (solution.equals("Retail")) {
								
								json.put("spotsIcon",  "fa fa-cog");
								json.put("spots", 	    "/facesix/spots?cid="+cid);
								
								json.put("retail_icon", 		"fa fa-home");
								json.put("retail_text", 		"HOME");
								json.put("retail_link", 		"/facesix/mesh-topology?cid="+cid);
								
							} else {
								
								json.put("spotsIcon", 				"fa fa-mixcloud");
								json.put("spots", 					"/facesix/spots?cid="+cid);
								json.put("findIcon", 				"fa fa-bluetooth");
								json.put("find", 					"/facesix/web/finder/device/list?cid="+cid);	
								siteList = siteService.findByCustomerId(cid);
								
								if (siteList != null && siteList.size() > 0) {
									json.put("totalVenue", 	siteList.size());
									json.put("icon", 		"fa fa-map-marker");
									json.put("text", 		"Venue List");
									json.put("link", 		"/facesix/web/site/list?cid=" + cid);
								} else {
									json.put("icon", 		"fa fa-plus");
									json.put("text", 		"Venue Config");
									json.put("link", 		"/facesix/web/site/list?cid=" + cid);
								}
							}
							
							
							jsonArray.add(json);
					}
				
				jsonList.put("customer", jsonArray);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Customer details getting error ", e);
			}
		}
	
		return jsonList;
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public @ResponseBody List<Customer> get(@RequestParam(value = "id", required = false) String id) {
		return customerService.findOneById(id);
	}
	
	@RequestMapping(value = "/listAll", method = RequestMethod.GET)
	public @ResponseBody Iterable<Customer> get() {
		return customerService.findAll();
	}
	

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(@RequestBody Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {

		String customerId = "" + param.get("customerId");
		LOG.info("param delete CustomerId " + customerId);

		List<UserAccount> account 			= null;
		List<Site> siteList 				= null;
		Iterable<Gustpass> guestpassList	= null;
		List<Device> device 				= null;
		List<NetworkDevice> networkDevice	= null;
		List<Portion> portion 				= null;
		List<ClientDevice> client 			= null;
		List<Beacon> beacon 				= null;
		List<BeaconDevice> beaconDevice		= null;
		List<GeoFinderLayoutData> geofinder = null;
		List<CaptivePortal> captivePortal   = null;
		Iterable<Casting> castingList 		= null;
		Iterable<PortalUsers> portalUsers   = null;
		List<BeaconAlertData> beaconAlert   = null;
		
		try {
			if (customerId != null) {
				
				device 			= getDeviceService().findByCid(customerId);
				networkDevice 	= networkDeviceService.findByCid(customerId);
				portion 		= portionService.findByCid(customerId);
				siteList 		= siteService.findByCustomerId(customerId);
				beacon 			= beaconService.getSavedBeaconByCid(customerId);
				beaconAlert 	= beaconAlertDataService.findByCid(customerId);
				
				
				if (beacon != null) {
					for (Beacon b : beacon) {
						beaconService.delete(b);
					}
				}
				
				 // customized alert for Tag
				if (beaconAlert != null) {
					for (BeaconAlertData beaconAlertData : beaconAlert) {
						beaconAlertDataService.delete(beaconAlertData);
					}
				}
				
				beaconDevice = beaconDeviceService.findByCid(customerId);
				if (beaconDevice != null) {
					for (BeaconDevice beaconDv : beaconDevice) {
						LOG.info("BeaconDevice UID " + beaconDv.getUid());
						beaconDeviceService.delete(beaconDv);
					}
				}

				String[] stringArray = new String[] { "none" };
				
				client = getClientDeviceService().findByCid(customerId);
				if (client != null) {
					for (ClientDevice c : client) {
						getClientDeviceService().delete(c);
					}
				}
				
				if (device != null) { //Device
					for (Device dev : device) {
						LOG.info("Device Delete " +dev.getUid());
						deviceRestController.rpc(dev.getUid(), null, null, "DELETE", stringArray);
						getDeviceService().delete(dev);
					}
				}
				
				for(NetworkDevice nd :networkDevice){ //NetworkDevice
					LOG.info("NetworkDevice UID " + nd.getUid());
					networkDeviceService.delete(nd);
				}
				
				if (portion != null) {
					for (Portion p : portion) { // Portion
						LOG.info("Portion Delete " + p.getUid());
						portionService.delete(p);
					}
				}
				
				if (siteList != null) { //Site
					for (Site site : siteList) {
						LOG.info("Site Delete " +site.getUid());
						siteService.delete(site);
					}
				}
				
				guestpassList = gustpassService.findByCustomerId(customerId); //GUESTPASS
				if (guestpassList != null) {
					for (Gustpass gustpass : guestpassList) {
							gustpassService.delete(gustpass);
					}
				}
				
				account = userAccountService.findByCustomerId(customerId);  // CUSTOMER ASSOCIATE USERS LOGIN DETAILS (USERACCOUNT)
				if (account != null) {
					for (UserAccount userAccount : account) {
						LOG.info("UserAccount Delete " +userAccount.getName());
						userAccountService.delete(userAccount);
					}
				}

				customerService.delete(customerId); // CUSTOMER

				geofinder = geoFinderLayoutDataService.findByCid(customerId); //JNI GEOPOINTS DELETE OPTIONS
				if (geofinder != null) {
					for (GeoFinderLayoutData data : geofinder) {
						geoFinderLayoutDataService.delete(data);
					}
				}

				captivePortal = captivePortalService.findByCid(customerId);
				if (captivePortal != null) {
					for (CaptivePortal data : captivePortal) {
						captivePortalService.delete(data);
					}
				}
				
				castingList = castingService.findByCid(customerId);
				if (castingList != null) {
					for (Casting data : castingList) {
						castingService.delete(data);
					}
				}
				portalUsers  = portalUsersService.findByCid(customerId);
				if (portalUsers != null) {
					for (PortalUsers data : portalUsers) {
						portalUsersService.delete(data);
					}
				}
				
				String rootPath = _CCC.properties.getProperty("facesix.fileio.binary.root", "/var/www/html");
				String filePath = rootPath+"/"+"casting_"+customerId;
				
				File file = new File(filePath);
				deleteDirectory(file);
				
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void deleteDirectory(File file) {
		boolean success = false;
		if (file.isDirectory()) {
			for (File deleteMe : file.listFiles()) {
				deleteDirectory(deleteMe);
			}
		}
		success = file.delete();
		if (success) {
			System.out.println("Folder Deleted sucess");
		} else {
			System.out.println("Folder  Deletion failed!!!");
		}
	}
	
	@RequestMapping(value = "/upload" , method = RequestMethod.POST)
	public void upload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			String id = request.getParameter("cid");
			Customer customer = customerService.findById(id);

			LOG.info(" cid " + id);

			if (customer != null) {

				try {

					String cid 		= customer.getId();
					String rootPath = _CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_");

					MultipartHttpServletRequest mRequest;
					mRequest = (MultipartHttpServletRequest) request;

					MultipartFile mycerfile  = mRequest.getFile("cert");
					MultipartFile mykeyfile  = mRequest.getFile("file");
					MultipartFile logo 		 = mRequest.getFile("logo");
					MultipartFile background = mRequest.getFile("background");
				
					if (mykeyfile != null && !mykeyfile.isEmpty() && mykeyfile.getSize() > 1) {

						Path path = Paths.get(rootPath, (cid + "_" + mykeyfile.getOriginalFilename()));
						Files.copy(mykeyfile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

						customer.setAlexakeyfilepath(path.toString());

						LOG.info(" key file name " + mykeyfile.getOriginalFilename());
						LOG.info(" key file path " + path);
					}

					if (mycerfile != null && !mycerfile.isEmpty() && mycerfile.getSize() > 1) {

						Path path = Paths.get(rootPath, (cid + "_" + mycerfile.getOriginalFilename()));
						Files.copy(mycerfile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

						customer.setAlexacerfilepath(path.toString());

						LOG.info(" cert file name " + mycerfile.getOriginalFilename());
						LOG.info(" cert file path " + path);
					}

					if (logo != null && !logo.isEmpty() && logo.getSize() > 1) {
						Path path = Paths.get(rootPath, (cid + "_" + logo.getOriginalFilename()));
						Files.copy(logo.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
						customer.setLogofile(path.toString());
						LOG.info(" LOGO file name " + logo.getOriginalFilename());
						LOG.info(" LOGO file path " + path);
					}
					if (background != null && !background.isEmpty() && background.getSize() > 1) {
						Path path = Paths.get(rootPath, (cid + "_" + background.getOriginalFilename()));
						Files.copy(background.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
						customer.setBackground(path.toString());
						LOG.info(" BG file name " + background.getOriginalFilename());
						LOG.info(" BG file path " + path);
					}
					
					customer.setModifiedBy(whoami(request, response));
					customer.setModifiedOn(now());
					customer = customerService.save(customer);
				} catch (Exception e) {
					LOG.error("while updating alexa file error " + e);
				}
			} else {
				LOG.info("Customer object null cid " + id);
			}
		} else{
			LOG.info(" Unauthorized user");
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Restponse<String> save(@RequestBody Customer customer, HttpServletRequest request, HttpServletResponse response) {

		String ifNewuser   = "Your account has been updated successfully.";
		boolean flag 	   = false;
		String id 		   = null;
		boolean custFlag   = false;

		try {
			//LOG.info("customer " +customer.toString());
			if (SessionUtil.isAuthorized(request.getSession())) {
				if (customer.getId() == null) {
					customer.setServiceStartDate(CustomerUtils.formatDate(customer.getServiceStartDate()));
					customer.setServiceExpiryDate(CustomerUtils.formatDate(customer.getServiceExpiryDate()));
					customer.setCreatedBy(whoami(request, response));
					customer.setCreatedOn(now());
					customer.setStatus(CustomerUtils.ACTIVE());
					customer.setBleserverip("0.0.0.0");
					customer.setBattery_threshold("40");
					customer.setPassword(_CCC.cryptor.encrypt(customer.getPassword()));
					ifNewuser = "Your account has been created successfully";
					custFlag = true;
				}
				
				String customer_name = customer.getCustomerName().trim().toUpperCase();
				String f_name 		 = customer.getContactPerson().toUpperCase().trim();
				String l_name 		 = customer.getContactPersonlname().toUpperCase().trim();
				String preferedUrl   = customer.getCustomerName().toLowerCase();
				preferedUrl			 = preferedUrl.replace("\\s+", "_");
				
				String inactivity_mail = customer.getInactivityMail();
				String inactivity_SMS = customer.getInactivitySMS();
				
				if (inactivity_mail == null || inactivity_mail.equals("false")) {
					customer.setInactivityMail("false");
				} else {
					customer.setInactivityMail("true");
				}
				
				if (inactivity_SMS == null || inactivity_SMS.equals("false")) {
					customer.setInactivitySMS("false");
				} else {
					customer.setInactivitySMS("true");
				}
				
				customer.setVersion(cloud_version);
				customer.setCustomerName(customer_name);
				customer.setContactPerson(f_name);
				customer.setContactPersonlname(l_name);
				customer.setPreferedUrlName(preferedUrl);
				customer.setServiceStartDate(CustomerUtils.formatDate(customer.getServiceStartDate()));
				customer.setServiceExpiryDate(CustomerUtils.formatDate(customer.getServiceExpiryDate()));
				customer.setModifiedBy(whoami(request, response));
				customer.setModifiedOn(now());
				
				long remainDays = CustomerUtils.getRemainglicenceDays(customer.getServiceExpiryDate());
			
				Map<String,String> params = new HashMap<String,String>();
				params.put("id", customer.getId());
				
				if (remainDays <= 0) {
					if (customer.getStatus().equals("ACTIVE")) {
						this.deactivatelicence(params, request, response);
						customer.setStatus("INACTIVE");
					}
				} else {
					customer.setStatus("ACTIVE");
				}
				
				customer.setJedittings(customer.getJedittings());
				customer = customerService.save(customer);
				
				UserAccount newAccount = null;
				String uid 			   = customer.getEmail().trim();
				newAccount 			   = userAccountService.findOneByUid(uid);
				
				if (newAccount == null) {
					newAccount = new UserAccount();
					newAccount.setUid(customer.getEmail());
					newAccount.setVersion(cloud_version);
					newAccount.setFname(f_name);
					newAccount.setLname(l_name);
					newAccount.setDesignation(customer.getDesignation());
					newAccount.setPhone(customer.getMobileNumber());
					newAccount.setEmail(customer.getEmail());
					newAccount.setCustomerId(customer.getId());
					newAccount.setPassword(customer.getPassword());
					newAccount.setRole("appadmin");
					newAccount.setIsMailalert("true");
					newAccount.setIsSmsalert("true");
					newAccount.setCreatedBy(whoami(request, response));
					newAccount.setCreatedOn(now());
					newAccount.setStatus(CustomerUtils.ACTIVE());
					newAccount = userAccountService.saveContact(newAccount);
					customer.setUserAccId(newAccount.getId());//
					customer = customerService.save(customer);
					LOG.info(" Customer UserAccId  " +customer.getUserAccId());
				} else {
					newAccount.setUid(customer.getEmail());
					newAccount.setVersion(cloud_version);
					newAccount.setFname(f_name);
					newAccount.setLname(l_name);
					newAccount.setDesignation(customer.getDesignation());
					newAccount.setPhone(customer.getMobileNumber());
					newAccount.setEmail(customer.getEmail());
					newAccount.setModifiedBy(whoami(request, response));
					newAccount.setModifiedOn(now());
					newAccount = userAccountService.saveContact(newAccount);
				}
				
				accountUpdationEmailAndSMSAlert(customer, custFlag, remainDays);
				
				flag = true;
				id   = customer.getId();	
			} else {
				ifNewuser = "Unauthorized User";
				flag = false;
			}

		} catch (Exception e) {
			ifNewuser = "Error occured while creating account.";
			flag = false;
			LOG.error("saving customer Error ", e);
		}

		return new Restponse<String>(flag, 200, ifNewuser,id);

	}

	private void accountUpdationEmailAndSMSAlert(Customer customer, boolean mailsmsFlag, long remainDays) {

		
		try {

			String default_url = customerUtils.cloudUrl() + "/facesix/";
			String preferred_url = "";
			String message       = "Hi "+customer.getCustomerName()+"\n\n";
			
			if (mailsmsFlag) {
				message += " Your account has been created successfully";
			} else {
				message += " Your account has been updated successfully";
			}
			
			if (customer.getPreferedUrlName() == null || customer.getVersion() == null) {
				preferred_url = default_url;
			} else {
				preferred_url = default_url + customer.getPreferedUrlName();
			}

			message += "It will expire after " + remainDays + " Days.\n " + "You can access your account by "
					+ "clicking the link below \n " + preferred_url + "\n Login id : " + customer.getEmail();

			if (customer.getOauth() != null && customer.getOauth().equals("true")) {

				if (customer.getRestToken() != null && !customer.getRestToken().isEmpty()) {
					message += "\n Rest Token : " + customer.getRestToken();
				}

				if (customer.getMqttToken() != null && !customer.getMqttToken().isEmpty()) {
					message += "\n MQTT Token : " + customer.getMqttToken();
				}
	
			}
			
			message += "\n\n";
			
			String subject = "PORTAL UPDATE INFORMATION";
			
			customerUtils.customizeSupportEmail(null, customer.getEmail(), subject, message, null);

			if (mailsmsFlag == false) {
				String cid = customer.getId();
				List<UserAccount> useraccounts = userAccountService.findByCustomerId(cid);
				for (UserAccount user : useraccounts) {
					if (!user.getEmail().equals(customer.getEmail())) {
						message = "Hi "+user.getFname()+",\n\n Your Portal has been updated.\n Now you can Login by clicking the link below :\n "
								+ preferred_url + "\n Your Login Id is : " + user.getEmail();
						customerUtils.customizeSupportEmail(cid,user.getEmail(),subject,message,null);
						user.setVersion(customer.getVersion());
						user = userAccountService.saveContact(user);
					}
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/licence", method = RequestMethod.GET)
	public @ResponseBody JSONObject licence(HttpServletRequest request) {

		JSONObject jsonObject = new JSONObject();
		try {
			
			JSONArray jsonArray = new JSONArray();
			JSONObject json 	= null;
			long remainDays 	= 0;
			String id 			= "";
			
			Iterable<Customer> customerList = new ArrayList<Customer>();

			Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			

			if (model != null) {
				if (model.get("id") != null) {
					id = model.get("id").toString();
				}
			}
			boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);

			if (privFlag) {
				customerList = customerService.findAll();
			} else {
				customerList = customerService.findOneById(id);
			}

			for (Customer cust : customerList) {
				json = new JSONObject();
				if (cust.getStatus() != null) {
					if (cust.getStatus().equals(CustomerUtils.ACTIVE())) {
						json.put("cid", 		cust.getId());
						json.put("licenceFor",  cust.getCustomerName());
						json.put("type", 		cust.getVenueType());
						if (cust.getServiceStartDate() != null && cust.getServiceExpiryDate() != null) {
							json.put("purchasedOn", simpleDateFormat.format(cust.getServiceStartDate()));
							json.put("expriesOn", 	simpleDateFormat.format(cust.getServiceExpiryDate()));
							remainDays = CustomerUtils.getRemainglicenceDays(cust.getServiceExpiryDate());
							json.put("timeLeft", 	remainDays);
						}
						jsonArray.add(json);

					}

				}

			}
			jsonObject.put("licence", jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/customerList", method = RequestMethod.GET)
	public JSONObject get(HttpServletRequest request) {

		JSONObject json 				= null;
		JSONArray jsonArray 			= new JSONArray();
		JSONObject jsonList 			= new JSONObject();
		Iterable<Customer> customerList = new ArrayList<Customer>();

		try {
			
			Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			String id = "" ;
			
			if (model != null) {
				if (model.get("id") != null) {
					id = model.get("id").toString();
				}
			}
			
			boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);
			if (privFlag) {
				customerList = customerService.findAll();
			} else {
				customerList = customerService.findOneById(id);
			}

			if (customerList != null) {
				for (Customer cust : customerList) {
					json = new JSONObject();
					if (cust.getStatus().equals(CustomerUtils.ACTIVE())) {
						json.put("id", cust.getId());
						json.put("custName", cust.getCustomerName());
						jsonArray.add(json);
					}
				}
				jsonList.put("customer", jsonArray);
			}
		}catch(Exception e) {}
		return jsonList;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/inactive", method = RequestMethod.GET)
	public @ResponseBody JSONObject inactive(HttpServletRequest request, HttpServletResponse response) {

		JSONObject jsonList = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {

			JSONObject json 	  = null;
			String serviceStrDate = "";
			String serviceExpDate = "";
			String id 			  = "";
			
			Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			
			
			if (model != null) {
				if (model.get("id") != null) {
					id = model.get("id").toString();
				}
			}
			
			boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);

			Iterable<Customer> customerList = null;
			if (privFlag) {
				customerList = customerService.findAll();
			} else {
				customerList = customerService.findOneById(id);
			}
			if(customerList!=null){
				for (Customer customer : customerList) {
					if (customer.getStatus() != null) {
						if (customer.getStatus().equals(CustomerUtils.INACTIVE())) {
							json = new JSONObject();
							json.put("id", customer.getId());
							json.put("customerName", customer.getCustomerName());
							json.put("venueType", customer.getVenueType());
							json.put("city", customer.getCity());
							json.put("state", customer.getState());
							json.put("offerPackage", customer.getOfferPackage());
							json.put("solution", customer.getSolution());
							json.put("noOfGateway", customer.getNoOfGateway());
							if (customer.getServiceStartDate() != null)
								serviceStrDate = simpleDateFormat.format(customer.getServiceStartDate());
							json.put("serviceStartDate", serviceStrDate);
							if (customer.getServiceExpiryDate() != null)
								serviceExpDate = simpleDateFormat.format(customer.getServiceExpiryDate());
							json.put("serviceExpiryDate", serviceExpDate);
							json.put("serviceDurationinMonths", customer.getServiceDurationinMonths());
							json.put("mobileNumber", customer.getMobileNumber());
							json.put("email", customer.getEmail());
							json.put("status", customer.getStatus());
							jsonArray.add(json);
						}
					}
				}
				jsonList.put("inactivecustomer", jsonArray);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Get Inactive Customer error ", e);
		}
		return jsonList;
	}

	@RequestMapping(value = "/deactivate", method = RequestMethod.POST)
	public Restponse<String> deactivatelicence(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
		
		String usermessage = null;
		String custmessage = null;
		boolean flag  	   = false;
		
		try {
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				String customerId  = params.get("id");
				
				if (customerId != null) {
					Customer customer = customerService.findById(customerId);
					if (customer != null) {
						
						 usermessage = "Your Portal has been deactivated. To get more information contact admin.\n ";
						 custmessage = "Your Portal has been deactivated. To get more information contact our "
										   + "sales team or mail us at support@qubercomm.com.\n ";
						
						this.changeState(customerId, customerUtils.INACTIVE(), custmessage, usermessage);
						flag = true;
					}	
				}
			}
		} catch (Exception e) {
			LOG.info("Deactivate Customer Error{} ", e);
		}
		
		return new Restponse<String>(flag, 200, usermessage);
	}

	@RequestMapping(value = "/active", method = RequestMethod.POST)
	public Restponse<String> active(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {

		String userMessage = null;
		String custMessage = null;
		boolean flag   = false;
	
		try {
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				String customerId   = params.get("customerId");

				if (customerId != null) {
					Customer customer = customerService.findById(customerId);
					if(customer != null){
						
						long remainDays = CustomerUtils.getRemainglicenceDays(customer.getServiceExpiryDate());
						
						custMessage = "Your Portal has been Activated. Your portal will expire after "+remainDays+" days.";
						
						userMessage = "Your account has been activated successfully. Click the below link to login to your account\n";
						
						this.changeState(customerId, customerUtils.ACTIVE(), custMessage, userMessage);
						
						flag = true;
					}
				}
			}

		} catch (Exception e) {
			LOG.info("activating Customer Error{} ", e);
		}
		
		return new Restponse<String>(flag, 200, userMessage);
	}

	@RequestMapping(value = "/supportlist", method = RequestMethod.GET)
	public @ResponseBody JSONObject support(HttpServletRequest request, HttpServletResponse response) {

		JSONObject jsonList = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {

			JSONObject json = null;
			String id 		= "" ;
			
			Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			
			if (model != null) {
				if (model.get("id") != null) {
					id = model.get("id").toString();
				}
			}

			boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);

			Iterable<Customer> customerList = null;
			if (privFlag) {
				customerList = customerService.findAll();
			} else {
				customerList = customerService.findOneById(id);
			}
			for (Customer customer : customerList) {
				json = new JSONObject();
				if (customer != null) {
					if (customer.getStatus() != null) {
						if(customer.getStatus().equals(CustomerUtils.ACTIVE())){
							json.put("id", customer.getId());
							json.put("customerName", customer.getCustomerName());
							json.put("qubercommAssist", customer.getQubercommAssist());
							jsonArray.add(json);
						}
						
					}
				}
				}
			
			jsonList.put("support", jsonArray);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Customer Support getting error ", e);
		}
		return jsonList;
	}
	
	@RequestMapping(value = "/support", method = RequestMethod.POST)
	public @ResponseBody Restponse<Customer> support(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
		try {
			Customer customer = customerService.findById(params.get("id"));
			if (customer != null) {
				customer.setQubercommAssist(params.get("flag"));
				customer.setModifiedBy(whoami(request, response));
				customer.setModifiedOn(now());
				customerService.save(customer);
				return new Restponse<Customer>(customer);
			}
		} catch (Exception e) {
			LOG.error("eror updating support flag for {} ", params, e);
		}
		return new Restponse<Customer>(false, 402);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/timeZone", method = RequestMethod.GET)
	public @ResponseBody JSONObject timeZone() {

		TimeZone timeZone 			= null;
		JSONObject timezoneObject 	= null;
		JSONArray array 			= new JSONArray();
		JSONObject jsonList 		= new JSONObject();

		String time[] 	= TimeZone.getAvailableIDs();
		String zonename = "";
		
		for (String str : time) {
			timeZone 		= TimeZone.getTimeZone(str);
			zonename 		= timeZone.getDisplayName();
			timezoneObject  = new JSONObject();
			
			timezoneObject.put("zoneId", str);
			timezoneObject.put("zoneName", zonename);
			array.add(timezoneObject);
		}
		jsonList.put("timezone", array);
		return jsonList;
	}
		
	@RequestMapping(value = "/cloudlog", method = RequestMethod.POST)
	public @ResponseBody JSONObject cloudlog(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObject = null;
		try {
			Customer customer = customerService.findById(params.get("id"));
			if (customer != null) {
				String logState = params.get("logState");
				//LOG.info("Customer " + customer.getCustomerName()+"  LOG state "+logState);
				customer.setLogs(logState);
				customer = customerService.save(customer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("eror updating support flag for {} ", params, e);
		}
		return jsonObject;
	}

	@RequestMapping(value = "/vpn", method = RequestMethod.POST)
	public @ResponseBody Customer openVpn(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
		Customer customer = null;
		try {
			customer = customerService.findById(params.get("id"));
			if (customer != null) {
				String vpnState = params.get("vpnState");
				//LOG.info("Customer " + customer.getCustomerName()+"  VPN "+vpnState);
				customer.setVpn(vpnState);
				customer = customerService.save(customer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("eror updating vpn flag for {} ", params, e);
		}
		return customer;
	}
	
	
	@RequestMapping(value = "/paramValue" , method = RequestMethod.GET)
	public JSONObject paramValue(
			@RequestParam(value = "cid" ,required = true)String cid ,
			HttpServletRequest request) throws IOException{
		
		JSONObject jsonObject = new JSONObject();
		if (SessionUtil.isAuthorized(request.getSession())) {
			if (cid != null && !cid.equals("undefined")) {
				Customer cx = customerService.findById(cid);
				
				if (cx != null) {
					jsonObject.put("customerName", cx.getCustomerName());
					String pref_url = cx.getPreferedUrlName() == null ? "" : cx.getPreferedUrlName();
					String logofile = cx.getLogofile() == null ? "" : cx.getLogofile();
					jsonObject.put("pref_url", pref_url);
					jsonObject.put("logofile", logofile);
				} else {
					jsonObject.put("customerName", "");
					jsonObject.put("pref_url", "");
					jsonObject.put("logofile", "");
				}
			}

		}
		return jsonObject;
	}
	
	
	@RequestMapping(value = "/duplicateCustomerName", method = RequestMethod.GET)
	public Restponse<String> byCustomerName(@RequestParam(value="customerName" ,required=true) String name, 
											HttpServletRequest request) throws Exception {
		
		String message 	= "new";
		boolean flag 	= false;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			name = name.trim().toUpperCase();
			List<Customer> customer = null;
			customer 				= customerService.findByCustomerName(name);
		
			if (customer != null && customer.size() > 0) {
				Customer cust 		= customer.get(0);
				String customerName = cust.getCustomerName();
				if (customerName.equalsIgnoreCase(name.trim())) {
						message = "duplicate";
				}
			}

			flag = true;
			//LOG.info(" Message " +message +" name  " +name);
			
			return new Restponse<String>(flag, 200, message);
		} else {
			message = "duplicate";
			flag 	= false;
			return new Restponse<String>(flag, 200, message);
		}
	}
	
	@RequestMapping(value = "/updateInactivityInfo", method = RequestMethod.POST)
	public Restponse<String> inactivityInfo(@RequestBody JSONObject json,HttpServletRequest request, HttpServletResponse response) {
		
		String message 	= "failure";
		boolean flag 	= true;
		LOG.info(" json "+json);
		
		if (SessionUtil.isAuthorized(request.getSession())) {
	
		try {
		
			String cid  			    = json.getString("cid");
			String battery_threshold    = json.getString("battery_threshold");
			String inactivityMail 		= json.getString("inactivityMail");
			String inactivity_duration  = json.getString("default_inactivity_time");
			
			String devInacEmailSMS 	   	= json.getString("inactivitydevMail");
			String finderDevInacTime 	= json.getString("default_dev_inactivity_time");
			
			if (cid != null && finderDevInacTime != null) {
				finderDevUpdateKeepAlive(cid, finderDevInacTime);
			}
			
			//LOG.info(" Finder  devInacEmailSMS " +devInacEmailSMS + " finderDevInacTime  " +finderDevInacTime);
			List<UserAccount> users = userAccountService.findByUid(SessionUtil.currentUser(request.getSession()));
			if (users != null && users.size() > 0) {
				UserAccount user = users.get(0);
				String finderEmailSms = devInacEmailSMS == null ? "false" : devInacEmailSMS;
				user.setCustomizeEmailSms(finderEmailSms);
				user= userAccountService.saveContact(user);
				LOG.info("user " +user.getCustomizeEmailSms());
			}
			
			Customer customer = customerService.findById(cid);
			if (customer != null) {
				customer.setInactivityMail(inactivityMail);
				customer.setBattery_threshold(battery_threshold);
				customer.setTagInact(inactivity_duration);
				customer.setCustomizeFinderDevInacTime(finderDevInacTime);
				customer.setModifiedBy(whoami(request, response));
				customer.setModifiedOn(now());
				customerService.save(customer);
				flag = true;
				message = "success";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		return new Restponse<String>(flag, 200, message);
	}
	

	public boolean finderDevUpdateKeepAlive(String cid,String devDefaultInac) {
		List<BeaconDevice> beaconDev = beaconDeviceService.findByCid(cid);
		if (beaconDev != null) {
			for (BeaconDevice Dev : beaconDev) {
				Dev.setKeepAliveInterval(devDefaultInac);
				Dev.setModifiedOn(now());
				Dev = beaconDeviceService.save(Dev, false);
				//LOG.info(" KeepAliveInterval " +Dev.getKeepAliveInterval());
			}
		}
		return true;
	}
	
	@RequestMapping(value = "/GW_UpdateInactivityDevInfo", method = RequestMethod.POST)
	public Restponse<String> gw_updateInactivityDevInfo(@RequestBody JSONObject json,HttpServletRequest request, HttpServletResponse response) {
		
		String message 	= "failure";
		boolean flag 	= true;
		int     code    = 500;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			LOG.info(" json "+json);
			
			try {
				
				String cid  			 = (String)json.get("cid");
				String devInacEmailSMS 	 = (String)json.getString("inactivitydevMail");
				String gatewyDevInacTime = json.getString("gatewyDevInacTime");
				JSONArray gwinacDevInfo  = (JSONArray) json.get("gwinacDevInfo");
				
				LOG.info(" GW  devInacEmailSMS " +devInacEmailSMS);
				
				if (gwinacDevInfo != null) {
					Iterator<JSONObject> iter = gwinacDevInfo.iterator();
					while (iter.hasNext()) {
						
						JSONObject object 			= iter.next();
						String uid 					= (String) object.getString("uid");
						String inacTime 			= (String) object.getString("inactivityTime");
						String custkeepAliveflag 	= (String) object.getString("custkeepAliveflag");
						
						Device device 		= null;
						device = getDeviceService().findByUidAndCid(uid, cid);
						
						if (device != null) {
							device.setKeepAliveInterval(inacTime);
							device.setCustomizekeepAliveflag(custkeepAliveflag);
							device.setModifiedBy(whoami(request, response));
							device.setModifiedOn(now());
							getDeviceService().save(device, false);
						}
					}
				}
				
				List<UserAccount> users = userAccountService.findByUid(SessionUtil.currentUser(request.getSession()));
				if (users != null && users.size() > 0) {
					UserAccount user = users.get(0);
					String gatwayEmailSms = devInacEmailSMS == null ? "false" : devInacEmailSMS;
					user.setCustomizeEmailSms(gatwayEmailSms);
					userAccountService.saveContact(user);
				}
				
				Customer customer = customerService.findById(cid);
				if (customer != null) {
					customer.setCustomizeGatewyDevInacTime(gatewyDevInacTime);
					customer.setModifiedBy(whoami(request, response));
					customer.setModifiedOn(now());
					customerService.save(customer);
					
					code 		= 200;
					flag 		= true;
					message 	= "success";
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
		} else {
			flag = false;
			message = "timeout";
		}
		
		return new Restponse<String>(flag, code, message);
	}
	
	@RequestMapping(value = "/GW_DeviceDetails", method = RequestMethod.GET)
	public JSONObject gWDeviceDetails(
			@RequestParam(value = "cid", required = true) String cid,
			HttpServletRequest request,	HttpServletResponse response) {

		JSONObject jsonList = new JSONObject();
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			JSONObject json 		= null;
			JSONArray jsonArray 	= new JSONArray();
			
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			List<Device> device  = null;
			device 		 = getDeviceService().findByCid(cid);
			String portinName = "NA";
			
			for(Device dev : device) {

				if (dev.getSpid() != null) {
					String spid = dev.getSpid();
					portinName = getPortionName(spid);
				}

				json = new JSONObject();
				
				json.put("uid", 		dev.getUid());
				json.put("name", 		dev.getAlias());
				json.put("portionName",	portinName);
				json.put("keepalive", 	dev.getKeepAliveInterval());
				String keepAliveFlg		 = dev.getCustomizekeepAliveflag() == null ? "false" : dev.getCustomizekeepAliveflag();
				json.put("keepAliveFlg", keepAliveFlg);
				jsonArray.add(json);
			}
			
			jsonList.put("location", jsonArray);
			
			String gatwayEmailSms    = "false";
			List<UserAccount> users = userAccountService.findByUid(SessionUtil.currentUser(request.getSession()));
			if (users != null && users.size() > 0) {
				UserAccount user = users.get(0);
				gatwayEmailSms = user.getCustomizeEmailSms() == null ? "false" : user.getCustomizeEmailSms();
			}
			jsonList.put("mailSmsFlag",gatwayEmailSms);
			
			Customer customer = customerService.findById(cid);
			if (customer != null) {
				String finderDevInacTime	 = customer.getCustomizeFinderDevInacTime() == null ? "30": customer.getCustomizeFinderDevInacTime();
				String gwDevInacTime 		 = customer.getCustomizeGatewyDevInacTime() == null ? "30" : customer.getCustomizeGatewyDevInacTime();
				jsonList.put("finderDevInacTime", 	finderDevInacTime);
				jsonList.put("gwDevInacTime", 		gwDevInacTime);

			}
			
			return jsonList;
		}

		return jsonList;

	}
	
	public String getPortionName(String spid) {
		Portion portion = portionService.findById(spid);
		String portionName = "NA";
		if (portion != null) {
			portionName = portion.getUid().toUpperCase();
		}
		return portionName;
	}

	@RequestMapping(value = "/getInactivityInfo", method = RequestMethod.GET)
	public JSONObject getInactivityInfo(@RequestParam(value = "cid", required = true) String cid,
									    HttpServletRequest request, HttpServletResponse response) {
		
		if (SessionUtil.isAuthorized(request.getSession())) {
		
			try {
				
			Customer customer = customerService.findById(cid);
			if (customer != null) {
				
				String inactivityMail = customer.getInactivityMail() == null ? "": customer.getInactivityMail();
				String inactivitySMS  = customer.getInactivitySMS() == null ? "": customer.getInactivitySMS();
				
				String battery_threshold = customer.getBattery_threshold();
				if (battery_threshold == null || battery_threshold.isEmpty()) {
					battery_threshold = "40";
				}
				String default_inactivity_time 	= customer.getTagInact();
				String finderDevInacTime 	  	= customer.getCustomizeFinderDevInacTime() == null ? "30" : customer.getCustomizeFinderDevInacTime();
				String finderEmailSms    		= "false";
				
				List<UserAccount> users = userAccountService.findByUid(SessionUtil.currentUser(request.getSession()));
				if (users != null && users.size() > 0) {
					UserAccount user = users.get(0);
					finderEmailSms = user.getCustomizeEmailSms() == null ? "false" : user.getCustomizeEmailSms();
				}
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("inactivityMail", 			inactivityMail);
				jsonObject.put("inactivitySMS", 			inactivitySMS);
				jsonObject.put("battery_threshold", 		battery_threshold);
				jsonObject.put("default_inactivity_time",   default_inactivity_time);
				jsonObject.put("finderInactime", 			finderDevInacTime);
				jsonObject.put("finderEmailSms", 			finderEmailSms);
				
				return jsonObject;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		return null;
	}
	
	@RequestMapping(value = "/getTagTypes", method = RequestMethod.GET)
	public HashSet<String> getTagTypes(@RequestParam(value = "cid", required = true) String cid,
									    HttpServletRequest request, HttpServletResponse response) {
		try {
			Collection<Beacon> beacon = beaconService.getSavedBeaconByCidAndStatus(cid,"checkedout");
			HashSet<String> tagTypes = new HashSet<String>();
			for(Beacon b: beacon){
				tagTypes.add(b.getTag_type());
			}
			return tagTypes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "/getTagNames", method = RequestMethod.GET)
	public HashSet<String> getTagNames(@RequestParam(value = "cid", required = true) String cid,
									    HttpServletRequest request, HttpServletResponse response) {
		try {
			Collection<Beacon> beacon = beaconService.getSavedBeaconByCidAndStatus(cid,"checkedout");
			HashSet<String> tagNames = new HashSet<String>();
			for(Beacon b: beacon){
				tagNames.add(b.getAssignedTo());
			}
			return tagNames;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "/emailsupport", method = RequestMethod.POST)
	public JSONObject supportEmail(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
		
		
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			try {
				
				LOG.info("cid" +params.get("cid") +" flagsc " +params.get("flag"));
				
				Customer cx = customerService.findById(params.get("cid"));
				
				if (cx != null) {
					
					String host 			=cx.getCustSupportHost();
					String port 			= cx.getCustSupportPort();
					
					if (host != null && port != null) {

						cx.setCustSupportEmailEnable(params.get("flag"));
						cx.setModifiedBy(whoami(request, response));
						cx.setModifiedOn(now());
						customerService.save(cx);

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("support", cx.getCustSupportEmailEnable());

						return jsonObject;
						
					} else {
						LOG.info("Can't be enable support Email host AND Port is empty...");
					}

				}
			} catch (Exception e) {
				LOG.error("eror updating support flag for {} ", params, e);
			}
		}
		
		return null;
	}
	
	
	@RequestMapping(value = "/updateSupportDetails", method = RequestMethod.POST)
	public Restponse<String> updateSupportDetails(@RequestBody JSONObject json,HttpServletRequest request, HttpServletResponse response) {
		
		String message 	= "Authentication Failure";
		boolean flag 	= false;
		int     code    = 500;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
		
			try {
				
				String cid 			= json.getString("cid");
				String host 		= json.getString("host");
				String port 		= json.getString("port");
				String supportMail 	= json.getString("username");
				String password 	= json.getString("password");
				String support 		= json.getString("support");

				
				boolean authentication = testSupportCredentials(host, port, supportMail, password);
				
				if (authentication) {
					Customer cx = customerService.findById(cid);
					if (cx != null) {
						cx.setCustSupportHost(host);
						cx.setCustSupportPort(port);
						cx.setCustSupportEmailId(supportMail);
						cx.setCustSupportEmailEnable(support);
						cx.setCustSupportPassword(password);

						customerService.save(cx);
						message = "success";
						code = 200;
						flag = true;
					}
				}

				LOG.info("status " +message);
				
			} catch (Exception e) {
				LOG.error("while update support email error " +e);
			}
		}
		
		return new Restponse<String>(flag, code, message);
	}
	
	@RequestMapping(value = "/supportDetails", method = RequestMethod.GET)
	public JSONArray supportDetails(HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject json 	= null;
		JSONArray jsonArray = new JSONArray();
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			try {
				
				Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
				
				String cid = "";
				if (model != null) {
					if (model.get("id") != null) {
						cid = model.get("id").toString();
					}
				}
				
				boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);

				Iterable<Customer> cx = null;
				
				if (privFlag) {
					cx = customerService.findAll();
				} else {
					cx = customerService.findOneById(cid);
				}

				if (cx != null ) {
					for(Customer c: cx){
						json = new JSONObject();

						String host 			= c.getCustSupportHost();
						String port 			= c.getCustSupportPort();
						String emailId   	    = c.getCustSupportEmailId();
						String pwd              = c.getCustSupportPassword();
						String emailFlag        = c.getCustSupportEmailEnable();
							
						json.put("host", 			host);
						json.put("port", 			port);
						json.put("username", 		emailId);
						json.put("password", 		pwd);
						json.put("support", 		emailFlag);
						json.put("customerName", 	c.getCustomerName());
						json.put("cid", 			c.getId());
						jsonArray.add(json);
					}
				}
				
			} catch (Exception e) {
				LOG.info("While get support email id error ");
				e.printStackTrace();
			}
		}
		return jsonArray;
	}
	
	public boolean testSupportCredentials(String host, String port, String supportMail, String password) {

		boolean flag = true;

		try {

			Properties props = new Properties();

			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(supportMail, password);
				}
			};

			Session session = Session.getInstance(props, auth);
			Transport transport = session.getTransport("smtp");
			transport.connect(host, supportMail, password);
			transport.close();

		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	private DeviceService getDeviceService() {
		if (devService == null) {
			devService = Application.context.getBean(DeviceService.class);
		}
		return devService;
	}
	
	private ClientDeviceService getClientDeviceService() {
		if (clientDeviceService == null) {
			clientDeviceService = Application.context.getBean(ClientDeviceService.class);
		}
		return clientDeviceService;
	}

	public boolean changeState(String cid, String state,String custMessage, String userMessage) {

		Customer customer 			= customerService.findById(cid);
		List<UserAccount> userList 	= userAccountService.findByCustomerId(cid);
		List<Site> siteList 		= siteService.findByCustomerId(cid);
		List<Portion> portionList 	= portionService.findByCid(cid);
		String subject = "Portal "+ state.substring(0,1).toUpperCase()+state.substring(1).toLowerCase() +" Notification";
		
		
		if (customer != null) {
			customer.setModifiedBy("cloud");
			customer.setModifiedOn(now());
			customer.setStatus(state);
			customer = customerService.save(customer);
			custMessage = "Hi " + customer.getCustomerName() + ",\n\n " + custMessage;
			customerUtils.customizeSupportEmail(null, customer.getEmail(), subject, custMessage,null);
		}

		if (userList != null) {
			
			if (state.equals("ACTIVE")) {
				String peferred_url = customer.getPreferedUrlName().trim();				
				String default_url  = customerUtils.cloudUrl()+"/facesix/";
				userMessage += default_url+peferred_url +"\n Login id: ";
			}
			
			
			for (UserAccount acc : userList) {
				if (customer.getId().equals(acc.getCustomerId())) {
					acc.setModifiedBy("cloud");
					acc.setModifiedOn(now());
					acc.setStatus(state);
					acc = userAccountService.saveContact(acc);
					
					String email 	= acc.getEmail().trim();
					String mobile 	= acc.getPhone().trim();
					String name 	= acc.getFname().trim();
					
					String msg = "Hi " + name + ",\n\n " + userMessage;

					if (state.equals("ACTIVE")) {
						msg += email + "\n";
					}
					customerUtils.customizeSupportEmail(null, email, subject, msg,null);
				}
			}
		}
		
		if (siteList != null) {
			for (Site site : siteList) {
				if (site.getCustomerId().equals(customer.getId())) {
					site.setModifiedOn(now());
					site.setModifiedBy("cloud");
					site.setStatus(state);
					site = siteService.save(site);
				}
			}
		}
		
		if (portionList != null) {
			for (Portion portion : portionList) {
				portion.setModifiedOn(now());
				portion.setModifiedBy("cloud");
				portion.setStatus(state);
				portion = portionService.save(portion);
			}
		}
		
		return true;
	}
	
}