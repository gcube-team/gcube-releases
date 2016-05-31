package org.gcube.portlets.user.td.gwtservice.shared.tr.table;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ChangeTableTypeSession implements Serializable {

	private static final long serialVersionUID = -2634056887663230720L;

	protected TRId trId;
	protected TableType tableType;

	public ChangeTableTypeSession(){
		
	}
	
	public ChangeTableTypeSession(TRId trId, TableType tableType){
		this.trId=trId;
		this.tableType=tableType;
	}
	
	
	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public TableType getTableType() {
		return tableType;
	}

	public void setTableType(TableType tableType) {
		this.tableType = tableType;
	}

	@Override
	public String toString() {
		return "ChangeTableTypeSession [trId=" + trId + ", tableType="
				+ tableType + "]";
	}

}
