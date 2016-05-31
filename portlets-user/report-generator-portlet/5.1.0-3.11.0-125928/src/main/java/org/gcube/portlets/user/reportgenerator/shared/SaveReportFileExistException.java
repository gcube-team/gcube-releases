package org.gcube.portlets.user.reportgenerator.shared;

public class SaveReportFileExistException extends ReportExporterException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7241356056474394295L;
	
	public SaveReportFileExistException() {
		
	}

	public SaveReportFileExistException(String error) {
		super(error);
	}

}
