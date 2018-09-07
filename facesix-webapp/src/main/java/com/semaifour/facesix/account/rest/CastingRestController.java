package com.semaifour.facesix.account.rest;

import java.io.File;
import java.io.FileOutputStream;
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
import com.semaifour.facesix.data.captive.portal.*;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/rest/captive/casting")
public class CastingRestController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(CastingRestController.class.getName());

	@Autowired
	CustomerService customerService;

	@Autowired
	CastingService castingService;
	
		
	@Autowired
	PrivilegeService privilegeService;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	CaptivePortalRestController captivePortalRestController;
	
	@RequestMapping(value = "/castingUpload", method = RequestMethod.POST)
	public Restponse<String> save(@RequestBody Casting casting,
			HttpServletRequest request, HttpServletResponse response) {

		String message     = "Your Casting has been updated successfully.";
		boolean flag 	   = false;
		String id	   	   = null;
		int statusCode	   = 500;
		
		try {
			
			//LOG.info("casting " +casting.toString());
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				if (casting.getId() == null) {
					casting.setCreatedBy(whoami(request, response));
					casting.setCreatedOn(now());
					casting.setStatus(CustomerUtils.ACTIVE());
					message = "Your Casting has been uploaded successfully.";
				}
				
				String cid 		    = casting.getCid();
				String customerName = casting.getCustomerName();
				
				if (cid == null || cid.isEmpty()) {
					cid = SessionUtil.getCurrentCustomer(request.getSession());
					casting.setCid(cid);
				}
				
				Customer customer = customerService.findById(cid);
				if (customer != null && (customerName == null || customerName.isEmpty())) {
					String name = customer.getCustomerName();
					casting.setCustomerName(name);
				}
				
				casting.setModifiedBy(whoami(request, response));
				casting.setModifiedOn(now());

				String screenshot   = casting.getScreenshot();
				String file 		= casting.getFile();
				
				String uploadType = "casting";
				
				casting = castingService.save(casting);
				
				id = casting.getId();
				
				String screenShotPath = saveFile(screenshot,uploadType,cid);
				String imgPath 		= saveFile(file,uploadType,cid);
				
				//String screenShotPath = savelocalFile(screenshot,uploadType,cid);
				//String imgPath 		  = savelocalFile(file,uploadType,cid);
				
				
				casting.setTypefs(uploadType);
				casting.setScreenshot(screenShotPath);
				casting.setFile(imgPath);
				casting.setPath(imgPath); // upload image file path
				
				String fileType = casting.getType();
				if (fileType != null) {
					String data = fileType.split("/")[0];
					casting.setFileType(data);
				}
				
				LOG.info(" cid " +cid);
				
				casting = castingService.save(casting);
				
				LOG.info("saved casting " +casting.toString());
				
				statusCode  = 200;
				flag 		= true;
				
			}

		} catch (Exception e) {
			message = "Error occured while creating casting.";
			flag 	= false;
			LOG.error("while casting saving Error ", e);
		}

		return new Restponse<String>(flag, statusCode, message,id);

	}
	
	private String saveFile(String dataCode,String uploadType,String cid) {

		try {
			
			String extension  = captivePortalRestController.getFileExtension(dataCode);
			
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

			LOG.info("casting cloud file  path  " +path);	
			
			return path;
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String savelocalFile(String dataCode,String uploadType,String cid) {

		try {
			
			String extension  = captivePortalRestController.getFileExtension(dataCode);
			
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

			LOG.info("casting local upload file  path  " +path);	
			
			return path;
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	@RequestMapping(value = "/castingListAll", method = RequestMethod.GET)
	public @ResponseBody Iterable<Casting> listData(HttpServletRequest request, HttpServletResponse response) {

		Iterable<Casting> castingList = null;
		String id = "";
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			try {
				
				Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
				
				if (model != null) {
					if (model.get("id") != null) {
						id = model.get("id").toString();
					}
				}

				
				String fileType = request.getParameter("fileType");
				String cid 		= request.getParameter("cid");
				
				if (fileType != null && !fileType.isEmpty()) {
					castingList = castingService.findByCidAndFileType(cid, fileType);
				} else {
					boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(),Privilege.CUST_WRITE);
					if (privFlag) {
						castingList = castingService.findAll();
					} else {
						castingList = castingService.findByCid(id);
					}
				}

				return castingList;
				
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("casting details getting error ", e);
			}
		}
		return castingList;
	}

	@RequestMapping(value = "/castingDelete", method = RequestMethod.POST)
	public  Restponse<String> delete(@RequestBody String fsObject ,HttpServletRequest request, HttpServletResponse response) {
		
		
		String message   = "Your Casting has been deleted successfully.";
		boolean flag 	 = false;
		int statusCode	 = 500;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			try {

				
				JSONObject json 	= JSONObject.fromObject(fsObject);
				String castingId 	= (String) json.get("path");

				LOG.info("delete by Given casting Id " + castingId);
				
				if (castingId != null) {
					
					Casting casting = castingService.findById(castingId);
					if (casting != null) {
						
						String p1  = casting.getPath();
						String p2  = casting.getScreenshot();
						
						castingService.delete(casting);
						
						LOG.info("path1 " +p1);
						LOG.info("path2 " +p2);
						
						this.deleteFileUrl(p1);
						this.deleteFileUrl(p2);
						

						flag       = true;
						statusCode = 200;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				message = "Error occured while removing casting."; 
			}
		} else {
			message = "Unauthorized User";
		}
		return new Restponse<String>(flag, statusCode, message);
	}

	public void deleteFileUrl (String imgpath ) {

		LOG.info("ROOT PATH " +imgpath);
		
		if (imgpath != null && !imgpath.isEmpty()) {

			String data[] 	= imgpath.split("=");
			String pp 		= data[1];
			String filePath = pp;

			File file = new File(filePath);
			CaptivePortalRestController.deleteDirectory(file);
			
			LOG.info(" FOLDER PATH " + filePath);


		}
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public @ResponseBody Iterable<Casting> findOneById(@RequestParam(value = "id", required = true) String id) {
		return castingService.findOneById(id);
	}

}
