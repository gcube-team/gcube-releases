package org.gcube.portlets.user.td.gwtservice.shared.tr.rows;

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
public class DuplicatesSession implements Serializable {

	private static final long serialVersionUID = -4503878699159491057L;
	
	public enum DuplicateOp {
		VALIDATE, DELETE;
	}
	
	protected TRId trId;
	protected ArrayList<ColumnData> columns;
	protected DuplicateOp duplicateOp;
	
	
	public DuplicatesSession() {

	}

	public DuplicatesSession(TRId trId, ArrayList<ColumnData> columns,DuplicateOp duplicateOp) {
		this.trId = trId;
		this.columns = columns;
		this.duplicateOp=duplicateOp;
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

	public DuplicateOp getDuplicateOp() {
		return duplicateOp;
	}

	public void setDuplicateOp(DuplicateOp duplicateOp) {
		this.duplicateOp = duplicateOp;
	}

	@Override
	public String toString() {
		return "DuplicateSession [trId=" + trId + ", columns=" + columns
				+ ", duplicateOp=" + duplicateOp + "]";
	}

	
}
