package com.semaifour.facesix.spring;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.codec.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.semaifour.facesix.web.WebController;
import com.semaifour.facesix.web.WelcomeController;

/**
 * AuthAuthFilter
 * 
 * @author mjs
 *
 */
@Component
public class AuthAuthFilter implements Filter {
	
	private static Logger LOG = LoggerFactory.getLogger(AuthAuthFilter.class.getName());

	
	@Autowired
	ApplicationMessages applicationMessages;
	
	@Autowired
	ApplicationProperties applicationProperties;
	
	@Autowired
	WelcomeController welcomeController;

	@Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

    	HttpServletRequest request = (HttpServletRequest) req;
    	HttpServletResponse response = (HttpServletResponse) res;
        String uri = request.getRequestURI().trim();
        for (String path : applicationProperties.getUnrestrictedUris()) {
        	if (uri.startsWith(path)) {
        		//if it's restricted
        		HttpSession session = request.getSession();
        		
        		if (session.isNew() || session.getAttribute("user") == null) {
        			//check if header has auth tokens
        			String auth = request.getHeader("Authorization");
        			if (!StringUtils.isEmpty(auth)) {
        				String[] tokens = auth.split(" ", 2);
        				auth = tokens[1];
        				auth = Base64.decodeToString(auth);
        				tokens = auth.split(":");
        				String login = tokens[0];
            			String pwd = tokens[1];
        				//do login using appid and secret
        				try {
							if (!welcomeController.login(request, response, login, pwd)) {
								if (uri.contains("/rest/")) {
									response.setHeader("WWW-Authenticate", "Basic realm=facesix");
									response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization Denied");
								} else {
									String qs = request.getQueryString();
									session.setAttribute("ORG_REQUEST_URI", qs == null ? uri : uri + "?" + request.getQueryString());
									response.sendRedirect(applicationProperties.getServerContextPath());
								}
							}
						} catch (IllegalBlockSizeException | BadPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			} else {
        				if (uri.contains("/rest/")) {
    						response.setHeader("WWW-Authenticate", "Basic realm=facesix");
    						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization Denied");
    					} else {
       						String qs = request.getQueryString();
    						session.setAttribute("ORG_REQUEST_URI", qs == null ? uri : uri + "?" + request.getQueryString());
    						response.sendRedirect(applicationProperties.getServerContextPath());
    					}        			
        			}
        		} else {
        			//check if ORG_REQUEST_URI is found,if so, redirect to it 
        			//to take user to the org url which was sent to log-in page
        			
        			if (!uri.contains("/rest/") && session.getAttribute("ORG_REQUEST_URI") != null && Boolean.getBoolean(request.getParameter("iourl")) == false) {
            			uri = (String) session.getAttribute("ORG_REQUEST_URI");
            			session.removeAttribute("ORG_REQUEST_URI");
						response.sendRedirect(uri);
        			}
        		}
        		break;
        	}
        }
        chain.doFilter(request, response);
    }
	
	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
}