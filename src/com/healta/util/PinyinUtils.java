package com.healta.util;

import com.jfinal.kit.PropKit;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtils {
    
	public static String toPinyin(String chinese) {
		return toPinyin(chinese,false);
	}
    /**
     * 汉字转为拼音
     * @param chinese :中文
     * @param filter :是否过滤特殊字符
     * @return
     */
    public static String toPinyin(String chinese,boolean filter) {
        // 用StringBuffer（字符串缓冲）来接收处理的数据
        StringBuffer sb = new StringBuffer();
        //字符串转换字节数组
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        //转换类型（大写or小写）
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //定义中文声调的输出格式
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //定义字符的输出格式
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
        for (int i = 0; i < arr.length; i++) {
            //判断是否是汉子字符
            if (arr[i] > 128) {
                try {
                    String[] string = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (string != null) {
                        sb.append(capitalize(string[0]));
                    } else {
                        // string为空，可能为特殊字符 如：）（
                        sb.append(arr[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                // 如果不是汉字字符，直接拼接
                sb.append(arr[i]);
            }
        }
        return filter?filterSpecialChar(sb.toString()):sb.toString();
    }
    
    /**
     * 首字母大写 
     * @param str 
     * @return 
     */  
    public static String capitalize(String str) {  
        char ch[];  
        ch = str.toCharArray();  
        if (ch[0] >= 'a' && ch[0] <= 'z') {  
            ch[0] = (char) (ch[0] - 32);  
        }  
        String newString = new String(ch);  
        return newString;  
    }
    
    /**
     *  对拼音进行特殊字符过滤
     * @param patientnamepy
     * @return
     */
    public static String filterSpecialChar(String py) {
    	String patientname_py_filter = PropKit.use("system.properties").get("patientname_py_filter", "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].·<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’.。，、？]");
    	return py.replaceAll(patientname_py_filter, "");
    }
    
    public static void main(String[] args) {
        System.out.println(toPinyin("颈椎CT平扫"));
        System.out.println(filterSpecialChar("颈。椎CT.平.扫")); 
    }

}
