package com.semaifour.facesix.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.semaifour.facesix.session.SessionCache;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor{

	Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	SessionCache sessionCache;
	
	@Autowired
	ApplicationMessages applicationMessages;
	
	@Autowired
	ApplicationProperties applicationProperties;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model) throws Exception {
		if (model != null) {
			Map<String, Object> privs = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			
			if (privs != null) model.getModel().putAll(privs);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
