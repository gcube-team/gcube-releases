package org.gcube.data.analysis.tabulardata.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.commons.templates.model.ColumnDescriptor;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.ColumnCreatorAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.TemplateColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.LocaleReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TemplateNotCompatibleException;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.PlaceholderReplacer;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.utils.Factories;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;

@Slf4j
public class TemplateEngine {

	private final static OperationId changeTableTypeOperationID = new OperationId(1002);
	private final static OperationId changeToAttributeColumnTypeOperationID = new OperationId(2001);
	private final static OperationId changeToMeasureColumnTypeOperationID = new OperationId(2002);
	private final static OperationId changeToCodeNameColumnTypeOperationID = new OperationId(2004);
	private final static OperationId changeToCodeDescriptionColumnTypeOperationID = new OperationId(2005);
	private final static OperationId changeToAnnotationColumnTypeOperationID = new OperationId(2000);
	private final static OperationId changeToCodeColumnTypeOperationID = new OperationId(2003);
	private final static OperationId changeToDimensionColumnTypeOperationID = new OperationId(2006);
	private final static OperationId changeToTimeDimensionColumnTypeOperationID = new OperationId(2007);


	private static final String TABLE_TYPE_PARAMETER_ID = "tableType";
	private static final String TARGET_DATA_TYPE_PARAMETER_ID = "targetDataType";
	private static final String PERIOD_FORMAT_PARAMETER_ID = "periodFormat";
	private static final String INPUT_FORMAT_ID ="inputFormatId";


	//private static final String EXTERNAL_CONDITION_PARAMETER = "expression";
	private static final String TARGET_COLUMN_PARAMETER = "refColumn";

	private Factories operationFactories;
	private Template template;
	private Table table;
	private TemplateFinalActionFactory tfaf;

	protected TemplateEngine(Template template, Table table, Factories operationFactories, TemplateFinalActionFactory tfaf){
		this.operationFactories = operationFactories;
		this.template = template;
		this.table = table;
		this.tfaf = tfaf;
	}

	private InternalInvocation getRuleInvocation() throws Exception{
		
		final String EXPRESSION_PARAMETER= "expression";
		final String COMPOSITE_RULE_PARAMETER = "rules";
		final String NAME_PARAMETER ="name";
		final long RULE_VALIDATION_OP = 5009;

		WorkerFactory<?> factory = operationFactories.get(new OperationId(RULE_VALIDATION_OP));
		if (factory==null){
			log.error("operation with id {} not found", RULE_VALIDATION_OP);
			throw new OperationNotFoundException("operation with id "+RULE_VALIDATION_OP+" not found");
		}
		Map<String, ColumnDescriptor> mapTemplateColumnToTable = getColumnMappingTemplateToTable(table);
		List<Map<String, Object>> rulesParameterInstance = new ArrayList<Map<String, Object>>();
		for (TemplateColumn<?> templateColumn: template.getColumns()){
			String columnId = mapTemplateColumnToTable.get(templateColumn.getId()).getColumnId().getValue();
			DataType columnType = mapTemplateColumnToTable.get(templateColumn.getId()).getType().newInstance();
			
			Map<String, String> placeholderColumnMapping = new HashMap<String, String>(1); 
			placeholderColumnMapping.put(templateColumn.getId(), columnId);
			
			for (Expression expression: templateColumn.getExpressions()){
				PlaceholderReplacer replacer = new PlaceholderReplacer(expression).replaceAll(new ColumnReference(table.getId(), new ColumnLocalId(columnId), columnType));
				Map<String, Object> singleRuleparameters = new HashMap<String, Object>();
				singleRuleparameters.put(EXPRESSION_PARAMETER, replacer.getExpression());
				singleRuleparameters.put(NAME_PARAMETER, "anonymus rule" );
				rulesParameterInstance.add(singleRuleparameters);
			}
			
		}
		
		if(rulesParameterInstance.isEmpty()) throw new Exception("no rules found");
		InternalInvocation internalInvocation = 
				new InternalInvocation(Collections.singletonMap(COMPOSITE_RULE_PARAMETER, (Object)rulesParameterInstance), factory);

		return internalInvocation;
	
	}

	public List<InternalInvocation> getTemplateSteps() throws TemplateNotCompatibleException, Exception{
		Map<String, ColumnDescriptor> mapTemplateColumnToTable = getColumnMappingTemplateToTable(table);	
		List<InternalInvocation> invocationList = new ArrayList<InternalInvocation>();
		for (TemplateColumn<?> templateColumn: template.getColumns())
			invocationList.add(retrieveInvocation(templateColumn, mapTemplateColumnToTable));
		
		invocationList.add(getInvocationForTableTypeChange());
		
		try{
			invocationList.add(getRuleInvocation());
		}catch(Exception e){
			log.warn("rules not applicable to template",e);
		}
		
		List<InternalInvocation> invocationActions = new ArrayList<InternalInvocation>();
		for (TemplateAction<Long> action : template.getActions()){
			log.debug("creating action "+action.getClass());
			Map<String, Object> mapping = action.replaceColumnReferences(table.getId(), mapTemplateColumnToTable);
			InternalInvocation invocation = createInvocationCouple(null, new OperationId(action.getIdentifier()), operationFactories, mapping);
			if (action instanceof ColumnCreatorAction){
				int index = 0;
				for (TemplateColumn<?> column : ((ColumnCreatorAction) action).getCreatedColumns()){
					String newColumnId = InternalInvocation.getDinamicallyCreatedColumnId(invocation.getInvocationId(),index++);
					log.debug("template column ID is "+column.getId()+" and new column id is "+newColumnId);
					mapTemplateColumnToTable.put(column.getId(), new ColumnDescriptor(new ColumnLocalId(newColumnId), column.getValueType()));
				}
			}
			if (action instanceof TemplateColumnAction){
				ColumnLocalId changedColumnId = mapTemplateColumnToTable.get(((TemplateColumnAction) action).getColumnId()).getColumnId();
				log.debug("changing column id "+((TemplateColumnAction) action).getColumnId()+" with "+changedColumnId.getValue());
				invocation.setColumnId(changedColumnId);
			}
						
			invocationActions.add(invocation);
		}
		invocationList.addAll(invocationActions);
		return invocationList;
	}

	private InternalInvocation retrieveInvocation(TemplateColumn<?> templateColumn, Map<String, ColumnDescriptor> mapTemplateColumnToTable) throws Exception{
		switch (templateColumn.getColumnType()) {
		case ATTRIBUTE:
			return createInvocationCouple(mapTemplateColumnToTable.get(templateColumn.getId()).getColumnId(), 
					changeToAttributeColumnTypeOperationID, operationFactories, simpleTypeParameterRetriever(templateColumn));
		case DIMENSION:
			DimensionReference dimRef = (DimensionReference)templateColumn.getReference();
			Map<String, Object> dimensionParameters = new HashMap<String, Object>();
			dimensionParameters.put(TARGET_COLUMN_PARAMETER, new ColumnReference(dimRef.getTableId(), dimRef.getColumnId()));
			InternalInvocation dimInvocation = createInvocationCouple(mapTemplateColumnToTable.get(templateColumn.getId()).getColumnId(), changeToDimensionColumnTypeOperationID, operationFactories, dimensionParameters );
			ColumnDescriptor dimDescriptor = new ColumnDescriptor(new ColumnLocalId(InternalInvocation.getDinamicallyCreatedColumnId(dimInvocation.getInvocationId(), 0)), IntegerType.class);
			mapTemplateColumnToTable.put(templateColumn.getId(), dimDescriptor);
			return dimInvocation;
		case TIMEDIMENSION:
			TimeDimensionReference periodReference = (TimeDimensionReference)templateColumn.getReference();
			Map<String, Object> timeParameter = new HashMap<String, Object>();
			if (periodReference.getFormatIdentifier()!=null) timeParameter.put(INPUT_FORMAT_ID, periodReference.getFormatIdentifier());				
			timeParameter.put(PERIOD_FORMAT_PARAMETER_ID, periodReference.getPeriod().getName());
			InternalInvocation timeDimInvocation = createInvocationCouple(mapTemplateColumnToTable.get(templateColumn.getId()).getColumnId(), changeToTimeDimensionColumnTypeOperationID, operationFactories, timeParameter );
			ColumnDescriptor timeDescriptor = new ColumnDescriptor(new ColumnLocalId(InternalInvocation.getDinamicallyCreatedColumnId(timeDimInvocation.getInvocationId(), 0)), IntegerType.class);
			mapTemplateColumnToTable.put(templateColumn.getId(), timeDescriptor);
			return timeDimInvocation;
		case MEASURE:
			return createInvocationCouple(mapTemplateColumnToTable.get(templateColumn.getId()).getColumnId(), changeToMeasureColumnTypeOperationID, 
					operationFactories, simpleTypeParameterRetriever(templateColumn) );
		case CODENAME:
			LocaleReference reference = (LocaleReference) templateColumn.getReference();
			Map<String, Object> codeNameParams = new HashMap<String, Object>(1);
			codeNameParams.put("additionalMeta", new DataLocaleMetadata(reference.getLocale()));
			return createInvocationCouple(mapTemplateColumnToTable.get(templateColumn.getId()).getColumnId(), changeToCodeNameColumnTypeOperationID, operationFactories, codeNameParams);
		case CODEDESCRIPTION:
			return createInvocationCouple(mapTemplateColumnToTable.get(templateColumn.getId()).getColumnId(), changeToCodeDescriptionColumnTypeOperationID, operationFactories);
		case ANNOTATION:
			return createInvocationCouple(mapTemplateColumnToTable.get(templateColumn.getId()).getColumnId(), changeToAnnotationColumnTypeOperationID, operationFactories);
		case CODE:
			return createInvocationCouple(mapTemplateColumnToTable.get(templateColumn.getId()).getColumnId(), changeToCodeColumnTypeOperationID, operationFactories);
		default:
			throw new RuntimeException("unexpected option");
		}
	}
	
	
	public Map<String, Object> simpleTypeParameterRetriever(TemplateColumn<?> templateColumn) throws Exception{
		Map<String, Object> attributeParameters = new HashMap<>(2);
		attributeParameters.put(TARGET_DATA_TYPE_PARAMETER_ID, (Object) templateColumn.getValueType().newInstance());
		FormatReference formatReference = (FormatReference)templateColumn.getReference();
		if (formatReference!=null) attributeParameters.put(INPUT_FORMAT_ID, formatReference.getFormatIdentifier());
		return attributeParameters;
	}

	public TemplateFinalActionExecutor getInvocationForFinalAction(){
		if (template.getAddToFlow()!=null){
			return tfaf.getExecutor(template);
		}else return null;
	}

	private InternalInvocation createInvocationCouple(ColumnLocalId targetColumn, OperationId operationId, Factories operationFactories){
		return createInvocationCouple(targetColumn, operationId, operationFactories, new HashMap<String, Object>(0));
	}

	private InternalInvocation createInvocationCouple(ColumnLocalId targetColumn, OperationId operationId, Factories operationFactories, Map<String, Object>  parameter){
		WorkerFactory<?> factory = operationFactories.get(operationId);
		if (factory==null) throw new RuntimeException("operation with id "+operationId+" not found");
		InternalInvocation couple = new InternalInvocation(parameter, factory);
		couple.setColumnId(targetColumn);
		return couple;
	}

	private Map<String, ColumnDescriptor> getColumnMappingTemplateToTable(Table table) throws TemplateNotCompatibleException{
		List<TemplateColumn<?>> templateColumns =  template.getColumns();
		List<Column> tableColumns = table.getColumns();	
		Map<String, ColumnDescriptor> mapTemplateColumnToTable = new HashMap<String, ColumnDescriptor>();	
		int templateIndex = 0;

		for (Column column: tableColumns){
			if (column.getColumnType() instanceof IdColumnType || column.getColumnType() instanceof ValidationColumnType)
				continue;
			if (templateIndex>=templateColumns.size())
				throw new TemplateNotCompatibleException("the template specified is not compatible with the table "+table.getId().getValue());
			mapTemplateColumnToTable.put(templateColumns.get(templateIndex).getId(), new ColumnDescriptor(column.getLocalId(), templateColumns.get(templateIndex).getValueType()));
			templateIndex++;
		}

		if (mapTemplateColumnToTable.size()!=templateColumns.size()) 
			throw new TemplateNotCompatibleException("the template specified is not compatible with the table "+table.getId().getValue());
		return mapTemplateColumnToTable;
	}

	private InternalInvocation getInvocationForTableTypeChange(){
		InternalInvocation changeTableTypeInvocation = null;		
		switch (template.getCategory()){
		case CODELIST:
			Map<String, Object> codelistTypeParameter = Collections.singletonMap(TABLE_TYPE_PARAMETER_ID, (Object)new CodelistTableType().getName());
			changeTableTypeInvocation = createInvocationCouple(null, changeTableTypeOperationID, operationFactories, codelistTypeParameter);
			break;
		case DATASET:
			Map<String, Object> datasetTypeParameter = Collections.singletonMap(TABLE_TYPE_PARAMETER_ID, (Object)new DatasetTableType().getName());
			changeTableTypeInvocation = createInvocationCouple(null, changeTableTypeOperationID, operationFactories, datasetTypeParameter);
			break;
		default:
			Map<String, Object> genericTypeParameter = Collections.singletonMap(TABLE_TYPE_PARAMETER_ID, (Object)new GenericTableType().getName());
			changeTableTypeInvocation = createInvocationCouple(null, changeTableTypeOperationID, operationFactories, genericTypeParameter);
			break;
		}
		return changeTableTypeInvocation;
	}

}
