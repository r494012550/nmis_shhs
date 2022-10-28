package com.healta.plugin.activemq;

import java.util.List;
import java.util.stream.Collectors;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.apache.log4j.Logger;

import com.healta.constant.PatientSource;
import com.healta.constant.StudyprocessStatus;
import com.healta.model.Studyitem;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.plugin.activemq.Destination;
import com.healta.plugin.activemq.JmsReceiver;
import com.healta.plugin.workforce.WorkforceServer;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class WorkforceReceiver extends JmsReceiver {
	private final static Logger log = Logger.getLogger(WorkforceReceiver.class);
  
    public WorkforceReceiver(String name,
                       Connection connection,
                       Destination type,
                       String subject) throws JMSException {
    	super(name, connection, type, subject);
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage msg = (ObjectMessage) message;
                try {
                	StudyprocessOrder order = (StudyprocessOrder)msg.getObject();
                    log.info("WorkforceReceiver start processing " + order);
                    Studyprocess sp=order.getSp();
//                    Studyorder so=null;
                    if(sp.getStudyorderfk()!=null){
//                    	so=Studyorder.dao.findById(sp.getStudyorderfk());
//                    	if(so!=null) {
                    	boolean exclude=false;
                    	Record record=null;
                    	if(StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableEed(), "1")){
                    		record=Db.findFirst("select top 1 admission.patientsource from admission,studyorder where admission.id=studyorder.admissionidfk and studyorder.id=?",sp.getStudyorderfk());
                    		if(record!=null){
                    			if(StrKit.equals(PatientSource.emergency, record.getStr("patientsource"))){
                    				exclude=true;
                    			}
                    		}
                    	}
                    	if(StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableEped(), "1")){
                    		if(record==null){
                    			record=Db.findFirst("select top 1 admission.patientsource from admission,studyorder where admission.id=studyorder.admissionidfk and studyorder.id=?",sp.getStudyorderfk());
                    		}
                    		if(record!=null){
                    			if(StrKit.equals(PatientSource.physical, record.getStr("patientsource"))){
                    				exclude=true;
                    			}
                    		}
                    	}

                    	if(!exclude){
//                    		if(StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableAdard(), "1")) {  //Average distribution according to examine difficulty,检查难度平均分配
                    		List<Record> list=Db.find("select studyitem.id,orderid,studyitem.item_id,studyitem.modality,studyorder.modalityid,(select dic_examitem.coefficient from dic_examitem where dic_examitem.id=studyitem.item_id) as coefficient "
                					+ "from studyorder,studyitem where studyorder.id=studyitem.orderid and studyorder.id=?",sp.getStudyorderfk());
                    		int examitem_coefficient= list.stream().mapToInt(x->x.getInt("coefficient")).sum();
                    		
                    		String examitemids=list.stream().map(x->x.getStr("item_id")).collect(Collectors.joining(","));
                    		String modality=list.get(0).getStr("modality");
                    		Integer modalityid=list.get(0).getInt("modalityid");
                    		Record parare=new Record();
                    		parare.set("modality", modality);
                    		parare.set("modalityid", modalityid);
                    		parare.set("examitemids", examitemids);
                			Integer physician_id=null;
                			if(StrKit.equals(sp.getStatus(), StudyprocessStatus.completed)){//检查完成后获取指定的报告医生
                				physician_id = WorkforceServer.INSTANCE.getReportAssignee(examitem_coefficient,sp.getStudyorderfk(),parare);
                			} else if(StrKit.equals(sp.getStatus(), StudyprocessStatus.created)) {//初步报告后获取指定的初审医生preliminary
                				if(PropKit.use("system.properties").getBoolean("PreAudit",true)) {
                					physician_id = WorkforceServer.INSTANCE.getPreAuditPhysicianAssignee(examitem_coefficient,sp.getStudyorderfk(),parare);
                				} else {
                					physician_id = WorkforceServer.INSTANCE.getAuditPhysicianAssignee(examitem_coefficient,sp.getStudyorderfk(),parare);
                				}
                			} else if(StrKit.equals(sp.getStatus(), StudyprocessStatus.Preliminaryreview)) {//初审报告后获取指定的审核医生
                				physician_id = WorkforceServer.INSTANCE.getAuditPhysicianAssignee(examitem_coefficient,sp.getStudyorderfk(),parare);
                			}
                			Db.update("update studyorder set report_assignee=? where id=?",physician_id,sp.getStudyorderfk());
//                    		}
                    	}
//                    	}
                    }

                    log.info("WorkforceReceiver Finished processing");
                }
                catch (JMSException e) {
	                log.error("jms error during processing message: " + message, e);
	            }
                catch (Throwable e) {
	                log.error("unexpected error during processing message: " + message,e);
	            }
            } 
            else {
            	log.info(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
