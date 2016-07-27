/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.document;

import java.io.Serializable;




/**
 * SDMX document type interface. All SDMX document type have to implement this interface.
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface SDMXDocument extends Serializable {
	
	/**
	 * Returns the document id.
	 * @return the document id.
	 */
	public String getId();
	
	/**
	 * Returns the document name.
	 * @return the document name.
	 */
	public String getName();
	
	/**
	 * Returns the document description.
	 * @return the document description.
	 */
	public String getDescription();
	
	
}
