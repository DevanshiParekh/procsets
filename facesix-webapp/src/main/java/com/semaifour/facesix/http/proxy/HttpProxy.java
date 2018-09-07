package com.semaifour.facesix.http.proxy;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;

public class HttpProxy {
	Logger LOG = LoggerFactory.getLogger(HttpProxy.class.getName());

    private RestOperations restOperations;
    private String host;
    private int port;

    public HttpProxy(RestOperations restTemplate, String host, int port) {
        this.restOperations = restTemplate;
        this.host = host;
        this.port = port;
    }

    /**
     * Proxy a request without copying any headers.
     * 
     * @param httpRequest
     * @param httpResponse
     */
    public void proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        proxy(httpRequest.getServletPath(), httpRequest, httpResponse);
    }

    /**
     * Proxy a request and copy the given headers on both the request and the response.
     * 
     * @param httpRequest
     * @param httpResponse
     * @param headerNamesToCopy
     */
    public void proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String... headerNamesToCopy) {
        proxy(httpRequest.getServletPath(), httpRequest, httpResponse, 
        	  new DefaultRequestCallback(httpRequest, headerNamesToCopy),
        	  						     new DefaultResponseExtractor(httpResponse, headerNamesToCopy));
    }

    /**
     * Proxy a request, using the given path instead of the servlet path in the HttpServletRequest.
     * 
     * @param path
     * @param httpRequest
     * @param httpResponse
     * @param headerNamesToCopy
     */
    public void proxy(String path, HttpServletRequest httpRequest, HttpServletResponse httpResponse, 
    		          String... headerNamesToCopy) {
        proxy(path, httpRequest, httpResponse, 
        	  new DefaultRequestCallback(httpRequest, headerNamesToCopy), 
        	  							 new DefaultResponseExtractor(httpResponse, headerNamesToCopy));
    }

    /**
     * Specify your own request callback and response extractor. This gives you the most flexibility, but does the least
     * for you.
     * 
     * @param path
     * @param httpRequest
     * @param httpResponse
     * @param requestCallback
     * @param responseExtractor
     * @return
     */
    public <T> T proxy(String path, HttpServletRequest httpRequest, 
    		           HttpServletResponse httpResponse, RequestCallback requestCallback,
    		           ResponseExtractor<T> responseExtractor) {
        URI uri = buildUri(httpRequest, host, port, path);
        HttpMethod method = determineMethod(httpRequest);
        if (LOG.isInfoEnabled()) LOG.info("Proxying to URI: {} : {}", method.name(), uri);
        if (method == HttpMethod.POST) {
        	LOG.info(requestCallback.toString());
        }
        return restOperations.execute(uri, method, requestCallback, responseExtractor);
    }

    protected HttpMethod determineMethod(HttpServletRequest request) {
        return HttpMethod.valueOf(request.getMethod());
    }

    protected URI buildUri(HttpServletRequest httpRequest, String host, int port, String path) {
        try {
            return new URI("http", null, host, port, path, httpRequest.getQueryString(), null);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Unable to build URI, cause: " + ex.getMessage(), ex);
        }
    }
}