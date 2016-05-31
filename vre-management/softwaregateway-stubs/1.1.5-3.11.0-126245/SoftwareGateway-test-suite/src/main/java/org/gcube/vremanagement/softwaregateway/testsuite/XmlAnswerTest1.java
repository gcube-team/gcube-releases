package org.gcube.vremanagement.softwaregateway.testsuite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEServiceQuery;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.junit.Before;
import org.junit.Test;

public class XmlAnswerTest1 {

	StringBuilder xml;
	List<GCubeCoordinates> resolved;
	List<GCubeCoordinates> missing;
	ISClient isClient;
	GCUBEScope scope;
	String  [] server;


//	@Before
	public void before1() throws BadCoordinatesException  {
		 xml=new StringBuilder();
		 resolved= new ArrayList<GCubeCoordinates>();
		 resolved.add(new GCubeCoordinates("class1", "name1", "v1", "pn1", "pv1"));
		 resolved.add(new GCubeCoordinates("class2", "name2", "v2", "pn2", "pv2"));
		 resolved.add(new GCubeCoordinates("class3", "name3", "v3", "pn3", "pv3"));
		 missing= new ArrayList<GCubeCoordinates>();
		 missing.add(new GCubeCoordinates("class1", "name1", "v1", "pn1", "pv1"));
		 missing.add(new GCubeCoordinates("class2", "name2", "v2", "pn2", "pv2"));
		 missing.add(new GCubeCoordinates("class3", "name3", "v3", "pn3", "pv3"));
	}
	
//	@Test
	public void testXMLDependencies(){

			xml.append("<DependencyResolutionReport>\n");
			xml.append("\t<ResolvedDependencies>\n");
			for(Iterator<GCubeCoordinates> it=resolved.iterator(); it.hasNext();){
				GCubeCoordinates gCubeCoordinates= it.next();
				xml.append("\t\t\t<Dependency>\n");
				xml.append("\t\t\t\t<Service>\n");
				xml.append("\t\t\t\t\t<Class>").append(gCubeCoordinates.getServiceClass()).append("</Class>\n");
				xml.append("\t\t\t\t\t<Name>").append(gCubeCoordinates.getServiceName()).append("</Name>\n");
				xml.append("\t\t\t\t\t<Version>").append(gCubeCoordinates.getServiceVersion()).append("</Version>\n");
				xml.append("\t\t\t</Dependency>\n");
			}
			xml.append("\t</ResolvedDependencies>\n");
			xml.append("\t<MissingDependencies>\n");
			for(Iterator<GCubeCoordinates> it=resolved.iterator(); it.hasNext();){
				GCubeCoordinates gCubeCoordinates= it.next();
				xml.append("\t\t\t<MissingDependency>\n");
				xml.append("\t\t\t\t<Service>\n");
				xml.append("\t\t\t\t\t<Class>").append(gCubeCoordinates.getServiceClass()).append("</Class>\n");
				xml.append("\t\t\t\t\t<Name>").append(gCubeCoordinates.getServiceName()).append("</Name>\n");
				xml.append("\t\t\t\t\t<Version>").append(gCubeCoordinates.getServiceVersion()).append("</Version>\n");
				xml.append("\t\t\t</MissingDependency>\n");
			}
			xml.append("\t</MissingDependencies>\n");
			xml.append("</DependencyResolutionReport>\n");
		   System.out.println("RESULT:\n "+xml.toString());
	}
	
//	@Test
	public void testXMLPlugins(){
		xml.append("<ServicePlugins>\n");
		for(Iterator<GCubeCoordinates> it=resolved.iterator(); it.hasNext();){
			GCubeCoordinates gCubeCoordinates= it.next();
			xml.append("\t<Plugin>\n");
			xml.append("\t\t<Service>\n");
			xml.append("\t\t\t<Class>").append(gCubeCoordinates.getServiceClass()).append("</Class>\n");
			xml.append("\t\t\t<Name>").append(gCubeCoordinates.getServiceName()).append("</Name>\n");
			xml.append("\t\t\t<Version>").append(gCubeCoordinates.getServiceVersion()).append("</Version>\n");
			xml.append("\t\t</Service>\n");
			xml.append("\t</Plugin>\n");
		}
		xml.append("</ServicePlugins>");
		System.out.println("RESULT:\n "+xml.toString());
	}
	
	/*GET PROFILE */
	
	
	  @Before
	  public void before() throws Exception{
		scope = GCUBEScope.getScope("/gcube");
		isClient = GHNContext.getImplementation(ISClient.class);
	  }
	  
	  @Test
	  public void Test(){
		  String serviceClass="VREManagement";
		  String serviceName="SoftwareRepository";
		  String serviceVersion="1.0.0";
		  String packageName="SoftwareRepository-service";
		  String packageVersion="1.2.1";
		  GCUBEServiceQuery serviceQuery=null;
			try{
				serviceQuery=isClient.getQuery(GCUBEServiceQuery.class);
			}catch(Exception e ){
//				throw new Exception();
			}
		  serviceQuery=buildServiceQuery(serviceName, serviceClass, serviceVersion, packageName,
					packageVersion, serviceQuery);
			try{
				for ( GCUBEService resource:isClient.execute(serviceQuery, scope)){
					String body=resource.getResourceVersion();
					System.out.println("body found:   "+body);
				}

			}catch(Exception e){
				System.out.println("ERRORE RECUPERO RESOURCE PROFILE ");
			}
		  
	  }
	  
		private GCUBEServiceQuery buildServiceQuery(String serviceName, String serviceClass,
				String serviceVersion, String packageName, String packageVersion,
				GCUBEServiceQuery serviceQuery) {
			serviceQuery.addAtomicConditions(new AtomicCondition("//Profile/Name",serviceName));
			serviceQuery.addAtomicConditions(new AtomicCondition("//Profile/Class",serviceClass));
			serviceQuery.addAtomicConditions(new AtomicCondition("//Profile/Version",serviceVersion));
			serviceQuery.addAtomicConditions(new AtomicCondition("//Profile/Class",packageName));
			serviceQuery.addAtomicConditions(new AtomicCondition("//Profile/Version",packageVersion));
			return serviceQuery;
		}

	
	
}
