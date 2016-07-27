package org.gcube.portlets.user.td.gwtservice.shared.tr.resources;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class StringResourceTD extends ResourceTD {

	private static final long serialVersionUID = 7172340185053019104L;

	private String value;

	public StringResourceTD() {
		super();
	}

	/**
	 * 
	 * @param value
	 */
	public StringResourceTD(String value) {
		super(value);
		this.value = value;

	}

	@Override
	public String getStringValue() {
		return value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "StringResourceTD [value=" + value + "]";
	}

}
