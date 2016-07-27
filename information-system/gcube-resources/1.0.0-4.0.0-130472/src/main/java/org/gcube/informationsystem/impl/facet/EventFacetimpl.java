/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.facet.EventFacet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class EventFacetimpl extends FacetImpl implements EventFacet {

	protected ValueSchema type;
	
	protected ValueSchema date;

	@Override
	public ValueSchema getType() {
		return this.type;
	}

	@Override
	public void setType(ValueSchema type) {
		this.type = type;
	}

	@Override
	public ValueSchema getDate() {
		return this.date;
	}

	@Override
	public void setDate(ValueSchema date) {
		this.date = date;
	}

}
