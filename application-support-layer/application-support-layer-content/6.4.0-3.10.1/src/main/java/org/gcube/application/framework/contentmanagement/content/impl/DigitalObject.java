package org.gcube.application.framework.contentmanagement.content.impl;

import gr.uoa.di.madgik.rr.ResourceRegistryException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalObject {
	
	/* The logger. */
	private static final Logger logger = LoggerFactory.getLogger(DigitalObject.class);
	
	/* The ASLSession that the current object belongs to. */
	private ASLSession session = null;
	
	/* The access logger. */
	private AccessLogger accessLogger = AccessLogger.getAccessLogger();
	
	/* The collectionID where the object resides. */
	private String colID;
	
	/* The reader of the object's tree. */
	private TReader treader = null;
	
	/* The URI of the specific tree inside the tree collection. */
	private String objectURI;
	
	/* The Tree representation of the specific object. */
	//private Tree objectTree;
	
	/* The type of the specific tree. */
	private DigitalObjectType objectType = null;
	
	/* The scope where the tree belongs. */
	private String objectScope;
	
	/* The ID of the specific object inside the tree collection. */
	private String objectID;
	
	/* The content of a specific tree. */
	private String objectContent = null;
	
	/* The HTML representation of the tree object. */
	private String HTMLRepresentation = null;
	
	/* The title of the tree object. */
	private String title = null;
	
	/* The mime type of the tree object. */
	private String mimeType = "XML_URL";
	
	/* The name of the collection the tree belongs to. */
	private String collectionName;

	/* The rank of the tree object. */
	private String rank;
	
	/**
	 * @param session the ASL session to be used for retrieving information needed
	 * @param objectURI the object URI
	 * @param colID the collection identifier
	 */
	public DigitalObject(ASLSession session, String objectURI, String colID) {
		
		logger.info("The objectURI(1) is: " + objectURI);

		this.objectURI = objectURI; //Store the object's URI.
		this.session = session;
		
		this.colID = colID;
		logger.info("The collectionID(1) of the object is: " + colID);
		
		//this.collectionName = Collection.getCollectionName(colID);
		try {
			this.collectionName = Collection.getCollectionNameByID(this.colID);
		}
		catch (ResourceRegistryException e1) {
			logger.error("The Digital Object cannot be created, because of the exception: ", e1);
			return;
		}
		logger.info("The collectionName(1) of the object is: " + collectionName);
		
		if(objectURI != null && objectURI.contains("/tree/")) {
			//The URI refers to a collection came from the Tree Manager.
			URI uri = null;
			
			try {
				uri = new URI(objectURI);
			}
			catch (URISyntaxException e1) {
				logger.error("The Digital Object cannot be created, because of the exception: ", e1);
				return;
			}
			
			this.objectScope = uri.getQuery().split("=")[1];
			logger.info("The scope(1) of the object is: " + objectScope);
			
			this.objectID = uri.getPath().split("/")[3];
			logger.info("The ID(1) of the object is: " + objectID);
				
			/*ScopeProvider.instance.set(objectScope);
			StatefulQuery query = TServiceFactory.readSource().withId(colID).build();
			treader = TServiceFactory.reader().matching(query).build();
			try {
				objectTree = treader.get(objectID);
			}
			catch (UnknownTreeException e) {
				logger.error("Exception:", e);
			}*/
		}
		else{
			this.objectType = DigitalObjectType.Generic;
		}
	}
	
	/**
	 * @param session the ASL session to be used for retrieving information needed
	 * @param objectURI the object URI
	 * @param colID the collection identifier
	 */
	public DigitalObject(ASLSession session, String objectURI) {
		
		logger.info("The objectURI is: " + objectURI);
		
		this.objectURI = objectURI; //Store the object's URI.
		this.session = session;
		
		if(objectURI != null && objectURI.contains("/tree/")) {
			//The URI refers to a collection came from the Tree Manager.
			URI uri = null;
			
			try {
				uri = new URI(objectURI);
			}
			catch (URISyntaxException e1) {
				logger.error("The Digital Object cannot be created, because of the exception: ", e1);
				return;
			}
			
			this.objectScope = uri.getQuery().split("=")[1];
			logger.info("The scope of the object is: " + objectScope);

			this.objectID = uri.getPath().split("/")[3];
			logger.info("The ID of the object is: " + objectID);
			
			this.colID = uri.getPath().split("/")[2];
			logger.info("The collectionID of the object is: " + colID);
			
			//this.collectionName = Collection.getCollectionName(colID, objectScope);
			try {
				this.collectionName = Collection.getCollectionNameByID(this.colID);
			}
			catch (ResourceRegistryException e1) {
				logger.error("The Digital Object cannot be created, because of the exception: ", e1);
				return;
			}
			logger.info("The collectionName of the object is: " + collectionName);
			
			/*ScopeProvider.instance.set(objectScope);
			StatefulQuery query = TServiceFactory.readSource().withId(colID).build();
			treader = TServiceFactory.reader().matching(query).build();
			try {
				objectTree = treader.get(objectID);
			}
			catch (UnknownTreeException e) {
				logger.error("The tree of the Digital Object cannot be created, because of the exception: ", e);
			}*/
		}
		else{
			this.objectType = DigitalObjectType.Generic;
		}
	}
	
	public String getURI() {
		return objectURI;
	}
	
	public String getObjectId() {
		return objectID;
	}
	
	public String getCollectionName() {
		return collectionName;
	}
	
	public String getCollectionID() {
		return colID;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getHTMLrepresentation() {
		return HTMLRepresentation;
	}
	
	public void setHTMLRepresentation(String representation) {
		HTMLRepresentation = representation;
	}
	
	public long getLength() {
		return 0;
	}
	
	public void setRank(String rank) {
		this.rank = rank;
	}
	
	public String getRank() {
		//TODO - Currently, rank is not implemented for the Tree Manager.
		return "";
	}
	
	public String getContent() throws IllegalStateException, IOException {
		URL url;
		HttpURLConnection connection = null;
		
		/* If the content has already been stored. */
		if(objectContent != null) {
			logger.info("The content of the object is: " + objectContent);
			return objectContent;
		}
		
		//ScopeProvider.instance.set(session.getScopeName());
		//ScopeProvider.instance.set(objectScope);
		
		// Create connection
		//url = new URL(objectTree.uri().toURL().toString());
		url = new URL(objectURI);
		
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		// Get Response
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuffer response = new StringBuffer();
		
		String line = null;
		while ((line = rd.readLine()) != null) {
			response.append(line + '\r');
		}
		rd.close();
		
		objectContent = response.toString();

		if (connection != null)
			connection.disconnect();
		
		return objectContent;
	}
	
	public void setContent(String content) {
		objectContent = content;
	}
	
	/* public String getTreeManagerServices() {
		XQuery query = ICFactory.queryFor(ServiceInstance.class);
		query.addCondition("$resource/Data/gcube:ServiceClass/text() eq 'DataAccess'").
              addCondition("$resource/Data/gcube:ServiceName/text() eq 'tree-manager-service'");
		
		DiscoveryClient<ServiceInstance> client = ICFactory.clientFor(ServiceInstance.class);
		
		List<ServiceInstance> props = client.submit(query);
		StringBuilder builder = new StringBuilder("\n");
		
		for(ServiceInstance si:props)
			builder.append(si.toString() + "\n");
		
		//System.out.println(props);
		return builder.toString();
	}
	
	public String someServiceInstanceProperties() {
		XQuery query = ICFactory.queryFor(ServiceInstance.class);
		query.addNamespace("tm",URI.create("http://gcube-system.org/namespaces/data/tm")).
			  addCondition("$resource/Data/tm:Plugin/name/text() eq 'species-tree-plugin'");
		
		DiscoveryClient<ServiceInstance> client = ICFactory.clientFor(ServiceInstance.class);
		
		List<ServiceInstance> props = client.submit(query);
		
		StringBuilder builder = new StringBuilder("\n");
		for(ServiceInstance si:props)
			builder.append(si.id() + "\n");
		
		//System.out.println(props);
		return builder.toString();
	} */
	
	public DigitalObjectType getType() {
		
		/* If the type has already been stored. */
		if(objectType != null) {
			logger.info("The type of the object is: " + objectType);
			return objectType;
		}
		
		String type = "";
		
		//ScopeProvider.instance.set(session.getScopeName());
		ScopeProvider.instance.set(objectScope);
		
		XQuery query = ICFactory.queryFor(ServiceInstance.class);
		query.addNamespace("tm",URI.create("http://gcube-system.org/namespaces/data/tm"))
			  .addCondition("$resource/Data/gcube:ServiceClass/text() eq 'DataAccess'")
              .addCondition("$resource/Data/gcube:ServiceName/text() eq 'tree-manager-service'")
              .addCondition("$resource/Data/tm:SourceId/text() eq '" + colID + "'")
              .addVariable("$type", "$resource/Data/tm:Type")
              .setResult("local-name-from-QName(resolve-QName($type/text(),$type))");
		
		DiscoveryClient<String> client = ICFactory.client();
		try {
			Set<String> props = new HashSet<String>(client.submit(query));
			if((props!=null) && (!props.isEmpty()))
				type = props.iterator().next();
			else
				type = "";
		}
		catch (Exception e) {
			logger.error("It's either not a tree type or a known tree type, setting it to Generic...");
			objectType = DigitalObjectType.Generic;
			return objectType;
		}
		
		try {
			if((type!=null) && (type!="")){
				logger.info("Setting the Type of the object to: " + type);
				objectType = DigitalObjectType.valueOf(type);
			}
		}
		catch(NullPointerException ex) {
			logger.debug("Type " + type +" is not a known DigitalObjectType, setting it to Generic!");
			objectType = DigitalObjectType.Generic;
		}
		catch(IllegalArgumentException ex) {
			logger.debug("Type " + type +" is not a known DigitalObjectType, setting it to Generic!");
			objectType = DigitalObjectType.Generic;
		}
		return objectType;
	}
	
	//TODO: check caller!
	public void setType(String type) {
		logger.debug("Setting objectType: "+ type);
		objectType = DigitalObjectType.valueOf(type);
	}
	
	//TODO: check caller!
	public static DigitalObject createNewDigitalObject(String colID, InputStream content, String objectID, ASLSession session, String mimeType) throws IOException {
		
		BufferedReader breader = new BufferedReader(new InputStreamReader(content));
		StringBuilder cont = new StringBuilder();
		
		String inputLine = null;
		while((inputLine = breader.readLine()) != null)
			cont.append(inputLine);
		
		breader.close();
		
		DigitalObject newObject = new DigitalObject(session, objectID, colID);
		newObject.setMimeType(mimeType);
		newObject.setContent(cont.toString());
		
		return newObject;
	}
	
	public InputStream getObject() throws URISyntaxException, IOException {
		InputStream istream = null;
		
		//ScopeProvider.instance.set(session.getScopeName());
		//ScopeProvider.instance.set(objectScope);
		
		// Create connection
		URL url = new URL(new URI(objectURI).toURL().toString());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		
		istream = connection.getInputStream();
		
		return istream;
	}
	
	public static String getContent(String objectID, String scope) throws URISyntaxException, IOException {
		
		//ScopeProvider.instance.set(scope);
		
		// Create connection
		URL url = new URL(new URI(objectID).toURL().toString());
		HttpURLConnection connection = null;
		
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		// Get Response
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuffer response = new StringBuffer();
		
		String line = null;
		while ((line = rd.readLine()) != null) {
			response.append(line + '\r');
		}
		rd.close();

		if (connection != null)
			connection.disconnect();
		
		return response.toString();
	}
}





	
	
