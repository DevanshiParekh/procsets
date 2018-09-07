package com.semaifour.facesix.http.proxy;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RequestCallback;

public class DefaultRequestCallback implements RequestCallback {
	
	Logger LOG = LoggerFactory.getLogger(DefaultRequestCallback.class.getName());

    private HttpServletRequest httpRequest;
    private String[] headerNamesToCopy;

    /**
     * TODO May want the ability to specify header names to exclude instead of including.
     */
    public DefaultRequestCallback(HttpServletRequest httpRequest, String... headerNamesToCopy) {
        this.httpRequest = httpRequest;
        this.headerNamesToCopy = headerNamesToCopy;
    }

    @Override
    public void doWithRequest(ClientHttpRequest request) throws IOException {
        copyHeaders(httpRequest, request);
        FileCopyUtils.copy(httpRequest.getInputStream(), request.getBody());
        if (true) {
        	LOG.info(request.toString());
        }
    }

    protected void copyHeaders(HttpServletRequest httpRequest, ClientHttpRequest request) {
        if (headerNamesToCopy != null) {
            for (String name : headerNamesToCopy) {
                String value = httpRequest.getHeader(name);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Setting client HTTP header '{}' to '{}'", name, value);
                }
                request.getHeaders().set(name, value);
            }
        } else {
        	Enumeration<String> headers = httpRequest.getHeaderNames();
        	String name = null;
            while (headers.hasMoreElements()){
            	name = headers.nextElement();
                String value = httpRequest.getHeader(name);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Setting client HTTP header '{}' to '{}'", name, value);
                }
                request.getHeaders().set(name, value);
            }
        }
    }

    public void setHeaderNamesToCopy(String[] headerNamesToInclude) {
        this.headerNamesToCopy = headerNamesToInclude;
    }

}