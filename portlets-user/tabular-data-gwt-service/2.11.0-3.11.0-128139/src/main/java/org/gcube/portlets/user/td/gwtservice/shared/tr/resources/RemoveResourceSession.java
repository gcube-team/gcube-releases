package org.gcube.portlets.user.td.gwtservice.shared.tr.resources;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class RemoveResourceSession implements Serializable {

	private static final long serialVersionUID = -48669918467995522L;
	private TRId trId;
	private ArrayList<ResourceTDDescriptor> resources;

	public RemoveResourceSession(){
		
	}

	public RemoveResourceSession(TRId trId,
			ArrayList<ResourceTDDescriptor> resources) {
		super();
		this.trId = trId;
		this.resources = resources;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ArrayList<ResourceTDDescriptor> getResources() {
		return resources;
	}

	public void setResources(ArrayList<ResourceTDDescriptor> resources) {
		this.resources = resources;
	}

	@Override
	public String toString() {
		return "RemoveResourceSession [trId=" + trId + ", resources="
				+ resources + "]";
	}

	
	
	
	
}
