/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.informationsystem.model.impl.entity.ResourceImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Dataset;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Dataset.NAME)
public class DatasetImpl extends ResourceImpl implements Dataset {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -8344300098282501665L;
	
}
