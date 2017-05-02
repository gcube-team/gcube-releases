package org.gcube.data.analysis.dataminermanagercl.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitor;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitorListener;
import org.gcube.data.analysis.dataminermanagercl.shared.Constants;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.MapResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ListDBNameTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(ListDBNameTest.class);

	public void testExecute() {

		if (Constants.TEST_ENABLE) {
			logger.debug("Test ListDBName");
			try {
				DataMinerService dataMinerService=new DataMinerService();
				SClient sClient = dataMinerService.getClient();
				Operator operator = sClient
						.getOperatorById(OperatorId.LISTDBNAMES.toString());

				if (operator == null) {
					logger.error("Operator not found");
				} else {
					logger.debug("Operator Name: " + operator.getName()
							+ " (" + operator.getId() + ")");
					logger.debug("Operator: " + operator);
					List<Parameter> parameters = sClient
							.getInputParameters(operator);
					logger.debug("Parameters: " + parameters);
					for (Parameter parameter : parameters) {
						logger.debug("Parameter:[Name=" + parameter.getName()
								+ ", Typology=" + parameter.getTypology() + "]");
					}

					createRequest(operator);
					logger.debug("Start Computation");
					ComputationId computationId = sClient
							.startComputation(operator);
					logger.debug("Started ComputationId: " + computationId);
					monitoringComputation(computationId, sClient);
				}

				assertTrue("Success", true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
				assertTrue("Error", false);
			}

		} else {
			assertTrue("Success", true);
		}
	}

	private void monitoringComputation(final ComputationId computationId,
			final SClient sClient) {

		DMMonitorListener listener = new DMMonitorListener() {

			@Override
			public void running(double percentage) {
				logger.debug("Operation Running: " + percentage);

			}

			@Override
			public void failed(String message, Exception exception) {
				logger.error("Operation Failed");
				logger.error(message);
				logger.error(exception.getStackTrace().toString());

			}

			@Override
			public void complete(double percentage) {
				logger.debug("Operation Completed");
				logger.debug("Perc: " + percentage);
				retrieveOutput(computationId, sClient);

			}

			@Override
			public void cancelled() {
				logger.debug("Operation Cancelled");
			}

			@Override
			public void accepted() {
				logger.debug("Operation Accepted");

			}
		};

		DMMonitor dmMonitor = new DMMonitor(computationId, sClient);
		dmMonitor.add(listener);
		dmMonitor.start();
		

	}

	private void retrieveOutput(ComputationId computationId, SClient sClient) {
		try {
			OutputData output = sClient
					.getOutputDataByComputationId(computationId);
			logger.debug("Output: " + output);
			Resource resource=output.getResource();
			if(resource.isMap()){
				MapResource mapResource=(MapResource) resource;
				for(String key: mapResource.getMap().keySet()){
					logger.debug("Entry: "+key+" = "+mapResource.getMap().get(key));
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBNAMES
	 * &DataInputs=MaxNumber=-1;
	 *
	 * @param operator
	 */
	private void createRequest(Operator operator) {
		logger.debug("Create Request");

		ObjectParameter maxNumber = new ObjectParameter();
		maxNumber.setName("MaxNumber");
		maxNumber.setValue("-1");

		List<Parameter> parameters = new ArrayList<>();
		
		parameters.add(maxNumber);
		
		logger.debug("Parameters set: " + parameters);
		operator.setOperatorParameters(parameters);

	}

}
