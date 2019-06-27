package org.gcube.common.encryption;

import static org.junit.Assert.fail;

import java.security.InvalidKeyException;
import java.security.Key;

import org.gcube.common.scope.api.ScopeProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SymmetricKeyTest {

	Key key1;
	Key key2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testGetKey() {
		try {
			key1= SymmetricKey.getKey();
			System.out.println("key successfully loaded");
			System.out.println("key " + key1.getEncoded());
			System.out.println("key algorithm " + key1.getAlgorithm());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			fail("Failed to load the symmetric key");
		}
	}

//	@Test
//	public final void testGetKeyOld() throws InvalidKeyException{
//		String keyAlgorithm = "AES";
//		String localKey = "/symm.key";
//		byte[] rawKey;
//		 try {
//			 rawKey = JavaUtils.getBytesFromStream(SymmetricKey.class.getResourceAsStream(localKey));
//		} catch (Exception e) {
//				System.out.println("Unable to load the Key from the classpath");	
//				e.printStackTrace();
//				throw new InvalidKeyException();
//		} 
//		 try {
//			 key2 = new SecretKeySpec(rawKey, 0, rawKey.length, keyAlgorithm);
//		 }catch (Exception e) {
//			e.printStackTrace();
//			throw new InvalidKeyException();
//		 } 
//			System.out.println("key successfully loaded");
//			System.out.println("key " + key2.getEncoded());
//			System.out.println("key algorithm " + key2.getAlgorithm());
//	}
//	
//	@Test
//	public  final void compare(){
//		assertEquals(key1, key2);
//	}
	

}
