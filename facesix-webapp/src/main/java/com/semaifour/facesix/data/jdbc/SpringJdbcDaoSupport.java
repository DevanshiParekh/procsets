package com.semaifour.facesix.data.jdbc;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.semaifour.facesix.data.elasticsearch.datasource.Datasource;

public class SpringJdbcDaoSupport {

	static Logger LOG = LoggerFactory.getLogger(SpringJdbcDaoSupport.class.getName());

	private DataSource dataSource;
	
	protected JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(getDataSource());
	}

	protected DataSource getDataSource() {
		return this.dataSource;
	}
	
	protected void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
	
	/**
	 * 
	 * Returns an instance of DataSource ( DriverManagerDataSource )
	 * 
	 * @param jdbcds
	 * @return
	 */
	public static DataSource newDataSource(Datasource jdbcds) {
		DriverManagerDataSource dmds = new DriverManagerDataSource();	
		try {
			dmds.setDriverClassName(jdbcds.getDriverClassName());
			dmds.setUrl(jdbcds.getConnectionString());
			dmds.setUsername(jdbcds.getUsername());
			dmds.setPassword(jdbcds.getPassword());
			//dmds.setLoginTimeout(jdbcds.getLoginTimeout());
			dmds.setConnectionProperties(jdbcds.getSettings());

		} catch (Exception e) {
			LOG.error("Error while initializing DriverManagerDataSource for " + jdbcds.getConnectionString(), e);
		}
		
		return dmds;
	}
}
