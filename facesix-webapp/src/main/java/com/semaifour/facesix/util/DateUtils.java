package com.semaifour.facesix.util;

import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateUtils {
	public static final DateTimeFormatter locformatter = ISODateTimeFormat.dateTimeParser().withOffsetParsed();
	public static final DateTimeFormatter utcFormatter = ISODateTimeFormat.dateTimeParser().withOffsetParsed().withZoneUTC();
	
	/**
	 * 
	 * Parse the given ISO date-time string to Date object
	 * 
	 * @param isodatetime
	 * @return Date object
	 */
	public static Date parse2Timestamp(String isodatetime) {
		return locformatter.parseDateTime(isodatetime).toDate();
	}
	
	/**
	 * 
	 * Parse the given ISO date-time string to UTC date-time string.
	 * 
	 * @param isodatetime
	 * @return UTC format String
	 */
	public static String parse2UTC(String isodatetime) {
		return utcFormatter.parseDateTime(isodatetime).toString();
	}
}
