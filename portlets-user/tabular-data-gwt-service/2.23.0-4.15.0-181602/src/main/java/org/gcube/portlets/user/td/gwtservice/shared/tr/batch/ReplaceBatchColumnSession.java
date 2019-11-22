package org.gcube.portlets.user.td.gwtservice.shared.tr.batch;

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
public class ReplaceBatchColumnSession implements Serializable {

	private static final long serialVersionUID = -1896235499708614266L;

	protected TRId trId;
	protected ColumnData columnData;
	protected ArrayList<ReplaceEntry> replaceEntryList;
	protected boolean replaceDimension;
	protected ColumnData connection;

	

	public ReplaceBatchColumnSession() {
	}

	public ReplaceBatchColumnSession(TRId trId, ColumnData columnData,
			ArrayList<ReplaceEntry> replaceEntryList, boolean replaceDimension,
			ColumnData connection) {
		this.trId = trId;
		this.columnData = columnData;
		this.replaceEntryList = replaceEntryList;
		this.replaceDimension = replaceDimension;
		this.connection=connection;
	}

	
	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ColumnData getColumnData() {
		return columnData;
	}

	public void setColumnData(ColumnData columnData) {
		this.columnData = columnData;
	}

	public ArrayList<ReplaceEntry> getReplaceEntryList() {
		return replaceEntryList;
	}

	public void setReplaceEntryList(ArrayList<ReplaceEntry> replaceEntryList) {
		this.replaceEntryList = replaceEntryList;
	}

	public boolean isReplaceDimension() {
		return replaceDimension;
	}

	public void setReplaceDimension(boolean replaceDimension) {
		this.replaceDimension = replaceDimension;
	}
	
	
	public ColumnData getConnection() {
		return connection;
	}

	public void setConnection(ColumnData connection) {
		this.connection = connection;
	}

	@Override
	public String toString() {
		return "ReplaceBatchColumnSession [trId=" + trId + ", columnData="
				+ columnData + ", replaceEntryList=" + replaceEntryList
				+ ", replaceDimension=" + replaceDimension + ", connection="
				+ connection + "]";
	}

	
	
	

}
