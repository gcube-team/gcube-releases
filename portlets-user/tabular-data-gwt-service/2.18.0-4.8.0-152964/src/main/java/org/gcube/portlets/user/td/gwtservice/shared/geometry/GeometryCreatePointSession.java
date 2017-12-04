package org.gcube.portlets.user.td.gwtservice.shared.geometry;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class GeometryCreatePointSession implements Serializable {

	private static final long serialVersionUID = 7962959445625100578L;
	private TRId trId;
	private ColumnData latitude;
	private ColumnData longitude;
	private String columnLabel;

	public GeometryCreatePointSession() {
	}

	public GeometryCreatePointSession(TRId trId, ColumnData latitude, ColumnData longitude, String columnLabel) {
		super();
		this.trId = trId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.columnLabel = columnLabel;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ColumnData getLatitude() {
		return latitude;
	}

	public void setLatitude(ColumnData latitude) {
		this.latitude = latitude;
	}

	public ColumnData getLongitude() {
		return longitude;
	}

	public void setLongitude(ColumnData longitude) {
		this.longitude = longitude;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	@Override
	public String toString() {
		return "GeometryCreatePointSession [trId=" + trId + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", columnLabel=" + columnLabel + "]";
	}

}
