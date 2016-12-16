package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form;

import com.extjs.gxt.ui.client.data.BeanModel;

public class ColumnItem extends BeanModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4376285744187519203L;
	public static final String CODE="code";
	public static final String NAME="name";

	public ColumnItem(String code,String name) {
		set(NAME, name);
		set(CODE,code);
	}

	public String getName() {
		return get(NAME);
	}

	public String getCode(){
		return get(CODE);
	}
}