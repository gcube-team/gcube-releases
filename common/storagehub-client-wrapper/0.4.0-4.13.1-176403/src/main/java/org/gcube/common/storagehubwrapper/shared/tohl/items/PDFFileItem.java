/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.items;


/**
 * The Interface FileItemPDF.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 21, 2018
 */
public interface PDFFileItem extends FileItem{

	/**
	 * Gets the number of pages.
	 *
	 * @return the numberOfPages
	 */
	public Long getNumberOfPages();


	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion();


	/**
	 * Gets the author.
	 *
	 * @return the author
	 */
	public String getAuthor();


	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle();


	/**
	 * Gets the producer.
	 *
	 * @return the producer
	 */
	public String getProducer();


}
