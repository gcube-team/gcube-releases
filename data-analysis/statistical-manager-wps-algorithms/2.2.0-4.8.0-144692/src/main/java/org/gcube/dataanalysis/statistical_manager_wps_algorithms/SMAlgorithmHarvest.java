package org.gcube.dataanalysis.statistical_manager_wps_algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.dataanalysis.statistical_manager_wps_algorithms.output.OutputBuilderUtil;
import org.gcube.dataanalysis.statistical_manager_wps_algorithms.utils.SMutils;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.ExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class SMAlgorithmHarvest extends SMAlgorithm {

	private static final Logger logger = LoggerFactory
			.getLogger(SMAlgorithmHarvest.class);

	// public SMAlgorithmHarvest(String agorithmID, String scope) {
	// super(agorithmID, scope);
	// // TODO Auto-generated constructor stub
	// }

	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputData)
			throws ExceptionReport {
		logger.info("Run: "+inputData);
		
		outputBuilder = new OutputBuilderUtil(algorithmId);
		Map<String, IData> wpsResultMap = new HashMap<String, IData>();

		try {
			List<Parameter> list = manageInputParameter(inputData);
			ComputationId computationId = executeComputation(list);
			monitoringComputation(computationId, wpsResultMap);

			return wpsResultMap;
		} catch (ExceptionReport e) {
			throw e;
		} catch (Exception e) {
			throw new ExceptionReport("DataMiner operation failed:"
					+ e.getLocalizedMessage(), "SM");
		}

	}

	private void monitoringComputation(final ComputationId computationId,
			final Map<String, IData> wpsResultMap) throws Exception {
		logger.debug("monitoringComputation()");
		boolean notEnd = true;
		while (notEnd) {
			ComputationStatus computationStatus = SMutils.getSClient()
					.getComputationStatus(computationId);
			switch (computationStatus.getStatus()) {
			case ACCEPTED:
				logger.info("Operation Accepted");
				break;
			case CANCELLED:
				notEnd = false;
				logger.info("Operation Cancelled");
				break;
			case COMPLETE:
				notEnd = false;
				logger.debug("Operation Completed");
				retrieveOutput(computationId, wpsResultMap);
				logger.debug("Output Retrieved");
				break;
			case FAILED:
				notEnd = false;
				logger.error("Operation Failed");
				logger.error(computationStatus.getMessage(),
						computationStatus.getError());
				Exception e = new Exception("Operation Failed: "
						+ computationStatus.getMessage());
				throw e;
			case RUNNING:
				logger.debug("Operation Running: "
						+ computationStatus.getPercentage());
				break;
			default:
				break;
			}
			Thread.sleep(2000);
		}
	}

	private void retrieveOutput(ComputationId computationId,
			final Map<String, IData> wpsResultMap) throws ExceptionReport {
		try {
			logger.debug("retrieveOutput()");
			GenericFileDataBinding res = outputBuilder
					.getXmlFileDataBinding(SMutils
							.getOutputResourceByComputationId(computationId));
			wpsResultMap.put("resultList", res);

		} catch (Exception e) {
			logger.error(
					"DataMiner error retrieving output:"
							+ e.getLocalizedMessage(), e);
			throw new ExceptionReport("DataMiner error retrieving output:"
					+ e.getMessage(), "SM");
		}
	}

	public Class<?> getInputDataType(String id) {
		return LiteralStringBinding.class;

	}

	public Class<?> getOutputDataType(String id) {

		return GenericFileDataBinding.class;

	}

	@Override
	public List<String> getInputIdentifiers() {
		logger.debug("getInputIdentifiers()");
		List<String> identifierList = new ArrayList<String>();
		try {
			logger.info("Search input identifiers in: "+algorithmId);
			SClient sClient = SMutils.getSClient();
			Operator operator = sClient.getOperatorById(algorithmId);
			List<Parameter> parameters = sClient
					.getInputParameters(operator);
			logger.info("Parameters Retrieved: "+parameters);
			for (Parameter p : parameters) {
				defaultParameterValue.put(p.getName(), p);
				identifierList.add(p.getName());
			}
		
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return identifierList;

		}
		
		logger.info("Input IdentifierList: " + identifierList);
		return identifierList;
	}

	@Override
	public List<String> getOutputIdentifiers() {
		logger.debug("getOutputIdentifiers()");
		List<String> identifierList = new ArrayList<String>();
		identifierList.add("resultList");
		logger.info("Output Identifiers: " + identifierList);
		return identifierList;
	}

}
