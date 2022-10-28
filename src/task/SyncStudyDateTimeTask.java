package task;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.StudyImageStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.model.Mwlitem;
import com.healta.model.Studyorder;
import com.healta.plugin.dcm.SPSStatus;
import com.healta.server.dicom.Echo;
import com.healta.server.dicom.PatientInfo;
import com.healta.util.DateUtil;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

public class SyncStudyDateTimeTask implements Runnable , BaseTask{
	private final static Logger log = Logger.getLogger(SyncStudyDateTimeTask.class);
	private Date lastRunTime;
	private String errorMessage;
	
	public void run(){
		log.info("----Begin SyncStudyDateTimeTask----");
		errorMessage=null;
		try {
			Integer beforedays=PropKit.use("system.properties").getInt("beforedays");
			Integer beforehours=PropKit.use("system.properties").getInt("beforehours");
			
			if(beforehours!=null&&beforedays!=null) {
			
				Echo echo = new Echo();
				long time = echo.doEcho(PropKit.use("system.properties").get("syncstudydatetimedcmURL"));
				if(time!=-1){
					String sql="select id,studyid from studyorder where regdatetime >'"+LocalDate.now().minusDays(beforedays).format(DateTimeFormatter.ISO_LOCAL_DATE)+"' "
							+ "and regdatetime<'"+LocalDateTime.now().minusHours(beforehours).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"' and studydatetime is null";
					Db.find(sql).stream().forEach(x->{
						PatientInfo patientInfo=new PatientInfo();
						patientInfo.callFindWithQuery("", "", "", "", "","",x.getStr("studyid") , "", "", //
								PropKit.use("system.properties").get("syncstudydatetimedcmURL"), "");
						patientInfo.getStudyList().stream().forEach(y->{
							
				    			Studyorder tmp=new Studyorder();
				    			log.info("update studyorder id:"+x.getInt("id") +" studydatetime="+y.getStr("studydatetime"));
				    			tmp.setId(x.getInt("id"));
				    			tmp.setStudyinstanceuid(y.getStr("studyinstanceuid"));
				    			tmp.setStudydescription(y.getStr("studydescription"));
				    			try {
									tmp.setStudydatetime(DateUtil.getHL7Date(y.getStr("studydatetime")));
								} catch (ParseException e) {
									log.error("studydatetime invalid format:"+y.getStr("studydatetime"));
								}
				    			if(StringUtils.isNumeric(y.getStr("numberofstudyrelatedinstances"))){
				    				tmp.setNumberofstudyrelatedinstances(Integer.valueOf(y.getStr("numberofstudyrelatedinstances")));
				    			}
				    			if(StrKit.equals(x.getStr("status"), StudyOrderStatus.registered)) {
				    				tmp.setStatus(StudyOrderStatus.completed);
				    			}
				    			tmp.setImagestatus(StudyImageStatus.MATCH);
				    			tmp.update();
	//			    			updateMWlitem(accessNum, SPSStatus.COMPLETED);
				    			//更新worklist状态
				    			Mwlitem wl=Mwlitem.dao.findFirst("select * from mwlitem where studyorderidfk=?",x.getInt("id"));
				    			if(wl!=null&&SPSStatus.SCHEDULED==wl.getSpsStatus()) {
				    				wl.setSpsStatus(SPSStatus.COMPLETED);
				    				wl.update();
				    			}
				    			
						});;
						
					});
				}
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
			errorMessage=ex.getMessage();
		} finally {
			lastRunTime=new Date();
		}
		
	}

	@Override
	public String getTaskName(){
		return "同步检查时间";
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
