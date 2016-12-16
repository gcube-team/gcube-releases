package org.gcube.data.analysis.tabulardata.model.relationship;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

public class TableRelationshipImpl implements TableRelationship {

	private static final long serialVersionUID = 8369191690239495474L;

	private ColumnRelationship delegate;

	private ColumnReference foreignKeyColumnReference;
	
	@SuppressWarnings("unused")
	private TableRelationshipImpl() {}

	public TableRelationshipImpl(TableId targetTableId, ColumnLocalId targetColumnId, TableId foreignKeyTableId,
			ColumnLocalId foreignKeyColumnId) {
		this.delegate = new ImmutableColumnRelationship(targetTableId, targetColumnId);
		this.foreignKeyColumnReference = new ColumnReference(foreignKeyTableId, foreignKeyColumnId);
	}
	
	public TableRelationshipImpl(ColumnRelationship columnRelationship, TableId foreignKeyTableId, ColumnLocalId foreignKeyColumnId){
		this.delegate = columnRelationship;
		this.foreignKeyColumnReference = new ColumnReference(foreignKeyTableId, foreignKeyColumnId);
	}

	public TableId getTargetTableId() {
		return delegate.getTargetTableId();
	}

	public ColumnLocalId getTargetColumnId() {
		return delegate.getTargetColumnId();
	}

	public TableId getForeignKeyTableId() {
		return foreignKeyColumnReference.getTableId();
	}

	public ColumnLocalId getForeignKeyColumnId() {
		return foreignKeyColumnReference.getColumnId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
		result = prime * result + ((foreignKeyColumnReference == null) ? 0 : foreignKeyColumnReference.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableRelationshipImpl other = (TableRelationshipImpl) obj;
		if (delegate == null) {
			if (other.delegate != null)
				return false;
		} else if (!delegate.equals(other.delegate))
			return false;
		if (foreignKeyColumnReference == null) {
			if (other.foreignKeyColumnReference != null)
				return false;
		} else if (!foreignKeyColumnReference.equals(other.foreignKeyColumnReference))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TableRelationshipImpl [delegate=");
		builder.append(delegate);
		builder.append(", foreignKeyColumnReference=");
		builder.append(foreignKeyColumnReference);
		builder.append("]");
		return builder.toString();
	}

}
