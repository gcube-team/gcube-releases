package org.gcube.dataanalysis.executor.tests;

import java.util.ArrayList;
import java.util.List;

/*
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;
import org.gcube.common.core.scope.GCUBEScope;
*/
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class TestGetRunningExecutor {

	/* TODO Rewrite
	private static int findNodes(String scopeString) throws Exception {
		AnalysisLogger.getLogger().debug("*****");
		GCUBEScope scope = GCUBEScope.getScope(scopeString);
		ISClient client = GHNContext.getImplementation(ISClient.class);
		WSResourceQuery wsquery = client.getQuery(WSResourceQuery.class);
		wsquery.addAtomicConditions(new AtomicCondition("//gc:ServiceName", "Executor"));
//		wsquery.addAtomicConditions(new AtomicCondition("/child::*[local-name()='Task']/name", "ExecutorScript"));
		wsquery.addAtomicConditions(new AtomicCondition("/child::*[local-name()='Task']/name[text()='ExecutorScript']", "ExecutorScript"));
		List<RPDocument> listdoc = client.execute(wsquery, scope);
		EndpointReferenceType epr = null;
		ArrayList eprs = new ArrayList<EndpointReferenceType>();
		int numberOfEP = 0;
		for (RPDocument resource : listdoc) {
			epr = resource.getEndpoint();
			numberOfEP++;
			AnalysisLogger.getLogger().debug("*** " + epr);
			eprs.add(epr);
		}
		AnalysisLogger.getLogger().debug("Found " + numberOfEP + " endpoints");
		AnalysisLogger.getLogger().debug("-> "+ eprs);
		
		return numberOfEP;
	}
	*/

	/* TODO Rewrite
	private static int findRunningNodes(String scopeString) throws Exception {

		AnalysisLogger.getLogger().debug("*****");
		GCUBEScope scope = GCUBEScope.getScope(scopeString);
		System.out.println("BROKER:"+scope.getServiceMap().getEndpoints(GHNContext.MSGBROKER).iterator().next().getAddress().toString());
		ISClient client = GHNContext.getImplementation(ISClient.class);
		WSResourceQuery wsquery = client.getQuery(WSResourceQuery.class);
		wsquery.addAtomicConditions(new AtomicCondition("//gc:ServiceName", "Executor"));
		wsquery.addAtomicConditions(new AtomicCondition(" /child::*[local-name()='State']", "RUNNING"));
		List<RPDocument> listdoc = client.execute(wsquery, scope);
		EndpointReferenceType epr = null;
		ArrayList eprs = new ArrayList<EndpointReferenceType>();
		int numberOfEP = 0;
		for (RPDocument resource : listdoc) {
			epr = resource.getEndpoint();
			numberOfEP++;
			AnalysisLogger.getLogger().debug("*** " + epr);
			eprs.add(epr);
		}
		AnalysisLogger.getLogger().debug("Found " + numberOfEP + " endpoints");
		AnalysisLogger.getLogger().debug("-> "+ eprs);
		return numberOfEP;

	}*/
	
	public static void main(String[] args) throws Exception{
		String scope = "/gcube"; 
		AnalysisLogger.setLogger("./cfg/ALog.properties");
		/*
		 * TODO Revisit
		 * findNodes(scope);
		*/
//		findRunningNodes(scope);
	}
	
}
