package org.gcube.data.analysis.tabulardata.model.metadata.column;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ViewColumnMetadata implements ColumnMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6401724106566830356L;

	private ColumnReference targetColumnReference;

	private ColumnLocalId sourceDimensionColumnId;

	@SuppressWarnings("unused")
	private ViewColumnMetadata() {}

	public ViewColumnMetadata(TableId targetTableId, ColumnLocalId targetColumnId, ColumnLocalId sourceDimensionColumnId) {
		this.targetColumnReference = new ColumnReference(targetTableId, targetColumnId);
		this.sourceDimensionColumnId = sourceDimensionColumnId;
	}

	public TableId getTargetTableId() {
		return targetColumnReference.getTableId();
	}

	public ColumnLocalId getTargetTableColumnId() {
		return targetColumnReference.getColumnId();
	}

	public ColumnLocalId getSourceTableDimensionColumnId() {
		return sourceDimensionColumnId;
	}

	public boolean isInheritable() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceDimensionColumnId == null) ? 0 : sourceDimensionColumnId.hashCode());
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
		ViewColumnMetadata other = (ViewColumnMetadata) obj;
		if (sourceDimensionColumnId == null) {
			if (other.sourceDimensionColumnId != null)
				return false;
		} else if (!sourceDimensionColumnId.equals(other.sourceDimensionColumnId))
			return false;
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
		builder.append("ViewColumnMetadata [getTargetTableId()=");
		builder.append(getTargetTableId());
		builder.append(", getTargetColumnId()=");
		builder.append(getTargetTableColumnId());
		builder.append(", getSourceTableDimensionColumnId()=");
		builder.append(getSourceTableDimensionColumnId());
		builder.append("]");
		return builder.toString();
	}

}
