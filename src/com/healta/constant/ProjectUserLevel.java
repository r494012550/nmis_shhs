package com.healta.constant;

public enum ProjectUserLevel {
	
	ADMINISTRATOR(99, "系统管理员"),
	DIRECTOR(80, "科室主任"),
	PROJECTMANAGER(70, "组长"),
	PROJECTMEMBER(60,"组员");
	
	private int level;
	private String name;
	
	private ProjectUserLevel(int level,String name){
		this.level=level;
		this.name=name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static String getValue(int level) {
		for (ProjectUserLevel projectUserLevel : values()) {
			if(projectUserLevel.getLevel()==level){
				return projectUserLevel.getName();
			}
		}
		return null;
	}
	
}
