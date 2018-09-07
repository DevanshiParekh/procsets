package com.semaifour.facesix.impl.finder;


import java.io.IOException;
import java.util.Map;
import java.util.Properties;

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

import org.graylog2.rest.models.system.sessions.requests.SessionCreateRequest;
import org.graylog2.rest.models.system.sessions.responses.SessionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.Font;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.web.BeaconWebController;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.graylog.GraylogRestClient;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.session.Contactus;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

/**
 * 
 * Welcome Controller for the webapp
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/finder")
public class FinderWelcomeController extends WebController {
	
	static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	static Logger LOG = LoggerFactory.getLogger(FinderWelcomeController.class.getName());
	
	@Autowired
	BeaconWebController beaconWebController;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	UserAccountService userService;
	
	@Autowired
	CustomerUtils customerUtils;
		
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
			
	@RequestMapping("/contact")
	public String contact(Map<String, Object> model) {
		Contactus contact = new Contactus();
		model.put("fsobject", contact);
		model.put("Contactus", TAB_HIGHLIGHTER);		
		return _CCC.pages.getPage("facesix.contact", "contact");
	}	
	
	@RequestMapping(value = "/sendmail", method = RequestMethod.POST)
	public String sendmail(Map<String, Object> model, @ModelAttribute Contactus contact) {
		
		Properties props = new Properties();
		LOG.info("contactus notification: sendmail");
		
		props.put("mail.smtp.host",	 			Contactus.smtp);
		props.put("mail.smtp.port", 			Contactus.port);		
		props.put("mail.smtp.auth", 			"true");
		props.put("mail.smtp.starttls.enable", 	"true");
			
	    Session session = Session.getInstance(props,new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Contactus.user_name, Contactus.passwd);
			}
	    });
	    
		try{  
			MimeMessage message = new MimeMessage(session);  
			message.setFrom(new InternetAddress(contact.getEmail()));  
			message.addRecipient(RecipientType.TO, new InternetAddress(Contactus.to_addr));  
			message.setSubject("Qubercloud Contactus::Mail From "+contact.getName());  
			message.setText(contact.getDesc());
   
			// Send message  
			Transport.send(message);  
   
		}catch (MessagingException mex) {
			mex.printStackTrace();
		}	
		
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
	
		Contactus contact = new Contactus();
		model.put("fsobject", contact);
		model.put("uid", id);
		model.put("token", token);
		return _CCC.pages.getPage("facesix.reset", "reset");
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
		return root(model, request, response);
	}

}