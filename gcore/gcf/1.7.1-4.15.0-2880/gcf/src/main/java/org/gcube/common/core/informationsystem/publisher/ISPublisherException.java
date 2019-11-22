package org.gcube.common.core.informationsystem.publisher;

import org.gcube.common.core.informationsystem.ISException;

/**
 * {@link ISPublisher} exception
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class ISPublisherException extends ISException {
	

	private static final long serialVersionUID = 7083591360885431590L;

	public ISPublisherException(String exceptionMessage){
		super("ISPublisherException: "+exceptionMessage);
	}


}
