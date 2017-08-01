package org.gcube.portlets.user.dataminermanager.shared.data.computations;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ComputationValue implements Serializable {

	private static final long serialVersionUID = -2047623108851748745L;
	protected ComputationValueType type;
	protected String value;

	public ComputationValue() {
		super();
		this.type = ComputationValueType.String;
	}

	public ComputationValue(ComputationValueType type) {
		super();
		this.type = type;
	}

	public ComputationValue(String value) {
		super();
		this.type = ComputationValueType.String;
		this.value = value;
	}

	public ComputationValue(ComputationValueType type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	public ComputationValueType getType() {
		return type;
	}

	public void setType(ComputationValueType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ComputationValue [type=" + type + ", value=" + value + "]";
	}

}
