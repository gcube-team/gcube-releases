package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl;

class ObservationExceeded extends Throwable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -55980821703939168L;

	private int maxObservations;
	
	public ObservationExceeded(int maxObservations) {
		super ("Observation number exceeded");
		this.maxObservations = maxObservations;
	}
	
	public String getMaxObservationLogMessage ()
	{
		return new String ("Max number of allowed observations "+this.maxObservations);
	}
	
}
