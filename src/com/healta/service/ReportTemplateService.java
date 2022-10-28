package com.healta.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.healta.model.Mwlitem;
import com.healta.model.Patient;
import com.healta.model.Reporttemplate;
import com.healta.model.Reporttemplatenode;
import com.healta.model.Studyitem;
import com.healta.model.User;
import com.healta.model.Xslttemplate;
import com.healta.util.IDUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.template.stat.ast.If;

public class ReportTemplateService {
	
	private final static Logger log = Logger.getLogger(ReportTemplateService.class);
	
	/**
	 *  先获取根节点，在获取子节点
	 * @param modality  设备的类型
	 * @param type  判断模板的类型
	 * @param ispublic 判断模板是否为公共模板
	 * @param creator 判断是否需要筛选，非空即可
	 * @param user  获取用户的id
	 * @return
	 */
	public List<Record>  getTemplateNodeByModality_new(String modality,String type,String ispublic,String creator,User user) {
		List<Record> roots = Db.find("SELECT * FROM reporttemplatenode WHERE modality = ? and parent=0",modality);
		List<Record> nodes = new ArrayList<Record>();
		for (int i = 0; i < roots.size(); i++) {
			Record record = new Record();
			if (StringUtils.isBlank(ispublic)||StringUtils.equals(ispublic, "1")&&StringUtils.equals(roots.get(i).getStr("ispublic"), "1")) {
				record.set("id", roots.get(i).getInt("id"));
				record.set("text",roots.get(i).getStr("nodename"));
				record.set("parent", roots.get(i).getInt("parent"));
				record.set("type", "node");
				record.set("ispublic", roots.get(i).getStr("ispublic"));
				record.set("attributes", roots.get(i));
				record.set("state","open");
				record.set("children", getTemplateNodeByNodeid(roots.get(i).getInt("id"),type,ispublic,creator,user));
			}
			nodes.add(record);
		}
		return nodes;
	}
	
	/**
	 *  获取子节点和模板
	 * @param nodeid  节点的id
	 * @param type  判断模板的类型
	 * @param ispublic  判断模板是否为公共模板
	 * @param creator  判断是否需要筛选，非空即可
	 * @param user  获取用户的id
	 * @return
	 */
	public List<Record> getTemplateNodeByNodeid(int nodeid,String type,String ispublic,String creator,User user) {
		List<Record> ret=new ArrayList<Record>();
		// 获取根节点下的所有子节点
		List<Record> list=Db.find("select * from reporttemplatenode where parent=? order by ispublic desc,parent " , nodeid);
		// 将节点下的模板放入list
		list.addAll(Db.find("select * from reporttemplate where nodeid=" + nodeid));
		
		for (Record re:list) {
			Record record = new Record();
			record.set("id",re.getInt("id"));
			if (re.getInt("nodeid")==null) {
				//节点
				//需要筛选creator && 不是公共的 && !(创建人匹配||系统创建)
				if(StringUtils.isNotBlank(creator)&&StringUtils.equals(re.getStr("ispublic"), "0")
						&&!(re.getInt("creator").intValue()==user.getId().intValue()||re.getInt("creator").intValue()==0)) {
					continue;
				}
				record.set("text",re.getStr("nodename"));
				record.set("parent", re.getInt("parent"));
				record.set("type", "node");
				record.set("ispublic", re.getStr("ispublic"));
				record.set("attributes", re);
				record.set("state","closed");
			}else if(!StringUtils.equals("node", type)){
				//非节点
				//需要筛选creator&&不是公共的&&创建人不匹配
				if(StringUtils.isNotBlank(creator)&&StringUtils.equals(re.getStr("ispublic"), "0")
						&&user.getId().intValue()!=re.getInt("creator").intValue()) {
					continue;
				}
				record.set("text",re.getStr("name"));
				record.set("type", "template");
				record.set("ispublic", re.getStr("ispublic"));
				record.set("attributes", re);
				record.set("state","open");
			}
			
			if(StringUtils.isNotBlank(record.getStr("type"))&&
					(StringUtils.isBlank(ispublic)||StringUtils.equals(ispublic, "1")&&StringUtils.equals(record.getStr("ispublic"), "1"))) {
				ret.add(record);
			}
		}
		
		return ret;
	}
	
	
	public boolean newTemplateNode(Reporttemplatenode rtn){
		boolean ret=true;
		try{
			rtn.save();
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	public boolean modifyTemplateNode(Reporttemplatenode rtn){
		boolean ret=true;
		try{
			rtn.update();
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	//@Before(Tx.class)
	public boolean deleteTemplateNode(int id){
		//boolean ret=true;
		boolean succeed=Db.tx(() ->{
					boolean ret=true;
					List<Record> parent=new ArrayList<Record>();
					parent.add(Db.findFirst("SELECT * FROM reporttemplatenode WHERE id=?",id));

					List<Record> delnodes=new ArrayList<Record>();
					getChildNodes(parent,delnodes);
					
					for(Record record:delnodes){
						
						log.info("delete node="+record);
						Integer a = Db.update("delete from reporttemplate where nodeid=?",record.getInt("id"));
						Integer b = Db.update("delete from reporttemplatenode where id=?",record.getInt("id"));
						ret = ret&&a instanceof Integer&&b instanceof Integer;
//						ret=ret&&Reporttemplatenode.dao.deleteById(record.getInt("id"));
					}
					return ret;				      
			});
		
		return succeed;
	}
	

/**
 * 递归,查询到所有的子节点
 * @param classifys
 */
private void getChildNodes(List<Record> nodes,List<Record> delnodes) {
	Iterator<Record> node = nodes.iterator();
	while(node.hasNext()) {
		Record record = node.next();
		delnodes.add(record);
		int id = record.getInt("id");
		List<Record> childs = Db.find("SELECT * FROM reporttemplatenode WHERE parent = ?",id);
		if(childs.size() > 0) {
			getChildNodes(childs,delnodes);
		}
	}
}

	
	/**
	 * 获取模板
	 */
	public List<Reporttemplate> getTemplate(int nodeid) {
		// controller.setAttr("success", "success");
		return Reporttemplate.dao.find("select * from reporttemplate where nodeid=" + nodeid);
	}
	
	
	public boolean saveReportTemplate(Reporttemplate rt){
		boolean ret=true;
		try{
			if(rt.getId()==null||rt.getId()==0){
				rt.remove("id").save();
			}
			else{
				rt.update();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
	public boolean delReportTemplate(int id){
		boolean ret=true;
		try{
			Reporttemplate.dao.deleteById(id);
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
	
	public void saveXslt(Xslttemplate xslt){
//		boolean ret=true;
//		try{
//			Xslttemplate tmp=xslt.getXsltByName(xslt.getName());
			if(xslt.getId()!=null){
//				xslt.setId(tmp.getId());
//				xslt.setCreatetime(tmp.getCreatetime());
				xslt.update();
			}
			else{
				xslt.remove("id").save();
			}
//		}
//		catch(Exception ex){
//			ex.printStackTrace();
//			ret=false;
//		}
//		return ret;
	}
	
	
	public boolean uploadXslt(Xslttemplate xslt){
		boolean ret=true;
		try{

			xslt.update();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
	
	public List<Xslttemplate> getXsltTemplates(String name,String viaversion){

		String sql="select * from xslttemplate ";
		String where="";
		
		where+=StrKit.notBlank(name)?" (name like '%"+name+"%' or displayname like '%"+name+"%') ":"";
		
		if(StrKit.notBlank(viaversion)){
			where+=StrKit.notBlank(where)?" and ":"";
			where+=" viaversion='"+viaversion+"' ";
		}
		where=StrKit.notBlank(where)?" where "+where:where;
		return Xslttemplate.dao.find(sql+where);
	}
	
	
	public boolean delXsltTemplate(int id){
		boolean ret=true;
		try{
			Xslttemplate.dao.deleteById(id);
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
	/**
	 * 搜索报告模板
	 * @param userid  登入用户的id
	 * @param modality 设备
	 * @param searchContent  搜索的内容
	 */
	public List<Record> searchTemplate(Integer userid, String modality, String searchContent) {
	    log.info("用户的id：" + userid + ",设备的类型：" + modality + ",搜索的内容：" + searchContent);
	    List<Record> res = new ArrayList<Record>();
	    // 个人
        List<Record> records = Db.find("select reporttemplate.* from reporttemplatenode,reporttemplate where reporttemplatenode.id = reporttemplate.nodeid "
              + " and reporttemplate.creator = ? and reporttemplatenode.modality = ? and reporttemplate.ispublic = '0' "
              + " and reporttemplate.name like '%"+ searchContent +"%' ", userid, modality);
        // 公共
        List<Record> records1 = Db.find("select reporttemplate.* from reporttemplatenode,reporttemplate where reporttemplatenode.id = reporttemplate.nodeid "
                + " and reporttemplatenode.modality = ? and reporttemplate.ispublic = '1' "
                + " and reporttemplate.name like '%"+ searchContent +"%' ", modality);
        records.addAll(records1);
        if (CollectionUtils.isNotEmpty(records)) {
            log.info("符合搜索条件的内容有：" + records.size() + "条数据");
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                record.set("id", record.getInt("id"));
                record.set("text",record.getStr("name"));
                record.set("type", "template");
                record.set("ispublic", record.getStr("ispublic"));
                record.set("attributes", record);
                record.set("state","open");
                res.add(record);
            }
        }
        return res;
    }
	
	/**
	 *  移动模板
	 * @param templateid  模板的id
	 * @param nodeid    节点的id
	 * @param ispublic   是否为公共模板（1是公共模板；0是非公共模板）
	 */
	public void moveNode(Integer templateid, Integer nodeid, String ispublic) {
	    log.info("templateid:" + templateid + ",nodeid:" + nodeid + ",ispublic：" + ispublic);
	    Reporttemplate reporttemplate = Reporttemplate.dao.findById(templateid);
	    if (reporttemplate != null) {
	        reporttemplate.setNodeid(nodeid);
	        reporttemplate.setIspublic(ispublic);
	        reporttemplate.update();
        }
    }
	
	/**
	 * 当个人模板或公共模板移动时，模板改为复制
	 * @param user  登入的用户
	 * @param templateid  模板的id
	 * @param nodeid  节点的id
	 * @param ispublic  是否为公共模板（1是公共模板；0是非公共模板）
	 */
	public boolean copyNode(User user, Integer templateid, Integer nodeid, String ispublic,String templateName) {
	    boolean flag=true;
		//检查模板名称是否重复
		String sql="";
		if("1".equals(ispublic)) {
			sql="SELECT * FROM Reporttemplate where name='"+templateName+"' and ispublic=1";
		}else if("0".equals(ispublic)) {
			sql="SELECT * FROM Reporttemplate where name='"+templateName+"' and creator="+user.getId()+" and ispublic=0";
		}
		List<Record> list=Db.find(sql);
		if(list.size()>0) {
			flag=false;
			return flag;
		}
		
		//搜索出移动的模版
	    Reporttemplate reporttemplate = Reporttemplate.dao.findById(templateid);
	    Reporttemplate reporttemplatenew = new Reporttemplate();
	    reporttemplatenew = reporttemplate;
	    reporttemplatenew.setNodeid(nodeid);
	    reporttemplatenew.setIspublic(ispublic);
	    reporttemplatenew.setCreator(user.getId());
	    reporttemplatenew.setCreatorName(user.getName());
	    reporttemplatenew.setCreatetime(new Date());
	    reporttemplatenew.remove("id").save();
	    return flag;
    }
	
}
