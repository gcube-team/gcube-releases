package org.gcube.common.utils.encryption;

import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.apache.xml.security.utils.JavaUtils;

final class SymmetricKey {

	private static Key key;
	
	private static String localKey = "/symm.key";
		
	private static String keyAlgorithm = "AES";
		
	
	//private constructor
	private SymmetricKey() {}
	
	/**
	 * Gets the key for encryption/decryption
	 * @return the key
	 * @throws InvalidKeyException if the key is not available or is invalid
	 */
	protected static Key getKey() throws InvalidKeyException {
		if (key == null) load();
		return key;
	}

	/**
	 * Loads the key from the classpaht
	 * @throws InvalidKeyException if the key is not available or is invalid
	 */
	private static void load() throws InvalidKeyException {
		byte[] rawKey;
		 try {
			 rawKey = JavaUtils.getBytesFromStream(SymmetricKey.class.getResourceAsStream(localKey));
		} catch (Exception e) {
				System.out.println("Unable to load the Key from the classpath");	
				e.printStackTrace();
				throw new InvalidKeyException();
		} 
		 try {
			 key = new SecretKeySpec(rawKey, 0, rawKey.length, keyAlgorithm);
		 }catch (Exception e) {
			e.printStackTrace();
			throw new InvalidKeyException();
		 } 
	}
	
}
