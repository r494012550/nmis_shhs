package com.healta.plugin.queueup;

import java.io.Serializable;

import com.healta.plugin.activemq.BaseJmsOrder;

public class QueueupSendOrder extends BaseJmsOrder implements Serializable {

	private static final long serialVersionUID = -926576382671082069L;
	private Node node;
	private Enum method;
	public QueueupSendOrder(Node node,Enum method) {

		if(node==null){
			throw new RuntimeException("The node can not be null!");
		}
		if(node.getModalityid()==null){
			throw new RuntimeException("The modalityid can not be null!");
		}	
		this.node=node;
		this.method=method;
	}
//	public QueueupSendOrder() {
//		this.method=QueueMethod.Init;
//	}

    public Node getNode() {
		return node;
	}

	public void setMethod(Enum method) {
		this.method = method;
	}

	public Enum getMethod() {
		return method;
	}
    
    
}
