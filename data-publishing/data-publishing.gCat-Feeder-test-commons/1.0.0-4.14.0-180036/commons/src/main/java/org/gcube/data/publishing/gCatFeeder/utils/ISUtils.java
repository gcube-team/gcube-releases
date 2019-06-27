package org.gcube.data.publishing.gCatFeeder.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ISUtils {

	private static final Logger log= LoggerFactory.getLogger(ISUtils.class);


	private static DocumentBuilder builder; 
	private static DocumentBuilderFactory factory; 


	static {

		factory= DocumentBuilderFactory.newInstance();
		try{
			builder= factory.newDocumentBuilder();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}


	}


	public static List<ServiceEndpoint> queryForServiceEndpoints(String category, String platformName){
		log.debug("Querying for Service Endpoints [category : {} , platformName : {}, currentScope : {} ]",category,platformName,ContextUtils.getCurrentScope());

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+platformName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query);
	}

	public static List<ServiceEndpoint> queryForServiceEndpointsByName(String category, String name){
		log.debug("Querying for Service Endpoints [category : {} , name : {}, currentScope : {} ]",category,name,ContextUtils.getCurrentScope());

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'")
		.addCondition("$resource/Profile/Name/text() eq '"+name+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query);
	}

	public static List<GCoreEndpoint> queryForGCoreEndpoint(String serviceClass,String serviceName){
		log.debug("Querying for GCore Endpoints [ServiceClass : {} , ServiceName : {}, currentScope : {} ]",serviceClass,serviceName,ContextUtils.getCurrentScope());


		SimpleQuery query =queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+serviceClass+"'")
		.addCondition("$resource/Profile/ServiceName/text() eq '"+serviceName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		return client.submit(query);
	}


	public static List<GenericResource> queryForGenericResources(String name,String secondaryType) {
		log.debug("Querying for Generic Resource [Name : {} , SecondaryType : {}, currentScope : {} ]",secondaryType,name,ContextUtils.getCurrentScope());


		SimpleQuery query =queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq '"+secondaryType+"'")
		.addCondition("$resource/Profile/Name/text() eq '"+name+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		return client.submit(query);

	}

	public static Map<String,String> loadConfiguration(){
		HashMap<String,String> toReturn=new HashMap<>();
		try {
			String confXML= ISUtils.queryForGenericResources("gcat-feeder", "configuration").get(0).profile().bodyAsString();
			confXML=confXML.replaceAll(" ", "");

			Document xml = convertStringToDocument(confXML);
			Node user = xml.getFirstChild();
			NodeList childs = user.getChildNodes();
			Node child;
			for (int i = 0; i < childs.getLength(); i++) {
				child = childs.item(i);
				System.out.println(child.getNodeName());
				System.out.println(child.getTextContent());
				toReturn.put(child.getNodeName(), child.getTextContent());
			}
		}catch(Throwable t) {
			log.warn("Unable to load IS configuration",t);
		}
		return toReturn;
	}


	private static Document convertStringToDocument(String xmlStr) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
