package com.healta.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import org.apache.log4j.Logger;
import com.jfinal.render.Render;

public class ImageRender extends Render {
	private final static Logger log = Logger.getLogger(ImageRender.class);
	
	private String imagefile;
	private File imgfile;
	
	public ImageRender(String imagefile){
		this.imagefile=imagefile;
	}
	public ImageRender(File imgfile){
		this.imgfile=imgfile;
	}
	
	@Override
	public void render() {
		// TODO Auto-generated method stub
		
		if(imgfile==null){
			imgfile=new File(imagefile);
		}
		if(imgfile.isFile()&&imgfile.exists()){
	        response.addHeader("Pragma","no-cache");
	        response.addHeader("Cache-Control","no-cache");
	        response.setDateHeader("Expires", 0);
	        response.setContentType("image/png");
	        ServletOutputStream sos = null;
	        FileInputStream in=null;
	        try {
	//            ImageIO.write(ImageIO.read(new File(imagefile)), "png",sos);   
	//            sos = response.getOutputStream();
	            
	            
	//            ServletContext context = getServletContext();// 得到背景对象
	//            InputStream imageIn = context.getResourceAsStream(imagePath);// 文件流
	//            BufferedInputStream bis = new BufferedInputStream(imageIn);// 输入缓冲流
	//            BufferedOutputStream bos = new BufferedOutputStream(output);// 输出缓冲流
	//            byte data[] = new byte[4096];// 缓冲字节数
	//            int size = 0;
	//            size = bis.read(data);
	//            while (size != -1) {
	//                bos.write(data, 0, size);
	//                size = bis.read(data);
	//            }
	            in=new FileInputStream(imgfile);
	            sos = response.getOutputStream();
	            byte[] buffer = new byte[10240];
				for (int len = -1; (len = in.read(buffer)) != -1;) {
					sos.write(buffer, 0, len);
				}
				sos.flush();
	        } catch (Exception e) {
	        	log.error(e);
	            //throw new RuntimeException(e);
	        }
	        finally {
	        	if (in != null)
	                try {in.close();} catch (IOException e) {log.error(e);}
	//            if (sos != null)
	//                try {sos.close();} catch (IOException e) {e.printStackTrace();}
	        }
		}
		else{
			log.warn("The file "+imagefile+ " not exists!");
		}
	}

}
