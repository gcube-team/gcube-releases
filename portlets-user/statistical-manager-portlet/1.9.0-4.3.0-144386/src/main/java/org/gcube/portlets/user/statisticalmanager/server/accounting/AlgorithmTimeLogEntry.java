package org.gcube.portlets.user.statisticalmanager.server.accounting;


public class AlgorithmTimeLogEntry extends AccessLogEntry {

	/*
	 */
	private String algorithmName;
	private int millisecondTime;
	/**
	 * Constructor
	 */
	public AlgorithmTimeLogEntry(String algName, int millisecondTime) {
		super("StatisticalManager_Performance");
		this.algorithmName=algName;
		this.millisecondTime=millisecondTime;
		
	}
	/**
	 */
	@Override
	public String getLogMessage() {
		return "Execution time of algorithm "+ algorithmName+" is "+ millisecondTime;
	}

}
