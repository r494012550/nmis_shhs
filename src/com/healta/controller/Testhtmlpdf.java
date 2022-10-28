package com.healta.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.layout.font.FontProvider;


public class Testhtmlpdf {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// IO
//        File htmlSource = new File("input.html");
        File pdfDest = new File("c:/output.pdf");
        String str = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"  
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">"  
                + "<head>"  
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"  
                + "<title>HTML 2 PDF</title>"  
                + "<style type=\"text/css\">" 
                + "body{font-family:SimSun;}"
                +"table{margin-bottom:10px;border-collapse:collapse;display:table;}"
    			+"td,th{padding: 5px 10px;border: 1px solid #DDD;}"
                +"caption{border:1px dashed #DDD;border-bottom:0;padding:3px;text-align:center;}"
                +"th{border-top:1px solid #BBB;background-color:#F7F7F7;}"
                +"table tr.firstRow th{border-top-width:2px;}"
                +".ue-table-interlace-color-single{ background-color: #fcfcfc; } .ue-table-interlace-color-double{ background-color: #f7faff; }"
                +"td p{margin:0;padding:0;}"
                +"hr{height:1px;}" 
                + "</style>"  
                + "</head>"  
                + "<body>"  
                + "<div style=\"width:90%; height:160px;\">多情浪漫的人，其实内心的情感是非常脆弱的，感情的末梢，有那么一点儿敏感，还有那么一点儿想入非非。所以和浪漫多情的人在一起，言语一定要斯文，说话不能像火炮，态度一定要温柔、语气也要婉转。遇到对方的一个眼神，也许有些人并没发现什么端倪，但是放在懂得浪漫的人身上，就会体会出万种滋味，百般柔情来。"  
                + "</div>"  
                +"<p>"
                +"<span style=\"font-size: 14px; font-family: 宋体, SimSun;\"><strong>冠脉详细信息</strong></span>"
        		+"</p>"
                + "<hr/>"  
                + "<table cellspacing=\"0\" cellpadding=\"0\" style=\"width:100%; border:1px;\">"  
                + "  <tr>"  
                + "  <td style=\"width:30%\">table中的中文显示及换行"  
                + "  </td>"  
                + "  <td>多情浪漫的人，其实内心的情感是非常脆弱的，感情的末梢，有那么一点儿敏感，还有那么一点儿想入非非。所以和浪漫多情的人在一起，言语一定要斯文，说话不能像火炮，态度一定要温柔、语气也要婉转。遇到对方的一个眼神，也许有些人并没发现什么端倪，但是放在懂得浪漫的人身上，就会体会出万种滋味，百般柔情来。"  
                + "  </td>"  
                + "  </tr>"  
                + "  <tr>"  
                + "  <td colspan=\"2\">"  
               // + "     <img src=\"0.jpg\" />"  
                + "  </td>"  
                + "  </tr>"  
                + "</table>" + "</body>" + "</html>";  
        // pdfHTML specific code
        ConverterProperties converterProperties = new ConverterProperties();
        FontProvider fp = new FontProvider();
        fp.addStandardPdfFonts();
        fp.addDirectory("C:\\Windows\\Fonts\\");
//
        converterProperties.setFontProvider(fp);
        converterProperties.setBaseUri("C:\\Windows\\Fonts\\");
        
        try {
			HtmlConverter.convertToPdf(new ByteArrayInputStream(str.getBytes()), new FileOutputStream(pdfDest), converterProperties);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static void generatePDF(Integer propId,String ln_pic_location, String fileName) throws Exception {           
	       
//		//连接数据库语句
////		        Connection conn  = DriverManager.getConnection("jdbc:default:connection:");
//		        Connection conn  = DbHelper.getConnection();
//		       
//		        StringBuffer htmlString = new StringBuffer();
//		       
//		        Statement stm = conn.createStatement();
//		       
//
//		        //pre_flag
//		        String pre_flag = "";
//		        ResultSet rs = stm.executeQuery("select col_value from report_data where prop_id = "+propId+" and report_id = 'PSOBPD1' and report_level = 'SN' and LINE_SEQ =0 and col_name = 'pre_flag'");
//		        while(rs.next()){
//		            pre_flag = rs.getString("col_value");
//		        }
//		           
//		        //post_flag
//		        String post_flag = "";
//		        rs = stm.executeQuery("select col_value from report_data where prop_id = "+propId+" and report_id = 'PSOBPD1' and report_level = 'SN' and LINE_SEQ =0 and col_name = 'post_flag'");
//		        while(rs.next()){
//		            post_flag = rs.getString("col_value");
//		        }
//		           
//		        //count
//		        int par_count = 0;
//		        rs = stm.executeQuery("select count(prop_id) par_count from report_data where prop_id = "+propId+" and report_id = 'PSOBPD1' and report_level = 'SN' and LINE_SEQ !=0 and col_name = 'name'");
//		        while(rs.next()){
//		            par_count = rs.getInt("par_count");
//		        }
//		           
//		        htmlString.append("<?xml version='1.0'?><!DOCTYPE some_name [ <!ENTITY copy '&#169;'> <!ENTITY nbsp '&#160;'>]><html>");
//		        htmlString.append("<head><style>"+
//		                    "@page {"+
//		                      "size:landscape;"+
//		                      "margin-top:95pt;"+
//		                      "@top-center {content: element(header)}"+
//		                      "@bottom-center {content: element(footer)}"+
////		                      "page-break-before:always"+
//		                    "}"+
//		                    ".pageNext{page-break-after: always;}   "+
//		                    ".header1 {position: running(header);color:#cccccc;font-family: SimSun;padding-top:25pt;}"+
//		                    ".footer { position: running(footer) }"+
//		                "body{font-size:12px;font-family:Arial,sans-serif;}"+
//		                "table.data{border-top: 1px solid #333;border-bottom: 1px solid #333;width:100%;border:1px solid #333;}"+
//		                "table{ border-collapse:collapse;}"+
//		                "table td{ padding:0 0 20px 0; vertical-align:top;}"+
//		                "table.uReportStandard > thead > tr > th{ border:0.5pt #333 solid; background:#d5d0c3;color:#000;text-align:center;font-size:15px;font-family:Arial,sans-serif;font-weight:bold;}"+
//		                "table.uReportStandard > tbody > tr > td{padding:1px 2px; font-size:12px;}"+
//		                ".data td.left_text{ font-size:12px;font-family:Arial,sans-serif;width:300px;}"+
//		                ".data td.right_text{ text-align:right;font-size:12px;font-family:Arial,sans-serif;width:120px;}"+
//		                "table#uPageCols td#uRightCol,table#uPageCols td#uRightCol aside{width:0;}"+
//		                "table.uReportStandard{border:0.5px #333 solid;}"+
//		               
//		            "</style></head>");
//		        htmlString.append("<body id='praint'>");
//		        htmlString.append("<div id='uOneCol'><div>");
//		        //页眉
//		        htmlString.append("<div style='width:1000px;margin:0 auto;'><div class='header1' style='text-align:right;margin-top:0;'><div style='text-align:right;margin:0;'>"+
//		                        "<img width='240px' src='"+ln_pic_location+"'/>"+
//		                    "</div><br />");   
//		           
//		        htmlString.append("<div style='text-align:center;margin-bottom:2px;font-family:Arial,sans-serif;'>"+
//		                    "<span style='color: #7D1931;font-size:14pt;'>"+
//		                        "Supplemental Executive Retirement Plan"+
//		                    "</span><br />"+
//		                    "<span style='color: #7D1931;text-decoration: underline;font-size:18pt;'>"+
//		                        "Participant Summary of Benefit Plan Design"+
//		                    "</span>"+
//		                "</div></div>");
//		           
//		        htmlString.append("<div style=' width:100%; margin:0 auto;'>");
//		        htmlString.append("<table summary='accrualschedule' class='uReport uReportStandard' style='width:100%;margin:0 auto;'>");
//		        htmlString.append("<thead><tr><th align='center' style='color:#000;padding:25px;'>Name</th>"+
//		                            "<th align='center' style='color:#000;'>Date of Birth</th>"+
//		                            "<th align='center' style='color:#000;'>Age as of 2012-3-1</th>"+
//		                            "<th align='center' style='color:#000;'>Proj. Ret. Age</th>"+
//		                            "<th align='center' style='color:#000;padding:10px 45px;'>Benefit Formula</th>"+
//		                            "<th align='center' style='color:#000;'>Initial Annual Benefit</th>");
//		                   
//		                            if (pre_flag.equals("Yes")){
//		                                htmlString.append("<th align='center' style='color:#000;'>Pre Ret. Inflator</th>");
//		                            }
//		                            if (post_flag.equals("Yes") ){
//		                                htmlString.append("<th align='center' style='color:#000;white-space: nowrap;'>Post Ret.<br/>Inflator</th>");
//		                            }
//		                            htmlString.append("<th align='center' style='color:#000;padding:10px 45px;'>Payment Duration</th>"+
//		                            "<th align='center' style='color:#000;'>Projected Total Payments</th>"+
//		                            "<th align='center' style='color:#000;'>Liability at Proj. Ret. Date</th>"+
//		                            "<th align='center' style='color:#000;'>Discount Rate</th></tr></thead>");
//		        htmlString.append("<tbody>");   
//		           
//		        for(int i = 1;i<=par_count;i++){
//		            rs = stm.executeQuery("select col_value,col_position from report_data where prop_id = "+propId+" and report_id = 'PSOBPD1' and report_level = 'SN' and LINE_SEQ = "+i+"  order by col_position");
//		            htmlString.append("<tr class='font-size-css'>");
//		               
//		            while(rs.next()){
//		                if(rs.getInt("col_position")==1 ){
//		                    htmlString.append("<td align='left' >"+rs.getString("col_value")+"</td>");
//		                }else if(rs.getInt("col_position")==2||rs.getInt("col_position")==3||rs.getInt("col_position")==4){
//		                    htmlString.append("<td align='center' >"+rs.getString("col_value")+"</td>");
//		                }else{
//		                    htmlString.append("<td align='right' >"+rs.getString("col_value")+"</td>");
//		                }
//		            }
//		            htmlString.append("</tr>");
//		        }
//		           
//		        htmlString.append("</tbody>");
//		        htmlString.append("</table>");
//		           
//		        htmlString.append("</div></div></div></div>");
//
//		        //分页
//		        htmlString.append("<div class='pageNext'></div>");
//		           
//		        //pg2_pre_flag
//		        String pg2_pre_flag = "";
//		        rs = stm.executeQuery("select col_value from report_data where prop_id = "+propId+" and report_id = 'PSOBPD2' and report_level = 'SN' and LINE_SEQ =0 and col_name = 'pg2_pre_flag'");
//		        while(rs.next()){
//		            pg2_pre_flag = rs.getString("col_value");
//		        }
//		           
//		        //pg2_post_flag
//		        String pg2_post_flag = "";
//		        rs = stm.executeQuery("select col_value from report_data where prop_id = "+propId+" and report_id = 'PSOBPD2' and report_level = 'SN' and LINE_SEQ =0 and col_name = 'pg2_post_flag'");
//		        while(rs.next()){
//		            pg2_post_flag = rs.getString("col_value");
//		        }
//		           
//		        //count
//		        par_count = 0;
//		        rs = stm.executeQuery("select count(prop_id) par_count from report_data where prop_id = "+propId+" and report_id = 'PSOBPD2' and report_level = 'SN' and LINE_SEQ !=0 and col_name = 'pg2_name'");
//		        while(rs.next()){
//		            par_count = rs.getInt("par_count");
//		        }
//		           
//		        htmlString.append("<div id='uOneCol'><div><div style='width:1000px;margin:6pt; auto;'>");
//		           
//		        htmlString.append("<div style=' width:100%; margin:0 auto;'>");
//		        htmlString.append("<table summary='accrualschedule' class='uReport uReportStandard' style='width:100%;margin:0 auto;'>");
//		        htmlString.append("<thead><tr><th align='center' style='color:#000;padding:25px;'>Name</th>"+
//		                            "<th align='center' style='color:#000'>Date of Birth</th>"+
//		                            "<th align='center' style='color:#000'>Age as of 2012-3-1</th>"+
//		                            "<th align='center' style='color:#000'>Proj. Ret. Age</th>"+
//		                            "<th align='center' style='color:#000'>Current Salary</th>"+
//		                            "<th align='center' style='color:#000'>Salary Inflator</th>"+
//		                            "<th align='center' style='color:#000'>Proj. Final Salary</th>"+
//		                            "<th align='center' style='color:#000'>Benefit Formula</th>"+
//		                            "<th align='center' style='color:#000'>Initial Annual Benefit</th>");
//		                           
//		                            if (pg2_post_flag.equals("Yes") ){
//		                                htmlString.append("<th align='center' style='color:#000;'>Post Ret. Inflator</th>");
//		                            }
//		                            htmlString.append("<th align='center' style='color:#000;padding:10px 45px;'>Payment Duration</th>"+
//		                            "<th align='center' style='color:#000'>Projected Total Payments</th>"+
//		                            "<th align='center' style='color:#000'>Liability at Proj. Ret. Date</th>"+
//		                            "<th align='center' style='color:#000'>Discount Rate</th></tr></thead>");
//		        htmlString.append("<tbody>");   
//		           
//		           
//		        for(int i = 1;i<=par_count;i++){
//		            rs = stm.executeQuery("select col_value,col_position from report_data where prop_id = "+propId+" and report_id = 'PSOBPD2' and report_level = 'SN' and LINE_SEQ = "+i+"  order by col_position");
//		            htmlString.append("<tr class='font-size-css'>");
//		           
//		            while(rs.next()){
//		                if(rs.getInt("col_position")==1 ){
//		                    htmlString.append("<td align='left' >"+(rs.getString("col_value")!=null ?rs.getString("col_value"):"")+"</td>");
//		                }else if(rs.getInt("col_position")==2||rs.getInt("col_position")==3||rs.getInt("col_position")==4){
//		                    htmlString.append("<td align='center' >"+(rs.getString("col_value")!=null ?rs.getString("col_value"):"")+"</td>");
//		                }else{
//		                    htmlString.append("<td align='right' >"+(rs.getString("col_value")!=null ?rs.getString("col_value"):"")+"</td>");
//		                }
//		            }
//		            htmlString.append("</tr>");
//		        }
//		           
//		            htmlString.append("</tbody>");
//		            htmlString.append("</table>");
//		               
//		        htmlString.append("</div></div></div></div>");
//
//		        htmlString.append("</body>");
//		        htmlString.append("</html>");  
//		       
//
//		         //此处为将html转换为document，然后用itextRender渲染生成pdf
//		        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//		        Document doc = builder.parse(new ByteArrayInputStream(htmlString.toString().getBytes()));
//		        ITextRenderer renderer = new ITextRenderer();
//		        renderer.setDocument(doc, null);
//		        renderer.layout();
//		       
//		        // write to the output stream
////		        BLOB blob = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);
////		        OutputStream out = blob.setBinaryStream(1l);
////		        renderer.createPDF(out);
////		        out.close();
////		        return blob;
//		       
//
//		        OutputStream out = new FileOutputStream(fileName);
//		        renderer.createPDF(out);
//		        out.close();
//		        System.out.println("ok");
		    }

}
