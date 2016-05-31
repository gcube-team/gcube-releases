package org.gcube.datatransfer.agent.tree.test;


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
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.generators.TemplateFactory;
import org.gcube.data.trees.patterns.EdgePattern;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.data.trees.patterns.TreePattern;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.streams.IdRemover;
import org.gcube.datatransfer.agent.impl.streams.Counter;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetSourceTest {

	//static String remoteAddress = "node6.d.d4science.research-infrastructures.eu";
	//static int remotePort = 8080;
	static String scope = "/gcube/devsec";
	
	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set(scope);
		System.out.println(ServiceContext.getContext().getStatus());		
	}

	@Test
	public void wholeProcess() throws Exception {
		//initialization	
		Pattern pattern = designPattern();
		
		readRS("from_portlet2",pattern);		
	}

	public static Pattern designPattern(){
		Pattern pattern;
		//pattern= Patterns.any();

		pattern=Patterns.tree(Patterns.one("fasdfad",Patterns.bool()));
		return pattern;
	}
	public static void readRS(String SOURCE_ID, Pattern pattern) throws Exception {
		try {
			TReader client_reader = TServiceFactory.reader().matching(TServiceFactory.readSource().withId(SOURCE_ID).build()).build();
			
			Stream<Tree> stream = client_reader.get(pattern);
				
			String msg="";
			int num=0;
			while(stream.hasNext()){
				Tree tree = stream.next();
				for(Edge edge:tree.edges() ){					
				//	System.out.println(edge);
				}
				for(Node node:tree.children() ){					
			//		System.out.println(node.id()+" -- "+node.parent().id()+"--"+node);
				}

				msg=msg+tree+"\n";
				num++;
				//keep only one 
				//if(num>0)break;
			}			
			System.out.println("\nReceived trees:\n"+msg+"\nnum="+num+"\n");
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

}

