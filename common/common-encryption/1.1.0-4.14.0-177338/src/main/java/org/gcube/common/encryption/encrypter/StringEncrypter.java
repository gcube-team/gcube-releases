package org.gcube.common.encryption.encrypter;

import javax.crypto.Cipher;
import java.security.Key;
import javax.xml.bind.DatatypeConverter;

import org.gcube.common.encryption.SymmetricKey;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class StringEncrypter implements Encrypter<String> {
	
	private static StringEncrypter singleton;
	
	private StringEncrypter() {
	}
	
	/**
	 * Gets the Encrypter for {@link String}
	 * @return the encrypter
	 */
	public static StringEncrypter getEncrypter() {
		if(singleton == null) {
			singleton = new StringEncrypter();
		}
		return singleton;
	}
	
	/**
	 * Encrypts the string with the context Key
	 * @param string the string to encrypt
	 * @return the encrypted string in a Base64 encoding
	 * @throws Exception
	 */
	@Override
	public String encrypt(String string) throws Exception {
		Key ekey = SymmetricKey.getKey();
		return encrypt(string, ekey);
	}
	
	/**
	 * Encrypts the string with the given key
	 * @param string the string to encrypt
	 * @param key the key for encrypting
	 * @return the encrypted string in a Base64 encoding
	 * @throws Exception
	 */
	@Override
	public String encrypt(String string, Key ekey) throws Exception {
		Cipher cipher = Cipher.getInstance(ekey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, ekey);
		// return new String(Base64.encode(cipher.doFinal(string.getBytes())));
		return new String(DatatypeConverter.printBase64Binary((cipher.doFinal(string.getBytes()))));
	}
	
	/**
	 * Decrypts the string with the given key
	 * @param key the key to use for decrypting
	 * @return the decripted string
	 * @throws Exception
	 */
	@Override
	public String decrypt(String string) throws Exception {
		Key dkey = SymmetricKey.getKey();
		return decrypt(string, dkey);
	}
	
	/**
	 * Decrypts the string with the given key
	 * @param key the key to use for decrypting
	 * @return the decripted string
	 * @throws Exception
	 */
	@Override
	public String decrypt(String string, Key dkey) throws Exception {
		Cipher cipher = Cipher.getInstance(dkey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, dkey);
		// return new String(cipher.doFinal(Base64.decode(string.getBytes())));
		return new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(string)));
	}
	
}
