package com.semaifour.facesix.handlebars;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars.SafeString;
import com.github.jknack.handlebars.Options;
import com.semaifour.facesix.spring.SpringComponentUtils;

public class HelperSource {
 
	static Logger LOG = LoggerFactory.getLogger(HelperSource.class.getName());

	public static String now() {
		return new Date().toString();
	}
	
	public static String label(String key,  String defaultValue) {
		return new SafeString(SpringComponentUtils.getApplicationMessages().getMessage(key, defaultValue)).toString();
	}
	
	public static String getmapvalue(Map map, Options options) {
		return new SafeString(String.valueOf(map.get(options.param(0)))).toString();
	}
	
	/**
	 * 
	 * name:{{name}}
	 * 
	 * @param link
	 * @param options
	 * @return
	 */
	public static String makeenlinkquery(Map link, Options options) {
		String query = String.valueOf(link.get("target-query"));
		Map<Object, Object> data = options.param(0);
		if (query != null) {
			String regex = "\\{\\{.*?\\}\\}";
			Pattern pattern = Pattern.compile(regex);
		    Matcher matcher = pattern.matcher(query);
		    String key, param = null;
		    Object val = null;
		    while (matcher.find()) {
		    	param = matcher.group();
		    	key = param.substring(2, param.length() - 2);
		    	val = data.get(key);
		    	if (val != null) {
		    		try {
						query = query.replace(param,  URLEncoder.encode(String.valueOf(val), "UTF-8"));
					} catch (Exception e) {
						LOG.debug("failed to url.encode parameter :" + val, e);
					}
		    	}
		      }
			return new SafeString(query).toString();
		}
		return null;
	}
	
	
	
	/**
	 * @param millis
	 * @return
	 */
	public static String millis2mins(Object millis) {
		long duration = 0;
		try {
			duration = Long.parseLong(String.valueOf(millis));
			duration = duration / 1000;
		} catch (Exception e) {
			return "-1";
		}
		return ""+(duration / 60);
	}

	
	/**
	 * Converts give time in millis to near by time
	 * 
	 * @param millis
	 * @return
	 */
	public static String millis2time(Object millis) {
		double duration = 0;

		try {
			duration = Long.parseLong(String.valueOf(millis));
			duration = duration / 1000;
		} catch (Exception e) {
			return String.valueOf(millis) + e.toString();
		}

		if (duration < 60) {
			return duration + "s";
		} else if (duration < 3600) {
			return String.format("%d", (long)(duration/60)) + "m " + String.format("%d", (long)(duration%60)) + "s";
		} else if (duration < 86400) {
			return String.format("%d", (long)(duration/3600)) + "h " + String.format("%d", (long)((duration%3600)/60)) + "m";
		} else {
			return String.format("%.2f", (long)(duration/86400)) + "d " + String.format("%d", (long)(duration/3600)) + "h";
		}
	}
	
	
	/**
	 * 
	 * Converts secs to near human readable time for display like 10s, 1m, 2h, etc
	 * 
	 * @param secs
	 * @return
	 */
	public static String sec2time(Object secs) {
		double duration = 0;
		
		try {
			duration = Long.parseLong(String.valueOf(secs));
		} catch (Exception e) {
			return String.valueOf(secs) + e.toString();
		}
		
		if (duration < 60) {
			return duration + "s";
		} else if (duration < 3600) {
			return String.format("%d", (long)(duration/60)) + "m " + String.format("%d", (long)(duration%60)) + "s";
		} else if (duration < 86400) {
			return String.format("%d", (long)(duration/3600)) + "h " + String.format("%d", (long)((duration%3600)/60)) + "m";//String.format("%.2f", (duration/3600)) + "h";
		} else {
			return String.format("%d", (long)(duration/86400)) + "d " + String.format("%d", (long)((duration%86400)/3600)) + "h";
		}
	}
	
	/**
	 * 
	 * Formats date for display
	 * 
	 * @param date
	 * @param format http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html#Examples
	 * @return
	 */
	public static String date2display(Object date, String format) {
		if (date != null) {
		SimpleDateFormat df = new SimpleDateFormat(format);
			try {
				return df.format(date);
			} catch (Exception e) {
				return String.valueOf(date);
			}
		}
		return "";
	}
}