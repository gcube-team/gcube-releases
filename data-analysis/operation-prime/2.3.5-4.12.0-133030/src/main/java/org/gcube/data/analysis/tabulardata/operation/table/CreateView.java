package org.gcube.data.analysis.tabulardata.operation.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ViewColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.CountMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetViewTableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.QueryProgress;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.EmptyType;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateView extends MetadataWorker {

	private final static Logger log = LoggerFactory.getLogger(CreateView.class);

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private Table targetDataset;

	public CreateView(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider,
			OperationInvocation invocation) {
		super(invocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		targetDataset = cubeManager.getTable(invocation.getTargetTableId());
	}

	@Override
	protected EmptyType execute() throws WorkerException {
		if (targetDataset.hasRelationships()){		
			updateProgress(0.02f,"Creating view schema");
			Table viewTable = createDatasetViewTable();
			String sql = createSQLStatement(viewTable);
			updateProgress(0.05f,"Filling view");
			executeSQLCommand(sql, viewTable, "Filling view", 0.9f);
			updateProgress(0.95f,"View created");
			log.trace("Created view table:\n" + viewTable);
			createResultTable(viewTable);
		}
		return EmptyType.instance();
	}

	private void executeSQLCommand(String sql, Table viewTable, String humanReadableProgress, float percentForInsert)  throws WorkerException {
		try {
			float startProgress = getProgress();
			int extimatedCount = SQLHelper.getCountEstimation(connectionProvider, "SELECT * FROM "+targetDataset.getName());
			QueryProgress progress = SQLHelper.SQLInsertCommandWithProgress(viewTable, sql, extimatedCount, connectionProvider);
			float progressValue=0;
			while ((progressValue=progress.getProgress())< 1){
				float newProgress = (startProgress+(percentForInsert*progressValue));
				updateProgress(newProgress,humanReadableProgress);
				Thread.sleep(1000);
			}
			
			cubeManager.modifyTableMeta(viewTable.getId()).setTableMetadata(new CountMetadata(progress.getTotalUpdated())).create();
		} catch (Exception e) {
			log.error("Error occurred while executing SQL Command", e);
			throw new WorkerException("Error occurred while executing SQL Command", e);
		}
	}

	private Table createResultTable(Table viewTable) {
		TableMetaCreator tmc = cubeManager.modifyTableMeta(targetDataset.getId());
		tmc.setTableMetadata(new DatasetViewTableMetadata(viewTable.getId()));
		return tmc.create();
	}

	@SuppressWarnings("unchecked")
	private String createSQLStatement(Table viewTable) {
		//Collection<Table> linkedTables = collectLinkedTables();

		StringBuilder sqlSelect = new StringBuilder("SELECT ");
		appendDatasetColumnNames(sqlSelect);
		//appendLinkedTableColumnNames(linkedTables, sqlSelect);
		sqlSelect.append("FROM " + targetDataset.getName() + " AS d ");

		int i = 0;
		
		Collection<Column> dimensionColumns = targetDataset.getColumnsByType(new DimensionColumnType(),
				new TimeDimensionColumnType());
		for (Column dimensionColumn : dimensionColumns) {
			
			Table linkedTable;
					
			if (dimensionColumn.getColumnType() instanceof TimeDimensionColumnType && dimensionColumn.contains(PeriodTypeMetadata.class)){
				PeriodType periodType = dimensionColumn.getMetadata(PeriodTypeMetadata.class).getType();
				linkedTable = cubeManager.getTimeTable(periodType);
			} else 
				linkedTable = cubeManager.getTable(dimensionColumn.getRelationship().getTargetTableId());
						
			String linkedTableAlias = "c" + i;

			String linkedTableName = linkedTable.getName();
			String foreignKeyColumnName = dimensionColumn.getName();
			String targetIdColumnName = linkedTable.getColumnsByType(IdColumnType.class).get(0).getName();
			sqlSelect.append(String.format(" LEFT JOIN %1$s AS %2$s ON d.%3$s = %2$s.%4$s ", linkedTableName,
					linkedTableAlias, foreignKeyColumnName, targetIdColumnName));
			i++;
		}
		// Create INSERT statement
		StringBuilder orderedColumnNames = new StringBuilder("id, ");
		for (Column column : viewTable.getColumnsExceptTypes(IdColumnType.class)) 
			orderedColumnNames.append(column.getName() + " ,");
		orderedColumnNames.deleteCharAt(orderedColumnNames.length() - 1); // Remove
		// last
		// comma
		String sqlInsert = String.format("INSERT INTO %s (%s) ", viewTable.getName(), orderedColumnNames);

		StringBuilder sql =new StringBuilder(sqlInsert).append(sqlSelect).append(";");
		log.trace("Generated \"view filling\" SQL statement:\n" + sql);
		return sql.toString();
	}

	@SuppressWarnings("unchecked")
	private void appendDatasetColumnNames(StringBuilder sqlSelect) {
		int relIndex = 0;
		sqlSelect.append("d.id, ");
		for (Column column : targetDataset.getColumnsExceptTypes(IdColumnType.class)) {
			sqlSelect.append("d.").append(column.getName()).append(" ,");
			if (column.getColumnType() instanceof DimensionColumnType || column.getColumnType() instanceof TimeDimensionColumnType){
				Table linkedTable;
				if (column.contains(PeriodTypeMetadata.class))
					linkedTable = cubeManager.getTimeTable(column.getMetadata(PeriodTypeMetadata.class).getType());	
				else
					linkedTable = cubeManager.getTable(column.getRelationship().getTargetTableId());
					
				for (Column relColumn : linkedTable.getColumnsByType(CodeColumnType.class, CodeNameColumnType.class,
						CodeDescriptionColumnType.class,  AnnotationColumnType.class))
					sqlSelect.append("c").append(relIndex).append(".").append(relColumn.getName()).append(" ,");
				relIndex++;
			}
		}
		sqlSelect.deleteCharAt(sqlSelect.length() - 1); // Remove last comma
	}

	@SuppressWarnings("unchecked")
	private Table createDatasetViewTable() throws WorkerException {
		try {
			TableCreator tableCreator = cubeManager.createTable(new DatasetViewTableType());
			tableCreator.like(targetDataset, false);
			Collection<Column> dimensionColumns = targetDataset.getColumnsByType(new DimensionColumnType(),
					new TimeDimensionColumnType());
			for (Column dimensionColumn : dimensionColumns) {
				Column afterColumn = dimensionColumn;
				Table dimensionTable = null; 
				if (dimensionColumn.getColumnType() instanceof DimensionColumnType || !dimensionColumn.contains(PeriodTypeMetadata.class))	
					dimensionTable = cubeManager.getTable(dimensionColumn.getRelationship().getTargetTableId());
				else{
					PeriodTypeMetadata periodMetadata =  dimensionColumn.getMetadata(PeriodTypeMetadata.class);
					dimensionTable = cubeManager.getTimeTable(periodMetadata.getType());
				}
										
				for (Column column : dimensionTable.getColumnsByType(CodeColumnType.class, CodeNameColumnType.class,
						CodeDescriptionColumnType.class, AnnotationColumnType.class)) {
					if (column.contains(NamesMetadata.class) && dimensionTable.contains(TableDescriptorMetadata.class)){
						NamesMetadata names = column.getMetadata(NamesMetadata.class);
						TableDescriptorMetadata tableDescriptor = dimensionTable.getMetadata(TableDescriptorMetadata.class);
						List<LocalizedText> newNames = new ArrayList<LocalizedText>();
						for (LocalizedText ltext : names.getTexts())
							newNames.add(new ImmutableLocalizedText(tableDescriptor.getName()+":"+ltext.getValue(), ltext.getLocale()));
						column.setMetadata(new NamesMetadata(newNames));
					}  
					column.setMetadata(new ViewColumnMetadata(dimensionTable.getId(), column.getLocalId(),
							dimensionColumn.getLocalId()));
					tableCreator.addColumnAfter(column, afterColumn);
					afterColumn = column;
				}
			}
			Table viewTable = tableCreator.create();
			log.debug("Created view: " + viewTable);
			return viewTable;
		} catch (Exception e) {
			throw new WorkerException("Unable to create dataset view", e);
		}

	}

}
