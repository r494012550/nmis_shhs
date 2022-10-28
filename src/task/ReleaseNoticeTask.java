package task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.healta.listener.MySessionContext;
import com.healta.model.Notice;
import com.healta.model.NoticeRecord;
import com.healta.service.NoticeService;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.jfinal.kit.StrKit;

public class ReleaseNoticeTask implements Runnable, BaseTask{

	@Override
	public String getTaskName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getLastRunTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canCopy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void run() {
		List<Notice> NoticeList = Notice.dao.find("select * from notice where releasedflag = 0 and sendtime < ?", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		for (Notice notice : NoticeList) {
			List<Integer> sendUserID = NoticeService.queryNoSendNoticeByNoticeId(notice.getId());
			for (Integer id : sendUserID) {
				if(StrKit.notBlank(MySessionContext.getSessionId(id))) {
					WebSocketUtils.sendMessage(id, new WebsocketVO(WebsocketVO.NOTICE,notice.getId() + "," + notice.getMustread() + ",1").toJson());
				}
			}
		}
	}
}
