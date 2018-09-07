package com.semaifour.facesix.data.elasticsearch;

import java.net.InetAddress;

import javax.annotation.PreDestroy;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.semaifour.facesix.http.proxy.ElasticRestProxy;
import com.semaifour.facesix.spring.ApplicationProperties;


/**
 * Elasticsearch Configuration
 * 
 * @author mjs
 *
 */

@Component
@EnableElasticsearchRepositories(basePackages = "com.semaifour.facesix")
public class ElasticsearchConfiguration {
	
	Logger LOG = LoggerFactory.getLogger(ElasticsearchConfiguration.class.getName());
	
	@Autowired
	ApplicationProperties properties;
	
	private Client client = null;
	private HttpClient httpClient = null;
	
	public ApplicationProperties properties() {
		return properties;
	}
	
	
	@Bean
	public Client client() {

		LOG.info("Connecting to ES :" + properties().getProperty("elasticsearch.host"));
		
		try {
			Settings settings = Settings.settingsBuilder().put("cluster.name", properties().getProperty("elasticsearch.cluster.name")).build();

			TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName(properties().getProperty("elasticsearch.host")),  properties().getInt("elasticsearch.port", 9300));
			
			client = TransportClient.builder().settings(settings).build().addTransportAddress(address);
			
			return client;

		} catch (Exception e) {
			LOG.error("Failed to connect to ES ", e);
		}
		return null;
	}

    @Bean
    public ElasticsearchOperations elasticsearchOperations() {
        return new ElasticsearchTemplate(client());
    }
    
    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() {
        return new ElasticsearchTemplate(client());
    }
    

    /**
     * Return a HttpProxy for the underlying Elasticsearch
     * 
     * @return
     */
    @Bean
	public ElasticRestProxy elasticsearchProxy() {
        BasicCredentialsProvider prov = new BasicCredentialsProvider();
        prov.setCredentials(new AuthScope(properties().getProperty("elasticsearch.host"),  
        								  properties().getInt("elasticsearch.http.port", 9200), 
        								  AuthScope.ANY_REALM), 
        								  new UsernamePasswordCredentials(properties().getProperty("elasticsearch.host"), properties().getProperty("elasticsearch.host")));
        httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(prov).build();
        RestTemplate restemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        return new ElasticRestProxy(restemplate, 
        					 properties().getProperty("elasticsearch.host"),  
        					 properties().getInt("elasticsearch.http.port", 9200));
    }
 
    
    @PreDestroy
    public void close() {
    	client.close();
		LOG.info("Closed Client to ES :" + properties().getProperty("elasticsearch.host"));
    }
    
}