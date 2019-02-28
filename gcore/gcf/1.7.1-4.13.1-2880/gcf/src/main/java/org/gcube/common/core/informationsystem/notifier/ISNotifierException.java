package org.gcube.common.core.informationsystem.notifier;

import org.gcube.common.core.informationsystem.ISException;

/**
 * IS Notifier specific exception
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class ISNotifierException extends ISException {
	
	private static final long serialVersionUID = 2623942901296539055L;

	public ISNotifierException(String exceptionMessage) {
		super("ISNotifierException: " + exceptionMessage);
	}

}
