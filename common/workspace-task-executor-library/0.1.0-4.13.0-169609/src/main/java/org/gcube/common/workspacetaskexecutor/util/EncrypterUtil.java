/**
 *
 */
package org.gcube.common.workspacetaskexecutor.util;

import org.gcube.common.encryption.StringEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class EncrypterUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public class EncrypterUtil {

	private static Logger logger = LoggerFactory.getLogger(EncrypterUtil.class);


	/**
	 * Encrypt string.
	 *
	 * @param toEncrypt the to encrypt
	 * @return the string
	 */
	public static String encryptString(String toEncrypt){
		try {
			return StringEncrypter.getEncrypter().encrypt(toEncrypt);
		}
		catch (Exception e) {
			//silent
			logger.warn("Encrypt error for the string: "+toEncrypt);
		}
		return toEncrypt;
	}


	/**
	 * Decrypt string.
	 *
	 * @param toDecrypt the to decrypt
	 * @return the string
	 */
	public static String decryptString(String toDecrypt){
		try {
			return StringEncrypter.getEncrypter().decrypt(toDecrypt);
		}
		catch (Exception e) {
			//silent
			logger.warn("Decrypt error for the string: "+toDecrypt);
		}
		return toDecrypt;
	}
}
