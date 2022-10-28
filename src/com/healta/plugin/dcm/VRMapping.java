package com.healta.plugin.dcm;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.jfinal.kit.PathKit;

public class VRMapping extends Properties{
	private static final long serialVersionUID = 1L;
	private static final VRMapping instance = new VRMapping();
	
	public static VRMapping getInstance() {
        return instance;
    }
	
	public String getProperty(String key) {
        if (key == null || Character.isLowerCase(key.charAt(0)))
                return key;
        String value = super.getProperty(key);
        if (value == null)
            throw new IllegalArgumentException("key: " + key);
        return value;
    }
	
	private VRMapping() {
        try {
            InputStream in =new FileInputStream(PathKit.getWebRootPath()+"\\WEB-INF\\classes\\conf\\vrmapping.properties");
            load(in);
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load jdbc properties", e);
        }
    }
}
