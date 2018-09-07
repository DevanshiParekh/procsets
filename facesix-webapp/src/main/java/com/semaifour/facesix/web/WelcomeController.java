package com.semaifour.facesix.web;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.graylog2.rest.models.system.sessions.requests.SessionCreateRequest;
import org.graylog2.rest.models.system.sessions.responses.SessionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.itextpdf.text.Font;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.captive.portal.CaptivePortal;
import com.semaifour.facesix.data.captive.portal.CaptivePortalService;
import com.semaifour.facesix.data.graylog.GraylogRestClient;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.gustpass.Gustpass;
import com.semaifour.facesix.gustpass.GustpassService;
import com.semaifour.facesix.session.Contactus;
import com.semaifour.facesix.util.SessionUtil;

/**
 * 
 * Welcome Controller for the webapp
 * 
 * @author mjs
 *
 */
@Controller
public class WelcomeController extends WebController {
	
	static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	static Logger LOG = LoggerFactory.getLogger(WelcomeController.class.getName());
	
	@Autowired
	com.semaifour.facesix.util.CustomerUtils CustomerUtils;
	
	@Autowired
	GustpassService gustpassService;
	
	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	AccountWebController accountWebController;
	
	@Autowired
	CaptivePortalService captivePortalService;
	
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	SiteService service;
	
	@Value("${facesix.cloud.version}")
	private String cloud_version;
			
	@RequestMapping(value = {"", "/"})
	public String root(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {
			String c = _CCC.properties.getProperty("facesix.path2home");
			if (c != null) {
				try {
					response.sendRedirect(c);
				} catch (Exception e) {
					LOG.warn("Failed to redirect to path2home :" + c);
					return _CCC.pages.getPage("facesix.home", "home");
				}
			} else {
				return _CCC.pages.getPage("facesix.home", "home");
			}
		} else {
			HttpSession session = request.getSession();
			String message 		= (String) session.getAttribute("message");
			if (message != null && !message.isEmpty()) {
				model.put("messages", message);
			}
			session.invalidate();
			return _CCC.pages.getPage("facesix.login", "login");
		}
		return _CCC.pages.getPage("facesix.home", "home");
	}
	
	public String error(Map<String, Object> model) {
		return _CCC.pages.getPage("facesix.error", "error");
	}
	
	@RequestMapping("/home")
	public String home(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {
			return _CCC.pages.getPage("facesix.home", "home");
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}
	
	@RequestMapping("/account")
	public String account(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {
			String cid = SessionUtil.getCurrentCustomer(request.getSession());
			model.put("cid", cid);
			return _CCC.pages.getPage("facesix.account", "account");
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}	
	
	
	@RequestMapping("/qubercast")
	public String qubercast(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {
			return _CCC.pages.getPage("facesix.qubercast", "qubercast");
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}	
	
	@RequestMapping("/logs")
	public String logs(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {	
	        
	        return _CCC.pages.getPage("facesix.logs", "logs");
			
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}
		
	@RequestMapping("/client")
	public String client(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {
			return _CCC.pages.getPage("facesix.client.list", "client-list");
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}
	@RequestMapping("/contact")
	public String contact(Map<String, Object> model) {
		Contactus contact = new Contactus();
		model.put("fsobject", contact);
		model.put("Contactus", TAB_HIGHLIGHTER);		
		return _CCC.pages.getPage("facesix.contact", "contact");
	}	
	
	@RequestMapping(value = "/sendmail", method = RequestMethod.POST)
	public String sendmail(Map<String, Object> model,@ModelAttribute Contactus contact,HttpServletRequest request) {
		
		Customer cx 	 = null;
		String param 	 = request.getParameter("url");
		String toMail    = "support@qubercomm.com";
		String cid = "";
		
		if(param != null && !param.isEmpty()) {
			cx = customerService.findByPreferredUrl(param);
			if(cx != null){
				model.put("customer", cx);
				model.put("customizedLogin", "true");
				model.put("verifiedUrl", "true");
				
				if (cx.getCustSupportEmailEnable() != null) {
					String supportEnable = cx.getCustSupportEmailEnable();
					if (supportEnable.equals("true")) {
						toMail = cx.getCustSupportEmailId();
					}
				}
				cid = cx.getId();
			} else {
				model.put("customizedLogin", "true");
				model.put("verifiedUrl", "false");
			}
		}else {
			model.put("customizedLogin", "false");
		}
		
		String subject = "Contact Us::Mail From " + contact.getName();
		String desc = "Hi ,\n\n You have received a Mail from "+ contact.getName()
					+ "\n\n Description: "+contact.getDesc();
		
		CustomerUtils.customizeSupportEmail(cid, toMail, subject, desc, null);

		return _CCC.pages.getPage("facesix.login", "login");
	}
	
	@RequestMapping("/forgot")
	public String fotgot(Map<String, Object> model, @RequestParam(value="u",  required=false) String user, 
													@RequestParam(value="p",  required=false) String pwd,
													@RequestParam(value="re", required=false) String rem,
													@RequestParam(value="fgt",required=false) String fgt) {
	
		Contactus contact = new Contactus();
		model.put("fsobject", contact);
		model.put("Contactus", TAB_HIGHLIGHTER);
		return _CCC.pages.getPage("facesix.forgot", "forgot");
	}
	
	@RequestMapping("/reset")
	public String reset(Map<String, Object> model, @RequestParam(value="id",  required=true) String id, 
													@RequestParam(value="token",  required=true) String token) {
	
		UserAccount user = userAccountService.findById(id);

		String message = "Invalid User";
		Date now = new Date();
		
		boolean restStatus = user.getResetStatus();
		
		if (restStatus) {
			message = "Link has either expired or it has been already used";
		}
		
		boolean notExpried = withinTimeLimit(user.getResetTime(),now.getTime());
		
		if (user != null && restStatus && notExpried) {
			Contactus contact = new Contactus();
			model.put("fsobject", contact);
			model.put("uid", id);
			model.put("token", token);
			return _CCC.pages.getPage("facesix.reset", "reset");
		} else {
			model.put("reason", message);
			return _CCC.pages.getPage("facesix.notFound","notFound");
		}
	}	
	
		
	@RequestMapping(value="/welcome",method=RequestMethod.GET)
	public String welcome(Map<String, Object> model, @RequestParam(value="u",  required=false) String user, 
													 @RequestParam(value="p",  required=false) String pwd,
													 @RequestParam(value="re", required=false) String rem,
													 @RequestParam(value="fgt",required=false) String fgt,
													 @RequestParam(value = "url", required=false) String pref_url,
													 HttpServletRequest request, HttpServletResponse response) {
				
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			//return resolveWelcomePage(request, response);
			return _CCC.pages.getPage("facesix.home", "home");
		} else {
			if (!StringUtils.isEmpty(user) && !StringUtils.isEmpty(pwd)) {
				try {
					if (login(request, response, user, pwd)) {
						
						if (rem != null && rem.equals("on")) {
							Cookie c = new Cookie("userid", request.getSession().getId());
							c.setDomain(request.getServerName());
							c.setPath(request.getContextPath());
						    c.setMaxAge(365*24*60*60);
						    response.addCookie(c);
						}
						UserAccount account = userAccountService.findOneByUid(user);
						Boolean flag=true;
						Boolean customizedLogin = true;
						if(account != null){
							Customer cust = customerService.findById(account.getCustomerId());
							if (cust != null) {
								if (account.getVersion() == null && (pref_url != null && !pref_url.isEmpty())) {
									if (!pref_url.equalsIgnoreCase(cust.getCustomerName())) {
										customizedLogin = false;
									}
								}
								LOG.info("cust.getStatus is :" + cust.getStatus() + " account.getStatus is : " + account.getStatus());
								if (cust.getStatus().equalsIgnoreCase("inactive") || account.getStatus().equalsIgnoreCase("inactive")) {
									// if customer or user is inactive then
									// redirect to login page.
									model.put("messages", "Your account is not active at present");
									request.getSession().invalidate();
									flag = false;

								}

								if (pref_url != null && !pref_url.isEmpty()) {

									if (cust.getVersion() != null && !cust.getPreferedUrlName().equals(pref_url)) {
										// new customer using wrong url
										LOG.info(" url is not matching .....");
										LOG.info(" cust.getPreferedUrlName() " + cust.getPreferedUrlName()
												+ " pref_url " + pref_url);
										model.put("messages", "You are not allowed to access this link");
										request.getSession().invalidate();
										flag = false;

									} else if (!cust.getPreferedUrlName().equals(pref_url)) {
										// any existing customer using wrong url
										LOG.info(" not authorized to use customized login");
										model.put("messages", "You are not allowed to access this link");
										request.getSession().invalidate();
										flag = false;
									}
								}
							}

							if (account.getRole().equals("superadmin") && pref_url != null && !pref_url.isEmpty()) {
								if (customizedLogin) {
									LOG.info(" You are superadmin and pref url is " + pref_url);
									model.put("messages", "You are not allowed to access this link");
									request.getSession().invalidate();
									flag = false;
								}
							}

							if (!account.getRole().equals("superadmin") && (pref_url == null || pref_url.isEmpty())) {
								if (customizedLogin) {
									LOG.info("You are not superadmin and you are using superadmin portal!!!!!!!");
									model.put("messages", "You are not allowed to access this link");
									request.getSession().invalidate();
									flag = false;
								}
							}
						}
						if (flag) {
							LOG.info("User successfully logged in with id :" + user);
							account.setCount(account.getCount() + 1);
							userAccountService.saveContact(account);
							if (request.getSession().getAttribute("ORG_REQUEST_URI") != null) {
								String uri = (String) request.getSession().getAttribute("ORG_REQUEST_URI");
								request.getSession().removeAttribute("ORG_REQUEST_URI");
								try {
									response.sendRedirect(uri);
								} catch (IOException e) {
									return _CCC.pages.getPage("facesix.home", "home");
								}
							}
						}
						
					} else {
						model.put("messages", "Invalid username or password, please try again.");
						request.getSession().invalidate();
					}
				} catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			if (pref_url != null && !pref_url.isEmpty()) {
				Customer cx = customerService.findByPreferredUrl(pref_url);
				if (cx != null) {
					model.put("customer", cx);
				}
			}
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}
	
	private String resolveWelcomePage(HttpServletRequest request, HttpServletResponse response) {
		String c = _CCC.properties.getProperty("facesix.path2home");
		if (c != null) {
			try {
				response.sendRedirect(c);
				return null;
			} catch (Exception e) {
				return _CCC.pages.getPage("facesix.home", "home");
			}
		} else {
			return _CCC.pages.getPage("facesix.home", "home");
		}
	}
	
	public  boolean login(HttpServletRequest request, HttpServletResponse response, String user, String pwd) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return slogin(user, pwd) || glogin(user, pwd, request, response);
	}
	
	public  boolean slogin(String user, String pwd) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		if (_CCC.properties.isconfig("facesix.admin.user", user)  &&
			_CCC.properties.isconfig("facesix.admin.secret", _CCC.cryptor.encrypt(pwd))) {
			return true;
		} else {
			return false;
		}
	}
	
	public  boolean glogin(String user, String pwd, HttpServletRequest request, HttpServletResponse response) {
		try {
			GraylogRestClient graylogRestClient = new GraylogRestClient(_CCC.graylog.getRestUrl());
			SessionCreateRequest scr =  SessionCreateRequest.create(user, pwd, request.getRemoteAddr());
			ResponseEntity<SessionResponse> resp = graylogRestClient.invoke(HttpMethod.POST, "/system/sessions", scr, SessionResponse.class);
			if (resp.getStatusCode() == HttpStatus.OK && resp.getBody().sessionId() != null) {
				SessionUtil.authorizeSession(request.getSession(), user);
			}
		} catch (Throwable t) {
			LOG.warn("Login attempt failed with user :" + user, t);
			return false;
		}
		return true;
	}

	@RequestMapping("/goodbye")
	public String goodbye(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			Cookie c = cookies[i];            

			if (c.getName().equals("userid")) {
				if (c.getValue().equals(request.getSession().getId())) {
					c.setMaxAge(0);
					c.setValue(null);
					response.addCookie(c);    
				}
			} 
		}
		
		UserAccount account = userAccountService.findOneByEmail(SessionUtil.currentUser(request.getSession()));
		
		if(account != null) {
			long count  = account.getCount() ; 
			count 		= count - 1;
			if (count > 0) {
				account.setCount(count);
			} else {
				account.setCount(0);
			}
			userAccountService.saveContact(account);  
		}
		request.getSession().invalidate();

		return _CCC.pages.getPage("facesix.login", "login");
	}
	
	@RequestMapping("/theme")
	public String theme(Map<String, Object> model) {
		return "theme";
	}
	
	@RequestMapping("/page/{page}/{op}")
	public String page(Map<String, Object> model, @PathVariable("page") String page, @PathVariable("op") String op, HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {
			if (op != null) {
				return page + "-" + op;
			} else {
				return page;
			}
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}
	
	@RequestMapping("/page/{page}")
	public String page(Map<String, Object> model, @PathVariable("page") String page, HttpServletRequest request, HttpServletResponse response) {
		return page(model, page, null, request, response);
	}
	
	@RequestMapping("/page")
	public String page1(Map<String, Object> model, @RequestParam("page") String page, HttpServletRequest request, HttpServletResponse response) {
		return page(model, page, null, request, response);
	}
	
	@RequestMapping("/template/**")
	public String template(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		//return root(model, request, response);
		String path = request.getRequestURI();
		path = path.substring(path.indexOf("/template/") + 10 );
		return page(model, path, request, response);
	}
	
	@RequestMapping("/scan")
	public String scan(Map<String, Object> model, @RequestParam(value = "sid",  required  = false)  String sid,
			 									  @RequestParam(value = "spid",  required = false)  String spid,
			 									  @RequestParam(value = "uid",  required  = false)  String uid,
			 									  @RequestParam(value = "cid",  required  = false)  String cid,
			 									 @RequestParam(value = "clientmac",  required  = false)  String clientmac,
			 									  HttpServletRequest request, HttpServletResponse response) {		

		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			if (uid == null || uid.trim().isEmpty() || uid.trim().equals("?")) {
			} else {
				model.put("uid", uid);
			}	
			if (cid.isEmpty() || cid.equals("") || cid==null) {
				cid  = SessionUtil.getCurrentCustomer(request.getSession());
				LOG.info("scan CID is NULL" + cid);
			}
			prepare(model, request, response);
			model.put("sid",  sid);
			model.put("spid", spid);	
			model.put("cid",  cid);
			
			if (clientmac != null && !clientmac.isEmpty()) {
				model.put("clientmac",  clientmac);
			}
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 		CustomerUtils.Heatmap(cid));
			
			page = _CCC.pages.getPage("facesix.scan", "scan");
		}
		
		
		return page;
	}	
	
	@RequestMapping("/spots")
	public String spots(Map<String, Object> model, 
			@RequestParam(value = "cid", required = false) String  cid,
			@RequestParam(value = "sid", required = false) String  sid, 
			@RequestParam(value = "spid", required = false) String spid, 
			HttpServletRequest request, HttpServletResponse response) {
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			sessionCache.clearAttribute(request.getSession(), "sid", "suid", "spid", "spuid");
			
			if (cid == null || cid.isEmpty() || cid.equals("")) {
				cid  = SessionUtil.getCurrentCustomer(request.getSession());
				LOG.info("Spot CID is NULL" + cid);
			} 
			SessionUtil.setCurrentCustomer(request.getSession(), cid);
			prepare(model, request, response);
			
			List<Site> fsobjects = null;
			fsobjects = service.findByCustomerId(cid);
			
			if (fsobjects != null && fsobjects.size() > 0) {
				Site site    = fsobjects.get(0);
				sid 		 = site.getId();
			}
			
			model.put("cid", cid);
			if (!CustomerUtils.isRetail(cid)) {
				model.put("sid", sid);
				model.put("spid", spid);
			}

			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
			model.put("Retail", 	    CustomerUtils.isRetail(cid));
			
			page = _CCC.pages.getPage("facesix.spots", "spots");
		}
		
		
		return page;
	}
	
	@RequestMapping("/gwalerts")
	public String gwalerts(Map<String, Object> model, 
			@RequestParam(value = "cid", required = false) String cid, 
			HttpServletRequest request, HttpServletResponse response) {
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			if (cid.isEmpty() || cid.equals("") || cid == null) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			SessionUtil.setCurrentCustomer(request.getSession(), cid);
			prepare(model, request, response);
			
			model.put("cid", cid);
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
			
			page = _CCC.pages.getPage("facesix.gwalerts", "gwalerts");
		} 
		
		return page;
	}
	
	@RequestMapping("/gwreports")
	public String gwreports(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			if (cid.isEmpty() || cid.equals("") || cid == null) {
				cid  = SessionUtil.getCurrentCustomer(request.getSession());
			} 
			
			SessionUtil.setCurrentCustomer(request.getSession(), cid);
			prepare(model, request, response);
			
			model.put("cid", cid);
			Customer customer = customerService.findById(cid);
			if (customer != null) {
				model.put("customername", customer.getCustomerName());
			}
			model.put("sid", sid);
			model.put("spid",spid);
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
			
			page = _CCC.pages.getPage("facesix.gwreports", "gwreports");
		}
		
		return page;
	}
	
	@RequestMapping("/gwregdevices")
	public String gwregdevices(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			if (cid.isEmpty() || cid.equals("") || cid == null) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			} 
			
			SessionUtil.setCurrentCustomer(request.getSession(), cid);
			prepare(model, request, response);
			
			model.put("cid", cid);
			model.put("sid", sid);
			model.put("spid",spid);
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 		CustomerUtils.Heatmap(cid));
			
			page = _CCC.pages.getPage("facesix.gwregdevices", "gwregdevices");
		}
		
		
		return page;
	}
	
	@RequestMapping("/finderDeviceInfo")
	public String deviceInfo(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		model.put("cid", cid);
		model.put("sid", sid);
		model.put("spid",spid);
		
		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.beacon.deviceInfo", "deviceInfo");
	}
	
	@RequestMapping("/gwDeviceInfo")
	public String gwDeviceInfo(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			if (cid.isEmpty() || cid.equals("") || cid == null) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			} 
			
			SessionUtil.setCurrentCustomer(request.getSession(), cid);
			prepare(model, request, response);
			
			model.put("cid", cid);
			model.put("sid", sid);
			model.put("spid",spid);
			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 		CustomerUtils.Heatmap(cid));
			
			page = _CCC.pages.getPage("facesix.gwDeviceInfo", "gwDeviceInfo");
		}
		
		
		return page;
	}
	
	@RequestMapping("/drawfloor")
	public String drawfloor(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
	
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			prepare(model, request, response);
			
			model.put("sid", sid);
			model.put("spid",spid);

			model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
			model.put("Gateway", 		CustomerUtils.Gateway(cid));
			model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
			model.put("Heatmap", 	    CustomerUtils.Heatmap(cid));
			
			page = _CCC.pages.getPage("facesix.index", "index");
		}
		
		
		return page;
	}
	
	@RequestMapping("/captiveportal")
	public String captiveportal(
			Map<String, Object> model, 
			@RequestParam(value="id",  required=true) String id,
			HttpServletRequest request,
			HttpServletResponse response) {
		Gustpass gustpass = gustpassService.findById(id);
		model.put("fsobject", gustpass);
		model.put("ssid", false);
		return _CCC.pages.getPage("facesix.captiveportal", "captiveportal");
	}
	
	
	@RequestMapping(value = "/portalBg", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPlanFile(HttpServletRequest request, HttpServletResponse response) {
		
		try {
				
				String imgPath 	= request.getParameter("path");
				
				//LOG.info("PORTAL IMG PATH " +imgPath);
				
				if (imgPath != null && !imgPath.isEmpty()) {
					return ResponseEntity.ok(resourceLoader.getResource("file:" + imgPath));
				}
				
				if (imgPath == null || imgPath.isEmpty()) {
					String defaultPath = "./uploads/user.png";
					return ResponseEntity.ok(resourceLoader.getResource("file:" + defaultPath));
				}
			
		} catch (Exception e) {
			LOG.warn("Failed to load portal Bg pic :", e);
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@RequestMapping(value = "/preferredBackgroundUrl", method = RequestMethod.GET)
	@ResponseBody public ResponseEntity<?> preferredBackground(HttpServletRequest request, HttpServletResponse response) {
		
		String defaultPath  	= "./uploads/bground.jpg";
		Object resourcePath 	= resourceLoader.getResource("file:" + defaultPath);
		
		
		try {
			
			String id 				= request.getParameter("id");
			Customer customer  		= customerService.findById(id);
			
			LOG.info("preferredBackgroundUrl");
			LOG.info("cid " +id);
			
			if (id == null || id.isEmpty() || customer == null) {
				LOG.info("DEFAULT BG  " +resourcePath);
				return ResponseEntity.ok(resourcePath);
			}
	
			String bgpath = null;
			
			if (customer.getBackground() != null && !customer.getBackground().trim().isEmpty()) {
				bgpath = customer.getBackground();
			}
			
			Path path = Paths.get(bgpath);
			if (Files.exists(path)) {
				resourcePath = resourceLoader.getResource("file:" + bgpath);
			} else {
				resourcePath = resourceLoader.getResource("file:" + defaultPath);
			}
			
			LOG.info("BG  Path " +resourcePath);
			
			return ResponseEntity.ok(resourcePath);	
			
		} catch (Exception e) {
			return ResponseEntity.ok(resourcePath);	
		}
		
	}
	
	@RequestMapping(value = "/preferredLogoUrl", method = RequestMethod.GET)
	@ResponseBody public ResponseEntity<?> preferredLogo(HttpServletRequest request, HttpServletResponse response) {
		
		String defaultPath  = "./uploads/logo-home.png";
		
		Object resourcePath = resourceLoader.getResource("file:" + defaultPath);
		
		try {

			String id 			= request.getParameter("id");
			Customer customer   = customerService.findById(id);
			
			if (id == null || id.isEmpty() || customer == null) {
				//LOG.info("LOGO Path " +resourcePath);
				return ResponseEntity.ok(resourcePath);
			}
			
			String logoPath = null;
			if (customer.getLogofile() != null && !customer.getLogofile().trim().isEmpty()) {
				logoPath = customer.getLogofile();
			}
	
			Path path = null;
			if(logoPath != null){
				path = Paths.get(logoPath);
			}else{
				path = Paths.get(defaultPath);
			}
			if (Files.exists(path)) {
				resourcePath = resourceLoader.getResource("file:"+path);
			} else {
				resourcePath = resourceLoader.getResource("file:"+defaultPath);
			}
	
			//LOG.info("LOGO Path " +resourcePath);
			
			return ResponseEntity.ok(resourcePath);	
			
		} catch (Exception e) {
			return ResponseEntity.ok(resourcePath);	
		}
		
	}
	
	
	@RequestMapping("/{param}")
	public String customisedLogin(Map<String, Object> model, 
							@PathVariable(value = "param", required = false) String param,
			 			    HttpServletRequest request, HttpServletResponse response) {
		
		
		param 		= param.trim().toLowerCase();
		Customer cx = customerService.findByPreferredUrl(param);
		String message = "INVALID URL";
		
		if (cx != null) {
			model.put("customer", cx);
			HttpSession session = request.getSession();
			message = (String)session.getAttribute("message");
			if (message != null && !message.isEmpty()) {
				model.put("messages", message);
			}
			session.invalidate();
		} else {
			model.put("reason", message);
			return _CCC.pages.getPage("facesix.notFound","notFound");
		}
		return _CCC.pages.getPage("facesix.login", "login");
	}
	
	
	
	@RequestMapping("/reset/{param}")
	public String customizedReset(Map<String, Object> model, @RequestParam(value="id",  required=true) String id, 
													@RequestParam(value="token",  required=true) String token,
													 @PathVariable(value = "param",required=false) String param) {
	
		UserAccount user = userAccountService.findById(id);
		
		Date now = new Date();
		String message = "INVALID URL";
		
		if (user.getResetStatus()) {
			message = "Link has either expired or it has been already used";
		}
		
		boolean notExpried = withinTimeLimit(user.getResetTime(),now.getTime());
		
		if (user != null && user.getResetStatus() && notExpried) {

			Customer cx = customerService.findByPreferredUrl(param);
			
			Contactus contact = new Contactus();
			if (cx != null) {
				model.put("customer", cx);
			} else {
				model.put("reason", message);
				return _CCC.pages.getPage("facesix.notFound","notFound");
			}
			model.put("fsobject", contact);
			model.put("uid", id);
			model.put("token", token);
			return _CCC.pages.getPage("facesix.reset", "reset");
		} else {
			model.put("reason", message);
			return _CCC.pages.getPage("facesix.notFound","notFound");
		}
	}
	
	@RequestMapping("/contact/{param}")
	public String customizedContact(Map<String, Object> model,
			@PathVariable(value = "param",required=false) String param) {
		
		
		Contactus contact = new Contactus();
		model.put("fsobject", contact);
		model.put("Contactus", TAB_HIGHLIGHTER);
		Customer cx = customerService.findByPreferredUrl(param);
		
		if (cx != null) {
			model.put("customer", cx);
		} else {
			model.put("reason", "Invalid URL");
			return _CCC.pages.getPage("facesix.notFound","notFound");
		}
		return _CCC.pages.getPage("facesix.contact", "contact");
	}
	
	@RequestMapping("/forgot/{param}")
	public String customizedFotgot(Map<String, Object> model, 
								  @PathVariable(value = "param",required=true) String param) {
	
		Contactus contact = new Contactus();
		model.put("fsobject", contact);
		model.put("Contactus", TAB_HIGHLIGHTER);
		
		Customer cx = customerService.findByPreferredUrl(param);
		
		if (cx != null) {
			model.put("customer", cx);
		} else {
			model.put("reason", "Invalid URL");
			return _CCC.pages.getPage("facesix.notFound","notFound");
		}
		return _CCC.pages.getPage("facesix.forgot", "forgot");
	}
	
	@RequestMapping("/goodbye/{param}")
	public String customizedGoodbye(Map<String, Object> model, 
									@PathVariable(value = "param",required=false) String param,
									HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			Cookie c = cookies[i];            

			if (c.getName().equals("userid")) {
				if (c.getValue().equals(request.getSession().getId())) {
					c.setMaxAge(0);
					c.setValue(null);
					response.addCookie(c);    
				}
			} 
		}
		
		UserAccount account = userAccountService.findOneByEmail(SessionUtil.currentUser(request.getSession()));
		
		if(account != null) {
			long count  = account.getCount() ; 
			count 		= count - 1;
			if (count > 0) {
				account.setCount(count);
			} else {
				account.setCount(0);
			}
			userAccountService.saveContact(account);  
		}
		request.getSession().invalidate();

		Customer cx = customerService.findByPreferredUrl(param);
		if (cx != null) {
			model.put("customer", cx);
		} 
		return _CCC.pages.getPage("facesix.login", "login");
	}
	
	@RequestMapping(value = "/fgnotify", method = RequestMethod.GET)
	public void fgnotify(Map<String, Object> model, @ModelAttribute Contactus contact, HttpServletRequest request,
			HttpServletResponse response) {
		try {
		
			
			String param 		= request.getParameter("url");
			boolean bFound 		= true;
			UserAccount user 	= userAccountService.findOneByEmail(contact.getEmail());

			if (user == null) {
				user = userAccountService.findOneByUid(contact.getName());
				if (user == null) {
					bFound = false;
				} 
			}
			
			if (user != null) {
				String status = user.getStatus();
				if (CustomerUtils.INACTIVE().equalsIgnoreCase(status)) {
					LOG.info("Email Id " + contact.getEmail());
					LOG.info("inactive user not reset password contact to Qubercomm admin ");
					bFound = false;
				}
			}
			if (bFound && param != null && !param.isEmpty()) {
				Customer cx = customerService.findByPreferredUrl(param);
				if (cx != null) {
					String cid 	 = cx.getId();
					String userCid   = user.getCustomerId();
					if (userCid != null && !userCid.equals(cid)) {
						bFound = false;
					}
				}
			}

			LOG.info("eMail User Details" + contact.getEmail());
			
			if (bFound == true) {

				Date date 	 = new Date();
				String token = UUID.randomUUID().toString();
				
				user.setToken(token);
				user.setResetStatus(true);
				user.setResetTime(date.getTime());
				user = userAccountService.saveContact(user);

				String appUrl 	= CustomerUtils.cloudUrl() + request.getContextPath();

				constructResetTokenEmail(appUrl, request.getLocale(),token, user, param);

				String str = "/facesix/"+param;
				response.sendRedirect(str);
				
			} else {
				response.sendRedirect("/facesix/");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info(" fgnotify redirection error");
		}
		LOG.info("Forgot Mail Notification:");
	}
	
	
	void constructResetTokenEmail (String contextPath, Locale locale, String token, UserAccount user , String pref_url) {
		
		if (user != null) {
			
			String url 			 = contextPath + "/reset?id=" + user.getId() + "&token=" + token;
			String name 		 = null;
			
			if(pref_url != null && !pref_url.isEmpty()) {
				url = contextPath + "/reset/"+pref_url+"?id=" + user.getId() + "&token=" + token;
			}
			
			name 			= user.getFname();
			name 			= name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();// capitalize first letter
			String cid 		= user.getCustomerId();
			String body 	= "Hi " + name+",\n\n Please click below link to reset or activate your "
							+ "username / password. \n Note: The Below link will expire after 30 minutes.\n" + url;
			String toMail 	= user.getEmail();
			
			CustomerUtils.customizeSupportEmail(cid, toMail, "Reset Password", body, null);
		}
	}	
	
	@RequestMapping(value = "/changepwd", method = RequestMethod.POST)
	public String changepwd(Map<String, Object> model, @ModelAttribute Contactus contact, 
							@RequestParam( value="id",    required=false) String id,
							@RequestParam( value="token", required=false) String token,
							HttpServletRequest request, HttpServletResponse response) {
		try {

		LOG.info("Id" 		+ id);
		LOG.info("Token" 	+ token);
		
		UserAccount user 	= userAccountService.findById(id);
		
		Customer cx 			= null;
		String preferred_url 	= null;
		
		
			if (user != null && user.getResetStatus()) {
				if (user.getToken().equals(token)) {
					
					if (user.getCustomerId() !=null) {
						cx	= customerService.findById(user.getCustomerId());
						if (cx != null) {
							if (cx.getPreferedUrlName() != null && !cx.getPreferedUrlName().isEmpty()) {
								preferred_url		= cx.getPreferedUrlName();
							}
						}
						
					}
					accountWebController.setpasswd(model, user.getUid(), contact.getName());
					user.setResetStatus(false);
					user.setPassword(_CCC.cryptor.encrypt(contact.getName()));
					user = userAccountService.save(user, preferred_url);
				}
			
				HttpSession session = request.getSession(true);
				session.setAttribute("message", model.get("messages"));
				String str = "";
			
				if (preferred_url != null && !preferred_url.isEmpty()) {
					str = "/facesix/" + preferred_url;
				} else {
					str = "/facesix/";
				}
				response.sendRedirect(str);
		}
		
		LOG.info("Reset Notification:Change Password");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _CCC.pages.getPage("facesix.login", "login");
	}
	
	@RequestMapping(value = {"/portal/{portalUrl}"})
	public String QubercommPortal(Map<String, Object> model,
			@PathVariable(value = "portalUrl", required = true) String portalUrl,
			@RequestParam(value = "mac", required = true) String mac) {
		
		
		if (portalUrl != null && !portalUrl.isEmpty()) {
		
			 portalUrl 			 = portalUrl.trim().toLowerCase();
			 CaptivePortal portal = null;
			 portal = captivePortalService.findByPreferedUrl(portalUrl);
			
			if (mac != null) {
				mac = mac.replaceAll("/", "");
				model.put("mac",    mac);
			}

			LOG.info("Captive Portal URL " + portalUrl);
			LOG.info("Client Peer Mac "    + mac);
			
			if (portal != null) {
				model.put("portal", portal);
			}
		}
		
		return _CCC.pages.getPage("facesix.captive.portal", "captive.portal");
	}
	
	@RequestMapping("/GW_CustomizeAlert")
	public String customizeAlert(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		model.put("cid", cid);
		model.put("sid", sid);
		model.put("spid",spid);
		
		model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
		model.put("Gateway", 		CustomerUtils.Gateway(cid));
		model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.GWCustomizeAlert", "GWCustomizeAlert");
	}

	@RequestMapping("/mesh-topology")
	public String meshHome(Map<String, Object> model, 
			@RequestParam(value = "cid", required = true) String cid, 
			HttpServletRequest request, HttpServletResponse response) {
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			if (cid == null || cid.isEmpty() || cid.equals("")) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
		
			SessionUtil.setCurrentCustomer(request.getSession(), cid);
			prepare(model, request, response);
			
			model.put("cid", cid);
			model.put("Retail", 	    CustomerUtils.isRetail(cid));
			
			page = _CCC.pages.getPage("facesix.mesh-topology", "mesh-topology");
		} 
		
		return page;
	}
	
	@RequestMapping("/device_details")
	public String device_details(Map<String, Object> model, 
			@RequestParam(value = "cid", required = true) String cid, 
			HttpServletRequest request, HttpServletResponse response) {
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			if (cid == null || cid.isEmpty() || cid.equals("")) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			SessionUtil.setCurrentCustomer(request.getSession(), cid);
			prepare(model, request, response);
			
			model.put("cid", cid);
			model.put("Retail", 	    CustomerUtils.isRetail(cid));
			
			page = _CCC.pages.getPage("facesix.nmesh.device_details", "device_details");
		} 
		
		return page;
	}
	
	@RequestMapping("/client_details")
	public String client_details(Map<String, Object> model, 
			@RequestParam(value = "cid", required = true) String cid, 
			HttpServletRequest request, HttpServletResponse response) {
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			if (cid == null || cid.isEmpty() || cid.equals("")) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			SessionUtil.setCurrentCustomer(request.getSession(), cid);
			prepare(model, request, response);
			
			model.put("cid", cid);
			model.put("Retail", CustomerUtils.isRetail(cid));
			
			page = _CCC.pages.getPage("facesix.nmesh.clients_details", "clients_details");
		} 
		
		return page;
	}
	
	private boolean withinTimeLimit(long resetTime, long time) {
		long max_diff = 30*60*1000; // 30 minutes (min * sec * millisec)
		long diff = time - resetTime;
		if(diff <= max_diff){
			return true;
		}
		return false;
	}
}