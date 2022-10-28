package com.healta.plugin.queueup;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.healta.constant.CallCommand;
import com.healta.constant.StudyOrderStatus;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.expr.ast.Array;


public class QueuingUpSignService {
	private final static Logger log = Logger.getLogger(QueuingUpSignService.class);
	private static final String MAP_KEY = "signin";
	private final static String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final ConcurrentHashMap<String, PriorityBlockingQueue<Node>> QUEUEMAP=new ConcurrentHashMap<String, PriorityBlockingQueue<Node>>();
	
	private static String currentDate;
	
	public static void initQueue(){
		
		QUEUEMAP.clear();
		currentDate=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		//Db.delete("delete from queueup_queue where currentdate!=?", currentDate);

//		List<Record> list=Db.find("select distinct modalityid from queueup_queue where currentdate=?",currentDate);
//		for(Record re:list){
//			re.getInt("modalityid");
//			
//			List<QueueupQueue> qqs=QueueupQueue.dao.find("select * from queueup_queue where currentdate=? and modalityid=?", currentDate,re.getInt("modalityid"));
//			ArrayList<Node> nodes=new ArrayList<Node>();
//			for(QueueupQueue qq:qqs){
//				Node node = JSON.parseObject(qq.getJson(), new TypeReference<Node>(){});
//				nodes.add(node);
//			}
//			QUEUEMAP.put(re.getInt("modalityid"), new ConcurrentLinkedQueue<Node>(nodes));
//		}
		

//		List<QueueupQueue> records=QueueupQueue.dao.find("select * from queueup_queue where currentdate=?", currentDate);
//		for(QueueupQueue record:records){
//			ArrayList<Node> nodes = JSON.parseObject(record.getJson(), new TypeReference<ArrayList<Node>>() {});
//			QUEUEMAP.put(record.getModalityid(), new ConcurrentLinkedQueue<Node>(nodes));
//		}

		List<Record> list=Db.find("select (select patientname from patient where patient.id=studyorder.patientidfk) as patientname,studyid,modality_type,modalityid,"
				+ "(select dic_modality.modality_name+'@-@'+location from dic_modality where dic_modality.id=studyorder.modalityid) as modalityinfo,sequencenumber "
				+ "from studyorder where appointmenttime >=? and appointmenttime<? and status = ?",
				currentDate,LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE),StudyOrderStatus.ARRIVED);
		
		
		for(Record re:list){
			Integer modalityid=re.getInt("modalityid");
			PriorityBlockingQueue<Node> clq=QUEUEMAP.get(MAP_KEY);
			
			if(clq==null){
				clq=new PriorityBlockingQueue<Node>();
				QUEUEMAP.put(MAP_KEY, clq);
			}
			
			Node node=new Node(false);
			node.setModality(re.getStr("modality_type"));
			node.setModalityid(modalityid);
			String modalityinfo=re.getStr("modalityinfo");
			if(StrKit.notBlank(modalityinfo)){
				node.setModalityname(modalityinfo.split("@-@")[0]);
				node.setLocation(modalityinfo.split("@-@")[1]);
			}
			//node.setDoctor(doctor);
			node.setPatientname(re.getStr("patientname"));
			node.setSn(re.getStr("sequencenumber"));
			node.setStudyid(re.getStr("studyid"));
			clq.add(node);
		}
		
	}
	
	public static synchronized boolean offer(Node node){
		boolean ret=true;

		log.info("queue.offer: "+node);
		PriorityBlockingQueue<Node> queue=QUEUEMAP.get(MAP_KEY);
		if(queue==null){
			queue=new PriorityBlockingQueue<Node>();
			QUEUEMAP.put(MAP_KEY, queue);
		}
		ret=queue.offer(node);
		Node head=queue.peek();
		String filestr = getFilestr(queue,head,CallCommand.update_waiting_list);
        try {
			FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+"signtointe"+".txt"), filestr, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}
	
//	public static synchronized boolean insert(Node node,Integer index){
//		boolean ret=true;
//
//		log.info("queue.offer: "+node);
//		PriorityBlockingQueue<Node> queue=QUEUEMAP.get(node.getModalityid());
//		if(queue==null){
//			queue=new PriorityBlockingQueue<Node>();
//			QUEUEMAP.put(node.getModalityid(), queue);
//		}
//		ret=queue.offer(node);
//		
//		//queue.
//		//ArrayBlockingQueue<E>
//		Node head=queue.peek();
//		String line =System.getProperty("line.separator");
//		String filestr=StrKit.getRandomUUID()+line+head.getSn()+" "+head.getPatientname()+line;
//		Iterator<Node> iterator = queue.iterator();
//		iterator.next();
//        while (iterator.hasNext()){
//        	Node e=iterator.next();
//        	filestr=filestr.concat(e.getSn()).concat(" ").concat(e.getPatientname()).concat(";");
//        }
//        try {
//			FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+node.getModalityid()+".txt"), filestr, "UTF-8");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return ret;
//	}
	
	public static synchronized Node poll(Integer modalityid){
		Node node=null;
		PriorityBlockingQueue<Node> queue=null;
		try{
			queue=QUEUEMAP.get(MAP_KEY);
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
				
//				Iterator<Node> iterator = queue.iterator();
//				//iterator.next();
//		        while (iterator.hasNext()){
//		        	Node e=iterator.next();
//		        	filestr=filestr.concat(e.getSn()).concat(" ").concat(e.getPatientname()).concat(";");
//		        }
			}
			try {
				log.info("===>pollfilestr:"+filestr);
				FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+"signtointe"+".txt"), filestr, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return node;
	}
	
	
	public static synchronized boolean remove(Node node){
		boolean ret=true;

		//Node node1=null;
		log.info("queue.remove: "+node);
		PriorityBlockingQueue<Node> queue=null;
		try{
			log.info("modalityid===="+node.getModalityid());
			queue=QUEUEMAP.get(MAP_KEY);
			log.info("queue----");
			log.info(queue);
			if(queue!=null){
				queue.remove(node);
				//node1=queue.peek();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			String filestr="";
			filestr = getFilestr(queue,node,CallCommand.call_update_waiting_list);
			log.info("进入filetr");
			log.info(filestr);
			try {
				log.info("===>removefilestr:"+filestr);
				FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+"signtointe"+".txt"), filestr, "UTF-8");
			} catch (IOException e) {
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
			queue=QUEUEMAP.get(MAP_KEY);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			String filestr = getFilestr(queue,node,CallCommand.call_update_waiting_list);
			try {
				FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+"signtointe"+".txt"), filestr, "UTF-8");
			} catch (IOException e) {
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
			queue=QUEUEMAP.get(MAP_KEY);
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
			filestr = getFilestr(queue,node,CallCommand.update_waiting_list);
			try {
				log.info("===>deletefilestr:"+filestr);
				FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+"signtointe"+".txt"), filestr, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return ret;
	}
	
	public static synchronized boolean switchUser(Node node){
		boolean ret=true;
		
		log.info("queue.switchUser: "+node);
		PriorityBlockingQueue<Node> queue=null;
		try{
			queue=QUEUEMAP.get(node.getModalityid());
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			String filestr = getFilestr(queue,node,CallCommand.user_login);

			try {
				FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("queueupdir")+"\\"+"signtointe"+".txt"), filestr, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return ret;
	}
	
	/**
	 * 获取写入文件的字符串
	 */
	public static String getFilestr(PriorityBlockingQueue<Node> queue,Node head,String call_command) {
		String filestr="";
		
		if(CallCommand.call_update_waiting_list.equals(call_command)) {
			filestr=StrKit.getRandomUUID()+LINE_SEPARATOR+call_command+LINE_SEPARATOR;//Message ID + Message Type
			filestr=filestr+head.getDoctor()+LINE_SEPARATOR+LINE_SEPARATOR;//doctor and picture
			filestr=filestr+head.getSn()+"="+head.getPatientname()+LINE_SEPARATOR;//patient to operate	
		}else if(CallCommand.user_login.equals(call_command)) {
			filestr=StrKit.getRandomUUID()+LINE_SEPARATOR+CallCommand.call_update_waiting_list+LINE_SEPARATOR;//Message ID + Message Type
			filestr=filestr+head.getDoctor()+LINE_SEPARATOR+LINE_SEPARATOR;//doctor and picture
			filestr=filestr+LINE_SEPARATOR;//patient to operate
		}else {
			filestr=StrKit.getRandomUUID()+LINE_SEPARATOR+call_command+LINE_SEPARATOR;//Message ID + Message Type
			filestr=filestr+LINE_SEPARATOR+LINE_SEPARATOR;//doctor and picture
			filestr=filestr+LINE_SEPARATOR;//patient to operate
		}
		
		
		if(queue!=null) {
			List<Node> list=new ArrayList<Node>();

			Iterator<Node> iterator = queue.iterator();
//			if(isPeek) {
//				iterator.next();
//			}
			while (iterator.hasNext()) {
	        	list.add(iterator.next());
	        }
			list.sort(queue.comparator());
			for(Node e:list) {
				filestr=filestr.concat(e.getSn()).concat("=").concat(e.getPatientname()).concat(";");
			}
		}else {
			filestr=filestr+LINE_SEPARATOR;//patient waiting
		}
		//+PropKit.use("system.properties").get("call_tips")
		if(head.getDicMessage() !=null) {
			filestr=filestr+LINE_SEPARATOR + head.getDicMessage();//Tips
		}else {
			filestr=filestr+LINE_SEPARATOR + "";//Tips
		}
		
		return filestr;
	}
	
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		
//		Node node =new Node();
//		node.setDoctor("张珊");
//		node.setLocation("CT室1");
//		node.setModality("CT");
//		node.setModalityid(14);
//		node.setModalityname("西门子CT");
//		node.setPatientname("李四");
//		node.setSn("1");
//		node.setStudyid("0000001");
//		
//		QueuingUpService.offer(14, node);

	}
	
	
}
