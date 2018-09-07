package com.semaifour.facesix.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.data.graylog.GraylogRestClient;
import com.semaifour.facesix.web.WebController;

@RestController
@RequestMapping("/rest/gl")
public class GraylogRestController  extends WebController {
	
	static Logger LOG = LoggerFactory.getLogger(GraylogRestController.class.getName());
	
    @RequestMapping("/invoke/**")
    public @ResponseBody String invoke(HttpServletRequest request, HttpServletResponse response) {
    	StringBuffer path = request.getRequestURL();
    	//chop '/facesix/rest/gl/invoke' from URI, the rest is the GL rest path
    	String restpath = path.substring(path.indexOf("invoke") + 6) + "?" + request.getQueryString();
    	GraylogRestClient graylogRestClient = new GraylogRestClient(_CCC.graylog);
    	
		ResponseEntity<String> glresponse = graylogRestClient.invoke(restpath, extractRequestBody(request, response),String.class);
    	return glresponse.getBody().toString();
    }
    
}