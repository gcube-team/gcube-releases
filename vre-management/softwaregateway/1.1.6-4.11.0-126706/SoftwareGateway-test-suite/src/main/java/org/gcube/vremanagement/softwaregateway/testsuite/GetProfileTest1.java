package org.gcube.vremanagement.softwaregateway.testsuite;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEServiceQuery;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.junit.Before;
import org.junit.Test;

public class GetProfileTest1 {

	ISClient isClient;
	GCUBEScope scope;
	String [] server;
	
  @Before
  public void before() throws Exception{
	scope = GCUBEScope.getScope("/gcube");
	isClient = GHNContext.getImplementation(ISClient.class);
  }
  
  @Test
  public void Test(){
	  String serviceClass="VREManagement";
	  String serviceName="SoftwareRepository";
	  String serviceVersion="1.2.0";
	  String packageName="SoftwareRepository-service";
	  String packageVersion="1.2.0";
	  GCUBEServiceQuery serviceQuery=null;
		try{
			serviceQuery=isClient.getQuery(GCUBEServiceQuery.class);
		}catch(Exception e ){
//			throw new Exception();
		}
	  serviceQuery=buildServiceQuery(serviceName, serviceClass, serviceVersion, packageName,
				packageVersion, serviceQuery);
		try{
			for ( GCUBEService resource:isClient.execute(serviceQuery, scope)){
				String body=resource.getServiceName();
				String version=resource.getLastResourceVersion();
				System.out.println("body found:   "+body+" last version: "+version);
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
//		serviceQuery.addAtomicConditions(new AtomicCondition("//Profile/Version",serviceVersion));
		serviceQuery.addAtomicConditions(new AtomicCondition("//Profile/Packages/Main/Name",packageName));
//		serviceQuery.addAtomicConditions(new AtomicCondition("//Profile/Packages/Main/Version",packageVersion));
		return serviceQuery;
	}
  
}
