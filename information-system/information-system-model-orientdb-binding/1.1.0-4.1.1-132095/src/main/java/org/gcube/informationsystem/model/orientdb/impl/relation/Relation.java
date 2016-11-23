/**
 * 
 */
package org.gcube.informationsystem.model.orientdb.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.orientdb.impl.embedded.Header;

import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.InVertex;
import com.tinkerpop.frames.OutVertex;
import com.tinkerpop.frames.Property;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface Relation<Out extends Entity, In extends Entity> extends
		EdgeFrame, org.gcube.informationsystem.model.relation.Relation<Out, In> {

	@Property(HEADER_PROPERTY)
	@Override
	public Header getHeader();

	@Property(HEADER_PROPERTY)
	public void setHeader(Header header);

	@OutVertex
	public Out getSource();

	@InVertex
	public In getTarget();

	@Property(RELATION_PROPERTY)
	public RelationProperty getRelationProperty();

}
