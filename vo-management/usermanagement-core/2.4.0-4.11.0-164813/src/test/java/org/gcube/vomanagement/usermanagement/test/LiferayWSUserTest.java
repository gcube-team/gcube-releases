package org.gcube.vomanagement.usermanagement.test;
import java.util.ArrayList;
import java.util.List;

import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.ws.LiferayWSUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;


/**
 * Test class for Liferay User WS
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class LiferayWSUserTest{

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LiferayWSUserTest.class);

	private UserManager userManager = null;
	private String user = "";
	private String password = ""; // Put a valid password here
	private String host = "next.d4science.org";
	private String schema = "https";
	private int port = 443;

	//@Before
	public void init() throws Exception{

		logger.info("Init method, building LiferayWSUserManager");
		userManager = new LiferayWSUserManager(user, password, host, schema, port);

	}
	
	//@Test
	public void getUserByUsername() throws UserManagementSystemException, UserRetrievalFault{

		String username = "costantino.perciante";
		GCubeUser gcubeUser = userManager.getUserByUsername(username);
		logger.debug("Retrieved user object " + gcubeUser);

	}

	//@Test
	@SuppressWarnings("deprecation")
	public void getUserByScreenName() throws UserManagementSystemException, UserRetrievalFault{

		String username = "costantino.perciante";
		GCubeUser gcubeUser = userManager.getUserByScreenName(username);
		logger.debug("Retrieved user object " + gcubeUser);

	}

	//@Test
	public void listUsersByGroup() throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault{

		long init = System.currentTimeMillis();
		List<GCubeUser> gcubeUsers = userManager.listUsersByGroup(21660);
		logger.debug("Retrieved user object " + gcubeUsers.size());
		logger.debug("Time is " + (System.currentTimeMillis() - init) );
	}
	
	//@Test
	public void getUsersHavingRole() throws UserManagementSystemException, RoleRetrievalFault, GroupRetrievalFault, UserRetrievalFault{

		long roleId  = 29548512; // data miner manager
		long groupId = 21660;
		long init = System.currentTimeMillis();
		List<GCubeUser> users = userManager.listUsersByGroupAndRole(groupId, roleId);
		logger.debug("Time is " + (System.currentTimeMillis() - init) );
		logger.debug("Users are " + users);
		
		for (GCubeUser user:users) {
			logger.debug("Username is " + user.getUsername() + "\n");
			
		}

	}

	//@Test
	public void readCustomAttr() throws UserRetrievalFault{

		long userId = 21325; 
		String value = (String) userManager.readCustomAttr(userId, "industry");
		logger.debug("Retrieved custom field value " + value);

	}

	//@Test
	public void getUserByEmail() throws UserRetrievalFault, UserManagementSystemException{

		String email = "m.assante@gmail.com";
		GCubeUser user = userManager.getUserByEmail(email);
		logger.debug("Retrieved custom field value " + user);

	}

	//@Test
	public void listUsersByGlobalRole(){

		long roleId  = 20161; // administrators
		List<GCubeUser> users = userManager.listUsersByGlobalRole(roleId); 
		logger.debug("Users are " + users.size());
	}

	//@Test
	public void getUsersInRoot() throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault{

		long init = System.currentTimeMillis();
		List<String> usernames = new ArrayList<String>();
		List<GCubeUser> users = userManager.listUsersByGroup(20495);
		for (GCubeUser gCubeUser : users) {
			usernames.add(gCubeUser.getUsername());
		}

		long end = System.currentTimeMillis();
		logger.debug("Time taken to retrieve " + users.size() + " usernames is " + (end - init));
	}
	
	//@Test
	public void getList() throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault{

		long init = System.currentTimeMillis();
		List<Long> users = userManager.getUserIdsByGroup(21660);
		long end = System.currentTimeMillis();
		logger.debug("Time taken to retrieve " + users.size() + " usernames is " + (end - init));
	}

}
