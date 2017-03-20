package org.gcube.dataanalysis.statistical_manager_wps_algorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.dataanalysis.statistical_manager_wps_algorithms.output.OutputBuilderUtil;
import org.gcube.dataanalysis.statistical_manager_wps_algorithms.utils.SMutils;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralBooleanBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralFloatBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralLongBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SMAlgorithm extends AbstractSelfDescribingAlgorithm {

	private static final Logger logger = LoggerFactory
			.getLogger(SMAlgorithm.class);

	protected String algorithmId;
	protected OutputBuilderUtil outputBuilder;
	protected HashMap<String, Parameter> defaultParameterValue = new HashMap<String, Parameter>();;

	@Override
	public boolean processDescriptionIsValid() {
		logger.debug("validation*******");
		return true;
	}

	protected ComputationId executeComputation(List<Parameter> parameters)
			throws Exception {
		logger.debug("executeComputation()");
		SClient sClient = SMutils.getSClient();
		Operator operator = sClient.getOperatorById(algorithmId);
		logger.debug("Parameters set: " + parameters);
		sClient.getInputParameters(operator);
		operator.setOperatorParameters(parameters);

		ComputationId computationId = sClient.startComputation(operator);
		logger.debug("Started ComputationId: " + computationId);
		return computationId;
	}

	/*
	 * public ArrayList<String> manageOutput(SMAbstractResource
	 * abstractResource) { ArrayList<String> output = new ArrayList<String>();
	 * SMResource smResource = abstractResource.resource(); int
	 * resourceTypeIndex = smResource.resourceType(); SMResourceType smResType =
	 * SMResourceType.values()[resourceTypeIndex];
	 * 
	 * switch (smResType) {
	 * 
	 * case FILE: SMFile fileRes = (SMFile) smResource; // resource = new
	 * FileResource(fileRes.url(), fileRes.mimeType()); break; case OBJECT:
	 * SMObject objRes = (SMObject) smResource;
	 * 
	 * if (objRes.name().contentEquals(PrimitiveTypes.MAP.toString())) { //
	 * resource = new MapResource(objRes.url()); } else if
	 * (objRes.name().contentEquals( PrimitiveTypes.IMAGES.toString())) { //
	 * resource = new ImagesResource(objRes.url()); } else // resource = new
	 * ObjectResource(objRes.url());
	 * 
	 * break; case TABULAR: SMTable tableRes = (SMTable) smResource; // resource
	 * = new TableResource(tableRes.template()); break; }
	 * 
	 * return output; }
	 */

	protected String formatParameterForSM(String id, IData data)
			throws Exception {
		logger.debug("formatParameterForSM()");
		String parameter = new String();
		Parameter prm = null;
		if (defaultParameterValue.containsKey(id)) {
			prm = defaultParameterValue.get(id);
		}

		if (prm == null) {
			return "";
		}

		switch (prm.getTypology()) {
		case COLUMN:
			break;
		case COLUMN_LIST:
			break;
		case DATE:
			break;
		case ENUM:
			parameter = ((LiteralStringBinding) data).getPayload();
		case FILE:
			break;
		case LIST:
			break;
		case OBJECT:
			parameter = retrieveObjectParameter(data);
			break;
		case TABULAR:
			break;
		case TABULAR_LIST:
			break;
		case TIME:
			break;
		case WKT:
			break;
		default:
			break;

		}

		logger.debug(parameter);
		return parameter;
	}

	private String retrieveObjectParameter(IData data) {
		Class<?> inputDataTypeClass = data.getClass();

		if (inputDataTypeClass == LiteralBooleanBinding.class) {
			boolean bool = ((LiteralBooleanBinding) data).getPayload();
			if (bool)
				return "true";
			else {
				return "false";

			}

		} else if (inputDataTypeClass == LiteralIntBinding.class) {
			Integer i = ((LiteralIntBinding) data).getPayload();
			return i.toString();

		} else if (inputDataTypeClass == LiteralStringBinding.class) {
			return ((LiteralStringBinding) data).getPayload();

		} else if (inputDataTypeClass == LiteralDoubleBinding.class) {
			Double i = ((LiteralDoubleBinding) data).getPayload();
			return i.toString();

		} else if (inputDataTypeClass == LiteralFloatBinding.class) {
			Float i = ((LiteralFloatBinding) data).getPayload();
			return i.toString();

		} else {
			return "";
		}
	}

	protected Class<?> retrieveInputDataTypeClass(ObjectParameter objectParameter) {
		
		Class<?>inputDataTypeClass = LiteralStringBinding.class;
		
		if(objectParameter!=null && objectParameter.getType()!=null &&
				!objectParameter.getType().isEmpty()){
			String type=objectParameter.getType();
			logger.debug("InputDataTypeClass: "+type);
			switch(type){
			case "java.lang.Boolean":
				inputDataTypeClass=LiteralBooleanBinding.class;
				break;
			case "java.lang.Integer":
				inputDataTypeClass=LiteralIntBinding.class;
				break;
			case "java.lang.Float":
				inputDataTypeClass=LiteralFloatBinding.class;
				break;
			case "java.lang.Double":
				inputDataTypeClass=LiteralDoubleBinding.class;
				break;
			case "java.lang.Long":
				inputDataTypeClass=LiteralLongBinding.class;
				break;
			default:
				break;
			}
			
			
		}
		return inputDataTypeClass;
		
	}

	
	
	protected List<Parameter> manageInputParameter(
			Map<String, List<IData>> inputData) throws Exception {
		logger.debug("Manage Input Parameter: "+inputData);
		List<Parameter> inputParameters = null;
		SClient sClient = SMutils.getSClient();
		Operator operator = sClient.getOperatorById(algorithmId);
		inputParameters = sClient.getInputParameters(operator);
		logger.debug("Input Parameters for algorithm: "+algorithmId);
		logger.debug("Input Parameters: "+inputParameters);
		for (Parameter parameter : inputParameters) {
			List<IData> iDatas = inputData.get(parameter.getName());
			if (iDatas != null && iDatas.size() > 0) {
				String value=retrieveObjectParameter(iDatas.get(0));
				parameter.setValue(value);
			}
		}
		logger.debug("Manage Input Parameter retrieved: "+inputParameters);
		return inputParameters;

	}
	
	
}
