package org.gcube.portlets.admin.accountingmanager.server.export.model;

import java.util.ArrayList;

/**
 * CSV Model Specification
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class AccountingModelSpec {
	private AccountingDataModel op;
	private ArrayList<AccountingDataModel> ops;

	public AccountingDataModel getOp() {
		return op;
	}

	public void setOp(AccountingDataModel op) {
		this.op = op;
	}

	public ArrayList<AccountingDataModel> getOps() {
		return ops;
	}

	public void setOps(ArrayList<AccountingDataModel> ops) {
		this.ops = ops;
	}

}
