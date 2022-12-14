package com.healta.model;

import com.healta.model.base.BaseSyngoviareport;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Syngoviareport extends BaseSyngoviareport<Syngoviareport> {
	public static final Syngoviareport dao = new Syngoviareport().dao();
	
	
	public Syngoviareport getByStudyid(String studyid){
		return dao.findFirst("select * from syngoviareport where studyid=? and delflag=0",studyid);
	}
	
	public Syngoviareport getByStudyInsUid(String studyinsuid){
		return dao.findFirst("select * from syngoviareport where studyinsuid=? and delflag=0",studyinsuid);
	}
}
