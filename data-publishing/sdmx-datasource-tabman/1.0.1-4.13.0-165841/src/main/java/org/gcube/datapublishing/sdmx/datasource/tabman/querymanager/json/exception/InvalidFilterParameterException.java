package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception;

import org.sdmxsource.sdmx.api.engine.DataWriterEngine.FooterMessage.SEVERITY;

public class InvalidFilterParameterException extends SDMXDataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6558498829040344333L;

	
	public InvalidFilterParameterException(String parameterName, String requestedType) {
		super ("Invalid filter parameter "+parameterName+": requested type "+requestedType,"500","Invalid filter parameter "+parameterName+": requested type "+requestedType);
	}


	@Override
	public SEVERITY getSeverity() {

		return SEVERITY.ERROR;
	}
	
	
}
