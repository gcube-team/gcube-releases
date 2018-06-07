package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception;

import org.sdmxsource.sdmx.api.engine.DataWriterEngine.FooterMessage.SEVERITY;

public class NoDataException extends SDMXDataException {



	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3610869278732658293L;

	public NoDataException() {
		super ("No data found","404","No data found");
	}

	@Override
	public SEVERITY getSeverity() {

		return SEVERITY.INFORMATION;
	}
	
	
}
