/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.entity.Resource;

/**
 * @author Luca Frosini (ISTI - CNR)
 * This Relation is for internal use only
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isRelatedTo
 */
// @JsonDeserialize(as=IsRelatedToImpl.class) Do not uncomment to manage subclasses
public interface IsRelatedTo<Out extends Resource, In extends Resource>
		extends Relation<Out, In> {
	
	public static final String NAME = "IsRelatedTo"; //IsRelatedTo.class.getSimpleName();
	
}
