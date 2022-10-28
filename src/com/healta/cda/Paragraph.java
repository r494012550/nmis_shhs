package com.healta.cda;
import org.dom4j.Element;
public class Paragraph {
    
    public String section_id;
    public String studyInstanceUID;
    public String caption;
    public String content_ID;
    public String text;
    
    
    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
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


    public String getCaption() {
        return caption;
    }


    public void setCaption(String caption) {
        this.caption = caption;
    }


    public String getContent_ID() {
        return content_ID;
    }


    public void setContent_ID(String content_ID) {
        this.content_ID = content_ID;
    }


    public Paragraph(Element e,String section_id){
        this.setSection_id(section_id);
        this.setCaption(e.elementText("caption"));
        this.setContent_ID(e.element("content")!=null?e.element("content").attributeValue("ID"):null);
        this.setText(e.getText());
    }
}
