/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.IsDerivationOfImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Configuration;
import org.gcube.resourcemanagement.model.reference.entity.resource.ConfigurationTemplate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isDerivationOf
 */
@JsonDeserialize(as=IsDerivationOfImpl.class)
public interface IsDerivationOf<Out extends Configuration, In extends ConfigurationTemplate> 
	extends IsRelatedTo<Out, In> {
	
	public static final String NAME = "IsDerivationOf"; // IsDerivationOf.class.getSimpleName();

}
