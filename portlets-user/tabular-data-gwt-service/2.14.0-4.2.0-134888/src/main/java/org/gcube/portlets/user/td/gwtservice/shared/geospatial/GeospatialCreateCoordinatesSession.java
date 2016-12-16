package org.gcube.portlets.user.td.gwtservice.shared.geospatial;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.geospatial.GeospatialCoordinatesType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class GeospatialCreateCoordinatesSession implements Serializable {

	private static final long serialVersionUID = 7962959445625100578L;
	private TRId trId;
	private ColumnData latitude;
	private ColumnData longitude;
	private GeospatialCoordinatesType type;
	private boolean hasQuadrant;
	private ColumnData quadrant;
	private Double resolution;

	public GeospatialCreateCoordinatesSession() {
	}

	/**
	 * 
	 * @param trId
	 * @param latitude
	 * @param longitude
	 * @param type
	 * @param hasQuadrant
	 *            TODO
	 * @param quadrant
	 *            TODO
	 * @param resolution
	 *            TODO
	 */
	public GeospatialCreateCoordinatesSession(TRId trId, ColumnData latitude,
			ColumnData longitude, GeospatialCoordinatesType type,
			boolean hasQuadrant, ColumnData quadrant, Double resolution) {
		super();
		this.trId = trId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
		this.hasQuadrant = hasQuadrant;
		this.quadrant = quadrant;
		this.resolution = resolution;
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

	public GeospatialCoordinatesType getType() {
		return type;
	}

	public void setType(GeospatialCoordinatesType type) {
		this.type = type;
	}

	public boolean isHasQuadrant() {
		return hasQuadrant;
	}

	public void setHasQuadrant(boolean hasQuadrant) {
		this.hasQuadrant = hasQuadrant;
	}

	public ColumnData getQuadrant() {
		return quadrant;
	}

	public void setQuadrant(ColumnData quadrant) {
		this.quadrant = quadrant;
	}

	public Double getResolution() {
		return resolution;
	}

	public void setResolution(Double resolution) {
		this.resolution = resolution;
	}

	@Override
	public String toString() {
		return "GeospatialCreateCoordinatesSession [trId=" + trId
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", type=" + type + ", hasQuadrant=" + hasQuadrant
				+ ", quadrant=" + quadrant + ", resolution=" + resolution + "]";
	}

}
