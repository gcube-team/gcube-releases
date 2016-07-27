package org.gcube.data.oai.tmplugin;

import static org.gcube.data.tml.proxies.TServiceFactory.binder;
import static org.gcube.data.tml.proxies.TServiceFactory.reader;
import static org.gcube.data.trees.io.Bindings.toText;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.oai.tmplugin.requests.RequestBinder;
import org.gcube.data.oai.tmplugin.requests.WrapRepositoryRequest;
import org.gcube.data.oai.tmplugin.requests.WrapSetsRequest;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.proxies.BindRequest;
import org.gcube.data.tml.proxies.Binding;
import org.gcube.data.tml.proxies.TBinder;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.AnyPattern;


public class Test {
	//	private static final Logger log = LoggerFactory.getLogger("test");
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//test
		//		ScopeProvider.instance.set("/gcube/devsec");
		//			URI uri = new URI("http://node6.d.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/data/tm/binder");

		ScopeProvider.instance.set("/gcube/devNext");	
		URI uri = new URI("http://node2.d.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/data/tm/binder");


		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/Ecosystem");
		//		URI uri = new URI("http://node20.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/data/tm/reader");
		//		
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps");
		//		URI uri = new URI("http://dewn06.madgik.di.uoa.gr:8080/wsrf/services/gcube/data/tm/binder");	

		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/Ecosystem");	
		//		URI uri = new URI("http://node20.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/data/tm/binder");

		WrapRepositoryRequest request = new WrapRepositoryRequest();
		request.setRepositoryUrl("http://darchive.mblwhoilibrary.org:8080/oai/request");
//		request.addSets("aquacomm");
		request.setMetadataFormat("oai_dc");
		request.setName("WHOAS");
		request.setDescription("A collection resulting from Woods Hole Open Access Server (WHOAS). The mission of WHOAS is to capture, store, preserve, and redistribute the intellectual output of the Woods Hole scientific community in digital form. WHOAS, an institutional repository (IR), is managed by the MBLWHOI Library as a service to the Woods Hole scientific community.");
		request.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
		request.setTitleXPath("//*[local-name()='title']");
		request.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	

		//								WrapSetsRequest request = new WrapSetsRequest("http://ws.pangaea.de/oai/");
		//								request.setMetadataFormat("oai_dc");
		//								request.addSets("bk");
		//								request.setName("bioline");
		//								request.setDescription("bioline");
		//								request.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
		//								request.setTitleXPath("//*[local-name()='title']");
		//								request.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	


		TBinder binder = binder().at(uri).withTimeout(5, TimeUnit.MINUTES).build();
		RequestBinder db = new RequestBinder();	


		System.out.println("Request: " + request.toString());	
		BindRequest params = new BindRequest("oai-tm-plugin",db.bind(request), true);

		System.out.println("params: " + params.toString());

		List<Binding> bindings = binder.bind(params);

		System.out.println("bindings: " + bindings.toString());

		Binding binding = bindings.get(0);

		TReader reader = reader().at(binding.readerRef()).build();

		Stream<Tree> stream = reader.get(new AnyPattern());

		int count = 0;
		while(stream.hasNext()) {
			Tree tree = stream.next();
			//			System.out.println(toText(tree));
			System.out.println(count + " - " + tree.toString());
			//			System.out.println("single get: "+toText(reader.get(tree.id(), new AnyPattern())));
			count++;
		}		
		System.out.println(request.getRepositoryUrl() + " - cardinality: " + count);

	} 


}
