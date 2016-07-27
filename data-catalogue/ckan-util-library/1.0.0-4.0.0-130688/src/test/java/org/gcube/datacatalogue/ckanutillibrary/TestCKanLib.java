package org.gcube.datacatalogue.ckanutillibrary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.datacatalogue.ckanutillibrary.models.CKanUserWrapper;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesIntoOrganization;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanOrganization;

public class TestCKanLib {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TestCKanLib.class);

	CKanUtilsImpl instance;

	//@Test
	public void before() throws Exception{

		instance = new CKanUtilsImpl("/gcube");
		List<String> orgs = instance.getOrganizationsNamesByUser("costantino.perciante");
		for (String string : orgs) {
			System.out.println("org is " + string);
		}

	}

	//@Test
	public void testgetApiKeyFromUser() {

		logger.debug("Testing getApiKeyFromUser");

		String username = "francescomangiacrapa";
		String key = instance.getApiKeyFromUsername(username);

		System.out.println("key for " + username + " is " + key);
	}

	//@Test
	public void testgetUserFromApiKey() {

		logger.debug("Testing getApiKeyFromUser");

		String key = "put-your-key-here";
		CKanUserWrapper user = instance.getUserFromApiKey(key);

		System.out.println("user for " + key + " is " + user);
	}

	//@Test
	public void getOrganizationsByUser() {

		System.out.println("Testing getOrganizationsByUser");

		String username = "francescomangiacrapa";
		List<CkanOrganization> organizations = instance.getOrganizationsByUser(username);

		System.out.println("organizations for user " + username + " are: ");

		for (CkanOrganization ckanOrganization : organizations) {
			System.out.println("-" + ckanOrganization.getName());
		}
	}

	//@Test
	public void getGroupsAndRolesByUser() throws Exception {

		logger.debug("Testing getGroupsAndRolesByUser");

		String username = "andrea.rossi";
		instance = new CKanUtilsImpl("/gcube");
		List<RolesIntoOrganization> rolesToMatch = new ArrayList<RolesIntoOrganization>();
		rolesToMatch.add(RolesIntoOrganization.ADMIN);
		rolesToMatch.add(RolesIntoOrganization.MEMBER);
		rolesToMatch.add(RolesIntoOrganization.EDITOR);
		Map<String, List<RolesIntoOrganization>> map = instance.getGroupsAndRolesByUser(username, rolesToMatch);

		System.out.println("organizations for user " + username + " are " + map);
	}

	//@Test
	public void getUsers() throws Exception{

		instance = new CKanUtilsImpl("/gcube");

		List<RolesIntoOrganization> rolesToMatch = new ArrayList<RolesIntoOrganization>();
		rolesToMatch.add(RolesIntoOrganization.ADMIN);
		rolesToMatch.add(RolesIntoOrganization.EDITOR);

		Map<String, List<RolesIntoOrganization>> orgs = instance.getGroupsAndRolesByUser("costantino_perciante", rolesToMatch);

		Iterator<Entry<String, List<RolesIntoOrganization>>> iterator = orgs.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<java.lang.String, java.util.List<org.gcube.datacatalogue.ckanutillibrary.models.RolesIntoOrganization>> entry = (Map.Entry<java.lang.String, java.util.List<org.gcube.datacatalogue.ckanutillibrary.models.RolesIntoOrganization>>) iterator
					.next();

			logger.debug("Org is " + entry.getKey() + " and role is " + entry.getValue().get(0));

		}
	}
}
