package org.gcube.vomanagement.usermanagement.test;
import java.util.List;

import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.VirtualGroupNotExistingException;
import org.gcube.vomanagement.usermanagement.impl.ws.LiferayWSGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;
import org.slf4j.LoggerFactory;

/**
 * Test class for Liferay Group WS
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class LiferayWSGroupTest {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LiferayWSGroupTest.class);

	private GroupManager groupManager = null;
	private String user = "";
	private String password = ""; // Put a valid password here
	private String host = "";
	private String schema = "";
	private int port = -1;

	//@Before
	public void init() throws Exception{

		logger.info("Init method, building LiferayWSGroupManager");
		groupManager = new LiferayWSGroupManager(user, password, host, schema, port);

	}
	
	//@Test
	public void getGroupParentId() throws UserManagementSystemException, GroupRetrievalFault{
		
		long id = groupManager.getGroupParentId(-1);
		logger.debug("Retrieved parent id " + id);
		
	}
	
	//@Test
	public void getGroupId() throws UserManagementSystemException, GroupRetrievalFault{
		
		long id = groupManager.getGroupId("devNext");
		logger.debug("Retrieved  id " + id);
		
	}

	//@Test
	public void getGroup() throws UserManagementSystemException, GroupRetrievalFault{
		
		GCubeGroup group = groupManager.getGroup(-1);
		logger.debug("Retrieved  group " + group);
		
	}
	
	//@Test
	public void getGroupIdFromInfrastructureScope() throws IllegalArgumentException, UserManagementSystemException, GroupRetrievalFault{
		
		long id = groupManager.getGroupIdFromInfrastructureScope("/gcube/devNext/NextNext");
		logger.debug("Retrieved  group " + id);
	}

	//@Test
	public void getInfrastructureScope() throws IllegalArgumentException, UserManagementSystemException, GroupRetrievalFault{
		
		String scope = groupManager.getInfrastructureScope(-1);
		logger.debug("Retrieved  scope " + scope);
	}
	
	//@Test
	public void listGroupsByUser() throws IllegalArgumentException, UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault{
		
		List<GCubeGroup> groups = groupManager.listGroupsByUser(-1);
		logger.debug("Retrieved  groups " + groups);
	}
	
	//@Test
	public void getGateways(){
		List<GCubeGroup> gateways = groupManager.getGateways();
		logger.debug("Retrieved  gateways are " + gateways.size());
		
		for (GCubeGroup gCubeGroup : gateways) {
			logger.debug("Name is " + gCubeGroup.getGroupName());
		}
		
	}
	
	//@Test
	public void getVirtualGroups() throws GroupRetrievalFault, VirtualGroupNotExistingException{
		// virtual groups of nextnext vre
		List<VirtualGroup> virtualGroups = groupManager.getVirtualGroups(21660); 
		logger.debug("Retrieved  vg " + virtualGroups);
		
		// virtual groups of the gateway
		List<VirtualGroup> virtualGroupsGateway = groupManager.getVirtualGroups(1142377); 
		logger.debug("Retrieved  vg " + virtualGroupsGateway);
		
	}
	
	//@Test
	public void getGatewayInfo() throws UserManagementSystemException, GroupRetrievalFault{
		
		GCubeGroup getGroup = groupManager.getGroup(1142377);
		logger.debug("Retrieved  vg " + getGroup);
		
		String result = (String)groupManager.readCustomAttr(getGroup.getGroupId(), "Emailsender");
		logger.debug("Value is " + result);
	}
	
}