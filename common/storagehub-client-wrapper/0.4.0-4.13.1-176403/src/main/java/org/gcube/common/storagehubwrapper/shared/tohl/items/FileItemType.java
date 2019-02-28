/**
 *
 */

package org.gcube.common.storagehubwrapper.shared.tohl.items;

import org.gcube.common.storagehub.model.types.GenericItemType;

/**
 * The Enum FileItemType.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 *         Jun 21, 2018
 */
public enum FileItemType implements GenericItemType {
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
	 * A gCube item.
	 */
	GCUBE_ITEM
}
