package org.gcube.datatransfer.agent.library.test.tree;

import java.util.Date;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.generators.TemplateFactory;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class FillTreeSourceTest {
	
	//static String scope = "/d4science.research-infrastructures.eu/Ecosystem";
	static String scope = "/gcube/devsec";
	static TWriter client_writer =null;
	static TReader client_reader =null;
	static String SOURCE_ID="from_portlet5";
	
	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set(scope);
		System.out.println("FillTreeSourceTest - scope:"+scope);		
	}
	
	@Test
	public void process(){
		try{
		client_writer = TServiceFactory.writer().matching(TServiceFactory.writeSource().withId(SOURCE_ID).build()).build();
		client_reader = TServiceFactory.reader().matching(TServiceFactory.readSource().withId(SOURCE_ID).build()).build();
		
	//	writeRS(client_writer);
		writeRSOneByOne(client_writer);
		readRS(client_reader, Patterns.any());
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void readRS(TReader client_reader,Pattern pattern){
		Stream<Tree> stream = client_reader.get(pattern);
		while(stream.hasNext()){
			System.out.println(stream.next().toString());
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
	public static void writeRSOneByOne(TWriter client_writer){
		try{
			client_writer.add(getATree());
	
		}catch(Exception e){
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
	public static Tree getATree() throws Exception {
		Tree tree = new Tree();
		Date date = new Date();
		
		tree.add(new Edge(new QName("test"+"_"+date.getTime()),new InnerNode()));
		tree.add(new Edge(new QName("test2"+"_"+date.getTime()),new InnerNode()));
		return tree;
	}
	public static void sleepFor(int sec){
		try{
			Thread.sleep(sec*1000);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
