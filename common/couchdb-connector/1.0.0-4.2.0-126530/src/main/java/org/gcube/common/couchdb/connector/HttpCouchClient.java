package org.gcube.common.couchdb.connector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;



public class HttpCouchClient {

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

	
	public <T> T getDoc(String docId, Class<T> _class) throws Exception{
		String callUrl = endpoint+"/"+docId;
		System.out.println(callUrl);
		URL url = new URL(callUrl);
		HttpURLConnection connection = makeRequest(url, "GET");
		checkStatus(connection.getResponseCode());

		try(InputStream is = (InputStream)connection.getContent()){
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(is, _class);
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

	public <T> List<T> getFilteredDocs(Class<T> _class, String designName, String viewName, String ... keyValues) throws Exception{
		
		String key = "";
		if (keyValues !=null && keyValues.length>0){
			StringBuffer keyBuilder = new StringBuffer("[");
			for (String value : keyValues)
				keyBuilder.append("\"").append(value).append("\"").append(",");
			keyBuilder.deleteCharAt(keyBuilder.lastIndexOf(",")).append("]");
			key= keyBuilder.toString();
		} else key = "\""+keyValues==null?null:keyValues[0]+"\"";
		
		String callUrl = endpoint+"/_design/"+designName+"/_view/"+viewName+"?key="+key+"";
		
		URL url = new URL(callUrl);
		HttpURLConnection connection = makeRequest(url, "GET");
		checkStatus(connection.getResponseCode());

		List<T> toReturn = new ArrayList<T>();
		
		try(InputStream is = (InputStream)connection.getContent()){
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode =  mapper.readTree(is);
			ArrayNode rows = (ArrayNode)rootNode.get("rows") ;
			Iterator<JsonNode> rowsIterator =  rows.iterator();			
			while (rowsIterator.hasNext())
				toReturn.add(mapper.readValue(rowsIterator.next().get("value"), _class));
		}
		return toReturn;
	}
	
	public void put(String json, String docId) throws Exception{
		String callUrl = endpoint+"/"+docId;
		URL url = new URL(callUrl);
		HttpURLConnection connection = makeRequest(url, "PUT");
		connection.setDoOutput(true);
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))){
			writer.append(json);
		}
		checkStatus(connection.getResponseCode());
	}
	
	public void put(Entity entity) throws Exception{
		if (entity.get_id()==null) throw new IllegalArgumentException("entity id not valid");
		String callUrl = endpoint+"/"+entity.get_id();
		URL url = new URL(callUrl);
		HttpURLConnection connection = makeRequest(url, "PUT");
		connection.setDoOutput(true);
			
		try(OutputStream os = connection.getOutputStream()){
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(os, entity);
		}
		checkStatus(connection.getResponseCode());
	}
	
	public void delete(Entity entity) throws Exception{
		if (entity.get_id()==null && entity.get_rev()==null ) throw new IllegalArgumentException("entity not valid");
		String callUrl = endpoint+"/"+entity.get_id()+"?rev="+entity.get_rev();
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
		case 409: throw new Exception("conflict occurred saving entry");
		default:
			throw new Exception("error contacting couch Db: response code is "+statusCode);
		}
			
		
	}
	
}
