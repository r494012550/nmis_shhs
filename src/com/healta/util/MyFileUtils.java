package com.healta.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

//import jcifs.UniAddress;
//import jcifs.smb.NtlmPasswordAuthentication;
//import jcifs.smb.SmbException;
//import jcifs.smb.SmbFile;
//import jcifs.smb.SmbFileInputStream;
//import jcifs.smb.SmbFileOutputStream;
//import jcifs.smb.SmbSession;

public class MyFileUtils {
	
	
	public static HashMap MAP=new HashMap();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		File file = new File("");
//		try {
//			System.out.println(file.getCanonicalPath());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		//System.out.println(copyFile(new File("D:/test/004/000/000.jpg"),new File("D:/work1/jboss-4.0.5.GA/server/default/deploy/IIP.war/app/pic")));
		
		
		//smb://administrator:Admin123@192.168.23.137/PatientFile/cs/cs/2014-01/1203/1203.jpg
		
//		 String smbMachine="smb://administrator:Admin123@192.168.23.137/PatientFile/cs/cs/2014-01/1203/1203.jpg";  
//         
//         String localPath="smb://administrator:adm$pwd$4$med@192.168.23.174/cifspacs02";  
         
//         smb://administrator:adm$pwd$4$med@192.168.23.174/cifspacs02
         
         
         try {
//			FileUtil.moveRemoteFile(smbMachine, localPath);
        	 
//        	 FileUtil.readRemoteFile(smbMachine);
        	 
//        	 String temp="PatientFile/cs/cs/2014-01/1203/1203.jpg";
//        	 System.out.println("\\"+temp.substring(0, temp.lastIndexOf("/")));
        	 
//        	 zipFiles("D:\\rebound\\temp\\44CE611C","D:\\rebound\\temp\\test.zip");
        	 
//        	 String dic_str="D:\\rebound\\temp\\22222222_"+System.currentTimeMillis();
//     		
//     		File dic=new File(dic_str);
//     		if(!dic.exists()){
//     			dic.mkdirs();
//     		}
        	 
        	 String file="\\127.0.0.1/2016/11/20/17/FC2144AA/44CEB6DD-1690531.tar!44CEB6DD/00012006";
        	 System.out.println(file.indexOf("\\\\"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static boolean move(String srcFile, String destPath){ 
		boolean success=false;
		try{
			// File (or directory) to be moved 
			File file = new File(srcFile); 
	
			// Destination directory 
			File dir = new File(destPath); 
	
			// Move file to new directory 
			success = file.renameTo(new File(dir, file.getName())); 
		
		}
		catch(Exception ex){
			ex.printStackTrace();
		}

		return success; 
	} 
	
	
	public static byte[] getBytesFromFile(String src){
		byte[] ret=null;
		
        if (src == null){
            return ret;
        }
        try{
//            FileInputStream stream = new FileInputStream(f);
//            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
//            byte[] b = new byte[1000];
//            for (int n;(n = stream.read(b)) != -1;) {
//            	out.write(b, 0, n);
//            }
//            stream.close();
//            out.close();
            
            
            File file=new File(src);
            if(file!=null){
            	FileInputStream fis=new FileInputStream (file);
            	if(fis!=null){
            		int len=fis.available();
            		ret=new byte[len];
            		fis.read(ret);
            		fis.close();
            		 
            	}
            }
            
//            return out.toByteArray();
        } catch (IOException e){
        	
        }
        return ret;
    }
	
	public static byte[] zipFiles(String directory,String zipfilename){
		byte[] ret=null;
//		System.out.println(directory);
        if (directory == null||zipfilename==null){
            return ret;
        }
        try{
        	 File file = new File(directory);  
             File zipFile = new File(zipfilename);  
             InputStream input = null;  
             ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(  
                     zipFile));  
             zipOut.setComment("hello");  
//             System.out.println(file.isDirectory());
             if(file.isDirectory()){  
                 File[] files = file.listFiles();  
                 for(int i = 0; i < files.length; ++i){  
                     
                     
                     BufferedInputStream bis = new BufferedInputStream(  
                             new FileInputStream(files[i]));  
                     ZipEntry entry = new ZipEntry(file.getName()  
                             + File.separator + files[i].getName());  
                     zipOut.putNextEntry(entry);  
                     int count;  
                     byte data[] = new byte[20192];  
                     while ((count = bis.read(data, 0, 20192)) != -1) {  
                    	 zipOut.write(data, 0, count);  
                     }  
                     bis.close();  
                 }  
             }  
             zipOut.close();  
        } catch (IOException e){
        	
        }
        return ret;
    }
	
	public static byte[] zipFiles(ArrayList<String> files,String zipfilename){
		byte[] ret=null;
//		System.out.println(directory);
        if (files == null||zipfilename==null){
            return ret;
        }
        try{
        	   
             File zipFile = new File(zipfilename);  
             InputStream input = null;  
             ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(  
                     zipFile));  
//             zipOut.setComment("hello");  
  
                 for(int i = 0; i < files.size(); ++i){  
                     
                     File file=new File(files.get(i));
                     BufferedInputStream bis = new BufferedInputStream(  
                             new FileInputStream(file));  
                     ZipEntry entry = new ZipEntry(file.getName());  
                     zipOut.putNextEntry(entry);  
                     int count;  
                     byte data[] = new byte[20192];  
                     while ((count = bis.read(data, 0, 20192)) != -1) {  
                    	 zipOut.write(data, 0, count);  
                     }  
                     bis.close();  
                 }  
 
             zipOut.close();  
        } catch (IOException e){
        	
        }
        return ret;
    }
	
	
//	public static byte[] readRemoteFile(String filename,String path,String username,String pwd) throws Exception{
//		byte[] ret=null;
//		
//        if (filename == null){
//            return ret;
//        }
//        
//        InputStream bis=null;
//        try{
//
//        	filename="smb://"+username+":"+pwd+"@"+path+"/"+filename;;//"smb://"+path+"/"+filename;
//        	
//            System.out.println(filename);
//             
//            String ip=path;
//            if(ip.indexOf("/")>=0){
//            	ip=ip.substring(0, ip.indexOf("/"));
//            }
////             NtlmPasswordAuthentication authentication =(NtlmPasswordAuthentication)MAP.get(ip);
////             if(authentication==null){
////            	 UniAddress dc = UniAddress.getByName(ip);  
////                 authentication = new NtlmPasswordAuthentication(ip, username, pwd);  
////                 SmbSession.logon(dc, authentication);
////                 
////                 MAP.put(ip, authentication);
////             }
//             
//             
//        	 SmbFile rmifile = new SmbFile(filename);//,authentication);  
////             String filename=rmifile.getName();     
//             bis=new BufferedInputStream(new SmbFileInputStream(rmifile)); 
//             int length=rmifile.getContentLength(); 
//             ret=new byte[length];     
//             
//             bis.read(ret); 
//   
//        } 
//        catch(UnknownHostException ex){
//        	ex.printStackTrace();
//        	throw new Exception(ex.getMessage());
//        }
//        catch(MalformedURLException ex){
//        	ex.printStackTrace();
//        	throw new Exception(ex.getMessage());
//        }
//        catch (IOException e){
//        	e.printStackTrace();
//        	throw new Exception(e.getMessage());
//        }
//        finally{     
//            try { 
//            	if(bis!=null)
//                bis.close();     
//            } catch (IOException e) {     
//                e.printStackTrace();     
//            }                 
//        }    
//        return ret;
//    }
	
	
//	public static boolean moveRemoteFile(String smbMachine,String remotepath) throws Exception{   
//		
//		boolean ret=true;
//		SmbFile localfile=null;     
//        InputStream bis=null;     
//        OutputStream bos=null;     
//        try{     
//        	
//        	System.out.println("smbMachine=="+smbMachine); 
//        	Date date=new Date();     
//            SmbFile rmifile = new SmbFile(smbMachine);     
//            String filename=rmifile.getName();     
//            bis=new BufferedInputStream(new SmbFileInputStream(rmifile));
//            
//            System.out.println("remotepath=="+remotepath);  
//            
////            UniAddress dc = UniAddress.getByName("192.168.23.130");  
////            NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication("192.168.23.130", "administrator", "adm$pwd$4$med");  
////            SmbSession.logon(dc, authentication);
//            
//            
//            
//            localfile=new SmbFile(remotepath);
//            
//            
//            if(!localfile.exists()){
//            	localfile.mkdirs();
//            }
//            System.out.println("localfile=="+remotepath+"/"+filename);  
//            localfile=new SmbFile(remotepath+"/"+filename);
//           
//            
//            bos=new BufferedOutputStream(new SmbFileOutputStream(localfile)); 
//            int length=rmifile.getContentLength();  
//            System.out.println("length=="+length);  
//            byte[] buffer=new byte[length];  
//            bis.read(buffer);    
//            bos.write(buffer);   
//  
//            Date end=new Date();     
//            int time= (int) ((end.getTime()-date.getTime())/1000);     
//            if(time>0)     
//                System.out.println("用时:"+time+"秒 "+"速度:"+length/time/1024+"kb/秒");                 
//        } 
//        catch(UnknownHostException ex){
//        	ex.printStackTrace();
//        	throw new Exception(ex.getMessage());
//        }
//        catch(MalformedURLException ex){
//        	ex.printStackTrace();
//        	throw new Exception(ex.getMessage());
//        }
//        catch (Exception e){   
//        	ret=false; 
//        	e.printStackTrace();
//        	throw new Exception(e.getMessage());
//        }
//        finally{     
//            try {     
//                bos.close();     
//                bis.close();     
//            } catch (IOException e) {     
//                e.printStackTrace();     
//            }                 
//        }     
//        return ret;     
//    }     
	
	
	public static long copyFile(String src,String dest){
		   long copySizes = 0;
		   
		   File srcFile=new File(src);
		   File destDir=new File(dest);
		   if(!srcFile.exists()){
		    System.out.println("source file do not exsit!");
		    copySizes = -1;
		   }
		   else if(!destDir.exists()){
		    System.out.println("dest directory do not exsit!");
		    copySizes = -1;
		   }
		   
		   else{
		    try {
		     FileChannel fcin = new FileInputStream(srcFile).getChannel();
		     FileChannel fcout = new FileOutputStream(
		           new File(destDir,srcFile.getName())).getChannel();
//		     ByteBuffer buff = ByteBuffer.allocate(1024);
//		     int b = 0 ,i = 0;
//		     long t1 = System.currentTimeMillis();
		     /*while(fcin.read(buff) != -1){
		      buff.flip();
		      fcout.write(buff);
		      buff.clear();
		      i++;
		     }*/
		     long size = fcin.size();
		     fcin.transferTo(0,fcin.size(),fcout);
		     //fcout.transferFrom(fcin,0,fcin.size());
		     //一定要分清哪个文件有数据，那个文件没有数据，数据只能从有数据的流向
		     //没有数据的文件
//		     long t2 = System.currentTimeMillis();
		     fcin.close();
		     fcout.close();
		     copySizes = size;
//		     long t = t2-t1;
//		     System.out.println("复制了" + i + "个字节\n" + "时间" + t);
//		     System.out.println("复制了" + size + "个字节\n" + "时间" + t);
		    } catch (FileNotFoundException e) {    
		     e.printStackTrace();
		    } catch (IOException e) {
		     e.printStackTrace();
		    }
		   }
		   return copySizes;
		}
		 

	public static long copyFile(String src,String dest,String destname){
		   long copySizes = 0;
		   
		   File srcFile=new File(src);
		   File destDir=new File(dest);
		   
		   if(!srcFile.exists()){
		    System.out.println("source file do not exsit!");
		    copySizes = -1;
		   }
		   else if(!destDir.exists()){
		    System.out.println("dest directory do not exsit!");
		    copySizes = -1;
		   }
		   
		   else{
		    try {
		     FileChannel fcin = new FileInputStream(srcFile).getChannel();
		     FileChannel fcout = new FileOutputStream(
		           new File(destDir,destname)).getChannel();
		     ByteBuffer buff = ByteBuffer.allocate(1024);
		     int b = 0 ,i = 0;
//		     long t1 = System.currentTimeMillis();
		     /*while(fcin.read(buff) != -1){
		      buff.flip();
		      fcout.write(buff);
		      buff.clear();
		      i++;
		     }*/
		     long size = fcin.size();
		     fcin.transferTo(0,fcin.size(),fcout);
		     //fcout.transferFrom(fcin,0,fcin.size());
		     //一定要分清哪个文件有数据，那个文件没有数据，数据只能从有数据的流向
		     //没有数据的文件
//		     long t2 = System.currentTimeMillis();
		     fcin.close();
		     fcout.close();
		     copySizes = size;
//		     long t = t2-t1;
//		     System.out.println("复制了" + i + "个字节\n" + "时间" + t);
//		     System.out.println("复制了" + size + "个字节\n" + "时间" + t);
		    } catch (FileNotFoundException e) {    
		     e.printStackTrace();
		    } catch (IOException e) {
		     e.printStackTrace();
		    }
		   }
		   return copySizes;
		}


	
	//删除文件夹
	//param folderPath 文件夹完整绝对路径

	     public static void delFolder(String folderPath) {
	     try {
	        delAllFile(folderPath); //删除完里面所有内容
	        String filePath = folderPath;
	        filePath = filePath.toString();
	        java.io.File myFilePath = new java.io.File(filePath);
	        myFilePath.delete(); //删除空文件夹
	     } catch (Exception e) {
	       e.printStackTrace(); 
	     }
	}

	//删除指定文件夹下所有文件
	//param path 文件夹完整绝对路径
	   public static boolean delAllFile(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) {
	         return flag;
	       }
	       String[] tempList = file.list();
	       if(tempList.length==0){
	    	   return true;
	       }
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	             delFolder(path + "/" + tempList[i]);//再删除空文件夹
	             flag = true;
	          }
	       }
	       return flag;
	     }
	   
	   public static boolean writeFileToLocal(byte[] bt,String filename){
		   boolean flag = true;
			try {
				File file = new File(filename);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(bt);
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flag=false;
			}
			return flag;
			
	   }
		public static String unZipFiles(File zipf){
			String ret=StrKit.getRandomUUID();
			byte[] buf=new byte[1024];;
			int readedBytes;
			try{ 
				ZipInputStream zipIn = new ZipInputStream (new BufferedInputStream(new FileInputStream(zipf))); 
				FileOutputStream fileOut=null;
				ZipEntry zipEntry=null;

	            while((zipEntry = zipIn.getNextEntry()) != null){ 
	            	File file = new File(PropKit.use("system.properties").get("tempdir")+"\\ctpa\\"+ret+"\\"+zipEntry.getName()); 
//	                System.out.println(file);
	 
	                if(zipEntry.isDirectory()){ 
	                    file.mkdirs(); 
	                } 
	                else{ 
	                    //如果指定文件的目录不存在,则创建之. 
	                    File parent = file.getParentFile(); 
	                    if(!parent.exists()){ 
	                        parent.mkdirs(); 
	                    } 
	 
	                    fileOut = new FileOutputStream(file); 
	                    while((readedBytes = zipIn.read(buf) ) > 0){ 
	                        fileOut.write(buf , 0 , readedBytes ); 
	                    } 
	                    fileOut.close(); 
	                } 
	                zipIn.closeEntry();     
	            } 
	        }catch(IOException ioe){ 
	            ioe.printStackTrace(); 
	        }
	        return PropKit.use("system.properties").get("tempdir")+"\\ctpa\\"+ret;
	    }

}
