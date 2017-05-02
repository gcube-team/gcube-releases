package org.gcube.portlets.user.td.chartswidget.client.store;

import org.gcube.portlets.user.td.widgetcommonevent.shared.charts.ChartType;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ChartTypeElement {
	
	protected int id; // For insert in table only
	protected ChartType type;
	
	
	public ChartTypeElement(int id, ChartType type){
		this.id=id;
		this.type=type;
	}
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}


	public ChartType getType() {
		return type;
	}
	

	public void setType(ChartType type) {
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
