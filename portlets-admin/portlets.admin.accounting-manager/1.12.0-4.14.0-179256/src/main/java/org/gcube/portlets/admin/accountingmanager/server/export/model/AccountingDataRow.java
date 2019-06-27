package org.gcube.portlets.admin.accountingmanager.server.export.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Accounting data row
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class AccountingDataRow implements Serializable {

	private static final long serialVersionUID = -707694572028800959L;
	private ArrayList<String> data;

	public AccountingDataRow() {
		super();
	}

	public AccountingDataRow(ArrayList<String> data) {
		super();
		this.data = data;
	}

	public ArrayList<String> getData() {
		return data;
	}

	public void setData(ArrayList<String> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "AccountingDataRow [data=" + data + "]";
	}

	

}
