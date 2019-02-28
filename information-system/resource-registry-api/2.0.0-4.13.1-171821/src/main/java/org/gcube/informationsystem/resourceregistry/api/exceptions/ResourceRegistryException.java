/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api.exceptions;

import org.gcube.informationsystem.model.reference.ISManageable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = ISManageable.CLASS_PROPERTY)
public class ResourceRegistryException extends Exception {

	/**
	 *  Generated Serial Version UID
	 */
	private static final long serialVersionUID = -8321258637131836003L;
	
	public ResourceRegistryException(String message) {
		super(message);
	}
	
	public ResourceRegistryException(Throwable cause) {
		super(cause);
	}
	
	public ResourceRegistryException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
