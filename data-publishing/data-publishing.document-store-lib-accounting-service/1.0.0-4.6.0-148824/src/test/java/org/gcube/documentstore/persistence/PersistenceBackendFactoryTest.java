/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 *
 */
public class PersistenceBackendFactoryTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceBackendFactoryTest.class);
	
	@Test
	public void parsingTest() throws Exception {
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		
		PersistenceBackend persistenceBackend = PersistenceBackendFactory.getPersistenceBackend("/gcube/devNext/NextNext");
		logger.debug("{}", persistenceBackend);
		
		
		PersistenceBackendFactory.discoverPersistenceBackend("/gcube/devNext/NextNext", (FallbackPersistenceBackend) persistenceBackend);
		
		
		
		PersistenceBackendFactory.flushAll(100, TimeUnit.MILLISECONDS);
		PersistenceBackendFactory.flush(null, 100, TimeUnit.MILLISECONDS);
		persistenceBackend.flush(100, TimeUnit.MILLISECONDS);
	}
}
