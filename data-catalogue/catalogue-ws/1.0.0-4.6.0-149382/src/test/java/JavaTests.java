import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.catalogue.beans.resource.CustomField;
import org.gcube.datacatalogue.catalogue.utils.CachesManager;
import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.metadatadiscovery.DataCalogueMetadataFormatReader;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;



public class JavaTests {

	//@Test
	public void testGetMetadataNames() throws Exception {

		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		DataCalogueMetadataFormatReader reader = new DataCalogueMetadataFormatReader();
		List<MetadataProfile> listProfiles = reader.getListOfMetadataProfiles();
		System.out.println("Result is " + listProfiles);

	}

	//@Test
	public void testJSONValueCategory(){
		JSONObject catJsonObj = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("id", "aaaaa");
		value.put("title", "this is the title");
		value.put("description", "this is the description");
		catJsonObj.put("qualified_name", value); // TODO check
		System.out.println(catJsonObj);
	}

	//@Test
	public void testDate(){

		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		String value = "2005-03-01";

		try{

			Date d = formatter2.parse(value);
			System.out.println("F1 is " + d.toLocaleString());
			if(d == null)
				d = formatter2.parse(value);

		}catch(Exception e){
			System.err.println("failed to parse date " + e);
		}

	}


	//@Test
	public void testSorting(){

		List<CustomField> toSort = new ArrayList<CustomField>();

		toSort.add(new CustomField("C", "value1", Integer.MAX_VALUE, 7)); 
		toSort.add(new CustomField("E", "value1", Integer.MAX_VALUE, 6)); 
		toSort.add(new CustomField("X", "value1", Integer.MAX_VALUE, 3));
		toSort.add(new CustomField("Z", "value1", 0, 3)); 
		toSort.add(new CustomField("A", "value2", 1, 2)); 
		toSort.add(new CustomField("D", "value1", 2, 4)); 
		toSort.add(new CustomField("B", "value1", 2, 1)); 

		Collections.sort(toSort);

		System.out.println("Sorted list " + toSort);
	}

	//@Test
	public void testJsonArray(){

		JSONArray tagsArrayOriginal = new JSONArray();
		String tag = "";
		tagsArrayOriginal.add(tag);

		System.out.println(tagsArrayOriginal);

	}

	//@Test
	public void testEHCache() throws Exception{

		ScopeProvider.instance.set("/gcube/devNext/NextNext");

		Cache profilesCache = CachesManager.getCache(CachesManager.PROFILES_READERS_CACHE);
		String context = ScopeProvider.instance.get();
		List<String> toReturn = new ArrayList<String>();

		DataCalogueMetadataFormatReader reader;
		if(profilesCache.isKeyInCache(context))
			reader = (DataCalogueMetadataFormatReader) profilesCache.get(context).getObjectValue();
		else{
			reader = new DataCalogueMetadataFormatReader();
			profilesCache.put(new Element(context, reader));
		}

		Thread.sleep(1000 * 60);
	}

	//@Test
	public void testJSONObj() throws Exception{

		JSONObject obj = new JSONObject();
		obj.put("c", "d");
		modifyJSON(obj);
		System.out.println(obj);
	}

	private static void modifyJSON(JSONObject obj){

		obj.put("a", "b");

	}

	//@Test
	public void testJSONParser() throws ParseException{

		String jsonString = "{\"help\": \"https://ckan-d-d4s.d4science.org/api/3/action/help_show?name=package_create\", \"success\": true, \"result\": {\"license_title\": \"License Not Specified\", \"maintainer\": \"Giancarlo Panichi\", \"relationships_as_object\": [], \"private\": false, \"maintainer_email\": \"giancarlo.panichi@isti.cnr.it\", \"num_tags\": 2, \"id\": \"e39990ac-c644-49f4-a8a2-6774c2e69b59\", \"metadata_created\": \"2017-03-31T12:07:23.836973\", \"metadata_modified\": \"2017-03-31T12:07:23.836983\", \"author\": \"Costantino Perciante\", \"author_email\": \"costantino.perciante@isti.cnr.it\", \"state\": \"active\", \"version\": \"1\", \"creator_user_id\": \"ab8189ff-6b07-4963-b36a-c83d53d7e49b\", \"type\": \"dataset\", \"resources\": [{\"mimetype\": null, \"cache_url\": null, \"hash\": \"\", \"description\": \"\", \"name\": \"resource A\", \"format\": \"\", \"url\": \"http://www.test.it\", \"datastore_active\": false, \"cache_last_updated\": null, \"package_id\": \"e39990ac-c644-49f4-a8a2-6774c2e69b59\", \"created\": \"2017-03-31T14:07:23.943698\", \"state\": \"active\", \"mimetype_inner\": null, \"last_modified\": null, \"position\": 0, \"revision_id\": \"687a9a8e-4450-4904-9f54-603e6dc71d54\", \"url_type\": null, \"id\": \"fb0c2e90-bc5e-4028-ae61-8cc799814c4d\", \"resource_type\": null, \"size\": null}], \"num_resources\": 1, \"tags\": [{\"vocabulary_id\": null, \"state\": \"active\", \"display_name\": \"Artifact Name-catalogue-ws\", \"id\": \"5b287a15-ff52-429a-8f24-d5f9a4cc0f42\", \"name\": \"Artifact Name-catalogue-ws\"}, {\"vocabulary_id\": null, \"state\": \"active\", \"display_name\": \"Java\", \"id\": \"ad62fb1a-02f7-4ee5-96f8-50c919866552\", \"name\": \"Java\"}], \"groups\": [], \"license_id\": \"notspecified\", \"relationships_as_subject\": [], \"organization\": {\"description\": \"VRE nextnext\", \"created\": \"2016-06-21T17:29:47.479879\", \"title\": \"NextNext\", \"name\": \"nextnext\", \"is_organization\": true, \"state\": \"active\", \"image_url\": \"\", \"revision_id\": \"12264679-247a-4c40-945d-38607c006d2a\", \"type\": \"organization\", \"id\": \"131d8f52-2566-458b-8bc4-04fb57f2580d\", \"approval_status\": \"approved\"}, \"name\": \"test-web-service-3\", \"isopen\": false, \"url\": null, \"notes\": null, \"owner_org\": \"131d8f52-2566-458b-8bc4-04fb57f2580d\", \"extras\": [{\"key\": \"Field/Scope of use\", \"value\": \"Any use\"}, {\"key\": \"UsageMode\", \"value\": \"Download\"}, {\"key\": \"categoryref:artifact_information:Artifact Name\", \"value\": \"catalogue-ws\"}, {\"key\": \"categoryref:artifact_information:Maven Location\", \"value\": \"http://maven.research-infrastructures.eu/nexus/index.html\"}, {\"key\": \"categoryref:artifact_information:Programming language\", \"value\": \"Java\"}, {\"key\": \"categoryref:developer_information:Identifier\", \"value\": \"costantino.perciante\"}, {\"key\": \"categoryref:extra_information:First Release\", \"value\": \"2017-04-03\"}, {\"key\": \"categoryref:extra_information:Size MB\", \"value\": \"2\"}, {\"key\": \"metadatacategory:artifact_information\", \"value\": \"{\\\"id\\\":\\\"artifact_information\\\",\\\"title\\\":\\\"Artifact Information\\\",\\\"description\\\":\\\"Artifact's main information    \\\\t\\\\t\\\"}\"}, {\"key\": \"metadatacategory:developer_information\", \"value\": \"{\\\"id\\\":\\\"developer_information\\\",\\\"title\\\":\\\"Developer Information\\\",\\\"description\\\":\\\"This section is about developer(s) information    \\\\t\\\\t\\\"}\"}, {\"key\": \"metadatacategory:extra_information\", \"value\": \"{\\\"id\\\":\\\"extra_information\\\",\\\"title\\\":\\\"Extras\\\",\\\"description\\\":\\\"Other information about the artifact    \\\\t\\\\t\\\"}\"}, {\"key\": \"metadatatype\", \"value\": \"software_type\"}, {\"key\": \"spatial\", \"value\": \"{\\\"type\\\": \\\"Point\\\",\\\"coordinates\\\": [-3.145, 53.078]}\"}], \"title\": \"title test from web service-3\", \"revision_id\": \"687a9a8e-4450-4904-9f54-603e6dc71d54\"}}";

		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject)parser.parse(jsonString);
		JSONArray tags = (JSONArray)((JSONObject)obj.get(CatalogueUtils.RESULT_KEY)).get(CatalogueUtils.TAGS_KEY);
		System.out.println("Tags are " + tags);
	}

}
