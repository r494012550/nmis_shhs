package com.healta.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jfinal.ext.kit.DateKit;


public class AgeUtils {
	
	private static Logger log = Logger.getLogger(AgeUtils.class);
	public static AgeUtils ins=new AgeUtils();
	
	/*
	 * 根据出生日期得到年龄
	 * @param birthdate (格式为：yyyyMMdd或yyyy-MM-dd)
	 * */
	public Age birthdayToAge(String birthday) {
		try {
			DateKit.setDatePattern("yyyyMMdd");
			Date date=DateKit.toDate(birthday);
			return birthdayToAge(date);
		} catch(IllegalArgumentException e) {
			log.error(birthday+" "+e.getMessage());
		}
		try {
			DateKit.setDatePattern("yyyy-MM-dd");
			Date date=DateKit.toDate(birthday);
			return birthdayToAge(date);
		} catch(IllegalArgumentException e) {
			log.error(birthday+" "+e.getMessage());
		}
		return null;
	}
	
	public Age birthdayToAge(Date birthday) {
		LocalDate bday= birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Age ret=new Age();
		int nowyear = LocalDate.now().getYear();
		int year = bday.getYear();
		if(nowyear > year) {
			ret.setAge(nowyear - year);
			ret.setAgeunit("Y");
		}else if(nowyear == year){
			int nowmonth = LocalDate.now().getMonthValue();
			int month = bday.getMonthValue();
			if(nowmonth > month) {
    			ret.setAge(nowmonth - month);
    			ret.setAgeunit("M");
			}else if(nowmonth == month) {
				int nowday = LocalDate.now().getDayOfMonth();
				int day = bday.getDayOfMonth();
				if(nowday > day) {
    				ret.setAge(nowday - day);
        			ret.setAgeunit("D");
				}
			}
		}
		return ret;
	}
	/**
	 *  根据出生日期得到年龄
	 * @param birthdate (格式为：yyyyMMdd)
	 * @return
	 */
	public static HashMap<String, Object> getAgeByBirthday(String birthdate) {
		HashMap<String, Object> birthMap = new HashMap<String, Object>();
		birthMap.put("result", true);
		
		if(StringUtils.isNumeric(birthdate) && birthdate.length()>4) {
    		String birth = birthdate.substring(0,birthdate.length()-4)+"-"+birthdate.substring(birthdate.length()-4,birthdate.length()-2)+"-"+birthdate.substring(birthdate.length()-2);
    		
    		birthMap.put("birth", birth);
    		
    		Calendar calendar = Calendar.getInstance();
    		int nowyear = calendar.get(Calendar.YEAR);
    		Integer age = null;
        	String ageunit = "";
    		try {
    			int year = Integer.parseInt(birth.substring(0,birth.indexOf('-')));
        		if(nowyear > year) {
        			age = nowyear - year;
        			ageunit = "Y";
        		}else if(nowyear == year){
        			int nowmonth = calendar.get(Calendar.MONTH)+1;
        			int month = Integer.parseInt(birth.substring(birth.indexOf('-')+1,birth.lastIndexOf('-')));
        			if(nowmonth > month) {
        				age = nowmonth - month;
            			ageunit = "M";
        			}else if(nowmonth == month) {
        				int nowday = calendar.get(Calendar.DAY_OF_MONTH);
        				int day = Integer.parseInt(birth.substring(birth.lastIndexOf('-')+1));
        				if(nowday > day) {
        					age = nowday - day;
            				ageunit = "D";
        				}
        			}
        		}
        		
        		birthMap.put("age", age);
        		birthMap.put("ageunit", ageunit);
    		} catch(NumberFormatException e) {
    			e.printStackTrace();
    			birthMap.put("result", false);
    			log.error("birthdate invalid format:"+birthdate);
    		}
    	} else {
    		birthMap.put("result", false);
    		log.error("birthdate invalid format:"+birthdate);
    	}
		return birthMap;
	}
	
	public static void main(String[] args) {
//		new AgeUtils().getAgeByBirthday("20210912");
		
//		System.out.println(LocalDate.now().getMonthValue());
//		System.out.println(new Date().getDay());
		
//		DateKit.setDatePattern("yyyyMMdd");
//		Date date=DateKit.toDate("2022-04-06");
		
		Age age=AgeUtils.ins.birthdayToAge("2010-04-07");
		System.out.println(age.getAge());
		System.out.println(age.getAgeunit());
	}
	
	public class Age{
		private int age;
		private String ageunit;
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public String getAgeunit() {
			return ageunit;
		}
		public void setAgeunit(String ageunit) {
			this.ageunit = ageunit;
		}
	}

}


