package com.healta.plugin.queueup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.healta.constant.PatientSource;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class Node implements Comparable<Node> , Serializable{

	private static final long serialVersionUID = -1084876866162883942L;
	private static final String regEx="[^0-9]";
	private boolean emergency=false;
	private Integer priority;
	private String studyid;
	private String patientname;
	private String sn;
	private String modality;
	private Integer modalityid;
	private String modalityname;
	private String location;
	private String doctor;
	private String dicmessage;
	
	public Node(boolean emergency){
		this.emergency=emergency;
	}
	
	public Node(Record record,String doctor){
		if(StrKit.equals(record.getStr("patientsource"), PatientSource.emergency)){
			this.emergency=true;
		}
		else{
			this.emergency=false;
		}
		this.studyid = record.getStr("studyid");
		this.patientname = record.getStr("patientname");
		setSn(record.getStr("sequencenumber"));
		this.modalityid = record.getInt("modalityid");
		this.modalityname = record.getStr("modality_name");
		this.modality = record.getStr("modality_type");
		this.location = record.getStr("location");
		this.doctor = doctor;
		this.dicmessage = record.getStr("dicmessage");
	}
	
	public Node(String modality,Integer modalityid,String doctor){
		this.emergency=false;
		this.setModality(modality);
		this.setModalityid(modalityid);
		this.setDoctor(doctor);
	}
	
	public String getStudyid() {
		return studyid;
	}
	public void setStudyid(String studyid) {
		this.studyid = studyid;
	}
	public String getPatientname() {
		return patientname;
	}
	public void setPatientname(String patientname) {
		this.patientname = patientname;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		if(StrKit.notBlank(sn)){
			Pattern p = Pattern.compile(regEx);   
			Matcher m = p.matcher(sn);   
			//System.out.println( m.replaceAll("").trim());
			Float snint=Float.valueOf(m.replaceAll("").trim());
			Integer w=1000000;
			if(emergency){
				w=1000000;
			}
			else{
				snint=snint+1000;
			}
			Float f=(snint/1000)*w;
			this.priority=f.intValue();
			//System.out.println(this.priority);
		}
		this.sn = sn;
	}
	public Integer getModalityid() {
		return modalityid;
	}
	public void setModalityid(Integer modalityid) {
		this.modalityid = modalityid;
	}
	public String getModalityname() {
		return modalityname;
	}
	public void setModalityname(String modalityname) {
		this.modalityname = modalityname;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDoctor() {
		return doctor;
	}
	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}
	public String getModality() {
		return modality;
	}
	public void setModality(String modality) {
		this.modality = modality;
	}
	public Integer getPriority() {
		return priority;
	}
	
	public String getDicMessage() {
		return dicmessage;
	}
	
	/**
	     *
	     * @Description:当前对象和其他对象做比较，当前优先级大就返回-1，优先级小就返回1
	     * 值越小优先级越高
	     * @param TODO
	     * @author 
	     * @date 2017年8月27日 上午11:28:10
	     */
	@Override
	public int compareTo(Node node) {
		return this.priority.compareTo(node.getPriority());
	}
	
	@Override
    public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
        	Node n= (Node) obj;
        	if(n.getModalityid()==null||n.getSn()==null){
        		return false;
        	}
        	else{
        		return (this.modalityid+"--"+this.studyid).equalsIgnoreCase(n.getModalityid()+"--"+n.getStudyid());
        	}
        
		}
        return false;
    }
	
	@Override
	public String toString(){
		return "emergency="+emergency+
				";priority="+priority+
				";studyid="+studyid+
				";patientname="+patientname+
				";sn="+sn+
				";modality="+modality+
				";modalityid="+modalityid+
				";modalityname="+modalityname+
				";location="+location+
				";doctor="+doctor;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			
			PriorityBlockingQueue<Node> queue = new PriorityBlockingQueue<Node>();
			
			for(int i=0; i<12; i++){
				Node node = new Node(false);
				node.setSn("CT00"+(i+1)+"");
				node.setPatientname("李四"+i);
				node.setModalityid(1);
				queue.add(node);
			}
			for(int i=12; i<16; i++){
				Node node = new Node(true);
				node.setSn("CT00"+(i)+"");
				node.setPatientname("张珊"+i);
				node.setModalityid(1);
				queue.add(node);
			}
		
		
		for(int i=0; i<16; i++){
			queue.poll();
			
			List<Node> list=new ArrayList<Node>();
			queue.stream().forEach(x->{list.add(x);});
			list.sort(queue.comparator());
			String filestr="";
			for(Node e:list){
				filestr=filestr.concat(e.getSn()).concat(";");
			}
			
			System.out.println(filestr);
		}
	}

}
