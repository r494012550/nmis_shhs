package com.healta.plugin.hl7.message;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.regenstrief.xhl7.HL7XMLLiterate;

public class EVN {
    public final String Event_Type_Code;//事件类型代码
    
    public final String Event_TIME;//日期/时间事件  
    
    public final String Planned_Event_TIME;//日期/时间计划事件  
    
    /*
     *  01      Patient request就诊者请求
     *  02      Physician order医师医嘱
     *  03      Census management人口普查管理
     * */
    public final String Event_Reason_Code;//事件原因代码
    
    
    public final String Operator_ID;//操作员 ID  
    
    public EVN(Document msg) {
        Element evn = msg.getRootElement().element("EVN");
        List fields = evn.elements(HL7XMLLiterate.TAG_FIELD);
        Event_Type_Code=toString(fields,0);
        Event_TIME=toString(fields,1);
        Planned_Event_TIME=toString(fields,2);
        Event_Reason_Code=toString(fields,3);
        Operator_ID=toString(fields,4);
    }
    
    private static String toString(Object el) {
        return el != null ? ((Element) el).getText() : "";
    }
    
    
    private static String toString(List l,int index) {
        String ret="";
        if(l!=null&&l.size()>index){
            Object el=l.get(index);
            ret= (el != null ? ((Element) el).getText() : "");
        }
        if("<cr>".equals(ret))ret="";
        return ret;
    }
    
    public String toString() {
        return "Event_Type_Code="+Event_Type_Code+";Event_TIME="+Event_TIME+";Planned_Event_TIME="+Planned_Event_TIME+";Event_Reason_Code=" +
                Event_Reason_Code+";Operator_ID="+Operator_ID;
    }
    
}
