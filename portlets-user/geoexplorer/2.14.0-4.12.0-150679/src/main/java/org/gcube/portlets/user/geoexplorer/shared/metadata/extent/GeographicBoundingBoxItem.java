package org.gcube.portlets.user.geoexplorer.shared.metadata.extent;

import java.io.Serializable;


public class GeographicBoundingBoxItem extends GeographicExtentItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5147007499567338111L;
	
    /**
     * Returns the western-most coordinate of the limit of the
     * dataset extent. The value is expressed in longitude in
     * decimal degrees (positive east).
     */
    private double westBoundLongitude;

    /**
     * Returns the eastern-most coordinate of the limit of the
     * dataset extent. The value is expressed in longitude in
     * decimal degrees (positive east).
     */
    private double eastBoundLongitude;

    /**
     * Returns the southern-most coordinate of the limit of the
     * dataset extent. The value is expressed in latitude in
     * decimal degrees (positive north).
     */
    private double southBoundLatitude;

    /**
     * Returns the northern-most, coordinate of the limit of the
     * dataset extent. The value is expressed in latitude in
     * decimal degrees (positive north).
     *
     */
    private double northBoundLatitude;

	private String BBOX;


    public GeographicBoundingBoxItem() {
	}
    
	public GeographicBoundingBoxItem(double westBoundLongitude,
			double eastBoundLongitude, double southBoundLatitude,
			double northBoundLatitude) {
		super();
		this.westBoundLongitude = westBoundLongitude;
		this.eastBoundLongitude = eastBoundLongitude;
		this.southBoundLatitude = southBoundLatitude;
		this.northBoundLatitude = northBoundLatitude;
	}

	public double getWestBoundLongitude() {
		return westBoundLongitude;
	}

	public void setWestBoundLongitude(double westBoundLongitude) {
		this.westBoundLongitude = westBoundLongitude;
	}

	public double getEastBoundLongitude() {
		return eastBoundLongitude;
	}

	public void setEastBoundLongitude(double eastBoundLongitude) {
		this.eastBoundLongitude = eastBoundLongitude;
	}

	public double getSouthBoundLatitude() {
		return southBoundLatitude;
	}

	public void setSouthBoundLatitude(double southBoundLatitude) {
		this.southBoundLatitude = southBoundLatitude;
	}

	public double getNorthBoundLatitude() {
		return northBoundLatitude;
	}

	public void setNorthBoundLatitude(double northBoundLatitude) {
		this.northBoundLatitude = northBoundLatitude;
	}

	public void setBBOX(String string) {
		this.BBOX = string;
		
	}

	public String getBBOX() {
		return BBOX;
	}

	public void setBboxToString(String bboxToString) {
		this.BBOX = bboxToString;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeographicBoundingBoxItem [westBoundLongitude=");
		builder.append(westBoundLongitude);
		builder.append(", eastBoundLongitude=");
		builder.append(eastBoundLongitude);
		builder.append(", southBoundLatitude=");
		builder.append(southBoundLatitude);
		builder.append(", northBoundLatitude=");
		builder.append(northBoundLatitude);
		builder.append(", bboxToString=");
		builder.append(BBOX);
		builder.append("]");
		return builder.toString();
	}
	
	

}
