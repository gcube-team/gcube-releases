/**
 *
 */
package org.gcube.common.workspacetaskexecutor.dataminer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.common.workspacetaskexecutor.shared.TaskOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameter;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameterType;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ImageResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.TableResource;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ColumnListParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ColumnParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.DateParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.EnumParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.FileParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ListParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ParameterType;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.TabularListParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.TabularParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.TimeParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.WKTParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class DMConverter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 7, 2018
 */
public class DMConverter {


	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(DMConverter.class);

	/**
	 * To dm computation id.
	 *
	 * @param computationId the computation id
	 * @param startTime the start time
	 * @param endTime the end time
	 * @return the task computation
	 */
	public static TaskComputation toDMComputationId(ComputationId computationId, Long startTime, Long endTime){
		if(computationId==null)
			return null;

		return new TaskComputation(computationId.getId(), computationId.getUrlId(), computationId.getOperatorId(), computationId.getOperatorName(), computationId.getEquivalentRequest(), startTime, endTime);

	}


	/**
	 * To computation id.
	 *
	 * @param taskComputation the task computation
	 * @return the computation id
	 */
	public static ComputationId toComputationId(TaskComputation taskComputation){

		if(taskComputation==null)
			return null;

		return new ComputationId(taskComputation.getId(), taskComputation.getUrlId(), taskComputation.getOperatorId(), taskComputation.getOperatorName(), taskComputation.getEquivalentRequest());

	}


	/**
	 * To task operator.
	 *
	 * @param op the op
	 * @param inputParameters the input parameters
	 * @param outputParameters the output parameters
	 * @return the task operator
	 */
	public static TaskOperator toTaskOperator(Operator op, List<Parameter> inputParameters, List<Parameter> outputParameters){

		if(op==null)
			return null;
		//Converting input parameters
		List<TaskParameter> listInOperator = null;
		if(inputParameters!=null){
			listInOperator = new ArrayList<TaskParameter>(inputParameters.size());
			for (Parameter param: inputParameters) {
				TaskParameter tp = toTaskParameter(param);
				if(tp!=null)
					listInOperator.add(tp);
			}
		}

		//Converting output parameters
		List<TaskParameter> listOutOperator = null;
		if(outputParameters!=null){
			listOutOperator = new ArrayList<TaskParameter>(outputParameters.size());
			for (Parameter param: outputParameters) {
				TaskParameter tp = toTaskParameter(param);
				if(tp!=null)
					listOutOperator.add(tp);
			}
		}

		return new TaskOperator(op.getId(), op.getName(), op.getBriefDescription(), op.getDescription(), listInOperator, listOutOperator, op.hasImage());
	}


	/**
	 * To task parameter.
	 *
	 * @param parameter the parameter
	 * @return the task parameter
	 */
	public static TaskParameter toTaskParameter(Parameter parameter){

		if(parameter==null)
			return null;

		List<String> defaultValues = null;
		switch (parameter.getTypology()) {
		case FILE:
			FileParameter fp = (FileParameter) parameter;
			break;
		case OBJECT:
			ObjectParameter op = (ObjectParameter) parameter;
			defaultValues = new ArrayList<String>(1);
			defaultValues.add(op.getDefaultValue());
			break;
		case TABULAR:
			TabularParameter tp = (TabularParameter) parameter;
			break;
		case ENUM:
			EnumParameter ep = (EnumParameter) parameter;
			defaultValues = new ArrayList<String>(ep.getValues().size());
			defaultValues.addAll(ep.getValues());
			System.out.println("############### The VALUES ARE" +ep.getValues());
			break;
		case LIST:
			ListParameter lp = (ListParameter) parameter;
			break;
		case COLUMN:
			ColumnParameter cp = (ColumnParameter) parameter;
			defaultValues = new ArrayList<String>(1);
			defaultValues.add(cp.getDefaultColumn());
			break;
		case COLUMN_LIST:
			ColumnListParameter clp = (ColumnListParameter) parameter;
			break;
		case DATE:
			DateParameter dp = (DateParameter) parameter;
			defaultValues = new ArrayList<String>(1);
			defaultValues.add(dp.getDefaultValue());
			break;
		case TABULAR_LIST:
			TabularListParameter tlp = (TabularListParameter) parameter;
			break;
		case TIME:
			TimeParameter timep = (TimeParameter) parameter;
			defaultValues = new ArrayList<String>(1);
			defaultValues.add(timep.getDefaultValue());
			break;
		case WKT:
			WKTParameter wktp = (WKTParameter) parameter;
			defaultValues = new ArrayList<String>(1);
			defaultValues.add(wktp.getDefaultValue());
			break;
		default:
			break;
		}

		return new TaskParameter(parameter.getName(), parameter.getValue(), defaultValues, toTaskParameterType(parameter.getTypology()));

	}

	/**
	 * To task parameter type.
	 *
	 * @param type the type
	 * @return the task parameter type
	 */
	public static TaskParameterType toTaskParameterType(ParameterType type){
		if(type==null)
			return null;

		return new TaskParameterType(type.name().toString());
	}


	/**
	 * To task parameter.
	 *
	 * @param taskParameter the task parameter
	 * @return the parameter
	 */
	public static Parameter toParameter(TaskParameter taskParameter){

		ParameterType toParameterType = null;
		try{

			if(taskParameter==null || taskParameter.getType()==null)
				return null;

			toParameterType = ParameterType.valueOf(taskParameter.getType().getType());
			Parameter p = null;
			switch (toParameterType) {
			case FILE:
				p =new FileParameter();
				break;
			case OBJECT:
				p = new ObjectParameter();
				break;
			case TABULAR:
				p = new TabularParameter();
				break;
			case ENUM:
				p = new EnumParameter();
				break;
			case LIST:
				p = new ListParameter();
				break;
			case COLUMN:
				p = new ColumnParameter();
				break;
			case COLUMN_LIST:
				p = new ColumnListParameter();
				break;
			case DATE:
				p = new DateParameter();
				break;
			case TABULAR_LIST:
				p = new TabularListParameter();
				break;
			case TIME:
				p = new TimeParameter();
				break;
			case WKT:
				p = new WKTParameter();
				break;
			default:
				break;
			}

			p.setName(taskParameter.getKey());
			p.setValue(taskParameter.getValue());
			return p;

		}catch(Exception e){
			logger.warn("Impossible to convert the value: "+taskParameter+" at one of values "+ParameterType.values());
			return null;
		}
	}


	/**
	 * Gets the output message.
	 *
	 * @param key the key
	 * @param res the res
	 * @return the output message
	 */
	public static String getOutputMessage(String key, Resource res){

		switch (res.getResourceType()) {
		case FILE:
			FileResource fileResource = (FileResource) res;
			String fileName=retrieveFileName(fileResource.getUrl());
			logger.debug("Entry: " + key + "= "+ fileResource+", FileName="+fileName);
			return "FileName= "+fileName + ", PublicLink="+fileResource.getUrl();
		case IMAGE:
			ImageResource imageResource = (ImageResource) res;
			String imageName=retrieveFileName(imageResource.getLink());
			logger.debug("Entry: " + key + " = "+ imageResource+", ImageName="+imageName);
			return "ImageName= " + imageName + ", PublicLink= "+ imageResource.getLink();
		case MAP:
			logger.debug("Entry: " + key + "= "+ res);
			return "Entry: " + key + "= "+ res;
		case OBJECT:
			logger.debug("Entry: " + key + "= "+ res);
			return "Entry: " + key + "= "+ res;
		case TABULAR:
			TableResource tableResource = (TableResource) res;
			String tableName=retrieveFileName(tableResource.getResourceId());
			logger.debug("TableName= " + tableName + ", ResourceId= "+ tableResource.getResourceId());
			return "TableName= " + tableName + ", ResourceId= "+ tableResource.getResourceId();
		default:
			logger.debug("Entry= " + key + "= "+ res);
			return "Entry: " + key + "= "+ res;
		}

	}

	/**
	 * Retrieve file name.
	 * provided by Giancarlo
	 * @param url the url
	 * @return the string
	 */
	private static String retrieveFileName(String url) {
		String fileName = "output";

		try {

			URL urlObj;
			urlObj = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) urlObj
					.openConnection();
			connection.setRequestMethod("GET");
			String contentDisposition = connection
					.getHeaderField("Content-Disposition");
			Pattern regex = Pattern.compile("(?<=filename=\").*?(?=\")");
			Matcher regexMatcher = regex.matcher(contentDisposition);
			if (regexMatcher.find()) {
				fileName = regexMatcher.group();
			}

			if (fileName == null || fileName.isEmpty()) {
				fileName = "output";
			}

			return fileName;
		} catch (Throwable e) {
			logger.error(
					"Error retrieving file name: " + e.getLocalizedMessage(), e);
			return fileName;
		}

	}

}
