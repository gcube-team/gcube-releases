package org.gcube.datatransformation.adaptors.tree;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.UUID;

import javax.xml.stream.XMLStreamException;

//import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransformation.adaptors.tree.queue.MemoryFileBackedQueue;
//import org.gcube.data.streams.Stream;
//import org.gcube.data.tml.proxies.TReader;
//import org.gcube.data.tml.proxies.TServiceFactory;
//import org.gcube.data.trees.data.Tree;
//import org.gcube.data.trees.io.XMLBindings;
//import org.gcube.data.trees.patterns.Patterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Test {

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	
	public static void main(String[] args) throws IOException, XMLStreamException {
		
		try {
			testQueues();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	
	private static void testQueues() throws IOException, XMLStreamException{
/*		
		PrintWriter out = new PrintWriter(System.out);
		
//		String collectionID = "78e01c59-35b9-48ae-b20d-ebf6d403670e";
//		String scope = "/gcube/devNext/NextNext";
		/////////////
//		String collectionID = "976dc1bd-b0f0-4ea5-a987-991706527dd9"; //WHOAS
		String collectionID = "c9076f3f-be8d-43e2-9f02-de35e6d8f72c";  //ZooKeys
		String scope = "/gcube/devNext";
		
		
		String collectionName = null;
//		String queued = "false";
		
		TCollectionReader treeCollectionReader;
//		if(collectionName == null)
			treeCollectionReader = new TCollectionReader(collectionID, scope);
//		else
//			treeCollectionReader = new TCollectionReader(collectionID, scope, collectionName);
		
//		if("false".equalsIgnoreCase(queued)) //if user provides queued=false
			treeCollectionReader.readPrintCollections(out);
//		else //default case, queued
//			treeCollectionReader.readPrintCollectionsQueued2(out);
		
		out.flush();
		out.close();
	*/	
	}
	
	
	

	
}

