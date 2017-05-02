/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsConfiguredByImpl;
import org.gcube.informationsystem.model.entity.resource.ConfigurationTemplate;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

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
