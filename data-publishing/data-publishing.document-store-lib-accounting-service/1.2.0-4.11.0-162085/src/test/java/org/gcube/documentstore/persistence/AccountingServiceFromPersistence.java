/**
 * 
 */
package org.gcube.documentstore.persistence;

import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccountingServiceFromPersistence extends AccountingServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(AccountingServiceFromPersistence.class);

	@Before
	public void before() {
		String context = ScopedTest.getCurrentContext();
		persistenceBackend = PersistenceBackendFactory.getPersistenceBackend(context);
		if(persistenceBackend instanceof FallbackPersistenceBackend) {
			persistenceBackend = PersistenceBackendFactory.discoverPersistenceBackend(context,
				(FallbackPersistenceBackend) persistenceBackend);
		}
		
		Assert.assertTrue(persistenceBackend instanceof PersistenceAccountingService);
		logger.debug("Persistence Backend is {}", persistenceBackend.getClass().getSimpleName());
		
	}
	
}