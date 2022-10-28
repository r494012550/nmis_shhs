package com.healta.constant;

import java.util.Arrays;
import java.util.Optional;

public enum ReportType {

	NORMAL(0,"常规报告","report",0,0),
//	IMAGE_REPORT_ONE(1,"图文报告1图","image_report",1,150),
	IMAGE_REPORT_TWO(2,"图文报告2图","image_report",2,150),
//	IMAGE_REPORT_THREE(3,"图文报告3图","image_report",3,150),
	IMAGE_REPORT_FOUR(4,"图文报告4图","image_report",4,150),
//	IMAGE_REPORT_FIVE(5,"图文报告5图","image_report",4,150),
	IMAGE_REPORT_SIX(6,"图文报告6图","image_report",4,150),
//	IMAGE_REPORT_SEVEN(7,"图文报告7图","image_report",4,150),
	IMAGE_REPORT_EIGHT(8,"图文报告8图","image_report",4,150),
	STRUCTURED(99,"结构化报告",null,0,0);
	
	public Integer name;
	public String displayName;
	public String reportfilename;
	public Integer column;
	public Integer height;
	
	private ReportType(Integer name,String displayName,String reportfilename,Integer column,Integer height){
		this.name = name;
		this.displayName=displayName;
		this.reportfilename=reportfilename;
		this.column = column;
		this.height=height;
	}

	public Integer getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Integer getColumn() {
		return column;
	}

	public Integer getHeight() {
		return height;
	}
	
	public String getReportfilename() {
		return this.reportfilename;
	}

	public static ReportType getReportType(Integer name){
		Optional<ReportType> op=Arrays.asList(ReportType.values()).stream().filter(x->
			x.getName().intValue()==name.intValue()
		).findFirst();
		if(op.isPresent()){
			return op.get();
		}
		return NORMAL;
	}
	
//	public static void main(String[] arg) {
//		System.out.println(ReportType.getReportType(2));
//		System.out.println(ReportType.IMAGE_REPORT_TWO.getReportfilename());
//		
//		
////        .filter(d -> d.getTypeOfDay().equals("off"))
////        .forEach(System.out::println);
//		
//		
//		
//		Arrays.asList(ReportType.values())
//		  .forEach(day -> System.out.println(day));
//		
//		
//		Optional<ReportType> op=Arrays.asList(ReportType.values()).stream().filter(x->
//			x.getName().equals("2")
//		).findFirst();
//		
//		if(op.isPresent()){
//			System.out.println(op.get());
//		}
//		
//		System.out.println(ReportType.valueOf("IMAGE_REPORT_TWO"));
//    }
}
