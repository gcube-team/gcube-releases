package org.gcube.common.scope;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;

public class ProviderTest {

	@Test
	public void scopesAreThreadInherited() throws Exception {
		
		final ScopeProvider provider = ScopeProvider.instance;
		
		provider.set("scope");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		new Thread() {
			public void run() {
				assertNotNull(provider.get());
				latch.countDown();
			};
		}.start();
		
		if (!latch.await(100, TimeUnit.MILLISECONDS))
			fail("scope was null in testing thread");
	}
	
}
