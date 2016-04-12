/**
 * 
 */
package org.gcube.common.homelibary.model.items.gcube;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface DocumentMetadata {
	
	/**
	 * Metadata schema name.
	 * @return the schema name.
	 */
	public String getSchemaName();
	
	/**
	 * Metadata XML.
	 * @return the XML
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public String getXML() throws Exception;

}
