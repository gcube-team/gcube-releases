package org.gcube.portlets.user.td.gwtservice.shared.destination;

import java.io.Serializable;


public interface Destination extends Serializable {
	
	/**
	 * Returns the document type id.
	 * @return the document type id.
	 */
	public String getId();
	
	/**
	 * Returns the document type name.
	 * @return the document type name.
	 */
	public String getName();
	
	/**
	 * Returns the document type description.
	 * @return the document type description.
	 */
	public String getDescription();
	
	
}
