package org.gcube.contentmanager.storageserver.test;

import static org.junit.Assert.*;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageserver.parse.utils.ValidationUtils;
import org.junit.Test;

public class ValidationScopeTest {
	
	private String scope="/d4science.research-infrastructures.eu/FARM";

	@Test
	public void test() {
		assertTrue(ValidationUtils.validationScope(scope));
	}

}
