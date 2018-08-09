package org.gcube.data.transfer.service;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.data.transfer.service.transfers.engine.AccountingManager;

public class Accountingtests {

	public static void main(String[] args) {
		SecurityTokenProvider.instance.set("feda0617-cd9d-4841-b6f0-e047da5d32ed-98187548");
		AccountingManager manager=AccountingManager.get();
		String id=manager.createNewRecord();
		manager.setSuccessful(id, true);
		System.out.println("DONE");
	}

}
