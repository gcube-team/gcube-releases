package org.gcube.common.encryption.encrypter;

import java.security.Key;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 * @param <T> the type of the object to encrypt/decrypt
 */
public interface Encrypter<T> {
	
	public T encrypt(T t) throws Exception;
	
	public T encrypt(T t, Key key) throws Exception;
	
	public T decrypt(T t) throws Exception;
	
	public T decrypt(T t, Key key) throws Exception;
		
}
