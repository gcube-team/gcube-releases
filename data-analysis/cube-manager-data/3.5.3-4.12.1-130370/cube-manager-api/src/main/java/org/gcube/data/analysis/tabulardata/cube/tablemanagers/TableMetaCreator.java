package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;

public interface TableMetaCreator {
	
	public TableMetaCreator setTableMetadata(TableMetadata... metadata);
	
	public TableMetaCreator setTableType(TableType tableType);

	public TableMetaCreator removeTableMetadata(Class<? extends TableMetadata> metadataType);

	public TableMetaCreator removeAllTableMetadata();

	public TableMetaCreator setColumnMetadata(ColumnLocalId columnId, ColumnMetadata... metadata);

	public TableMetaCreator removeColumnMetadata(ColumnLocalId columnId, Class<? extends ColumnMetadata> metadataType);

	public TableMetaCreator removeAllColumnMetadata(ColumnLocalId columnId);
	
	public Table create() throws TableCreationException;

}
