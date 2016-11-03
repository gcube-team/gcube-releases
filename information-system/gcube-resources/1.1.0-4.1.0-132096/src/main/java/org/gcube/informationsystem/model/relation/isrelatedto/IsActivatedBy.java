/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsActivatedByImpl;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.RunningPlugin;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isActivatedBy
 */
@JsonDeserialize(as=IsActivatedByImpl.class)
public interface IsActivatedBy<Out extends RunningPlugin, In extends EService> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsActivatedBy"; // IsActivatedBy.class.getSimpleName();
	
}
