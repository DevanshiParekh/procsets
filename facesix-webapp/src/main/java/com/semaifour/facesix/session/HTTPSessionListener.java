package com.semaifour.facesix.session;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.util.SessionUtil;

@Controller
public class HTTPSessionListener implements HttpSessionListener{
	
	static Logger LOG = LoggerFactory.getLogger(HTTPSessionListener.class.getName());
	
	@Autowired
	UserAccountService userAccountService;
	
	
  public void sessionCreated(HttpSessionEvent event){
	  LOG.info(" Session Created.. ");
	  event.getSession().setMaxInactiveInterval(3600);
  }
	  
  public void sessionDestroyed(HttpSessionEvent event){
		  String uid 			 = SessionUtil.currentUser(event.getSession());
		  UserAccount account    = userAccountService.findOneByEmail(uid);
	
		if (account != null){
			LOG.info("Session Destroyed with uid.. " + account.getEmail());
			
			long count  = account.getCount();
			count 		= count - 1;
			
			if (count > 0) {
				account.setCount(count);
			} else {
				account.setCount(0);
			}
			userAccountService.saveContact(account);	
			
			}
			event.getSession().invalidate();
	  }
	}
