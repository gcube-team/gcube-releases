package org.gcube.common.core.scope;

/**
 * 
 * The Exception is thrown when a given GCUBEScope does not have a related ServiceMap associated to the GHN.
 * 
 * @author Andrea Manzi (ISTI-CNR)
 *
 */
public class GCUBEScopeNotSupportedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public GCUBEScopeNotSupportedException(GCUBEScope scope) {
		super("One or More Service Maps not found for the scope: "+(scope==null?scope:scope.toString()));
	}
}
