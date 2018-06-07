/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.CPUFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=CPUFacet.NAME)
public class CPUFacetImpl extends FacetImpl implements CPUFacet {
	
	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -870802380193638592L;
	
	protected String model;
	protected String vendor;
	protected String clockSpeed;
	
	@Override
	public String getModel() {
		return this.model;
	}

	@Override
	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public String getVendor() {
		return this.vendor;
	}

	@Override
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	
	@Override
	public String getClockSpeed() {
		return this.clockSpeed;
	}

	@Override
	public void setClockSpeed(String clockSpeed) {
		this.clockSpeed = clockSpeed;
	}

}
