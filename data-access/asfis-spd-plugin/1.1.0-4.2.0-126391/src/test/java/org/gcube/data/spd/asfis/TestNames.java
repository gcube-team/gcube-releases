package org.gcube.data.spd.asfis;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.asfis.capabilities.NamesMappingImpl;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class TestNames {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		AsfisPlugin b = new AsfisPlugin();
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'ASFIS' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		System.out.println(resources.size());

		if(resources.size() != 0) {	   
			try {
				b.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		NamesMappingImpl a = new NamesMappingImpl();
		try {

			a.getRelatedScientificNames(new ObjectWriter<String>() {

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
			},"Arao");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
