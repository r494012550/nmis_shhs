package task;

import java.util.Date;
import org.apache.log4j.Logger;

import com.healta.plugin.queueup.QueuingUpService;

public class CallingTask implements Runnable, BaseTask {
    
    private final static Logger log = Logger.getLogger(CallingTask.class);
    
    private Date lastRunTime;
    private String errorMessage;

    @Override
    public void run() {
    	// TODO Auto-generated method stub
    			log.info("----Begin CallingTask----");
    			errorMessage=null;
    			try {
    				QueuingUpService.initQueue();
    			} catch(Exception ex){
    				ex.printStackTrace();
    				errorMessage=ex.getMessage();
    			} finally {
    				lastRunTime=new Date();
    			}
    			log.info("----End CallingTask----");
    }
    
    @Override
    public String getTaskName() {
        return "问诊叫号和检查叫号定时清空数组";
    }

    @Override
    public Date getLastRunTime() {
        return lastRunTime;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean canCopy() {
        return false;
    }
    
}
