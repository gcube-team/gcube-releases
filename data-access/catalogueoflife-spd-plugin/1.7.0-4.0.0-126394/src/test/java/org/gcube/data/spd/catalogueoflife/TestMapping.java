package org.gcube.data.spd.catalogueoflife;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;
import java.util.Set;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.catalogueoflife.capabilities.NamesMappingImpl;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class TestMapping {


	public static void main(String[] args) throws Exception {

		CatalogueOfLifePlugin a = new CatalogueOfLifePlugin();

		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'CatalogueOfLife' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		System.out.println(resources.size());
		
		if(resources.size() != 0) {	   
			try {
				a.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		NamesMappingImpl b = new NamesMappingImpl();
		try {
			Set<String> c = b.commonNameToScientificNamesMapping("Äkta tunga");
			System.out.println(c);
			
//			Set<String> d = b.scientificNameToCommonNamesMapping("Carcharhinus albimarginatus");
//			System.out.println(d);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

