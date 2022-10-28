package com.healta.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DateUtil {

	private final static String _JUST_DATE = "yyyy-MM-dd";
	private final static String _JUST_DATE_CHS = "yyyy年MM月dd日";
	private final static String _DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	private final static String _DATE_TIME_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
	private final static String _DATE_TIME_CHS = "yyyy年MM月dd日 HH点mm分ss秒";
	private final static String _DATE_HOUR = "yyyy-MM-dd HH";
	private final static String _DATE_HOUR_CHS = "yyyy年MM月dd日 HH点";
	
	public final static String _DATE="yyyy/M/d";
	
	public final static String _SHORTDATE="yyyyMMdd";
	
	public final static String _DATE_HL7="yyyyMMddHHmmss";
	
	public final static String _DATE_SCHEDULE="yyyyMMddHHmm";
	
	//无符号正则表达式
	public static final String DATE_FORMAT_NO_SPLIT_REGEX = "^(\\d{4})(\\d{2})(\\d{2})$";
	//有符号正常日期格式
	public static final String DATE_FORMAT_COMMON_REGEX = "^(\\d{4,})[/-](\\d{1,2})[/-](\\d{1,2})$";
	//有符号正常日期格式替换
	public static final String DATE_FORMAT_COMMON_REPLACE = "$1-$2-$3 00:00:00.000";
	//倒序的日期格式
	public static final String DATE_FORMAT_REVERT_REGEX = "^(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4,})$";
	//有符号正常日期格式替换
	public static final String DATE_FORMAT_REVERT_REPLACE = "$3-$2-$1 00:00:00.000";
	//正常时间格式
	public static final String DATETIME_HOUR_FORMAT_REGEX = "^(\\d{4,})[/-](\\d{1,2})[/-](\\d{1,2}).{1}(\\d{1,2}):(\\d{1,2})$";
	//正常时间格式替换
	public static final String DATETIME_HOUR_FORMAT_REPLACE = "$1-$2-$3 $4:$5:00.000";
	//正常时间格式
	public static final String DATETIME_FORMAT_REGEX = "^(\\d{4,})[/-](\\d{1,2})[/-](\\d{1,2}).{1}(\\d{1,2}):(\\d{1,2}):(\\d{1,2})$";
	//正常时间格式替换
	public static final String DATETIME_FORMAT_REPLACE = "$1-$2-$3 $4:$5:$6.000";
	// 时间格式化字符串 yyyy-MM-dd HH:mm:ss.SSS
	public static final String DATETIME_FULL_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	
	// 缓存的自动识别的格式正则表达式
	private static List<DateReplace> autoDateCache = new ArrayList<DateReplace>();
	static {
		registerAutoFormat(DATE_FORMAT_NO_SPLIT_REGEX, DATE_FORMAT_COMMON_REPLACE);
		registerAutoFormat(DATE_FORMAT_COMMON_REGEX, DATE_FORMAT_COMMON_REPLACE);
		registerAutoFormat(DATE_FORMAT_REVERT_REGEX, DATE_FORMAT_REVERT_REPLACE);
		registerAutoFormat(DATETIME_HOUR_FORMAT_REGEX, DATETIME_HOUR_FORMAT_REPLACE);
		registerAutoFormat(DATETIME_FORMAT_REGEX, DATETIME_FORMAT_REPLACE);
	}
	
	/**
	* 时间格式字符串
	*/
	private static class DateReplace {
		// 正则表达式
		public String regex;
		// 替换表达式
		public String replace;
		// 终止标志位
		public boolean end;
	}

	/**
	* 注册正则表达式，将时间转换为正确格式的正则表达式，后注册的会优先执行 。
	*
	* @param regex 正则表达式
	* @param replace 替换表达式
	*/
	public static void registerAutoFormat(String regex, String replace) {
		registerAutoFormat(regex, replace, true);
	}
	
	/**
	* 注册正则表达式，将时间转换为正确格式的正则表达式，后注册的会优先执行 。
	*
	* @param regex 正则表达式
	* @param replace 替换表达式
	* @param end 是否需要结束
	*/
	public static void registerAutoFormat(String regex, String replace, boolean end) {
		DateReplace item = new DateReplace();
		item.regex = regex;
		item.replace = replace;
		item.end = end;
		autoDateCache.add(item);
	}
	
	/**
	* 根据时间字符串自动识别时间
	*
	* @param date 时间字符串
	* @return 时间
	*/
	public static Date getAutoDate(String date) throws ParseException {
		if (date == null) {
			return null;
		}
		int size = autoDateCache.size();
		for (int i = size - 1; i >= 0; i--) {
			// 遍历所有时间格式
			DateReplace item = autoDateCache.get(i);
			String dateTo = date.replaceAll(item.regex, item.replace);
			// 如何替换成功，且终止标志位为true则终止执行
			boolean isBreak = item.end && !dateTo.equals(date);
			date = dateTo;
			if (isBreak) {
				break;
			}
		}
		// 将正常格式的时间字符串转换为时间
		return new SimpleDateFormat(DATETIME_FULL_FORMAT).parse(String.valueOf(date));
	}

	public static java.sql.Date UDtoSD(Date date){
		return new java.sql.Date(date.getTime());
	}
	public static Date stdJustDate(String date){//日期－－> 2011－1－1
		return std(date,_JUST_DATE);
	}
	public static Date stdJustDate_CHS(String date){//日期－－> 2011年1月1日
		return std(date,_JUST_DATE_CHS);	 
	}
	public static Date stdWithTime(String date){//日期－－> 2011－1－1 00:00:00
		return std(date,_DATE_TIME);
	}
	public static Date stdWithTime_CHS(String date){//日期－－> 2011年1月1日 00点00分00秒
		return std(date,_DATE_TIME_CHS);	 
	}
	public static Date stdWithHour(String date){//日期－－> 2011－1－1 00
		return std(date,_DATE_HOUR);
	}
	public static Date stdWithHour_CHS(String date){//日期－－> 2011年1月1日  00点
		return std(date,_DATE_HOUR_CHS);	 
	}
	
	public static String toShortStr(Date date){//日期－－> 2011年1月1日  00点
		return dts(date,_SHORTDATE);	 
	}
	
	public static String dtsJustDate(Date date){//日期－－> 2011－1－1
		return dts(date,_JUST_DATE);
	}
	public static String dtsJustDate_CHS(Date date){//日期－－> 2011年1月1日
		return dts(date,_JUST_DATE_CHS);	 
	}
	public static String dtsWithTime(Date date){//日期－－> 2011－1－1 00:00:00
		return dts(date,_DATE_TIME);
	}
	public static String dtsWithTime_CHS(Date date){//日期－－> 2011年1月1日 00点00分00秒
		return dts(date,_DATE_TIME_CHS);	 
	}
	public static String dtsWithHour(Date date){//日期－－> 2011－1－1 00
		return dts(date,_DATE_HOUR);
	}
	public static String dtsWithHour_CHS(Date date){//日期－－> 2011年1月1日  00点
		return dts(date,_DATE_HOUR_CHS);	 
	}


	public static String dtsWithTimeS0(Date date){//日期－－> 2011－1－1 11:12:00
		return dtsS0(date,_DATE_TIME);
	}
	public static String dtsWithTimeT0(Date date){//日期－－> 2011－1－1 00:00:00
		return dtsT0(date,_DATE_TIME);
	}
	public static String dtsWithTimeT0_CHS(Date date){//日期－－> 2011年1月1日 00点00分00秒
		return dtsT0(date,_DATE_TIME_CHS);	 
	}
	public static String dtsWithHourT0(Date date){//日期－－> 2011－1－1 00
		return dtsT0(date,_DATE_HOUR);
	}
	public static String dtsWithHourT0_CHS(Date date){//日期－－> 2011年1月1日  00点
		return dtsT0(date,_DATE_HOUR_CHS);	 
	}
	
	
	public static Date getHL7Date(String str) throws ParseException{
	    
	    Date ret=null;
	    if(str!=null&&!"".equals(str)){
	        SimpleDateFormat format=new SimpleDateFormat(_DATE_HL7);
	        ret=format.parse(str);
	    }
	    
	    return ret;
	}
	
	/**
	 *  his预约检查时间
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	public static Date stdyyyyMMddHHmm(String str) throws ParseException {
        Date ret = null;
        if (str != null && !"".equals(str)) { 
            SimpleDateFormat format = new SimpleDateFormat(_DATE_SCHEDULE);
            ret = format.parse(str);
        }
        return ret;
    }
	
	/**
	 *  将字符串 yyyyMMdd 转为 yyyy-MM-dd 格式
	 * @param yyyyMMdd
	 * @return
	 * @throws ParseException
	 */
	public static String stsyyyy_MM_dd(String yyyyMMdd) throws ParseException {
		String birthday = "";
		if (yyyyMMdd != null && !"".equals(yyyyMMdd)) { 
            SimpleDateFormat format = new SimpleDateFormat(_SHORTDATE);
            Date date = format.parse(yyyyMMdd);
            DateFormat format1 = new java.text.SimpleDateFormat(_JUST_DATE);
            birthday = format1.format(date);
        }
        return birthday;
	}
	
	public static String dtsHL7Date(Date date) throws ParseException{
	    return dts(date,_DATE_HL7);  
    }
	
	public static String dtsyyyyMMddHHmm(Date date) throws ParseException{
	    return dts(date,_DATE_SCHEDULE);  
    }
	

	private static String dts(Date date,String type){
		if(date != null){
			DateFormat format = new java.text.SimpleDateFormat(type);
		    String s = format.format(date);
		    return s;
		}
		return null;	
	}
	private static String dtsT0(Date date,String type){
		if(date != null){
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			DateFormat format = new java.text.SimpleDateFormat(type);
		    String s = format.format(date);
		    return s;
		}
		return null;	
	}
	private static String dtsS0(Date date,String type){
		if(date != null){
			date.setSeconds(0);
			DateFormat format = new java.text.SimpleDateFormat(type);
		    String s = format.format(date);
		    return s;
		}
		return null;	
	}
	public static Date std(String str,String type){
		try{
			if(str != null){//
				DateFormat format = new SimpleDateFormat(type); 
				Date date = format.parse(str); 
				return date;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Timestamp getTimestamp(Date date){
		return Timestamp.valueOf(new SimpleDateFormat(_DATE_TIME_SSS).format(date));
	}
	
	public static String hl7DateFormatToString(String date) {
		return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8)+ " " + date.substring(8, 10) + ":" + date.substring(10, 12) + ":" + date.substring(12, 14);
	}
	
	/**
	 * 19411202 to 1941-12-02
	 * @param birthdate
	 * @return
	 */
	public static String hl7BirthdateFormatToString(String birthdate) {
		return birthdate.substring(0,birthdate.length()-4)+"-"+birthdate.substring(birthdate.length()-4,birthdate.length()-2)+"-"+birthdate.substring(birthdate.length()-2);
	}
	
	public static String hl7BirthdateFormat(String birthdate) {
		return birthdate.substring(0,birthdate.indexOf('-'))+birthdate.substring(birthdate.indexOf('-')+1,birthdate.lastIndexOf('-'))+birthdate.substring(birthdate.lastIndexOf('-')+1);
	}
	
	public static void main(String s[]) throws ParseException{
//		System.out.println(stdJustDate("2011-4-1"));
//		System.out.println(hl7BirthdateFormat("2008-11-14"));
		
		System.out.println(getAutoDate("1987-11-04"));
		System.out.println(getAutoDate("1987-11-4 12:5"));
		System.out.println(getAutoDate("1987-11-04 12:50:15"));
		System.out.println(getAutoDate("1987-11-04 12:50:15.000"));
		System.out.println(getAutoDate("19871104"));
		System.out.println(getAutoDate("4/11/1987"));
		System.out.println(getAutoDate("1987/11/11"));
	}
}
