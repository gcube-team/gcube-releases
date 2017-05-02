package org.acme;
import static org.junit.Assert.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.uri.ScopedMint;
import org.gcube.common.uri.ScopedURIBean;
import org.junit.Test;

public class UriBeanTest {

	@Test
	public void accessBean() {
		
		String scope = "/some/scope";
		
		ScopeProvider.instance.set(scope);
		
		List<String> elements = Arrays.asList("some","path");
		
		ScopedMint mint = new ScopedMint();
		
		URI original = mint.mint(elements); 
		
		ScopedURIBean bean = new ScopedURIBean(original);
		
		ScopeProvider.instance.set(bean.scope());
		
		URI derived = mint.mint(bean.elements());
		
		assertEquals(original,derived);
	}
}
