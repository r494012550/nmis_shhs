package com.healta.constant;

/**
 *  ris 系统的确费状态 
 *  (ris系统登记时，发送确费消息，his将无法更改)
 * @author admin
 */
public class ChargeStatus {
	
	/**
	 *  未确费 （登记成功后状态）
	 */
	public final static String NO_CONFIRM = "0";
	
	/**
	 *  确费成功 （给his发送确费消息，并返回AA接收成功的消息）
	 */
	public final static String CONFIRM = "1";
	
	/**
	 *  取消确费 （ris 系统取消检查）
	 */
	public final static String CANCEL = "2";

	/**
	 *  确费失败 （ris 系统确费失败）
	 */
	public final static String FAILURE = "3";
}
