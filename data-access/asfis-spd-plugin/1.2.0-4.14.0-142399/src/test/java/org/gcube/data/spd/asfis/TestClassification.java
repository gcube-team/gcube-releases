package org.gcube.data.spd.asfis;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.asfis.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.InvalidRecordException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.exceptions.WrapperAlreadyDisposedException;
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
	 * @throws ExternalRepositoryException 
	 * @throws IdNotValidException 
	 * @throws WrapperAlreadyDisposedException 
	 * @throws InvalidRecordException 
	 */
	public static void main(String[] args) throws IdNotValidException, ExternalRepositoryException, InvalidRecordException, WrapperAlreadyDisposedException {
		AsfisPlugin b = new AsfisPlugin();
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'ASFIS' ");
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


		a.searchByScientificName("sarda", new ObjectWriter<TaxonomyItem>() {

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


		TaxonomyItem tax = a.retrieveTaxonById("4");
		logger.trace(tax.toString());

		// Test retrieveTaxonChildsByTaxonId

		//kingdom
		List<TaxonomyItem> lista3 = a.retrieveTaxonChildrenByTaxonId("12425");
		for (TaxonomyItem ti : lista3){
			System.out.println(ti.toString());
			System.out.println(ti.toString());
		}
		//order
		List<TaxonomyItem> lista1 = a.retrieveTaxonChildrenByTaxonId("12659");
		for (TaxonomyItem ti : lista1){
			System.out.println(ti.toString());
		}
		//family
		List<TaxonomyItem> lista2 = a.retrieveTaxonChildrenByTaxonId("12425");
		for (TaxonomyItem ti : lista2){
			System.out.println(ti.toString());
		}


//		LocalWrapper<String> wrap = new LocalWrapper<String>();
//		wrap.add("12659");
//		wrap.add("12425");
//		wrap.add("12659");
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
