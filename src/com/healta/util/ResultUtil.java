package com.healta.util;

import com.healta.vo.ResultVO;

/**
 * JSON结果帮助类
 * @author Administrator
 *
 */
public class ResultUtil {
	
	private final static Integer _SUCCESS =0;
	private final static Integer _FAIL=-1;
	
	
	/**
	 * 携带数据的成功响应
	 * @param data
	 * @return
	 */
	public static ResultVO success(Object data) {
		ResultVO vo = new ResultVO();
		vo.setCode(_SUCCESS);
		vo.setMessage("请求成功");
		vo.setData(data);
		return vo;
	}
	
	/**
	 * 不携带数据的成功响应
	 * @return
	 */
	public static ResultVO success() {
		return success("");
	}
	
	/**
	 * 带参数请求失败的响应
	 * @param code
	 * @param message
	 * @return
	 */
	public static ResultVO fail(Integer code, String message, Object data) {
		ResultVO vo = new ResultVO();
		vo.setCode(code);
		vo.setMessage(message);
		vo.setData(data);
		return vo;
	}
	
	/**
	 * 请求失败的响应
	 * @param code
	 * @param message
	 * @return
	 */
	public static ResultVO fail(Integer code, String message) {
		ResultVO vo = new ResultVO();
		vo.setCode(code);
		vo.setMessage(message);
		vo.setData("");
		return vo;
	}
	
	/**
	 * 请求失败的响应
	 * @param code
	 * @param message
	 * @return
	 */
	public static ResultVO fail(String message) {
		ResultVO vo = new ResultVO();
		vo.setCode(_FAIL);
		vo.setMessage(message);
		vo.setData("");
		return vo;
	}
}
