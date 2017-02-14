/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.SimplePropertyFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=SimplePropertyFacet.NAME)
public class SimplePropertyFacetImpl extends FacetImpl implements SimplePropertyFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 3217017583429546546L;
	
	protected String name;
	protected String value;
	
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param value the value to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the value
	 */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}
	
}
