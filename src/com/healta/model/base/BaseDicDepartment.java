package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDicDepartment<M extends BaseDicDepartment<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setDeptcode(java.lang.String deptcode) {
		set("deptcode", deptcode);
	}
	
	public java.lang.String getDeptcode() {
		return getStr("deptcode");
	}

	public void setDeptname(java.lang.String deptname) {
		set("deptname", deptname);
	}
	
	public java.lang.String getDeptname() {
		return getStr("deptname");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}
	
	public java.lang.String getType() {
		return getStr("type");
	}
	
	public void setDeleted(java.lang.String deleted) {
		set("deleted", deleted);
	}

	public java.lang.String getDeleted() {
		return get("deleted");
	}
	
	public void setInstitutionId(java.lang.Integer institutionid) {
        set("institutionid", institutionid);
    }

    public java.lang.Integer getInstitutionId() {
        return get("institutionid");
    }

    public void setShifts(java.lang.Integer shifts) {
        set("shifts", shifts);
    }

    public java.lang.Integer getShifts() {
        return get("shifts");
    }
    
}
