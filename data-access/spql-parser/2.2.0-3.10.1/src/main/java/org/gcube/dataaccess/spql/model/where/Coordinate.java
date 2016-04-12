/**
 * 
 */
package org.gcube.dataaccess.spql.model.where;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class Coordinate {
	
	protected double latitude;
	protected double longitude;
	
	/**
	 * @param latitude
	 * @param longitude
	 */
	public Coordinate(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Coordinate [latitude=");
		builder.append(latitude);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append("]");
		return builder.toString();
	}
}
