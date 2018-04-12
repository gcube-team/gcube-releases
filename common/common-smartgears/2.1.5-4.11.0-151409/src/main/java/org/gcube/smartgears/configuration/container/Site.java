package org.gcube.smartgears.configuration.container;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.NotNull;

/**
 * The geographical site of the container.
 *  
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="site")
public class Site {

	@XmlElement
	@NotNull
	String country;
	
	@XmlElement
	@NotNull
	String location;
	
	@XmlElement
	@NotNull
	String latitude;
	
	@XmlElement
	@NotNull
	String longitude;
	
	/**
	 * Returns the country.
	 * @return the country
	 */
	public String country() {
		return country;
	}

	/**
	 * Sets the country.
	 * @param the country
	 * @return this configuration
	 */
	public Site country(String country) {
		this.country=country;
		return this;
	}
	
	
	/**
	 * Returns the latitude.
	 * @return the latitude
	 */
	public String latitude() {
		return latitude;
	}

	/**
	 * Sets the latitude.
	 * @param the latitude
	 * @return this configuration
	 */
	public Site latitude(String latitude) {
		this.latitude=latitude;
		return this;
	}

	
	/**
	 * Returns the longitude.
	 * @return the longitude
	 */
	public String longitude() {
		return longitude;
	}

	/**
	 * Sets the longitude.
	 * @param the longitude
	 * @return this configuration
	 */
	public Site longitude(String longitude) {
		this.longitude=longitude;
		return this;
	}
	
	/**
	 * Returns the location.
	 * @return the location
	 */
	public String location() {
		return location;
	}

	/**
	 * Sets the location.
	 * @param the location
	 * @return this location
	 */
	public Site location(String location) {
		this.location=location;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Site other = (Site) obj;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}
		
	
	
}