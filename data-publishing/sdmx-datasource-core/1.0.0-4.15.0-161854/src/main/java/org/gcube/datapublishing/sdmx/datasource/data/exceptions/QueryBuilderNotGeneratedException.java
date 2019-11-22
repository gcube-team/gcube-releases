package org.gcube.datapublishing.sdmx.datasource.data.exceptions;

public class QueryBuilderNotGeneratedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 76681100613256509L;

	public QueryBuilderNotGeneratedException(String message) {
		super (message);
	}

	public QueryBuilderNotGeneratedException(String message, Throwable cause) {
		super (message,cause);
	}
	
}
