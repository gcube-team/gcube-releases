package org.gcube.common.utils.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.xml.security.utils.JavaUtils;


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
        DESedeKeySpec keySpec = new DESedeKeySpec(JavaUtils.getBytesFromFile(file.getAbsolutePath()));
        SecretKeyFactory skf = SecretKeyFactory.getInstance(jceAlgorithmName);
        SecretKey key = skf.generateSecret(keySpec);
        System.out.println("Key successfully loaded from " + file.toURI().toURL().toString());
        return key;
    }

 }
