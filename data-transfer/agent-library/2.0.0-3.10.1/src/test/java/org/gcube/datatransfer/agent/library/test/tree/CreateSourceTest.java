package org.gcube.datatransfer.agent.library.test.tree;


import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tml.proxies.BindRequest;
import org.gcube.data.tml.proxies.Binding;
import org.gcube.data.tml.proxies.TBinder;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.trees.patterns.ManyPattern;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.junit.BeforeClass;
import org.junit.Test;

public class CreateSourceTest {
	static String SOURCE_ID="from_portlet";
	//static String SOURCE_ID="test_";
	
	static TWriter client_writer =null;
	static TReader client_reader =null;

	static Pattern pattern = Patterns.any(); 
	static String conclusionMessage="";

	static String remoteAddress = "node6.d.d4science.research-infrastructures.eu";
//	static String remoteAddress="node20.p.d4science.research-infrastructures.eu";
	static int remotePort = 8080;
	static String scope = "/gcube/devsec";
//	static String scope = "/d4science.research-infrastructures.eu/Ecosystem";

	
	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set(scope);
		System.out.println(scope);		
	}

	@Test
	public void wholeProcess() throws Exception {
		//initialization
		createSource();
		//createReadersAndWritersForSources();
	
		System.out.println("******** conclusionMessage ********\n"+conclusionMessage);
	}

	public static void createSource() {
		try {
			ScopeProvider.instance.set(scope);
			//TBinder binder = TServiceFactory.binder().at("localhost", 9999).build();
			
			TBinder binder = TServiceFactory.binder().matching(TServiceFactory.plugin("tree-repository")).build();
			BindSource request = new BindSource(SOURCE_ID);
			BindRequest params = new BindRequest("tree-repository",request.toElement());

			Binding binding = binder.bind(params).get(0);
			System.out.println( "source="+binding.source());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void createReadersAndWritersForSources() {
		try{
		client_reader = TServiceFactory.reader().matching(TServiceFactory.readSource().withId(SOURCE_ID).build()).build();
		client_writer = TServiceFactory.writer().matching(TServiceFactory.writeSource().withId(SOURCE_ID).build()).build();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
}

