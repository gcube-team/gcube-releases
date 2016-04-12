package org.gcube.portlets.user.td.gwtservice.shared.monitor;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.task.State;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SDMXImportMonitor implements Serializable {
	
	private static final long serialVersionUID = -5998841163159590481L;
	
	protected float progress;
	protected State status;
	protected String statusDescription;
	protected Throwable error;
	protected TRId trId;
	
	public float getProgress(){
		return progress;
	};

	public State getStatus(){
		return status;
	}
	
	public String getStatusDescription(){
		return statusDescription;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public void setStatus(State status) {
		this.status = status;
	}
	
	public void setStatus(int status) {
		this.status = State.values()[status];
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}


	@Override
	public String toString() {
		return "SDMXImportMonitor [progress=" + progress + ", status=" + status
				+ ", statusDescription=" + statusDescription + ", error="
				+ error + ", trId=" + trId  + "]";
	}
	
	
}
