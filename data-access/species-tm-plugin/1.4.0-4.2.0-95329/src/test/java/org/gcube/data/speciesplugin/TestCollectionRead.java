/**
 * 
 */
package org.gcube.data.speciesplugin;

import static org.gcube.data.tml.proxies.TServiceFactory.readSource;
import static org.gcube.data.tml.proxies.TServiceFactory.reader;
import static org.gcube.data.trees.io.Bindings.toText;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.AnyPattern;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestCollectionRead {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//		ScopeProvider.instance.set("/gcube/devsec");
		ScopeProvider.instance.set("/gcube/devsec");

		String id = "eb023ff2-e88a-4dc1-b389-db96454b78b6";
		
		StatefulQuery query = readSource().withId(id).build();
		TReader reader = reader().matching(query).build();
		Stream<Tree> stream = reader.get(new AnyPattern());
		
//		Stream<Tree> stream = reader.get(Patterns.tree());
		
		while(stream.hasNext()) {
			Tree tree = stream.next();
			System.out.println(toText(tree));
			System.out.println();
			
			System.out.println("single get: "+toText(reader.get(tree.id(), new AnyPattern())));
			System.out.println(tree.sourceId());
			
			
//			oai:ojs.ijict.org:article/217 after pruning it with ijoat:TN
		}
		

		
	}

}
