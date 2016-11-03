package org.gcube.portlets.user.statisticalmanager.server.accounting;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

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
	public String getLogMessage() {
		return "Execution time of algorithm "+ algorithmName+" is "+ millisecondTime;
	}

}
