package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.informationsystem.model.reference.annotations.Abstract;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasActionImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ActionFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Relation among a {@link Service} and its {@link ActionFacet}.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
@Abstract
@JsonDeserialize(as=HasActionImpl.class)
public interface HasAction<Out extends Service, In extends ActionFacet> extends ConsistsOf<Out, In> {
	
	public static final String NAME = "HasAction"; 
	
}
