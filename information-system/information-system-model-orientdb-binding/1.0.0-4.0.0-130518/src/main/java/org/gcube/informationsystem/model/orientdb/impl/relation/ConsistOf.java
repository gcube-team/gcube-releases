/**
 * 
 */
package org.gcube.informationsystem.model.orientdb.impl.relation;

import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;

import com.tinkerpop.frames.EdgeFrame;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface ConsistOf<Out extends Resource, In extends Facet>
		extends EdgeFrame, org.gcube.informationsystem.model.relation.ConsistOf<Out, In> {
}
