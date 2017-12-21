/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsCustomizedByImpl;
import org.gcube.informationsystem.model.entity.resource.Configuration;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isCustomizedBy
 */
@JsonDeserialize(as=IsCustomizedByImpl.class)
public interface IsCustomizedBy<Out extends Service, In extends Configuration> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsCustomizedBy"; //IsCustomizedBy.class.getSimpleName();
	
}
