package com.healta.util;

import org.apache.commons.lang.math.RandomUtils;

import com.healta.model.Id1;
import com.healta.model.Id2;
import com.healta.model.Id3;
import com.healta.model.ResearchId1;
import com.healta.model.Syscode;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class IDUtil {
	
	public static int DEFAULT_LEN=8;
	
	public static String STR="0000000000000000000000";
	
	public static String getPatientID(){
		String prefix=PropKit.use("system.properties").get("patientidprefix");
		int len=PropKit.use("system.properties").getInt("patientidlen",DEFAULT_LEN);
		Id1 id = new Id1();
		id.set("a", "a").save();
		int n = id.getId();
		id.delete();
		return String.format("%s%0"+len+"d",prefix!=null?prefix:"", n);
	}
	
	public static String getPatientID_Research(){
		String prefix=PropKit.use("system.properties").get("res_patientidprefix");
		int len=PropKit.use("system.properties").getInt("res_patientidlen",DEFAULT_LEN);
		ResearchId1 id = new ResearchId1();
		id.set("a", "a").save();
		int n = id.getId();
		id.delete();
		return String.format("%s%0"+len+"d",prefix!=null?prefix:"", n);
	}
	
	/**
	  *  按规则生成studyid
	 * @param modality
	 * @param deptCode
	 * @return
	 */
	public static String getStudyID(String modality, String deptCode) {
		// studyid 的长度
		int len = PropKit.use("system.properties").getInt("studyidlen",DEFAULT_LEN);
		Record id = new Record();
		id.set("a", "a");
		String tablename = "id_" + modality;
		Db.save(tablename, id);
		int n = id.getInt("id");
		Db.delete(tablename, id);
		return (PropKit.use("system.properties").getInt("generator_department_used") == 1 ? (deptCode == null ? "" : deptCode) : "")
				+(PropKit.use("system.properties").getInt("generator_studytype_used") == 1 ? modality:"")+String.format("%0"+len+"d", n);
	}
	
	public static String getAdmissionID() {
		String prefix=PropKit.use("system.properties").get("admissionidprefix");
		int len = PropKit.use("system.properties").getInt("admissionidlen",DEFAULT_LEN);
		Id3 id = new Id3();
		id.set("a", "a").save();
		int n = id.getId();
		id.delete();
		return String.format("%s%0"+len+"d",prefix!=null?prefix:"",n);
	}
	
	public static String getStudyInsUid(String modality) {
		int id=RandomUtils.nextInt(100);
		Syscode code=SyscodeKit.INSTANCE.getSysCode("0004", modality);
		if(code!=null) {
			id=code.getId();
		}
		return PropKit.use("system.properties").get("implentationclassroot")+"."+id+"."+
			System.currentTimeMillis()+RandomUtils.nextInt(10000);
	}
	
}
