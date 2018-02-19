package org.gcube.datacatalogue.ckanutillibrary.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.UtilMethods;
import org.gcube.datacatalogue.ckanutillibrary.shared.CKanUserWrapper;
import org.gcube.datacatalogue.ckanutillibrary.shared.CkanDatasetRelationship;
import org.gcube.datacatalogue.ckanutillibrary.shared.DatasetRelationships;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
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

	//@Before
	public void before() throws Exception{
		factory = DataCatalogueFactory.getFactory();
	}
	
	//@Test
	public void getSysadminEmail() throws Exception{
		DataCatalogueImpl utils = factory.getUtilsPerScope(scope);
		System.out.println(utils.getCatalogueEmail());
	}

	//@Test
	public void getStatistics() throws Exception{

		DataCatalogueImpl utils = factory.getUtilsPerScope(scope);
		logger.debug("Statistics " + utils.getStatistics());

	}
	
	//@Test
	public void getLandingPages() throws Exception{

		DataCatalogueImpl utils = factory.getUtilsPerScope(scope);
		logger.debug("Landing pages " + utils.getLandingPages());

	}

	//@Test 
	public void getDatasetIdsFromDB() throws Exception{
		DataCatalogueImpl utils = factory.getUtilsPerScope(scope);
		List<String> ids = utils.getProductsIdsInGroupOrOrg("aquamaps", true, 0, Integer.MAX_VALUE);
		logger.debug("Size is " + ids.size());
	}

	//@Test
	public void searchInOrganization() throws Exception{
		DataCatalogueImpl utils = factory.getUtilsPerScope(scope);
		String apiKey = utils.getApiKeyFromUsername(testUser);
		List<CkanDataset> matches = utils.searchForPackageInOrganization(apiKey, "\"asfis:HMC+eez:AGO;FAO+grsf-org:INT+eez:AGO;RFB+iso3:AGO+isscfg:01.1.1\"", 0, 10, "grsf_admin");
		logger.debug("Size is " + matches.size());
	}

	//@Test
	public void search() throws Exception{
		DataCatalogueImpl utils = factory.getUtilsPerScope(scope);
		String apiKey = utils.getApiKeyFromUsername(testUser);
		List<CkanDataset> matches = utils.searchForPackage(apiKey, "\"asfis:HMC+eez:AGO;FAO+grsf-org:INT+eez:AGO;RFB+iso3:AGO+isscfg:01.1.1\"", 0, 10);
		logger.debug("Size is " + matches.size());
	}

	//	@Test
	public void testManageProduct() throws Exception{

		DataCatalogueImpl catalogue = factory.getUtilsPerScope(scope);
		String apiKey = catalogue.getApiKeyFromUsername("costantino_perciante");
		//Map<String, List<String>> map = new HashMap<String, List<String>>();
		//map.put("a new custom field", Arrays.asList("a new custom field 2"));
		//catalogue.patchProductCustomFields("test-searchable-504043", apiKey, map);
		catalogue.removeCustomField("test-searchable-504043", "a new custom field", "a new custom field", apiKey);
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
			Map.Entry<java.lang.String, java.util.List<org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg>> entry = (Map.Entry<java.lang.String, java.util.List<org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg>>) iterator
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
		instance.createCKanDataset(instance.getApiKeyFromUsername("user_editor_devvre"), "dataset_as_editor_devvre_private", null, "devvre", null, null, null, null, 1, null, null, null, null, null, false);

	}

	//@Test
	public void adminCreateDataset() throws Exception{

		DataCatalogueImpl instance = factory.getUtilsPerScope("/gcube/devsec/devVRE");
		//instance.createCKanDataset(instance.getApiKeyFromUsername("user_admin_devvre"), "dataset_as_admin_devvre", "devvre", null, null, null, null, 1, null, null, null, null, null, false);
		instance.createCKanDataset(instance.getApiKeyFromUsername("user_admin_devvre"), "dataset_as_admin_devvre_private", null, "devvre", null, null, null, null, 1, null, null, null, null, null, false);
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

	//	@Test
	public void testSearchableSet() throws Exception{

		DataCatalogueImpl util = factory.getUtilsPerScope("/gcube/devNext/NextNext");
		boolean setSearchability = util.setSearchableField("1b261d07-9f9c-414f-ad8d-c5aa429548fc", false);
		logger.debug("Searchability set? " + setSearchability);
	}

	//@Test
	public void testNameConversion(){

		logger.debug(UtilMethods.fromCKanUsernameToUsername("costantino_perciante"));

	}

	//@Test
	public void getGroupsRoles() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		Map<RolesCkanGroupOrOrg, List<String>> res = instance.getRolesAndUsersGroup("abundance-level");
		logger.debug(res.toString());

	}

	//@Test
	public void testResourcePatch() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		String id = "858f5e77-80c2-4cb2-bcfc-77529693dc9a";
		instance.patchResource(id, "http://ftp.d4science.org/previews/69cc0769-de6f-45eb-a842-7be2807e8887.jpg", "new_name_for_testing_patch.csv", "description test", "", instance.getApiKeyFromUsername("costantino_perciante"));
	}

	//@Test
	public void testPatchProduct() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		Map<String, List<String>> customFieldsToChange = new HashMap<String, List<String>>();
		customFieldsToChange.put("Status", Arrays.asList("Pending"));
		instance.patchProductCustomFields("a-test-to-ignore", instance.getApiKeyFromUsername("costantino_perciante"), customFieldsToChange, false);
	}

	//@Test
	public void addTag()throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		instance.addTag("test-after-tags-editing", instance.getApiKeyFromUsername("costantino_perciante"), "api add tag to test");
	}

	//@Test
	public void removeTag()throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		instance.removeTag("test-after-tags-editing", instance.getApiKeyFromUsername("costantino_perciante"), "api add tag to test");

	}

	//@Test
	public void removeGroup()throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		instance.removeDatasetFromGroup("pending", "test-after-tags-editing", instance.getApiKeyFromUsername("costantino_perciante"));

	}

	//@Test
	public void getGroups() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		instance.getParentGroups("abundance-level", instance.getApiKeyFromUsername("costantino_perciante"));

	}

	//	@Test
	public void createGroupsAndSetAsFather() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		//CkanGroup childGroup = instance.createGroup("test-group-child", "A child group", null);
		//CkanGroup parentGroup = instance.createGroup("test-group-parent", "A parent group", null);
		instance.setGroupParent("test-group-parent", "test-group-child");
	}

	//@Test
	public void testBelongsToGroup() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		boolean checked = instance.isDatasetInGroup("assessment-unit", "test-after-updates-17-1654");
		logger.debug("Result is " + checked);
	}

	//	@Test
	public void getDatasetsInGroup() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		List<CkanDataset> result = instance.getProductsInGroup("assessment-unit");
		for (CkanDataset ckanDataset : result) {
			logger.debug("Dataset name is " + ckanDataset.getName());
		}
	}

	//@Test
	public void getUserRoleByGroup() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		String username = "costantino_perciante";
		long init = System.currentTimeMillis();
		instance.getUserRoleByGroup(username, instance.getApiKeyFromUsername(username));
		long end = System.currentTimeMillis();
		logger.debug("Time taken " + (end - init));
	}

	//@Test
	public void getUserRoleByOrganization() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		String username = "costantino_perciante";
		long init = System.currentTimeMillis();
		instance.getUserRoleByOrganization(username, instance.getApiKeyFromUsername(username));
		long end = System.currentTimeMillis();
		logger.debug("Time taken " + (end - init));
	}

	//@Test
	public void getHigher(){
		logger.debug("Max is " + RolesCkanGroupOrOrg.getHigher(RolesCkanGroupOrOrg.ADMIN, RolesCkanGroupOrOrg.ADMIN));
	}

	//@Test
	public void getUrlProduct() throws Exception{
		DataCatalogueImpl instance = factory.getUtilsPerScope(scope);
		String datasetName = "test_from_andrea_rossi";
		String url = instance.getUrlFromDatasetIdOrName(datasetName);
		logger.debug("url is " + url);
	}

}
