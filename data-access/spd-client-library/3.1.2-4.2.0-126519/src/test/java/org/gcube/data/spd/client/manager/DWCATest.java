package org.gcube.data.spd.client.manager;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.executor;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;
import static org.gcube.data.streams.dsl.Streams.convert;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Executor;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.stubs.types.NodeStatus;
import org.gcube.data.spd.stubs.types.Status;
import org.gcube.data.streams.Stream;

public class DWCATest {

		
	public static void main(String[] args) throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		Executor creator = executor().build();
		
		//"CatalogueOfLife:13445516" chordata
		//"CatalogueOfLife:13446218" cervidae
		String jobId = createDWCAJobTest(creator, "CatalogueOfLife:13446218");
		//String jobId = createOccurrence(creator);
		Status response= null;
		do{
			 Thread.sleep(10000);
			 response= creator.getStatus(jobId);
			 System.out.println("thes status is "+response.getStatus());
			 System.out.println("the number of element read are "+response.getCompletedEntries());
			 if(response.getSubNodes()!=null)
				 for (NodeStatus status : response.getSubNodes())
					 System.out.println(status.getScientificName()+"--"+status.getStatus());
				 
		}while(!response.getStatus().equals("FAILED") &&  !response.getStatus().equals("COMPLETED"));
		
		System.out.println("result uri "+creator.getResultLink(jobId));
		System.out.println("error uri "+creator.getErrorLink(jobId));
		
		
	}
		
	
	private static String createDWCAJobTest(Executor creator, String id) throws Exception {
		return creator.createDwCAByChildren(id);
	}
	
	private static String createOccurrence(Executor creator) throws Exception {
		Stream<String> keyStream =convert(new String[]{"Obis:522634-1695----", "Obis:465686-1695----", "Obis:429081-721----", "Obis:822676-1691----", "Obis:742361-119----", "Obis:447539-1695----", "GBIF:sarda||130||11956||57744173||","GBIF:sarda||82||400||50917042||","GBIF:sarda||427||14113||60499431||"});
		return creator.createDarwincoreFromOccurrenceKeys(keyStream);
	}
	
	private static String createOccurrenceFromSardasarda(Executor creator) throws Exception {
		
		Manager manager = manager().build();
		
		Stream<ResultElement> rsStream = manager.search("'sarda sarda' as SN in GBIF, Obis return *");
		
		List<String> keylist = new ArrayList<String>();
		while (rsStream.hasNext()){
			ResultItem rs = (ResultItem) rsStream.next();
			for (Product product: rs.getProducts())
				if (product.getCount()>0 && product.getType()==ProductType.Occurrence) keylist.add(product.getKey());
		}
			
		
		Stream<String> keyStream =convert(keylist);
		return creator.createCSV(keyStream);
	}
	
	
		
}
