package org.gcube.datacatalogue.ckanutillibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.models.CKanUserWrapper;
import org.gcube.datacatalogue.ckanutillibrary.models.CkanDatasetRelationship;
import org.gcube.datacatalogue.ckanutillibrary.models.DatasetRelationships;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesCkanGroupOrOrg;
import org.gcube.datacatalogue.ckanutillibrary.utils.UtilMethods;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanUser;

/**
 * Tests class.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class TestDataCatalogueLib {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TestDataCatalogueLib.class);

	private DataCatalogueFactory factory;
	private String scope = "/gcube/devNext/NextNext";
	private String testUser = "costantino_perciante";
	String subjectId = "aa_father4";
	String objectId = "bb_son4";

	//	@Before
	public void before(){
		factory = DataCatalogueFactory.getFactory();
	}

	//@Test
	public void getRole() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		instance.getOrganizationsAndRolesByUser(testUser, Arrays.asList(RolesCkanGroupOrOrg.ADMIN, RolesCkanGroupOrOrg.EDITOR, RolesCkanGroupOrOrg.MEMBER
				));

	}

	//@Test
	public void datasetsRelationshipCreate() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);

		DatasetRelationships relation = DatasetRelationships.parent_of;

		boolean resC = instance.createDatasetRelationship(subjectId, objectId, relation, "Comment for this relationship", instance.getApiKeyFromUsername(testUser));

		logger.debug("Res is " + resC);
	}

	//@Test
	public void datasetsRelationshipDelete() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		DatasetRelationships relation = DatasetRelationships.child_of;

		boolean resD = instance.deleteDatasetRelationship(subjectId, objectId, relation, instance.getApiKeyFromUsername(testUser));

		logger.debug("ResD is " + resD);
	}

	//@Test
	public void datasetRelationshipRetrieve() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);

		List<CkanDatasetRelationship> res = instance.getRelationshipDatasets(subjectId, objectId, instance.getApiKeyFromUsername(testUser));

		logger.debug("Relationships " + res);

	}

	//@Test
	public void factoryTest() throws Exception{

		DataCatalogueFactory factory = DataCatalogueFactory.getFactory();

		while(true){
			factory.getUtilsPerScope("/gcube");
			Thread.sleep(60* 1000 * 3);
			factory.getUtilsPerScope("/gcube");
			break;
		}

		for (int i = 0; i < 5; i++) {
			Thread.sleep(1000);
			factory.getUtilsPerScope("/gcube");
		}

	}

	//@Test
	public void testgetApiKeyFromUser() throws Exception {

		logger.debug("Testing getApiKeyFromUser");
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);

		String username = "francescomangiacrapa";
		String key = instance.getApiKeyFromUsername(username);

		System.out.println("key for " + username + " is " + key);
	}

	//@Test
	public void testgetUserFromApiKey() throws Exception {

		logger.debug("Testing getApiKeyFromUser");
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);

		String key = "put-your-key-here";
		CKanUserWrapper user = instance.getUserFromApiKey(key);

		System.out.println("user for " + key + " is " + user);
	}

	//@Test
	public void getOrganizationsByUser() throws Exception {

		System.out.println("Testing getOrganizationsByUser");
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);

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
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);

		String username = "andrea.rossi";
		instance = new DataCatalogueImpl("/gcube");
		List<RolesCkanGroupOrOrg> rolesToMatch = new ArrayList<RolesCkanGroupOrOrg>();
		rolesToMatch.add(RolesCkanGroupOrOrg.ADMIN);
		rolesToMatch.add(RolesCkanGroupOrOrg.MEMBER);
		rolesToMatch.add(RolesCkanGroupOrOrg.EDITOR);
		Map<String, List<RolesCkanGroupOrOrg>> map = instance.getOrganizationsAndRolesByUser(username, rolesToMatch);

		System.out.println("organizations for user " + username + " are " + map);
	}

	//@Test
	public void getUsers() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);

		List<RolesCkanGroupOrOrg> rolesToMatch = new ArrayList<RolesCkanGroupOrOrg>();
		rolesToMatch.add(RolesCkanGroupOrOrg.ADMIN);
		rolesToMatch.add(RolesCkanGroupOrOrg.EDITOR);

		Map<String, List<RolesCkanGroupOrOrg>> orgs = instance.getOrganizationsAndRolesByUser("costantino_perciante", rolesToMatch);

		Iterator<Entry<String, List<RolesCkanGroupOrOrg>>> iterator = orgs.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<java.lang.String, java.util.List<org.gcube.datacatalogue.ckanutillibrary.models.RolesCkanGroupOrOrg>> entry = (Map.Entry<java.lang.String, java.util.List<org.gcube.datacatalogue.ckanutillibrary.models.RolesCkanGroupOrOrg>>) iterator
					.next();

			logger.debug("Org is " + entry.getKey() + " and role is " + entry.getValue().get(0));

		}
	}

	//@Test
	public void getScopePerUrl(){

		ScopeProvider.instance.set("/gcube");
		String url = "https://dev4.d4science.org/group/devvre/ckan";
		String scopeToUse = ApplicationProfileScopePerUrlReader.getScopePerUrl(url);
		logger.debug("Retrieved scope is " + scopeToUse);

		ScopeProvider.instance.reset(); // the following sysout should print null
		String url2 = "https://dev4.d4science.org/group/devvre/ckan";
		String scopeToUse2 = ApplicationProfileScopePerUrlReader.getScopePerUrl(url2);
		logger.debug("Retrieved scope is " + scopeToUse2);
	}

	//@Test
	public void createUsers() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope("/gcube/devsec/devVRE");
		CheckedCkanClient client = new CheckedCkanClient(instance.getCatalogueUrl(), instance.getApiKeyFromUsername("costantino_perciante"));
		CkanUser editorUser = new CkanUser("user_editor_devvre", "user_editor_devvre@test.it", "");
		client.createUser(editorUser);
		CkanUser adminUser = new CkanUser("user_admin_devvre", "user_admin_devvre@test.it", "");
		client.createUser(adminUser);
		CkanUser memberUser = new CkanUser("user_member_devvre", "user_member_devvre@test.it", "");
		client.createUser(memberUser);

	}

	//@Test
	public void createAsEditor() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope("/gcube/devsec/devVRE");
		boolean checkedEditor = instance.checkRoleIntoOrganization("user_editor_devvre", "devvre", RolesCkanGroupOrOrg.MEMBER);
		if(checkedEditor){
			logger.debug("Created editor in devvre? " + checkedEditor);
		}
		boolean checkedAdmin = instance.checkRoleIntoOrganization("user_admin_devvre", "devvre", RolesCkanGroupOrOrg.MEMBER);
		if(checkedAdmin){
			logger.debug("Created admin in devvre? " + checkedAdmin);
		}
		boolean checkedMember = instance.checkRoleIntoOrganization("user_member_devvre", "devvre", RolesCkanGroupOrOrg.MEMBER);
		if(checkedMember){
			logger.debug("Created member in devvre? " + checkedMember);
		}
	}

	//@Test
	public void editorCreateDataset() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope("/gcube/devsec/devVRE");
		instance.createCKanDataset(instance.getApiKeyFromUsername("user_editor_devvre"), "dataset_as_editor_devvre_private", "devvre", null, null, null, null, 1, null, null, null, null, null, false);

	}

	//@Test
	public void adminCreateDataset() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope("/gcube/devsec/devVRE");
		//instance.createCKanDataset(instance.getApiKeyFromUsername("user_admin_devvre"), "dataset_as_admin_devvre", "devvre", null, null, null, null, 1, null, null, null, null, null, false);
		instance.createCKanDataset(instance.getApiKeyFromUsername("user_admin_devvre"), "dataset_as_admin_devvre_private", "devvre", null, null, null, null, 1, null, null, null, null, null, false);
	}

	//@Test
	public void adminChangeVisibility() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope("/gcube/devsec/devVRE");
		//instance.createCKanDataset(instance.getApiKeyFromUsername("user_editor_devvre"), "dataset_as_editor_devvre", "devvre", null, null, null, null, 1, null, null, null, null, null, false);
		//instance.setDatasetPrivate(true, "3571cca5-b0ae-4dc6-b791-434a8e062ce5", "dataset_as_admin_devvre_public", instance.getApiKeyFromUsername("user_admin_devvre"));

		boolean res = instance.setDatasetPrivate(false, "3571cca5-b0ae-4dc6-b791-434a8e062ce5", "33bbdcb1-929f-441f-8718-a9e5134f517d", instance.getApiKeyFromUsername("user_admin_devvre"));
		logger.debug(""+res);

		//		CheckedCkanClient client = new CheckedCkanClient(instance.getCatalogueUrl(), instance.getApiKeyFromUsername("user_admin_devvre"));
		//		CkanDataset dataset = client.getDataset("dataset_as_admin_devvre_private");
		//		logger.debug("Current value for private: " + dataset.isPriv());
		//		dataset.setPriv(!dataset.isPriv());
		//		CkanDataset datasetUpd = client.updateDataset(dataset);
		//		logger.debug("Private value is "  + datasetUpd.isPriv());
		//		

	}

	//@Test
	public void testInvalidOrgRole() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		boolean res = instance.isRoleAlreadySet("francesco_mangiacrapa", "devvre_group", RolesCkanGroupOrOrg.ADMIN, true);
		logger.debug(""+res);

		// set to editor
		instance.checkRoleIntoGroup("francesco_mangiacrapa", "devvre_group", RolesCkanGroupOrOrg.EDITOR);

	}

	//@Test
	public void existProductWithNameOrId() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		boolean res = instance.existProductWithNameOrId("notification_portlet_2");
		logger.debug(""+res);

	}

	//@Test
	public void createGroup() throws Exception{

		//		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		String title = "                 SoBigData.eu: Method Metadata NextNext                      ";
		String result = UtilMethods.fromGroupTitleToName(title);
		logger.debug(result);
		//		CkanGroup group = instance.createGroup(title, title, "A description for this group");
		//
		//		if(group != null){
		//

		// boolean associated = instance.checkRoleIntoGroup("user_admin_devvre", "sobigdata_eu_method_metadata_nextnext", RolesCkanGroupOrOrg.ADMIN);
		//
		//			if(associated){
		//
		//				boolean assigned = instance.assignDatasetToGroup(title, "dataset_random_editor", instance.getApiKeyFromUsername("user_editor_devvre"));
		//				logger.debug("Assigned is " + assigned);
		//			}
		//		}
	}

	//@Test
	public void testGroupAssociation() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		String datasetId = "test_grsf_group_stock_groups_sadsad";
		String groupName = "assessment-unit";
		instance.assignDatasetToGroup(groupName, datasetId, instance.getApiKeyFromUsername("costantino_perciante"));

	}

	//@Test
	public void testAddResource() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		String datasetId = "test_publish_folder_15_44";
		//instance.assignDatasetToGroup(groupName, datasetId, instance.getApiKeyFromUsername("costantino_perciante"));

		String api = instance.getApiKeyFromUsername("costantino_perciante");
		CheckedCkanClient client = new CheckedCkanClient(instance.getCatalogueUrl(), api);
		List<String> randomName = Arrays.asList("FIRMS", "RAM", "FishSource");
		for (int i = 0; i < 100; i++) {

			CkanResource resource = new CkanResource("https://goo.gl/FH5AQ5", datasetId);
			String name = randomName.get((int)Math.round(Math.ceil(Math.random() * 3)));

			resource.setName(name);
			client.createResource(resource);

		}
	}

	//@Test
	public void checkGroupRole() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		String role = instance.getRoleOfUserInGroup("francesco.mangiacrapa", "test-by-francesco", instance.getApiKeyFromUsername("francesco.mangiacrapa"));
		logger.debug("Role is " + role);
	}

	//@Test
	public void getURL() throws Exception{
		DataCatalogueImpl util = factory.getUtilsPerScope("/gcube/devNext/NextNext");

		CkanDataset dataset = new CkanDataset();
		CheckedCkanClient client = new CheckedCkanClient(util.getCatalogueUrl(), util.getApiKeyFromUsername("costantino_perciante"));

		dataset.setName("random-test-" + UUID.randomUUID().toString().substring(0, 5));
		dataset.setOwnerOrg(client.getOrganization("devvre").getId());
		dataset.setOpen(true);
		dataset.setPriv(true);
		//CkanDataset id = client.createDataset(dataset);

		//		util.setDatasetPrivate(true, client.getOrganization("devvre").getId(), id.getId(), util.getApiKeyFromUsername("costantino_perciante"));

	}

	//@Test
	public void deleteAndPurgeDataset() throws Exception{

		DataCatalogueImpl util = factory.getUtilsPerScope("/gcube/devNext/NextNext");
		boolean deleted = util.deleteProduct("random-test-56sadadsfsdf", util.getApiKeyFromUsername("user_admin_devvre"), true);
		logger.debug("Deleted ? " + deleted);

	}

}
