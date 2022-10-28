package com.healta.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.healta.model.SscSaleOrder;
import com.healta.model.User;
import com.healta.service.PurchaseService;
import com.healta.service.SaleService;
import com.healta.service.StockService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;

public class StockController extends Controller{
	private static final Logger log = Logger.getLogger(StockController.class);
	private static final StockService sv=new StockService();
	private static final PurchaseService ps=new PurchaseService();
	private static final SaleService ss=new SaleService();
	public void index() {
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
		setAttr("name", user.getStr("name"));
		setAttr("user_id", user.getId());
		renderJsp("/view/admin/dic/stock.jsp");
	}
	/**
	 * 查询所有待审核订单
	 */
	public void findOrder() {
		JSONArray jsonarray=sv.findOrder();
		renderJson(jsonarray.toString());
	}
	
	/**
	 * 审核订单
	 */
	public void approvalOrder() {
		String data =getPara("data");
		Integer id =getParaToInt("id");
		String type =getPara("type");
		User user=(User) getSession().getAttribute("user");
		try{
			if(sv.approvalOrder(data,user,type)) {
				//审批通过后修改订单状态
				if("1".equals(type))
					ps.submitSscPurchase(id, "2");
				if("2".equals(type))
					ss.submitSscSaleOrder1(id, "2");
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
