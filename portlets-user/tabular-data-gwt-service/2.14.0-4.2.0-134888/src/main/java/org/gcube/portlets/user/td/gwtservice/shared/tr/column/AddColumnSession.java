package org.gcube.portlets.user.td.gwtservice.shared.tr.column;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class AddColumnSession implements Serializable {
	
	private static final long serialVersionUID = -1896235499708614266L;
	
	private TRId trId;
	private ColumnMockUp columnMockUp;
	
	
	public AddColumnSession(){
		super();
	}
	
	
	public AddColumnSession(TRId trId, ColumnMockUp columnMockUp) {
		super();
		this.trId = trId;
		this.columnMockUp = columnMockUp;
	}



	public TRId getTrId() {
		return trId;
	}


	public void setTrId(TRId trId) {
		this.trId = trId;
	}


	public ColumnMockUp getColumnMockUp() {
		return columnMockUp;
	}


	public void setColumnMockUp(ColumnMockUp columnMockUp) {
		this.columnMockUp = columnMockUp;
	}


	@Override
	public String toString() {
		return "AddColumnSession [trId=" + trId + ", columnMockUp="
				+ columnMockUp + "]";
	}
	
	
	
}
