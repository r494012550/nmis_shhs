package com.healta.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.healta.constant.TableNameConstant;
import com.healta.model.ClinicalCode;
import com.healta.model.DicRegex;
import com.healta.model.SRComponent;
import com.healta.model.SRTemplate;
import com.healta.model.Srcomponentoption;
import com.healta.model.Srsection;
import com.healta.model.User;
import com.healta.model.Xslttemplate;
import com.healta.service.SRtemplateService;
import com.healta.util.MyFileUtils;
import com.healta.util.ResultUtil;
import com.healta.util.SerializeRes;
import com.itextpdf.html2pdf.jsoup.Jsoup;
import com.itextpdf.html2pdf.jsoup.select.Elements;
import com.jfinal.core.Controller;
import com.jfinal.i18n.Res;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

public class SRTemplateController extends Controller {
	
	private final static Logger log = Logger.getLogger(SRTemplateController.class);
	private static SRtemplateService sv=new SRtemplateService();

	/**
	 * 跳转视图
	 */
	public void index() {
		renderJsp("/view/admin/templetmanage.jsp");
	}

	/**
	 * 使用结构化模板
	 */
	public void useStruct() {
		setAttr("studyid", getPara("studyid"));
		setAttr("patientname", getPara("patientname"));
		renderJsp("/view/front/structReport.jsp");
	}

	/**
	 * 获取树
	 */
	public void getTree() {
		String path = getRequest().getServletContext().getRealPath("upload/report/");
//		renderJson(service.queryAll(getPara("name"), path));
	}
	
	public void findSRTemplate(){
		String tempname=getPara("name");
		renderJson(sv.findSRTemplate(tempname,getBoolean("withContent", true)));
	}
	
	public void getSRTemplateById(){
		renderJson(SRTemplate.dao.findById(getInt("id")));
	}
	
	public void findSRSections(){
		String name=getPara("name");
		Integer sectionid=getParaToInt("sectionid");
		renderJson(sv.findSRSections(name,sectionid,getBoolean("withContent", true)));
	}
	
	public void getSRSectionById(){
		renderJson(Srsection.dao.findById(getInt("id")));
	}
	
	public void findSRSections_NoContent(){
		String name=getPara("name");
		Integer sectionid=getParaToInt("sectionid");
		renderJson(sv.findSRSections_NoContent(name,sectionid));
	}
	
	public void findSRComponent(){
		String name=getPara("name");
//		System.out.println("name="+name);
		renderJson(sv.findSRComponent(name));
	}
	
	
	public void findSRComponentByUid(){
		String uid=getPara("uid");
//		System.out.println("name="+name);
		renderJson(SRComponent.dao.findFirst("select * from srcomponent where uid=?",uid));
	}
	
	
	public void findSRTemplateAndComponent(){
		String name=getPara("name");
		renderJson(sv.findSRTemplateAndComponent(name));
	}
	
	public void delSRTemplateAndComponent(){
		try {
			if (sv.delSRTemplateAndComponent(getPara("tempids"), getPara("compids"),get("secids"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "删除失败"));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	

	/**
	 * 删除模板
	 */
	public void delete() {
		try {
//			if (service.delete(getParaToInt("id"))) {
//				renderJson(ResultUtil.success());
//			} else {
//				renderJson(ResultUtil.fail(-1, "删除章节失败"));
//			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 * 更新模板
	 */
	public void updateSRtemplate() {
		SRTemplate templet = getModel(SRTemplate.class,"", true);
		SerializeRes res=(SerializeRes)getSessionAttr("locale");
//		templet.set("id", getParaToInt("id"));
//		templet.set("name", getPara("name"));
//		templet.set("content", getPara("content"));
//		templet.set("maprule", getPara("maprule"));
//		templet.
		
		String content=templet.getTemplatecontent();
		content=content.replaceAll(getRequest().getContextPath()+"/image/image/", "image/image/");
		templet.setTemplatecontent(content);
		
		try {
			if (sv.updateSRTemplate(templet,res)) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "模板保存失败!"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	public void updateSRSection() {
		Srsection sec = getModel(Srsection.class,"", true);
		SerializeRes res=(SerializeRes)getSessionAttr("locale");
		String content=sec.getSectioncontent();
		content=content.replaceAll(getRequest().getContextPath()+"/image/image/", "image/image/");
		sec.setSectioncontent(content);
		
		try {
			if (sv.updateSRSection(sec,res)) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "章节保存失败!"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	/**
	 * 保存模板
	 */
	public void newSRtemplate() {
		SRTemplate templet = getModel(SRTemplate.class,"", true);
		SerializeRes res=(SerializeRes)getSessionAttr("locale");
//		templet.set("name", getPara("name"));
//		templet.set("content", getPara("content"));
//		templet.set("maprule", getPara("maprule"));
		String content=templet.getTemplatecontent();
		content=content.replaceAll(getRequest().getContextPath()+"/image/image/", "image/image/");
		templet.setTemplatecontent(content);
		User user = (User) getSession().getAttribute("user");
		templet.set("creator", user.getId());
		templet.set("creatorname", user.getName());
		
		try {
			int id =sv.newSRtemplate(templet,res);
			if (templet.getId()!=null) {
				renderJson("id",id);
			} else {
				renderJson(ResultUtil.fail(-1, "添加模板失败！"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void newSRSection() {
		Srsection sec = getModel(Srsection.class,"", true);
		SerializeRes res=(SerializeRes)getSessionAttr("locale");
		String content=sec.getSectioncontent();
		content=content.replaceAll(getRequest().getContextPath()+"/image/image/", "image/image/");
		sec.setSectioncontent(content);
		User user = (User) getSession().getAttribute("user");
		sec.set("creator", user.getId());
		sec.set("creatorname", user.getName());
		sec.setUid(StrKit.getRandomUUID());
		try {
			int id =sv.newSRSection(sec,res);
			if (sec.getId()!=null) {
				renderJson("id",id);
			} else {
				renderJson(ResultUtil.fail(-1, "添加章节失败！"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 * 上传模板
	 */
	public void upload() {
		UploadFile file = getFile("file", "/report");
		String targetName = getPara("id") + ".jasper";
		if (!file.getFileName().equals(targetName)) {
			String path = getRequest().getServletContext().getRealPath("upload/report/");
			File src = new File(path + "/" + targetName);
			File upload = file.getFile();
			if (src.exists()) {
				src.delete();
			}
			upload.renameTo(src);
		}
		renderJson(ResultUtil.success());
	}

	/**
	 * 返回上传文件页面
	 */
	public void uploadJsp() {
		renderJsp("/view/admin/upload.jsp");
	}
	
	/**
	 * 获取所有模板
	 */
	public void allgetTemplete(){
		
		try {
//			List<Record> allTemplete =  service.allgetTemplete();
//			renderJson("data", allTemplete);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	} 
	/**
	 * 获取结构化模板信息
	 */
	public void getTemplateInfo(){
//		renderJson(service.queryStructTemplateInfo(getPara("keyWord")));
	}
//查询结构化模板名称是否被占用
	public void findStructTempName(){
//		boolean b = service.findStructTempName(getPara("name"));
//		if(!b){
//			renderJson(ResultUtil.success());
//		}else{
//			renderJson(ResultUtil.fail(1, "模板名称已存在！"));
//		}
		
	}
	//查询结构化模板映射规则是否被占用
	public void findStructTempMaprule(){
//		boolean b = service.findStructTempMaprule(getPara("maprule"));
//		if(!b){
//			renderJson(ResultUtil.success());
//		}else{
//			renderJson(ResultUtil.fail(1, "模板映射已存在！"));
//		}
		
	}
	//删除结构化模板
	public void delStructTemp(){
//		boolean b = service.delStructTemp(getParaToInt("id"));
//		if(b){
//			renderJson(ResultUtil.success());
//		}else{
//			renderJson(ResultUtil.fail(1, "模板删除失败！"));
//		}
	}
		
	//删除结构化模板
	public void findTempName(){
		try {
			String name = getPara("name");
//			 boolean flg = service.findTempName(name);
//			 if(flg){
//				 renderJson(ResultUtil.success());
//			 }else{
//				 renderJson(ResultUtil.fail(1, "失败"));
//			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void openSRTemplateList(){
		renderJsp("/view/admin/template/srtemplatelist.jsp");
	}
	
	public void gotoSRSectionList(){
		setAttr("sectionid", getPara("sectionid"));
		renderJsp("/view/admin/template/srsectionlist.jsp");
	}
	
	public void goSrtemplate_Right(){
		setAttr("showsection", getBoolean("showsection",true));
		renderJsp("/view/admin/template/srtemplate_right.jsp");
	}
	
	public void goSrsection_Right(){
		renderJsp("/view/admin/template/srsection_right.jsp");
	}
	
	public void openSaveSRTemplateDialog(){
		
		if(StringUtils.isNotEmpty(getPara("id"))){
			SRTemplate temp=SRTemplate.dao.findById(getPara("id"));
//			setAttr("name", temp.getName());
//			setAttr("maprule", temp.getMaprule());
//			setAttr("enablefilter", temp.getEnablefilter());
//			setAttr("filter_width", temp.getFilterWidth());
			setAttr("template", temp);
		}
		renderJsp("/view/admin/template/savesrtemplate.jsp");
	}
	
	public void openSaveSRSectionDialog(){
		if(StringUtils.isNotEmpty(getPara("id"))){
			Srsection temp=Srsection.dao.findById(getPara("id"));
			setAttr("sec", temp);
		}
		renderJsp("/view/admin/template/savesrsection.jsp");
	}
	
	public void findRegex(){
		renderJson(sv.findRegex());
	}
	
	public void findClinicalCode(){
		renderJson(sv.findClinicalCode(getPara("scheme"),getPara("meaning")));
	}
	
	public void checkComponentName(){
		try {
			String uid=getPara("uid");
			SRComponent src=sv.findSRComponentByName(getPara("name"));
			if(src!=null&&!StrKit.equals(uid, src.getUid())){
				renderJson(ResultUtil.success(src));
			}
			else{
				renderJson(ResultUtil.success());
			}
			
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
//	public void saveComponent_Textarea(){
//		try {
//			User user=(User)getSession().getAttribute("user");
//			SRComponent src=getModel(SRComponent.class, "", true);
//			renderJson(ResultUtil.success(sv.saveComponent_Text(src, user)));
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(1, e.getMessage()));
//		}
//	}
	
	public void saveComponent_Input(){
		try {
			User user=(User)getSession().getAttribute("user");
			SRComponent src=getModel(SRComponent.class, "", true);
			renderJson(ResultUtil.success(sv.saveComponent_Input(src, user)));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void saveComponent_Select(){
		try {
			User user=(User)getSession().getAttribute("user");
			SRComponent src=getModel(SRComponent.class, "", true);
			
			String options=getPara("select_option");
			renderJson(ResultUtil.success(sv.saveComponent_Select(src, options, user)));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void findComponentSelectOption(){
//		try {
		
		if(StringUtils.isNotEmpty(getPara("uid"))){
			renderJson(sv.findComponentSelectOption(getPara("uid")));
		}
		else{
			renderNull();
		}
		
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(1, e.getMessage()));
//		}
	}
	
	
	public void addPrivateCode(){
		try {
			ClinicalCode cc=new ClinicalCode();
			cc.setScheme(getPara("scheme"));
			cc.setCode(getPara("code"));
			cc.setMeaning(getPara("meaning"));
			
			if(sv.findClinicalCodeByCode(cc.getCode())!=null){
				renderJson(ResultUtil.fail(1, "hasexist"));
			}
			else{
				sv.addPrivateCode(cc);
				renderJson(ResultUtil.success(cc));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void goTextComponent(){
		String idstr=getPara("id");
		if(StringUtils.isNotEmpty(idstr)){
			Integer id=Integer.valueOf(idstr);
			SRComponent src=SRComponent.dao.findById(id);
			setAttr("id", idstr);
			setAttr("uid", src.getUid());
			setAttr("name", src.getName());
			setAttr("code", src.getCode());
			setAttr("unit", src.getUnit());
			setAttr("defaultvalue", src.getDefaultvalue());
			setAttr("required", src.getRequired());
		}
		
		renderJsp("/view/admin/template/inserttext.jsp");
	}
	
	public void goSelectComponent(){
		String idstr=getPara("id");
		if(StringUtils.isNotEmpty(idstr)){
			Integer id=Integer.valueOf(idstr);
			SRComponent src=SRComponent.dao.findById(id);
			
//			'/view/admin/template/insertselect.jsp?id='+row.id+'&uid='+(row.uid||'')+'&name='+row.name+'&code='+row.code+"&multiple="+multiple+"&size="+size,
			setAttr("id", idstr);
			setAttr("uid", src.getUid());
			setAttr("name", src.getName());
			setAttr("code", src.getCode());
			setAttr("multiple", getPara("multiple"));
			setAttr("width", getPara("width"));
			setAttr("size", getPara("size"));
			setAttr("required", src.getRequired());
		}
		
		renderJsp("/view/admin/template/insertselect.jsp");
	}
	
	public void downloadSRTemplate(){ 
		try {
			
			String rids=getPara("rids");
			String cids=getPara("cids");
			String sids=getPara("sids");
			String componentuids="";
			String section_uids="";
			ArrayList<String> images=new ArrayList<String>();
			
			
			Document doc=DocumentHelper.createDocument();
        	Element roote=doc.addElement("rootsrtemplates","xsd:http://www.w3.org/2001/XMLSchema").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        	if(StringUtils.isNotEmpty(rids)){
				
				Element srtemplatesele=roote.addElement("srtemplates");
				
				rids=rids.substring(0, rids.length()-1);
				List<SRTemplate> templates=sv.findSRTemplateByIds(rids);
				
				for(SRTemplate temp:templates){
					
					Element srtemplateele=srtemplatesele.addElement("srtemplate");
					
					srtemplateele.addElement("name").addText(temp.getName());
					srtemplateele.addElement("maprule").addText(temp.getMaprule());
					srtemplateele.addElement("content").addText(Base64.encodeBase64String(temp.getTemplatecontent().getBytes("UTF-8")));
					srtemplateele.addElement("enablefilter").addText(temp.getEnablefilter()!=null?temp.getEnablefilter():"");
					srtemplateele.addElement("filter_width").addText(temp.getFilterWidth()!=null?temp.getFilterWidth():"");
					srtemplateele.addElement("footer_left").addText(temp.getFooterLeft()!=null?temp.getFooterLeft():"");
					srtemplateele.addElement("footer_middle").addText(temp.getFooterMiddle()!=null?temp.getFooterMiddle():"");
					srtemplateele.addElement("footer_right").addText(temp.getFooterRight()!=null?temp.getFooterRight():"");
					String footer_img="";
					if(StrKit.notBlank(temp.getFooterImg())){
						File fooimg=new File(PropKit.use("system.properties").get("sysdir")+"\\"+"srtemplate\\"+temp.getFooterImg());
						if(fooimg.exists()){
							footer_img=Base64.encodeBase64String(FileUtils.readFileToByteArray(fooimg));
						}
					}
					srtemplateele.addElement("footer_img").addText(footer_img);

					com.itextpdf.html2pdf.jsoup.nodes.Document jsoupdoc=Jsoup.parse(temp.getTemplatecontent());
					//获取模板中的组件
					Elements elements=jsoupdoc.select("[name$=Component][uid]");
					for(com.itextpdf.html2pdf.jsoup.nodes.Element element : elements){
						componentuids+="'"+element.attr("uid")+"',";
					}
					//获取模板中的图片，排除条形码图片
					elements=jsoupdoc.select("img:not([barcodetype])");
					for(com.itextpdf.html2pdf.jsoup.nodes.Element element : elements){
						images.add(element.attr("src"));
					}
					//获取模板中的章节id
					elements=jsoupdoc.select("[sectionuid]");
					for(com.itextpdf.html2pdf.jsoup.nodes.Element element : elements){
						section_uids+="'"+element.attr("sectionuid")+"',";
					}
				}
				if(StrKit.notBlank(section_uids)){
					section_uids=section_uids.substring(0, section_uids.length()-1);
				}
			}
        	
        	
        	//章节
        	if(StrKit.notBlank(sids)||StrKit.notBlank(section_uids)) {
        		Element srsectionsele=roote.addElement("srsections");
        		if(sids!=null) {
        			sids=sids.lastIndexOf(",")>=0?sids.substring(0, sids.length()-1):sids;
        		}
				List<Srsection> sections=sv.findSRSectionByIdsOrUids(sids,section_uids);
				for(Srsection sec:sections){
					Element srsectionele=srsectionsele.addElement("srsection");
					srsectionele.addElement("uid").addText(sec.getUid());
					srsectionele.addElement("name").addText(sec.getName());
					srsectionele.addElement("displayname").addText(sec.getDisplayname()!=null?sec.getDisplayname():"");
					srsectionele.addElement("is_qc").addText(sec.getIsQc()!=null?(sec.getIsQc()+""):"");
					srsectionele.addElement("clone").addText(sec.getClone()!=null?(sec.getClone()+""):"");
					srsectionele.addElement("catalog").addText(sec.getCatalog()!=null?(sec.getCatalog()+""):"");
					srsectionele.addElement("header").addText(sec.getHeader()!=null?(sec.getHeader()+""):"");
					srsectionele.addElement("content").addText(Base64.encodeBase64String(sec.getSectioncontent().getBytes("UTF-8")));
					
					Pattern pattern = Pattern.compile("uid=\".+?\"");
					Matcher matcher = pattern.matcher(sec.getSectioncontent());
					while(matcher.find()){
						componentuids+="'"+matcher.group().substring(5, matcher.group().length()-1)+"',";
					}
					
					pattern = Pattern.compile("src=\".+?\"");
					matcher = pattern.matcher(sec.getSectioncontent());
					while(matcher.find()){
						images.add(matcher.group().substring(5, matcher.group().length()-1));
					}
					
				}
        	}

        	if(images.size()>0){
        		Element imgsele=roote.addElement("images");
        		String basedir=getSession().getServletContext().getRealPath("");
	        	for(String image:images){
	        		log.info(basedir+"\\"+image);
	        		File img=new File(basedir+"\\"+image);
	        		if(img.exists()){
	        			
	        			Element imgele=imgsele.addElement("image");
	        			imgele.addElement("path").addText(image);
	        			imgele.addElement("type").addText(image.substring(image.lastIndexOf(".")+1));
	        			imgele.addElement("content").addText(Base64.encodeBase64String(FileUtils.readFileToByteArray(img)));
	        		}
	        	
	        	}
        	}

        	if(StringUtils.isNotEmpty(componentuids)){
				componentuids=componentuids.substring(0, componentuids.length()-1);
			}
        	
			if(StrKit.notBlank(cids)||StrKit.notBlank(componentuids)){
				Element componentsele=roote.addElement("components");
				cids=StrKit.notBlank(cids)?cids.substring(0, cids.length()-1):null;
				List list=sv.findSRComponentByIdOrUid(cids, componentuids);
				List<SRComponent> components=(ArrayList<SRComponent>)list.get(0);
				HashMap<String, ArrayList<Srcomponentoption>> map=(HashMap<String, ArrayList<Srcomponentoption>>)list.get(1);
				
				for(SRComponent component:components){
					Element componentele=componentsele.addElement("component");
					componentele.addElement("uid").addText(component.getUid());
					componentele.addElement("name").addText(component.getName());
					componentele.addElement("code").addText(StringUtils.trimToEmpty(component.getCode()));
					componentele.addElement("standardcode").addText(StringUtils.trimToEmpty(component.getStandardcode()));
					componentele.addElement("type").addText(component.getType()+"");
					componentele.addElement("unit").addText(StringUtils.trimToEmpty(component.getUnit()));
					componentele.addElement("defaultvalue").addText(StringUtils.trimToEmpty(component.getDefaultvalue()));
					componentele.addElement("html").addText(Base64.encodeBase64String(component.getHtml().getBytes("UTF-8")));
					componentele.addElement("classifyid").addText(component.getClassifyId()!=null?component.getClassifyId()+"":"");
					componentele.addElement("required").addText(StringUtils.trimToEmpty(component.getRequired()));
					componentele.addElement("summary_text").addText(StrKit.notBlank(component.getSummaryText())?Base64.encodeBase64String(component.getSummaryText().getBytes("UTF-8")):"");
					Element optionsele=componentele.addElement("options");
					ArrayList<Srcomponentoption> srcomponentoptions=map.get(component.getId()+"");
					
					if(srcomponentoptions!=null){
						for(Srcomponentoption option:srcomponentoptions){
							Element optionele=optionsele.addElement("option");
							optionele.addElement("code").addText(StringUtils.trimToEmpty(option.getCode()));
							optionele.addElement("value").addText(StringUtils.trimToEmpty(option.getValue()));
							optionele.addElement("displayname").addText(StringUtils.trimToEmpty(option.getDisplayname()));
							optionele.addElement("standard_code").addText(StringUtils.trimToEmpty(option.getStandardCode()));
							optionele.addElement("opindex").addText(option.getOpindex()!=null?option.getOpindex()+"":"");
							optionele.addElement("sectionuid").addText(StringUtils.trimToEmpty(option.getSectionuid()));
							optionele.addElement("defaultoption").addText(StringUtils.trimToEmpty(option.getDefaultoption()));
							optionele.addElement("mutex").addText(StringUtils.trimToEmpty(option.getMutex()));
							optionele.addElement("color").addText(StringUtils.trimToEmpty(option.getColor()));
						}
					}
				}
				
			}

			String filename=PropKit.use("system.properties").get("tempdir")+"\\"+UUID.randomUUID().toString()+".xml";
			log.info("filename2="+filename);
			File xml=new File(filename);
			FileUtils.writeStringToFile(xml, doc.asXML(), "UTF-8");
			renderFile(xml);
//			FileUtils.deleteQuietly(xml);	

			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	public void importSRTemplate(){
		try {
			User user = (User) getSession().getAttribute("user");
			
			UploadFile file=getFile("srtemplatexml","UTF-8");
			if(file!=null){
				
				SAXReader reader = new SAXReader();
				Document doc=reader.read(new FileInputStream(file.getFile()));
				Element root = doc.getRootElement();
				Element components =root.element("components");
				if(components!=null){
					List componentlist=components.elements("component");
					for(int i=0;i<componentlist.size();i++){
						Element componentele=(Element)componentlist.get(i);
						SRComponent src=new SRComponent();
						src.setUid(componentele.elementText("uid"));
						log.info(componentele.elementText("name"));
						src.setName(componentele.elementText("name"));
						src.setCode(componentele.elementText("code"));
						src.setStandardcode(componentele.elementText("standardcode"));
						src.setType(Integer.valueOf(componentele.elementText("type")));
						if(StringUtils.isNotEmpty(componentele.elementText("unit"))){
							src.setUnit(componentele.elementText("unit"));
						}
						if(StringUtils.isNotEmpty(componentele.elementText("defaultvalue"))){
							src.setDefaultvalue(componentele.elementText("defaultvalue"));
						}
						src.setHtml(new String(Base64.decodeBase64(componentele.elementText("html")),"UTF-8"));
						if(StringUtils.isNotEmpty(componentele.elementText("classifyid"))){
							src.setClassifyId(Integer.valueOf(componentele.elementText("classifyid")));
						}
						if(StringUtils.isNotEmpty(componentele.elementText("required"))){
							src.setRequired(componentele.elementText("required"));
						}
						if(StringUtils.isNotEmpty(componentele.elementText("summary_text"))){
							src.setSummaryText(new String(Base64.decodeBase64(componentele.elementText("summary_text")),"UTF-8"));
						}

						src.setCreator(user.getId());
						src.setCreatorname(user.getName());
						
						List optionlist=componentele.element("options").elements("option");
						List<Srcomponentoption> srcomponentoptions=new ArrayList<Srcomponentoption>();
						for(int j=0;j<optionlist.size();j++){
							Element optionele=(Element)optionlist.get(j);
							Srcomponentoption srco=new Srcomponentoption();
							srco.setUid(UUID.randomUUID().toString());
							srco.setComponentUid(src.getUid());
							if(StringUtils.isNotEmpty(optionele.elementText("code"))){
								srco.setCode(optionele.elementText("code"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("value"))){
								log.info(optionele.elementText("value"));
								srco.setValue(optionele.elementText("value"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("displayname"))){
								srco.setDisplayname(optionele.elementText("displayname"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("standard_code"))){
								srco.setStandardCode(optionele.elementText("standard_code"));
							}
							srco.setOpindex(Integer.valueOf(optionele.elementText("opindex")));
							if(StringUtils.isNotEmpty(optionele.elementText("sectionuid"))){
								srco.setSectionuid(optionele.elementText("sectionuid"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("defaultoption"))){
								srco.setDefaultoption(optionele.elementText("defaultoption"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("mutex"))){
								srco.setMutex(optionele.elementText("mutex"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("color"))){
								srco.setColor(optionele.elementText("color"));
							}
							srcomponentoptions.add(srco);
						}
						
						sv.insertSRComponent(src,srcomponentoptions);
					}
				}

				Element srtemplates =root.element("srtemplates");
				if(srtemplates!=null){
					List srtemplatelist=srtemplates.elements("srtemplate");
					List<SRTemplate> srtemplateslist=new ArrayList<SRTemplate>();
					for(int i=0;i<srtemplatelist.size();i++){
						Element srtemplateele=(Element)srtemplatelist.get(i);
						SRTemplate srt=new SRTemplate();
						srt.setName(srtemplateele.elementText("name"));
						srt.setMaprule(srtemplateele.elementText("maprule"));
						srt.setTemplatecontent(new String(Base64.decodeBase64(srtemplateele.elementText("content")),"UTF-8"));
						
						if(StrKit.notBlank(srtemplateele.elementText("enablefilter"))){
							srt.setEnablefilter(srtemplateele.elementText("enablefilter"));
						}
						if(StrKit.notBlank(srtemplateele.elementText("filter_width"))){
							srt.setFilterWidth(srtemplateele.elementText("filter_width"));
						}
						if(StrKit.notBlank(srtemplateele.elementText("footer_left"))){
							srt.setFooterLeft(srtemplateele.elementText("footer_left"));
						}
						if(StrKit.notBlank(srtemplateele.elementText("footer_middle"))){
							srt.setFooterMiddle(srtemplateele.elementText("footer_middle"));
						}
						if(StrKit.notBlank(srtemplateele.elementText("footer_right"))){
							srt.setFooterRight(srtemplateele.elementText("footer_right"));
						}
						if(StrKit.notBlank(srtemplateele.elementText("footer_img"))){
							String imgname=StrKit.getRandomUUID()+".png";
							FileUtils.writeByteArrayToFile(new File(PropKit.use("system.properties").get("sysdir")+"\\"+"srtemplate\\"+imgname), 
									Base64.decodeBase64(srtemplateele.elementText("footer_img")));
							srt.setFooterImg(imgname);
						}
						srt.setCreator(user.getId());
						srt.setCreatorName(user.getName());
						srtemplateslist.add(srt);
					}
					
					sv.insertSRTemplate(srtemplateslist);
				}
				
				Element srsections =root.element("srsections");
				if(srsections!=null){
					List srsectionlist=srsections.elements("srsection");
					List<Srsection> srsectionslist=new ArrayList<Srsection>();
					for(int i=0;i<srsectionlist.size();i++){
						Element srsectionele=(Element)srsectionlist.get(i);
						Srsection sec=new Srsection();
						sec.setUid(srsectionele.elementText("uid"));
						sec.setName(srsectionele.elementText("name"));
						if(StrKit.notBlank(srsectionele.elementText("displayname"))) {
							sec.setDisplayname(srsectionele.elementText("displayname"));
						}
						if(StrKit.notBlank(srsectionele.elementText("is_qc"))) {
							sec.setIsQc(Integer.valueOf(srsectionele.elementText("is_qc")));
						}
						if(StrKit.notBlank(srsectionele.elementText("clone"))) {
							sec.setClone(Integer.valueOf(srsectionele.elementText("clone")));
						}
						if(StrKit.notBlank(srsectionele.elementText("catalog"))) {
							sec.setCatalog(Integer.valueOf(srsectionele.elementText("catalog")));
						}
						if(StrKit.notBlank(srsectionele.elementText("header"))) {
							sec.setHeader(Integer.valueOf(srsectionele.elementText("header")));
						}
						sec.setSectioncontent(new String(Base64.decodeBase64(srsectionele.elementText("content")),"UTF-8"));
						sec.setCreator(user.getId());
						sec.setCreatorname(user.getName());
						srsectionslist.add(sec);
					}
					sv.insertSRSection(srsectionslist);
				}
				
				Element imgsele =root.element("images");
				if(imgsele!=null){
					String basedir=getSession().getServletContext().getRealPath("");
					List imagelist=imgsele.elements("image");
					for(int i=0;i<imagelist.size();i++){
						Element imgele=(Element)imagelist.get(i);
						File img=new File(basedir+"\\"+imgele.elementText("path"));
						if(!img.exists()){
							FileUtils.writeByteArrayToFile(img, Base64.decodeBase64(imgele.elementText("content")));
						}
					}
				}
				
			}
			if(file!=null){
				FileUtils.deleteQuietly(file.getFile());
			}
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void openEditFromulaDialog(){
		setAttr("formula", getPara("formula"));
		renderJsp("/view/admin/template/editFormula.jsp");
	}
	
	/**
	 * 修改用户头像
	 */
	public void uploadTempFooterImg() {
		try {
			String img=getPara("imgBase64");
			String tempid=get("tempid");
			String header ="data:image";  
	        String[] imageArr=img.split(",");  
	        if(imageArr[0].contains(header)){//是img的  
	            // 去掉头部  
	        	img=imageArr[1];  
	        }
			String imagefile=StrKit.getRandomUUID()+"_"+new Date().getTime()+".png";
			File file = new File(PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator")+"userAvatar"+ System.getProperty("file.separator")+imagefile);
			String userAvatar = file.getName();
			try {
				FileUtils.writeByteArrayToFile(file, Base64.decodeBase64(img));
				FileUtils.copyFile(file, new File(PropKit.use("system.properties").get("sysdir")+"//srtemplate//"+ userAvatar));
				FileUtils.deleteQuietly(file);
//				User user=(User)getSession().getAttribute("user");
//				Db.update("update "+ TableNameConstant.CHAT_USERS + " set avatar = ? WHERE id = ?",userAvatar, user.getId());
//				user.setAvatar(userAvatar);
				if(StrKit.notBlank(tempid)){
					Db.update("update srtemplate set footer_img=? where id=?",userAvatar,tempid);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			renderJson(ResultUtil.success(getSession().getServletContext().getContextPath()+"/image/getSRTemplateImg?path=" + imagefile));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	public void getSRSectionByUids(){
		renderJson(sv.getSRSectionByUids(getPara("sectionuids")));
	}
	
	public void getAnatomycharts(){
		renderJson(sv.getAnatomycharts(getRequest()));
	}
	
	public void toLocationComponent(){
		renderJsp("/view/admin/template/locationComponent.jsp");
	}
	
	public void toDomains(){
		render("/view/admin/template/domains.html");
	}
	
	public void getDomains(){
		renderJson(HttpKit.get(PropKit.use("system.properties").get("matrix_url")+"/api/getDomains_v01"));
	}
	
	public void getModels(){
		Integer domainid=getInt("domainid");
		renderJson(HttpKit.get(PropKit.use("system.properties").get("matrix_url")+"/api/getModels_v01?domainid="+domainid));
	}
	
	public void getAttrs(){
		Integer modelid=getInt("modelid");
		renderJson(HttpKit.get(PropKit.use("system.properties").get("matrix_url")+"/api/getAttrs_v01?modelid="+modelid));
	}
}
