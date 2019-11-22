/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.informationsystem.model.impl.entity.ResourceImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Schema;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Schema.NAME)
public class SchemaImpl extends ResourceImpl implements Schema {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -2667306128901183874L;

	
}
