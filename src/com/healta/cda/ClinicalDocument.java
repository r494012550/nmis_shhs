package com.healta.cda;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

//import org.dcm4chex.archive.hl7.HL7Exception;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

//import com.lshi.ejb.util.DateTools;

public class ClinicalDocument {
    
    public static SAXReader reader = new SAXReader();
    public String study_UID;
    public String studyInstanceUID;
    
    public String realmCode;
    public String typeId_root;
    public String typeId_extension;
    public String templateId_root;
    public String id_root;
    public String id_extension;
    
    public String code_code;
    public String code_codeSystem;
    public String code_codeSystemName;
    public String code_displayName;
    public String title;
    public Date effectiveTime_value;
    public String confidentialityCode_code;
    public String confidentialityCode_codeSystem;
    public String languageCode_code;
    public String setId;
    public String versionNumber;
    
    public RecordTarget rt;
    
    public Date author_time;
    public String author_assignedAuthor_id_root;
    public String author_assignedAuthor_id_extension;
    public String author_assignedAuthor_addr;
    public String author_assignedAuthor_telecom;
    
    public String custodian_assignedCustodian_representedCustodianOrganization_id_root;
    public String custodian_assignedCustodian_representedCustodianOrganization_id_extension;
    public String custodian_assignedCustodian_representedCustodianOrganization_name;
    public String custodian_assignedCustodian_representedCustodianOrganization_addr;
    public String custodian_assignedCustodian_representedCustodianOrganization_telecom;
   
    public String participant_typeCode;
    public String participant_associatedEntity_classCode;
    public String participant_associatedEntity_id;
    public String participant_associatedEntity_code;
    public String participant_associatedEntity_name;
    public String participant_associatedEntity_telecom;
    public String participant_associatedEntity_addr;
    
    public String documentationOf_serviceEvent_classCode;
    public String documentationOf_serviceEvent_id_root;
    public String documentationOf_serviceEvent_id_extension;
    public Date documentationOf_serviceEvent_effectiveTime;
    
    public String relatedDocument_typeCode;
    
    public ArrayList relatedDocument_parentDocuments;
    public ArrayList sectiones;
    
    
    
    public Date getEffectiveTime_value() {
        return effectiveTime_value;
    }

    public void setEffectiveTime_value(Date effectiveTime_value) {
        this.effectiveTime_value = effectiveTime_value;
    }

    public Date getDocumentationOf_serviceEvent_effectiveTime() {
        return documentationOf_serviceEvent_effectiveTime;
    }

    public void setDocumentationOf_serviceEvent_effectiveTime(
            Date documentationOf_serviceEvent_effectiveTime) {
        this.documentationOf_serviceEvent_effectiveTime = documentationOf_serviceEvent_effectiveTime;
    }

    public Date getAuthor_time() {
        return author_time;
    }

    public void setAuthor_time(Date author_time) {
        this.author_time = author_time;
    }

    public String getStudy_UID() {
        return study_UID;
    }

    public void setStudy_UID(String study_UID) {
        this.study_UID = study_UID;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }

    public ArrayList getSectiones() {
        return sectiones;
    }

    public void setSectiones(ArrayList sectiones) {
        this.sectiones = sectiones;
    }

    public static SAXReader getReader() {
        return reader;
    }

    public static void setReader(SAXReader reader) {
        ClinicalDocument.reader = reader;
    }

    public String getRealmCode() {
        return realmCode;
    }

    public void setRealmCode(String realmCode) {
        this.realmCode = realmCode;
    }

    public String getTypeId_root() {
        return typeId_root;
    }

    public void setTypeId_root(String typeId_root) {
        this.typeId_root = typeId_root;
    }

    public String getTypeId_extension() {
        return typeId_extension;
    }

    public void setTypeId_extension(String typeId_extension) {
        this.typeId_extension = typeId_extension;
    }

    public String getTemplateId_root() {
        return templateId_root;
    }

    public void setTemplateId_root(String templateId_root) {
        this.templateId_root = templateId_root;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConfidentialityCode_code() {
        return confidentialityCode_code;
    }

    public void setConfidentialityCode_code(String confidentialityCode_code) {
        this.confidentialityCode_code = confidentialityCode_code;
    }

    public String getConfidentialityCode_codeSystem() {
        return confidentialityCode_codeSystem;
    }

    public void setConfidentialityCode_codeSystem(
            String confidentialityCode_codeSystem) {
        this.confidentialityCode_codeSystem = confidentialityCode_codeSystem;
    }

    public String getLanguageCode_code() {
        return languageCode_code;
    }

    public void setLanguageCode_code(String languageCode_code) {
        this.languageCode_code = languageCode_code;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public RecordTarget getRt() {
        return rt;
    }

    public void setRt(RecordTarget rt) {
        this.rt = rt;
    }

    public String getAuthor_assignedAuthor_id_root() {
        return author_assignedAuthor_id_root;
    }

    public void setAuthor_assignedAuthor_id_root(
            String author_assignedAuthor_id_root) {
        this.author_assignedAuthor_id_root = author_assignedAuthor_id_root;
    }

    public String getAuthor_assignedAuthor_id_extension() {
        return author_assignedAuthor_id_extension;
    }

    public void setAuthor_assignedAuthor_id_extension(
            String author_assignedAuthor_id_extension) {
        this.author_assignedAuthor_id_extension = author_assignedAuthor_id_extension;
    }

    public String getAuthor_assignedAuthor_addr() {
        return author_assignedAuthor_addr;
    }

    public void setAuthor_assignedAuthor_addr(String author_assignedAuthor_addr) {
        this.author_assignedAuthor_addr = author_assignedAuthor_addr;
    }

    public String getAuthor_assignedAuthor_telecom() {
        return author_assignedAuthor_telecom;
    }

    public void setAuthor_assignedAuthor_telecom(
            String author_assignedAuthor_telecom) {
        this.author_assignedAuthor_telecom = author_assignedAuthor_telecom;
    }

    public String getCustodian_assignedCustodian_representedCustodianOrganization_id_root() {
        return custodian_assignedCustodian_representedCustodianOrganization_id_root;
    }

    public void setCustodian_assignedCustodian_representedCustodianOrganization_id_root(
            String custodian_assignedCustodian_representedCustodianOrganization_id_root) {
        this.custodian_assignedCustodian_representedCustodianOrganization_id_root = custodian_assignedCustodian_representedCustodianOrganization_id_root;
    }

    public String getCustodian_assignedCustodian_representedCustodianOrganization_id_extension() {
        return custodian_assignedCustodian_representedCustodianOrganization_id_extension;
    }

    public void setCustodian_assignedCustodian_representedCustodianOrganization_id_extension(
            String custodian_assignedCustodian_representedCustodianOrganization_id_extension) {
        this.custodian_assignedCustodian_representedCustodianOrganization_id_extension = custodian_assignedCustodian_representedCustodianOrganization_id_extension;
    }

    public String getCustodian_assignedCustodian_representedCustodianOrganization_name() {
        return custodian_assignedCustodian_representedCustodianOrganization_name;
    }

    public void setCustodian_assignedCustodian_representedCustodianOrganization_name(
            String custodian_assignedCustodian_representedCustodianOrganization_name) {
        this.custodian_assignedCustodian_representedCustodianOrganization_name = custodian_assignedCustodian_representedCustodianOrganization_name;
    }

    public String getCustodian_assignedCustodian_representedCustodianOrganization_addr() {
        return custodian_assignedCustodian_representedCustodianOrganization_addr;
    }

    public void setCustodian_assignedCustodian_representedCustodianOrganization_addr(
            String custodian_assignedCustodian_representedCustodianOrganization_addr) {
        this.custodian_assignedCustodian_representedCustodianOrganization_addr = custodian_assignedCustodian_representedCustodianOrganization_addr;
    }

    public String getCustodian_assignedCustodian_representedCustodianOrganization_telecom() {
        return custodian_assignedCustodian_representedCustodianOrganization_telecom;
    }

    public void setCustodian_assignedCustodian_representedCustodianOrganization_telecom(
            String custodian_assignedCustodian_representedCustodianOrganization_telecom) {
        this.custodian_assignedCustodian_representedCustodianOrganization_telecom = custodian_assignedCustodian_representedCustodianOrganization_telecom;
    }

    public String getParticipant_typeCode() {
        return participant_typeCode;
    }

    public void setParticipant_typeCode(String participant_typeCode) {
        this.participant_typeCode = participant_typeCode;
    }

    public String getParticipant_associatedEntity_classCode() {
        return participant_associatedEntity_classCode;
    }

    public void setParticipant_associatedEntity_classCode(
            String participant_associatedEntity_classCode) {
        this.participant_associatedEntity_classCode = participant_associatedEntity_classCode;
    }

    public String getParticipant_associatedEntity_id() {
        return participant_associatedEntity_id;
    }

    public void setParticipant_associatedEntity_id(
            String participant_associatedEntity_id) {
        this.participant_associatedEntity_id = participant_associatedEntity_id;
    }

    public String getParticipant_associatedEntity_code() {
        return participant_associatedEntity_code;
    }

    public void setParticipant_associatedEntity_code(
            String participant_associatedEntity_code) {
        this.participant_associatedEntity_code = participant_associatedEntity_code;
    }

    public String getParticipant_associatedEntity_name() {
        return participant_associatedEntity_name;
    }

    public void setParticipant_associatedEntity_name(
            String participant_associatedEntity_name) {
        this.participant_associatedEntity_name = participant_associatedEntity_name;
    }

    public String getParticipant_associatedEntity_telecom() {
        return participant_associatedEntity_telecom;
    }

    public void setParticipant_associatedEntity_telecom(
            String participant_associatedEntity_telecom) {
        this.participant_associatedEntity_telecom = participant_associatedEntity_telecom;
    }

    public String getParticipant_associatedEntity_addr() {
        return participant_associatedEntity_addr;
    }

    public void setParticipant_associatedEntity_addr(
            String participant_associatedEntity_addr) {
        this.participant_associatedEntity_addr = participant_associatedEntity_addr;
    }

    public String getDocumentationOf_serviceEvent_classCode() {
        return documentationOf_serviceEvent_classCode;
    }

    public void setDocumentationOf_serviceEvent_classCode(
            String documentationOf_serviceEvent_classCode) {
        this.documentationOf_serviceEvent_classCode = documentationOf_serviceEvent_classCode;
    }

    public String getDocumentationOf_serviceEvent_id_root() {
        return documentationOf_serviceEvent_id_root;
    }

    public void setDocumentationOf_serviceEvent_id_root(
            String documentationOf_serviceEvent_id_root) {
        this.documentationOf_serviceEvent_id_root = documentationOf_serviceEvent_id_root;
    }

    public String getDocumentationOf_serviceEvent_id_extension() {
        return documentationOf_serviceEvent_id_extension;
    }

    public void setDocumentationOf_serviceEvent_id_extension(
            String documentationOf_serviceEvent_id_extension) {
        this.documentationOf_serviceEvent_id_extension = documentationOf_serviceEvent_id_extension;
    }

    public String getRelatedDocument_typeCode() {
        return relatedDocument_typeCode;
    }

    public void setRelatedDocument_typeCode(String relatedDocument_typeCode) {
        this.relatedDocument_typeCode = relatedDocument_typeCode;
    }

    public ArrayList getRelatedDocument_parentDocuments() {
        return relatedDocument_parentDocuments;
    }

    public void setRelatedDocument_parentDocuments(
            ArrayList relatedDocument_parentDocuments) {
        this.relatedDocument_parentDocuments = relatedDocument_parentDocuments;
    }

    public ClinicalDocument(){
        
    }
    
    public ClinicalDocument(String xml){
        Document doc=null;
        
        try {
            doc = reader.read(new StringReader(xml));
        } catch (DocumentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }// 
        try {
            initClinicalDocument(doc);
        }catch(Exception ex){
//            throw new Exception(ex.getMessage());
        }
        
    }
    
    public ClinicalDocument(Document doc){
        try {
            initClinicalDocument(doc);
        }catch(Exception ex){
//          throw new Exception(ex.getMessage());
      }
    }
    
    
    public void initClinicalDocument(Document doc)throws Exception{
        
        if(doc!=null){
            
            try{
                Element root = doc.getRootElement();
//                System.out.println("root:"+root.getName());
                
                
                for(Iterator k = root.elementIterator();k.hasNext();){
                    Element e = (Element) k.next();
//                    System.out.println("--"+ e.getName()+"--"+e.attributeValue("root"));
                    
                    if("realmCode".equals(e.getName())){
                        this.setRealmCode(e.getText());
                    }
                    else if("typeId".equals(e.getName())){
                        this.setTypeId_root(e.attributeValue("root"));
                        this.setTypeId_extension(e.attributeValue("extension"));
                    }
                    else if("templateId".equals(e.getName())){
                        this.setTemplateId_root(e.attributeValue("root"));
                    }
                    else if("id".equals(e.getName())){
                        this.setId_root(e.attributeValue("root"));
                        this.setId_extension(e.attributeValue("extension"));
                    }
                    else if("code".equals(e.getName())){
                        this.setCode_code(e.attributeValue("code"));
                        this.setCode_codeSystem(e.attributeValue("codeSystem"));
                        this.setCode_codeSystemName(e.attributeValue("codeSystemName"));
                        this.setCode_displayName(e.attributeValue("displayName"));
                    }
                    else if("title".equals(e.getName())){
                        this.setTitle(e.getText());
                    }
                    else if("effectiveTime".equals(e.getName())){
//                        this.setEffectiveTime_value(DateTools.getHL7Date(e.attributeValue("value")));
                    }
                    else if("confidentialityCode".equals(e.getName())){
                        this.setConfidentialityCode_code(e.attributeValue("code"));
                        this.setConfidentialityCode_codeSystem(e.attributeValue("codeSystem"));
                    }
                    else if("languageCode".equals(e.getName())){
                        this.setLanguageCode_code(e.attributeValue("code"));
                    }
                    else if("setId".equals(e.getName())){
                        this.setSetId(e.getText());
                    }
                    else if("versionNumber".equals(e.getName())){
                        this.setVersionNumber(e.getText());
                    }
                    else if("recordTarget".equals(e.getName())){
                        this.setRt(new RecordTarget(e));
                    }
                    else if("author".equals(e.getName())){
//                        this.setAuthor_time(e.element("time")!=null?DateTools.getHL7Date(e.element("time").attributeValue("value")):null);
                        Element aa=e.element("assignedAuthor");
                        if(aa!=null){
                            this.setAuthor_assignedAuthor_id_root(aa.element("id")!=null?aa.element("id").attributeValue("root"):null);
                            this.setAuthor_assignedAuthor_id_extension(aa.element("id")!=null?aa.element("id").attributeValue("extension"):null);
                            this.setAuthor_assignedAuthor_addr(aa.elementText("addr"));
                            this.setAuthor_assignedAuthor_telecom(aa.elementText("telecom"));
                        }
                    }
                    else if("custodian".equals(e.getName())){
                        Element ac=e.element("assignedCustodian");
                        if(ac!=null){
                            Element rco=ac.element("representedCustodianOrganization");
                            if(rco!=null){
                                this.setCustodian_assignedCustodian_representedCustodianOrganization_addr(rco.elementText("addr"));
                                this.setCustodian_assignedCustodian_representedCustodianOrganization_id_extension(rco.element("id")!=null?rco.element("id").attributeValue("extension"):null);
                                this.setCustodian_assignedCustodian_representedCustodianOrganization_id_root(rco.element("id")!=null?rco.element("id").attributeValue("root"):null);
                                this.setCustodian_assignedCustodian_representedCustodianOrganization_name(rco.elementText("name"));
                                this.setCustodian_assignedCustodian_representedCustodianOrganization_telecom(rco.elementText("telecom"));
                            }
                        }
                    }
                    else if("participant".equals(e.getName())){
                        this.setParticipant_typeCode(e.attributeValue("typeCode"));
                        Element ae=e.element("associatedEntity");
                        if(ae!=null){
                            this.setParticipant_associatedEntity_addr(ae.elementText("addr"));
                            this.setParticipant_associatedEntity_classCode(ae.attributeValue("classCode"));
                            this.setParticipant_associatedEntity_code(ae.elementText("code"));
                            this.setParticipant_associatedEntity_id(ae.elementText("id"));
                            this.setParticipant_associatedEntity_telecom(ae.elementText("telecom"));
                            
                            this.setParticipant_associatedEntity_name(ae.element("associatedPerson")!=null?ae.element("associatedPerson").elementText("name"):null);
                        }
                    }
                    else if("documentationOf".equals(e.getName())){
                        Element se=e.element("serviceEvent");
                        if(se!=null){
                            this.setDocumentationOf_serviceEvent_classCode(se.attributeValue("classCode"));
//                            this.setDocumentationOf_serviceEvent_effectiveTime(se.element("effectiveTime")!=null?DateTools.getHL7Date(se.element("effectiveTime").attributeValue("value")):null);
                            this.setDocumentationOf_serviceEvent_id_root(se.element("id")!=null?se.element("id").attributeValue("root"):null);
                            this.setDocumentationOf_serviceEvent_id_extension(se.element("id")!=null?se.element("id").attributeValue("extension"):null);
                        }
                        
                    }
    //                public String relatedDocument_typeCode;
    //                public ArrayList relatedDocument_parentDocuments;
                    else if("relatedDocument".equals(e.getName())){
                        this.setRelatedDocument_typeCode(e.attributeValue("typeCode"));
                        Element pd=e.element("parentDocument");
                        if(pd!=null){
                            relatedDocument_parentDocuments =new ArrayList();
                            for(Iterator j = root.elementIterator();j.hasNext();){
                                Element el = (Element) j.next();
                                if("id".equals(el.getName())){
                                    relatedDocument_parentDocuments.add(el.attributeValue("root"));
                                }
                            }
                        }
                        
                    }
                    else if("component".equals(e.getName())){
                        Element sb= e.element("structuredBody");
                        if(sb!=null){
                            sectiones=new ArrayList();
                            for(Iterator j = sb.elementIterator();j.hasNext();){
                                Element el = (Element) j.next();
                               
                                Element s=el.element("section");
                                if(s!=null){
                                    Section tmps=new Section(s);
                                    if(tmps.getStudyInstanceUID()!=null&&!"".equals(tmps.getStudyInstanceUID())){
                                        this.setStudyInstanceUID(tmps.getStudyInstanceUID());
                                    }
                                    tmps.setSr_id(this.getId_extension());
                                    sectiones.add(tmps);
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception ex){
                throw new Exception(ex.getMessage());
            }
        }
    }
    
}
