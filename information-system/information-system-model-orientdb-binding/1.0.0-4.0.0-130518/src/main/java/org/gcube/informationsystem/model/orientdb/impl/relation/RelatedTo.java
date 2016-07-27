/**
 * 
 */
package org.gcube.informationsystem.model.orientdb.impl.relation;

import org.gcube.informationsystem.model.entity.Resource;

import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.OutVertex;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Relation-Facet 
 * Please note that targetResource described in the wiki is implicit. The 
 * {@link OutVertex} of this Edge. Look at {@link RelatedTo#getTargetResource()}
 */
public interface RelatedTo<Out extends Resource, In extends Resource>
		extends EdgeFrame, org.gcube.informationsystem.model.relation.RelatedTo<Out, In> {
	
}
