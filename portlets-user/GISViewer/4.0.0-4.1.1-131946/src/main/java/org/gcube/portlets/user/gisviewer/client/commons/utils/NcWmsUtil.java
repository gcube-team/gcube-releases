/**
 * 
 */
package org.gcube.portlets.user.gisviewer.client.commons.utils;

import com.google.gwt.i18n.client.NumberFormat;


/**
 * The Class NcWmsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 7, 2016
 */
public class NcWmsUtil {
	

	/**
	 * Format decimal of min max.
	 *
	 * @param minmax the minmax
	 * @param maxNumberOfDecimal the max number of decimal
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String formatDecimalOfMinMax(String minmax, int maxNumberOfDecimal){
//		DecimalFormat df = new DecimalFormat();

		String[] minmaxvalue = minmax.split(",");
		
		double min = Double.parseDouble(minmaxvalue[0]);
		double max = Double.parseDouble(minmaxvalue[1]);
		
//		String format = "#0";
//		if(maxNumberOfDecimal>0){
//			format+=".0";
//			for (int i = 1; i < maxNumberOfDecimal; i++) {
//				format+="#";
//			}
//		}
		StringBuilder numberPattern = new StringBuilder(
	            (maxNumberOfDecimal <= 0) ? "" : ".");
	    for (int i = 0; i < maxNumberOfDecimal; i++) {
	        numberPattern.append('0');
	    }
	    return NumberFormat.getFormat(numberPattern.toString()).format(min)+","+NumberFormat.getFormat(numberPattern.toString()).format(max);
		
	}
}
