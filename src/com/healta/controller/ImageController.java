package com.healta.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.model.StudyImage;
import com.healta.model.Syngoviaimage;
import com.healta.model.User;
import com.healta.render.ImageRender;
import com.healta.service.ChatService;
import com.healta.util.ChatKit;
import com.healta.util.IPKit;
import com.healta.util.ResultUtil;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

/**
 * 检查报告相关控制层
 * 
 * @author Administrator
 *
 */
public class ImageController extends Controller {

	
	/**
	 * 业务层
	 */
	
	private final static Logger log = Logger.getLogger(ImageController.class);

	public void image_GetViaImage(){
		
		Integer id=getParaToInt("id");
		String imageid=getPara("imageid");
		String studyid=getPara("studyid");
		String studyinsuid=getPara("studyinsuid");
		Syngoviaimage image=null;
		if(id!=null){
			image=Syngoviaimage.dao.findById(id);
		}
		else if(StrKit.notBlank(imageid)&&StrKit.notBlank(studyinsuid)){
			image=Syngoviaimage.dao.getImageByImageUid(imageid, studyinsuid);
		}
		else if(StrKit.notBlank(imageid)&&StrKit.notBlank(studyid)){
			image=Syngoviaimage.dao.getImageByImageid(imageid, studyid);
		}
		
		if(image!=null&&image.getImagefile()!=null){
			String filename=PropKit.use("system.properties").get("online_path")+System.getProperty("file.separator")+image.getImagefile();
			if(new File(filename).exists()){
				render(new ImageRender(filename));
			}
			else{
				log.info("The file :"+filename+" not exists");
				renderNull();
			}
			//renderFile(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+image.getImagefile());
		}
		else{
			renderJson();
		}
		
//		renderFile(Syngoviaimage.dao.findById(getParaToInt("id")).getImagefile());
	}
	
	public void image_GetSignImg(){
		String path=getPara("path");
		if(StringUtils.isNotEmpty(path)){
			render(new ImageRender(PropKit.use("system.properties").get("e-signpath")+System.getProperty("file.separator")+path));
		}
		else{
			renderJson();
		}
	}
	
	public void image_GetApplyImg(){
		String path=getPara("path");
		if(StringUtils.isNotEmpty(path)){
			render(new ImageRender(PropKit.get("base_upload_path")+path));
		}
		else{
			renderJson();
		}
	}
	
	
	public void getHtml(){
		
		String html="<html xmlns:b=\"urn:hl7-org:v3\">"+
					"<head>"+
					"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>"+
					"</head>"+
					"<body>"+
					"<h3>Findings:CaScore_Percentile</h3>"+
					"<table border=\"0\">"+
					"<tr>"+
					"<td>冠状动脉年龄：</td>"+
					"<td>&gt;70</td>"+
					"</tr>"+
					"<tr>"+
					"<td>参考数据库：</td>"+
					"<td>Raggi, Circulation, 2000 (USA, 9730 patients)</td>"+
					"</tr>"+
					"<tr>"+
					"<td>百分位标题：</td>"+
					"<td>Raggi, Circulation 2000</td>"+
					"</tr>"+
					"<tr>"+
					"<td>百分位参考文献：</td>"+
					"<td>Raggi et al, Circulation 2000,101,850-855</td>"+
					"</tr>"+
					"<tr>"+
					"<td>百分位参考文献：</td>"+
					"<td>Raggi et al, Circulation 2000,101,850-855</td>"+
					"</tr>"+
					"<tr>"+
					"<td>百分位参考文献：</td>"+
					"<td>Raggi et al, Circulation 2000,101,850-855</td>"+
					"</tr>"+
					"<tr>"+
					"<td>百分位参考文献：</td>"+
					"<td>Raggi et al, Circulation 2000,101,850-855</td>"+
					"</tr>"+
					"<tr>"+
					"<td>百分位参考文献：</td>"+
					"<td>Raggi et al, Circulation 2000,101,850-855</td>"+
					"</tr>"+
					"<tr>"+
					"<td>百分位参考文献：</td>"+
					"<td>Raggi et al, Circulation 2000,101,850-855</td>"+
					"</tr>"+
					"<tr>"+
					"<td>百分位参考文献：</td>"+
					"<td>Raggi et al, Circulation 2000,101,850-855</td>"+
					"</tr>"+
					"</table>"+
					"</body>"+
					"</html>";
					
		
		renderHtml(html);
	}
	
	public void toScanView(){
		setAttr("basePath", getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/");
		setAttr("userid", getParaToInt("userid")+990000);
		setAttr("ip", getPara("ip"));
		setAttr("launchip", getPara("launchip"));
		render("/tools/scan.html");
	}
	
	public void getIP(){
		//log.info(getSession().getId());
		log.info(getRequest().getHeader("User-Agent"));
		renderText(IPKit.getIP(getRequest()));
	}
	
	public void uploadApplyForm(){
		UploadFile file = getFile("file", "/apply/tmp");
		log.info(file.getFileName());
		
		Integer orderid = getParaToInt("orderid");
		String studyid = getPara("studyid");
		if(orderid!=null && StringUtils.isNotBlank(studyid)) {
			try {
				String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
				String dirname=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				FileUtils.moveFileToDirectory(file.getFile(), new File(PropKit.get("base_upload_path")+"\\apply\\"+year+"\\"+dirname), true);
				
				StudyImage studyImage=StudyImage.dao.findByOrderid(orderid);
				if(studyImage == null) {
					studyImage = new StudyImage();
					studyImage.setOrderId(orderid);
					studyImage.setStudyid(studyid);
				}
				for(int i=1; i<=10; i++) {
					if(studyImage.get("img"+i) == null) {
						studyImage.set("img"+i, "/apply/"+year+"/"+dirname+"/"+file.getFileName());
						break;
					}
				}
				
				if(studyImage.getId()!=null){
	  				studyImage.update();
	  			}
	  			else{
	  				studyImage.remove("id").save();
	  			}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else {
			String ip=IPKit.getIP(getRequest());

			log.info("ip="+ip+";launchip="+getPara("launchip"));
			
			if(StrKit.equals(ip, getPara("launchip"))){
				WebSocketUtils.sendMessage(getParaToInt("userid")-990000, new WebsocketVO(WebsocketVO.SCANAPPFORM,file.getFileName()).toJson());
			}
		}
		
		renderJson(ResultUtil.success());
	}
	
	/**
	 * 返回聊天图片
	 */
	public void getChatImg() {
		
		String filename=PropKit.use("system.properties").get("sysdir")+"\\"+"chat\\img\\"+ ChatKit.getChatService().queryMessageImgById(getParaToInt("id")).getImgpath();
		File image=new File(filename);
		if(image.isFile()&&image.exists()){
			render(new ImageRender(filename));
		}
		else{
			log.info("The file :"+filename+" not exists");
			renderNull();
		}
		
	}
	
	/**
	 * 返回头像图片
	 */
	public void getAvatarImg() {
		
		String basepath=StrKit.equals(getPara("tmp"), "1")?PropKit.use("system.properties").get("tempdir"):PropKit.use("system.properties").get("sysdir");
		String filename=basepath+"\\"+"userAvatar\\"+ getPara("path");
		File image=new File(filename);
		if(image.isFile()&&image.exists()){
			render(new ImageRender(filename));
		}
		else{
			log.info("The file :"+filename+" not exists");
			File i=new File(getSession().getServletContext().getRealPath("")+"\\themes\\head.ico");
			if(i.isFile()&&i.exists()){
				render(new ImageRender(i));
			}
			else{
				renderNull();
			}
		}
	}
	
	/**
	 * 返回群头像图片
	 */
	public void getGroupAvatarImg() {
		String basepath=StrKit.equals(getPara("tmp"), "1")?PropKit.use("system.properties").get("tempdir"):PropKit.use("system.properties").get("sysdir");
		String filename = StrKit.equals(getPara("tmp"), "1")? basepath+"\\"+"groupAvatar\\"+ getPara("path") : basepath + "\\"+"groupAvatar\\" + getPara("path");
		System.out.println(filename);
		File image=new File(filename);
		if(image.isFile()&&image.exists()){
			render(new ImageRender(filename));
		}
		else{
			log.info("The file :"+filename+" not exists");
			File i=new File(getSession().getServletContext().getRealPath("")+"\\themes\\head.ico");
			if(i.isFile()&&i.exists()){
				render(new ImageRender(i));
			}
			else{
				renderNull();
			}
		}
	}
	
	/**
	 * 返回模板页眉页脚图片
	 */
	public void getSRTemplateImg() {
		
		String basepath=StrKit.equals(getPara("tmp"), "1")?PropKit.use("system.properties").get("tempdir"):PropKit.use("system.properties").get("sysdir");
		String filename=basepath+"\\"+"srtemplate\\"+ getPara("path");
		File image=new File(filename);
		if(image.isFile()&&image.exists()){
			render(new ImageRender(filename));
		}
		else{
			log.info("The file :"+filename+" not exists");
			renderNull();
		}
	}
}
