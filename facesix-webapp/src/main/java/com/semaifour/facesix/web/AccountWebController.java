package com.semaifour.facesix.web;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.graylog2.restclient.models.api.requests.ChangePasswordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Font;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.graylog.GraylogRestClient;
import com.semaifour.facesix.session.Contactus;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;

/**
 * 
 * AccountWebController  for the webapp
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/web/account")
public class AccountWebController extends WebController {
	
	static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	static Logger LOG = LoggerFactory.getLogger(AccountWebController.class.getName());
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	CCC _CCCC;
	
	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired 
	WelcomeController welcomeController;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@RequestMapping(value = {"", "/list"})
	public String list(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
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
			return _CCC.pages.getPage("facesix.login", "login");
		}
		return _CCC.pages.getPage("facesix.home", "home");
	}

	@RequestMapping(value="profile", method = RequestMethod.POST)
	public String profilePost(Map<String, Object> model, @ModelAttribute UserAccount account, @RequestParam( value="user_pic", required=false) MultipartFile proFile, HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {
			UserAccount user = userAccountService.findOneByUid(SessionUtil.currentUser(request.getSession()));
			if (user == null)  {
				user = new UserAccount();
				user.setUid(SessionUtil.currentUser(request.getSession()));
			}
			//if (account.getId() != null) {
				user.setFname(account.getFname());
				user.setLname(account.getLname());
				user.setGroup(account.getGroup());
				user.setDescription(account.getDescription());
				user.setEmail(account.getEmail());
				user.setPhone(account.getPhone());
				user.setPassword(_CCC.cryptor.iencrypt(account.getPassword()));
				user.setJedittings(account.getJedittings());
				user = userAccountService.save(user);

			//}
			if(!proFile.isEmpty() && proFile.getSize() > 1) {
				try {
					Path path = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSPROFILES_"), (user.getId() + "_" + proFile.getOriginalFilename()));
					Files.createDirectories(path.getParent());
					Files.copy(proFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					user.setPath(path.toString());
					user = userAccountService.save(user);
				} catch (IOException e) {
					LOG.warn("Failed to save profile pic file", e);
				}
			}
			
			
			if (StringUtils.isNotEmpty(account.getPassword()) && StringUtils.equals(account.getPassword(), request.getParameter("c_password"))) {
				setpasswd (model, user.getUid(), account.getPassword());
			}

			model.put("user", user);
			String cid = SessionUtil.getCurrentCustomer(request.getSession());
			model.put("cid", cid);
			
			return _CCC.pages.getPage("facesix.account", "account");
		} else {
			return _CCC.pages.getPage("facesix.login", "login");
		}
	}
	
	/**
	 * Returns floor plan file content
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/profilepic", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPlanFile(HttpServletRequest request, HttpServletResponse response) {
		
		try {

			if (SessionUtil.isAuthorized(request.getSession())) {
				
				String userId 			= request.getParameter("userid");
				UserAccount account 	= null;
				
				if (userId != null) {
					account = userAccountService.findById(userId);
				}
				
				if (account != null && account.getPath() != null) {
					return ResponseEntity.ok(resourceLoader.getResource("file:" + account.getPath()));
				}
				
				if (account != null && account.getPath() == null) {
					String defaultPath = "./uploads/user.png";
					return ResponseEntity.ok(resourceLoader.getResource("file:" + defaultPath));		
				}
			}
			
		} catch (Exception e) {
			LOG.warn("Failed to load profile pic :", e);
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.notFound().build();
	}

	
	@RequestMapping(value="profile", method = RequestMethod.GET)
	public String profile(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {
			UserAccount user = userAccountService.findOneByUid(SessionUtil.currentUser(request.getSession()));
			if (user == null)  {
				user = new UserAccount();
				user.setUid(SessionUtil.currentUser(request.getSession()));
			}
			String cid = SessionUtil.getCurrentCustomer(request.getSession());
			model.put("cid", cid);
			model.put("user", user);
			
			return _CCC.pages.getPage("facesix.account", "account");
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
			
			boolean isalive = true;
			if (user != null) {
				isalive = user.getStatus().equalsIgnoreCase(customerUtils.INACTIVE());
			}
			
			if (bFound == true && isalive) {
				String token = UUID.randomUUID().toString();
				user.setToken(token);
				user = userAccountService.saveContact(user);

				String appUrl 	= customerUtils.cloudUrl()+ request.getContextPath();

				SimpleMailMessage email = constructResetTokenEmail(appUrl, request.getLocale(),
						token, user, param);
				if (email != null) {
					this.mailSender.send(email);
				}
			}

			String str = "/facesix/" + param;
			response.sendRedirect(str);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info(" fgnotify redirection error");
		}
		LOG.info("Forgot Mail Notification:");
	}
	
	
	SimpleMailMessage constructResetTokenEmail (String contextPath, Locale locale, String token, UserAccount user , String pref_url) {
		
		String url 			 = contextPath + "/reset?id=" + user.getId() + "&token=" + token;
		String default_email = "support@qubercomm.com";
		String from_mail 	 = null;
		String 	name 		 = null;
		Customer customer    = null;
		
		if(pref_url != null && !pref_url.isEmpty()) {
			url = contextPath + "/reset/"+pref_url+"?id=" + user.getId() + "&token=" + token;
		}
		user.setResetStatus(true);
		user = userAccountService.saveContact(user);
		
		if(user != null) {
			name 		= user.getFname();
			name 		= name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();// capitalize first letter
			String cid  = user.getCustomerId();
			customer 	= customerService.findById(cid);
			
			if (customer != null && pref_url == null) {
				String customer_pref_url = customer.getPreferedUrlName();
				if (!customer_pref_url.equals(pref_url)) {
					return null;
				}
				from_mail = customer.getEmail();
			} else {
				from_mail = default_email;
			}
		}
		SimpleMailMessage email = new SimpleMailMessage();
		email.setFrom(from_mail);
		email.setTo(user.getEmail());
	    email.setSubject("Reset Password");
	    email.setText ("Hi " + name+",\n Please click below link to reset or activate your user name / password " + url);
        	    
		return email;
	}	
	
	@RequestMapping(value = "/changepwd", method = RequestMethod.POST)
	public void changepwd(Map<String, Object> model, @ModelAttribute Contactus contact, 
							@RequestParam( value="id",    required=false) String id,
							@RequestParam( value="token", required=false) String token,
			HttpServletRequest request, HttpServletResponse response) {
		try {

			String param 	 = request.getParameter("url");
			UserAccount user = userAccountService.findById(id);

			if (user != null && user.getResetStatus()) {
				boolean isalive = user.getStatus().equalsIgnoreCase(customerUtils.ACTIVE());
				if (user.getToken().equals(token) && isalive) {
					LOG.info("user identified");
					setpasswd(model, user.getUid(), contact.getName());
					user.setResetStatus(false);
					// Found the User, Set the Password
					user.setPassword(_CCC.cryptor.encrypt(contact.getName()));
					user = userAccountService.save(user);
				}
			}

			LOG.info("Reset Notification:Change Password");
			String str = "/facesix/" + param;
			response.sendRedirect(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		//return _CCC.pages.getPage("facesix.login", "login");
	}
	
	void setpasswd (Map<String, Object> model, String user, String pwd) {
		ChangePasswordRequest pwdr = new ChangePasswordRequest();
		pwdr.setPassword(pwd);
		GraylogRestClient graylogRestClient = new GraylogRestClient(_CCC.graylog.getRestUrl(), _CCC.graylog.getPrincipal(), _CCC.graylog.getSecret());
		try {
			ResponseEntity<Object> response = graylogRestClient.invoke(HttpMethod.PUT, "/users/" + user + "/password", pwdr, Object.class);
			switch(response.getStatusCode().value()) {
				case 204:
					model.put("pwdmsg", "The password was successfully updated. Subsequent requests must be made with the new password.");
					break;
				case 400:
					model.put("pwdmsg", "If the old or new password is missing.");
					break;
				case 403:
					model.put("pwdmsg", "If the requesting user has insufficient privileges to update the password for the given user or the old password was wrong.");
					break;
				case 404:
					model.put("pwdmsg", "If the user does not exist.");
					break;
			}
		} catch (Exception e) {
			model.put("pwdmsg", "Failed to change password.");
			LOG.warn("Failed to change password ", e);
		}		
	}		

}