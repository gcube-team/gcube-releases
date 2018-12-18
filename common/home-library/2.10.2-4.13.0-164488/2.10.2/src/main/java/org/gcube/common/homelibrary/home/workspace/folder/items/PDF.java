/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder.items;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface PDF {
	
	/**
	 * The PDF number of pages.
	 * @return the number of pages.
	 */
	public int getNumberOfPages();
	
	/**
	 * The PDF version.
	 * @return the version.
	 */
	public String getVersion();
	
	/**
	 * The PDF author.
	 * @return the author.
	 */
	public String getAuthor();
	
	/**
	 * The PDF title.
	 * @return the title.
	 */
	public String getTitle();
	
	/**
	 * The PDF producer.
	 * @return the producer.
	 */
	public String getProducer();

}
