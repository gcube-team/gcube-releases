package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.cube.metadata.CubeMetadataWrangler;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class DefaultTableMetaCreator implements TableMetaCreator {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private CubeMetadataWrangler cmw;
	
	private Table startingTable;

	public DefaultTableMetaCreator(CubeMetadataWrangler cmw, Table startingTable) {
		this.cmw = cmw;
		this.startingTable = startingTable;
	}
	
	
	@Override
	public TableMetaCreator setTableType(TableType tableType) {
		startingTable.setTableType(tableType);
		return this;
	}



	@Override
	public TableMetaCreator setTableMetadata(TableMetadata... metadata) {
		for (TableMetadata cubeMetadata : metadata) {
			startingTable.setMetadata(cubeMetadata);
		}
		return this;
	}

	@Override
	public TableMetaCreator removeTableMetadata(Class<? extends TableMetadata> metadataType) {
		startingTable.removeMetadata(metadataType);
		return this;
	}

	@Override
	public TableMetaCreator removeAllTableMetadata() {
		startingTable.removeAllMetadata();
		return this;
	}
	
	@Override
	public TableMetaCreator setColumnMetadata(ColumnLocalId columnId, ColumnMetadata... metadata) {
		try {
			Column column = startingTable.getColumnById(columnId);
			column.setAllMetadata(Lists.newArrayList(metadata));
		} catch (NoSuchColumnException e) {
			log.warn(String.format("Column with name %s cannot be found in table\n%s\n, skipping operation.",columnId,startingTable));
		}
		return this;
	}

	@Override
	public TableMetaCreator removeColumnMetadata(ColumnLocalId columnId, Class<? extends ColumnMetadata> metadataType) {
		try {
			Column column = startingTable.getColumnById(columnId);
			column.removeMetadata(metadataType);
		} catch (NoSuchColumnException e) {
			log.warn(String.format("Column with name %s cannot be found in table\n%s\n, skipping operation.",columnId,startingTable));
		}
		return this;
	}

	@Override
	public TableMetaCreator removeAllColumnMetadata(ColumnLocalId columnId) {
		try {
			Column column = startingTable.getColumnById(columnId);
			column.removeAllMetadata();
		} catch (NoSuchColumnException e) {
			log.warn(String.format("Column with name %s cannot be found in table\n%s\n, skipping operation.",columnId,startingTable));
		}
		return this;
	}

	@Override
	public Table create() throws TableCreationException {
		return cmw.save(startingTable, true);
	}

}
