package com.healta.plugin.activemq;

import java.io.Serializable;

import com.healta.model.Studyprocess;
import com.jfinal.plugin.activerecord.Db;

public class StudyprocessOrder extends BaseJmsOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8324671020481760713L;

	private Studyprocess sp;
	
	private int port;
	
	private String serverurl;
	
	public StudyprocessOrder(Studyprocess sp){
		this.sp=sp;
	}
	
	public StudyprocessOrder(Studyprocess sp,int port,String serverurl){
		this.sp=sp;
		this.port=port;
		this.serverurl=serverurl;
	}

	public Studyprocess getSp() {
		return sp;
	}

	public void setSp(Studyprocess sp) {
		this.sp = sp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServerurl() {
		return serverurl;
	}

	public void setServerurl(String serverurl) {
		this.serverurl = serverurl;
	}
	
}
