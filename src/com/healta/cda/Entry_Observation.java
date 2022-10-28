package com.healta.cda;

import org.dom4j.Element;

public class Entry_Observation {
    
    public String section_id;
    public String studyInstanceUID;
    public String classCode;
    public String moodCode;
    public String templateId_root;
    
    public String code_code;
    public String code_codeSystem;
    public String code_codeSystemName;
    public String code_displayName;
    public String code_originalText_reference_value;
    
    public String value_xsitype;
    public String value_value;
    public String value_value1;
    public String value_unit;
    public String value_code;
    public String value_codeSystem;
    public String value_codeSystemName;
    public String value_displayName;
    public int xml_index;
    
    public boolean isAct=false;
    
    public String nextIsAct="0";
    
    
    public String getNextIsAct() {
        return nextIsAct;
    }

    public void setNextIsAct(String nextIsAct) {
        this.nextIsAct = nextIsAct;
    }

    public int getXml_index() {
        return xml_index;
    }

    public void setXml_index(int xml_index) {
        this.xml_index = xml_index;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getMoodCode() {
        return moodCode;
    }

    public void setMoodCode(String moodCode) {
        this.moodCode = moodCode;
    }

    public String getTemplateId_root() {
        return templateId_root;
    }

    public void setTemplateId_root(String templateId_root) {
        this.templateId_root = templateId_root;
    }

    public String getCode_code() {
        return code_code;
    }

    public void setCode_code(String code_code) {
        this.code_code = code_code;
    }

    public String getCode_codeSystem() {
        return code_codeSystem;
    }

    public void setCode_codeSystem(String code_codeSystem) {
        this.code_codeSystem = code_codeSystem;
    }

    public String getCode_codeSystemName() {
        return code_codeSystemName;
    }

    public void setCode_codeSystemName(String code_codeSystemName) {
        this.code_codeSystemName = code_codeSystemName;
    }

    public String getCode_displayName() {
        return code_displayName;
    }

    public void setCode_displayName(String code_displayName) {
        this.code_displayName = code_displayName;
    }

    public String getCode_originalText_reference_value() {
        return code_originalText_reference_value;
    }

    public void setCode_originalText_reference_value(
            String code_originalText_reference_value) {
        this.code_originalText_reference_value = code_originalText_reference_value;
    }

    public String getValue_xsitype() {
        return value_xsitype;
    }

    public void setValue_xsitype(String value_xsitype) {
        this.value_xsitype = value_xsitype;
    }

    public String getValue_value() {
        return value_value;
    }

    public void setValue_value(String value_value) {
        this.value_value = value_value;
    }

    public String getValue_value1() {
        return value_value1;
    }

    public void setValue_value1(String value_value1) {
        this.value_value1 = value_value1;
    }

    public String getValue_unit() {
        return value_unit;
    }

    public void setValue_unit(String value_unit) {
        this.value_unit = value_unit;
    }

    public String getValue_code() {
        return value_code;
    }

    public void setValue_code(String value_code) {
        this.value_code = value_code;
    }

    public String getValue_codeSystem() {
        return value_codeSystem;
    }

    public void setValue_codeSystem(String value_codeSystem) {
        this.value_codeSystem = value_codeSystem;
    }

    public String getValue_codeSystemName() {
        return value_codeSystemName;
    }

    public void setValue_codeSystemName(String value_codeSystemName) {
        this.value_codeSystemName = value_codeSystemName;
    }

    public String getValue_displayName() {
        return value_displayName;
    }

    public void setValue_displayName(String value_displayName) {
        this.value_displayName = value_displayName;
    }


    public Entry_Observation(Element e){
        try{
            Element ob=e.element("observation");
            if(ob!=null){
                this.setClassCode(ob.attributeValue("classCode"));
                this.setMoodCode(ob.attributeValue("moodCode"));
                this.setTemplateId_root(ob.element("templateId")!=null?ob.element("templateId").attributeValue("root"):null);
                
                Element co=ob.element("code");
                if(co!=null){
                    this.setCode_code(co.attributeValue("code"));
                    this.setCode_codeSystem(co.attributeValue("codeSystem"));
                    this.setCode_codeSystemName(co.attributeValue("codeSystemName"));
                    this.setCode_displayName(co.attributeValue("displayName"));
                    Element ot=co.element("originalText");
                    if(ot!=null){
                        this.setCode_originalText_reference_value(ot.element("reference")!=null?ot.element("reference").attributeValue("value"):null);
                    }
                }
                
                Element va=ob.element("value");
                if(va!=null){
                    this.setValue_xsitype(va.attributeValue("type"));
                    this.setValue_value(va.attributeValue("value"));
                    this.setValue_value1(va.getText());
                    this.setValue_unit(va.attributeValue("unit"));
                    this.setValue_code(va.attributeValue("code"));
                    this.setValue_codeSystem(va.attributeValue("codeSystem"));
                    this.setValue_codeSystemName(va.attributeValue("codeSystemName"));
                    this.setValue_displayName(va.attributeValue("displayName"));
                    
    //                System.out.println(this.getValue_value()+";"+this.getValue_value1());
                }
            }
            else{
                Element ob1=e.element("act");
                if(ob1!=null){
                    this.setAct(true);
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean isAct() {
        return isAct;
    }

    public void setAct(boolean isAct) {
        this.isAct = isAct;
    }
    
    
}
