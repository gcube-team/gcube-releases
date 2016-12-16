package org.gcube.common.searchservice.searchlibrary.resultset.security;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

import sun.security.rsa.RSAKeyPairGenerator;

/**
 * Key generator
 * @author Konstantinos Tsakalozos
 */
public class KeyGenerator {

	private int keySize = 512;
	
	/**
	 * Generate a key from byte array
	 * @param encKey the encryption key
	 * @return the public key
	 * @throws Exception Failed to generate key
	 */
	public static PublicKey GenKeyFromBytes(byte [] encKey) throws Exception
	{
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(pubKeySpec);
	}
	
	/**
	 * Generate a key pair
	 * @return the key pair
	 * @throws Exception Failed to generate key
	 */
	public KeyPair GenKeyPair() throws Exception
	{
			RSAKeyPairGenerator keyGen = new RSAKeyPairGenerator();//.getInstance("RSA");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(keySize, random);
			KeyPair pair = keyGen.generateKeyPair();
			return pair;
	}

	/**
	 * Get the key size
	 * @return the key size
	 */
	public int getKeySize() {
		return keySize;
	}

	/**
	 * Set the key size
	 * @param keySize the key size
	 * @return true if the set was successful
	 */
	public boolean setKeySize(int keySize) {
		if ((keySize % 2) ==0){
			this.keySize = keySize;
			return true;
		}else{
			return false;
		}
	}
	
}
