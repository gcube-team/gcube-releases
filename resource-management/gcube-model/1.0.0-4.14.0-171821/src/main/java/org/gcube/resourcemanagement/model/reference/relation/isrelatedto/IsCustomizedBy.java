/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.IsCustomizedByImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.ConfigurationTemplate;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isCustomizedBy
 */
@JsonDeserialize(as=IsCustomizedByImpl.class)
public interface IsCustomizedBy<Out extends Service, In extends ConfigurationTemplate> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsCustomizedBy"; //IsCustomizedBy.class.getSimpleName();
	
}
