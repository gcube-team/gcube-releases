/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.MemoryFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=MemoryFacet.NAME)
public class MemoryFacetImpl extends FacetImpl implements MemoryFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 6670219616322243388L;
	
	protected long size;
	protected long used;
	protected MemoryUnit unit;
	
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
	public MemoryUnit getUnit() {
		return this.unit;
	}

	@Override
	public void setUnit(MemoryUnit unit) {
		this.unit = unit;
	}

}
