/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Relation.NAME)
public class RelationImpl<Out extends Entity, In extends Entity> implements
		Relation<Out, In> {

	protected Header header;

	protected Out source;
	protected In target;

	protected RelationProperty relationProperty;

	@JsonIgnore
	protected Map<String, Object> additionalProperties;
	
	/**
	 * Used to allow to have an additional property starting with '_' or '@'
	 */
	protected final static Set<String> allowedAdditionalKeys;
	
	static {
		allowedAdditionalKeys = new HashSet<>();
	}
	
	
	protected RelationImpl(){
		additionalProperties = new HashMap<>();
	}
	
	protected RelationImpl(Out source, In target,
			RelationProperty relationProperty) {
		this();
		this.source = source;
		this.target = target;
		this.relationProperty = relationProperty;
	}

	@Override
	public Header getHeader() {
		return header;
	}

	@Override
	public Out getSource() {
		return source;
	}
	
	protected void setSource(Out source) {
		this.source = source;
	}
	
	@Override
	public In getTarget() {
		return target;
	}

	protected void setTarget(In target) {
		this.target = target;
	}
	
	@Override
	public RelationProperty getRelationProperty() {
		return this.relationProperty;
	}

	protected void setRelationProperty(RelationProperty relationProperty) {
		this.relationProperty = relationProperty;
	}

	@Override
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	@Override
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public Object getAdditionalProperty(String key) {
		return additionalProperties.get(key);
	}

	@Override
	public void setAdditionalProperty(String key, Object value) {
		if(!allowedAdditionalKeys.contains(key)){
			if(key.startsWith("_")) {
				return;
			}
			if(key.startsWith("@")) {
				return;
			}
		}
		this.additionalProperties.put(key, value);
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
