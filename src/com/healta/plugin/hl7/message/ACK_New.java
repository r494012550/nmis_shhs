package com.healta.plugin.hl7.message;

import java.text.ParseException;
import java.util.Date;

import com.healta.plugin.hl7.Hl7Exception;
import com.healta.util.DateUtil;

public class ACK_New {

	private HL7Message ack;
	
	public ACK_New(HL7Message message ,Hl7Exception hl7ex) throws ParseException{
		
		String receivingApplication="UNKNOWN";
		String receivingFacility="UNKNOWN";
		String messageControlID="UNKNOWN";
		
		if(message!=null){
			if(message.get("MSH.3")!=null){
				receivingApplication=message.get("MSH.3");
			}
			if(message.get("MSH.4")!=null){
				receivingFacility=message.get("MSH.4");
			}
			if(message.get("MSH.10")!=null){
				messageControlID=message.get("MSH.10");
			}
		}
		String mshstr="MSH|^~\\&|Healta|Healta|"+receivingApplication+"|"+receivingFacility+"|"+DateUtil.dtsHL7Date(new Date())+"||ACK^^ACK_ACK|ack"+DateUtil.dtsHL7Date(new Date())+"|P|2.3";
		
		ack=new HL7Message();
		ack.addSegments(mshstr);
		if(hl7ex!=null){
			//MSA|AR|ZZ9380|UNKNOWN COUNTY CODE
			String msastr1="MSA|"+hl7ex.getAcknowledgementCode()+"|"+messageControlID+"|"+hl7ex.getMessage();
			ack.addSegments(msastr1);
		}
		else{
			String msastr2="MSA|AA|"+messageControlID;
			ack.addSegments(msastr2);
		}
	}
	
	public HL7Message getAck() {
		return ack;
	}

	public void setAck(HL7Message ack) {
		this.ack = ack;
	}

	public String toHL7String(){
		return ack.toHL7String();
	}
	
	public static void main(String[] args) {
		String me="MSH|^~\\&|IWM|SIEMENS|EXTERNAL|HIS|20130221140925||ORU^R01|IWM201302211409254000052|P|2.4|||NE|AL||\r"+
			"PID|||PATID1234^5^M11||JONES^WILLIAM^A^III||19610615|M||C|1200 N ELM STREET^GREENSBORO^NC^27401 1020|GL|(919)379 1212|(919)271 3434||S||PATID12345001^2^M10|123456789|987654^NC|\r"+
			"ORC|NW|z3678^LIS|33570773^CR||||||19980727000000|||HAVILAND|\r"+
			"OBR|4000006|z3678|33570773^CR|285^右颞颌关节张口闭口位^CR||||||||||||||||1||19980727101010|||F|||||||1^管理员~4^系统管理员|\r"+
			"OBR|4000006|z3678|33570773^CR|284^下颌骨双侧位^CR||||||||||||||||2||19980727101010|||F|||||||1^管理员~4^系统管理员|\r"+
			"NTE|1||||||||||||||||||||\r"+
			"OBX|1|CE|&IMP|1|||||||||||||||||||||||||||||||||||||||||||||||\r"+
			"OBX|2|CE|&REC|2|右侧上颌窦炎。%enter%左侧上颌窦炎。%enter%右侧颧弓未见明显异常。%enter%副鼻窦未见明显异常。||||||||||||||||||||||||||||||||||||||||||||||\r"+
			"OBX|3|TX|&GDT|3|右侧上颌窦腔密度增高、模糊；窦腔变小，骨壁结构未见异常改变。%enter%左侧上颌窦腔密度增高、模糊；窦腔变小，骨壁结构未见异常改变。%enter%右侧颧弓形态结构良好，未见明显骨质增生破坏，未见明显外伤性改变。%enter%两侧额窦、筛窦及上颌窦发育气化良好；骨壁结构正常，未见密度异常改变。|||N||||||||||||||||||||||||\r"+
			"OBX|4|RP|&RRP|4|4000006.pdf||||||||||||||||||||||||||||||||||||||||||||||\r";
		
		HL7Message message=new HL7Message(me);
		
		try {
			ACK_New ack=new ACK_New(message, new Hl7Exception("dd", "error"));
			
			System.out.println(ack.toHL7String());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
