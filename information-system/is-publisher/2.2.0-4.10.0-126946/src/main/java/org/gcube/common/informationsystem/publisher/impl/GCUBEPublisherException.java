package org.gcube.common.informationsystem.publisher.impl;

import org.gcube.common.core.informationsystem.publisher.ISPublisherException;

/**
 * 
 * The GCUBEPublisherException 
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GCUBEPublisherException extends ISPublisherException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param exceptionMessage the exceptionMEssage
	 */
	public GCUBEPublisherException(String exceptionMessage){
		super(exceptionMessage);
	}
	

	/**
	 * Constructor
	 * @param exceptionMessage the Exception Message
	 * @param e the exception
	 */
	public GCUBEPublisherException(String exceptionMessage, Exception e){
		super("Publisher Exception"+e);
	}
}
