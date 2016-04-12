package org.gcube.vremanagement.resourcebroker.local.testsuite;

import java.util.List;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.XMLResult.ISResultEvaluationException;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.vremanagement.resourcebroker.impl.support.queries.QueryLoader;
import org.gcube.vremanagement.resourcebroker.impl.support.queries.QueryPath;
import org.gcube.vremanagement.resourcebroker.utils.performance.PerformanceMonitor;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.RequirementElemPath;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.RequirementRelationType;

public class RequirementTest {
	private PerformanceMonitor timer = new PerformanceMonitor();
	private GCUBEClientLog logger = null;

	public RequirementTest() {
		this.logger = new GCUBEClientLog(StandaloneISQueryTest.class);
	}

	private void testMatching(XMLResult node, Requirement[] reqs) {

		for (Requirement req : reqs) {
			try {
				List<String> results = node.evaluate(req.getEvalString());
				if (results != null && results.size() > 0) {
					String result = results.get(0);
					logger.debug("*** [OK] value of node " + req.getEvalString() + ": " + result);
				} else {
					logger.error("*** [FAIL] cannot find results for: " + req.getEvalString());
				}
			} catch (ISResultEvaluationException e) {
				logger.error("*** cannot find results for: " + req.getEvalString());
				e.printStackTrace();
			}
		}
	}

	public void doJob(String query, Requirement[] reqs) {
		timer.start();
		logger.debug("*** ISClientRequester accessing the IS");
		List<XMLResult> results = null;
		try {
			ISClient client = GHNContext.getImplementation(ISClient.class);

			GCUBEGenericQuery isQuery = null;
			isQuery = client.getQuery(GCUBEGenericQuery.class);
			isQuery.setExpression(query);
			isQuery.addParameters(new AtomicCondition("MAXWAIT", "40"));
			results = client.execute(isQuery, GCUBEScope.getScope("/gcube/devsec"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.debug("*********************************************************************************");
		logger.debug("QUERY: \n" + query);
		logger.debug("*********************************************************************************");
		if (results == null || results.isEmpty()) {
			logger.debug("NO RESULTS!!! \n\n");
		} else {
			XMLResult node = results.get(0);
			logger.debug(node + "\n\n");
			testMatching(node, reqs);
		}
		timer.stop();
		logger.debug("Elapsed time: [" + timer.getLastIntervalSecs() + "s]");
	}

	public static void main(final String args[]) throws Exception {
		String query = QueryLoader.getQuery(QueryPath.COUNT_RI_ON_DYNAMIC_ALIVE_GHN);
		Requirement[] reqs = {
				new Requirement(RequirementElemPath.OS , RequirementRelationType.NOT_EQUAL, "OSX"),
				new Requirement(RequirementElemPath.PLATFORM, RequirementRelationType.EQUAL, "i386"),
				new Requirement(RequirementElemPath.MEM_RAM_AVAILABLE, RequirementRelationType.GREATER_OR_EQUAL, "200"),
				new Requirement(RequirementElemPath.MEM_RAM_SIZE, RequirementRelationType.GREATER, "3000"),
				new Requirement(RequirementElemPath.MEM_VIRTUAL_AVAILABLE, RequirementRelationType.GREATER, "280"),
				new Requirement(RequirementElemPath.MEM_VIRTUAL_SIZE, RequirementRelationType.GREATER, "300"),
				new Requirement(RequirementElemPath.HOST, RequirementRelationType.CONTAINS, "aurora"),
				new Requirement(RequirementElemPath.DISK_SPACE, RequirementRelationType.GREATER, "800"),
				new Requirement(RequirementElemPath.LOAD1MIN, RequirementRelationType.LESS, "1"),
				new Requirement(RequirementElemPath.LOAD5MIN, RequirementRelationType.LESS, "1"),
				new Requirement(RequirementElemPath.LOAD15MIN, RequirementRelationType.LESS, "0.02"),
				new Requirement(RequirementElemPath.PROCESSOR_NUM, RequirementRelationType.NOT_EQUAL, "2"),
				new Requirement(RequirementElemPath.PROCESSOR_NUM, RequirementRelationType.EQUAL, "2"),
				new Requirement(RequirementElemPath.PROCESSOR_BOGOMIPS, RequirementRelationType.GREATER_OR_EQUAL, "3000"),
				new Requirement(RequirementElemPath.SITE_LOCATION, RequirementRelationType.CONTAINS, "Athens"),
				new Requirement(RequirementElemPath.SITE_COUNTRY, RequirementRelationType.CONTAINS, "it"),
				new Requirement(RequirementElemPath.SITE_DOMAIN, RequirementRelationType.CONTAINS, "research-infrastructures.eu"),
				new Requirement(RequirementElemPath.RUNTIME_ENV_STRING, "ANT_HOME", RequirementRelationType.CONTAINS, "/ant"),
				new Requirement(RequirementElemPath.RUNTIME_ENV_STRING, "ANT_HOME", RequirementRelationType.EQUAL, "/usr/share/ant"),
				// Here simply requires that the environment contains that key (the value is not relevant)
				new Requirement(RequirementElemPath.RUNTIME_ENV_STRING, "GLOBUS_OPTIONS", RequirementRelationType.EQUAL, null),
				new Requirement(RequirementElemPath.CUSTOM_REQUIREMENT, "/GHNDescription/Architecture[@PlatformType = 'i386']")
		};
		new RequirementTest().doJob(query, reqs);
	}
}
