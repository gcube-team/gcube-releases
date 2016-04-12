package org.acme;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.gcube.common.uri.ap.CachingSDP;
import org.gcube.common.uri.ap.ScopedAuthorityProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CachingDPTest {

	@Mock
	ScopedAuthorityProvider provider;
	
	@Test
	public void domainsAreCached() {
		
		when(provider.authorityIn(anyString())).thenReturn("some.server");
		
		CachingSDP cached = new CachingSDP(provider);
		
		//ask twice
		cached.authorityIn("foo");
		cached.authorityIn("foo");
		
		//delegate only once
		verify(provider,times(1)).authorityIn(anyString());
		
		
	}
}
