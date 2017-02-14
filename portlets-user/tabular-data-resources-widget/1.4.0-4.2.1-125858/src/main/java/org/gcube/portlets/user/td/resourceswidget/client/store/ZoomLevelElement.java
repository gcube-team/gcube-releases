package org.gcube.portlets.user.td.resourceswidget.client.store;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ZoomLevelElement {
	
	protected int id; // For insert in table only
	protected ZoomLevelType type;
	
	
	public ZoomLevelElement(int id, ZoomLevelType type){
		this.id=id;
		this.type=type;
	}
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}


	public ZoomLevelType getType() {
		return type;
	}
	

	public void setType(ZoomLevelType type) {
		this.type = type;
	}
	
	public String getLabel() {
		return type.getIdI18N();
	}

	@Override
	public String toString() {
		return "ChartTypeElement [id=" + id + ", type=" + type + "]";
	}

	
}
