package org.gcube.portlets.user.td.gwtservice.shared.tr.clone;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class CloneTabularResourceSession implements Serializable {

	private static final long serialVersionUID = -1896235499708614266L;

	private TRId trId;
	private TRId trIdClone;
	
	public CloneTabularResourceSession(){
		
	}
	
	public CloneTabularResourceSession(TRId trId){
		this.trId=trId;
		this.trIdClone=null;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	
	public TRId getTrIdClone() {
		return trIdClone;
	}

	public void setTrIdClone(TRId trIdClone) {
		this.trIdClone = trIdClone;
	}

	@Override
	public String toString() {
		return "CloneTabularResourceSession [trId=" + trId + ", trIdClone="
				+ trIdClone + "]";
	}

	
}
