/*
 *  Copyright 2014-2015 snakerflow.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.healta.plugin.shiro;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.json.JSONArray;
import org.json.JSONObject;

import com.healta.model.ResearchResource;
import com.healta.model.Resource;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.Db;

/**
 * 支持Shiro的插件
 * @author yuqs
 * @since 0.1
 */
public class ShiroPlugin implements IPlugin {
	
	private static final Logger log = Logger.getLogger(ShiroPlugin.class);
	public static final String PREMISSION_FORMAT = "perms[\"{0}\"]";
	private static FilterChainManager manager = null;
	@Override
	public boolean start() {
		if(manager == null) return false;
		
		if(PropKit.use("system.properties").getBoolean("resetResource")==true) {
			Db.update("truncate table resource");
			File file = new File(PathKit.getWebRootPath()+"\\WEB-INF\\classes\\"+"system_source.json");
			try {
				String input = FileUtils.readFileToString(file, "UTF-8");//UTF-8 GBK
				JSONArray jsonArray = new JSONArray(input);
				JSONArray returnArray = new JSONArray();
			    for(int i=0;i<jsonArray.length();i++) {
			    	getResource(jsonArray.getJSONObject(i), returnArray);
			    }
			    
			    List<Resource> modelList = new ArrayList<Resource>();
			    for(int i=0;i<returnArray.length();i++) {
			    	JSONObject json = returnArray.getJSONObject(i);
			    	Resource resource = new Resource();
			    	resource.setRid(json.getInt("rid"));
			    	resource.setDisplayZhCn(json.getString("display_zh_CN"));
			    	resource.setDisplayEnUs(json.getString("display_en_US"));
			    	resource.setResource(json.getString("resource"));
			    	resource.setName(json.getString("name"));
			    	modelList.add(resource);
			    }
			    Db.batchSave(modelList, modelList.size());
			}catch(Exception e) {
				e.printStackTrace(); 
			}
			
			try {
				Db.update("truncate table research_resource");
				File file1 = new File(PathKit.getWebRootPath()+"\\WEB-INF\\classes\\"+"research_project_source.json");
				String input = FileUtils.readFileToString(file1, "UTF-8");//UTF-8 GBK
				JSONArray jsonArray = new JSONArray(input);
				JSONArray returnArray = new JSONArray();
			    for(int i=0;i<jsonArray.length();i++) {
			    	getResource(jsonArray.getJSONObject(i), returnArray);
			    }
			    
			    List<ResearchResource> modelList = new ArrayList<ResearchResource>();
			    for(int i=0;i<returnArray.length();i++) {
			    	JSONObject json = returnArray.getJSONObject(i);
			    	ResearchResource researchresource = new ResearchResource();
			    	researchresource.setRid(json.getInt("rid"));
			    	researchresource.setDisplayZhCn(json.getString("display_zh_CN"));
			    	researchresource.setDisplayEnUs(json.getString("display_en_US"));
			    	researchresource.setResourceValue(json.getString("resource"));
			    	researchresource.setResourceName(json.getString("name"));
			    	modelList.add(researchresource);
			    }
			    Db.batchSave(modelList, modelList.size());
			}catch(Exception e) {
				e.printStackTrace(); 
			}
		
		}
		
		List<Resource> resources = Resource.dao.getWithAuthAll();
		for(Resource resource : resources) {
			String source = resource.getStr("resource");
			String authority = resource.getStr("authorityName");
			if(StringUtils.isEmpty(source)) {
				continue;
			}
			if(source.indexOf(";") != -1) {
				String[] sources = source.split(";");
				for(String singleSource : sources) {
					createChain(manager, singleSource, authority);
				}
			} else {
				createChain(manager, source, authority);
			}
		}
		manager.createChain("/**", "user");
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}
	
	private void createChain(FilterChainManager manager, String key, String value) {
	    log.info("add authority url[url=" + key + "\tvalue=" + value + "]");
	    manager.createChain(key, MessageFormat.format(PREMISSION_FORMAT, value));
	}

	public static void setFilterChainManager(FilterChainManager manager) {
		ShiroPlugin.manager = manager;
	}
	
	public void getResource(JSONObject jsonObject,JSONArray returnArray) {
		if(jsonObject.has("children")) {
			JSONArray jsonArray = jsonObject.getJSONArray("children");
			for(int i=0; i<jsonArray.length(); i++) {
				getResource( jsonArray.getJSONObject(i), returnArray);
			}
		}else if(jsonObject.has("resource")&&StringUtils.isNotBlank(jsonObject.getString("resource"))) {
			returnArray.put(jsonObject);
		}
	}
}
