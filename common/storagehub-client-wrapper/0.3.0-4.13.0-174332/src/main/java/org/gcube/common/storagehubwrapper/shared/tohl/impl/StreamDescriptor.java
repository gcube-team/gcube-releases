/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import java.io.InputStream;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.gcube.common.storagehubwrapper.shared.tohl.items.ItemStreamDescriptor;


/**
 * Instantiates a new stream descriptor.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 4, 2018
 */

/**
 * Instantiates a new stream descriptor.
 */
@NoArgsConstructor

/**
 * Instantiates a new stream descriptor.
 *
 * @param stream the stream
 * @param fileName the file name
 */

/**
 * Instantiates a new stream descriptor.
 *
 * @param stream the stream
 * @param itemName the item name
 */
@AllArgsConstructor

/**
 * Gets the file name.
 *
 * @return the file name
 */

@Getter

/**
 * Sets the item name.
 *
 * @param itemName the new item name
 */
@Setter
public class StreamDescriptor implements ItemStreamDescriptor, Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -5482612709953553644L;
	private InputStream stream;
	private String itemName;
	private Long size;
	private String mimeType;


}
