package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception;

import org.sdmxsource.sdmx.api.engine.DataWriterEngine.FooterMessage.SEVERITY;

public abstract class SDMXDataException extends Exception {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8012490840820152844L;
	
	private String sdmxMessage;
	private String code;
	
	public SDMXDataException(String exceptionMessage, String sdmxCode,String sdmxMessage) {
		super (exceptionMessage);
		this.sdmxMessage = sdmxMessage;
		this.code = sdmxCode;
	}

	public abstract SEVERITY getSeverity (); 


	public String getSDMXMessage ()
	{
		return this.sdmxMessage;
	}
	
	public String getCode ()
	{
		return this.code;
	}
	
}
