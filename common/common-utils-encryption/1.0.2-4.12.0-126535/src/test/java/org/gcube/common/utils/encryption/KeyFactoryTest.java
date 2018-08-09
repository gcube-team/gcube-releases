package org.gcube.common.utils.encryption;

import static org.junit.Assert.*;



import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tester for {@link KeyFactory}
 * @author manuele
 *
 */
public class KeyFactoryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testNewAESKey() {
		try {
			KeyFactory.newAESKey();
			//KeySerialization.store(key, new File("/Users/manuele/work/workspace-gcf/tests/EncryptionWithSantuario/src/main/resources/symm.xml"));
		} catch (Exception e) {
			fail("");
			e.printStackTrace();
		}
	}

	@Test
	public final void testNewTripleDESKey() {
		try {
			KeyFactory.newTripleDESKey();
		} catch (Exception e) {
			fail("");
			e.printStackTrace();
		}
	}

	@Test
	public final void testNewRijndaelKey() {
		try {
			KeyFactory.newRijndaelKey();
		} catch (Exception e) {
			fail("");
			e.printStackTrace();
		}
	}

	@Test
	public final void testNewDESKey() {
		try {
			KeyFactory.newDESKey();
		} catch (Exception e) {
			fail("");
			e.printStackTrace();
		}
	}


}
