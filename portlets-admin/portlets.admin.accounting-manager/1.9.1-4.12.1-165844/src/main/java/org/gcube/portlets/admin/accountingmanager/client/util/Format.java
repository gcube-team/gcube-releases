/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.client.util;

/**
 * 
  * @author Giancarlo Panichi 
 *
 *
 */
public class Format {

	/**
	 * Converts a file size into a {@link String} representation adding the misure unit.
	 * @param size the file size.
	 * @return the textual representation.
	 */
	public static String fileSize(long size) {
		StringBuilder text = new StringBuilder();
		if (size < 1024) {
			text.append(size);
			text.append(" bytes");
		} else if (size < 1048576) {
			text.append(Math.round(((size * 10) / 1024)) / 10);
			text.append(" KB");
		} else if (size < 1073741824) {
			text.append(Math.round(((size * 10) / 1048576)) / 10);
			text.append(" MB");
		} else {
			text.append(Math.round(((size * 10) / 1073741824)) / 10);
			text.append(" GB");
		}
		return text.toString();
	}

}
