package com.healta.util;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.jfinal.kit.StrKit;

public class ExcelUtil {
    
    /**
     *  导出Excel
     *
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @param widths 每一列的宽度
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName,String[] title,String[][] values, String[] widths) {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        // 创建一个居中格式
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setFillForegroundColor((short) 13);// 设置背景色
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        
        HSSFFont font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 16);//设置字体大小
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
        style.setFont(font);//选择需要用到的字体格式
        sheet.setColumnWidth(0, Integer.valueOf(widths[0]) * 50); // 第一个参数为列的位置，第二个为宽度值

        // 声明列对象
        HSSFCell cell = null;

        // 创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        
        // 创建列宽
        for (int i = 1; i < widths.length; i++) {
            sheet.setColumnWidth(i, Integer.valueOf(widths[i]) * 50);
        }

        // 创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < title.length; j++) {
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }
    
    /*
     * sheetName:工作表名称
     * title：列标题
     * values：值二维数组
     * options：下拉选项
     * dataformat:数据格式，支持日期格式(yyyy-MM-dd),日期时间格式(yyyy-MM-dd HH:mm:ss)
     * */
    public static SXSSFWorkbook getXSSFWorkbook(String sheetName,String[] title,String[][] values,Map<Integer ,String[]> options,String[] dataformat) {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
    	SXSSFWorkbook wb = new SXSSFWorkbook();
    	// 列头样式
    	CellStyle headerStyle = wb.createCellStyle();
    	headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//    	headerStyle.setBorderBottom(CellStyle.BORDER_THIN);
//    	headerStyle.setBorderLeft(CellStyle.BORDER_THIN);
//    	headerStyle.setBorderRight(CellStyle.BORDER_THIN);
//    	headerStyle.setBorderTop(CellStyle.BORDER_THIN);
//    	headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
    	headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    	headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());// 设置背景色
    	Font headerFont = wb.createFont();
    	headerFont.setFontHeightInPoints((short) 11);
    	headerFont.setFontName("宋体");
    	headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
    	headerStyle.setFont(headerFont);
        
	    // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
    	SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(sheetName);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
    	SXSSFRow row = (SXSSFRow) sheet.createRow(0);
    	row.setHeight((short)(22*20));

        // 第四步，创建单元格，并设置值表头 设置表头居中
//    	CellStyle style = wb.createCellStyle();
//        // 创建一个居中格式
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setFillForegroundColor((short) 10);// 设置背景色
//        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        
//        Font font = wb.createFont();
//        font.setFontName("黑体");
//        font.setFontHeightInPoints((short) 16);//设置字体大小
//        font.setBoldweight(Font.BOLDWEIGHT_BOLD);//粗体显示
//        style.setFont(font);//选择需要用到的字体格式
        

        // 声明列对象
        Cell cell = null;

        // 创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 100*50); // 第一个参数为列的位置，第二个为宽度值
        }
        
        // 创建列宽
//        for (int i = 1; i < widths.length; i++) {
//            sheet.setColumnWidth(i, Integer.valueOf(widths[i]) * 50);
//        }

        // 创建内容
        if(values!=null) {
	        for (int i = 0; i < values.length; i++) {
	            row = (SXSSFRow)sheet.createRow(i + 1);
	            for (int j = 0; j < title.length; j++) {
	                //将内容按顺序赋给对应的列对象
	            	Cell ce =row.createCell(j);
	            	ce.setCellValue(values[i][j]);
	                if(dataformat!=null&&StrKit.notBlank(dataformat[j])) {
	                	setCellStyle(wb,ce,dataformat[j]);
	                }
	            }
	        }
        }
        //添加下拉选项
        addValidationData(sheet,options);
        return wb;
    }
    
    public static void setCellStyle(SXSSFWorkbook wb,Cell cell,String dataformat) {
    	CellStyle style = wb.createCellStyle();
    	DataFormat format =wb.createDataFormat();
    	style.setDataFormat(format.getFormat(dataformat));
    	cell.setCellStyle(style);
    }
    
    public static void addValidationData(SXSSFSheet sheet,Map<Integer ,String[]> options) {
    	if(options!=null) {
    		options.forEach((k,v)->{
    			DataValidationHelper helper = sheet.getDataValidationHelper();
    	        //设置下拉框数据
    	        DataValidationConstraint constraint = helper.createExplicitListConstraint(v);
    	        //设置生效的起始行、终止行、起始列、终止列
    	        CellRangeAddressList addressList = new CellRangeAddressList(1,2000,k,k);
    	        DataValidation validation = helper.createValidation(constraint,addressList);
    	        //适配xls和xlsx
    	        if(validation instanceof HSSFDataValidation){
    	            validation.setSuppressDropDownArrow(false);
    	        }else{
    	            validation.setSuppressDropDownArrow(true);
    	            validation.setShowErrorBox(true);
    	        }
    	    	sheet.addValidationData(validation);
    		});
    	}
    }
    
}
