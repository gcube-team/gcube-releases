/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.facet.CoverageFacet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class CoverageFacetImpl extends FacetImpl implements CoverageFacet {

	protected ValueSchema spatial;

	protected ValueSchema temporal;

	@Override
	public ValueSchema getSpatial() {
		return this.spatial;
	}

	@Override
	public void setSpatial(ValueSchema spatial) {
		this.spatial = spatial;
	}

	@Override
	public ValueSchema getTemporal() {
		return temporal;
	}

	@Override
	public void setTemporal(ValueSchema temporal) {
		this.temporal = temporal;
	}

}
