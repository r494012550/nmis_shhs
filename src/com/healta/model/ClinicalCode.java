package com.healta.model;

import com.healta.model.base.BaseClinicalCode;

@SuppressWarnings("serial")
public class ClinicalCode extends BaseClinicalCode<ClinicalCode> {
	public static final ClinicalCode dao = new ClinicalCode().dao();
}
