package org.gcube.informationsystem.resourceregistry.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.informationsystem.model.impl.entity.ContextImpl;
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.ER;
import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ResourceRegistryContextClientTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(ResourceRegistryContextClientTest.class);
	
	protected ResourceRegistryContextClient resourceRegistryContextClient;
	
	public ResourceRegistryContextClientTest() {
		resourceRegistryContextClient = ResourceRegistryContextClientFactory.create();
	}
	
	public static final String CTX_NAME_A = "A";
	public static final String CTX_NAME_B = "B";
	public static final String CTX_NAME_C = "C";
	
	public static String getUser() {
		String user = org.gcube.informationsystem.model.reference.embedded.Header.UNKNOWN_USER;
		try {
			String token = SecurityTokenProvider.instance.get();
			if(token != null) {
				AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
				if(authorizationEntry != null) {
					ClientInfo clientInfo = authorizationEntry.getClientInfo();
					String clientId = clientInfo.getId();
					if(clientId != null && clientId.compareTo("") != 0) {
						user = clientId;
					} else {
						throw new Exception("Username null or empty");
					}
				}
			}
		} catch(Exception e) {
			logger.error("Unable to retrieve user. {} will be used", user);
		}
		return user;
	}
	
	public static void checkHeader(ER er, UUID uuid, boolean create) {
		Assert.assertTrue(er.getHeader() != null);
		Assert.assertTrue(er.getHeader().getUUID() != null);
		
		if(uuid != null) {
			Assert.assertTrue(er.getHeader().getUUID().compareTo(uuid) == 0);
		}
		
		String user = getUser();
		Assert.assertTrue(er.getHeader().getModifiedBy().compareTo(user) == 0);
		
		if(create) {
			Assert.assertTrue(er.getHeader().getCreator().compareTo(user) == 0);
			Assert.assertTrue(er.getHeader().getCreationTime().compareTo(er.getHeader().getLastUpdateTime()) == 0);
		} else {
			Assert.assertTrue(er.getHeader().getCreationTime().before(er.getHeader().getLastUpdateTime()));
		}
		
	}
	
	protected void assertions(Context pre, Context post, boolean checkParent, boolean create) {
		if(checkParent) {
			if(pre.getHeader() != null) {
				checkHeader(post, pre.getHeader().getUUID(), create);
			} else {
				checkHeader(post, null, create);
			}
		}
		
		Assert.assertTrue(pre.getName().compareTo(post.getName()) == 0);
		if(checkParent && pre.getParent() != null && post.getParent() != null) {
			Context preParent = pre.getParent().getSource();
			Context postParent = post.getParent().getSource();
			assertions(preParent, postParent, false, false);
		}
	}
	
	protected Context read(UUID uuid) throws ResourceRegistryException, IOException {
		Context c = resourceRegistryContextClient.read(uuid);
		Assert.assertTrue(c.getHeader() != null);
		Assert.assertTrue(c.getHeader().getUUID() != null);
		Assert.assertTrue(c.getHeader().getUUID().compareTo(uuid) == 0);
		return c;
	}
	
	protected Context create(Context context) throws ResourceRegistryException, IOException {
		Context c = resourceRegistryContextClient.create(context);
		assertions(context, c, true, true);
		return c;
	}
	
	protected Context update(Context context) throws ResourceRegistryException, IOException {
		Context c = resourceRegistryContextClient.update(context);
		assertions(context, c, true, false);
		return c;
	}
	
	protected boolean delete(UUID uuid) throws ResourceRegistryException {
		boolean deleted = resourceRegistryContextClient.delete(uuid);
		Assert.assertTrue(deleted);
		logger.debug("Deleted {} with UUID {}", Context.NAME, uuid);
		return deleted;
	}
	
	protected boolean delete(Context context) throws ResourceRegistryException {
		return delete(context.getHeader().getUUID());
	}
	
	protected void invalidCreate(Context context) throws ResourceRegistryException, IOException {
		try {
			Context c = create(context);
			throw new RuntimeException(ISMapper.marshal(c) + " was created successfully. This is not what we expected");
		} catch(ContextAlreadyPresentException e) {
			logger.debug("As expected {} cannot be created.", ISMapper.marshal(context));
		}
	}
	
	protected void invalidUpdate(Context context) throws ResourceRegistryException, IOException {
		try {
			Context c = update(context);
			throw new RuntimeException(ISMapper.marshal(c) + " was updated successfully. This is not what we expected");
		} catch(ContextAlreadyPresentException e) {
			logger.debug("As expected {} cannot be updated.", ISMapper.marshal(context));
		}
	}
	
	protected void invalidDelete(Context context) throws ResourceRegistryException, JsonProcessingException {
		String contextString = ISMapper.marshal(context);
		try {
			delete(context);
			throw new RuntimeException(contextString + " was deleted successfully. This is not what we expected");
		} catch(ContextException e) {
			logger.debug("As expected {} cannot be deleted.", contextString);
		}
	}
	
	// @Test
	public void readTest() throws Exception {
		UUID uuid = UUID.fromString("4828d488-285b-4383-af4b-4d72069ad11b");
		Context gcube = read(uuid);
		logger.debug(ISMapper.marshal(gcube));
	}
	
	@Test
	public void completeTest() throws Exception {
		Context contextA1 = new ContextImpl(CTX_NAME_A);
		contextA1 = create(contextA1);
		// ________A1________
		
		Context contextA2 = new ContextImpl(CTX_NAME_A);
		contextA2.setParent(contextA1);
		contextA2 = create(contextA2);
		// ________A1________
		// ___A2
		
		Context contextB3 = new ContextImpl(CTX_NAME_B);
		contextB3.setParent(contextA2);
		contextB3 = create(contextB3);
		// ________A1________
		// ___A2
		// B3
		
		Context contextB4 = new ContextImpl(CTX_NAME_B);
		contextB4.setParent(contextA1);
		contextB4 = create(contextB4);
		// ________A1________
		// ___A2_______B4____
		// B3
		
		Context contextA5 = new ContextImpl(CTX_NAME_A);
		contextA5.setParent(contextB4);
		contextA5 = create(contextA5);
		// ________A1________
		// ___A2_______B4____
		// B3______________A5
		
		Context invalidContextA1 = new ContextImpl(CTX_NAME_A);
		invalidCreate(invalidContextA1);
		
		Context invalidContextA2 = new ContextImpl(CTX_NAME_A);
		invalidContextA2.setParent(contextA1);
		invalidCreate(invalidContextA2);
		
		Context invalidContextB3 = new ContextImpl(CTX_NAME_B);
		invalidContextB3.setParent(contextA2);
		invalidCreate(invalidContextB3);
		
		Context invalidContextB4 = new ContextImpl(CTX_NAME_B);
		invalidContextB4.setParent(contextA1);
		invalidCreate(invalidContextB4);
		
		Context invalidContextA5 = new ContextImpl(CTX_NAME_A);
		invalidContextA5.setParent(contextB4);
		invalidCreate(invalidContextA5); // Trying to recreate A5. Fails
		
		// Trying to move A5 as child of A1. It fails due to A2.
		Context nullContext = null;
		contextA5.setParent(nullContext);
		invalidUpdate(contextA5);
		contextA5.setParent(contextB4);
		// ________A1________
		// ___A2_______B4____
		// B3______________A5
		
		nullContext = null;
		contextB4.setParent(nullContext);
		update(contextB4);
		// _____A1____B4_____
		// __A2__________A5__
		// B3
		
		contextB4.setParent(contextA1);
		update(contextB4);
		// ________A1________
		// ___A2_______B4____
		// B3______________A5
		
		// Trying to rename with the new name A. It fails due to A5.
		contextB3.setName(CTX_NAME_A);
		update(contextB3);
		// ________A1________
		// ___A2_______B4____
		// A3______________A5
		
		// After Restoring name B, trying to move B3 as child of A1. It fails due to B4.
		contextB3.setName(CTX_NAME_B);
		contextB3.setParent(contextA1);
		invalidUpdate(contextB3);
		// ________A1________
		// ___A2_______B4____
		// A3______________A5
		
		// Restoring A3 (was B3) as B3 and with parent A2.OK.
		contextB3.setName(CTX_NAME_B);
		contextB3.setParent(contextA2);
		update(contextB3);
		// ________A1________
		// ___A2_______B4____
		// B3______________A5
		
		// This update should not has eny effects except updating the lastUpdateTime.
		contextB3.setName(CTX_NAME_B);
		contextB3.setParent(contextA2);
		update(contextB3);
		
		// Trying to move A5 as child of A1. It fails due to A2.
		contextA5.setParent(contextA1);
		invalidUpdate(contextA5);
		// Restoring A5
		contextA5.setParent(contextB4);
		// ________A1________
		// ___A2_______B4____
		// B3______________A5
		
		// Moving B3 as child of B4. OK.
		contextB3.setParent(contextB4);
		update(contextB3);
		// ________A1________
		// ___A2_______B4____
		// ________B3______A5
		
		// Restoring the initial situation by moving B3 as child of A2. OK.
		contextB3.setParent(contextA2);
		update(contextB3);
		// ________A1________
		// ___A2_______B4____
		// B3______________A5
		
		// Renaming B3 as C3. OK.
		contextB3.setName(CTX_NAME_C);
		update(contextB3);
		// ________A1________
		// ___A2_______B4____
		// C3______________A5
		
		// Moving C3 (was B3) as child of A1. Now it is possible. OK.
		contextB3.setParent(contextA1);
		update(contextB3);
		// ________A1________
		// ___A2___C3___B4___
		// ________________A5
		
		// Trying to rename C3 (was B3) newly to B3. Fails due to B4.
		contextB3.setName(CTX_NAME_B);
		invalidUpdate(contextB3);
		// ________A1________
		// ___A2___C3___B4___
		// ________________A5
		
		// Moving back C3 (was B3) as child of A2. OK.
		contextB3.setParent(contextA2);
		update(contextB3);
		// ________A1________
		// ___A2_______B4____
		// C3______________A5
		
		// Renaming C3 (was B3) to B3. OK.
		contextB3.setName(CTX_NAME_B);
		update(contextB3);
		// ________A1________
		// ___A2_______B4____
		// B3______________A5
		
		// The following delete are not allowed because they are not child contexts
		invalidDelete(contextA1);
		invalidDelete(contextA2);
		invalidDelete(contextB4);
		
		delete(contextA5);
		// ________A1________
		// ___A2_______B4____
		// B3
		
		try {
			delete(contextA5);
		} catch(ContextNotFoundException e) {
			logger.debug("The context with uuid {} was not found. (Was already deleted)",
					contextA5.getHeader().getUUID());
		}
		
		delete(contextB3);
		// ________A1________
		// ___A2_______B4____
		
		delete(contextB4);
		// ________A1________
		// ___A2
		
		delete(contextA2);
		// ________A1________
		
		delete(contextA1);
		logger.debug("The DB should be now clean");
	}
	
	@Test
	public void testGetAll() throws Exception {
		List<Context> all = resourceRegistryContextClient.all();
		for(Context c : all) {
			logger.debug("{}", ISMapper.marshal(c));
		}
	}
	
	@Test
	public void createDissertationContext() throws Exception {
		Context d4science = new ContextImpl("d4science");
		d4science = create(d4science);
		
		Context soBigData = new ContextImpl("SoBigData");
		soBigData.setParent(d4science);
		soBigData = create(soBigData);
		
		Context tagMe = new ContextImpl("TagMe");
		tagMe.setParent(soBigData);
		tagMe = create(tagMe);
		
		Context blueBRIDGE = new ContextImpl("BlueBRIDGE");
		blueBRIDGE.setParent(d4science);
		blueBRIDGE = create(blueBRIDGE);
		
		Context biodiversityLab = new ContextImpl("Biodiversity Lab");
		biodiversityLab.setParent(blueBRIDGE);
		biodiversityLab = create(biodiversityLab);
		
	}
	
	// @Test
	public void createDevContext() throws Exception {
		Context gcube = new ContextImpl("gcube");
		gcube = create(gcube);
		
		Context devsec = new ContextImpl("devsec");
		devsec.setParent(gcube);
		devsec = create(devsec);
		
		Context devVRE = new ContextImpl("devVRE");
		devVRE.setParent(devsec);
		devVRE = create(devVRE);
		
		Context devNext = new ContextImpl("devNext");
		devNext.setParent(gcube);
		devNext = create(devNext);
		
		Context nextNext = new ContextImpl("NextNext");
		nextNext.setParent(devNext);
		nextNext = create(nextNext);
		
		Context preprod = new ContextImpl("preprod");
		preprod.setParent(gcube);
		preprod = create(preprod);
		
		Context preVRE = new ContextImpl("preVRE");
		preVRE.setParent(preprod);
		preVRE = create(preVRE);
	}
	
	// @Test
	public void createPARTHENOSContext() throws Exception {
		// /d4science.research-infrastructures.eu/ParthenosVO/PARTHENOS_Registry
		
		Context d4science = new ContextImpl("d4science.research-infrastructures.eu");
		create(d4science);
		
		Context parthenosVO = new ContextImpl("ParthenosVO");
		parthenosVO.setParent(d4science);
		create(parthenosVO);
		
		Context parthenosRegistry = new ContextImpl("PARTHENOS_Registry");
		parthenosRegistry.setParent(parthenosVO);
		create(parthenosRegistry);
		
	}
	
	// @Test
	public void createProductionMissingContext() throws Exception {
		UUID d4ResearchUUID = UUID.fromString("8b926d1c-4460-4d7a-adab-c75ad2770a21");
		UUID farmUUID = UUID.fromString("dbafdb3e-f7f9-4039-ad1c-3432c041f53c");
		
		Map<String,UUID> contexts = new HashMap<>();
		contexts.put("ICES_FIACO2017", d4ResearchUUID);
		contexts.put("D4STeam", farmUUID);
		
		for(String contextName : contexts.keySet()) {
			Context parent = read(contexts.get(contextName));
			Context context = new ContextImpl(contextName);
			context.setParent(parent);
			create(context);
		}
		
	}
	
}
