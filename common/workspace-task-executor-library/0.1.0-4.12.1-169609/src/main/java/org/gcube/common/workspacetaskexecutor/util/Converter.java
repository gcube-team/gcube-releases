/**
 *
 */
package org.gcube.common.workspacetaskexecutor.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



/**
 * The Class Converter.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 16, 2018
 */
public class Converter {


	private static SimpleDateFormat defaultSDF = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");


	/**
	 * Gets the enum list.
	 *
	 * @param <E> the element type
	 * @param enumClass the enum class
	 * @return the enum list
	 */
	public static <E extends Enum<E>> List<E> getEnumList(Class<E> enumClass) {
		 return new ArrayList<E>(Arrays.asList(enumClass.getEnumConstants()));
	}


	/**
	 * Gets the current formatted date.
	 *
	 * @param sdf the sdf
	 * @return the current formatted date
	 */
	public static String getCurrentFormattedDate(SimpleDateFormat sdf){
		if(sdf==null)
			sdf = defaultSDF;

		 return sdf.format(new Date());
	}
}
