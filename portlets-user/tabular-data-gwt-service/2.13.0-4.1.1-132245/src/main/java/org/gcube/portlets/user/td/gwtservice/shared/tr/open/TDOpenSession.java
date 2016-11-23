/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.tr.open;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDOpenSession implements Serializable {

	private static final long serialVersionUID = 4176034045408445284L;
	
	protected String id;
	
	protected TabResource selectedTabResource;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	public TabResource getSelectedTabResource() {
		return selectedTabResource;
	}
	public void setSelectedTabResource(TabResource selectedTabResource) {
		this.selectedTabResource = selectedTabResource;
	}

	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("TDOpenSession [getId()=");
		builder.append(getId());
		builder.append(", getSelectedTabResource()=");
		builder.append(getSelectedTabResource());
		builder.append("]");
		return builder.toString();
		
	}
}
