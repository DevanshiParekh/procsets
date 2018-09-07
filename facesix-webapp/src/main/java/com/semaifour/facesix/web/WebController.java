package com.semaifour.facesix.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.semaifour.facesix.session.SessionCache;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.SessionUtil;

/**
 * 
 * Base Controller Controller for the webapp
 * 
 * @author mjs
 *
 */
public class WebController {
	
	static Logger LOG = LoggerFactory.getLogger(WebController.class.getName());
	
	public static final List<Map<String, Object>> EMPTY_LIST_MAP = new ArrayList<Map<String, Object>>();
	public static final Map<String, Map<String, Object>> EMPTY_MAP_MAP = new HashMap<String, Map<String, Object>>();
	
	public static String TAB_HIGHLIGHTER = "underline";

	@Autowired
	protected SessionCache sessionCache;
	
	@Autowired
	protected CCC _CCC;
	
	/**
	 * Return current user
	 * 
	 * @return
	 */
	public String getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
		return SessionUtil.currentUser(request.getSession());
	}
	
	/**
	 * Return current user
	 * @return
	 */
	public String whoami(HttpServletRequest request, HttpServletResponse response) {
		return getCurrentUser(request, response);
	}
	
	/**
	 * Return current date time. Basically 'return new Date()'.
	 * 
	 * @return
	 */
	public Date now() {
		return new Date();
	}
	
	public void pre(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		if (_CCC.properties.isconfig("facesix.admin.user", SessionUtil.currentUser(request.getSession()))) {
			model.put("canedit", "true");
		}
	}
	
	/**
	 * 
	 * Prepares model object with basic attrs for display
	 * 
	 * @param model
	 */
	protected void prepare(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		model.put("user", SessionUtil.currentUser(request.getSession()));
		model.put("sid", sessionCache.getAttribute(request.getSession(), "sid"));
		model.put("suid", sessionCache.getAttribute(request.getSession(), "suid"));
		model.put("spid", sessionCache.getAttribute(request.getSession(), "spid"));
		model.put("spuid", sessionCache.getAttribute(request.getSession(), "spuid"));
		model.put("cid", sessionCache.getAttribute(request.getSession(), "cid"));
	}
	
	protected void post(Map<String, Object> model) {
	}
	
	protected MultiValueMap<String, String> requestBody(HttpServletRequest request, HttpServletResponse response) {
		MultiValueMap<String, String> requestbody = new LinkedMultiValueMap<String, String>();
		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			for (String v : entry.getValue()) {
				requestbody.add(entry.getKey(), v);
			}
		}
		return requestbody;
	}
	
	public String stream2String(InputStreamReader ipsr) {
		BufferedReader rd = new BufferedReader(ipsr);
		StringBuilder output = new StringBuilder();
		try {
			String line = null;
			while ((line = rd.readLine()) != null) {
				output.append(line).append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
	
	public String executeHTTPRequest(HttpUriRequest request) throws IOException {
		String result = null;
		CloseableHttpClient client =  HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request);
			if (response.getEntity() != null) {
				
				result = stream2String(new InputStreamReader(response.getEntity().getContent()));
			}
			if (response.getStatusLine().getStatusCode() != 200) {
				LOG.error(request.getMethod() + " " + request.getURI() + "    " + response.getStatusLine() );
			}
		} catch (Exception e) {
			LOG.warn("Proxy Failed  for HTTP Request :" + request.getURI(), e);
		} finally {
			response.close();
			client.close();
		}
		return result;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String extractRequestBody(HttpServletRequest request, HttpServletResponse response) {
		try {
			return stream2String(new InputStreamReader(request.getInputStream()));
		} catch (Exception e) {
			return null;
		}
	}
	
}