package org.gcube.vomanagement.usermanagement.test;

import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.ws.LiferayWSRoleManager;
import org.slf4j.LoggerFactory;

public class LiferayWSRoleTest {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LiferayWSRoleTest.class);

	private RoleManager roleManager = null;
	private String user = "";
	private String password = ""; // Put a valid password here
	private String host = "next.d4science.org";
	private String schema = "https";
	private int port = 443;

	//@Before
	public void init() throws Exception{

		logger.info("Init method, building LiferayWSRoleTest");
		roleManager = new LiferayWSRoleManager(user, password, host, schema, port);

	}

	//@Test
	public void getRoleByName() throws UserManagementSystemException, UserRetrievalFault, RoleRetrievalFault{

		String roleName = "Administrator";
		long id = roleManager.getRoleIdByName(roleName);
		logger.debug("Role id is " + id);

	}
	
}
