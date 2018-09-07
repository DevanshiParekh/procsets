package com.semaifour.facesix.data.elasticsearch.datasource;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.semaifour.facesix.domain.FSObject;

@Document(indexName = "fsi-ds-#{systemProperties['fs.app'] ?: 'default'}", type = "datasource")
public class Datasource extends FSObject {
	
	@Id
	private String id;
	
	private String connectionString;
	private String username;
	private String password;
	private String driverClassName;
	private int loginTimeout;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public int getLoginTimeout() {
		return loginTimeout;
	}

	public void setLoginTimeout(int loginTimeout) {
		this.loginTimeout = loginTimeout;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	@Override
	public String toString() {
		return "Datasource [connectionString=" + connectionString
				+ ", username=" + username + ", password=" + password
				+ ", driverClassName=" + driverClassName
				+ ", loginTimeout=" + loginTimeout + ", super.toString()="
				+ super.toString() + "]";
	}
}
