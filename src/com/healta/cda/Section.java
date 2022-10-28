package com.healta.cda;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

//import com.intelray.api.GUID;

public class Section {
    
    public String templateId_root;
    public String id;
    
    public String sr_id;
    public String studyInstanceUID;
    
    public String code_code;
    public String code_codeSystem;
    public String code_codeSystemName;
    public String code_displayName;
    
    public String title;
    
    public Text text;
    
    public ArrayList entry_observation;
    
    
    
    
    public String getSr_id() {
        return sr_id;
    }

    public void setSr_id(String sr_id) {
        this.sr_id = sr_id;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }

    public String getTemplateId_root() {
        return templateId_root;
    }

    public void setTemplateId_root(String templateId_root) {
        this.templateId_root = templateId_root;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public ArrayList getEntry_observation() {
        return entry_observation;
    }

    public void setEntry_observation(ArrayList entry_observation) {
        this.entry_observation = entry_observation;
    }

    public Section(Element e){
        try{
            if(e.element("templateId")!=null){
                this.setTemplateId_root(e.element("templateId").attributeValue("root"));
            }
            if(e.element("id")!=null){
                this.setId(e.elementText("id"));
            }
            
            if(this.getId()==null||"".equals(this.getId())){
//                this.setId(GUID.createId().toSimpleString());
            }
            
            Element c=e.element("code");
            if(c!=null){
               this.setCode_code(c.attributeValue("code"));
               this.setCode_codeSystem(c.attributeValue("codeSystem"));
               this.setCode_codeSystemName(c.attributeValue("codeSystemName"));
               this.setCode_displayName(c.attributeValue("displayName"));
            }
            this.setTitle(e.elementText("title"));
            
            Element t=e.element("text");
            if(t!=null){
                this.setText(new Text(t,this.getId()));
            }
            
            List entries=e.elements("entry");
            if(entries!=null){
                entry_observation =new ArrayList();
                
                for(int i=0;i<entries.size();i++){
                    Element en=(Element)entries.get(i);
                    Entry_Observation tmpen=new Entry_Observation(en);
                    tmpen.setXml_index(i);
                    
                    if(tmpen.isAct()){
                        if(i>0){
                            Entry_Observation tmp=(Entry_Observation)entry_observation.get(entry_observation.size()-1);
                            tmp.setNextIsAct("1");
                        }
                        
                        continue;
                    }
                    if("".equals(tmpen.getCode_code())||tmpen.getCode_code()==null){
                        continue;
                    }
                    tmpen.setSection_id(this.getId());
                    if("0020000d".equals(tmpen.getCode_code())||"0020000D".equals(tmpen.getCode_code())){
                        this.setStudyInstanceUID(tmpen.getValue_value1());
                        
    //                    System.out.println(this.getStudyInstanceUID());
                    }
                    entry_observation.add(tmpen);
                }
           
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
}
