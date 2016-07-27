package org.gcube.data.analysis.tabulardata.model.metadata.table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;


@XmlRootElement(name="GlobalDataValidationReportMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class GlobalDataValidationReportMetadata implements TableMetadata {

	private static final long serialVersionUID = 5346249544710409540L;

	private ColumnLocalId validationColumnId;
	
	
	@SuppressWarnings("unused")
	private GlobalDataValidationReportMetadata() {
	}
	
	
	public GlobalDataValidationReportMetadata(LocalizedText description,
			LocalizedText name, int invalidRowsCount,
			ColumnLocalId validationColumnId) {
		super();
		this.validationColumnId = validationColumnId;
	}

	@Override
	public boolean isInheritable() {		
		return false;
	}



	/**
	 * @return the validationColumnId
	 */
	public ColumnLocalId getValidationColumnId() {
		return validationColumnId;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((validationColumnId == null) ? 0 : validationColumnId
						.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GlobalDataValidationReportMetadata other = (GlobalDataValidationReportMetadata) obj;
		if (validationColumnId == null) {
			if (other.validationColumnId != null)
				return false;
		} else if (!validationColumnId.equals(other.validationColumnId))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GlobalDataValidationReportMetadata [validationColumnId=");
		builder.append(validationColumnId);
		builder.append("]");
		return builder.toString();
	}
	

	
	
}
