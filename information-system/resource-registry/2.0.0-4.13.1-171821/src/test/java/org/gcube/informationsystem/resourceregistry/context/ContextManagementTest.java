package org.gcube.informationsystem.resourceregistry.context;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.model.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.model.impl.entity.ContextImpl;
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.model.reference.relation.IsParentOf;
import org.gcube.informationsystem.resourceregistry.ScopedTest;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.security.ContextSecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.SecurityType;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagementTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class ContextManagementTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(ContextManagementTest.class);

	@Test
	public void testJava() throws Exception {
		Context gcube = new ContextImpl("gcube");
		logger.debug("gcube : {}", ISMapper.marshal(ISMapper.unmarshal(Context.class, ISMapper.marshal(gcube))));

		Context devsec = new ContextImpl("devsec");
		gcube.addChild(devsec);
		logger.debug("devsec : {}", ISMapper.marshal(ISMapper.unmarshal(Context.class, ISMapper.marshal(devsec))));

		Context devVRE = new ContextImpl("devVRE");
		devsec.addChild(devVRE);
		logger.debug("devVRE : {}", ISMapper.marshal(ISMapper.unmarshal(Context.class, ISMapper.marshal(devVRE))));

		Context devNext = new ContextImpl("devNext");
		gcube.addChild(devNext);
		logger.debug("devNext : {}", ISMapper.marshal(ISMapper.unmarshal(Context.class, ISMapper.marshal(devNext))));

		Context NextNext = new ContextImpl("NextNext");
		devNext.addChild(NextNext);
		logger.debug("NextNext : {}", ISMapper.marshal(ISMapper.unmarshal(Context.class, ISMapper.marshal(NextNext))));

		logger.debug("------------------------------------");

		logger.debug("gcube : {}", ISMapper.marshal(gcube));
		logger.debug("devsec : {}", ISMapper.marshal(devsec));
		logger.debug("devVRE : {}", ISMapper.marshal(devVRE));
		logger.debug("devNext : {}", ISMapper.marshal(devNext));
		logger.debug("NextNext : {}", ISMapper.marshal(NextNext));
	}

	public static final String CTX_NAME_A = "A";
	public static final String CTX_NAME_B = "B";
	public static final String CTX_NAME_C = "C";

	protected void assertions(Context pre, Context post, boolean checkParent, boolean create)
			throws ResourceRegistryException {
		if (checkParent) {
			if (pre.getHeader() != null) {
				FacetManagementTest.checkHeader(post, pre.getHeader().getUUID(), create);
			} else {
				FacetManagementTest.checkHeader(post, null, create);
			}
		}

		Assert.assertTrue(pre.getName().compareTo(post.getName()) == 0);
		if (checkParent && pre.getParent() != null && post.getParent() != null) {
			Context preParent = pre.getParent().getSource();
			Context postParent = post.getParent().getSource();
			assertions(preParent, postParent, false, false);
		}

	}

	protected void roleUserAssertions(UUID uuid, UUID oldParentUUID, boolean deleted) throws ResourceRegistryException {
		ContextSecurityContext contextSecurityContext = new ContextSecurityContext();
		ContextUtility.getInstance().addSecurityContext(contextSecurityContext.getUUID().toString(),
				contextSecurityContext);

		OrientGraph orientGraph = contextSecurityContext.getGraph(PermissionMode.READER);
		ODatabaseDocumentTx oDatabaseDocumentTx = orientGraph.getRawGraph();
		OSecurity oSecurity = oDatabaseDocumentTx.getMetadata().getSecurity();
		
		SecurityContext securityContext = null;
		if(deleted) {
			securityContext = new SecurityContext(uuid);
		} else {
			securityContext = ContextUtility.getInstance().getSecurityContextByUUID(uuid);
		}

		boolean[] booleanArray = new boolean[] { false, true };
		for (boolean hierarchic : booleanArray) {
			for (PermissionMode permissionMode : PermissionMode.values()) {
				String role = securityContext.getSecurityRoleOrUserName(permissionMode, SecurityType.ROLE, hierarchic);
				ORole oRole = oSecurity.getRole(role);
				Assert.assertEquals(oRole == null, deleted);

				String user = securityContext.getSecurityRoleOrUserName(permissionMode, SecurityType.USER, hierarchic);
				OUser oUser = oSecurity.getUser(user);
				Assert.assertEquals(oUser == null, deleted);
				if(oUser!=null) {
					Assert.assertTrue(oUser.hasRole(oRole.getName(), false));
				}
				
				if(hierarchic) {
					SecurityContext parent = null;
					if(deleted){
						if(oldParentUUID!=null) {
							parent = ContextUtility.getInstance().getSecurityContextByUUID(oldParentUUID);
						}
					}
					parent = securityContext.getParentSecurityContext();
					while(parent!=null) {
						String parentUser = parent.getSecurityRoleOrUserName(permissionMode, SecurityType.USER, hierarchic);
						OUser parentOUser = oSecurity.getUser(parentUser);
						Assert.assertTrue(parentOUser != null);
						Assert.assertEquals(parentOUser.hasRole(oRole.getName(), false), !deleted);
						parent = parent.getParentSecurityContext();
					}
				}
				
			}
		}
	}

	protected Context read(UUID uuid) throws ResourceRegistryException, IOException {
		ContextManagement contextManagement = new ContextManagement();
		contextManagement.setUUID(uuid);
		String contextString = contextManagement.read();
		logger.debug("Read {}", contextString);
		roleUserAssertions(uuid, null, false);
		return ISMapper.unmarshal(Context.class, contextString);
	}

	protected Context create(Context context) throws ResourceRegistryException, IOException {
		ContextManagement contextManagement = new ContextManagement();
		contextManagement.setJSON(ISMapper.marshal(context));
		String contextString = contextManagement.create();
		logger.debug("Created {}", contextString);
		Context c = ISMapper.unmarshal(Context.class, contextString);
		assertions(context, c, true, true);
		roleUserAssertions(c.getHeader().getUUID(), null, false);
		return c;
	}

	protected Context update(Context context) throws ResourceRegistryException, IOException {
		ContextManagement contextManagement = new ContextManagement();
		contextManagement.setJSON(ISMapper.marshal(context));
		String contextString = contextManagement.update();
		logger.debug("Updated {}", contextString);
		Context c = ISMapper.unmarshal(Context.class, contextString);
		assertions(context, c, true, false);
		roleUserAssertions(c.getHeader().getUUID(), null, false);
		return c;
	}

	protected boolean delete(UUID uuid) throws ResourceRegistryException {
		ContextManagement contextManagement = new ContextManagement();
		contextManagement.setUUID(uuid);
		
		SecurityContext securityContext = ContextUtility.getInstance().getSecurityContextByUUID(uuid);
		
		UUID oldParentUUID = null;
		if(securityContext.getParentSecurityContext()!=null) {
			oldParentUUID = securityContext.getParentSecurityContext().getUUID();
		}
		
		boolean deleted = contextManagement.delete();
		Assert.assertTrue(deleted);
		roleUserAssertions(uuid, oldParentUUID, true);
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
		} catch (ContextAlreadyPresentException e) {
			logger.debug("As expected {} cannot be created.", ISMapper.marshal(context));
		}
	}

	protected void invalidUpdate(Context context) throws ResourceRegistryException, IOException {
		try {
			Context c = update(context);
			throw new RuntimeException(ISMapper.marshal(c) + " was updated successfully. This is not what we expected");
		} catch (ContextAlreadyPresentException e) {
			logger.debug("As expected {} cannot be updated.", ISMapper.marshal(context));
		}
	}

	protected void invalidDelete(Context context) throws ResourceRegistryException, JsonProcessingException {
		String contextString = ISMapper.marshal(context);
		try {
			delete(context);
			throw new RuntimeException(contextString + " was deleted successfully. This is not what we expected");
		} catch (ContextException e) {
			logger.debug("As expected {} cannot be deleted.", contextString);
		}
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

		invalidCreate(contextA1); // Trying to recreate A1. Fails
		invalidCreate(contextA2); // Trying to recreate A2. Fails
		invalidCreate(contextB3); // Trying to recreate B3. Fails
		invalidCreate(contextB4); // Trying to recreate B4. Fails
		invalidCreate(contextA5); // Trying to recreate A5. Fails

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
		
		/*
		// This updates (move) has been made to test HRoles and HUsers
		
		contextA2.setParent(contextA5);
		update(contextA2);
		// __A1______________
		// _____B4___________
		// ________A5________
		// ___________A2_____
		// ______________B3__
		
		
		contextA5.setParent(contextA1);
		update(contextA5);
		// _________A1________
		// ______A5_____B4____
		// ___A2______________
		// B3_________________
		
		
		contextA5.setParent(contextB4);
		update(contextA5);
		// __A1______________
		// _____B4___________
		// ________A5________
		// ___________A2_____
		// ______________B3__
				
		
		
		contextA2.setParent(contextA1);
		update(contextA2);
		// ________A1________
		// ___A2_______B4____
		// B3______________A5
		*/
		

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
		} catch (ContextNotFoundException e) {
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

		Context contextC = new ContextImpl(CTX_NAME_C);
		contextC.setHeader(new HeaderImpl(contextA1.getHeader().getUUID()));
		invalidCreate(contextC);

		delete(contextA1);
		logger.debug("The DB should be now clean");
	}
	
	private List<Context> getAll() throws Exception{
		ContextManagement contextManagement = new ContextManagement();
		String allString = contextManagement.all(false);
		logger.trace(allString);
		List<Context> all = ISMapper.unmarshalList(Context.class, allString);
		return all;
	}
	
	/*
	// @Test
	public void deleteAll() throws Exception {
		List<Context> all = getAll();
		while(all.size()>0) {
			for (Context context : all) {
				logger.trace(ISMapper.marshal(context));
				List<IsParentOf<Context, Context>> children = context.getChildren();
				if(children==null || children.size()==0) {
					// delete(context);
				}
			}
			// all = getAll();
		}
	}
	*/
	
	@Test
	public void testGetAll() throws Exception {
		List<Context> contexts = getAll();
		for (Context context : contexts) {
			logger.trace(ISMapper.marshal(context));
			List<IsParentOf<Context, Context>> children = context.getChildren();
			for (IsParentOf<Context, Context> child : children) {
				Assert.assertTrue(child.getSource() == context);
				Context childContext = child.getTarget();
				Assert.assertTrue(childContext.getParent().getSource() == context);
			}
			roleUserAssertions(context.getHeader().getUUID(), null, false);
		}
	}

	// @Test
	public void readContext() throws ResourceRegistryException, IOException {
		Context context = read(UUID.fromString(""));
		logger.debug("{}", context);
	}

	
	// @Test
	public void deleteContext() throws ResourceRegistryException, IOException {
		Context context = read(UUID.fromString(""));
		delete(context);
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

}
