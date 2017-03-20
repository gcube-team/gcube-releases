/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.LocationFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=LocationFacet.NAME)
public class LocationFacetImpl extends FacetImpl implements LocationFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 4041460426127480418L;
	
	protected String country;
	protected String location;
	protected String latitude;
	protected String longitude;
	
	/**
	 * @return the country
	 */
	@Override
	public String getCountry() {
		return country;
	}
	
	/**
	 * @param country the country to set
	 */
	@Override
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * @return the location
	 */
	@Override
	public String getLocation() {
		return location;
	}
	
	/**
	 * @param location the location to set
	 */
	@Override
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	
	/**
	 * @param latitude the latitude to set
	 */
	@Override
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * @return the longitude
	 */
	@Override
	public String getLongitude() {
		return longitude;
	}
	
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
}
