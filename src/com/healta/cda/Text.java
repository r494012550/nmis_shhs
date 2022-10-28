package com.healta.cda;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

public class Text {
    
    public String text;
    public String section_id;
    public ArrayList paragraphs;
    
    

    public String getSection_id() {
        return section_id;
    }


    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

    public ArrayList getParagraphs() {
        return paragraphs;
    }


    public void setParagraphs(ArrayList paragraphs) {
        this.paragraphs = paragraphs;
    }


    public Text(Element e,String section_id){
        this.setText(e.asXML());
        this.setSection_id(section_id);
//        List pas=e.elements("paragraph");
//        if(pas!=null){
//            paragraphs =new ArrayList();
//            for(int i=0;i<pas.size();i++){
//                paragraphs.add(new Paragraph((Element)pas.get(i),section_id));
//            }
//        }
        
//        List tables=e.elements("table");
    }
}
