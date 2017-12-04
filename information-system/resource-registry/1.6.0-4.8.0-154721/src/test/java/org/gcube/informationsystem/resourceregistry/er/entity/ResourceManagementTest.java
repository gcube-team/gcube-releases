package org.gcube.informationsystem.resourceregistry.er.entity;

import java.util.HashMap;
import java.util.Map;

import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.resourceregistry.ScopedTest;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceManagementTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(ResourceManagementTest.class);
	
	@Test
	public void testAllWithCostraint() throws ResourceRegistryException {
		String relationType = ConsistsOf.NAME;
		String facetType = SoftwareFacet.NAME;
		
		Map<String, String> constraint = new HashMap<>();
		constraint.put(AccessPath.RELATION_TYPE_PATH_PART, relationType);
		constraint.put(AccessPath.FACET_TYPE_PATH_PART, facetType);

		
		constraint.put(SoftwareFacet.GROUP_PROPERTY, "Gis");
		constraint.put(SoftwareFacet.NAME_PROPERTY, "Thredds");
		
		String type = Service.NAME;
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagement.getERManagement(type);

		if (erManagement instanceof ResourceManagement) {
			String ret = ((ResourceManagement) erManagement).all(false, constraint);
			logger.debug(ret);
			
			constraint.put(AccessPath.RELATION_TYPE_PATH_PART, relationType);
			constraint.put(AccessPath.FACET_TYPE_PATH_PART, facetType);
			ret = ((ResourceManagement) erManagement).all(true, constraint);
			logger.debug(ret);
		}
		
		
	}

}
