package org.gcube.portlets.user.statisticalmanager.server.accounting;


public class ImportLogEntry extends AccessLogEntry {

	/*
	 */
	//private String filename;
	//private String fileType;
	//private String source;

	/**
	 * Constructor
	 */
	public ImportLogEntry() {
		super("StatisticaManager_Import");
		
	}

	/**
	 */
	@Override
	public String getLogMessage() {
		return "Operation of import.";
	}

}
