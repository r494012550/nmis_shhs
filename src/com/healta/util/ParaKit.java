package com.healta.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.model.Statisticalcustomconditions;
import com.jfinal.kit.StrKit;

public class ParaKit {
	private final static Logger log = Logger.getLogger(ParaKit.class);
	private Map<String, String[]> map;
	
	public ParaKit(Map<String, String[]> map) {
		this.map=map;
	}
	
	public String getPara(String key){
		String ret=null;
		if(map.get(key)!=null&&map.get(key).length>0){
			ret=map.get(key)[0];
		}
		return ret;
	}
	
	public static String generateWhere(HttpServletRequest req,HashMap<String, Object> parameters){
		String ret=" 1=1 ";
		String datatype="";
		Enumeration<?> temp = req.getParameterNames();
		while (temp.hasMoreElements()) {
			String paramName = (String) temp.nextElement();
			String paramValue = req.getParameter(paramName);
			parameters.put(paramName, paramValue);
			
			log.info("paramName="+paramName+"; and paramValue="+paramValue);
			
			if(StrKit.equals("datetype", paramName)){
				datatype=paramValue;
			}
			else if(StrKit.equals("datefrom", paramName)&&StrKit.notBlank(paramValue)){
				ret+=" and _datatype_ >='"+paramValue+" 00:00:00:000'";
			}
			else if(StrKit.equals("dateto", paramName)&&StrKit.notBlank(paramValue)){
				ret+=" and _datatype_ <'"+paramValue+" 23:59:59:998'";
			}
			else if(StrKit.equals("patientsource", paramName)&&StrKit.notBlank(paramValue)){
				ret+=" and admission.patientsource ='"+paramValue+"'";
			}
			else if(StrKit.equals("modality", paramName)&&StrKit.notBlank(paramValue)){
				ret+=" and studyorder.modality_type ='"+paramValue+"'";
			}
			else if(StrKit.equals("institution", paramName)&&StrKit.notBlank(paramValue)){
				ret+=" and admission.institutionid ="+paramValue;
			}
			else if(StrKit.equals("modalityid", paramName)){
				String[] ids=req.getParameterValues(paramName);
				if(ids.length==1){
					ret+=" and studyorder.modalityid ="+paramValue;
				}
				else if(ids.length>1){
					ret+=" and studyorder.modalityid in ("+makeArrToStr(ids,true)+")";
				}
			}
			else if(StrKit.equals("regoperator", paramName)){
				String[] ids=req.getParameterValues(paramName);
				if(ids.length==1){
					ret+=" and studyorder.creator ='"+paramValue+"'";
				}
				else if(ids.length>1){
					ret+=" and studyorder.creator in ("+makeArrToStr(ids,false)+")";
				}
			}
			else if(StrKit.equals("technologists", paramName)){
				String[] ids=req.getParameterValues(paramName);
				if(ids.length==1){
					ret+=" and study.creator ='"+paramValue+"'";
				}
				else if(ids.length>1){
					ret+=" and study.creator in ("+makeArrToStr(ids,false)+")";
				}
			}
			else if(StrKit.equals("reportphysician", paramName)){
				String[] ids=req.getParameterValues(paramName);
				if(ids.length==1){
					ret+=" and report.reportphysician ='"+paramValue+"'";
				}
				else if(ids.length>1){
					ret+=" and report.reportphysician in ("+makeArrToStr(ids,false)+")";
				}
			}
			else if(StrKit.equals("auditphysician", paramName)){
				String[] ids=req.getParameterValues(paramName);
				if(ids.length==1){
					ret+=" and report.auditphysician ='"+paramValue+"'";
				}
				else if(ids.length>1){
					ret+=" and report.auditphysician in ("+makeArrToStr(ids,false)+")";
				}
			}
			else if(StringUtils.startsWith(paramName, "customconditions_")&&StrKit.notBlank(paramValue)){
				Statisticalcustomconditions scc=Statisticalcustomconditions.dao.findById(Integer.valueOf(paramName.substring("customconditions_".length())));
				
				switch(scc.getOperator()){
					case "in":{
						ret+=" and "+scc.getColumnname()+" in("+paramValue+")";
						break;
					}
					case "like":{
						ret+=" and "+scc.getColumnname()+" like '%"+paramValue+"%'";
						break;
					}
					default:{
						if("str".equals(scc.getType())){
							ret+=" and "+scc.getColumnname()+scc.getOperator()+" '"+paramValue+"'";
						}
						else{
							ret+=" and "+scc.getColumnname()+scc.getOperator()+paramValue;
						}
					}
				}
			}
			//parameters.put(paramName, paramValue);
		}
		return StringUtils.replace(ret, "_datatype_", datatype);
	}
	
	public static String makeArrToStr(String[] paras,boolean isint){
		String ret="";
		for(String x : paras){
			if(isint){
				ret+=x+",";
			}
			else{
				ret+="'"+x+"',";
			}
		}
		if(StrKit.notBlank(ret)){
			ret=ret.substring(0, ret.length()-1);
		}
		return ret;
	}
	
	public boolean hasPara(String key){
		if(map.get(key)!=null&&map.get(key).length>0&&StrKit.notBlank(map.get(key)[0])){
			return true;
		} else{
			return false;
		}
	}
}
