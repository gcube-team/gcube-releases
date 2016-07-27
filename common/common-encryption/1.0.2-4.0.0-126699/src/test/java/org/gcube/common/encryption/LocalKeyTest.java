package org.gcube.common.encryption;

import static org.junit.Assert.*;

import java.security.InvalidKeyException;

import org.gcube.common.encryption.SymmetricKey;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalKeyTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
	}
	
	@Test
	public void test() throws InvalidKeyException {
		String key=SymmetricKey.getKeyFileName();
		System.out.println("file key founded: "+key);
	}

}
