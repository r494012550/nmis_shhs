package com.healta.config;

import com.healta.constant.CacheName;
import com.healta.model.DicDepartment;
import com.healta.model.DicEmployee;
import com.healta.model.DicExamitem;
import com.healta.model.DicModality;
import com.healta.model.DicOrgan;
import com.healta.model.DicReportcorrectRules;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public class InitializeCache {
	
	public static void init(){
		
		CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_INSTITUTION_KEY, new IDataLoader(){ 
			public Object load() { 
				return DicDepartment.dao.find("select * from dic_institution where deleted=0"); 
			}
		});
		
		CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_DEPARTMENT_KEY, new IDataLoader(){ 
			public Object load() { 
				return DicDepartment.dao.find("select * from dic_department where deleted=0"); 
			}
		});
		
		CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_EMPLOYEE_KEY, new IDataLoader() { 
			public Object load() { 
				return DicEmployee.dao.find("select * from dic_employee where deleted=0"); 
			}
		});
		
		CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY, new 
			IDataLoader(){ 
			    public Object load() { 
			      return DicModality.dao.find("select * from dic_modality where deleted=0");
		}});
		
		CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_EXAMITEM_KEY, new IDataLoader(){ 
			public Object load() { 
				return DicExamitem.dao.find("select * from dic_examitem where deleted=0"); 
			}
		});
		
		CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_ORGAN_KEY, new 
			IDataLoader(){ 
			    public Object load() { 
			      return DicOrgan.dao.find("select * from dic_organ where deleted=0"); 
			}
		});
		
		CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_REPORT_CORRECT_RULES_KEY, new 
			IDataLoader(){ 
			    public Object load() { 
			      return DicReportcorrectRules.dao.find("select * from dic_reportcorrect_rules"); 
			}
		});

	}

}
