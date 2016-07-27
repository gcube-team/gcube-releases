/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.facet.CPUFacet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class CPUFacetImpl extends FacetImpl implements CPUFacet {
	
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
