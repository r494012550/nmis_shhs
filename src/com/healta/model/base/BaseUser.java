package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUser<M extends BaseUser<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}
	
	public void setEmployeefk(java.lang.Integer employeefk) {
		set("employeefk", employeefk);
	}

	public java.lang.Integer getEmployeefk() {
		return get("employeefk");
	}

	public void setUsername(java.lang.String username) {
		set("username", username);
	}

	public java.lang.String getUsername() {
		return get("username");
	}

	public void setPassword(java.lang.String password) {
		set("password", password);
	}

	public java.lang.String getPassword() {
		return get("password");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setRole(java.lang.String role) {
		set("role", role);
	}

	public java.lang.String getRole() {
		return get("role");
	}

	public void setDescription(java.lang.String description) {
		set("description", description);
	}

	public java.lang.String getDescription() {
		return get("description");
	}
	
	public void setActive(java.lang.String active) {
		set("active", active);
	}

	public java.lang.String getActive() {
		return get("active");
	}
	
	public void setLock(java.lang.String lock) {
		set("lock", lock);
	}

	public java.lang.String getLock() {
		return get("lock");
	}
	
	public void setExpiredate(java.util.Date expiredate) {
		set("expiredate", expiredate);
	}
	
	public java.util.Date getExpiredate() {
		return get("expiredate");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}

	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
	public void setDefaultmodule(java.lang.String defaultmodule) {
		set("defaultmodule", defaultmodule);
	}

	public java.lang.String getDefaultmodule() {
		return get("defaultmodule");
	}
	
	public void setSign(java.lang.String sign) {
		set("sign", sign);
	}

	public java.lang.String getSign() {
		return get("sign");
	}
	
	public void setAvatar(java.lang.String avatar) {
		set("avatar", avatar);
	}

	public java.lang.String getAvatar() {
		return get("avatar");
	}
	
	public void setViausername(java.lang.String viausername) {
		set("viausername", viausername);
	}
	
	public java.lang.String getViausername() {
		return getStr("viausername");
	}

	public void setViapassword(java.lang.String viapassword) {
		set("viapassword", viapassword);
	}
	
	public java.lang.String getViapassword() {
		return getStr("viapassword");
	}

	public void setPlazausername(java.lang.String plazausername) {
		set("plazausername", plazausername);
	}
	
	public java.lang.String getPlazausername() {
		return getStr("plazausername");
	}

	public void setPlazapassword(java.lang.String plazapassword) {
		set("plazapassword", plazapassword);
	}
	
	public java.lang.String getPlazapassword() {
		return getStr("plazapassword");
	}
	public void setModality(java.lang.String modality) {
		set("modality", modality);
	}
	
	public java.lang.String getModality() {
		return getStr("modality");
	}
	public void setDeleted(java.lang.String deleted) {
        set("deleted", deleted);
    }

    public java.lang.String getDeleted() {
        return get("deleted");
    }
    public void setVipFlag(java.lang.Integer vipFlag) {
		set("vip_flag", vipFlag);
	}
	
	public java.lang.Integer getVipFlag() {
		return getInt("vip_flag");
	}

	public void setMatrixUsername(java.lang.String matrixUsername) {
		set("matrix_username", matrixUsername);
	}

	public java.lang.String getMatrixUsername() {
		return getStr("matrix_username");
	}

	
}