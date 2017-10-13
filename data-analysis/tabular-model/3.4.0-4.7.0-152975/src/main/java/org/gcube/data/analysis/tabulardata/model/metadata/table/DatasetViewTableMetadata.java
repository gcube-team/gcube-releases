package org.gcube.data.analysis.tabulardata.model.metadata.table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.DataDependentMetadata;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DatasetViewTableMetadata implements DataDependentMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7643108186021125617L;

	private TableId targetDatasetViewTableId;

	@SuppressWarnings("unused")
	private DatasetViewTableMetadata() {
	}

	public DatasetViewTableMetadata(TableId datasetViewTableId) {
		this.targetDatasetViewTableId = datasetViewTableId;
	}

	public TableId getTargetDatasetViewTableId() {
		return targetDatasetViewTableId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((targetDatasetViewTableId == null) ? 0 : targetDatasetViewTableId.hashCode());
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
		DatasetViewTableMetadata other = (DatasetViewTableMetadata) obj;
		if (targetDatasetViewTableId == null) {
			if (other.targetDatasetViewTableId != null)
				return false;
		} else if (!targetDatasetViewTableId.equals(other.targetDatasetViewTableId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DatasetViewTableMetadata [\n\ttargetDatasetViewTableId=");
		builder.append(targetDatasetViewTableId);
		builder.append("\n]");
		return builder.toString();
	}

	public boolean isInheritable() {
		return false;
	}

}
