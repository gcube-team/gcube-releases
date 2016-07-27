/**
 * 
 */
package org.gcube.informationsystem.model.orientdb.impl.relation;

import org.gcube.informationsystem.model.entity.Context;

import com.tinkerpop.frames.EdgeFrame;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface ParentOf<Out extends Context, In extends Context> extends
		EdgeFrame, org.gcube.informationsystem.model.relation.ParentOf<Out, In> {

}
