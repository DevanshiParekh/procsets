/*package com.semaifour.facesix.web;


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

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resou/facesix/template/qcom/index#/accountrceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.rest.NetworkConfRestController;
import com.semaifour.facesix.rest.NetworkDeviceRestController;
import com.semaifour.facesix.util.SessionUtil;

*//**
 * 
 * Site Portion Controller for the webapp - responsible for managing portions of iot site locations
 * 
 * @author mjs
 *
 *//*
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
	
	private String resolveSite(String sid) {
		if (!sessionCache.equals(request.getSession(), "sid", sid)) {
			Site site = siteService.findById(sid);
			if (site != null) {
				sessionCache.setAttribute(request.getSession(), "sid", sid);
				sessionCache.setAttribute(request.getSession(), "suid", site.getUid());
			} else {
				return null;
			}
		}
		return sid;
	}
	
	private String resolveSitePortion(String spid) {
		if (!sessionCache.equals(request.getSession(), "spid", spid)) {
			Portion site = portionService.findById(spid);
			if (site != null) {
				sessionCache.setAttribute(request.getSession(), "spid", spid);
				sessionCache.setAttribute(request.getSession(), "spuid", site.getUid());
			} else {
				return null;
			}
		}
		return spid;
	}
	
	*//**
	 * 
	 * Lists all Sites
	 * 
	 * @param model
	 * @return
	 *//*
	@RequestMapping("/list")
	public String list(Map<String, Object> model, @RequestParam(value = "sid", required=false) String sid) {
		sid = resolveSite(sid);
	
		if (sid != null) {
			model.put("fsobjects", portionService.findBySiteId(sid));
		}
		
		prepare(model);
		
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");
	}
	
	@RequestMapping("/sort")
	public String sort(Map<String, Object> model, @RequestParam(value = "sid", required=false) String sid) {
		sid = resolveSite(sid);
	
		if (sid != null) {
			List<Portion> plist  = portionService.findBySiteId(sid);
			List<Portion> mlist  = Collections.unmodifiableList(plist);
			List<Portion> list   = new ArrayList<Portion>(mlist);
			
			Comparator<Portion> cmp = Collections.reverseOrder(); 
			Collections.sort(list, cmp); 			
			model.put("fsobjects", list);
		}
		
		prepare(model);		
		
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");
	}	
	
	*//**
	 * Opens a blank form
	 * 
	 * @param model
	 * @param sid
	 * @return
	 *//*
	@RequestMapping("/new")
	public String addnew(Map<String, Object> model, @RequestParam(value = "sid", required = true) String sid) {
		sid = resolveSite(sid);
	
		model.put("message", Message.newInfo("Please enter new portion details correctly"));

		prepare(model);

		return _CCC.pages.getPage("facesix.iot.site.portion.edit", "site-portion-edit");
	}
	
	
	*//**
	 * 
	 * Copies given Portion to another
	 * 
	 * @param model
	 * @param id
	 * @return
	 *//*
	@RequestMapping("/copy")
	public String copy(Map<String, Object> model, @RequestParam(value = "spid") String spid) {
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
		
		prepare(model);

		return _CCC.pages.getPage("facesix.iot.site.portion.edit", "site-portion-edit");
	}
	
	
	
	*//**
	 * 
	 * Open a given Site
	 * 
	 * @param model
	 * @param id
	 * @return
	 *//*
	@RequestMapping("/open")
	public String open(Map<String, Object> model, @RequestParam(value = "spid", required = true) String spid) {
		Portion portion = null;
		portion = portionService.findById(spid);
		model.put("message", Message.newInfo("Please update portion details correctly"));
		model.put("fsobject", portion);

		prepare(model);

		return _CCC.pages.getPage("facesix.iot.site.portion.edit", "site-portion-edit");
	}
	
	*//**
	 * Deletes the Id
	 * 
	 * @param model
	 * @param id
	 * @return
	 *//*
	@RequestMapping("/delete")
	public String delete(Map<String, Object> model,
						@RequestParam(value = "sid",  required = false) String sid, 
						@RequestParam(value = "spid", required = true) String spid) {
		
		LOG.info("Delete SPID " + spid);
		Portion portion = portionService.findById(spid);
		networkcntrl.deletespid(spid);
		portionService.delete(spid);
		if (portion == null) portion = new Portion();
		portion.setId(null);
		model.put("fsobject", portion);
		model.put("message", Message.newError("Deleted successfully :" + portion.getUid()));
		
		if (sid != null) {
			model.put("fsobjects", portionService.findBySiteId(sid));
		}
		
		prepare(model);
		
		try {
			String str = "/facesix/web/site/portion/list?sid=" + portion.getSiteId();
			response.sendRedirect(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");		

	}
	
	*//**
	 * Deletes the Id
	 * 
	 * @param model
	 * @param id
	 * @return
	 *//*
	@RequestMapping(value = "/deleteall", method = RequestMethod.GET)
	public String deleteall(Map<String, Object> model,
						@RequestParam(value = "sid",  required = false) String sid, 
						@RequestParam(value = "spid", required = true) String spid) {
		
		LOG.info("Delete SPID " + spid);
		String id = null;
		Portion portion = portionService.findById(spid);
		
		if (portion != null) {
			id = portion.getSiteId();
			LOG.info("Delete ID " + id);
		}

		portionService.delete(spid);
		if (portion == null) portion = new Portion();
		portion.setId(null);
		model.put("fsobject", portion);
		model.put("message", Message.newError("Deleted successfully :" + portion.getUid()));
		
		if (sid != null) {
			model.put("fsobjects", portionService.findBySiteId(sid));
		}
		
		prepare(model);
				
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");
	}	
	
	
	*//**
	 * Dashboard Floor
	 * 
	 * @param model
	 * @param id
	 * @return
	 *//*
	@RequestMapping("/dashboard")
	public String dashboard(Map<String, Object> model, @RequestParam(value = "spid", required = true) String spid) {
		spid = resolveSitePortion(spid);
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);

		prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.dashboard", "site-portion-dashboard");
	}
	
	@RequestMapping("/flrdash")
	public String flrdash(Map<String, Object> model, @RequestParam(value = "uid", required = false) String uid, 
													 @RequestParam(value = "spid", required = true) String spid) {
		spid = resolveSitePortion(spid);
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);
		model.put("uid", uid);
		model.put("spid", spid);
		prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.flrdash", "site-portion-flrdash");
	}	
		
	@RequestMapping("/nwcfg")
	public String nwcfg(Map<String, Object> model, @RequestParam(value = "sid",  required = true)  String sid, 
												   @RequestParam(value = "spid", required = true)  String spid ,
												   @RequestParam(value = "uid",  required = true)  String uid) {
		model.put("message", Message.newInfo("Please enter site details correctly"));
		spid = resolveSitePortion(spid);
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);
		model.put("sid",  sid);
		model.put("spid", spid);
		model.put("uid",  uid);
		prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.nwcfg", "site-portion-nwcfg");
	}	
	
	@RequestMapping("/dashview")
	public String dashview(Map<String, Object> model, @RequestParam(value = "sid", required = true) String sid) {
		sid = resolveSite(sid);
		if (sid != null) {
			model.put("fsobjects", portionService.findBySiteId(sid));
		}
		
		prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.dashview", "site-portion-dashview");
	}	
	
	
	@RequestMapping("/map")
	public String map(Map<String, Object> model, @RequestParam(value = "spid", required = true) String spid) {
		spid = resolveSitePortion(spid);
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);		
		model.put("message", Message.newInfo("Please enter site details correctly"));
		model.put("spid", spid);
		prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.map", "site-portion-map");
	}
	
	@RequestMapping("/logview")
	public String logview(Map<String, Object> model, @RequestParam(value = "sid",  required = false) String sid, 
													 @RequestParam(value = "spid", required = false) String spid, 
													 @RequestParam(value = "uid",  required = false) String uid) {
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);		
		model.put("message", Message.newInfo("Please enter site details correctly"));
		model.put("sid",  sid);
		model.put("spid", spid);
		model.put("uid",  uid);
		prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.logview", "site-portion-logview");
	}		
	
	
	@RequestMapping("/devboard")
	public String devboard(Map<String, Object> model, @RequestParam(value = "uid", required = true) String uid, 
													  @RequestParam(value = "spid", required = true) String spid) {
		spid = resolveSitePortion(spid);
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);		
		model.put("uid", uid);
		model.put("spid", spid);
		prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.dev.dashboard", "site-portion-dev-dashboard");
	}	
	
	@RequestMapping("/swiboard")
	public String swiboard(Map<String, Object> model, @RequestParam(value = "uid", required = true) String uid, 
													  @RequestParam(value = "spid", required = true) String spid) {
		spid = resolveSitePortion(spid);
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);		
		model.put("uid", uid);
		model.put("spid", spid);
		prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.swi.dashboard", "site-portion-swi-dashboard");
	}	
	
	*//**
	 * Saves Sites
	 * 
	 * @param model
	 * @param newfso
	 * @return
	 *//*
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Map<String, Object> model, @ModelAttribute Portion newfso, @RequestParam( value="file", required=false) MultipartFile planFile) {
		resolveSite(newfso.getSiteId());
		
		boolean shouldSave = true;
		if (newfso.getId() == null) {
			newfso.setCreatedOn(new Date());
			newfso.setModifiedOn(new Date());
			newfso.setCreatedBy(SessionUtil.currentUser(request.getSession()));
			newfso.setModifiedBy(newfso.getCreatedBy());
		} else {
			resolveSitePortion(newfso.getId());
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
		
		if(!planFile.isEmpty() && planFile.getSize() > 1) {
			try {
				Path path = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"), (newfso.getId() + "_" + planFile.getOriginalFilename()));
				Files.createDirectories(path.getParent());
				Files.copy(planFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				newfso.setPlanFilepath(path.toString());
				newfso = portionService.save(newfso);
			}  catch (IOException e) {
				LOG.warn("Failed save floor plan file", e);
			}
		}
		
		model.put("fsobjects", portionService.findBySiteId(newfso.getSiteId()));
		
		prepare(model);
		
		try {
			String str = "/facesix/web/site/portion/list?sid=" + newfso.getSiteId();
			response.sendRedirect(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");
	}
	
	*//**
	 * Returns floor plan file content
	 * 
	 * @param id
	 * @return
	 *//*
	@RequestMapping(value = "/planfile", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPlanFile(@RequestParam(value = "spid", required = true) String spid) {

		try {
			Portion oldfso = portionService.findById(spid);
			if (oldfso != null && oldfso.getPlanFilepath() != null) {
				return ResponseEntity.ok(resourceLoader.getResource("file:" + oldfso.getPlanFilepath()));
			}
		} catch (Exception e) {
			LOG.warn("Failed to load floor plan for portion :" + spid, e);
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.notFound().build();
	}
	
}*/


package com.semaifour.facesix.web;


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

import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.rest.NetworkConfRestController;
import com.semaifour.facesix.rest.NetworkDeviceRestController;
import com.semaifour.facesix.util.SessionUtil;

/**
 * 
 * Site Portion Controller for the webapp - responsible for managing portions of iot site locations
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/web/site/new/portion")
public class SitePortionWebNewController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(SitePortionWebNewController.class.getName());
			
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
	
	private String resolveSite(String sid, HttpServletRequest request, HttpServletResponse response) {
		if (!sessionCache.equals(request.getSession(), "sid", sid)) {
			Site site = siteService.findById(sid);
			if (site != null) {
				sessionCache.setAttribute(request.getSession(), "sid", sid);
				sessionCache.setAttribute(request.getSession(), "suid", site.getUid());
			} else {
				return null;
			}
		}
		return sid;
	}
	
	private String resolveSitePortion(String spid, HttpServletRequest request, HttpServletResponse response) {
		if (!sessionCache.equals(request.getSession(), "spid", spid)) {
			Portion site = portionService.findById(spid);
			if (site != null) {
				sessionCache.setAttribute(request.getSession(), "spid", spid);
				sessionCache.setAttribute(request.getSession(), "spuid", site.getUid());
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
	public String list(Map<String, Object> model, @RequestParam(value = "sid", required=false) String sid, HttpServletRequest request, HttpServletResponse response) {
		sid = resolveSite(sid, request, response);
	
		if (sid != null) {
			model.put("fsobjects", portionService.findBySiteId(sid));
		}
		
		prepare(model, request, response);
		
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");
	}
	
	@RequestMapping("/sort")
	public String sort(Map<String, Object> model, @RequestParam(value = "sid", required=false) String sid, HttpServletRequest request, HttpServletResponse response) {
		sid = resolveSite(sid, request, response);
	
		if (sid != null) {
			List<Portion> plist  = portionService.findBySiteId(sid);
			List<Portion> mlist  = Collections.unmodifiableList(plist);
			List<Portion> list   = new ArrayList<Portion>(mlist);
			
			Comparator<Portion> cmp = Collections.reverseOrder(); 
			Collections.sort(list, cmp); 			
			model.put("fsobjects", list);
		}
		
		prepare(model, request, response);		
		
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");
	}	
	
	/**
	 * Opens a blank form
	 * 
	 * @param model
	 * @param sid
	 * @return
	 */
	@RequestMapping("/new")
	public String addnew(Map<String, Object> model, @RequestParam(value = "sid", required = true) String sid, HttpServletRequest request, HttpServletResponse response) {
		sid = resolveSite(sid, request, response);
	
		model.put("message", Message.newInfo("Please enter new portion details correctly"));

		prepare(model, request, response);

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
	public String open(Map<String, Object> model, @RequestParam(value = "spid", required = true) String spid) {
		//Portion portion = null;
		//portion = portionService.findById(spid);
		//model.put("message", Message.newInfo("Please update portion details correctly"));
		//model.put("fsobject", portion);

		//prepare(model);

		return _CCC.pages.getPage("facesix.iot.site.portion.edit", "site-portion-edit");
	}
	
	@RequestMapping("/draw")
	public String draw(Map<String, Object> model, @RequestParam(value = "spid", required = true) String spid) {
		Portion portion = null;
		portion = portionService.findById(spid);
		model.put("message", Message.newInfo("Please update portion details correctly"));
		model.put("fsobject", portion);

		//prepare(model);

		return _CCC.pages.getPage("facesix.iot.site.portion.draw", "site-portion-draw");
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
						@RequestParam(value = "spid", required = true) String spid, HttpServletRequest request, HttpServletResponse response) {
		
		LOG.info("Delete SPID " + spid);
		Portion portion = portionService.findById(spid);
		networkcntrl.deletespid(spid);
		portionService.delete(spid);
		if (portion == null) portion = new Portion();
		portion.setId(null);
		model.put("fsobject", portion);
		model.put("message", Message.newError("Deleted successfully :" + portion.getUid()));
		
		if (sid != null) {
			model.put("fsobjects", portionService.findBySiteId(sid));
		}
		
		prepare(model, request, response);
		
		try {
			//String str = "/facesix/web/site/portion/list?sid=" + portion.getSiteId();
			String str = "/facesix/qubercloud/welcome#/floor/"+portion.getSiteId();
			response.sendRedirect(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return _CCC.pages.getPage("facesix.iot.site.portion.list", "site-portion-list");		

	}
	
	/**
	 * Deletes the Id
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteall", method = RequestMethod.GET)
	public String deleteall(Map<String, Object> model,
						@RequestParam(value = "sid",  required = false) String sid, 
						@RequestParam(value = "spid", required = true) String spid, HttpServletRequest request, HttpServletResponse response) {
		
		LOG.info("Delete SPID " + spid);
		String id = null;
		Portion portion = portionService.findById(spid);
		
		if (portion != null) {
			id = portion.getSiteId();
			LOG.info("Delete ID " + id);
		}

		portionService.delete(spid);
		if (portion == null) portion = new Portion();
		portion.setId(null);
		model.put("fsobject", portion);
		model.put("message", Message.newError("Deleted successfully :" + portion.getUid()));
		
		if (sid != null) {
			model.put("fsobjects", portionService.findBySiteId(sid));
		}
		
		prepare(model, request, response);
				
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
	public String dashboard(Map<String, Object> model, @RequestParam(value = "spid", required = true) String spid, HttpServletRequest request, HttpServletResponse response) {
		spid = resolveSitePortion(spid, request, response);
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);

		prepare(model, request, response);
		return _CCC.pages.getPage("facesix.iot.site.portion.dashboard", "site-portion-dashboard");
	}
	
	@RequestMapping("/flrdash")
	public String flrdash(Map<String, Object> model, @RequestParam(value = "uid", required = false) String uid, 
													 @RequestParam(value = "spid", required = true) String spid, HttpServletRequest request, HttpServletResponse response) {
		spid = resolveSitePortion(spid, request, response);
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);
		model.put("uid", uid);
		model.put("spid", spid);
		prepare(model, request, response);
		return _CCC.pages.getPage("facesix.iot.site.portion.flrdash", "site-portion-flrdash");
	}	
		
	@RequestMapping("/nwcfg")
	public String nwcfg(Map<String, Object> model, @RequestParam(value = "sid",  required = true)  String sid, 
												   @RequestParam(value = "spid", required = true)  String spid ,
												   @RequestParam(value = "uid",  required = true)  String uid) {
		//model.put("message", Message.newInfo("Please enter site details correctly"));
		//spid = resolveSitePortion(spid);
		//Portion portion = portionService.findById(spid);
		//model.put("fsobject", portion);
		//model.put("sid",  sid);
		//model.put("spid", spid);
		//model.put("uid",  uid);
		//prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.nwcfg", "site-portion-nwcfg");
	}	
	
	@RequestMapping("/devcfg")
	public String devcfg(Map<String, Object> model, @RequestParam(value = "sid",  required = true)  String sid, 
												   @RequestParam(value = "spid", required = true)  String spid ,
												   @RequestParam(value = "uid",  required = true)  String uid) {
		//model.put("message", Message.newInfo("Please enter site details correctly"));
		//spid = resolveSitePortion(spid);
		//Portion portion = portionService.findById(spid);
		//model.put("fsobject", portion);
		//model.put("sid",  sid);
		//model.put("spid", spid);
		//model.put("uid",  uid);
		//prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.devcfg", "site-portion-devcfg");
	}	
	
	@RequestMapping("/topview")
	public String topview(Map<String, Object> model, @RequestParam(value = "sid",  required = false)  String sid, 
												   @RequestParam(value = "spid", required = false)  String spid ,
												   @RequestParam(value = "uid",  required = false)  String uid) {
		//model.put("message", Message.newInfo("Please enter site details correctly"));
		//spid = resolveSitePortion(spid);
		//Portion portion = portionService.findById(spid);
		//model.put("fsobject", portion);
		//model.put("sid",  sid);
		//model.put("spid", spid);
		//model.put("uid",  uid);
		//prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.topview", "site-portion-topview");
	}	
	
	
	@RequestMapping("/dashview")
	public String dashview(Map<String, Object> model, @RequestParam(value = "sid", required = true) String sid, HttpServletRequest request, HttpServletResponse response) {
		sid = resolveSite(sid, request, response);
		if (sid != null) {
			model.put("fsobjects", portionService.findBySiteId(sid));
		}
		
		prepare(model, request, response);
		return _CCC.pages.getPage("facesix.iot.site.portion.dashview", "site-portion-dashview");
	}	
	
	
	@RequestMapping("/map")
	public String map(Map<String, Object> model, @RequestParam(value = "spid", required = true) String spid, HttpServletRequest request, HttpServletResponse response) {
		spid = resolveSitePortion(spid, request, response);
		//Portion portion = portionService.findById(spid);
		//model.put("fsobject", portion);		
		//model.put("message", Message.newInfo("Please enter site details correctly"));
		//model.put("spid", spid);
		//prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.map", "site-portion-map");
	}
	
	@RequestMapping("/topcfg")
	public String topcfg(Map<String, Object> model,@RequestParam(value = "sid",  required = false)  String sid, 
			   @RequestParam(value = "spid", required = false)  String spid ,
			   @RequestParam(value = "uid",  required = false)  String uid) {
		//Portion portion = portionService.findById(spid);
		//model.put("fsobject", portion);		
		//model.put("message", Message.newInfo("Please enter site details correctly"));
		//model.put("spid", spid);
		//prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.topcfg", "site-portion-topcfg");
	}
	
	@RequestMapping("/logview")
	public String logview(Map<String, Object> model, @RequestParam(value = "sid",  required = false) String sid, 
													 @RequestParam(value = "spid", required = false) String spid, 
													 @RequestParam(value = "uid",  required = false) String uid,
													 HttpServletRequest request, HttpServletResponse reponse) {
		Portion portion = portionService.findById(spid);
		model.put("fsobject", portion);		
		model.put("message", Message.newInfo("Please enter site details correctly"));
		model.put("sid",  sid);
		model.put("spid", spid);
		model.put("uid",  uid);
		List<String> alerts = networkDeviceRestController.alerts(null, sid, spid, null, uid, "5d", request, response);
		model.put("alerts", alerts);
		prepare(model, request, response);
		return _CCC.pages.getPage("facesix.iot.site.portion.logview", "site-portion-logview");
	}		
	
	
	@RequestMapping("/devboard")
	public String devboard(Map<String, Object> model, @RequestParam(value = "uid", required = true) String uid, 
													  @RequestParam(value = "spid", required = true) String spid) {
		//spid = resolveSitePortion(spid);
		//Portion portion = portionService.findById(spid);
		//model.put("fsobject", portion);		
		//model.put("uid", uid);
		//model.put("spid", spid);
		//prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.dev.dashboard", "site-portion-dev-dashboard");
	}	
	
	@RequestMapping("/swiboard")
	public String swiboard(Map<String, Object> model, @RequestParam(value = "uid", required = true) String uid, 
													  @RequestParam(value = "spid", required = true) String spid) {
		//spid = resolveSitePortion(spid);
		//Portion portion = portionService.findById(spid);
		//model.put("fsobject", portion);		
		//model.put("uid", uid);
		//model.put("spid", spid);
		//prepare(model);
		return _CCC.pages.getPage("facesix.iot.site.portion.swi.dashboard", "site-portion-swi-dashboard");
	}	
	
	/**
	 * Saves Sites
	 * 
	 * @param model
	 * @param newfso
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Map<String, Object> model, @ModelAttribute Portion newfso, @RequestParam( value="file", required=false) MultipartFile planFile, HttpServletRequest request, HttpServletResponse response) {
		resolveSite(newfso.getSiteId(), request, response);
		boolean shouldSave = true;
		if (newfso.getId() == null) {
			newfso.setCreatedOn(new Date());
			newfso.setModifiedOn(new Date());
			newfso.setCreatedBy(SessionUtil.currentUser(request.getSession()));
			newfso.setModifiedBy(newfso.getCreatedBy());
		} else {
			resolveSitePortion(newfso.getId(), request, response);
			//it's existing
			Portion oldfso = portionService.findById(newfso.getId());
			if (oldfso == null) {
				model.put("message", Message.newFailure("Site not found with ID :" + newfso.getId()));
				shouldSave = false;
			} else {
				//check the mac/device id not overwritten
				oldfso.update(newfso);
				oldfso.setModifiedOn(new Date());
				newfso.setModifiedBy(SessionUtil.currentUser(request.getSession()));
				newfso = oldfso;
			}
		}
	
		if (shouldSave && !planFile.isEmpty() ) {
			newfso = portionService.save(newfso);
			model.put("disabled", "disabled");
			model.put("message", Message.newSuccess("Site saved successfully."));
		}
		
		if(!planFile.isEmpty() && planFile.getSize() > 1) {
			try {
				Path path = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"), (newfso.getId() + "_" + planFile.getOriginalFilename()));
				Files.createDirectories(path.getParent());
				Files.copy(planFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				newfso.setPlanFilepath(path.toString());
				newfso = portionService.save(newfso);
			}  catch (IOException e) {
				LOG.warn("Failed save floor plan file", e);
			}
		}
		
		model.put("fsobjects", portionService.findBySiteId(newfso.getSiteId()));
		
		prepare(model, request, response);
		
		try {
			//String str = "/facesix/web/site/portion/list?sid=" + newfso.getSiteId();
			String str = "/facesix/qubercloud/welcome#/floor/"+newfso.getSiteId();
			response.sendRedirect(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		} catch (Exception e) {
			LOG.warn("Failed to load floor plan for portion :" + spid, e);
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.notFound().build();
	}
	
}