/**
 * 
 */
package org.gcube.informationsystem.model.relation.consistsof;

import org.gcube.informationsystem.impl.relation.consistsof.HasContributorImpl;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.ConsistsOf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasContributor
 */
@JsonDeserialize(as=HasContributorImpl.class)
public interface HasContributor<Out extends Resource, In extends ContactFacet> 
	extends ConsistsOf<Out, In> {

	public static final String NAME = "HasContributor"; //HasContributor.class.getSimpleName();
}
