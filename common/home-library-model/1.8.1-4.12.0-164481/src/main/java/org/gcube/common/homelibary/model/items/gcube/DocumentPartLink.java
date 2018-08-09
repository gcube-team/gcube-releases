/**
 * 
 */
package org.gcube.common.homelibary.model.items.gcube;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface DocumentPartLink {
	
	/**
	 * Return the parent URI.
	 * @return the parent URI.
	 */
	public String getParentURI();
	
	/**
	 * The info object URI.
	 * @return the URI.
	 */
	public String getURI();
	
	/**
	 * Return this part name.
	 * @return the part name.
	 */
	public String getName();
	
	/**
	 * The part mime type.
	 * @return the mime type.
	 */
	public String getMimeType();

}
