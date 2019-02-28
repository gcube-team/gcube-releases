/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.facet;

import org.gcube.resourcemanagement.model.reference.entity.facet.ContainerStateFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ContainerStateFacet.NAME)
public class ContainerStateFacetImpl extends StateFacetImpl implements ContainerStateFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 6795158357114843672L;

}
