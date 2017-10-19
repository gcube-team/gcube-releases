/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.util.concurrent.TimeUnit;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.testutility.ScopedTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class PersistenceCouchBaseTest extends ScopedTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceCouchBaseTest.class);
	
	public static final long timeout = 5000;
	public static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;
	
	
	@Test
	public void persistenceIsCouchBase() throws ObjectNotFound, Exception {
		logger.debug("Going to check if the Persistence is CouchBase");
		PersistenceBackendFactory.setFallbackLocation(null);
		FallbackPersistenceBackend fallbackPersistenceBackend = PersistenceBackendFactory.createFallback(ScopedTest.getCurrentContext());
		
		PersistenceBackend persistenceBackend = PersistenceBackendFactory.rediscoverPersistenceBackend(fallbackPersistenceBackend, ScopedTest.getCurrentContext());
		
		Assert.assertTrue(persistenceBackend instanceof PersistenceCouchBase);
		
	}
	
}
