package com.healta.plugin.queueup;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alibaba.druid.util.StringUtils;
import com.healta.constant.CallCommand;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;


public class QueuingUpService {
	private final static Logger log = Logger.getLogger(QueuingUpService.class);
	private final static String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final ConcurrentHashMap<Integer, PriorityBlockingQueue<Node>> QUEUEMAP=new ConcurrentHashMap<Integer, PriorityBlockingQueue<Node>>();
	
	private static List<Record> examinepatientlist = new ArrayList<Record>();
	private static List<Record> interrogationpatientlist = new ArrayList<Record>();

	public static void initQueue(){
		examinepatientlist.clear();
		interrogationpatientlist.clear();
	}
	
	public static synchronized boolean offer(Node node){
		boolean ret=true;

		log.info("queue.offer: "+node);
		PriorityBlockingQueue<Node> queue=QUEUEMAP.get(node.getModalityid());
		if(queue==null){
			queue=new PriorityBlockingQueue<Node>();
			QUEUEMAP.put(node.getModalityid(), queue);
		}
		ret=queue.offer(node);
		Node head=queue.peek();
		String filestr = getFilestr(head,CallCommand.update_waiting_list);
        try {
        	log.info("===>offerfilestr:"+filestr);
			//FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+node.getModalityid()+".txt"), filestr, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}
	

	
	public static synchronized Node poll(Integer modalityid){
		Node node=null;
		PriorityBlockingQueue<Node> queue=null;
		try{
			queue=QUEUEMAP.get(modalityid);
			if(queue!=null){
				node=queue.poll();
				log.info("queue.poll: "+node);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			String filestr="";
			if(node!=null){
				String line =System.getProperty("line.separator");
				filestr=StrKit.getRandomUUID()+line+node.getSn()+" "+node.getPatientname()+line;
				
				List<Node> list=new ArrayList<Node>();
				queue.stream().forEach(x->{list.add(x);});
				list.sort(queue.comparator());
				for(Node e:list){
					filestr=filestr.concat(e.getSn()).concat(" ").concat(e.getPatientname()).concat(";");
				}
				
			}
			try {
				log.info("===>pollfilestr:"+filestr);
				FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+modalityid+".txt"), filestr, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return node;
	}
	
	//检查叫号，检查完成
	public static synchronized boolean remove(Node node){
		boolean ret=true;

		log.info("examine queue.remove: "+node);

		try{
			log.info("modalityid===="+node.getModalityid());

			Record record = new Record();
			record.set("patientname", node.getPatientname());
			record.set("dicmessage", node.getDicMessage());
			record.set("sendtime", new Date());
			
			if(examinepatientlist.size() == PropKit.use("system.properties").getInt("ExamineCallingLines")) {
				//判断patientinfolist数组长度是否超过规定大小，是则需要替换发送时间早的record
				int mindateindex = 0;
				Date mindate = examinepatientlist.get(0).getDate("sendtime");
				for(int i=0; i < examinepatientlist.size(); i++) {
					if(examinepatientlist.get(i).getDate("sendtime").compareTo(mindate) < 0 ) {
						mindateindex = i;
					}
				}
				examinepatientlist.set(mindateindex, record);
			}else {
				examinepatientlist.add(record);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			String filestr="";
			filestr = getFilestr(node,CallCommand.call);
			log.info("进入filetr");
			log.info(filestr);
			try {
				log.info("===>removefilestr:"+filestr);
				ActiveMQ.sendTextMessage(filestr, MQSubject.TOPICEXAMINE.getSendName(), StrKit.getRandomUUID(), 4);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return ret;
	}
	
	
	//问诊叫号
	public static synchronized boolean InterrogationRemove(Node node){
		boolean ret=true;

		log.info("queue.InterrogationRemove: "+node);
		try{
			log.info("modalityid===="+node.getModalityid());

			Record record = new Record();
			record.set("studyid", node.getStudyid());
			record.set("patientname", node.getPatientname());
			record.set("sendtime", new Date());
			
			//需要判断是否是同一个病人
			if(!interrogationpatientlist.isEmpty()) {
				if(StringUtils.equals(node.getStudyid(), interrogationpatientlist.get(0).getStr("studyid"))) {
					//是一个人
					//判断interrogationlpatientlist数组长度是否为5，是则需要替换发送时间早的record
					if(interrogationpatientlist.size() == PropKit.use("system.properties").getInt("InterrogationCallingLines")) {
						Date mindate = interrogationpatientlist.get(0).getDate("sendtime");
						int minindex = 0;
						for(int i=0; i<interrogationpatientlist.size(); i++) {
							if(interrogationpatientlist.get(i).getDate("sendtime").compareTo(mindate) <0) {
								minindex = i;
							}
						}
						interrogationpatientlist.set(minindex,record);
						
					}else {
						interrogationpatientlist.add(record);
					}
				}else {
					//不是同一个人，清空list
					interrogationpatientlist.clear();
					interrogationpatientlist.add(record);
				}
			}else{
				interrogationpatientlist.add(record);
			}
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			String filestr="";
			filestr = Interrogationfilestr(node,CallCommand.call);
			log.info("进入filetr");
			log.info(filestr);
			try {
				log.info("===>removefilestr:"+filestr);
				ActiveMQ.sendTextMessage(filestr, MQSubject.TOPICINTERROGATION.getSendName(), StrKit.getRandomUUID(), 4);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return ret;
	}
	
	//问诊完成
	public static synchronized boolean InterroagationCompleted(Node node){
		boolean ret=true;

		log.info("queue.InterroagationCompleted: "+node);
		try{
			log.info("modalityid===="+node.getModalityid());
			
			if(node != null) {
				interrogationpatientlist.clear();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			String filestr="";
			filestr = Interrogationfilestr(node,CallCommand.interrogation_completed);
			log.info("进入filetr");
			log.info(filestr);
			try {
				log.info("===>removefilestr:"+filestr);
				ActiveMQ.sendTextMessage(filestr, MQSubject.TOPICINTERROGATION.getSendName(), StrKit.getRandomUUID(), 4);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return ret;
	}
	
	public static synchronized boolean repeatCall(Node node){
		boolean ret=true;
		
		log.info("queue.repeatCall: "+node);
		PriorityBlockingQueue<Node> queue=null;
		try{
			queue=QUEUEMAP.get(node.getModalityid());
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			String filestr = getFilestr(node,CallCommand.call_update_waiting_list);
			try {
				//FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+node.getModalityid()+".txt"), filestr, "UTF-8");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return ret;
	}
	
	public static synchronized boolean delete(Node node){
		boolean ret=true;

		log.info("queue.delete: "+node);
		PriorityBlockingQueue<Node> queue=null;
		try{
			queue=QUEUEMAP.get(node.getModalityid());
			if(queue!=null){
				boolean delete=queue.remove(node);
				log.info("delete:"+delete);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			String filestr="";
			filestr = getFilestr(node,CallCommand.update_waiting_list);
			try {
				log.info("===>deletefilestr:"+filestr);
				//FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+node.getModalityid()+".txt"), filestr, "UTF-8");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return ret;
	}
	
	
	//检查发送的字符串
	public static String getFilestr(Node head,String call_command) {
		String filestr="";
		filestr = filestr+head.getModalityid()+"_";
		if(CallCommand.call.equals(call_command)) {
			filestr=filestr+StrKit.getRandomUUID()+LINE_SEPARATOR+CallCommand.call+LINE_SEPARATOR;//Message ID + Message Type
			filestr=filestr+head.getDoctor()+LINE_SEPARATOR+LINE_SEPARATOR;//doctor and picture
			for(int i=0; i<examinepatientlist.size(); i++) {
				filestr=filestr+examinepatientlist.get(i).getStr("patientname")+"="+examinepatientlist.get(i).getStr("dicmessage")+";";
			}
			filestr=filestr+LINE_SEPARATOR;		
		}else {
			filestr=StrKit.getRandomUUID()+LINE_SEPARATOR+call_command+LINE_SEPARATOR;//Message ID + Message Type
			filestr=filestr+LINE_SEPARATOR+LINE_SEPARATOR;//doctor and picture
			filestr=filestr+LINE_SEPARATOR;//patient to operate
		}
		return filestr;
	}
	
	//问诊发送的字符串
	public static String Interrogationfilestr(Node node,String call_command) {
		String filestr = "";
		filestr = filestr+node.getModalityid()+"_";
		if(CallCommand.call.equals(call_command)) {
			filestr=filestr+StrKit.getRandomUUID()+LINE_SEPARATOR+CallCommand.call+LINE_SEPARATOR;//Message ID + Message Type
			filestr=filestr+node.getDoctor()+LINE_SEPARATOR+LINE_SEPARATOR;//doctor and picture
			for(int i=0; i<interrogationpatientlist.size(); i++) {
				filestr=filestr+interrogationpatientlist.get(i).getStr("patientname")+";";
			}
			filestr=filestr+LINE_SEPARATOR;	
		}else {
			filestr=filestr+StrKit.getRandomUUID()+LINE_SEPARATOR+call_command+LINE_SEPARATOR;//Message ID + Message Type
			filestr=filestr+LINE_SEPARATOR+LINE_SEPARATOR;//doctor and picture
			filestr=filestr+LINE_SEPARATOR;//patient to operate
		}
		return filestr;
	}
	
	public static void main(String[] args) {

	}
	
	
}
