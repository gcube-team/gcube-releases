/**
 * 
 */
package org.gcube.informationsystem.model.impl.entity;

import java.io.StringWriter;

import org.gcube.informationsystem.model.impl.ERImpl;
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.entity.Entity;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Entity.NAME)
public abstract class EntityImpl extends ERImpl implements Entity {
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -4488771434017342703L;
	
	protected EntityImpl(){
		super();
		this.header = null;
	}
	
	@Override
	public String toString(){
		StringWriter stringWriter = new StringWriter();
		try {
			ISMapper.marshal(this, stringWriter);
			return stringWriter.toString();
		}catch(Exception e){
			try {
				ISMapper.marshal(this.header, stringWriter);
				return stringWriter.toString();
			} catch(Exception e1){
				return super.toString();
			}
		}
	}

}
