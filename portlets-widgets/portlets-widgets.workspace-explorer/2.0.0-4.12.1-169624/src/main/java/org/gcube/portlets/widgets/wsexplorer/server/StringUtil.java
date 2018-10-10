/**
 * 
 */
package org.gcube.portlets.widgets.wsexplorer.server;

import java.text.DecimalFormat;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 13, 2015
 */
public class StringUtil {
	
	public static String readableFileSize(long size) {
		if(size < 0) return "Unknown";
	    if(size == 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))+units[digitGroups];
	}

}
