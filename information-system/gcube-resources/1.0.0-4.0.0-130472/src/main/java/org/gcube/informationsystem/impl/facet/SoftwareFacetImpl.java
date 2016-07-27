/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.facet.SoftwareFacet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class SoftwareFacetImpl extends FacetImpl implements SoftwareFacet {

	protected String name;
	
	protected String group;
	
	protected String version;
	
	protected String description;
	
	protected String qualifier;
	
	protected String role;
	
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
	public String getRole() {
		return this.role;
	}

	@Override
	public void setRole(String role) {
		this.role = role;
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
