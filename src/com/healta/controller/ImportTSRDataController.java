package com.healta.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.service.ImportTSRDataService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

public class ImportTSRDataController extends Controller {
	private static final Logger log = Logger.getLogger(ImportTSRDataController.class);
	private static final ImportTSRDataService sv = new ImportTSRDataService();
	
	public void importDataToReport(){
		//String content=get("clipboardContent");
		//decode content
		try {
			String filepath="D:\\360MoveData\\Users\\hx\\Documents\\WeChat Files\\wxid_ysb061hdit4n22\\FileStorage\\File\\2022-03\\MI_neurology.csv";
			String content= readFile(filepath);
			//content=new String(Base64.decodeBase64(content),"UTF-8");
			//if(StringUtils.contains(content, "PARENCHYMA ANALYSIS REPORT")){//lung3D data
				sv.importViaLung3DData(content);
			//}
		} catch (UnsupportedEncodingException e3) {
			e3.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderNull();
	}
	
	
	public void importUnitedAIData(){
		try {
			UploadFile file=getFile("aidata");
			if(file!=null){
				sv.importUnitedAIData(file.getFile());
				FileUtils.deleteQuietly(file.getFile());
			}
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void importCTPA(){
		try {
			List<UploadFile> files=getFiles("/ctpa/tmp");
			sv.importCTAPData(files,get("studyid"));
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}
	
	public void batchImportCTPA(){
		try {
			UploadFile file=getFile("ctpazipfiles");
			sv.batchImportCTPA(file);
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try {
			String filepath="D:\\360MoveData\\Users\\hx\\Documents\\WeChat Files\\wxid_ysb061hdit4n22\\FileStorage\\File\\2022-03\\MI_neurology.csv";
			String content= readFile(filepath);
			content=new String(Base64.decodeBase64(content),"UTF-8");
			sv.importViaLung3DData(content);
		} catch (UnsupportedEncodingException e3) {
			e3.printStackTrace();
		}
	}
	  public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
	        InputStream is = new FileInputStream(filePath);
	        String line; // 用来保存每行读取的内容
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        line = reader.readLine(); // 读取第一行
	        while (line != null) { // 如果 line 为空说明读完了
	            buffer.append(line); // 将读到的内容添加到 buffer 中
	            buffer.append("\n"); // 添加换行符
	            line = reader.readLine(); // 读取下一行
	        }
	        reader.close();
	        is.close();
	  }
	  public static String readFile(String filePath) throws IOException {
	        StringBuffer sb = new StringBuffer();
	        readToBuffer(sb, filePath);
	        return sb.toString();
	    }
}
