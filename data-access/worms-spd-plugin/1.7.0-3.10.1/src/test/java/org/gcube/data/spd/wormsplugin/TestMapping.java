package org.gcube.data.spd.wormsplugin;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;
import java.util.Set;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.wormsplugin.capabilities.NamesMappingImpl;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class TestMapping {


	public static void main(String[] args) throws Exception {

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'WoRMS' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		System.out.println(resources.size());
		
		WormsPlugin a = new WormsPlugin();
		if(resources.size() != 0) {	   
			try {
				a.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		WormsPlugin.binding = (aphia.v1_0.AphiaNameServiceBindingStub)
				new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
		
		NamesMappingImpl b = new NamesMappingImpl();
		
//		try {
//			Set<String> c = b.scientificNameToCommonNamesMapping("Sarda");
//			System.out.println("CommonName " + c.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}

