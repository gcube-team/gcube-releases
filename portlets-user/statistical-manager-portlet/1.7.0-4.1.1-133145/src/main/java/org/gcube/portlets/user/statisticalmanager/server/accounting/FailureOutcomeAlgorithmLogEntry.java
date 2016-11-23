package org.gcube.portlets.user.statisticalmanager.server.accounting;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

public class FailureOutcomeAlgorithmLogEntry extends AccessLogEntry {

	/*
	 */
	private String algorithmName;
	/**
	 * Constructor
	 */
	public FailureOutcomeAlgorithmLogEntry(String algName) {
		super("StatisticalManager_Outcome");
		this.algorithmName=algName;
	}

	/**
	 */
	public String getLogMessage() {
		return "Failure execution of "+ algorithmName+" algorithm";
	}

}
