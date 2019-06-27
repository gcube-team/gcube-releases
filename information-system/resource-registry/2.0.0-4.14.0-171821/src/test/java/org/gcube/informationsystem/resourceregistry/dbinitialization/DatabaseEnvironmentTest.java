package org.gcube.informationsystem.resourceregistry.dbinitialization;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseEnvironmentTest {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseEnvironmentTest.class);
	
	@Test
	public void createDB() throws Exception{
		String db = DatabaseEnvironment.DB_URI;
		logger.trace("Created DB is {}", db);
	}
}
