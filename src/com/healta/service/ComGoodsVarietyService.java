package com.healta.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.healta.model.ComGoodsVariety;
import com.healta.util.ResultUtil;
import com.healta.vo.ResultVO;
import com.jfinal.kit.StrKit;

public class ComGoodsVarietyService {
	private static final Logger log = Logger.getLogger(ComGoodsVarietyService.class);
	private static final ComGoodsVariety sdo=new ComGoodsVariety();
	
	public List<ComGoodsVariety> findComGoodsVariety(String str){
		return sdo.findComGoodsVariety(str);
	}

	public boolean deleteComGoodsVariety(Integer id) {
		return sdo.deleteComGoodsVariety(id);
	}
	
	//校验品种编码是否存在
	public boolean checkCode(ComGoodsVariety comgoodsvariety) {
		List<ComGoodsVariety> list=sdo.checkCode(comgoodsvariety.getComGoodsVarietyCode());
		
		//根据ID判断是新增还是修改，修改的话需要排除自己
		if(StrKit.notBlank(String.valueOf(comgoodsvariety.getId()))) {
			if(list.size()>0) {
				ComGoodsVariety obj=list.get(0); //品种code是唯一的，所以只有一条记录
				if(obj.getId()!=comgoodsvariety.getId()){
					return true;
				}
			}
		}else {
			if(list.size()>0) {
				return true;
			}
		}
		return false;
	}
}
