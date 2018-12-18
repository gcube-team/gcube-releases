package org.gcube.informationsystem.resourceregistry.er.entity;

import java.util.UUID;

import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.ER;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.resourceregistry.ScopedTest;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.utils.Utility;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.er.ERManagementTest;
import org.gcube.informationsystem.resourceregistry.utils.HeaderUtility;
import org.gcube.resourcemanagement.model.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.SoftwareFacet;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class FacetManagementTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(ERManagementTest.class);
	
	public static final String GROUP = "InformationSystem";
	public static final String NAME = "resource-registry";
	public static final String VERSION = "1.0.0";
	public static final String NEW_VERSION = "2.0.0";
	
	public static SoftwareFacet getSoftwareFacet() {
		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup(GROUP);
		softwareFacet.setName(NAME);
		softwareFacet.setVersion(VERSION);
		return softwareFacet;
	}
	
	public static void checkSoftwareFacetAssertion(SoftwareFacet softwareFacet, String version) {
		Assert.assertTrue(softwareFacet.getGroup().compareTo(GROUP) == 0);
		Assert.assertTrue(softwareFacet.getName().compareTo(NAME) == 0);
		Assert.assertTrue(softwareFacet.getVersion().compareTo(version) == 0);
	}
	
	public static void checkHeader(ER er, UUID uuid, boolean create) {
		Assert.assertTrue(er.getHeader() != null);
		Assert.assertTrue(er.getHeader().getUUID() != null);
		
		if(uuid != null) {
			Assert.assertTrue(er.getHeader().getUUID().compareTo(uuid) == 0);
		}
		
		String user = HeaderUtility.getUser();
		Assert.assertTrue(er.getHeader().getModifiedBy().compareTo(user) == 0);
		
		if(create) {
			Assert.assertTrue(er.getHeader().getCreator().compareTo(user) == 0);
			Assert.assertTrue(er.getHeader().getCreationTime().compareTo(er.getHeader().getLastUpdateTime()) == 0);
		} else {
			Assert.assertTrue(er.getHeader().getCreationTime().before(er.getHeader().getLastUpdateTime()));
		}
	}
	
	public static void checkAssertion(Facet facet, UUID uuid, boolean create) {
		checkHeader(facet, uuid, create);
	}
	
	protected <F extends Facet> F create(F facet) throws Exception {
		FacetManagement facetManagement = new FacetManagement();
		String facetType = Utility.getType(facet);
		facetManagement.setElementType(facetType);
		facetManagement.setJSON(ISMapper.marshal(facet));
		
		String json = facetManagement.create();
		logger.debug("Created : {}", json);
		@SuppressWarnings("unchecked")
		F createdFacet = (F) ISMapper.unmarshal(facet.getClass(), json);
		logger.debug("Unmarshalled {}", createdFacet);
		
		UUID uuid = null;
		if(facet.getHeader() != null) {
			uuid = facet.getHeader().getUUID();
		}
		checkAssertion(createdFacet, uuid, true);
		return createdFacet;
	}
	
	protected <F extends Facet> F update(F facet) throws Exception {
		FacetManagement facetManagement = new FacetManagement();
		String facetType = Utility.getType(facet);
		facetManagement.setElementType(facetType);
		facetManagement.setJSON(ISMapper.marshal(facet));
		
		String json = facetManagement.update();
		logger.debug("Updated : {}", json);
		@SuppressWarnings("unchecked")
		F updatedFacet = (F) ISMapper.unmarshal(facet.getClass(), json);
		logger.debug("Unmarshalled {}", updatedFacet);
		
		UUID uuid = facet.getHeader().getUUID();
		checkAssertion(updatedFacet, uuid, false);
		
		return updatedFacet;
	}
	
	protected <F extends Facet> F read(F facet) throws Exception {
		UUID uuid = facet.getHeader().getUUID();
		
		FacetManagement facetManagement = new FacetManagement();
		String facetType = Utility.getType(facet);
		facetManagement.setElementType(facetType);
		facetManagement.setUUID(uuid);
		
		String json = facetManagement.read();
		logger.debug("Read : {}", json);
		@SuppressWarnings("unchecked")
		F readFacet = (F) ISMapper.unmarshal(facet.getClass(), json);
		logger.debug("Unmarshalled {}", readFacet);
		
		checkAssertion(readFacet, uuid, false);
		
		return readFacet;
	}
	
	protected <F extends Facet> boolean delete(F facet) throws Exception {
		FacetManagement facetManagement = new FacetManagement();
		String facetType = Utility.getType(facet);
		facetManagement.setElementType(facetType);
		facetManagement.setUUID(facet.getHeader().getUUID());
		
		boolean deleted = facetManagement.delete();
		Assert.assertTrue(deleted);
		
		try {
			read(facet);
		} catch(FacetNotFoundException e) {
			logger.info("Facet not found as expected");
		}
		
		return deleted;
	}
	
	protected <F extends Facet> boolean addToContext(F facet) throws Exception {
		FacetManagement facetManagement = new FacetManagement();
		String facetType = Utility.getType(facet);
		facetManagement.setElementType(facetType);
		facetManagement.setUUID(facet.getHeader().getUUID());
		
		boolean added = facetManagement.addToContext(ContextUtility.getCurrentSecurityContext().getUUID());
		Assert.assertTrue(added);
		
		return added;
	}
	
	protected <F extends Facet> boolean removeFromContext(F facet) throws Exception {
		FacetManagement facetManagement = new FacetManagement();
		String facetType = Utility.getType(facet);
		facetManagement.setElementType(facetType);
		facetManagement.setUUID(facet.getHeader().getUUID());
		
		boolean added = facetManagement.removeFromContext(ContextUtility.getCurrentSecurityContext().getUUID());
		Assert.assertTrue(added);
		
		return added;
	}
	
	interface ActionFunction<F extends Facet> {
		void call(F facet) throws Exception;
	}
	
	protected <F extends Facet, C extends Exception, E extends Exception> void assertThrow(F facet, Class<C> c,
			ActionFunction<F> action) throws Exception {
		try {
			action.call(facet);
			throw new RuntimeException("Expected " + c.getName());
		} catch(Exception e) {
			if(c.isAssignableFrom(e.getClass())) {
				logger.debug("As expected {} has been thrown", c.getName());
				return;
			}
			throw e;
		}
	}
	
	@Test
	public void createUpdateReadDelete() throws Exception {
		SoftwareFacet softwareFacet = getSoftwareFacet();
		
		/* Testing Create */
		softwareFacet = create(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, VERSION);
		
		/* Testing Update */
		softwareFacet.setVersion(NEW_VERSION);
		softwareFacet = update(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, NEW_VERSION);
		
		/* Testing Read */
		softwareFacet = read(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, NEW_VERSION);
		
		assertThrow(softwareFacet, FacetAlreadyPresentException.class, (SoftwareFacet s) -> {
			create(s);
		});
		
		/* Testing Delete */
		delete(softwareFacet);
		
		/* Testing new Create to check creation with provided UUID */
		softwareFacet = create(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, NEW_VERSION);
		
		delete(softwareFacet);
		
	}
	
	@Test
	public void testHierarchy() throws Exception {
		/* Setting scope /gcube/devNext/NextNext */
		ScopedTest.setContext(GCUBE_DEVNEXT_NEXTNEXT);
		
		SoftwareFacet softwareFacet = getSoftwareFacet();
		
		/* Testing Create */
		softwareFacet = create(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, VERSION);
		
		softwareFacet = update(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, VERSION);
		
		/* Testing Read */
		softwareFacet = read(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, VERSION);
		
		/* Setting (parent) scope /gcube/devNext */
		ScopedTest.setContext(GCUBE_DEVNEXT);
		
		assertThrow(softwareFacet, FacetAvailableInAnotherContextException.class, (SoftwareFacet s) -> {
			read(s);
		});
		
		/* Entering hierarchic mode */
		ContextUtility.getHierarchicMode().set(true);
		
		softwareFacet = read(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, VERSION);
		
		/* Setting (parent of parent) scope /gcube */
		ScopedTest.setContext(GCUBE);
		
		softwareFacet = read(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, VERSION);
		
		/* Leaving hierarchic mode */
		ContextUtility.getHierarchicMode().set(false);
		
		assertThrow(softwareFacet, FacetAvailableInAnotherContextException.class, (SoftwareFacet s) -> {
			read(s);
		});
		
		/* Adding to /gcube. The context are now /gcube and /gcube/devNext/NextNext */
		addToContext(softwareFacet);
		softwareFacet = read(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, VERSION);
		
		softwareFacet.setVersion(NEW_VERSION);
		softwareFacet = update(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, NEW_VERSION);
		
		/* Restoring scope /gcube/devNext/NextNext */
		ScopedTest.setContext(GCUBE_DEVNEXT_NEXTNEXT);
		read(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, NEW_VERSION);
		
		/* Removing from /gcube/devNext/NextNext. The context is now /gcube */
		removeFromContext(softwareFacet);
		
		assertThrow(softwareFacet, FacetAvailableInAnotherContextException.class, (SoftwareFacet s) -> {
			read(s);
		});
		
		/* Setting (parent) scope /gcube/devNext */
		ScopedTest.setContext(GCUBE_DEVNEXT);
		assertThrow(softwareFacet, FacetAvailableInAnotherContextException.class, (SoftwareFacet s) -> {
			read(s);
		});
		
		/* Entering hierarchic mode */
		ContextUtility.getHierarchicMode().set(true);
		
		assertThrow(softwareFacet, FacetAvailableInAnotherContextException.class, (SoftwareFacet s) -> {
			read(s);
		});
		
		/* Setting (parent of parent) scope /gcube */
		ScopedTest.setContext(GCUBE);
		// The facet must be readable in hierarchic mode in /gcube because the context
		// has been explicitly added
		read(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, NEW_VERSION);
		
		/* Leaving hierarchic mode */
		ContextUtility.getHierarchicMode().set(false);
		
		read(softwareFacet);
		checkSoftwareFacetAssertion(softwareFacet, NEW_VERSION);
		
		delete(softwareFacet);
	}
	
}
