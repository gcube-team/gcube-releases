package org.gcube.common.informationsystem.client.eximpl.tests;

import java.util.Iterator;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.QueryParameter;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;


import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.runninginstance.Endpoint;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.common.core.utils.logging.GCUBELog;

public class ISClientTest {

	static GCUBELog logger = new GCUBEClientLog(ISClientTest.class);
	
	public static void main(String[] args) throws Exception {
		try{
		ISClient client = GHNContext.getImplementation(ISClient.class);//get IS client implementation
	
		GCUBEScope scope = GCUBEScope.getScope("/gcube/devsec");//predefine scope for all queries below
		
		//build a free-form query: maximum generality but also full exposure to implementation details
		GCUBEGenericQuery query1 = client.getQuery(GCUBEGenericQuery.class);// get query implementation
		String exp1 = "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';"+
		"for $result in collection('/db/Profiles')//Document/Data/is:Profile/Resource return $result/ID";
		query1.setExpression(exp1);//set query expression
		for (XMLResult result : client.execute(query1,scope)) logger.debug(result.evaluate("/ID/text()"));//displays a singleton list

		//use a predefined query: generality and exspure to implementation in degrees proportional to customisability of its expression
		//e.g. from the documentation of the reference implementation it turns out that 
		//this pre-defined query is equivalent to the one above if not further customised. However, it abstracts over 
		//namespaces and non-relevant db structure
		GCUBEGenericQuery query2 = client.getQuery("GCUBEResourceQuery");
		for (XMLResult result : client.execute(query2,scope)) logger.debug(result.evaluate("/ID/text()"));
		
		//a bit of customisation goes a long way whilst keeping the previous abstractions
		query2.addParameters(new QueryParameter("RESULT","$result/Type")); //specialise result format
		for (XMLResult result : client.execute(query2, scope)) logger.debug(result.evaluate("/Type/text()"));
				
		//specialise to GCUBE Running Instances (NB. queries can be composed incrementally, possibly by different objects in different methods)
		query2.addParameters(new QueryParameter("TYPE",GCUBERunningInstance.TYPE)); 
		for (XMLResult result : client.execute(query2,scope)) logger.debug(result.evaluate("/Type/text()"));
		
		//introduce a filter (NB. parameters can be added in batches) 
		query2.addParameters(new QueryParameter("TYPE",GCUBERunningInstance.TYPE), //ovverride previous setting
							 new QueryParameter("FILTER","$result/Profile/ServiceClass/string() eq 'Annotation'"),
							 new QueryParameter ("RESULT", "$result/Profile/Description")); //any Xquery condition on $result would do
		for (XMLResult result : client.execute(query2,scope)) logger.debug(result.evaluate("//Description")); //displays a singleton list
		
		// use a typed query: result processing with object models
		// NB. trade-off efficiency of result transfer for convenience of processing and abstraction over implementation
		GCUBERIQuery query3 = client.getQuery(GCUBERIQuery.class);
		for (GCUBERunningInstance instance : client.execute(query3,scope)) //show all endpoints of all RIs
			for (Endpoint endpoint : instance.getAccessPoint().getRunningInstanceInterfaces().getEndpoint())
				logger.debug(instance.getServiceName()+":"+endpoint.getEntryName());

		// can exert more control whilst retaining all previous advantages
		query3.addAtomicConditions(new AtomicCondition("//ServiceClass","InformationSystem"));
		for (GCUBERunningInstance instance : client.execute(query3,scope))
			for (Endpoint endpoint : instance.getAccessPoint().getRunningInstanceInterfaces().getEndpoint())
				logger.debug(endpoint.getEntryName());
		
		query3.clearConditions();
		//any Xquery condition on $result would do
		query3.addGenericCondition("$result/Profile/ServiceName/string() eq 'GHNManager' or $result/Profile/ServiceName/string() eq 'SoftwareRepository'");
		for (GCUBERunningInstance instance : client.execute(query3,scope)) //show all endpoints of all RIs
			for (Endpoint endpoint : instance.getAccessPoint().getRunningInstanceInterfaces().getEndpoint())
				logger.debug(instance.getServiceName()+":"+endpoint.getEntryName());
		
		//moving to wsresources: same possible approaches...
		//...pre-defined query
		GCUBEGenericQuery query4 = client.getQuery("GCUBEWSResourceQuery");
		for (XMLResult result : client.execute(query4,scope)) logger.debug(result);
		
		//...customisable...
		query4.addParameters(new QueryParameter("FILTER", "$result/gc:ServiceClass/string() eq 'Samples'"),
							 new QueryParameter("RESULT", "$result/gc:ServiceID"));
		for (XMLResult result : client.execute(query4,scope)) logger.debug(result.evaluate("/ServiceID/text()"));
	
		// ...or a typed query where results are object models of RPDDocuments
		//with getters and setters for gCUBE RPs and endpoints and a XPath engine
		//for custom RPs and more generally to resolve arbitrary expressions.
		WSResourceQuery query5 = client.getQuery(WSResourceQuery.class);
		query5.addAtomicConditions(new AtomicCondition("/gc:ServiceClass","Samples"),
									new AtomicCondition("/gc:ServiceName","SampleService"));
		for (RPDocument d : client.execute(query5,scope)) {
			logger.debug(d);
			logger.debug(d.getEndpoint());
			logger.debug(d.getServiceName());
			logger.debug(d.getServiceClass());
			logger.debug(d.getGHNID());
			logger.debug(d.getRIID());
			logger.debug(d.getScope());
			logger.debug(d.getTerminationTime());			
			logger.debug(d.evaluate("/Visits/text()"));
		}
	/*
		GCUBEGenericQuery query =  client.getQuery("GCUBEFullWSQuery");
		query.addParameters(
				 new QueryParameter("FILTER", "$result/Source eq 'http://dlib33.isti.cnr.it:8002/wsrf/services/gcube/common/vremanagement/Deployer'"),
				 new QueryParameter("RESULT", "$result/SourceKey")
		);
				
		List<XMLResult> results = client.execute(query, GCUBEScope.getScope("/gcube/devsec")); 
		for (XMLResult r : results) System.out.println(r.toString());
		*/
		query3 = client.getQuery(GCUBERIQuery.class);
		logger.debug("--- query by ref ---");
		Iterator<GCUBERunningInstance> it=client.executeByRef(query3,scope).iterator();
		while (it.hasNext())
			logger.debug(it.next().getGHNID());
		}catch(Exception e){logger.debug(e);}
	}
}
