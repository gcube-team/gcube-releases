package org.gcube.data.speciesplugin;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.executor;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Executor;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.adapters.IteratorStream;

public class TestDwCA {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
			
		Manager call = manager().withTimeout(5, TimeUnit.MINUTES).build();
		Executor ex = executor().withTimeout(5, TimeUnit.MINUTES).build();

		ArrayList<String> ids = new ArrayList<String>();
		
		Stream<ResultElement> result = call.search("'parachela' as ScientificName return Taxon");

		while(result.hasNext()){
			ResultElement el = result.next();
			System.out.println(el.getId());
			ids.add(el.getId());
		}		
//		Iterator<String> iterator = ids.iterator();
//		ex.createDwCAByIds(iterator);
		
		Iterator<String> itr = ids.iterator();
		Stream<String> iterator = new IteratorStream<String>(itr);
		String jobId = (ex.createDwCAByIds(iterator));
//		File fileDwcA = createSpeciesJob(jobId);
		System.out.println(jobId);
	}


}
