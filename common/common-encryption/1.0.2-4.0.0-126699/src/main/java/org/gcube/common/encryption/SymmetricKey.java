package org.gcube.common.encryption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

//import org.apache.xml.security.utils.JavaUtils;
import javax.xml.bind.DatatypeConverter;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;


final class SymmetricKey {

	private static Key key;
		
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
		String keyFileName=null;
		 try {
			 keyFileName=getKeyFileName();
			 InputStream is =SymmetricKey.class.getResourceAsStream("/"+keyFileName);
			 rawKey = getBytesFromStream(is);
		} catch (Exception e) {
				System.out.println("Unable to load the Key "+keyFileName+" from the classpath");	
				e.printStackTrace();
				throw new InvalidKeyException("Unable to load the Key "+keyFileName+" from the classpath");
		} 
		 try {
			 key = new SecretKeySpec(rawKey, 0, rawKey.length, keyAlgorithm);
		 }catch (Exception e) {
			e.printStackTrace();
			throw new InvalidKeyException();
		 } 
	}

	private static byte[] getBytesFromStream(InputStream is) throws IOException {
		byte[] rawKey;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}
		buffer.flush();
		rawKey= buffer.toByteArray();
		return rawKey;
	}
	
	
	protected static String getKeyFileName() throws InvalidKeyException{
		String keyFile=null;
		String scope = ScopeProvider.instance.get();
		if(scope!=null){
			ScopeBean bean = new ScopeBean(scope);
			if(bean.is(Type.VRE)) 
	     		bean = bean.enclosingScope(); 
			String name = bean.name();
	//build keyfile name with name
			keyFile=name+".gcubekey";
		}else{
			throw new InvalidKeyException(" invalid key for scope: "+scope);
		}
		return keyFile;
	}
	
}
