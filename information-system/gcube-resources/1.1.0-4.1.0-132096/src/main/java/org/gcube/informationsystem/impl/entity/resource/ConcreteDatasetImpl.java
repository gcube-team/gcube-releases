/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.model.entity.resource.ConcreteDataset;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=ConcreteDataset.NAME)
public class ConcreteDatasetImpl extends DatasetImpl implements ConcreteDataset {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 5183624758026295787L;
	
}
