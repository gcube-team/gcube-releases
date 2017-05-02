/**
 * 
 */
package org.gcube.informationsystem.model.entity.resource;

import org.gcube.informationsystem.impl.entity.resource.ConcreteDatasetImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Concrete_Dataset
 */
@JsonDeserialize(as=ConcreteDatasetImpl.class)
public interface ConcreteDataset extends Dataset {

	public static final String NAME = "ConcreteDataset"; // ConcreteDataset.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Dataset information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}
