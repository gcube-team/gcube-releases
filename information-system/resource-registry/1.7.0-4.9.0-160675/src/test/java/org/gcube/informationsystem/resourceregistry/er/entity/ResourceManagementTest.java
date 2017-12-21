package org.gcube.informationsystem.resourceregistry.er.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.gcube.informationsystem.impl.entity.resource.EServiceImpl;
import org.gcube.informationsystem.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.resourceregistry.ScopedTest;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceManagementTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(ResourceManagementTest.class);
	
	public static void checkAssertion(EService eService, UUID eServiceUUID, boolean create) {
		FacetManagementTest.checkHeader(eService, eServiceUUID, create);
		
		SoftwareFacet softwareFacet = eService.getFacets(SoftwareFacet.class).get(0);
		FacetManagementTest.checkAssertion(softwareFacet, null, create);
	}
	
	
	@Test
	public void createUpdateReadDelete() throws Exception {
		
		/* Creating EService*/
		EService eService = new EServiceImpl();

		
		SoftwareFacet softwareFacet = FacetManagementTest.getSoftwareFacet();
		IsIdentifiedBy<EService, Facet> isIdentifiedBy = new IsIdentifiedByImpl<EService, Facet>(
				eService, softwareFacet, null);
		eService.addFacet(isIdentifiedBy);
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(EService.NAME);
		resourceManagement.setJSON(ISMapper.marshal(eService));
		String json = resourceManagement.create();
		logger.trace("Created {}", json);
		eService = ISMapper.unmarshal(EService.class, json);
		
		checkAssertion(eService, null, true);
		FacetManagementTest.checkSoftwareFacetAssertion(softwareFacet, FacetManagementTest.VERSION);
		
		UUID uuid = eService.getHeader().getUUID();
		softwareFacet = eService.getFacets(SoftwareFacet.class).get(0); 
		
		
		/* Updating a Facet of the EService via EServcie update */
		softwareFacet.setVersion(FacetManagementTest.NEW_VERSION);
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eService.getHeader().getUUID());
		resourceManagement.setJSON(ISMapper.marshal(eService));
		
		json = resourceManagement.update();
		logger.trace("Updated {}", json);
		eService = ISMapper.unmarshal(EService.class, json);
		
		checkAssertion(eService, uuid, false);
		softwareFacet = eService.getFacets(SoftwareFacet.class).get(0);
		FacetManagementTest.checkSoftwareFacetAssertion(softwareFacet, FacetManagementTest.NEW_VERSION);
		
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eService.getHeader().getUUID());
		
		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
	}
	
	
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
		ERManagement erManagement = ERManagementUtility.getERManagement(type);

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
