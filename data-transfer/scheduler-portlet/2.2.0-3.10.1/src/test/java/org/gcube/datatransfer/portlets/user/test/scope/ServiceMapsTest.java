package org.gcube.datatransfer.portlets.user.test.scope;

import static org.gcube.resources.discovery.icclient.stubs.CollectorConstants.localname;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.api.ServiceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceMapsTest {

	
	public static void main(String[] args) {
		try{
		Logger logger = LoggerFactory.getLogger("ServiceMapsTest");
		ScopeProvider.instance.set("/gcube/devsec");
		
		String address = ServiceMap.instance.endpoint(localname);
		System.out.println(address);
		logger.debug("address="+address);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
