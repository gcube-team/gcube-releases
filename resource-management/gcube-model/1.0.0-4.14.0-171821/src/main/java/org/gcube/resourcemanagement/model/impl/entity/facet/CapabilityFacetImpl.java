/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.facet;

import org.gcube.informationsystem.model.impl.entity.FacetImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.CapabilityFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=CapabilityFacet.NAME)
public class CapabilityFacetImpl extends FacetImpl implements CapabilityFacet {
	
	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -4036703255922676717L;
	
	protected String name;
	protected String description;
	protected String qualifier;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	
	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	
	@Override
	public String getQualifier() {
		return this.qualifier;
	}

	@Override
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

}
