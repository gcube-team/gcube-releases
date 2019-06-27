package org.gcube.data.access.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Iterator;

import org.gcube.data.access.bean.GSInstance;
import org.gcube.data.access.bean.GSInstanceList;
import org.gcube.data.access.bean.GSUser;
import org.gcube.data.access.bean.GrantType;
import org.gcube.data.access.bean.Group;
import org.gcube.data.access.bean.Instance;
import org.gcube.data.access.bean.PositionType;
import org.gcube.data.access.bean.Rule;
import org.gcube.data.access.bean.RuleList;
import org.gcube.data.access.bean.Rules;
import org.gcube.data.access.bean.UserGroup;
import org.gcube.data.access.bean.UserGroupList;
import org.gcube.data.access.bean.UserGroupList.UserGroups;
import org.gcube.data.access.bean.User;
import org.gcube.data.access.bean.UserList;
import org.gcube.data.access.bean.UserList.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class TestAPI {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	GeoFence gf = new GeoFence("https://geofence-d-d4s.d4science.org/geofence/rest");
	
	//@Test
	public void updateRule(){
		logger.info("TestUpdateRule");
		Rule rule = new Rule();

		rule.setGroup("5", "Mio gruppo 91");
		rule.setId("11");
		rule.setInstance("2","default-gs");
		rule.setLayer("lay");
		rule.setPriority("2");
		rule.setRequest("request");
		rule.setService("service");
		rule.setUser("44","test_7");
		rule.setWorkspace("exx");
		
		HttpStatus status = gf.updateRule(rule);
		assertEquals(204, status.value());
	}
	
	//@Test
	public void deleteRule(){
		logger.info("TestDeleteRule");
		HttpStatus status = gf.deleteRule("11");
		assertEquals(200, status.value());
	}
	
	//@Before
	public void createRule(){
		logger.info("TestCreateRule");
		Rule rule = new Rule();
		rule.setPosition(PositionType.FIXED_PRIORITY.toString(), "1");
		rule.setGrant(GrantType.DENY);
		rule.setGroup("8", "Mio gruppo 69");
		//rule.setId(id);
		rule.setInstance("2","default-gs");
		rule.setLayer("*");
		rule.setPriority("1");
		rule.setRequest("*");
		rule.setService("*");
		rule.setUser("40","test_26");
		rule.setWorkspace("xxx");

		//System.out.println(Utils.marshal(rule, Rule.class));
		
		HttpStatus status = gf.createRule(rule);
		assertEquals(201, status.value());
	}
	
	//@Test
	public void getRule(){
		logger.info("TestGetRule");
		Rules rl = gf.getRulesById("8");
		logger.info(rl.getId() + " " + rl.getUser().getName() + " " + rl.getGrant());
		
		String id = rl.getId();
		String priority = rl.getPriority();
		String service = rl.getService();
		String request = rl.getRequest();
		String workspace = rl.getWorkspace();
		String layer = rl.getLayer();
		String userId = rl.getUser().getId();
		String groupId = rl.getGroup().getId();
		String instanceId = rl.getInstance().getId();
		HttpStatus status = gf.updateRuleById(id, priority, service, request, workspace, layer, userId, groupId, instanceId);
		assertEquals(201, status.value());
	}
	
	//@Test
	public void getRules(){
		logger.info("TestGetRules");
		RuleList rl = gf.getRulesList();
		
		logger.info("rules " + rl.getRules().size());
		Iterator<Rules> it = rl.getRules().iterator();
		while (it.hasNext()) {
			Rules rule = it.next();
			logger.info(rule.getId() + " " + rule.getGrant() + " name: " + rule.getUser().getName() + " - " + rule.getGroup().getName() + " - " + rule.getInstance().getName());
		}
	}
	
	
	//@Test
	public void deleteInstance(){
		logger.info("TestDeleteInstance");
//		HttpStatus status = gf.deleteInstanceById("12");
//		assertEquals(200, status.value());
		
		HttpStatus status = gf.deleteInstanceByName("ss", false);
		assertEquals(200, status.value());
	}
	
	//@Test
	public void updateInstance(){
		logger.info("TestUpdateInstance");
		Instance instance = new Instance();
		instance.setId("12");
		instance.setBaseURL("https://geoserver1-spatial-dev.d4science.org/geoserver");
		instance.setName("geoserver_test");
		instance.setPassword("geoserver");
		instance.setUsername("admin");
		instance.setDescription("My geoserver test");
		HttpStatus status = gf.updateInstance(instance);		
		assertEquals(204, status.value());
	}
	
	//@Test
	public void testInstance() {
		logger.info("TestInstance");
		GSInstanceList g =  gf.getInstanceList();
		int size = g.getInstances().size();
		assertNotEquals(0, size);
		
		int x = (int) Math.floor(Math.random() * size);		
		String id = g.getInstances().get(x).getId();
		assertNotNull(id);		
		logger.info("id:" + id);
		GSInstance ins = gf.getInstanceById(id);
		logger.info(ins.getName() + " " + ins.getUsername() + "  " + ins.getBaseURL());

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
		
		
		//ins = gf.getInstanceByName("test" + ran);
		//logger.info(ins.getId());
		
//		HttpStatus status = gf.updateInstanceById(id, "admin", "geoserver", "https://geoserver1-spatial-dev.d4science.org/geoserver", "geoserver");
//		assertEquals(200, status.value());
	}
	
	
//	@Test
	public void testRemoveUserGroup() {
		logger.info("TestRemoveUserGroup");
//		HttpStatus status = gf.removeUserGroupByUserIdGroupId("49", "11");
//		assertEquals(204, status.value());
//		
//		status = gf.removeUserGroupByUserIdGroupName("49", "Mio gruppo 66");
//		assertEquals(204, status.value());
		
		HttpStatus status = gf.removeUserGroupByUserNameGroupId("test_78", "5");
		assertEquals(204, status.value());
		
		status = gf.removeUserGroupByUserNameGroupName("test_78", "Mio gruppo 96");
		assertEquals(204, status.value());
	}
	
	//@Test
	public void testAssignToUserGroup() {
		logger.info("TestAssignToUserGroup");
//		HttpStatus status = gf.assignToUserGroupByUserIdGroupId("49", "11");
//		assertEquals(204, status.value());
//		
//		status = gf.assignToUserGroupByUserIdGroupName("49", "Mio gruppo 66");
//		assertEquals(204, status.value());
		
		HttpStatus status = gf.assignToUserGroupByUserNameGroupId("test_78", "5");
		assertEquals(204, status.value());
		
		status = gf.assignToUserGroupByUserNameGroupName("test_78", "Mio gruppo 96");
		assertEquals(204, status.value());
	}
	
	//@Test
	public void testDeleteUserGroup() {
		logger.info("TestDeleteUserGroup");
		
		UserGroupList ugl = gf.getUserGroupList();
		int size = ugl.getUserGroups().size();
		assertNotEquals(0, size);

		int x = (int) Math.floor(Math.random() * size);		
		String id = gf.getUserGroupList().getUserGroups().get(x).getId();		
		if (size > 5) {// delete only usergroups > 5
			logger.info("DeleteUserGroup with id: " + id);
			HttpStatus status = gf.deleteUserGroupById(id);
			assertEquals(200, status.value());
		}
		
		x = (int) Math.floor(Math.random() * size);		
		String name = gf.getUserGroupList().getUserGroups().get(x).getName();
		
		if (size > 4) {// delete only usergroups > 4
			logger.info("DeleteUserGroup with name: " + name);
			HttpStatus status = gf.deleteUserGroupByName(name);
			assertEquals(200, status.value());
		}

	}
	
	//@Test
	public void testGetUserGroupList() {
		logger.info("TestGetUserGroupList");
		UserGroupList ugl = gf.getUserGroupList();
		int size = ugl.getUserGroups().size();
		assertNotNull(size);
		logger.info("GetUsersGroupList - num. userGroups: " + size);
		Iterator<UserGroups> i = ugl.getUserGroups().iterator();
		while (i.hasNext()) {
			UserGroups u = i.next();
			logger.info("id: " + u.getId() + " name: " + u.getName() + " enabled: " + u.isEnabled());
			assertNotNull(u.getId());
		}
	}
	
	//@Before
	public void testGetUserGroup() {
		logger.info("TestGetUserGroup");
		
		int size = gf.getUserGroupList().getUserGroups().size();
		assertNotEquals(0, size);

		String id = gf.getUserGroupList().getUserGroups().get(size - 1).getId();
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
		
		int size = gf.getUserGroupList().getUserGroups().size();
		assertNotEquals(0, size);

		int x = (int) Math.floor(Math.random() * size);
		
		String id = gf.getUserGroupList().getUserGroups().get(x).getId();
		Group g = gf.getUserGroupById(id);
		String name = g.getName();
		boolean enabled = Math.random() < 0.5;
		HttpStatus status = gf.updateUserGroupByName(name, enabled);
		assertEquals(204, status.value());
		
		x = (int) Math.floor(Math.random() * size);
		id = gf.getUserGroupList().getUserGroups().get(x).getId();
		g = gf.getUserGroupById(id);
		
		enabled = Math.random() < 0.5;
		status = gf.updateUserGroupById(g.getId(), enabled);
		assertEquals(204, status.value());
		
		g.setEnabled(false);
		gf.updateUserGroup(g);
		assertEquals(204, status.value());
	}	
	
	
	//@Before
	public void testCreateUserGroup() {
		logger.info("TestCreateUserGroup");
		// create random user
		int ran = (int) Math.floor(Math.random() * 100);
		UserGroup group = new UserGroup();
		group.setExtId("ext_id_" + ran);
		group.setName("Mio gruppo " + ran);
		group.setDateCreation(new Date());
		boolean enabled = Math.random() < 0.5;
		logger.info("enabled " + enabled);
		group.setEnabled(enabled);
		HttpStatus status = gf.createUserGroup(group);
		assertEquals(201, status.value());
	}

	//@Test
	public void testCreateUser() {
		logger.info("TestCreateUser");
		// create random user
		int ran = (int) Math.floor(Math.random() * 100);
		User user = new User();
		user.setExtId("id_" + ran);
		user.setName("test_" + ran);
		user.setPassword("test");
		user.setEmailAddress("email" + ran + " @gmail.com");
		user.setEnabled(true);
		user.setFullName("MyTest_" + ran);
		user.setAdmin(true);
		HttpStatus status = gf.createUser(user);
		assertEquals(201, status.value());
	}

	//@Test
	public void testGetUsers() {
		logger.info("TestGetUsers");
		UserList ul = gf.getUserList();
		int size = ul.getUsers().size();
		assertNotNull(size);
		logger.info("GetUsers - num. users: " + size);
		Iterator<Users> i = ul.getUsers().iterator();
		while (i.hasNext()) {
			Users u = i.next();
			logger.info("ExId: " + u.getExtId() + " userName: " + u.getUserName() + " id: " + u.getId());
			assertNotNull(u.getId());
		}
	}

	//@After
	public void testGetUser() {
		logger.info("TestGetUser");
		int size = gf.getUserList().getUsers().size();
		assertNotEquals(0, size);

		String id = gf.getUserList().getUsers().get(size - 1).getId();
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
		int size = gf.getUserList().getUsers().size();
		assertNotEquals(0, size);

		int x = (int) Math.floor(Math.random() * size);
		String id = gf.getUserList().getUsers().get(x).getId();
		logger.info("UpdateUser with id: " + id);
		int ran = (int) Math.floor(Math.random() * 100);
		HttpStatus status = gf.updateUserById(id, "new_pass", "email" + ran + "@gmail.com", false, false);
		assertEquals(204, status.value());
	}

	//@Test
	public void testDeleteUser() {
		logger.info("TestDeleteUser");
		int size = gf.getUserList().getUsers().size();
		assertNotEquals(0, size);

		int x = (int) Math.floor(Math.random() * size);
		String id = gf.getUserList().getUsers().get(x).getId();
		logger.info("DeleteUser with id: " + id);
		if (size > 5) {// delete only users > 5
			HttpStatus status = gf.deleteUserById(id, true);
			assertEquals(200, status.value());
		}
	}

}
