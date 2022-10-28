package com.healta.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.CacheName;
import com.healta.constant.PrintTemplateType;
import com.healta.model.Client;
import com.healta.model.DicEquipGroup;
import com.healta.model.DicReportcorrectRules;
import com.healta.model.Label;
import com.healta.model.Labelfolder;
import com.healta.model.Modules;
import com.healta.model.Printer;
import com.healta.model.Printtemplate;
import com.healta.model.RisTrigger;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

public class SystemConfigService {
	private static final Logger log = Logger.getLogger(SystemConfigService.class);
	
	public List<Modules> findModules(String value){
		
		String sql="select * from modules";
		if(StringUtils.isNotEmpty(value)){
			sql+=" where name like '%"+value+"%'";
		}
		
		return Modules.dao.find(sql);
	}
	
	public boolean saveModule(Modules res){
		boolean ret=true;
		try{
			if(res.getId()==null){
				res.save();
			}
			else{
				res.update();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
/*
 * label增删改查
 */
	
	public List<Record> getLabels(String username){
		String sql1="select * from labelfolder where ispublic=1";
		String sql2="select * from label";
		List<Record> folders=Db.find(sql1);
		List<Record> labels=Db.find(sql2);
		List<Record> parents=new ArrayList<Record>();
		for(Record folder:folders){
			if(folder.getInt("parent")==0){
				parents.add(folder);
			}
		}
		rerangeFolder(parents,folders,labels);
		log.info(parents);
		return parents;
	}

	public void rerangeFolder(List<Record> parents,List<Record> folders,List<Record> labels){
		for(Record parent:parents){
			if(StrKit.notBlank(parent.getStr("foldername"))){
				parent.set("text", parent.getStr("foldername"));
				parent.set("display","");
				int parentid=parent.getInt("id");
				List<Record> childs=getChildren_Folder(folders, parentid);
				if(childs.size() > 0) {
					List<Record> labs=getChildren_Label(labels, parentid);
					childs.addAll(labs);
					parent.set("children", childs);
					rerangeFolder(childs,folders,labels);
				}else {
					List<Record> labs=getChildren_Label(labels, parentid);
					parent.set("children", labs);
				}
			}
		}
	}
	private List<Record> getChildren_Folder(List<Record> nodes,int parent){
		List<Record> ret=new ArrayList<Record>();
		for(Record re:nodes){
			if(re.getInt("parent").intValue()==parent){
				ret.add(re);
			}
		}
		return ret;
	}
	private List<Record> getChildren_Label(List<Record> labels,int parent){
		List<Record> ret=new ArrayList<Record>();
		for(Record re:labels){
			if(re.getInt("folderfk").intValue()==parent){
				re.set("text", re.getStr("label"));
				re.set("display", re.getStr("label"));
				re.set("labelid", re.getInt("id"));
				re.set("id", re.getInt("id")+"_label");
				ret.add(re);
			}
		}
		return ret;
	}
	public boolean saveLabelFolder(Labelfolder lf,Integer userid){
		boolean ret = false;
		List<Label> list = Label.dao.find("SELECT * FROM labelfolder WHERE parent=? AND foldername=? AND id!=?", lf.getParent(), lf.getFoldername(), lf.getId()==null?0:lf.getId());
		if(list.size()>0) {
			return ret;
		}
		
		if(lf.getId()!=null) {
			ret=lf.update();
		}
		else {
			lf.setCreator(userid);
			lf.setIspublic("1");
			ret=lf.remove("id").save();
		}	
		return ret;
	}
	
	public boolean saveLabel(Label label, Integer userid){
		boolean ret = false;
		List<Label> list = Label.dao.find("SELECT * FROM label WHERE folderfk=? AND label=? AND id!=?", label.getFolderfk(), label.getLabel(), label.getId()==null?0:label.getId());
		if(list.size()>0) {
			return ret;
		}
		
		if(label.getId() != null) {
			ret = label.update();
		}
		else {
			label.setCreator(userid);
			ret = label.remove("id").save();
		}
		
		return ret;
	}
	
	public boolean deleteLabelFolder(int id,Integer userid){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			try{
				String sql1="select * from labelfolder where ispublic='1' and creator=?";
				String sql2="select * from label where creator=?";
				List<Record> folders=Db.find(sql1,userid);
				List<Record> labels=Db.find(sql2,userid);
				List<Record> parents=new ArrayList<Record>();
				
				Record root=null;
				for(Record folder:folders){
					if(folder.getInt("id").intValue()==id){
						root=folder;
					}
				}
				getChildFolder(parents,root,folders);
				for(Record record:parents){
					log.info(record);
					Db.deleteById("labelfolder", record.getInt("id"));
					Db.delete("delete from label where folderfk=?", record.getInt("id"));
					
				}
			}
			catch(Exception ex){
				ret=false;
			}
			return ret;
		});
	}
	public void getChildFolder(List<Record> list,Record root,List<Record> folders){

		list.add(root);
		List<Record> childs=getChildren_Folder(folders, root.getInt("id"));
		if(childs.size() > 0) {
			for(Record child:childs){
				getChildFolder(list,child,folders);
			}
		}

	}
	public boolean deleteLabel(int id){
		return Db.deleteById("label", id);
	}
/*
 * 打印机
 * *
 */	
	public List<Record> getPrinter(String value) {
		
		String tmps=PropKit.use("system.properties").get("normal_report_print_templates");
		if(StrKit.notBlank(tmps)) {
			Arrays.asList(tmps.split(",")).stream().forEach(x -> {
				Printer printer=Printer.dao.findFirst("select top 1 * from printer where templatename=?",x);
				if(printer==null) {
					printer=new Printer();
					printer.setTemplatename(x);
					printer.setTemplatetype(1);
					printer.remove("id").save();
				}
			});
		}
		Db.find("select srtemplate.name,printer.id from srtemplate left join printer on srtemplate.name=printer.templatename").stream().forEach(x -> {
			if(x.getInt("id")==null) {
				Printer printer=new Printer();
				printer.setTemplatename(x.getStr("name"));
				printer.setTemplatetype(2);
				printer.remove("id").save();
			}
		});
		
		
		String where = "";
		List<Object> list=new ArrayList<Object>();
	
		if(StringUtils.isNotBlank(value)){
			where += " and printername like CONCAT('%',?,'%')";
			list.add(value);
		}
		String sql="select * from printer where 0=0"+where;

		List<Record> ret=Db.find(sql,list.toArray());
		ret.stream().forEach(x->{
			if(x.getInt("templatetype").intValue()==1) {
				x.set("group", "常规报告模板");
			}
			else {
				x.set("group", "结构化报告模板");
			}
		});
		
		return ret;

	}
	
	public boolean savePrinter(Printer printer){
		boolean ret=true;
		if(printer.getId()!=null){
			printer.update();
		}
		else{
			printer.remove("id").save();
		}

		return ret;
	}
	
	public boolean deletePrinter(Integer id) {
		boolean ret=true;
		return ret&&Printer.dao.deleteById(id);
	}
	
/*
 * 客户端
 * *
 */	
	
	public boolean checkIP(String hostip,Integer id){		
		boolean ret = true;
		String sql="select * from client where hostip = ?";
		if(id!=null) {
			sql+=" and id <> "+id;
			if(Client.dao.find(sql,hostip).size()!=0){
				return false;
			}				
		}
		else {
			if(Client.dao.find(sql,hostip).size()!=0){
				return false;
			}	
		}
		return ret;
	
	}
	
	
	public List<Client> getClient(String value,Integer modalityid) {
		String where = "";
		List<Object> list=new ArrayList<Object>();
	
		if(StringUtils.isNotBlank(value)){
			where += " and hostname like CONCAT('%',?,'%')";
			list.add(value);
		}
		if(modalityid!=null) {
			where += " and modalityid =?";
			list.add(modalityid);
		}
		String sql="select *,(select name_zh from syscode where syscode.code = client.type and syscode.type='0020')as type"
				+ ",(select modality_name from dic_modality where dic_modality.id= client.modalityid) as modalityname"
				+ " from client where 0=0"+where;
		return Client.dao.find(sql,list.toArray());
	}
	
	/**
	 * 保存客户端
	 * @param client
	 * @return
	 */
	public boolean saveClient(Client client){
		boolean ret=true;

		if(client.getId()==null){
			ret=ret&&client.remove("id").save();
		}
		else{
			if(!StringUtils.equals("Exam", client.getType())) {
				client.setModalityid(null);
			}
			ret=ret&&client.update();
		}
		
		return ret;
	}
	
	/**
	 * 删除客户端
	 * @param id
	 * @return
	 */
	public boolean deleteClient(Integer id) {
		boolean ret=true;
		return ret&&Client.dao.deleteById(id);
	}
	
	public boolean saveRisTriggers(String name, boolean on_off) {
		boolean ret=true;
		RisTrigger rt=RisTrigger.dao.findFirst("select * from ris_trigger where name=?",name);
		if(rt==null) {
			rt=new RisTrigger();
			rt.setName(name);
			rt.setOnOff(on_off?"1":"0");
			ret=ret&&rt.remove("id").save();
		}
		else {
			rt.setOnOff(on_off?"1":"0");
			ret=ret&&rt.update();
		}
		
		CacheKit.remove(CacheName.DICCACHE, "ris_triggers");
		return ret;
	}
	
//查询打印模板
	public List<Record> findPrinttemplate(String template_name){
		List<Record> ret=new ArrayList<Record>();
		List<Object> list = new ArrayList<Object>();
		String where = "";
		if(StringUtils.isNotBlank(template_name)) {
			where = " AND template_name = ?";
			list.add(template_name);
		}
		String sql = "SELECT *,(select item_name from dic_examitem where dic_examitem.id=printtemplate.examitem_id) as item_name FROM printtemplate WHERE 0=0" + where;
		ret = Db.find(sql, list.toArray());
		ret.stream().forEach(x->{
			x.set("temptype", PrintTemplateType.getDisplayName(x.get("type")));
		});
		return ret;
	}
	
	//保存打印模板
	public boolean savePrinttemplate(Printtemplate printtemplate, String examitem_ids){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			if(StringUtils.isNotBlank(examitem_ids)){
				List<Printtemplate> savelist=new ArrayList<Printtemplate>();
				String ids[]=examitem_ids.split(",");
				for(String id:ids){
					Printtemplate tmp = Printtemplate.dao.findFirst("select top 1 * from printtemplate where template_name=? and modality_type=? and examitem_id=?",
							printtemplate.getTemplateName(),printtemplate.getModalityType(),Integer.parseInt(id));
					if(tmp==null){
						tmp =new Printtemplate();
						tmp.setType(printtemplate.getType());
						tmp.setTemplateName(printtemplate.getTemplateName());
						tmp.setModalityType(printtemplate.getModalityType());
						tmp.setExamitemId(Integer.parseInt(id));;
						savelist.add(tmp.remove("id"));
					}
				}
				Db.batchSave(savelist, savelist.size());
			} else{
				ret = ret && Db.save("printtemplate", printtemplate.remove("id").toRecord());
			}
			return ret;
		});
	}
	
	//删除打印模板
	public boolean deletePrinttemplate(String ids) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret = true;
			String []idArr= ids.split(",");
			Object paraarr[][]=new Object[idArr.length ][1];
			for(int i=0,len=idArr.length;i<len;i++){
				paraarr[i][0]=idArr[i];
			}
			Db.batch("delete from printtemplate where id=?", paraarr, idArr.length);
			return ret;
		});
	}
	
	public List<Record> getExamitemByPrinttemplate(String modality_type,String itemname) {
		List<Object> paras=new ArrayList<>();
		paras.add(modality_type);
		String where="";
		if(StrKit.notBlank(itemname)){
			where+=" and dic_examitem.item_name like CONCAT('%',?,'%') ";
			paras.add(itemname);
		}
		String sql = "SELECT dic_examitem.*"
					+ " ,(SELECT treename_zh FROM dic_organ WHERE dic_organ.id = dic_examitem.organfk) AS organ"
					+ " ,(SELECT treename_zh FROM dic_organ WHERE dic_organ.id = dic_examitem.suborganfk) AS suborgan"
					+ " FROM dic_examitem "
					+ " WHERE dic_examitem.deleted = '0' AND dic_examitem.type = ? ";
		return  Db.find(sql+where, paras.toArray());
	}
	
	public List<Record> getReportCheckErrorRules(String keyword){
		List<Object> list=new ArrayList<Object>();
		String where = "";
		if(StringUtils.isNotBlank(keyword)){
			where += " and keyword like CONCAT('%',?,'%')";	
			list.add(keyword);
		}
		String sql = "select * from dic_reportcorrect_rules where 0=0" + where;
		return Db.find(sql,list.toArray());
	}
	
	public boolean saveReportCorrectRule(DicReportcorrectRules r) {
		boolean ret=true;
		if(r.getId()!=null) {
			ret=ret&r.update();
		} else {
			ret=ret&r.remove("id").save();
		}
		return ret;
	}
	

	
	/**
	 * 删除报告纠错规则
	 * @param id
	 * @return
	 */
	public boolean deleteCorrectRule(Integer id) {
		return DicReportcorrectRules.dao.deleteById(id);
	}
	
	public boolean checkKeyword(String keyword) {
		if(DicReportcorrectRules.dao.find("select * from report_errorcorrect_rules where keyword=?",keyword).size()!=0){
			return false;
		}else{
			return true;
		}
	}
	
	public List<Printtemplate> getPrintTemps(String type){
		return Printtemplate.dao.find("select * from printtemplate where type =? order by id",type);
	}
	
}
