package org.gcube.portlets.user.homelibrary.jcr.repository;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class RuntimeResourcesAccess {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		
		
		System.out.println("Root name " + "/gcube");
		
		ScopeProvider.instance.set("/gcube");
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq 'HomeLibraryRepository' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		if(resources.size() != 0) {	   
			try {
				ServiceEndpoint resource = resources.get(0);
				
				for (AccessPoint ap:resource.profile().accessPoints()) {
					
					if (ap.name().equals("JCR")) {
						System.out.println(StringEncrypter.getEncrypter().decrypt(ap.password()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
