package org.gcube.portlets.user.statisticalmanager.server.accounting;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

public class SuccessOutcomeAlgorithmLogEntry extends AccessLogEntry {

	/*
	 */
	private String algorithmName;
	/**
	 * Constructor
	 */
	public SuccessOutcomeAlgorithmLogEntry(String algName) {
		super("StatisticalManage_Outcome");
		this.algorithmName=algName;
	}

	/**
	 */
	public String getLogMessage() {
		return "Success execution of "+ algorithmName+" algorithm";
	}

}
