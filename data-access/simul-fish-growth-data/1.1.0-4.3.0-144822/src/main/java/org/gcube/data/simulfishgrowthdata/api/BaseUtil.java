package org.gcube.data.simulfishgrowthdata.api;

public class BaseUtil {
	protected boolean inTransaction;

	public BaseUtil() {
		inTransaction = false;
	}

	public BaseUtil(boolean inTransaction) {
		this.inTransaction = inTransaction;
	}

}
