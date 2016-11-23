package org.gcube.portlets.admin.accountingmanager.server.amservice.query;

import java.util.ArrayList;

/**
 * Accounting Query Specification
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class AccountingQuerySpec {
	private AccountingQuery op;
	private ArrayList<AccountingQuery> ops;

	public AccountingQuery getOp() {
		return op;
	}

	public void setOp(AccountingQuery op) {
		this.op = op;
	}

	public ArrayList<AccountingQuery> getOps() {
		return ops;
	}

	public void setOps(ArrayList<AccountingQuery> ops) {
		this.ops = ops;
	}

}
