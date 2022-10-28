package task.report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.PatientSource;
import com.healta.constant.ReportStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.model.Studyorder;
import com.healta.plugin.workforce.WorkforceServer;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import task.BaseTask;

public class TimeoutReminderTask implements Runnable,BaseTask {
	private final static Logger log = Logger.getLogger(TimeoutReminderTask.class);
	private Date lastRunTime;
	private String errorMessage;
	
	public void run() {
		log.info("----Begin TimeoutReminderTask----");
		errorMessage=null;
		try {
			//获取报告超时提醒的时间范围，如几天前，0代表只提醒当天，1代表提醒昨天和今天，以此类推
			LocalDate date= LocalDate.now().minusDays(PropKit.use("system.properties").getLong("report_reminder_task_before_days", 0l));
			
			List<Studyorder> modelList = new ArrayList<Studyorder>();
			String sql = "SELECT studyorder.id AS orderid, studyorder.status, studyorder.regdatetime, studyorder.studydatetime,studyorder.modality_type as modality"
					+ ", report.reportstatus, admission.patientsource,studyorder.report_assignee"
					+ ",(select min(ISNULL(dic_examitem.report_alert_hour,0)*60+ISNULL(dic_examitem.report_alert_minute,0)) "
					+ "from studyitem,dic_examitem where studyorder.id=studyitem.orderid and studyitem.item_id=dic_examitem.id) as alertMinute_item"
					+ ",(select sum(ISNULL(dic_examitem.coefficient,0)) "
					+ "from studyitem,dic_examitem where studyorder.id=studyitem.orderid and studyitem.item_id=dic_examitem.id) as coefficient"
					+ ",(select MIN(ISNULL(report_alert_hour,0)*60+ISNULL(report_alert_minute,0)) "
					+ "from  dic_modality where dic_modality.id=studyorder.modalityid) as alertMinute_modality "
					+ " FROM admission,studyorder "
					+ " LEFT JOIN report ON studyorder.id = report.studyorderfk"
					+ " WHERE studyorder.admissionidfk = admission.id and studyorder.studydatetime >=? AND studyorder.status = ?"
					+ " AND (report.reportstatus IS NULL OR report.reportstatus < ?)";
			List<Record> list = Db.find(sql,date.format(DateTimeFormatter.ISO_LOCAL_DATE), StudyOrderStatus.completed, ReportStatus.FinalResults);
			Calendar now = Calendar.getInstance();
		    now.setTime(new Date());
			for(Record record : list) {
			    Calendar studydatetime = Calendar.getInstance();
				studydatetime.setTime(record.getDate("studydatetime"));
			    if(record.getInt("alertMinute_item")>0) {//检查项目配置了警告时间
			    	studydatetime.add(Calendar.MINUTE, record.getInt("alertMinute_item"));
			    	if(studydatetime.before(now)) {
				    	Studyorder studyorder = new Studyorder();
				    	studyorder.setId(record.getInt("orderid"));
				    	studyorder.setPrecedence(2);
				    	Integer reAssign_ReportPhysician= reAssign_ReportPhysician_Timeout(record);
				    	if(reAssign_ReportPhysician!=null){
				    		studyorder.setReportAssignee(reAssign_ReportPhysician);
				    	}
				    	modelList.add(studyorder);
				    }		
			    }else if(record.getInt("alertMinute_modality")>0) {//设备配置了警告时间
			    	studydatetime.add(Calendar.MINUTE, record.getInt("alertMinute_modality"));
			    	if(studydatetime.before(now)) {
				    	Studyorder studyorder = new Studyorder();
				    	studyorder.setId(record.getInt("orderid"));
				    	studyorder.setPrecedence(2);
				    	Integer reAssign_ReportPhysician= reAssign_ReportPhysician_Timeout(record);
				    	if(reAssign_ReportPhysician!=null){
				    		studyorder.setReportAssignee(reAssign_ReportPhysician);
				    	}
				    	modelList.add(studyorder);
				    }	
			    }  
			}
			
			if(modelList.size() > 0) {
				Db.batchUpdate(modelList, modelList.size());
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			errorMessage=ex.getMessage();
		}finally {
			lastRunTime=new Date();
		}
	}
	
	/*
	 * 超时报告未审核将重新指派医生
	 * 
	 * 需要判断排除体检和急诊
	 * 
	 * */
	public Integer reAssign_ReportPhysician_Timeout(Record record){
		Integer ret=null;
		//排除急诊检查
    	if(StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableEed(), "1")&&StrKit.equals(PatientSource.emergency, record.getStr("patientsource"))){
    		return ret;
    	}
    	//排除体检检查
    	if(StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableEped(), "1")&&StrKit.equals(PatientSource.physical, record.getStr("patientsource"))){
    		return ret;
    	}
		
		Integer report_assignee=record.getInt("report_assignee");
//		if(report_assignee!=null){
			String reportstatus=record.getStr("reportstatus");
			if(StrKit.isBlank(reportstatus)||StrKit.equals(reportstatus, ReportStatus.Noresult)||StrKit.equals(reportstatus, ReportStatus.Created)){
				ret=WorkforceServer.INSTANCE.getReportAssignee(record.getInt("coefficient"), record.getInt("orderid"), 
						new Record()
						.set("reassgin_physicianid", report_assignee)
						.set("studydatetime", record.getDate("studydatetime")).set("modality", record.getStr("modality")));
			} else if(StrKit.equals(reportstatus, ReportStatus.Preliminary)){
				ret=WorkforceServer.INSTANCE.getPreAuditPhysicianAssignee(record.getInt("coefficient"), record.getInt("orderid"), new Record()
						.set("reassgin_physicianid", report_assignee)
						.set("studydatetime", record.getDate("studydatetime")).set("modality", record.getStr("modality")));
			} else if(StrKit.equals(reportstatus, ReportStatus.Preliminary_review)){
				ret=WorkforceServer.INSTANCE.getAuditPhysicianAssignee(record.getInt("coefficient"), record.getInt("orderid"), new Record()
						.set("reassgin_physicianid", report_assignee)
						.set("studydatetime", record.getDate("studydatetime")).set("modality", record.getStr("modality")));
			}
//		}
		return ret;
	}
	
	@Override
	public String getTaskName(){
		return "根据报警时间设置写报告的优先权并且重新指派报告医生";
	}
	
	@Override
	public Date getLastRunTime(){
		return lastRunTime;
	}
	
	@Override
	public String getErrorMessage(){
		return errorMessage;
	}
	
	@Override
	public boolean canCopy(){
		return false;
	}
}
