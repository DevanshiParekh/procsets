package com.semaifour.facesix.data.jdbc;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.semaifour.facesix.data.elasticsearch.datasource.Datasource;
import com.semaifour.facesix.domain.JSONMap;


public class Reader extends SpringJdbcDaoSupport {
	
	private Datasource jdbcProp;
	
	public Reader(Datasource jdbcProp) {
		super();
		this.jdbcProp = jdbcProp;
		super.setDataSource(newDataSource(this.jdbcProp));
	}
	
	public List<JSONMap> read(String query, RowMapper<JSONMap> rowMapper) {
		
		return getJdbcTemplate().query(query, rowMapper);
	}

}
