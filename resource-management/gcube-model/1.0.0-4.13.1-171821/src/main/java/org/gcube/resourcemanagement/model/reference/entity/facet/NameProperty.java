package org.gcube.resourcemanagement.model.reference.entity.facet;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.entity.Facet;
/**
 * A name property for a {@link Facet}.
 *  
 * @author Manuele Simi (ISTI CNR)
 *
 */
public interface NameProperty {

	public static final String NAME_PROPERTY = "name";

	/**
	 * Gets the name of the facet.
	 * @return the name
	 */
	@ISProperty(name=NAME_PROPERTY, mandatory=true, nullable=false)
	public String getName();

	/**
	 * Sets the name of the facet.
	 * @param name the new name
	 */
	public void setName(String name);	

}
