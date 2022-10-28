package com.healta.ws;

import java.io.Serializable;

import com.healta.model.Admission;
import com.healta.model.Eorder;
import com.healta.model.Patient;
import com.healta.model.Report;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.model.Urgent;
import com.healta.plugin.activemq.BaseJmsOrder;
import com.jfinal.plugin.activerecord.Record;

public class WsOrder extends BaseJmsOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5134665218307136542L;

	private Patient patient;
	private Admission admission;
	private Studyorder studyorder;
	private Report report;
//	private HisApply hisApply;
	private Urgent urgent;
	private Record record;
	private Eorder eorder;
	
	private Studyprocess sp;
	
	/**
	 *  操作类型，确费时使用（1人工操作 ，自助确费不填）
	 */
	private String reMrak;
	
	private Method method;
	
	public WsOrder(Admission admission,Studyorder so, Method method,String reMrak) {
		this.admission=admission;
		this.studyorder=so;
		this.method=method;
		this.reMrak=reMrak;
	}
	
	public WsOrder(Studyprocess sp,Method method){
		this.sp=sp;
		this.method=method;
	}
	
	public WsOrder(Studyorder so, Method method) {
		this.studyorder=so;
		this.method=method;
	}

	public Studyprocess getSp() {
		return sp;
	}

	public void setSp(Studyprocess sp) {
		this.sp = sp;
	}
	
	public String getReMrak() {
		return reMrak;
	}

	public void setReMrak(String reMrak) {
		this.reMrak = reMrak;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Admission getAdmission() {
		return admission;
	}

	public void setAdmission(Admission admission) {
		this.admission = admission;
	}

	public Studyorder getStudyorder() {
		return studyorder;
	}

	public void setStudyorder(Studyorder studyorder) {
		this.studyorder = studyorder;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Urgent getUrgent() {
		return urgent;
	}

	public void setUrgent(Urgent urgent) {
		this.urgent = urgent;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public Method getMethod() {
		return method;
	}
	
	public void setMethod(Method method) {
		this.method = method;
	}
	
	public void setEorder(Eorder eorder) {
		this.eorder = eorder;
	}

	public Eorder getEorder() {
		return eorder;
	}
	
	public enum Method {
		REGISTER_REPORT("注册检查报告","ExamPoint/ExaminationReport?wsdl","createExaminationReport"),
		REGISTER_REPORT_EVENT("注册检查报告事件","ExamPoint/ExamReportEvent?wsdl","createExamReportEvent"),
		CREATE_EXAMINATION_CRISIS("注册检查危急值","ExamPoint/ExaminationCrisis?wsdl","createExaminationCrisis"),
		RECOPTER_PACSFEE_CONFIRM("注册检查门诊终端确费","ExamPoint/OutFeeConfirm?wsdl","recOpTerPacsFeeConfirm"),
		RECIPTER_PACSFEE_CONFIRM("注册检查住院终端确费","ExamPoint/InFeeConfirm?wsdl","recIpTerPacsFeeConfirm"),
		RECOPTER_PACSFEE_CANCEL("注册检查门诊终端确费取消","ExamPoint/OutFeeCancel?wsdl","recOpTerPacsFeeCancel"),
		RECIPTER_PACSFEE_CANCEL("注册检查住院终端确费取消","ExamPoint/InFeeCancel?wsdl","recIpTerPacsFeeCancel"),
		PACSFEE_CONFIRM("医院原先的确费消息(走平台)","roc/Service.asmx?wsdl","ServiceSoap"),
		
		PACSFEE_CONFIRM_OLD("医院原先的确费消息(不走平台)","",""),
		CALLING("叫号","","CheckIn");

		/**
		 *  描述 
		 */
		private String note;
		
		/**
		 *  调用webservice的地址
		 */
		private String endpoint;
		
		/**
		 *  调用的方法
		 */
		private String method;
		
		private Method(String note, String endpoint, String method) {
			this.note = note;
			this.endpoint = endpoint;
			this.method = method;
		}
		
		public String getNote() {
			return this.note;
		}
		
		public String getEndpoint() {
			return this.endpoint;
		}
		
		public String getMethod() {
			return this.method;
		}

	}
}
