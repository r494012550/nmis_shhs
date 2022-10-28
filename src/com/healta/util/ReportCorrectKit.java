package com.healta.util;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.healta.constant.CacheName;
import com.healta.model.DicReportcorrectRules;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

public class ReportCorrectKit {
	private final static Logger log = Logger.getLogger(ReportCorrectKit.class);
//	public static List<DicReportcorrectRules> rules;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/*
	 * Record:必须包含
	 * 患者性别sex
	 * 检查项目examitem
	 * 所见desc
	 * 诊断result
	 * 
	 * return string  返回检测信息，为空表示通过检测，不为空表示检测出错误
	 * */
	public static String correct(Record r){
		String ret="";
		List<DicReportcorrectRules> rules=getRules();
		for(DicReportcorrectRules rule:rules){
			if(StrKit.equals("sex", rule.getType())){
				ret+=correct_sex(rule,r);
			} else if(StrKit.equals("examitem", rule.getType())){
				ret+=correct_examitem(rule,r);
			} else if(StrKit.equals("content", rule.getType())){
				ret+=correct_content(rule,r);
			} else if(StrKit.equals("modality_type", rule.getType())){
				ret+=correct_modality_type(rule,r);
			}
		}
		return ret;
	}

	
	public static List<DicReportcorrectRules> getRules(){
		return (List<DicReportcorrectRules>)CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_REPORT_CORRECT_RULES_KEY);
	}
	
	
	public static String correct_sex(DicReportcorrectRules rule,Record r){
		String ret="";
		if(StrKit.equals(rule.getKeyword(), r.getStr("sex"))){
			Pattern pattern = Pattern.compile(rule.getRules());
			String matcherstr="";
			Matcher matcher = pattern.matcher(r.getStr("desc"));
			while(matcher.find()){
				matcherstr+="“"+matcher.group()+"”";
			}
			matcher = pattern.matcher(r.getStr("result"));
			while(matcher.find()){ 
				matcherstr+="“"+matcher.group()+"”";
			}
			if(StrKit.notBlank(matcherstr)){
				ret=rule.getWarning();
				ret=ret.replaceAll("\\{sex\\}", r.getStr("sex"));
				ret=ret.replaceAll("\\{matchers\\}", matcherstr);
			}
		}
		return ret;
	}
	
	/**
	 * 设备类型纠错
	 * @param rule
	 * @param r
	 * @return
	 */
	public static String correct_modality_type(DicReportcorrectRules rule,Record r){
		String ret="";

		if(r.getStr("modality_type").contains(rule.getKeyword())){
			Pattern pattern = Pattern.compile(rule.getRules());
			String matcherstr="";
			Matcher matcher = pattern.matcher(r.getStr("desc"));
			while(matcher.find()){
				matcherstr+="“"+matcher.group()+"”";
			}
			matcher = pattern.matcher(r.getStr("result"));
			while(matcher.find()){ 
				matcherstr+="“"+matcher.group()+"”";
			}
			if(StrKit.notBlank(matcherstr)){
				ret=rule.getWarning();
				ret=ret.replaceAll("\\{modality_type\\}", r.getStr("modality_type"));
				ret=ret.replaceAll("\\{matchers\\}", matcherstr);
			}
		}
		return ret;
	}
	
	public static String correct_examitem(DicReportcorrectRules rule,Record r){
		String ret="";
		if(StrKit.equals(rule.getKeyword(), r.getStr("examitem"))){
			Pattern pattern = Pattern.compile(rule.getRules());
			String matcherstr="";
			Matcher matcher = pattern.matcher(r.getStr("desc"));
			while(matcher.find()){
				matcherstr+=matcher.group();
			}
			matcher = pattern.matcher(r.getStr("result"));
			while(matcher.find()){
				matcherstr+=matcher.group();
			}
			if(StrKit.notBlank(matcherstr)){
				ret=rule.getWarning();
				ret=ret.replaceAll("\\{examitem\\}", r.getStr("examitem"));
				ret=ret.replaceAll("\\{matchers\\}", matcherstr);
			}
		}
		
		return ret;
	}
	
	
	public static String correct_content(DicReportcorrectRules rule,Record r){
		String ret="";
		Pattern pattern = Pattern.compile(rule.getKeyword());
		Matcher matcher = pattern.matcher(r.getStr("desc"));
		String matcherstr="";
		while(matcher.find()){
			matcherstr+=matcher.group();
		}
		pattern = Pattern.compile(rule.getRules());
		matcher = pattern.matcher(r.getStr("result"));
		String matcherstr1="";
		while(matcher.find()){
			matcherstr1+=matcher.group();
		}
		if(StrKit.notBlank(matcherstr)||StrKit.notBlank(matcherstr1)){
			ret=rule.getWarning();
			ret=ret.replaceAll("\\{matcher_desc\\}", matcherstr);
			ret=ret.replaceAll("\\{matcher_result\\}", matcherstr1);
		}
		return ret;
	}
}
