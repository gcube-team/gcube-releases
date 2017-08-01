package org.gcube.portlets.widgets.exporter.shared;

import java.io.Serializable;

public class ReportExporterException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1020757346366625622L;
	private String error;
	
	public ReportExporterException() {
		
	}
	
	public ReportExporterException(String error) {
		this.error = error;
	}
	
	@Override
	public String getMessage() {
		return error;
	}
}
