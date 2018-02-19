package org.gcube.accounting.aggregator.workspace;

import java.util.HashMap;
import java.util.Map;

import org.gcube.accounting.aggregator.plugin.ScopedTest;
import org.gcube.accounting.aggregator.workspace.WorkSpaceManagement;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.utils.Group;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkSpaceManagementTest extends ScopedTest {

	public static Logger logger = LoggerFactory.getLogger(WorkSpaceManagementTest.class);
	
	@Test
	public void endpointTest(){
		Profile profile = WorkSpaceManagement.gCoreEndpoint.profile();
		Group<Endpoint> endpoints = profile.endpoints();
		Map<String, String> restEndpointMap = new HashMap<>();
		for(Endpoint endpoint : endpoints){
			String endpointName = endpoint.name();
			String endpointURI = endpoint.uri().toString();
			if(endpointURI.contains("rest")){
				restEndpointMap.put(endpointName, endpointURI);
			}
		}
		logger.debug("{}", restEndpointMap);
	}
	
}
