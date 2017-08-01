package org.gcube.portlets.user.td.gwtservice.shared.geospatial;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class GeospatialDownscaleCSquareSession implements Serializable {

	private static final long serialVersionUID = -7790774506217649775L;
	private TRId trId;
	private ColumnData csquareColumn;
	private String resolution;

	public GeospatialDownscaleCSquareSession() {
	}

	public GeospatialDownscaleCSquareSession(TRId trId, ColumnData csquareColumn, String resolution) {
		super();
		this.trId = trId;
		this.csquareColumn = csquareColumn;
		this.resolution = resolution;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ColumnData getCsquareColumn() {
		return csquareColumn;
	}

	public void setCsquareColumn(ColumnData csquareColumn) {
		this.csquareColumn = csquareColumn;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	@Override
	public String toString() {
		return "GeospatialDownscaleCSquareSession [trId=" + trId + ", csquareColumn=" + csquareColumn + ", resolution="
				+ resolution + "]";
	}

}
