package org.gcube.data.spd.itis;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.itis.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestClassification {
	static Logger logger = LoggerFactory.getLogger(TestClassification.class);
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'ITIS' ");
	
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		System.out.println(resources.size());

		ItisPlugin b = new ItisPlugin();
		if(resources.size() != 0) {	   
			try {
				b.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		//		// Test retrieveTaxaByScientificName
		ClassificationCapabilityImpl a = new ClassificationCapabilityImpl();

		//		System.out.println(a.retrieveTaxonChildsByTaxonId(202423+""));
		//		logger.trace("end retrieveTaxonChildsByTaxonId");
		//
		//		
		//		TaxonomyItem tax = a.retrieveTaxonById("159903");
		//		logger.trace(tax);

		System.out.println(a.retrieveTaxonChildrenByTaxonId(159902+""));	

		a.searchByScientificName("parachela", new ObjectWriter<TaxonomyItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(TaxonomyItem t) {
				System.out.println(t.toString());

				if (t.getParent()!=null){
					System.out.println("+++++++ " + t.getParent());
					if (t.getParent().getParent()!=null)
						System.out.println("+++++++ " + t.getParent().getParent());
					System.out.println("citation " + t.getParent().getCitation());
				}
				return false;
			}

			@Override
			public boolean write(StreamException ri) {
				return false;
			}
		} );

		a.getSynonymnsById(new ObjectWriter<TaxonomyItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(TaxonomyItem ri) {
				System.out.println(ri.toString());
				return false;
			}

			@Override
			public boolean write(StreamException ri) {
				System.out.println(ri.toString());
				return false;
			}
		}, 
		"1408"
				);

//		LocalWrapper<String> wrap = new LocalWrapper<String>();
//		wrap.add("177415");
//		wrap.add("177762");
//		wrap.add("188501");
//		wrap.add("504760");
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

