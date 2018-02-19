package org.gcube.common.couchdb.connector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessControlException;

import javax.xml.bind.DatatypeConverter;

import org.gcube.common.couchdb.connector.exceptions.ObjectNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HttpCouchClient {

	private static Logger logger = LoggerFactory.getLogger(HttpCouchClient.class);
	
	public static final String ID = "id";
	
	private String endpoint;
	private String user;
	private String passwd;

	public HttpCouchClient(String host, String dbName, String user, String passwd){
		this.passwd = passwd;
		this.user = user;
		this.endpoint = generateEndpoint(host, dbName);
	}

	private String generateEndpoint(String host, String dbName){
		if (!host.endsWith("/"))
			host= host+"/";
		if(host.startsWith("http://") || host.startsWith("https://")){
			return host+dbName;
		}
		return "http://"+host+dbName;
	}
	
	public String getDoc(String docId) throws Exception{
		String callUrl = endpoint+"/"+docId;
		System.out.println(callUrl);
		URL url = new URL(callUrl);
		HttpURLConnection connection = makeRequest(url, "GET");
		checkStatus(connection.getResponseCode());

		try(BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)connection.getContent()));){
			StringBuilder result = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) 
				result.append(line);
			return result.toString();
		}
	}

	
	public String getAllDocs(String startKey, String endKey, Integer limit) throws Exception{
		String startKeyQuery= "";
		String endKeyQuery= "";
		String limitQuery="";
		if (startKey!=null){
			startKeyQuery=String.format("startkey=\"%s\"",startKey);
			if (endKey!=null)
				endKeyQuery=String.format("&endkey=\"%s\"",endKey);
			if (limit!=null && limit>0)
				limitQuery=String.format("&limit=\"%i\"",limit);
				
		}
		String callUrl = !startKeyQuery.isEmpty()||!endKeyQuery.isEmpty()||!limitQuery.isEmpty()?
				endpoint+"/_all_docs?"+startKeyQuery+endKeyQuery+limitQuery:
					endpoint+"/_all_docs";
		
		URL url = new URL(callUrl);
		HttpURLConnection connection = makeRequest(url, "GET");
		checkStatus(connection.getResponseCode());

		try(BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)connection.getContent()))){
			StringBuilder result = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) 
				result.append(line);
			return result.toString();
		}
	}

	public String getAllDocs() throws Exception{
		return getAllDocs(null, null, null);
	}

	public String getFilteredDocs(String designName, String viewName, String ... keyValues) throws Exception{
		
		String key = "";
		if (keyValues !=null && keyValues.length>0){
			StringBuffer keyBuilder = new StringBuffer("[");
			for (String value : keyValues)
				keyBuilder.append("\"").append(value).append("\"").append(",");
			keyBuilder.deleteCharAt(keyBuilder.lastIndexOf(",")).append("]");
			key= keyBuilder.toString();
		} else key = "\""+((keyValues==null ||  keyValues.length==0)?null:keyValues[0])+"\"";
		
		String callUrl = endpoint+"/_design/"+designName+"/_view/"+viewName+"?&keys="+key+"";
		
		logger.trace(callUrl);
		
		URL url = new URL(callUrl);
		
		HttpURLConnection connection = makeRequest(url, "GET");
		connection.setDoInput(true);
		checkStatus(connection.getResponseCode());

		try(BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)connection.getContent()))){
			StringBuilder result = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) 
				result.append(line);
			return result.toString();
		}
	}
	
	public void put(String json, String docId) throws Exception{
		String callUrl = endpoint+"/"+docId;
		URL url = new URL(callUrl);
		logger.trace(callUrl);
		HttpURLConnection connection = makeRequest(url, "PUT");
		connection.setDoOutput(true);
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))){
			writer.append(json);
		}
		checkStatus(connection.getResponseCode());
	}
	
	public static String getRevision(String jsonString){
		logger.trace("Getting revison from {}", jsonString);
		String[] strings = jsonString.split(",");
		String rev = "";
		for(String s : strings){
			if(s.contains("\"_rev\":\"")){
				String sanitized = s.replace("\"_rev\":\"", "");
				sanitized = sanitized.replace("{\"_rev\":\"", "");
				rev = sanitized.replace("\"", "");
			}
			//logger.trace(s);
		}
		
		logger.trace("Rev {}", rev);
		return rev;
	}
	
	public void delete(String id, String revision) throws Exception{
		if (id==null && revision==null ) {
			throw new IllegalArgumentException("entity not valid");
		}
		String callUrl = endpoint+"/"+id+"?rev="+revision;
		URL url = new URL(callUrl);
		HttpURLConnection connection = makeRequest(url, "DELETE");
		checkStatus(connection.getResponseCode());
	}

	private HttpURLConnection makeRequest(URL url, String method) throws Exception{
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod(method);
		String userCredential = user+":"+passwd;
		String basicAuth = "Basic " + new String(DatatypeConverter.printBase64Binary(userCredential.getBytes()));
		connection.setRequestProperty ("Authorization", basicAuth);
		return connection;
	}
	
	private void checkStatus(int statusCode) throws Exception{
		switch (statusCode) {
			case 200:
			case 201:
				return;	 
			case 401: throw new AccessControlException("wrong username or password");
			case 404: throw new ObjectNotFound();
			case 409: throw new Exception("conflict occurred saving entry");
			default:
				throw new Exception("error contacting couch Db: response code is "+statusCode);
		}
	}
	
}
