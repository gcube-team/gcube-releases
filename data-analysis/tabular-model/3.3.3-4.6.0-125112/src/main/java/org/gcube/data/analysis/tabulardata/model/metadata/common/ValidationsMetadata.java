package org.gcube.data.analysis.tabulardata.model.metadata.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;

@XmlRootElement(name = "ValidationsMetadata")
public class ValidationsMetadata implements TableMetadata, ColumnMetadata {

	private static final long serialVersionUID = 8038015441467481226L;
	
	@SuppressWarnings("unused")
	private ValidationsMetadata() {}
	
	@XmlElementWrapper(name = "Validations")
	@XmlElementRef(name = "Validation",type=Validation.class)
	List<Validation> validations = new ArrayList<Validation>();

	public ValidationsMetadata(List<Validation> validations) {
		if(validations==null) throw new IllegalArgumentException("Validation list cannot be null");
		this.validations.addAll(validations);
	}

	public List<Validation> getValidations() {
		return Collections.unmodifiableList(validations);
	}

	public boolean isInheritable() {
		return false;
	}

	public boolean addValidation(Validation toAdd){
		return this.validations.add(toAdd);
	}
	
	public boolean addAllValidations(Collection<Validation> toAdd){
		return this.validations.addAll(toAdd);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((validations == null) ? 0 : validations.hashCode());
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
		ValidationsMetadata other = (ValidationsMetadata) obj;
		if (validations == null) {
			if (other.validations != null)
				return false;
		} else if (!validations.equals(other.validations))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ValidationsMetadata [validations=");
		builder.append(validations);
		builder.append("]");
		return builder.toString();
	}

	
	
}
