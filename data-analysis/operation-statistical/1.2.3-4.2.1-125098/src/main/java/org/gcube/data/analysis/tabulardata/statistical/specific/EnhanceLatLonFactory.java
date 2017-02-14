package org.gcube.data.analysis.tabulardata.statistical.specific;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetViewTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.TimeCodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.data.add.AddColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TDTypeValueParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDataWithExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.gcube.data.analysis.tabulardata.statistical.Common;
import org.gcube.data.analysis.tabulardata.statistical.Constants;
import org.gcube.data.analysis.tabulardata.statistical.StatisticalOperationFactory;

import static org.gcube.data.analysis.tabulardata.expression.dsl.Comparators.*;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Logicals.*;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Types.*;

@Singleton
public class EnhanceLatLonFactory extends TableTransformationWorkerFactory {

	private static final OperationId OPERATION_ID=new OperationId(10101);
	
	private static final List<TableType> allTableTypes=new ArrayList<TableType>();
	private static final List<ColumnType> numericTypes=new ArrayList<ColumnType>();
	private static final List<DataType> numericDataTypes=new ArrayList<DataType>();
	private static final List<String> features=new ArrayList<String>();
	
	static{
		allTableTypes.add(new CodelistTableType());
		allTableTypes.add(new DatasetTableType());
		allTableTypes.add(new DatasetViewTableType());
		allTableTypes.add(new GenericTableType());
		allTableTypes.add(new HierarchicalCodelistTableType());
		allTableTypes.add(new TimeCodelistTableType());
		
		numericTypes.add(new AttributeColumnType());
		numericTypes.add(new MeasureColumnType());
		numericTypes.add(new AnnotationColumnType());
		
		numericDataTypes.add(new IntegerType());
		numericDataTypes.add(new NumericType());
		
		for(LatLongFeature feat:LatLongFeature.values())
			features.add(feat.name());
	}
	
	
	public static final TargetColumnParameter LATITUDE_COLUMN_PARAM=new TargetColumnParameter("latitude", "Latitude Column", "The column containing latitude information", Cardinality.ONE,allTableTypes,numericTypes,numericDataTypes);
	
	public static final TargetColumnParameter LONGITUTE_COLUMN_PARAM=new TargetColumnParameter("longitude", "Longitude Column", "The column containing longitude information", Cardinality.ONE,allTableTypes,numericTypes,numericDataTypes);
	
	public static final TargetColumnParameter QUADRANT_COLUMN_PARAMETER=new TargetColumnParameter("quadrant","Quadrant Column","The column containing quadrant information",Cardinality.OPTIONAL,allTableTypes,numericTypes,Collections.singletonList((DataType)new IntegerType()));
	
	public static final TDTypeValueParameter RESOLUTION_PARAM=new TDTypeValueParameter("resolution", "CSquare Code Resolution", "Resolution of the resulting csquarecodes", Cardinality.OPTIONAL,Collections.singletonList((DataType)new NumericType()));
	
	public static final MultivaluedStringParameter TO_ADD_FEATURE_PARAM=new MultivaluedStringParameter("feature", "To Add Feature", "Feature to be added to the table", Cardinality.ONE,features);
	
	public static final SimpleStringParameter USER=StatisticalOperationFactory.USER;
	
	public static final BooleanParameter DELETE_GENERATED=StatisticalOperationFactory.CLEAR_DATASPACE;
	public static final BooleanParameter DELETE_REMOTE=StatisticalOperationFactory.REMOVE_EXPORTED;
	
	private static List<Parameter> params=Arrays.asList(
			(Parameter)LATITUDE_COLUMN_PARAM,
			LONGITUTE_COLUMN_PARAM,
			QUADRANT_COLUMN_PARAMETER,
			RESOLUTION_PARAM,
			TO_ADD_FEATURE_PARAM,
			USER,
			DELETE_GENERATED,
			DELETE_REMOTE);
	
	
	private CubeManager cm;
	private StatisticalOperationFactory statFactory;
	private AddColumnFactory addColFactory;
	private ValidateDataWithExpressionFactory validationFactory;

	@Inject
	public EnhanceLatLonFactory(CubeManager cm,
			StatisticalOperationFactory statFactory,
			AddColumnFactory addColFactory, ValidateDataWithExpressionFactory validationFactory) {
		super();
		this.cm = cm;
		this.statFactory = statFactory;
		this.addColFactory = addColFactory;
		this.validationFactory = validationFactory;
	}

	@Override
	public DataWorker createWorker(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0, cm);
		performSpecificChecks(arg0);
		checkEnvironment(arg0);
		return new EnhanceLatLong(arg0, statFactory, addColFactory, cm);
	}

	private void checkEnvironment(OperationInvocation invocation) throws InvalidInvocationException{
		String toUseAlgorithm=null;
		LatLongFeature feature=LatLongFeature.valueOf(OperationHelper.getParameter(EnhanceLatLonFactory.TO_ADD_FEATURE_PARAM, invocation));
		switch(feature){
		case CSQUARECODE : toUseAlgorithm=Constants.CSQUARE_ALGORITHM;
		break;
		case OCEANAREA : if(invocation.getParameterInstances().containsKey(QUADRANT_COLUMN_PARAMETER.getIdentifier())) toUseAlgorithm=Constants.OCEAN_AREA_QUADRANT_ALGORITHM;
		else toUseAlgorithm=Constants.OCEAN_AREA_ALGORITHM;
		}
		try{
			if(!Common.isSMAlgorithmAvailable(toUseAlgorithm)) throw new InvalidInvocationException(invocation, Constants.ALGORITHM_NOT_FOUND);
		}catch(Exception e){
			throw new InvalidInvocationException(invocation, Constants.SERVICE_NOT_FOUND);
		}
		
	}
	
	private void performSpecificChecks(OperationInvocation invocation)throws InvalidInvocationException{
		TableId tableId=invocation.getTargetTableId();
		if(!OperationHelper.getParameter(LATITUDE_COLUMN_PARAM, invocation).getTableId().equals(tableId))
			throw new InvalidInvocationException(invocation,"Latitude column not present in this table, tableID is : "+OperationHelper.getParameter(LATITUDE_COLUMN_PARAM, invocation).getTableId());
		if(!OperationHelper.getParameter(LONGITUTE_COLUMN_PARAM, invocation).getTableId().equals(tableId))
			throw new InvalidInvocationException(invocation,"Longitude column not present in this table, tableID is : "+OperationHelper.getParameter(LONGITUTE_COLUMN_PARAM, invocation).getTableId());
		try{
			if(!OperationHelper.getParameter(QUADRANT_COLUMN_PARAMETER, invocation).getTableId().equals(tableId))
			throw new InvalidInvocationException(invocation,"Quadrant column not present in this table, tableID is : "+OperationHelper.getParameter(QUADRANT_COLUMN_PARAMETER, invocation).getTableId());
		}catch(Throwable t){
			// quadrant not declared
		}
	}
	
	
	
	@Override
	protected String getOperationDescription() {
		return "Adds a geospatial feature based on latitude and longitude columns, relying on Statistical Manager's facilities.";
	}

	@Override
	protected String getOperationName() {
		return "EnhanceLatLong";
	}

	@Override
	protected List<Parameter> getParameters() {
		return params;
	}

	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	@Override
	public Map<String, WorkerFactory<ValidationWorker>> getPreconditionValidationMap() {
		return Collections.singletonMap("expression", (WorkerFactory<ValidationWorker>) validationFactory);
	}

	@Override
	public Map<String, Object> getParametersForPrecondion(String identifier, TableId table, ColumnLocalId columnId,
			Map<String, Object> sourceParameterInstance){
		switch (identifier) {
		case "expression":
			return createExpressionParameters(sourceParameterInstance);
		default:
			return sourceParameterInstance;			
		}
	}

	private Map<String, Object> createExpressionParameters(Map<String, Object> sourceParameterInstance) {
		ColumnReference latitude = OperationHelper.getParameter(LATITUDE_COLUMN_PARAM, sourceParameterInstance);
		ColumnReference longitude = OperationHelper.getParameter(LONGITUTE_COLUMN_PARAM, sourceParameterInstance);
		ColumnReference quadrant =null;
		LatLongFeature feature=LatLongFeature.valueOf(OperationHelper.getParameter(EnhanceLatLonFactory.TO_ADD_FEATURE_PARAM, sourceParameterInstance));
		try{
			quadrant= OperationHelper.getParameter(QUADRANT_COLUMN_PARAMETER, sourceParameterInstance);
		}catch(Exception e){
			//quadrant not set
		}

		String description = null;
		Expression expression = null;
		Map<String, Object> validationParameters = new HashMap<String, Object>();
		validationParameters.put(ValidateDataWithExpressionFactory.VALIDATION_TITLE_PARAMETER.getIdentifier(), "Coordinates validation");
		
		if (feature== LatLongFeature.OCEANAREA && quadrant!=null){
			description = String.format("Longitude value must be between [-180, 180], Latitude value must be between [-90, 90] and Quadrant value must be between [1,4] ");
			expression = and(lessEq(longitude, numeric(180)), greaterEq(longitude, numeric(0)), lessEq(latitude, numeric(90)), greaterEq(latitude, numeric(0)), lessEq(quadrant, numeric(4)), greaterEq(quadrant, numeric(1)));
		} else {
			description = String.format("Longitude value must be between [-180, 180], Latitude value must be between [-90, 90]");
			expression = and(lessEq(longitude, numeric(180)), greaterEq(longitude, numeric(-180)), lessEq(latitude, numeric(90)), greaterEq(latitude, numeric(-90)));
		}
		validationParameters.put(ValidateDataWithExpressionFactory.DESCRIPTION_PARAMETER.getIdentifier(), description);
		validationParameters.put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), expression);
		validationParameters.put(ValidateDataWithExpressionFactory.VALIDATION_CODE_PARAMETER.getIdentifier(), "302");
		return validationParameters;
	}

}
