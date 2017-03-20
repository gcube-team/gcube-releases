package org.acme;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.uri.Mint;
import org.gcube.common.uri.ScopedMint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HttpMintTest {

	@Mock
	Mint urimint;
	
	Mint mint;
	
	String authority = "data.mine.org";
	
	
	@Before
	public void setup() {

		when(urimint.mint(anyListOf(String.class))).thenReturn(URI.create("http://foo.org/a/b"));
		
		mint= new ScopedMint(urimint);
	}
	
	@Test
	public void mintOne() {
		
		String scope = "/some/scope";
				
		ScopeProvider.instance.set(scope);
		
		URI uri = mint.mint(asList("some","path"));
		
		assertEquals(uri.getQuery(),"scope="+scope);

	}
	
}
