package org.gcube.data.spd.client.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.plugins.AbstractPlugin;
import org.gcube.data.spd.client.proxies.ExecutorClient;
import org.gcube.data.spd.client.proxies.ManagerClient;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.service.types.CompleteJobStatus;
import org.gcube.data.spd.model.service.types.JobStatus;
import org.gcube.data.spd.model.service.types.MetadataDetails;
import org.gcube.data.spd.model.service.types.NodeStatus;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.junit.Test;


public class DWCATest {

	private static List<String> occurrenceKeys = Arrays.asList("GBIF:84028840-f762-11e1-a439-00145eb45e9a^^Marine and Coastal Management - Demersal Surveys (years 1991-1995) (AfrOBIS)^^Marine and Coastal Management - Demersal Surveys^^0e0fc0f0-828e-11d8-b7ed-b8a03c50a862^^Ocean Biogeographic Information System||5208593" , "GBIF:ed820bdb-4345-4143-a280-4fbffaacd31d^^The Pisces Collection at the Staatssammlung für Anthropologie und Paläoanatomie München^^Staatliche Naturwissenschaftliche Sammlungen Bayerns: The Pisces Collection at the Staatssammlung für Anthropologie und Paläoanatomie München^^0674aea0-a7e1-11d8-9534-b8a03c50a862^^Staatliche Naturwissenschaftliche Sammlungen Bayerns||5712279", "GBIF:8609f1a0-f762-11e1-a439-00145eb45e9a^^Marine and Coastal Management - Linefish Dataset (Second Semester of 1992) (AfrOBIS)^^Marine and Coastal Management - Linefish Dataset^^0e0fc0f0-828e-11d8-b7ed-b8a03c50a862^^Ocean Biogeographic Information System||5208602" );

	
	@Test
	public void OccurrenceJobFromSardaSarda() throws Exception{
		
		SecurityTokenProvider.instance.set("94a3b80a-c66f-4000-ae2f-230f5dfad793-98187548");
		ScopeProvider.instance.set("/gcube/devsec");
		ExecutorClient creator = AbstractPlugin.executor().build();
		
		//"CatalogueOfLife:13445516" chordata
		//"CatalogueOfLife:13446218" cervidae
		String jobId = createLayerFromSardasarda(creator);
		//String jobId = createOccurrence(creator);
		CompleteJobStatus response= null;
		do{
			 Thread.sleep(10000);
			 response= creator.getStatus(jobId);
			 System.out.println("thes status is "+response.getStatus());
			 System.out.println("the number of elements read are "+response.getCompletedEntries());
			 if(response.getSubNodes()!=null)
				 for (NodeStatus status : response.getSubNodes())
					 System.out.println(status.getScientificName()+"--"+status.getStatus());
				 
		}while(response.getStatus()!=JobStatus.FAILED &&  response.getStatus()!=JobStatus.COMPLETED);
		
		System.out.println("result uri "+creator.getResultLink(jobId));
		System.out.println("error uri "+creator.getErrorLink(jobId));
		
		
	}
		
	
	private static String createDWCAJobTest(ExecutorClient creator, String id) throws Exception {
		return creator.createDwCAByChildren(id);
	}
	
	private static String createOccurrence(ExecutorClient creator) throws Exception {
		Stream<String> keyStream =Streams.convert(occurrenceKeys);
		return creator.createDarwincoreFromOccurrenceKeys(keyStream);
	}
	
	private static String createOccurrenceFromSardasarda(ExecutorClient creator) throws Exception {
		
		ManagerClient manager = AbstractPlugin.manager().build();
		
		Stream<ResultItem> rsStream = manager.search("SEARCH BY SN 'sarda sarda'");
		
		List<String> keylist = new ArrayList<String>();
		int i =0;
		while (rsStream.hasNext()){
			ResultItem rs = rsStream.next();
			for (Product product: rs.getProducts())
				if (product.getCount()>0 && product.getType()==ProductType.Occurrence) keylist.add(product.getKey());
			if (i++>=5)
				break;
		}
		rsStream.close();	
						
		System.out.println("keyList is "+keylist.size());
		
		System.in.read();
		
		Stream<String> keyStream =Streams.convert(keylist);
		return creator.createCSV(keyStream);
	}
	
	private static String createLayerFromSardasarda(ExecutorClient creator) throws Exception {
		/*
		ManagerClient manager = AbstractPlugin.manager().build();
		
		Stream<ResultItem> rsStream = manager.search("SEARCH BY SN 'sarda sarda'");
		
		List<String> keylist = new ArrayList<String>();
		int i =0;
		while (rsStream.hasNext()){
			ResultItem rs = rsStream.next();
			for (Product product: rs.getProducts())
				if (product.getCount()>0 && product.getType()==ProductType.Occurrence) keylist.add(product.getKey());
			if (i++>=5)
				break;
		}
		rsStream.close();	
						
		System.out.println("keyList is "+keylist.size());
		
		System.in.read();
		*/
		//Stream<String> keyStream =Streams.convert(keylist);
		
		Stream<String> keyStream =Streams.convert(occurrenceKeys);
		MetadataDetails details=new MetadataDetails(
				"This layers means nothing to me", "Mind your business", "Just a layer", "Qualcuno", "insert credits");
		return creator.createLayer(keyStream, details);
	}
		
}
