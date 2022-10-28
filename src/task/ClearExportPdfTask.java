package task;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.jfinal.kit.PropKit;

public class ClearExportPdfTask implements Runnable, BaseTask {
    
    private final static Logger log = Logger.getLogger(ClearExportPdfTask.class);
    
    private Date lastRunTime;
    private String errorMessage;

    @Override
    public void run() {
        log.info("----Begin ClearExportPdfTask----");
        errorMessage = null;
        try {
            String path = PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator")+"export";
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                // 如果此文件存在并且是文件夹
            	FileUtils.cleanDirectory(file);
//                String[] tempList = file.list();  
//                File temp = null;  
//                for (int i = 0; i < tempList.length; i++) {  
//                    if (path.endsWith(File.separator)) {  
//                        temp = new File(path + tempList[i]);  
//                    } else {  
//                        temp = new File(path + File.separator + tempList[i]);  
//                    }  
//                    if (temp.isFile()) {  
//                        temp.delete();  
//                    } 
//                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorMessage = ex.getMessage();
        } finally {
            lastRunTime = new Date();
        }
        log.info("----End ClearExportPdfTask----");
    }
    
    @Override
    public String getTaskName() {
        return "定时清除服务器上临时目录中的pdf文件";
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
