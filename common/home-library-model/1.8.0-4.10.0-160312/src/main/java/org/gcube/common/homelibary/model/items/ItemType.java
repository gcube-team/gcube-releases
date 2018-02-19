/**
 * 
 */
package org.gcube.common.homelibary.model.items;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum ItemType {

	/**
	 * A folder.
	 */
	FOLDER,

	/**
	 * A shared folder
	 */
	SHARED_FOLDER,

	/**
	 * A smart folder
	 */
	SMART_FOLDER,

	/**
	 * A folder item.
	 */
	FOLDER_ITEM,

	/**
	 * A trash folder.
	 */	
	TRASH_FOLDER,

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
	 * An AquaMaps Item.
	 */
	AQUAMAPS_ITEM,

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
	 * Annotation.
	 */
	ANNOTATION,

	/**
	 * Document.
	 */
	DOCUMENT_LINK,

	/**
	 * Image document.
	 */
	IMAGE_DOCUMENT_LINK,

	/**
	 * PDF document.
	 */
	PDF_DOCUMENT_LINK,

	/**
	 * Metadata.
	 */
	METADATA_LINK,

	/**
	 * Annotation.
	 */
	ANNOTATION_LINK,

	/**
	 * Workflow report. 
	 */
	WORKFLOW_REPORT,

	/**
	 * Workflow template.
	 */
	WORKFLOW_TEMPLATE,

	/**
	 * External Resource Link
	 */
	EXTERNAL_RESOURCE_LINK,

	/**
	 * Tabular Data Link
	 */
	TABULAR_DATA_LINK,

	/**
	 * Trash item
	 */
	TRASH_ITEM,

	/**
	 * A gCube item.
	 */	
	GCUBE_ITEM, 
	
	/**
	 * A system folder
	 */
	SYSTEM_FOLDER,
	
	/**
	 * A download folder
	 */
	DOWNLOAD_FOLDER,
	
	/**
	 * Root workspace user
	 */
	HOME,
	
	/**
	 * Root sent messages 
	 */
	ROOT_ITEM_SENT, 
	
	/**
	 * Generic file
	 */
	FILE;

}
