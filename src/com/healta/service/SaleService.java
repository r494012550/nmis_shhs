package com.healta.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.healta.model.SscSaleOrder;
import com.healta.model.SscSaleOrderLines;
import com.healta.model.Stock;
import com.healta.model.User;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class SaleService {
	private static final Logger log = Logger.getLogger(SaleService.class);
	private static String orderType = "S";
	private static long orderNum;
	private static final SscSaleOrder sd=new SscSaleOrder();
	public static final Stock dao = new Stock().dao();
	
	public boolean save(String data, String dataLines, User user) {
		boolean flag=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
				try {
					com.alibaba.fastjson.JSONObject item=JSON.parseObject(data);
					com.alibaba.fastjson.JSONArray items=JSON.parseArray(dataLines);
					SscSaleOrder sso=new SscSaleOrder();
					if(StrKit.notBlank(item.getString("sale_no"))) {
						sso.setSaleNo(item.getString("sale_no"));
					}else {
						sso.setSaleNo(getOrderNo()); 
					}
					sso.setOrderStartDate(item.getString("order_start_date"));
					sso.setOrderEndDate(item.getString("order_end_date"));
					sso.setCreator(item.getString("creator"));
					sso.setContacts(item.getString("contacts"));
					sso.setContactsNumber(item.getInteger("contacts_number"));
					sso.setClient(item.getString("client"));
					sso.setClientAddress(item.getString("client_address"));
					sso.setNote(item.getString("note"));
					sso.setCreatedBy(user.getId());
					sso.setModifyBy(user.getId());
					if(StrKit.notBlank(item.getString("id"))) {
						sso.setId(item.getInteger("id"));
						sso.update();
					}else {
						sso.remove("id").save();
					}
					for (int i = 0; i < items.size(); i++) {
						com.alibaba.fastjson.JSONObject object=items.getJSONObject(i);
						SscSaleOrderLines ssol=new SscSaleOrderLines();
						ssol.setSscSaleOrderId(sso.getId());
						ssol.setGoodsId(object.getInteger("goods_id"));
						ssol.setGoodsName(object.getString("goods_name"));
						ssol.setComLot(object.getString("com_lot"));
						ssol.setEfficiencyDate(object.getString("efficiency_date"));
						ssol.setExpireDate(object.getString("expire_date"));
						ssol.setCalibrationDose(object.getString("calibration_dose"));
						ssol.setCapacity(object.getString("capacity"));
						ssol.setSaleQty(object.getInteger("sale_qty"));
						if(StrKit.notBlank(object.getString("id"))) {
							ssol.setId(object.getInteger("id"));
							ssol.update();
						}else {
							ssol.remove("id").save();
						}
					}
					return true;
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
					return false;
				}
			}
		});
		return flag;
	}
	/** 
     * 生成订单编号 
     * @return 
     */  
    public static synchronized String getOrderNo() {  
        String str = new SimpleDateFormat("yyyyMMdd").format(new Date());  
        Record Record=sd.getNewNum();
        orderNum=Record.getLong("num")+1;
        long orderNo = Long.parseLong((str)) * 10000;  
        orderNo += orderNum;;  
        return orderType+orderNo+"";  
    }
	public JSONArray findOrdertree() {
		JSONObject root =null;
		JSONArray arr = new JSONArray();
		root = new JSONObject();
		List<JSONObject> list= getNodes("1");
		root.put("id", "");
		root.put("text", "新增");
		root.put("state", "open");
		root.put("iconCls", "icon-fo");
		//判断是否有子节点
		if(list!=null&&list.size()>0) {
			root.put("children", list); //只查询状态为新增的采购单
		}
		arr.put(root);
	return arr;
}  

	private List<JSONObject> getNodes(String status) {
		List<JSONObject> jsonobjectList=new ArrayList<JSONObject>();
		List<Record> recordList=sd.getNodes(status);
		for (int i = 0; i < recordList.size(); i++) {
			Record record=recordList.get(i);
			JSONObject obj = new JSONObject();
			obj.put("id", record.get("id"));
			obj.put("text", record.get("sale_no"));
			obj.put("state", "undefined");
			obj.put("iconCls", "icon-fo");
			jsonobjectList.add(obj);
		}
		return jsonobjectList;
	}
	public Record findSscSaleOrder(String id) {
		return sd.findSscSaleOrder(id);
	}
	public List<Record> findSscSaleOrderLines(String id) {
		return sd.findSscSaleOrderLines(id);
	}
	public boolean submitSscSaleOrder(Integer id, String status) {
		boolean flag=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
				try {
					SscSaleOrder sso=new SscSaleOrder();
					HashMap<String,String> map =new HashMap<String,String>();
					sso.setId(id);
					if("1".equals(status)) {
						sso.setStatus("2");
					}else if("2".equals(status)){
						sso.setStatus("3");
					}
					sso.update();
					
					List<Record> list=sd.findSscSaleOrderLines(String.valueOf(id));
					for (int i = 0; i < list.size(); i++) {
						Record record=list.get(i);
						map.clear();
						map.put("goods_id",record.getStr("goods_id"));
						map.put("com_lot", record.getStr("com_lot"));
						map.put("efficiency_date", record.getStr("efficiency_date"));
						map.put("expire_date", record.getStr("expire_date"));
						List<Stock> listStock=dao.findStock(map);
						if(listStock.size()==0||listStock.size()>1) {
							return false;
						}
						Stock stock=listStock.get(0);
						Integer available=stock.getOnavailable()-record.getInt("sale_qty");
						Integer send;
						if(stock.getOnsend()==null) {
							send=record.getInt("sale_qty");
						}else {
							send=stock.getOnsend()+record.getInt("sale_qty");
						}
						stock.setOnavailable(available);
						stock.setOnsend(send);
						stock.update();
					}
					return true;
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
					return false;
				}
			}
		});
		return flag;
		
		
	}
	public boolean deleteSscSaleOrder(Integer id) {
		return sd.deleteSscSaleOrder(id);
	}
	public boolean submitSscSaleOrder1(Integer id, String status) {
		SscSaleOrder sso=new SscSaleOrder();
		sso.setId(id);
		if("1".equals(status)) {
			sso.setStatus("2");
		}else if("2".equals(status)){
			sso.setStatus("3");
		}
		return sso.update();
	}
}
