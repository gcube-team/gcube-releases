package org.gcube.contentmanagement.blobstorage.service.directoryOperation;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import org.gcube.common.encryption.StringEncrypter;


/**
 * This class can be used to encrypt and decrypt using DES and a given key
 *@author Roberto Cirillo (ISTI-CNR)
 *
 */
public class Encrypter {

	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	public static final String DES_ENCRYPTION_SCHEME = "DES";

	private KeySpec keySpec;
	private SecretKeyFactory  keyFactory;
	private static final String UNICODE_FORMAT = "UTF8";

	@Deprecated
	public Encrypter( String encryptionScheme ) throws EncryptionException
	{
		this( encryptionScheme, null );
	}

	public Encrypter( String encryptionScheme, String encryptionKey )
	throws EncryptionException
	{

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

			keyFactory = SecretKeyFactory.getInstance( encryptionScheme);
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
	}

	/**
	 *  Encrypt a string
	 * @param unencryptedString string to encrypt
	 * @return encrypted string
	 * @throws EncryptionException
	 */
	public String encrypt( String unencryptedString ) throws EncryptionException
	{
		if ( unencryptedString == null || unencryptedString.trim().length() == 0 )
			throw new IllegalArgumentException(
			"unencrypted string was null or empty" );
		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			return StringEncrypter.getEncrypter().encrypt(unencryptedString, key);//t(unencryptedString, key);
		}
		catch (Exception e)
		{
			throw new EncryptionException( e );
		}
	}

	/**
	 * decrypt a string
	 * @param encryptedString encrypted string
	 * @return decrypted string
	 * @throws EncryptionException
	 */
	public String decrypt( String encryptedString ) throws EncryptionException
	{
		if ( encryptedString == null || encryptedString.trim().length() <= 0 )
			throw new IllegalArgumentException( "encrypted string was null or empty" );

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			return StringEncrypter.getEncrypter().decrypt(encryptedString, key);
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
