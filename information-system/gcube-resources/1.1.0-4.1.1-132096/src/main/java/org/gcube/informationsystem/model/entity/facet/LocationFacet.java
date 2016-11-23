/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.LocationFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Location_Facet
 */
@JsonDeserialize(as=LocationFacetImpl.class)
public interface LocationFacet extends Facet {

	public static final String NAME = "LocationFacet"; // LocationFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Location information";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public String getCountry();
	
	public void setCountry(String country);

	@ISProperty
	public String getLocation();
	
	public void setLocation(String location);
	
	@ISProperty()
	public String getLatitude();
	
	public void setLatitude(String latitude);
	
	@ISProperty()
	public String getLongitude();
	
	public void setLongitude(String longitude);
	
}
