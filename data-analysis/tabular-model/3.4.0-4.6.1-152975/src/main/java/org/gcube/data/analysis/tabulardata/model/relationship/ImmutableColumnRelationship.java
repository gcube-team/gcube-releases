package org.gcube.data.analysis.tabulardata.model.relationship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImmutableColumnRelationship implements ColumnRelationship {

	private static final long serialVersionUID = -170595322344282579L;

	private ColumnReference targetColumnReference;

	@SuppressWarnings("unused")
	private ImmutableColumnRelationship() {
		// Serialization only
	}

	public ImmutableColumnRelationship(TableId targetTableId, ColumnLocalId targetColumnId) {
		this.targetColumnReference = new ColumnReference(targetTableId, targetColumnId);
	}

	@SuppressWarnings("unchecked")
	public ImmutableColumnRelationship(Table targetTable) {
		Column column;
		try {
			column = targetTable.getColumnsByType(IdColumnType.class).get(0);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to retrieve Id Column from table:\n" + targetTable);
		}
		this.targetColumnReference = new ColumnReference(targetTable.getId(), column.getLocalId());
	}

	public TableId getTargetTableId() {
		return targetColumnReference.getTableId();
	}

	public ColumnLocalId getTargetColumnId() {
		return targetColumnReference.getColumnId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((targetColumnReference == null) ? 0 : targetColumnReference.hashCode());
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
		ImmutableColumnRelationship other = (ImmutableColumnRelationship) obj;
		if (targetColumnReference == null) {
			if (other.targetColumnReference != null)
				return false;
		} else if (!targetColumnReference.equals(other.targetColumnReference))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnRelationshipImpl [targetTableId()=");
		builder.append(getTargetTableId());
		builder.append(", targetColumnId()=");
		builder.append(getTargetColumnId());
		builder.append("]");
		return builder.toString();
	}

}
