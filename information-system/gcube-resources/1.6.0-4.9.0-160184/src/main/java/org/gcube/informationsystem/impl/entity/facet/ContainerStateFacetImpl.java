/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import org.gcube.informationsystem.model.entity.facet.ContainerStateFacet;

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
