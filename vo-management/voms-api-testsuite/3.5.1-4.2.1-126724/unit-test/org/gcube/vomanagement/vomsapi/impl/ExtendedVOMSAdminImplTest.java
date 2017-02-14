package org.gcube.vomanagement.vomsapi.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.vomanagement.vomsapi.VOMSACL;
import org.gcube.vomanagement.vomsapi.VOMSAPITest;
import org.gcube.vomanagement.vomsapi.VOMSAdmin;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

public class ExtendedVOMSAdminImplTest extends VOMSAPITest {

	private VOMSAdmin vomsAdminMock;

	private VOMSAPIFactory factoryMock;

	private VOMSACL vomsACLMock;

	private ExtendedVOMSAdminImpl extendedVOMSAdminImpl;

	private String testDN = "testDN";

	private String testCA = "testCA";

	private String testVO = "testVO";

	private String testGroup1 = "testGroup1";

	private String testGroup2 = "testGroup2";
	
	private String testRole1 = "role1";

	private String testRole2 = "role2";


	@Before
	public void setUp() throws Exception {

		// create a mock VOMSAdmin
		this.vomsAdminMock = createMock(VOMSAdmin.class);

		// create a mock VOMSAPIFactory
		this.factoryMock = createMock(VOMSAPIFactory.class);

		// create a mock VOMSACL
		this.vomsACLMock = createMock(VOMSACL.class);

		// create the ExtendedVOMSAdmin to test
		this.extendedVOMSAdminImpl = new ExtendedVOMSAdminImpl(
				this.vomsAdminMock, this.vomsACLMock, this.factoryMock);

	}

	@After
	public void tearDown() throws Exception {

		verify(this.vomsAdminMock, this.vomsACLMock, this.factoryMock);

		this.extendedVOMSAdminImpl = null;
		this.vomsAdminMock = null;
		this.factoryMock = null;
		this.vomsACLMock = null;
	}

	@Test
	public void testListGroupedRoles() throws Exception {

		String fqanRole1 = "/" + this.testVO + "/" + this.testGroup1 + "/Role="
		+ this.testRole1;
		
		String fqanRole2 = "/" + this.testVO + "/Role=" + this.testRole2;
		
		String[] testRoles = new String[] { fqanRole1, fqanRole2};
		
		// set expectation on the vomsAdminMock
		expect(this.vomsAdminMock.listRoles(this.testDN, this.testCA))
				.andReturn(testRoles);

		// replay
		replay(this.factoryMock, this.vomsACLMock, this.vomsAdminMock);

		// test method
		HashMap<String, String[]> groupedRoles = this.extendedVOMSAdminImpl
				.listGroupedRoles(this.testDN, this.testCA);
		
		//check if groups contains the proper roles
		assertTrue(groupedRoles.size() == 2);
		assertTrue(groupedRoles.get("/" + this.testVO + "/" + this.testGroup1).length == 1);
		assertTrue(groupedRoles.get("/" + this.testVO).length == 1);		
		assertEquals(this.testRole1, groupedRoles.get("/" + this.testVO + "/" + this.testGroup1)[0]);
		assertEquals(this.testRole2, groupedRoles.get("/" + this.testVO)[0]);

	}

	@Test
	public void testListGroups() throws Exception {
		
		String fqanGroup1 = "/" + this.testVO;
		
		String fqanGroup2 = "/" + this.testVO + "/" + this.testGroup1;

		String fqanGroup3 = "/" + this.testVO + "/" + this.testGroup1 + "/" + this.testGroup2;
		
		// set expectation on the vomsAdminMock
		expect(this.vomsAdminMock.getVOName())
		.andReturn(fqanGroup1).anyTimes();		
		expect(this.vomsAdminMock.listSubGroups(fqanGroup1))
				.andReturn(new String[]{fqanGroup2}).anyTimes();
		expect(this.vomsAdminMock.listSubGroups(fqanGroup2))
		.andReturn(new String[]{fqanGroup3}).anyTimes();
		expect(this.vomsAdminMock.listSubGroups(fqanGroup3))
		.andReturn(new String[]{}).anyTimes();

		// replay
		replay(this.factoryMock, this.vomsACLMock, this.vomsAdminMock);

		// test method listGroupsRecursively
		String[] groups = this.extendedVOMSAdminImpl
				.listGroupsRecursively();
		
		//check if all groups have been returned
		assertTrue(groups.length == 3);
		assertArrayEquals(new String[]{fqanGroup1, fqanGroup2, fqanGroup3}, groups);
		
		// test method listSubGroupsRecursively
		String[] subGroups = this.extendedVOMSAdminImpl
				.listSubGroupsRecursively();
		
		//check if all groups have been returned
		assertTrue(groups.length == 3);
		assertArrayEquals(new String[]{fqanGroup2, fqanGroup3}, subGroups);
		
		// test method listGroupsRecursively
		String[] groups2 = this.extendedVOMSAdminImpl
				.listGroupsRecursively(fqanGroup2);
		
		//check if all groups have been returned
		assertTrue(groups.length == 3);
		assertArrayEquals(new String[]{fqanGroup2, fqanGroup3}, groups2);
		
		// test method listSubGroupsRecursively
		String[] subGroups2 = this.extendedVOMSAdminImpl
				.listSubGroupsRecursively(fqanGroup2);
		
		//check if all groups have been returned
		assertTrue(groups.length == 3);
		assertArrayEquals(new String[]{fqanGroup3}, subGroups2);
		
	}

	@Test
	public void testListRoles() throws Exception {

		String fqanRole1 = "/" + this.testVO + "/" + this.testGroup1 + "/Role="
		+ this.testRole1;
		
		String fqanRole2 = "/" + this.testVO + "/Role=" + this.testRole2;
		
		String fqanGroup1 = "/" + this.testVO + "/" + this.testGroup1;
		
		String[] testRoles = new String[] { fqanRole1, fqanRole2};
		
		// set expectation on the vomsAdminMock
		expect(this.vomsAdminMock.listRoles(this.testDN, this.testCA))
				.andReturn(testRoles);	

		// replay
		replay(this.factoryMock, this.vomsACLMock, this.vomsAdminMock);
		
		//perform list roles test
		String[] roles = this.extendedVOMSAdminImpl.listRoles(fqanGroup1, this.testDN, this.testCA);		
		
		//check roles returned
		assertTrue(roles.length == 1);
		assertEquals(this.testRole1, roles[0]);

		
	}

	@Test
	public void testListUsersAndGroups() throws Exception {
		
		//create Mock users
		User user1 = createMock(User.class);
		User user2 = createMock(User.class);
		
		//create groups
		String fqanGroup1 = "/" + this.testVO;
		
		String fqanGroup2 = "/" + this.testVO + "/" + this.testGroup1;

		String fqanGroup3 = "/" + this.testVO + "/" + this.testGroup1 + "/" + this.testGroup2;
			
				
		//configure the behaviour of the vomsAdminMock object
		expect(this.vomsAdminMock.getVOName()).andReturn(fqanGroup1);
		expect(this.vomsAdminMock.listSubGroups(fqanGroup1)).andReturn(new String[]{fqanGroup2});
		expect(this.vomsAdminMock.listSubGroups(fqanGroup2)).andReturn(new String[]{fqanGroup3});
		expect(this.vomsAdminMock.listSubGroups(fqanGroup3)).andReturn(new String[]{});

		expect(this.vomsAdminMock.listUsersWithRole(fqanGroup1, this.testRole1)).andReturn(new User[]{user1, user2});
		expect(this.vomsAdminMock.listUsersWithRole(fqanGroup2, this.testRole1)).andReturn(new User[]{});		
		expect(this.vomsAdminMock.listUsersWithRole(fqanGroup3, this.testRole1)).andReturn(new User[]{user2});

		// replay
		replay(this.factoryMock, this.vomsACLMock, this.vomsAdminMock);
	
		
		//perform the call
		HashMap<User, String[]> usersAndGroups = this.extendedVOMSAdminImpl.listUsersAndGroups(this.testRole1);
		
		//check returned user and groups
		assertTrue(usersAndGroups.containsKey(user1));
		assertTrue(usersAndGroups.containsKey(user2));
		assertEquals(1, usersAndGroups.get(user1).length);
		assertEquals(2, usersAndGroups.get(user2).length);
		assertEquals(fqanGroup1, usersAndGroups.get(user1)[0]);

	}

	@Test
	public void testListUsersAndRoles() throws Exception {
		
		//create Mock users
		User user1 = createMock(User.class);
		User user2 = createMock(User.class);
		
		//create group
		String fqanGroup2 = "/" + this.testVO + "/" + this.testGroup1;

		//configure the behaviour of the vomsAdminMock object
		expect(this.vomsAdminMock.listRoles()).andReturn(new String[]{this.testRole1, this.testRole2});
		expect(this.vomsAdminMock.listUsersWithRole(fqanGroup2, this.testRole1)).andReturn(new User[]{user1, user2});
		expect(this.vomsAdminMock.listUsersWithRole(fqanGroup2, this.testRole2)).andReturn(new User[]{user1});
		
		// replay
		replay(this.factoryMock, this.vomsACLMock, this.vomsAdminMock);		
	
		//perform the call
		HashMap<User, String[]> usersAndRoles = this.extendedVOMSAdminImpl.listUsersAndRoles(fqanGroup2);
		
		//check returned user and roles
		assertTrue(usersAndRoles.containsKey(user1));
		assertTrue(usersAndRoles.containsKey(user2));
		assertEquals(2, usersAndRoles.get(user1).length);
		assertEquals(1, usersAndRoles.get(user2).length);
		assertEquals(this.testRole1, usersAndRoles.get(user2)[0]);		
	}

	@Test
	public void testConfigureAsAdmin() {
		//TODO: test to be implemented		

		// replay
		replay(this.factoryMock, this.vomsACLMock, this.vomsAdminMock);			
	}

	@Test
	public void testRevokeAsAdmin() {
		//TODO: test to be implemented
		
		// replay
		replay(this.factoryMock, this.vomsACLMock, this.vomsAdminMock);	
	}

}
