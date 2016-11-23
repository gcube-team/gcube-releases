/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.SimplePropertyFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Simple_Property_Facet
 */
@JsonDeserialize(as=SimplePropertyFacetImpl.class)
public interface SimplePropertyFacet extends Facet {
	
	public static final String NAME = "SimplePropertyFacet"; // SimplePropertyFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Collect name-value property";
	public static final String VERSION = "1.0.0";
	
	@ISProperty(mandatory=true, nullable=false)
	public String getName();
	
	public void setName(String schema);
	
	@ISProperty(mandatory=true, nullable=false)
	public String getValue();
	
	public void setValue(String value);
	
}
