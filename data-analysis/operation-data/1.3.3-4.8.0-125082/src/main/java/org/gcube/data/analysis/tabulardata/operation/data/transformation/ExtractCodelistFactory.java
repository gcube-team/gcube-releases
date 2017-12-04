package org.gcube.data.analysis.tabulardata.operation.data.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableResourceCreatorWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ColumnMetadataParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ColumnTypeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TDTypeValueParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemover;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemoverProvider;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.TableResourceRemover;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

@Singleton
public class ExtractCodelistFactory extends TableResourceCreatorWorkerFactory implements ResourceRemoverProvider {
	
	@Override
	protected OperationType getOperationType() {
	 return OperationType.RESOURCECREATOR;
	}
	
	@Override
	public Class<ResourceCreatorWorker> getWorkerType() {
		return ResourceCreatorWorker.class;
	}

	private ResourceRemover resourceRemover;
	
	public static class ColumnMapping{
		private boolean createNewColumn=false;
		
		// existing target column
		private ColumnReference targetColumn=null;
		
		// to create column
		private ColumnType columnType=null;
		private List<ColumnMetadata> columnMetadata=null;
		private TDTypeValue defaultValue=null;
		
		public ColumnMapping(ColumnReference targetColumn) {
			super();
			this.targetColumn = targetColumn;
		}

		public ColumnMapping(ColumnType columnType,
				List<ColumnMetadata> columnMetadata, TDTypeValue defaultValue) {
			super();
			this.columnType = columnType;
			this.columnMetadata = columnMetadata;
			this.defaultValue = defaultValue;
			createNewColumn=true;
		}
		
		public boolean isCreateNewColumn() {
			return createNewColumn;
		}

		/**
		 * @return the targetColumn
		 */
		public ColumnReference getTargetColumn() {
			return targetColumn;
		}

		/**
		 * @return the columnType
		 */
		public ColumnType getColumnType() {
			return columnType;
		}

		/**
		 * @return the columnMetadata
		 */
		public List<ColumnMetadata> getColumnMetadata() {
			return columnMetadata;
		}

		/**
		 * @return the defaultValue
		 */
		public TDTypeValue getDefaultValue() {
			return defaultValue;
		}
		
		
	}
	
	
	private static final OperationId OPERATION_ID = new OperationId(11001);
	
	public static TargetColumnParameter SOURCE_COLUMN=new TargetColumnParameter("source", "Source column", "Column to export", Cardinality.ONE);
	
	
	//********* Column definition
	
	
	public static ColumnTypeParameter COLUMN_TYPE_PARAMETER=new ColumnTypeParameter("column_type", "Column type", "Column type", Cardinality.ONE, new CodelistTableType().getAllowedColumnTypes());
	public static ColumnMetadataParameter METADATA_PARAMETER=new ColumnMetadataParameter("metadata", "Metadata", "To set metadata", new Cardinality(0, Integer.MAX_VALUE));
	public static TDTypeValueParameter	DEFAULT_VALUE_PARAMETER=new TDTypeValueParameter("default", "Default value", "Default value", Cardinality.OPTIONAL);
	public static SimpleStringParameter	NAME_PARAMETER=new SimpleStringParameter("resource_name", "Resource name", "Resource name", Cardinality.OPTIONAL);

	
	public static CompositeParameter COLUMN_DEFINITION=new CompositeParameter("column_definition","Column definition","New Column Definition",Cardinality.OPTIONAL,
			Arrays.asList(new Parameter[]{
					COLUMN_TYPE_PARAMETER,
					METADATA_PARAMETER,
					DEFAULT_VALUE_PARAMETER,
			})); 
	
	
	
	// ******** Target column 
	
	public static TargetColumnParameter TARGET_CODE_COLUMN=new TargetColumnParameter("target_code_column", "Target code column", "The code column to which exctracted values must be added", Cardinality.OPTIONAL, 
			Collections.singletonList((TableType)new CodelistTableType()),new CodelistTableType().getAllowedColumnTypes());
	
	
	
	
	
	/**
	 * 
	 *  List of mapping SOURCE_COLUMN -> target definition, where target definition must be one of the follow:
	 * 
	 * 		* TARGET_CODE_COLUMN : puts values in an existing codelist (all target references must refer to the same codelist)
	 * 		* NEW_COLUMN_DEFINITION : creates a new column in which to put SOURCE_COLUMN values
	 * 
	 */
	
	
	
	
	public static CompositeParameter COLUMN_MAPPING=new CompositeParameter("mapping", "Column mapping", "Additional columns to export", new Cardinality(1, Integer.MAX_VALUE), 
			Arrays.asList(new Parameter[]{
					SOURCE_COLUMN,
					TARGET_CODE_COLUMN,
					COLUMN_DEFINITION
			}));
	
	private List<Parameter> parameters = Arrays.asList((Parameter)NAME_PARAMETER, (Parameter)COLUMN_MAPPING);
	
	
	
	CubeManager cubeManager;
	
	DatabaseConnectionProvider connectionProvider;
	SQLExpressionEvaluatorFactory sqlExpressionEvaluatorFactory;
	
	@Inject
	public ExtractCodelistFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlExpressionEvaluatorFactory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlExpressionEvaluatorFactory = sqlExpressionEvaluatorFactory;
		this.resourceRemover =  new TableResourceRemover(this.cubeManager);
	}
	
	@Override
	protected String getOperationDescription() {
		return "Extract distinct values from the target column and puts them in the selected codelist or a new one.";
	}
	
	@Override
	protected String getOperationName() {
		return "Extract codelist";
	}
	@Override
	protected OperationId getOperationId() {		
		return OPERATION_ID;
	}
		
	
	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}
	
	
	@Override
	public ExtractCodelist createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkParametersValidity(invocation); 
		return new ExtractCodelist(invocation, cubeManager, connectionProvider,sqlExpressionEvaluatorFactory);
	}
	
	private void checkParametersValidity(OperationInvocation invocation) throws InvalidInvocationException{
		Map<ColumnReference,ColumnMapping> mappings=getMapping(invocation);
		Table targetCodelist=null;
		Table targetTable=cubeManager.getTable(invocation.getTargetTableId());
		for(Entry<ColumnReference,ColumnMapping> entry:mappings.entrySet()){
			// source must belong to target table
			// target column references must belong to same codelist if any
			// target mapping data type must be coherent with source column
//			if(!entry.getKey().getTableId().equals(invocation.getTargetTableId())) throw new InvalidInvocationException(invocation, "Source columns must belong to target table");
			
			ColumnMapping target=entry.getValue();
			DataType sourceDataType=targetTable.getColumnById(entry.getKey().getColumnId()).getDataType();
			DataType targetDataType=null;
			if(!target.isCreateNewColumn()){
				if(targetCodelist==null) targetCodelist=cubeManager.getTable(target.getTargetColumn().getTableId());
				else if(!targetCodelist.getId().equals(target.getTargetColumn().getTableId())) throw new InvalidInvocationException(invocation, "Target columns must refer to the same codelist");				
				targetDataType=targetCodelist.getColumnById(target.getTargetColumn().getColumnId()).getDataType();
			}else targetDataType=target.getDefaultValue().getReturnedDataType();
			
			if(!Cast.isCastSupported(sourceDataType, targetDataType)) throw new InvalidInvocationException(invocation, String.format("Cast not supported from %s to %s",sourceDataType,targetDataType));
		}		
	}

	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkParametersValidity(invocation);
		Map<ColumnReference,ColumnMapping> mappings=getMapping(invocation);
		
		Table targetTable=cubeManager.getTable(invocation.getTargetTableId());
		ArrayList<Column> sourceColumns=new ArrayList<>();
		for(Entry<ColumnReference,ColumnMapping> entry:mappings.entrySet()){
			sourceColumns.add(targetTable.getColumnById(entry.getKey().getColumnId()));
			if(!entry.getValue().isCreateNewColumn()){
				Table targetCodelist=cubeManager.getTable(entry.getValue().getTargetColumn().getTableId());
				return String.format("Add values to %s", OperationHelper.retrieveTableLabel(targetCodelist));
			}
		}
		return String.format("Create codelist from ", OperationHelper.getColumnLabelsSnippet(sourceColumns));
	}
	
	static Map<ColumnReference,ColumnMapping> getMapping(OperationInvocation invocation) throws InvalidInvocationException{
		Object mappingObject=invocation.getParameterInstances().get(COLUMN_MAPPING.getIdentifier());
		try{
			if(mappingObject instanceof Map<?,?>)
				return getColumnMapping((Map<String, Object>) mappingObject);
			else {
				Map<ColumnReference,ColumnMapping> toReturn=new HashMap<ColumnReference, ColumnMapping>();
				for(Map<String,Object> singleMapping:(Iterable<Map<String,Object>>)mappingObject)
					toReturn.putAll(getColumnMapping(singleMapping));
				return toReturn;
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new InvalidInvocationException(invocation, e);
		}
	}
	
	private static Map<ColumnReference,ColumnMapping> getColumnMapping(Map<String,Object> mapping) throws Exception{
		ColumnReference sourceColumn= (ColumnReference) mapping.get(SOURCE_COLUMN.getIdentifier());
		ColumnMapping target=null;
		if(mapping.containsKey(TARGET_CODE_COLUMN.getIdentifier()))
			target=new ColumnMapping((ColumnReference) mapping.get(TARGET_CODE_COLUMN.getIdentifier()));
		else if(mapping.containsKey(COLUMN_DEFINITION.getIdentifier())){
			// new column definition
			Map<String,Object> colDefinition=(Map<String, Object>) mapping.get(COLUMN_DEFINITION.getIdentifier());
			ColumnType colType=(ColumnType) colDefinition.get(COLUMN_TYPE_PARAMETER.getIdentifier());
			TDTypeValue defaultValue=(TDTypeValue) (colDefinition.containsKey(DEFAULT_VALUE_PARAMETER.getIdentifier())?colDefinition.get(DEFAULT_VALUE_PARAMETER.getIdentifier()):colType.getDefaultDataType().getDefaultValue());
			ArrayList<ColumnMetadata> meta=new ArrayList<>();
			
			if(colDefinition.containsKey(METADATA_PARAMETER.getIdentifier())){
				Object metaObject=colDefinition.get(METADATA_PARAMETER.getIdentifier());
				if(metaObject instanceof ColumnMetadata) meta.add((ColumnMetadata) metaObject);
				else for(ColumnMetadata colMeta:(Iterable<ColumnMetadata>)metaObject)
						meta.add(colMeta);
			}
			
			target=new ColumnMapping(colType, meta, defaultValue);
		}else throw new Exception(String.format("No target column / new column definition provided for source column %s.",sourceColumn));
		
		return Collections.singletonMap(sourceColumn, target);		
	}

	@Override
	public ResourceRemover getResourceRemover() {
		return this.resourceRemover;
	}
	
	
}
