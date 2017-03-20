package org.gcube.portlets.user.statisticalmanager.server.accounting;


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
	@Override
	public String getLogMessage() {
		return "Success execution of "+ algorithmName+" algorithm";
	}

}
