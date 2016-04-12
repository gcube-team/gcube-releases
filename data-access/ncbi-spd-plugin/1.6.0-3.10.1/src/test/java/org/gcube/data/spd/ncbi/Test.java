package org.gcube.data.spd.ncbi;


import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class Test {
	static GCUBELog logger = new GCUBELog(Test.class);
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub		
		
//		System.setProperty("GLOBUS_LOCATION", "/home/valentina/gCore");

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'NCBI' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

//		System.out.println(resources.size());
		
		NcbiPlugin b = new NcbiPlugin();
		if(resources.size() != 0) {	   
			try {
				b.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}



		b.searchByScientificName("rosa", new ObjectWriter<ResultItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(ResultItem t) {
				System.out.println(t.getScientificName());
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