/**
 * 
 */
package org.gcube.informationsystem.model.relation.consistsof;

import org.gcube.informationsystem.impl.relation.consistsof.HasCreatorImpl;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.ConsistsOf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasCreator
 */
@JsonDeserialize(as=HasCreatorImpl.class)
public interface HasCreator<Out extends Resource, In extends ContactFacet> 
	extends ConsistsOf<Out, In> {

	public static final String NAME = "HasCreator"; // HasCreator.class.getSimpleName();
}
