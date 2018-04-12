/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.extract;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ExtractCodelistSession implements Serializable {

	private static final long serialVersionUID = -2537172740573310646L;

	private TRId trId;
	private TabResource tabResource;
	private ArrayList<ColumnData> sourceColumns;
	private ArrayList<ExtractCodelistTargetColumn> targetColumns;
	private boolean automaticallyAttach;
	private ColumnData attachColumn;

	public ExtractCodelistSession() {
	}

	public ExtractCodelistSession(TRId trId, TabResource tabResource,
			ArrayList<ColumnData> sourceColumns,
			ArrayList<ExtractCodelistTargetColumn> targetColumns,
			boolean automaticallyAttach, ColumnData attachColumn) {
		this.trId = trId;
		this.tabResource = tabResource;
		this.sourceColumns = sourceColumns;
		this.targetColumns = targetColumns;
		this.automaticallyAttach = automaticallyAttach;
		this.attachColumn = attachColumn;
	}

	public ArrayList<ColumnData> getSourceColumns() {
		return sourceColumns;
	}

	public void setSourceColumns(ArrayList<ColumnData> sourceColumns) {
		this.sourceColumns = sourceColumns;
	}

	public ArrayList<ExtractCodelistTargetColumn> getTargetColumns() {
		return targetColumns;
	}

	public void setTargetColumns(
			ArrayList<ExtractCodelistTargetColumn> targetColumns) {
		this.targetColumns = targetColumns;
	}

	public TabResource getTabResource() {
		return tabResource;
	}

	public void setTabResource(TabResource tabResource) {
		this.tabResource = tabResource;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public boolean isAutomaticallyAttach() {
		return automaticallyAttach;
	}

	public void setAutomaticallyAttach(boolean automaticallyAttach) {
		this.automaticallyAttach = automaticallyAttach;
	}

	public ColumnData getAttachColumn() {
		return attachColumn;
	}

	public void setAttachColumn(ColumnData attachColumn) {
		this.attachColumn = attachColumn;
	}

	@Override
	public String toString() {
		return "ExtractCodelistSession [trId=" + trId + ", tabResource="
				+ tabResource + ", sourceColumns=" + sourceColumns
				+ ", targetColumns=" + targetColumns + ", automaticallyAttach="
				+ automaticallyAttach + ", attachColumn=" + attachColumn + "]";
	}

}
