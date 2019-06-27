package org.gcube.common.encryption;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roberto Cirillo (ISTI - CNR)
 * @author Lucio Lelii (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public final class SymmetricKey {
	
	private static Map<String,Key> keyContextMap = Collections.synchronizedMap(new HashMap<String,Key>());
	
	private static String keyAlgorithm = "AES";
	
	private static Logger logger = LoggerFactory.getLogger(SymmetricKey.class);
	
	//private constructor
	private SymmetricKey() {
	}
	
	/**
	 * Gets the key for encryption/decryption
	 * @return the key
	 * @throws InvalidKeyException if the key is not available or is invalid
	 */
	public synchronized static Key getKey() throws InvalidKeyException {
		if(!keyContextMap.containsKey(ScopeProvider.instance.get()))
			load(ScopeProvider.instance.get());
		return keyContextMap.get(ScopeProvider.instance.get());
	}
	
	public static Key loadKeyFromFile(File keyFile, String keyAlgorithm) throws InvalidKeyException {
		try(InputStream is = new FileInputStream(keyFile)) {
			byte[] rawKey = getBytesFromStream(is);
			Key key = new SecretKeySpec(rawKey, 0, rawKey.length, keyAlgorithm);
			return key;
		} catch(Exception e) {
			throw new InvalidKeyException("Unable to load the Key " + keyFile.getAbsolutePath() + " from the classpath");
		}
	}
	
	/**
	 * Loads the key from the classpath
	 * @throws InvalidKeyException if the key is not available or is invalid
	 */
	private static void load(String context) throws InvalidKeyException {
		byte[] rawKey;
		String keyFileName = null;
		try {
			keyFileName = getKeyFileName(context);
			InputStream is = SymmetricKey.class.getResourceAsStream("/" + keyFileName);
			rawKey = getBytesFromStream(is);
		} catch(Exception e) {
			logger.error("Unable to load the Key " + keyFileName + " from the classpath");
			throw new InvalidKeyException("Unable to load the Key " + keyFileName + " from the classpath");
		}
		try {
			Key key = new SecretKeySpec(rawKey, 0, rawKey.length, keyAlgorithm);
			keyContextMap.put(context, key);
		} catch(Exception e) {
			logger.error("error getting key", e);
			throw new InvalidKeyException();
		}
	}
	
	private static byte[] getBytesFromStream(InputStream is) throws IOException {
		byte[] rawKey;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		rawKey = buffer.toByteArray();
		return rawKey;
	}
	
	protected static String getKeyFileName(String context) throws InvalidKeyException {
		String keyFile = null;
		if(context != null) {
			ScopeBean bean = new ScopeBean(context);
			if(bean.is(Type.VRE))
				bean = bean.enclosingScope();
			String name = bean.name();
			//build keyfile name with name
			keyFile = name + ".gcubekey";
		} else {
			throw new InvalidKeyException("invalid key for scope: " + context);
		}
		return keyFile;
	}
	
}
