
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpGet;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.ContentType;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.internal.org.apache.http.util.EntityUtils;

/**
 * An example of publishing in the data catalogue.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CataloguePublishExample {
	
	private static final String LICENSE_LIST = "/api/licenses/list/";
	private static final String ORGANIZATIONS_LIST = "/api/organizations/list";
	private static final String ORGANIZATIONS_SHOW = "/api/organizations/show?id=";
	private static final String GROUPS_LIST = "/api/groups/list";
	private static final String PROFILES_LIST = "/api/profiles/profile_names/";
	private static final String GET_SINGLE_PROFILE_XML = "/api/profiles/profile?name="; // GET IT'S XML
	private static final String CREATE_ITEM = "/api/items/create";
	private static final String GET_BACK_ITEM = "/api/items/show?id=";
	
	public static void main(String[] args) throws Exception {
		
		// you should recover the address via IS...
		String catalogueServiceEndpoint = "http://catalogue-ws-d-d4s.d4science.org/catalogue-ws/rest";
		
		// token for the VRE in which the catalogue is present (and where I have at least catalogue-editor role, if
		// I'm willing to publish). Note: a catalogue may host more than one VRE/Organization. I'm taking the token I need.
		String myToken = "";
		
		// fetch list of licenses and print it
		JSONObject licenseBean = get(catalogueServiceEndpoint + LICENSE_LIST, myToken);
		
		if(licenseBean != null) {
			
			// get "real result" and print couple license id - license name
			JSONArray licenses = (JSONArray) licenseBean.get("result");
			
			for(int i = 0; i < licenses.size(); i++) {
				
				JSONObject license = (JSONObject) licenses.get(i);
				System.out.println("License name is " + (String) license.get("id"));
				System.out.println("License id is " + (String) license.get("title"));
				
			}
			
		}
		
		// do the same with organizations	
		JSONObject organizationsBean = get(catalogueServiceEndpoint + ORGANIZATIONS_LIST, myToken);
		if(organizationsBean != null) {
			
			// get list of organizations (a list of names is returned)
			JSONArray organizations = (JSONArray) organizationsBean.get("result");
			
			for(int i = 0; i < organizations.size(); i++) {
				String organization = (String) organizations.get(i);
				System.out.println("Organization name is " + organization);
			}
			
		}
		
		// take "nextnext" and show its details
		JSONObject organizationBean = get(catalogueServiceEndpoint + ORGANIZATIONS_SHOW + "nextnext", myToken);
		if(organizationBean != null) {
			
			// get list of organizations (a list of names is returned)
			JSONObject organization = (JSONObject) organizationBean.get("result");
			System.out.println("Next Next looks like " + organization.toJSONString());
			
		}
		
		// list groups
		JSONObject groupsBean = get(catalogueServiceEndpoint + GROUPS_LIST, myToken);
		if(groupsBean != null) {
			
			// get list of organizations (a list of names is returned)
			JSONArray groups = (JSONArray) groupsBean.get("result");
			
			for(int i = 0; i < groups.size(); i++) {
				String group = (String) groups.get(i);
				System.out.println("Group name is " + group);
			}
			
		}
		
		// now, the interesting part for publishing: we need to check for "profiles" metadata, if any, in the context
		// Currently, in dev, we have:
		//		Profile name is Test profile and Categories
		//		Profile name is Empty Profile
		//		Profile name is SoBigData.eu: Dataset Metadata NextNext
		//		Profile name is SoBigData.eu: Method Metadata NextNext
		//		Profile name is EOSCService
		//		Profile name is SoBigData.eu: Application Metadata NextNext
		JSONObject profilesBean = get(catalogueServiceEndpoint + PROFILES_LIST, myToken);
		if(profilesBean != null) {
			
			// get list of organizations (a list of names is returned)
			JSONArray profiles = (JSONArray) profilesBean.get("result");
			
			for(int i = 0; i < profiles.size(); i++) {
				String profile = (String) profiles.get(i);
				System.out.println("Profile name is " + profile);
			}
		}
		
		// let's check (because it sounds interesting) the "Empty" profile.. (NOTE: xml is returned here)
		String emptyProfile = getXML(catalogueServiceEndpoint + GET_SINGLE_PROFILE_XML + "Empty%20Profile", myToken);
		System.out.println("Empty profile looks like \n");
		System.out.println(emptyProfile);
		
		// result is:
		//		<metadataformat xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		//				type="EmptyType" xsi:noNamespaceSchemaLocation="https://wiki.gcube-system.org/images_gcube/e/e8/Gcdcmetadataprofilev3.xsd"/>
		
		// great! Now let's create an item with such profile
		JSONObject item = new JSONObject();
		item.put("name", "my_test_item"); // the name will be part of the uri.. it must be unique
		item.put("title", "This is a WS test");
		item.put("license_id", "cc-by");
		JSONArray tags = new JSONArray();
		JSONObject myTag = new JSONObject();
		myTag.put("name", "my marvellous item");
		tags.add(myTag);
		// Note: we have profiles, so we must set an extra with key system:type and a proper value(for instance, "EmptyType").
		JSONArray extras = new JSONArray();
		JSONObject sysTypeObject = new JSONObject();
		sysTypeObject.put("key", "system:type");
		sysTypeObject.put("value", "EmptyType");
		JSONObject otherField = new JSONObject();
		otherField.put("key", "Temporal Coverage");
		otherField.put("value", "whatever");
		extras.add(sysTypeObject);
		extras.add(otherField);
		item.put("extras", extras);
		item.put("tags", tags);
		
		JSONObject created = post(catalogueServiceEndpoint + CREATE_ITEM, item, myToken);
		System.out.println("Result is " + created.toJSONString());
		
	}
	
	/**
	 * Executes an http get request
	 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	private static JSONObject get(String url, String token) throws Exception {
		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			
			HttpGet req = new HttpGet(url);
			req.setHeader("gcube-token", token);
			HttpResponse response = httpClient.execute(req);
			
			if(response.getStatusLine().getStatusCode() != 200)
				throw new Exception("There was an error while serving the request " + response.getStatusLine());
			
			JSONParser parser = new JSONParser();
			JSONObject parsedObject = (JSONObject) parser.parse(EntityUtils.toString(response.getEntity()));
			return parsedObject;
			
		} catch(Exception e) {
			System.err.println("Error while serving request " + e);
			throw e;
		}
	}
	
	/**
	 * Executes a post request
	 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
	 * @param url
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	private static JSONObject post(String url, JSONObject request, String token) throws Exception {
		
		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			
			HttpPost post = new HttpPost(url);
			post.setHeader("gcube-token", token);
			
			StringEntity params = new StringEntity(request.toJSONString(), ContentType.APPLICATION_JSON);
			post.setEntity(params);
			
			HttpResponse response = httpClient.execute(post);
			
			if(response.getStatusLine().getStatusCode() != 200)
				throw new Exception("There was an error while serving the request " + response.getStatusLine());
			
			JSONParser parser = new JSONParser();
			JSONObject parsedObject = (JSONObject) parser.parse(EntityUtils.toString(response.getEntity()));
			return parsedObject;
			
		} catch(Exception e) {
			System.err.println("Error while serving request " + e);
			throw e;
		}
		
	}
	
	/**
	 * Executes an http get request to fetch an xml data
	 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	private static String getXML(String url, String token) throws Exception {
		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			
			HttpGet req = new HttpGet(url);
			req.setHeader("gcube-token", token);
			HttpResponse response = httpClient.execute(req);
			
			if(response.getStatusLine().getStatusCode() != 200)
				throw new Exception("There was an error while serving the request " + response.getStatusLine());
			
			return EntityUtils.toString(response.getEntity());
			
		} catch(Exception e) {
			System.err.println("Error while serving request " + e);
			throw e;
		}
	}
}
