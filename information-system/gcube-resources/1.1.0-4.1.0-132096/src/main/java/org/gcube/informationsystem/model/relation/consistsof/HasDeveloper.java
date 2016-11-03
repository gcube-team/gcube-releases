/**
 * 
 */
package org.gcube.informationsystem.model.relation.consistsof;

import org.gcube.informationsystem.impl.relation.consistsof.HasDeveloperImpl;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.ConsistsOf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasDeveloper
 */
@JsonDeserialize(as=HasDeveloperImpl.class)
public interface HasDeveloper<Out extends Resource, In extends ContactFacet> 
	extends ConsistsOf<Out, In> {

	public static final String NAME = "HasDeveloper"; // HasDeveloper.class.getSimpleName();
}
