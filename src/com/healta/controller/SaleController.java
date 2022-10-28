package com.healta.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import com.healta.model.User;
import com.healta.service.SaleService;
import com.healta.service.StockService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;

public class SaleController extends  Controller{
	private static final StockService sk=new StockService();
	private static final SaleService sv=new SaleService();
	
	public void index() {
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
		setAttr("name", user.getStr("name"));
		setAttr("user_id", user.getId());
		renderJsp("/view/admin/dic/ssc_sale.jsp");
	}
	/**
	 * 查询订单树
	 */
	public void findOrdertree() {
		JSONArray jsonarray=sv.findOrdertree();
		renderJson(jsonarray.toString());
	}
	
	
	/**
	 * 查询商品库存
	 */
	public void findGoodsStock() {
		String goods_id=getPara("comGoodsId");
		String goods_name=getPara("comGoodsName");
		HashMap<String,String> map=new HashMap<String,String>();
		map.put("goods_id", goods_id);
		map.put("goods_name", goods_name);
		try{
			renderJson(sk.findGoodsStock(map));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
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
	 * 查询销售订单主表
	 * @param id
	 */
	public void findSscSaleOrder() {
		String id =getPara("id");
		try{
			renderJson(sv.findSscSaleOrder(id));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 查询销售订单明细
	 * 
	 */
	public void findSscSaleOrderLines() {
		String id =getPara("id");
		try{
			renderJson(sv.findSscSaleOrderLines(id));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	/**
	 * 提交销售订单
	 */
	public void submitSscSaleOrder() {
		Integer id =getParaToInt("id");
		String status =getPara("status");
		try{
			if(sv.submitSscSaleOrder(id,status)) {
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
	public void deleteSscSaleOrder() {
		Integer id =getParaToInt("id");
		try{
			if(sv.deleteSscSaleOrder(id)) {
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
