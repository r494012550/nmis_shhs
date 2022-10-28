package com.healta.plugin.hl7.message;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.regenstrief.xhl7.HL7XMLLiterate;

import com.healta.util.DateUtil;


public class PV {
    
    public static SimpleDateFormat format_s=new SimpleDateFormat("yyyyMMDDhhmmss");
    public static SimpleDateFormat format_m=new SimpleDateFormat("yyyyMMDDhhmm");
    public static SimpleDateFormat format_h=new SimpleDateFormat("yyyyMMDDhh");
    public static SimpleDateFormat format_d=new SimpleDateFormat("yyyyMMDD");
    
    public String Set_ID;//
    /*
     * E        Emergency急诊病人
        I       Inpatient住院病人
        O       Outpatient门诊病人
        P       Preadmit提前预约住院
        R       Recurring Patient定期复诊病人
        B       Obstetrics产科病人
     */
    public String Patient_Class;//3.3.3.2       就诊者类别
    public String ward_code;//病区号
    public String room_code;//病房号
    public String bed_code;//病床号
    /*
     * A        Accident意外事故
        E       Emergency急诊
        L       Labor and Delivery临产和分娩
        R       Routine常规

     */
    public String Admission_Type;//3.3.3.4      入院类型
    public String Preadmit_Number;//3.3.3.5     提前指定的住院号
    public String Prior_Patient_Location;//3.3.3.6      以前的就诊者位置
    public String Attending_Doctor_ID;//主治医生ID
    public String Attending_Doctor_name;//主治医生姓名
    public String Referring_Doctor_ID;//转诊医生ID
    public String Referring_Doctor_name;//转诊医生姓名
    public String Consulting_Doctor_ID;//3.3.3.9        顾问医生ID
    public String Consulting_Doctor_name;//3.3.3.9      顾问医生姓名
    public String Hospital_Service;//3.3.3.10   医院服务
    public String Temporary_Location;//3.3.3.11 临时位置
    public String Preadmit_Test_Indicator;//3.3.3.12    预约住院化验指示器
    public String Readmission_Indicator;//3.3.3.13      再次入院指示器
    public String Admit_Source;//3.3.3.14       入院来源
    public String Ambulatory_Status;//3.3.3.15  不卧床的状态
    public String VIP_Indicator;//3.3.3.16      VIP 指示器  
    public String Admitting_Doctor_ID;//3.3.3.17        接收就诊者入院的医生ID
    public String Admitting_Doctor_name;//3.3.3.17      接收就诊者入院的医生姓名
    public String Patient_Type;//3.3.3.18       就诊者类型
    public String Visit_Number;//3.3.3.19       就诊号码
    public String Financial_Class;//3.3.3.20    财务种类
    public String Charge_Price_Indicator;//3.3.3.21     费用价格指示器
    public String Courtesy_Code;//3.3.3.22      赞助代码
    public String Credit_Rating;//3.3.3.23      信贷等级
    public String Contract_Code;//3.3.3.24      合约代码
    public String Contract_Effective_Date;//3.3.3.25    合约的有效日期
    public String Contract_Amount;//3.3.3.26    合约总额
    public String Contract_Period;//3.3.3.27    合约周期
    public String Interest_Code;//3.3.3.28      利息代码
    public String Transfer_to_Bad_Debt_Code;//3.3.3.29  转成债务的代码
    public String Transfer_to_Bad_Debt_Date;//3.3.3.30  转成债务的日期
    public String Bad_Debt_Agency_Code;//3.3.3.31       债务代理机构代码
    public String Bad_Debt_Transfer_Amount;//3.3.3.32   债务转让总额
    public String Bad_Debt_Recovery_Amount;//3.3.3.33   债务赔偿总额
    public String Delete_Account_Indicator;//3.3.3.34   删除帐户指示器
    public String Delete_Account_Date;//3.3.3.35        删除帐户日期
    public String Discharge_Disposition;//3.3.3.36      出院处理
    public String Discharged_to_Location;//3.3.3.37     出院后的位置
    public String Diet_Type;//3.3.3.38  日常饮食类型
    public String Servicing_Facility;//3.3.3.39 维修设备
    /*
     * C        Closed关闭
        H       Housekeeping保留
        O       Occupied占有
        U       Unoccupied空床
        K       Contaminated污染
        I       Isolated隔离

     */
    public String Bed_Status;//3.3.3.40 床位状态
    public String Account_Status;//3.3.3.41     帐户状态
    public String Pending_Location;//3.3.3.42   待定位置
    public String Prior_Temporary_Location;//3.3.3.43   早先的临时位置
    public Date Admit_Date;//3.3.3.44   入院日期/时间  
    public Date Discharge_Date;//3.3.3.45       出院 日期/时间  
    public Float Current_Patient_Balance;//3.3.3.46     当前就诊者的余额
    public Float Total_Charges;//3.3.3.47       总费用
    public Float Total_Adjustments;//3.3.3.48   总调整
    public Float Total_Payments;//3.3.3.49      总共要支付的金额
    public String Alternate_Visit_ID;//3.3.3.50 交替就诊ID   
    public String Visit_Indicator;//3.3.3.51    就诊指示器
    public String Other_Healthcare_Provider;//3.3.3.52  其他医护提供者
    
    
    public PV(HL7Message mess) throws ParseException {
//        Element evn = msg.getRootElement().element("PV1");
//        List fields = evn.elements(HL7XMLLiterate.TAG_FIELD);
        
        
        this.Set_ID=mess.get("PV1[0].1");//toString(fields,0);
        this.Patient_Class=mess.get("PV1[0].2");//toString(fields,1);
//        String ward=mess.get("PV1[0].3.1");//toString(fields,2);
//        if(!"".equals(ward)){
            
//            Object[] tmp=split(ward);
//            System.out.println("ward="+ward+";tmp="+tmp.length+";--"+ward.indexOf("^"));
            this.ward_code=mess.get("PV1[0].3.1");//tmp[0].toString();
//            if(tmp.length>1)
                this.room_code=mess.get("PV1[0].3.2");//tmp[1].toString();
//            if(tmp.length>2)
                this.bed_code=mess.get("PV1[0].3.3");//tmp[2].toString();
//        }
        this.Admission_Type=mess.get("PV1[0].4");//toString(fields,3);
        this.Preadmit_Number=mess.get("PV1[0].5");//toString(fields,4);
        this.Prior_Patient_Location=mess.get("PV1[0].6");//toString(fields,5);
//        String Attending_Doctor=toString(fields,6);
//        if(!"".equals(Attending_Doctor)){
//            Object tmp[]=split2(Attending_Doctor);
            this.Attending_Doctor_ID=mess.get("PV1[0].7.1");//tmp[0].toString();
//            if(tmp.length>1)
                this.Attending_Doctor_name=mess.get("PV1[0].7.2");//tmp[1].toString();
//        }
       
//        String Referring_Doctor=toString(fields,7);
//        if(!"".equals(Referring_Doctor)){
//            Object tmp[]=split2(Referring_Doctor);
            this.Referring_Doctor_ID=mess.get("PV1[0].8.1");//tmp[0].toString();
//            if(tmp.length>1)
                this.Referring_Doctor_name=mess.get("PV1[0].8.2");//tmp[1].toString();
//        }
        
//        String Consulting_Doctor=toString(fields,8);
//        if(!"".equals(Consulting_Doctor)){
//            Object tmp[]=split2(Consulting_Doctor);
            this.Consulting_Doctor_ID=mess.get("PV1[0].9.1");//tmp[0].toString();
//            if(tmp.length>1)
                this.Consulting_Doctor_name=mess.get("PV1[0].9.2");//tmp[1].toString();
//        }
        
        Hospital_Service=mess.get("PV1[0].10");//toString(fields,9);
        Temporary_Location=mess.get("PV1[0].11");//toString(fields,10);
        Preadmit_Test_Indicator=mess.get("PV1[0].12");//toString(fields,11);
        Readmission_Indicator=mess.get("PV1[0].13");//toString(fields,12);
        Admit_Source=mess.get("PV1[0].14");//toString(fields,13);
        Ambulatory_Status=mess.get("PV1[0].15");//toString(fields,14);
        VIP_Indicator=mess.get("PV1[0].16");//toString(fields,15);
        
//        String Admitting_Doctor=toString(fields,16);
//        if(!"".equals(Admitting_Doctor)){
//            Object tmp[]=split2(Admitting_Doctor);
            this.Admitting_Doctor_ID=mess.get("PV1[0].17.1");//tmp[0].toString();
//            if(tmp.length>1)
                this.Admitting_Doctor_name=mess.get("PV1[0].17.2");//tmp[1].toString();
//        }

        Patient_Type=mess.get("PV1[0].18");//toString(fields,17);
        Visit_Number=mess.get("PV1[0].19");//toString(fields,18);
        Financial_Class=mess.get("PV1[0].20");//toString(fields,19);
        Charge_Price_Indicator=mess.get("PV1[0].21");//toString(fields,20);
        Courtesy_Code=mess.get("PV1[0].22");//toString(fields,21);
        Credit_Rating=mess.get("PV1[0].23");//toString(fields,22);
        Contract_Code=mess.get("PV1[0].24");//toString(fields,23);
        Contract_Effective_Date=mess.get("PV1[0].25");//toString(fields,24);
        Contract_Amount=mess.get("PV1[0].26");//toString(fields,25);
        Contract_Period=mess.get("PV1[0].27");//toString(fields,26);
        Interest_Code=mess.get("PV1[0].28");//toString(fields,27);
        Transfer_to_Bad_Debt_Code=mess.get("PV1[0].29");//toString(fields,28);
        Transfer_to_Bad_Debt_Date=mess.get("PV1[0].30");//toString(fields,29);
        Bad_Debt_Agency_Code=mess.get("PV1[0].31");//toString(fields,30);
        Bad_Debt_Transfer_Amount=mess.get("PV1[0].32");//toString(fields,31);
        Bad_Debt_Recovery_Amount=mess.get("PV1[0].33");//toString(fields,32);
        Delete_Account_Indicator=mess.get("PV1[0].34");//toString(fields,33);
        Delete_Account_Date=mess.get("PV1[0].35");//toString(fields,34);
        Discharge_Disposition=mess.get("PV1[0].36");//toString(fields,35);
        Discharged_to_Location=mess.get("PV1[0].37");//toString(fields,36);
        Diet_Type=mess.get("PV1[0].38");//toString(fields,37);
        Servicing_Facility=mess.get("PV1[0].39");//toString(fields,38);
        
        Bed_Status=mess.get("PV1[0].40");//toString(fields,39);
        Account_Status=mess.get("PV1[0].41");//toString(fields,40);
        Pending_Location=mess.get("PV1[0].42");//toString(fields,41);
        Prior_Temporary_Location=mess.get("PV1[0].43");//toString(fields,42);
        Admit_Date=DateUtil.getHL7Date(mess.get("PV1[0].44"));//toDate(mess.get("PV1[0].44"),43);
        Discharge_Date=DateUtil.getHL7Date(mess.get("PV1[0].45"));//toDate(fields,44);
        Current_Patient_Balance=toFloat(mess.get("PV1[0].46"));//
        Total_Charges=toFloat(mess.get("PV1[0].47"));//toFloat(fields,46);
        Total_Adjustments=toFloat(mess.get("PV1[0].48"));//toFloat(fields,47);
        Total_Payments=toFloat(mess.get("PV1[0].49"));//toFloat(fields,48);
        Alternate_Visit_ID=mess.get("PV1[0].50");//toString(fields,49);
        Visit_Indicator=mess.get("PV1[0].51");//toString(fields,50);
        Other_Healthcare_Provider=mess.get("PV1[0].52");//toString(fields,51);
    }
    
    private static Date toDate(List l,int index) {
        Date ret=null;

        if(l!=null&&l.size()>index){
            Element el=(Element)l.get(index);
            
            if(el!=null&&!"".equals(el.getText())&&!"<CR>".equals(el.getText())){
                try {
                    if(el.getText().length()==14){
                        ret=format_s.parse(el.getText());
                    }
                    else if(el.getText().length()==12){
                        ret=format_m.parse(el.getText());
                    }
                    else if(el.getText().length()==10){
                        ret=format_h.parse(el.getText());
                    }
                    else if(el.getText().length()==8){
                        ret=format_d.parse(el.getText());
                    }
                    else{
                        throw new IllegalArgumentException("error date format in PV1-"+(index+1)+":"+el.getText());
                    }
                    
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    throw new IllegalArgumentException("error date format in PV1-"+(index+1)+":"+el.getText());
                }
            }
        }
        return ret;
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
    
    private static Float toFloat(String str) {
        Float ret=null;
//        if(l!=null&&l.size()>index){
//            Element el=(Element)l.get(index);
//            if(el!=null&&!"".equals(el.getText())){
//                
//                try {
//                    ret=Float.valueOf(el.getText());
//                } catch (NumberFormatException e) {
//                    // TODO Auto-generated catch block
//                    throw new IllegalArgumentException("error number format in PV1-"+(index+1));
//                }
//                
//            }
//        }
        
        if(str!=null&&!"".equals(str)){
            try {
              ret=Float.valueOf(str);
          } catch (NumberFormatException e) {
              // TODO Auto-generated catch block
              throw new IllegalArgumentException("error number format in PV1-");
          }
        }
        return ret;
    }
    
    private static Object[] split(String str){
        Object[] ret=null;
        if(str!=null&&!"".equals(str)){
            List list=new ArrayList();
            while(str.indexOf("^")>0){
                if(str.indexOf("^")>0){
                    list.add(str.substring(0, str.indexOf("^")));
                    str=str.substring(str.indexOf("^")+1);
                }
            }
            list.add(str);
            ret=list.toArray();
        }
        return ret;
    }
    
    private static Object[] split2(String str){
        Object[] ret=null;
        if(str!=null&&!"".equals(str)){
            List list=new ArrayList();
            
                if(str.indexOf("^")>0){
                    list.add(str.substring(0, str.indexOf("^")));
                    str=str.substring(str.indexOf("^")+1);
                    list.add(str);
                }
            
            
            ret=list.toArray();
        }
        return ret;
    }
    
    public String toString() {
        
        return "Set_ID="+Set_ID+";Patient_Class="+Patient_Class+";ward_code="+ward_code+";room_code="+room_code+";bed_code="+bed_code
                +";Admission_Type="+Admission_Type+";Preadmit_Number="+Preadmit_Number+";Prior_Patient_Location="+Prior_Patient_Location
                +";Attending_Doctor_ID="+Attending_Doctor_ID+";Attending_Doctor_name="+Attending_Doctor_name+";Referring_Doctor_ID="+Referring_Doctor_ID
                +";Referring_Doctor_name="+Referring_Doctor_name+";Consulting_Doctor_ID="+Consulting_Doctor_ID+";Consulting_Doctor_name="+Consulting_Doctor_name
                +";Hospital_Service="+Hospital_Service+";Temporary_Location="+Temporary_Location+";Preadmit_Test_Indicator="+Preadmit_Test_Indicator
                +";Readmission_Indicator="+Readmission_Indicator+";Admit_Source="+Admit_Source+";Ambulatory_Status="+Ambulatory_Status
                +";VIP_Indicator="+VIP_Indicator+";Admitting_Doctor_ID="+Admitting_Doctor_ID+";Admitting_Doctor_name="+Admitting_Doctor_name
                +";Patient_Type="+Patient_Type+";Visit_Number="+Visit_Number+";Financial_Class="+Financial_Class+";Charge_Price_Indicator="+Charge_Price_Indicator
                +";Courtesy_Code="+Courtesy_Code+";Credit_Rating="+Credit_Rating+";Contract_Code="+Contract_Code+";Contract_Effective_Date="+Contract_Effective_Date
                +";Contract_Amount="+Contract_Amount+";Contract_Period="+Contract_Period+";Interest_Code="+Interest_Code
                +";Transfer_to_Bad_Debt_Code="+Transfer_to_Bad_Debt_Code+";Transfer_to_Bad_Debt_Date="+Transfer_to_Bad_Debt_Date
                +";Bad_Debt_Agency_Code="+Bad_Debt_Agency_Code+";Bad_Debt_Transfer_Amount="+Bad_Debt_Transfer_Amount+";Bad_Debt_Recovery_Amount="+Bad_Debt_Recovery_Amount
                +";Delete_Account_Indicator="+Delete_Account_Indicator+";Delete_Account_Date="+Delete_Account_Date+";Discharge_Disposition="+Discharge_Disposition
                +";Discharged_to_Location="+Discharged_to_Location+";Diet_Type="+Diet_Type+";Servicing_Facility="+Servicing_Facility
                +";Bed_Status="+Bed_Status+";Account_Status="+Account_Status+";Pending_Location="+Pending_Location+";Prior_Temporary_Location="+Prior_Temporary_Location
                +";Admit_Date="+Admit_Date+";Discharge_Date="+Discharge_Date+";Current_Patient_Balance="+Current_Patient_Balance
                +";Total_Charges="+Total_Charges+";Total_Adjustments="+Total_Adjustments+";Total_Payments="+Total_Payments+";Alternate_Visit_ID="+Alternate_Visit_ID
                +";Visit_Indicator="+Visit_Indicator+";Other_Healthcare_Provider="+Other_Healthcare_Provider;

    }
}
