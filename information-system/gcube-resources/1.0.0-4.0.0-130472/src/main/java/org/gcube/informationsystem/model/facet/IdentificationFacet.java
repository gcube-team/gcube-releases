/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public interface IdentificationFacet extends Facet {
	
	public static final String NAME = IdentificationFacet.class.getSimpleName();
	public static final String DESCRIPTION = "This facet is to collect "
			+ "information on Identifiers that can be attached to a resource. ";
	public static final String VERSION = "1.0.0";

	public enum IdentificationType {
		URI, DOI, IRI, URL, URN, UUID
	}
	
	@ISProperty
	public String getValue();
	
	public void setValue(String value);
	
	@ISProperty
	public IdentificationType getType();
	
	public void setType(IdentificationType type);
	
	@ISProperty
	public boolean isPersistent();
	
	public void setPersistent(boolean persistent);
}
