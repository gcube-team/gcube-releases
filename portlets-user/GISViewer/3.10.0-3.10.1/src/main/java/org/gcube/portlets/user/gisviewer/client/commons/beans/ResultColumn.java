package org.gcube.portlets.user.gisviewer.client.commons.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResultColumn implements IsSerializable {

	private String null_value="null";
	private String value;

	public ResultColumn() {

	}

	public ResultColumn(String value) {
		this.value = value;
	}

	public String getValue() {
		if(value.equals("")){
			return null_value;
		}
		return value;
	}

	public String getNull_value() {
		return null_value;
	}

	public void setNull_value(String nullValue) {
		null_value = nullValue;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
