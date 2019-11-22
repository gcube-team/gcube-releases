/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.informationsystem.model.impl.entity.ResourceImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Service.NAME)
public abstract class ServiceImpl extends ResourceImpl implements Service {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -6020679647779327575L;

	
}
