package com.healta.plugin;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.healta.constant.UserAuditType;
import com.healta.model.UserAuditLogs;

public class UserAuditLogsKit {

	public static void addLog(Integer userfk,String type,String ip) {
		Date now=new Date();
		UserAuditLogs log= new UserAuditLogs();
		log.setUserfk(userfk);
		log.setAuditType(type);
		log.setAuditTime(now);
		log.setIp(ip);
		log.remove("id").save();
		if(UserAuditType.LOGOUT.equals(type)) {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					UserAuditLogs ual=UserAuditLogs.dao.findFirst("select top 1 * from user_audit_logs where audit_type=? and userfk=? order by audit_time desc",UserAuditType.LOGIN,userfk);
					if(ual!=null&&ual.getAuditTime()!=null) {
						ual.setDuration((now.getTime()-ual.getAuditTime().getTime())/1000);
						ual.update();
					}
				}
			}, 1000l);
		}
	}
}
