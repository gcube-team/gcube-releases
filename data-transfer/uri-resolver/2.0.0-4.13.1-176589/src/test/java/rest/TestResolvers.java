/**
 *
 */
package rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileReader;
import org.gcube.datatransfer.resolver.util.HTTPCallsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class TestResolvers.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 31, 2018
 */
public class TestResolvers {


	public static final String URI_RESOLVER_SERVICE_ENDPOINT = "https://data1-d.d4science.net";

	public static final Logger logger = LoggerFactory.getLogger(TestResolvers.class);

	/**
	 * Storage hub test.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void testStorageHub() throws Exception{

		String storageHubId = "E_RHdYOUxBSGJ3dU1ESjEyMXNuc2ZMRm5HbXh0d1ZqaWJ4MHBaN0lOZ0dBR1dCQlVhWnVIS0hrdmN2VDhkTXk0UA==";
		String url = String.format("%s/shub/%s",URI_RESOLVER_SERVICE_ENDPOINT,storageHubId);
		logger.info("Request to URL: "+url);
		URL toURL;
		int status;
		try {
			toURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) toURL.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			status = con.getResponseCode();
			System.out.println("header fields: "+con.getHeaderFields());
			String fileName = getFilename(con.getHeaderFields());
			System.out.println("Response status is: "+status);
			if(status==HttpStatus.SC_OK){
				Path target = Files.createTempFile(FilenameUtils.getBaseName(fileName), "."+FilenameUtils.getExtension(fileName));
				try (InputStream in = con.getInputStream()) {
				    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
				}
				System.out.println("File downloaded at: "+target.toAbsolutePath());
			}else{
				System.out.println("\nNo file downoladed");
				System.out.println("Response: \n"+getContentReponse(con.getInputStream()));
			}
			//con.setRequestProperty("Content-Type", "application/json");
			//con.setConnectTimeout(5000);
			//con.setReadTimeout(5000)

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error: ",e);
			throw e;
		}
	}


	/**
	 * Storage hub test.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void testStorageID() throws Exception{

		String storageID = "clZ2YmxTYytETzVLaHkwMjM3TmVETTFMb256YVRMS3lHbWJQNStIS0N6Yz0";
		String url = String.format("%s/storage/%s",URI_RESOLVER_SERVICE_ENDPOINT,storageID);
		logger.info("Request to URL: "+url);
		URL toURL;
		int status;
		try {
			toURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) toURL.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			status = con.getResponseCode();
			//printHeaders(con.getHeaderFields());
			String fileName = getFilename(con.getHeaderFields());
			System.out.println("Response status is: "+status);
			if(status==HttpStatus.SC_OK){
				Path target = Files.createTempFile(FilenameUtils.getBaseName(fileName), "."+FilenameUtils.getExtension(fileName));
				try (InputStream in = con.getInputStream()) {
				    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
				}
				System.out.println("File downloaded at: "+target.toAbsolutePath());
			}else{
				System.out.println("\nNo file downoladed");
				System.out.println("Response: \n"+getContentReponse(con.getInputStream()));
			}
			//con.setRequestProperty("Content-Type", "application/json");
			//con.setConnectTimeout(5000);
			//con.setReadTimeout(5000)

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error: ",e);
			throw e;
		}
	}

	/**
	 * Storage hub test.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void testStorageIDdoHEAD() throws Exception{

		String storageID = "clZ2YmxTYytETzVLaHkwMjM3TmVETTFMb256YVRMS3lHbWJQNStIS0N6Yz0";
		String url = String.format("%s/storage/%s",URI_RESOLVER_SERVICE_ENDPOINT,storageID);
		logger.info("Request to URL: "+url);
		URL toURL;
		int status;
		try {
			toURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) toURL.openConnection();
			con.setRequestMethod("HEAD");
			con.connect();
			status = con.getResponseCode();
			//printHeaders(con.getHeaderFields());
			System.out.println("Response status is: "+status);
			if(status==HttpStatus.SC_OK){
				System.out.println("\nFile to URL: "+url +" is reachable via doHEAD");
				System.out.println(IOUtils.toString(con.getInputStream()));
			}else{
				System.err.println("\nNo file reachable at: "+url);
			}
			//con.setRequestProperty("Content-Type", "application/json");
			//con.setConnectTimeout(5000);
			//con.setReadTimeout(5000)

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error: ",e);
			throw e;
		}
	}

	/**
	 * Storage hub test.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void testCatalogueResolver() throws Exception{

		String entityName = "sarda-sarda";
		String entityContext = "ctlg";
		String vreName = "devVRE";

		String url = String.format("%s/%s/%s/%s",URI_RESOLVER_SERVICE_ENDPOINT,entityContext, vreName, entityName);
		logger.info("Request to URL: "+url);
		URL toURL;
		int status;
		try {
			toURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) toURL.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			status = con.getResponseCode();
			System.out.println("Response status is: "+status);
			if(status==HttpStatus.SC_OK){
				System.out.println("Response: \n"+getContentReponse(con.getInputStream()));
			}else{
				System.out.println("\nError on resolving the Catalogue URL: "+toURL);
				System.out.println("Response: \n"+getContentReponse(con.getInputStream()));
			}
			//con.setRequestProperty("Content-Type", "application/json");
			//con.setConnectTimeout(5000);
			//con.setReadTimeout(5000)

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error: ",e);
			throw e;
		}
	}

	/**
	 * Storage hub test.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void testCatalogueResolverCreatePublicItemURL() throws Exception{

		String entityName = "sarda-sarda";
		//String entityContext = "product";
		String entityContext = "dataset";
		String scope = "/gcube/devNext/NextNext";

		String url = String.format("%s/%s",URI_RESOLVER_SERVICE_ENDPOINT,"catalogue");
		logger.info("POST Request to URL: "+url);
		try {

			HTTPCallsUtils httCaller = new HTTPCallsUtils(null, null);
			String jsonString =
					"{" +
					"\"gcube_scope\":\""+scope+"\"," +
					"\"entity_context\":\""+entityContext+"\"," +
					"\"entity_name\":\""+entityName+"\"" +
					"}";
			System.out.println("Sending JSON: "+jsonString);
//			JSONObject json = new JSONObject();
//			json.append("gcube_scope", scope);
//			json.append("entity_context", entityContext);
//			json.append("entity_name", entityName);
//			System.out.println("Sending json object: "+json.toString());
			InputStream response = httCaller.post(url, jsonString, "application/json");
			System.out.println("Response: \n"+getContentReponse(response));
			//con.setRequestProperty("Content-Type", "application/json");
			//con.setConnectTimeout(5000);
			//con.setReadTimeout(5000)

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error: ",e);
			throw e;
		}
	}


	/**
	 * Test catalogue resolver create private item url.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void testCatalogueResolverCreatePrivateItemURL() throws Exception{

		String entityName = "dynamic_reporting";
		//String entityContext = "product";
		String entityContext = "dataset";
		String scope = "/gcube/devsec/devVRE";

		String url = String.format("%s/%s",URI_RESOLVER_SERVICE_ENDPOINT,"catalogue");
		logger.info("POST Request to URL: "+url);
		try {

			HTTPCallsUtils httCaller = new HTTPCallsUtils(null, null);
			String jsonString =
					"{" +
					"\"gcube_scope\":\""+scope+"\"," +
					"\"entity_context\":\""+entityContext+"\"," +
					"\"entity_name\":\""+entityName+"\"" +
					"}";
			System.out.println("Sending JSON: "+jsonString);
//			JSONObject json = new JSONObject();
//			json.append("gcube_scope", scope);
//			json.append("entity_context", entityContext);
//			json.append("entity_name", entityName);
//			System.out.println("Sending json object: "+json.toString());
			InputStream response = httCaller.post(url, jsonString, "application/json");
			System.out.println("Response: \n"+getContentReponse(response));
			//con.setRequestProperty("Content-Type", "application/json");
			//con.setConnectTimeout(5000);
			//con.setReadTimeout(5000)

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error: ",e);
			throw e;
		}
	}




	//@Test
	/**
	 * Test gis resolver.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void testGisResolver() throws Exception{

		String gisUUID = "55c19a1f-214b-4f81-9220-fba09fcfa91f";
		String scope = "/gcube/devsec/devVRE";

		String queryString = "gis-UUID="+gisUUID +"&scope="+scope;
		String url = String.format("%s/gis?%s",URI_RESOLVER_SERVICE_ENDPOINT,queryString);

		logger.info("Request to URL: "+url);
		URL toURL;
		int status;
		try {
			toURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) toURL.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			status = con.getResponseCode();
			//printHeaders(con.getHeaderFields());
			System.out.println("Response status is: "+status);
			if(status==HttpStatus.SC_SEE_OTHER){
				System.out.println("\nResponse to URL: "+url);
				System.out.println(IOUtils.toString(con.getInputStream()));
			}else{
				System.err.println("\nNo file reachable at: "+url);
			}
			//con.setRequestProperty("Content-Type", "application/json");
			//con.setConnectTimeout(5000);
			//con.setReadTimeout(5000)

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error: ",e);
			throw e;
		}
	}


	/**
	 * Gets the content reponse.
	 *
	 * @param is the is
	 * @return the content reponse
	 */
	public static String getContentReponse(InputStream is){
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String inputLine;
		StringBuffer content = new StringBuffer();
		try {
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(in!=null)
				try {
					in.close();
				}
				catch (IOException e) {
					//silent
				}
		}
		return content.toString();
	}

	/**
	 * Prints the headers.
	 *
	 * @param map the map
	 */
	public static void printHeaders(Map<String, List<String>> map){

		System.out.println("Printing Response Header...");
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey()  + " ,Value : " + entry.getValue());
		}
	}


	/**
	 * Gets the filename.
	 *
	 * @param map the map
	 * @return the filename
	 */
	public static String getFilename(Map<String, List<String>> map){

		String fileName = "filename";
		List<String> contentDispValue = map.get("Content-Disposition");
		if(contentDispValue==null)
			return fileName;

		System.out.println("Printing content-disposition from Response Header...");
		for (String value : contentDispValue) {
			System.out.println(value);
			int start = value.indexOf("\"");
			if(start>=0){
				int end = value.lastIndexOf("\"");
				fileName = value.substring(start+1,end).trim();
			}
		}

		System.out.println("Filename is: "+fileName);
		return fileName;
	}

	/**
	 *
	 */
	private static final String ORG_GCUBE_PORTLETS_USER_DATAMINERMANAGER_SERVER_DATA_MINER_MANAGER_SERVICE_IMPL =
		"org.gcube.portlets.user.dataminermanager.server.DataMinerManagerServiceImpl";
	/**
	 *
	 */
	private static final String APPLICATION_PROFILE = "ApplicationProfile";

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		ApplicationProfileReader reader = new ApplicationProfileReader("/gcube/preprod/preVRE", APPLICATION_PROFILE, ORG_GCUBE_PORTLETS_USER_DATAMINERMANAGER_SERVER_DATA_MINER_MANAGER_SERVICE_IMPL, false);
		System.out.println(reader);
	}
}
