/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class QueryImplTest {

	private static Logger logger = LoggerFactory.getLogger(QueryImplTest.class);
	
	@Test
	public void testQuery() throws InvalidQueryException{
		ScopeProvider.instance.set("/gcube/devNext");
		QueryImpl queryImpl = new QueryImpl();
		
		String query = "select * from CPUFacet";
		String ret = queryImpl.query(query, -7, null);
		
		logger.debug(ret);
	}
	
}
