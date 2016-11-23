/**
 * 
 */
package org.gcube.informationsystem.model.orientdb.impl.entity;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface Context extends
		org.gcube.informationsystem.model.entity.Context, Entity, VertexFrame {

	@Adjacency(label = "ParentOf", direction = Direction.IN)
	public Iterable<Context> getParentContext();

	@Property(NAME_PROPERTY)
	@Override
	public String getName();

	@Property(NAME_PROPERTY)
	@Override
	public void setName(String name);
}
