package org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ReplaceByExternalSession implements Serializable {

	private static final long serialVersionUID = 3791535835292213831L;
	protected TRId trId;
	protected TabResource currentTabularResource;
	protected TabResource externalTabularResource;
	protected ColumnData currentColumn;
	protected ColumnData replaceColumn;
	protected ArrayList<ColumnData> currentColumns;
	protected ArrayList<ColumnData> externalColumns;
	protected ArrayList<ReplaceByExternalColumnsMapping> columnsMatch;

	public ReplaceByExternalSession() {

	}
	
	/**
	 * 
	 * @param trId
	 * @param currentTabularResource
	 * @param externalTabularResource
	 * @param currentColumn
	 * @param replaceColumn
	 * @param currentColumns
	 * @param externalColumns
	 * @param columnsMatch
	 */
	public ReplaceByExternalSession(TRId trId,
			TabResource currentTabularResource,
			TabResource externalTabularResource, ColumnData currentColumn,
			ColumnData replaceColumn, ArrayList<ColumnData> currentColumns,
			ArrayList<ColumnData> externalColumns,
			ArrayList<ReplaceByExternalColumnsMapping> columnsMatch) {
		super();
		this.trId = trId;
		this.currentTabularResource = currentTabularResource;
		this.externalTabularResource = externalTabularResource;
		this.currentColumn = currentColumn;
		this.replaceColumn = replaceColumn;
		this.currentColumns = currentColumns;
		this.externalColumns = externalColumns;
		this.columnsMatch = columnsMatch;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public TabResource getCurrentTabularResource() {
		return currentTabularResource;
	}

	public void setCurrentTabularResource(TabResource currentTabularResource) {
		this.currentTabularResource = currentTabularResource;
	}

	public TabResource getExternalTabularResource() {
		return externalTabularResource;
	}

	public void setExternalTabularResource(TabResource externalTabularResource) {
		this.externalTabularResource = externalTabularResource;
	}

	public ColumnData getCurrentColumn() {
		return currentColumn;
	}

	public void setCurrentColumn(ColumnData currentColumn) {
		this.currentColumn = currentColumn;
	}

	public ColumnData getReplaceColumn() {
		return replaceColumn;
	}

	public void setReplaceColumn(ColumnData replaceColumn) {
		this.replaceColumn = replaceColumn;
	}

	public ArrayList<ColumnData> getCurrentColumns() {
		return currentColumns;
	}

	public void setCurrentColumns(ArrayList<ColumnData> currentColumns) {
		this.currentColumns = currentColumns;
	}

	public ArrayList<ColumnData> getExternalColumns() {
		return externalColumns;
	}

	public void setExternalColumns(ArrayList<ColumnData> externalColumns) {
		this.externalColumns = externalColumns;
	}

	public ArrayList<ReplaceByExternalColumnsMapping> getColumnsMatch() {
		return columnsMatch;
	}

	public void setColumnsMatch(
			ArrayList<ReplaceByExternalColumnsMapping> columnsMatch) {
		this.columnsMatch = columnsMatch;
	}

	@Override
	public String toString() {
		return "ReplaceByExternalSession [trId=" + trId
				+ ", currentTabularResource=" + currentTabularResource
				+ ", externalTabularResource=" + externalTabularResource
				+ ", currentColumn=" + currentColumn + ", replaceColumn="
				+ replaceColumn + ", currentColumns=" + currentColumns
				+ ", externalColumns=" + externalColumns + ", columnsMatch="
				+ columnsMatch + "]";
	}

}
