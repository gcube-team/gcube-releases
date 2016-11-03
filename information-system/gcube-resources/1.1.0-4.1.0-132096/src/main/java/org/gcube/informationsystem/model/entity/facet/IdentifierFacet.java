/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.IdentifierFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.annotations.Key;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Identifier_Facet
 */
@Key(fields={IdentifierFacet.VALUE_PROPERTY, IdentifierFacet.TYPE_PROPERTY})
@JsonDeserialize(as=IdentifierFacetImpl.class)
public interface IdentifierFacet extends Facet {
	
	public static final String NAME = "IdentifierFacet"; // IdentifierFacet.class.getSimpleName();
	public static final String DESCRIPTION = "This facet collects "
			+ "information on Identifiers that can be attached to a resource. ";
	public static final String VERSION = "1.0.0";

	public static final String VALUE_PROPERTY = "value";
	public static final String TYPE_PROPERTY = "type";
	
	public enum IdentificationType {
		URI, DOI, IRI, URL, URN, UUID
	}
	
	@ISProperty(name=VALUE_PROPERTY, mandatory=true, nullable=false)
	public String getValue();
	
	public void setValue(String value);
	
	@ISProperty(name=TYPE_PROPERTY, mandatory=true, nullable=false)
	public IdentificationType getType();
	
	public void setType(IdentificationType type);
	
	@ISProperty
	public boolean isPersistent();
	
	public void setPersistent(boolean persistent);
}
