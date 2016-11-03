package org.gcube.data.analysis.tabulardata.cube.tablemanagers.view;

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
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetViewTableType;

import com.google.common.collect.Lists;

public class ViewTableCreator extends DefaultTableCreator {

	public ViewTableCreator(DatabaseWrangler dw, CubeMetadataWrangler cmw, TableManager tm, Event<TableCreationEvent> tableCreatedEvent) {
		super(dw, cmw, tm, new DatasetViewTableType(), tableCreatedEvent);
	}

	private static List<ColumnType> indexedColumnTypes = Lists.newArrayList(new IdColumnType(),
			new DimensionColumnType());

	@Override
	protected void addIndexes(String tableName, Collection<Column> columns) {
		for (Column column : columns) {
			if (indexedColumnTypes.contains(column.getColumnType()))
				dbWrangler.createIndex(tableName, column.getName());
		}
	}

}
