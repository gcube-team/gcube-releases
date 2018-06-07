package org.gcube.informationsystem.resourceregistry.query;

import org.gcube.informationsystem.resourceregistry.ScopedTest;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(QueryTest.class);
	
	//@Test
	public void testGremlinQuery() throws InvalidQueryException {
		Query query = new QueryImpl();
		String ret = query.gremlinQuery("g.V()");
		logger.debug("Gremlin Query Result is : {}", ret);
	}
	
}
