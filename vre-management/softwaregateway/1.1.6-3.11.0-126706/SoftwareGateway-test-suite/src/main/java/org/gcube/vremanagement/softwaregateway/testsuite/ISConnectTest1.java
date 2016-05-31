package org.gcube.vremanagement.softwaregateway.testsuite;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEServiceQuery;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.scope.GCUBEScope;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ISConnectTest1 {
	
	ISClient isClient;
	GCUBEScope scope;
	String  [] server;
	
  @Before
  public void before() throws Exception{
	scope = GCUBEScope.getScope("/gcube/devsec");
	isClient = GHNContext.getImplementation(ISClient.class);
	GCUBEServiceQuery query = isClient.getQuery(GCUBEServiceQuery.class);
//	query.addGenericCondition("count($result//MavenCoordinates[./groupId/string()='org.gcube.execution' and ./artifactId/string()='rrmodel' and ./version/string()='1.2.1' ])>0");
	query.addAtomicConditions(new AtomicCondition("//Profile/Name","ContentManager"));
	query.addAtomicConditions(new AtomicCondition("//Profile/Class","ContentManagement"));
	query.addAtomicConditions(new AtomicCondition("//Profile/Version","1.0.0"));
	for (GCUBEService resource:isClient.execute(query, scope)){
		System.out.println(resource.getServiceClass());
		System.out.println(resource.getServiceName());
		List<Package> listP=resource.getPackages();
		//I assume that the first package in the list of packages is the main package			
		for(Package p : listP){
			System.out.println("founded package with pn: "+p.getName()+" pv "+p.getVersion());
		}
	}
//	System.out.println("query: "+query.getExpression());
  }
	
	private String[] parseXmlFile(InputSource body){
		String[] list=null;
		try{
		//get the factory
		  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		  DocumentBuilder db = dbf.newDocumentBuilder();
		  Document doc = db.parse(body);
		  doc.getDocumentElement().normalize();
		  NodeList nodeLst = doc.getElementsByTagName("server_list");
		  int i=0;
		  list=new String[nodeLst.getLength()];
		  for (int s = 0; s < nodeLst.getLength(); s++) {
		    Node fstNode = nodeLst.item(s);
		    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
		      Element fstElmnt = (Element) fstNode;
		      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("server");
		      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
		      String ip=fstNmElmnt.getAttribute("url");
//		      String port=fstNmElmnt.getAttribute("port");
		      list[i]=ip;
		      i++;
		    }

		  }
		} catch (Exception e) {
		   e.printStackTrace();
		}
		return list;
	}
	
	@Test
	public void showServerList(){
//		for(int i=0;i<server.length; i++){
//			System.out.println("server "+i+" = "+server[i]);
//		}
		System.out.println("executed");
	}

}
