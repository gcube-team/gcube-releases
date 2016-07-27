/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.facet.IdentificationFacet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class IdentificationFacetImpl extends FacetImpl implements IdentificationFacet {

	protected String value;
	
	protected IdentificationType type;
	
	protected boolean persistent;
	
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
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the type
	 */
	@Override
	public IdentificationType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(IdentificationType type) {
		this.type = type;
	}

	/**
	 * @return the persistent
	 */
	@Override
	public boolean isPersistent() {
		return persistent;
	}

	/**
	 * @param persistent the persistent to set
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

}
