package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResultRow implements IsSerializable {

	private List<ResultColumn> columns;

	public ResultRow() {

	}

	public ResultRow(List<ResultColumn> columns) {
		this.columns = columns;
	}

	public List<ResultColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ResultColumn> columns) {
		this.columns = columns;
	}

}
