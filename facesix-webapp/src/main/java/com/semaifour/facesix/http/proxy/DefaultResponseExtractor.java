package com.semaifour.facesix.http.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseExtractor;

public class DefaultResponseExtractor implements ResponseExtractor<Void> {

	Logger LOG = LoggerFactory.getLogger(DefaultResponseExtractor.class.getName());

    private HttpServletResponse httpResponse;
    private String[] headerNamesToCopy;

    public DefaultResponseExtractor(HttpServletResponse httpResponse, String... headerNamesToCopy) {
        this.httpResponse = httpResponse;
        this.headerNamesToCopy = headerNamesToCopy;
    }

    @Override
    public Void extractData(ClientHttpResponse response) throws IOException {
        copyHeaders(httpResponse, response);
        InputStream body = response.getBody();
        if (body != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Copying the client HTTP response body to the servlet HTTP response");
            }
            FileCopyUtils.copy(response.getBody(), httpResponse.getOutputStream());
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("No body in the client HTTP response, so not copying anything to the servlet HTTP response");
        }

        httpResponse.setStatus(response.getRawStatusCode());
        body.close();
        return null;
    }

    
    protected void copyHeaders(HttpServletResponse httpResponse, ClientHttpResponse response) {
        if (headerNamesToCopy != null) {
            for (String name : headerNamesToCopy) {
                List<String> values = response.getHeaders().get(name);
                if (values != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Setting servlet HTTP header '%s' to '%s'", name, values);
                    }
                    for (String value : values) {
                        httpResponse.addHeader(name, value);
                    }
                }
            }
        } else {
            for (String name : response.getHeaders().keySet()) {
                List<String> values = response.getHeaders().get(name);
                if (values != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Setting servlet HTTP header '%s' to '%s'", name, values);
                    }
                    for (String value : values) {
                        httpResponse.addHeader(name, value);
                    }
                }
            }
        }
    }

}