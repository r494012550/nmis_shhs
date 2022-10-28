package com.healta.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.CacheName;
import com.healta.model.ComGoods;
import com.healta.model.DicExamitem;
import com.healta.model.User;
import com.healta.service.ComGoodsService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public class ComGoodsController extends Controller{
	private static final Logger log = Logger.getLogger(ComGoodsController.class);
	private static final ComGoodsService sv=new ComGoodsService();
	
	public void editComGoods() {
		if(!StringUtils.isBlank(getPara("id"))){
			int id=getParaToInt("id");
			ComGoods item=ComGoods.dao.findById(id);
			setAttr("item", item);
		}
		renderJsp("/view/admin/dic/editCom_Goods.jsp");
	}
	/**
	 * 保存商品信息
	 */
	public void saveComGoods() {
		try{
			ComGoods comgoods=getModel(ComGoods.class, "", true);
			User user=(User) getSession().getAttribute("user");
			comgoods.set("created_by", user.getId());
			comgoods.set("last_updated_by", user.getId());
			if(sv.saveComGoods(comgoods)){
				renderJson(ResultUtil.success());
				 //新增或修改数据后，清空缓存
				 CacheKit.remove(CacheName.DICCACHE,CacheName.DATADIC_COM_GOODS_KEY);
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
		
	}
	/**
	 * 修改商品信息
	 */
	public void updateComGoods() {
		try{
			ComGoods comgoods=getModel(ComGoods.class, "", true);
			User user=(User) getSession().getAttribute("user");
			comgoods.set("last_updated_by", user.getId());
			comgoods.setModifytime(new Date());
			if(sv.updateComGoods(comgoods)){
				renderJson(ResultUtil.success());
				CacheKit.remove(CacheName.DICCACHE,CacheName.DATADIC_COM_GOODS_KEY);
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
		
	}
	
	/**
	   * 查询商品信息
	  */
	public void findComGoods() {
		try{
			//getParaMap() 获取前段from表单中的所有信息
			renderJson(sv.findComGoods(getParaMap()));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	/**
	 * 删除商品
	 */
	public void deleteComGoods() {
		 try {
			 if(sv.deleteComGoods(getParaToInt("id"))) {
				 renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}		 
		}catch(Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	 }
	
	/**
	 * 从缓存中读取
	 */
	public void getDicComGoodsCache(){
		List<ComGoods> institution = CacheKit.get(CacheName.DICCACHE, CacheName.DATADIC_COM_GOODS_KEY, new IDataLoader(){ 
			public Object load() { 
				return ComGoods.dao.find("select * from com_goods cg where deleted=0"); 
			}
		});
		renderJson(institution);
	}
}
