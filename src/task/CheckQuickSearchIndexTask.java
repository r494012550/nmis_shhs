package task;

import java.util.Date;

import org.apache.log4j.Logger;
import com.healta.model.Quicksearch;
import com.jfinal.plugin.activerecord.Db;

public class CheckQuickSearchIndexTask implements Runnable,BaseTask {
	private final static Logger log = Logger.getLogger(CheckQuickSearchIndexTask.class);
	private Date lastRunTime;
	private String errorMessage;
	
	@Override
	public void run() {
		log.info("----Begin Check Quick Search Index----");
		errorMessage=null;
		try{
			String sql="select top 300 p.id as patientkey,p.patientid,p.patientname,p.py,p.telephone,p.idnumber,a.id as admissionkey,a.accessionnumber,a.cardno,a.inno,a.outno,"
					+ "s.id as studyorderkey,s.studyid from patient p,admission a,studyorder s  where p.id=a.patientidfk and a.id=s.admissionidfk and "
					+ "not exists (select studyorderkey from quicksearch where quicksearch.studyorderkey =s.id)";
			
			Db.find(sql).stream().forEach(x->{
				Quicksearch.dao.createFromRecord(x).save();
			});
		} catch(Exception ex){
			errorMessage=ex.getMessage();
		} finally {
			lastRunTime=new Date();
		}
		log.info("----End Check Quick Search Index----");
	}
	
	@Override
	public String getTaskName(){
		return "检查快速检索索引（新增索引内容）";
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
		return true;
	}
}
