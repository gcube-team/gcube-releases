/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsDerivationOfImpl;
import org.gcube.informationsystem.model.entity.resource.Configuration;
import org.gcube.informationsystem.model.entity.resource.ConfigurationTemplate;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isDerivationOf
 */
@JsonDeserialize(as=IsDerivationOfImpl.class)
public interface IsDerivationOf<Out extends Configuration, In extends ConfigurationTemplate> 
	extends IsRelatedTo<Out, In> {
	
	public static final String NAME = "IsDerivationOf"; // IsDerivationOf.class.getSimpleName();

}
