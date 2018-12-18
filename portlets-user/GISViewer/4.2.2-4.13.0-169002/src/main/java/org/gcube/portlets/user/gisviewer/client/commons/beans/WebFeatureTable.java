package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The Class WebFeatureTable.
 *
 * @author Ceras
 * @author updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *         Jan 21, 2016
 */
public class WebFeatureTable implements IsSerializable {

	private String title;
	private List<BaseModel> rows = new ArrayList<BaseModel>();
	private boolean error = false;
	private String errorMsg;


	public WebFeatureTable() {
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {

		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {

		this.errorMsg = errorMsg;
	}



	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {

		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {

		this.title = title;
	}

	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 */
	public List<BaseModel> getRows() {

		return rows;
	}

	/**
	 * Sets the rows.
	 *
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(List<BaseModel> rows) {

		this.rows = rows;
	}

	/**
	 * Gets the column names.
	 *
	 * @return the columnNames
	 */
	public List<String> getColumnNames() {

		if (rows.size() > 0)
			return new ArrayList<String>(rows.get(0).getPropertyNames());
		else
			return new ArrayList<String>();
	}

	/**
	 * Adds the row.
	 *
	 * @param row
	 *            the row
	 */
	public void addRow(BaseModel row) {

		this.rows.add(row);
	}

	/**
	 * Sets the error.
	 *
	 * @param error
	 *            the error to set
	 */
	public void setError(boolean error) {

		this.error = error;
	}

	/**
	 * Checks if is error.
	 *
	 * @return the error
	 */
	public boolean isError() {

		return error;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("WebFeatureTable [title=");
		builder.append(title);
		builder.append(", rows=");
		builder.append(rows);
		builder.append(", error=");
		builder.append(error);
		builder.append(", errorMsg=");
		builder.append(errorMsg);
		builder.append("]");
		return builder.toString();
	}
}
