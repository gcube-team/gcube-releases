/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.resourcemanagement.model.reference.entity.resource.ConcreteDataset;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ConcreteDataset.NAME)
public class ConcreteDatasetImpl extends DatasetImpl implements ConcreteDataset {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 5183624758026295787L;
	
}
