package com.healta.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.healta.model.PurchaseOrder;
import com.healta.model.PurchaseOrderLines;
import com.healta.model.User;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class PurchaseService {
	private static final Logger log = Logger.getLogger(PurchaseService.class);
	private static final PurchaseOrder sd=new PurchaseOrder();
	private static long orderNum;
	private static String orderType = "P";
	
	public boolean save(String data, String dataLines, User user) {
		boolean flag=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
				try {
					com.alibaba.fastjson.JSONObject item=JSON.parseObject(data);
					com.alibaba.fastjson.JSONArray items=JSON.parseArray(dataLines);
					PurchaseOrder purchase=new PurchaseOrder();
					if(StrKit.notBlank(item.getString("purchase_no"))) {
						purchase.setPurchaseNo(item.getString("purchase_no"));
					}else {
						purchase.setPurchaseNo(getOrderNo()); 
					}
					purchase.setOrderStartDate(item.getString("order_start_date"));
					purchase.setOrderEndDate(item.getString("order_end_date"));
					purchase.setCreator(item.getString("creator"));
					purchase.setContacts(item.getString("contacts"));
					purchase.setContactsNumber(item.getInteger("contacts_number"));
					purchase.setVender(item.getString("vender"));
					purchase.setVenderAddress(item.getString("vender_address"));
					purchase.setReceiviGoodsAddress(item.getString("receivi_goods_address"));
					purchase.setNote(item.getString("note"));
					purchase.setCreatedBy(user.getId());
					purchase.setModifyBy(user.getId());
					if(StrKit.notBlank(item.getString("id"))) {
						purchase.setId(item.getInteger("id"));
						purchase.update();
					}else {
						purchase.remove("id").save();
					}
					for (int i = 0; i < items.size(); i++) {
						com.alibaba.fastjson.JSONObject object=items.getJSONObject(i);
						PurchaseOrderLines purchaseorderlines=new PurchaseOrderLines();
						purchaseorderlines.setPurchaseId(purchase.getId());
						purchaseorderlines.setGoodsId(object.getInteger("goods_id"));
						purchaseorderlines.setGoodsName(object.getString("goods_name"));
						purchaseorderlines.setComLot(object.getString("com_lot"));
						purchaseorderlines.setEfficiencyDate(object.getString("efficiency_date"));
						purchaseorderlines.setExpireDate(object.getString("expire_date"));
						purchaseorderlines.setCalibrationDose(object.getString("calibration_dose"));
						purchaseorderlines.setCapacity(object.getString("capacity"));
						purchaseorderlines.setPurQty(object.getInteger("pur_qty"));
						if(StrKit.notBlank(object.getString("id"))) {
							purchaseorderlines.setId(object.getInteger("id"));
							purchaseorderlines.update();
						}else {
							purchaseorderlines.remove("id").save();
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
			obj.put("text", record.get("purchase_no"));
			obj.put("state", "undefined");
			obj.put("iconCls", "icon-fo");
			jsonobjectList.add(obj);
		}
		return jsonobjectList;
	}
	public String statusConversion(String status) {
		if("1".equals(status)) {
			return "新增";
		}else if("2".equals(status)) {
			return "已提交";
		}else if("3".equals(status)) {
			return "已完成";
		}else {
			return status;
		}
	}
	public Record findSscPurchase(String id) {
		return sd.findSscPurchase(id);
	}
	public List<Record> findSscPurchaseLines(String id) {
		return sd.findSscPurchaseLines(id);
	}
	public boolean submitSscPurchase(Integer id, String status) {
		PurchaseOrder purchase=new PurchaseOrder();
		purchase.setId(id);
		if("1".equals(status)) {
			purchase.setStatus("2");
		}else if("2".equals(status)){
			purchase.setStatus("3");
		}
		return purchase.update();
	}
	public boolean deleteSscPurchase(Integer id) {
		return sd.deleteSscPurchase(id);
	}
}
