/**
 * 
 */
package org.gcube.common.homelibrary.consistency;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CheckerUtil {
	
	/**
	 * Check if a field is null.
	 * @param <T> the field type.
	 * @param fieldName field name.
	 * @param fieldValue field value.
	 * @throws CheckException if the field is null.
	 */
	public static <T> void checkNotNull(String fieldName, T fieldValue) throws CheckException {
		if (fieldValue == null) throw new CheckException(fieldName+" field null");
	}
	
	/**
	 * Check the passed stream.
	 * @param fieldName the field name.
	 * @param is the stream to check
	 * @throws CheckException if there are errors during the stream check.
	 */
	public static void checkStream(String fieldName, InputStream is) throws CheckException {
		try {
			IOUtils.copy(is, new NullOutputStream());
			is.close();
		} catch (IOException e) {
			throw new CheckException("Error checking the stream for field "+fieldName, e);
		}
	}

}
