package com.healta.cda;

import org.dom4j.Element;

public class RecordTarget {
    
    public String patientRole_classCode;
    
    public String id_root;
    public String id_extension;
    
    public String addr;
    
    public String telecom;
    
    public String patient_id;
    public String patient_name;
    public String patient_administrativeGenderCode_codeSystem;
    public String patient_administrativeGenderCode_code;
    public String patient_birthTime;
    
    
    
    public String getPatientRole_classCode() {
        return patientRole_classCode;
    }
    public void setPatientRole_classCode(String patientRole_classCode) {
        this.patientRole_classCode = patientRole_classCode;
    }
    public String getId_root() {
        return id_root;
    }
    public void setId_root(String id_root) {
        this.id_root = id_root;
    }
    public String getId_extension() {
        return id_extension;
    }
    public void setId_extension(String id_extension) {
        this.id_extension = id_extension;
    }
    public String getAddr() {
        return addr;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getTelecom() {
        return telecom;
    }
    public void setTelecom(String telecom) {
        this.telecom = telecom;
    }
    public String getPatient_id() {
        return patient_id;
    }
    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }
    public String getPatient_name() {
        return patient_name;
    }
    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }
    public String getPatient_administrativeGenderCode_codeSystem() {
        return patient_administrativeGenderCode_codeSystem;
    }
    public void setPatient_administrativeGenderCode_codeSystem(
            String patient_administrativeGenderCode_codeSystem) {
        this.patient_administrativeGenderCode_codeSystem = patient_administrativeGenderCode_codeSystem;
    }
    public String getPatient_administrativeGenderCode_code() {
        return patient_administrativeGenderCode_code;
    }
    public void setPatient_administrativeGenderCode_code(
            String patient_administrativeGenderCode_code) {
        this.patient_administrativeGenderCode_code = patient_administrativeGenderCode_code;
    }
    public String getPatient_birthTime() {
        return patient_birthTime;
    }
    public void setPatient_birthTime(String patient_birthTime) {
        this.patient_birthTime = patient_birthTime;
    }
    
    
    public RecordTarget(Element e){
        
        if(e!=null){
//            System.out.println(e.element("patientRole").element("id").attributeValue("root"));
            this.setPatientRole_classCode(e.element("patientRole").attributeValue("classCode"));
            this.setId_root(e.element("patientRole").element("id").attributeValue("root"));
            this.setId_extension(e.element("patientRole").element("id").attributeValue("extension"));
            this.setAddr(e.element("patientRole").elementText("addr"));
            this.setTelecom(e.element("patientRole").elementText("telecom"));
            this.setPatient_id(e.element("patientRole").element("patient").elementText("id"));
            this.setPatient_name(e.element("patientRole").element("patient").element("name").elementText("family"));
            this.setPatient_administrativeGenderCode_code(e.element("patientRole").element("patient").element("administrativeGenderCode").attributeValue("code"));
            this.setPatient_administrativeGenderCode_codeSystem(e.element("patientRole").element("patient").element("administrativeGenderCode").attributeValue("codeSystem"));
            this.setPatient_birthTime(e.element("patientRole").element("patient").element("birthTime").attributeValue("value"));
            
        }
    }
    
    
}
