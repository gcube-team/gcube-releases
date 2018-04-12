package org.gcube.data_catalogue.grsf_publish_ws;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.Resource;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.TimeSeriesBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.record.Common;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.record.FisheryRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.record.StockRecord;
import org.gcube.data_catalogue.grsf_publish_ws.utils.HelperMethods;
import org.gcube.data_catalogue.grsf_publish_ws.utils.csv.CSVUtils;
import org.gcube.data_catalogue.grsf_publish_ws.utils.threads.AssociationToGroupThread;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.common.enums.Abundance_Level;
import org.gcube.datacatalogue.common.enums.Fishery_Type;
import org.gcube.datacatalogue.common.enums.Sources;
import org.gcube.datacatalogue.common.enums.Status;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JTests {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JTests.class);

	//@Test
	public void test() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException {

		FisheryRecord recordFishery = new FisheryRecord();
		recordFishery.setType(Fishery_Type.Fishing_Description);
		recordFishery.setDatabaseSources(new ArrayList<Resource<Sources>>());
		recordFishery.setStatus(Status.Pending);

		List<String> tags = new ArrayList<String>();
		List<String> groupsTitles = new ArrayList<String>();
		Map<String, Object> extras = new HashMap<String, Object>();

		// bottom up, looks up for Tag/Group fields
		Class<?> current = recordFishery.getClass();
		do{
			System.out.println("Class is " + current.getCanonicalName());
			Field[] fields = current.getDeclaredFields();

			for (Field field : fields) {
				if(field.isAnnotationPresent(Tag.class)){
					Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(recordFishery);
					if(f != null){

						tags.add(f.toString());

					}
				}
				if(field.isAnnotationPresent(Group.class)){
					Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(recordFishery);
					if(f != null){

						groupsTitles.add(f.toString());

					}
				}
				if(field.isAnnotationPresent(CustomField.class)){
					Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(recordFishery);
					if(f != null){

						// get the key to put into the map first
						extras.put(field.getAnnotation(CustomField.class).key(), f);

					}
				}
			}
		}
		while((current = current.getSuperclass())!=null);

		// print
		System.out.println("TAGS " + tags);
		System.out.println("GROUPS " + groupsTitles);
		System.out.println("EXTRAS " + extras);
	}

	//@Test
	public void testJsonSerializer(){
		//		
		//		String pendingAsString = "pending";
		//		Status resSatus = Status.onDeserialize(pendingAsString);
		//		System.out.println("Res is " + resSatus);
		//		
		//		System.out.println("To string is " + resSatus.onSerialize());

		Abundance_Level type = Abundance_Level.onDeserialize("Uncertain Not assessed");
		System.out.println("Res is  " + type.onSerialize());
	}

	//@Test
	public void testNameToStringEnum(){

		Abundance_Level elem = Abundance_Level.Uncertain_Not_Assessed;
		System.out.println("Enum name is = " + elem.name() + ", enum to string is = " + elem.toString() + ", enum on serialize = " + elem.onSerialize());

		// try deserializer
		String deserialize = "uncertain not assessed";
		Abundance_Level res = Abundance_Level.onDeserialize(deserialize);
		System.out.println(res.name());
	}

	//	@Test
	public void testJSONMapping() throws IOException{

		//		FisheryRecord record = new FisheryRecord();
		//		record.setType(Type.Assessment_Unit);
		//		record.setFisheryId("sajhdskajda");
		//		record.setScientificName("assadsadada");
		//		//		record.setExploitationRate(Exploitation_Rate.High_Fishing_Mortality);
		//		//		record.setAbundanceLevel(Abundance_Level.Uncertain_Not_Assessed);
		//		ArrayList<DatabaseSource> list = new ArrayList<DatabaseSource>();
		//		list.add(new DatabaseSource("http", null, Source.FIRMS));
		//		list.add(new DatabaseSource("http", null, Source.FIRMS));
		//		list.add(new DatabaseSource("http", null, Source.FIRMS));
		//		list.add(new DatabaseSource("http", null, Source.FIRMS));
		//		list.add(new DatabaseSource("http", null, Source.FISHSOURCE));
		//		list.add(new DatabaseSource("http", null, Source.RAM));
		//		record.setManagementEntity("management ashdskad");
		//		record.setProductionSystemType(Production_System_Type.Artisanal);
		//		record.setDatabaseSources(list);
		//		record.setAuthor("Costantino Perciante");
		//		record.setMaintainer("Costantino Perciante");
		//		record.setAuthorContact("costantino.perciante@isti.cnr.it");
		//		record.setStatus(Status.Pending);
		//		record.setVersion(new Long(1));
		//		//		record.setSpeciesScientificName("Katsuwonus pelamis (or SKJ)");
		//		record.setCatchesOrLandings("Catch - 18962 - ton - 2014");
		//		record.setDataOwner("Giancarlo Panichi");
		//
		//		List<String> groups = new ArrayList<String>();
		//
		//		// check group generation
		//		HelperMethods.getGroups(groups, record);
		//
		//		for (String group : groups) {
		//			System.out.println("Group is " + group);
		//		}
		//
		//		List<String> tags = new ArrayList<String>();
		//
		//		// check group generation
		//		HelperMethods.getTags(tags, record);
		//
		//		for (String tag : tags) {
		//			System.out.println("Tag is " + tag);
		//		}
		//
		//		Map<String, String> extras = new HashMap<String, String>();
		//		HelperMethods.getExtras(extras, record);
		//
		//		Iterator<Entry<String, String>> it = extras.entrySet().iterator();
		//
		//		while (it.hasNext()) {
		//			Map.Entry<java.lang.String, java.lang.String> entry = (Map.Entry<java.lang.String, java.lang.String>) it
		//					.next();
		//			System.out.println("Extra is " + entry);
		//		}


		//		List<Resource> sourcesOfInformation = recordStock.getSourceOfInformation();
		//		for (Resource sourceOfinformation : sourcesOfInformation) {
		//			Set<ConstraintViolation<Resource>> violationsSourceOfinformationsBean = validator.validate(sourceOfinformation);
		//			for (ConstraintViolation<Resource> constraintViolation : violationsSourceOfinformationsBean) {
		//				System.out.println("Violation is about " + constraintViolation.getPropertyPath() + ", message error is " + constraintViolation.getMessage());
		//			}
		//		}

		//
		//
		ObjectMapper mapper = new ObjectMapper();

		//Object to JSON in String
		StockRecord jsonInString = mapper.readValue("{\n   \"short_title\":\"Thunnus maccoyii SEAFO division D.1\",\n   \"data_owner\":\"CCSBT\",\n   \"narrative_state_and_trend\":\"<p>The 2014 assessment suggested that the SBT spawning biomass is at a very low fraction (9%) of its original biomass as well as below the level that could produce maximum sustainable yield. However, there has been some improvement since the 2011 stock assessment and the fishing mortality rate is below the level associated with MSY. &nbsp;The current TAC has been set using the management procedure adopted in 2011, which has a 70% probability of rebuilding to the interim target biomass level by 2035.<\\/p>\",\n   \"database_sources\":[\n      {\n         \"name\":\"firms\",\n         \"description\":\"unknown\",\n         \"url\":\"unkown\"\n      },\n      {\n         \"name\":\"fishsource\",\n         \"description\":\"unknown\",\n         \"url\":\"unkown\"\n      }\n   ],\n   \"reporting_year\":2015,\n   \"stock_name\":\"Southern Bluefin tuna - Global Name\",\n   \"assessment_methods\":\"Survey index\",\n   \"abundance_level\":[\n      {\n         \"year\":2014,\n         \"value\":\"low abundance\"\n      },\n      {\n         \"year\":2015,\n         \"value\":\"intermediate abundance\"\n      }\n   ],\n   \"abundance_level_for_grouping\":\"low abundance\",\n   \"scientific_advice\":\"<p>Based on the results of the MP operation for 2015&ndash;17 in its 2013 meeting and the outcome of the review of exceptional circumstances in its 2015 meeting, the ESC recommended that there is no need to revise the Extended Commission&rsquo;s 2013 TAC decision regarding the TAC for 2016&ndash;17. The recommended annual TAC for the years 2016-2017 is 14,647.4t.<\\/p>\",\n   \"type\":\"assessment unit\",\n   \"stock_id\":\"Southern Bluefin tuna - Global Title\",\n   \"uuid_knowledge_base\":\"c898163b-1dbe-4b97-ba4a-5a73e8b81db9\",\n   \"traceability_flag\":true,\n   \"water_area\":[\n      \"SEAFO division B.1\",\n      \"SEAFO division D.0\",\n      \"Indian Ocean, East \\/ 57.6\",\n      \"SEAFO division D.1\",\n      \"Cape of Good Hope\",\n      \"Indian Ocean, West \\/ 51.6\",\n      \"Indian Ocean, West \\/ 51.7\"\n   ],\n   \"assessment_distribution_area\":\"an area\",\n   \"stock_uri\":\"http:\\/\\/www.bluebridge.com\\/grsf\\/stock\\/c898163b-1dbe-4b97-ba4a-5a73e8b81db9\",\n   \"exploiting_fishery\":\"Tunas and billfishes fishery\",\n   \"exploitation_rate\":[\n      {\n         \"year\":2014,\n         \"value\":\"moderate fishing mortality\"\n      },\n      {\n         \"year\":2015,\n         \"value\":\"high fishing mortality\"\n      }\n   ],\n   \"exploitation_rate_for_grouping\":\"No or low fishing mortality\",\n   \"state_of_marine_resource\":\"Overexploited\",\n   \"species_scientific_name\":\"Thunnus maccoyii\",\n   \"source_of_information\":[\n      {\n         \"name\":\"sourcename\",\n         \"description\":\"unknown\",\n         \"url\":\"https:\\/\\/www.ccsbt.org\\/userfiles\\/file\\/docs_english\\/meetings\\/meeting_reports\\/ccsbt_22\\/report_of_SC20.pdf\"\n      },\n      {\n         \"name\":\"sourcename\",\n         \"description\":\"unknown\",\n         \"url\":\"https:\\/\\/www.ccsbt.org\\/userfiles\\/file\\/docs_english\\/meetings\\/meeting_reports\\/ccsbt_22\\/report_of_SC20-2.pdf\"\n      }\n   ],\n   \"status\":\"pending\",\n   \"catches_or_landings\":{\n      \"name\":\"catches_or_landings_example\",\n      \"description\":\"unknown\",\n      \"url\":\"a url\"\n   }\n}", StockRecord.class);
		System.out.println(jsonInString);


		//
		//		// JSON back to object
		//		StockRecord converted = mapper.readValue(jsonInString, recordStock.getClass());
		//		System.out.println(converted);
	}

	//@Test
	public void testFromScopeToOrgName(){

		System.out.println("Valid ? " + HelperMethods.isNameValid("this is not valid"));
		//		System.out.println(HelperMethods.retrieveOrgNameFromScope("/gcube/devNext/NextNext"));

	}

	//@Test
	public void testJSONResource() throws Exception{
		DataCatalogueFactory factory = DataCatalogueFactory.getFactory();
		DataCatalogue instance = factory.getUtilsPerScope("/gcube/devNext/NextNext");
		String datasetName = "test-after-time-series-bean-5";

		// time series
		List<TimeSeriesBean<String, Void>> timeSeries = new ArrayList<TimeSeriesBean<String,Void>>();
		timeSeries.add(new TimeSeriesBean<String, Void>(2001L, "Value A", null, null, null, null));
		timeSeries.add(new TimeSeriesBean<String, Void>(2231L, "Value B", null, null, null, null));
		timeSeries.add(new TimeSeriesBean<String, Void>(1943L, "Value C", null, null, null, null));
		timeSeries.add(new TimeSeriesBean<String, Void>(1054L, "Value D", null, null, null, null));
		timeSeries.add(new TimeSeriesBean<String, Void>(3422L, "Value E", null, null, null, null));

		Collections.sort(timeSeries);

		File csvFile = CSVUtils.listToCSV(timeSeries, null);

		// send file
		instance.uploadResourceFile(csvFile, datasetName, instance.getApiKeyFromUsername("costantino.perciante"), "random_name.csv", null, null, null);
	}

	//	@Test
	public void sharedVREFolderWriteTest() throws Exception{

		String token = "";
		String context = "/gcube/devNext/NextNext";

		ScopeProvider.instance.set(context);
		SecurityTokenProvider.instance.set(token);

		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();

		// Get a VRE folder by scope
		WorkspaceSharedFolder vreFolder = ws.getVREFolderByScope(context);

		//Get the VRE Folder catalogue
		WorkspaceCatalogue catalogueFolder = vreFolder.getVRECatalogue();

		logger.debug("Catalogue folder retrieved " + catalogueFolder.getName());

		//		WorkspaceItem stockFolder = catalogueFolder.find("stock");

		//		vreFolder.removeChild(stockFolder);

		/**
		 * Test is
		 * .catalogue:
		 * 		-test
		 * 			- a
		 * 				-aproductwiththisname
		 * 					- csv
		 * 						- testfile.csv
		 */

		String allSubPath = "/test/a/aproductwiththisname/";
		//WorkspaceFolder lastFolder = createGetSubFoldersByPath(catalogueFolder, allSubPath);
		//		WorkspaceFolder recordFolder = (WorkspaceFolder)getFolderOrCreate(catalogueFolder, "test", "");
		//		String firstLetter = "a";
		//		WorkspaceFolder firstLetterFolder = (WorkspaceFolder)getFolderOrCreate(recordFolder, firstLetter, "");
		//		String folderPath = "aproductwiththisname";
		//		WorkspaceFolder productFolder = (WorkspaceFolder)getFolderOrCreate(firstLetterFolder, folderPath, "");
		//logger.debug("Test folder created/get..its path is " + lastFolder.getPath());
		//				String ccsvUnderProductFolderName = productFolderName + "/" + "csv";
		//				WorkspaceFolder csvUnderProductFolder = (WorkspaceFolder)getFolderOrCreate(catalogueFolder, ccsvUnderProductFolderName, "");
		//				
		//				logger.debug("FOLDERS created " + csvUnderProductFolder.getPath());
		//		treeCheck(catalogueFolder);

	}

	public void treeCheck(WorkspaceFolder rootFolder) throws InternalErrorException{
		List<WorkspaceItem> children = rootFolder.getChildren();
		for (WorkspaceItem workspaceItem : children) {
			if(workspaceItem.isFolder()){
				logger.debug("children folder is " + workspaceItem.getName());
				treeCheck((WorkspaceFolder)workspaceItem);
			}
		}
	}

	/**
	 * Create subfolders in cascade, returning the last created ones
	 * It could be also used for getting them if they already exists
	 * @param folder
	 * @param subPath
	 * @return
	 */
	private static WorkspaceFolder createGetSubFoldersByPath(WorkspaceFolder folder, String subPath){

		String pathSeparator = "/";
		WorkspaceFolder parentFolder = folder;
		if(folder == null)
			throw new IllegalArgumentException("Root folder is null!");

		if(subPath == null || subPath.isEmpty())
			throw new IllegalArgumentException("subPath is null/empty!");

		try{
			if(subPath.startsWith(pathSeparator))
				subPath = subPath.replaceFirst(pathSeparator, "");

			if(subPath.endsWith(subPath))
				subPath = subPath.substring(0, subPath.length() - 1);

			logger.debug("Splitting path " + subPath);

			String[] splittedPaths = subPath.split(pathSeparator);

			for (String path : splittedPaths) {
				WorkspaceFolder createdFolder = getFolderOrCreate(parentFolder, path, "");
				logger.debug("Created subfolder with path " + createdFolder.getPath());
				parentFolder = createdFolder;
			}

		}catch(Exception e){
			logger.error("Failed to create the subfolders by path " + subPath);
		}

		return parentFolder;
	}

	/**
	 * Get a folder within the catalogue folder or create it if it doesn't exist.
	 * @return
	 */
	private static WorkspaceFolder getFolderOrCreate(WorkspaceFolder folder, String relativePath, String descriptionFolder){
		WorkspaceFolder result = null;
		try {
			if(folder.exists(relativePath) && folder.find(relativePath).isFolder())
				result = (WorkspaceFolder) folder.find(relativePath);
			if(result != null)
				logger.debug("Folder found with name " + result.getName() + ", it has id " + result.getId());
			else
				throw new Exception("There is no folder with name " + relativePath + " under foler " + folder.getName());
		} catch (Exception e) {
			logger.debug("Probably the folder doesn't exist", e);
			try{
				result = folder.createFolder(relativePath, descriptionFolder);
			} catch (InsufficientPrivilegesException | InternalErrorException | ItemAlreadyExistException e2) {
				logger.error("Failed to get or generate this folder", e2);
			}
		}
		return result;
	}

	//@Test
	public void GRSFServiceUrl() throws Exception{
		String context = "/gcube/devNext/NextNext";
		String url = GcoreEndpointGRSFService.getServiceUrl(context);
		logger.debug("Url is " + url);

	}

	//@Test
	public void removeHTML() throws Exception{

		String toTest = "<p>Based on the results of the MP operation[] asdkljlasdklsa . - * ; for 2015&ndash;17 in its 2013 meeting and the outcome of the review of exceptional circumstances in its 2015 meeting, the ESC recommended that there is no need to revise the Extended Commission&rsquo;s 2013 TAC decision regarding the TAC for 2016&ndash;17. The recommended annual TAC for the years 2016-2017 is 14,647.4t.</p>";
		HelperMethods.removeHTML(toTest);

	}

	//@Test
	public void fatherGroupAnnotation(){

		Common commonRecord = new Common();
		commonRecord.setStatus(Status.Approved);

		commonRecord.getStatus();

		Class<?> current = commonRecord.getClass();
		do{
			Field[] fields = current.getDeclaredFields();
			for (Field field : fields) {
				if(field.isAnnotationPresent(Group.class)){

					logger.debug("Field  is " + field.getType() + " and " + field.getType().isEnum());
					// check if the field is an enumerator, and the enum class is also annotated with @Group
					if(field.getClass().isEnum() && field.getClass().isAnnotationPresent(Group.class)){

						// extract the name from the enum class and add it to the groups
						// also convert to the group name that should be on ckan
						String groupName = HelperMethods.getGroupNameOnCkan(field.getClass().getSimpleName());
						logger.debug("Name is " +groupName );

					}

				}
			}
		}
		while((current = current.getSuperclass())!=null); // start from the inherited class up to the Object.class

	}

	//@Test
	public void testHierarchy() throws Exception{
		String name = "low-abundance";
		DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance("/gcube/devNext/NextNext");
		List<String> uniqueGroups = new ArrayList<String>();
		uniqueGroups.add(name);
		uniqueGroups.add(name);
		AssociationToGroupThread.findHierarchy(uniqueGroups, catalogue, catalogue.getApiKeyFromUsername("costantino_perciante"));
		logger.debug("Hierarchy is " + uniqueGroups);
	}

	//@Test
	public void testAssociationThread() throws Exception{
		String name = "low-abundance";
		DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance("/gcube/devNext/NextNext");
		AssociationToGroupThread threadGroups = new AssociationToGroupThread(Arrays.asList(name), "another-test-test-please-ignore", "grsf", "costantino_perciante", catalogue, "apiKey");
		threadGroups.start();
		threadGroups.join();
		logger.info("Thread stopped!");


	}
	
	//@Test
	public void testCaches(){
		
		String context = "/gcube/devNext/NextNext";
		String token = "";
		for (int i = 0; i < 1000; i++) {
			logger.debug(HelperMethods.getUserEmail(context, token));	
		}
		
	}
	
	//@Test
	public void testMatch(){
		
		Boolean value = Boolean.TRUE;
		String expr  = "false";
		
		System.out.println("Check " + value.toString().matches(expr));
		
		
	}
	
}
