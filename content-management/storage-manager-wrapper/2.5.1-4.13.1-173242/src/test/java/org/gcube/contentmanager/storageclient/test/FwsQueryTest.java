package org.gcube.contentmanager.storageclient.test;

import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
import static org.gcube.resources.discovery.icclient.ICFactory.*;
import java.util.ArrayList;
import java.util.List;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageclient.wrapper.ISClientConnector;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FwsQueryTest {
	
	String scope="/gcube/devsec";
	
	@Before
	public void setscope(){
		ScopeProvider.instance.set(scope);
	}
	
//	@Test
	public void getServerFws(){
		SimpleQuery query = queryFor(GenericResource.class);
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> resources = client.submit(query);
		List<GenericResource> filteredResources = new ArrayList<GenericResource>();
		for (GenericResource gr:resources) {
			String serviceName = gr.profile().name();
			if (serviceName.equalsIgnoreCase("MongoDBServer")) {
				XPathHelper helper = new XPathHelper(gr.profile().body());
				List<String> list= helper.evaluate("server_list/server/@ip");
				String[] server=new String[list.size()];
				int i=0;
				for(String s : list){
					System.out.println("string founded: "+s);
					server[i]=s;
					i++;
				}
				break;
			}
		}
	}
	
	@Test
	public void getServerFwsWithoutXPATh(){
		SimpleQuery query = queryFor(GenericResource.class);
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> resources = client.submit(query);
		List<GenericResource> filteredResources = new ArrayList<GenericResource>();
		String[] server=new String[3];
		for (GenericResource gr:resources) {
			String serviceName = gr.profile().name();
			if (serviceName.equalsIgnoreCase("MongoDBServer")) {
				
				Element body=gr.profile().body();
				NodeList nodes=body.getChildNodes();
				Node node=nodes.item(0);
				NodeList serversNode = node.getChildNodes();
				int i=0;
				Node s=serversNode.item(i);
				while(s!=null){
					NamedNodeMap maps=s.getAttributes();
					if(maps==null)
						break;
					Node ip1=maps.getNamedItem("ip");
					server[i]=ip1.getNodeValue();
					i++;
					s=serversNode.item(i);
				}
				
				
	
				for(int j=0; j<server.length; j++){
					System.out.println("s" +j+" = "+server[j]);
				}
				break;
			}
		}
	}
	
	@Test
	public void getRR() throws Exception{
		System.out.println("retrieve server from RuntimeResource");
		ISClientConnector isConnector= new ISClientConnector();
		ServiceEndpoint resource = isConnector.getStorageEndpoint(scope);
		String[] server=isConnector.retrieveConnectionInfo(resource);
		for(int j=0; j<server.length; j++){
			System.out.println("s" +j+" = "+server[j]);
		}
		
	}

}






