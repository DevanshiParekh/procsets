package com.semaifour.facesix.rest;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.binary.BinarySetting;
import com.semaifour.facesix.binary.BinarySettingService;
import com.semaifour.facesix.util.CustomerUtils;

@RequestMapping("/rest/binary")
@RestController
public class BinaryRestController {

	static Logger LOG = LoggerFactory.getLogger(BinaryRestController.class.getName());
	
	@Autowired
	BinarySettingService binarySettingService;
	
	@Autowired
	CustomerUtils CustomerUtils;
	
	private int time = 0;


	@RequestMapping(value = "/upgrade", method = RequestMethod.POST)
	public String upgrade(@RequestParam(value = "sid", required = false) String sid,
						@RequestParam(value = 	"spid", required = false) String spid,
						@RequestParam(value = 	"uid", required = false) String uid,
						@RequestParam(value = 	"cid", required = false) String cid,
						@RequestParam(value = 	"flag", required = true) String flag,
						@RequestParam(value = 	"init", required = true) String init,
						HttpServletRequest request) throws IOException {

		String 	result 				= "onprogress";
		boolean isUID  				= false;
		boolean IsgwSolution		= false;
		BinarySetting binarySetting = null;

		try {

			String u_Id 	= "";
			String id		= "";
			String i_id 	= null;

			if (init.equals("true")) {
				time = 1;
			} else {
				time += 1;
			}
						
			if (cid != null) {
				if (CustomerUtils.Gateway(cid)) {
					IsgwSolution = true;
				}
			}
			
			LOG.info("Timer Value == " + time);
			

			if (sid != null && flag.equals("Venue")) {
				i_id = sid;
			} else if (spid != null && flag.equals("Floor")) {
				i_id = spid;
			} else if (cid != null && flag.equals("Customer")) {
				i_id = cid;
			} else { // mac_id
				i_id  = uid;
				u_Id  = i_id.replaceAll("[^a-zA-Z0-9]", "");
				isUID = true;
			}

			if (i_id != null && time == 1) {
				id = i_id.replaceAll("[^a-zA-Z0-9]", "");
				binarySetting = binarySettingService.findOneByUid (id);
				if (binarySetting != null) {
					binarySetting.setModifiedOn(new Date(System.currentTimeMillis()));
					binarySetting.setReason("onprogress");
					if (isUID == true && IsgwSolution == false) {
						i_id 	= i_id.toUpperCase();
					} else if (IsgwSolution == true) {
						i_id	= i_id.toLowerCase();
					}
					binarySetting = binarySettingService.BINARY_BOOT(binarySetting, true, i_id);
				}
			}

			if (isUID == true) {
				binarySetting = binarySettingService.findOneByUid(u_Id);
				
				if (binarySetting != null) {
					result = binarySetting.getReason();
				}
				
				if (!result.equals("onprogress")) {
					time = 0;
					return result = "Uploaded!!!";
				}
			}

           if (time == 100) {
				time = 0;
				return result = "Uploaded!!!";
			}

		} catch (Exception e) {
			LOG.info("While QuberBinary updating  error ",e);
			time = 0;
			return result = "Upload Failed !!!";
		}
		return result;
	}
}

