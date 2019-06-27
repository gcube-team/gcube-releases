/**
 *
 */

package org.gcube.common.storagehubwrapper.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Oct 17, 2018
 */
public class WrapperUtility {

	private static Logger logger = LoggerFactory.getLogger(WrapperUtility.class);

	/**
	 * To input stream.
	 *
	 * @param content the content
	 * @return the input stream
	 */
	public static InputStream toInputStream(byte[] content) {

		int size = content.length;
		InputStream is = null;
		byte[] b = new byte[size];
		try {
			is = new ByteArrayInputStream(content);
			is.read(b);
			return is;
		}
		catch (IOException e) {
			logger.warn("ToInputStream error: ",e);
			return null;
		}
		finally {
			try {
				if (is != null)
					is.close();
			}
			catch (Exception ex) {
			}
		}
	}
}
