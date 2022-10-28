package task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.StudyOrderStatus;
import com.healta.model.Patient;
import com.healta.model.Studyorder;
import com.healta.model.Synctime;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;


public class SynchrodataTask implements Runnable{
	private final static Logger log = Logger.getLogger(SynchrodataTask.class);// Logger.getLogger(SynchrodataTask.class);
	private final static SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmss");
	
	public void run(){
		
		
		log.info("----Begin Synchro data----");
		
		Synctime st=Synctime.dao.getSynctime();
		List<Record> list=Db.use("mssql").find("select distinct top 100 patient.patientid,patient.fullname,patient.birthdate,patient.sex,study.studydate,"
				+ "study.studytime,study.studyid,study.studyinstanceuid,study.accessionnumber,study.studydescription,study.numberofstudyrelatedinstances,study.modalitiesinstudy,"
				+"(select max(series.inserttime) from series where study.oid=series.study_oid) as createtime "
				+ "from patient,study,series where patient.oid=study.patient_oid and study.oid=series.study_oid and "
				+ "series.inserttime>=? order by createtime",st.getTime());
		Date tmp=null;
		for(Record re:list){
			try {
				Patient pa=Patient.dao.getPatientByIdAndName(re.getStr("patientid"), re.getStr("fullname"));
				if(pa==null){
					pa=new Patient();
					pa.setPatientid(re.getStr("patientid"));
					pa.setPatientname(re.getStr("fullname"));
					pa.setSex(re.getStr("sex"));
					pa.setBirthdate(re.getStr("birthdate"));
					pa.save();
				}
				
				String studyinstanceuid =re.getStr("studyinstanceuid");
				String studyid=re.getStr("studyid");
				log.info(studyinstanceuid+"====studyinstanceuid=="+studyinstanceuid.replaceAll("\\.", ""));
				Studyorder so=Studyorder.dao.getStudyorderByUid(studyinstanceuid);
				log.info("--------------"+so);
				if(so==null){
					so=new Studyorder();
					so.setPatientidfk(pa.getId());
					so.setStudyinstanceuid(studyinstanceuid);
					if(StringUtils.isNotEmpty(studyid)){
						so.setStudyid(studyid);
					}
					else{
						so.setStudyid(studyinstanceuid.replaceAll("\\.", ""));
					}

					so.setStudydescription(re.getStr("studydescription"));
					so.setModalityType(re.getStr("modalitiesinstudy"));
					so.setAccessionnumber(re.getStr("studyid"));
					so.setNumberofstudyrelatedinstances(re.getInt("numberofstudyrelatedinstances"));
					so.setPatientid(re.getStr("patientid"));
					so.setStudydatetime(parse(re.getStr("studydate"), re.getStr("studytime")));
					so.setStatus(StudyOrderStatus.completed);
					so.save();
				}
				
				tmp=re.getDate("createtime");
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		if(tmp!=null){
			st.setTime(tmp);
			st.update();
		}
		
	}
	
	
	private Date parse(String date,String time) throws ParseException{
		return format.parse(date+time);
	}

}
