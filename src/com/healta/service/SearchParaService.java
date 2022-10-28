package com.healta.service;

import java.util.ArrayList;
import java.util.List;

import com.healta.model.Quicksearch;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class SearchParaService{
	
	
	public static List<Quicksearch> getLatestContent(Integer patientkey,Integer studyorderkey){
		String sql="select p.id as patientkey,p.patientid,p.patientname,p.py,p.telephone,p.idnumber,a.id as admissionkey,a.cardno,a.inno,a.outno,"
				+ "s.id as studyorderkey,s.studyid from patient p,admission a,studyorder s where p.id=a.patientidfk and a.id=s.admissionidfk ";
		
		List<Record> list=null;
		if(studyorderkey!=null) {
			list=Db.find(sql+" and s.id=?",studyorderkey);
		}
		else if(patientkey!=null) {
			list=Db.find(sql+" and p.id=?",patientkey);
		}
		List<Quicksearch> ret=new ArrayList<Quicksearch>();
		if(list!=null) {
			list.stream().forEach(x->{
				ret.add(Quicksearch.dao.createFromRecord(x));
			});
		}
		
		return ret;
	}
	
//	public static Quicksearch queryByStudyOrderKey(Integer studyOrderKey) {
//		return Quicksearch.dao.findFirst("select * from search_para where studyorderkey = " + studyOrderKey);
//	}
//	
//	public static List<Quicksearch> queryByPatientKey(Integer patientKey){
//		return Quicksearch.dao.find("select * from search_para where patientkey = " + patientKey);
//	}
	
	public static boolean delByPatientkey(Integer patientkey) {
		return Db.delete("delete from quicksearch where patientkey = " + patientkey) > 0 ? true : false;
	}
	
//	public static boolean delByPatientkey(Integer patientkey1, Integer patientkey2) {
//		return Db.delete("delete from quicksearch where patentkey = " + patientkey1 + " or patentkey = " + patientkey2) > 0 ? true : false;
//	}
	
	public static boolean delByStudyorderkey(Integer studyorderkey) {
		return Db.delete("delete from quicksearch where studyorderkey = " + studyorderkey) > 0 ? true : false;
	}
	
//	public static List<Record> queryAllByPatientFK(Integer patientfk){
//		return Db.find("SELECT p.patientid, s.studyid, a.outno, a.inno, a.admissionid, p.patientname, a.cardno, p.idnumber, s.patientidfk, s.admissionidfk, s.id FROM studyorder s LEFT JOIN patient p ON p.id = s.patientidfk LEFT JOIN admission a ON s.admissionidfk = a.id  WHERE p.id = " + patientfk);
//	}
//	
//	public static Quicksearch getSearchPara(Integer patientkey, Integer studyorderkey, Integer admissionkey, Integer reportkey, String content) {
//		Quicksearch searchPara = new Quicksearch();
//		searchPara.setPatientkey(patientkey);
//		searchPara.setStudyorderkey(studyorderkey);
//		searchPara.setAdmissionkey(admissionkey);
//		searchPara.setReportkey(reportkey);
//		searchPara.setContent(content);
//		return searchPara; 
//	}
}
