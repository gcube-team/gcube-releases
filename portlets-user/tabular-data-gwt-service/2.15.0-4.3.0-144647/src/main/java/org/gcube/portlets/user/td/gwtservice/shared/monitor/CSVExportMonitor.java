/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.monitor;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.task.State;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabExportMetadata;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class CSVExportMonitor implements Serializable {

private static final long serialVersionUID = -5998841163159590481L;
	
	private float progress;
	private State status;
	private String statusDescription;
	private Throwable error;
	private TRId trId;
	private TabExportMetadata trExportMetadata;
	
	
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

	public TabExportMetadata getTrExportMetadata() {
		return trExportMetadata;
	}

	public void setTrExportMetadata(TabExportMetadata trExportMetadata) {
		this.trExportMetadata = trExportMetadata;
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
		return "CSVExportMonitor [progress=" + progress + ", status=" + status
				+ ", statusDescription=" + statusDescription + ", error="
				+ error + ", trId=" + trId + ", trExportMetadata="
				+ trExportMetadata + "]";
	}


	

		
}
