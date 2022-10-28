package task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.log4j.Logger;

import com.healta.model.Quicksearch;
import com.jfinal.plugin.activerecord.Db;

public class RebuildQuickSearchIndexTask implements Runnable ,BaseTask {
	private final static Logger log = Logger.getLogger(RebuildQuickSearchIndexTask.class);
	
	private Date lastRunTime;
	
	private String errorMessage;
	
	@Override
	public void run() {
		log.info("----Begin Rebuild Quick Search Index----");
		errorMessage=null;
		try{
			String sql="select q.id as quicksearchkey, p.id as patientkey,p.patientid,p.patientname,p.py,p.telephone,p.idnumber,a.id as admissionkey,a.accessionnumber,a.cardno,a.inno,a.outno,"
					+ "s.id as studyorderkey,s.studyid from patient p,admission a,studyorder s,quicksearch q where p.id=a.patientidfk and a.id=s.admissionidfk and "
					+ "s.id=q.studyorderkey and q.createtime>=?";
			String begindate=LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
			log.info("Rebuild Quick Search Index begin:"+begindate);
			Db.find(sql,begindate).stream().forEach(x->{
				Quicksearch qs=Quicksearch.dao.createFromRecord(x);
				qs.setId(x.getInt("quicksearchkey"));
				qs.update();
			});
		} catch(Exception ex){
			errorMessage=ex.getMessage();
		} finally {
			lastRunTime=new Date();
		}
		log.info("----End Rebuild Quick Search Index----");
	}
	
	@Override
	public String getTaskName(){
		return "重建快速检索索引（更新索引内容）";
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
