package com.healta.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;

import org.apache.commons.io.FileUtils;

import com.jfinal.render.Render;

public class PdfRender extends Render {

	private byte[] bytes;
	
	private String pdffile;
	
	public PdfRender(String pdffile){
		try {
			bytes=FileUtils.readFileToByteArray(new File(pdffile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		this.pdffile=pdffile;
	}
	
	public PdfRender(byte[] bytes){
		this.bytes=bytes;
	}
	
	@Override
	public void render() {
		// TODO Auto-generated method stub
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("application/pdf ");      
        response.setContentType("application/pdf");
		response.setContentLength(bytes.length);
        ServletOutputStream sos = null;
        try {
        	sos = response.getOutputStream();
        	sos.write(bytes, 0, bytes.length);
        	
        	
//            sos = response.getOutputStream();
//            in=new FileInputStream(new File(pdffile));
//            
//            byte[] buffer = new byte[102400];
//			for (int len = -1; (len = in.read(buffer)) != -1;) {
//				sos.write(buffer, 0, len);
//			}
			sos.flush();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (sos != null)
                try {sos.close();} catch (IOException e) {e.printStackTrace();}
        }
	}

}
