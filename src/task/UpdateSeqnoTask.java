package task;

import java.util.Date;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;

public class UpdateSeqnoTask implements Runnable , BaseTask {
	private final static Logger log = Logger.getLogger(UpdateSeqnoTask.class);
	private Date lastRunTime;
	private String errorMessage;
	
	public void run() {
		log.info("----Begin update studyorder_number----");
		errorMessage=null;
		try {
			Db.update("truncate table studyorder_number");
		}catch(Exception e) {
			e.printStackTrace();
			errorMessage=e.getMessage();
		} finally {
			lastRunTime=new Date();
		}
	}
	
	@Override
	public String getTaskName(){
		return "清空检查顺序表";
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
