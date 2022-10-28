package com.healta.constant;

public class StudyprocessStatus {
	
	public final static String scheduled = "1";//预约
	public final static String scheduleturntoregister = "sch_reg";//预约转登记
	public final static String canceltheappointment = "2";//预约取消
	public final static String modifyschedule = "mod_sch";//修改预约信息
	public final static String signin = "signin";//签到
	public final static String registered = "3";//登记
	public final static String registerturntoschedule = "reg_sch";//登记转预约
	public final static String cancelregistered = "cancel_reg";//登记取消
	public final static String modifyregistered = "mod_reg";//修改登记信息
	public final static String deletePatientinfo = "del_pat_info";//删除患者信息
	public final static String deletestudyinfo = "del_study_info";//删除检查信息
	public final static String addstudyitem = "add_study_item";//添加检查项目
	public final static String deletestudyitem = "del_study_item";//删除检查项目
	public final static String mergepatient = "merge_pat";//合并患者
	public final static String cancelmergepatient = "cancel_merge_pat";//取消合并患者
	public final static String reassignStudy = "reassignStudy";//重新关联检查
	public final static String splitadmission = "split_admis";//拆分入院记录
	public final static String Inquiring = "4";//问诊
	public final static String injection = "6";//注射
	public final static String canceled = "5";//检查取消
	public final static String inprocess = "7";//开始检查
	public final static String completed = "9";//检查完成
	public final static String re_examine = "11";//重拍
	public final static String cancelchecked = "12";//取消已检查
	public final static String openreport = "openreport";//打开报告
	public final static String noresult = "20";//未写
	public final static String created = "21";//已创建
	public final static String savereport = "savereport";// 之后保存报告为暂存报告
	public final static String preliminary = "23";//初步报告
	public final static String preliminaryreject = "25";//初步报告驳回
	public final static String Preliminaryreview = "27";//初审
	public final static String Preliminaryreviewreject = "29";//初审驳回
	public final static String finalresults = "31";//已审核
	public final static String cancelAuditReport = "cancelAuditReport";//取消审核
	public final static String reportprinted = "33";//已发放
	public final static String filmprinted = "print_film";//打印胶片
	
	
}
