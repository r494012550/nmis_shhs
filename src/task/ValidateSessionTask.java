package task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import com.healta.constant.CacheName;
import org.apache.log4j.Logger;
import com.healta.constant.UserAuditType;
import com.healta.license.VerifyLicense;
import com.healta.listener.MySessionContext;
import com.healta.model.UserAuditLogs;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.jfinal.plugin.ehcache.CacheKit;

public class ValidateSessionTask implements Runnable,BaseTask {
	private final static Logger log = Logger.getLogger(ValidateSessionTask.class);
	private Date lastRunTime;
	private String errorMessage;
	
	@Override
	public void run() {
		log.info("----Begin Validate Session----");
		errorMessage=null;
		try{
			//遍历缓存中的userid，在线的用户，duration加5分钟
			List<Integer> userids=MySessionContext.getAllUserId();
			if(userids!=null) {
				userids.stream().forEach(x->{
					UserAuditLogs ual= UserAuditLogs.dao.findFirst("select top 1 * from user_audit_logs where audit_type=? and userfk=? order by createtime desc",UserAuditType.LOGIN,x);
					if(ual!=null&&MySessionContext.isOnline(x)) {
						if(ual.getDuration()==null) {
							Long du=(new Date().getTime()-ual.getAuditTime().getTime())/1000;
							ual.setDuration(du<=300l?du:300l);
						} else {
							ual.setDuration(ual.getDuration()+5*60l);
						}
						ual.update();
					}
				});
			}
			
			LocalDate now = LocalDate.now();
			UserAuditLogs.dao.find("select * from user_audit_logs where audit_type=? and duration is null and createtime >?",
				UserAuditType.LOGIN,now.minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE)).stream().forEach(x->{
					if(!MySessionContext.isOnline(x.getUserfk())) {
						x.setDuration((new Date().getTime()-x.getAuditTime().getTime())/1000);
						x.update();
						if(VerifyLicense.hasModule("chat")) {
							WebSocketUtils.broadcastMessageExceptMe(x.getUserfk(),new WebsocketVO(WebsocketVO.OFFLINE_REMINDER, x.getUserfk()+"").toJson());
						}
					}
			});
		} catch(Exception ex){
			errorMessage=ex.getMessage();
		} finally {
			lastRunTime=new Date();
		}
		log.info("----End Validate Session----");
	}
	
	@Override
	public String getTaskName(){
		return "验证session有效性";
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
