package org.gcube.data.spd.wordssplugin;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.wordssplugin.WordssPlugin;
import org.gcube.data.spd.wordssplugin.capabilities.ClassificationCapabilityImpl;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class TestClassification {
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
		
		ClassificationCapabilityImpl b = new ClassificationCapabilityImpl();
		
//		TaxonomyItem item = b.retrieveTaxonById("368663");
//		System.out.println(item);
		
//		// Test retrieveTaxaByScientificName
		b.searchByScientificName("sarda sarda", new ObjectWriter<TaxonomyItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(TaxonomyItem t) {
				while (t != null){
					System.out.println(t.getId() + " " + t.getScientificName());
					System.out.println(t.getRank() + " " + t.getStatus());
					System.out.println(t.getCitation());
					System.out.println("**** " + t.getCredits());
					t = t.getParent();
					
				}
				return false;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		} );
		

		
		System.out.println(b.retrieveTaxonChildrenByTaxonId("274299"));	

//
//		LocalWrapper<String> wrap = new LocalWrapper<String>();
//		wrap.add("273810");
//		wrap.add("163034");
//		wrap.add("305833");
//		wrap.add("154712");
//		wrap.add("323133");
//		LocalReader<String> list = new LocalReader<String>(wrap);
//		b.retrieveTaxonByIds(list, new Writer<TaxonomyItem>(null) {
//
//			public boolean put(TaxonomyItem t) {
//				System.out.println(t.getId());
//				System.out.println(t);
//				return false;
//			}
//
//			public void close() {
//				// 
//
//			}
//		} );
//
//

//
//
////
//		b.getSynonymnsById(new ObjectWriter<TaxonomyItem>() {
//
//			public boolean put(TaxonomyItem ri) {
////				System.out.println(ri.toString());
//				System.out.println(ri.getScientificName());
//				return true;
//			}
//
//			public void close() {
//				System.out.println("************************");
//			}
//
//			@Override
//			public boolean put(StreamException arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		}, "319969"
//				);
//	}

	// Test retrieveTaxonChildsByTaxonId

	//		System.out.println(b.retrieveTaxonChildsByTaxonId(126062+""));	
	}

}
