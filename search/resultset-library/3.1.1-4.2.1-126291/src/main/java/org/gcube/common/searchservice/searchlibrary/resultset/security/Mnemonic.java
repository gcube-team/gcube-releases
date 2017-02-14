package org.gcube.common.searchservice.searchlibrary.resultset.security;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.log4j.Logger;

/**
 * Encryption helper for the RS
 * @author Konstantinos Tsakalozos
 */
public class Mnemonic {
	private SecretKey key = null;
	private Cipher desCipher = null;
	private static Logger logger = Logger.getLogger(HeadMnemonic.class);
	
	/**
	 * Generate a secret key
	 * @return the Secret key
	 * @throws Exception Failure in generating the key
	 */
	public static SecretKey genKey() throws Exception
	{
		javax.crypto.KeyGenerator kg = javax.crypto.KeyGenerator.getInstance("DES");
	    kg.init(56); // 56 is the keysize. Fixed for DES
	    SecretKey key = kg.generateKey();
	    return key;
	}

	/**
	 * Create a new encryption helper
	 * @param enckey the encryption key
	 * @throws Exception Failed to create the helper
	 */
	public Mnemonic(byte[] enckey) throws Exception
	{
		logger.trace("Mnemonic");
//		logger.info("Secret Key to use: "+ new String(enckey));
		DESKeySpec keySpec = new DESKeySpec(enckey);
		//DESKeySpec skeySpec = new DESKeySpec(enckey);//, "DES");
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		//this.key = keyFactory.generateSecret(skeySpec);
		this.key = keyFactory.generateSecret(keySpec);
	}
	
	/**
	 * Create a new encryption helper
	 * @param key the encryption key
	 * @throws Exception Failed to create the helper
	 */
	public Mnemonic(Key key) throws Exception
	{
		this.key = (SecretKey)key;
		desCipher = Cipher.getInstance("DES");
	}

	/**
	 * Encrypt input byte array
	 * @param input byte array
	 * @return the encrypted byte array
	 * @throws Exception Encryption failed
	 */
	public byte[] Encrypt(byte[] input) throws Exception
	{
	    desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	    desCipher.init(Cipher.ENCRYPT_MODE, key);
	    //return desCipher.doFinal(inpBytes);
    	  return (new sun.misc.BASE64Encoder().encode(desCipher.doFinal(input))).getBytes();
	}

	/**
	 * Decrypt input byte array
	 * @param input byte array
	 * @return the decrypted byte array
	 * @throws Exception Encryption failed
	 */
	public byte[] Decrypt(byte[] input) throws Exception
	{
		desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	    desCipher.init(Cipher.DECRYPT_MODE, key);
//	    return desCipher.doFinal(input);
        return desCipher.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(new String(input)));
	}
	
	@SuppressWarnings("unused")
	private Mnemonic(){}

	/**
	 * Get Cipher
	 * @return the cipher
	 * @throws Exception Failure
	 */
	public Cipher getCipher() throws Exception{
	    desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	    desCipher.init(Cipher.ENCRYPT_MODE, key);
		return desCipher;
	}

	/**
	 * Get De-Cipher
	 * @return the De-cipher
	 * @throws Exception Failure
	 */
	public Cipher getDeCipher() throws Exception{
		desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	    desCipher.init(Cipher.DECRYPT_MODE, key);
		return desCipher;
	}

	/**
	 * Get Secret key
	 * @return secret key
	 */
	public SecretKey getKey() {
		return key;
	}

	/**
	 * Set Secret key
	 * @param key secret key
	 */
	public void setKey(SecretKey key) {
		this.key = key;
	}
}
