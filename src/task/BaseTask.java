package task;

import java.util.Date;

public interface BaseTask {
	public String getTaskName();
	public Date getLastRunTime();
	public String getErrorMessage();
	public boolean canCopy();
}
