/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import java.io.StringWriter;

import org.gcube.informationsystem.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=Entity.NAME)
public abstract class EntityImpl implements Entity {
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -4488771434017342703L;
	
	protected HeaderImpl header;
	
	protected EntityImpl(){
		this.header = null;
	}
	
	@Override
	public Header getHeader() {
		return this.header;
	}

	@Override
	public int compareTo(Entity entity) {
		return header.compareTo(entity.getHeader());
	}
	
	@Override
	public String toString(){
		StringWriter stringWriter = new StringWriter();
		try {
			Entities.marshal(this, stringWriter);
			return stringWriter.toString();
		}catch(Exception e){
			try {
				Entities.marshal(this.header, stringWriter);
				return stringWriter.toString();
			} catch(Exception e1){
				return super.toString();
			}
		}
	}

}
