package org.gcube.informationsystem.registry.impl.filters;

public class Filter {

	private String target = "";
	
	private String value = "";
	
	private FILTEROPERATION operation = null; 
	
	protected enum FILTEROPERATION {exclude, exclude_if_contains}

	protected Filter() {}
	
	/**
	 * @return the target
	 */
	public String getTarget() {
		return this.target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the operation
	 */
	public FILTEROPERATION getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(FILTEROPERATION operation) {
		this.operation = operation;
	};
}
