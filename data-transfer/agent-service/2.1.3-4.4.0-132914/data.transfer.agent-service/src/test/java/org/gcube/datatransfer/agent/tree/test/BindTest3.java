package org.gcube.datatransfer.agent.tree.test;

import static org.gcube.datatransfer.agent.tree.test.TestUtils.SOURCE_ID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.tml.proxies.TBinder;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.generators.TemplateFactory;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.streams.IdRemover;
import org.gcube.datatransfer.agent.impl.streams.Counter;
import org.junit.BeforeClass;
import org.junit.Test;

public class BindTest3 {

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

		createReadersAndWritersForSources();

		//write some trees in the sources and read
		//writeRS(client_writer);		
		//readRS(client_reader, SOURCE_ID);		
		
		//transfer from source1 -> source2
		//transfer(client_reader,client_writer2,"Transfer from S1->S2");
		//read
		readRS(client_reader, SOURCE_ID);		
		readRS(client_reader2, SOURCE_ID+"2");
		
		System.out.println("\n\n\n\n******** conclusionMessage ********\n"+conclusionMessage);
	}

	public static void createReadersAndWritersForSources() {
		try{				
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
			Iterator<Tree> iter = getSomeTrees();
			while(iter.hasNext()){
				client_writer.add(iter.next());
			}
						
			sleepFor(5);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void readRS(TReader client_reader, String sourceId) throws Exception {
		try {
			Stream<Tree> stream = client_reader.get(pattern);
					
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
	public static Iterator<Tree> getSomeTrees() throws Exception {
		Tree tree = new Tree();
		tree.add(new Edge(new QName("test"),new InnerNode()));
		tree.add(new Edge(new QName("test2"),new InnerNode()));

		Iterator<Tree> trees = TemplateFactory.aTreeLike(tree).generate(10);
		//Stream<Tree> stream =  Streams.convert(trees);
		//return stream;
		return trees;
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

