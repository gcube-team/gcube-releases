/**
 * 
 */
package org.gcube.informationsystem.model.relation.consistsof;

import org.gcube.informationsystem.impl.relation.consistsof.HasMaintainerImpl;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.ConsistsOf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasMaintainer
 */
@JsonDeserialize(as=HasMaintainerImpl.class)
public interface HasMaintainer<Out extends Resource, In extends ContactFacet> 
	extends ConsistsOf<Out, In> {

	public static final String NAME = "HasMaintainer"; // HasMaintainer.class.getSimpleName();
}
