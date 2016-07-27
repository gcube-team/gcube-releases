package org.gcube.search.datafusion.test;

//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.search.datafusion.DataFusion;
//import org.gcube.search.datafusion.datatypes.PositionalRecordWrapper;
//import org.gcube.search.datafusion.datatypes.RSFusedIterator;

//import org.gcube.search.client.library.proxies.SearchCLProxyI;
//import org.gcube.search.client.library.proxies.SearchDSL;
//import org.gcube.data.streams.Stream;
//import gr.uoa.di.madgik.grs.record.GenericRecord;

import org.junit.Test;

import junit.framework.TestCase;

public class DatafusionTest extends TestCase {

	@Test
	public void testDummy() {

	}

	/*@Test
	public void testFusion() throws Exception {

		try {
			int count = 60000;
			System.out.println("Starting fusion");

			ScopeProvider.instance.set("/gcube/devNext/NextNext");
			SearchCLProxyI proxyRandom = SearchDSL.getSearchProxyBuilder().at("dl07.di.uoa.gr", 8080).build();

			String query1 = "((ac6e40cd-f6b7-4e5e-a199-30e3944064e3 = Macrobiotus) and (gDocCollectionID == d686c177-8d4c-4947-b85a-f6e8ef65620e)) project *";
			String query2 = "((ac6e40cd-f6b7-4e5e-a199-30e3944064e3 = Macrobiotidae) and (gDocCollectionID == d686c177-8d4c-4947-b85a-f6e8ef65620e)) project *";
			String query3 = "((ac6e40cd-f6b7-4e5e-a199-30e3944064e3 = paok) and (gDocCollectionID == d686c177-8d4c-4947-b85a-f6e8ef65620e)) project *";
			String query4 = "((ac6e40cd-f6b7-4e5e-a199-30e3944064e3 = Taxonomic) and (gDocCollectionID == d686c177-8d4c-4947-b85a-f6e8ef65620e)) project *";
			String query5 = "((ac6e40cd-f6b7-4e5e-a199-30e3944064e3 = Macrobiotidae) and (gDocCollectionID == d686c177-8d4c-4947-b85a-f6e8ef65620e)) project *";
			String query6 = "((ac6e40cd-f6b7-4e5e-a199-30e3944064e3 = paok) and (gDocCollectionID == d686c177-8d4c-4947-b85a-f6e8ef65620e)) project *";

			long starttime = System.currentTimeMillis();
			Stream<GenericRecord> records1 = proxyRandom.search(query1);
			long endtime = System.currentTimeMillis();
			System.out.println("~> time for query1 : " + (endtime - starttime) / 1000.0 + " secs");
			//
			starttime = System.currentTimeMillis();
			Stream<GenericRecord> records2 = proxyRandom.search(query2);
			endtime = System.currentTimeMillis();
			System.out.println("~> time for query2 : " + (endtime - starttime) / 1000.0 + " secs");

			starttime = System.currentTimeMillis();
			Stream<GenericRecord> records3 = proxyRandom.search(query3);
			endtime = System.currentTimeMillis();
			System.out.println("~> time for query3 : " + (endtime - starttime) / 1000.0 + " secs");

			starttime = System.currentTimeMillis();
			Stream<GenericRecord> records4 = proxyRandom.search(query4);
			endtime = System.currentTimeMillis();
			System.out.println("~> time for query4 : " + (endtime - starttime) / 1000.0 + " secs");

			starttime = System.currentTimeMillis();
			Stream<GenericRecord> records5 = proxyRandom.search(query5);
			endtime = System.currentTimeMillis();
			System.out.println("~> time for query5 : " + (endtime - starttime) / 1000.0 + " secs");

			starttime = System.currentTimeMillis();
			Stream<GenericRecord> records6 = proxyRandom.search(query6);
			endtime = System.currentTimeMillis();
			System.out.println("~> time for query6 : " + (endtime - starttime) / 1000.0 + " secs");

			List<Iterator<GenericRecord>> streamIters = new ArrayList<Iterator<GenericRecord>>();
			streamIters.add(records1);
			streamIters.add(records2);
			streamIters.add(records3);
			streamIters.add(records4);
			streamIters.add(records5);
			streamIters.add(records6);

			starttime = System.currentTimeMillis();
			Iterator<PositionalRecordWrapper> iter = new RSFusedIterator(streamIters);
			endtime = System.currentTimeMillis();
			System.out.println("~> time creating the fused iterator : " + (endtime - starttime) / 1000.0 + " secs");

			starttime = System.currentTimeMillis();
			List<String> l = new ArrayList<String>();
			l.add("b93a6f30-ac85-42d1-8250-987793917067");

			DataFusion.rerankRecords(iter, "Macrobiotidae", count, l, false);
			endtime = System.currentTimeMillis();
			System.out.println("Total reranking time : " + (endtime - starttime) / 1000.0 + " secs");
		} catch (Exception e) {
			//e.printStackTrace();
			//fail();
		}
	}*/

}
