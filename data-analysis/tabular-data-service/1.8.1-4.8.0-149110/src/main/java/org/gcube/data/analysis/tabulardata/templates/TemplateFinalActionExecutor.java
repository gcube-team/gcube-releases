package org.gcube.data.analysis.tabulardata.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals.DuplicateBehaviour;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchOption;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.importer.empty.EmptyTableCreatorFactory;
import org.gcube.data.analysis.tabulardata.service.OperationManagerImpl;
import org.gcube.data.analysis.tabulardata.service.TabularResourceManagerImpl;
import org.gcube.data.analysis.tabulardata.task.RunnableTask;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateFinalActionExecutor implements RunnableTask{

	private static final long EMPTY_TABLE_CREATOR_ID = 103;
	private static final long UNION_ID=3208;
	private static final long DUPLICATE_ROW_REMOVER_ID = 3007;
	
	
	private static final String DATA_TYPE="dataType";
	private static final String COLUMN_TYPE="columnType";
	private static final String LABEL= "label";
	private static final String COMPOSITE= "column";

	private static final String KEY = "key";
	private static final String INVALIDATE_MODE = "invalidatemode";
	
	public static final String TABLE_PARAMETER = "table";

	private Template template;
	
	private static Logger log = LoggerFactory.getLogger(TemplateFinalActionExecutor.class);
	
	private OperationManagerImpl opManager;
	private TabularResourceManagerImpl trImpl;
	private CubeManager cubeManager;
	private EntityManagerHelper emHelper;
	
	protected TemplateFinalActionExecutor(OperationManagerImpl opManager,
			TabularResourceManagerImpl trImpl, Template template, CubeManager cubeManager, EntityManagerHelper emHelper) {
		this.template = template;
		this.trImpl = trImpl;
		this.opManager = opManager;
		this.cubeManager = cubeManager;
		this.emHelper = emHelper;
	}

	public void run(Table templateApplyedTable) {
		
		try{
			StorableTabularResource tr = trImpl.getTabularResourceByIdWithoutAuth(template.getAddToFlow().getTabularResource());
			TaskInfo info = null;
			List<OperationExecution> executions = new ArrayList<>();
			if (tr.getTableId()==null)			
				executions.add(getEmptyCreationExecution(template.getActualStructure(), templateApplyedTable ));
				
			executions.add(getAddToFlowExecution(tr, templateApplyedTable.getId()));
			
			if (tr.getTableId()!=null && template.getAddToFlow().getDuplicatesBehaviuor()!=DuplicateBehaviour.None)
				executions.add(getDuplicateRemoverExecution(template.getAddToFlow().getDuplicatesBehaviuor(), 
						cubeManager.getTable(new TableId(tr.getTableId()))));
			
			info = opManager.execute(executions, BatchOption.NONE, tr, true, emHelper.getEntityManager());
			log.info("resource creation task started with task id "+info.getIdentifier());
		}catch(Exception e){
			log.error("error trying to executo final action for template",e);
			return;
		}	
	}

	private OperationExecution getEmptyCreationExecution(List<TemplateColumn<?>> columns, Table templateApplyedTable){
		List<Map<String, Object>> columnMapping = new ArrayList<>();
			
		int index = 0;
		@SuppressWarnings("unchecked")
		List<Column> tableColumns = templateApplyedTable.getColumnsExceptTypes(IdColumnType.class, ValidationColumnType.class );
		for (TemplateColumn<?> col : columns)
			try {
				Map<String, Object> single = new HashMap<>();
				single.put(DATA_TYPE, col.getValueType().newInstance());
				single.put(COLUMN_TYPE, col.getColumnType().getModelType());
				LocalizedText label = new ImmutableLocalizedText(col.getLabel()==null?"column-"+index:col.getLabel());
				if (tableColumns.get(index).contains(NamesMetadata.class))
					label = tableColumns.get(index).getMetadata(NamesMetadata.class).getTextWithLocale("en");
				single.put(LABEL, label);
				if (col.getColumnType().equals(ColumnCategory.DIMENSION)){
					DimensionReference reference = ((DimensionReference)col.getReference());
					single.put(EmptyTableCreatorFactory.RELATIONSHIP.getIdentifier(), new ColumnReference(reference.getTableId(), reference.getColumnId()));	
				} else if (col.getColumnType().equals(ColumnCategory.TIMEDIMENSION)){
					TimeDimensionReference reference = ((TimeDimensionReference)col.getReference());
					single.put(EmptyTableCreatorFactory.PERIOD_TYPE.getIdentifier(), reference.getPeriod().name());
				}
								
				columnMapping.add(single);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException("error getting table structure",e);
			}finally{
				index++;
			}
			
		Map<String, Object> parameters = Collections.singletonMap(COMPOSITE, (Object)columnMapping );
		
		return new OperationExecution(EMPTY_TABLE_CREATOR_ID, parameters);
	}
	
	private OperationExecution getAddToFlowExecution(StorableTabularResource str, TableId templateTable){
		return new OperationExecution(UNION_ID, Collections.singletonMap(TABLE_PARAMETER, (Object)templateTable));
	}
	
	@SuppressWarnings("unchecked")
	private OperationExecution getDuplicateRemoverExecution(DuplicateBehaviour behaviour, Table table){
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(INVALIDATE_MODE, behaviour.name());
		
		List<Column> keyColumns = table.getColumnsByType(AttributeColumnType.class, CodeColumnType.class, DimensionColumnType.class, TimeDimensionColumnType.class);
		
		if (keyColumns.isEmpty())
			keyColumns= table.getColumnsExceptTypes(IdColumnType.class);
		
		List<ColumnReference> references = new ArrayList<>();
		for (Column col : keyColumns )
			references.add( new ColumnReference(table.getId(), col.getLocalId()));
		
		parameters.put(KEY, references);
		
		return new OperationExecution(DUPLICATE_ROW_REMOVER_ID, parameters );
	}

}
