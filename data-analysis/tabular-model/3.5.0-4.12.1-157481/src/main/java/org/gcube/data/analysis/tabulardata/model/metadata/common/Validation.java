package org.gcube.data.analysis.tabulardata.model.metadata.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "Validation")
@XmlAccessorType(XmlAccessType.FIELD)
public class Validation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2060034110909329791L;

	private String description;

	private boolean valid;

	private int conditionId;
	
	@SuppressWarnings("unused")
	private Validation() {}
	
	public Validation(String description, boolean valid,int conditionId) {
		this.description = description;
		this.valid = valid;
		this.conditionId=conditionId;
	}

	public String getDescription() {
		return description;
	}

	public boolean isValid() {
		return valid;
	}

	public int getConditionId() {
		return conditionId;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + conditionId;
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
		Validation other = (Validation) obj;
		if (conditionId != other.conditionId)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Validation [description=");
		builder.append(description);
		builder.append(", valid=");
		builder.append(valid);
		builder.append(", conditionId=");
		builder.append(conditionId);
		builder.append("]");
		return builder.toString();
	}

	

	
	
}
