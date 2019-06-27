package org.gcube.contentmanager.storageclient.model.protocol.smp;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class can be used to encrypt and decrypt using DES and a given key
 *
 * @author Javid Jamae @author Roberto Cirillo
 * 
 *
 */
public class StringDecrypter {

	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	public static final String DES_ENCRYPTION_SCHEME = "DES";
	private KeySpec  keySpec;
	private SecretKeyFactory keyFactory;
	private Cipher  cipher;
	private static final String  UNICODE_FORMAT = "UTF8";
	private static final Logger logger = LoggerFactory.getLogger(StringDecrypter.class);

	public StringDecrypter( String encryptionScheme , String phrase) throws EncryptionException{
		if(phrase!=null)
			buildEncryption(encryptionScheme, phrase);
		else
			throw new RuntimeException("No valid encripted phrase discovered on IS");
//			buildEncryption(encryptionScheme, DEFAULT_ENCRYPTION_KEY);
	}

	private void buildEncryption(String encryptionScheme, String encryptionKey)
			throws EncryptionException {
		if ( encryptionKey == null )
			throw new IllegalArgumentException( "encryption key was null" );
		if ( encryptionKey.trim().length() < 24 )
			throw new IllegalArgumentException(
			"encryption key was less than 24 characters" );

		try
		{
			byte[] keyAsBytes = encryptionKey.getBytes( UNICODE_FORMAT );

			if ( encryptionScheme.equals( DESEDE_ENCRYPTION_SCHEME) )
			{
				keySpec = new DESedeKeySpec( keyAsBytes );
			}
			else if ( encryptionScheme.equals( DES_ENCRYPTION_SCHEME ) )
			{
				keySpec = new DESKeySpec( keyAsBytes );
			}
			else
			{
				throw new IllegalArgumentException( "Encryption scheme not supported: "
						+ encryptionScheme );
			}

			keyFactory = SecretKeyFactory.getInstance( encryptionScheme );
			cipher = Cipher.getInstance( encryptionScheme );

		}
		catch (InvalidKeyException e)
		{
			throw new EncryptionException( e );
		}
		catch (UnsupportedEncodingException e)
		{
			throw new EncryptionException( e );
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new EncryptionException( e );
		}
		catch (NoSuchPaddingException e)
		{
			throw new EncryptionException( e );
		}
	}

	public String decrypt( String encryptedString ) throws EncryptionException
	{
		logger.trace("decrypting string...");
		if ( encryptedString == null || encryptedString.trim().length() <= 0 )
			throw new IllegalArgumentException( "encrypted string was null or empty" );

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			cipher.init( Cipher.DECRYPT_MODE, key );
			return org.gcube.common.encryption.StringEncrypter.getEncrypter().decrypt(encryptedString, key);
		}
		catch (Exception e)
		{
			throw new EncryptionException( e );
		}
	}


	@SuppressWarnings("serial")
	public static class EncryptionException extends Exception
	{
		public EncryptionException( Throwable t )
		{
			super( t );
		}
	}
	
}
