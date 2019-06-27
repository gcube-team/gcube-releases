package org.gcube.data.access.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.gcube.data.access.bean.GSInstanceList;
import org.gcube.data.access.bean.GSUser;
import org.gcube.data.access.bean.Group;
import org.gcube.data.access.bean.Instance;
import org.gcube.data.access.bean.Rule;
import org.gcube.data.access.bean.RuleList;
import org.gcube.data.access.bean.Rules;
import org.gcube.data.access.bean.User;
import org.gcube.data.access.bean.UserGroup;
import org.gcube.data.access.bean.UserGroupList;
import org.gcube.data.access.bean.GrantType;
import org.gcube.data.access.bean.PositionType;
import org.gcube.data.access.bean.UserGroupList.UserGroups;
import org.gcube.data.access.bean.UserList.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class TestAllAPI {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	GeoFence gf = new GeoFence("https://geofence-d-d4s.d4science.org/geofence/rest");

	//@Before
	public void testCreateUser() {
		logger.info("TestCreateUser");
		// create random user
		int ran = (int) Math.floor(Math.random() * 100);
		User user = new User();
		user.setExtId("id_" + ran);
		user.setName("test_" + ran);
		user.setPassword("test");
		user.setEmailAddress("email" + ran + " @gmail.com");
		boolean b = Math.random() < 0.5;
		user.setEnabled(b);
		user.setFullName("MyTest_" + ran);
		user.setAdmin(true);
		HttpStatus status = gf.createUser(user);
		assertEquals(201, status.value());

		// create 2nd user
		ran = (int) Math.floor(Math.random() * 100);
		user = new User();
		user.setExtId("id_" + ran);
		user.setName("test_" + ran);
		user.setPassword("test");
		user.setEmailAddress("email" + ran + " @gmail.com");
		b = Math.random() < 0.5;
		user.setEnabled(b);
		user.setFullName("MyTest_" + ran);
		b = Math.random() < 0.5;
		user.setAdmin(b);
		status = gf.createUser(user);
		assertEquals(201, status.value());
	}

	//@Test
	public void testGetUser() {
		logger.info("TestGetUser");
		List<Users> users = gf.getUserList().getUsers();
		int size = users.size();
		assertNotEquals(0, size);

		String id = users.get(size - 1).getId();
		GSUser user = gf.getUserById(id);
		String userName = user.getName();
		logger.info("GetUser - id: " + user.getId() + " userName: " + userName);
		assertNotNull(userName);
		assertEquals(id, user.getId().toString());

		logger.info("GetUserGroup: " + user.getGroups().getGroup().size());
		if (user.getGroups().getGroup().size() > 0) {
			logger.info("Group name: " + user.getGroups().getGroup().get(0).getName());
		}

	}

	//@Test
	public void testUpdateUser() {
		logger.info("TestUpdateUser");
		List<Users> users = gf.getUserList().getUsers();
		int size = users.size();
		assertNotEquals(0, size);

		int x = (int) Math.floor(Math.random() * size);
		String id = users.get(x).getId();
		logger.info("UpdateUser with id: " + id);
		int ran = (int) Math.floor(Math.random() * 100);
		HttpStatus status = gf.updateUserById(id, "new_pass", "email" + ran + "@gmail.com", false, false);
		assertEquals(204, status.value());

		x = (int) Math.floor(Math.random() * size);
		String userName = users.get(x).getUserName();
		logger.info("UpdateUser with userName: " + userName);
		ran = (int) Math.floor(Math.random() * 100);
		status = gf.updateUserByUsername(userName, "new_pass", "email" + ran + "@gmail.com", false, false);
		assertEquals(204, status.value());
	}

	//@After
	public void testDeleteUser() {
		logger.info("TestDeleteUser");
		List<Users> users = gf.getUserList().getUsers();
		int size = users.size();
		assertNotEquals(0, size);

		int x = (int) Math.floor(Math.random() * size);
		String id = users.get(x).getId();
		logger.info("DeleteUser with id: " + id);
		if (size > 5) {// delete only if users > 5
			HttpStatus status = gf.deleteUserById(id, true);
			assertEquals(200, status.value());
		}
		x = (int) Math.floor(Math.random() * (size - 1));
		String userName = users.get(x).getUserName();
		logger.info("DeleteUser with userName: " + userName);
		if (size > 5) {// delete only if users > 5
			HttpStatus status = gf.deleteUserByUsername(userName, false);
			assertEquals(200, status.value());
		}
	}

	//@Before
	public void testCreateUserGroup() {
		logger.info("TestCreateUserGroup");
		// create random usergroup
		int ran = (int) Math.floor(Math.random() * 100);
		UserGroup group = new UserGroup();
		group.setExtId("ext_id_" + ran);
		group.setName("My group " + ran);
		group.setDateCreation(new Date());
		boolean enabled = Math.random() < 0.5;
		group.setEnabled(enabled);
		HttpStatus status = gf.createUserGroup(group);
		assertEquals(201, status.value());
		
		// create 2nd usergroup
		ran = (int) Math.floor(Math.random() * 100);
		group = new UserGroup();
		group.setExtId("ext_id_" + ran);
		group.setName("My group " + ran);
		group.setDateCreation(new Date());
		enabled = Math.random() < 0.5;
		group.setEnabled(enabled);
		status = gf.createUserGroup(group);
		assertEquals(201, status.value());
	}
	
	//@Test
	public void testGetUserGroup() {
		logger.info("TestGetUserGroup");	
		List<UserGroups> ugl = gf.getUserGroupList().getUserGroups();
		int size = ugl.size();
		assertNotEquals(0, size);

		String id = ugl.get(size - 1).getId();
		Group g = gf.getUserGroupById(id);
		String name = g.getName();
		logger.info("Group - id: " + g.getId() + " name: " + g.getName());		
		g = gf.getUserGroupByName(name);
		assertNotNull(name);
		assertEquals(id, g.getId());		
	}
	
	//@Test
	public void testUpdateUserGroup() {
		logger.info("TestUpdateUserGroup");		
		List<UserGroups> ugl = gf.getUserGroupList().getUserGroups();
		int size = ugl.size();		
		assertNotEquals(0, size);
		int x = (int) Math.floor(Math.random() * size);		
		String id = ugl.get(x).getId();
		Group g = gf.getUserGroupById(id);
		String name = g.getName();
		boolean enabled = Math.random() < 0.5;
		HttpStatus status = gf.updateUserGroupByName(name, enabled);
		assertEquals(204, status.value());
		
		x = (int) Math.floor(Math.random() * size);
		id = ugl.get(x).getId();
		g = gf.getUserGroupById(id);		
		enabled = Math.random() < 0.5;
		status = gf.updateUserGroupById(g.getId(), enabled);
		assertEquals(204, status.value());
		
		g.setEnabled(false);
		gf.updateUserGroup(g);
		assertEquals(204, status.value());
	}
	
	//@After
	public void testDeleteUserGroup() {
		logger.info("TestDeleteUserGroup");		
		UserGroupList ugl = gf.getUserGroupList();
		int size = ugl.getUserGroups().size();
		assertNotEquals(0, size);

		int x = (int) Math.floor(Math.random() * size);		
		String id = ugl.getUserGroups().get(x).getId();		
		if (size > 5) {// delete only if usergroups > 5
			logger.info("DeleteUserGroup with id: " + id);
			HttpStatus status = gf.deleteUserGroupById(id);
			assertEquals(200, status.value());
		}
		
		x = (int) Math.floor(Math.random() * (size - 1));		
		String name = ugl.getUserGroups().get(x).getName();		
		if (size > 5) {// delete only if usergroups > 5
			logger.info("DeleteUserGroup with name: " + name);
			HttpStatus status = gf.deleteUserGroupByName(name);
			assertEquals(200, status.value());
		}
	}
	

	//@Test
	public void testAssignRemoveUserToUserGroup() {
		logger.info("TestAssignRemoveUserToUserGroup");
		List<Users> users = gf.getUserList().getUsers();
		int size = users.size();
		assertNotEquals(0, size);

		int x = (int) Math.floor(Math.random() * size);
		String userId = users.get(x).getId();
		String userName = users.get(x).getUserName();
		
		List<UserGroups> ugl = gf.getUserGroupList().getUserGroups();
		size = ugl.size();
		assertNotEquals(0, size);
		
		x = (int) Math.floor(Math.random() * size);
		String groupId = ugl.get(x).getId();
		String groupName = ugl.get(x).getName();

		HttpStatus status = gf.assignToUserGroupByUserIdGroupId(userId, groupId);
		assertEquals(204, status.value());		
		status = gf.removeUserGroupByUserIdGroupId(userId, groupId);
		assertEquals(204, status.value());
		
		status = gf.assignToUserGroupByUserIdGroupName(userId, groupName);
		assertEquals(204, status.value());
		status = gf.removeUserGroupByUserIdGroupName(userId, groupName);
		assertEquals(204, status.value());	
		
		status = gf.assignToUserGroupByUserNameGroupId(userName, groupId);
		assertEquals(204, status.value());
		status = gf.removeUserGroupByUserNameGroupId(userName, groupId);
		assertEquals(204, status.value());
		
		status = gf.assignToUserGroupByUserNameGroupName(userName, groupName);
		assertEquals(204, status.value());	
		status = gf.removeUserGroupByUserNameGroupName(userName, groupName);
		assertEquals(204, status.value());
	}

	//@Before
	public void testInstance() {
		logger.info("TestInstance");
		int ran = (int) Math.floor(Math.random() * 100);
		Instance instance = new Instance();
		instance.setBaseURL("https://geoserver1-spatial-dev.d4science.org/geoserver");
		instance.setName("geoserver_test");
		instance.setPassword("geoserver");
		instance.setUsername("admin");
		instance.setDescription("A geoserver test number " + ran);
		instance.setDateCreation(new Date());
		
		HttpStatus status = gf.createInstance(instance);
		assertEquals(201, status.value());
		
		ran = (int) Math.floor(Math.random() * 100);
		instance = new Instance();
		instance.setBaseURL("https://geoserver1-spatial-dev.d4science.org/geoserver");
		instance.setName("geoserver_test_" +ran);
		instance.setPassword("geoserver");
		instance.setUsername("admin");
		instance.setDescription("A geoserver 2 test number " + ran);
		instance.setDateCreation(new Date());
		
		status = gf.createInstance(instance);
		assertEquals(201, status.value());
	}
	
	
	//@Before
	public void createRule(){
		logger.info("TestCreateRule");
		Rule rule = new Rule();
		rule.setPosition(PositionType.FIXED_PRIORITY.toString(), "1");
		rule.setGrant(GrantType.DENY);

		List<Users> users = gf.getUserList().getUsers();
		int size = users.size();
		assertNotEquals(0, size);
		int x = (int) Math.floor(Math.random() * size);
		String id = users.get(x).getId();
		String name = users.get(x).getUserName();
		rule.setUser(id, name);
		
		List<UserGroups> ugl = gf.getUserGroupList().getUserGroups();
		size = ugl.size();
		assertNotEquals(0, size);		
		x = (int) Math.floor(Math.random() * size);
		id = ugl.get(x).getId();
		name = ugl.get(x).getName();
		rule.setGroup(id, name);
		
		List<org.gcube.data.access.bean.GSInstanceList.Instance> instances = gf.getInstanceList().getInstances();
		size = instances.size();
		assertNotEquals(0, size);
		x = (int) Math.floor(Math.random() * size);
		id = instances.get(x).getId();
		name = instances.get(x).getName();		
		rule.setInstance(id, name);
		
		rule.setLayer("*");
		rule.setPriority("1");
		rule.setRequest("*");
		rule.setService("*");		
		rule.setWorkspace("test");	
		HttpStatus status = gf.createRule(rule);
		assertEquals(201, status.value());
		
		int ran = (int) Math.floor(Math.random() * 100);
		rule.setLayer("*");
		rule.setPriority("2");
		rule.setRequest("*");
		rule.setService("*");		
		rule.setWorkspace("test_" + ran);	
		status = gf.createRule(rule);
		assertEquals(201, status.value());
	}

	//@After
	public void updateInstance(){
		logger.info("TestUpdateInstance");
		GSInstanceList g =  gf.getInstanceList();
		int size = g.getInstances().size();
		assertNotEquals(0, size);
		
		int x = (int) Math.floor(Math.random() * size);		
		String id = g.getInstances().get(x).getId();
		Instance instance = new Instance();
		instance.setId(id);
		instance.setBaseURL("https://geoserver1-spatial-dev.d4science.org/geoserver");
		instance.setName("geoserver_test");
		instance.setPassword("geoserver");
		instance.setUsername("admin");
		int ran = (int) Math.floor(Math.random() * 100);
		instance.setDescription("My geoserver update " + ran);
		HttpStatus status = gf.updateInstance(instance);		
		assertEquals(204, status.value());
	}
	
	//@Test
	public void getRules(){
		logger.info("TestGetRules");
		RuleList rl = gf.getRulesList();
		int size = rl.getRules().size();
		assertNotEquals(0, size);
		
		Iterator<Rules> it = rl.getRules().iterator();
		String id = null;
		while (it.hasNext()) {
			Rules rule = it.next();
			logger.info(rule.getId() + " " + rule.getGrant() + " name: " + rule.getUser().getName() + " - " + rule.getGroup().getName() + " - " + rule.getInstance().getName());
			id = rule.getId();
		}
		
		Rules rule = gf.getRulesById(id);
		logger.info(rule.getId() + " - " + rule.getUser().getName() + " - " + rule.getGrant());		
	}
	
	//@After
	public void deleteInstance(){
		logger.info("TestDeleteInstance");
		GSInstanceList g =  gf.getInstanceList();
		int size = g.getInstances().size();
		assertNotEquals(0, size);
		
		int x = (int) Math.floor(Math.random() * size);		
		String name = g.getInstances().get(x).getName();
		
		HttpStatus status = gf.deleteInstanceByName(name, false);
		assertEquals(200, status.value());
		x = (int) Math.floor(Math.random() * (size - 1));	
		String id = g.getInstances().get(x).getId();
		
		status = gf.deleteInstanceById(id);
		assertEquals(200, status.value());
	}
	
}
