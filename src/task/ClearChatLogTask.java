package task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;

public class ClearChatLogTask implements Runnable , BaseTask {
	private final static Logger log = Logger.getLogger(ClearChatLogTask.class);

	private Date lastRunTime;
	
	private String errorMessage;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.info("----Begin clear unread group chat log ----");
		errorMessage=null;
		try{
			LocalDate ld=LocalDate.now().minusDays(PropKit.use("system.properties").getInt("keep_unread_group_chat_log_days"));
			Db.delete("delete from chat_group_msg where createtime < ?", ld.format(DateTimeFormatter.ISO_LOCAL_DATE));
		}
		catch(Exception ex){
			errorMessage=ex.getMessage();
		} finally {
			lastRunTime=new Date();
		}
		log.info("----End clear unread group chat log ----");
	}
	
	@Override
	public String getTaskName(){
		return "删除群聊历史记录";
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
