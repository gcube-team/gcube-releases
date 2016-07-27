/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import java.net.URI;
import java.util.List;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Access-Point-Interface-Facet
 */
public interface AccessPointInterfaceFacet extends Facet {
	
	public static final String NAME =  AccessPointInterfaceFacet.class.getSimpleName();
	public static final String DESCRIPTION = "This facet is expected to "
			+ "capture information on “access points” for a resource, i.e. any "
			+ "endpoint to interact with the resource via a known protocol.";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public String getEntryName();
	
	public void setEntryName(String entryName);
	
	@ISProperty
	public URI getEndpoint();
	
	public void set(URI endpoint);
	
	@ISProperty
	public String getProtocol();
	
	public void setProtocol(String protocol);
	
	@ISProperty
	public String getDescription();
	
	public void setDescription(String description);
	
	@ISProperty
	public ValueSchema getAuthorization();
	
	public void setSpatial(ValueSchema authorization);
	
	@ISProperty
	public List<ValueSchema> getProperties();
	
	public void setProperties(List<ValueSchema> properties);

}
