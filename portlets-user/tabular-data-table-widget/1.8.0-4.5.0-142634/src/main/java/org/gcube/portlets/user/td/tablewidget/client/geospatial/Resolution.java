package org.gcube.portlets.user.td.tablewidget.client.geospatial;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class Resolution {
	private int id;
	private Double value;
	private String stringValue;

	public Resolution() {
	}

	public Resolution(int id, Double value, String stringValue) {
		super();
		this.id = id;
		this.value = value;
		this.stringValue=stringValue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String toString() {
		return "Resolution [id=" + id + ", value=" + value + ", stringValue="
				+ stringValue + "]";
	}

	

}
