package org.gcube.portlets.user.statisticalmanager.server.accounting;


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
	@Override
	public String getLogMessage() {
		return "Failure execution of "+ algorithmName+" algorithm";
	}

}
