package com.semaifour.facesix.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.semaifour.facesix.binary.BinarySetting;
import com.semaifour.facesix.binary.BinarySettingService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;

@RequestMapping("/web/binary")
@Controller
public class BinaryWebController extends WebController{
	
	static Logger LOG = LoggerFactory.getLogger(BinaryWebController.class.getName());

	@Autowired
	BinarySettingService binarySettingService;

	@Autowired
	HttpServletResponse response;

	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	CustomerUtils CustomerUtils;
	
	@Autowired
	private BeaconDeviceService beaconDeviceService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Map<String, Object> model,
					   @RequestParam(value = "sid",  required = false)  String sid,
					   @RequestParam(value = "spid", required = false) String spid,
					   @RequestParam(value = "uid",  required = false) String u_uid,
					   @RequestParam(value = "cid",  required = false) String cid,
					   @RequestParam(value = "id",   required = false) String id,
					   HttpServletRequest request) {

		String version   = "unknown";
		String buildTime = "unknown";
		
		try {
			
			

			BinarySetting fsobject =null;
			String uid = "";
			
			LOG.info("u_uid " +u_uid);
			
			if (u_uid != null) {
				uid = u_uid.replaceAll("[^a-zA-Z0-9]", "");
				fsobject = binarySettingService.findOneByUid(uid);
			}
			if (fsobject == null) {
				fsobject = new BinarySetting();
				if (u_uid.trim().equals("?")) {
					u_uid = "";
				}
				fsobject.setU_id(u_uid);
			}
			
			if (cid.isEmpty() || cid==null) {
				cid  = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			model.put("fsobject", fsobject);
			
			BeaconDevice beaconDevice = beaconDeviceService.findOneByUid(u_uid);
			
			if (beaconDevice != null) {
				
		        if(beaconDevice.getVersion() != null)
					version = beaconDevice.getVersion();
				if(beaconDevice.getBuild() !=null)
					buildTime = beaconDevice.getBuild();				
			}
			
			model.put("version"  , version.toUpperCase());
			model.put("buildtime", buildTime.toUpperCase());
			
			model.put("sid", sid);
			model.put("spid", spid);
			model.put("cid", cid);

			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			
		} catch (Exception e) {
			LOG.info("While Binary Listing error ", e);
		}

		return _CCC.pages.getPage("facesix.iot.site.binary", "site-binary");
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Map<String, Object> model, @ModelAttribute BinarySetting newfso,
						@RequestParam(value = "file", required = true) MultipartFile planFile,
						@RequestParam(value = "sid", required = false) String sid,
						@RequestParam(value = "spid", required = false) String spid,
						@RequestParam(value = "uuid", required = false) String u_uid,
						@RequestParam(value = "cid", required = false) String cid,
						HttpServletRequest request) {
		
		try {
				
			LOG.info("Binary CId " + cid);
			LOG.info("Binary SId " + sid);
			LOG.info("Binary SPId " + spid);
			LOG.info("Binary U_Id " + u_uid);
			
			String uid = "";
			String str = "";
			
			if (u_uid != null) {
				uid = u_uid.replaceAll("[^a-zA-Z0-9]", "");
				str = "/facesix/web/binary/list?sid="+sid+"&spid="+spid+"&uid="+u_uid+"&cid="+cid;
			}else{
				u_uid = "";
			}

			if (newfso.getBinaryType() != null) {
				if (sid != null && newfso.getBinaryType().equalsIgnoreCase("Venue")) {
					uid = sid;
				} else if (spid != null && newfso.getBinaryType().equalsIgnoreCase("Floor")) {
					uid = spid;
				} else if (cid != null && newfso.getBinaryType().equalsIgnoreCase("Customer")) {
					uid = cid;
				}
				str = "/facesix/web/binary/list?sid="+sid+"&spid="+spid+"&uid="+uid+"&cid="+cid;
			}

			LOG.info("Binary UID " + uid);
				
				BinarySetting settings = binarySettingService.findOneByUid(uid);
				if (settings == null) {
					newfso.setCreatedOn(new Date());
					newfso.setModifiedOn(new Date());
					newfso.setCreatedBy(SessionUtil.currentUser(request.getSession()));
					newfso.setModifiedBy(newfso.getCreatedBy());
					newfso.setU_id(u_uid);
					newfso.setUid(uid);
					newfso.setUpgradeType(newfso.getUpgradeType());
					newfso.setBinaryType(newfso.getBinaryType());
				}
				else {
					// it's existing
					BinarySetting oldfso = binarySettingService.findOneByUid(uid);
					if (oldfso != null) {
						oldfso.setDescription(newfso.getDescription());
						oldfso.setModifiedOn(new Date());
						oldfso.setModifiedBy(SessionUtil.currentUser(request.getSession()));
						oldfso.setUpgradeType(newfso.getUpgradeType());
						oldfso.setBinaryType(newfso.getBinaryType());
						newfso = oldfso;
					}
				}
				
				newfso = binarySettingService.save(newfso, false);
				
				LOG.info("newfso " +newfso.getUpgradeType());

			if (!planFile.isEmpty() && planFile.getSize() > 1) {
				String fileName = newfso.getId() + "_" + planFile.getOriginalFilename();
				Path path = Paths.get(_CCC.properties.getProperty("facesix.fileio.binary.root", "/var/www/html"),(fileName));
				Files.createDirectories(path.getParent());
				Files.copy(planFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				newfso.setPlanFilepath(path.toString());
				newfso.setMqttfilePath(fileName);
				String md5CheckSum = checkSumMD5(path.toString());
				newfso.setMd5Checksum(md5CheckSum);
				newfso = binarySettingService.save(newfso, false);
			}
			response.sendRedirect(str);
		} catch (IOException e) {
			LOG.warn("Failed save binary setting file", e);
		}
		return _CCC.pages.getPage("facesix.iot.site.binary", "site-binary");
	}
	
	
	
	public static String checkSumMD5(String file) throws IOException {
		String checksum = "";
		FileInputStream inputStream = new FileInputStream(file);
		try {
			checksum = DigestUtils.md5Hex(inputStream);
			 LOG.info("Calulating MD5 checksum  "+checksum);
		} catch (IOException ex) {
			LOG.info("While Calulating MD5 checksum error ",ex);
		}finally{
			inputStream.close();
		}
		return checksum;
	}
}
