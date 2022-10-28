package com.healta.plugin.activemq;

import java.io.Serializable;

public class ESIndexDocOrder extends BaseJmsOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2376540643160366086L;
	private Integer dataid;
	
	public ESIndexDocOrder(Integer dataid){
		this.dataid=dataid;
	}

	public Integer getDataid() {
		return dataid;
	}

	public void setDataid(Integer dataid) {
		this.dataid = dataid;
	}

}
