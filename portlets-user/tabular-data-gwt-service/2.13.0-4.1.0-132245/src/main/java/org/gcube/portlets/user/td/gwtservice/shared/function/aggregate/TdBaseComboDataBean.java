package org.gcube.portlets.user.td.gwtservice.shared.function.aggregate;

import java.io.Serializable;

/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TdBaseComboDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5022327067102919324L;

	private String id;
	private String label;

	/**
	 * 
	 */
	public TdBaseComboDataBean() {
	}

	/**
	 * @param id
	 * @param label
	 */
	public TdBaseComboDataBean(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String label() {
		return label;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdDataBean [id=");
		builder.append(id);
		builder.append(", label=");
		builder.append(label);
		builder.append("]");
		return builder.toString();
	}
}
