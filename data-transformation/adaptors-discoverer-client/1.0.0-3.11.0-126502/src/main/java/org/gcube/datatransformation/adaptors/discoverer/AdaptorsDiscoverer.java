package org.gcube.datatransformation.adaptors.discoverer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gcube.datatransformation.adaptors.common.db.discover.DBPropsDiscoverer;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.datatransformation.adaptors.common.tree.discover.TreeResourceDiscoverer;
import org.gcube.datatransformation.adaptors.common.xmlobjects.TreeResource;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import static org.gcube.resources.discovery.icclient.ICFactory.*;


public class AdaptorsDiscoverer {

	
	private String scope;
	private DBPropsDiscoverer dbDiscoverer;
	private TreeResourceDiscoverer treeDiscoverer;
	
	
	public static void main (String [] args) throws IOException{
		
//		AdaptorsDiscoverer d = new AdaptorsDiscoverer("/gcube/devNext");

		/*
		//discover db endpoints
		List<String> dbeps = d.discoverAllDBEps();
		System.out.println(dbeps);
		*/
		
		/*
		List<String> dbeps1 = d.discoverDBEps("http://dionysus.di.uoa.gr");
		System.out.println(dbeps1);
		*/
		
//		List<String> dbeps2 = d.discoverAllDBEps("DionysusDB", "CountriesTree");
//		System.out.println(dbeps2);
		
		
		//discover tree endpoints
		
	}
	
	public AdaptorsDiscoverer (String scope){
		this.scope = scope;
	}
	
	
	
	public List<String> discoverTreeEps() throws IOException{
		treeDiscoverer = new TreeResourceDiscoverer(new RIDiscovererISimpl(), new ResourceHarvester<TreeResource>());
		List <String> endpoints = new ArrayList<String>();
		for(String ept : treeDiscoverer.discoverTreeServiceRunningInstances(scope))
			for(String treeID : queryForTreeProps(ept))
				endpoints.add(ept + "/HarvestTreeCollection?treeCollectionID=" + treeID);
		return endpoints;
	}
	
	private List<String> queryForTreeProps(String endpoint) throws IOException{

		List<String> output = new ArrayList<String>();
		
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//		HttpGet httpget = new HttpGet(endpoint + "/AvailableResources");
//		httpget.addHeader("gcube-scope", "/gcube/devNext");
//		HttpResponse response = httpclient.execute(httpget);
//		
//		//if it has failed for any reason, return the empty 
//		if(response.getStatusLine().getStatusCode() != 200)
//			return output;
//		
//		HttpEntity entity = response.getEntity();
//		
//		
//		try {
//			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
//			NodeList resources = doc.getElementsByTagName("resource");
//			for (int i = 0; i < resources.getLength(); i++) {
//				Node nNode = resources.item(i);
////				System.out.println("\nCurrent Element :" + nNode.getNodeName());
//				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//					Element eElement = (Element) nNode;
////					System.out.println("resourceid : " + eElement.getElementsByTagName("resourceid").item(0).getTextContent());
////					System.out.println("sourcename : " + eElement.getElementsByTagName("sourcename").item(0).getTextContent());
////					System.out.println("propsname : " + eElement.getElementsByTagName("propsname").item(0).getTextContent());
//					output.add(new Info(eElement.getElementsByTagName("sourcename").item(0).getTextContent(), 
//										eElement.getElementsByTagName("propsname").item(0).getTextContent()));
//				}
//			}
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (SAXException e) {
//			e.printStackTrace();
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		}
		return output;
	}
	
	
	
	

	public List<String> discoverAllDBEps() throws IOException{
		dbDiscoverer = new DBPropsDiscoverer(new RIDiscovererISimpl(), new ResourceHarvester<DBProps>());
		List <String> endpoints = new ArrayList<String>();
		for(String ept : dbDiscoverer.discoverDBServiceRunningInstances(scope))
			for(Info info : queryForDBProps(ept))
				endpoints.add(ept + "/HarvestDatabase?sourcename=" + info.sourcename + "&propsname=" + info.propsname);
		return endpoints;
	}
	
	
	public List<String> discoverDBEps(String hostUri) throws IOException{
		dbDiscoverer = new DBPropsDiscoverer(new RIDiscovererISimpl(), new ResourceHarvester<DBProps>());
		URL hostUrl;
		try{
			hostUrl = new URL(hostUri);
		}catch(MalformedURLException e){
			throw new MalformedURLException("The provided host is not an understandable URL");
		}	
		List <String> endpoints = new ArrayList<String>();
		try{
			for(String ept : dbDiscoverer.discoverDBServiceRunningInstances(scope)){
				URL eptUrl = new URL(ept);
				if(eptUrl.getHost().equalsIgnoreCase(hostUrl.getHost()))
					for(Info info : queryForDBProps(ept))
						endpoints.add(ept + "/HarvestDatabase?sourcename=" + info.sourcename + "&propsname=" + info.propsname);
			}
		}catch(MalformedURLException malformed){
			throw new MalformedURLException("Some of the discovered endpoints do not have an understandable URL");
		}
		return endpoints;
	}
	
	
	
	public List<String> discoverAllDBEps(String sourcename, String propsname) throws IOException {
		dbDiscoverer = new DBPropsDiscoverer(new RIDiscovererISimpl(), new ResourceHarvester<DBProps>());
		List<String> endpoints = new ArrayList<String>();
		for (String ept : dbDiscoverer.discoverDBServiceRunningInstances(scope)){
			for (Info info : queryForDBProps(ept)){
				if(info.getSourcename().equals(sourcename) && info.getPropsname().equals(propsname))
					endpoints.add(ept + "/HarvestDatabase?sourcename=" + info.sourcename + "&propsname=" + info.propsname);
			}
		}
		return endpoints;
	}
	
	
	public List<Info> queryForDBProps(String endpoint) throws IOException{

		List<Info> output = new ArrayList<Info>();
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(endpoint + "/AvailableResources");
		httpget.addHeader("gcube-scope", scope);
		HttpResponse response = httpclient.execute(httpget);
		
		//if it has failed for any reason, return the empty
		if(response.getStatusLine().getStatusCode() != 200)
			return output;
		
		HttpEntity entity = response.getEntity();
		
		
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
			NodeList resources = doc.getElementsByTagName("resource");
			for (int i = 0; i < resources.getLength(); i++) {
				Node nNode = resources.item(i);
//				System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					output.add(new Info(eElement.getElementsByTagName("sourcename").item(0).getTextContent(), 
										eElement.getElementsByTagName("propsname").item(0).getTextContent()));
				}
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	class Info {
		
		private String sourcename;
		private String propsname;
		
		public Info(String sourcename, String propsname){
			this.sourcename = sourcename;
			this.propsname = propsname;
		}
		
		public String getSourcename() {
			return sourcename;
		}
		public void setSourcename(String sourcename) {
			this.sourcename = sourcename;
		}
		public String getPropsname() {
			return propsname;
		}
		public void setPropsname(String propsname) {
			this.propsname = propsname;
		}
		
		
	}
	
	
//	/**
//	 * This is by using the old way to contact the IS, prefer the other one
//	 * 
//	 * @return a map of {DB urls , list of endpoints (urls) appended with different configurations}
//	 * @throws IOException 
//	 */
//	public Map<String, List<String>> discoverDBEps1() throws IOException{
//		ScopeProvider.instance.set(scope);
//		SimpleQuery query = queryFor(GCoreEndpoint.class);
//		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
////		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+ConstantNames.SERVICE_CLASS+"'");
////		query.addCondition("$resource/Profile/ServiceName/text() eq '"+ConstantNames.SERVICE_NAME_DB+"'");
//		List<GCoreEndpoint> resources = client.submit(query);
//		HashMap<String, List<String>> dbEps = new HashMap<String, List<String>>();
//		for(GCoreEndpoint res : resources){
//			if(res.profile().serviceClass().equals(ConstantNames.SERVICE_CLASS) && res.profile().serviceName().equals(ConstantNames.SERVICE_NAME_DB)){
//				List<String> eps = new ArrayList<String>();
//				for(Endpoint endpoint : res.profile().endpoints().toArray(new Endpoint[0])){
//					if(endpoint.name().equals(ConstantNames.ENDPOINT_KEY)){
//						//query it and get associated sources and properties
//						for(Info info : queryForProps(endpoint.uri().toString()))
//							eps.add(endpoint.uri().toString()+"/HarvestDatabase?sourcename="+info.sourcename+"&propsname="+info.propsname);
//					}
//				}
//				if(!eps.isEmpty())
//					dbEps.put(res.id(), eps);
//			}
//		}
//		return dbEps;
//	}
	
	
}
