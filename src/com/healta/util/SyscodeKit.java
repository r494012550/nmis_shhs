package com.healta.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.healta.model.Syscode;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public enum SyscodeKit {
	
	INSTANCE;
//	public static SyscodeKit getInstance(){
//		return SyscodeKitHolder.instance;
//	}
//	
//	private static class SyscodeKitHolder{
//        private final static SyscodeKit instance=new SyscodeKit();
//    }
	
	public String getCodeDisplayname(Syscode syscode,String locale){
		if(syscode!=null){
			if(StrKit.equals(locale, "zh_CN")||StrKit.equals(locale, "zh")){
				return syscode.getNameZh();
			}
			else{
				return syscode.getNameEn();
			}
		}
		else{
			return "";
		}
	}
	
	public List<Syscode> getCodes(String type){
		HashMap<String ,List<Syscode>> map = loadSyscodeFromCache();
		return map.get(type);
	}
	
	public String getCodeDisplay(String type,String code,String locale){
		if(type==null||code==null){
			return "";
		}
		HashMap<String ,List<Syscode>> map = loadSyscodeFromCache();
		if(map!=null){
			List<Syscode> retcodes=map.get(type);
			if(retcodes!=null){
				for(Syscode co:retcodes){
					if(StrKit.equals(code, co.getCode())){
						if(StrKit.equals(locale, "zh_CN")||StrKit.equals(locale, "zh")){
							code=co.getNameZh();
						}
						else{
							code=co.getNameEn();
						}
					}
				}
			}
		}
		
		return code;
	}
	
	public Syscode getSysCode(String type,String code){
		Syscode syscode=null;
		if(type==null||code==null){
			return syscode;
		}
		HashMap<String ,List<Syscode>> map = loadSyscodeFromCache();
		if(map!=null){
			List<Syscode> retcodes=map.get(type);
			if(retcodes!=null){
				for(Syscode co:retcodes){
					if(StrKit.equals(code, co.getCode())){
						syscode=co;
						break;
					}
				}
			}
		}
		
		return syscode;
	}
	
	public String getCodeTypeDisplay(String type,String locale){
		HashMap<String ,Syscode> map = loadSyscodeTypeFromCache();
		String ret="";
		if(map!=null){
			Syscode syscode=map.get(type);
			if(syscode!=null){
				if(StrKit.equals(locale, "zh_CN")||StrKit.equals(locale, "zh")){
					ret=syscode.getNameZh();
				}
				else{
					ret=syscode.getNameEn();
				}
			}
		}
		
		return ret;
	}
	
	public HashMap<String ,Syscode> loadSyscodeTypeFromCache(){
		return CacheKit.get("syscodeCache", "syscodetype", new 
			IDataLoader(){
			    public Object load() {
			    	HashMap<String ,Syscode> ret=new HashMap<String ,Syscode>();
			    	List<Syscode> syscodes=Syscode.dao.find("select * from syscode where parent=0 order by type,code");
			    	for(Syscode code:syscodes){
				    	ret.put(code.getType(), code);
			    	}
			    	return ret;
			    }
			}
		);
	}
	
	public HashMap<String ,List<Syscode>> loadSyscodeFromCache(){
		return CacheKit.get("syscodeCache", "syscode", new 
			IDataLoader(){
			    public Object load() {
			    	HashMap<String ,List<Syscode>> ret=new HashMap<String ,List<Syscode>>();
			    	List<Syscode> syscodes=Syscode.dao.find("select * from syscode where parent!=0 order by type,id");
			    	for(Syscode code:syscodes){
	//		    		if(code.getParent()!=0){
				    		List<Syscode> codelist=ret.get(code.getType());
				    		if(codelist==null){
				    			codelist=new ArrayList<Syscode>();
				    		}
				    		codelist.add(code);
				    		ret.put(code.getType(), codelist);
	//		    		}
			    	}
			    	return ret;
			    }
			}
		);
	}
}
