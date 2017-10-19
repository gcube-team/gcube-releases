/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.access;

import org.gcube.informationsystem.resourceregistry.ScopedTest;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.query.QueryImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class QueryImplTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(QueryImplTest.class);
	
	@Test
	public void testQuery() throws InvalidQueryException{
		QueryImpl queryImpl = new QueryImpl();
		
		String query = "select * from CPUFacet";
		String ret = queryImpl.query(query, -7, null);
		
		logger.debug(ret);
	}
	
}
