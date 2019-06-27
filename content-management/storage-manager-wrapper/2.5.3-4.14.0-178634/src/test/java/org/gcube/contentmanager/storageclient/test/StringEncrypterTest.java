package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter;
import org.gcube.contentmanager.storageclient.model.protocol.smp.StringDecrypter.EncryptionException;
import org.gcube.contentmanager.storageclient.test.utils.Costants;
import org.junit.BeforeClass;
import org.junit.Test;

public class StringEncrypterTest {

//	private static StringEncrypter encrypter;
	private static Encrypter encrypter;
	private String phrase="yfvjAEFu5UuhW9vsQ9E8MQSglmEKAPkBG7AbD75ZKLZWnh3DQferKg==";
	private String phrase1="smp://Wikipedia_logo_silver.png?";
//	private static final String passPhrase="this is a phrasethis is a phrase";
	private String encryption="";
	private String decryption="";
	
	@BeforeClass
	public static void setEncrypter() throws EncryptionException, org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter.EncryptionException{
//		encrypter=new StringEncrypter("DES");
		encrypter=new Encrypter("DES", Costants.DEFAULT_PASS_PHRASE);
	}
	
	@Test
	public void decrypt() {
//		try {
			try {
				decryption=encrypter.decrypt(phrase);
			} catch (org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter.EncryptionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		} catch (EncryptionException e) {
//			
//		}
		System.out.println("decryption: "+decryption);
		assertNotNull(decryption);
	}

	@Test
	public void encrypt() {
//		try {
			try {
				encryption=encrypter.encrypt(phrase1);
			} catch (org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter.EncryptionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		} catch (EncryptionException e) {
//			
//		}
		System.out.println("encryption: "+encryption);
		assertNotNull(encryption);
	}

	
}
