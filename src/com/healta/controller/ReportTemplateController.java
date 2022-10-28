package com.healta.controller;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.model.Reporttemplate;
import com.healta.model.Reporttemplatenode;
import com.healta.model.User;
import com.healta.model.Xslttemplate;
import com.healta.service.ReportTemplateService;
import com.healta.util.MyFileUtils;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

/**
 * 报告模板相关控制层
 * 
 * @author Administrator
 *
 */
public class ReportTemplateController extends Controller {

	private final static Logger log = Logger.getLogger(ReportTemplateController.class);
	/**
	 * 业务层
	 */
	private static ReportTemplateService sv=new ReportTemplateService();
	
	/**
	 * 跳转页面
	 */
	public void index() {
		setAttr("id", getParaToInt("id"));
		setAttr("nodeid", getParaToInt("nodeid"));
		setAttr("studyid", getPara("studyid"));
		renderJsp("/view/front/template.jsp");
	}
			
	
	public void findReportTemplate(){
		
	}
	
	public void showTemplate() {
		renderJsp("/view/admin/template/edittemplate.jsp");
//		renderJsp("/view/front/report/edittemplate.jsp");
	}

	
	/**
	 * 跳转添加页面
	 */
	public void addTemplate() {
		setAttr("nodeid", getParaToInt("nodeid"));
		setAttr("orderid", getParaToInt("orderid"));
		setAttr("tempid", getParaToInt("tempid"));
		renderJsp("/view/front/report/edittemplate.jsp");
	}

	/**
	 * 保存模板
	 */
	public void saveTemplate() {
		try {
			Reporttemplate temp = getModel(Reporttemplate.class, "", true);
			User user = (User) getSession().getAttribute("user");
			temp.set("creator", user.getId());
			temp.set("creator_name", user.get("name"));
			temp.set("ispublic", getPara("ispublic"));
			temp.set("nodeid", getParaToInt("nodeid"));
			
			if (sv.saveReportTemplate(temp)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(1, "保存失败！"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	/**
	 * 使用默认模板
	 */
	public void useDefault() {
		setAttr("studyid", getPara("studyid"));
		setAttr("patientname", getPara("patientname"));
		renderJsp("/view/front/reportSR.jsp");
	}

	/**
	 * 获取模板
	 */
	public void getTemplate() {
		try {
			renderJson("data", sv.getTemplate(getParaToInt("nodeid")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	/**
	 * 删除模板
	 */
	public void delTemplate() {
		try {
			if (sv.delReportTemplate(getParaToInt("tempid"))) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(1, "删除失败！"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	/**
	 * 设备列表
	 */
	public void getModalitydic() {
		try {
			// renderJson("data", reportTemplateService.getModalitydic(this));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		renderJson();
	}
	/**
	 * 通过id查找模板
	 */
	public void  findTemplateById(){
//		try {
//			int id = getParaToInt("id");
//			renderJson("data", reportTemplateService.findTemplateById(id));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(1, e.getMessage()));
//		}
	}
	/**
	 * 修改模板
	 */
	public void updateTemplate() {
//		try {
//			Record record = new Record();
//			Reporttemplate temp = getModel(Reporttemplate.class, "", true);
//			User user = (User) getSession().getAttribute("user");
//			record.set("username", user.getUsername());
//			record.set("temp", temp);
//			record.set("id", getParaToInt("id"));
//			if (reportTemplateService.updateTemplate(record)) {
//				renderJson(ResultUtil.success());
//			}else {
//				renderJson(ResultUtil.fail(1, "修改失败"));
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(1, e.getMessage()));
//		}
	}
//	/**
//	 * 获取所有的设备类型
//	 */
//	public void getModalityType(){
//		List<Record> list = reportTemplateService.getModalityType();
//		renderJson(list);
//	}
	
	/**
	 * 获取模板信息
	 */
	public void getTemplateInfo(){
//			Record record = reportTemplateService.getTemplateInfo(getPara("templateId"));
//			renderJson(record);
	}
	
	
	/**
	 * 获取与当前模板类型相同的所有模板节点
	 */
	public void getTemplateNodeByModality() {
	    List<Record> nodes = new ArrayList<Record>();
        User user = (User)getSession().getAttribute("user");
        if (getPara("modality") != null) {
            if(StrKit.notBlank(getPara("id"))) {
                log.info("nodeid:" + getParaToInt("id") + ",type:" + getPara("type") + ",ispublic:" 
                        + getPara("ispublic") + ",creator:" + getPara("creator"));
                nodes = sv.getTemplateNodeByNodeid(getParaToInt("id"),getPara("type"),getPara("ispublic"),getPara("creator"),user);
            } else {
                log.info("modality:" + getPara("modality") + ",type:" + getPara("type") + ",ispublic:" 
                        + getPara("ispublic") + ",creator:" + getPara("creator"));
                nodes=sv.getTemplateNodeByModality_new(getPara("modality"),getPara("type"),getPara("ispublic"),getPara("creator"),user);
            }
        }
		renderJson(nodes);
	}
	
	/**
	 * 添加模板节点
	 */
	public void addTemplateNode() {
		try {
			
			User user = (User) getSession().getAttribute("user");
			Reporttemplatenode node = new Reporttemplatenode();
			node.set("nodename", getPara("nodename"));
			node.set("modality", getPara("modality"));
			node.set("parent", getParaToInt("parent"));
			node.set("creator", user.getId());
			node.set("ispublic", getPara("ispublic"));
			
			boolean result = sv.newTemplateNode(node);
			if(result) {
				Record newNode = new Record();
				newNode.set("id", node.getId());
				newNode.set("text", node.getNodename());
				newNode.set("checked", "false");
				newNode.set("state", "closed");
				Record attr = new Record();
				attr.set("parent", node.getParent());
				attr.set("modality", node.getModality());
				attr.set("type", "node");
				newNode.set("attributes", attr);
				renderJson(ResultUtil.success(newNode));
			} else {
				renderJson(ResultUtil.fail(-1,"添加子节点失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	/**
	 * 修改模板节点
	 */
	public void modifyTemplateNode() {
		try {
			Reporttemplatenode record = new Reporttemplatenode();
			record.setId(getParaToInt("nodeid"));
			record.set("nodename", getPara("nodename"));
			if (sv.modifyTemplateNode(record)) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1,"修改节点失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}
	
	/**
	 * 删除模板节点
	 */
	public void delTemplateNode() {
		try {
			if (sv.deleteTemplateNode(getParaToInt("nodeid"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1,"删除节点失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}
	
	public void getXsltTemplates(){
		renderJson(sv.getXsltTemplates(getPara("name"),getPara("viaversion")));
	}
	
	public void isExistXsltName(){
		try {
			
			String name=getPara("name");
			Integer id=getParaToInt("id");
			String belongreport=getPara("belongreport");
			String version=getPara("version");
//			if(name!=null){
//				name=name.substring(0, name.indexOf(".")-1);
//			}
			Xslttemplate xslttemplate=Xslttemplate.dao.getXsltByName(name,belongreport,version);
			
			boolean ret=false;
			if(id!=null){
				ret=xslttemplate!=null&&xslttemplate.getId()!=id?true:false;
			}
			else{
				ret=xslttemplate!=null?true:false;
			}
			
			renderJson(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveXslt(){
		try {
			User user = (User) getSession().getAttribute("user");
			Xslttemplate xslt=getModel(Xslttemplate.class, "", true);
			xslt.setCreator(user.getId());
			xslt.setCreatorName(user.getName());
			sv.saveXslt(xslt);
			renderJson(ResultUtil.success());
			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	public void uploadXslt(){
		try {
			UploadFile file=getFile("xslt_text","/xslt");
			UploadFile file_sr=getFile("xslt_sr","/xslt");
			Integer id=getParaToInt("id");
			Xslttemplate xslt=new Xslttemplate();
			xslt.setId(id);
			if(file!=null){
				xslt.setXslt(FileUtils.readFileToString(file.getFile(),"UTF-8"));
			}
			if(file_sr!=null){
				xslt.setXsltSr(FileUtils.readFileToString(file_sr.getFile(),"UTF-8"));
			}
			
			if(sv.uploadXslt(xslt)){
				if(file!=null){
					FileUtils.deleteQuietly(file.getFile());
				}
				if(file_sr!=null){
					FileUtils.deleteQuietly(file_sr.getFile());
				}
			}
			
			renderJson(ResultUtil.success());
			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void downloadXslt(){ 
		try {
			Integer id=getParaToInt("id");
			Xslttemplate xslt=Xslttemplate.dao.findById(id);
			User user = (User) getSession().getAttribute("user");
			
			ArrayList<String> files=new ArrayList<String>();
			if(xslt.getXslt()!=null){
				String filename=PropKit.use("system.properties").get("tempdir")+"\\xslttext_"+id+"_"+user.getUsername()+"_"+(new Date()).getTime()+".xslt";
				
				log.info("filename1="+filename);
				
				FileUtils.writeStringToFile(new File(filename), xslt.getXslt(), "UTF-8");
				files.add(filename);
			}
			
			if(xslt.getXsltSr()!=null){
				String filename=PropKit.use("system.properties").get("tempdir")+"\\xsltsr_"+id+"_"+user.getUsername()+"_"+(new Date()).getTime()+".xslt";
				log.info("filename2="+filename);
				FileUtils.writeStringToFile(new File(filename), xslt.getXsltSr(), "UTF-8");
				files.add(filename);
			}
			
			String zipfile=PropKit.use("system.properties").get("tempdir")+"\\xslt_"+id+"_"+user.getUsername()+"_"+(new Date()).getTime()+".zip";
			MyFileUtils.zipFiles(files, zipfile);
			
			renderFile(new File(zipfile));
			
//			FileUtils.deleteQuietly(new File(zipfile));
			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	public void delXsltTemplate() {
		try {
			if (sv.delXsltTemplate(getParaToInt("id"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1,"删除失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}
	
	 /**
     *  搜索报告模板
     */
    public void searchTemplate() {
        User user = (User) getSession().getAttribute("user");
        if (user != null) {
            renderJson(sv.searchTemplate(user.getId(), get("modality"),get("searchContent")));
        }
    }
    
    /**
     *  移动模板
     */
    public void moveNode() {
        User user = (User) getSession().getAttribute("user");
        if (user != null) {
            sv.moveNode(getInt("templateid"), getInt("nodeid"),get("ispublic"));
            renderJson();
        }
    }
    
    /**
     *  当个人模板或公共模板移动时，模板改为复制
     */
   public void copyNode() {
       User user = (User) getSession().getAttribute("user");
       if (user != null) {
           if(sv.copyNode(user, getInt("templateid"), getInt("nodeid"),get("ispublic"),getPara("templateName"))) {
        	   renderJson();
           }else {
        	   renderJson(ResultUtil.fail(-1,"模板名称已存在，添加失败!"));
           }
           
       }
   }
	
//********************************规避权限检查重复冲突********************************************
	
	//添加模板节点 
	public void addTemplateNode_wl() {
		try {
			
			User user = (User) getSession().getAttribute("user");
			Reporttemplatenode node = new Reporttemplatenode();
			node.set("nodename", getPara("nodename"));
			node.set("modality", getPara("modality"));
			node.set("parent", getParaToInt("parent"));
			node.set("creator", user.getId());
			node.set("ispublic", getPara("ispublic"));
			
			boolean result = sv.newTemplateNode(node);
			if(result) {
				Record newNode = new Record();
				newNode.set("id", node.getId());
				newNode.set("text", node.getNodename());
				newNode.set("checked", "false");
				newNode.set("state", "closed");
				Record attr = new Record();
				attr.set("parent", node.getParent());
				attr.set("modality", node.getModality());
				attr.set("type", "node");
				newNode.set("attributes", attr);
				renderJson(ResultUtil.success(newNode));
			} else {
				renderJson(ResultUtil.fail(-1,"添加子节点失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	//跳转添加页面
	public void addTemplate_wl() {
		setAttr("nodeid", getParaToInt("nodeid"));
		setAttr("reportid", getParaToInt("reportid"));
		setAttr("tempid", getParaToInt("tempid"));
		renderJsp("/view/front/report/edittemplate.jsp");
	}
	

	//修改模板节点
	public void modifyTemplateNode_wl() {
		try {
			Reporttemplatenode record = new Reporttemplatenode();
			record.setId(getParaToInt("nodeid"));
			record.set("nodename", getPara("nodename"));
			if (sv.modifyTemplateNode(record)) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1,"修改节点失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}
	

	//删除模板节点
	public void delTemplateNode_wl() {
		try {
			if (sv.deleteTemplateNode(getParaToInt("nodeid"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1,"删除节点失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}
	

	//删除模板
	public void delTemplate_wl() {
		try {
			if (sv.delReportTemplate(getParaToInt("tempid"))) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(1, "删除失败！"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	

	//保存模板
	public void saveTemplate_wl() {
		try {
			Reporttemplate temp = getModel(Reporttemplate.class, "", true);
			User user = (User) getSession().getAttribute("user");
			temp.set("creator", user.getId());
			temp.set("creator_name", user.get("name"));
			temp.set("ispublic", getPara("ispublic"));
			temp.set("nodeid", getParaToInt("nodeid"));
			
			Reporttemplate rp=null;
			if(temp.getId()!=null) {
				 rp=Reporttemplate.dao.findFirst("select * from Reporttemplate where name=? and id!=?",temp.getName(),temp.getId());
			}else {
				 rp=Reporttemplate.dao.findFirst("select * from Reporttemplate where name=?",temp.getName());
			}
			
			if(rp!=null) {
				renderJson(ResultUtil.fail(2, "该模板名称已存在，保存失败！"));
			}else {
				if (sv.saveReportTemplate(temp)) {
					renderJson(ResultUtil.success());
				}else {
					renderJson(ResultUtil.fail(1, "保存失败！"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	//获取模板
	public void getTemplate_wl() {
		try {
			renderJson("data", sv.getTemplate(getParaToInt("nodeid")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void reporttemplatepreview() {
		Reporttemplate reporttemplate = Reporttemplate.dao.findById(getParaToInt("id"));
		if(reporttemplate!=null) {
			setAttr("studymethod", reporttemplate.getStudymethod());
			setAttr("desccontent_html", reporttemplate.getDesccontentHtml());
			setAttr("resultcontent_html", reporttemplate.getResultcontentHtml());
		}
		
		renderJsp("/view/front/report/reporttemplatepreview.jsp");
	}

//********************************规避权限检查重复冲突********************************************
	
}
