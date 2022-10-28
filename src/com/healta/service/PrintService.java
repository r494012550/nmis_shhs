package com.healta.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import com.healta.config.MainConfig;
import com.healta.constant.PrintTemplateParameters;
import com.healta.model.Report;
import com.healta.model.ResearchTestgroupData;
import com.healta.service.PrintService.Header;
import com.healta.service.PrintService.PageXofY;
import com.healta.util.MyConverterProperties;
import com.healta.util.PrintTemplateName;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

public class PrintService {
	private final static Logger log = Logger.getLogger(PrintService.class);
	
	public byte[] generatePdfReport(Report report,int port,String serverurl){
		byte[] pdFile=null;
		if(report.getTemplateId() != null && report.getTemplateId() >0){
//				printHtml(getParaToInt("reportid"),request.getServerPort());
			try {
				pdFile= FileUtils.readFileToByteArray(new File(makeSrReportAsPdf(report.getId(),port)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			String printtempname = PrintTemplateName.getPrintTemplate_Report(report);
			if(StrKit.notBlank(printtempname)){
				File reportFile = new File(JFinal.me().getServletContext().getRealPath("upload/print/"+printtempname+".jasper"));
				log.info(reportFile);
				if (reportFile.exists() == false) {
					return null;
				}
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				parameters.put(PrintTemplateParameters.REPORT_REPORTID, report.getId());
				parameters.put(PrintTemplateParameters.REPORT_DESC_FONTSIZE, report.getDescFontsize()!=null?report.getDescFontsize()+"":PrintTemplateName.REPORT_DEFAULT_PRINT_FONTSIZE);
				parameters.put(PrintTemplateParameters.REPORT_RESULT_FONTSIZE, report.getResultFontsize()!=null?report.getResultFontsize()+"":PrintTemplateName.REPORT_DEFAULT_PRINT_FONTSIZE);
				parameters.put(PrintTemplateParameters.SERVER_URL, serverurl);
				pdFile=makeNormalReportAsPdf(reportFile, parameters);
			}
		}
		
		return pdFile;
	}
	
	/**
	 *  创建普通报告的pdf
	 * @param jasper  普通报告模板文件
	 * @param params  需要传入的参数
	 * @return
	 */
	public byte[] makeNormalReportAsPdf(File jasper,HashMap<String, Object>params) {
	    Connection connection = null;
		byte[] bytes = null;
		try {
		    // 连接数据库
			connection = MainConfig.mainDbPlugin.getDataSource().getConnection();
			// 据据jasper文件生成JasperPrint对象
			if (jasper.exists() == false) {
				return null;
			}
			bytes = JasperRunManager.runReportToPdf(jasper.getPath(), params, connection);
		} catch (JRException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}
	
	/**
	 *  创建结构化模板的pdf
	 * @param reportid  报告的id
	 * @param port      请求端口
	 * @param patientname  病人的姓名
	 * @return
	 */
	public String makeSrReportAsPdf(Integer reportid,int port) {
		Report report=Report.dao.findFirst("select report.*,(select patientname from patient,studyorder where patient.id=studyorder.patientidfk and studyorder.id=report.studyorderfk) as patientname "
				+ " from report where id=?",reportid);
		String pdfname=PropKit.use("system.properties").get("tempdir")+"\\"+report.getStr("patientname")+"_"+report.getStudyid()+"_"+reportid+".pdf";
		log.info("pdfname"+pdfname);
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfname));
		
        // step 3
//    	document.open();
        // step 4
    
    	String html=report.getCheckdescHtml();
    	
//    	html=html.replaceAll("/image/image_GetViaImage", "http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/image/image_GetViaImage");
//    	html=html.replaceAll("/image/image_GetSignImg", "http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/image/image_GetSignImg");
    	html=html.replaceAll("image/image", "http://localhost:"+port+JFinal.me().getContextPath()+"/image/image");
//    	html=html.replaceAll("/img/via", "http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/img/via");
    	html=html.replaceAll("<br>", "<br/>");
    	html=html.replaceAll("<hr>", "<hr/>");
    	html=html.replaceAll("(?=alt=\"img\")(?:[^>]*)([>])", "alt=\"img\"/>" );
//    	html=html.replaceAll("_ueditor_page_break_tag_", "<div class='pageNext'></div>");
    	
    	html="<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
    			+ "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
    				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://localhost:"+port+JFinal.me().getContextPath()+"/js/ueditor/themes/default/css/ueditor.css\"/>"
    				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://localhost:"+port+JFinal.me().getContextPath()+"/js/ueditor/themes/iframe.css\"/>"
    					//+ "<script type=\"text/javascript\" src=\"http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/js/easyui/jquery.min.js\"></script>"
    					//+ "<script type=\"text/javascript\" src=\"http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/js/jquery-barcode.min.js\"></script>"
    			+ "<style>"
//    			+"@page {"+
//                //"size:landscape;"+
//                "margin-top:50pt;"+
//                "@top-center {content: element(header)}"+
//                "@bottom-center {content: element(footer)}"+
////                "page-break-before:always"+微软雅黑, Microsoft YaHei,思源宋体, Source Han Serif ,微软雅黑, Microsoft YaHei,
//              "}"
    			+ "body{font-family: "+PropKit.use("system.properties").get("report_print_fonts")+" !important;font-size: 14px !important;}"
    			+"table{margin-bottom:5px;border-collapse:collapse;display:table;}"
    			+"td,th{padding: 1px 2px;border: 1px solid #DDD;}"
                +"caption{border:1px dashed #DDD;border-bottom:0;padding:3px;text-align:center;}"
                +"th{border-top:1px solid #BBB;background-color:#F7F7F7;}"
                +"table tr.firstRow th{border-top-width:2px;}"
                +".ue-table-interlace-color-single{ background-color: #fcfcfc; } .ue-table-interlace-color-double{ background-color: #f7faff; }"
                +"td p{margin:0;padding:0;}"
                
//                + ".pagebreak{display:block;clear:both !important;cursor:default !important;width: 100% !important;margin:0;}"
                + ".pageNext{page-break-after: always;}"
                +".hlong *{display:inline-block;vertical-align:middle}"
//                +".header1 {position: running(header);color:#cccccc;font-family: SimSun;padding-top:25pt;}"
//                +".footer { position: running(footer) }"
                //+"hr{height:1px;}" 
    			+ "</style></head><body>"
//    			+ "<div style='margin:0 auto;'><div class='header1' style='text-align:right;margin-top:0;'><div style='text-align:right;margin:0;'>header</div><br/></div>"
    			+html+"<br/></body></html>";
    	
    	//	    com.itextpdf
//		XMLWorkerHelper.getInstance().parseXHtml(writer, document,new ByteArrayInputStream(html.getBytes("UTF-8")),Charset.forName("UTF-8"),new StFontProvider());
//		XMLWorkerHelper.getInstance().parseXHtml(writer, doc, in, charset, fontProvider);
//		FontProvider prider=new XMLWorkerFontProvider(fontsPath);
//		prider.
//		document.close();
    	//Create Document
    	
    	PdfDocument pdfDocument=null;
    	try {
	        PdfWriter writer= new PdfWriter(pdfname);
	        pdfDocument = new PdfDocument(writer);
	        //Create event-handlers
	        String header = "";
	        Header headerHandler = new Header(header);
	        PageXofY footerHandler = new PageXofY(pdfDocument);
	 
	        //Assign event-handlers
	        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE,headerHandler);
	        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE,footerHandler);
	        //Convert
	        HtmlConverter.convertToDocument(html, pdfDocument, MyConverterProperties.getInstance());
	    
	        //Write the total number of pages to the placeholder
	        footerHandler.writeTotal(pdfDocument);
//	    	HtmlConverter.convertToPdf(html, pdfDocument, MyConverterProperties.getInstance());
	        
    	
    	}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally {
    		if(pdfDocument!=null&&!pdfDocument.isClosed()) {
    			pdfDocument.close();
    		}
    	}
    	
    	return pdfname;
	}
	
	
	public static class Header implements IEventHandler {

		protected String header;
	    public Header(String header) {
	        this.header = header;
	    }
	    @Override
	    public void handleEvent(Event event) {
	        //Retrieve document and
	        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
	        PdfDocument pdf = docEvent.getDocument();
	        PdfPage page = docEvent.getPage();
	        Rectangle pageSize = page.getPageSize();
	        PdfCanvas pdfCanvas = new PdfCanvas(
	                page.getLastContentStream(), page.getResources(), pdf);
	        Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
	        canvas.setFontSize(18f);
	        //Write text at position
	        canvas.showTextAligned(header,
	                pageSize.getWidth() / 2,
	                pageSize.getTop() - 30, TextAlignment.CENTER);
	    }

	}
	
	//page X of Y 
    public static class PageXofY implements IEventHandler {
        protected PdfFormXObject placeholder;
        protected float side = 20;
        protected float x = 300;
        protected float y = 25;
        protected float space = 4.5f;
        protected float descent = 3;
        protected boolean showTotal=true;
        public PageXofY(PdfDocument pdf) {
            placeholder =
                    new PdfFormXObject(new Rectangle(0, 0, side, side));
        }
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdf.getPageNumber(page);
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(
                    page.getLastContentStream(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
            Paragraph p = new Paragraph()
                    //.add("Page ")
                    .add(String.valueOf(pageNumber)).add(showTotal?" /":"  ");
            canvas.showTextAligned(p, x, y, TextAlignment.RIGHT);
            pdfCanvas.addXObject(placeholder, x + space, y - descent);
            pdfCanvas.release();
        }
        public void writeTotal(PdfDocument pdf) {
            Canvas canvas = new Canvas(placeholder, pdf);
            canvas.showTextAligned(String.valueOf(pdf.getNumberOfPages()),
                    0, descent, TextAlignment.LEFT);
        }
        
        public void setShowTotal(boolean showTotal){
        	this.showTotal=showTotal;
        }
    }

    public String makeImageAsPdf(Integer reportid,int port) throws IOException {
		Report report=Report.dao.findFirst("select report.*,(select patientname from patient,studyorder where patient.id=studyorder.patientidfk and studyorder.id=report.studyorderfk) as patientname "
				+ " from report where id=?",reportid);
		String pdfname=PropKit.use("system.properties").get("tempdir")+"\\"+report.getStr("patientname")+"_"+report.getStudyid()+"_"+reportid+".pdf";
		log.info("pdfname"+pdfname);

    	String html=report.getPrintImagesHtml();//FileUtils.readFileToString(new File("d:\\te1.html"));////
    	html=html.replaceAll("image/image", "http://localhost:"+port+JFinal.me().getContextPath()+"/image/image");
    	html=html.replaceAll("<br>", "<br/>");
    	html=html.replaceAll("<hr>", "<hr/>");
    	html=html.replaceAll("(?=alt=\"img\")(?:[^>]*)([>])", "alt=\"img\"/>" );
//    	html=html.replaceAll("_ueditor_page_break_tag_", "<div class='pageNext'></div>");
    	
    	html="<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
    			+ "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
    				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://localhost:"+port+JFinal.me().getContextPath()+"/js/front/report/report_image_print.css\"/>"
    			+ "</head><body>"
    			+html+"<br/></body></html>";
    	
    	log.info(html);
    	
    	PdfDocument pdfDocument=null;
    	try {
	        PdfWriter writer= new PdfWriter(pdfname);
	        pdfDocument = new PdfDocument(writer);
	        //Create event-handlers
	        String header = "";
	        Header headerHandler = new Header(header);
	        PageXofY footerHandler = new PageXofY(pdfDocument);
	        footerHandler.setShowTotal(false);
	 
	        //Assign event-handlers
	        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE,headerHandler);
	        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE,footerHandler);
	        //Convert
//	        HtmlConverter.convertToDocument(html, pdfDocument, MyConverterProperties.getInstance());
	    
	        //Write the total number of pages to the placeholder
//	        footerHandler.writeTotal(pdfDocument);
	    	HtmlConverter.convertToPdf(html, pdfDocument, MyConverterProperties.getInstance());
//	    	footerHandler.writeTotal(pdfDocument);
    	
    	}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally {
    		if(pdfDocument!=null&&!pdfDocument.isClosed()) {
    			pdfDocument.close();
    		}
    	}
    	
    	return pdfname;
	}
    
    public static void main(String[] arg){
    	PdfDocument pdfDocument=null;
    	try {
    		
    		String html=FileUtils.readFileToString(new File("d:\\te1.html"));
    		System.out.println(html);
	        PdfWriter writer= new PdfWriter("d:\\12.pdf");
	        pdfDocument = new PdfDocument(writer);
	        //Create event-handlers
	        String header = "";
	        Header headerHandler = new Header(header);
	        PageXofY footerHandler = new PageXofY(pdfDocument);
	 
	        //Assign event-handlers
	        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE,headerHandler);
	        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE,footerHandler);
	        //Convert
	        HtmlConverter.convertToDocument(html, pdfDocument, null);
	    
	        //Write the total number of pages to the placeholder
	        footerHandler.writeTotal(pdfDocument);
//	    	HtmlConverter.convertToPdf(html, pdfDocument, MyConverterProperties.getInstance());
	        
    	
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally {
    		if(pdfDocument!=null&&!pdfDocument.isClosed()) {
    			pdfDocument.close();
    		}
    	}
    }
    public String makeFormDataAsPdf(Integer dataid,int port) {
		ResearchTestgroupData data=ResearchTestgroupData.dao.findById(dataid);
		String pdfname=PropKit.use("system.properties").get("tempdir")+"\\form_"+data.getId()+"_"+data.getFormName()+".pdf";
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfname));
		
        // step 3
//    	document.open();
        // step 4
    
    	String html=data.getPrintHtml();
    	
//    	html=html.replaceAll("/image/image_GetViaImage", "http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/image/image_GetViaImage");
//    	html=html.replaceAll("/image/image_GetSignImg", "http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/image/image_GetSignImg");
    	html=html.replaceAll("image/image", "http://localhost:"+port+JFinal.me().getContextPath()+"/image/image");
//    	html=html.replaceAll("/img/via", "http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/img/via");
    	html=html.replaceAll("<br>", "<br/>");
    	html=html.replaceAll("<hr>", "<hr/>");
    	html=html.replaceAll("(?=alt=\"img\")(?:[^>]*)([>])", "alt=\"img\"/>" );
//    	html=html.replaceAll("_ueditor_page_break_tag_", "<div class='pageNext'></div>");
    	
    	html="<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
    			+ "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
    				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://localhost:"+port+JFinal.me().getContextPath()+"/js/ueditor/themes/default/css/ueditor.css\"/>"
    				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://localhost:"+port+JFinal.me().getContextPath()+"/js/ueditor/themes/iframe.css\"/>"
    					//+ "<script type=\"text/javascript\" src=\"http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/js/easyui/jquery.min.js\"></script>"
    					//+ "<script type=\"text/javascript\" src=\"http://localhost:"+getRequest().getServerPort()+getRequest().getContextPath()+"/js/jquery-barcode.min.js\"></script>"
    			+ "<style>"
//    			+"@page {"+
//                //"size:landscape;"+
//                "margin-top:50pt;"+
//                "@top-center {content: element(header)}"+
//                "@bottom-center {content: element(footer)}"+
////                "page-break-before:always"+微软雅黑, Microsoft YaHei,思源宋体, Source Han Serif ,微软雅黑, Microsoft YaHei,
//              "}"
    			+ "body{font-family: "+PropKit.use("system.properties").get("report_print_fonts")+" !important;font-size: "+PropKit.use("system.properties").get("font_size","14px")+" !important;}"
    			+"table{margin-bottom:5px;border-collapse:collapse;display:table;}"
    			+"td,th{padding: 1px 2px;border: 1px solid #DDD;}"
                +"caption{border:1px dashed #DDD;border-bottom:0;padding:3px;text-align:center;}"
                +"th{border-top:1px solid #BBB;background-color:#F7F7F7;}"
                +"table tr.firstRow th{border-top-width:2px;}"
                +".ue-table-interlace-color-single{ background-color: #fcfcfc; } .ue-table-interlace-color-double{ background-color: #f7faff; }"
                +"td p{margin:0;padding:0;}"
                
//                + ".pagebreak{display:block;clear:both !important;cursor:default !important;width: 100% !important;margin:0;}"
                + ".pageNext{page-break-after: always;}"
                +".hlong *{display:inline-block;vertical-align:middle}"
//                +".header1 {position: running(header);color:#cccccc;font-family: SimSun;padding-top:25pt;}"
//                +".footer { position: running(footer) }"
                //+"hr{height:1px;}" 
    			+ "</style></head><body>"
//    			+ "<div style='margin:0 auto;'><div class='header1' style='text-align:right;margin-top:0;'><div style='text-align:right;margin:0;'>header</div><br/></div>"
    			+html+"<br/></body></html>";
    	
    	//	    com.itextpdf
//		XMLWorkerHelper.getInstance().parseXHtml(writer, document,new ByteArrayInputStream(html.getBytes("UTF-8")),Charset.forName("UTF-8"),new StFontProvider());
//		XMLWorkerHelper.getInstance().parseXHtml(writer, doc, in, charset, fontProvider);
//		FontProvider prider=new XMLWorkerFontProvider(fontsPath);
//		prider.
//		document.close();
    	//Create Document
    	
    	PdfDocument pdfDocument=null;
    	try {
	        PdfWriter writer= new PdfWriter(pdfname);
	        pdfDocument = new PdfDocument(writer);
	        //Create event-handlers
	        String header = "";
	        Header headerHandler = new Header(header);
	        PageXofY footerHandler=null;
//	        Record tmp=null;
	       /* if(data!=null){
	        	tmp=Db.findFirst("select id,name,footer_left,footer_middle,footer_right,footer_img ,"
	        			+ "(select top 1 '姓名：'+patient.patientname from patient,studyorder,report "
	        			+ "where patient.id=studyorder.patientidfk and studyorder.id=report.studyorderfk and report.id=?) as patientname "
	        			+ " from srtemplate where id=?",researchformdata.getId(),researchformdata.getTemplateId());
	        }*/
//	        if(tmp!=null){
//	        	footerHandler = new PageXofY(pdfDocument,tmp.getStr("footer_left"),tmp.getStr("footer_middle"),tmp.getStr("footer_right"),tmp.getStr("footer_img"),tmp.getStr("patientname"));
//	        } else{
	        	footerHandler = new PageXofY(pdfDocument);
//	        }
	        //Assign event-handlers
	        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE,headerHandler);
	        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE,footerHandler);
	        //Convert
	        HtmlConverter.convertToDocument(html, pdfDocument, MyConverterProperties.getInstance());
	    
	        //Write the total number of pages to the placeholder
	        footerHandler.writeTotal(pdfDocument);
//	    	HtmlConverter.convertToPdf(html, pdfDocument, MyConverterProperties.getInstance());
	        
    	
    	}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally {
    		if(pdfDocument!=null&&!pdfDocument.isClosed()) {
    			pdfDocument.close();
    		}
    	}
    	
    	return pdfname;
	}
}
