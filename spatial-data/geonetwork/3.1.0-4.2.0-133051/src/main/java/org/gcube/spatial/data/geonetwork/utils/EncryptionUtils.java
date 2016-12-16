package org.gcube.spatial.data.geonetwork.utils;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.spatial.data.geonetwork.model.faults.EncryptionException;

public class EncryptionUtils {

	
	public static final String decrypt(String toDecrypt) throws EncryptionException{
		try{
			return StringEncrypter.getEncrypter().decrypt(toDecrypt);
		}catch(Exception e){
			throw new EncryptionException(e);
		}
		
	}
	
	public static final String encrypt(String toEncrypt) throws EncryptionException{		
		try{
			return StringEncrypter.getEncrypter().encrypt(toEncrypt);
		}catch(Exception e){
			throw new EncryptionException(e);
		}
	}
}
