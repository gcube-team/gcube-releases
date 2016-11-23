package org.gcube.portlets.user.td.gwtservice.shared.tr.rows;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DeleteRowsSession implements Serializable {

	private static final long serialVersionUID = -4503878699159491057L;
	
	protected TRId trId;
	protected ArrayList<String> rows;

	public DeleteRowsSession(){
		
	}
	
	public DeleteRowsSession(TRId trId, ArrayList<String> rows){
		this.trId=trId;
		this.rows=rows;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ArrayList<String> getRows() {
		return rows;
	}

	public void setRows(ArrayList<String> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "DeleteRowsSession [trId=" + trId + ", rows=" + rows + "]";
	}
	
	
}
