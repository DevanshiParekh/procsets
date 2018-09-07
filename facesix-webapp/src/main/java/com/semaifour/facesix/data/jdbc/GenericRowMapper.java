package com.semaifour.facesix.data.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.semaifour.facesix.domain.JSONMap;

public class GenericRowMapper implements RowMapper<JSONMap> {
	
	static Logger LOG = LoggerFactory.getLogger(GenericRowMapper.class.getName());

	public void begin() {
	}
	
	@Override
	public JSONMap mapRow(ResultSet rs, int rowNum) throws SQLException {
		JSONMap row = new JSONMap();
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			row.put(rsmd.getColumnLabel(i), toString(rs.getObject(i)));
		}
		return row;
	}
	
	private String toString(Object o) {
		if (o == null) return "";
		if (o instanceof Byte) return Hex.encodeHexString(new byte[] {(Byte)o});
		if (o instanceof byte[]) return Hex.encodeHexString((byte[])o);
		return String.valueOf(o);
	}

	public void end() {
	}
}
