package org.gcube.data.analysis.tabulardata.cube.tablemanagers.hcl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.event.Event;

import org.gcube.data.analysis.tabulardata.cube.data.DatabaseWrangler;
import org.gcube.data.analysis.tabulardata.cube.events.TableCreationEvent;
import org.gcube.data.analysis.tabulardata.cube.metadata.CubeMetadataWrangler;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.DefaultTableCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;

public class HCLCreator extends DefaultTableCreator {

	public HCLCreator(DatabaseWrangler dbWrangler, CubeMetadataWrangler cmWrangler, TableManager tableManager, Event<TableCreationEvent> tableCreatedEvent) {
		super(dbWrangler, cmWrangler, tableManager, new HierarchicalCodelistTableType(), tableCreatedEvent);
	}
	
	private static List<ColumnType> indexedColumnTypes = new ArrayList<ColumnType>();
	
	static {
		indexedColumnTypes.add(new DimensionColumnType());
	}

	@Override
	protected void addIndexes(String tableName, Collection<Column> columns) {
		for (Column column : columns) {
			if (indexedColumnTypes.contains(column.getColumnType()))
				dbWrangler.createIndex(tableName, column.getName());
		}
	}

}
