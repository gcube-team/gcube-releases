package org.gcube.data.spd.flora;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.flora.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;



public class TestClassification {
	static GCUBELog logger = new GCUBELog(TestClassification.class);
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// Test retrieveTaxaByName
		
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
		
		ClassificationCapabilityImpl a = new ClassificationCapabilityImpl();

		
		a.searchByScientificName("rosa", new ObjectWriter<TaxonomyItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(TaxonomyItem t) {
				System.out.println(t.toString());
				return false;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		a.getSynonymnsById(new ObjectWriter<TaxonomyItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(TaxonomyItem t) {
				System.out.println(t.toString());
				return false;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		}, 
				"33");

		TaxonomyItem tax = a.retrieveTaxonById("4127");
		logger.trace(tax);



		// Test retrieveTaxonChildsByTaxonId

//		logger.trace(a.retrieveTaxonChildrenByTaxonId(4137+""));
//
//		LocalWrapper<String> wrap = new LocalWrapper<String>();
//		wrap.add("100");
//		wrap.add("32");
//		wrap.add("172");
//		LocalReader<String> list = new LocalReader<String>(wrap);
//		a.retrieveTaxonByIds(list, new ClosableWriter<TaxonomyItem>() {
//
//			@Override
//			public boolean write(TaxonomyItem t) {
//				System.out.println(t.toString());
//				return false;
//			}
//
//			@Override
//			public boolean write(StreamException error) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public void close() {
//				// TODO Auto-generated method stub
//				
//			}
//		});

	}

}
