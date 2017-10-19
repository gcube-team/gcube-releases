/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class Coordinate implements IsSerializable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2357675565732391317L;

	protected float latitude;
	protected float longitude;

	public Coordinate(){}

	/**
	 * @param latitude
	 * @param longitude
	 */
	public Coordinate(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(float longitude) {
		this.longitude = longitude;
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
