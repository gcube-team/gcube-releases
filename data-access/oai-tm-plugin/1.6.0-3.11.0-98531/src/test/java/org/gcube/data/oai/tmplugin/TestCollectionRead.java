/**
 * 
 */
package org.gcube.data.oai.tmplugin;

import static org.gcube.data.tml.proxies.TServiceFactory.*;
import static org.gcube.data.trees.io.Bindings.*;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.oai.tmplugin.utils.Utils;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.AnyPattern;
import org.gcube.data.trees.patterns.Patterns;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestCollectionRead {

	/**
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {

				ScopeProvider.instance.set("/gcube/devsec");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/EUBrazilOpenBio");
//		ScopeProvider.instance.set("/gcube/devNext");	

		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/FARM");	
		
//	ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps");
		//		String id = "1713af3e-57d6-403e-8767-1544f7e1a345";
		String id = "e62991a1-2552-4f9b-b070-5f4ffa60eaa3";

//		StatefulQuery query = TServiceFactory.readSource().withId(treeCollectionID).build();
//        TReader treader = TServiceFactory.reader().matching(query).build();
//
//        treesReader = treader.get(Patterns.tree());
//        
        
		StatefulQuery query = readSource().withId(id).build();
		TReader reader = reader().matching(query).build();
//		Stream<Tree> stream = reader.get(new AnyPattern());
		
		Stream<Tree> stream = reader.get(Patterns.tree());
//		System.out.println("single get: "+toText(reader.get("10.3897124sep47sep35biorisk.3.11", new AnyPattern())));

//		System.out.println("single get: "+toText(reader.get("10.3897124sep47sep35biorisk.3.11", new AnyPattern())));
		int i = 0;
		while(stream.hasNext()) {

//			if (i%100==0)
//				System.out.println(i);
			
			i++;
			Tree tree = stream.next();

			System.out.println(i + " - " + toText(tree));
			System.out.println(i + " - " + tree.id() );
			try{
			System.out.println(i + " single get: "+toText(reader.get(tree.id(), new AnyPattern())));
			}catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		System.out.println("cardinality " + i);
	}

}

