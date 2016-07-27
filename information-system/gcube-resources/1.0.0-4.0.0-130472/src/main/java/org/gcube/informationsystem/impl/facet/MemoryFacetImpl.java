/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.facet.MemoryFacet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class MemoryFacetImpl extends FacetImpl implements MemoryFacet {

	protected long size;

	protected long used;
	
	protected String unit;
	
	@Override
	public long getSize() {
		return this.size;
	}

	@Override
	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public long getUsed() {
		return this.used;
	}

	@Override
	public void setUsed(long used) {
		this.used = used;
	}

	@Override
	public String getUnit() {
		return this.unit;
	}

	@Override
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
