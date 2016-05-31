package org.gcube.portlets.user.trendylyzer_portlet.server.accounting;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;


public class StatisticalManagerExecution extends AccessLogEntry {

	/*
	 */
	private String algorithmName;
	private String executionTime;
	private String feedback;
	/**
	 * Constructor
	 */
	public StatisticalManagerExecution(String algName, int executionTime, String feedback) {
		super("StatisticalManager_Execution");
		this.algorithmName=algName;
		this.executionTime=Integer.toString(executionTime);
		this.feedback=feedback;
	}

	/**
	 */
	public String getLogMessage() {
		return SMAccountingConstant.ALGORITHM_NAME +SMAccountingConstant.eqChar+
				algorithmName+SMAccountingConstant.separateCharacters+
				SMAccountingConstant.EXECUTION_OUTCOME+
				SMAccountingConstant.eqChar+feedback+
				SMAccountingConstant.separateCharacters+
				SMAccountingConstant.EXECUTION_TIME + SMAccountingConstant.eqChar+
				executionTime;
	}

}
