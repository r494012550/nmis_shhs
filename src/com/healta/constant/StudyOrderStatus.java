package com.healta.constant;

public class StudyOrderStatus {

	public final static String scheduled="1"; //预约
	
	public final static String cancel_the_appointment="2"; //预约取消
	
	public static final String registered = "3"; //登记（保留字段）
	
	public static final String ARRIVED = "5"; //已签到
	
	public static final String CONSULTED = "7"; //已问诊
	
    public static final String injected = "9"; //已注射
    
	public final static String canceled="11"; //检查取消
	
	public final static String in_process="13"; //开始检查
	
	public final static String completed="15"; //完成检查
	
	public final static String re_examine="17"; //重拍

}
