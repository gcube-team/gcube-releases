/**
 * 
 */
package org.gcube.common.homelibary.model.items.type;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum FolderItemType implements GenericItemType{
	
	/**
	 * External image.
	 */
	EXTERNAL_IMAGE,
	
	/**
	 * External file.
	 */
	EXTERNAL_FILE,
	
	/**
	 * External PDF file.
	 */
	EXTERNAL_PDF_FILE,
	
	/**
	 * External url. 
	 */
	EXTERNAL_URL,
	
	/**
	 * Query.
	 */
	QUERY,
	
	/**
	 * Report template.
	 */
	REPORT_TEMPLATE,
	
	/**
	 * Report. 
	 */
	REPORT,
	
	/**
	 * A time series.
	 */
	TIME_SERIES,
	
	/**
	 * Document.
	 */
	DOCUMENT,
	
	/**
	 * Image document.
	 */
	IMAGE_DOCUMENT,
	
	/**
	 * PDF document.
	 */
	PDF_DOCUMENT,
	
	/**
	 * Url document.
	 */
	URL_DOCUMENT,
	
	/**
	 * Metadata.
	 */
	METADATA,
	
	/**
	 * Trash item
	 */
	TRASH_ITEM,

	/**
	 * A gCube item.
	 */	
	GCUBE_ITEM,
	
	/**
	 * Workflow report. 
	 */
	WORKFLOW_REPORT;
	
}
