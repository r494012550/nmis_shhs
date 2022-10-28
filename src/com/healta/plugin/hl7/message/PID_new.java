package com.healta.plugin.hl7.message;


import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.Element;
import org.regenstrief.xhl7.HL7XMLLiterate;

public class PID_new {
    
    public String patient_UID;
    public String Patient_ID;//就诊者ID
    public String Patient_ID1;//就诊者ID1
    public String Patient_Name;//就诊者姓名
    public String Birth_Date;//出生的日期时间 
    public String identitycard;//身份证号
    /*
     *  F   Female女
        M   Male男
        O   Other其它
        U   Unknown未知
    */
    public String Sex;// 性别
    public String Patient_Alias;// 就诊者别名
    public String Patient_Address;//就诊者的地址
    public String Phone_Number ;//电话号码
    /*
     *  A       Separated分居
        D       Divorced离婚
        M       Married已婚
        S       Single未婚
        W       Widowed丧偶
     */
    public String Marital_Status;//婚姻状态
    public String Ethnic;//民族
    public String Birth_Place;//出生地
    
    
    
    
    public String Set_ID;//
    public String Patient_ID_ex;//外部ID
    public String Alternate_Patient_ID;//交替的patient ID 
    public String Mother_Maiden_Name;//婚前姓名
    public String Race;//种族
    public String County_Code;//县区代码
    public String Phone_Number_Business;//商业电话号码
    public String Primary_Language;//主要语言
    public String Religion;//3.3.2.17   宗教
    public String Patient_Account_Number;//3.3.2.18     就诊者帐号
    public String SSN_Number ;//3.3.2.19        SSN 号码
    public String Driver_Lic_Num ;//3.3.2.20    就诊者驾驶执照号码 
    public String Mother_Identifier;//3.3.2.21  母亲标识符
    public String Multiple_Birth_Indicator;//3.3.2.24   多包胎指示器
    public String Birth_Order;//3.3.2.25        出生顺序
    public String Citizenship;//3.3.2.26        户籍
    public String Veterans_Military_Status;//3.3.2.27   是否是退伍军人
    public String Nationality ;//3.3.2.28       国籍
    public String Patient_Death_DateTime;//3.3.2.29     就诊者死亡日期和时间
    public String Patient_Death_Indicator;//3.3.2.30    就诊人死亡指示
    
    public String UID;//
    
    public PID_new() {
        
    }
    public PID_new(HL7Message mess) {
//        Element evn = msg.getRootElement().element("PID");
//        List fields = evn.elements(HL7XMLLiterate.TAG_FIELD);
        
        
        Patient_ID=mess.get("PID[0].3.1");//toString(fields,2);
        
        if(Patient_ID==null||"".equals(Patient_ID.trim())){
            Patient_ID=UUID.randomUUID().toString();//GUID.createId().toSimpleString();
        }
        identitycard=mess.get("PID[0].18");
//        if(!"".equals(Patient_ID)&&Patient_ID.indexOf("^")>=0){
//            Patient_ID=Patient_ID.substring(0, Patient_ID.indexOf("^"));
//        }
        
        if(!"".equals(mess.get("PID[0].4.1"))){
            Patient_ID1=mess.get("PID[0].4.1");//toString(fields,2);
        }
        Patient_Name=mess.get("PID[0].5.1");//toString(fields,4);
        Birth_Date=mess.get("PID[0].7");//toString(fields,6);
        if(Birth_Date!=null&&!"".equals(Birth_Date)&&Birth_Date.length()>8){
            Birth_Date=Birth_Date.substring(0, 8);
        }
        Sex=mess.get("PID[0].8");//toString(fields,7);
        Patient_Alias=mess.get("PID[0].9");//toString(fields,8);
        Patient_Address=mess.get("PID[0].11");//toString(fields,10);
        
        String phone1=mess.get("PID[0].13");
        String phone2=mess.get("PID[0].14");
        
        Phone_Number=(phone1!=null?phone1:"")+"/"+(phone2!=null?phone2:"");//toString(fields,12);
        Marital_Status=mess.get("PID[0].16");//toString(fields,15);
        Ethnic=mess.get("PID[0].22");//toString(fields,21);
        Birth_Place=mess.get("PID[0].23");//toString(fields,22);
        
        
        Set_ID=mess.get("PID[0].1");//toString(fields,0);
        Patient_ID_ex=mess.get("PID[0].2");//toString(fields,1);
        Alternate_Patient_ID=mess.get("PID[0].4");//toString(fields,3);
        Mother_Maiden_Name=mess.get("PID[0].6");//toString(fields,5);
        Race=mess.get("PID[0].10");//toString(fields,9);
        County_Code=mess.get("PID[0].12");//toString(fields,11);
        Phone_Number_Business=mess.get("PID[0].14");//toString(fields,13);
        Primary_Language=mess.get("PID[0].15");//toString(fields,14);
        Religion=mess.get("PID[0].17");//toString(fields,16);
        Patient_Account_Number=mess.get("PID[0].18");//toString(fields,17);
        SSN_Number=mess.get("PID[0].19");//toString(fields,18);
        Driver_Lic_Num=mess.get("PID[0].20");//toString(fields,19);
        Mother_Identifier=mess.get("PID[0].21");//toString(fields,20);
        Multiple_Birth_Indicator=mess.get("PID[0].24");//toString(fields,23);
        Birth_Order=mess.get("PID[0].25");//toString(fields,24);
        Citizenship=mess.get("PID[0].26");//toString(fields,25);
        Veterans_Military_Status=mess.get("PID[0].27");//toString(fields,26);
        Nationality=mess.get("PID[0].28");//toString(fields,27);
        Patient_Death_DateTime=mess.get("PID[0].29");//toString(fields,28);
        Patient_Death_Indicator=mess.get("PID[0].30");//toString(fields,29);
    }
    
    
    private static String toString(Object el) {
        return el != null ? ((Element) el).getText() : "";
    }
    
    private static String toString(List l,int index) {
        String ret="";
        if(l!=null&&l.size()>index){
            Object el=l.get(index);
            List comps = ((Element)el).elements(HL7XMLLiterate.TAG_COMPONENT);
            //System.out.println("comps="+comps.size());
            if(comps!=null&&comps.size()>0){
                ret+=((Element)el).getText()+"^";
                for(int i=0;i<comps.size();i++){
                    Element e = (Element) comps.get(i);
                    ret+=e.getText()+"^";
                }
                if(ret.endsWith("^")){
                    ret=ret.substring(0, ret.length()-1);
                }
                
            }
            else{
                ret= (el != null ? ((Element) el).getText() : "");
            }
        }
        
        if("<cr>".equals(ret))ret="";
        try {
            ret=new String(ret.getBytes(),"GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    public String toString() {
        return "Patient_ID="+Patient_ID+";Patient_ID1="+Patient_ID1+";Patient_Name="+Patient_Name+";Birth_Date="+Birth_Date+";Sex=" +
                Sex+";Patient_Alias="+Patient_Alias+";Patient_Address="+Patient_Address+";Phone_Number="+Phone_Number+";Marital_Status="+Marital_Status
                +";Ethnic="+Ethnic+";Birth_Place="+Birth_Place+";Set_ID="+Set_ID
                +";Patient_ID_ex="+Patient_ID_ex+";identitycard="+identitycard
                +";Alternate_Patient_ID="+Alternate_Patient_ID+";Mother_Maiden_Name="+Mother_Maiden_Name+";Race="+Race+";County_Code="+County_Code
                +";Phone_Number_Business="+Phone_Number_Business+";Primary_Language="+Primary_Language+";Religion="+Religion
                +";Patient_Account_Number="+Patient_Account_Number+";SSN_Number="+SSN_Number+";Driver_Lic_Num="+Driver_Lic_Num+";Mother_Identifier="+Mother_Identifier
                +";Birth_Order="+Birth_Order+";Citizenship="+Citizenship+";Veterans_Military_Status="+Veterans_Military_Status
                +";Nationality="+Nationality+";Patient_Death_DateTime="+Patient_Death_DateTime+";Patient_Death_Indicator="+Patient_Death_Indicator;
    }
}
