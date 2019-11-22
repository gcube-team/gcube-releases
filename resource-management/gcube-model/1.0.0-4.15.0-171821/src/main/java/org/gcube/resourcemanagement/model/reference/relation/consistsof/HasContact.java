/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.informationsystem.model.reference.annotations.Abstract;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasContactImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasContact
 */
@Abstract
@JsonDeserialize(as=HasContactImpl.class)
public interface HasContact<Out extends Resource, In extends ContactFacet> 
	extends ConsistsOf<Out, In> {

	public static final String NAME = "HasContact"; //HasContact.class.getSimpleName();
}
