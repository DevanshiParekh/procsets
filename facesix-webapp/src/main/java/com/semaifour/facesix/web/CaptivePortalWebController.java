package com.semaifour.facesix.web;

import java.util.Map;

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
import com.semaifour.facesix.account.PrivilegeService;
import com.semaifour.facesix.data.captive.portal.CaptivePortalService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.gustpass.Gustpass;
import com.semaifour.facesix.gustpass.GustpassService;
import com.semaifour.facesix.util.SessionUtil;

@Controller
@RequestMapping("/web/captive/portal")
public class CaptivePortalWebController extends WebController{
	
	Logger LOG = LoggerFactory.getLogger(CaptivePortalWebController.class.getName());
	
	@Autowired
	GustpassService gustpassService;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	CaptivePortalService captivePortalService;
	
	@Autowired
	PrivilegeService privilegeService;
	
	
	@RequestMapping(value = "/portalBg", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPlanFile(HttpServletRequest request, HttpServletResponse response) {
		
		try {

			if (SessionUtil.isAuthorized(request.getSession())) {
				
				String imgPath 	= request.getParameter("path");
				
				//LOG.info("PORTAL IMG PATH " +imgPath);
				
				if (imgPath != null && !imgPath.isEmpty()) {
					return ResponseEntity.ok(resourceLoader.getResource("file:" + imgPath));
				}
				
				if (imgPath == null || imgPath.isEmpty()) {
					String defaultPath = "./uploads/user.png";
					return ResponseEntity.ok(resourceLoader.getResource("file:" + defaultPath));
				}
			}
			
		} catch (Exception e) {
			LOG.warn("Failed to load portal Bg pic :", e);
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@RequestMapping(value = "/process", method = RequestMethod.POST)
	public String process(Map<String, Object> model, @ModelAttribute Gustpass guestpass, 
							@RequestParam( value="id",    required=false) String id,
							@RequestParam( value="token", required=false) String token) {
		
		LOG.info("Id" 		+ id);
		LOG.info("Token" 	+ token);
		
		Gustpass pass = null;
		String message = "Invalid OTP Code";
		
		try {
				pass = gustpassService.findById(id);
				if (pass != null) {
					if (pass.getToken().equals(token.trim())) {
						model.put("ssid", pass.getSsid());
						message = "Valid OTP";
					}
				} else {
					message = "Invalid Users";
				}
				model.put("fsobject", pass);
				model.put("message", Message.newInfo(message));
		} catch (Exception e) {
			LOG.warn("Captive process error ",e);
		}

		LOG.info("CAPTIVE PORTAL VALIDATION RESULT" + message);
		return _CCC.pages.getPage("facesix.captiveportal", "captiveportal");
	}
}
