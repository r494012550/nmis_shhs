package com.healta.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.healta.constant.ChargeStatus;
import com.healta.constant.UrgentStatus;
import com.healta.model.Studyorder;
import com.healta.model.Urgent;
import com.healta.util.DateUtil;
import com.healta.util.ResultUtil;
import com.healta.vo.ResultVO;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

/**
 *  发送webService请求
 * @author admin
 */
public class WsClient {
	
	private static Logger log = Logger.getLogger(WsClient.class);
	
	
	/**
	 *  发送 webservice 请求
	 * @param methodName  请求他们的方法名称
	 * @param xml 请求报文
	 * @param studyorderId 请求的检查id
	 */
	public ResultVO send(String address, WsOrder.Method method, String xml, Integer studyorderId, Date businessTime) {
		ResultVO resultVO = ResultUtil.fail("");
		try {
			String wsdl = PropKit.use("system.properties").get("terrace_webservice_ip") + address;
			log.info("平台 wsdl:" + wsdl);

	        OMFactory fac = OMAbstractFactory.getOMFactory();
	        //此处地址为namespace，可在wsdl里面查到
            OMNamespace omNs = fac.createOMNamespace(PropKit.use("system.properties").get("terrace_webservice_namespace"), "urn"); 
            //设置方法名
            OMElement method_e = fac.createOMElement(PropKit.use("system.properties").get("terrace_webservice_action"), omNs);
	        OMElement message = fac.createOMElement("message", omNs);
            OMElement action = fac.createOMElement("action", omNs);
            
            if (WsOrder.Method.CREATE_EXAMINATION_CRISIS.equals(method)) {
            	// 危急值
            	//设置入参值（要传入方法内的参数） 
                message.setText(xml);
            } else {
            	//设置入参值（要传入方法内的参数） 
                message.setText("<![CDATA[" + xml + "]]>");
            }
            
            action.setText(method.getMethod());
            method_e.addChild(action);
            method_e.addChild(message);
            
            method_e.build();
	        
	        //构造soap请求信息
	        StringBuffer sb = new StringBuffer("");                                                            
	        sb.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:urn=\"urn:hl7-org:v3\">");
	        sb.append("<soap:Header/>");
	        sb.append("<soap:Body>");
	        
	        // 不对<都进行转换
//	        sb.append("<urn:action>");
//	        sb.append(action);
//	        sb.append("</urn:action>");
//	        sb.append("<urn:message>");
//	        sb.append("<![CDATA[" + xml + "]]>");
//	        sb.append("</urn:message>");
	        
	        // 对<进行转换
	        sb.append(method.toString());
	        
	        sb.append("</soap:Body>");
	        sb.append("</soap:Envelope>");
//	        log.info(method.toString());
	        
	        // HttpClient发送SOAP请求             
	        log.info("HttpClient 发送SOAP请求");
	        HttpClient client = new HttpClient();
	        client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
	        client.getHttpConnectionManager().getParams().setSoTimeout(2000);
	        PostMethod postMethod = new PostMethod(wsdl);                                                      
	        // 然后把Soap请求数据添加到PostMethod中             
	        log.info(sb.toString());
	        RequestEntity requestEntity = new StringRequestEntity(sb.toString(), "text/xml", "UTF-8"); 
	        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	        log.info("此消息的 uuid 为:" + uuid);
	        postMethod.setRequestHeader("Content-type","text/xml;");
	        postMethod.setRequestHeader("rootId", uuid);
	        postMethod.setRequestHeader("token", studyorderId + "");
	        postMethod.setRequestHeader("domain", "PACS");
	        postMethod.setRequestHeader("businessTime", DateUtil.dtsWithTime(businessTime));
	        postMethod.setRequestHeader("operationTime", DateUtil.dtsWithTime(new Date()));
	        // 平台提供的授权码
	        postMethod.setRequestHeader("key", PropKit.use("system.properties").get("terrace_webservice_key"));
	        // 设置请求体    
	        postMethod.setRequestEntity(requestEntity);                                                        
	        int status = client.executeMethod(postMethod);
	        // 打印请求状态码      
	        log.info("status:" + status);
	        // 获取响应体输入流                                                                                        
	        InputStream is = postMethod.getResponseBodyAsStream();                                             
	        // 获取请求结果字符串                                                                                       
	        byte[] bytes = new byte[is.available()];
	        is.read(bytes);
	        String result = new String(bytes);
	        log.info("result: " + result);
	        
		} catch (HttpException e) {
			e.printStackTrace();
			log.info(e.getMessage());
			resultVO = ResultUtil.fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.info(e.getMessage());
			resultVO = ResultUtil.fail(e.getMessage());
		}
		return resultVO;
	}	
	/**
	 * 发送叫号方法(修改)
	 * @param methodName
	 * @param xml
	 */
	public static ResultVO sendCallNum(Integer studyorderId,String xml,Date businessTime) {
		ResultVO resultVO = ResultUtil.fail("");
		try {
			String wsdl = PropKit.use("wsconf.properties").get("call_number_path");
			log.info("wsdl:" + wsdl);
			
			String methodName = "CheckIn";
			String namespace = "http://tempuri.org/";

	        OMFactory fac = OMAbstractFactory.getOMFactory();
	        //此处地址为namespace，可在wsdl里面查到
            OMNamespace omNs = fac.createOMNamespace(namespace, "tem"); 
            //设置方法名
            OMElement method = fac.createOMElement(methodName, omNs);
	        OMElement sInput = fac.createOMElement("sInput", omNs);
            
        	//设置入参值（要传入方法内的参数） 
	        sInput.setText("<![CDATA[" + xml + "]]>");
            method.addChild(sInput);
            
            method.build();
	        
	        //构造soap请求信息
	        StringBuffer sb = new StringBuffer("");                                                            
	        sb.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"" + namespace + "\">");
	        sb.append("<soap:Header/>");
	        sb.append("<soap:Body>");
	        sb.append(method.toString());
	        sb.append("</soap:Body>");
	        sb.append("</soap:Envelope>");
	        log.info(method.toString());
	        
	        // HttpClient发送SOAP请求             
	        PostMethod postMethod = new PostMethod(wsdl);                                                      
	        // 然后把Soap请求数据添加到PostMethod中             
	        log.info(sb.toString());

	        RequestEntity requestEntity = new StringRequestEntity(sb.toString(), "text/xml", "UTF-8"); 
	        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	        log.info("此消息的 uuid 为:" + uuid);
	        postMethod.setRequestHeader("Content-type","text/xml;");
	        // 设置请求体    
	        postMethod.setRequestEntity(requestEntity);                                                        
//	        int status = client.executeMethod(postMethod);
	        String result = post(postMethod,sb.toString());
	        // 打印请求状态码      
	        log.info("result:" + result);
	        resultVO = ResultUtil.success(result);
		}	catch (IOException e) {
			log.info(e.getMessage());
			e.printStackTrace();			
			resultVO = ResultUtil.fail(e.getMessage());
		}
		return resultVO;
	}
	
	/**
	 *  发送确费消息（走平台，由平台进行转发）
	 * @param address
	 * @param xml
	 * @param studyorderId
	 * @return
	 */
	public static ResultVO sendFeeConfirm(Integer studyorderId,String xml,Date businessTime) {
		ResultVO resultVO = ResultUtil.fail("");
		try {
			String wsdl = PropKit.use("wsconf.properties").get("terrace_webservice_ip") + WsOrder.Method.PACSFEE_CONFIRM.getEndpoint();
			log.info("平台确费地址wsdl:" + wsdl);
			
	        //构造soap请求信息
	        StringBuffer sb = new StringBuffer("");                                                            
	        sb.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
	        sb.append("<soap:Body>");
	        sb.append("<Process xmlns=\"http://tempuri.org/\">");
	        sb.append("<transCode>");
	        sb.append(PropKit.use("wsconf.properties").get("fee_confirm_transcode"));
	        sb.append("</transCode>");
	        sb.append("<xml>");
	        sb.append("<![CDATA[" + xml + "]]>");
	        sb.append("</xml>");
	        sb.append("</Process>");
	        sb.append("</soap:Body>");
	        sb.append("</soap:Envelope>");
	        
	        // HttpClient发送SOAP请求             
	        PostMethod postMethod = new PostMethod(wsdl);                                                      
	        // 然后把Soap请求数据添加到PostMethod中             
	        log.info(sb.toString());
	        postMethod.setRequestHeader("Content-type","text/xml;");
	        postMethod.setRequestHeader("rootId", StrKit.getRandomUUID());
	        postMethod.setRequestHeader("token", studyorderId + "");
	        postMethod.setRequestHeader("domain", "PACS");
	        postMethod.setRequestHeader("businessTime", DateUtil.dtsWithTime(businessTime));
	        postMethod.setRequestHeader("operationTime", DateUtil.dtsWithTime(new Date()));
	        // 平台提供的授权码
	        postMethod.setRequestHeader("key", PropKit.use("wsconf.properties").get("terrace_webservice_key"));
                                                      
	        String result = post(postMethod,sb.toString());
	        log.info("result: " + result);
	        if(result!=null) {
		        // 医院确费消息
				Document inputDoc = DocumentHelper.parseText(result);
				Element root = inputDoc.getRootElement();
				Element element = root.element("Body").element("result");
				String resultType = element.element("ResultCode").getText();
				String resultMsg = element.element("ResultMsg").getText();
				
				if ("1".equals(resultType)) {
					// 请求成功
					Studyorder studyorder = Studyorder.dao.findById(studyorderId);
					if (studyorder != null) {
						studyorder.setChargeStatus(ChargeStatus.CONFIRM);
						if(studyorder.update())
							resultVO = ResultUtil.success();
					}
				} else {
					// 确费失败 将失败原因记录下来
					Studyorder studyorder = Studyorder.dao.findById(studyorderId);
					if (studyorder != null && !ChargeStatus.CONFIRM.equals(studyorder.getChargeStatus())) {
						studyorder.setChargeStatus(ChargeStatus.FAILURE);
						studyorder.update();
					}
					resultVO = ResultUtil.fail(resultType + "; " + resultMsg);
				}
	        }
		}  catch (DocumentException e) {
			log.info(e.getMessage());
			e.printStackTrace();
			resultVO = ResultUtil.fail(e.getMessage());
		}
		return resultVO;
	}
	
	/**
	 *  发送确费消息（不走平台，直接调用医院原先的his确费接口）
	 * @param xml
	 * @param studyorderId
	 */
	public static ResultVO sendFeeConfirmOld(Integer studyorderId,String xml) {
		ResultVO resultVO = ResultUtil.fail("");
		try {
			String wsdl = PropKit.use("wsconf.properties").get("fee_confirm_path");
			log.info("wsdl:" + wsdl);
			
	        //构造soap请求信息
	        StringBuffer sb = new StringBuffer("");                                                            
	        sb.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
	        sb.append("<soap:Body>");
	        sb.append("<Process xmlns=\"http://tempuri.org/\">");
	        sb.append("<transCode>");
	        sb.append(PropKit.use("wsconf.properties").get("fee_confirm_transcode"));
	        sb.append("</transCode>");
	        sb.append("<xml>");
	        sb.append("<![CDATA[" + xml + "]]>");
	        sb.append("</xml>");
	        sb.append("</Process>");
	        sb.append("</soap:Body>");
	        sb.append("</soap:Envelope>");
	        log.info(sb.toString());
	        String result = post(wsdl,sb.toString());
	        log.info("result: " + result);
	        if(result!=null) {
		        // 医院确费消息
				Document inputDoc = DocumentHelper.parseText(result);
				Element root = inputDoc.getRootElement();
				Element element = root.element("Body").element("ProcessResponse").element("ProcessResult");
				Document doc = DocumentHelper.parseText(element.getText());
				root = doc.getRootElement();
				String resultType = root.element("ResultCode").getText();
				String resultMsg = root.element("ResultMsg").getText();
				Studyorder studyorder = Studyorder.dao.findById(studyorderId);
				if ("1".equals(resultType)) {// 请求成功
					if (studyorder != null) {
						studyorder.setChargeStatus(ChargeStatus.CONFIRM);
						if(studyorder.update()) {
							resultVO = ResultUtil.success();
						}
					}
				} else {// 确费失败 将失败原因记录下来
					if (studyorder != null && !ChargeStatus.CONFIRM.equals(studyorder.getChargeStatus())) {
						studyorder.setChargeStatus(ChargeStatus.FAILURE);
						studyorder.update();
					}
					resultVO = ResultUtil.fail(resultType + "; " + resultMsg);
				}
	        }
		} catch (DocumentException e) {
			log.info(e.getMessage());
			e.printStackTrace();
			resultVO = ResultUtil.fail(e.getMessage());
		}
		return resultVO;
	}
	
	/**
	 *  发送 报告信息
     * @param studyorderId 请求的检查id
	 * @param xml 请求报文
	 */
	public static ResultVO sendRegisterReport(Integer studyorderId,String xml, Date businessTime) {
		ResultVO resultVO = ResultUtil.fail("");
		try {
			String wsdl = PropKit.use("wsconf.properties").get("terrace_webservice_ip")+WsOrder.Method.REGISTER_REPORT.getEndpoint();
			log.info("平台 wsdl:" + wsdl);
			String method=WsOrder.Method.REGISTER_REPORT.getMethod();
	        OMFactory fac = OMAbstractFactory.getOMFactory();
	        //此处地址为namespace，可在wsdl里面查到
            OMNamespace omNs = fac.createOMNamespace(PropKit.use("wsconf.properties").get("terrace_webservice_namespace"), "urn"); 
            //设置方法名
            OMElement method_e = fac.createOMElement(PropKit.use("wsconf.properties").get("terrace_webservice_action"), omNs);
	        OMElement message = fac.createOMElement("message", omNs);
            OMElement action = fac.createOMElement("action", omNs);
            
            //设置入参值（要传入方法内的参数） 
            message.setText("<![CDATA[" + xml + "]]>");
            
            action.setText(method);
            method_e.addChild(action);
            method_e.addChild(message);
            
            method_e.build();
	        
	        //构造soap请求信息
	        StringBuffer sb = new StringBuffer("");                                                            
	        sb.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:urn=\"urn:hl7-org:v3\">");
	        sb.append("<soap:Header/>");
	        sb.append("<soap:Body>");
	        sb.append(method_e.toString());
	        
	        sb.append("</soap:Body>");
	        sb.append("</soap:Envelope>");
	        
	        PostMethod postMethod = new PostMethod(wsdl);                                                      
	        // 然后把Soap请求数据添加到PostMethod中             
	        log.info(sb.toString());
	        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	        log.info("此消息的 uuid 为:" + uuid);
	        postMethod.setRequestHeader("Content-type","text/xml;");
	        postMethod.setRequestHeader("rootId", uuid);
	        postMethod.setRequestHeader("token", studyorderId + "");
	        postMethod.setRequestHeader("domain", "PACS");
	        postMethod.setRequestHeader("businessTime", DateUtil.dtsWithTime(businessTime));
	        postMethod.setRequestHeader("operationTime", DateUtil.dtsWithTime(new Date()));
	        // 平台提供的授权码
	        postMethod.setRequestHeader("key", PropKit.use("wsconf.properties").get("terrace_webservice_key"));  
	        
	        String result = post(postMethod,sb.toString());
	        if(result!=null) {
	        	resultVO = ResultUtil.success();
	        }
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			resultVO = ResultUtil.fail(e.getMessage());
		}
		return resultVO;
	}
	
	
	
	
	
	public static String post(PostMethod postMethod,String xml) {
		String ret=null;
		InputStream is=null;
		try {
			HttpClient client = new HttpClient();
	        client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
	        client.getHttpConnectionManager().getParams().setSoTimeout(2000);                                                    
	        // 然后把Soap请求数据添加到PostMethod中             
	        RequestEntity requestEntity = new StringRequestEntity(xml, "text/xml", "UTF-8");
	        // 设置请求体    
	        postMethod.setRequestEntity(requestEntity);                                                        
	        int status = client.executeMethod(postMethod);
	        if(status==HttpStatus.SC_OK) {
	        	// 获取响应体输入流                                                                                        
		        is = postMethod.getResponseBodyAsStream();                                
		        // 获取请求结果字符串                                                                                       
		        byte[] bytes = new byte[is.available()];
		        is.read(bytes);
		        ret = new String(bytes, "utf-8");
	        } else {
	        	// 打印请求状态码      
		        log.info("Post request failed,status:" + status);
	        }
		} catch (UnsupportedEncodingException e) {
			log.error("Post request failed:"+postMethod.getPath()+",error message:"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Post request failed:"+postMethod.getPath()+",error message:"+e.getMessage());
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return ret;
	}
	
	// HttpClient发送SOAP请求             
	public static String post(String url,String xml) {
		String ret=null;
		InputStream is=null;
		try {
			HttpClient client = new HttpClient();
	        client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
	        client.getHttpConnectionManager().getParams().setSoTimeout(2000);
	        PostMethod postMethod = new PostMethod(url);                                                      
	        // 然后把Soap请求数据添加到PostMethod中             
	        RequestEntity requestEntity = new StringRequestEntity(xml, "text/xml", "UTF-8");
	        postMethod.setRequestHeader("Content-type","text/xml;");
	        // 设置请求体    
	        postMethod.setRequestEntity(requestEntity);                                                        
	        int status = client.executeMethod(postMethod);
	        if(status==HttpStatus.SC_OK) {
	        	// 获取响应体输入流                                                                                        
		        is = postMethod.getResponseBodyAsStream();                                             
		        // 获取请求结果字符串                                                                                       
		        byte[] bytes = new byte[is.available()];
		        is.read(bytes);
		        ret = new String(bytes, "utf-8");
	        } else {
	        	// 打印请求状态码      
		        log.info("Post request failed,status:" + status);
	        }
		} catch (UnsupportedEncodingException e) {
			log.error("Post request failed:"+url+",error message:"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Post request failed:"+url+",error message:"+e.getMessage());
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return ret;
	}
	
//	public Element handleResult(String result) {
//		try {
//			Document inputDoc = DocumentHelper.parseText(result);
//			Element root = inputDoc.getRootElement();
//			log.info(root);
//			Element element = root.element("Body").element("ProcessResponse").element("ProcessResult");
//			log.info(element.getText());
//			Document doc = DocumentHelper.parseText(element.getText());
//			root = doc.getRootElement();
//
//			String resultType = root.element("ResultCode").getText();
//			String resultMsg = root.element("ResultMsg").getText();
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	/**
	 * 调用自己，测试用
	 * @param methodName
	 * @param xml
	 */
	public void send1(String methodName, String xml) {
		log.info("请求的方法为：" + methodName);
		
		// webService接口地址
		EndpointReference targetEPR = new EndpointReference("http://127.0.0.1:8080/report/services/RISWebService");
		
        Options options = new Options();
        options.setAction(methodName); //调用接口方法
        
        options.setTo(targetEPR);
        options.setTimeOutInMilliSeconds(600 * 1000);
        options.setProperty(HTTPConstants.SO_TIMEOUT, 600 * 1000);
        ServiceClient sender = null;
        try {
            sender = new ServiceClient();
            sender.setOptions(options);
            OMFactory fac = OMAbstractFactory.getOMFactory();
            //此处地址为namespace，可在wsdl里面查到
            OMNamespace omNs = fac.createOMNamespace("http://webservice.healta.com", ""); 
            //设置方法名
            OMElement method = fac.createOMElement(methodName, omNs);
            
            OMElement header = fac.createOMElement("Content-type", "text/xml;", "");
            //设置入参名称，如有多个参数可创建多个OMElement对象，如name1、name2。。
            OMElement input = fac.createOMElement("input", omNs);
            
        	//设置入参值（要传入方法内的参数） 
            input.setText(xml);
            method.addChild(input);
            
            method.build();
            
            log.info(method.toString());
            sender.addHeader(header);
            
            OMElement response = sender.sendReceive(method);  
            
            log.info("==================");
            OMElement elementReturn = response.getFirstElement();
            //可用此方法elementReturn.getText()，来获取被调方法的返回值
            String result = elementReturn.getText();
            log.info("=========result=========" + result);
            log.info("==================");
            response = sender.sendReceive(method);  
        } catch (AxisFault e) {
            log.info("请求失败："+ e.getMessage()); 
            e.printStackTrace();  
        }  
	}
	
	public static void main(String[] args) throws DocumentException {
//		String result = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
//				+ "		<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
//				+ "		<soapenv:Body><result>\r\n"
//				+ "		<ResultCode>-2</ResultCode>\r\n"
//				+ "		<ResultMsg>未查询到有效的患者费用信息，已超时</ResultMsg>\r\n"
//				+ "		</result></soapenv:Body>\r\n"
//				+ "		</soapenv:Envelope>";
//		
//		Document inputDoc = DocumentHelper.parseText(result);
//		Element root = inputDoc.getRootElement();
//		Element element = root.element("Body").element("result");
//		String resultType = element.element("ResultCode").getText();
//		String resultMsg = element.element("ResultMsg").getText();
		
		
//		System.out.println(StrKit.getRandomUUID());
//		System.setProperty("javax.net.ssl.keyStore", "d:\\ssl\\ws.jks");
//        System.setProperty("javax.net.ssl.keyStorePassword", "admin");
//        System.setProperty("javax.net.ssl.keyStoreType", "JKS");
//        System.setProperty("javax.net.ssl.trustStore", "d:\\ssl\\ws.jks");
//        System.setProperty("javax.net.ssl.trustStorePassword", "admin");
//        System.setProperty("javax.net.ssl.trustType", "JKS");
		
	}

}
