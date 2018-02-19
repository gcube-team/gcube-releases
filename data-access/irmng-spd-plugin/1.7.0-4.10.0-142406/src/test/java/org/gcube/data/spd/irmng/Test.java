package org.gcube.data.spd.irmng;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Test {
	static Logger logger = LoggerFactory.getLogger(Test.class);
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		IrmngPlugin b = new IrmngPlugin();
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'IRMNG' ");
//		ScopeProvider.instance.set("/gcube/devsec");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

//		System.out.println(resources.size());
		
		if(resources.size() != 0) {	   
			try {
				b.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		b.searchByScientificName("animalia", new ObjectWriter<ResultItem>() {
			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(ResultItem t) {
		System.out.println(t.toString());
//				System.out.println(t.getId() + " - " +t.getScientificName() + " - " + t.getScientificNameAuthorship());
//				if (t.getParent()!= null)
//					System.out.println("parent " + t.getParent().getScientificName());
				return true;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});

	}
}
