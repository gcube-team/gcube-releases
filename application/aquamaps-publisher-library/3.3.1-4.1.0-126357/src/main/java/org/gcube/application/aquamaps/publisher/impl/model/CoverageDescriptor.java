package org.gcube.application.aquamaps.publisher.impl.model;

import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;


@TableRootDefinition
public class CoverageDescriptor {

	@FieldDefinition(precision={120}, specifications={Specification.NOT_NULL})
	private String tableId;
	@FieldDefinition(specifications={Specification.NOT_NULL})
	private String parameters;
	@FieldDefinition(specifications={Specification.NOT_NULL})
	private boolean customized= false;
	
	protected CoverageDescriptor(){};
	
	public CoverageDescriptor(String tableId, String parameters) {
		super();
		this.tableId = tableId;
		this.parameters = parameters;
	}
	
	/**
	 * @return the tableId
	 */
	public String getTableId() {
		return tableId;
	}
	/**
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}
	
	/**
	 * @return the customized
	 */
	public boolean isCustomized() {
		return customized;
	}

	/**
	 * @param tableId the tableId to set
	 */
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	/**
	 * @param customized the customized to set
	 */
	public void setCustomized(boolean customized) {
		this.customized = customized;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
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
		CoverageDescriptor other = (CoverageDescriptor) obj;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (tableId == null) {
			if (other.tableId != null)
				return false;
		} else if (!tableId.equals(other.tableId))
			return false;
		return true;
	}

	
	
	
}
