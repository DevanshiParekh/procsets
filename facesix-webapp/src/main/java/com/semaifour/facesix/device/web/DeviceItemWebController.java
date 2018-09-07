package com.semaifour.facesix.device.web;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.semaifour.facesix.device.data.DeviceItem;
import com.semaifour.facesix.device.data.DeviceItemService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.rest.DeviceRestController;
import com.semaifour.facesix.web.WebController;

/**
 * 
 * Device Item Controller for the webapp
 * 
 * @author mjs
 *
 */

@Controller
@RequestMapping("/web/deviceitem")
public class DeviceItemWebController extends WebController {

	Logger LOG = LoggerFactory.getLogger(this.getClass());
			
	@Autowired
	DeviceItemService deviceItemService;
	
	@Autowired
	DeviceRestController deviceRestController;
	
	@RequestMapping("/list/{typefs}")
	public String list(Map<String, Object> model, HttpServletRequest request, 
					   HttpServletResponse response, @PathVariable("typefs") String typefs) {
		super.pre(model, request, response);
		try {
			model.put("time", new Date());
			model.put("typefs", typefs);
			
			Iterable<DeviceItem> devices = null;
			if (typefs == null) {
				devices = deviceItemService.findAll();
			} else {
				devices = deviceItemService.findByType(typefs);
			}
			model.put("devicesitems", devices);
			model.put("deviceitems", TAB_HIGHLIGHTER);
		} catch (Exception e) {
			LOG.warn("Error fetching devices", e);
		}
		return "device-item-list";
	}

	@RequestMapping("/list")
	public String list(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		return list(model, request, response, null);
	}

	@RequestMapping("/open")
	public String open(Map<String, Object> model,
					   @RequestParam(value = "id", required = false) String id,
					   @RequestParam(value = "uid", required = false) String uid,
					   @RequestParam(value = "fstype", required = false, defaultValue="default") String fstype,
					   @RequestParam(value = "mac", required = false) String mac) {

		model.put("time", new Date());
		DeviceItem device = null;
		if (id != null) {
			device = deviceItemService.findById(id);
		} else if (uid != null) {
			device = deviceItemService.findOneByUid(uid);
			//fetch config by id
		} else if (mac != null) {
			//fetch config by name
			device = deviceItemService.findOneByMac(mac);
		}
		
		if (device != null) {
			//model.put("disabled", "disabled");
			model.put("message", Message.newInfo("Please update existing device config correctly"));
		}
		
		model.put("deviceitem", device);
		model.put("deviceitem", TAB_HIGHLIGHTER);

		return "device-item-edit";
	}
	
	
	@RequestMapping("/delete")
	public String delete(Map<String, Object> model,
					  @RequestParam(value = "id", required = true) String id, 
					  @RequestParam(value = "mac", required = false) String mac,
					  @RequestParam(value = "typefs", required = false) String typefs,
					  HttpServletRequest request, HttpServletResponse response) {
		if (id != null) deviceItemService.delete(id);
		if (mac != null) deviceItemService.deleteByMac(mac);
		
		model.put("message", Message.newInfo("Device item deleted successfully with id {0}, mac {1}", id, mac));
		model.put("deviceitem", TAB_HIGHLIGHTER);

		return list(model, request, response, typefs);
	}
	
}