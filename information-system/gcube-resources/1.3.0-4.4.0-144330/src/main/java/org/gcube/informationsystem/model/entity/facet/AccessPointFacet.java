/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import java.net.URI;
import java.util.List;

import org.gcube.informationsystem.impl.entity.facet.AccessPointFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.annotations.Key;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Access_Point_Facet
 */
@Key(fields={AccessPointFacet.ENDPOINT_PROPERTY})
@JsonDeserialize(as=AccessPointFacetImpl.class)
public interface AccessPointFacet extends Facet {
	
	public static final String NAME = "AccessPointFacet"; //AccessPointFacet.class.getSimpleName();
	public static final String DESCRIPTION = "This facet is expected to "
			+ "capture information on “access points” for a resource, i.e. any "
			+ "endpoint to interact with the resource via a known protocol.";
	public static final String VERSION = "1.0.0";
	
	public static final String ENDPOINT_PROPERTY = "endpoint";
	
	
	@ISProperty
	public String getEntryName();
	
	public void setEntryName(String entryName);
	
	@ISProperty(name=ENDPOINT_PROPERTY, mandatory=true, readonly=true, nullable=false)
	public URI getEndpoint();
	
	public void setEndpoint(URI endpoint);
	
	@ISProperty
	public String getProtocol();
	
	public void setProtocol(String protocol);
	
	@ISProperty
	public String getDescription();
	
	public void setDescription(String description);
	
	@ISProperty
	public ValueSchema getAuthorization();
	
	public void setAuthorization(ValueSchema authorization);
	
	@ISProperty
	public List<ValueSchema> getProperties();
	
	public void setProperties(List<ValueSchema> properties);

}
