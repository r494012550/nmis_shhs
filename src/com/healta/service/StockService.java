package com.healta.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.json.JsonObject;

import org.json.JSONArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.healta.model.PurchaseOrder;
import com.healta.model.SscSaleOrder;
import com.healta.model.Stock;
import com.healta.model.User;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class StockService {
	private static final String[] STR_LIST ={ "采购", "销售"};
	private static final PurchaseOrder po=new PurchaseOrder();
	private static final SscSaleOrder sso=new SscSaleOrder();
	private static final Stock sd=new Stock();
	
	public JSONArray findOrder() {
		JSONObject root =null;
		JSONArray arr = new JSONArray();
		for (int i = 0; i < STR_LIST.length; i++) {
			List<JSONObject> list= getNodes("2",STR_LIST[i]);
			root = new JSONObject();
			root.put("id", i);
			root.put("text", STR_LIST[i]);
			root.put("state", "open");
			root.put("iconCls", "icon-fo");
			root.put("isPrent", "true");
			//判断是否有子节点
			if(list!=null&&list.size()>0) {
				root.put("children",list); 
			}
			arr.put(root);
		}
		return arr;
	}
	
	private List<JSONObject> getNodes(String status,String str) {
		List<JSONObject> jsonobjectList=new ArrayList<JSONObject>();
		List<Record> recordList = new ArrayList<>();
		recordList.clear();
		if("采购".equals(str)) {
			recordList=po.getNodes(status);
		}
		if("销售".equals(str)) {
			recordList=sso.getNodes(status);
		}
		if(recordList!=null) {
			for (int i = 0; i < recordList.size(); i++) {
				Record record=recordList.get(i);
				JSONObject obj = new JSONObject();
				obj.put("id", record.get("id"));
				if("采购".equals(str)) {
					obj.put("text", record.get("purchase_no"));
					obj.put("type", "1");
				}
				if("销售".equals(str)) {
					obj.put("text", record.get("sale_no"));
					obj.put("type", "2");
				}
				obj.put("state", "undefined");
				obj.put("iconCls", "icon-fo");
				obj.put("isPrent", "false");
				jsonobjectList.add(obj);
			}
		}
		return jsonobjectList;
	}

	public boolean approvalOrder(String data, User user, String type) throws Exception{
		com.alibaba.fastjson.JSONArray items=JSON.parseArray(data);
		HashMap<String,String> map=new HashMap<String,String>();
		Stock stock=new Stock();
		for (int i = 0; i < items.size(); i++) {
			com.alibaba.fastjson.JSONObject item=items.getJSONObject(i);
			map.put("goods_id", item.getString("goods_id"));
			map.put("com_lot", item.getString("com_lot"));
			map.put("efficiency_date", item.getString("efficiency_date"));
			map.put("expire_date", item.getString("expire_date"));
			List<Stock> list=sd.findStock(map);
			if(list.size()>1) {
				 throw new Exception();
			}
			if(list.size()==1) {
				stock=list.get(0);
				if("1".equals(type)) {
					stock.setOnhand(stock.getOnhand()+item.getInteger("qty"));
					stock.setOnavailable(stock.getOnavailable()+item.getInteger("qty"));
				}
				if("2".equals(type)) {
					stock.setOnhand(stock.getOnhand()-item.getInteger("qty"));
					stock.setOnsend(stock.getOnsend()-item.getInteger("order_qty"));
					Integer send=item.getInteger("order_qty")-item.getInteger("qty");
					stock.setOnavailable(stock.getOnavailable()+send);
				}
				stock.update();
			}else {
				stock.setGoodsId(item.getInteger("goods_id"));
				stock.setGoodsName(item.getString("goods_name"));
				stock.setComLot(item.getString("com_lot"));
				stock.setEfficiencyDate(item.getString("efficiency_date"));
				stock.setExpireDate(item.getString("expire_date"));
				stock.setOnhand(item.getInteger("qty"));
				stock.setOnavailable(item.getInteger("qty"));
				stock.setCreatedBy(user.getId());
				stock.setModifyBy(user.getId());
				stock.remove("id").save();
			}
		}
		return true;
	}
	/**
	 * 查询商品库存
	 */
	public List<Record> findGoodsStock(Map<String,String> map) {
		return sd.findGoodsStock(map);
	}
}
