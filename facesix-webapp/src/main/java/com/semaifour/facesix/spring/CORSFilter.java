package com.semaifour.facesix.spring;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CORSFilter
 * 
 * @author mjs
 *
 */
@Component
public class CORSFilter implements Filter {
	
	@Autowired
	ApplicationMessages applicationMessages;
	
	@Autowired
	ApplicationProperties applicationProperties;

	@Override
    public void doFilter(ServletRequest req, 
    					 ServletResponse res,
    					 FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Max-Age", "1000");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Cache-Control", "private");
        
       chain.doFilter(req, res);
    }

   
	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
}