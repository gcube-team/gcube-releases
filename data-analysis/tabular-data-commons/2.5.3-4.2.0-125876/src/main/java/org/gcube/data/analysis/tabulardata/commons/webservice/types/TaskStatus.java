package org.gcube.data.analysis.tabulardata.commons.webservice.types;

public enum TaskStatus {

	INITIALIZING(false), 
	IN_PROGRESS(false),
	VALIDATING_RULES(false),
	GENERATING_VIEW(false),
	STOPPED(true),
	SUCCEDED(true),
	ABORTED(true),
	FAILED(true);
	
	private boolean isFinal;
	
	TaskStatus(boolean isFinal) {
		this.isFinal = isFinal;
	}

	/**
	 * @return the isFinal
	 */
	public boolean isFinal() {
		return isFinal;
	}
	
	
}


