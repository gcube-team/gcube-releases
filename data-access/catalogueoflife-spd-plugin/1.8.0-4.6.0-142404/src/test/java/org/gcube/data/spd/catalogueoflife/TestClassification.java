package org.gcube.data.spd.catalogueoflife;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.catalogueoflife.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
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
		CatalogueOfLifePlugin a = new CatalogueOfLifePlugin();
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'CatalogueOfLife' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		System.out.println(resources.size());
		
		if(resources.size() != 0) {	   
			try {
				a.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//		String[] query1 ={"marisarabica", "chiru", "Mollusca", "arabica", "Orthoptera"};
		//
		//		for (int i=0 ; i< 4 ; i++) {
		//			new NewThread(query1[i], i); // creo un nuovo thread
		//		}

		// Test retrieveTaxaByName
//		ClassificationCapabilityImpl b = new ClassificationCapabilityImpl();
//		b.searchByScientificName("Scombridae", new ObjectWriter<TaxonomyItem>() {
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public boolean write(TaxonomyItem t) {
//				System.out.println(t);
//				return false;
//			}
//
//			@Override
//			public boolean write(StreamException arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
//
//		b.getSynonymnsById(new ObjectWriter<TaxonomyItem>() {
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public boolean write(TaxonomyItem t) {
//				System.out.println(t);
//				return false;
//			}
//
//			@Override
//			public boolean write(StreamException arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		}, 
//		"10076132"
//				);
//
//
//
//
//		List<TaxonomyItem> list = b.retrieveTaxonChildrenByTaxonId(11670252+"");
//		System.out.println(list);
//
//		b.searchByScientificName("Carcharodon", new ObjectWriter<TaxonomyItem>() {
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public boolean write(TaxonomyItem t) {
//				System.out.println(t);
//				return false;
//			}
//
//			@Override
//			public boolean write(StreamException arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});



		//		 Test retrieveTaxonChildsByTaxonId
		ClassificationCapabilityImpl c = new ClassificationCapabilityImpl();
//		List<TaxonomyItem> lista = c.retrieveTaxonChildrenByTaxonId(14376129+"");
//		System.out.println("size: " + lista.size());
//		for (TaxonomyItem item : lista)
//			System.out.println(item.toString());
		
		


//		LocalWrapper<String> wrap = new LocalWrapper<String>();
//		wrap.add("14376129");
//		wrap.add("14376358");
//		wrap.add("13694496");
//		LocalReader<String> list1 = new LocalReader<String>(wrap);
//		
//		c.retrieveTaxonByIds(list1, new ClosableWriter<TaxonomyItem>() {
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
//			public boolean write(TaxonomyItem t) {
//				System.out.println(t.toString());
//				return false;
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



//class NewThread extends Thread {
//
//	Integer idThread;
//	String query;
//
//	NewThread(String query, Integer idThread) {
//
//		super("Thread");
//		this.idThread = idThread;
//		this.query = query;
//		start(); // Start the thread
//
//	}
//
//	// This is the entry point for the child threads
//	public void run() {
//
//		ClassificationCapabilityImpl b = new ClassificationCapabilityImpl();
//
//		//			
//		b.searchByScientificName(query, new ObjectWriter<TaxonomyItem>() {
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public boolean write(TaxonomyItem t) {
//				System.out.println(t);
//				return false;
//			}
//
//			@Override
//			public boolean write(StreamException arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		} );
//
//	} 
//
//
//
//
//}





