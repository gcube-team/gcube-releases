/**
 * 
 */
package org.gcube.informationsystem.model.orientdb.impl.entity;

import org.gcube.informationsystem.model.orientdb.impl.embedded.Header;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface Entity extends org.gcube.informationsystem.model.entity.Entity, VertexFrame {
	
	@Property(HEADER_PROPERTY)
	@Override
	public Header getHeader();
	
	@Property(HEADER_PROPERTY)
	public void setHeader(Header header);
	
}
