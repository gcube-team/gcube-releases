package org.gcube.informationsystem.resourceregistry.er.multicontext;

import java.util.UUID;

import org.gcube.informationsystem.model.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.relation.IsIdentifiedBy;
import org.gcube.informationsystem.resourceregistry.ScopedTest;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.resourcemanagement.model.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.resource.EServiceImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.SoftwareFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.EService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleTest extends ScopedTest {

	private static Logger logger = LoggerFactory
			.getLogger(RuleTest.class);

	public EService createEservice() throws Exception {
		EService eService = new EServiceImpl();
		
		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		IsIdentifiedBy<EService, Facet> isIdentifiedBy = new IsIdentifiedByImpl<EService, Facet>(
				eService, softwareFacet, null);
		eService.addFacet(isIdentifiedBy);
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(EService.NAME);
		resourceManagement.setJSON(ISMapper.marshal(eService));

		String json = resourceManagement.create();
		logger.debug("Created : {}", json);
		eService = ISMapper.unmarshal(EService.class, json);
		logger.debug("Unmarshalled {} {}", EService.NAME, eService);
		
		return eService;
	}
	
	public UUID checkEServiceCreation(EService eService) throws Exception{
		UUID eServiceUUID = eService.getHeader().getUUID();
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eServiceUUID);
		String json = resourceManagement.read();
		EService readEService = ISMapper.unmarshal(EService.class, json);
		Assert.assertTrue(readEService.getHeader().getUUID().compareTo(eServiceUUID) == 0);
		
		return eServiceUUID;
	}
	
	
	public UUID checkFacetCreation(EService eService) throws Exception {
		UUID softwareFacetUUID = eService.getIdentificationFacets().get(0).getHeader().getUUID();
		
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setUUID(softwareFacetUUID);
		String json = facetManagement.read();
		SoftwareFacet readSoftwareFacet = ISMapper.unmarshal(SoftwareFacet.class, json);
		Assert.assertTrue(readSoftwareFacet.getHeader().getUUID().compareTo(softwareFacetUUID) == 0);
		
		return softwareFacetUUID;
	}
	
	@Test
	public void test() throws Exception {
		EService eService = createEservice();
		
		@SuppressWarnings("unused")
		UUID eServiceUUID = checkEServiceCreation(eService);
		@SuppressWarnings("unused")
		UUID softwareFacetUUID = checkFacetCreation(eService);
		
		SoftwareFacet softwareFacet = (SoftwareFacet) eService.getIdentificationFacets().get(0);
		String softwareFacetString = ISMapper.marshal(softwareFacet);
		
		// Trying to recreate SoftwareFacet
		FacetManagement softwareFacetManagement = new FacetManagement();
		softwareFacetManagement.setElementType(SoftwareFacet.NAME);
		softwareFacetManagement.setJSON(softwareFacetString);
		try{
			softwareFacetManagement.create();
		}catch (FacetAlreadyPresentException e) {
			// OK
			logger.debug("As expected {} cannot be recreated", softwareFacetString, e);
		}catch (Exception e) {
			throw e;
		}
		
		
		// Trying to recreate EService
		String eServiceString = ISMapper.marshal(eService);
		
		ResourceManagement eServiceManagement = new ResourceManagement();
		eServiceManagement.setElementType(EService.NAME);
		eServiceManagement.setJSON(eServiceString);
		try{
			eServiceManagement.create();
		}catch (ResourceAlreadyPresentException e) {
			// OK
			logger.debug("As expected {} cannot be recreated", eServiceString, e);
		}catch (Exception e) {
			throw e;
		}
		
		
		// TODO continue with checks
		
		
		
		
		eServiceManagement = new ResourceManagement();
		eServiceManagement.setElementType(EService.NAME);
		eServiceManagement.setJSON(eServiceString);
		boolean deleted = eServiceManagement.delete();
		Assert.assertTrue(deleted);
	}
	
}
