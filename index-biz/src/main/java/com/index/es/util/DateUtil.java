package com.index.es.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间格式转换工具类
 * @author lican
 * @date 2018年8月28日
 * @since v1.0.0
 */
public class DateUtil {

	private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);
	
	private static final String DEFAULT_FORMATTER = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 时间格式字符串 转 时间戳
	 * @param dateStr
	 * @param format
	 * @return 时间戳，单位：秒
	 */
	public static long stringToLong(String dateStr,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		if(StringUtils.isNotBlank(dateStr) && StringUtils.equalsIgnoreCase("null", dateStr)) {
			try {
				date = sdf.parse(dateStr);
			} catch (Exception e) {
				LOG.error("[stringToLong]date string :{},parse error;",dateStr,e);
				return 0L;
			}
		}
		long timestamp = 0L;
		if(null != date) {
			timestamp = date.getTime() / 1000;
		}
		return timestamp;
	}
	
	/**
	 * 时间类型 转 时间戳
	 * @param date
	 * @return 时间戳，单位：秒
	 */
	public static long dateToStamp(Date date) {
		return date.getTime()/1000;
	}
	
	/**
	 * 时间戳 转 默认的时间格式字符串
	 * 
	 * @param time
	 * @return 时间格式字符串:yyyy-MM-dd HH:mm:ss
	 */
	public static String stampToDate(long time) {
		return stampToDate(time, DEFAULT_FORMATTER);
	}
	
	/**
	 * 时间戳 转 时间格式字符串
	 * @param time
	 * @param format
	 * @return 时间格式字符串
	 */
	public static String stampToDate(long time,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(time/1000));
	}
	
}
