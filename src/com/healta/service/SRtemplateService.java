package com.healta.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.healta.model.ClinicalCode;
import com.healta.model.DicRegex;
import com.healta.model.SRComponent;
import com.healta.model.SRTemplate;
import com.healta.model.Srcomponentoption;
import com.healta.model.Srsection;
import com.healta.model.User;
import com.healta.util.SerializeRes;
import com.jfinal.aop.Before;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class SRtemplateService {
	
	
	public int newSRtemplate(SRTemplate st,SerializeRes res){
		if(st.dao.queryByName(st.getName()) != null) {
			throw new RuntimeException(res.get("admin.templatenameexist"));
		}
		st.save();
		return st.getId();
	}
	
	public int newSRSection(Srsection ss,SerializeRes res){
		if(ss.dao.queryByName(ss.getName()) != null) {
			throw new RuntimeException(res.get("admin.sectionnameexist"));
		}
		ss.save();
		return ss.getId();
	}
	
	public boolean updateSRTemplate(SRTemplate st,SerializeRes res){
		if(st.dao.queryByNameAndNotEquSelf(st.getName(),st.getId()) != null) {
			throw new RuntimeException(res.get("admin.templatenameexist"));
		}
		
		return st.update();
	}
	
	public boolean updateSRSection(Srsection ss,SerializeRes res){
		if(ss.dao.queryByNameAndNotEquSelf(ss.getName(),ss.getId()) != null) {
			throw new RuntimeException(res.get("admin.sectionnameexist"));
		}
		
		return ss.update();
	}
	
	public List<Record> findSRTemplate(String name,boolean withContent){
		List<Record> ret=null;
		String columns=SRTemplate.dao.toSelectStr(withContent?"":"templatecontent");
		if(StrKit.notBlank(name)){
			ret = Db.find("select top 200 "+columns+" from srtemplate where name like ?",name+"%");
		}
		else{
			ret = Db.find("select top 200 "+columns+" from srtemplate");
		}
		ret.stream().forEach(x->{
			try {
				x.set("srtemplatename", Base64.encodeBase64String(x.getStr("name").getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return ret;
		
	}
	
	public List<Srsection> findSRSections(String name,Integer sectionid,boolean withContent){
		String columns=Srsection.dao.toSelectStr(withContent?"":"sectioncontent");
		String sql="select top 500 "+columns+" from srsection where 1=1 ";
		List<Object> paras=new ArrayList<Object>();
		if(StrKit.notBlank(name)){
			sql+=" and name like ?";
			paras.add(name+"%");
		}
		if(sectionid!=null){
			sql+=" and id!=?";
			paras.add(sectionid);
		}
		return Srsection.dao.find(sql,paras.toArray());
	}
	
public List<Srsection> findSRSections_NoContent(String name,Integer sectionid){
		
		String sql="select id,uid,name,is_qc,clone,creator,creatorname,createtime from srsection where 1=1 ";
		List<Object> paras=new ArrayList<Object>();
		if(StrKit.notBlank(name)){
			sql+=" and name like ?";
			paras.add(name+"%");
		}
		if(sectionid!=null){
			sql+=" and id!=?";
			paras.add(sectionid);
		}
		return Srsection.dao.find(sql,paras.toArray());
	}
	
	public SRTemplate findSRTemplateByName(String name){
		return SRTemplate.dao.findFirst("select * from srtemplate where name = ?",name);
	}
	
	public Srsection findSRSectionByName(String name){
		return Srsection.dao.findFirst("select * from srsection where name = ?",name);
	}
	
	public Srsection findSRSectionByNameOrUid(String name,String uid){
		return Srsection.dao.findFirst("select * from srsection where name = ? or uid=?",name,uid);
	}
	
	public List<SRTemplate> findSRTemplateByIds(String ids){
		return SRTemplate.dao.find("select * from srtemplate where id in ("+ids+")");
	}
	
	public List<Srsection> findSRSectionByIdsOrUids(String ids,String uids){
		String sql="select * from srsection";
		String where="";
		if(StrKit.notBlank(ids)){
			where+=" id in("+ids+") ";
		}
		if(StrKit.notBlank(uids)){
			if(StrKit.notBlank(where)){
				where+=" or ";
			}
			where+=" uid in("+uids+") ";
		}
		return Srsection.dao.find(sql+(StrKit.notBlank(where)?" where "+where:" where 1=0"));
	}
	
	public List<Record> findSRComponent(String name){
		
		String sql="select * from srcomponent";
		
		if(StrKit.notBlank(name)){
			sql+=" where name like '"+name+"%'";
		}
		
		List<Record> list =Db.find(sql);
		for(Record re:list){
			switch(re.getInt("type")){
				case 0:{
					re.set("group", "数字输入框");
					break;
				}
				case 1:{
					re.set("group", "选择框");
					break;
				}
				case 2:{
					re.set("group", "多行输入框");
					break;
				}
				case 3:{
					re.set("group", "单行输入框");
					break;
				}
				case 4:{
					re.set("group", "日期时间输入框");
					break;
				}
				case 5:{
					re.set("group", "复选框");
					break;
				}
				case 6:{
					re.set("group", "单选框");
					break;
				}
			}
		}
		
		return list;
	}
	
	
	public List<Record> findSRTemplateAndComponent(String name){
		List<Record> ret=new ArrayList<Record>();
		String sql="";
		String sql1="";
		String sql2="";
		if(name!=null&&!"".equals(name)){
			sql="select top 1000 * from srtemplate where name like '"+name+"%'";
			sql1="select top 1000 * from srcomponent where name like '"+name+"%'";
			sql2="select top 1000 * from srsection where name like '"+name+"%'";
		}
		else{
			sql="select top 1000 * from srtemplate";
			sql1="select top 1000 * from srcomponent";
			sql2="select top 1000 * from srsection";
		}
		List<SRTemplate> templates=SRTemplate.dao.find(sql);
		
		for(SRTemplate template:templates){
			Record record=new Record();
			record.set("id", template.get("id"));
			record.set("name", template.get("name"));
			record.set("creatorname", template.get("creatorname"));
			record.set("createtime", template.get("createtime"));
			record.set("group", "结构化模板");
			record.set("groupid", "srtemplate");
			ret.add(record);
		}
		
		List<Srsection> sections=Srsection.dao.find(sql2);
		
		for(Srsection section:sections){
			Record record=new Record();
			record.set("id", section.get("id"));
			record.set("name", section.get("name"));
			record.set("creatorname", section.get("creatorname"));
			record.set("createtime", section.get("createtime"));
			record.set("group", "结构化章节");
			record.set("groupid", "srsection");
			ret.add(record);
		}
		
		List<SRComponent> components=SRComponent.dao.find(sql1);
		
		for(SRComponent component:components){
			Record record=new Record();
			record.set("id", component.get("id"));
			record.set("name", component.get("name"));
			record.set("creatorname", component.get("creatorname"));
			record.set("createtime", component.get("createtime"));
			record.set("group", "结构化组件");
			record.set("groupid", "srcomponent");
			ret.add(record);
		}
		return ret;
	}
	
	
	public boolean delSRTemplateAndComponent(String tempids,String compids,String secids){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			try{
				if(StrKit.notBlank(tempids)){
					String ids[]=tempids.split(",");
					for(String id:ids){
						SRTemplate.dao.deleteById(id);
					}
				}
				if(StrKit.notBlank(compids)){
					String ids[]=compids.split(",");
					for(String id:ids){
						SRComponent.dao.deleteById(id);
						Db.delete("delete from srcomponentoption where component_id=?", id);
					}
				}
				if(StrKit.notBlank(secids)) {
					String ids[]=secids.split(",");
					for(String id:ids){
						Srsection.dao.deleteById(id);
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
				ret=false;
			}
			return ret;
		});
	}
	
	public List<DicRegex> findRegex(){
		return DicRegex.dao.find("select * from regex");
	}
	
	
	public List<ClinicalCode> findClinicalCode(String scheme,String meaning){
		
		String sql="select top 200 * from clinicalcode where 1=1";
		if(scheme!=null&&!"".equals(scheme)){
			
			if(scheme.indexOf(",")>=0){
				String schemes="";
				for(String sc:scheme.split(",")){
					schemes+="'"+sc+"',";
				}
				sql+=" and scheme in ("+schemes.substring(0, schemes.length()-1)+")";
			}
			else{
				sql+=" and scheme='"+scheme+"'";
			}
		}
		if(meaning!=null&&!"".equals(meaning)){
			sql+=" and (meaning like '%"+meaning+"%' or code like '%"+meaning+"%')";
		}
		
		return ClinicalCode.dao.find(sql);
	}
	
	public SRComponent findSRComponentByName(String name){
		return SRComponent.dao.findFirst("select top 1 * from srcomponent where name=?",name);
	}
	
	public SRComponent findSRComponentByNameOrUid(String name,String uid){
		return SRComponent.dao.findFirst("select top 1 * from srcomponent where name=? or uid=?",name,uid);
	}
	
	public List findSRComponentByIdOrUid(String ids,String uids){
		List ret=new ArrayList();
		
		String sql1="select * from srcomponent ";
		String where1="";
		
		if(StrKit.notBlank(ids)){
			where1+=StrKit.notBlank(where1)?" or ":"";
			where1+=" id in("+ids+")";
		}
		
		if(StrKit.notBlank(uids)){
			where1+=StrKit.notBlank(where1)?" or ":"";
			where1+=" uid in("+uids+")";
		}
		where1=StrKit.notBlank(where1)?" where ("+where1+")":where1;
		
		List<SRComponent> list1=SRComponent.dao.find(sql1+where1);
		
		ret.add(list1);
		List<Srcomponentoption> list2=Srcomponentoption.dao.find("select * from srcomponentoption where exists "
				+ "(select null from srcomponent "+where1+" and srcomponentoption.component_id=srcomponent.id)");
		
		HashMap<String, ArrayList<Srcomponentoption>> map=new HashMap<String, ArrayList<Srcomponentoption>>();
		for(Srcomponentoption op:list2){
			ArrayList<Srcomponentoption> list3=map.get(op.getComponentId()+"");
			if(list3==null){
				list3=new ArrayList<Srcomponentoption>();
				map.put(op.getComponentId()+"", list3);
			}
			list3.add(op);
		}
		ret.add(map);
		return ret;
	}

	public SRComponent saveComponent_Input(SRComponent src,User user){
		src.setStandardcode(src.getCode());
		src.setCreator(user.getId());
		src.setCreatorname(user.getName());
		
		SRComponent tmp=null;
		if(StrKit.notBlank(src.getUid())){
			tmp=SRComponent.dao.getSRComponentByUID(src.getUid());
		}
		if(tmp!=null){
			String html=src.getHtml();
			html=html.replaceAll("@id@", src.getUid());
			html=html.replaceAll("@uid@", src.getUid());
			src.setHtml(html);
			src.setId(tmp.getId());
			src.update();
		} else{
			src.setUid(UUID.randomUUID().toString());
			String html=src.getHtml();
			html=html.replaceAll("@id@", src.getUid());
			html=html.replaceAll("@uid@", src.getUid());
			src.setHtml(html);
			src.remove("id").save();
		}
		return src;
	}
	
	public SRComponent saveComponent_Select(SRComponent src,String select_option,User user){
		src.setStandardcode(src.getCode());
		src.setCreator(user.getId());
		src.setCreatorname(user.getName());
		SRComponent tmp=SRComponent.dao.getSRComponentByUID(src.getUid());
		if(tmp!=null){
			src.setId(tmp.getId());
			String html=src.getHtml();
			html=html.replaceFirst("@id@", src.getUid());
			html=html.replaceFirst("@uid@", src.getUid());
			src.setHtml(html);
			src.update();
		}
		else{
			src.setUid(UUID.randomUUID().toString());
			src.remove("id").save();
			String html=src.getHtml();
			html=html.replaceFirst("@id@", src.getUid());
			html=html.replaceFirst("@uid@", src.getUid());
			src.setHtml(html);
			src.update();
		}
		
		
		Db.delete("delete from srcomponentoption where component_id=?", src.getId());
		
		
		if(StrKit.notBlank(select_option)){
			
			List<JSONObject> list=JSON.parseArray(select_option,JSONObject.class);
			for(int i=0;i<list.size();i++){
				JSONObject obj=list.get(i);
				Srcomponentoption op=new Srcomponentoption();
				op.setUid(UUID.randomUUID().toString());
				op.setCode(obj.getString("code"));
				op.setValue(obj.getString("value"));
				op.setDisplayname(obj.getString("displayname"));
				op.setComponentId(src.getId());
				op.setComponentUid(src.getUid());
				op.setStandardCode(obj.getString("code"));
				op.setOpindex(i);
				op.setDefaultoption(obj.getString("defaultoption"));
				op.setSectionuid(obj.getString("sectionuid"));
				op.setMutex(obj.getString("mutex"));
				op.setColor(obj.getString("color"));
				op.save();
			}
		}
		return src;
	}
	
	public List<Srcomponentoption> findComponentSelectOption(String uid){
		return Srcomponentoption.dao.find("select *,(select name from srsection where srsection.uid=srcomponentoption.sectionuid) as sectionname from srcomponentoption where exists (select null from srcomponent where srcomponent.uid=srcomponentoption.component_uid and srcomponent.uid=?) order by opindex", uid);
	}
	
	
//	/**
//	 * 联想查询国际标准码
//	 */
//	public List<ClinicalCode> findClinicalCode(String name) {
//		String sql = "select * from clinicalcode where meaning like '%' ? '%' or code like '%' ? '%' limit 100";
//		return ClinicalCode.dao.find(sql,name,name);
//	}
	
	/**
	 * 联想查询国际标准码
	 */
	public ClinicalCode findClinicalCodeByCode(String code) {
		String sql = "select * from clinicalcode where code = ?";
		return ClinicalCode.dao.findFirst(sql, code);
	}
	
	public ClinicalCode addPrivateCode(ClinicalCode cc){
		
		cc.save();
	
		return cc;
	}
	
	public void insertSRComponent(SRComponent src,List<Srcomponentoption> opts){
		Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			SRComponent sc=findSRComponentByNameOrUid(src.getName(),src.getUid());
			if(sc==null){
				ret=ret&src.save();
			} else{
				src.setId(sc.getId());
				ret=ret&src.update();
				Db.delete("delete from srcomponentoption where component_id=?", src.getId());
			}
			for(Srcomponentoption op:opts){
				op.setComponentId(src.getId());
				ret=ret&op.save();
			}
			return ret;
		});
	}
	
	@Before(Tx.class)
	public void insertSRTemplate(List<SRTemplate> srts){

		for(SRTemplate srt:srts){
			SRTemplate srTemplate=findSRTemplateByName(srt.getName());
			if(srTemplate==null){
				srt.save();
			}
			else {
				srt.setId(srTemplate.getId());
				srt.update();
			}
		}
		
	}
	
	public void insertSRSection(List<Srsection> secs){

		for(Srsection sec:secs){
			Srsection srsection=findSRSectionByNameOrUid(sec.getName(),sec.getUid());
			if(srsection==null){
				sec.save();
			}
			else {
				sec.setId(srsection.getId());
				sec.update();
			}
		}
		
	}
	public List<Srsection> getSRSectionByUids(String sectionuids){
		List<Srsection> ret=new ArrayList<Srsection>();
		if(StrKit.notBlank(sectionuids)){
			if(sectionuids.endsWith(",")){
				sectionuids=sectionuids.substring(0, sectionuids.length()-1);
			}
			List<String> paras=new ArrayList<String>();
			Arrays.asList(sectionuids.split(",")).stream().forEach(x->paras.add("'"+x+"'"));
			ret=Srsection.dao.find("select * from srsection where uid in("+StringUtils.join(paras, ",")+")");
		}
		return ret;
	}
	
	
//	public void upOption(int index, int component_id){
//		Db.update("update srcomponentoption set opindex=? where component_id=? and opindex=?");
//		
//	}
	
	public List<Record> getAnatomycharts(HttpServletRequest req){
		List<Record> ret=new ArrayList<>();
		File dir = new File(req.getServletContext().getRealPath("image/anatomychart"));
		if(dir.isDirectory()){
			String[] filenames=dir.list();
			for(int i=0,len=filenames.length;i<len;i++){
				Record re = new Record();
				re.set("id", i);
				re.set("name", filenames[i]);
				ret.add(re);
			}
		}
		return ret;
	}

}
