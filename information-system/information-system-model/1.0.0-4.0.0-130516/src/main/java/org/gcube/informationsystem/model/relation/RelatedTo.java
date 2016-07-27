/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.entity.Resource;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Relation-Facet 
 * Please note that targetResource described in the wiki is implicit. The 
 * {@link OutVertex} of this Edge. Look at {@link RelatedTo#getTargetResource()}
 */
public interface RelatedTo<Out extends Resource, In extends Resource>
		extends Relation<Out, In> {
	
	public static final String NAME = RelatedTo.class.getSimpleName();
	
}
