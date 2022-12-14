package com.healta.model;

import java.util.List;

import com.healta.model.base.BaseComGoodsVariety;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class ComGoodsVariety extends BaseComGoodsVariety<ComGoodsVariety> {
	public static final ComGoodsVariety dao = new ComGoodsVariety().dao();
	
	public List<ComGoodsVariety> findComGoodsVariety(String str){
		StringBuffer sb=new StringBuffer();
		sb.append("select *from com_goods_variety where deleted=0 ");
		if(StrKit.notBlank(str)) {
			sb.append(" and (com_goods_variety_code LIKE '%"+str+"%' or com_goods_variety_name like '%"+str+"%')");
		}
		return dao.find(sb.toString());
	}

	public boolean deleteComGoodsVariety(Integer id) {
		StringBuffer sb=new StringBuffer();
		sb.append("update com_goods_variety set deleted=1 where id="+id);
		int i=Db.update(sb.toString());
		if(i==1) {
			return true;
		}
		return false;
	}

	public List<ComGoodsVariety> checkCode(String code) {
		StringBuffer sb =new StringBuffer();
		sb.append("select * from com_goods_variety where deleted=0 and com_goods_variety_code='"+code+"'");
		return dao.find(sb.toString());
	}
}
