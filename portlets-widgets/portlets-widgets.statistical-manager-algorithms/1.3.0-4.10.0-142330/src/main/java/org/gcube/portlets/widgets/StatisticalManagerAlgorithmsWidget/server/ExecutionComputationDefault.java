/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server;



import java.util.List;

import javax.servlet.http.HttpSession;


import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationConfig;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntries;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMInputEntry;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server.util.SessionUtil;


public  class ExecutionComputationDefault implements ExecutionComputation {

	@Override
	public String getId() {
		
		return "ExecutionComputationDefault";
	}

	@Override
	public String startComputation(HttpSession httpSession, Operator operator, String computationTitle,
			String computationDescription) throws Exception {
		
		String catId = operator.getCategory().getId();
		// SMComputation computation = new SMComputation(operator.getId(),
		// ComputationalAgentClass.fromString(cat), computationTitle); // TODO

		// create computation config
		SMComputationConfig config = new SMComputationConfig();

		// create list SMEntries
		List<Parameter> parameters = operator.getOperatorParameters();
		SMInputEntry[] list = new SMInputEntry[parameters.size()];
		int i = 0;

		for (Parameter p : operator.getOperatorParameters())
			list[i++] = new SMInputEntry(p.getName(), p.getValue());
		config.parameters(new SMEntries(list));
		config.algorithm(operator.getId());

		// create a computation request
		SMComputationRequest request = new SMComputationRequest();
		request.user(SessionUtil.getUsername(httpSession));
		request.title(computationTitle);
		request.description(computationDescription);
		request.config(config);

		try {
			StatisticalManagerFactory factory = SessionUtil.getFactory(httpSession);

			String computationId = factory.executeComputation(request);
			return computationId;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	



}
