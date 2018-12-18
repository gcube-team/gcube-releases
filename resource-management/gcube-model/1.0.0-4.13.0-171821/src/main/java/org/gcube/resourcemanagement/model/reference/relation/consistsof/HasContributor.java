/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasContributorImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasContributor
 */
@JsonDeserialize(as=HasContributorImpl.class)
public interface HasContributor<Out extends Resource, In extends ContactFacet> 
	extends HasContact<Out, In> {

	public static final String NAME = "HasContributor"; //HasContributor.class.getSimpleName();
}
