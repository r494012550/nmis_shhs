package com.healta.util;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.layout.font.FontProvider;
import com.jfinal.core.JFinal;

public class MyConverterProperties extends ConverterProperties {
	private static MyConverterProperties mcp=null;
	
	public static MyConverterProperties getInstance(){
		if(mcp==null){
			mcp=new MyConverterProperties();
			FontProvider fp = new FontProvider();
//			fp.addSystemFonts();
	        fp.addStandardPdfFonts();
	        fp.addDirectory(JFinal.me().getServletContext().getRealPath("/fonts"));
	        mcp.setFontProvider(fp);
	        mcp.setBaseUri(JFinal.me().getServletContext().getRealPath("/upload/print"));
		}
		
		return mcp;
	}
}
