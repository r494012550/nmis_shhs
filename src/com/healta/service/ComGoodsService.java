package com.healta.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.healta.controller.ComGoodsController;
import com.healta.model.ComGoods;
import com.healta.model.User;

import oracle.net.ns.SessionAtts;

public class ComGoodsService {
	private static final Logger log = Logger.getLogger(ComGoodsService.class);
	private static final ComGoods sdo=new ComGoods();
	/**
	 * 保存商品信息
	 */
	public boolean saveComGoods(ComGoods comgoods) {
		return sdo.saveComGoods(comgoods);
	}
	/**
	 * 修改商品信息
	 */
	public boolean updateComGoods(ComGoods comgoods) {
		return sdo.updateComGoods(comgoods);
	}
    /**
      * 查询商品信息
     * @param map
     */
	public List<ComGoods> findComGoods(Map<String, String[]> map) {
		return sdo.findComGoods(map);
	}
	public boolean deleteComGoods(Integer id) {
		return sdo.deleteComGoods(id);
	}
}
