/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.DependsOnImpl;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#dependsOn
 */
@JsonDeserialize(as=DependsOnImpl.class)
public interface DependsOn<Out extends Software, In extends Software> 
	extends IsRelatedTo<Out, In> {
	
	public static final String NAME = "DependsOn"; // DependsOn.class.getSimpleName();

}
