package task.report;

import java.util.Date;
import org.apache.log4j.Logger;
import com.healta.plugin.workforce.WorkforceServer;

import task.BaseTask;

public class WorkforceResetTask implements Runnable,BaseTask {
	private final static Logger log = Logger.getLogger(WorkforceResetTask.class);
	private Date lastRunTime;
	private String errorMessage;
	
	@Override
	public void run() {
		log.info("----Begin Workforce Reset Task----");
		errorMessage=null;
		try{
			WorkforceServer.INSTANCE.clear();
			WorkforceServer.INSTANCE.init(false);
		} catch(Exception ex){
			errorMessage=ex.getMessage();
		} finally {
			lastRunTime=new Date();
		}
		log.info("----End Workforce Reset Task----");
	}
	
	@Override
	public String getTaskName(){
		return "重置报告医生排版信息";
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
