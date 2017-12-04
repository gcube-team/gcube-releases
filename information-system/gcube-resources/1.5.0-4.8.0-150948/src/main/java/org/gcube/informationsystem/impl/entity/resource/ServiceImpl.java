/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.impl.entity.ResourceImpl;
import org.gcube.informationsystem.model.entity.resource.Service;

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
