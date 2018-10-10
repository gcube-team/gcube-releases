package org.gcube.data.spd.itis;

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

		System.setProperty("GLOBUS_LOCATION", "/home/valentina/gCore");

		//		ISClient client = GHNContext.getImplementation(ISClient.class); 
		//
		//		GCUBERuntimeResourceQuery query = client.getQuery(GCUBERuntimeResourceQuery.class);
		//
		//		query.addAtomicConditions(new AtomicCondition("/Profile/Category","BiodiversityRepository"), new AtomicCondition("/Profile/Name","ITIS"));
		//
		//		List<ServiceEndpoint> result = client.execute(query, GCUBEScope.getScope("/gcube/devsec"));

//		org.apache.xml.security.Init.init();
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'ITIS' ");
		ScopeProvider.instance.set("/gcube/devsec");

//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityResearchEnvironment");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

//		System.out.println(resources.size());
		
		
		
		ItisPlugin b = new ItisPlugin();
		if(resources.size() != 0) {	   
			try {
				b.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//
		//		String[] query1 ={"Carcharodon", "chiru", "Mollusca", "arabica", "Orthoptera"};
		//
		//		for (int i=0 ; i< 4 ; i++) {
		//			new NewThread1(query1[i], i); // creo un nuovo thread
		//		}

		b.searchByScientificName("parachela", new ObjectWriter<ResultItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(ResultItem ri) {
				System.out.println(ri.toString());
				System.out.println(ri.getId() + " " + ri.getScientificName());
				return false;
			}

			@Override
			public boolean write(
					StreamException error) {
				// TODO Auto-generated method stub
				return false;
			}


		});


	}

}

class NewThread1 extends Thread {

	static Logger logger = LoggerFactory.getLogger(NewThread1.class);
	Integer idThread;
	String query;

	NewThread1(String query, Integer idThread) {

		super("Thread");
		this.idThread = idThread;
		this.query = query;
		start(); // Start the thread

	}

	// This is the entry point for the child threads
	public void run() {

		//		ItisPlugin b = new ItisPlugin();


		//		b.searchByScientificName(query, new ObjectWriter<ResultItem>() {
		//
		//			@Override
		//			public boolean put(ResultItem t) {
		////				System.out.println(t.toString());
		//				System.out.println(t.getId());
		//				return false;
		//			}
		//
		//			@Override
		//			public void close() {
		//				logger.trace("************************");
		//
		//			}
		//
		//			@Override
		//			public boolean put(StreamException arg0) {
		//				// TODO Auto-generated method stub
		//				return false;
		//			}
		//		});
	}
}
