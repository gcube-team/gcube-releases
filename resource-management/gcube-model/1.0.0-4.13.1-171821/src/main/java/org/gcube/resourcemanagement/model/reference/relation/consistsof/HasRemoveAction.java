package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasRemoveActionImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ActionFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * An action triggered when a {@link Service} is deactivated.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
@JsonDeserialize(as=HasRemoveActionImpl.class)
public interface HasRemoveAction<Out extends Service, In extends ActionFacet> 
extends HasAction<Out, In> {
	
	public static final String NAME = "HasRemoveAction"; 

}
