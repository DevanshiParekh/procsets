package com.semaifour.facesix.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import com.semaifour.facesix.data.elasticsearch.ElasticsearchConfiguration;
import com.semaifour.facesix.data.graylog.GraylogConfiguration;
import com.semaifour.facesix.kiweb.KiwebConfiguration;
import com.semaifour.facesix.rest.ESQLRestController;
import com.semaifour.facesix.rest.FSqlRestController;
import com.semaifour.facesix.util.Cryptor;

@Component
public class CCC {

	@Autowired
	public ApplicationPages pages;
	
	@Autowired
	public GraylogConfiguration graylog;
	
	@Autowired
	public ApplicationProperties properties;
	
	@Autowired
	public ApplicationMessages messages;
	
	@Autowired
	public ElasticsearchConfiguration elasticsearch;
	
	//@Autowired
	//public MongodbConfiguration mongodb;
	
	@Autowired
	public KiwebConfiguration kiweb;
	
	@Autowired
	public ESQLRestController esqlRestController;
	
	@Autowired
	public FSqlRestController fsqlRestController;

	@Autowired
	public ElasticsearchTemplate elasticsearchTemplate;
	
	@Autowired
	public Cryptor cryptor;
	
	
}
