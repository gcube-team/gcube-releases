package org.gcube.data.analysis.tabulardata.operation.data.transformation.csquare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchPosixRegexp;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.data.replace.ReplaceByExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDataWithExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
@Singleton
public class DownScaleCsquareFactory extends ColumnTransformationWorkerFactory {

	private static final OperationId OPERATION_ID=new OperationId(3010);

	public static final String CSQUARE_REGEXP="[1357][0-8](0[0-9]|1[0-7])(:((1[0-4][0-4])|2[0-4][5-9]|3[5-9][0-4]|4[5-9][5-9]))*(:(1([0-4]{2})?|2([0-4][5-9])?|3([5-9][0-4])?|4([5-9][5-9])?))?";


	public static MultivaluedStringParameter RESOLUTION_PARAM;




	static{
		List<String> toShowOptions=new ArrayList<>();
		for(Resolution res: Resolution.values())
			toShowOptions.add(res.getLabel());
		RESOLUTION_PARAM=new MultivaluedStringParameter("resolution", "Target Resolution", "The final resolution of csquare codes", Cardinality.OPTIONAL, toShowOptions); 
	}

	private CubeManager cm;
	private ValidateDataWithExpressionFactory validatorFactory;
	private DatabaseConnectionProvider connProvider;
	private ReplaceByExpressionFactory replaceFactory;
	
	
	
	@Inject
	public DownScaleCsquareFactory(CubeManager cm,
			ValidateDataWithExpressionFactory validatorFactory,
			DatabaseConnectionProvider connProvider,
			ReplaceByExpressionFactory replaceFactory) {
		super();
		this.cm = cm;
		this.validatorFactory = validatorFactory;
		this.connProvider = connProvider;
		this.replaceFactory = replaceFactory;
	}



	@Override
	public DataWorker createWorker(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0, cm);
		performSpecificChecks(arg0);
		return new DownScaleCsquareWorker(arg0, cm, replaceFactory, connProvider);
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	@Override
	protected String getOperationDescription() {
		return "Downscale csquare codes resolution";
	}

	@Override
	protected String getOperationName() {
		return "Downscale Csquare";
	}

	@Override
	protected List<Parameter> getParameters() {
		return Collections.singletonList((Parameter)RESOLUTION_PARAM);
	}

	public void performSpecificChecks(OperationInvocation invocation) throws InvalidInvocationException{
		try{
			Table targetTable=cm.getTable(invocation.getTargetTableId());
			Column target=targetTable.getColumnById(invocation.getTargetColumnId());			
			if(!(target.getDataType() instanceof TextType)) throw new InvalidInvocationException(invocation, String.format("Wrong data type (%s), TextType expected",target.getDataType()));
//			Resolution currentResolution=getCurrentResolution(targetTable.getColumnReference(target),cm,connProvider);
//			if(currentResolution.equals(Resolution.TEN)) throw new InvalidInvocationException(invocation, "Resolution is already minimum");
			if(invocation.getParameterInstances().containsKey(RESOLUTION_PARAM.getIdentifier())){
				Resolution specifiedRes=Resolution.fromLabel(OperationHelper.getParameter(RESOLUTION_PARAM, invocation));
				if(specifiedRes.equals(Resolution.HALF_MILLI)) throw new InvalidInvocationException(invocation,"Cannot downscale to maxmimu resolution");
//				if(specifiedRes.compareTo(currentResolution)>=0)
//					new InvalidInvocationException(invocation,"Specified resolution is higher or equal current one. Please specify a lower one.");			
			}
//		}catch(SQLException e){
//			throw new InvalidInvocationException(invocation,"Unable to evaluate current resolution",e);
		}catch(Exception e){
			throw new InvalidInvocationException(invocation, e);
		}
	}


	@Override
	public String describeInvocation(OperationInvocation toDescribeInvocation)
			throws InvalidInvocationException {
		try{
			performBaseChecks(toDescribeInvocation, cm);
		performSpecificChecks(toDescribeInvocation);
		ColumnReference colRef=new ColumnReference(toDescribeInvocation.getTargetTableId(), toDescribeInvocation.getTargetColumnId());
		Table table=cm.getTable(colRef.getTableId());
		Column col=table.getColumnById(colRef.getColumnId());
		
		String targetColumnLabel=OperationHelper.retrieveColumnLabel(col);
		return String.format("Downscale %s",targetColumnLabel);
		}catch(InvalidInvocationException e){
			throw e;
		}catch(Exception e){
			throw new InvalidInvocationException(toDescribeInvocation, e);
		}
	}


	public static Resolution getFinalResolution(OperationInvocation invocation,CubeManager cm, DatabaseConnectionProvider connProv) throws SQLException, Exception{
		if(invocation.getParameterInstances().containsKey(RESOLUTION_PARAM.getIdentifier()))
			return Resolution.fromLabel(OperationHelper.getParameter(RESOLUTION_PARAM, invocation));
		else{
			Resolution current=getCurrentResolution(new ColumnReference(invocation.getTargetTableId(), invocation.getTargetColumnId()), cm, connProv);
			return Resolution.values()[current.ordinal()-1];
		}
	}

	public static Resolution getCurrentResolution(ColumnReference colRef,CubeManager cm,DatabaseConnectionProvider connProv) throws SQLException,Exception{
		Table table=cm.getTable(colRef.getTableId());
		Column col=table.getColumnById(colRef.getColumnId());
		String firstValue=(String)SQLHelper.sampleColumn(connProv, table, col);
		if(!Pattern.matches(CSQUARE_REGEXP, firstValue)) throw new Exception("Value "+firstValue+" is not a valid code ");
		for(Resolution res:Resolution.values())
			if(firstValue.length()==res.getCsquareLength().intValue()) return res;
		throw new Exception("Resolution not supported, code was "+firstValue);
	}
	
	@Override
	public Map<String, WorkerFactory<ValidationWorker>> getPreconditionValidationMap() {
		return Collections.singletonMap("expression",(WorkerFactory<ValidationWorker>) validatorFactory);
	}
	
	@Override
	public Map<String, Object> getParametersForPrecondion(String identifier,
			TableId tableId, ColumnLocalId columnId,
			Map<String, Object> sourceParameterInstance)
			throws InvalidInvocationException {
		if(identifier.equals("expression")){
			Map<String,Object> toReturn=new HashMap<>();
			toReturn.put(ValidateDataWithExpressionFactory.VALIDATION_CODE_PARAMETER.getIdentifier(), "301");
			toReturn.put(ValidateDataWithExpressionFactory.VALIDATION_TITLE_PARAMETER.getIdentifier(), "Csquare Syntax");
			toReturn.put(ValidateDataWithExpressionFactory.DESCRIPTION_PARAMETER.getIdentifier(), "Check if entries are valid csquare codes");
			
			Expression validate=new TextMatchPosixRegexp(new ColumnReference(tableId, columnId), new TDText(CSQUARE_REGEXP));			
			toReturn.put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), validate);
			return toReturn;			
		}else return super.getParametersForPrecondion(identifier, tableId, columnId,
				sourceParameterInstance);
	}
}
