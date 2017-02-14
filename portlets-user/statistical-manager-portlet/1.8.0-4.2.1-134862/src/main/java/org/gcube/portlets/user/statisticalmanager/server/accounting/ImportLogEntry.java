package org.gcube.portlets.user.statisticalmanager.server.accounting;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

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
	public String getLogMessage() {
		return "Operation of import.";
	}

}
