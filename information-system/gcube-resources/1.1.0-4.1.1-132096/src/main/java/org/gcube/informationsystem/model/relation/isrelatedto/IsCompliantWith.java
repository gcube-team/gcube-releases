/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsCompliantWithImpl;
import org.gcube.informationsystem.model.entity.resource.Dataset;
import org.gcube.informationsystem.model.entity.resource.Schema;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isCompliantWith
 */
@JsonDeserialize(as=IsCompliantWithImpl.class)
public interface IsCompliantWith<Out extends Dataset, In extends Schema> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsCompliantWith"; // IsCompliantWith.class.getSimpleName();
	
}
