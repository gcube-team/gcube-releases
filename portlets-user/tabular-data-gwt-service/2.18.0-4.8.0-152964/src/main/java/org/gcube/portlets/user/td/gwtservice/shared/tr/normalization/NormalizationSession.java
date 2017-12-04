package org.gcube.portlets.user.td.gwtservice.shared.tr.normalization;

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
public class NormalizationSession implements Serializable {

	private static final long serialVersionUID = 4139331553059193758L;

	protected TRId trId;
	protected ArrayList<ColumnData> columns;
	protected String normalizedColumnName;
	protected String valueColumnName;

	public NormalizationSession() {

	}

	public NormalizationSession(TRId trId, ArrayList<ColumnData> columns, String normalizedColumnName,
			String valueColumnName) {
		this.trId = trId;
		this.columns = columns;
		this.normalizedColumnName = normalizedColumnName;
		this.valueColumnName = valueColumnName;
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

	public String getNormalizedColumnName() {
		return normalizedColumnName;
	}

	public void setNormalizedColumnName(String normalizedColumnName) {
		this.normalizedColumnName = normalizedColumnName;
	}

	public String getValueColumnName() {
		return valueColumnName;
	}

	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

	@Override
	public String toString() {
		return "NormalizationSession [trId=" + trId + ", columns=" + columns + ", normalizedColumnName="
				+ normalizedColumnName + ", valueColumnName=" + valueColumnName + "]";
	}

}
