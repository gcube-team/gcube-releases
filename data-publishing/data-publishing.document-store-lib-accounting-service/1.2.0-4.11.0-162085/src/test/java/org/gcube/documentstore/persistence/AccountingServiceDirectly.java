/**
 * 
 */
package org.gcube.documentstore.persistence;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccountingServiceDirectly extends AccountingServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(AccountingServiceDirectly.class);

	@Before
	public void before() throws Exception {
		PersistenceBackendConfiguration persitenceConfiguration = PersistenceBackendConfiguration
				.getInstance(PersistenceAccountingService.class);
		persistenceBackend = new PersistenceAccountingService();
		persistenceBackend.prepareConnection(persitenceConfiguration);
		
		logger.debug("Persistence Backend is {}", persistenceBackend.getClass().getSimpleName());
	}
	
	

}