package org.gcube.common.utils.encryption;

import static org.junit.Assert.*;

import org.gcube.common.utils.encryption.StringEncrypter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class StringEncrypterTest {

	static java.security.Key key;
	static String toEnc = "String to encrypt";
	static String encString;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		key = SymmetricKey.getKey();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testEncrypt() {
		try {
			System.out.println("---- STRING ENCRYPTION ----");
			System.out.println("String to encrypt " + toEnc);
			encString = StringEncrypter.getEncrypter().encrypt(toEnc,key);
			System.out.println("Encrypted string " + encString);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to encrypt");
		}
	}

	@Test
	public final void testDecrypt() {
		try {
			System.out.println("---- STRING DECRYPTION ----");
			System.out.println("String to decrypt " + encString);
			System.out.println("Decrypted string " + StringEncrypter.getEncrypter().decrypt(encString,key));
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to decrypt");
		}	
	}

}
