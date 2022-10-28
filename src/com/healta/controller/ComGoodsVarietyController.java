package com.healta.controller;

import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.healta.model.ComGoodsVariety;
import com.healta.model.Studyitem;
import com.healta.service.ComGoodsVarietyService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.jfinal.kit.StrKit;

public class ComGoodsVarietyController extends Controller{
	private static final Logger log = Logger.getLogger(ComGoodsVarietyController.class);
	
	private static final ComGoodsVarietyService sv=new ComGoodsVarietyService();
	/**
	   * 保存商品品种
	  */
	public void saveComGoodsVariety() {
		String str =getPara("str");
		com.alibaba.fastjson.JSONArray items=JSON.parseArray(str);
		for (int i = 0; i < items.size(); i++) {
			com.alibaba.fastjson.JSONObject item=items.getJSONObject(i);
			ComGoodsVariety cgv=new ComGoodsVariety();
			cgv.setComGoodsVarietyCode(item.getString("com_goods_variety_code"));
			cgv.setComGoodsVarietyName(item.getString("com_goods_variety_name"));
			//校验编码是否唯一
			 if(sv.checkCode(cgv)){
				 renderJson(ResultUtil.fail(1,"品种编码:"+item.getString("com_goods_variety_code")+"已存在，请重新输入!!"));
				 return;
			 }
			
			//根据ID判断是执行修改还是新增
			if(StrKit.notBlank(item.getString("id"))) {
				cgv.setId(item.getInteger("id"));
				cgv.update();
			}else {
				cgv.remove("id").save();
			}
		}
		renderJson(ResultUtil.success());
	}
	
	/**
	   * 查询商品品种信息
	  */
	public void findComGoodsVariety() {
		try{
			
			renderJson(sv.findComGoodsVariety(getPara("value")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	/**
	 * 删除商品品种
	 */
	public void deleteComGoodsVariety() {
		 try {
			 if(sv.deleteComGoodsVariety(getParaToInt("id"))) {
				 renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}		 
		}catch(Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	}
}
