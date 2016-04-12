package org.gcube.datatransfer.agent.tree.test;

import static org.gcube.data.tml.proxies.TServiceFactory.readSource;
import static org.gcube.data.tml.proxies.TServiceFactory.writeSource;
import static org.gcube.datatransfer.agent.tree.test.TestUtils.SOURCE_ID;
import static org.gcube.datatransfer.agent.tree.test.TestUtils.VO;
import static org.gcube.datatransfer.agent.tree.test.TestUtils.callBinderAndBroadCastWith;
import static org.gcube.datatransfer.agent.tree.test.TestUtils.callBinderWith;
import static org.gcube.datatransfer.agent.tree.test.TestUtils.log;
import static org.gcube.datatransfer.agent.tree.test.TestUtils.queryProperties;
import static org.gcube.datatransfer.agent.tree.test.TestUtils.setCurrentScope;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.tm.stubs.SourceBinding;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.SourceQueryBuilder;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.tr.neo.NeoStore;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.generators.TemplateFactory;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyContainerTestRunner.class)
public class BindTest {


	@Deployment
	static Gar myGar  = new Gar(new File("src/test/resources/tree-manager-service.gar"));

	@Deployment
	static Gar dependencies = new Gar("dependencies").addLibraries("src/test/resources");
	
	static NeoStore store;
	
	static SourceBinding binding = null;

	static TWriter client_writer =null;
	static TReader client_reader =null;
	@BeforeClass
	public static void setup() throws Exception {

		setCurrentScope(VO);

		store = newTestStore();
		
	}

	public static NeoStore newTestStore() {
		try {
			NeoStore store = new NeoStore(SOURCE_ID);
			File tempLocation = File.createTempFile("pref", "suff").getParentFile();
			store.start(tempLocation);
			return store;
		}
		catch(Exception e) {
			throw new RuntimeException("cannot start test container",e);
		}
	}

	@Test
	public void bindSampleSourceWithReaderAndWriter() throws Exception {

		SourceBinding binding = callBinderAndBroadCastWith(new BindSource(SOURCE_ID));

		EndpointReferenceType readerEndpoint = binding.getReaderEndpoint();
		resourcePropertiesAreAccessible(readerEndpoint);

		EndpointReferenceType writerEndpoint = binding.getWriterEndpoint();

		resourcePropertiesAreAccessible(writerEndpoint);

	}

	@Test
	public void bindSampleSourceWithReader() throws Exception {

		SourceBinding binding = callBinderWith(new BindSource(SOURCE_ID));

		EndpointReferenceType readerEndpoint = binding.getReaderEndpoint();
		resourcePropertiesAreAccessible(readerEndpoint);
	}

	@Test
	public void bindSampleSourceWithWriter() throws Exception {

		SourceBinding binding = callBinderWith(new BindSource(SOURCE_ID));

		EndpointReferenceType writerEndpoint = binding.getWriterEndpoint();
		resourcePropertiesAreAccessible(writerEndpoint);
	}

	//@Test
	//public void bindSampleSourceAsynchronously() throws Exception {
	//}

	@Test
	 public void writeRS() throws Exception {
		 
		Tree tree = new Tree();
			
		tree.add(new Edge(new QName("test"),new InnerNode()));
			
		tree.add(new Edge(new QName("test2"),new InnerNode()));
			
//		StatefulQuery queryWrite = writeSource().withId(SOURCE_ID).build();
//		TWriter client_writer = TServiceFactory.writer().matching(queryWrite).build();
//		queryWrite.fire();
		
	    Iterator<Tree> trees = TemplateFactory.aTreeLike(tree).generate(1000);
	    
	    Stream<Tree> stream =  Streams.convert(trees);
	    	
	    stream = store.add(stream);
	    
	    while (stream.hasNext()) {
	        try {
	        	log.debug("added:"+stream.next().id());
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	
		 
	// helper
	static void resourcePropertiesAreAccessible(EndpointReferenceType endpoint) {
		String properties = queryProperties(endpoint);
		log.info(properties);
	}
}

