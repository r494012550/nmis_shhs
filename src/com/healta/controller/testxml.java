package com.healta.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;

import com.healta.cda.Entry_Observation;
import com.healta.cda.Section;

import groovy.lang.TracingInterceptor;
import sun.util.logging.resources.logging;

public class testxml {
	
	
	public static void Transform(String xmlcontent, String xslFileName, String htmlFileName) {  
        try {  
            TransformerFactory tFac = TransformerFactory.newInstance();  
            Source xslSource = new StreamSource(xslFileName);  
            Transformer t = tFac.newTransformer(xslSource);  
//            File xmlFile = new File(xmlFileName);  
            File htmlFile = new File(htmlFileName);  
//            Source source = new StreamSource(xmlFile);
            
            StringReader in=new StringReader(xmlcontent);
            Source source1 = new StreamSource(in);
            
            Result result = new StreamResult(htmlFile);
            t.transform(source1, result);
            
			String content = FileUtils.readFileToString(htmlFile);
			System.out.println(content);
//			FileUtils.deleteQuietly(htmlFile);
            
        } 
        catch (TransformerConfigurationException e) {  
            e.printStackTrace();  
        } 
        catch (TransformerException e) {  
            e.printStackTrace();  
        }
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public static List ReadCDA(){
		SAXReader reader = new SAXReader();
		List docs=new ArrayList();
		try {
			Document doc=reader.read(new File("D:\\cda.xml"));
			Element root = doc.getRootElement();
			Element com=(Element)root.element("component").element("structuredBody").elements("component").get(1);

			Element sec=com.element("section");
			List entries=sec.elements("entry");
			Document finding=null;
			
			for(int i=0;i<entries.size();i++){
                Element en=(Element)entries.get(i);
                //提取图片
                Element e_image=en.element("observationMedia");
                if(e_image!=null){
//                	System.out.println(e_image.attributeValue("ID"));
					FileUtils.writeByteArrayToFile(new File("D:\\image\\"+e_image.attributeValue("ID")+".png"), Base64.decodeBase64(e_image.element("value").getText()));
                }
                
                //提取finding
                Element obs=en.element("observation");
                if(obs!=null){
                	
                	if("FindingType".equals(obs.element("code").attributeValue("code"))){
                		System.out.println(obs.element("value").getText());
                		if(finding!=null){
//                			System.out.println(finding.asXML());
                			docs.add(finding);
                		}
                		
                		finding=DocumentHelper.createDocument();
                    	Element roote=finding.addElement("entries","urn:hl7-org:v3").addNamespace("voc", "urn:hl7-org:v3/voc").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//                    	System.out.println(en.declaredNamespaces());
                    	roote.add((Element)en.clone());
                    	
                    	
                	}
                	else{
	                	if(finding!=null){
	                		finding.getRootElement().add((Element)en.clone());
	                	}
                	}
                }
                
                
			}
			if(finding!=null){
//    			System.out.println(finding.asXML());
    			docs.add(finding);
    		}
//			System.out.println(docs.size());
			
		} 
		catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return docs;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String xmlFileName = "C:\\Users\\Administrator\\Desktop\\Untitled2.xml";  
//        String xslFileName = "C:\\Users\\Administrator\\Desktop\\Untitled3.xslt";  
//        String htmlFileName = "C:\\Users\\Administrator\\Desktop\\Untitled4.html";  
//        
//		
//		List findings=ReadCDA();
//		
//		Document doc=(Document)findings.get(0);
//		
////		Element root=doc.getRootElement();
////		System.out.println(root.remove(Namespace.get("voc","urn:hl7-org:v3")));
////		System.out.println(root.asXML());
//		Transform(doc.asXML(), xslFileName, htmlFileName);
		
		
//		System.out.println( Pattern.matches(".*CT.*", "腹部螺旋CT增强"));
		
//		File file=new File("D:\\ris\\20180404\\00000002\\00000002.xml");
//		System.out.println(file.getAbsolutePath());
//		System.out.println(file.getPath());
//		System.out.println(file.getParent());
		
		
//		String str="afsdf alt=\"img\">";
//		
//		System.out.println(str.replaceAll(str, "alt=\"img\"/>"));
//		Pattern   PATTERN   =   Pattern.compile("<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)",   Pattern.CASE_INSENSITIVE   |   Pattern.MULTILINE);  
//		
//		String Str = new String("<p ></p><img src='dd' style='' alt=\"img\">sdf<img src='dd' alt=\"img\"/>sdf<img src='dd' alt=\"img\">fdfasdfs");
//		System.out.println(Str);
//		
//		
////		 Matcher   matcher   =   PATTERN.matcher(Str);   
////	        
////	        while   (matcher.find())   {   
////	            String   group   =   matcher.group(1);   
////	            System.out.println(group);
////	        }
////		
//
////        System.out.print("匹配成功返回值 :" );
//        System.out.println(Str.replaceAll("(?=alt=\"img\")(?:[^>]*)([>])", "alt=\"img\"/>" ));
//        System.out.print("匹配失败返回值 :" );
//        System.out.println(Str.replaceAll("(.*)taobao(.*)", "runoob" ));&gt;
//		String string="\T\gt;";
//		System.out.println("\\T\\gt;");
		
//		System.out.println(Pattern.matches(".*Cardiac.*", "Cardiac^0__Seq_Cardiac_Bolus1 (Adult)"));
		
		
//		String str="gafdasdfasdf_ueditor_page_break_tag_fasdfa_ueditor_page_break_tag_sfsa";
//		str=str.replaceAll("_ueditor_page_break_tag_", "<div class='pageNext'></div>");
//		
//		System.out.println(str);
		
//		String ss="冠脉起始：</span><select name=\"newSelectField\" code=\"0001\" id=\"1\" selected_index=\"0\" "
//				+ "multiple=\"multiple\" size=\"2\" onchange=\"var values=[];var codes=[];for(var i=0;i&lt;this.options.length;i++)"
//				+ "{if(this.options[i].selected){values.push(this.options[i].value);codes.push(this.options[i].getAttribute(&#39;code&#39;));"
//				+ "}}this.setAttribute(&#39;gvalue&#39;,values);this.setAttribute(&#39;gvaluecode&#39;,codes);"
//				+ "this.setAttribute(&#39;hasChange&#39;,&#39;1&#39;);\" gvalue=\"\" gvaluecode=\"\" style=\"width: 100px;\" "
//				+ "haschange=\"\"><option value=\"如常\" code=\"0002\">如常</option><option value=\"变异\" code=\"0003\">变异</option>"
//				+ "</select><span style=\"font-family: 宋体, SimSun; font-size: 14px;\">&nbsp; &nbsp;&nbsp;冠脉优势："
//				+ "<select name=\"newSelectField\" code=\"0004\" id=\"2\" uid=\"UUID1\" selected_index=\"0\" "
//				+ "onchange=\"this.setAttribute(&#39;gvalue&#39;,this.options[this.selectedIndex].value);"
//				+ "this.setAttribute(&#39;gvaluecode&#39;,this.options[this.selectedIndex].getAttribute(&#39;code&#39;));"
//				+ "this.setAttribute(&#39;hasChange&#39;,&#39;1&#39;);this.setAttribute(&#39;selected_index&#39;,this.selectedIndex);\" "
//				+ "gvalue=\"\" gvaluecode=\"\" style=\"height: 22px; width: 100px;\" haschange=\"\">"
//				+ "<option value=\"\">&nbsp;</option><option value=\"均衡型\" code=\"0005\">均衡型</option><option value=\"右侧优势型\" "
//				+ "code=\"0006\">右侧优势型</option><option value=\"左侧优势型\" code=\"0007\">左侧优势型</option></select>"
//				+ "&nbsp; &nbsp;&nbsp;左主干病变/三支病变：<select name=\"newSelectField\" code=\"0008\" id=\"3\" uid=\"UUID2\" "
//				+ "selected_index=\"0\" onchange=\"this.setAttribute(&#39;gvalue&#39;,this.options[this.selectedIndex].value);"
//				+ "this.setAttribute(&#39;gvaluecode&#39;,this.options[this.selectedIndex].getAttribute(&#39;code&#39;));"
//				+ "this.setAttribute(&#39;hasChange&#39;,&#39;1&#39;);this.setAttribute(&#39;selected_index&#39;,this.selectedIndex);\" ";
//				//+ "gvalue="" gvaluecode="" style="height: 22px; width: 100px;" haschange=""><option value="">&nbsp;</option><option value="是" code="0009">是</option><option value="否" code="0010">否</option></select></span></p><p><span style="font-size: 14px;"></span></p><p><span style="font-size: 14px;"></span></p><hr/><table align="left"><tbody><tr class="firstRow"><td valign="top" "
//		
		String s = "100;numberof_clientconcurrency:10;modules:[user],[patient],[worklist],[report];";
		// 把要匹配的字符串写成正则表达式，然后要提取的字符使用括号括起来
		// 在这里，我们要提取最后一个数字，正则规则就是“一个数字加上大于等于0个非数字再加上结束符”
		//Pattern pattern = Pattern.compile("(\\d)[^\\d]*$");
		Pattern pattern = Pattern.compile("numberof_clientconcurrency:\\d+");
//		Pattern pattern = Pattern.compile("modules:.+;?");
		Matcher matcher = pattern.matcher(s);
		if(matcher.find()){
			
//			System.out.println(matcher.);
			System.out.println(matcher.group());
		}
		
		
//		System.out.println(StringUtils.trimToEmpty("dfsd dffd"));
		
		
	}

}
