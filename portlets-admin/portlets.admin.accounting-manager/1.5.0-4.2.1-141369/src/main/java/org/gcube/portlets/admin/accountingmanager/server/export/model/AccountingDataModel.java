package org.gcube.portlets.admin.accountingmanager.server.export.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Accounting Data Model
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingDataModel implements Serializable {

	private static final long serialVersionUID = 7127656837246518599L;
	private String name;
	private ArrayList<String> header;
	private ArrayList<AccountingDataRow> rows;

	public AccountingDataModel() {
		super();
	}

	public AccountingDataModel(String name, ArrayList<String> header,
			ArrayList<AccountingDataRow> rows) {
		super();
		this.name = name;
		this.header = header;
		this.rows = rows;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getHeader() {
		return header;
	}

	public void setHeader(ArrayList<String> header) {
		this.header = header;
	}

	public ArrayList<AccountingDataRow> getRows() {
		return rows;
	}

	public void setRows(ArrayList<AccountingDataRow> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "AccountingDataModel [name=" + name + ", header=" + header
				+ ", rows=" + rows + "]";
	}

	
	
}