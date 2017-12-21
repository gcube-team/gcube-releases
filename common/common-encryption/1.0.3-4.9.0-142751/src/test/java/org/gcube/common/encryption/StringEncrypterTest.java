package org.gcube.common.encryption;

import static org.junit.Assert.*;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.encryption.SymmetricKey;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class StringEncrypterTest {

	static java.security.Key key;
	static String toEnc = "String to encrypt";
	static String encString;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
		key = SymmetricKey.getKey();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testEncryptDecrypt() {
		try {
			System.out.println("---- STRING ENCRYPTION ----");
			System.out.println("String to encrypt " + toEnc);
			encString = StringEncrypter.getEncrypter().encrypt(toEnc,key);
			System.out.println("Encrypted string " + encString);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to encrypt");
		}
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
