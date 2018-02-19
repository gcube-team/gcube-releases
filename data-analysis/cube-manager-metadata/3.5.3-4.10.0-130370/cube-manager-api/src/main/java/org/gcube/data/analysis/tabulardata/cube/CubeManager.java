package org.gcube.data.analysis.tabulardata.cube;

import java.util.Collection;

import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

/**
 * The cube manager holds
 * information on statistical data (datasets, codelists, hierarchical codelists, transformation mappings)
 * and allows to perform several actions on database resources:
 * <ul>
 * <li>create empty tables, given a structure.</li>
 * <li>clone a table (and their relationship if's a dataset)</li>
 * <li>obtain information about tables, their structure (columns and indexes),
 * relationships between tables</li>
 * <li>add validation information on tables</li>
 * </ul>
 * 
 * @author "Luigi Fortunati"
 * 
 */
public interface CubeManager {

	public TableCreator createTable(TableType type);
	
	public TableMetaCreator modifyTableMeta(TableId tableId) throws NoSuchTableException;
	
	public Collection<Table> getTables();
	
	public Collection<Table> getTables(TableType tableType);
	
	public Table getTimeTable(PeriodType periodType) throws NoSuchTableException;
	
	public Table getTable(TableId id) throws NoSuchTableException;
	
	public void removeTable(TableId id) throws NoSuchTableException;
	
	//public Table createTimeCodelist(PeriodType periodType);

	public Table removeValidations(TableId id) throws NoSuchTableException;
	
	public Table removeColumn(TableId id, ColumnLocalId localId) throws NoSuchTableException;
	
	public Table addValidations(TableId id,  ValidationsMetadata tableValidationMetadata, Column ... validationColumns ) throws NoSuchTableException;
	
	public Table addValidations(TableId id, Column ... validationColumns ) throws NoSuchTableException;
	
	public Table exchangeColumnPosition(TableId tableId, ColumnLocalId columnId, int newPosition ) throws NoSuchTableException;

}
