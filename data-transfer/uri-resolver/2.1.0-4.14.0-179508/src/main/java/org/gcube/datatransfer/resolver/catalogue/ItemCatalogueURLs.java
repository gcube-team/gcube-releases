/**
 *
 */
package org.gcube.datatransfer.resolver.catalogue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * The Class ItemCatalogueURLs.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 26, 2018
 */
@AllArgsConstructor
@Getter
@Setter
public class ItemCatalogueURLs {


	private String itemName;
	private boolean isPublicItem;
	private String privateCataloguePortletURL;
	private String publicCataloguePortletURL;
}
