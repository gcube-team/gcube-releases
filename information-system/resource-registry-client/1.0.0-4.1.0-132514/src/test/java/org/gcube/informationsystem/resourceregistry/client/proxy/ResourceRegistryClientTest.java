/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client.proxy;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.client.plugin.ResourceRegistryClientPlugin;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ResourceRegistryClientTest {

	private static Logger logger = LoggerFactory
			.getLogger(ResourceRegistryClientTest.class);
	
	@Test
	public void testQuery() throws InvalidQueryException{
		
		ScopeProvider.instance.set("/gcube/devNext");
		ResourceRegistryClientPlugin plugin = new ResourceRegistryClientPlugin();
		
		ResourceRegistryClient rrc = new StatelessBuilderImpl<EndpointReference, ResourceRegistryClient>(plugin).build();
		
		String res = rrc.query("SELECT FROM V", null);
		logger.debug(res);
	}
}
