package org.gcube.portlets.user.td.chartswidget.client.store;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ValueOperationElement {
	
	protected int id; // For insert in table only
	protected ValueOperationType type;
	
	
	public ValueOperationElement(int id, ValueOperationType type){
		this.id=id;
		this.type=type;
	}
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}


	public ValueOperationType getType() {
		return type;
	}
	

	public void setType(ValueOperationType type) {
		this.type = type;
	}
	
	public String getLabel() {
		return type.toString();
	}

	@Override
	public String toString() {
		return "ChartTypeElement [id=" + id + ", type=" + type + "]";
	}

	
}
