package org.gcube.data.analysis.dataminermanagercl.test;

import java.util.List;

import junit.framework.TestCase;

import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.Constants;
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
public class DataMinerParametersTest extends TestCase {
	private static Logger logger = LoggerFactory
			.getLogger(DataMinerParametersTest.class);

	
	public void testExecute() {

		if (Constants.TEST_ENABLE) {
			logger.debug("Test Parameters");
			try {
				DataMinerService dataMinerService = new DataMinerService();
				SClient sClient = dataMinerService.getClient();
				Operator dBScanOperator = sClient
						.getOperatorById(OperatorId.DBSCAN.toString());

				if (dBScanOperator == null) {
					logger.error("Operator not found");
				} else {
					logger.debug("Operator Name: " + dBScanOperator.getName()
							+ " (" + dBScanOperator.getId() + ")");
					logger.debug("Operator: " + dBScanOperator);
					List<Parameter> inputParameters = sClient
							.getInputParameters(dBScanOperator);
					logger.debug("Parameters: " + inputParameters);
					for (Parameter parameter : inputParameters) {
						logger.debug("Input Parameter:" + parameter);
					}

					List<Parameter> outputParameters = sClient
							.getOutputParameters(dBScanOperator);
					logger.debug("Output Parameters: " + inputParameters);
					for (Parameter parameter : outputParameters) {
						logger.debug("Output Parameter:" + parameter);
					}

				}

				assertTrue("Success", true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(),e);
				assertTrue("Error", false);
			}

		} else {
			assertTrue("Success", true);
		}
	}

}
