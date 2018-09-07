package com.semaifour.facesix.http.proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.semaifour.facesix.web.WebController;

/**
 * 
 * 
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/proxy")
public class ElasticRestProxyController extends WebController {

	@Autowired
	ElasticRestProxy elasticRestProxy;
	 
    @RequestMapping("/es/**")
    @ResponseBody
    public void proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    	elasticRestProxy.proxy(proxyPath(httpRequest), httpRequest, httpResponse, (String[])null);
    }
    
    
	/**
	 * extract path
	 * 
	 * @param httpRequest
	 * @return
	 */
	public String proxyPath(HttpServletRequest httpRequest) {
    	String t = httpRequest.getServletPath();
    	int pos = t.indexOf("/proxy/");
    	t = t.substring(pos + 9);
    	return t;
    }


}