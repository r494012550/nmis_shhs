package task;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.healta.constant.PatientSource;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.StudyprocessStatus;
import com.healta.model.DicModality;
import com.healta.model.Mwlitem;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.activemq.StudyprocessOrder;
import com.healta.plugin.dcm.SPSStatus;
import com.healta.plugin.queueup.Node;
import com.healta.plugin.queueup.QueueMethod;
import com.healta.plugin.queueup.QueueupSendOrder;
import com.healta.util.SequenceNo_Generator;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class ScheduleToRegisterTask implements Runnable, BaseTask {
    
    private final static Logger log = Logger.getLogger(ScheduleToRegisterTask.class);
    
    private Date lastRunTime;
    private String errorMessage;

    @Override
    public void run() {
        log.info("----Begin ScheduleToRegisterTask----");
        errorMessage = null;
        try {
            String dateFrom = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dateTo = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));  
            log.info("dateFrom:" + dateFrom + " ,dateTo:" + dateTo);
            // 将当天预约的所有信息查询出来
            List<Record> list = Db.find("SELECT id FROM studyorder WHERE studyorder.appointmenttime >= ?"
                    + " AND studyorder.appointmenttime < ? AND studyorder.status = '1'" 
                    + " ORDER BY studyorder.regdatetime DESC", dateFrom, dateTo);
            // 将查询出来的预约转登记
            for (int i = 0; i < list.size(); i++) {
                int orderid = list.get(i).get("id");
                Db.tx(new IAtom() {
                    public boolean run() throws SQLException {
                        boolean ret = true;
                        Date now = new Date();
                        Record record = Db.findFirst("select top 1 patient.id as patientkey,patient.patientname as patientname,admission.id as admissionkey,studyorder.modalityid as modalitykey,"
                                + "admission.patientsource as patientsource,studyorder.studyid as studyid,studyorder.sequencenumber as sequencenumber,studyorder.appointmenttime as appointmenttime"
                                + " from patient,admission,studyorder where patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk and studyorder.id=?",orderid);
                        
                        log.info(record.getInt("modalitykey"));
                        
                        DicModality dicmodality = DicModality.dao.findById(record.getInt("modalitykey"));
                        Studyorder so = new Studyorder();
                        so.setId(orderid);
                        so.set("status", StudyOrderStatus.registered);
                        so.setRegdatetime(now);
                        so.setModifytime(now);

                        Mwlitem mwl=new Mwlitem();
                        mwl.setPatientidfk(record.getInt("patientkey"));
                        mwl.setAdmissionidfk(record.getInt("admissionkey"));
                        mwl.setStudyorderidfk(orderid);
                        mwl.setSpsStatus(SPSStatus.SCHEDULED);
                        mwl.setStartDatetime(now);
                        mwl.setStationAet(dicmodality.getWorklistscu());
                        mwl.setStationName(dicmodality.getModalityName());
                        mwl.setModality(dicmodality.getType());
                        mwl.setAccessionNo(record.getStr("studyid"));
                        mwl.setUpdatedTime(now);
                        mwl.setCreatedTime(now);
                        ret=ret&&mwl.remove("id").save();
                        
                        ret=ret&&so.update();
                        
                        ret=ret&&SequenceNo_Generator.updateStatus(dicmodality.getId(), record.getDate("appointmenttime"));
                        if (ret) {
                            //发送叫号消息
                            Node node=new Node(StrKit.equals(record.getStr("patientsource"), PatientSource.emergency));
                            node.setStudyid(record.getStr("studyid"));
                            node.setPatientname(record.getStr("patientname"));
                            node.setSn(record.getStr("sequencenumber"));
                            node.setModality(dicmodality.getType());
                            node.setModalityid(dicmodality.getId());
                            node.setModalityname(dicmodality.getModalityName());
                            node.setLocation(dicmodality.getLocation());
            
                            ActiveMQ.sendObjectMessage(new QueueupSendOrder(node, QueueMethod.Offer), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
                            
                            //发送检查进程-预约转登记
                            ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
                                    .set("studyorderfk", orderid)
                                    .set("status", StudyprocessStatus.scheduleturntoregister)
                                    .set("operator", "自动转登记")
                                    .set("operatorname", "自动转登记")), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
                        }
                        return ret; 
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorMessage = ex.getMessage();
        } finally {
            lastRunTime = new Date();
        }
        log.info("----End ScheduleToRegisterTask----");
    }
    
    @Override
    public String getTaskName() {
        return "预约转登记";
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
