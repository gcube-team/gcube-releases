/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.informationsystem.model.impl.entity.ResourceImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Software.NAME)
public class SoftwareImpl extends ResourceImpl implements Software {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 190088853237846140L;

}
