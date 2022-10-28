package task;

import java.util.Date;

import org.apache.log4j.Logger;

import com.healta.plugin.sequence.SequenceService;

public class SequenceTask implements Runnable , BaseTask {
	private final static Logger log = Logger.getLogger(SequenceTask.class);
	private Date lastRunTime;
	private String errorMessage;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.info("----Begin SequenceTask----");
		errorMessage=null;
		try {
			SequenceService.start();
		} catch(Exception ex){
			ex.printStackTrace();
			errorMessage=ex.getMessage();
		} finally {
			lastRunTime=new Date();
		}
		log.info("----End SequenceTask----");
	}
	
	@Override
	public String getTaskName(){
		return "初始化检查序号";
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
