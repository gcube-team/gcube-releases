package org.gcube.informationsystem.resourceregistry.dbinitialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.config.OStorageConfiguration;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class DatabaseIntializatorTest {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseIntializatorTest.class);
	
	//@Test
	public void testInitDB() throws Exception{
		
		DatabaseIntializator.initGraphDB();
		
		OrientGraphFactory factory = new OrientGraphFactory(
				DatabaseEnvironment.DB_URI,
				DatabaseEnvironment.CHANGED_ADMIN_USERNAME,
				DatabaseEnvironment.CHANGED_ADMIN_PASSWORD)
				.setupPool(1, 10);

		OrientGraphNoTx orientGraphNoTx = factory.getNoTx();

		/* Updating Datetime Format to be aligned with IS model definition */
		OStorageConfiguration configuration = orientGraphNoTx.getRawGraph().getStorage().getConfiguration();
		logger.debug("Got DateTimeFormat {}", configuration.getDateTimeFormat());
		
	}
}
