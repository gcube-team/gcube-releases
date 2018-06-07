package org.gcube.common.utils.encryption;

import static org.junit.Assert.*;

import java.security.InvalidKeyException;
import java.security.Key;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SymmetricKeyTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testGetKey() {
		try {
			Key key = SymmetricKey.getKey();
			System.out.println("key successfully loaded");
			System.out.println("key algorithm " + key.getAlgorithm());

		} catch (InvalidKeyException e) {
			e.printStackTrace();
			fail("Failed to load the symmetric key");
		}
	}

}
