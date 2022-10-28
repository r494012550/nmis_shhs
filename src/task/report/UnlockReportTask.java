package task.report;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.healta.listener.MySessionContext;
import com.healta.model.Report;
import com.healta.model.Studyorder;
import com.healta.model.User;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;

import task.BaseTask;

public class UnlockReportTask implements Runnable , BaseTask{
	private final static Logger log = Logger.getLogger(UnlockReportTask.class);
	private Date lastRunTime;
	private String errorMessage;
	
	public void run(){
		log.info("----Begin unlock report----");
		errorMessage=null;
		try {
			List<Report> reports=Report.dao.find("select top 100 * from report where islocking=1");
			for(Report report:reports){
//				String sessionid=MySessionContext.getSessionId(report.getLockingpeople());
				if(report.getLockingpeople()!=null&&!MySessionContext.isOnline(report.getLockingpeople())){
					log.info("Unlock report,The report id is:"+report.getId());
					Db.update("update report set islocking=0 ,lockingpeople = null where id=?", report.getId());
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
		return "解锁报告锁定";
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
