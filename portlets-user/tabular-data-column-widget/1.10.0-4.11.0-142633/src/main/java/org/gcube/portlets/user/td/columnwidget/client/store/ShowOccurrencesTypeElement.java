package org.gcube.portlets.user.td.columnwidget.client.store;

import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ShowOccurrencesType;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ShowOccurrencesTypeElement {

	protected int id; // For insert in table only
	protected ShowOccurrencesType type;

	public ShowOccurrencesTypeElement() {
	}

	public ShowOccurrencesTypeElement(int id, ShowOccurrencesType type) {
		this.id = id;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ShowOccurrencesType getType() {
		return type;
	}

	public String getLabel() {
		return type.toString();
	}

	public void setType(ShowOccurrencesType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ShowOccurrencesTypeElement [id=" + id + ", type=" + type + "]";
	}

}
