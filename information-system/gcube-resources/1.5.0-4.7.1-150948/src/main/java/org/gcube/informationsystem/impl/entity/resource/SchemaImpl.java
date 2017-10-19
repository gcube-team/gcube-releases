/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.impl.entity.ResourceImpl;
import org.gcube.informationsystem.model.entity.resource.Schema;

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
