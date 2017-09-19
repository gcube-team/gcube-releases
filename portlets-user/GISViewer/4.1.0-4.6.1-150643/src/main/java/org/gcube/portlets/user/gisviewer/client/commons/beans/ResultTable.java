package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResultTable implements IsSerializable {

	private List<ResultRow> rows;

	public ResultTable() {

	}

	public ResultTable(List<ResultRow> rows) {
		this.rows = rows;
	}

	public List<ResultRow> getRows() {
		return rows;
	}

	public void setRows(List<ResultRow> rows) {
		this.rows = rows;
	}
	
	public void addResultRow(ResultRow resultRow) {
		this.rows.add(resultRow);
	}

}
