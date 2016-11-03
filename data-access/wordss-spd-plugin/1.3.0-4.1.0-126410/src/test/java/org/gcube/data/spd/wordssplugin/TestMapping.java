package org.gcube.data.spd.wordssplugin;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.wordssplugin.capabilities.NamesMappingImpl;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class TestMapping {


	public static void main(String[] args) throws Exception {

		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'WoRDSS' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		System.out.println(resources.size());
		
		WordssPlugin a = new WordssPlugin();
		if(resources.size() != 0) {	   
			try {
				a.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		WordssPlugin.binding = (aphia.v1_0.AphiaNameServiceBindingStub)
				new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
		
		NamesMappingImpl b = new NamesMappingImpl();
		try {
			b.getRelatedScientificNames(new ObjectWriter<String>() {

				@Override
				public boolean write(String t) {
					System.out.println(t.toString());
					return false;
				}

				@Override
				public boolean write(StreamException error) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean isAlive() {
					// TODO Auto-generated method stub
					return true;
				}
			}, "Galapagos");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

