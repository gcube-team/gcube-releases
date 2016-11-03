package org.gcube.datatransfer.agent.tree.test;

import static org.gcube.datatransfer.agent.tree.test.TestUtils.SOURCE_ID;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.mycontainer.Gar;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.streams.handlers.IgnoreHandler;
import org.gcube.data.tml.proxies.BindRequest;
import org.gcube.data.tml.proxies.Binding;
import org.gcube.data.tml.proxies.TBinder;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.generators.TemplateFactory;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.streams.Counter;
import org.gcube.datatransfer.agent.impl.streams.IdRemover;
import org.junit.BeforeClass;
import org.junit.Test;

//@RunWith(MyContainerTestRunner.class)
public class BindTest2 {

	//@Deployment
	static Gar myGar  = new Gar(new File("src/test/resources/tree-manager-service.gar"));

	//@Deployment
	static Gar dependencies = new Gar("dependencies").addLibraries("src/test/resources");

	static TWriter client_writer =null;
	static TReader client_reader =null;
	static W3CEndpointReference readerRef; 
	static W3CEndpointReference writerRef; 

	static TWriter client_writer2 =null;
	static TReader client_reader2 =null;
	static W3CEndpointReference readerRef2; 
	static W3CEndpointReference writerRef2; 
	static Pattern pattern = Patterns.any(); 
	static String conclusionMessage="";

	static String SOURCE_ID="from_portlet";
	
	static String remoteAddress = "node6.d.d4science.research-infrastructures.eu";
	static int remotePort = 8080;
	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set(TestUtils.VO.toString());
		System.out.println(ServiceContext.getContext().getStatus());		
	}

	@Test
	public void wholeProcess() throws Exception {
		//initialization
		createSources();
		createReadersAndWritersForSources();
		if(client_writer==null){System.out.println("client_writer==null");return;}
		System.out.println("client_writer.toString()="+client_writer.toString());
		//write some trees in the sources and read
	//	writeRS(client_writer);
		//writeRS(client_writer2);
		readRS(client_reader, SOURCE_ID);		
	//	readRS(client_reader2, SOURCE_ID+"2");
		
		//transfer from source1 -> source2
	//	transfer(client_reader,client_writer2,"Transfer from S1->S2");
		//read
	//	readRS(client_reader, SOURCE_ID);		
	//	readRS(client_reader2, SOURCE_ID+"2");
		
		//transfer from source2 -> source1
	//	transfer(client_reader2,client_writer,"Transfer from S2->S1");
		//read
	//	readRS(client_reader, SOURCE_ID);		
	//	readRS(client_reader2, SOURCE_ID+"2");
		
		System.out.println("******** conclusionMessage ********\n"+conclusionMessage);
	}

	public static void createSources() {
		try {
			ScopeProvider.instance.set(TestUtils.VO.toString());
			//TBinder binder = TServiceFactory.binder().at("localhost", 9999).build();
			
			TBinder binder = TServiceFactory.binder().at(remoteAddress, remotePort).build();
			BindSource request = new BindSource(SOURCE_ID);
			BindRequest params = new BindRequest("tree-repository",request.toElement());

			Binding binding = binder.bind(params).get(0);
			readerRef = binding.readerRef();  //use this to create a TReader
			writerRef = binding.writerRef(); //use this to create a TWriter		
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void createReadersAndWritersForSources() {
		try{
			System.out.println("***********************\n" +
					"readerRef="+readerRef+"\n" +
					"writerRef="+writerRef+"\n" +
					"readerRef2="+readerRef2+"\n" +
					"writerRef2="+writerRef2+"\n");
			
/*			client_reader = TServiceFactory.reader().at(readerRef).build();
			client_writer = TServiceFactory.writer().at(writerRef).build();
			
			client_reader2 = TServiceFactory.reader().at(readerRef2).build();
			client_writer2 = TServiceFactory.writer().at(writerRef2).build();*/
			client_reader = TServiceFactory.reader().matching(TServiceFactory.readSource().withId(SOURCE_ID).build()).build();
			client_writer = TServiceFactory.writer().matching(TServiceFactory.writeSource().withId(SOURCE_ID).build()).build();
			client_reader2 = TServiceFactory.reader().matching(TServiceFactory.readSource().withId(SOURCE_ID+"2").build()).build();
			client_writer2 = TServiceFactory.writer().matching(TServiceFactory.writeSource().withId(SOURCE_ID+"2").build()).build();

			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void writeRS(TWriter client_writer){
		try{
			Stream<Tree> treeStream = Streams.pipe(getSomeTrees()).through(new IdRemover());	
			client_writer.add(treeStream);
			
			while(!treeStream.isClosed())sleepFor(2);
			sleepFor(2);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void readRS(TReader client_reader, String sourceId) throws Exception {
		try {				
		//	Stream<Tree> stream = client_reader.get(pattern);
			Counter countAllTrees = new Counter();
			Counter countTreesWithoutException = new Counter();
			
			Counter countAllTreesAfterWrite = new Counter();
			Counter countTreesWithoutExceptionAfterWrite = new Counter();
			
			Counter counterAllTrees = new Counter();
			StreamCopyListenerForTesting listener = new StreamCopyListenerForTesting();
			IgnoreHandler IGNORE_POLICY = new IgnoreHandler();
			
			Stream<Tree> stream = Streams.pipe(client_reader.get(pattern)).through(new IdRemover());				
			stream = Streams.guard(stream).with(IGNORE_POLICY);
			stream = Streams.pipe(stream).through(counterAllTrees);	
			stream = Streams.monitor(stream).with(listener);
						
			Stream<Tree> streamReturned = client_writer.add(stream);
			streamReturned = Streams.pipe(streamReturned).through(countAllTreesAfterWrite);	
			streamReturned = Streams.guard(streamReturned).with(IGNORE_POLICY);
			streamReturned = Streams.pipe(streamReturned).through(countTreesWithoutExceptionAfterWrite);	
			streamReturned = Streams.monitor(streamReturned).with(listener);
			
			List<String> treeIds = new ArrayList<String>();
			while(stream.hasNext()){
				Tree tree = stream.next();
				treeIds.add(tree.id());
			}			
			printAndStoreMsg(treeIds,sourceId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void transfer(TReader client_reader, TWriter client_writer,String msg) {
		try {				
			Stream<Tree> filtered =  Streams.pipe(client_reader.get(pattern)).through(new IdRemover());	
			client_writer.add(filtered);	
			
			/*Stream<Tree> filtered =  Streams.pipe(client_reader.get(pattern)).through(new IdRemover());	
			//input stream 
			TreeGenerator forCountingInputTrees = new TreeGenerator();
			filtered=Streams.pipe(filtered).through(forCountingInputTrees);
			
			//stream after the add
			Stream<Tree> afterTheAdd = client_writer.add(filtered);	
			if(!afterTheAdd.isClosed()){
			TreeGenerator forCountingFinalTransferredTrees = new TreeGenerator();
			filtered=Streams.pipe(afterTheAdd).through(forCountingFinalTransferredTrees);
			//consume them
			
			while(afterTheAdd.hasNext()){
				Tree tree = afterTheAdd.next();
			}
			System.out.println("input length="+forCountingInputTrees+" - output length="+forCountingFinalTransferredTrees);
			}else System.out.println("afterTheAdd is closed");*/
			
			conclusionMessage=conclusionMessage+msg+"\n";
			
			while(!filtered.isClosed())sleepFor(2);
			sleepFor(2);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	public static void sleepFor(int sec){
		try{
			Thread.sleep(sec*1000);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static Stream<Tree> getSomeTrees() throws Exception {
		Tree tree = new Tree();
		tree.add(new Edge(new QName("test"),new InnerNode()));
		tree.add(new Edge(new QName("test2"),new InnerNode()));

		Iterator<Tree> trees = TemplateFactory.aTreeLike(tree).generate(10);
		Stream<Tree> stream =  Streams.convert(trees);
		return stream;
	}
	
	public static void printAndStoreMsg(List<String> treeIds, String sourceId){
		String msg = "******** sourceID="+sourceId+" ********\n" +
				"treeIds.size()="+treeIds.size()+"\nprint id's:";
		System.out.println(msg);
		conclusionMessage=conclusionMessage+msg+"\n";
		if(treeIds.size()>0){
			msg="";
			for(String tmp:treeIds){
				msg=msg+tmp+",";
				System.out.print(tmp+",");
			}
			msg=msg+"\n";
			System.out.println("");
		}
		conclusionMessage=conclusionMessage+msg+"\n";
	}

}

