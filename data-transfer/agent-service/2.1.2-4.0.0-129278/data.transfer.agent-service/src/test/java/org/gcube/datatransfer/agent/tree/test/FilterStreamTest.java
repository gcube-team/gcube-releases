package org.gcube.datatransfer.agent.tree.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.streams.handlers.IgnoreHandler;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.generators.TemplateFactory;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.streams.Counter;
import org.junit.BeforeClass;
import org.junit.Test;

public class FilterStreamTest {
	
	static Pattern pattern = Patterns.any(); 
	static String conclusionMessage="";

	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set(TestUtils.VO.toString());
		System.out.println(ServiceContext.getContext().getStatus());		
	}

	@Test
	public void wholeProcess() throws Exception {
		readThroughPipe();		
		System.out.println("******** conclusionMessage ********\n"+conclusionMessage);
	}

	public static void readThroughPipe() throws Exception {
		try {				
			Counter countAllTrees = new Counter();
			StreamCopyListenerForTesting listener = new StreamCopyListenerForTesting(countAllTrees);
			IgnoreHandler IGNORE_POLICY = new IgnoreHandler();
			
			Stream<Tree> stream = getSomeTrees();				
			stream = Streams.guard(stream).with(IGNORE_POLICY);
			stream = Streams.pipe(stream).through(countAllTrees);	
			stream = Streams.monitor(stream).with(listener);
						
			List<String> treeIds = new ArrayList<String>();
			while(stream.hasNext()){
				Tree tree = stream.next();
				treeIds.add(tree.toString());
			}			
			System.out.println("print tree ids - size="+treeIds.size());
			for(String tmp: treeIds)System.out.println(tmp);
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
	
}

