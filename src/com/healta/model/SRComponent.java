package com.healta.model;

import com.healta.model.base.BaseSRComponent;

@SuppressWarnings("serial")
public class SRComponent extends BaseSRComponent<SRComponent>{
	public static final SRComponent dao = new SRComponent().dao();
	
	public SRComponent getSRComponentByUID(String uid) {
		return SRComponent.dao.findFirst("select top 1 * from srcomponent where uid=?",uid);
	}
}
