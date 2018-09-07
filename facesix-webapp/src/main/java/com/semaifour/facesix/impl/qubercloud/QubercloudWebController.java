package com.semaifour.facesix.impl.qubercloud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.aws.pubsub.PublishSubscribeSample;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WelcomeController;
import com.semaifour.facesix.util.CustomerUtils;

@Controller
@RequestMapping("qubercloud")
public class QubercloudWebController extends WelcomeController{

	Logger LOG=LoggerFactory.getLogger(QubercloudWebController.class.getName());
	@Autowired
	SiteService service;
	
	@Autowired
	PortionService portionservice;
	
	@Autowired
	UserAccountService userService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	PublishSubscribeSample PublishSubscribe;
	
	@RequestMapping("/welcome")
	public String welcome(Map<String, Object> model, @RequestParam(value="u",  required=false) String user, 
													 @RequestParam(value="p",  required=false) String pwd,
													 @RequestParam(value="re", required=false) String rem,
													 @RequestParam(value="fgt",required=false) String fgt,
													 @RequestParam(value = "url", required=false) String pref_url,
													 HttpServletRequest request, HttpServletResponse response) {


		try {
			
			prepare(model, request, response);	
			
			if (user != null) {
				user = user.trim();
			}
			
			LOG.info("User Id " + user);
			
			super.welcome(model, user, pwd, rem, fgt, pref_url,request, response);
			
			if (SessionUtil.isAuthorized(request.getSession())) {

				if (user == null) {
					user = SessionUtil.currentUser(request.getSession());
				}
				
				UserAccount account = userService.findOneByUid(user);
				
				if (account != null && account.getResetStatus()) {
					account.setResetStatus(false);
					account = userService.saveContact(account);
				}
			
				Customer cust 	= customerService.findById(account.getCustomerId());		
				String Page 	= _CCC.pages.getPage("facesix.index.list", "site-list");
				
				if (cust != null) {
					
					String cid 	= cust.getId();
					
					SessionUtil.setCurrentSiteCustomerId(request.getSession(), cid);
					SessionUtil.setCurrentCustomer(request.getSession(), cid);
					
					model.put("cid", cid);
				
					String sid 					= null;
					String spid 				= null;
					List<Site> sitelist 		= null;
					List<Portion>portionlist 	= null;
					
					sitelist = service.findByCustomerId(cid);
					
					
					String solution = cust.getSolution();
					String str 		= "/facesix/web/finder/device/list?cid=" + cid;
					
					if (solution.contains("Gateway") || solution.equals("GeoLocation")) {
						str = "/facesix/web/site/list?cid=" + cid;
					}
					
					if (solution.contains("Gateway") || solution.equals("GeoLocation")) {
						if (sitelist != null && sitelist.size() > 0) {
							if (sitelist.size() == 1) {
								Site site 	= sitelist.get(0);
								sid 		= site.getId();
								if (solution.equals("GatewayFinder")) {
									str = "/facesix/web/beacon/venuetag?sid=" + sid + "&cid=" + cid;
								} else {
									str = "/facesix/web/site/portion/dashview?sid=" + sid + "&cid=" + cid;
								}
							}
						}
						response.sendRedirect(str);
					} else if (solution.equals("GeoFinder")) {

						if (sitelist != null && sitelist.size() > 0) {
							
							str = "/facesix/web/site/list?cid=" + cid;

							if (sitelist.size() == 1) {
									Site site   = sitelist.get(0);
									sid 	    = site.getId();
									portionlist = portionservice.findBySiteId(sid);
								if (portionlist != null && portionlist.size() > 0) {
									Portion portion = portionlist.get(0);
									spid 			= portion.getId();
									str = "/facesix/web/beacon/venuetag?cid=" + cid + "&sid=" + sid + "&spid="+spid;
								}
							}

						}
						response.sendRedirect(str);
					} else if (solution.equals("Heatmap")) {

						if (sitelist != null && sitelist.size() > 0) {
							
							str = "/facesix/web/site/list?cid=" + cid;

							if (sitelist.size() == 1) {
									Site site   = sitelist.get(0);
									sid 	    = site.getId();
									portionlist = portionservice.findBySiteId(sid);
								if (portionlist != null && portionlist.size() > 0) {
									Portion portion = portionlist.get(0);
									spid 			= portion.getId();
									str = "/facesix/web/site/portion/fullheatmap?sid="+sid+"&spid="+spid+"&cid="+cid;
								}
							}

						}
						response.sendRedirect(str);
					
					} else if (solution.equals("Retail")) {
						String retailer = "/facesix/mesh-topology?cid="+cid;
						response.sendRedirect(retailer);
					}

				} else {
					String cid = SessionUtil.getCurrentCustomer(request.getSession());
					if (cid != null && !cid.contains("null")) {
						model.put("cid", cid);
					}
				}
				
				return Page;
			} else {
				
				HttpSession session = request.getSession(true);
				session.setAttribute("message", model.get("messages"));
				String str = "";
			
				if (pref_url != null && !pref_url.isEmpty()) {
					str = "/facesix/" + pref_url;
				} else {
					str = "/facesix/";
				}
				response.sendRedirect(str);
				return _CCC.pages.getPage("facesix.login", "login");
			}
		} catch (Exception e) {
			return _CCC.pages.getPage("facesix.login", "login");
		}
		
	}
	
	@Override
	public  boolean login(HttpServletRequest request, HttpServletResponse response, String user, String pwd) {
		try {
			UserAccount userobj = userService.findOneByUid(user);
			if (user != null && pwd != null && userobj != null	&& StringUtils.equals(userobj.getPassword(), _CCC.cryptor.encrypt(pwd)) 
					&& userobj.getStatus().equals("ACTIVE")) {
				
				SessionUtil.authorizeSession(request.getSession(), userobj.getUid());
				sessionCache.setAttribute(request.getSession(), "user", userobj);
				sessionCache.setAttribute(request.getSession(), "time", now());
				sessionCache.setAttribute(request.getSession(), "role", userobj.getRole());
				sessionCache.setAttribute(request.getSession(), "userId", userobj.getId());
				
				Map<String, Object> privilege = new HashMap<String, Object>();
				privilege.put("role",userobj.getRole());
				privilege.put("id",userobj.getCustomerId());
				sessionCache.setAttribute(request.getSession(),"privs", privilege);
	
				if (StringUtils.equals("superadmin", userobj.getRole())) {
					privilege.put("CUST_READ", true);
					privilege.put("SITE_READ", true);
					privilege.put("SYS_READ", true);
					privilege.put("ACC_READ", true);

					privilege.put("CUST_WRITE", true);
					privilege.put("SITE_WRITE", true);
					privilege.put("SYS_WRITE", true);
					privilege.put("ACC_WRITE", true);
					
					privilege.put("BINARY_WRITE", true);
					privilege.put("ADMIN_WRITE", true);
					privilege.put("INACTIVE_WRITE", true);
					privilege.put("MANAGEMENT_WRITE", true);
				
					privilege.put("SUPERAPP_ADMIN_WRITE", true);
				} else if (StringUtils.equals("appadmin", userobj.getRole())) {
					Customer cust = customerService.findById(userobj.getCustomerId());
					if(cust.getSolution().equalsIgnoreCase("Gateway")||cust.getSolution().equalsIgnoreCase("GatewayFinder")){
						privilege.put("CAPTIVE_PORTAL", true);
					}else{
						privilege.put("CAPTIVE_PORTAL", false);
					}
					privilege.put("CUST_READ", false);
					privilege.put("SITE_READ", true);
					privilege.put("SYS_READ", true);
					privilege.put("ACC_READ", true);

					privilege.put("CUST_WRITE", false);
					privilege.put("SITE_WRITE", true);
					privilege.put("SYS_WRITE", true);
					privilege.put("ACC_WRITE", true);	
					privilege.put("APP_WRITE", true);
					
					privilege.put("BINARY_WRITE", true);
					privilege.put("MANAGEMENT_WRITE", true);
					
					privilege.put("SUPERAPP_ADMIN_WRITE", true);
					
				}  else if (StringUtils.equals("siteadmin", userobj.getRole())) {
					privilege.put("CUST_READ", false);
					privilege.put("SITE_READ", true);
					privilege.put("SYS_READ", true);
					privilege.put("ACC_READ", true);

					privilege.put("CUST_WRITE", false);
					privilege.put("SITE_WRITE", true);
					privilege.put("SYS_WRITE", false);
					privilege.put("ACC_WRITE", false);
					privilege.put("APP_WRITE", true);

				}  else if (StringUtils.equals("sysadmin", userobj.getRole())) {
					privilege.put("CUST_READ", false);
					privilege.put("SITE_READ", true);
					privilege.put("SYS_READ", true);
					privilege.put("ACC_READ", true);

					privilege.put("CUST_WRITE", false);
					privilege.put("SITE_WRITE", false);
					privilege.put("SYS_WRITE", true);
					privilege.put("ACC_WRITE", false);	
					privilege.put("APP_WRITE", true);
				}  else if (StringUtils.equals("useradmin", userobj.getRole())) {
					Customer cust = customerService.findById(userobj.getCustomerId());
					if(cust.getSolution().equalsIgnoreCase("Gateway")||cust.getSolution().equalsIgnoreCase("GatewayFinder")){
						privilege.put("CAPTIVE_PORTAL", true);
					}else{
						privilege.put("CAPTIVE_PORTAL", false);
					}
					privilege.put("CUST_READ", false);
					privilege.put("SITE_READ", true);
					privilege.put("SYS_READ", true);
					privilege.put("ACC_READ", true);

					privilege.put("CUST_WRITE", false);
					privilege.put("SITE_WRITE", false);
					privilege.put("SYS_WRITE", false);
					privilege.put("ACC_WRITE", true);	
					privilege.put("MANAGEMENT_WRITE", true);
				}  else if (StringUtils.equals("user", userobj.getRole())) {
					privilege.put("CUST_READ", false);
					privilege.put("SITE_READ", true);
					privilege.put("SYS_READ", true);
					privilege.put("ACC_READ", true);

					privilege.put("CUST_WRITE", false);
					privilege.put("SITE_WRITE", false);
					privilege.put("SYS_WRITE", false);
					privilege.put("ACC_WRITE", false);
					privilege.put("APP_WRITE", true);
				}
				
			return true;
			} else {
				if (super.login(request, response, user, pwd) && StringUtils.equals(user, _CCC.properties.getDefaultAdminUser())) {
					Map<String, Object> privilege = new HashMap<String, Object>();
					privilege.put("role", "superadmin");
					privilege.put("id",0);
					sessionCache.setAttribute(request.getSession(),"privs", privilege);
					privilege.put("CUST_READ", true);
					privilege.put("SITE_READ", true);
					privilege.put("SYS_READ", true);
					privilege.put("ACC_READ", true);
					return true;
				} else {
					return false;
				}
			}
		} catch (Throwable t) {
			LOG.warn("Login attempt failed with user :" + user, t);
			return false;
		}
	}

	
	@RequestMapping("/validateLogin")
	public String validateLogin(Map<String, Object> model, @RequestParam(value = "u", required = false) String user,
			@RequestParam(value = "p", required = false) String pwd,
			@RequestParam(value = "re", required = false) String rem,
			@RequestParam(value = "fgt", required = false) String fgt, HttpServletRequest request, HttpServletResponse response) {

		LOG.info("Welcome  validateLogin Entered");
		//super.welcome(model, user, pwd, rem, fgt);
		if (SessionUtil.isAuthorized(request.getSession())) {
			return _CCC.pages.getPage("facesix.index.list", "site-list");
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}

	}
	
	@RequestMapping("/cloud/home")
	public String welcome(Map<String, Object> model,@RequestParam(value="user",required=false) String user,
						 HttpServletRequest request, HttpServletResponse response) {
		
		String page =_CCC.pages.getPage("facesix.login", "login");
		
		try {
			
			prepare(model, request, response);
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				if (user == null) {
					user = SessionUtil.currentUser(request.getSession());
				}
				
				UserAccount account = userService.findOneByUid(user);

				if (account != null) {
					if (account.getCustomerId() != null) {
						
						String cid = account.getCustomerId();
						
						model.put("cid", cid);
						model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
						model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
						model.put("Gateway", 		customerUtils.Gateway(cid));
						model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
						
						}
					return _CCC.pages.getPage("facesix.index.list", "site-list");
				}
			}
			return page;
			
		} catch (Exception e) {
			return page;
		}

	}
}
