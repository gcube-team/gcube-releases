/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.IsPluginOfImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Plugin;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isPluginOf
 */
@JsonDeserialize(as = IsPluginOfImpl.class)
public interface IsPluginOf<Out extends Plugin, In extends Software> extends DependsOn<Out,In> {
	
	public static final String NAME = "IsPluginOf"; // IsPluginOf.class.getSimpleName();
	
}
