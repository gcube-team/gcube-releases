package org.gcube.data.spd.flora;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;




public class TestUpdate {

	/**
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {

		//
		//		UpdateThread t = new UpdateThread();
		//		t.upInfo();


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

		//		if (!(Utils.SQLTableExists(FloraPlugin.tableName)))
		//			new CreateDBThread();
		//
		//		new UpdateThread();

		//		
		//		UpdateThread t = new UpdateThread();
		//		t.upInfo();
		//
		//		UpdateSynonymsThread a = new UpdateSynonymsThread();
		//		a.synonyms();

		//		new UpdateThread();
		//		new UpdateSynonymsThread();


		if ((!Utils.SQLTableExists(FloraPlugin.tableName) || (!Utils.SQLTableExists("updates")))){
			System.out.println("Create tables");
			Utils.createDB();
		}

		long update = Utils.lastupdate();
		new UpdateThread(update);	
//		new UpdateSynonymsThread(update);
	}

}
