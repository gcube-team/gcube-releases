/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;




/**
 * The Interface FileItemURL.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 21, 2018
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper=false)
public class PDFFile extends FileItem implements org.gcube.common.storagehubwrapper.shared.tohl.items.PDFFileItem{

	/**
	 *
	 */
	private static final long serialVersionUID = 1367372682598677200L;

	Long numberOfPages;

	String version;

	String author;

	String title;

	String producer;

}
