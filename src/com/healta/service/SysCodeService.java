package com.healta.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.healta.model.Syscode;
import com.healta.util.SyscodeKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;


public class SysCodeService {
	public List<Record> findSyscode(String value){
		List<Record> ret=new ArrayList<Record>();
		Map<Integer,String> group=new HashMap<Integer,String>();
		
		String where="";
		List<Object> list=new ArrayList<Object>();
		if(StringUtils.isNotBlank(value)){
			where += " and name_zh like CONCAT('%',?,'%') or parent=0";
			list.add(value);
		}
		String sql="select * from syscode where 0=0"+where+" order by id asc";
		
		/*String sql="select * from syscode";
		if(StringUtils.isNotEmpty(value)){
			sql+=" where name_zh like '%"+value+"%' or parent=0" ;
		}
		
		sql+=" order by id asc";*/
		List<Syscode> Syscodes= Syscode.dao.find(sql,list.toArray());
		for(Syscode sc:Syscodes) {
			Record record=new Record();
			int parent=sc.getInt("parent");
			if(parent==0) {
				group.put(sc.getId(), sc.get("name_zh"));
			}
			else {
				record.set("id", sc.get("id"));
				record.set("code", sc.get("code"));
				record.set("name_zh", sc.get("name_zh"));
				record.set("name_en", sc.get("name_en"));
				record.set("type", sc.get("type"));
				record.set("parent", parent);
				record.set("group", group.get(parent));
				ret.add(record);
			}
			
		}
		return ret;
	}
	
	public List<Record> findSyscodeGroup(){
		List<Record> ret=new ArrayList<Record>();
		String sql="select * from syscode where parent=0";
		List<Syscode> Syscodes= Syscode.dao.find(sql);
		for(Syscode sc:Syscodes) {
			Record record=new Record();
			record.set("id", sc.get("id"));
			record.set("code", sc.get("code"));
			record.set("name_zh", sc.get("name_zh"));
			record.set("name_en", sc.get("name_en"));
			record.set("type", sc.get("type"));
			record.set("parent", 0);
			ret.add(record);
		}
		return ret;
	}
	
	public boolean saveSysCode(Syscode sc,int parent){
		boolean ret=false;
		Syscode Syscodes= Syscode.dao.findById(parent);
		sc.set("type", Syscodes.get("type"));
		if(sc.getId()!=null){
			ret=sc.update();
		}
		else{
			ret=sc.remove("id").save();
		}
		if(ret){
			CacheKit.removeAll("syscodeCache");
		}
		return ret;
	}

	public boolean saveSysCodeGroup(Syscode sc) {
		// TODO Auto-generated method stub
		boolean ret=false;
		int a=0;
		String sql="select * from syscode where parent=0";
		List<Syscode> Syscodes= Syscode.dao.find(sql);
		for(Syscode sys:Syscodes) {
			a=a>Integer.parseInt(sys.get("type"))?a:Integer.parseInt(sys.get("type"));	
		}
		sc.set("type",new DecimalFormat("0000").format(a+1));
		if(sc.getId()!=null){
			ret=sc.update();
		}
		else{
			ret=sc.remove("id").save();
		}
		if(ret){
			CacheKit.removeAll("syscodeCache");
		}
		return ret;
	}
	
	public String getCodeDisplay(String type,String code){
		HashMap<String ,List<Syscode>> map = SyscodeKit.INSTANCE.loadSyscodeFromCache();
		
		if(map!=null){
			List<Syscode> retcodes=map.get(type);
			if(retcodes!=null){
				for(Syscode co:retcodes){
					if(code.equals(co.getCode())){
						code=co.getNameZh();
					}
				}
			}
		}
		
		return code;
	}

}
