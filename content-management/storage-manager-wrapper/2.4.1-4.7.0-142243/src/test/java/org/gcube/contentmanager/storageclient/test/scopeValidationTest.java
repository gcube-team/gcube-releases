package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import org.gcube.common.scope.impl.ServiceMapScannerMediator;
import org.junit.BeforeClass;
import org.junit.Test;

public class scopeValidationTest {

	static ServiceMapScannerMediator scanner;
	String scope="/d4science.research-infrastructures.eu";
	
	@BeforeClass
	public static void init(){
		scanner=new ServiceMapScannerMediator();
	}
	
	
	@Test
	public void test() {
		assertTrue(scanner.isValid(scope));
	}

}
