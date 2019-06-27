/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.IsConfiguredByImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.ConfigurationTemplate;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isConfiguredBy
 */
@JsonDeserialize(as=IsConfiguredByImpl.class)
public interface IsConfiguredBy<Out extends Software, In extends ConfigurationTemplate> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsConfiguredBy"; // IsConfiguredBy.class.getSimpleName();
	
}
