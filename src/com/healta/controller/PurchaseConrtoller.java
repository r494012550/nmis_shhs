package com.healta.controller;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.healta.model.User;
import com.healta.service.PurchaseService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;

public class PurchaseConrtoller  extends Controller{
	private static final Logger log = Logger.getLogger(PurchaseConrtoller.class);
	private static final PurchaseService sv=new PurchaseService();
	public void index() {
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
		setAttr("name", user.getStr("name"));
		setAttr("user_id", user.getId());
		renderJsp("/view/admin/dic/ssc_purchase.jsp");
	}
	
	/**  保存订单信息
	 * @param data  主表信息
	 * @param dataLines  明细表信息
	 * @return true 成功 false 失败
	 */
	public void save() {
		String data =getPara("data");
		String dataLines =getPara("dataLines");
		User user=(User) getSession().getAttribute("user");
		if(sv.save(data,dataLines,user)) {
			renderJson(ResultUtil.success());
		}else {
			renderJson(ResultUtil.fail(""));
		}
	}
	
	/**
	 * 查询订单树
	 */
	public void findOrdertree() {
		JSONArray jsonarray=sv.findOrdertree();
		renderJson(jsonarray.toString());
	}
	/**
	 * 查询采购订单主表
	 * @param id
	 */
	public void findSscPurchase() {
		String id =getPara("id");
		try{
			renderJson(sv.findSscPurchase(id));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 查询采购订单明细
	 * 
	 */
	public void findSscPurchaseLines() {
		String id =getPara("id");
		try{
			renderJson(sv.findSscPurchaseLines(id));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	/**
	 * 提交订单
	 */
	public void submitSscPurchase() {
		Integer id =getParaToInt("id");
		String status =getPara("status");
		try{
			if(sv.submitSscPurchase(id,status)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	/**
	 * 删除订单
	 */
	public void deleteSscPurchase() {
		Integer id =getParaToInt("id");
		try{
			if(sv.deleteSscPurchase(id)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
}
