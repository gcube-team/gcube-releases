/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.impl.entity.ResourceImpl;
import org.gcube.informationsystem.model.entity.resource.Dataset;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=Dataset.NAME)
public class DatasetImpl extends ResourceImpl implements Dataset {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -8344300098282501665L;
	
}
