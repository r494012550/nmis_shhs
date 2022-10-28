package com.healta.plugin.activemq;

import java.io.Serializable;

import com.healta.model.Admission;
import com.healta.model.Patient;
import com.healta.model.Studyorder;
import com.jfinal.plugin.activerecord.Record;

public class PacsUpdateOrder extends BaseJmsOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1143566538195947826L;
	
	private Patient patient;
	private Admission admission;
	private Studyorder studyorder;
	private Record record;
	private String type;
	
	public PacsUpdateOrder(Patient patient, Admission admission, Studyorder studyorder, Record record, String type) {
		this.patient = patient;
		this.admission = admission;
		this.studyorder = studyorder;
		this.record = record;
		this.type = type;
	}
	
	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
