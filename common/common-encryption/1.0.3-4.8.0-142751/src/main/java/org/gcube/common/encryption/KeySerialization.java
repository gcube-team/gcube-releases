package org.gcube.common.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;


/**
 * Manage keys serialization
 * @author Manuele Simi (CNR)
 *
 */
public class KeySerialization { 
	
    /**
     * Stores the key 
     * @param key the key to store
     * @param file the file where to store the key
     * @throws Exception
     */
    protected static void store(Key key, File file) throws Exception {
        byte[] keyBytes = key.getEncoded();
        FileOutputStream f = new FileOutputStream(file);
        f.write(keyBytes);
        f.close();
        System.out.println("Key successfully stored in " + file.toURI().toURL().toString());

    }
    
	/**
     * Loads the key
     * @param file the name of the file where the key has been stored
     * @param jceAlgorithmName the name of the algorithm of the key
     */
    protected static SecretKey loadDESede(File file, String jceAlgorithmName) throws Exception {
    	byte[] data=getBytesFromFile(file);
//        DESedeKeySpec keySpec = new DESedeKeySpec(JavaUtils.getBytesFromFile(file.getAbsolutePath()));
    	DESedeKeySpec keySpec = new DESedeKeySpec(data);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(jceAlgorithmName);
        SecretKey key = skf.generateSecret(keySpec);
        System.out.println("Key successfully loaded from " + file.toURI().toURL().toString());
        return key;
    }

	private static byte[] getBytesFromFile(File file) {
		byte[] data = new byte[(int) file.length()];
    	try {
    	    new FileInputStream(file).read(data);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	return data;
	}

 }
