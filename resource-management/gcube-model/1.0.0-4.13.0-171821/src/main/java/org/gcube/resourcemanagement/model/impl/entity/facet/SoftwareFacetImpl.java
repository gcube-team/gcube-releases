/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.facet;

import org.gcube.informationsystem.model.impl.entity.FacetImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.NameProperty;
import org.gcube.resourcemanagement.model.reference.entity.facet.SoftwareFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=SoftwareFacet.NAME)
public class SoftwareFacetImpl extends FacetImpl implements SoftwareFacet, NameProperty {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 1062768788238515868L;
	
	
	protected String name;
	protected String group;
	protected String version;
	
	protected String description;
	protected String qualifier;
	protected boolean optional;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
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

	@Override
	public boolean isOptional() {
		return this.optional;
	}

	@Override
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

}
