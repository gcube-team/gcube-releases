package org.gcube.portlets.user.td.gwtservice.shared.tr.rows;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class EditRowSession implements Serializable {

	private static final long serialVersionUID = -4503878699159491057L;

	private TRId trId;

	private ArrayList<ColumnData> columns;

	private boolean newRow;

	/**
	 * A HashMap from rowId to HashMap from columnId to value
	 */
	private HashMap<String, HashMap<String, String>> rowsMaps;

	private ArrayList<String> rowsId;

	public EditRowSession() {

	}

	public EditRowSession(TRId trId, ArrayList<ColumnData> columns,
			HashMap<String, HashMap<String, String>> rowsMaps) {
		this.trId = trId;
		this.columns = columns;
		this.rowsMaps = rowsMaps;
		this.newRow = true;
	}

	public EditRowSession(TRId trId, ArrayList<ColumnData> columns,
			HashMap<String, HashMap<String, String>> rowsMaps,
			ArrayList<String> rowsId) {
		this.trId = trId;
		this.columns = columns;
		this.rowsMaps = rowsMaps;
		this.newRow = false;
		this.rowsId = rowsId;
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

	public boolean isNewRow() {
		return newRow;
	}

	public void setNewRow(boolean newRow) {
		this.newRow = newRow;
	}

	public HashMap<String, HashMap<String, String>> getRowsMaps() {
		return rowsMaps;
	}

	public void setRowsMaps(HashMap<String, HashMap<String, String>> rowsMaps) {
		this.rowsMaps = rowsMaps;
	}

	public ArrayList<String> getRowsId() {
		return rowsId;
	}

	public void setRowsId(ArrayList<String> rowsId) {
		this.rowsId = rowsId;
	}

	@Override
	public String toString() {
		return "EditRowSession [trId=" + trId + ", columns=" + columns
				+ ", newRow=" + newRow + ", rowsMaps=" + rowsMaps + ", rowsId="
				+ rowsId + "]";
	}

}
