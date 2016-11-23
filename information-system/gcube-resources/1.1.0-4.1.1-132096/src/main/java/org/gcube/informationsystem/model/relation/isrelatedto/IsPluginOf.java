/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsPluginOfImpl;
import org.gcube.informationsystem.model.entity.resource.Plugin;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isPluginOf
 */
@JsonDeserialize(as=IsPluginOfImpl.class)
public interface IsPluginOf<Out extends Plugin, In extends Software> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsPluginOf"; // IsPluginOf.class.getSimpleName();
	
}
