/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.SimpleFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=SimpleFacet.NAME)
public class SimpleFacetImpl extends FacetImpl implements SimpleFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 3217017583429546546L;
	
}
