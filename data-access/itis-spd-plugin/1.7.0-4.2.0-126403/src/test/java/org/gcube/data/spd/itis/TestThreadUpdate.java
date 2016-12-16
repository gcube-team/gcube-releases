package org.gcube.data.spd.itis;

import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.scope.GCUBEScope;

public class TestThreadUpdate {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {


		//		System.setProperty("GLOBUS_LOCATION", "/home/valentina/gCore");
		//
		//		ISClient client = GHNContext.getImplementation(ISClient.class); 
		//
		//		GCUBERuntimeResourceQuery query = client.getQuery(GCUBERuntimeResourceQuery.class);
		//
		//		query.addAtomicConditions(new AtomicCondition("/Profile/Category","BiodiversityRepository"), new AtomicCondition("/Profile/Name","ITIS"));
		//
		//		List<GCUBERuntimeResource> result = client.execute(query, GCUBEScope.getScope("/gcube/devsec"));
		//
		//		System.out.println(result.size());
		//		
		//		ItisPlugin b = new ItisPlugin();
		//		
		//		if(result.size() != 0) {	   
		//			try {
		//				b.initialize(result.get(0));
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		new UpdateThread();

		
		
		
		if (!Utils.SQLTableExists("updates")){
			new UpdateThread(0);			
		}
		else{
			long update = Utils.lastupdate();
			System.out.println(update);
			new UpdateThread(update);
		}
	}

}
