package com.healta.vo;

import com.alibaba.fastjson.JSON;

/**
 * JSON结果格式封装类
 * @author Administrator
 *
 */
public class ResultVO {
	/**
	 * 返回码 非0 为请求失败
	 */
	private Integer code;
	/**
	 * 返回请求结果信息 
	 */
	private String message;
	/**
	 * 返回请求响应的数据
	 */
	private Object data;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public String toJsonStr(){
		return JSON.toJSONString(this);
	}
}
