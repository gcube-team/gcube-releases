package org.acme;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.ArrayList;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.uri.HttpMint;
import org.gcube.common.uri.Mint;
import org.gcube.common.uri.ap.AuthorityProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HttpUriMintTest {

	@Mock
	AuthorityProvider provider;
	
	Mint mint;
	
	String authority = "data.mine.org";
	
	
	@Before
	public void setup() {
		

		when(provider.authority()).thenReturn(authority);
		
		mint= new HttpMint(provider);
	}
	
	@Test
	public void mintOne() {
		
		ScopeProvider.instance.set("/some/scope");
		
		URI uri = mint.mint(asList("some","path"));
		
		assertEquals(uri.getAuthority(), authority);
		assertEquals(uri.getPath(), "/some/path");
	}
	
	@Test
	public void mintErrors() {

		try {
			mint.mint(null);
			fail();
		}
		catch(IllegalArgumentException e) {
			
		}
		
		try {
			mint.mint(new ArrayList<String>());
			fail();
		}
		catch(IllegalArgumentException e) {
			
		}
		
		try {
			mint.mint(asList("","path"));
			fail();
		}
		catch(IllegalArgumentException e) {
			
		}
		
		try {
			mint.mint(asList(null,"path"));
			fail();
		}
		catch(IllegalArgumentException e) {
			
		}
		
	}
}
