/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.facet.PeripheralFacet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class PeripheralFacetImpl extends FacetImpl implements PeripheralFacet {

	protected String model;
	
	protected String vendor;
	
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

}
