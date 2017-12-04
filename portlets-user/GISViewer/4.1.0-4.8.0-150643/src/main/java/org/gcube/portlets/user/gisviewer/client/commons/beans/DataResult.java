package org.gcube.portlets.user.gisviewer.client.commons.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DataResult implements IsSerializable {

	private String title;
	private ResultTable table;

	public DataResult() {

	}

	public DataResult(String title, ResultTable table) {
		this.title = title;
		this.table = table;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ResultTable getTable() {
		return table;
	}

	public void setTable(ResultTable table) {
		this.table = table;
	}

}
