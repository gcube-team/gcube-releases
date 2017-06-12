package org.gcube.portlets.user.td.gwtservice.shared.source;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public interface Source extends Serializable {

	/**
	 * Returns the document type id.
	 * 
	 * @return the document type id.
	 */
	public String getId();

	/**
	 * Returns the document type name.
	 * 
	 * @return the document type name.
	 */
	public String getName();

	/**
	 * Returns the document type description.
	 * 
	 * @return the document type description.
	 */
	public String getDescription();

}
