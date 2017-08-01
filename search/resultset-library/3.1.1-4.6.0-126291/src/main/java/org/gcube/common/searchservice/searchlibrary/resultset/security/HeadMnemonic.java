package org.gcube.common.searchservice.searchlibrary.resultset.security;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;

/**
 * Encryption helper for the header of the RS
 * @author Konstantinos Tsakalozos
 */
public class HeadMnemonic {
	private RSAPublicKey publickey = null;
	private PrivateKey privatekey = null;
	private Cipher RSACipher = null;
	private static Logger logger = Logger.getLogger(HeadMnemonic.class);
	
	
	/**
	 * Create e new Encryption header helper
	 * @throws Exception could not create encryption 
	 */
	public HeadMnemonic() throws Exception
	{
		RSACipher = Cipher.getInstance("RSA");
	}

	/**
	 * Enable RS encryption
	 * @param pk Public key 
	 * @throws Exception An error occurred
	 */
	public void EnableEncryption(PublicKey pk) throws Exception
	{
		logger.trace("EnableEncryption");
//		logger.trace("Key to decode: "+ new String(pk.getEncoded()));
		setPublickey((RSAPublicKey)pk);
	}

	/**
	 * Enable RS encryption
	 * @param pk Public key 
	 * @throws Exception An error occurred
	 */
	public void EnableEncryption(byte [] pk) throws Exception
	{
	    // create public key
//		logger.info("Key to decode: "+ new String(pk));
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pk);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		setPublickey((RSAPublicKey)kf.generatePublic(publicKeySpec));
	}

	/**
	 * Enable RS decryption
	 * @param pk Public key 
	 * @throws Exception An error occurred
	 */
	public void EnableDecryption(PrivateKey pk) throws Exception
	{
		setPrivatekey(pk);
	}

	/**
	 * Enable RS decryption
	 * @param pk Public key 
	 * @throws Exception An error occurred
	 */
	public void EnableDecryption(byte [] pk) throws Exception
	{
	    // create private key
	    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(pk);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    setPrivatekey(kf.generatePrivate(privateKeySpec));
	    
	}

	/**
	 * Encrypt input byte array
	 * @param input byte array
	 * @return the encrypted byte array
	 * @throws Exception Encryption failed
	 */
	public String Encrypt(byte[] input) throws Exception
	{
	      RSACipher.init(Cipher.ENCRYPT_MODE, publickey);
	  	  return new sun.misc.BASE64Encoder().encode(RSACipher.doFinal(input));
/*	      String enc = new String("Foo");
	      logger.info("Encrypted Data: "+ enc);
	  	  return "foo".getBytes();*/	
	}

	/**
	 * Decrypt input byte array
	 * @param input byte array
	 * @return the encrypted byte array
	 * @throws Exception Encryption failed
	 */
	public byte[] Decrypt(String input) throws Exception
	{
	      RSACipher.init(Cipher.DECRYPT_MODE, privatekey);
	      return RSACipher.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(input));
	}

	/**
	 * Get private key
	 * @return the private key
	 */
	public PrivateKey getPrivatekey() {
		return privatekey;
	}

	/**
	 * Set private key
	 * @param privatekey the private key
	 */
	public void setPrivatekey(PrivateKey privatekey) {
		this.privatekey = privatekey;
	}

	
	/**
	 * Get public key
	 * @return the public key
	 */
	public RSAPublicKey getPublickey() {
		return publickey;
	}

	/**
	 * Set public key
	 * @param publickey the public key
	 */
	public void setPublickey(RSAPublicKey publickey) {
		this.publickey = publickey;
	}
	
}
