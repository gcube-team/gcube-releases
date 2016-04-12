package org.gcube.data.spd.ncbi;


import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.ncbi.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;

public class TestClassification {
	public static void main(String[] args) throws Exception {

		// Test retrieveTaxaByName
		ClassificationCapabilityImpl b = new ClassificationCapabilityImpl();

		//		TaxonomyItem tax = b.retrieveTaxonById("641286");
		//		System.out.println(tax);
		// Test retrieveTaxonChildsByTaxonId
		//				System.out.println(b.retrieveTaxonChildsByTaxonId(172402+""));	

		b.getSynonymnsById(new ObjectWriter<TaxonomyItem>() {
			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(TaxonomyItem t) {
				System.out.println(t.getScientificName());
//				System.out.println(t.getCredits());
				return true;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		}, 
		"89-1"
				);

//		String[] query1 = {"Cup", "tele", "micro", "pro"};
//
//		for (int i=0 ; i< query1.length-1 ; i++) {
//			new NewThread(query1[i], i); // creo un nuovo thread
//		}

		b.searchByScientificName("tele", new ObjectWriter<TaxonomyItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(TaxonomyItem t) {
				System.out.println(t.getScientificName());
//				System.out.println(t.getCredits());
				return true;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});

//		b.searchByCommonName("vetch", new ObjectWriter<TaxonomyItem>() {
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public boolean write(TaxonomyItem arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public boolean write(StreamException arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});


//		LocalWrapper<String> wrap = new LocalWrapper<String>();
//		wrap.add("3909");
//		wrap.add("1010706");
//		wrap.add("53861");
//		wrap.add("3857");
//		wrap.add("89-3");
//		LocalReader<String> list = new LocalReader<String>(wrap);
//		b.retrieveTaxonByIds(list, new Writer<TaxonomyItem>(null) {
//
//			public boolean put(TaxonomyItem t) {
//				System.out.println(t);
//				return false;
//			}
//
//			public void close() {
//				System.out.println("************************");
//
//			}
//		} );
//




	}


}


class NewThread extends Thread {

	static GCUBELog logger = new GCUBELog(NewThread.class);
	Integer idThread;
	String query;

	NewThread(String query, Integer idThread) {

		super("Thread");
		this.idThread = idThread;
		this.query = query;
		start(); // Start the thread
	}

	// This is the entry point for the child threads
	public void run() {

		ClassificationCapabilityImpl b = new ClassificationCapabilityImpl();

		b.searchByScientificName(query, new ObjectWriter<TaxonomyItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean write(TaxonomyItem arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	} 
}

