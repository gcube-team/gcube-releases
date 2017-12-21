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
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.TabularParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CsquareColumnCreatorTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(CsquareColumnCreatorTest.class);

	public void testExecute() {

		if (Constants.TEST_ENABLE) {
			logger.debug("Test CSquareColumnCreator");
			try {
				DataMinerService dataMinerService = new DataMinerService();
				SClient sClient = dataMinerService.getClient();
				Operator csquareColumnCreatorOperator = sClient
						.getOperatorById(OperatorId.CSQUARE_COLUMN_CREATOR.toString());

				if (csquareColumnCreatorOperator == null) {
					logger.error("Operator not found");
				} else {
					logger.debug("Operator Name: " + csquareColumnCreatorOperator.getName()
							+ " (" + csquareColumnCreatorOperator.getId() + ")");
					logger.debug("Operator: " + csquareColumnCreatorOperator);
					List<Parameter> parameters = sClient
							.getInputParameters(csquareColumnCreatorOperator);
					logger.debug("Parameters: " + parameters);
					for (Parameter parameter : parameters) {
						logger.debug("Parameter:[Name=" + parameter.getName()
								+ ", Typology=" + parameter.getTypology() + "]");
					}

					createRequest(csquareColumnCreatorOperator);
					logger.debug("Start Computation");
					ComputationId computationId = sClient
							.startComputation(csquareColumnCreatorOperator);
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
				logger.error(message, exception);

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
			Resource resource = output.getResource();
			if (resource.isMap()) {
				MapResource mapResource = (MapResource) resource;
				for (String key : mapResource.getMap().keySet()) {
					logger.debug("Entry: " + key + " = "
							+ mapResource.getMap().get(key));
				}
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @param operator
	 */
	private void createRequest(Operator operator) {
		logger.debug("Create Request");

		TabularParameter occurencePointsTable = new TabularParameter();
		occurencePointsTable.setName("InputTable");
		occurencePointsTable
				.setValue("http://goo.gl/sdlD5a");

		ObjectParameter longitude = new ObjectParameter();
		longitude.setName("Longitude_Column");
		longitude.setValue("decimallongitude");
		
		ObjectParameter latitude = new ObjectParameter();
		latitude.setName("Latitude_Column");
		latitude.setValue("decimallatitude");
		
		ObjectParameter cSquareResolution = new ObjectParameter();
		cSquareResolution.setName("CSquare_Resolution");
		cSquareResolution.setValue("0.1");
		
		ObjectParameter outputTableName = new ObjectParameter();
		outputTableName.setName("OutputTableName");
		outputTableName.setValue("wps_csquare_column");
		

		List<Parameter> parameters = new ArrayList<>();
		parameters.add(occurencePointsTable);
		parameters.add(longitude);
		parameters.add(latitude);
		parameters.add(cSquareResolution);
		parameters.add(outputTableName);
		
		
		logger.debug("Parameters set: " + parameters);
		operator.setOperatorParameters(parameters);

	}

}
