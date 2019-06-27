/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.facet;

import org.gcube.resourcemanagement.model.reference.entity.facet.ServiceStateFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ServiceStateFacet.NAME)
public class ServiceStateFacetImpl extends StateFacetImpl implements ServiceStateFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -5718134380884679550L;

}
