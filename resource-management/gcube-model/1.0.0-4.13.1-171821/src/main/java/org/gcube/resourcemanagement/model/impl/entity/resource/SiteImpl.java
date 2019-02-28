/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.informationsystem.model.impl.entity.ResourceImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Site;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Site.NAME)
public class SiteImpl extends ResourceImpl implements Site {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -6923303652448686159L;

}
