/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.impl.entity.ResourceImpl;
import org.gcube.informationsystem.model.entity.resource.Site;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=Site.NAME)
public class SiteImpl extends ResourceImpl implements Site {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -6923303652448686159L;

}
