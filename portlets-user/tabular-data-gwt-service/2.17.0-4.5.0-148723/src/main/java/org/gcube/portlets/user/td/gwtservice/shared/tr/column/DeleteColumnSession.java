package org.gcube.portlets.user.td.gwtservice.shared.tr.column;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class DeleteColumnSession implements Serializable {
	
	private static final long serialVersionUID = -1896235499708614266L;
	
	private TRId trId;
	private ArrayList<ColumnData> columns;
	
	
	public DeleteColumnSession(){
		
	}
	
	public DeleteColumnSession(TRId trId,ArrayList<ColumnData> columns){
		this.trId=trId;
		this.columns=columns;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ArrayList<ColumnData> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnData> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "DeleteColumnSession [trId=" + trId + ", columns=" + columns
				+ "]";
	}
	
	
	
	
	
}
