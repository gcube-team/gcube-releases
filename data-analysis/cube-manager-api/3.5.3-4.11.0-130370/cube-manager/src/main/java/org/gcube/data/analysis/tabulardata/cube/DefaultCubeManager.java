package org.gcube.data.analysis.tabulardata.cube;

import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.events.TableRemovedEvent;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreatorProvider;
import org.gcube.data.analysis.tabulardata.cube.time.TimeCodelistCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataValidationMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ValidationReferencesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GlobalDataValidationReportMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetViewTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

@Singleton
@Default
public class DefaultCubeManager implements CubeManager {

	//private static Logger log = LoggerFactory.getLogger(DefaultCubeManager.class);

	
	private Event<TableRemovedEvent> tableRemovedEvent;
	
	private Provider<TableCreator> codelistCreatorProvider;
	private Provider<TableCreator> datasetCreatorProvider;
	private Provider<TableCreator> hclCreatorProvider;
	@SuppressWarnings("unused")
	private Provider<TableCreator> unsafeTableCreatorProvider;
	private Provider<TableCreator> genericTableCreatorProvider;
	private Provider<TableCreator> viewTableCreatorProvider;
	private TableMetaCreatorProvider defaultTableMetaCreatorProvider;
	private TableManager tableManager;
	private TimeCodelistCreator timeCodelistCreator;

	@Inject
	private DefaultCubeManager(TableManager tableManager,
			@Named("Codelist") Instance<TableCreator> codelistCreatorProvider,
			@Named("Dataset") Instance<TableCreator> datasetCreatorProvider,
			@Named("HCL") Instance<TableCreator> hclCreatorProvider,
			@Named("ViewTable") Instance<TableCreator> tempTableCreatorProvider,
			@Named("GenericTable") Instance<TableCreator> genericTableCreatorProvider,
			@Named("ViewTable") Instance<TableCreator> viewTableCreatorProvider,
			TableMetaCreatorProvider tableMetaCreatorProvider, TimeCodelistCreator timeCreator,
			Event<TableRemovedEvent> tableRemovedEvent) {
		super();
		this.tableManager = tableManager;

		this.codelistCreatorProvider = codelistCreatorProvider;
		this.datasetCreatorProvider = datasetCreatorProvider;
		this.hclCreatorProvider = hclCreatorProvider;
		this.unsafeTableCreatorProvider = tempTableCreatorProvider;
		this.genericTableCreatorProvider = genericTableCreatorProvider;
		this.viewTableCreatorProvider = viewTableCreatorProvider;
		this.defaultTableMetaCreatorProvider = tableMetaCreatorProvider;
		this.timeCodelistCreator = timeCreator;
		this.tableRemovedEvent = tableRemovedEvent;

	}


	public TableCreator createDataset() {
		return datasetCreatorProvider.get();
	}

	@Override
	public Collection<Table> getTables() {
		return tableManager.getAll();
	}

	@Override
	public Collection<Table> getTables(TableType tableType) {
		return tableManager.getAll(tableType);
	}

	@Override
	public Table getTable(TableId id) throws NoSuchTableException {
		return tableManager.get(id);
	}

	@Override
	public TableCreator createTable(TableType type) {
		if (type.equals(new CodelistTableType()))
			return codelistCreatorProvider.get();
		if (type.equals(new DatasetTableType()))
			return datasetCreatorProvider.get();
		if (type.equals(new GenericTableType()))
			return genericTableCreatorProvider.get();
		if (type.equals(new HierarchicalCodelistTableType()))
			return hclCreatorProvider.get();
		//		if (type.equals(new GenericTableType()))
		//			return unsafeTableCreatorProvider.get();
		if (type.equals(new DatasetViewTableType()))
			return viewTableCreatorProvider.get();
		throw new UnsupportedOperationException("Unsupported table type: " + type);
	}

	@Override
	public void removeTable(TableId id) throws NoSuchTableException {
		tableManager.remove(id);
		tableRemovedEvent.fire(new TableRemovedEvent(id));
	}


	@Override
	public TableMetaCreator modifyTableMeta(TableId tableId) throws NoSuchTableException {
		return defaultTableMetaCreatorProvider.get(tableId);
	}

	@Override
	public Table getTimeTable(PeriodType periodType) {
		Table resultTable = timeCodelistCreator.getTable(periodType);
		return resultTable;
	}



	@Override
	public Table removeValidations(TableId id) throws NoSuchTableException{
		Table table = tableManager.removeValidationColumns(id);
		for (Column column: table.getColumns()){
			column.removeMetadata(ValidationReferencesMetadata.class);
			column.removeMetadata(DataValidationMetadata.class);
		}
		table.removeMetadata(ValidationsMetadata.class);
		table.removeMetadata(GlobalDataValidationReportMetadata.class);
		for (Column col : table.getColumns())
			col.removeMetadata(ValidationsMetadata.class);
		tableManager.save(table, true);
		return table;
	}

	@Override
	public Table addValidations(TableId id, ValidationsMetadata tableValidationMetadata, Column ... validationColumns ) throws NoSuchTableException{
		Table table = tableManager.addValidationColumns(id, validationColumns);

		if (tableValidationMetadata!=null){
			if (!table.contains(ValidationsMetadata.class)){
				ValidationsMetadata validationsMetadata = new ValidationsMetadata(new ArrayList<Validation>());
				validationsMetadata.addAllValidations(tableValidationMetadata.getValidations());
				table.setMetadata(validationsMetadata);
			}else{ 
				ValidationsMetadata metadata = (ValidationsMetadata) table.getMetadata(ValidationsMetadata.class);
				metadata.addAllValidations(tableValidationMetadata.getValidations());
			}
		}

		tableManager.save(table, true);
		return table;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#addValidations(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.model.column.Column[])
	 */
	@Override
	public Table addValidations(TableId id, Column... validationColumns)
			throws NoSuchTableException {
		return addValidations(id, null, validationColumns);
	}


	@Override
	public Table removeColumn(TableId id, ColumnLocalId localId)
			throws NoSuchTableException {
		Table table = tableManager.removeColumn(id, localId);
		tableManager.save(table, true);
		return table;
	}


	@Override
	public Table exchangeColumnPosition(TableId id, ColumnLocalId columnId,
			int position) throws NoSuchTableException {
		Table table = tableManager.get(id);
		Column colToMove = table.getColumnById(columnId);
		table.getColumns().remove(colToMove);
		table.getColumns().add(position, colToMove);
		tableManager.save(table, true);
		return table;
	}

	
}
