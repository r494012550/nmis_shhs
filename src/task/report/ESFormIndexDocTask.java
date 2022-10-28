package task.report;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.healta.model.ResearchFormData;
import com.healta.model.ResearchTestgroupData;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import task.BaseTask;

public class ESFormIndexDocTask implements Runnable,BaseTask {
	private final static Logger log = Logger.getLogger(ESFormIndexDocTask.class);
	private Date lastRunTime;
	private String errorMessage;
	
	@Override
	public void run() {
		log.info("----Begin Index Form data Task----");
		errorMessage=null;
		try{
			//使用userless 可以移除缓存，下次使用同样的文件名的配置文件时，会重新读取配置文件
			PropKit.useless("task.properties");
			int beforeday=PropKit.use("task.properties").getInt("task15._before_day",3);
			
			LocalDate now=LocalDate.now();
			LocalDate before= now.minusDays(beforeday-1);
			
			log.info("createtime range:"+before.format(DateTimeFormatter.ISO_LOCAL_DATE)+" -- "+now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
			
			Db.find("select * from research_form_data where (indexed is null or indexed =0) and path is not null and createtime>? and createtime<=? order by dataid,id",
					before.format(DateTimeFormatter.ISO_LOCAL_DATE),
					now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)).stream().collect(Collectors.groupingBy(x->x.getInt("dataid"))).forEach((k,v)->{
				log.info("process dataid="+k);
				
				
				String model=null,domain=null,docid=null,dataid=null;
				JSONObject model_json=null;
				JSONArray attrs_arr=null;
				if(v.size()>0){
					String path=v.get(0).getStr("path");
					if(StrKit.notBlank(path)){
						model=getModel(path);
						domain=getDomain(path);
						//{"code":0,"data":true,"message":"请求成功"}
						JSONObject exist=JSONObject.parseObject(HttpKit.get(PropKit.use("system.properties").get("matrix_url")+"/api/existsIndexDoc_v01?indexname="+model+"&docid="+getDocid(k+"")));
						if(exist.getIntValue("code")==0&&exist.getBoolean("data")){
							JSONObject.parseObject(HttpKit.get(PropKit.use("system.properties").get("matrix_url")+"/api/deleteIndexDoc_v01?indexname="+model+"&docid="+getDocid(k+"")));
						}
						
						if(StrKit.notBlank(domain)&&StrKit.notBlank(model)){
							String res=HttpKit.get(PropKit.use("system.properties").get("matrix_url")+"/api/getModelByName_v01?domainname="+domain+"&modelname="+model);
							log.info("model="+res);
							if(StrKit.notBlank(res)){
								model_json=JSONObject.parseObject(res);
								String attrs_str=HttpKit.get(PropKit.use("system.properties").get("matrix_url")+"/api/getAttrs_v01?modelid="+model_json.getString("id"));
	//							log.info(attrs_str);
								attrs_arr=JSONArray.parseArray(attrs_str);
							}
						}
					}
				}

//				log.info(attrs_arr.toJSONString());
				
				List<Record> simple_list=new ArrayList<Record>();
				List<Record> array_obj_list=new ArrayList<Record>();
				//第一步循环查筛选出array_object属性的数据
				for(Record r:v) {
					String path=r.getStr("path"),group=r.getStr("grp");
					if(attrs_arr!=null){
						String p=getPath(path);
						JSONArray attrs=JSONArray.parseArray(attrs_arr.toJSONString());//重新创建JSONArray对象，后续方法中会修改attrs
						if(StrKit.notBlank(path)&&StrKit.notBlank(group)&&isArrayObjectType(p , attrs)) {
							array_obj_list.add(r);
						} else {
							simple_list.add(r);
						}
					}
				}
				
				JSONObject doc=new JSONObject();
//				HashMap<String, List<Record>> objarr_map=new HashMap<String, List<Record>>();
				//第二遍循环处理非array_object属性的数据
				for(Record r:simple_list) {
					dataid=r.getStr("dataid");
					docid=getDocid(dataid);
					String path=r.getStr("path");
	//				log.info("path==="+path);
					if(StrKit.notBlank(path)) {
						String p=getPath(path);
						Object value=r.getStr("value");
						// 处理多值字段
						if(attrs_arr!=null){
							JSONArray attrs=JSONArray.parseArray(attrs_arr.toJSONString());//重新创建JSONArray对象，后续方法中会修改attrs
							JSONObject attr=extract(p , attrs);
//							log.info("p="+p);
//							log.info("attr="+attr.toJSONString());
							if(attr!=null){
								String data_type=attr.getJSONObject("attributes").getString("datatype");
//								log.info("data_type="+data_type);
								if(StrKit.equals(data_type, "array_simple")){
									String type=attr.getJSONObject("attributes").getString("simple_type_array");
	//								if(value.indexOf(",")>=0){//处理多值字段
										switch(type){
											case "string":if(value!=null)value=value.toString().split(",");break;
											case "integer": {
												if(value!=null){
													value=convertStr2Int(value.toString().split(","));
												}
												break;
											}
											case "float": {
												if(value!=null){
													value=convertStr2Flo(value.toString().split(","));
												}
												break;
											}
											default: if(value!=null)value=value.toString().split(",");//默认数据类型为字符串//value="[\""+StringUtils.join(value.split(","), "\",\"")+"\"]"
										}
	//								}
									
								} 
//								else if(StrKit.equals(data_type, "array_object")) {
//									log.info("array_object="+p);
//									List<Record> list=objarr_map.get(p);
//									if(list==null) {
//										list=new ArrayList<Record>();
//									}
//									list.add(r);
//									continue;
//								}
							}
						} 
	
//						if(JSONPath.contains(doc, p)) {
//							
//						} else {
							JSONPath.set(doc, p, value);
							if(StrKit.notBlank(r.getStr("unit"))){
								JSONPath.set(doc, p+"___unit_", r.getStr("unit"));
							}
//						}
					}
				}
				
				//处理可复制章节中的数据
//				if(attrs_arr!=null){
//					getArrayObjValueNew(array_obj_list,attrs_arr,doc);
//				}
				//设置通用属性值
				setGenericAttrVal(k,doc);
				
				log.info("document="+doc.toString());
				if(model!=null&&docid!=null&&model_json!=null){
					JSONObject data=new JSONObject();
					data.put("indexname", model);
					data.put("docid", docid);
					//设置内置属性值
					JSONPath.set(doc, "__createtime__", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
					JSONPath.set(doc, "__source_id__", dataid);
					JSONPath.set(doc, "__source__", "1");
					data.put("json", doc.toString());
					Map<String, String> heads=new HashMap<String, String>();
					heads.put("content-type", "application/json");
					String result=HttpKit.post(PropKit.use("system.properties").get("matrix_url")+"/api/indexDoc_v01", data.toJSONString(),heads);
					log.info("index doc result:dataid="+docid+";result:"+result);
					
					JSONObject res=JSONObject.parseObject(result);
					//{"code":0,"data":"CREATED","message":"请求成功"}
					if(res.getIntValue("code")==0&&StrKit.equals("CREATED", res.getString("data"))){
						Db.update("update research_form_data set indexed=1 where dataid=?",dataid);
					}
					
				}
			});
			
		} catch(Exception ex){
			ex.printStackTrace();
			errorMessage=ex.getMessage();
		} finally {
			lastRunTime=new Date();
		}
		log.info("----End Index Form data Task----");
	}
	
	/*
	 * docid格式：{reportid}_{source_id}
	 * source_id: 数据来源：1:表单，2:结构化报告
	 * reportid: report表id
	 * */
	public String getDocid(String reportid){
		return reportid+"_1";
	}
	
	public String getDomain(String path) {
		return path.split("\\.")[0];
	}
	
	public String getModel(String path) {
		return path.split("\\.")[1];
	}
	
	public String getPath(String path) {
		//log.info("path="+path+";len="+path.length());
		//log.info((getDomain(path)+"."+getModel(path)+".").length());
		return path.substring((getDomain(path)+"."+getModel(path)+".").length());
	}
	
	public String getPath(String path,String domain_model) {
		//log.info("path="+path+";len="+path.length());
		//log.info("domain_model="+domain_model);
		if(StrKit.notBlank(domain_model)){
			if(path.startsWith(domain_model+".")){
				return path.substring(domain_model.length()+1);
			} else{
				return path;
			}
		} else{
			return getPath(path);
		}
		//return path.substring((getDomain(path)+"."+getModel(path)+".").length());
	}
	
	@Override
	public String getTaskName(){
		return "索引表单数据";
	}
	
	@Override
	public Date getLastRunTime(){
		return lastRunTime;
	}
	
	@Override
	public String getErrorMessage(){
		return errorMessage;
	}
	
	@Override
	public boolean canCopy(){
		return false;
	}
	
	public static Integer[] convertStr2Int(String[] arr){
		Integer[] ret= new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++){
			ret[i]=Integer.valueOf(arr[i]);
		}
		return ret;
	}
	
	public static Float[] convertStr2Flo(String[] arr){
		Float[] ret= new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++){
			ret[i]=Float.valueOf(arr[i]);
		}
		return ret;
	}
	
	public static void main(String[] args) throws IOException {
		
		//System.out.println(getPath("zhudongmaijiacengshuqianpinggu.zhudongmaijiacengshuqianpinggu.zhudongmaishuqianyingxiangxuezhenduan.hebingbingbiandongmaixiazhaimiaoshu.hebingbingbiandongmaixiazhaileijichangdu"));
//		String json=FileUtils.readFileToString(new File("d:\\12.json"));
		
//		System.out.println(json);
		
		//System.out.println(JSONPath.extract(json, "name"));
		
//		JSONArray arr =JSONArray.parseArray(json);
		//System.out.println(arr.getJSONObject(1));

		//JSONPath.extract(json, path)
		
//		String path="patientinfo.patientname";
//		System.out.println(extract(path,arr));
//		System.out.println(isArrayObjectType(path,arr));
		
//		System.out.println(StringUtils.join("4,5".split(","), "\",\""));
		
		JSONObject doc=new JSONObject();
		JSONPath.set(doc, "()a04ADgm", "bpm");
//		
//		System.out.println(doc.toJSONString());
	
//		getArrayObjValueNew(JSONArray.parseArray(FileUtils.readFileToString(new File("e:\\333.json"))),
//				JSONArray.parseArray(FileUtils.readFileToString(new File("e:\\444.json"))),new JSONObject());
//		JSONPath.eval(rootObject, path)
	}
	
	public static JSONObject extract(String path,JSONArray arr){
		JSONObject model_obj=null;
		String[] path_arr=path.split("\\.");
		for(String p:path_arr){
			model_obj=extractSub(p,arr);
			if(model_obj!=null){
				continue;
			}
		}
		return model_obj;
	}
	
	public static JSONObject extractSub(String path,JSONArray arr){
		for(int i=0,len=arr.size();i<len;i++){
			if(StrKit.equals(arr.getJSONObject(i).getString("name"), path)){
				JSONArray child=arr.getJSONObject(i).getJSONArray("children");
				if(child!=null){
					arr.clear();
					arr.addAll(child);
					return null;
				} else{
					return arr.getJSONObject(i);
				}
			}
		}
		return null;
		
	}
	
	public static boolean isArrayObjectType(String path,JSONArray arr){
		JSONArray attrs=(JSONArray)arr.clone();
		String[] path_arr=path.split("\\.");
		for(String p:path_arr){
			if(isArrayObjectTypeSub(p,attrs)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isArrayObjectTypeSub(String path,JSONArray attrs){
		for(int i=0,len=attrs.size();i<len;i++){
//			log.info("path---"+path);
			if(StrKit.equals(attrs.getJSONObject(i).getString("name"), path)){
				if(StrKit.equals(attrs.getJSONObject(i).getString("datatype"), "array_object")) {
//					System.out.println("path=---"+path);
//					System.out.println(arr.getJSONObject(i));
					return true;
				} else {
					JSONArray child=attrs.getJSONObject(i).getJSONArray("children");
					if(child!=null){
						attrs.clear();
						attrs.addAll(child);
						return false;
					}
				}
			}
		}
		return false;
	}
	
	public static String getRootPath(String path){
		String[] path_arr=path.split("\\.");
		if(path_arr.length==1) {
			return path;
		} else {
			return path_arr[0];
		}
	}
	
	public static String getParentPath(String path,JSONArray arr){
		String[] path_arr=path.split("\\.");
		if(path_arr.length==1) {
			return path;
		}
		for(int i=path_arr.length-2;i>=0;i--) {
			if(isArrayObjectType(path_arr[i],arr)) {
				log.info("isarra----"+path_arr[i]);
				return StringUtils.join(path_arr, ".", 0, i+1);
			}
		}
		return null;//StringUtils.join(path_arr, ".", 0, path_arr.length-1);
	}
	public static String getParentPath(String path){
		String[] path_arr=path.split("\\.");
		if(path_arr.length==1) {
			return "";
		}
		else{
			return StringUtils.join(path_arr, ".", 0, path_arr.length-1);
		}
	}
	public static String getParentgroup(String group){
		String[] path_arr=group.split("\\.");
		return path_arr.length==1?group:StringUtils.join(path_arr, ".", 0, path_arr.length-1);
	}
	
	public static void appendAttr(JSONObject json,String path,Object j) {
		Object obj=JSONPath.eval(json, path);
		if(obj==null) {
			JSONPath.set(json, path, j);
		} else {
			if(j instanceof JSONObject&&obj instanceof JSONObject) {
				JSONObject obj_j=(JSONObject)obj;
				JSONObject j_j=(JSONObject)j;
				
				obj_j.forEach((k,v)->{
					JSONPath.set(json, path+"."+k, v);
				});
				j_j.forEach((k,v)->{
					JSONPath.set(json, path+"."+k, v);
				});
				
			} else if(j instanceof JSONObject&&obj instanceof JSONArray) {
				JSONObject j_j=(JSONObject)j;
				j_j.forEach((k,v)->{
					JSONPath.set(json, path+"."+k, v);
				});
			} else if(j instanceof JSONArray&&obj instanceof JSONObject) {
				JSONPath.set(json, path, j);
				JSONObject obj_j=(JSONObject)obj;
				obj_j.forEach((k,v)->{
					JSONPath.set(json, path+"."+k, v);
				});
			} else {
				JSONArray arr=new JSONArray();
				arr.add(obj);
				arr.add(j);
				JSONPath.set(json, path, arr);
			}
		}
	}
	
	public void setGenericAttrVal(Integer dataid,JSONObject doc) {
		ResearchTestgroupData data=ResearchTestgroupData.dao.findById(dataid);
		if(data!=null) {
			String[] cols=ResearchTestgroupData.dao.getGeneralColumns();
			for(int i=0;i<cols.length;i++) {
				Object obj=data.get(cols[i]);
				if(obj instanceof Date) {
					Date date=(Date)obj;
					JSONPath.set(doc, cols[i], DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
				} else {
					JSONPath.set(doc, cols[i], data.getStr(cols[i]));
				}
			}
		}
	}
	
	public void getArrayObjValueNew(List<Record> list,JSONArray attrs,JSONObject doc) {
		String domain_model="";
		//List 转JSONArray
		JSONArray strs=new JSONArray();
		for(Record re:list) {
			JSONObject obj=new JSONObject();
			obj.put("id", re.getInt("id"));
			obj.put("dataid", re.getInt("dataid"));
			obj.put("code", re.getStr("code"));
			obj.put("optioncode", re.getStr("optioncode"));
			obj.put("value", re.getStr("value"));
			obj.put("unit", re.getStr("unit"));
			obj.put("grp", re.getStr("grp"));
			obj.put("path", re.getStr("path"));
			obj.put("indexed", re.getInt("indexed"));
			obj.put("createtime", re.getDate("createtime"));
			obj.put("___group", re.getStr("grp"));
			obj.put("___path", re.getStr("path"));
			strs.add(obj);
			
			if(StrKit.isBlank(domain_model)){
				domain_model=getDomain(obj.getString("___path"))+"."+getModel(obj.getString("___path"));
			}
		}
//		log.info(strs);
//		log.info(arr);
		//以root path 分组
		HashMap<String, JSONArray> map=new HashMap<String, JSONArray>();
		for(int i=0,len=strs.size();i<len;i++) {
			JSONObject json=strs.getJSONObject(i);
			String root=getRootPath(getPath(json.getString("___path")));
			JSONArray jsonarr=map.get(root);
			if(jsonarr==null) {
				jsonarr=new JSONArray();
			}
			jsonarr.add(json);
			map.put(root, jsonarr);
			
			if(StrKit.isBlank(domain_model)){
				domain_model=getDomain(json.getString("___path"))+"."+getModel(json.getString("___path"));
			}
		}
		//根据___group合并同一组的属性
		JSONArray jsons=new JSONArray();
		for (JSONArray array : map.values()) {

			JSONArray ja=new JSONArray();
			String pregroup=null;
			JSONObject j=null;
//				String prepath=null;
			for (int i = 0; i < array.size(); i++) {
				JSONObject json=array.getJSONObject(i);
//					log.info(json);
				String group=json.getString("___group"),path=getPath(json.getString("___path"),domain_model);
//					log.info("pregroup="+pregroup+";group="+group);
				if(!StrKit.equals(pregroup, group)) {
					j=new JSONObject();
					ja.add(j);
				}
				
				JSONPath.set(j, path, json.getString("value"));
				if(StrKit.notBlank(json.getString("unit"))){
					JSONPath.set(j, path+"___unit_", json.getString("unit"));
				}
				String parent_path=getParentPath(path),pa_group=getParentgroup(group);
				//log.info("path--"+path+"--parent_path="+parent_path+";pa_group="+pa_group);
				JSONPath.set(j, "___path", parent_path!=null?parent_path:path);
				JSONPath.set(j, "___group", pa_group);
				pregroup=group;
			}
			jsons.addAll(ja);
        }
		log.info(jsons);
		//根据___path的根值，合并相同的对象
		if(jsons.size()>1) {
			int count=0;
			while(true){
				boolean flag=true;
				String prepath=null;
				JSONObject j=null;
				JSONArray ja=new JSONArray();
				for (int i = 0; i < jsons.size(); i++) {
					JSONObject json=jsons.getJSONObject(i);
	//				log.info(json);
					String path=json.getString("___path");
					//log.info("prepath----"+prepath+"----path---"+path);
					if(StrKit.isBlank(path)){
						ja.add(json);
						flag=flag&true;
						prepath=null;
					} else{
						if(!StrKit.equals(prepath, path)) {
							j=new JSONObject();
							ja.add(j);
						}
						Object obj=JSONPath.eval(json, path);
						Object obj1=JSONPath.eval(j, path);
						if(obj1==null) {
							obj1=obj;
						}
						
		//				log.info(path+" is array="+isArrayObjectType(path, arr));
						
						if(StrKit.equals(prepath, path)) {//相同对象
							if(isArrayObjectType(path, attrs)){
								JSONArray lastarr=null;
								if(obj1 instanceof JSONObject) {
									lastarr=new JSONArray();
									lastarr.add(obj1);
									lastarr.add(obj);
								} else if(obj1 instanceof JSONArray) {
									lastarr=(JSONArray)obj1;
									lastarr.add(obj);
								}
								
								JSONPath.set(j, path, lastarr);
							} else{
								appendAttr(j,path,obj);
							}
						} else {
							JSONPath.set(j, path, obj);
						}
						
						String parent_path=getParentPath(path);
						if(StrKit.isBlank(parent_path)) {
							flag=flag&true;
						}  else {
							flag=false;
							JSONPath.set(j, "___path", parent_path);
						}
						prepath=path;
					}
				}
				log.info(ja);
				jsons=ja;
	
				if(flag) {
					break;
				}
			}
		}
		
		log.info(jsons);
		//将分组好的数据，添加到doc中
		for (int i = 0; i < jsons.size(); i++) {
			JSONObject json=jsons.getJSONObject(i);
			json.forEach((k,v)->{
				JSONPath.set(doc, k, v);
			});
		}
		
		log.info(doc);
	}
}
