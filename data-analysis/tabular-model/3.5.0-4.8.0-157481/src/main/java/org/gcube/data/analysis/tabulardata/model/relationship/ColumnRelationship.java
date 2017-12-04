package org.gcube.data.analysis.tabulardata.model.relationship;

import java.io.Serializable;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

public interface ColumnRelationship extends Serializable {

	public TableId getTargetTableId();

	public ColumnLocalId getTargetColumnId();

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object obj);

}