package org.gcube.common.encryption;

import java.security.Key;

/**
 * 
 * @author Roberto Cirillo (CNR)
 *
 * @param <T> the type of the object to encrypt/decrypt
 * 
 * Use {@link org.gcube.common.encryption.encrypter.Encrypter} instead
 */
@Deprecated
public interface IEncrypter<T> {
	
	/**
	 * Encrypts <T> with the given key or the default key
	 * @param t the object to encrypt
	 * @param key the key
	 * @return the encrypted object
	 * @throws Exception if the key is not available, invalid or the object cannot be encrypted

	 */
	@Deprecated
	public T encrypt(T t, Key ... key) throws Exception;
	
	/**
	 * Decrypts <T> with the given key or the default key
	 * @param t the object to decrypt
	 * @param key the key
	 * @return the decrypted object
	 * @throws Exception if the key is not available, invalid or the object cannot be decrypted
	 */
	@Deprecated
	public T decrypt(T t, Key ... key) throws Exception;

	
}