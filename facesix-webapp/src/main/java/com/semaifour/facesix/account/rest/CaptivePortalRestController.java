package com.semaifour.facesix.account.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.migcomponents.migbase64.Base64;
import com.semaifour.facesix.account.*;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.captive.portal.*;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/rest/captive/portal")
public class CaptivePortalRestController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(CaptivePortalRestController.class.getName());

	@Autowired
	CustomerService customerService;

	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	CaptivePortalService captivePortalService;
	
	@Autowired
	PrivilegeService privilegeService;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	SiteService siteService;
	
	@Autowired
	PortionService portionService;
	
	
	@Autowired
	DeviceService deviceService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Restponse<String> save(@RequestBody CaptivePortal captivePortal, HttpServletRequest request, HttpServletResponse response) {

		String message     = "Your Captive Portal has been updated successfully.";
		boolean flag 	   = false;
		String portalId	   = null;
		int statusCode	   = 500;
		
		try {
			
			//LOG.info("CaptivePortal " +captivePortal.toString());
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				if (captivePortal.getId() == null) {
					captivePortal.setCreatedBy(whoami(request, response));
					captivePortal.setCreatedOn(now());
					captivePortal.setStatus(CustomerUtils.ACTIVE());
					message = "Your Captive Portal has been created successfully";
				}
				
				String cid 		    = captivePortal.getCid();
				String customerName = captivePortal.getCustomerName();
				
				if (cid == null || cid.isEmpty()) {
					cid = SessionUtil.getCurrentCustomer(request.getSession());
					captivePortal.setCid(cid);
				}
				
				Customer customer = customerService.findById(cid);
				if (customer != null && (customerName == null || customerName.isEmpty())) {
					String name = customer.getCustomerName();
					captivePortal.setCustomerName(name);
				}
				
				captivePortal.setModifiedBy(whoami(request, response));
				captivePortal.setModifiedOn(now());

				String  portalType  = captivePortal.getPortalType().trim();
				
				String imageCode  = "portBg";
				String uploadType = "casting";
				
				if (portalType.equals("login")) {
					
					String supportComponents = captivePortal.getSupportComponents();
					String bgScreenShot 	 = captivePortal.getBgScreenShot();
					
					//LOG.info("getLogoImg " +      captivePortal.getLogoImg());
					//LOG.info("getBackgroundImg " +captivePortal.getBackgroundImg());
					

					if (supportComponents != null) {
						customerSupportComponents(supportComponents, cid);
					}
					if (!bgScreenShot.contains(imageCode)) {
						//String path = savelocalFile(bgScreenShot, uploadType, cid); // change to local path
						String path = saveFile(bgScreenShot, uploadType, cid); // change to cloud path
						captivePortal.setBgScreenShot(path);
					}
					if (captivePortal.getBackgroundImg() != null && !captivePortal.getBackgroundImg().isEmpty()) {
						String backgroundImg = captivePortal.getBackgroundImg();
						if (backgroundImg.contains("=")) {
							String backgroundUrl  = backgroundImg.split("=")[1];
							LOG.info("backgroundUrl " +backgroundUrl);
							customer.setBackground(backgroundUrl);
						}
					}
					if (captivePortal.getLogoImg() != null && !captivePortal.getLogoImg().isEmpty()) {
						String logo 	= captivePortal.getLogoImg();
						if (logo.contains("=")) {
							String logoUrl  = logo.split("=")[1];
							LOG.info("logoUrl " +logoUrl);
							customer.setLogofile(logoUrl);
						}
					}
					customerService.save(customer);
				} else { // registration form
					
					String preferdUrl = captivePortal.getPreferedUrl().trim().toLowerCase();
					preferdUrl 		  = preferdUrl.replace(" ", "_");
					
					captivePortal.setPreferedUrl(preferdUrl);
					
					LOG.info("preferdUrl " +preferdUrl);
					
					if (captivePortal.getBgScreenShot() !=null && !captivePortal.getBgScreenShot().isEmpty()) {
						String bgImg = captivePortal.getBgScreenShot();
						if (!bgImg.contains(imageCode)) {
							//String path = savelocalFile(bgImg, uploadType, cid); // change to local path
							String path = saveFile(bgImg,uploadType,cid); // change to cloud path
							captivePortal.setBgScreenShot(path);
						}						
					}
										
					String curAssociationWith = captivePortal.getAssociationWith() == null ? "cid" : captivePortal.getAssociationWith();
					captivePortal.setAssociationWith(curAssociationWith);
					
					if (captivePortal.getAssociationIds() == null) {
						JSONArray jsonArray = new JSONArray();
						jsonArray.add(cid);
						captivePortal.setAssociationIds(jsonArray);
					}
										
				}
				captivePortalService.save(captivePortal);
				
				statusCode  = 200;
				flag 		= true;
				
			} else {
				message = "Unauthorized User";
				flag = false;
			}

		} catch (Exception e) {
			message = "Error occured while creating Portal.";
			flag 	= false;
			LOG.error("while Portal saving Error ", e);
		}

		return new Restponse<String>(flag, statusCode, message,portalId);

	}
	
	private String saveFile(String dataCode,String uploadType,String cid) {

		try {
			
			String extension  = getFileExtension(dataCode);
			
			String default_url = customerUtils.cloudUrl()+"/";
			
			String url 		= default_url;
			
			String rootPath = _CCC.properties.getProperty("facesix.fileio.binary.root", "/var/www/html");
			
			String Folder   = uploadType+"_"+cid;
			rootPath 		= rootPath+"/"+Folder;
			File file      	= new File(rootPath);

			if (!file.exists()) {
				  boolean success = file.mkdirs();
				  if (success) {
					  LOG.info("Created path: " + file.getPath());
				  } else {
					  LOG.info("could not create directory..");
				  }
			}
			
			String fileName    = UUID.randomUUID().toString().replace("-", "");
			String imageName   = rootPath+"/"+fileName+"."+extension;
			
			String path 	   = url + Folder+"/"+fileName+"."+extension;
			
			String data 		= dataCode.substring(dataCode.indexOf(",") + 1);
			byte[] decodedBytes = Base64.decodeFast(data.getBytes());
			
			FileOutputStream out = new FileOutputStream(imageName);
			out.write(decodedBytes);
			out.close();

			LOG.info("portal file  path  " +path);	
			
			return path;
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String savelocalFile(String dataCode,String uploadType,String cid) {

		try {
			
			String extension  = getFileExtension(dataCode);
			
			String default_url = "/facesix/portalBg?path=";
			
			String url 		= default_url;
			
			String rootPath = _CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_");
			
			String Folder   = uploadType+"_"+cid;
			rootPath 		= rootPath+"/"+Folder;
			File file      	= new File(rootPath);

			if (!file.exists()) {
				  boolean success = file.mkdirs();
				  if (success) {
					  LOG.info("Created path: " + file.getPath());
				  } else {
					  LOG.info("could not create directory..");
				  }
			}
			
			
			 String fileName 	= UUID.randomUUID().toString().replace("-", "");
			 String imageName   = rootPath+"/"+fileName+"."+extension;
			 
			 String path = url + imageName;
			
			String data 		= dataCode.substring(dataCode.indexOf(",") + 1);
			byte[] decodedBytes = Base64.decodeFast(data.getBytes());
			
			FileOutputStream out = new FileOutputStream(imageName);
			out.write(decodedBytes);
			out.close();

			LOG.info("portal file  path  " +path);	
			
			return path;
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getFileExtension(String binaryCode) {
		
		String binaryData[]  = binaryCode.split(";");
		String fileExtension = binaryData[0];
		
		String extensionData[] = fileExtension.split("/");
		String extension 	   = extensionData[1];
		
		LOG.info("File fileExtension " +extension);
		
		return extension;
	}
	
	
	private void customerSupportComponents(String data, String cid) {
		
		try {
			
			Customer customer = null;
			customer 		  = customerService.findById(cid);
			
			if (customer != null) {
				
				JSONObject object = JSONObject.fromObject(data);
				
				String pincode 			=  null;
				String phone 	 		= null;
				
				if (object.get("postalCode") != null) {
					pincode = String.valueOf(object.get("postalCode"));
				}
				if (object.get("contactNumber") != null) {
					 phone = String.valueOf(object.get("contactNumber"));
				}
				
				String fb 				= (String)object.get("facebook");
				String tw 			   	= (String)object.get("twitter");
				String ln 				= (String)object.get("linkedin");
				/*String supportEmail 	= (String)object.get("support_link");*/
				String discover_link 	= (String)object.get("discover_link");
				String city 			= (String)object.get("city");
				String state 			= (String)object.get("state");
				String country 			= (String)object.get("country");
				String address 			= (String)object.get("address");
				
				if (CustomerUtils.isStringNotEmpty(fb))
					customer.setFacebook(fb);
				if (CustomerUtils.isStringNotEmpty(tw))
					customer.setTwitter(tw);
				if (CustomerUtils.isStringNotEmpty(ln))
					customer.setLinkedin(ln);
				if (CustomerUtils.isStringNotEmpty(phone))
					customer.setContactNumber(phone);
				/*if (CustomerUtils.isStringNotEmpty(supportEmail))
					customer.setSupport_link(supportEmail);*/
				if (CustomerUtils.isStringNotEmpty(discover_link))
					customer.setDiscover_link(discover_link); 
				if (CustomerUtils.isStringNotEmpty(address))
					customer.setAddress(address);
				if (CustomerUtils.isStringNotEmpty(city))
					customer.setCity(city);
				if (CustomerUtils.isStringNotEmpty(state))
					customer.setState(state);
				if (CustomerUtils.isStringNotEmpty(country))
					customer.setCountry(country);
				if (CustomerUtils.isStringNotEmpty(pincode))
					customer.setPostalCode(pincode);
				
				customerService.save(customer);
				
			}
		} catch (Exception e) {
			LOG.error("Customer Support Details update error ", e);
		}

	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody Iterable<CaptivePortal> listData(HttpServletRequest request, HttpServletResponse response) {

		Iterable<CaptivePortal> portalList = null;
		String id = "";
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			try {
				
				@SuppressWarnings("unchecked")
				Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			
				if (model != null) {
					if (model.get("id") != null) {
						id = model.get("id").toString();
					}
				}

				boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);
				if (privFlag) {
					portalList = captivePortalService.findAll();
				} else {
					portalList = captivePortalService.findByCid(id);
				}

				return portalList;
				
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Customer details getting error ", e);
			}
		}
		return portalList;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public  Restponse<String> delete(@RequestBody String fsObject ,HttpServletRequest request, HttpServletResponse response) {
		
		
		String message   = "Your Captive Portal has been deleted successfully.";
		boolean flag 	 = false;
		int statusCode	 = 500;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			try {

				String rootPath 	 = _CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_");
				
				JSONObject json 	= JSONObject.fromObject(fsObject);
				String portalId 	= (String) json.get("id");

				LOG.info("delete by Given portal Id " + portalId);
				
				if (portalId != null) {
					
					CaptivePortal portal = captivePortalService.findById(portalId);
					if (portal != null) {
						
						String filePath = rootPath+"/Portal_"+portalId;
						captivePortalService.delete(portal);
						
						File file = new File(filePath);
						deleteDirectory(file);

						flag       = true;
						statusCode = 200;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				message = "Error occured while removing Captive Portal."; 
			}
		} else {
			message = "Unauthorized User";
		}
		return new Restponse<String>(flag, statusCode, message);
	}

	
	
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public @ResponseBody Iterable<CaptivePortal> findOneById(@RequestParam(value = "id", required = true) String id) {
		return captivePortalService.findOneById(id);
	}
	
	@RequestMapping(value = "/prefferdUrl", method = RequestMethod.GET)
	public @ResponseBody CaptivePortal prefferdUrl(@RequestParam(value = "url", required = true) String prefferdUrl) {
		return captivePortalService.findByPreferedUrl(prefferdUrl);
	}
	
	@RequestMapping(value = "/existingPortalType", method = RequestMethod.GET)
	public JSONArray existingLoginTemplate(@RequestParam(value = "cid", required = true) String cid) throws Exception {

		List<CaptivePortal> portal  = null;
		JSONObject json 			= new JSONObject();
		JSONArray jsonArray 		= new JSONArray();
		int loginFlag 				= 1;

		if (cid != null && !cid.isEmpty()) {
			portal = captivePortalService.findByCid(cid);
			if (portal != null) {
				for (CaptivePortal captive : portal) {
					String portalType = captive.getPortalType();
					if (portalType.equals("login")) {
						loginFlag = 0;
						break;
					}
				}
			}
		}
		
		//LOG.info(" cid " + cid + " loginFlag " +loginFlag);
		
		json.put("id", 		 	0);
		json.put("name",     	"Captive Portal");
		json.put("url",      	"");
		json.put("isActive",  	1);
		json.put("editText",  	"Create New Portal");
		json.put("bgScreenShot","/facesix/static/qcom/img/captive_portals/regform.jpg");

		jsonArray.add(json);

		json = null;
		json = new JSONObject();

		json.put("id", 				 1);
		json.put("name",		 	"Login form");
		json.put("url", 			"login");
		json.put("isActive", 		loginFlag);
		json.put("editText",  		"Create New Login");
		json.put("bgScreenShot", 	"/facesix/static/qcom/img/captive_portals/loginform.jpg");

		jsonArray.add(json);

		return jsonArray;
	}
	
	@RequestMapping(value = "/duplicatePreferedUrl", method = RequestMethod.GET)
	public Restponse<String> preferedUrl(@RequestParam(value="preferedUrl" ,required=true) String preferedUrl,
										 @RequestParam(value="id" ,required=false) String id,
										 HttpServletRequest request) throws Exception {
		
		String message 	= "new";
		boolean flag 	= false;
		int statusCode  = 500;
		
		LOG.info("preferedUrl " +preferedUrl +" id " +id);
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			CaptivePortal portal = null;
			preferedUrl          = preferedUrl.trim();
			
			if (id != null && !id.isEmpty()) {
					portal = captivePortalService.findById(id);
				if (portal != null) {
					String prefurl = portal.getPreferedUrl();
					if (prefurl.equalsIgnoreCase(preferedUrl)) {
						message = "new";
					} else {
						message = checkPreferedUrl(preferedUrl);
					}
				}
			} else {
				message = checkPreferedUrl(preferedUrl);
			}
			statusCode = 200;
			flag 	   = true;

			//LOG.info(" preferedUrl status " +message );
			
			return new Restponse<String>(flag, statusCode, message);
		} else {
			message = "duplicate";
			flag 	= false;
			return new Restponse<String>(flag, statusCode, message);
		}
	}
	
   public void updateCustomizedTheme(String imageType, String imgData, String cid) {
		
		if (cid != null && !cid.isEmpty()) {

			try {
				
				String extension  = getFileExtension(imgData);
				
				String rootPath = _CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_");
				
				LOG.info(" imageType " +imageType  +" cid " +cid + " rootPath " +rootPath);
				
				String data 		= imgData.substring(imgData.indexOf(",") + 1);
				byte[] decodedBytes = Base64.decodeFast(data.getBytes());
	
				String fileName = rootPath + "/" + cid + "_" + imageType+"."+extension;
	
				FileOutputStream out = new FileOutputStream(fileName);
				out.write(decodedBytes);
				out.close();
	
				Customer customer = customerService.findById(cid);
	
				if (customer != null) {
					
					if (imageType.equals("background"))
						customer.setBackground(fileName);
					if (imageType.equals("logo"))
						customer.setLogofile(fileName);
					
					customer.setModifiedOn(now());
					customerService.save(customer);
				}
				
			} catch (IOException e) {
				LOG.error("While customized Login theme uploading error " +e);
			}
		}
		
	}
	
	public String checkPreferedUrl(String preferedUrl) {
		
		//LOG.info("---checkPreferedUrl---- " +preferedUrl );
		
		preferedUrl = preferedUrl.trim().toLowerCase();
		CaptivePortal  portal = captivePortalService.findByPreferedUrl(preferedUrl);
		String message =  "new";
		
		if (portal == null) {
			preferedUrl = preferedUrl.trim().toUpperCase();
			portal = captivePortalService.findByPreferedUrl(preferedUrl);
		}
		
		if (portal != null) {
			String  url = portal.getPreferedUrl().toLowerCase();
			if (url.equalsIgnoreCase(preferedUrl)) {
				message = "duplicate";
			}
		}
		return message;
	}
	
	
	public static String deleteFileUrl (String imgpath ) {

		LOG.info("ROOT PATH " +imgpath);
		
		if (imgpath != null && !imgpath.isEmpty()) {

			String data[] = imgpath.split("=");
			String pp = data[1];
			String filePath = pp;

			File file = new File(filePath);
			deleteDirectory(file);
			
			LOG.info(" FOLDER PATH " + filePath);

			return filePath;

		} else {
			return "";
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
			LOG.info("Folder Deleted sucess");
		} else {
			LOG.info("Folder  Deletion failed!!!");
		}
	}
	
	@RequestMapping(value = "/associationList", method = RequestMethod.GET)
	public List<?> associationList(@RequestParam(value="cid" ,required=true) String cid,
										 @RequestParam(value="associatedwith" ,required=true) String associatedwith,
										 HttpServletRequest request) throws Exception {
		if(associatedwith.equals("sid")){
			List<Site> sitelist = siteService.findByCustomerId(cid);
			return sitelist;
		} else if (associatedwith.equals("spid")) {
			List<Portion> portionlist = portionService.findByCid(cid); 
			return portionlist;
		} 
		return null;
	}
	
	
	public JSONArray getHotspotLink(String cid,String sid,String spid) {
		
		List<CaptivePortal> captivePortalList = null;
		CaptivePortal cp 					  = null;
		String hotspotLink 					  = "";
		boolean hotspotLinkadded 			  = false;
		JSONArray hotspot 				      = new JSONArray();

		if (spid != null && !spid.isEmpty()) {
			captivePortalList = captivePortalService.getCaptivePortalContainingAssociatedId(spid);
			if (captivePortalList != null && captivePortalList.size() > 0) {
				hotspotLinkadded = true;
			}
		}

		if (sid != null && !sid.isEmpty() && !hotspotLinkadded) {
			captivePortalList = captivePortalService.getCaptivePortalContainingAssociatedId(sid);
			if (captivePortalList != null && captivePortalList.size() > 0) {
				hotspotLinkadded = true;
			}
		}

		if (cid != null && !cid.isEmpty() && !hotspotLinkadded) {
			captivePortalList = captivePortalService.getCaptivePortalContainingAssociatedId(cid);
			if (captivePortalList != null && captivePortalList.size() > 0) {
				hotspotLinkadded = true;
			}
		}

		if (hotspotLinkadded) {
			cp = captivePortalList.get(0);
			hotspotLink = customerUtils.cloudUrl()+"/facesix/portal/"+cp.getPreferedUrl();
			JSONObject json = new JSONObject();
			json.put("captive-portal-url", hotspotLink);
			hotspot.add(json);
		}
		return hotspot;
	}
	
}
