package com.healta.model;

import com.healta.model.base.BaseDicCommon;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class DicCommon extends BaseDicCommon<DicCommon> {
	public static final DicCommon dao = new DicCommon().dao();

	public boolean containColumn(String col_name) {
		return this._getTable().getColumnNameSet().contains(col_name);
	}
}
