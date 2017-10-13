package org.gcube.portlets.user.td.gwtservice.shared.tr.resources;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StringResourceTD extends ResourceTD {

	private static final long serialVersionUID = 7172340185053019104L;

	private String value;

	public StringResourceTD() {
		super();
	}

	public StringResourceTD(String value) {
		super(value);
		this.value = value;

	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "StringResourceTD [value=" + value + ", getStringValue()=" + getStringValue() + "]";
	}

}
