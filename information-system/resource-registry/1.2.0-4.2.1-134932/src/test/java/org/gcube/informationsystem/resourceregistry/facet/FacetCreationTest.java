/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.facet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.impl.entity.facet.AccessPointFacetImpl;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.entity.facet.AccessPointFacet;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.resources.impl.EntityManagementImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class FacetCreationTest {

	private static Logger logger = LoggerFactory
			.getLogger(FacetCreationTest.class);

	protected EntityManagementImpl entityManagementImpl;

	public FacetCreationTest() {
		entityManagementImpl = new EntityManagementImpl();
	}
	
	@Test
	public void createAccessPointFacet() throws URISyntaxException, ResourceRegistryException, IOException{
		ScopeProvider.instance.set("/gcube/devNext");
		
		AccessPointFacet accessPointFacet = new AccessPointFacetImpl();
		accessPointFacet.setEndpoint(new URI("http://localhost"));
		accessPointFacet.setEntryName("port1");
		
		String json = entityManagementImpl.createFacet(AccessPointFacet.NAME, Entities.marshal(accessPointFacet));
		logger.debug("Created : {}", json);
		accessPointFacet = Entities.unmarshal(AccessPointFacet.class, json);
		logger.debug("Unmarshalled {} {}", AccessPointFacet.NAME, accessPointFacet);
		
	}
}
