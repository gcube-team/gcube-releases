package org.gcube.data.spd.flora;

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



//import org.gcube.application.speciesmanager.stubs.model.ResultItem;
//import org.gcube.application.speciesmanager.stubs.pluginhelper.writers.ObjectWriter;


public class test {
	static GCUBELog logger = new GCUBELog(test.class);
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//				Discover a = new Discover();
//				a.upInfo();
//				a.copy_flora();
//				Discover.copy_syn();

		//update db
		//		new UpdateThread();

		//		ArrayList<ArrayList<String>> list = a.discoverPath("http://checklist.florabrasil.net/service/TREE/VERSION/2012/FORMAT/xml/LANG/en/", null, "", null, false);
		//		System.out.println(list.size());
		//		for (int n=list.size(); n>0;n--) 
		//		{	
		////		System.out.println(list.get(1));
		//			new NewThread(list.remove(1), 6); // creo un nuovo thread
		//		}

		
		FloraPlugin b = new FloraPlugin();

		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'BrazilianFlora' ");
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

		b.searchByScientificName("rosa", new ObjectWriter<ResultItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(ResultItem t) {
				System.out.println(t.toString());
				System.out.println(t.getScientificName());
				return false;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		b.searchByScientificName("s", new ObjectWriter<ResultItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean write(ResultItem arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		//			Discover.createDB();
//		new UpdateThread();
		
		
		//create db
//		if (!(Discover.SQLTableExists(FloraPlugin.tableName)) & !(Discover.SQLTableExists(FloraPlugin.tableSyn)))
//			new CreateDBThread();
//		else
//			System.out.println("Tables already exist");
//		
//		new UpdateThread();
		



		//		Discover.syn();

		//		
		//		FloraPlugin b = new FloraPlugin();
		////		
		//		b.search("Calyptranthes");

	}

}
