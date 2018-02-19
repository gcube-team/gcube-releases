package org.gcube.data.spd.irmng;


import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.irmng.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TestClassification {
	static Logger logger = LoggerFactory.getLogger(TestClassification.class);
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		IrmngPlugin b = new IrmngPlugin();
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'IRMNG' ");
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
		
		// Test retrieveTaxaByName
		ClassificationCapabilityImpl a = new ClassificationCapabilityImpl();
		List<TaxonomyItem> result = a.retrieveTaxonChildrenByTaxonId("11");
		for(TaxonomyItem item: result)
			System.out.println(item.toString());
		
		TaxonomyItem tax = a.retrieveTaxonById("104404");
		logger.trace(tax.toString());
		if (tax.getParent()!=null)
			System.out.println("parent " + tax.getParent().getId());
		
		logger.trace("************************");

		
		TaxonomyItem tax1 = a.retrieveTaxonById("16");
		logger.trace(tax1.toString());
		if (tax1.getParent()!=null)
			System.out.println("parent " + tax1.getParent().getId());
		
		logger.trace("************************");
		
		a.searchByScientificName("Trixosceli", new ObjectWriter<TaxonomyItem>() {

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





		// Test retrieveTaxonChildsByTaxonId

		System.out.println(a.retrieveTaxonChildrenByTaxonId(11+""));

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
		"10506024"
				);

//		LocalWrapper<String> wrap = new LocalWrapper<String>();
//		wrap.add("10507207");
//		wrap.add("10507221");
//		wrap.add("1289");
//		LocalReader<String> list = new LocalReader<String>(wrap);
//		a.retrieveTaxonByIds(list, new ClosableWriter<TaxonomyItem>() {
//			
//			@Override
//			public boolean write(StreamException error) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//			
//			@Override
//			public boolean write(TaxonomyItem t) {
//				System.out.println(t.toString());
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
