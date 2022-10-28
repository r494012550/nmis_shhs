package com.healta.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.PropKit;

public class FileUtil {
	
//	public static void copyFile(String oldPath, String newPath) {
//		try {
//			@SuppressWarnings("unused")
//			int bytesum = 0;
//			int byteread = 0;
//			File oldfile = new File(oldPath);
//			if (oldfile.exists()) { // 文件存在时
//				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
//				FileOutputStream fs = new FileOutputStream(newPath);
//				byte[] buffer = new byte[1444];
//				while ((byteread = inStream.read(buffer)) != -1) {
//					bytesum += byteread; // 字节数 文件大小
//					fs.write(buffer, 0, byteread);
//				}
//				inStream.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 将一个文件夹下的文件拷贝至另一个文件夹
	 * @param oldPath 要拷贝的文件夹路径
	 * @param newPath 拷贝至的文件夹路径
	 * @return
	 */
//	public static boolean copyFileToFolder(String oldPath, String newPath) {
//		folderNotExistsCreateFolder(new File(newPath));
//		try {
//			File folderFile = new File(oldPath); 
//			String[] fileArray = folderFile.list(); 
//			File tempFile = null; 
//			for (int i = 0; i < fileArray.length; i++) {
//				tempFile = new File(oldPath + "/" + fileArray[i]);
//				copyFile(oldPath + "/" + fileArray[i], newPath + "/" + tempFile.getName());
//				tempFile.delete();
//			} 
//		}catch (Exception e) { 
//			e.printStackTrace(); 
//			return false;
//		} 
//		return true;
//	}
	
	
//	public static boolean deleteFileByFolderPath(String path) {
//		boolean state = false;
//		File folderFile = new File(path); 
//		String[] fileArray = folderFile.list(); 
//		File tempFile = null; 
//		for (int i = 0; i < fileArray.length; i++) {
//			tempFile = new File(path + "/" + fileArray[i]);
//			state = tempFile.delete();
//			if(!state) {
//				return false;
//			}
//		} 
//		return state;
//	}
	
//	public static void folderNotExistsCreateFolder(File file) {
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//	}
	
	public static String[] getFileNameByFolderPath(String folderPath){
		String[] fileArray = null;
		File folderFile = new File(folderPath);
		if(!folderFile.isFile()) {
			fileArray = folderFile.list(); 
		}
		return fileArray;
	}
	
//    public static boolean deleteFile(String path) {
//		
//        File file = new File(path);
//        if (!file.exists()) {
//        }
//        //是文件夹
//        if(file.isDirectory()) {
//        	File[] listfile = file.listFiles(); 
//            if (delAllFile(path)) {
//                return true;
//            } else {
//            }
//        }
//        //是文件
//        if(!file.isDirectory()) {
//            if (file.delete()) {
//                return true;
//            } else {
//            }
//        }
//		return false;
//	}
	

//    public static boolean delAllFile(String path) {
//        boolean flag = false;
//        File file = new File(path);
//        if (!file.exists()) { return flag; }
//        String[] tempList = file.list();
//        File temp = null;
//        for (int i = 0; i < tempList.length; i++) {
//            if (path.endsWith(File.separator)) {
//                temp = new File(path + tempList[i]);
//            } else {
//                temp = new File(path + File.separator + tempList[i]);
//            }
//            //是文件
//            if (temp.isFile()) {
//                temp.delete();
//                flag = true;
//            }
//            //是文件夹
//            if (temp.isDirectory()) {
//            	//先删除文件夹里面的文件
//                delAllFile(path + "/" + tempList[i]);
//                //再删除空文件夹
//                delFolder(path + "/" + tempList[i]);
//                flag = true;
//            }
//        }
//        return flag;
//    }
    

//    public static void delFolder(String folderPath) {
//        try {
//        	//删除完里面所有内容
//            delAllFile(folderPath);
//            String filePath = folderPath;
////            filePath = filePath.toString();
//            File myFilePath = new File(filePath);
//            //删除空文件夹
//            myFilePath.delete();
//        } catch (Exception e) {
//            e.printStackTrace(); 
//        }
//	}
	
	/**
	 * 创建xml文件
	 * @param xml
	 * @return
	 */
	public static String createXMLFile(String xml, String path) {
		if (StringUtils.isBlank(path)) {
			path = PropKit.use("system.properties").get("sysdir") + "\\" + "SDA" + "\\" + "SDA.xml";
		}
//        String xmlUrl =  relativePath + "\\"+risStudyId+".xml";
//        String xmlUrl =  "D:\\file\\SDA1.xml";
        try {
            xml = xml.replaceAll("×", "x").replaceAll("＝", "=");
            byte[] xmlByte = getUTF8BytesFromGBKString(xml);
            xml = new String(xmlByte, "utf-8");
//            File dir = new File(relativePath);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriterWithEncoding fw = new FileWriterWithEncoding(path, "utf-8");
            fw.write(xml);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
    
    public static byte[] getUTF8BytesFromGBKString(String gbkStr) {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte) m;
                continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return tmp;
        }
        return utfBytes;
    }


	public static void main(String[] args) {
//		delFolder("C:\\Users\\25800\\Desktop\\新建文件夹");
//		folderNotExistsCreateFolder(new File("C:\\Users\\25800\\Desktop\\3\\1"));
		
		System.out.println(Charset.defaultCharset());
	}
}
