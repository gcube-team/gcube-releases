package org.gcube.data.analysis.tabulardata.model.relationship;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

public interface TableRelationship extends ColumnRelationship {

	public TableId getForeignKeyTableId();
	
	public ColumnLocalId getForeignKeyColumnId();
	
}
