package com.semaifour.facesix.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.fsql.FSField;
import com.semaifour.facesix.fsql.FSql;
import com.semaifour.facesix.fsql.FSqlResultsExtractor;
import com.semaifour.facesix.fsql.FSqlResultsMapExtractor;
import com.semaifour.facesix.fsql.Sort;
import com.semaifour.facesix.web.WebController;

@RestController
@RequestMapping("/rest/fsql")
public class FSqlRestController  extends WebController {
	
	static Logger LOG = LoggerFactory.getLogger(FSqlRestController.class.getName());
	
	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

	@RequestMapping("/query/datatable")
	public @ResponseBody Map<String, Object> query4datatable(@RequestParam("fsql")String fsql) {
		Map<String, Object> datatable = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> list = query(fsql);
			datatable.put("data", list);
			FSql fsqlo = FSql.parse(fsql);
			List<Map<String, String>> cols = new ArrayList<Map<String, String>>();
			//if (list.size() > 0) {
				//for (final String c : list.get(0).keySet()) {
				for(final FSField f : fsqlo.getFileds()) {
					cols.add(new HashMap<String, String>() { { 	put("title", f.getOcolumn());
																put("data", f.getOcolumn());
																put("defaultContent", "");
															} });
				}
			//}	
			datatable.put("columns", cols);
		} catch (Exception e) {
			LOG.warn("Error querying datatable for fsql : %s", fsql, e);
			datatable.put("error", e.getMessage());
			datatable.put("fsql", fsql);
		}
		
		return datatable;
	}

	@RequestMapping(value = {"query", "query/newlist"})
	public @ResponseBody List<Map<String, Object>> newquery(@RequestParam("fsql")String fsql) {
		try {
			FSql fsqlo = FSql.parse(fsql);
	    	QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(fsqlo.getQuery());
	    	
	    	Client client = elasticsearchTemplate.getClient();
	    	
	    	SearchResponse response = null;
	    	SearchRequestBuilder request = client.prepareSearch(fsqlo.getIndex());
	   
	    	request.setIndices(fsqlo.getIndex());
	    	
	    	request.setQuery(queryBuilder);
	    
	    	if (fsqlo.getType() != null) {
	    		request.setTypes(fsqlo.getType());
	    	}
	    	request.setFrom(fsqlo.getFrom());
	    	request.setSize(fsqlo.getSize());
	    
	    	if (fsqlo.getSort() != null) {
	    		for (Sort sort : fsqlo.getSorts()) {
	    			request.addSort(SortBuilders.fieldSort(sort.getField()).order(SortOrder.valueOf(sort.getOrder())));
	    		}
	    	}
	    	
	    	FSqlResultsExtractor rse = new FSqlResultsExtractor(fsqlo);
	    	
	    	request.setTimeout(fsqlo.getTimeout());
	    	
	    	if (LOG.isDebugEnabled())  LOG.debug("ESQL :" + request.toString());
	    	
	    	response = request.get();
	    	
	    	List<Map<String, Object>> doclist = rse.extract(response);
	    	
	    	return doclist;
		} catch (Exception e) {
			LOG.warn("Error querying data for fsql : %s", fsql, e);
		}
		return EMPTY_LIST_MAP;
	}
	
	@RequestMapping(value = {"query", "query/list"})
	public @ResponseBody List<Map<String, Object>> query(@RequestParam("fsql")String fsql) {
		try {
			FSql fsqlo = FSql.parse(fsql);
			
			List<SortBuilder> sorts = new ArrayList<SortBuilder>();
	    	if (fsqlo.getSort() != null) {
	    		for (Sort sort : fsqlo.getSorts()) {
	    			sorts.add(SortBuilders.fieldSort(sort.getField()).order(SortOrder.valueOf(sort.getOrder())));
	    		}
	    	}
	    	
	    	QueryBuilder builder = QueryBuilders.queryStringQuery(fsqlo.getQuery());
	    	
	    	SearchQuery sq = new NativeSearchQuery(builder, null, sorts);
	    	
	    	if (fsqlo.getIndex() != null) {
	    		sq.addIndices(fsqlo.getIndex());
	    	}
	    	
	    	if (fsqlo.getType() != null) {
	    		sq.addTypes(fsqlo.getType());
	    	}
	    	
	    	sq.setPageable(new PageRequest(0,fsqlo.getSize()));
	    	
	    	FSqlResultsExtractor rse = new FSqlResultsExtractor(fsqlo);
	    	if (LOG.isDebugEnabled()) LOG.debug("ESQL :" + sq.getQuery().toString().replaceAll(System.lineSeparator(), " "));
	    	List<Map<String, Object>> doclist = elasticsearchTemplate.query(sq, rse);
	    	return doclist;
		} catch (Exception e) {
			LOG.warn("Error querying data for fsql : %s", fsql, e);
		}
		return EMPTY_LIST_MAP;
	}
	
	@RequestMapping("query/map")
	public @ResponseBody Map<String, Map<String, Object>> queryMap(@RequestParam("fsql")String fsql, @RequestParam("keyfield") String keyfield) {
		try {
			FSql fsqlo = FSql.parse(fsql);
			
			List<SortBuilder> sorts = new ArrayList<SortBuilder>();
	    	if (fsqlo.getSort() != null) {
	    		for (Sort sort : fsqlo.getSorts()) {
	    			sorts.add(SortBuilders.fieldSort(sort.getField()).order(SortOrder.valueOf(sort.getOrder())));
	    		}
	    	}

	    	
	    	QueryBuilder builder = QueryBuilders.queryStringQuery(fsqlo.getQuery());
	    	
	    	SearchQuery sq = new NativeSearchQuery(builder, null, sorts);
	    	if (fsqlo.getIndex() != null) {
	    		sq.addIndices(fsqlo.getIndex());
	    	}
	    	
	    	if (fsqlo.getType() != null) {
	    		sq.addTypes(fsqlo.getType());
	    	}
	    	
	    	sq.setPageable(new PageRequest(0,fsqlo.getSize()));
	    	
	    	FSqlResultsMapExtractor rse = new FSqlResultsMapExtractor(fsqlo, keyfield);
	    	return elasticsearchTemplate.query(sq, rse);
	    	
	    	
		} catch (Exception e) {
			LOG.warn("Error querying data for fsql : %s", fsql, e);
		}
		return EMPTY_MAP_MAP;
	}
	
	@RequestMapping("scroll")
	public Object scan(@RequestParam("fsql")String fsql) {
		
		FSql fsqlo = FSql.parse(fsql);

    	QueryBuilder qbuilder = QueryBuilders.queryStringQuery(fsqlo.getQuery());
    	SortBuilder sbuilder = SortBuilders.fieldSort("field");

		SearchQuery searchQuery = new NativeSearchQueryBuilder()
	    .withQuery(qbuilder)
	    .withIndices(fsqlo.getIndex())
	    .withTypes(fsqlo.getType())
	    .withPageable(new PageRequest(0,1))
	    .build();
		
		String scrollId = elasticsearchTemplate.scan(searchQuery,1000,false);
		List<Map> sampleEntities = new ArrayList<Map>();
		boolean hasRecords = true;
		while (hasRecords) {
		    Page<Map> page = elasticsearchTemplate.scroll(scrollId, 5000L , new ResultsMapper ()
		    {
		        public Page<Map> mapResults(SearchResponse response) {
		            List<Map> chunk = new ArrayList<Map>();
		            for(SearchHit searchHit : response.getHits()){
		                if(response.getHits().getHits().length <= 0) {
		                    return null;
		                }
		                Map user = new HashMap();
		                //user.setId(searchHit.getId());
		                //user.setMessage((String)searchHit.getSource().get("message"));
		                chunk.add(user);
		            }
		            return new PageImpl<Map>(chunk);
		        }

				@Override
				public <T> Page<T> mapResults(SearchResponse response,
						Class<T> clazz, Pageable pageable) {
					// TODO Auto-generated method stub
		            List<Map> chunk = new ArrayList<Map>();
		            for(SearchHit searchHit : response.getHits()){
		                if(response.getHits().getHits().length <= 0) {
		                    return null;
		                }
		                Map user = new HashMap();
		                //user.setId(searchHit.getId());
		                //user.setMessage((String)searchHit.getSource().get("message"));
		                chunk.add(user);
		            }
		            return (Page<T>) new PageImpl<Map>(chunk);				}

				@Override
				public <T> T mapResult(GetResponse response, Class<T> clazz) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public <T> LinkedList<T> mapResults(MultiGetResponse responses,
						Class<T> clazz) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public EntityMapper getEntityMapper() {
					// TODO Auto-generated method stub
					return null;
				}
		    });
		    if(page != null) {
		        sampleEntities.addAll(page.getContent());
		        hasRecords = page.hasNext();
		    }
		    else{
		        hasRecords = false;
		    }
		}
		return null;
	}
	
	
	/* Try 
	 * http://localhost:8175/facesix/rest/fsql/ssql or 
	 * locatum.qubercomm.com/facesix/reset/fsql/ssql
	 * 
	 */
	@RequestMapping("/ssql")
	public @ResponseBody String ssql(@RequestParam(value="query" ,required = false)String fsql) {
		
		 String inputLine = null;
		 
		try {
			
			fsql = "user = imongo?collection=userAccount\n"+ 
					"customer = imongo?collection=customer\n"+ 
					"device = ielastic?index=fsi-device-qubercloud/device\n"+ 
					"device = ssql?sql=select * from device\n" + 
					"result = ssql?sql=select user.fname, user.role, user.uid as uuid , contactPerson, customerName,"
							+ "device.uid as duid from user, customer, device where user.customerId = customer.pkid AND device.cid = customer.pkid LIMIT 1\n" + 
					"return?view=result&as=column\n" + 
					"close";
			
			  LOG.info("fsql " +fsql);
			  
			  byte[] postData     = fsql.getBytes( StandardCharsets.UTF_8 );
			  int  postDataLength = fsql.length();
			  
			  
			 String  url = "http://locatum.qubercomm.com:8191/progsets/rest/psql/exe";

				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setDoOutput( true );
				con.setInstanceFollowRedirects(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "text/psql");
				con.setRequestProperty ("Authorization", "Basic admin:nimda");				
				con.setRequestProperty( "charset", "utf-8");
				con.setRequestProperty( "Accept", "applicaton/json,text/plain,*/*");
				con.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
				con.setUseCaches( false );
				con.getOutputStream().write(postData);
				
			   
				InputStream is   = con.getInputStream();
				
				if (is != null) {
					BufferedReader in 	= new BufferedReader( new InputStreamReader(is));
					inputLine 	= in.readLine();
					in.close();							
				}
					return inputLine;
		} catch (Exception e) {
			LOG.warn("Error querying data for ssql "+e);
		}
		return inputLine;
	}
	
	@RequestMapping("/save/query")
	public @ResponseBody String saveQuery(@RequestParam(value="query" ,required = false)String fsql) {
		String response = null;
		return response;
	}
	
}
