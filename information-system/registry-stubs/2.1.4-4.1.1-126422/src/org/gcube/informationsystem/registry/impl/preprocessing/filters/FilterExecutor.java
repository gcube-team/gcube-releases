package org.gcube.informationsystem.registry.impl.preprocessing.filters;

import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Base resource filter
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class FilterExecutor {

	protected GCUBELog logger = new GCUBELog(this);
	
	public abstract boolean accept(GCUBEResource resource) throws InvalidFilterException;
	
	public static class InvalidFilterException extends Exception {	
		private static final long serialVersionUID = -8353431478801569045L;	
		public InvalidFilterException(String message) {super(message);}
	}	
}
