package org.gcube.data.analysis.tabulardata.model.metadata.column;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;

@XmlRootElement(name="DataValidationMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataValidationMetadata implements ColumnMetadata {

	private static final long serialVersionUID = 5346249544710409540L;

	private Validation validation;
	
	private int invalidRowsCount;
	
	@SuppressWarnings("unused")
	private DataValidationMetadata() {
	}

	public DataValidationMetadata(Validation validation, int invalidCount) {
		if(validation.isValid()&&invalidCount>0 || (!validation.isValid()&&invalidCount==0))
			throw new IllegalArgumentException("Validation flag and invalidCount are not coherent");
		this.validation = validation;		
		this.invalidRowsCount=invalidCount;
	}

	public boolean isValid() {
		return validation.isValid();
	}
	
	public String getDescription(){
		return validation.getDescription();
	}
	
	public int getConditionId(){
		return validation.getConditionId();
	}
	
	public int getInvalidRowsCount() {
		return invalidRowsCount;
	}

	public boolean isInheritable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + invalidRowsCount;
		result = prime * result
				+ ((validation == null) ? 0 : validation.hashCode());
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
		DataValidationMetadata other = (DataValidationMetadata) obj;
		if (invalidRowsCount != other.invalidRowsCount)
			return false;
		if (validation == null) {
			if (other.validation != null)
				return false;
		} else if (!validation.equals(other.validation))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataValidationMetadata [validation=");
		builder.append(validation);
		builder.append(", invalidRowsCount=");
		builder.append(invalidRowsCount);
		builder.append("]");
		return builder.toString();
	}

	

	

}
